package com.smdt.androidapi.utils;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smdt.androidapi.R;

/**
 * Description: 文字信息的弹框
 * AUTHOR: Champion Dragon
 * created at 2018/5/21
 **/

public class DialogText {
    private TextView codeTv;
    private Context context;
    private Dialog dialog;

    public DialogText(String str, Context context) {
        this.context = context;
        dialog = new Dialog(context, R.style.dialog);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_text, null);
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        codeTv = (TextView) view.findViewById(R.id.text_tv);
        codeTv.setText(str);
        dialog.setCancelable(false);
        dialog.setContentView(view, new LinearLayout.LayoutParams(
                dm.widthPixels * 4 / 5,
                LinearLayout.LayoutParams.MATCH_PARENT));
        dialog.show();
    }


    /*更新二维码下面的文字*/
    public void updateTv(String tv) {
        if (dialog != null) {
            codeTv.setText(tv);
        }
    }


    public void closeDia() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }


}
