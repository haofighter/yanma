package com.szxb.buspay.manager;

import com.szxb.buspay.db.entity.card.LineInfoEntity;

/**
 * 作者：Tangren on 2018-07-18
 * 包名：com.szxb.buspay.manager
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public interface IPosManager {
    void loadFromPrefs();

    String getLineName();

    void setLineName(String var1);

    String getLineNo();

    void setLineNo(String var1);

    int getBasePrice();

    void setBasePrice(int var1);

    int getWcPayPrice();

    void setWcPrice(int var1);

    void setUnionPayPrice(int price);

    int getUnionPayPrice();

    String getMac(String keyId);

    String getPublicKey(String keyId);

    long getOrderTime();

    String getmchTrxId();

    String geCityCode();

    int getInStationId();

    String getInStationName();

    String getChinese_name();

    void setChinese_name(String name);

    byte[] getKey();

    void setKey(String privateKey);

    String getBusNo();

    void setBusNo(String bus_no);

    String getAppId();

    void setAppId(String app_id);

    String getCityCode();

    void setCityCode();

    void setUnitno(String unitno);

    String getUnitno();

    void setDriverNo(String no);

    String getDriverNo();

    String getPosSN();

    LineInfoEntity getLineInfoEntity();

    void setLineInfoEntity();

    String getLastVersion();

    void setLastVersion(String version);

    String getBinVersion();

    String[]getCoefficent();

    void setCoefficent(String coefficent);

}
