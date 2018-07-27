package com.szxb.buspay.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.SystemClock;
import android.text.TextUtils;

import com.szxb.mlog.SLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

/**
 * 作者：Tangren on 2018-07-17
 * 包名：com.szxb.buspay.util
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class Util {

    /**
     * 防止按键抖动
     *
     * @param currentTime 当前按键时间
     * @param lastTime    上次按键时间
     * @return .
     */
    public static boolean checkEnterKey(long currentTime, long lastTime) {
        return currentTime - lastTime > 300;
    }


    /**
     * 两次扫码间隔
     *
     * @param currentTime
     * @param lastTime
     * @return
     */
    public static boolean checkQR(long currentTime, long lastTime) {
        return currentTime - lastTime > 2000;
    }


    //String 2 int
    public static int string2Int(String var) {
        try {
            return Integer.valueOf(var);
        } catch (Exception e) {
            e.printStackTrace();
            SLog.d("Util(string2Int.java:33)var=" + var + ",数字类型转换异常>>" + e.toString());
            return 100;
        }
    }

    //String 2 int
    public static int hex2Int(String var) {
        try {
            return Integer.valueOf(var, 16);
        } catch (Exception e) {
            e.printStackTrace();
            SLog.d("Util(string2Int.java:33)var=" + var + ",数字类型转换异常>>" + e.toString());
            return 100;
        }
    }

    /**
     * @param hex hex
     * @return .
     */
    public static String hex2IntStr(String hex) {
        return String.valueOf(hex2Int(hex));
    }

    public static String fen2Yuan(int prices) {
        DecimalFormat format = new DecimalFormat("0.00");
        return format.format((float) prices / (float) 100);
    }


    /**
     * 补足7位数字流水
     *
     * @param i
     * @return
     */
    public static String getNumSeq(int i) {
        return String.format("%08d", i);
    }

    /**
     * 随机字符串
     *
     * @param length
     * @return
     */
    public static String Random(int length) {
        char[] ss = new char[length];
        int i = 0;
        while (i < length) {
            int f = (int) (Math.random() * 5);
            if (f == 0)
                ss[i] = (char) ('A' + Math.random() * 26);
            else if (f == 1)
                ss[i] = (char) ('a' + Math.random() * 26);
            else
                ss[i] = (char) ('0' + Math.random() * 10);
            i++;
        }
        return String.valueOf(ss);
    }


    /**
     * 防误刷、连刷（3S）
     *
     * @param temCardNo 临时卡号
     * @param cardNo    当前卡号
     * @param lastTime  上一次刷卡时间
     * @return true则继续往下走
     */
    public static boolean check(String temCardNo, String cardNo, long lastTime) {

        return (!TextUtils.equals(temCardNo, cardNo) //不是相同的卡
                || checkSwipe(SystemClock.elapsedRealtime(), lastTime)//或者间隔超过5S
        )
                && filter(SystemClock.elapsedRealtime(), lastTime);//两次刷卡间隔大于1S(防止语音叠加)
    }

    public static boolean filter(long currentTime, long lastTime) {
        return currentTime - lastTime > 1200;
    }

    private static boolean checkSwipe(long currentTime, long lastTime) {
        return currentTime - lastTime > 3000;
    }

    /**
     * @param str       .
     * @param strLength .
     * @return 字符串右补0
     */
    public static String addZeroRight(String str, int strLength) {
        int strLen = str.length();
        if (strLen < strLength) {
            while (strLen < strLength) {
                str = str + "0";
                strLen = str.length();
            }
        }
        return str;
    }

    /**
     * @param status .
     * @return 扫码上传状态
     */
    public static String scanStatus(int status) {
        switch (status) {
            case 0:
            case 2:
            case 3:
                return "已上传";
            case 1:
                return "待上传";
            default:
                return "UNK[" + status + "]";
        }
    }


    /**
     * @param resCode 状态
     * @return 银联卡上传状态
     */
    public static String unionPayStatus(String resCode) {
        switch (resCode) {
            case "00":
                return "已扣款";
            case "03":
                return "无效商户";
            case "04":
                return "无效卡";
            case "05":
                return "认证失败";
            case "13":
                return "无效金额";
            case "14":
                return "无效卡号";
            case "30":
                return "报文错误";
            case "41":
                return "挂失卡";
            case "43":
                return "被窃卡";
            case "51":
                return "余额不足";
            case "54":
                return "卡过期";
            case "58":
                return "无效终端";
            case "98":
                return "超时";
            case "A0":
                return "重签到";
            case "A2":
            case "A4":
            case "A5":
            case "A6":
                return "待确认";
            case "94":
                return "流水重复";
            default:
                return "UNK[" + resCode + "]";
        }
    }


    /**
     * @param fileName 文件名
     * @param context  .
     * @return 本地配置参数
     */
    public static String readAssetsFile(String fileName, Context context) {
        StringBuilder builder = new StringBuilder();
        AssetManager manager = context.getAssets();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(manager.open(fileName)));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            SLog.d("Util(readAssetsFile.java:212)读取本地配置文件异常>>" + e.toString());
        }
        return builder.toString();
    }

    /**
     * @param intStr .
     * @param len    .
     * @return int string 转hex
     */
    public static String str2Hex(String intStr, int len) {
        int i = string2Int(intStr);
        String str = Integer.toHexString(i);
        int strLen = str.length();
        if (strLen < len) {
            while (strLen < len) {
                str = "0" + str;
                strLen = str.length();
            }
        }
        return str;
    }
}
