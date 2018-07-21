package com.szxb.buspay;

import android.app.Application;

import com.szxb.buspay.db.manager.DBCore;
import com.szxb.buspay.manager.PosManager;
import com.szxb.buspay.util.sound.SoundPoolUtil;
import com.szxb.java8583.module.manager.BusllPosManage;
import com.szxb.mlog.AndroidLogAdapter;
import com.szxb.mlog.CsvFormatStrategy;
import com.szxb.mlog.DiskLogAdapter;
import com.szxb.mlog.FormatStrategy;
import com.szxb.mlog.PrettyFormatStrategy;
import com.szxb.mlog.SLog;
import com.szxb.unionpay.config.UnionPayManager;
import com.yanzhenjie.nohttp.InitializationConfig;
import com.yanzhenjie.nohttp.Logger;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.OkHttpNetworkExecutor;

/**
 * 作者：Tangren on 2018-07-18
 * 包名：com.szxb.buspay
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class BusApp extends Application {
    private static BusApp instance;
    private static PosManager manager;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        DBCore.init(this, "BUS_INFO");

        UnionPayManager unionPayManager = new UnionPayManager();
        BusllPosManage.init(unionPayManager);

        manager = new PosManager();
        manager.loadFromPrefs();
        SoundPoolUtil.init(this);
        initLog();

        NoHttp.initialize(InitializationConfig.newBuilder(this)
                .networkExecutor(new OkHttpNetworkExecutor())
                .connectionTimeout(10 * 1000)
                .build());
        Logger.setDebug(true);
    }

    public static BusApp getInstance() {
        return instance;
    }

    public static PosManager getPosManager() {
        if (manager == null) {
            manager = new PosManager();
            manager.loadFromPrefs();
        }
        return manager;
    }

    private void initLog() {
        FormatStrategy format = PrettyFormatStrategy.newBuilder()
                .tag("ziBoBus:")
                .showThreadInfo(false)
                .methodCount(1)
                .build();
        SLog.addLogAdapter(new AndroidLogAdapter(format));
        FormatStrategy formatStrategy = CsvFormatStrategy.newBuilder()
                .tag("ziBoBus:")
                .fileName("LLLL")
                .build();
        SLog.addLogAdapter(new DiskLogAdapter(formatStrategy));
    }

}
