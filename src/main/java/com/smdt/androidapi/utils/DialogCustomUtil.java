package com.smdt.androidapi.utils;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smdt.androidapi.R;

/**
 * Description: 用户选择对话框
 * AUTHOR: Champion Dragon
 * created at 2017/11/11
 **/

public class DialogCustomUtil {
    public static Dialog CreatDialog(String title, String msg, Context context,
                                     View.OnClickListener ok, View.OnClickListener cancel) {
        final Dialog dialog = new Dialog(context, R.style.dialog);
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_custom, null);
        TextView titles = (TextView) view.findViewById(R.id.custom_title);
        titles.setText(title);
        TextView msgs = (TextView) view.findViewById(R.id.custom_msg);
        msgs.setText(msg);
        Button oks = (Button) view.findViewById(R.id.custom_bt_ok);
        oks.setOnClickListener(ok);
        Button cancels = (Button) view.findViewById(R.id.custom_bt_cancel);
        if (cancel == null) {
            cancels.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        } else {
            cancels.setOnClickListener(cancel);
        }
        dialog.setCancelable(false);
        dialog.setContentView(view, new LinearLayout.LayoutParams(
                dm.widthPixels * 4 / 5, LinearLayout.LayoutParams.MATCH_PARENT));

        return dialog;
    }

    public static Dialog create(String title, String msg, Context context,
                                View.OnClickListener ok, View.OnClickListener cancel) {
        Dialog dialog = CreatDialog(title, msg, context, ok, cancel);
        return dialog;
    }

    public static Dialog create(String title, String msg, Context context,
                                View.OnClickListener ok) {
        Dialog dialog = create(title, msg, context, ok, null);
        return dialog;
    }
}
