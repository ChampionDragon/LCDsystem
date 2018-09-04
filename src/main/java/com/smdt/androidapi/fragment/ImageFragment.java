package com.smdt.androidapi.fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.smdt.androidapi.R;
import com.smdt.androidapi.base.BaseApplication;
import com.smdt.androidapi.utils.Constant;
import com.smdt.androidapi.utils.DialogCustomUtil;
import com.smdt.androidapi.utils.Logs;
import com.smdt.androidapi.utils.SmallUtil;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Description: 图片碎片类
 * AUTHOR: Champion Dragon
 * created at 2017/11/17
 **/

public class ImageFragment extends Fragment {
    private View view;
    private List<String> imgUrl;//通过搜索本地资源
    private ImageView[] dots;//点的集合
    private ImageView ivFra;//播放的图片ImageView
    private int imgsize;// 图片的的总数
    private int imgCurrent;//当前图片播放的位置
    private int dotIndex;// 设置当前点的索引
    private Bitmap bitmap;//加载时替代的bitmap
    //    private Drawable drawable;//加载时替代的drawable
    private ScheduledExecutorService scheduledExecutorService;// 定时周期执行指定任务
    private Dialog videoErroDia;
    private String tag = "ImageFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragmentimage, container, false);
        Logs.d(tag + "  onCreateView");
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initImgSize();
        Logs.d(tag + "  onResume");
    }

    private void initImgSize() {
        imgUrl = SmallUtil.getImgPath(Constant.PathLS);
        Logs.v("数量 " + Arrays.toString(imgUrl.toArray()));
        imgsize = imgUrl.size();
        if (imgUrl.size() == 0) {
            ShowDialog("未搜索到图片资源文件\n是否启用FTP服务器接收文件");
        } else {
            initView();
        }
    }

    private void ShowDialog(String ss) {
        videoErroDia = DialogCustomUtil.create("警告", ss,
                getActivity(), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        videoErroDia.dismiss();
                        SmallUtil.startAPP(Constant.ftpPackageName, getActivity());
                        BaseApplication.getInstance().onTerminate();
                    }
                });
        videoErroDia.show();
    }


    private void initView() {
        ivFra = (ImageView) view.findViewById(R.id.fraIv);
        initDot();
        initImageView();
    }

    /*初始化下面的点*/
    private void initDot() {
        LinearLayout ll = (LinearLayout) view.findViewById(R.id.fraImall);
//        由于是自定义添加子布局为了防止子布局每次重复添加，添加之前先清空
        ll.removeAllViews();

        dots = new ImageView[imgUrl.size()];
        for (int i = 0; i < imgUrl.size(); i++) {
            //动态添加滚轮下面的小点
            dots[i] = new ImageView(getActivity());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            int distance = 6;
            if (imgsize > 6) {
                distance = 5;
            } else if (imgsize > 8) {
                distance = 4;
            } else if (imgsize > 10) {
                distance = 3;
            } else if (imgsize > 12) {
                distance = 2;
            } else {
                distance = 1;
            }
            params.setMargins(distance, distance, distance, distance);
            dots[i].setLayoutParams(params);
            dots[i].setImageResource(R.drawable.dot_bg);
            ll.addView(dots[i]);
        }
        dots[0].setPressed(true);
    }

    /**
     * ImageView初始化
     */
    private void initImageView() {
        if (scheduledExecutorService == null) {
            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
            scheduledExecutorService.scheduleWithFixedDelay(new ViewPagerTask(), 1,
                    6, TimeUnit.SECONDS);// 自动播放
        } else {
            Logs.e("当前点的位置：  " + imgCurrent);
            dots[0].setPressed(false);
        }
        Picasso.with(getActivity())
                .load(new File(imgUrl.get(imgCurrent)))
                .resize(800, 1080)
//                   .load(SmallUtil.SavePhoto(SmallUtil.compressBitmap(imgUrl.get(imgCurrent), 1080, 1333, 66),
//                            Constant.Pathroot, imgUrl.get(imgCurrent)))
                .placeholder(R.drawable.loadingpic).into(ivFra);
//             .placeholder(R.drawable.loadingpic)     new File(imgUrl.get(imgCurrent))

        bitmap = SmallUtil.compressBitmap(imgUrl.get(imgCurrent), 800, 666, 20);
//            drawable = new BitmapDrawable(bitmap);
    }


    /**
     * 设置当前点的颜色
     */
    private void setCurrentDot(int currentIndex) {
        dots[currentIndex].setPressed(true);
        dots[dotIndex].setPressed(false);
        dotIndex = currentIndex;
    }

    /**
     * 来定时播放图片的线程
     */
    private class ViewPagerTask implements Runnable {
        @Override
        public void run() {
            if (imgCurrent > imgsize - 2) {
                imgCurrent = 0;
            } else {
                ++imgCurrent;
            }
            handler.obtainMessage().sendToTarget();
        }
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            setCurrentDot(imgCurrent);
            String imgPath = imgUrl.get(imgCurrent);
            List<String> il = SmallUtil.getImgPath(Constant.PathLS);
//            Logs.d(tag+"183 每次遍历的大小：" + il.size() + "   之前遍历的大小" + imgsize);
            if (il.size() > imgsize) {
                initImgSize();
                return;
            }

            if (!SmallUtil.fileIsExists(imgPath)) {
                initImgSize();
                return;
            }
            Picasso.with(getActivity())
                    .load(new File(imgPath))
                    .resize(800, 1080)
//                  .load(SmallUtil.SavePhoto(SmallUtil.compressBitmap(imgUrl.get(imgCurrent), 1980, 1999, 88),
//                            Constant.Pathroot, imgUrl.get(imgCurrent)))
                    .placeholder(new BitmapDrawable(bitmap)).into(ivFra);
            bitmap = SmallUtil.compressBitmap(imgUrl.get(imgCurrent), 800, 666, 20);
//            drawable = new BitmapDrawable(bitmap);  new File(imgUrl.get(imgCurrent))
// .placeholder(drawable)  .placeholder(R.drawable.loadingpic)   ,1980
        }
    };
}
