package com.szxb.buspay.task.card;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.szxb.buspay.task.thread.ThreadScheduledExecutorUtil;

import java.util.concurrent.TimeUnit;

/**
 * 作者：Tangren on 2018-07-19
 * 包名：com.szxb.buspay.task.card
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class LoopCardTask extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ThreadScheduledExecutorUtil.getInstance().getService().scheduleAtFixedRate(new LoopCardThread(), 1000, 200, TimeUnit.MILLISECONDS);
    }
}
