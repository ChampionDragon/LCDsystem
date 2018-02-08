package com.smdt.androidapi.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.LruCache;
import android.widget.ImageView;

import com.smdt.androidapi.R;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Description: 加载图片缓存,防止OOM。
 * AUTHOR: Champion Dragon
 * created at 2017/11/20
 **/

public class LruCacheView {
    private LruCache<ImageView, Bitmap> lruCache;
//    private int i;
//    private Bitmap bitmap;

    public LruCacheView() {
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 4;
        Logs.e("系统占用内存大小：  " + cacheSize);
        // 创建LruCache对象,同时用匿名内部类的方式重写方法
        lruCache = new LruCache<ImageView, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(ImageView key, Bitmap value) {
//return super.sizeOf(key, value);
// 在每次存入缓存的时候调用,我们需要直接返回Bitmap value的实际大小
                return value.getByteCount();
            }
        };
    }

    /*存储数据到LruCache*/
    private void addToCache(ImageView img, Bitmap bitmap) {
        if (getFromCache(img) == null) {
            lruCache.put(img, bitmap);
        }
    }

    /*从LruCache得到数据*/
    private Bitmap getFromCache(ImageView img) {
        return lruCache.get(img);
    }

    /*向imageView添加数据*/
    public void setView(String str, ImageView imageView) {
        if (getFromCache(imageView) == null) {
//            Logs.d(imageView + "NoKey");
            new ViewAsyncTask(imageView).execute(str);
//            addToCache(imageView, str);
        } else {
            Logs.e(imageView + "");
            /*释放内存*/
//            if (bitmap != null && !bitmap.isRecycled()) {
//                bitmap.recycle();
//                bitmap = null;
//            }
//            System.gc();
//            bitmap = getLoacalBitmap(str);
            imageView.setImageBitmap(getFromCache(imageView));
        }
    }


    /*把图片地址通过流转化为Bitmap*/
    public static Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private class ViewAsyncTask extends AsyncTask<String, Boolean, Bitmap> {
        private ImageView img;

        public ViewAsyncTask(ImageView imageView) {
            img = imageView;
        }

        //在执行execute(Params...params)后立刻执行此方法,一般用来在于执行后台任务时做些UI标记。
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            img.setImageResource(R.drawable.loadingpic);
        }

        //用于处理耗时的一步操作,所以该方法不能对UI进行操作.但可以通过调用publishProgress触发onProgressUpdate对UI进行操作
        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = getLoacalBitmap(params[0]);//对应第一个参数

            Logs.i(bitmap.getByteCount() / 1024 + "KB  ");
            bitmap = SmallUtil.SizeImage(bitmap);
            Logs.v(bitmap.getByteCount() / 1024 + "KB 压缩 ");

            addToCache(img, bitmap);
            publishProgress(true);//对应第二个参数
            return bitmap;//对应第三个参数
        }

        //第二个参数的返回值
        @Override
        protected void onProgressUpdate(Boolean... values) {
            super.onProgressUpdate(values);
//            Logs.i("操作   " + values[0]);
        }

        //第三个参数返回值
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            img.setImageBitmap(bitmap);
        }
    }


}
