package com.szxb.buspay.util;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import com.szxb.buspay.BusApp;

import java.util.List;

/**
 * 作者: Tangren on 2017/8/7
 * 包名：com.szxb.utils
 * 邮箱：996489865@qq.com
 * TODO:判断是否是DEBUG模式
 */

public class AppUtil {

    private static Boolean isDebug = null;

    public static boolean isDebug() {
        return isDebug == null ? false : isDebug;
    }

    public static void syncISDebug(Context context) {
        if (isDebug == null) {
            isDebug = context.getApplicationInfo() != null && (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        }
    }

    public static String getVersionName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "1.0";
        }
    }

    /**
     * 判断某个Activity 界面是否在前台
     *
     * @param className 类名
     * @return
     */
    public static boolean isForeground(String className) {
        if (TextUtils.isEmpty(className)) {
            return false;
        }
        ActivityManager am = (ActivityManager) BusApp.getInstance().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (className.equals(cpn.getClassName())) {
                return true;
            }
        }
        return false;
    }


    /**
     * 是否有网络
     *
     * @return boolean
     */
    public static boolean checkNetStatus() {
        ConnectivityManager cm = (ConnectivityManager) BusApp.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo current = cm.getActiveNetworkInfo();
        return current != null && current.isAvailable();
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue .
     * @return .
     */
    public static int sp2px(Context context, float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

}
