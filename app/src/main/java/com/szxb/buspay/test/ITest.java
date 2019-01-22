package com.szxb.buspay.test;

import java.util.List;

/**
 * 作者：Tangren on 2018-09-18
 * 包名：com.szxb.buspay.test
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public interface ITest {

    void load();

    int getSNID();

    void setSNID();

    int getUnID();

    void setUnID();

    List<PosSnEntity> getPosList();

    void setPosList(List<PosSnEntity> list);
}
