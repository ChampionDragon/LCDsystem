package com.smdt.androidapi.lcd;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.smdt.SmdtManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.smdt.androidapi.R;
import com.smdt.androidapi.adapter.WifiAdapter;
import com.smdt.androidapi.base.BaseActivity;
import com.smdt.androidapi.pickerview.TimePickerView;
import com.smdt.androidapi.pickerview.other.pickerViewUtil;
import com.smdt.androidapi.utils.Constant;
import com.smdt.androidapi.utils.DialogCustomUtil;
import com.smdt.androidapi.utils.DialogNotileUtil;
import com.smdt.androidapi.utils.Logs;
import com.smdt.androidapi.utils.SpUtil;
import com.smdt.androidapi.utils.TimeUtil;
import com.smdt.androidapi.utils.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SetActivity extends BaseActivity implements View.OnClickListener {
    //brightness,bright,tvBright,
    private SeekBar volumne;
    private TextView tvVol, systemTime;
    private LinearLayout llVol;
    private SmdtManager smdt;
    private int vol;
    private Switch Swivol, SwiWifi, SwiScan;
    String tag = "SetActivity";
    private String rootDir = Environment.getExternalStorageDirectory().toString() + File.separator;
    private WifiManager mWifiManager;
    private WifiInfo mWifiInfo;
    private ListView mlistView;
    // 扫描出的网络连接列表
    private List<ScanResult> mWifiList;
    // 网络连接列表
    private List<WifiConfiguration> mWifiConfiguration;
    private String ssid;//WIFI名
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        smdt = SmdtManager.create(getApplicationContext());
        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        mWifiInfo = mWifiManager.getConnectionInfo();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
        systemTime.setText(TimeUtil.getSystem(Constant.cformatminute));
    }

    private void initData() {
        /*亮度设置*/
//        bright = smdt.getScreenBrightness(getApplicationContext());
//        brightness.setProgress(bright);
//        brightness.setOnSeekBarChangeListener(brightnessLisetner);
//        tvBright.setText(bright + "");

        vol = smdt.smdtGetVolume(getApplicationContext());
        volumne.setProgress(vol);
        tvVol.setText(vol + "");

        Swivol.setChecked(true);
        Swivol.setOnCheckedChangeListener(volCheck);
        volumne.setOnSeekBarChangeListener(volListener);

        boolean wifiState = mWifiManager.isWifiEnabled();
        SwiWifi.setChecked(wifiState);
    }

    private void initView() {
//        brightness = (SeekBar) findViewById(R.id.seekbar_brightness);
        volumne = (SeekBar) findViewById(R.id.seekbar_volunme);
//        tvBright = (TextView) findViewById(R.id.tv_brightness);
        tvVol = (TextView) findViewById(R.id.tv_volunme);
        llVol = (LinearLayout) findViewById(R.id.ll_volunme);
        Swivol = (Switch) findViewById(R.id.switch_volunm);
        findViewById(R.id.back_set).setOnClickListener(this);
        systemTime = (TextView) findViewById(R.id.tvSystemTime);
        systemTime.setOnClickListener(this);
//        findViewById(R.id.btn_log).setOnClickListener(this);
        SwiWifi = (Switch) findViewById(R.id.switch_wifi);
        SwiWifi.setOnCheckedChangeListener(wifiCheck);
        SwiScan = (Switch) findViewById(R.id.switch_wifiScan);
        SwiScan.setOnCheckedChangeListener(ScanCheck);

        mlistView = (ListView) findViewById(R.id.wifi_list);
        mlistView.setOnItemClickListener(itemclick);
        findViewById(R.id.set_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_set:
                finish();
                break;
            case R.id.tvSystemTime:
                systemTime();
                break;
//            case R.id.btn_log:
//                smdt.smdtGetSystemLogcat(rootDir);
//                DialogNotileUtil.show(SetActivity.this, "LOG信息以保存在根目录：" + rootDir + "下的logcat.txt文件中。");
//                break;
        }
    }

    private void systemTime() {
        pickerViewUtil.alertTimerPicker(SetActivity.this, TimePickerView.Type.ALL, Constant.cformatsecond,
                "设置系统时间", 33, new pickerViewUtil.TimerPickerCallBack() {
                    @Override
                    public void onTimeSelect(String date) {
                        systemTime.setText(date);
                        setTime(date);
                    }
                });
    }

    private void setTime(String date) {
        long time = TimeUtil.time2long(date, Constant.cformatsecond);
        int year = Integer.parseInt(TimeUtil.long2time(time, "yyyy"));
        int month = Integer.parseInt(TimeUtil.long2time(time, "MM"));
        int day = Integer.parseInt(TimeUtil.long2time(time, "dd"));
        int hour = Integer.parseInt(TimeUtil.long2time(time, "HH"));
        int minute = Integer.parseInt(TimeUtil.long2time(time, "mm"));

        Logs.d(year + " " + month + " " + day + " " + hour + " " + minute);

        smdt.setTime(getApplicationContext(), year, month, day, hour, minute);
    }

    /*亮度seekBar监听*/
    private SeekBar.OnSeekBarChangeListener brightnessLisetner = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//            tvBright.setText(progress + "");
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            smdt.setBrightness(getContentResolver(), seekBar.getProgress());
            Logs.e(tag + 81 + "  目前进度" + seekBar.getProgress() + "  " +
                    TimeUtil.getSystem(Constant.formatPhoto) + "   " + smdt.getScreenBrightness(getApplication()));
        }
    };

    /*声音seekBar监听*/
    private SeekBar.OnSeekBarChangeListener volListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            vol = progress;
            if (progress == 0) {
                vol = 1;
            }
            tvVol.setText(vol + "");

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            smdt.smdtSetVolume(SetActivity.this, vol);
            Logs.d(tag + 106 + "  目前进度" + seekBar.getProgress() + "  " +
                    TimeUtil.getSystem(Constant.formatPhoto) + "   " + smdt.smdtGetVolume(getApplication()));
        }
    };

    /*声音Switch监听*/
    CompoundButton.OnCheckedChangeListener volCheck = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                llVol.setVisibility(View.VISIBLE);
                smdt.setVolumeStates(3);
            } else {
                llVol.setVisibility(View.GONE);
                smdt.setVolumeStates(2);
            }
        }
    };

    /*WIFI Switch监听*/
    CompoundButton.OnCheckedChangeListener wifiCheck = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                mWifiManager.setWifiEnabled(true);
            } else {
                mWifiManager.setWifiEnabled(false);
            }
        }
    };

    /*WIFI扫描的Switch监听*/
    CompoundButton.OnCheckedChangeListener ScanCheck = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                mlistView.setVisibility(View.VISIBLE);
                Scan();
                if (mWifiList != null) {
                    mlistView.setAdapter(new WifiAdapter(mWifiList, SetActivity.this));
                }
            } else {
                mlistView.setVisibility(View.INVISIBLE);
            }
        }
    };


    /*++++++++++++++++++++++++++++++++++++++++   WIFI部分  +++++++++++++++++++++++++++++++++++++++++++++++++++*/
//扫描WIFI
    private void Scan() {
        mWifiManager.startScan();
        // 得到扫描结果
        List<ScanResult> results = mWifiManager.getScanResults();
        // 得到配置好的网络连接
        mWifiConfiguration = mWifiManager.getConfiguredNetworks();
        if (results == null) {
            if (mWifiManager.getWifiState() == 3) {
                ToastUtil.showShort("当前区域没有无线网络");
            } else if (mWifiManager.getWifiState() == 2) {
                ToastUtil.showShort("wifi正在开启，请稍后扫描");
            } else {
                ToastUtil.showShort("WiFi没有开启");
            }
        } else {
            mWifiList = new ArrayList<>();
            for (ScanResult result : results) {
                if (result.SSID == null || result.SSID.length() == 0
                        || result.capabilities.contains("[IBSS]")) {
                    continue;
                }
                boolean found = false;
                // Log.i("lcb", "result= " + result.SSID + " capabilities= "
                // + result.capabilities+" admin153");

                // 如果扫描到重复的ssid就终止添加到扫描出的网络连接列表mWifiList
                for (ScanResult item : mWifiList) {
                    if (item.SSID.equals(result.SSID)
                            && item.capabilities.equals(result.capabilities)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    mWifiList.add(result);
                }
            }
        }
    }

    /*wifi扫描列表点击监听*/
    AdapterView.OnItemClickListener itemclick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            AlertDialog.Builder alert = new AlertDialog.Builder(SetActivity.this);
            ssid = mWifiList.get(position).SSID;
            alert.setTitle(ssid);
            alert.setMessage("输入密码");
            final EditText et_password = new EditText(SetActivity.this);
            et_password.setBackgroundResource(R.drawable.wifi_bg);
            // final SharedPreferences preferences = getSharedPreferences(
            // "wifi_password", Context.MODE_PRIVATE);
            // et_password.setText(preferences.getString(ssid, ""));
            // 保留上次ssid的密码
            final SpUtil sp = SpUtil.getInstance("wifi_password",
                    Context.MODE_PRIVATE);
            et_password.setText(sp.getString(ssid));

            alert.setView(et_password);
            alert.setPositiveButton("连接",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            String pw = et_password.getText().toString();
                            if (TextUtils.isEmpty(pw)) {
                                Toast.makeText(SetActivity.this,
                                        "密码不能为空", Toast.LENGTH_SHORT)
                                        .show();
                                return;
                            }
                            // Editor editor = preferences.edit();
                            // editor.putString(ssid, pw);
                            // editor.commit();
                            sp.putString(ssid, pw);
                            addNetwork(CreateWifiInfo(ssid, et_password
                                    .getText().toString(), 3));
                            checkWifiConfiguration(ssid);
                        }
                    });
            alert.setNegativeButton("取消",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            // mWifiAdmin.removeWifi(mWifiAdmin.getNetworkId());
                        }
                    });
            alert.create();
            alert.show();
        }
    };

    // 添加一个网络并连接
    public void addNetwork(final WifiConfiguration wcg) {
        // 返回值为-1证明List<WifiConfiguration>未添加,其它数字源码里面mService.addOrUpdateNetwork(config)
        // 的一个值不是WifiConfiguration.networkId值
        int wcgID = mWifiManager.addNetwork(wcg);
        // b返回true只是让WifiManager去执行连接的命令，不代表连接成功了
        boolean b = mWifiManager.enableNetwork(wcgID, true);
        // 第二个参数true表示如果当前已经有连上一个wifi,要强制连到自己设定的wifi上，此参数必须为true否则连上的还是原来的wifi.
        ToastUtil.showLong("正在连接，请稍等");
        Logs.d(tag + "228      状态：" + wcg.status);
        Logs.v(tag + "229    " + wcgID);
    }

    // 创建wifi热点的。
    public WifiConfiguration CreateWifiInfo(String SSID, String Password,
                                            int Type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";

        // WifiConfiguration tempConfig = IsExsits(SSID);
        WifiConfiguration tempConfig = null;
        WifiConfiguration isExsits = IsExsits(SSID);
        if (isExsits != null) {
            tempConfig = isExsits;
            mWifiManager.removeNetwork(tempConfig.networkId);// 移除之前已连接过的热点的id,如果不移除
            // 在Android 6.0 中WiFiManager addNetwork(WifiConfiguration
            // config)，添加同一ssid时会返回-1，
            // 这个时候你再将这个-1 （NetWorkId）传进enableNetwork（-1，true），肯定连不上WiFi。
        }

        // if (tempConfig != null) {
        // mWifiManager.removeNetwork(tempConfig.networkId);//移除指定热点
        // }

        if (Type == 1) // 没有密码
        {
            config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (Type == 2) // 用wep加密
        {
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + Password + "\"";
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (Type == 3)// 用wpa加密
        {
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }


    /**
     * 判断指定ssid是否存在
     *
     * @param SSID 接入点的名字
     * @return 返回指定ssid的WifiConfiguration:类主要提供一个wifi配置的所有信息
     */
    // 如果手机在未开启wifi的情况下，mWifiManager.getConfiguredNetworks()返回的是空。
    public WifiConfiguration IsExsits(String SSID) {
        if (mWifiManager.getConfiguredNetworks() != null) {
            List<WifiConfiguration> existingConfigs = mWifiManager
                    .getConfiguredNetworks();// 得到所有配置好的网络连接
            for (WifiConfiguration existingConfig : existingConfigs) {
                Logs.d(tag + "437  " + existingConfig.SSID + "  " + SSID + "  " + existingConfigs.size());
                // log是判断List<WifiConfiguration>有几个
                if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                    return existingConfig;
                }
            }
        } else {
            dialog = DialogCustomUtil.create("警告", "亲，你确定在不开WIFI的情况下能连到网络？",
                    this, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
            dialog.show();
        }
        return null;
    }


    /**
     * 判断连接的ssid是否连上网
     */
    private void checkWifiConfiguration(final String SSID) {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if (!networkInfo.isConnected()) {
                    DialogNotileUtil.show(SetActivity.this, "密码错误，请重新再尝试");
                } else {
                    ToastUtil.showLong("连接成功");
                }
            }
        }, 13888);
    }
}
