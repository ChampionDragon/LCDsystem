package com.smdt.androidapi.fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.app.Service;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.smdt.androidapi.R;
import com.smdt.androidapi.base.AsyncTaskExecutor;
import com.smdt.androidapi.base.BaseApplication;
import com.smdt.androidapi.utils.Constant;
import com.smdt.androidapi.utils.DialogCustomUtil;
import com.smdt.androidapi.utils.Logs;
import com.smdt.androidapi.utils.NetConnectUtil;
import com.smdt.androidapi.utils.SmallUtil;
import com.smdt.androidapi.utils.ToastUtil;
import com.smdt.androidapi.view.DialogLoading;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;

import static android.media.AudioManager.ADJUST_LOWER;
import static android.media.AudioManager.ADJUST_RAISE;
import static android.media.AudioManager.FLAG_SHOW_UI;
import static android.media.AudioManager.STREAM_MUSIC;

/**
 * Description: 视频碎片类
 * AUTHOR: Champion Dragon
 * created at 2017/11/17
 **/

public class VideoFragment extends Fragment implements View.OnClickListener {
    private View view;
    private VideoView videoView;
    private Uri url;

    private String videoUrl = "";
    private List<String> videoList;//通过搜索本地资源
    private List<String> vl;//变成播放完了再遍历一遍

    private String netUrl = Constant.viedoUrl;
    //private SmdtManager smdt;//smdt是控制板的jdk对象有厂家提供
    private String tag = "VideoFragment";
    private Dialog videoErroDia;
    private DialogLoading dialoading;
    private AudioManager mAudioManager;//声音
    private LinearLayout Videomenu;
    private int videoNum;//设置视频轮播的编号
    private CheckBox videoCheck;
    private LinearLayout videoCtrl;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragmentvideo, container, false);
        Logs.d(tag + "  onCreateView");
        mAudioManager = (AudioManager) getActivity().getSystemService(Service.AUDIO_SERVICE);
        initView();
        initVideo();
        return view;
    }

    /*初始化播放视频的地址*/
    private void initUrl() {
        String TargetUrl = Constant.PathLS;
        videoList = SmallUtil.getViedoPath(TargetUrl);
        Logs.i("数量 " + Arrays.toString(videoList.toArray()));
        if (videoList.size() > 0) {
            /*默认遍历出来的第一视频为播放地址*/
            if (videoList.size() <= videoNum) {
                videoNum = 0;
            }
            videoUrl = videoList.get(videoNum);
            videoURL(videoUrl);
        } else {
            ShowErrorDialog("不存在");
        }
    }

    @Override
    public void onResume() {
        Logs.d(tag + "  onResume");
        super.onResume();
        initUrl();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Logs.d("videoFragment状态 " + hidden);
        if (videoView != null) {
            if (hidden) {//不在最前端显示 相当于调用了onPause();
                videoView.pause();
            } else {
                videoView.start();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (videoView != null) {
            videoView.pause();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (videoView != null) {
            videoView.start();
        }
    }

    private void initView() {
        Videomenu = (LinearLayout) view.findViewById(R.id.lcdVideo);
        Videomenu.setOnClickListener(this);
        view.findViewById(R.id.voladd).setOnClickListener(this);
        view.findViewById(R.id.volsub).setOnClickListener(this);
        view.findViewById(R.id.videos_full).setOnClickListener(this);
        view.findViewById(R.id.videos_half).setOnClickListener(this);
        videoView = (VideoView) view.findViewById(R.id.lcdvideoView);
        videoCheck = (CheckBox) view.findViewById(R.id.videocheck);
        videoCheck.setOnClickListener(this);
        videoCtrl = (LinearLayout) view.findViewById(R.id.videoCtrl);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.voladd:
                mAudioManager.adjustStreamVolume(STREAM_MUSIC, ADJUST_RAISE, FLAG_SHOW_UI);
                break;
            case R.id.volsub:
                mAudioManager.adjustStreamVolume(STREAM_MUSIC, ADJUST_LOWER, FLAG_SHOW_UI);
                break;
            case R.id.videos_full:
                setVideoViewLayoutParams(1);
                break;
            case R.id.videos_half:
                setVideoViewLayoutParams(2);
                break;
            case R.id.lcdVideo:
                showMenu(Videomenu);
                break;
            case R.id.videocheck:
                if (videoCheck.isChecked()) {
                    videoCtrl.setVisibility(View.VISIBLE);
                } else {
                    videoCtrl.setVisibility(View.GONE);
                }
                break;
        }
    }


    /*通过URL播放视频*/
    private void videoURL(String netUrl) {
        // 设置视频路径
        url = Uri.parse(netUrl);
        videoView.setVideoURI(url);
        // 开始播放视频
//        Logs.e("1111111111111111播放了111111111111111");
        videoView.start();
    }

    /*显示视频播放的menu*/
    private void showMenu(View menu) {
        PopupMenu popmenu = new PopupMenu(getActivity(), menu);
        popmenu.getMenuInflater().inflate(R.menu.videos, popmenu.getMenu());
        popmenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.videos_net:
                        videoURL(netUrl);
                        break;
                    case R.id.videos_local:
                        videoURL(videoUrl);
                        break;
                }
                return false;
            }
        });
        popmenu.show();
    }


    /**
     * 设置videiview的全屏和窗口模式
     *
     * @param i 标识 1为全屏模式 2为窗口模式
     */
    private void setVideoViewLayoutParams(int i) {
        if (i == 1) {
        /*全屏模式*/
            //设置充满整个父布局
            RelativeLayout.LayoutParams LayoutParams = new RelativeLayout.LayoutParams
                    (RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            //设置相对于父布局四边对齐
            LayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            LayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            LayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            LayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            //为VideoView添加属性
            videoView.setLayoutParams(LayoutParams);

        } else {
            /*窗口模式*/
            //获取整个屏幕的宽高
            DisplayMetrics DisplayMetrics = new DisplayMetrics();
//            getResources().getDisplayMetrics().heightPixels
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(DisplayMetrics);
            int videoHeight = DisplayMetrics.heightPixels * 2 / 3;//标题的view的高
            int videoWidth = DisplayMetrics.widthPixels;
            RelativeLayout.LayoutParams LayoutParams = new RelativeLayout.LayoutParams(videoWidth, videoHeight);
            //设置居中
            LayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            //为VideoView添加属性
            videoView.setLayoutParams(LayoutParams);
        }
    }


    private void initVideo() {
        // 播放完成回调
        videoView.setOnCompletionListener(completion);
        videoView.setOnErrorListener(errorListener);
        setVideoViewLayoutParams(2);
        videoView.setMediaController(new MediaController(getActivity()));
    }

    /*视频播放完成的监听*/
    MediaPlayer.OnCompletionListener completion = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            if (videoList.size() - 1 <= videoNum) {
                videoNum = 0;
            } else {
                ++videoNum;
            }
//            Logs.e("视频轮播 " + videoList.size() + "  " + videoNum);
            videoUrl = videoList.get(videoNum);

            /*每次遍历一遍视频文件*/
            vl = SmallUtil.getViedoPath(Constant.PathLS);

            /*每次遍历判断是否视频文件被删除了*/
            Logs.e("每次遍历的大小：" + vl.size() + "   之前遍历的大小" + videoList.size());
            if (vl.size() > videoList.size()) {
                initUrl();
            }
            if (SmallUtil.fileIsExists(videoUrl)) {
                videoURL(videoUrl);
            } else {
                initUrl();
            }
        }


    };


    /*播放错误的回调函数*/
    MediaPlayer.OnErrorListener errorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Logs.e(tag + "289   " + what + "   " + extra);
            ToastUtil.showShort("无法播放");
            initUrl();
//            设置成true就不会有播放视频"错误的弹框"
            return true;
        }

    };

    //"播放文件不存在或已损坏\n是否启用FTP服务器接收文件"
    /*弹出是否下载资源的弹框*/
    private void ShowErrorDialog(String errorStr) {
        videoErroDia = DialogCustomUtil.create("警告", getString(R.string.videoerror, errorStr),
                getActivity(), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        videoErroDia.dismiss();
//                            downBydialog();
                        SmallUtil.startAPP(Constant.ftpPackageName, getActivity());
                        BaseApplication.getInstance().onTerminate();
                    }
                });
        videoErroDia.show();
    }








/*+++++++++++++++++++++++++++++++++++++       从后台接口下载视频文件    +++++++++++++++++++++++++++++++++++++++++++*/

    private void downBydialog() {
        dialoading = new DialogLoading(getActivity(), "准备下载");
        dialoading.show();
        AsyncTaskExecutor.getinstance().submit(apkRunnable);
    }

    private int myprogress;
    Runnable apkRunnable = new Runnable() {
        @Override
        public void run() {
            OkHttpUtils.get().url(Constant.videoTest).build().execute(new FileCallBack(
                    Constant.fileLS.getAbsolutePath(), "1.mp4") {
                @Override
                public void onError(Call call, Exception e, int i) {
                    if (NetConnectUtil.NetConnect(getActivity())) {
                        ToastUtil.showLong("服务器异常,文件下载失败");
                    } else {
                        ToastUtil.showLong("未连接到网络,文件下载失败");
                    }
                    Logs.e(tag + "331播放错误原因 " + e + "  " + i);
                    dialoading.close();
                }

                @Override
                public void onResponse(File file, int i) {
                    dialoading.setTv("下载完成");
                    dialoading.close();
//                    Logs.v(tag + " 338 " + file.getAbsolutePath() + "  " + i);
                    //下载完成后开始播放
                    videoURL(videoUrl);
                }

                @Override
                public void inProgress(float progress, long total, int id) {
                    super.inProgress(progress, total, id);
                    myprogress = (int) (progress * 100);
                    Logs.v(progress + "   " + myprogress);
//                    if (myprogress % 5 == 0) {
                    dialoading.setTv("下载进度：" + myprogress + "%");
//                    }
                }
            });
        }
    };


}
