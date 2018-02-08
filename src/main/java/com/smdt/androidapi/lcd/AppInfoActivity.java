package com.smdt.androidapi.lcd;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.smdt.androidapi.R;
import com.smdt.androidapi.base.BaseActivity;
import com.smdt.androidapi.adapter.InfoAdapter;
import com.smdt.androidapi.utils.InfoBean;
import com.smdt.androidapi.utils.SystemUtil;

/**
 * Description: APP的基本信息类
 * AUTHOR: Champion Dragon
 * created at 2017/11/8
 **/

public class AppInfoActivity extends BaseActivity {
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_infor);
        findViewById(R.id.back_appinfo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initData();
    }

    /**
     * 初始化列表数据
     */
    private void initData() {
        InfoAdapter adapter = new InfoAdapter(this, R.layout.item_info);
        lv = (ListView) findViewById(R.id.appinfo_lv);
//        adapter.add(new InfoBean("app的签名", SystemUtil.AppSignature()));
        adapter.add(new InfoBean("app的名称", SystemUtil.AppName()));
        adapter.add(new InfoBean("app的版本号", SystemUtil.VersionCode() + ""));
        adapter.add(new InfoBean("app的版本号名", SystemUtil.VersionName()));
        adapter.add(new InfoBean("app的包名", SystemUtil.PackgeName()));
        adapter.add(new InfoBean("设备的IMEI号", SystemUtil.IMEI()));
//        adapter.add(new InfoBean("手机的IMSI", SystemUtil.IMSI()));
//        adapter.add(new InfoBean("手机的号码", SystemUtil.Num()));
        adapter.add(new InfoBean("设备的序列号", SystemUtil.SN()));
//        adapter.add(new InfoBean("手机的sim号", SystemUtil.SIM()));
        adapter.add(new InfoBean("设备的ID", SystemUtil.ID()));
        adapter.add(new InfoBean("设备的mac地址", SystemUtil.MAC()));
        adapter.add(new InfoBean("系统国家", SystemUtil.Country()));
        adapter.add(new InfoBean("系统语言", SystemUtil.Language()));
        adapter.add(new InfoBean("屏幕的高", SystemUtil.Height() + ""));
        adapter.add(new InfoBean("屏幕的宽", SystemUtil.Width() + ""));
        adapter.add(new InfoBean("屏幕的密度", SystemUtil.densityDpi() + ""));
        adapter.add(new InfoBean("屏幕的密度比", SystemUtil.Density() + ""));
        adapter.add(new InfoBean("系统版本名", Build.VERSION.RELEASE));
        adapter.add(new InfoBean("系统版本号", Build.VERSION.SDK_INT + ""));
        adapter.add(new InfoBean("系统型号", Build.MODEL));
        adapter.add(new InfoBean("系统定制商", Build.BRAND));
        adapter.add(new InfoBean("系统的主板", Build.BOARD));
        adapter.add(new InfoBean("手机制造商", Build.PRODUCT));
        adapter.add(new InfoBean("系统2", Build.HOST));
        adapter.add(new InfoBean("系统3", Build.TIME + "    " + System.currentTimeMillis()));
        adapter.add(new InfoBean("系统4", Build.USER));
        adapter.add(new InfoBean("系统硬件执照商", Build.MANUFACTURER));
        adapter.add(new InfoBean("builder类型", Build.MANUFACTURER));
        lv.setAdapter(adapter);
    }


}