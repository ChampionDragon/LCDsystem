package com.smdt.androidapi.fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.smdt.androidapi.R;
import com.smdt.androidapi.adapter.ImageViewAdapter;
import com.smdt.androidapi.base.BaseApplication;
import com.smdt.androidapi.utils.Constant;
import com.smdt.androidapi.utils.DialogCustomUtil;
import com.smdt.androidapi.utils.Logs;
import com.smdt.androidapi.utils.LruCacheView;
import com.smdt.androidapi.utils.SmallUtil;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
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
    private ViewPager vp;
    //    private int[] pics ={R.drawable.img1, R.drawable.img2, R.drawable.img3,
// R.drawable.img4, R.drawable.img5};//图片集合:通过app自带资源。
    private List<String> imgUrl;//通过搜索本地资源
    private ImageView[] dots;//点的集合
    private List<ImageView> list;
    private int imgsize;// 整个适配器的总长
    private ScheduledExecutorService scheduledExecutorService;// 定时周期执行指定任务
    private int currentIndex;// （自动播放时）定时周期要显示的图片的索引（viewpager中的图片位置）
    private int dotIndex = 1;// 设置当前点的索引
    private Dialog videoErroDia;
    private String tag = "ImageFragment";

    //private Map<Integer, String> map;
    //private Map<Integer, Boolean> isSave;
    private LruCacheView lruCacheView;

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
//        imgUrl = new ArrayList<>();
//        LCDActivity activity = (LCDActivity) getActivity();
        String TargetUrl = Constant.PathLS;
//        if (TargetUrl == null) {
//            Logs.v("外部USB根路径  " + activity.smdt.smdtGetUSBPath(getActivity(), 0));
//            TargetUrl = Constant.rootPath;
//            Logs.e("内部存储根路径  " + Constant.rootPath);
//        }

        imgUrl = SmallUtil.getImgPath(TargetUrl);
        Logs.v("数量 " + Arrays.toString(imgUrl.toArray()));

        // 整个适配器的总长
//        Logs.e(Arrays.toString(imgUrl.toArray()));


        imgsize = imgUrl.size() + 2;
        if (imgUrl.size() == 0) {
            ShowDialog("未搜索到图片资源文件\n是否启用FTP服务器接收文件");
        } else if (imgUrl.size() < 2) {
            ShowDialog("至少要两张图片才能实现滚轮效果\n是否启用FTP服务器接收文件");
        } else if (imgUrl.size() > 6) {
            ShowDialog("播放图片不宜超过六张,容易内存溢出\n是否启用FTP服务器删除文件");
        } else {
//            map = new HashMap<>();
//            isSave = new HashMap<>();
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
        vp = (ViewPager) view.findViewById(R.id.fraImaVp);
        initDot();
        initViewpage();
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
            params.setMargins(distance, distance, distance, distance);
            dots[i].setLayoutParams(params);
            dots[i].setImageResource(R.drawable.dot_bg);
            ll.addView(dots[i]);
        }
        dots[0].setPressed(true);
    }

    /**
     * viewpager初始化
     */
    private void initViewpage() {
        if (scheduledExecutorService == null) {
            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
            scheduledExecutorService.scheduleWithFixedDelay(new ViewPagerTask(), 6,
                    3, TimeUnit.SECONDS);// 自动播放
        }
        vp.setOnPageChangeListener(pageChangeListener);//替代方法addOnPageChangeListener
        list = new ArrayList<>();
        for (int i = 0; i < imgsize; i++) {
            ImageView imageView = new ImageView(getActivity());
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            list.add(imageView);
        }
        setImgRes(imgsize);
    }

    private void setImgRes(int length) {
        /*通过app自带资源*/
//        list.get(0).setImageResource(pics[pics.length - 1]);
//        list.get(length - 1).setImageResource(pics[0]);
//        for (int i = 0; i < length - 2; i++) {
//            list.get(i + 1).setImageResource(pics[i]);
//        }
        /*通过本地加载*/
//        lruCacheView = new LruCacheView();
//        lruCacheView.setView(imgUrl.get(imgUrl.size() - 1), list.get(0));
//        lruCacheView.setView(imgUrl.get(0), list.get(imgsize - 1));

        /*添加一头一尾资源*/
        Picasso.with(getActivity()).load(new File(imgUrl.get(imgUrl.size() - 1))).placeholder(R.drawable.loadingpic).into(list.get(0));
        Picasso.with(getActivity()).load(new File(imgUrl.get(0))).placeholder(R.drawable.loadingpic).into(list.get(imgsize - 1));

//        map.put(0, imgUrl.get(imgUrl.size() - 1));
//        map.put(imgsize - 1, imgUrl.get(0));
//        list.get(0).setImageBitmap(LruCacheView.getLoacalBitmap(imgUrl.get(imgUrl.size() - 1)));
//        list.get(imgsize - 1).setImageBitmap(LruCacheView.getLoacalBitmap(imgUrl.get(0)));

        for (int i = 0; i < length - 2; i++) {
//            lruCacheView.setView(imgUrl.get(i), list.get(i + 1));

            Picasso.with(getActivity()).load(new File(imgUrl.get(i))).placeholder(R.drawable.loadingpic).into(list.get(i + 1));

//            list.get(i + 1).setImageBitmap(LruCacheView.getLoacalBitmap(imgUrl.get(i)));
//            map.put(i + 1, imgUrl.get(i));
        }


//        isSave.put(0, true);
//        isSave.put(imgsize - 1, true);
//        for (int i = 0; i < length - 2; i++) {
//            lruCacheView.setView(imgUrl.get(i), list.get(i + 1));
//            isSave.put(i + 1, false);
//        }
       /*默认选中物理第二张图片*/
        vp.setAdapter(new ImageViewAdapter(list));
        vp.setCurrentItem(1);
    }


    ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (positionOffset == 0.0) {
                if (position == 0) {
                    vp.setCurrentItem(imgsize - 2, false);
                } else if (position == imgsize - 1) {
                    vp.setCurrentItem(1, false);
                }
            }
        }

        @Override
        public void onPageSelected(int position) {
//            lruCacheView.setView(map.get(position), list.get(position));
//            if (!isSave.get(position)) {
//            Bitmap bitmap = LruCacheView.getLoacalBitmap(map.get(position));
//            WeakReference weakReference = new WeakReference(bitmap);
//            list.get(position).setImageBitmap((Bitmap) weakReference.get());
//                isSave.put(position, true);
//            }
            currentIndex = position;// 把当前页的索引记住，方便跳转到下一页（这是必须的）
            if (currentIndex != imgsize - 1 && currentIndex != 0) {
                setCurrentDot(currentIndex);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    /**
     * 设置当前点的颜色
     */
    private void setCurrentDot(int currentIndex) {
//        Logs.d(tag + " 241  dot序号:" + currentIndex);
        dots[currentIndex - 1].setPressed(true);
        dots[dotIndex].setPressed(false);
        dotIndex = currentIndex - 1;
    }

    /**
     * 来定时播放图片的线程
     */
    private class ViewPagerTask implements Runnable {
        @Override
        public void run() {
            currentIndex++;
            handler.obtainMessage().sendToTarget();
        }
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            // 使viewpager跳转到指定页（true:带有滑动效果）
            vp.setCurrentItem(currentIndex, true);
        }
    };


}
