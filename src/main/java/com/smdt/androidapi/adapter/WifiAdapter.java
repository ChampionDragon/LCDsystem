package com.smdt.androidapi.adapter;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.smdt.androidapi.R;

import java.util.List;

/**
 * Description:扫描wifi的适配器
 * AUTHOR: Champion Dragon
 * created at 2017/11/16
 **/

public class WifiAdapter extends BaseAdapter{
    List<ScanResult> list;
    private Context context;
    public int level;

    public WifiAdapter(List<ScanResult> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.wifi_listitem, null);
        }
        ScanResult scanResult = list.get(position);
        TextView wifi_ssid = ViewHolderUtil.get(convertView, R.id.ssid);
        ImageView wifi_level =ViewHolderUtil.get(convertView,R.id.wifi_level);

        wifi_ssid.setText(scanResult.SSID);
        // Log.i(TAG, "scanResult.SSID=" + scanResult);//遍历所有热点数据
        // 设置接收wifi的等级信号
        level = WifiManager.calculateSignalLevel(scanResult.level, 5);
        if (scanResult.capabilities.contains("WEP")
                || scanResult.capabilities.contains("PSK")
                || scanResult.capabilities.contains("EAP")) {
            wifi_level.setImageResource(R.drawable.wifi_signal_lock);
        } else {
            wifi_level.setImageResource(R.drawable.wifi_signal_open);
        }
        wifi_level.setImageLevel(level);
        // 判断信号强度，显示对应的指示图标  

        return convertView;
    }
}
