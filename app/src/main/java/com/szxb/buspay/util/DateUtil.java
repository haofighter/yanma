package com.szxb.buspay.util;

import android.util.Log;

import com.szxb.buspay.BusApp;
import com.szxb.buspay.util.tip.BusToast;
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
     * @param type 0：公交卡，1：微信、银联卡
     * @return .
     */
    public static String[] time(int type) {
        if (type == 0) {
            String startTime = DateUtil.getTime("yyyyMMddHHmmss", 0, 0, 0, 0);
            String endTime = DateUtil.getTime("yyyyMMddHHmmss", 23, 59, 59, 999);
            return new String[]{startTime, endTime};
        } else {
            String startTime = DateUtil.getTime("yyyy-MM-dd HH:mm:ss", 0, 0, 0, 0);
            String endTime = DateUtil.getTime("yyyy-MM-dd HH:mm:ss", 23, 59, 59, 999);
            return new String[]{startTime, endTime};
        }
    }

    /**
     * @param format            格式：公交卡：yyyyMMddHHmmss,其他yyyy-MM-dd HH:mm:ss
     * @param hour_of_day_value 小时
     * @param minute_value      分钟
     * @param second_value      秒
     * @param millisecond_value 毫秒
     * @return 当前开始时间|结束时间
     */
    public static String getTime(String format, int hour_of_day_value,
                                 int minute_value, int second_value, int millisecond_value) {
        SimpleDateFormat ft = new SimpleDateFormat(format, new Locale("zh", "CN"));
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour_of_day_value);
        calendar.set(Calendar.MINUTE, minute_value);
        calendar.set(Calendar.SECOND, second_value);
        calendar.set(Calendar.MILLISECOND, millisecond_value);
        return ft.format(calendar.getTime());
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

    /**
     * 校准时间
     *
     * @param time .
     */
    public static void setTime(String time) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", new Locale("zh", "CN"));
            Date date = format.parse(time);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.get(Calendar.YEAR);
            calendar.get(Calendar.MONTH);
            calendar.get(Calendar.DATE);
            calendar.get(Calendar.HOUR);
            calendar.get(Calendar.MINUTE);

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int min = calendar.get(Calendar.MINUTE);
            BusApp.getInstance().getmService().setDateTime(year, month, day, hour, min);
            setK21Time();
            BusToast.showToast(BusApp.getInstance(), "校准成功", true);
        } catch (Exception e) {
            e.printStackTrace();
            SLog.d("DateUtil(setTime.java:190)时间校准失败>>" + e.toString());
            BusToast.showToast(BusApp.getInstance(), "校准失败\n" + e.toString(), false);
        }
    }


    //扫码当前时间的前day天
    public static String getScanCurrentDateLastDay(String format, int day) {
        Calendar beforeTime = Calendar.getInstance();
        beforeTime.add(Calendar.DATE, -day);//
        Date beforeD = beforeTime.getTime();
        SimpleDateFormat fd = new SimpleDateFormat(format, new Locale("zh", "CN"));
        return fd.format(beforeD);
    }
}

