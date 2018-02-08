package com.smdt.androidapi.lcd;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.smdt.androidapi.R;
import com.smdt.androidapi.adapter.InfoAdapter;
import com.smdt.androidapi.base.BaseActivity;
import com.smdt.androidapi.utils.InfoBean;
import com.smdt.androidapi.utils.NetConnectUtil;
import com.smdt.androidapi.utils.SmallUtil;

public class WifiInfo extends BaseActivity {
    private ListView lv;
    private TextView tv;
    private WifiManager mWifiManager;
    private android.net.wifi.WifiInfo mWifiInfo;

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
        tv.setText("查看网络信息");
        if (NetConnectUtil.NetConnect(this)) {
            initData();
        }else {
            tv.setText("未连接到网络");
        }
    }

    private void initData() {
        InfoAdapter adapter = new InfoAdapter(this, R.layout.item_info);
        lv = (ListView) findViewById(R.id.appinfo_lv);

        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        mWifiInfo = mWifiManager.getConnectionInfo();


        adapter.add(new InfoBean("热点名", mWifiInfo.getSSID()));
        adapter.add(new InfoBean("设备本身网卡的MAC地址", mWifiInfo.getMacAddress()));
        adapter.add(new InfoBean("连接的速度", mWifiInfo.getLinkSpeed() + ""));
        adapter.add(new InfoBean("SSID是否被隐藏", mWifiInfo.getHiddenSSID() + ""));
        adapter.add(new InfoBean("所连接的WIFI设备的MAC地址", mWifiInfo.getBSSID()));
        adapter.add(new InfoBean("网络号", mWifiInfo.getNetworkId() + ""));
        adapter.add(new InfoBean("网络信号强度", mWifiInfo.getRssi() + ""));
        adapter.add(new InfoBean("获取具体客户端状态的信息", mWifiInfo.getSupplicantState() + ""));

        // 得到DhcpInfo类的一些信息DHCP（Dynamic Host Configuration
        // Protocol，动态主机配置协议）是一个局域网的网络协议
        DhcpInfo dhcpInfo = mWifiManager.getDhcpInfo();
        String DhcpStr = dhcpInfo.toString();
        int dns1 = mWifiManager.getDhcpInfo().dns1;
        int dns2 = mWifiManager.getDhcpInfo().dns2;
        int leaseDuration = dhcpInfo.leaseDuration;
        int gateway = dhcpInfo.gateway;
        int ipAddresse = dhcpInfo.ipAddress;
        int describeContents = dhcpInfo.describeContents();
        int netmask = dhcpInfo.netmask;
        int serverAddress = dhcpInfo.serverAddress;

        adapter.add(new InfoBean("dns1地址", SmallUtil.intToString(dns1)));
        adapter.add(new InfoBean("dns2地址", SmallUtil.intToString(dns2)));
        adapter.add(new InfoBean("网关", SmallUtil.intToString(gateway)));
        adapter.add(new InfoBean("ip地址", SmallUtil.intToString(ipAddresse)));
        adapter.add(new InfoBean("子网掩码", SmallUtil.intToString(netmask)));
        adapter.add(new InfoBean("服务端地址", SmallUtil.intToString(serverAddress)));
        adapter.add(new InfoBean("连接速度", leaseDuration + ""));

        lv.setAdapter(adapter);
    }
}
