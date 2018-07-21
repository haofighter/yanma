package com.szxb.buspay.db.entity.card;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Evergarden on 2017/8/17.
 */

//        0	1	HEX	卡类型
//        1	1	HEX	交易类型
//        2	3	HEX	设备交易序号
//        5	8	BCD	卡号
//        13	3	HEX	卡余额
//        16	3	HEX	扣款金额
//        19	7	BCD	交易时间
//        26	2	HEX	用户卡脱机交易序号
//        28	4	HEX	TAC码
//        32	2	HEX	线路号
//        34	3	BCD	车号
//        37	4	BCD	当班司机号
//        41	6	BCD	PSAM卡号
//        47	1	HEX	行驶方向
//        48	1	HEX	站点号
//        49	1	HEX	补票标志
@Entity
public class CardRecord {
    @Id(autoincrement = true)
    private Long id;//id
    private String Status;//状态码
    private String CardType;//卡类型
    private String PayType;//交易类型
    private String DeviceNo;//设备交易序列号
    private String CardNumber;//卡号
    private String CardMoney;//卡内余额（扣款后）
    private String PayMoney;//消费金额
    private String DateTime;//交易日期和时间
    private String TrantScationNo;//用户卡脱机交易序号
    private String TACNo;//tac码
    private String LineNo;//线路号
    private String BusNo;//车号
    private String DriverNo;//驾驶员号
    private String PSAMNo;//PSAM卡号
    private String exposure;//方向
    private String up_station;//出站点
    private String ticket;//补票
    private String UpLoad;//是否上传 0未 1已

    private String reserve_1;
    private String reserve_2;
    private String reserve_3;
    @Generated(hash = 1592761669)
    public CardRecord(Long id, String Status, String CardType, String PayType,
            String DeviceNo, String CardNumber, String CardMoney, String PayMoney,
            String DateTime, String TrantScationNo, String TACNo, String LineNo,
            String BusNo, String DriverNo, String PSAMNo, String exposure,
            String up_station, String ticket, String UpLoad, String reserve_1,
            String reserve_2, String reserve_3) {
        this.id = id;
        this.Status = Status;
        this.CardType = CardType;
        this.PayType = PayType;
        this.DeviceNo = DeviceNo;
        this.CardNumber = CardNumber;
        this.CardMoney = CardMoney;
        this.PayMoney = PayMoney;
        this.DateTime = DateTime;
        this.TrantScationNo = TrantScationNo;
        this.TACNo = TACNo;
        this.LineNo = LineNo;
        this.BusNo = BusNo;
        this.DriverNo = DriverNo;
        this.PSAMNo = PSAMNo;
        this.exposure = exposure;
        this.up_station = up_station;
        this.ticket = ticket;
        this.UpLoad = UpLoad;
        this.reserve_1 = reserve_1;
        this.reserve_2 = reserve_2;
        this.reserve_3 = reserve_3;
    }
    @Generated(hash = 19461391)
    public CardRecord() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getStatus() {
        return this.Status;
    }
    public void setStatus(String Status) {
        this.Status = Status;
    }
    public String getCardType() {
        return this.CardType;
    }
    public void setCardType(String CardType) {
        this.CardType = CardType;
    }
    public String getPayType() {
        return this.PayType;
    }
    public void setPayType(String PayType) {
        this.PayType = PayType;
    }
    public String getDeviceNo() {
        return this.DeviceNo;
    }
    public void setDeviceNo(String DeviceNo) {
        this.DeviceNo = DeviceNo;
    }
    public String getCardNumber() {
        return this.CardNumber;
    }
    public void setCardNumber(String CardNumber) {
        this.CardNumber = CardNumber;
    }
    public String getCardMoney() {
        return this.CardMoney;
    }
    public void setCardMoney(String CardMoney) {
        this.CardMoney = CardMoney;
    }
    public String getPayMoney() {
        return this.PayMoney;
    }
    public void setPayMoney(String PayMoney) {
        this.PayMoney = PayMoney;
    }
    public String getDateTime() {
        return this.DateTime;
    }
    public void setDateTime(String DateTime) {
        this.DateTime = DateTime;
    }
    public String getTrantScationNo() {
        return this.TrantScationNo;
    }
    public void setTrantScationNo(String TrantScationNo) {
        this.TrantScationNo = TrantScationNo;
    }
    public String getTACNo() {
        return this.TACNo;
    }
    public void setTACNo(String TACNo) {
        this.TACNo = TACNo;
    }
    public String getLineNo() {
        return this.LineNo;
    }
    public void setLineNo(String LineNo) {
        this.LineNo = LineNo;
    }
    public String getBusNo() {
        return this.BusNo;
    }
    public void setBusNo(String BusNo) {
        this.BusNo = BusNo;
    }
    public String getDriverNo() {
        return this.DriverNo;
    }
    public void setDriverNo(String DriverNo) {
        this.DriverNo = DriverNo;
    }
    public String getPSAMNo() {
        return this.PSAMNo;
    }
    public void setPSAMNo(String PSAMNo) {
        this.PSAMNo = PSAMNo;
    }
    public String getExposure() {
        return this.exposure;
    }
    public void setExposure(String exposure) {
        this.exposure = exposure;
    }
    public String getUp_station() {
        return this.up_station;
    }
    public void setUp_station(String up_station) {
        this.up_station = up_station;
    }
    public String getTicket() {
        return this.ticket;
    }
    public void setTicket(String ticket) {
        this.ticket = ticket;
    }
    public String getUpLoad() {
        return this.UpLoad;
    }
    public void setUpLoad(String UpLoad) {
        this.UpLoad = UpLoad;
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

}
