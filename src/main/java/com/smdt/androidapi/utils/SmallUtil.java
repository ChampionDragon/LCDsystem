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
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Description: 小工具类
 * AUTHOR: Champion Dragon
 * created at 2017/11/11
 **/

public class SmallUtil {
    private static String tag = "SmallUtil";

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


    /**
     * 二次压缩，先按照像素压缩再按照质量压缩
     *
     * @param imgUrl   图片路径
     * @param reqWidth 期望宽度 可以根据市面上的常用分辨率来设置
     * @param size     期望图片的大小，单位为kb
     * @param quality  图片压缩的质量，取值1-100，越小表示压缩的越厉害，如输入30，表示压缩70%
     * @return Bitmap 压缩后得到的图片
     */
    public static Bitmap compressBitmap(String imgUrl, int reqWidth, int size, int quality) {
        // 创建bitMap
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgUrl, options);
        int height = options.outHeight;
        int width = options.outWidth;
        int reqHeight;
        reqHeight = (reqWidth * height) / width;
        // 在内存中创建bitmap对象，这个对象按照缩放比例创建的
        options.inSampleSize = calculateInSampleSize(
                options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        Bitmap bm = BitmapFactory.decodeFile(
                imgUrl, options);
        Bitmap mBitmap = compressImage(Bitmap.createScaledBitmap(
                bm, 480, reqHeight, false), size, quality);
//        Logs.d("smallUtil272  压缩后图片大小： " + mBitmap.getByteCount() / 1024);
        return mBitmap;
    }


    /**
     * 质量压缩图片，图片占用内存减小，像素数不变，常用于上传
     *
     * @param image
     * @param size    期望图片的大小，单位为kb
     * @param options 图片压缩的质量，取值1-100，越小表示压缩的越厉害,如输入30，表示压缩70%
     */
    private static Bitmap compressImage(Bitmap image, int size, int options) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        image.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
        while (baos.toByteArray().length / 1024 > size) {
            options -= 10;// 每次都减少10
            baos.reset();// 重置baos即清空baos
            // 这里压缩options%，把压缩后的数据存放到baos中
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }
        // 把压缩后的数据baos存放到ByteArrayInputStream中
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        // 把ByteArrayInputStream数据生成图片
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
        return bitmap;
    }


    /*计算像素压缩的缩放比例*/
    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;


        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }


    /**
     * 存储照片并返回文件
     */
    public static File SavePhoto(Bitmap bitmap, String path, String name) {
        String localpath = null;
        File photoFile = null;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            File fileDir = new File(path);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
            photoFile = new File(fileDir, name + ".png");
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(photoFile);
                if (bitmap != null) {
                    if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)) {
                        localpath = photoFile.getAbsolutePath();
                        try {
                            fos.flush();
                        } catch (IOException e) {
                            Logs.d("photoutil_72     " + e.getMessage());
                            photoFile.delete();
                            localpath = null;
                            e.printStackTrace();
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                Logs.d("photoutil_80     " + e.getMessage());
                photoFile.delete();
                localpath = null;
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    fos = null;
                }
            }

        }
        return photoFile;
    }


    public static String RootCommand(String command) {
        String result = "";
        Process process = null;
        DataOutputStream os = null;
        DataInputStream is = null;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
            is = new DataInputStream(process.getInputStream());
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            String out = new String(buffer);
            result = out;
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Logs.e(tag + "407:\n" + e);
            }
            process.destroy();
        }
        return result;
    }

    /**
     * 获取子网掩码
     */
    public static String getnetmask() {
        String result = RootCommand("ifconfig eth0");
        String[] split = result.split(" ");
//        Logs.w(tag + "418:\n" + Arrays.toString(split) + "\n" + result);
        result = split[4];
        return result;
    }

    /**
     * 获取网关
     */
    public static String getgateWay() {
        String result = RootCommand("ip route show");
        String[] split = result.split(" ");
//        Logs.w(tag + "418:\n" + Arrays.toString(split) + "\n" + result);
        result = split[2];
        return result;
    }


}
