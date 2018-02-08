package com.smdt.androidapi.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.smdt.SmdtManager;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;

/**
 * Description: 创建程序的基本类
 * AUTHOR: Champion Dragon
 * created at 2017/11/11
 **/

public class BaseActivity extends Activity {
    protected ProgressDialog mProgressDialog;
    public AsyncTaskExecutor executor;
    public SmdtManager smdt;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        smdt = SmdtManager.create(getApplicationContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {// 版本19或以上使用
            // 透明导航栏
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            // 透明状态栏
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            // 设置窗口全屏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        executor = AsyncTaskExecutor.getinstance();
    }

    public void stopProgressDialog() {
        if (mProgressDialog.isShowing() && mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
        }
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }
}
