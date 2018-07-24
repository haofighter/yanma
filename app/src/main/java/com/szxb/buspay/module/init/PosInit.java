package com.szxb.buspay.module.init;

import android.os.Environment;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.szxb.buspay.BusApp;
import com.szxb.buspay.db.dao.LineInfoEntityDao;
import com.szxb.buspay.db.entity.bean.FTPEntity;
import com.szxb.buspay.db.entity.card.LineInfoEntity;
import com.szxb.buspay.db.entity.scan.MacKeyEntity;
import com.szxb.buspay.db.entity.scan.PublicKeyEntity;
import com.szxb.buspay.db.manager.DBManager;
import com.szxb.buspay.http.JsonRequest;
import com.szxb.buspay.interfaces.InitOnListener;
import com.szxb.buspay.util.Config;
import com.szxb.buspay.util.DateUtil;
import com.szxb.buspay.util.HexUtil;
import com.szxb.buspay.util.ftp.FTP;
import com.szxb.buspay.util.param.ParamsUtil;
import com.szxb.buspay.util.param.sign.FileByte;
import com.szxb.buspay.util.tip.BusToast;
import com.szxb.mlog.SLog;
import com.yanzhenjie.nohttp.rest.Response;
import com.yanzhenjie.nohttp.rest.SyncRequestExecutor;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func3;
import rx.schedulers.Schedulers;

import static com.szxb.buspay.db.manager.DBCore.getDaoSession;

/**
 * 作者: Tangren on 2017-09-12
 * 包名：szxb.com.commonbus.module.init
 * 邮箱：996489865@qq.com
 * TODO:扫码初始化数据
 */

public class PosInit {

    private Subscription subscribe;

    public void init() {
        Observable<Boolean> macKey = Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                JsonRequest macRequest = new JsonRequest(Config.MAC_KEY);
                macRequest.set(ParamsUtil.getkeyMap());
                Response<JSONObject> execute = SyncRequestExecutor.INSTANCE.execute(macRequest);
                if (execute.isSucceed()) {
                    SLog.d("PosInit(call.java:61)" + execute.get().toJSONString());
                    String macMsg = execute.get().getString("retmsg");
                    if (!TextUtils.isEmpty(macMsg) && TextUtils.equals(macMsg, "success")) {
                        final JSONArray array = execute.get().getJSONArray("mackey_list");
                        for (int i = 0; i < array.size(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            MacKeyEntity macKeyEntity = new MacKeyEntity();
                            macKeyEntity.setTime(DateUtil.getCurrentDate());
                            macKeyEntity.setKey_id(object.getString("key_id"));
                            macKeyEntity.setPubkey(object.getString("mackey"));
                            getDaoSession().insertOrReplace(macKeyEntity);
                        }
                        subscriber.onNext(true);
                    } else {
                        subscriber.onNext(false);
                    }

                } else {
                    subscriber.onError(execute.getException());
                }
            }
        });

        Observable<Boolean> publicKey = Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                JsonRequest publicKeyRequest = new JsonRequest(Config.PUBLIC_KEY);
                publicKeyRequest.set(ParamsUtil.getkeyMap());
                Response<JSONObject> execute = SyncRequestExecutor.INSTANCE.execute(publicKeyRequest);
                if (execute.isSucceed()) {
                    String pubMsg = execute.get().getString("retmsg");
                    if (!TextUtils.isEmpty(pubMsg) && TextUtils.equals(pubMsg, "success")) {
                        SLog.d("PosInit(call.java:93)" + execute.get().toJSONString());
                        JSONArray pKeyarray = execute.get().getJSONArray("pubkey_list");
                        for (int i = 0; i < pKeyarray.size(); i++) {
                            JSONObject object = pKeyarray.getJSONObject(i);
                            PublicKeyEntity entity = new PublicKeyEntity();
                            entity.setKey_id(object.getString("key_id"));
                            entity.setPubkey(object.getString("pubkey"));
                            getDaoSession().insertOrReplace(entity);
                        }
                        subscriber.onNext(true);
                    } else {
                        subscriber.onNext(false);
                    }

                } else {
                    subscriber.onError(execute.getException());
                }
            }
        });
        Observable<Boolean> blackList = Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                JsonRequest blackListRequest = new JsonRequest(Config.BLACK_LIST);
                blackListRequest.set(ParamsUtil.getBlackListMap());
                Response<JSONObject> execute = SyncRequestExecutor.INSTANCE.execute(blackListRequest);
                if (execute.isSucceed()) {
                    SLog.d("PosInit(call.java:118)微信黑名单下载成功" + execute.get().toJSONString());
                    String bLMsg = execute.get().getString("retmsg");
                    if (!TextUtils.isEmpty(bLMsg) && TextUtils.equals(bLMsg, "ok")) {
                        final JSONArray array = execute.get().getJSONArray("black_list");
                        if (array == null || array.isEmpty()) {
                            subscriber.onNext(true);
                            return;
                        }
                        DBManager.addBlackList(array);
                        subscriber.onNext(true);
                    } else {
                        subscriber.onNext(false);
                    }
                } else {
                    subscriber.onError(execute.getException());
                }
            }
        });

        subscribe = Observable.zip(macKey, publicKey, blackList, new Func3<Boolean, Boolean, Boolean, Boolean>() {
            @Override
            public Boolean call(Boolean aBoolean, Boolean aBoolean2, Boolean aBoolean3) {
                return aBoolean && aBoolean2 && aBoolean3;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        SLog.d("PosInit(call.java:147)扫码初始化:" + aBoolean);
                        BusToast.showToast(BusApp.getInstance(), aBoolean ? "扫码初始化成功" : "扫码初始化失败", aBoolean);
                        if (listener != null) {
                            listener.onCallBack(aBoolean);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        SLog.d("PosInit(call.java:156)扫码初始化异常:" + throwable.toString());
                        BusToast.showToast(BusApp.getInstance(), "网络或服务器异常", false);
                        if (listener != null) {
                            listener.onCallBack(false);
                        }
                    }
                });

    }

    public void download(final String fileName) {
        final FTPEntity ftpEntity = BusApp.getPosManager().getFTP();
        Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                SLog.d("PosInit(call.java:172)开始下载版本文件>>>>>");
                boolean paramVersionisOk = new FTP()
                        .builder(ftpEntity.getI())
                        .setPort(ftpEntity.getP())
                        .setLogin(ftpEntity.getU(), ftpEntity.getPsw())
                        .setFileName("allline.json")
                        .setPath(Environment.getExternalStorageDirectory() + "/")
                        .setFTPPath("pram/allline.json")
                        .download();
                SLog.d("PosInit(call.java:181)版本文件下载成功:" + paramVersionisOk);
                subscriber.onNext(paramVersionisOk);
            }
        }).flatMap(new Func1<Boolean, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(Boolean aBoolean) {
                if (!aBoolean) {
                    SLog.d("PosInit(call.java:188)版本文件下载失败>>判断线路文件是否存在");
                    LineInfoEntity infoEntity = BusApp.getPosManager().getLineInfoEntity();
                    if (infoEntity == null) {
                        SLog.d("PosInit(call.java:191)版本文件下载失败>>线路文件不存在>>直接下载线路文件");
                        return Observable.just(true);
                    }
                    SLog.d("PosInit(call.java:194)版本文件下载失败>>线路文件存在>>本次跳过更新");
                    return Observable.just(false);
                }
                byte[] PramVesion = FileByte.File2byte(Environment.getExternalStorageDirectory() + "/" + "allline.json");
                try {
                    JSONObject jsonObject = JSONObject.parseObject(new String(PramVesion, "GB2312"));
                    JSONArray allline = jsonObject.getJSONArray("allline");
                    LineInfoEntity infoEntity = BusApp.getPosManager().getLineInfoEntity();
                    if (infoEntity == null) {
                        return Observable.just(true);
                    }
                    for (int i = 0; i < allline.size(); i++) {
                        JSONObject ob = allline.getJSONObject(i);
                        String acnt = ob.getString("acnt");
                        String routeno = ob.getString("routeno");

                        String routeversion = ob.getString("routeversion");
                        String fileName_ = acnt + "," + routeno + ".json";

//                        SLog.d("PosInit(call.java:213)routeversion="+routeversion+",rout="+infoEntity.getVersion()+",fileName_="+fileName_+",fileName="+fileName);
                        if (TextUtils.equals(fileName_, infoEntity.getRmk1())) {
                            if (TextUtils.equals(routeversion, infoEntity.getVersion())) {
                                SLog.d("PosInit(call.java:200)版本相同无需更新");
                                return Observable.just(false);
                            }
                        }
                    }
                    SLog.d("PosInit(call.java:188)" + jsonObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return Observable.just(true);
            }
        }).flatMap(new Func1<Boolean, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(Boolean aBoolean) {
                boolean lineInfo = false;
                if (aBoolean) {
                    lineInfo = new FTP()
                            .builder(ftpEntity.getI())
                            .setPort(ftpEntity.getP())
                            .setLogin(ftpEntity.getU(), ftpEntity.getPsw())
                            .setFileName(fileName)
                            .setPath(Environment.getExternalStorageDirectory() + "/")
                            .setFTPPath("pram/" + fileName)
                            .download();
                    SLog.d("PosInit(call.java:226)线路文件下载成功>>>" + lineInfo);
                    if (lineInfo) {
                        byte[] line = FileByte.File2byte(Environment.getExternalStorageDirectory() + "/" + fileName);
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = JSONObject.parseObject(new String(line, "GB2312"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        if (jsonObject == null) {
                            return Observable.just(false);
                        }
                        String s = jsonObject.toJSONString();
                        if (!TextUtils.isEmpty(s)) {
                            JSONObject object = JSONObject.parseObject(s);
                            SLog.d("PosInit(call.java:192)线路信息>>" + object.toJSONString());
                            LineInfoEntityDao dao = getDaoSession().getLineInfoEntityDao();
                            LineInfoEntity onLineInfo = new LineInfoEntity();
                            onLineInfo.setLine(object.getString("line"));
                            onLineInfo.setVersion(object.getString("version"));
                            onLineInfo.setUp_station(object.getString("up_station"));
                            onLineInfo.setDwon_station(object.getString("down_station"));
                            onLineInfo.setChinese_name(object.getString("chinese_name"));
                            onLineInfo.setIs_fixed_price(object.getString("is_fixed_price"));
                            onLineInfo.setIs_keyboard(object.getString("is_keyboard"));
                            onLineInfo.setFixed_price(object.getString("fixed_price"));
                            onLineInfo.setCoefficient(object.getString("coefficient"));
                            onLineInfo.setShortcut_price(object.getString("shortcut_price"));
                            onLineInfo.setRmk1(fileName);
                            //先删除所有
                            dao.deleteAll();
                            dao.insertOrReplace(onLineInfo);

                            HexUtil.parseLine(onLineInfo);

                            BusApp.getPosManager().setLineInfoEntity();
                        }
                    }
                }
                return Observable.just(lineInfo);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (listener != null) {
                            listener.onFtpCallBack();
                        }
                        BusToast.showToast(BusApp.getInstance(), aBoolean ? "线路信息初始化完成" : "线路信息初始化失败", aBoolean);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        SLog.d("PosInit(call.java:228)参数下载失败>>" + throwable.toString());
                        BusToast.showToast(BusApp.getInstance(), "线路信息初始化异常\n" + throwable.toString(), false);
                        if (listener != null) {
                            listener.onFtpCallBack();
                        }
                    }
                });

    }

    //下载指定文件
    public void downloadAppointFile(final String fileName, final String busNo) {
        Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                FTPEntity ftpEntity = BusApp.getPosManager().getFTP();
                boolean lineInfo = new FTP()
                        .builder(ftpEntity.getI())
                        .setPort(ftpEntity.getP())
                        .setLogin(ftpEntity.getU(), ftpEntity.getPsw())
                        .setFileName(fileName)
                        .setPath(Environment.getExternalStorageDirectory() + "/")
                        .setFTPPath("pram/" + fileName)
                        .download();
                SLog.d("PosInit(call.java:179)fileName=" + fileName + "下载成功=" + lineInfo);
                subscriber.onNext(lineInfo);
            }
        }).flatMap(new Func1<Boolean, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(Boolean aBoolean) {
                if (aBoolean) {
                    byte[] line = FileByte.File2byte(Environment.getExternalStorageDirectory() + "/" + fileName);
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = JSONObject.parseObject(new String(line, "GB2312"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    if (jsonObject == null) {
                        return Observable.just(false);
                    }
                    String s = jsonObject.toJSONString();
                    if (!TextUtils.isEmpty(s)) {
                        JSONObject object = JSONObject.parseObject(s);
                        SLog.d("PosInit(call.java:192)线路信息>>" + object.toJSONString());
                        LineInfoEntityDao dao = getDaoSession().getLineInfoEntityDao();
                        LineInfoEntity onLineInfo = new LineInfoEntity();
                        onLineInfo.setLine(object.getString("line"));
                        onLineInfo.setVersion(object.getString("version"));
                        onLineInfo.setUp_station(object.getString("up_station"));
                        onLineInfo.setDwon_station(object.getString("down_station"));
                        onLineInfo.setChinese_name(object.getString("chinese_name"));
                        onLineInfo.setIs_fixed_price(object.getString("is_fixed_price"));
                        onLineInfo.setIs_keyboard(object.getString("is_keyboard"));
                        onLineInfo.setFixed_price(object.getString("fixed_price"));
                        onLineInfo.setCoefficient(object.getString("coefficient"));
                        onLineInfo.setShortcut_price(object.getString("shortcut_price"));
                        onLineInfo.setRmk1(fileName);
                        //先删除所有
                        dao.deleteAll();
                        dao.insertOrReplace(onLineInfo);


                        HexUtil.parseLine(onLineInfo, busNo);

                        BusApp.getPosManager().setLineInfoEntity();
                    }
                }
                return Observable.just(aBoolean);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        BusToast.showToast(BusApp.getInstance(), aBoolean ? "线路更新成功" : "线路更新失败", aBoolean);
                        if (listener != null) {
                            listener.onFtpCallBack();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        SLog.d("PosInit(call.java:228)参数下载失败>>" + throwable.toString());
                        BusToast.showToast(BusApp.getInstance(), "线路更新异常\n" + throwable.toString(), false);
                        if (listener != null) {
                            listener.onFtpCallBack();
                        }
                    }
                });
    }

    public class RetryWithDelay implements
            Func1<Observable<? extends Throwable>, Observable<?>> {

        private final int maxRetries;
        private final int retryDelayMillis;
        private int retryCount;

        RetryWithDelay(int maxRetries, int retryDelayMillis) {
            this.maxRetries = maxRetries;
            this.retryDelayMillis = retryDelayMillis;
        }

        @Override
        public Observable<?> call(Observable<? extends Throwable> attempts) {
            return attempts
                    .flatMap(new Func1<Throwable, Observable<?>>() {
                        @Override
                        public Observable<?> call(Throwable throwable) {
                            if (++retryCount <= maxRetries) {
                                SLog.d("RetryWithDelay(call.java:323)" + "get error, it will try after " + retryDelayMillis
                                        + " millisecond, retry count " + retryCount);
                                return Observable.timer(retryDelayMillis,
                                        TimeUnit.MILLISECONDS);
                            }
                            return Observable.error(throwable);
                        }
                    });
        }
    }


    public void setOnCallBack(InitOnListener listener) {
        this.listener = listener;
    }

    private InitOnListener listener;
}
