package com.szxb.buspay.test;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Unique;

/**
 * 作者：Tangren on 2018-09-18
 * 包名：com.szxb.buspay.test
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */
@Entity
public class PosSnEntity {
    @Id(autoincrement = true)
    private Long id;
    @Unique
    private String posSN;
    private String usered_time;
    private String cnt;
    private String str_1 = "0";
    private String str_2 = "0";
    private String str_3 = "0";
    @Generated(hash = 1627371510)
    public PosSnEntity(Long id, String posSN, String usered_time, String cnt,
            String str_1, String str_2, String str_3) {
        this.id = id;
        this.posSN = posSN;
        this.usered_time = usered_time;
        this.cnt = cnt;
        this.str_1 = str_1;
        this.str_2 = str_2;
        this.str_3 = str_3;
    }
    @Generated(hash = 1779060644)
    public PosSnEntity() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getPosSN() {
        return this.posSN;
    }
    public void setPosSN(String posSN) {
        this.posSN = posSN;
    }
    public String getUsered_time() {
        return this.usered_time;
    }
    public void setUsered_time(String usered_time) {
        this.usered_time = usered_time;
    }
    public String getCnt() {
        return this.cnt;
    }
    public void setCnt(String cnt) {
        this.cnt = cnt;
    }
    public String getStr_1() {
        return this.str_1;
    }
    public void setStr_1(String str_1) {
        this.str_1 = str_1;
    }
    public String getStr_2() {
        return this.str_2;
    }
    public void setStr_2(String str_2) {
        this.str_2 = str_2;
    }
    public String getStr_3() {
        return this.str_3;
    }
    public void setStr_3(String str_3) {
        this.str_3 = str_3;
    }

}
