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

    public static final int EC_RE_QR_CODE = SCAN_SUCCESS + 1;//2.请刷新二维码

    public static final int EC_CODE_TIME = EC_RE_QR_CODE + 1;//3.二维码过期

    public static final int EC_CARD_CERT_TIME = EC_CODE_TIME + 1;//4.请联网刷

    public static final int EC_FEE = EC_CARD_CERT_TIME + 1;//5.超出最大金额

    public static final int VERIFY_FAIL = EC_FEE + 1;//6.验码失败

    public static final int EC_BALANCE = VERIFY_FAIL + 1;//7.余额不足

    public static final int IC_BASE = EC_BALANCE + 1;//8.铛

    public static final int IC_BASE2 = IC_BASE + 1;//9.铛铛

    public static final int IC_DIS = IC_BASE2 + 1;//10.优惠卡

    public static final int IC_EMP = IC_DIS + 1;//11.员工卡

    public static final int IC_BLOOD = IC_EMP + 1;//12.无偿献血卡

    public static final int IC_FREE = IC_BLOOD + 1;//13.免费卡

    public static final int IC_HONOR = IC_FREE + 1;//14.荣军卡

    public static final int IC_OLD = IC_HONOR + 1;//15.老年卡

    public static final int IC_STUDENT = IC_OLD + 1;//16.学生卡

    public static final int IC_INVALID = IC_STUDENT + 1;//17.卡失效

    public static final int IC_LOVE = IC_INVALID + 1;//18.爱心卡

    public static final int IC_TO_WORK = IC_LOVE + 1;//19.上班

    public static final int IC_OFF_WORK = IC_TO_WORK + 1;//20.下班

    public static final int IC_PUSH_MONEY = IC_OFF_WORK + 1;//21请投币

    public static final int IC_RE = IC_PUSH_MONEY + 1;//22重新刷卡

    public static final int IC_YEARLY = IC_RE + 1;//23请年检

    public static final int QR_ERROR = IC_YEARLY + 1;//24.二维码有误

    public static final int IC_RECHARGE = QR_ERROR + 1;//25.请充值

    public static final int IC_DEFECT = IC_RECHARGE + 1;//26.优抚卡

    public static final int IC_MANAGER = IC_DEFECT + 1;//27.管理卡

    public static final int IC_MONTH = IC_MANAGER + 1;//28.月票卡


    //菜单
    //公交卡刷卡
    public static final int POSITION_BUS_RECORD = 0;

    //扫码记录
    public static final int POSITION_SCAN_RECORD = POSITION_BUS_RECORD + 1;

    //银联卡记录
    public static final int POSITION_UNION_RECORD = POSITION_SCAN_RECORD + 1;

    //汇总
    public static final int POSITION_CNT = POSITION_UNION_RECORD + 1;

    //更新参数
    public static final int POSITION_UPDATE_PARAMS = POSITION_CNT + 1;

    //数据库导出
    public static final int POSITION_EXPORT_DB = POSITION_UPDATE_PARAMS + 1;

    //日志导出
    public static final int POSITION_EXPORT_LOG = POSITION_EXPORT_DB + 1;

    //检测网络
    public static final int POSITION_CHECK_NET = POSITION_EXPORT_LOG + 1;

    //校准时间
    public static final int POSITION_TIME = POSITION_CHECK_NET + 1;

    //导出7天记录
    public static final int POSITION_EXPORT_7 = POSITION_TIME + 1;

    //导出1个月记录
    public static final int POSITION_EXPORT_1_M = POSITION_EXPORT_7 + 1;

    //导出3个月记录
    public static final int POSITION_EXPORT_3_M = POSITION_EXPORT_1_M + 1;

    //查看参数信息
    public static final int POSITION_READ_PARAM = POSITION_EXPORT_3_M + 1;

}
