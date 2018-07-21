package com.szxb.buspay.task.thread;

import android.text.TextUtils;

import com.szxb.buspay.db.dao.ConsumeCardDao;
import com.szxb.buspay.db.entity.bean.card.ConsumeCard;
import com.szxb.buspay.db.manager.DBCore;

/**
 * 作者：Tangren on 2018-07-20
 * 包名：com.szxb.buspay.task.thread
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class RecordThread extends Thread {

    private Object record;

    public RecordThread(String name, Object record) {
        super(name);
        this.record = record;
    }

    @Override
    public void run() {
        super.run();
        String name = getName() == null ? "zibo" : getName();
        if (TextUtils.equals(name, "zibo")) {
            //淄博
            ConsumeCard consumeCard = (ConsumeCard) record;
            ConsumeCardDao dao = DBCore.getDaoSession().getConsumeCardDao();
            dao.insertOrReplace(consumeCard);
        }

    }
}
