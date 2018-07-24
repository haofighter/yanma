package com.szxb.buspay.task.scan;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.szxb.buspay.BusApp;
import com.szxb.buspay.db.entity.bean.QRCode;
import com.szxb.buspay.db.entity.bean.QRScanMessage;
import com.szxb.buspay.db.entity.scan.PosRecord;
import com.szxb.buspay.task.thread.ThreadScheduledExecutorUtil;
import com.szxb.buspay.util.Config;
import com.szxb.buspay.util.rx.RxBus;
import com.szxb.buspay.util.sound.SoundPoolUtil;
import com.szxb.buspay.util.tip.BusToast;
import com.szxb.jni.libszxb;

import java.util.concurrent.TimeUnit;

import static com.szxb.buspay.util.Util.checkQR;


/**
 * 作者: Tangren on 2017/7/31
 * 包名：com.szxb.task
 * 邮箱：996489865@qq.com
 * TODO:轮训扫码
 */

public class LoopScanTask extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //临时变量存储上次刷卡记录,为了防止重复刷卡
    private String tem = "0";
    //每次扫码后的时间
    private long lastTime = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        ThreadScheduledExecutorUtil.getInstance().getService().scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    byte[] recs = new byte[1024];
                    int barcode = libszxb.getBarcode(recs);
                    if (barcode > 0) {
                        String result = new String(recs, 0, barcode);
                        if (PosScanManager.isTenQRcode(result)) {
                            if (filterCheck(result)) {
                                return;
                            }
                            PosScanManager.getInstance().txposScan(result);
                        } else if (PosScanManager.isMyQRcode(result)) {
                            if (!checkQR(SystemClock.elapsedRealtime(), lastTime)) {
                                return;
                            }
                            PosScanManager.getInstance().xbposScan(result);
                        } else {
                            if (!checkQR(SystemClock.elapsedRealtime(), lastTime)) {
                                return;
                            }
                            SoundPoolUtil.play(Config.QR_ERROR);
                            BusToast.showToast(BusApp.getInstance(), "二维码有误", false);
                        }
                        tem = result;
                        lastTime = SystemClock.elapsedRealtime();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, 500, 200, TimeUnit.MILLISECONDS);
    }


    private boolean filterCheck(String result) {
        if (!checkQR(SystemClock.elapsedRealtime(), lastTime)) return true;
        if (TextUtils.equals(result, tem)) {
            RxBus.getInstance().send(new QRScanMessage(new PosRecord(), QRCode.REFRESH_QR_CODE));
            lastTime = SystemClock.elapsedRealtime();
            return true;
        }
        return false;
    }
}
