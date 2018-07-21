package com.szxb.buspay.db.entity.bean.card;

/**
 * 作者：Tangren on 2018-07-19
 * 包名：com.szxb.buspay.db.entity.bean.card
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class CardType {

    //普通卡
    public static final String CARD_NORMAL = "01";

    //学生卡
    public static final String CARD_STUDENT = "02";

    //老年卡
    public static final String CARD_OLD = "03";

    //免费卡
    public static final String CARD_FREE = "04";

    //纪念卡
    public static final String CARD_MEMORY = "05";

    //员工卡
    public static final String CARD_EMP = "06";

    //采集卡
    public static final String CARD_GATHER = "11";

    //签点卡
    public static final String CARD_SIGNED = "12";

    //检测卡
    public static final String CARD_CHECKED = "13";

    //稽查卡
    public static final String CARD_CHECK = "18";


    private String cardType;
    private int pay_fee;

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public int getPay_fee() {
        return pay_fee;
    }

    public void setPay_fee(int pay_fee) {
        this.pay_fee = pay_fee;
    }
}
