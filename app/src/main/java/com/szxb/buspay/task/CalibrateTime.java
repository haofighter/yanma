package com.szxb.buspay.task;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.szxb.buspay.BusApp;
import com.szxb.buspay.http.CallServer;
import com.szxb.buspay.http.HttpListener;
import com.szxb.buspay.http.JsonRequest;
import com.szxb.buspay.util.tip.BusToast;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Response;

import static com.szxb.buspay.util.DateUtil.setTime;

/**
 * 作者：Tangren on 2018-01-22
 * 包名：com.szxb.Thread
 * 邮箱：996489865@qq.com
 * TODO:校准时间
 */

public class CalibrateTime {

    private volatile static CalibrateTime instance = null;

    private CalibrateTime() {
    }

    public static CalibrateTime getInstance() {
        if (instance == null) {
            synchronized (CalibrateTime.class) {
                if (instance == null) {
                    instance = new CalibrateTime();
                }
            }
        }
        return instance;
    }

    public void request() {
        String url = "http://134.175.56.14/bipeqt/interaction/getStandardTime";
        JsonRequest request = new JsonRequest(url, RequestMethod.POST);
        request.setRetryCount(5);
        CallServer.getHttpclient().add(0, request, new HttpListener<JSONObject>() {
            @Override
            public void success(int what, Response<JSONObject> response) {
                JSONObject object = response.get();
                String rescode = object.getString("rescode");
                if (TextUtils.equals("0000", rescode)) {
                    String time = object.getString("date");
                    setTime(time);
                }
            }

            @Override
            public void fail(int what, String e) {
                BusToast.showToast(BusApp.getInstance(), "请检查网络", false);
            }
        });
    }

}
