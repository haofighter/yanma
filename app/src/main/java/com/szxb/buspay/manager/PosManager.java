package com.szxb.buspay.manager;

import android.text.TextUtils;

import com.example.zhoukai.modemtooltest.ModemToolTest;
import com.szxb.buspay.db.dao.LineInfoEntityDao;
import com.szxb.buspay.db.entity.bean.FTPEntity;
import com.szxb.buspay.db.entity.card.LineInfoEntity;
import com.szxb.buspay.db.manager.DBCore;
import com.szxb.buspay.db.manager.DBManager;
import com.szxb.buspay.db.sp.CommonSharedPreferences;
import com.szxb.buspay.db.sp.FetchAppConfig;
import com.szxb.buspay.util.DateUtil;
import com.szxb.buspay.util.Util;
import com.szxb.mlog.SLog;

/**
 * 作者：Tangren on 2018-07-18
 * 包名：com.szxb.buspay.manager
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class PosManager implements IPosManager, IAddRess {

    /**
     * 路线名
     */
    private String lineName;
    /**
     * 路线号
     */
    private String lineNo;
    /**
     * 起始站名
     */
    private String startStationName;
    /**
     * 终点站名
     */
    private String endStationName;
    /**
     * 设备号
     */
    private String posSn;

    /**
     * 基础金额
     */
    private int basePrice;
    /**
     * 实际扣款金额
     */
    private int paymarkedPrice;

    /**
     * 银联实际扣款
     */
    private int unionPaymarkedPrice;

    /**
     * 370300zibo  371200laiwu
     */
    private String cityCode = "370300";//淄博
    /**
     * 站点ID
     */
    private int inStationId = 13;
    /**
     * 备注
     */
    private String orderDesc = "淄博公交";
    /**
     * key
     */
    private byte[] key;
    /**
     * 车牌号
     */
    private String bus_no;
    /**
     * 腾讯商户号
     * <p>
     * 09 zibo  10laiwu
     */
    private String app_id = "10000009";
    /**
     * 今日交易笔数
     */
    private long payNum;
    /**
     * K21是否完成更新
     */
    private String k21Init;
    /**
     * 签到状态,默认未签到
     */
    private boolean isSign;
    /**
     * 单位号
     */
    private String unitno;
    /**
     * 司机号
     */
    private String driverNo;

    private String ftpIP;
    private int ftpPort;
    private String ftpUser;
    private String ftpPsw;

    private FTPEntity ftpEntity;

    //是否存在线路
    private boolean exitLine;

    //当前线路信息
    private LineInfoEntity lineInfoEntity;

    //上个bin版本
    private String lastVersion;

    //当前bin版本
    private String currentVersion;

    /**
     * ################淄博      栖霞、招远      莱芜
     * coefficient[0]:普通卡      普通卡        普通卡
     * coefficient[1]:学生卡      学生卡        学生卡
     * coefficient[2]:老年卡      老年卡        老年卡
     * coefficient[3]:免费        免费卡        免费卡
     * coefficient[4]:员工        优惠1         残疾人卡
     * coefficient[5]:优惠1       员工卡        员工卡
     * coefficient[6]:优惠2       优惠2         优惠2
     * coefficient[7]:优惠3       优惠3         优惠3
     * coefficient[8]:微信        微信          银联
     * coefficient[9]:银联        银联          济南卡
     */
    private String[] coefficient;

    private String chineseName;

    //递增流水号
    private int numSeq = 0;

    @Override
    public void loadFromPrefs() {
        //微信基础参数
//        app_id = FetchAppConfig.getAppId();
//        cityCode = FetchAppConfig.getCityCode();
//        orderDesc = FetchAppConfig.getOrderDesc();

        //pos基础参数
        basePrice = FetchAppConfig.getBasePrice();
        lineName = FetchAppConfig.getLineName();
        unitno = FetchAppConfig.unitno();
        lastVersion = FetchAppConfig.getLastVersion();
        currentVersion = FetchAppConfig.getBinVersion();
        lineNo = FetchAppConfig.getLineNo();
        bus_no = FetchAppConfig.getBusNo();
        driverNo = FetchAppConfig.getDriverNo();
        unitno = FetchAppConfig.unitno();
        numSeq = Integer.valueOf(FetchAppConfig.getNumSeq());
        chineseName = FetchAppConfig.chinese_name();

        //初始化SN号
        initSn();
        //初始化折扣
        initDis();

        //ftp基础参数
        if (cityCode.equals("370300")) {
            //淄博
            app_id = "10000009";
            cityCode = "370300";
            orderDesc = "淄博公交";
            ftpIP = "112.35.80.147";
            ftpPort = 21;
            ftpUser = "zbbusftpdan";
            ftpPsw = "ftp1234!@#$";
            ftpEntity = new FTPEntity(ftpIP, ftpPort, ftpUser, ftpPsw);
        }
//        ftpIP = FetchAppConfig.FTPIP();
//        ftpPort = FetchAppConfig.FTPPort();
//        ftpUser = FetchAppConfig.FTPUser();
//        ftpPsw = FetchAppConfig.FTPPassword();


        LineInfoEntityDao dao = DBCore.getDaoSession().getLineInfoEntityDao();
        lineInfoEntity = dao.queryBuilder().limit(1).unique();

    }

    private void initDis() {
        String dis = FetchAppConfig.coefficient();
        int len = dis.length() / 3;
        coefficient = new String[len];
        int index = 0;
        for (int i = 0; i < len; i++) {
            coefficient[i] = dis.substring(index, index += 3);
        }
    }

    private void initSn() {
        String item;
        try {
            item = ModemToolTest.getItem(7);
        } catch (Exception e) {
            item = "default";
            SLog.d("PosManager(initSn.java:209)SN获取失败>>" + e.toString());
        }
        if (TextUtils.isEmpty(item)) {
            posSn = "default";
        } else {
            posSn = item;
        }
    }

    @Override
    public String getLineName() {
        return lineName;
    }

    @Override
    public void setLineName(String var1) {
        this.lineName = var1;
        CommonSharedPreferences.put("line_name", var1);
    }

    @Override
    public String getLineNo() {
        return lineNo;
    }

    @Override
    public void setLineNo(String var1) {
        this.lineNo = var1;
        CommonSharedPreferences.put("line_no", var1);
    }

    @Override
    public int getBasePrice() {
        return basePrice;
    }

    @Override
    public void setBasePrice(int var1) {
        this.basePrice = var1;
        CommonSharedPreferences.put("base_price", var1);
    }

    @Override
    public int getWcPayPrice() {
        return 0;
    }

    @Override
    public void setWcPrice(int var1) {

    }

    @Override
    public void setUnionPayPrice(int price) {

    }

    @Override
    public int getUnionPayPrice() {
        return 0;
    }

    @Override
    public String getMac(String keyId) {
        return DBManager.getMac(keyId);
    }

    @Override
    public String getPublicKey(String keyId) {
        return DBManager.getPublicKey(keyId);
    }

    @Override
    public long getOrderTime() {
        return DateUtil.currentLong();
    }

    @Override
    public String getmchTrxId() {
        if (numSeq >= 99999999) {
            numSeq = 0;
        }
        numSeq++;
        String numSeqStr = Util.getNumSeq(numSeq);
        CommonSharedPreferences.put("num_seq", numSeqStr);

        return Util.Random(10) + numSeqStr;
    }

    @Override
    public String geCityCode() {
        return null;
    }

    @Override
    public int getInStationId() {
        return 0;
    }

    @Override
    public String getInStationName() {
        return null;
    }

    @Override
    public String getChinese_name() {
        return chineseName;
    }

    @Override
    public void setChinese_name(String name) {
        this.chineseName = name;
        CommonSharedPreferences.put("chinese_name", name);
    }

    @Override
    public byte[] getKey() {
        return new byte[0];
    }

    @Override
    public void setKey(String privateKey) {

    }

    @Override
    public String getBusNo() {
        return bus_no;
    }

    @Override
    public void setBusNo(String bus_no) {
        this.bus_no = bus_no;
        CommonSharedPreferences.put("bus_no", bus_no);
    }

    @Override
    public String getAppId() {
        return app_id;
    }

    @Override
    public void setAppId(String app_id) {

    }

    @Override
    public String getCityCode() {
        return cityCode;
    }

    @Override
    public void setCityCode() {

    }

    @Override
    public void setUnitno(String unitno) {
        this.unitno = unitno;
        CommonSharedPreferences.put("unitno", unitno);
    }

    @Override
    public String getUnitno() {
        return unitno;
    }

    @Override
    public void setDriverNo(String no) {
        this.driverNo = no;
        CommonSharedPreferences.put("driver_no", no);
    }

    @Override
    public String getDriverNo() {
        return driverNo;
    }

    @Override
    public String getPosSN() {
        return posSn;
    }

    @Override
    public LineInfoEntity getLineInfoEntity() {
        return lineInfoEntity;
    }

    @Override
    public void setLineInfoEntity() {
        LineInfoEntityDao dao = DBCore.getDaoSession().getLineInfoEntityDao();
        lineInfoEntity = dao.queryBuilder().limit(1).unique();
    }

    @Override
    public String getLastVersion() {
        return lastVersion;
    }

    @Override
    public void setLastVersion(String version) {
        this.lastVersion = version;
        CommonSharedPreferences.put("last_bin", version);
    }

    @Override
    public String getBinVersion() {
        return currentVersion;
    }

    @Override
    public String[] getCoefficent() {
        return coefficient;
    }

    @Override
    public void setCoefficent(String coefficent) {
        int len = coefficent.length() / 3;
        coefficient = new String[len];
        int index = 0;
        for (int i = 0; i < len; i++) {
            coefficient[i] = coefficent.substring(index, index += 3);
        }
        CommonSharedPreferences.put("coefficent", coefficent);
    }

    @Override
    public void setFtpIp(String ip) {
        this.ftpIP = ip;
        CommonSharedPreferences.put("FTPIP", ip);
    }

    @Override
    public String getFtpIP() {
        return ftpIP;
    }

    @Override
    public void setPort(int port) {
        this.ftpPort = port;
        CommonSharedPreferences.put("FTPPort", port);
    }

    @Override
    public int getPort() {
        return ftpPort;
    }

    @Override
    public void setFtpPsw(String psw) {
        this.ftpPsw = psw;
        CommonSharedPreferences.put("Password", psw);
    }

    @Override
    public String getFtpPsw() {
        return ftpPsw;
    }

    @Override
    public void setFtpUser(String user) {
        this.ftpUser = user;
        CommonSharedPreferences.put("FTPUser", user);
    }

    @Override
    public String getFtpUser() {
        return ftpUser;
    }

    @Override
    public void setUrlIp(String ip) {

    }

    @Override
    public String getUrlIp() {
        return null;
    }

    @Override
    public void setFTP(FTPEntity ftp) {
        this.ftpEntity = ftp;
        CommonSharedPreferences.put("ftp_ip", ftp.getI());
        CommonSharedPreferences.put("ftp_user", ftp.getU());
        CommonSharedPreferences.put("ftp_psw", ftp.getPsw());
        CommonSharedPreferences.put("port", ftp.getP());
    }

    @Override
    public FTPEntity getFTP() {
        return ftpEntity;
    }
}
