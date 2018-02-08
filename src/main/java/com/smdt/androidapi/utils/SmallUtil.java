package com.smdt.androidapi.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.WindowManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Description: 小工具类
 * AUTHOR: Champion Dragon
 * created at 2017/11/11
 **/

public class SmallUtil {

    /**
     * 直接跳转到某个界面
     */
    public static void getActivity(Activity a, Class<?> cls) {
        a.startActivity(new Intent(a, cls));
    }

    /**
     * 携带bundle值跳转到某个界面
     */
    public static void getActivity(Activity a, Class<?> cls, Bundle bundle) {
        a.startActivity(new Intent(a, cls).putExtras(bundle));
    }

    /**
     * 设置添加屏幕的背景透明度
     */
    public static void backgroundAlpaha(Activity context, float bgAlpha) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        context.getWindow()
                .addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        context.getWindow().setAttributes(lp);
    }

    /**
     * 将传入的int值转变为网络地址.例如:192.168.0.1
     */
    public static String intToString(int a) {
        StringBuffer sb = new StringBuffer();
        int b = (a >> 0) & 0xff;
        sb.append(b + ".");
        b = (a >> 8) & 0xff;
        sb.append(b + ".");
        b = (a >> 16) & 0xff;
        sb.append(b + ".");
        b = (a >> 24) & 0xff;
        sb.append(b);
        return sb.toString();
    }

    /*创建文件夹*/
    public static boolean setFile(String path) {
        if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            String rootPath = Environment.getExternalStorageDirectory().toString();
            File f = new File(new File(rootPath), path);
            if (!f.exists()) {
                try {
                    f.mkdirs();
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /*判断文件是否存在*/
    public static boolean fileIsExists(String strFile) {
        try {
            File f = new File(strFile);
            if (!f.exists()) {
                return false;
            }

        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public static List<String> list = new ArrayList<>();

    /*扫描指定目录下、指定格式的文件并返回路径*/
    public static List<String> getPath(String path, String fileFormat) {
        String ss = "";
        File file = new File(path);
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] fileArray = file.listFiles();
//                Logs.e(Arrays.toString(fileArray)+"  这个可能会为空");
                if (fileArray != null) {
                    for (File f : fileArray) {
                        if (f.isDirectory()) {
                            getPath(f.getPath(), fileFormat);
                            Logs.v(f.getPath());
                        } else {
                   /* 截取文件夹内.后的文字从而检测格式*/
                            ss = f.getName().substring(f.getName().lastIndexOf('.') + 1, f.getName().length());
                            //endsWith(".png") || f.getName().endsWith(".jpg")
                            //contains 当且仅当此字符串包含指定的 char 值序列时，返回 true。
                            if (fileFormat.contains(ss)) {
//                                Logs.w(f.getAbsolutePath());
                                list.add(f.getAbsolutePath());
                            }
                        }
                    }
                }
            }
        }
        return list;
    }

    /*返回此路径下图片文件的路径*/
    public static List<String> getImgPath(String path) {
        getPath(path, Constant.ffImage);
        List<String> list2 = new ArrayList<>();
        list2.addAll(list);
        list.clear();
        return list2;
    }

    /*返回此路径下视频文件的路径*/
    public static List<String> getViedoPath(String path) {
        getPath(path, Constant.ffVideo);
        List<String> list2 = new ArrayList<>();
        list2.addAll(list);
        list.clear();
        return list2;
    }


    /**
     * 压缩图片
     */
    public static Bitmap SizeImage(Bitmap image) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, os);
        /*本来我想一直将图片压缩到999KB以下,但压缩超过两次就无法再压缩了（陷入死循环)*/
//        while (os.toByteArray().length / 1024 > 999) {
//        Logs.d("正在压缩  " + os.toByteArray().length / 1024);
//        os.reset();
//        image.compress(Bitmap.CompressFormat.JPEG, 55, os);
//        Logs.i("正在压缩  " + os.toByteArray().length / 1024);
//        }

        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(is, null, options);
        options.inJustDecodeBounds = false;
        int size = os.toByteArray().length / 1024;
        int b = size / 888 + 1;//压缩到888KB以下
//        int h = options.outHeight;
//        int w = options.outWidth;
//        int hh = 1920;
//        int ww = 1080;
//        if (h > w && h > hh) {
//            b = h / hh;
//        } else if (w > h && w > ww) {
//            b = w / ww;
//        }
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inSampleSize = b;//改变图片压缩比例
        Logs.i(" 比例：" + b + " " + size);
        is = new ByteArrayInputStream(os.toByteArray());
        bitmap = BitmapFactory.decodeStream(is, null, options);
        return bitmap;
    }

    /*通过包名直接打开APP*/
    public static void startAPP(String packagename, Context context) {
        // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
        PackageInfo packageinfo = null;
        try {
            packageinfo = context.getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageinfo == null) {
            DialogNotileUtil.show(context, "此程序未安装");
            return;
        }
        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);
        // 通过getPackageManager()的queryIntentActivities方法遍历
        List<ResolveInfo> resolveinfoList = context.getPackageManager()
                .queryIntentActivities(resolveIntent, 0);
        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null) {
            // packagename = 参数packname
            String packageName = resolveinfo.activityInfo.packageName;
            // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
            String className = resolveinfo.activityInfo.name;
            // LAUNCHER Intent
            startAPP(packageName, className, context);
        }
    }

    /*通过包名和类名打开APP*/
    public static void startAPP(String packageName, String className, Context context) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        // 设置ComponentName参数1:packagename参数2:MainActivity路径
        ComponentName cn = new ComponentName(packageName, className);
        intent.setComponent(cn);
        context.startActivity(intent);
    }

    /*通过包名直接打开APP方式二：不同点，它会新开个栈，之前那个是在原APP上覆盖*/
    public static void starAPP(String packagename, Context context) {
        // 这里的packname就是从上面得到的目标apk的包名
        Intent resolveIntent = context.getPackageManager().getLaunchIntentForPackage(packagename);
        // 启动目标应用
        context.startActivity(resolveIntent);
    }

}
