package com.smdt.androidapi.lcd;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.smdt.androidapi.R;
import com.smdt.androidapi.adapter.InfoAdapter;
import com.smdt.androidapi.base.BaseActivity;
import com.smdt.androidapi.utils.InfoBean;

public class SDKInfoActivity extends BaseActivity {
    ListView lv;
    TextView tv;

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
        tv = (TextView) findViewById(R.id.appinfo_tv);
        tv.setText("查看SDK提供的信息");
        initData();

    }

    /*初始化SDK数据*/
    private void initData() {
        InfoAdapter adapter = new InfoAdapter(this, R.layout.item_info);
        lv = (ListView) findViewById(R.id.appinfo_lv);

        adapter.add(new InfoBean("API的平台-版本-日期信息", smdt.smdtGetAPIVersion()));
        adapter.add(new InfoBean("设备的型号", smdt.getAndroidModel()));
        adapter.add(new InfoBean("安卓系统版本", smdt.getAndroidVersion()));
        adapter.add(new InfoBean("运行内存", smdt.getRunningMemory()));
        adapter.add(new InfoBean("内部存储", smdt.getInternalStorageMemory ()));
        adapter.add(new InfoBean("设备固件的SDK版本", smdt.getFirmwareVersion()));
        adapter.add(new InfoBean("设备固件内核版本", smdt.getFormattedKernelVersion()));
        adapter.add(new InfoBean("设备固件系统版本和编译日期", smdt.getAndroidDisplay()));
        adapter.add(new InfoBean("当前系统音量大小", smdt.smdtGetVolume(getApplicationContext()) + ""));
        adapter.add(new InfoBean("显示屏分辨率宽X像素", smdt.smdtGetScreenWidth(getApplicationContext()) + ""));
        adapter.add(new InfoBean("显示屏分辨率高Y像素", smdt.smdtGetScreenHeight(getApplicationContext()) + ""));
        adapter.add(new InfoBean("设备以太网的MAC地址", smdt.smdtGetEthMacAddress()));
        adapter.add(new InfoBean("设备以太网的IP地址", smdt.smdtGetEthIPAddress()));
        adapter.add(new InfoBean("当前网络连接的类型", smdt.getCurrentNetType()));
        String state = "";
        if (smdt.smdtGetEthernetState()) {
            state = "开";
        } else {
            state = "关";
        }
        adapter.add(new InfoBean("设备以太网的开启状态", state));
        adapter.add(new InfoBean("获取外部存储SD卡路径", smdt.smdtGetSDcardPath(getApplicationContext())));
        adapter.add(new InfoBean("获取外部存储U盘路径", smdt.smdtGetUSBPath(getApplicationContext(), 0)));


        lv.setAdapter(adapter);
    }
}
