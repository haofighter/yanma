package com.szxb.buspay.db.sp;


import android.os.Environment;

/**
 * 作者: Tangren on 2017/7/12
 * 包名：com.szxb.onlinbus.util
 * 邮箱：996489865@qq.com
 * TODO:获取全局的SP数据
 */

public class FetchAppConfig {

    //获取appId
    public static String getAppId() {
        return (String) CommonSharedPreferences.get("app_id", "0000000");
    }

    //获取citycode
    public static String getCityCode() {
        return (String) CommonSharedPreferences.get("city_code", "0000000");
    }

    //车牌号
    public static String getBusNo() {
        return (String) CommonSharedPreferences.get("bus_no", "000000");
    }

    //备注
    public static String getOrderDesc() {
        return (String) CommonSharedPreferences.get("order_desc", "淄博公交");
    }

    //获取票价
    public static int getBasePrice() {
        return (Integer) CommonSharedPreferences.get("base_price", 0);
    }

    //司机卡号
    public static String getDriverNo() {
        return (String) CommonSharedPreferences.get("driver_no", "00000000");
    }

    //线路名
    public static String getLineName() {
        return (String) CommonSharedPreferences.get("line_name", "0000");
    }


    public static String getLineNo() {
        return (String) CommonSharedPreferences.get("line_no", "0000");
    }

    //线路名称说明
    public static String chinese_name() {
        return (String) CommonSharedPreferences.get("chinese_name", "未设置");
    }

    //是否为固定票价
    public static String is_fixed_price() {
        return (String) CommonSharedPreferences.get("is_fixed_price", "");
    }


    //折扣
    public static String coefficient() {
        return (String) CommonSharedPreferences.get("coefficient", "000000000000000000000000000000");
    }


    //FTP参数路径
    public static String FTPParameter() {
        return (String) CommonSharedPreferences.get("FTPParameter", "pram/dlydt.json");
    }

    //FTP黑名单路径
    public static String FTPBlackList() {
        return (String) CommonSharedPreferences.get("FTPBlcakList", "black/blacklist252.json");
    }

    //FTP保存路径
    public static String FTPLocalPath() {
        return (String) CommonSharedPreferences.get("FTPLocalPath", Environment.getExternalStorageDirectory() + "/");
    }

    //保存参数文件名
    public static String ParameterName() {
        return (String) CommonSharedPreferences.get("ParameterName", "dlydt.json");
    }

    //保存黑名单文件名
    public static String BlackListName() {
        return (String) CommonSharedPreferences.get("BlackListName", "blacklist252.json");
    }

    //参数标志位，(0:扣款，1:不扣款)最右1为员工卡，右2为免费卡，右3为老年卡是否扣款标志
    public static String Param_Flag() {
        return (String) CommonSharedPreferences.get("Param_Flag", "00000000");
    }

    //公司号
    public static String unitno() {
        return (String) CommonSharedPreferences.get("unitno", "0");
    }



    /*
     *
     * FTP基本参数
     *
     * */

    //IP
    public static String FTPIP() {
        return (String) CommonSharedPreferences.get("FTPIP", "112.35.80.147");
    }

    //port  21淄博  淄博用自己的移动云服务器
    public static int FTPPort() {
        return (Integer) CommonSharedPreferences.get("FTPPort", 21);
    }

    //User
    public static String FTPUser() {
        return (String) CommonSharedPreferences.get("FTPUser", "zbbusftpdan");
    }

    //Password
    public static String FTPPassword() {
        return (String) CommonSharedPreferences.get("Password", "ftp1234!@#$");
    }

    public static String getBinVersion() {
        return (String) CommonSharedPreferences.get("bin", "Q6_K21_180710102303.bin");
    }

    public static String getLastVersion() {
        return (String) CommonSharedPreferences.get("last_bin", "000000");
    }

    //参数信息是否设置完成，0否 1是
    public static int isInitFinish() {
        return (Integer) CommonSharedPreferences.get("finish", 0);
    }


    public static String getLineFileName() {
        return (String) CommonSharedPreferences.get("line_file_name", "0");
    }

    public static String getLineFileVersion() {
        return (String) CommonSharedPreferences.get("line_file_version", "0");
    }

    //数字流水00000000-99999999
    public static String getNumSeq() {
        return (String) CommonSharedPreferences.get("num_seq", "00000000");
    }
}