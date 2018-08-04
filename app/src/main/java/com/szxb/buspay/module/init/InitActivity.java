package com.szxb.buspay.module.init;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.TextView;

import com.szxb.buspay.BusApp;
import com.szxb.buspay.MainActivity;
import com.szxb.buspay.R;
import com.szxb.buspay.db.entity.card.LineInfoEntity;
import com.szxb.buspay.interfaces.InitOnListener;
import com.szxb.buspay.task.thread.ThreadScheduledExecutorUtil;
import com.szxb.buspay.util.Config;
import com.szxb.buspay.util.tip.BusToast;
import com.szxb.buspay.util.tip.MainLooper;
import com.szxb.java8583.core.Iso8583Message;
import com.szxb.java8583.core.Iso8583MessageFactory;
import com.szxb.java8583.module.ParamDownload;
import com.szxb.java8583.module.SignIn;
import com.szxb.java8583.module.manager.BusllPosManage;
import com.szxb.java8583.quickstart.SingletonFactory;
import com.szxb.java8583.quickstart.special.SpecialField62;
import com.szxb.jni.libszxb;
import com.szxb.mlog.SLog;
import com.szxb.unionpay.UnionPay;
import com.szxb.unionpay.unionutil.ParseUtil;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.szxb.unionpay.unionutil.ParseUtil.parseMackey;

/**
 * 作者：Tangren on 2018-08-03
 * 包名：com.szxb.buspay
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class InitActivity extends AppCompatActivity implements InitOnListener {

    private boolean lineOk = false;
    private boolean wcOk = false;
    private boolean binOk = false;

    private TextView update_info;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);
        update_info = (TextView) findViewById(R.id.update_info);
        TextView tip_info = (TextView) findViewById(R.id.tip_info);
        tip_info.setText(String.format("温馨提示:\n\t\t\t\t%1$s", Config.tip()));
        update_info.setText("微信同步中\n");
        PosInit init = new PosInit();
        init.setOnCallBack(this);
        update_info.append("bin初始化中\n");
        initBin();
        initUnionPay();
        init.init();
        init.downLoadBlack();
        LineInfoEntity lineInfoEntity = BusApp.getPosManager().getLineInfoEntity();
        update_info.append("线路文件同步中\n");
        if (lineInfoEntity != null) {
            init.download(lineInfoEntity.getFileName());
        } else {
            lineOk = true;
            update_info.append("线路暂未设置\n");
        }
    }

    private void initBin() {
        ThreadScheduledExecutorUtil.getInstance().getService().submit(new Runnable() {
            @Override
            public void run() {
                String lastVersion = BusApp.getPosManager().getLastVersion();
                String binName = BusApp.getPosManager().getBinVersion();
                if (!TextUtils.equals(lastVersion, binName)) {
                    AssetManager ass = BusApp.getInstance().getAssets();
                    int k = libszxb.ymodemUpdate(ass, binName);
                    BusApp.getPosManager().setLastVersion(binName);
                    binOk = true;
                    BusToast.showToast(BusApp.getInstance(), "固件更新成功", true);
                    if (lineOk && wcOk) {
                        startActivity(new Intent(InitActivity.this, MainActivity.class));
                        finish();
                    }
                } else {
                    binOk = true;
                }
                MainLooper.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        update_info.append("bin更新完成\n");
                    }
                });
            }
        });
    }

    private static int aidCnt = 0;

    public void initUnionPay() {
        boolean isSuppUnionPay = BusApp.getPosManager().isSuppUnionPay();
        if (!isSuppUnionPay) {
            return;
        }
        update_info.append("银联签到中\n");
        Observable.create(new Observable.OnSubscribe<byte[]>() {
            @Override
            public void call(Subscriber<? super byte[]> subscriber) {
                //1.签到
                BusllPosManage.getPosManager().setTradeSeq();
                Iso8583Message message = SignIn.getInstance().message(BusllPosManage.getPosManager().getTradeSeq());
                byte[] exe = UnionPay.getInstance().exeSyncSSL(message.getBytes());
                if (exe == null) {
                    SLog.d("InitZipActivity(call.java:83)签到失败>>");
                } else {
                    Iso8583MessageFactory factory = SingletonFactory.forQuickStart();
                    factory.setSpecialFieldHandle(62, new SpecialField62());
                    Iso8583Message message0810 = factory.parse(exe);

                    if (message0810.getValue(39).getValue().equals("00")) {
                        String batchNum = message0810.getValue(60).getValue().substring(2, 8);
                        BusllPosManage.getPosManager().setBatchNum(batchNum);
                        parseMackey(message0810.getValue(62).getValue());
                    }
                    SLog.d("InitZipActivity(call.java:89)签到\n" + message0810.toFormatString());
                }
                if (ParseUtil.isUpdateParams()) {
                    subscriber.onNext(exe);
                } else {
                    subscriber.isUnsubscribed();
                }
            }
        }).flatMap(new Func1<byte[], Observable<byte[]>>() {
            @Override
            public Observable<byte[]> call(byte[] bytes) {
                //2.查询需要下载的参数
                byte[] exe = UnionPay.getInstance().exeSyncSSL(ParamDownload.getInstance().aidMessage().getBytes());
                return Observable.just(exe);
            }
        }).flatMap(new Func1<byte[], Observable<String>>() {
            @Override
            public Observable<String> call(byte[] bytes) {
                if (bytes == null) {
                    SLog.d("InitZipActivity(call.java:118)参数查询失败>>");
                } else {
                    SLog.d("InitZipActivity(call.java:120)查询参数成功,开始下载参数>>");
                    Iso8583MessageFactory factory = SingletonFactory.forQuickStart();
                    factory.setSpecialFieldHandle(62, new SpecialField62());
                    Iso8583Message message0810 = factory.parse(bytes);
                    if (message0810.getValue(39).getValue().equals("00")) {
                        ParseUtil.setParmaInfo(message0810.getValue(62).getValue());
                    }

                }
                String adiList = BusllPosManage.getPosManager().aidIndexList();
                SLog.d("InitZipActivity(call.java:125)indexList:" + adiList);
                String macs[] = adiList.split(",");
                aidCnt = macs.length;
                return Observable.from(macs);
            }
        }).flatMap(new Func1<String, Observable<byte[]>>() {
            @Override
            public Observable<byte[]> call(String s) {
                //3.依次下载参数
                SLog.d("InitZipActivity(call.java:140)下载参数中>>");
                byte[] exe = UnionPay.getInstance().exeSyncSSL(ParamDownload.getInstance().messageAID(s).getBytes());
                return Observable.just(exe);
            }
        }).flatMap(new Func1<byte[], Observable<Integer>>() {
            @Override
            public Observable<Integer> call(byte[] bytes) {
                Iso8583MessageFactory factory = SingletonFactory.forQuickStart();
                factory.setSpecialFieldHandle(62, new SpecialField62());
                Iso8583Message message0810 = factory.parse(bytes);
                if (message0810.getValue(39).getValue().equals("00")) {
                    ParseUtil.save_ic_params(message0810.getValue(62).getValue());
                }
                SLog.d("InitZipActivity(call.java:153)参数剩余" + aidCnt + "个\n" + message0810.toFormatString());
                aidCnt -= 1;
                return Observable.just(aidCnt);
            }
        }).filter(new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer integer) {
                return integer <= 0;
            }
        }).flatMap(new Func1<Integer, Observable<byte[]>>() {
            @Override
            public Observable<byte[]> call(Integer integer) {
                //4.下载参数结束
                byte[] exe = UnionPay.getInstance().exeSyncSSL(ParamDownload.getInstance().messageAIDEnd().getBytes());
                return Observable.just(exe);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<byte[]>() {
                    @Override
                    public void call(byte[] bytes) {
                        Iso8583MessageFactory factory = SingletonFactory.forQuickStart();
                        factory.setSpecialFieldHandle(62, new SpecialField62());
                        Iso8583Message message0810 = factory.parse(bytes);
                        if (message0810.getValue(39).getValue().equals("00")) {
                            SLog.d("InitZipActivity(call.java:176)参数下载结束>>");
                            BusllPosManage.getPosManager().setCurrentUpdateTime(System.currentTimeMillis() / 1000);
                        }
                        SLog.d("InitZipActivity(call.java:178)" + message0810.toFormatString());
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        SLog.d("InitZipActivity(call.java:183)更新失败:" + throwable.toString());
                    }
                });
        update_info.append("银联签到完成\n");
    }


    @Override
    public void onCallBack(boolean isOk) {
        wcOk = true;
        SLog.d("InitActivity(onCallBack.java:94)微信初始化结束");
        update_info.append("微信同步完成\n");
        if (lineOk && binOk) {
            startActivity(new Intent(InitActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    public void onFtpCallBack() {
        lineOk = true;
        SLog.d("InitActivity(onCallBack.java:106)FTP始化结束");
        update_info.append("线路文件同步完成\n");
        if (wcOk && binOk) {
            startActivity(new Intent(InitActivity.this, MainActivity.class));
            finish();
        }
    }
}
