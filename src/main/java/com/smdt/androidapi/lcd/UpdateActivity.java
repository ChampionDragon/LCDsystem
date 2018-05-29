package com.smdt.androidapi.lcd;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.smdt.androidapi.R;
import com.smdt.androidapi.base.BaseActivity;
import com.smdt.androidapi.utils.Constant;
import com.smdt.androidapi.utils.DialogCustomUtil;
import com.smdt.androidapi.utils.Logs;
import com.smdt.androidapi.utils.NetConnectUtil;
import com.smdt.androidapi.utils.SmallUtil;
import com.smdt.androidapi.utils.SystemUtil;
import com.smdt.androidapi.utils.ToastUtil;
import com.smdt.androidapi.view.DialogLoading;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.util.Random;

import okhttp3.Call;

public class UpdateActivity extends BaseActivity implements View.OnClickListener {
    private int myprogress;
    private Dialog apkDia, videoDia;
    private DialogLoading dialoading;
    String tag = "UpdateActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        findViewById(R.id.update_apk).setOnClickListener(this);
        findViewById(R.id.update_video).setOnClickListener(this);
        findViewById(R.id.back_update).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_update:
                finish();
                break;
            case R.id.update_apk:
                UpdateApk();
                break;
            case R.id.update_video:
                UpdateVideo();
                break;
        }
    }

    /*下载更新广告视频*/
    private void UpdateVideo() {
        if (SmallUtil.fileIsExists(Constant.viedoPathLS)) {
            videoDia = DialogCustomUtil.create("提示", "检查到系统存在视频，是否继续下载。", this, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    videoDia.dismiss();
                    dialoading = new DialogLoading(UpdateActivity.this, "准备下载");
                    dialoading.show();
                    executor.submit(videoRunnable);
                }
            });
            videoDia.show();
        } else {
            dialoading = new DialogLoading(UpdateActivity.this, "准备下载");
            dialoading.show();
            executor.submit(videoRunnable);
        }
    }

    Runnable videoRunnable = new Runnable() {
        @Override
        public void run() {
            OkHttpUtils.get().url(Constant.videoTest).build().execute(new FileCallBack(
                    Constant.fileLS.getAbsolutePath(), "1.mp4") {
                @Override
                public void onError(Call call, Exception e, int i) {
                    if (NetConnectUtil.NetConnect(UpdateActivity.this)) {
                        ToastUtil.showLong("服务器异常,文件下载失败");
                    } else {
                        ToastUtil.showLong("未连接到网络,文件下载失败");
                    }
                    Logs.e(tag + "91 " + e + "  " + i);
                    dialoading.close();
                }

                @Override
                public void onResponse(File file, int i) {
                    dialoading.setTv("下载完成");
                    dialoading.close();
                    Logs.v(tag + " 99 " + file.getAbsolutePath() + "  " + i);
                }

                @Override
                public void inProgress(float progress, long total, int id) {
                    super.inProgress(progress, total, id);
                    myprogress = (int) (progress * 100);
                    dialoading.setTv("下载进度：" + myprogress + "%");
                }
            });
        }
    };

    /*下载更新APK*/
    private void UpdateApk() {
        int i = new Random().nextInt(10);
        Logs.w(tag + "115  " + Constant.fileLS.getAbsolutePath() + SystemUtil.AppName() + ".apk");
        if (i % 2 == 0) {
            apkDia = DialogCustomUtil.create("提示", "检查到系统已经是最新版本，是否还需下载。", this, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    apkDia.dismiss();
                    dialoading = new DialogLoading(UpdateActivity.this, "准备下载");
                    dialoading.show();
                    executor.submit(apkRunnable);
                }
            });
            apkDia.show();
        } else {
            dialoading = new DialogLoading(UpdateActivity.this, "准备下载");
            dialoading.show();
            executor.submit(apkRunnable);
        }
    }


    Runnable apkRunnable = new Runnable() {
        @Override
        public void run() {
            OkHttpUtils.get().url(Constant.apkUpdate).build().execute(new FileCallBack(
                    Constant.fileLS.getAbsolutePath(), Constant.apkNames) {
                @Override
                public void onError(Call call, Exception e, int i) {
                    if (NetConnectUtil.NetConnect(UpdateActivity.this)) {
                        ToastUtil.showLong("服务器异常,文件下载失败");
                    } else {
                        ToastUtil.showLong("未连接到网络,文件下载失败");
                    }
                    Logs.e(tag + "331 " + e + "  " + i);
                    dialoading.close();
                }

                @Override
                public void onResponse(File file, int i) {
                    dialoading.setTv("下载完成");
                    dialoading.close();
                    Logs.v(tag + " 157 " + file.getAbsolutePath() + "  " + i);
                    startActivity(getInstall());
                }

                @Override
                public void inProgress(float progress, long total, int id) {
                    super.inProgress(progress, total, id);
                    myprogress = (int) (progress * 100);
                    dialoading.setTv("下载进度：" + myprogress + "%");
                }
            });
        }
    };

    /*返回安装的意图*/
    private Intent getInstall() {
        File file = new File(Constant.fileLS.getAbsolutePath(), Constant.apkNames);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //系统自带安装程序
        //Uri uri = Uri.fromFile(file);和Uri.parse("file://" + file.getAbsolutePath())相同
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        return intent;
    }
}
