package com.smdt.androidapi.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.smdt.androidapi.R;
import com.smdt.androidapi.utils.CreateCodeUtil;
import com.smdt.androidapi.utils.DPUtil;
import com.smdt.androidapi.utils.Logs;

/**
 * Description:二维码的碎片类
 * AUTHOR: Champion Dragon
 * created at 2018/11/20
 **/

@SuppressLint("ValidFragment")
public class CodeFragment extends Fragment {
    private String tag = "CodeFragment";
    private View view;
    private TextView codeTv;
    private ImageView codeIv;
    private String tvstr, ivstr;

    public CodeFragment() {
    }

    public CodeFragment(String iv, String tv) {
        tvstr= tv;
        ivstr= iv;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragmentcode, container, false);
        codeIv = (ImageView) view.findViewById(R.id.fragcodeiv);
        codeTv = (TextView) view.findViewById(R.id.fragcodetv);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Logs.i(tag + "  onResume"+"  "+codeIv+"  "+codeTv);
        codeTv.setText(tvstr);
        codeIv.setImageBitmap(getBitamap(ivstr));
    }

    /*生成二维码的bitmap*/
    private Bitmap getBitamap(String str) {
        Bitmap qrCodeBitmap = CreateCodeUtil.createQRCode(str, DPUtil.dip2px(getActivity(), 666),
                DPUtil.dip2px(getActivity(), 666), BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.bs_login));
        return qrCodeBitmap;
    }

    public void updateData(String tv, String iv) {
        tvstr= tv;
        ivstr= iv;
        onResume();
    }

}
