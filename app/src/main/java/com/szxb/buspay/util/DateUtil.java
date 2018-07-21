package com.szxb.buspay.util;

import android.util.Log;

import com.szxb.jni.libszxb;
import com.szxb.mlog.SLog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 作者：Tangren on 2018-07-18
 * 包名：com.szxb.buspay.util
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class DateUtil {


    private static SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMddHHmmss", new Locale("zh", "CN"));
    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("zh", "CN"));
    private static SimpleDateFormat main_format = new SimpleDateFormat("yyyy.MM.dd\b\bHH:mm:ss", new Locale("zh", "CN"));
    private static SimpleDateFormat format_3 = new SimpleDateFormat("yyyy-MM-dd", new Locale("zh", "CN"));

    /**
     * 时钟
     *
     * @return .
     */
    public static String getMainTime() {
        return String.format("%1$s", main_format.format(new Date()));
    }


    //得到当前日期：yyyy-MM-dd HH:mm:ss
    public static String getCurrentDate() {
        return format.format(new Date());
    }

    //自定义格式获取当前日期
    public static String getCurrentDate(String format) {
        SimpleDateFormat ft = null;
        try {
            ft = new SimpleDateFormat(format, new Locale("zh", "CN"));
        } catch (Exception e) {
            return format_3.format(new Date());
        }
        return ft.format(new Date());
    }


    public static String getLastDate(String format) {
        SimpleDateFormat ft = null;
        try {
            ft = new SimpleDateFormat(format, new Locale("zh", "CN"));
        } catch (Exception e) {
            return format_3.format(new Date());
        }
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        return ft.format(calendar.getTime());
    }

    //获取当时Long型日期
    public static long currentLong() {
        return System.currentTimeMillis() / 1000;
    }


    //当前时间的前n分钟
    public static String getCurrentDateLastMi(int time) {
        Calendar beforeTime = Calendar.getInstance();
        beforeTime.add(Calendar.MINUTE, -time);// 1分钟之前的时间
        Date beforeD = beforeTime.getTime();
        return format.format(beforeD);
    }

    //当前时间的前n分钟
    public static String getCurrentDateLastMi(int time, String format) {
        Calendar beforeTime = Calendar.getInstance();
        beforeTime.add(Calendar.MINUTE, -time);// 1分钟之前的时间
        Date beforeD = beforeTime.getTime();
        return new SimpleDateFormat(format, new Locale("zh", "CN")).format(beforeD);
    }


    public static long getMILLISECOND(String startTime, String endTime) {
        try {
            Date startDate = format.parse(startTime);
            Date endDate = format.parse(endTime);
            long l = startDate.getTime() / 1000 - endDate.getTime() / 1000;
            Log.d("DateUtil",
                    "filterOpenID(DateUtil.java:211)相差时间=" + l);
            return l;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 设置k21时间
     */
    public static void setK21Time() {
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH) + 1;
        int day = now.get(Calendar.DAY_OF_MONTH);
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int min = now.get(Calendar.MINUTE);
        int sec = now.get(Calendar.SECOND);
        if (Calendar.getInstance().get(Calendar.YEAR) >= 2018) {
            try {
                libszxb.deviceSettime(year, month, day, hour, min, sec);
            } catch (Exception e) {
                e.printStackTrace();
                SLog.d("DateUtil(setK21Time.java:112)校准K21时间异常>>" + e.toString());
            }
        }
    }

}

