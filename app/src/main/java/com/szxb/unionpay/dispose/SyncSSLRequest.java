package com.szxb.unionpay.dispose;

import android.os.SystemClock;

import com.szxb.buspay.BusApp;
import com.szxb.buspay.db.dao.UnionPayEntityDao;
import com.szxb.buspay.db.manager.DBCore;
import com.szxb.buspay.task.thread.ThreadFactory;
import com.szxb.buspay.task.thread.WorkThread;
import com.szxb.buspay.util.Config;
import com.szxb.buspay.util.Util;
import com.szxb.buspay.util.sound.SoundPoolUtil;
import com.szxb.buspay.util.tip.BusToast;
import com.szxb.java8583.core.Iso8583Message;
import com.szxb.java8583.core.Iso8583MessageFactory;
import com.szxb.java8583.module.SignIn;
import com.szxb.java8583.module.manager.BusllPosManage;
import com.szxb.java8583.quickstart.SingletonFactory;
import com.szxb.java8583.quickstart.special.SpecialField62;
import com.szxb.mlog.SLog;
import com.szxb.unionpay.UnionPay;
import com.szxb.unionpay.config.UnionConfig;
import com.szxb.unionpay.entity.UnionPayEntity;
import com.szxb.unionpay.unionutil.SSLContextUtil;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.Response;
import com.yanzhenjie.nohttp.rest.SyncRequestExecutor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import static com.szxb.unionpay.unionutil.HexUtil.yuan2Fen;

/**
 * 作者：Tangren on 2018-09-08
 * 包名：com.szxb.unionpay.dispose
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class SyncSSLRequest {

    /**
     * @param sendData 源数据
     * @return 银联响应
     */
    synchronized public BankICResponse request(byte[] sendData) {
        BankICResponse icResponse = new BankICResponse();
        String url = BusllPosManage.getPosManager().getUnionPayUrl();
        Request<byte[]> request = NoHttp.createByteArrayRequest(url, RequestMethod.POST);
        request.setHeader("User-Agent", "Donjin Http 0.1");
        request.setHeader("Cache-Control", "no-cache");
        request.setHeader("Accept", "*/*");
        request.setHeader("Accept-Encoding", "*");
        request.setHeader("Connection", "close");
        request.setHeader("HOST", "120.204.69.139:30000");
        InputStream stream = new ByteArrayInputStream(sendData);
        request.setDefineRequestBody(stream, "x-ISO-TPDU/x-auth");
        SSLContext sslContext = SSLContextUtil.getSSLContext(BusApp.getInstance().getApplicationContext());
        request.setHostnameVerifier(SSLContextUtil.getHostnameVerifier());
        SSLSocketFactory socketFactory = sslContext.getSocketFactory();
        request.setSSLSocketFactory(socketFactory);
        request.setConnectTimeout(3000);
        request.setReadTimeout(3000);

        Response<byte[]> execute = SyncRequestExecutor.INSTANCE.execute(request);
        if (execute.isSucceed()) {
            Iso8583MessageFactory factory = SingletonFactory.forQuickStart();
            factory.setSpecialFieldHandle(62, new SpecialField62());
            Iso8583Message message0810 = factory.parse(execute.get());

            SLog.d("LoopCardThread_ZY(request.java:73)" + message0810.toFormatString());
            doDispose(icResponse, message0810);
        } else {
            icResponse.setResCode(BankCardParse.ERROR_NET);
            BusToast.showToast(BusApp.getInstance(), "网络超时", false);
        }
        return icResponse;
    }

    /**
     * @param icResponse  .
     * @param message0810 返回的报文
     */
    private void doDispose(BankICResponse icResponse, Iso8583Message message0810) {
        String pay_fee = message0810.getValue(4).getValue();
        String resCode = message0810.getValue(39).getValue();
        String tradeSeq = message0810.getValue(11).getValue();
        String batchNum = message0810.getValue(60).getValue().substring(2, 8);
        String uniqueFlag = tradeSeq + batchNum;
        UnionPayEntityDao dao = DBCore.getDaoSession().getUnionPayEntityDao();
        UnionPayEntity unique = dao.queryBuilder()
                .where(UnionPayEntityDao.Properties.UniqueFlag
                        .eq(uniqueFlag)).limit(1).build().unique();
        if (unique != null) {
            unique.setResCode(resCode);
            switch (resCode) {
                case "00":
                case "A2":
                case "A4":
                case "A5":
                case "A6":
                    //支付成功
                    String amount = message0810.getValue(4).getValue();
                    icResponse.setResCode(BankCardParse.SUCCESS);
                    icResponse.setMainCardNo(message0810.getValue(2).getValue());
                    icResponse.setMsg("扣款成功\n扣款金额" + yuan2Fen(amount) + "元");
                    icResponse.setLastTime(SystemClock.elapsedRealtime());
                    unique.setPayFee(pay_fee);
                    SoundPoolUtil.play(Config.IC_BASE2);
                    SLog.d("UnionPay(success.java:104)修改成功");
                    break;
                case "A0":
                    //重新签到
                    icResponse.setResCode(BankCardParse.ERROR_RE_SIGN);
                    BusToast.showToast(BusApp.getInstance().getApplicationContext(), "刷卡失败,正在签到,稍后重试", false);
                    BusllPosManage.getPosManager().setTradeSeq();
                    Iso8583Message message = SignIn.getInstance().message(BusllPosManage.getPosManager().getTradeSeq());
                    UnionPay.getInstance().exeSSL(UnionConfig.SIGN, message.getBytes(), true);
                    break;
                case "94"://重复交易（流水号重复）
                    icResponse.setResCode(-94);
                    icResponse.setMsg("流水号重复\n请重刷");
                    break;
                case "51"://余额不足
                    icResponse.setResCode(-51);
                    icResponse.setMsg("余额不足");
                    break;
                case "54"://卡过期
                    icResponse.setResCode(-54);
                    icResponse.setMsg("卡过期");
                    break;
                default:
                    icResponse.setResCode(BankCardParse.ERROR_ELSE);
                    icResponse.setMsg("刷卡失败[" + Util.unionPayStatus(resCode) + "]");
                    break;
            }

            //修改记录
            ThreadFactory.getScheduledPool().execute(new WorkThread("update_union", unique));
        }
    }

}
