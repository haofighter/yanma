package com.szxb.buspay;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.szxb.buspay.db.manager.DBCore;
import com.szxb.buspay.db.manager.DBManager;
import com.szxb.buspay.util.DateUtil;

/**
 * 作者：Tangren on 2018-07-23
 * 包名：com.szxb.buspay
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class TestActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_cnt);
        DBCore.getDaoSession().getScanInfoEntityDao().deleteAll();
        String startTime = DateUtil.getTime("yyyy-MM-dd HH:mm:ss", 0, 0, 0, 0);
        String endTime = DateUtil.getTime("yyyy-MM-dd HH:mm:ss", 23, 59, 59, 999);

        String sql = " SELECT SUM(PAY_FEE) as amount , COUNT(*) as cnt  from UNION_PAY_ENTITY WHERE TIME BETWEEN "
                + "'" + startTime + "'"
                + " AND "
                + "'" + endTime + "'"
                + " AND ( RES_CODE='00' OR RES_CODE='A2' OR RES_CODE='A4' OR RES_CODE='A5' OR RES_CODE='A6'  ) ";

        int[] cnt = DBManager.cnt(sql);
        for (int i : cnt) {
            Log.d("TestActivity",
                "onCreate(TestActivity.java:35)"+cnt[i]);
        }

    }
}
