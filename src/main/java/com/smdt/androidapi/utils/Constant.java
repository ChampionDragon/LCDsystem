package com.smdt.androidapi.utils;

import android.os.Environment;

import java.io.File;

/**
 * Description:常量类
 * AUTHOR: Champion Dragon
 * created at 2017/11/11
 **/

public class Constant {
    /* 网络视频地址 */
    public final static String viedoUrl =
            "http://yxfile.idealsee.com/9f6f64aca98f90b91d260555d3b41b97_mp4.mp4";

    /*LCD类handler标志*/
    public static final int diaCreate = 30;
    public static final int diaCreateByTime = 31;
    public static final int diaDis = 32;
    public static final int UpdateApk = 33;
    public static final int textCreate = 34;
    public static final int textDis = 35;
    public static final int textDisByTime = 36;
    public static final int setip = 37;
    public static final int codefragCreate= 38;
    public static final int codefragDis= 39;

    /*客户端请求码signway*/
    public static final String signSetIP = "setip";//设置ip的标志
    public static final String IPstr = "i";
    public static final String gateWaystr = "g";
    public static final String netMaskstr = "n";
    public static final String devid = "d";

    public static final String signcodefragCreate="codefragCreate";
    public static final String signcodefragDis="codefragDis";
    public static final String codefragstr ="codefragstr";
    public static final String codefragcode ="codefragcode";


    public static final String diastr = "dialogString";
    public static final String diacode = "dialogCode";
    public static final String diaDisTime = "dialogDismissTime";

    public static final String signdiaCreate = "diaCreate";
    public static final String signdiaCreateByTime = "diaCreateByTime";
    public static final String signdiaDis = "diaDis";

    public static final String signUpdateApk = "updateApk";
    public static final String apkname = "apkname";

    public static final String signtextCreate = "textCreate";
    public static final String signtextCreateByTime = "textCreateByTime";
    public static final String signtextDis = "textDis";

    public static final String sign = "signway";
    public static final String params = "params";

    public static final String success = "success";
    public static final String fail = "fail";
    public static final String result = "result";
    public static final String errorstr = "errorstr";


    /*设备最为服务端监听的端口号*/
    public static final String port = "26565";


    /* 文件夹 */
    // 整个项目的目录
    public final static String Pathroot = Environment.getExternalStorageDirectory().getAbsolutePath();
    //根文件夹
    public final static File fileDir = new File(Environment.getExternalStorageDirectory().toString());
    //视频文件路径
    public final static String viedoPath = fileDir.getAbsolutePath() + File.separator + "1.mp4";
    //APK的名字
    public final static String apkNames = "测试.apk";

    //联胜文件夹
    public final static File fileLS = new File(fileDir.getAbsolutePath(), "联胜智能");
    //联胜文件夹路径
    public final static String PathLS = fileLS.toString();
    public final static String viedoPathLS = fileLS.getAbsolutePath() + File.separator + "1.mp4";

    /* 其它设备相关设置  0: off, 1: on */
    public final static int wifipower = 3;


    /*更新APK的网址*/
    public final static String apkUpdate = "http://releases.b0.upaiyun.com/hoolay.apk";
    //文件下载
    public final static String videoTest = "http://192.168.10.179:8080/testcss/download.do";
    //文件上传
    public final static String fileUpload = "http://192.168.10.163:8888/testcss/upload.do";


    /*文件格式*/
    public final static String ffImage = "png,jpg,jpeg";
    public final static String ffVideo = "wmv,mp4,mkv,rmvb";


    /*FTP服务器APP的包名*/
    public final static String ftpPackageName = "be.ppareit.swiftp";


    /* 时间格式 */
    public final static String cformatDay = "yyyy年MM月dd日";
    public final static String cformatD = "MM月dd日";
    public final static String cformatsecond = "yyyy年MM月dd日HH时mm分ss秒";
    public final static String cformatminute = "yyyy年MM月dd日HH时mm分";
    public final static String formatminute = "HH:mm";
    public final static String formatsecond = "yyyy-MM-dd HH:mm:ss";
    public final static String formats = "mm:ss";
    public final static String formatday = "yyyy-MM-dd";
    public final static String formatPhoto = "yyyy年MM月dd日HH时mm分ss秒SSS毫秒";


    /*sharedpreferences的常量值*/
    // sharedpreferences表名
    public static final String SP_name = "lcd";

    // sharedpreferences的key值
    public static final String onOffSet = "onOffSet";
    public static final String onOffinterval = "onOffinterval";
    public static final String changeIP = "changeip";//判断是否修改过IP
    public static final String IP = "ip";//设置Ip
    public static final String gateWay = "gateway";//设置网管
    public static final String netmask = "netmask";//设置子网掩码
    public static final String fragcodestr = "fragcodestr";//二维码碎片的字符串
    public static final String fragcode = "fragcode";//二维码碎片的二维码

}
