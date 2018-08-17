package com.szxb.buspay.util.param;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.szxb.buspay.db.entity.scan.PosRecord;
import com.szxb.buspay.db.manager.DBManager;
import com.szxb.buspay.util.Config;
import com.szxb.buspay.util.DateUtil;
import com.szxb.buspay.util.param.sign.ParamSingUtil;

import java.util.HashMap;
import java.util.Map;

import static com.szxb.buspay.BusApp.getPosManager;

/**
 * @author 作者: Tangren on 2017/7/20
 *         包名：com.szxb.buspay.util
 *         邮箱：996489865@qq.com
 *         TODO:参数工具类
 */

public class ParamsUtil {
    /**
     * @param app_id    商户id
     * @param timestamp 请求时间
     * @return 通用map
     */
    public static Map<String, Object> commonMap(String app_id, String timestamp) {
        Map<String, Object> map = new HashMap<>();
        map.put("app_id", app_id);
        map.put("charset", "UTF-8");
        map.put("format", "json");
        map.put("timestamp", timestamp);
        map.put("version", "1.0");
        map.put("sign_type", "sha1withrsa");
        return map;
    }


    //扣款
    public static Map<String, Object> requestMap(PosRecord record) {
        JSONObject object = new JSONObject();
        object.put("open_id", record.getOpen_id());
        object.put("mch_trx_id", record.getMch_trx_id());
        object.put("order_time", record.getOrder_time());
        object.put("order_desc", record.getOrder_desc());
        object.put("total_fee", record.getTotal_fee());
        object.put("pay_fee", record.getPay_fee());
        object.put("city_code", record.getCity_code());
        object.put("exp_type", 0);
        object.put("charge_type", 0);
        object.put("bus_no", record.getBus_no());
        object.put("bus_line_name", record.getBus_line_name());
        object.put("pos_no", record.getPos_no());

        object.put("unitno", record.getUnitno());
        object.put("driveno", record.getDriveno());

        JSONObject ext = new JSONObject();
        ext.put("in_station_id", record.getIn_station_id());
        ext.put("in_station_name", record.getIn_station_name());
        object.put("ext", ext);

        JSONArray cord = new JSONArray();
        cord.add(record.getRecord());
        object.put("record", cord);
        DBManager.insert(object, record.getMch_trx_id(),
                record.getOpen_id(), record.getQr_code(),record.getPay_fee());//存储乘车记录


        JSONObject jsonObject = new JSONObject();
        JSONArray order_list = new JSONArray();
        order_list.add(object);
        jsonObject.put("order_list", order_list);

        String timestamp = DateUtil.getCurrentDate();
        String app_id = getPosManager().getAppId();
        Map<String, Object> map = commonMap(app_id, timestamp);
        map.put("sign", ParamSingUtil.getSign(app_id, timestamp, jsonObject, Config.private_key));
        map.put("biz_data", jsonObject.toString());
        return map;
    }


    /**
     * 公钥/MAC下载
     *
     * @return map
     */
    public static Map<String, Object> getkeyMap() {
        String timestamp = DateUtil.getCurrentDate();
        String app_id = getPosManager().getAppId();
        Map<String, Object> map = commonMap(app_id, timestamp);
        map.put("sign", ParamSingUtil.getSign(app_id, timestamp, null, Config.private_key));
        map.put("biz_data", "");
        return map;
    }


    /**
     * @return 黑名单下载列表map
     */
    public static Map<String, Object> getBlackListMap() {
        String timestamp = DateUtil.getCurrentDate();
        String app_id = getPosManager().getAppId();
        Map<String, Object> map = commonMap(app_id, timestamp);
        JSONObject object = new JSONObject();
        object.put("page_index", 1);
        object.put("page_size", 100);

        object.put("begin_time", DateUtil.getLastDate("yyyy-MM-dd HH:mm:ss"));
        object.put("end_time", DateUtil.getCurrentDate());

        map.put("sign", ParamSingUtil.getSign(app_id, timestamp, object, Config.private_key));
        map.put("biz_data", object.toString());
        return map;
    }



}
