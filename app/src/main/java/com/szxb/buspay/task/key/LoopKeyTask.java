package com.szxb.buspay.task.key;

import android.os.SystemClock;
import android.util.Log;

import com.szxb.buspay.interfaces.OnKeyListener;
import com.szxb.buspay.task.thread.ThreadScheduledExecutorUtil;
import com.szxb.buspay.util.CountTime;
import com.szxb.buspay.util.tip.MainLooper;
import com.szxb.jni.libszxb;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.szxb.buspay.util.HexUtil.printHexBinary;
import static com.szxb.buspay.util.Util.checkEnterKey;

/**
 * 作者: Tangren on 2017-12-08
 * 包名：szxb.com.commonbus.task.scan
 * 邮箱：996489865@qq.com
 * TODO:按键监听
 */

public class LoopKeyTask {

    private long lastTime = 0;
    private OnKeyListener listener;

    private volatile static LoopKeyTask instance = null;

    private LoopKeyTask() {
    }

    public static LoopKeyTask getInstance() {
        if (instance == null) {
            synchronized (LoopKeyTask.class) {
                if (instance == null) {
                    instance = new LoopKeyTask();
                }
            }
        }
        return instance;
    }


    private ScheduledFuture<?> scheduledFuture;

    public void startLoopKey() {
        scheduledFuture = ThreadScheduledExecutorUtil.getInstance()
                .getService().scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        enterKey();
                    }
                }, 500, 200, TimeUnit.MILLISECONDS);

    }



    private void enterKey() {
        byte[] b = new byte[5];
        int device = libszxb.devicekey(b);
        if (device == -2) {
            libszxb.deviceReset();
        }

        final String resultCode = printHexBinary(b);
        if (!resultCode.equals("0000000000")) {
            MainLooper.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch (resultCode) {
                        case "0100000000":
                            if (filterRE()) return;
                            if (listener != null) {
                                Log.d("LoopKeyTask",
                                        "enterKey(LoopKeyTask.java:66)按键1");
                                //上
                                listener.onKeyUp();
                                CountTime.getInstance().startCnt();
                            }
                            break;
                        case "0001000000":
                            if (filterRE()) return;
                            if (listener != null) {
                                Log.d("LoopKeyTask",
                                        "enterKey(LoopKeyTask.java:66)按键2");
                                //确定
                                listener.onKeyOk();
                                CountTime.getInstance().startCnt();
                            }
                            break;
                        case "0000010000":
                            if (filterRE()) return;
                            if (listener != null) {
                                Log.d("LoopKeyTask",
                                        "enterKey(LoopKeyTask.java:66)按键3");
                                //取消
                                listener.onKeyCancel();
                                CountTime.getInstance().stopCnt();
                            }
                            break;
                        case "0000000100":
                            if (filterRE()) return;
                            if (listener != null) {
                                Log.d("LoopKeyTask",
                                        "enterKey(LoopKeyTask.java:60)按键4");
                                //下
                                listener.onKeyDown();
                                CountTime.getInstance().startCnt();
                            }
                            break;
                        default:
                            break;
                    }
                    lastTime = SystemClock.elapsedRealtime();
                }
            });

        }
    }

    //取消
    public void cancel() {
        if (scheduledFuture != null && !scheduledFuture.isCancelled()) {
            scheduledFuture.cancel(true);
//            if (listener != null) {
//                listener = null;
//            }
        }
    }

    private boolean filterRE() {
        return !checkEnterKey(SystemClock.elapsedRealtime(), lastTime);
    }

    public void setOnKeyListener(OnKeyListener listener) {
        this.listener = listener;
    }
}
