package com.szxb.buspay.task;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.szxb.buspay.db.entity.bean.QRCode;
import com.szxb.buspay.db.entity.bean.QRScanMessage;
import com.szxb.buspay.db.entity.scan.PosRecord;
import com.szxb.buspay.util.rx.RxBus;

/**
 * 作者：Tangren on 2018-08-02
 * 包名：com.szxb.buspay.task
 * 邮箱：996489865@qq.com
 * TODO:监听网络状态变化
 */

public class NetChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        RxBus.getInstance().sendSingle(new QRScanMessage(new PosRecord(), QRCode.NET_STATUS));
    }
}
