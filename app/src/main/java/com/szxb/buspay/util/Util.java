package com.szxb.buspay.util;

import android.os.SystemClock;
import android.text.TextUtils;

import com.szxb.mlog.SLog;

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
                && filter(SystemClock.elapsedRealtime(), lastTime);//两次刷卡间隔大于2S(防止语音叠加)
    }

    private static boolean filter(long currentTime, long lastTime) {
        return currentTime - lastTime > 2000;
    }

    private static boolean checkSwipe(long currentTime, long lastTime) {
        return currentTime - lastTime > 5000;
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
}
