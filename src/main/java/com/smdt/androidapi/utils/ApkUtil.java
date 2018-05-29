package com.smdt.androidapi.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;

/**
 * Description: 未安装APK的信息类
 * AUTHOR: Champion Dragon
 * created at 2018/5/19
 **/

public class ApkUtil {
    /*得到PackageInfo对象，其中包含了该apk包含的activity和service*/
    public static PackageInfo getPackageInfo(Context context, String apkFilepath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pkgInfo = null;
        try {
            pkgInfo = pm.getPackageArchiveInfo(apkFilepath, PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pkgInfo;
    }

    /*得到APK的图标*/
    public static Drawable ApkIcon(Context context, String apkFilepath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pkgInfo = getPackageInfo(context, apkFilepath);
        if (pkgInfo == null) {
            return null;
        }
        ApplicationInfo appInfo = pkgInfo.applicationInfo;
        if (Build.VERSION.SDK_INT >= 8) {
            appInfo.sourceDir = apkFilepath;
            appInfo.publicSourceDir = apkFilepath;
        }
        return pm.getApplicationIcon(appInfo);
    }


    /*得到APK的名称*/
    public static String ApkName(Context context, String apkFilepath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pkgInfo = getPackageInfo(context, apkFilepath);
        if (pkgInfo == null) {
            return "没查到";
        }
        ApplicationInfo appInfo = pkgInfo.applicationInfo;
        if (Build.VERSION.SDK_INT >= 8) {
            appInfo.sourceDir = apkFilepath;
            appInfo.publicSourceDir = apkFilepath;
        }
        return pm.getApplicationLabel(appInfo).toString();
    }

    /*获取APK的权限*/
    public static String[] ApkPremission(Context context, String apkFilepath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pkgInfo = getPackageInfo(context, apkFilepath);
        if (pkgInfo == null) {
            return null;
        }
        return pkgInfo.requestedPermissions;
    }

    /*获取APK的签名*/

    public static String ApkSignature(Context context, String apkFilepath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pkgInfo = getPackageInfo(context, apkFilepath);
        if (pkgInfo == null) {
            return "没查到";
        }
        // 获取到所有的权限
        return pkgInfo.signatures[0].toCharsString();
    }

    /*获得APK版本号*/
    public static int VersionCode(Context context, String apkFilepath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pkgInfo = getPackageInfo(context, apkFilepath);
        if (pkgInfo == null) {
            return 0;
        }
        return pkgInfo.versionCode;
    }


    /*获得APK版本名*/

    public static String VersionName(Context context, String apkFilepath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pkgInfo = getPackageInfo(context, apkFilepath);
        if (pkgInfo == null) {
            return "没查到";
        }
        return pkgInfo.versionName;
    }

    /*得到软件包名*/
    public static String PackgeName(Context context, String apkFilepath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pkgInfo = getPackageInfo(context, apkFilepath);
        if (pkgInfo == null) {
            return "没查到";
        }
        return pkgInfo.packageName;
    }
}
