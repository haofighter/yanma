package com.szxb.buspay;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.lilei.tool.tool.IToolInterface;
import com.szxb.buspay.db.manager.DBCore;
import com.szxb.buspay.manager.PosManager;
import com.szxb.buspay.task.TaskDelFile;
import com.szxb.buspay.task.service.TimeSettleTask;
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
    //服务操作
    private IToolInterface mService;

    /**
     * 0：淄博
     * 1：泰安
     * 2：莱芜长运
     * 3：招远
     * 4：荣成
     * 5：潍坊
     */
    private static int city = 1;

    /**
     * taian.bin 泰安
     * zibo.bin 淄博
     * laiwu_cy.bin 莱芜长运
     * zhaoyuan.bin 招远
     */
    private static String binName = "taian.bin";

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        DBCore.init(this, "databases_bus.db");

        UnionPayManager unionPayManager = new UnionPayManager();
        BusllPosManage.init(unionPayManager);

        manager = new PosManager();
        manager.loadFromPrefs(city, binName);

        initLog();

        SoundPoolUtil.init(this);

        NoHttp.initialize(InitializationConfig.newBuilder(this)
                .networkExecutor(new OkHttpNetworkExecutor())
                .connectionTimeout(15 * 1000)
                .build());

        Intent timeSettleTaskIntent = new Intent(this, TimeSettleTask.class);
        startService(timeSettleTaskIntent);
        initService();
    }

    //连接服务
    private void initService() {
        Intent i = new Intent();
        i.setAction("com.lypeer.aidl");
        i.setPackage("com.lilei.tool.tool");
        boolean ret = bindService(i, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public IToolInterface getmService() {
        return mService;
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
            mService = IToolInterface.Stub.asInterface(service);
        }
    };


    public static BusApp getInstance() {
        return instance;
    }

    public static PosManager getPosManager() {
        if (manager == null) {
            manager = new PosManager();
            manager.loadFromPrefs(city, binName);
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
                .fileName(BusApp.getPosManager().getBusNo())
                .build();
        SLog.addLogAdapter(new DiskLogAdapter(formatStrategy));

        new TaskDelFile().del();
    }

}
