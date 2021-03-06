package com.smdt.androidapi.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smdt.androidapi.LCDActivity;
import com.smdt.androidapi.R;
import com.smdt.androidapi.listener.DiadisListener;
import com.smdt.androidapi.utils.CreateCodeUtil;
import com.smdt.androidapi.utils.DPUtil;
import com.smdt.androidapi.utils.Logs;

/**
 * Description: 生成二维码的Dialog
 * AUTHOR: Champion Dragon
 * created at 2017/11/17
 **/

public class DialogCode {
    private TextView codeTv;
    private ImageView codeIv;
    private Context context;
    private Dialog dialog;

    public DialogCode(Context context, DiadisListener listener) {
        this(context, null, null, listener);
    }

    public DialogCode(Context context, String code, DiadisListener listener) {
        this(context, code, null, listener);
    }

    /**
     * @param code 输出的二维码
     * @param tv   二维码底下的文字
     */
    public DialogCode(Context context, String code, String tv, final DiadisListener listener) {
        this.context = context;
        dialog = new Dialog(context, R.style.dialogcode);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_code, null);
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        codeTv = (TextView) view.findViewById(R.id.codeTv);
        codeIv = (ImageView) view.findViewById(R.id.codeIv);
        if (tv == null) {
            codeTv.setText("请扫码付款");
        } else {
            codeTv.setText(tv);
        }
        if (code == null) {
//            codeIv.setImageBitmap(getBitamap("欢迎来到联胜智能LCD屏系统"));
            codeIv.setVisibility(View.GONE);
        } else {
            codeIv.setImageBitmap(getBitamap(code));
        }
        view.findViewById(R.id.codeCross).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                listener.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.setContentView(view, new LinearLayout.LayoutParams(
                dm.widthPixels, dm.heightPixels));//LinearLayout.LayoutParams.MATCH_PARENT
        dialog.show();
        LCDActivity activity = (LCDActivity) context;
        HideNaviTitle(activity.getWindow().getDecorView());
    }

    /*隐藏标题栏导航栏*/
    private void HideNaviTitle(View decorView) {
        Logs.v(decorView + "  11111111111111111111");
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            decorView.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                    | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
            Logs.i(decorView + "  2222222222222222");
        }
    }

    /*生成二维码的bitmap*/
    private Bitmap getBitamap(String str) {
        Bitmap qrCodeBitmap = CreateCodeUtil.createQRCode(str, DPUtil.dip2px(context, 666),
                DPUtil.dip2px(context, 666), BitmapFactory.decodeResource(context.getResources(), R.drawable.bs_login));
        return qrCodeBitmap;
    }

    /*更新二维码*/
    public void updateCode(String code) {
        if (codeIv != null) {
            codeIv.setImageBitmap(getBitamap(code));
        } else {
            codeIv.setVisibility(View.GONE);
        }
//        codeIv.invalidate();
        if (!dialog.isShowing() && dialog != null) {
            dialog.show();
        }
    }

    /*更新二维码下面的文字*/
    public void updateTv(String tv) {
        codeTv.setText(tv);
//        codeTv.invalidate();
        if (!dialog.isShowing() && dialog != null) {
            dialog.show();
        }
    }

    public void closeDia() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }


    public void disDia() {
        dialog.dismiss();
    }


}
