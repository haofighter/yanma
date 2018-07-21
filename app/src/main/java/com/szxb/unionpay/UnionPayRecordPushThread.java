package com.szxb.unionpay;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.szxb.buspay.http.CallServer;
import com.szxb.buspay.http.HttpListener;
import com.szxb.buspay.http.JsonRequest;
import com.szxb.buspay.util.AppUtil;
import com.szxb.mlog.SLog;
import com.szxb.unionpay.entity.UnionPayEntity;
import com.szxb.unionpay.unionutil.ParseUtil;
import com.yanzhenjie.nohttp.rest.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者：Tangren on 2018-07-09
 * 包名：com.szxb.unionpay
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class UnionPayRecordPushThread extends Thread {
    @Override
    public void run() {
        super.run();
        try {
            if (!AppUtil.checkNetStatus()) {
                return;
            }
            pushWeb();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void pushWeb() {
        List<UnionPayEntity> unUpList = ParseUtil.unUpList();
        if (unUpList.size() == 0) {
            SLog.d("TimeTask(pushWeb.java:45)暂无未上传数据");
            return;
        }
        Map<String, Object> map = new HashMap<>();
        final JSONArray array = new JSONArray();
        for (UnionPayEntity payEntity : unUpList) {
            JSONObject object = new JSONObject();
            object.put("mchId", payEntity.getMchId());
            object.put("unionPosSn", payEntity.getUnionPosSn());
            object.put("posSn", payEntity.getPosSn());
            object.put("busNo", payEntity.getBusNo());
            object.put("totalFee", payEntity.getTotalFee());
            object.put("payFee", payEntity.getPayFee());
            object.put("resCode", payEntity.getResCode());
            object.put("time", payEntity.getTime());
            object.put("tradeSeq", payEntity.getTradeSeq());
            object.put("mainCardNo", payEntity.getMainCardNo());
            object.put("batchNum", payEntity.getBatchNum());
            object.put("bus_line_name", payEntity.getBus_line_name());
            object.put("bus_line_no", payEntity.getBus_line_no());
            object.put("driverNum", payEntity.getDriverNum());
            object.put("unitno", payEntity.getUnitno());
            object.put("upStatus", payEntity.getUpStatus());
            object.put("uniqueFlag", payEntity.getUniqueFlag());
            array.add(object);
        }
        SLog.d("TimeTask(pushWeb.java:67)请求数据:" + array.toJSONString());
        map.put("data", array.toJSONString());
        String url = "http://112.74.102.125/bipbus/interaction/bankjourAll";
        JsonRequest request = new JsonRequest(url);
        request.add(map);
        CallServer.getHttpclient().add(10101, request, new HttpListener<JSONObject>() {
            @Override
            public void success(int what, Response<JSONObject> response) {
                try {
                    JSONObject object = response.get();
                    SLog.d("TimeTask(success.java:73)" + object.toJSONString());
                    String rescode = object.getString("rescode");
                    if (TextUtils.equals(rescode, "0000")) {
                        JSONArray list = object.getJSONArray("datalist");
                        for (int i = 0; i < list.size(); i++) {
                            JSONObject ob = list.getJSONObject(i);
                            String uniqueFlag = ob.getString("uniqueFlag");
                            ParseUtil.updateUnionUpState(uniqueFlag);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void fail(int what, String e) {
                SLog.d("TimeTask(fail.java:88)上传失败:" + e);
            }
        });
    }
}
