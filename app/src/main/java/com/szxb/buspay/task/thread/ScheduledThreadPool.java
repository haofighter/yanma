package com.szxb.buspay.task.thread;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 作者：Tangren on 2018-09-04
 * 包名：com.szxb.jcbus.task.schedule
 * 邮箱：996489865@qq.com
 * TODO:周期性线程池
 */

public class ScheduledThreadPool {

    private ScheduledThreadPoolExecutor scheduledThreadPool;

    private Map<String, Future> futureMap = new HashMap<>();

    public ScheduledThreadPool(int scheduledPoolSize) {
        scheduledThreadPool = new ScheduledThreadPoolExecutor(scheduledPoolSize);
    }

    //循环执行
    public void executeCycle(Runnable runnable, int delay, int period, String tag, TimeUnit unit) {
        Future future = this.scheduledThreadPool.scheduleAtFixedRate(runnable, delay, period, unit);
        futureMap.put(tag, future);
    }

    //延迟执行
    public void executeDelay(Runnable runnable, int delay, TimeUnit unit) {
        this.scheduledThreadPool.schedule(runnable, delay, unit);
    }

    //立即执行
    public void execute(Runnable runnable) {
        this.scheduledThreadPool.execute(runnable);
    }

    //是否在线程池中执行
    public boolean isRunningInPool(String tag) {
        return futureMap.get(tag) != null;
    }

    public void stopTask(String tag) {
        Future future = this.futureMap.get(tag);
        if (future != null) {
            future.cancel(true);
            futureMap.remove(tag);
        }
    }

    public void shutDown() {
        if (!scheduledThreadPool.isShutdown()) {
            this.scheduledThreadPool.shutdown();
        }
    }
}
