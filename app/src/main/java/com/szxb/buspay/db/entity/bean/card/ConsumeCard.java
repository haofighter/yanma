package com.szxb.buspay.db.entity.bean.card;

import com.szxb.buspay.util.HexUtil;
import com.szxb.mlog.SLog;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Unique;

/**
 * 作者：Tangren on 2018-07-19
 * 包名：com.szxb.buspay.db.entity.bean.card
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */
@Entity
public class ConsumeCard {
    @Id(autoincrement = true)
    private Long id;
    //状态
    private String status;
    //卡类型
    private String cardType;
    //交易类型
    private String transType;
    //设备交易序号
    private String transNo;
    //卡号
    @Index
    private String cardNo;
    //卡余额
    private String cardBalance;
    //扣款金额
    private String payFee;
    //交易时间
    private String transTime;
    //用户卡脱机交易序号
    private String transNo2;
    //AC码
    private String tac;
    //线路号
    private String lineNo;
    //车号
    private String busNo;
    //当班司机号
    private String driverNo;
    //PSAM卡号
    private String pasmNo;
    //行驶方向
    private String direction;
    //站点号
    private String stationId;
    //补票标志
    private String fareFlag;

    //唯一标示
    @Unique
    private String uniqueFlag;
    //上传状态
    private Integer upStatus;
    //单条记录
    private String singleRecord;
    private String reserve_1;
    private String reserve_2;
    private String reserve_3;

    public ConsumeCard(byte[] receDatas, boolean isSignOrBack) {
        int index = 0;
        byte[] status_byte = new byte[1];
        System.arraycopy(receDatas, index, status_byte, 0, status_byte.length);
        status = HexUtil.printHexBinary(status_byte);

        byte[] cardType_byte = new byte[1];
        System.arraycopy(receDatas, index += status_byte.length, cardType_byte, 0, cardType_byte.length);
        cardType = HexUtil.printHexBinary(cardType_byte);

        byte[] transType_byte = new byte[1];
        System.arraycopy(receDatas, index += cardType_byte.length, transType_byte, 0, transType_byte.length);
        transType = HexUtil.printHexBinary(transType_byte);

        byte[] transNo_byte = new byte[3];
        System.arraycopy(receDatas, index += transType_byte.length, transNo_byte, 0, transNo_byte.length);
        transNo = HexUtil.printHexBinary(transNo_byte);

        byte[] cardNo_byte = new byte[8];
        System.arraycopy(receDatas, index += transNo_byte.length, cardNo_byte, 0, cardNo_byte.length);
        cardNo = HexUtil.bcd2Str(cardNo_byte);

        byte[] cardBalance_byte = new byte[3];
        System.arraycopy(receDatas, index += cardNo_byte.length, cardBalance_byte, 0, cardBalance_byte.length);
        cardBalance = HexUtil.printHexBinary(cardBalance_byte);

        byte[] payFee_byte = new byte[3];
        System.arraycopy(receDatas, index += cardBalance_byte.length, payFee_byte, 0, payFee_byte.length);
        payFee = HexUtil.printHexBinary(payFee_byte);

        byte[] transTime_byte = new byte[7];
        System.arraycopy(receDatas, index += payFee_byte.length, transTime_byte, 0, transTime_byte.length);
        transTime = HexUtil.bcd2Str(transTime_byte);

        byte[] transNo2_byte = new byte[2];
        System.arraycopy(receDatas, index += transTime_byte.length, transNo2_byte, 0, transNo2_byte.length);
        transNo2 = HexUtil.printHexBinary(transNo2_byte);

        byte[] tac_byte = new byte[4];
        System.arraycopy(receDatas, index += transNo2_byte.length, tac_byte, 0, tac_byte.length);
        tac = isSignOrBack ? HexUtil.bcd2Str(tac_byte) : HexUtil.printHexBinary(tac_byte);

        byte[] lineNoe_byte = new byte[2];
        System.arraycopy(receDatas, index += tac_byte.length, lineNoe_byte, 0, lineNoe_byte.length);
        lineNo = HexUtil.printHexBinary(lineNoe_byte);

        byte[] busNo_byte = new byte[3];
        System.arraycopy(receDatas, index += lineNoe_byte.length, busNo_byte, 0, busNo_byte.length);
        busNo = HexUtil.bcd2Str(busNo_byte);

        byte[] driverNo_byte = new byte[4];
        System.arraycopy(receDatas, index += busNo_byte.length, driverNo_byte, 0, driverNo_byte.length);
        driverNo = HexUtil.bcd2Str(driverNo_byte);

        byte[] pasmNo_byte = new byte[6];
        System.arraycopy(receDatas, index += driverNo_byte.length, pasmNo_byte, 0, pasmNo_byte.length);
        pasmNo = HexUtil.bcd2Str(pasmNo_byte);

        byte[] direction_byte = new byte[1];
        System.arraycopy(receDatas, index += pasmNo_byte.length, direction_byte, 0, direction_byte.length);
        direction = HexUtil.printHexBinary(direction_byte);

        byte[] stationId_byte = new byte[1];
        System.arraycopy(receDatas, index += direction_byte.length, stationId_byte, 0, stationId_byte.length);
        stationId = HexUtil.printHexBinary(stationId_byte);

        byte[] fareFlag_byte = new byte[1];
        System.arraycopy(receDatas, index + stationId_byte.length, fareFlag_byte, 0, fareFlag_byte.length);
        fareFlag = HexUtil.printHexBinary(fareFlag_byte);

        upStatus = 1;
        uniqueFlag = pasmNo + transNo + transTime;
        byte[] recordData = new byte[50];
        System.arraycopy(receDatas, 0, recordData, 0, recordData.length);
        singleRecord = HexUtil.printHexBinary(recordData);
        SLog.d("ConsumeCard(ConsumeCard.java:119)" + toString());
    }

    @Generated(hash = 1510813124)
    public ConsumeCard(Long id, String status, String cardType, String transType, String transNo, String cardNo, String cardBalance,
                       String payFee, String transTime, String transNo2, String tac, String lineNo, String busNo, String driverNo, String pasmNo,
                       String direction, String stationId, String fareFlag, String uniqueFlag, Integer upStatus, String singleRecord,
                       String reserve_1, String reserve_2, String reserve_3) {
        this.id = id;
        this.status = status;
        this.cardType = cardType;
        this.transType = transType;
        this.transNo = transNo;
        this.cardNo = cardNo;
        this.cardBalance = cardBalance;
        this.payFee = payFee;
        this.transTime = transTime;
        this.transNo2 = transNo2;
        this.tac = tac;
        this.lineNo = lineNo;
        this.busNo = busNo;
        this.driverNo = driverNo;
        this.pasmNo = pasmNo;
        this.direction = direction;
        this.stationId = stationId;
        this.fareFlag = fareFlag;
        this.uniqueFlag = uniqueFlag;
        this.upStatus = upStatus;
        this.singleRecord = singleRecord;
        this.reserve_1 = reserve_1;
        this.reserve_2 = reserve_2;
        this.reserve_3 = reserve_3;
    }

    @Generated(hash = 476783687)
    public ConsumeCard() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCardType() {
        return this.cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getTransType() {
        return this.transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public String getTransNo() {
        return this.transNo;
    }

    public void setTransNo(String transNo) {
        this.transNo = transNo;
    }

    public String getCardNo() {
        return this.cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getCardBalance() {
        return this.cardBalance;
    }

    public void setCardBalance(String cardBalance) {
        this.cardBalance = cardBalance;
    }

    public String getPayFee() {
        return this.payFee;
    }

    public void setPayFee(String payFee) {
        this.payFee = payFee;
    }

    public String getTransTime() {
        return this.transTime;
    }

    public void setTransTime(String transTime) {
        this.transTime = transTime;
    }

    public String getTransNo2() {
        return this.transNo2;
    }

    public void setTransNo2(String transNo2) {
        this.transNo2 = transNo2;
    }

    public String getTac() {
        return this.tac;
    }

    public void setTac(String tac) {
        this.tac = tac;
    }

    public String getLineNo() {
        return this.lineNo;
    }

    public void setLineNo(String lineNo) {
        this.lineNo = lineNo;
    }

    public String getBusNo() {
        return this.busNo;
    }

    public void setBusNo(String busNo) {
        this.busNo = busNo;
    }

    public String getDriverNo() {
        return this.driverNo;
    }

    public void setDriverNo(String driverNo) {
        this.driverNo = driverNo;
    }

    public String getPasmNo() {
        return this.pasmNo;
    }

    public void setPasmNo(String pasmNo) {
        this.pasmNo = pasmNo;
    }

    public String getDirection() {
        return this.direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getStationId() {
        return this.stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public String getFareFlag() {
        return this.fareFlag;
    }

    public void setFareFlag(String fareFlag) {
        this.fareFlag = fareFlag;
    }

    public Integer getUpStatus() {
        return this.upStatus;
    }

    public void setUpStatus(Integer upStatus) {
        this.upStatus = upStatus;
    }

    public String getReserve_1() {
        return this.reserve_1;
    }

    public void setReserve_1(String reserve_1) {
        this.reserve_1 = reserve_1;
    }

    public String getReserve_2() {
        return this.reserve_2;
    }

    public void setReserve_2(String reserve_2) {
        this.reserve_2 = reserve_2;
    }

    public String getReserve_3() {
        return this.reserve_3;
    }

    public void setReserve_3(String reserve_3) {
        this.reserve_3 = reserve_3;
    }

    public String getUniqueFlag() {
        return this.uniqueFlag;
    }

    public void setUniqueFlag(String uniqueFlag) {
        this.uniqueFlag = uniqueFlag;
    }

    public String getSingleRecord() {
        return this.singleRecord;
    }

    public void setSingleRecord(String singleRecord) {
        this.singleRecord = singleRecord;
    }

    @Override
    public String toString() {
        return "ConsumeCard{" +
                "id=" + id +
                ", status='" + status + '\'' +
                ", cardType='" + cardType + '\'' +
                ", transType='" + transType + '\'' +
                ", transNo='" + transNo + '\'' +
                ", cardNo='" + cardNo + '\'' +
                ", cardBalance='" + cardBalance + '\'' +
                ", payFee='" + payFee + '\'' +
                ", transTime='" + transTime + '\'' +
                ", transNo2='" + transNo2 + '\'' +
                ", tac='" + tac + '\'' +
                ", lineNo='" + lineNo + '\'' +
                ", busNo='" + busNo + '\'' +
                ", driverNo='" + driverNo + '\'' +
                ", pasmNo='" + pasmNo + '\'' +
                ", direction='" + direction + '\'' +
                ", stationId='" + stationId + '\'' +
                ", fareFlag='" + fareFlag + '\'' +
                ", uniqueFlag='" + uniqueFlag + '\'' +
                ", upStatus=" + upStatus +
                ", singleRecord='" + singleRecord + '\'' +
                ", reserve_1='" + reserve_1 + '\'' +
                ", reserve_2='" + reserve_2 + '\'' +
                ", reserve_3='" + reserve_3 + '\'' +
                '}';
    }
}
