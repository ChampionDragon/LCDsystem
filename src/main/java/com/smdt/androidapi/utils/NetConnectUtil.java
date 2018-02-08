package com.smdt.androidapi.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.smdt.androidapi.base.BaseApplication;

/**
 * Description: 网络查询类
 * AUTHOR: Champion Dragon
 * created at 2017/11/15
 **/
public class NetConnectUtil {
    static Context context = BaseApplication.context;
    static ConnectivityManager manager = (ConnectivityManager) context
            .getSystemService(Context.CONNECTIVITY_SERVICE);

    /**
     * 判断WIFI是否连接
     */
    public static boolean WIFI(Context context) {
        NetworkInfo networkInfo = manager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo == null) {
            return false;
        }
        return networkInfo.isConnectedOrConnecting();
    }

    /**
     * 判断MOBILE是否连接
     */
    public static boolean Mobile(Context context) {
        NetworkInfo networkInfo = manager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (networkInfo == null) {
            return false;
        }
        return networkInfo.isConnectedOrConnecting();
    }

    /**
     * 判断是否有网
     */
    public static boolean NetConnect(Context context) {
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return false;
        }
        return networkInfo.isConnectedOrConnecting();
    }
}
