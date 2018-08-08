package com.szxb.buspay.task.scan;

import com.google.gson.Gson;
import com.szxb.buspay.BusApp;
import com.szxb.buspay.db.entity.bean.FTPEntity;
import com.szxb.buspay.db.entity.bean.LINEntity;
import com.szxb.buspay.db.entity.bean.QRCode;
import com.szxb.buspay.db.entity.scan.param.UnionPayParam;
import com.szxb.buspay.module.init.PosInit;
import com.szxb.buspay.util.HexUtil;
import com.szxb.buspay.util.tip.BusToast;
import com.szxb.java8583.core.Iso8583Message;
import com.szxb.java8583.module.SignIn;
import com.szxb.java8583.module.manager.BusllPosManage;
import com.szxb.mlog.SLog;
import com.szxb.unionpay.UnionPay;
import com.szxb.unionpay.config.UnionConfig;

/**
 * 作者: Tangren on 2017-09-08
 * 包名：szxb.com.commonbus.util.report
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class XBPosReportManager {


    private static XBPosReportManager instance = null;

    private XBPosReportManager() {

    }

    public static XBPosReportManager getInstance() {
        synchronized (XBPosReportManager.class) {
            if (instance == null) {
                instance = new XBPosReportManager();
            }
        }
        return instance;
    }


    void posScan(int codeType, String qrcode) {
        try {
            if (qrcode.length() < 8) {
                BusToast.showToast(BusApp.getInstance(), "二维码参数有误", false);
                return;
            }
            switch (codeType) {
                case QRCode.CONFIG_CODE_FTP:
                    String resultFTP = qrcode.substring(8, qrcode.length());
                    FTPEntity ftpEntity = new Gson().fromJson(resultFTP, FTPEntity.class);
                    if (ftpEntity != null) {
                        BusApp.getPosManager().setFTP(ftpEntity);
                        BusToast.showToast(BusApp.getInstance(), "FTP设置成功\n重启生效", true);
                    }
                    break;
                case QRCode.CONFIG_CODE_MCH:

                    break;
                case QRCode.CONFIG_CODE_IP:

                    break;
                case QRCode.CONFIG_CODE_UNIONPAY:
                    String resultUnion = qrcode.substring(10, qrcode.length());
                    UnionPayParam unionPayParam = new Gson().fromJson(resultUnion, UnionPayParam.class);
                    if (unionPayParam != null) {
                        BusllPosManage.getPosManager().setMachId(unionPayParam.getMch());
                        BusllPosManage.getPosManager().setKey(unionPayParam.getKey());
                        BusllPosManage.getPosManager().setPosSn(unionPayParam.getSn());
                        BusToast.showToast(BusApp.getInstance(), "银联参数设置成功\n正在重新签到", true);

                        BusllPosManage.getPosManager().setTradeSeq();
                        Iso8583Message message = SignIn.getInstance().message(BusllPosManage.getPosManager().getTradeSeq());
                        UnionPay.getInstance().exeSSL(UnionConfig.SIGN, message.getBytes());
                    }
                    break;
                case QRCode.QR_MOREN:
                    LINEntity linEntityMore = new Gson().fromJson(qrcode.substring(10, qrcode.length()), LINEntity.class);


                    break;
                default:
                    BusToast.showToast(BusApp.getInstance(), "线路正在更新", true);
                    String resultLine = qrcode.substring(4, qrcode.length());
                    LINEntity linEntity = new Gson().fromJson(resultLine, LINEntity.class);
                    if (linEntity != null) {
                        String fileName = HexUtil.fileName(linEntity.getL());
                        SLog.d("XBPosReportManager(posScan.java:70)需要下载的文件名：" + fileName);
                        PosInit init = new PosInit();
                        init.downloadAppointFile(fileName, linEntity.getN());
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            SLog.d("XBPosReportManager(posScan.java:76)设置码出现异常>>" + e.toString());
        }
    }

}
