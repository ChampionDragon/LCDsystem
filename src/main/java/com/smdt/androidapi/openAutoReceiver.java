package com.smdt.androidapi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Description: 开机自动启动APP的Receiver类
 * AUTHOR: Champion Dragon
 * created at 2017/11/11
 **/
public class openAutoReceiver extends BroadcastReceiver {
    public openAutoReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Intent i = new Intent(context, LCDActivity.class);
//            非常重要，如果缺少的话，程序将在启动时报错
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}
