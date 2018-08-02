package com.szxb.buspay.task;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.szxb.buspay.db.entity.bean.QRCode;
import com.szxb.buspay.db.entity.bean.QRScanMessage;
import com.szxb.buspay.db.entity.scan.PosRecord;
import com.szxb.buspay.util.AppUtil;
import com.szxb.buspay.util.rx.RxBus;
import com.szxb.mlog.SLog;

/**
 * 作者：Tangren on 2018-08-02
 * 包名：com.szxb.buspay.task
 * 邮箱：996489865@qq.com
 * TODO:监听网络状态变化
 */

public class NetChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean b = AppUtil.checkNetStatus();
        SLog.d("NetChangeReceiver(onReceive.java:20)当前网络状态:" + (b ? "已连接" : "不可用"));
        RxBus.getInstance().send(new QRScanMessage(new PosRecord(), QRCode.NET_STATUS));
    }
}
