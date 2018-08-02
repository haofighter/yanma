package com.szxb.buspay.task.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.szxb.buspay.BusApp;
import com.szxb.buspay.task.thread.ThreadScheduledExecutorUtil;

import java.util.concurrent.TimeUnit;


/**
 * 作者: Tangren on 2017/8/16
 * 包名：szxb.com.commonbus.task
 * 邮箱：996489865@qq.com
 * TODO:定时处理未按时结算的订单
 */

public class TimeSettleTask extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (BusApp.getPosManager().isSuppScanPay()) {
            ThreadScheduledExecutorUtil.getInstance().getService().scheduleAtFixedRate(new RecordThread("scan"), 1, 60, TimeUnit.SECONDS);
        }

        if (BusApp.getPosManager().isSuppIcPay()) {
            ThreadScheduledExecutorUtil.getInstance().getService().scheduleAtFixedRate(new RecordThread("ic"), 1, 140, TimeUnit.SECONDS);
        }

        if (BusApp.getPosManager().isSuppUnionPay()) {
            ThreadScheduledExecutorUtil.getInstance().getService().scheduleAtFixedRate(new RecordThread("union"), 1, 160, TimeUnit.SECONDS);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
