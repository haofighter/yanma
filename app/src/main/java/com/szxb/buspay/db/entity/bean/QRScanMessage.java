package com.szxb.buspay.db.entity.bean;

import com.szxb.buspay.db.entity.scan.PosRecord;

/**
 * 作者: Tangren on 2017-09-11
 * 包名：szxb.com.commonbus.entity
 * 邮箱：996489865@qq.com
 * TODO:扫码信息
 */

public class QRScanMessage {

    private PosRecord posRecord;
    private int result;

    public PosRecord getPosRecord() {
        return posRecord;
    }

    public void setPosRecord(PosRecord posRecord) {
        this.posRecord = posRecord;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public QRScanMessage(PosRecord posRecord, int result) {
        this.posRecord = posRecord;
        this.result = result;
    }

    @Override
    public String toString() {
        return "QRScanMessage{" +
                "posRecord=" + posRecord +
                ", result=" + result +
                '}';
    }
}
