package com.szxb.buspay.util;

/**
 * 作者：Tangren on 2018-07-18
 * 包名：com.szxb.buspay.util
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class Config {

    //私钥
    public static final String private_key = "MIICXAIBAAKBgQCrnFUPueWNY3HLcVf55kXzJDb+ftYINmhde+4EMbKjPY38xaZQ\n" +
            "k+OjeXykbo8XgIi/xBpRvogWyOwZKOr4kdnV/PdLSoXCrr3DoTRU9INFiOKZPxFY" +
            "8nYmH6KI4c/z5ooeats8+1bwN5lZdXwXWL/MJA7JrSSSUt0qCwy9MI7+OQIDAQAB" +
            "AoGAIL37HL0DJy7KD17Ywj1FK1bFh1j7zSVUVEHI79PrmWmtJYUwbj9JN29+cIEH" +
            "nBxR+wSXYPFRVceQBFziN/rb7MAS0qNmBxcSzJfqjenoHPZa9smZXpX6W1zHuFTd" +
            "IloV8juM7ssQyRNRNLSIDs2zZBNXHV6eDqW0cdIJuWaKyYECQQDTkZpgv6531pby" +
            "trtWrdgIIjC55YsLZKWv3VqCfvHbhodETA+1EL9y/BV0F0yXE8oDlMbIR99uuU4X" +
            "24/q93mlAkEAz6Z+1KGqy2twmQ1JRO/8B4zfqgUlitYu41dWu+iHDfTC2ex84BRQ" +
            "dXVND2HGiz/fRB3yubc/WAnToLv/kdTGBQJAcDQnQKpH2CyJj52Ty0uVZ/LiDqUL" +
            "UfaF3LgzWUQD9t3o/TKtneSM9Gl240O8Dd+j4rRTnEJp3+oM3aBHOmEXNQJBAJR5" +
            "K/7FieXhcKU/BsCwB7kuVU6wV2OqOeR8Mpwxaz/jXt+LZM6kN9OEiBETjG9MwEto" +
            "ToHUMQq2HAe15MtVJDECQF7lh83AMlL31AtdmFkaHvqu8vrwYiDwqlam+dGADWPG" +
            "+Cpn7fcXw0wBqRLLMLymz6IAp2mJCN+N7W76j8GP08E=";


    private static final String IP = "http://111.230.85.238";//139.199.158.253

    //小兵mac
    public static final String MAC_KEY = IP + "/bipbus/interaction/getmackey";

    //小兵公钥
    public static final String PUBLIC_KEY = IP + "/bipbus/interaction/getpubkey";

    //小兵黑名单
    public static final String BLACK_LIST = IP + "/bipbus/interaction/blacklist";

    //小兵腾讯支付
    public static final String XBPAY = IP + "/bipbus/interaction/posrecv";

    //流水上传
    public static final String POST_BILL = IP + "/bipbus/interaction/posjour";


    public static final int SCAN_SUCCESS = 1;//1.扫码成功

    public static final int EC_RE_QR_CODE = 2;//2.请刷新二维码

    public static final int EC_CODE_TIME = 3;//3.二维码过期

    public static final int EC_CARD_CERT_TIME = 4;//4.请联网刷

    public static final int EC_FEE = 5;//5.超出最大金额

    public static final int VERIFY_FAIL = 6;//6.验码失败

    public static final int EC_BALANCE = 7;//7.余额不足

    public static final int IC_BASE = 8;//8.铛

    public static final int IC_BASE2 = 9;//9.铛铛

    public static final int IC_DIS = 10;//10.优惠卡

    public static final int IC_EMP = 11;//11.员工卡

    public static final int IC_BLOOD = 12;//12.无偿献血卡

    public static final int IC_FREE = 13;//13.免费卡

    public static final int IC_HONOR = 14;//14.荣军卡

    public static final int IC_OLD = 15;//15.老年卡

    public static final int IC_STUDENT = 16;//16.学生卡

    public static final int IC_INVALID = 17;//17.卡失效

    public static final int IC_LOVE = 18;//18.爱心卡

    public static final int IC_TO_WORK = 19;//19.上班

    public static final int IC_OFF_WORK = 20;//20.下班

    public static final int IC_PUSH_MONEY = 21;//21请投币

    public static final int IC_RE = 22;//22重新刷卡

    public static final int IC_YEARLY = 23;//23请年检

    public static final int QR_ERROR = 24;//24.二维码有误

    public static final int IC_RECHARGE = 25;//24.请充值

}
