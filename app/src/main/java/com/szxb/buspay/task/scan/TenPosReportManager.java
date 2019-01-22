package com.szxb.buspay.task.scan;

import android.text.TextUtils;

import com.szxb.buspay.BuildConfig;
import com.szxb.buspay.BusApp;
import com.szxb.buspay.db.entity.bean.QRCode;
import com.szxb.buspay.db.entity.bean.QRScanMessage;
import com.szxb.buspay.db.entity.scan.PosRecord;
import com.szxb.buspay.db.manager.DBManager;
import com.szxb.buspay.test.PosSnEntity;
import com.szxb.buspay.test.TestUtil;
import com.szxb.buspay.util.Config;
import com.szxb.buspay.util.Util;
import com.szxb.buspay.util.rx.RxBus;
import com.szxb.buspay.util.sound.SoundPoolUtil;
import com.szxb.buspay.util.tip.BusToast;
import com.szxb.mlog.SLog;
import com.tencent.wlxsdk.WlxSdk;


/**
 * 作者: Tangren on 2017-09-08
 * 包名：szxb.com.commonbus.util.report
 * 邮箱：996489865@qq.com
 * TODO:腾讯
 */

public class TenPosReportManager {

    private static TenPosReportManager instance = null;
    private WlxSdk wxSdk;

    private TenPosReportManager() {
        wxSdk = new WlxSdk();
    }

    public static TenPosReportManager getInstance() {
        if (instance == null) {
            synchronized (TenPosReportManager.class) {
                if (instance == null) {
                    instance = new TenPosReportManager();
                }
            }
        }
        return instance;
    }

    String[] useLineNo = new String[]{""};
    String[] useLineName = new String[]{""};
    String[] busNo = new String[]{""};

    public void posScan(String qrcode) {
        if (wxSdk == null) wxSdk = new WlxSdk();
        int init = wxSdk.init(qrcode);
        int key_id = wxSdk.get_key_id();
        String open_id = wxSdk.get_open_id();
        String mac_root_id = wxSdk.get_mac_root_id();
        int price = 100;

        switch (BuildConfig.FLAVOR) {
            case "zibo_app":
                useLineNo = TestUtil.ziboNo;
                useLineName = TestUtil.ziboName;
//                busNo = TestUtil.ziboBusno;
                busNo = new String[]{Util.fromatBusNo(BusApp.getTestPos().getSNID() + "")};
                break;
            case "zhaoyuan_app":
                useLineNo = TestUtil.zy_lienNo;
                useLineName = TestUtil.zy_linName;
                busNo = TestUtil.zy_busNo;
                break;
            case "zhaoyuan_app_new":
                useLineNo = TestUtil.zy_new_lienNo;
                useLineName = TestUtil.zy_new_linName;
                busNo = TestUtil.zy_new_busNo;
                break;
            case "lwgj_2_app":
            case "lwgj_1_app":
                useLineNo = TestUtil.lwcyNo;
                useLineName = TestUtil.lwcyName;
                busNo = TestUtil.lwcyBusNo;
                break;
            case "qixia_app":
                useLineNo = TestUtil.qx_lienNo;
                useLineName = TestUtil.qx_linName;
                busNo = TestUtil.qx_busNo;
                break;
            case "taian_app":
                useLineNo = TestUtil.taian_lineNo;
                useLineName = TestUtil.taian_lineName;
                busNo = TestUtil.taian_busno;
                break;
            case "rc_app":
                price=135;
                useLineNo = TestUtil.rc_lineNo;
                useLineName = TestUtil.rc_lineName;
                busNo = TestUtil.rc_busNo;
                break;
        }


        int verify = 0;
        if (!TextUtils.isEmpty(open_id)) {
            if (DBManager.filterOpenID(open_id)) {
                BusToast.showToast(BusApp.getInstance(), "禁止频繁刷码", false);
            } else if (DBManager.filterSameQR(qrcode)) {
                RxBus.getInstance().send(new QRScanMessage(new PosRecord(), QRCode.REFRESH_QR_CODE));
            } else if (DBManager.filterBlackName(open_id)) {
                //是黑名单里面的成员
                RxBus.getInstance().send(new QRScanMessage(new PosRecord(), QRCode.QR_ERROR));
            } else {

                if (init == 0 && key_id > 0) {
                    try {
                        PosSnEntity posSnEntity = BusApp.getTestPos().getPosList().get(BusApp.getTestPos().getSNID() % BusApp.getTestPos().getPosList().size());
                        //String open_id, String pub_key, int payfee, byte scene, byte scantype, String pos_id, String pos_trx_id, String aes_mac_root
                        verify = wxSdk.verify(open_id
                                , BusApp.getPosManager().getPublicKey(String.valueOf(key_id))
                                , BusApp.getPosManager().getWcPayPrice()//金额
                                , (byte) 1
                                , (byte) 1
                                , posSnEntity.getPosSN()
                                , Util.Random(8)
                                , BusApp.getPosManager().getMac(mac_root_id));

                        String record = wxSdk.get_record();

                        PosRecord posRecord = new PosRecord();
                        posRecord.setOpen_id(open_id);
                        posRecord.setQr_code(qrcode);
                        posRecord.setOrder_time(BusApp.getPosManager().getOrderTime());
                        posRecord.setTotal_fee(price);//金额，上线修改为posRecord.setTotal_fee(App.getPosManager().getMarkedPrice());

                        posRecord.setPay_fee(price);//实际扣款金额，上线修改为posRecord.setTotal_fee(App.getPosManager().getPayMarkPrice());

                        SLog.d("TenPosReportManager(posScan.java:84)微信应扣款=" + BusApp.getPosManager().getBasePrice());
                        SLog.d("TenPosReportManager(posScan.java:83)微信实际扣款=" + BusApp.getPosManager().getWcPayPrice());

                        posRecord.setCity_code(BusApp.getPosManager().getCityCode());
                        posRecord.setOrder_desc(useLineNo[BusApp.getTestPos().getSNID() % useLineNo.length]);//线路名
                        posRecord.setRecord(record);
                        posRecord.setBus_no(busNo[BusApp.getTestPos().getSNID() % busNo.length]);
                        posRecord.setUnitno("10");
                        posRecord.setDriveno("999999");

                        //line_name作为line_no处理
                        posRecord.setBus_line_name(useLineNo[BusApp.getTestPos().getSNID() % useLineNo.length]);

//                    posRecord.setPos_no(BusApp.getPosManager().getPosSN());


                        posRecord.setPos_no(posSnEntity.getPosSN());
                        posRecord.setBus_line_no(useLineNo[BusApp.getTestPos().getSNID() % useLineNo.length]);
                        posRecord.setIn_station_id(1);
                        posRecord.setIn_station_name(useLineName[BusApp.getTestPos().getSNID() % useLineName.length]);

                        PosRequest.getInstance().request(new QRScanMessage(posRecord, verify));
                    } catch (Exception e) {
                        SoundPoolUtil.play(Config.VERIFY_FAIL);
                        BusToast.showToast(BusApp.getInstance(), "线路选择错误\n[SDK初始化失败]", false);
                    }
                } else {
                    SoundPoolUtil.play(Config.VERIFY_FAIL);
                    BusToast.showToast(BusApp.getInstance(), "验码失败\n[SDK初始化失败]", false);
                }

            }
        }
    }


}
