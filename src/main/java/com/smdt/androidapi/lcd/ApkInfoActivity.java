package com.smdt.androidapi.lcd;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.smdt.androidapi.R;
import com.smdt.androidapi.adapter.InfoAdapter;
import com.smdt.androidapi.base.BaseActivity;
import com.smdt.androidapi.utils.ApkUtil;
import com.smdt.androidapi.utils.Constant;
import com.smdt.androidapi.utils.InfoBean;
import com.smdt.androidapi.utils.ToastUtil;

import java.io.File;

public class ApkInfoActivity extends BaseActivity {
    private ListView lv;
    private TextView tv;
    private String name, url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apk_info);

        findViewById(R.id.back_apkinfo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.apkinfo_update).setOnClickListener(buttonlisten);

        tv = (TextView) findViewById(R.id.apkinfo_tv);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            name = bundle.getString(ApkActivity.apkName);
            url = bundle.getString(ApkActivity.apkURL);
            tv.setText(name);
            initData();
        }
    }

    /*初始化数据*/
    private void initData() {
        InfoAdapter adapter = new InfoAdapter(this, R.layout.item_info);
        lv = (ListView) findViewById(R.id.apkinfo_lv);
        adapter.add(new InfoBean("apk名字", ApkUtil.ApkName(this, url)));
//        adapter.add(new InfoBean("apk签名", ApkUtil.ApkSignature(this, url)));
//        adapter.add(new InfoBean("apk权限", Arrays.toString(ApkUtil.ApkPremission(this, url))));
        adapter.add(new InfoBean("apk版本名", ApkUtil.VersionName(this, url)));
        adapter.add(new InfoBean("apk版本号", "" + ApkUtil.VersionCode(this, url)));
        adapter.add(new InfoBean("apk包名", "" + ApkUtil.PackgeName(this, url)));
        lv.setAdapter(adapter);
    }

    /*更新APK按钮监听*/
    View.OnClickListener buttonlisten = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            UpdateApk(name);
        }
    };


    private void UpdateApk(String params3) {
        File file = new File(Constant.fileLS.getAbsolutePath(), params3);
        if (file.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            startActivity(intent);
        } else {
            ToastUtil.showLong("\"" + params3 + "\"" + "文件不存在");
        }
    }
}
