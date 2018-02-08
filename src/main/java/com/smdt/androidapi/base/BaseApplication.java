package com.smdt.androidapi.base;


import android.app.Application;
import android.content.Context;
import android.net.wifi.WifiManager;

import com.smdt.androidapi.utils.Constant;
import com.smdt.androidapi.utils.SmallUtil;
import com.smdt.androidapi.utils.SpUtil;
import com.smdt.androidapi.utils.ToastUtil;

public class BaseApplication extends Application {
    private static BaseApplication mInstance = null;
    public static Context context;
    public static SpUtil sp;//程序启动时默认SharedPreferences对象
    private long firsttime;
    private WifiManager mWifiManager;
    private android.net.wifi.WifiInfo mWifiInfo;
    public static WifiManager.MulticastLock lock;


    public static BaseApplication getInstance() {
        if (mInstance == null) {
            mInstance = new BaseApplication();
        }
        return mInstance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        context = getApplicationContext();
        sp = SpUtil.getInstance(Constant.SP_name, MODE_PRIVATE);
        createDir();
        initWiFi();
    }

    /*初始化WIFI的信息*/
    private void initWiFi() {
        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        // 有的手机不能直接接收UDP包，可能是手机厂商在定制Rom的时候把这个功能给关掉了。实例化一个WifiManager.MulticastLock
        // 对象lock, 在调用广播发送、接收报文之前先调用lock.acquire()方法；
        // 用完之后及时调用lock.release()释放资源，否决多次调用lock.acquire()方法，程序可能会崩.
        lock = mWifiManager.createMulticastLock("wifi lcd");
    }

    /*创建文件夹*/
    private void createDir() {
        if (!Constant.fileLS.exists()) {
            Constant.fileLS.mkdir();
        }
    }

    /*返回此台设备的IP地址*/
    public String getIp() {
        mWifiInfo = mWifiManager.getConnectionInfo();
        return SmallUtil.intToString(mWifiInfo.getIpAddress());
    }

    /**
     * 退出整个程序
     */
    public void exitApp() {
        long secondtime = System.currentTimeMillis();
        if (secondtime - firsttime > 1000) {
            ToastUtil.showLong("再点一次就退出哦！");
            firsttime = secondtime;
        } else {
            onTerminate();
        }
    }

    @Override
    public void onTerminate() {
//        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//        manager.killBackgroundProcesses(getPackageName());
        System.exit(0);
        super.onTerminate();
    }


}
