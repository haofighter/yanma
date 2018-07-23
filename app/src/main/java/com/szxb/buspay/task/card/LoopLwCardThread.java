package com.szxb.buspay.task.card;

import android.os.SystemClock;
import android.text.TextUtils;

import com.szxb.buspay.BusApp;
import com.szxb.buspay.db.entity.bean.QRCode;
import com.szxb.buspay.db.entity.bean.QRScanMessage;
import com.szxb.buspay.db.entity.bean.card.ConsumeCard;
import com.szxb.buspay.db.entity.bean.card.SearchCard;
import com.szxb.buspay.db.entity.scan.PosRecord;
import com.szxb.buspay.db.manager.DBManager;
import com.szxb.buspay.task.thread.ThreadScheduledExecutorUtil;
import com.szxb.buspay.task.thread.WorkThread;
import com.szxb.buspay.util.Config;
import com.szxb.buspay.util.DateUtil;
import com.szxb.buspay.util.HexUtil;
import com.szxb.buspay.util.Util;
import com.szxb.buspay.util.rx.RxBus;
import com.szxb.buspay.util.sound.SoundPoolUtil;
import com.szxb.buspay.util.tip.BusToast;
import com.szxb.jni.libszxb;
import com.szxb.mlog.SLog;
import com.szxb.unionpay.UnionCard;

import static com.szxb.buspay.db.entity.bean.card.CardTypeZiBo.CARD_CHECK;
import static com.szxb.buspay.db.entity.bean.card.CardTypeZiBo.CARD_CHECKED;
import static com.szxb.buspay.db.entity.bean.card.CardTypeZiBo.CARD_EMP;
import static com.szxb.buspay.db.entity.bean.card.CardTypeZiBo.CARD_FREE;
import static com.szxb.buspay.db.entity.bean.card.CardTypeZiBo.CARD_GATHER;
import static com.szxb.buspay.db.entity.bean.card.CardTypeZiBo.CARD_MEMORY;
import static com.szxb.buspay.db.entity.bean.card.CardTypeZiBo.CARD_NORMAL;
import static com.szxb.buspay.db.entity.bean.card.CardTypeZiBo.CARD_OLD;
import static com.szxb.buspay.db.entity.bean.card.CardTypeZiBo.CARD_SIGNED;
import static com.szxb.buspay.db.entity.bean.card.CardTypeZiBo.CARD_STUDENT;
import static com.szxb.buspay.util.Util.fen2Yuan;
import static com.szxb.buspay.util.Util.filter;
import static com.szxb.buspay.util.Util.hex2Int;

/**
 * 作者：Tangren on 2018-07-23
 * 包名：com.szxb.buspay.task.card
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class LoopLwCardThread extends Thread {

    private boolean isBlack = false;
    private boolean isWhite = false;

    private String cardNoTemp = "0";
    private long lastTime = 0;
    private SearchCard searchCard;


    @Override
    public void run() {
        super.run();
        try {

            byte[] searchBytes = new byte[16];
            int status = libszxb.MifareGetSNR(searchBytes);
            if (status < 0) {
                SLog.e("LoopCardThread(run.java:19)寻卡状态=" + status);
            }

            if (searchBytes[0] != (byte) 0x00) {
                //如果寻卡状态不等于00..无法处理此卡
                return;
            }

            searchCard = new SearchCard(searchBytes);

            //拦截黑名单
            //do

            //1S防抖动
            if (!filter(SystemClock.elapsedRealtime(), lastTime)) {
                return;
            }

            String driverNo = BusApp.getPosManager().getDriverNo();
            if (TextUtils.equals(driverNo, String.format("%08d", 0))
                    && !TextUtils.equals(searchCard.cardType, "06")) {
                return;
            }

            if (TextUtils.equals(searchCard.cardType, "02")
                    || TextUtils.equals(searchCard.cardType, "03")
                    || TextUtils.equals(searchCard.cardType, "04")
                    || TextUtils.equals(searchCard.cardType, "45")) {
                //如果属于上述卡类型,如果票价小于普通卡金额做1分钟去重
                int normalAmount = payFee(CARD_NORMAL);
                int currentAmount = payFee(searchCard.cardType);
                if (currentAmount < normalAmount) {
                    if (DBManager.filterBrush(searchCard.cityCode + searchCard.cardNo)) {
                        BusToast.showToast(BusApp.getInstance(), "您已刷过[" + searchCard.cardType + "]", true);
                        lastTime = SystemClock.elapsedRealtime();
                        return;
                    }
                }
            }

            //防止重复刷卡
            //去重刷,同一个卡号3S内不提示
            if (!Util.check(cardNoTemp, searchCard.cardNo, lastTime)) {
                BusToast.showToast(BusApp.getInstance(), "您已刷过[" + searchCard.cardType + "]", true);
                return;
            }

            SLog.d("LoopCardThread(run.java:82)寻卡数据>>>" + searchCard);

            //1.判断是否已签到
            //2.未签到
            //3.已签到
            if (TextUtils.equals(driverNo, String.format("%08d", 0))) {
                //未签到
                if (TextUtils.equals(searchCard.cardType, "06")) {
                    //只允许普通员工签到
                    ConsumeCard response = response(0, false, false, false, true);
                    SLog.d("LoopCardThread(run.java:67)签到>>" + response);
                    if (TextUtils.equals(response.getStatus(), "00") &&
                            TextUtils.equals(response.getTransType(), "12")) {
                        //司机卡上班
                        BusApp.getPosManager().setDriverNo(response.getTac());
                        notice(Config.IC_TO_WORK, "司机卡上班[" + response.getTac() + "]", true);
                        RxBus.getInstance().send(new QRScanMessage(new PosRecord(), QRCode.SIGN));
                        saveRecord(response);
                    } else {
                        BusToast.showToast(BusApp.getInstance(), "签到失败[" + response.getStatus() + "|" + response.getTransType() + "]", false);
                    }
                } else {
                    //如果不是普通员工签到卡,则不做任何提示
                    SLog.d("LoopCardThread(run.java:77)不是普通员工签到卡>>>则不做任何提示");
                }
            } else {
                //如果已经签到,判断当前是否是下班卡
                if (TextUtils.equals(searchCard.cardType, "10")) {
                    //万能下班卡
                    noticeOffWork();
                } else if (TextUtils.equals(searchCard.cardType, "06")) {
                    //可能是消费卡，可能是下班卡
                    //如果当前司机号等于刷卡的司机号则是下班卡,否则走消费流程

                    int pay_fee = payFee(searchCard.cardType);
                    SLog.d("LoopCardThread(run.java:90)pay_fee=" + pay_fee);
                    ConsumeCard response = response(pay_fee, false, false, true, false);

                    SLog.d("LoopCardThread(run.java:92)员工卡>>>>" + response);
                    if (TextUtils.equals(response.getStatus(), "00")) {

                        if (TextUtils.equals(response.getTransType(), "13")) {
                            //下班
                            BusApp.getPosManager().setDriverNo(String.format("%08d", 0));
                            notice(Config.IC_OFF_WORK, "司机卡下班[00]", true);
                            RxBus.getInstance().send(new QRScanMessage(new PosRecord(), QRCode.SIGN));
                            saveRecord(response);

                        } else {
                            //员工卡正常消费
                            SLog.d("LoopCardThread(run.java:100)员工卡正常消费>>>金额=" + response.getPayFee() + "..余额=" + response.getCardBalance());

                            if (TextUtils.equals(searchCard.cardType, "06")) {
                                //如果司机卡是消费状态,1分钟去重
                                int normalAmount = payFee(CARD_NORMAL);
                                int currentAmount = payFee(searchCard.cardType);
                                if (currentAmount < normalAmount) {
                                    if (DBManager.filterBrush(searchCard.cityCode + searchCard.cardNo)) {
                                        BusToast.showToast(BusApp.getInstance(), "您已刷过[" + searchCard.cardType + "]", true);
                                        lastTime = SystemClock.elapsedRealtime();
                                        return;
                                    }
                                }
                            }
                            //检查余额是否大于5元
                            int balance = hex2Int(response.getCardBalance());
                            checkTheBalance(response, balance > 500 ? Config.IC_BASE2 : Config.IC_RECHARGE);
                        }

                    } else {
                        SLog.d("LoopCardThread(run.java:104)" + response);
                        notice(Config.IC_RE, "请重刷[" + response.getStatus() + "]", false);
                        searchCard.cardNo = "0";
                    }

                } else {
                    //其他卡
                    elseCardControl(searchCard);
                }
            }

            cardNoTemp = searchCard.cardNo;
            lastTime = SystemClock.elapsedRealtime();
        } catch (Exception e) {
            e.printStackTrace();
            SLog.d("LoopCardThread(run.java:60)LoopCardThread出现异常>>>" + e.toString());
        }
    }


    /**
     * @param searchCard 其他卡操作
     */
    private void elseCardControl(SearchCard searchCard) {

        if (TextUtils.equals(searchCard.cardModuleType, "F0")) {
            //银联卡
            UnionCard.getInstance().run(searchCard.cityCode + searchCard.cardNo);
        } else {
            int pay_fee = payFee(searchCard.cardType);
            ConsumeCard response = response(pay_fee, isBlack, isWhite, true, false);
            String status = response.getStatus();
            String balance = response.getCardBalance();
            String cardType = response.getCardType();
            if (TextUtils.equals(status, "00")) {
                switch (cardType) {
                    case "01"://普通卡和CPU福利卡
                    case "05"://纪念卡
                        checkTheBalance(response, hex2Int(balance) > 500 ? Config.IC_BASE2 : Config.IC_RECHARGE);
                        break;
                    case "02"://学生卡
                        checkTheBalance(response, hex2Int(balance) > 500 ? Config.IC_STUDENT : Config.IC_RECHARGE);
                        break;
                    case "03"://老年卡
                        response.setPayFee("0");
                        checkTheBalance(response, hex2Int(balance) > 500 ? Config.IC_OLD : Config.IC_RECHARGE);
                        break;
                    case "04"://免费卡
                        if (TextUtils.equals(response.getTransType(), "06")) {
                            //免费卡交易类型为06时判断余额是否小于5元
                            checkTheBalance(response, hex2Int(balance) > 500 ? Config.IC_HONOR : Config.IC_RECHARGE);
                        } else {
                            response.setPayFee("0");
                            checkTheBalance(response, Config.IC_HONOR);
                        }
                        break;
                    case "06"://员工卡
                        checkTheBalance(response, hex2Int(balance) > 500 ? Config.IC_EMP : Config.IC_RECHARGE);
                        break;
                    case "10"://线路票价设置卡(只做签退用)
                    case "11"://数据采集卡(只做签退用)
                        break;
                    case "12"://签点卡
                        notice(Config.IC_BASE, "签点卡", true);
                        break;
                    case "13"://检测卡
                        notice(Config.IC_BASE, "检测卡", true);
                        break;
                    case "18"://稽查卡
                        notice(Config.IC_BASE, "稽查卡", true);
                        break;
                    default://其他卡类型
                        checkTheBalance(response, hex2Int(balance) > 500 ? Config.IC_BASE2 : Config.IC_RECHARGE);
                        break;
                }
            } else if (TextUtils.equals(status, "10")) {
                //淄博公交，语音提示“无偿献血卡
                checkTheBalance(response, Config.IC_BLOOD);

            } else if (TextUtils.equals(status, "11")) {
                //淄博公交，语音提示“爱心卡”
                response.setPayFee("0");
                checkTheBalance(response, Config.IC_LOVE);

            } else if (status.equalsIgnoreCase("F1")) {
                //卡片未启用
                notice(Config.IC_PUSH_MONEY, "卡片未启用[F1]", false);
            } else if (status.equalsIgnoreCase("F2")) {
                //卡片过期
                notice(Config.IC_PUSH_MONEY, "卡片过期[F2]", false);
                DateUtil.setK21Time();

            } else if (status.equalsIgnoreCase("F3")) {
                //卡内余额不足
                notice(Config.IC_PUSH_MONEY, "卡内余额不足[F3]", false);
            } else if (status.equalsIgnoreCase("F4")) {
                //此卡为黑名单卡(已经锁了)
                notice(Config.IC_PUSH_MONEY, "黑名单卡[F4]", false);
            } else if (status.equalsIgnoreCase("F5")) {
                //不是本系统卡
                notice(Config.IC_PUSH_MONEY, "不是本系统卡[F5]", false);
            } else if (status.equalsIgnoreCase("F6")) {
                //月票卡不能乘坐本线路
                notice(Config.IC_PUSH_MONEY, "月票卡不能乘坐本线路[F6]", false);
            } else if (status.equalsIgnoreCase("FE")
                    || status.equalsIgnoreCase("FF")) {
                //消费异常(重新刷卡)
                notice(Config.IC_RE, "重新刷卡[" + status + "]", false);
                this.searchCard.cardNo = "0";
            }

        }

    }

    /**
     * @param cardType 卡类型
     * @return .
     */
    private int payFee(String cardType) {
        String[] coefficent = BusApp.getPosManager().getCoefficent();
        int basePrices = BusApp.getPosManager().getBasePrice();
        switch (cardType) {
            case CARD_NORMAL://普通卡
            case CARD_MEMORY://纪念卡
                return Util.string2Int(coefficent[0]) * basePrices / 100;
            case CARD_STUDENT://学生卡
                return Util.string2Int(coefficent[1]) * basePrices / 100;
            case CARD_OLD://老年卡
                return Util.string2Int(coefficent[2]) * basePrices / 100;
            case CARD_FREE://免费卡
                return Util.string2Int(coefficent[3]) * basePrices / 100;
            case CARD_EMP://员工卡
                return Util.string2Int(coefficent[4]) * basePrices / 100;
            case CARD_GATHER:
            case CARD_SIGNED:
            case CARD_CHECKED:
            case CARD_CHECK:
                return 0;
            default:
                return Util.string2Int(coefficent[0]) * basePrices / 100;
        }
    }


    /**
     * 消费报文
     *
     * @param pay_fee 实际扣款
     * @param isBlack 是否是黑名单：0x01黑名单锁卡
     * @param isWhite 是否是白名单,预留
     * @return 消费响应
     */
    private ConsumeCard response(int pay_fee, boolean isBlack, boolean isWhite, boolean workStatus, boolean isSign) {
        int total_fee = BusApp.getPosManager().getBasePrice();
        byte[] data = new byte[128];
        byte[] amount = HexUtil.int2Bytes(pay_fee, 3);
        byte[] baseAmount = HexUtil.int2Bytes(total_fee, 3);
        byte[] black = new byte[]{(byte) (isBlack ? 0x01 : 0x00)};
        byte[] white = new byte[]{0x01};
        byte[] busNo = HexUtil.str2Bcd(BusApp.getPosManager().getBusNo());
        byte[] lineNo = HexUtil.hex2byte(BusApp.getPosManager().getLineNo());
        byte[] workStatus_ = new byte[]{(byte) (workStatus ? 0x01 : 0x00)};
        byte[] driverNo = HexUtil.str2Bcd(BusApp.getPosManager().getDriverNo());
        byte[] direction = new byte[]{0x00};
        byte[] stationId = new byte[]{0x01};
        byte[] sendData = HexUtil.mergeByte(amount, baseAmount, black, white, busNo, lineNo,
                workStatus_, driverNo, direction, stationId, data);

        SLog.d("LoopCardThread(response.java:279)发送的报文:" + HexUtil.printHexBinary(sendData));

        int ret = libszxb.qxcardprocess(sendData);
        return new ConsumeCard(sendData, isSign,"zibo");
    }

    /**
     * 下班
     */
    private void noticeOffWork() {
        ConsumeCard response = response(0, false, false, true, false);
        SLog.d("LoopCardThread(noticeOffWork.java:161)下班>>" + response);
        if (TextUtils.equals(response.getStatus(), "00") &&
                TextUtils.equals(response.getTransType(), "13")) {
            //下班成功
            BusApp.getPosManager().setDriverNo(String.format("%08d", 0));
            notice(Config.IC_OFF_WORK, "司机卡下班[00]", true);
            RxBus.getInstance().send(new QRScanMessage(new PosRecord(), QRCode.SIGN));
            //保存下班记录
            saveRecord(response);
        } else {
            BusToast.showToast(BusApp.getInstance(), "签退失败[" + response.getStatus() + "|" + response.getTransType() + "]", false);
        }
    }


    /**
     * 检查余额并做提示
     *
     * @param response .
     */
    private void checkTheBalance(ConsumeCard response, int music) {
        notice(music, "本次扣款:"
                + fen2Yuan(hex2Int(response.getPayFee())) + "元\n余额:"
                + fen2Yuan(hex2Int(response.getCardBalance())) + "元", true);

        saveRecord(response);
    }

    /**
     * @param music  .
     * @param tipVar .
     * @param isOk   .
     */
    private void notice(int music, String tipVar, boolean isOk) {
        SoundPoolUtil.play(music);
        BusToast.showToast(BusApp.getInstance(), tipVar, isOk);
    }

    /**
     * 保存交易记录
     *
     * @param consumeCard .
     */
    private void saveRecord(ConsumeCard consumeCard) {
        ThreadScheduledExecutorUtil.getInstance().getService().submit(new WorkThread("zibo", consumeCard));
        SLog.d("LoopCardThread(saveRecord.java:345)保存成功");
    }

}
