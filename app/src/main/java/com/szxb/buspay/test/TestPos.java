package com.szxb.buspay.test;

import com.szxb.buspay.db.manager.DBCore;
import com.szxb.buspay.db.sp.CommonSharedPreferences;

import java.util.List;

/**
 * 作者：Tangren on 2018-09-18
 * 包名：com.szxb.buspay.test
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class TestPos implements ITest {


    private int snID;
    private int unID;
    private int max = 0;
    private List<PosSnEntity> list;


    @Override
    public void load() {
        snID = (Integer) CommonSharedPreferences.get("id_pos_", 0);
        unID = (Integer) CommonSharedPreferences.get("id_un_", 0);
        max = (int) DBCore.getDaoSession().getPosSnEntityDao().count();
        list = DBCore.getDaoSession().getPosSnEntityDao().queryBuilder().list();
    }

    @Override
    public int getSNID() {
        return snID;
    }

    @Override
    public int getUnID() {
        return unID;
    }

    @Override
    public void setSNID() {
        snID++;
        if (snID == max - 1) {
            snID = 0;
        }
        CommonSharedPreferences.put("id_pos_", snID);
    }

    @Override
    public void setUnID() {
        unID++;
        if (unID == max - 1) {
            unID = 0;
        }
        CommonSharedPreferences.put("id_un_", unID);
    }

    @Override
    public List<PosSnEntity> getPosList() {
        return list;
    }

    @Override
    public void setPosList(List<PosSnEntity> list) {
        this.list = list;
    }
}
