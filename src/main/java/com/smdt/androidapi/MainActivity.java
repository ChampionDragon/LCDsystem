package com.smdt.androidapi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.app.smdt.SmdtManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.smdt.androidapi.utils.Logs;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends Activity implements OnClickListener {

    private SmdtManager smdt;
    private AudioManager mAudioManager;
    String tag = "MainActivity";
    private Button button_close;
    private Button btn_gpio;
    private Button btn_devices;
    private Button btn_uart;
    private Button btn_io_closeback;
    private Button button_reboot;
    private Button btn_update, btn_wifi;
    private Button button_get_nettype;
    private Button btn_normal;
    private Button button_settimebynetoff;
    private Button button_takePictrue;
    private Button btn_watchdog;
    private Button btn_closedog;
    private Button btn_feeddog;
    private Button button_settime;
    private Button btn_sd;
    private Button btn_USB;
    private Button btn_openUsb;
    private Button btn_closeUsb;
    private Button btn_getEth;
    private Button button_settimebyNet;
    private Button btn_volumeadd;
    private Button btn_volumedec;
    private Button btn_mute;
    private Button button_silentInstall;
    private Button btn_openback;
    private Button btn_closeback;
    private Button btn_getback;
    private Button btn_set_power_off_on_time;
    private Button btn_showstatus;
    private Button btn_hidestatus;
    private Button btn_one;
    private Button btn_two;
    private Button btn_three;
    private Button btn_four;
    private Button btn_setVolume;
    private Button btn_getVolume;
    private Button btn_getBright;
    private Button btn_openlog;
    private Button btn_closelog;
    private Button btn_getMac;
    private Button btn_setIP;
    private Button btn_getIP;
    private EditText et_year;
    private EditText et_month;
    private EditText et_day;
    private EditText et_hour;
    private EditText et_minute;
    private EditText edit_hour;
    private EditText edit_minute;
    private EditText edit_values;
    private EditText edit_setBright;
    private EditText edit_enable;
    private TextView editNetType;
    private TextView textView;
    private TextView txt_volume_show;
    private TextView tv_bright;
    private TextView tv_Mac;
    private TextView tv_IP;
    private EditText tv_setIP;
    private TextView tv_usb;
    private TextView tv_sd;
    private TextView tv_eth;
    private Spinner spin_dev;
    private EditText tv_usbNum;
    private Button btn_openEthPower;
    private Button btn_closeEthPower;

    private Button btn_screenWidth;
    private Button btn_screenHeight;
    private TextView tv_screenWidth;
    private TextView tv_screenHeight;

    private TextView tv_read_eeprom;
    private EditText ed_usb_num;
    private EditText et_eeprom_data;
    private EditText et_deviceId;
    private EditText et_areaId;
    private EditText et_start_addr;
    private EditText et_size;
    private Button btn_write_eeprom;
    private Button btn_read_eeprom;

    private TextView tv_api_version;
    private Button btn_get_api_version;

    //3G
    private Button btn_3G_open;
    private Button btn_3G_close;
    private Button btn_3G_reset;

    //led
    private Button btn_led_open;
    private Button btn_led_close;

    //mic
    private Button btn_headsetmic_open;
    private Button btn_headsetmic_close;

    private Button btn_three_poweronoff;

    //unmount
    private Button btn_unmount;
    private EditText et_unmount_path;

    //screen_number
    private Button btn_get_screen_number;
    private TextView tv_screen_number;

    //hdmi in
    private Button btn_get_hdmiin_status;


    private static final int ADJUST_MUTE = -100;
    private static final int ADJUST_UNMUTE = 100;
    private static final int ADJUST_LOWER = -1;
    private static final int ADJUST_RAISE = 1;
    private static final int ADJUST_SAME = 0;
    private static final int FLAG_SHOW_UI = 1 << 0;
    private static final int ADJUST_TOGGLE_MUTE = 101;

    /**
     * The audio stream for phone calls
     */
    private static final int STREAM_VOICE_CALL = 0;
    /**
     * The audio stream for system sounds
     */
    private static final int STREAM_SYSTEM = 1;
    /**
     * The audio stream for the phone ring
     */
    private static final int STREAM_RING = 2;
    /**
     * The audio stream for music playback
     */
    private static final int STREAM_MUSIC = 3;
    /**
     * The audio stream for alarms
     */
    private static final int STREAM_ALARM = 4;
    /**
     * The audio stream for notifications
     */
    private static final int STREAM_NOTIFICATION = 5;
    /**
     * @hide The audio stream for phone calls when connected to bluetooth
     */
    private static final int STREAM_BLUETOOTH_SCO = 6;
    /**
     * @hide The audio stream for enforced system sounds in certain countries (e.g camera in Japan)
     */
    private static final int STREAM_SYSTEM_ENFORCED = 7;
    /**
     * The audio stream for DTMF Tones
     */
    private static final int STREAM_DTMF = 8;
    /**
     * @hide The audio stream for text to speech (TTS)
     */
    private static final int STREAM_TTS = 9;


    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 0x123) {
                Toast.makeText(MainActivity.this, "进入截图功能", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        smdt = SmdtManager.create(this);
        mAudioManager = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
        button_close = (Button) findViewById(R.id.button_close);
        button_reboot = (Button) findViewById(R.id.button_reboot);
        button_get_nettype = (Button) findViewById(R.id.button_get_nettype);
        button_takePictrue = (Button) findViewById(R.id.button_takepictrue);
        button_settime = (Button) findViewById(R.id.button_settime);
        button_settimebyNet = (Button) findViewById(R.id.button_settimebynet);
        button_settimebynetoff = (Button) findViewById(R.id.button_settimebynetoff);
        button_silentInstall = (Button) findViewById(R.id.button_silentInstall);
        editNetType = (TextView) findViewById(R.id.edit_get_net_type);
        textView = (TextView) findViewById(R.id.textView);
        tv_Mac = (TextView) findViewById(R.id.tv_showMac);
        tv_IP = (TextView) findViewById(R.id.tv_showIP);
        tv_setIP = (EditText) findViewById(R.id.tv_setIP);
        txt_volume_show = (TextView) findViewById(R.id.txt_volume_show);
        tv_sd = (TextView) findViewById(R.id.tv_sd);
        tv_usb = (TextView) findViewById(R.id.tv_USB);
        tv_bright = (TextView) findViewById(R.id.tv_bright);
        tv_eth = (TextView) findViewById(R.id.tv_eth);
        btn_set_power_off_on_time = (Button) findViewById(R.id.set_power_off_on_time);
        et_year = (EditText) findViewById(R.id.et_year);
        et_month = (EditText) findViewById(R.id.et_month);
        et_day = (EditText) findViewById(R.id.et_day);
        et_hour = (EditText) findViewById(R.id.et_hour);
        et_minute = (EditText) findViewById(R.id.et_minute);
        edit_hour = (EditText) findViewById(R.id.edit_hour);
        edit_minute = (EditText) findViewById(R.id.edit_minute);
        edit_values = (EditText) findViewById(R.id.edit_values);
        edit_setBright = (EditText) findViewById(R.id.edit_setBright);
        edit_enable = (EditText) findViewById(R.id.edit_enable);
        tv_usbNum = (EditText) findViewById(R.id.tv_usbNum);
        btn_gpio = (Button) findViewById(R.id.btn_gpio);
        btn_devices = (Button) findViewById(R.id.btn_devices);
        btn_uart = (Button) findViewById(R.id.btn_uart);
        btn_update = (Button) findViewById(R.id.btn_update);
        btn_wifi = (Button) findViewById(R.id.btn_wifi);
        btn_showstatus = (Button) findViewById(R.id.btn_showstatus);
        btn_hidestatus = (Button) findViewById(R.id.btn_hidestatus);
        btn_openback = (Button) findViewById(R.id.btn_openback);
        btn_closeback = (Button) findViewById(R.id.btn_closeback);
        btn_io_closeback = (Button) findViewById(R.id.btn_bright);
        btn_volumeadd = (Button) findViewById(R.id.btn_volumeadd);
        btn_volumedec = (Button) findViewById(R.id.btn_volumedec);
        btn_mute = (Button) findViewById(R.id.btn_mute);
        btn_normal = (Button) findViewById(R.id.btn_normal);
        btn_one = (Button) findViewById(R.id.btn_one);
        btn_two = (Button) findViewById(R.id.btn_two);
        btn_three = (Button) findViewById(R.id.btn_three);
        btn_four = (Button) findViewById(R.id.btn_four);
        btn_setVolume = (Button) findViewById(R.id.btn_setVolume);
        btn_getVolume = (Button) findViewById(R.id.btn_getVolume);
        btn_getBright = (Button) findViewById(R.id.btn_getBright);
        btn_openlog = (Button) findViewById(R.id.btn_openlog);
        btn_closelog = (Button) findViewById(R.id.btn_closelog);
        btn_getMac = (Button) findViewById(R.id.btn_getmac);
        btn_setIP = (Button) findViewById(R.id.btn_setIP);
        btn_getIP = (Button) findViewById(R.id.btn_getIP);
        btn_USB = (Button) findViewById(R.id.btn_USB);
        ed_usb_num = (EditText) findViewById(R.id.edit_usb_num);
        btn_sd = (Button) findViewById(R.id.btn_sd);
        btn_watchdog = (Button) findViewById(R.id.btn_watchdog);
        btn_closedog = (Button) findViewById(R.id.btn_closedog);
        btn_feeddog = (Button) findViewById(R.id.btn_feeddog);
        btn_openUsb = (Button) findViewById(R.id.btn_openUSB);
        btn_closeUsb = (Button) findViewById(R.id.btn_closeUSB);
        btn_getEth = (Button) findViewById(R.id.btn_getEth);
        btn_getback = (Button) findViewById(R.id.btn_getback);
        btn_openEthPower = (Button) findViewById(R.id.btn_openEthPower);
        btn_closeEthPower = (Button) findViewById(R.id.btn_closeEthPower);
        tv_screenWidth = (TextView) findViewById(R.id.tv_screenWidth);
        tv_screenHeight = (TextView) findViewById(R.id.tv_screenHeight);
        btn_screenHeight = (Button) findViewById(R.id.btn_screenHeight);
        btn_screenWidth = (Button) findViewById(R.id.btn_screenWidth);
        //eeprom
        btn_write_eeprom = (Button) findViewById(R.id.btn_write_eeprom);
        btn_read_eeprom = (Button) findViewById(R.id.btn_read_eeprom);
        tv_read_eeprom = (TextView) findViewById(R.id.tv_read_eeprom);
        et_eeprom_data = (EditText) findViewById(R.id.et_eeprom_data);
        et_deviceId = (EditText) findViewById(R.id.et_deviceId);
        et_areaId = (EditText) findViewById(R.id.et_areaId);
        et_start_addr = (EditText) findViewById(R.id.et_start_addr);
        et_size = (EditText) findViewById(R.id.et_size);
        //api version
        tv_api_version = (TextView) findViewById(R.id.tv_api_version);
        btn_get_api_version = (Button) findViewById(R.id.btn_get_api_version);
        //3G
        btn_3G_open = (Button) findViewById(R.id.btn_3G_open);
        btn_3G_close = (Button) findViewById(R.id.btn_3G_close);
        btn_3G_reset = (Button) findViewById(R.id.btn_3G_reset);
        //led
        btn_led_open = (Button) findViewById(R.id.btn_led_open);
        btn_led_close = (Button) findViewById(R.id.btn_led_close);
        btn_three_poweronoff = (Button) findViewById(R.id.button_three_poweronoff);

        //mic
        btn_headsetmic_open = (Button) findViewById(R.id.btn_headsetmic_open);
        btn_headsetmic_close = (Button) findViewById(R.id.btn_headsetmic_close);

        //unmount
        btn_unmount = (Button) findViewById(R.id.btn_unmount);
        et_unmount_path = (EditText) findViewById(R.id.et_unmount_path);

        //screen_number
        btn_get_screen_number = (Button) findViewById(R.id.btn_get_screen_number);
        tv_screen_number = (TextView) findViewById(R.id.tv_screen_number1);

        //hdmi in
        btn_get_hdmiin_status = (Button) findViewById(R.id.btn_get_hdmiin_status);

        btn_watchdog.setOnClickListener(this);
        btn_closedog.setOnClickListener(this);
        btn_feeddog.setOnClickListener(this);
        btn_USB.setOnClickListener(this);
        btn_sd.setOnClickListener(this);
        button_close.setOnClickListener(this);
        button_reboot.setOnClickListener(this);
        button_get_nettype.setOnClickListener(this);
        button_takePictrue.setOnClickListener(this);
        button_settime.setOnClickListener(this);
        button_settimebyNet.setOnClickListener(this);
        button_settimebynetoff.setOnClickListener(this);
        button_silentInstall.setOnClickListener(this);
        btn_set_power_off_on_time.setOnClickListener(this);
        btn_gpio.setOnClickListener(this);
        btn_devices.setOnClickListener(this);
        btn_uart.setOnClickListener(this);
        btn_update.setOnClickListener(this);
        btn_showstatus.setOnClickListener(this);
        btn_hidestatus.setOnClickListener(this);
        btn_openback.setOnClickListener(this);
        btn_closeback.setOnClickListener(this);
        btn_volumeadd.setOnClickListener(this);
        btn_volumedec.setOnClickListener(this);
        btn_mute.setOnClickListener(this);
        btn_normal.setOnClickListener(this);
        btn_wifi.setOnClickListener(this);
        btn_one.setOnClickListener(this);
        btn_two.setOnClickListener(this);
        btn_three.setOnClickListener(this);
        btn_four.setOnClickListener(this);
        btn_io_closeback.setOnClickListener(this);
        btn_setVolume.setOnClickListener(this);
        btn_getVolume.setOnClickListener(this);
        btn_getBright.setOnClickListener(this);
        btn_openlog.setOnClickListener(this);
        btn_closelog.setOnClickListener(this);
        btn_getMac.setOnClickListener(this);
        btn_setIP.setOnClickListener(this);
        btn_getIP.setOnClickListener(this);
        btn_openUsb.setOnClickListener(this);
        btn_closeUsb.setOnClickListener(this);
        btn_getEth.setOnClickListener(this);
        btn_getback.setOnClickListener(this);
        btn_openEthPower.setOnClickListener(this);
        btn_closeEthPower.setOnClickListener(this);
        btn_screenWidth.setOnClickListener(this);
        btn_screenHeight.setOnClickListener(this);
        btn_write_eeprom.setOnClickListener(this);
        btn_read_eeprom.setOnClickListener(this);
        btn_get_api_version.setOnClickListener(this);
        //3G
        btn_3G_open.setOnClickListener(this);
        btn_3G_close.setOnClickListener(this);
        btn_3G_reset.setOnClickListener(this);
        //led
        btn_led_open.setOnClickListener(this);
        btn_led_close.setOnClickListener(this);
        btn_three_poweronoff.setOnClickListener(this);

        //led
        btn_headsetmic_open.setOnClickListener(this);
        btn_headsetmic_close.setOnClickListener(this);

        //unmout
        btn_unmount.setOnClickListener(this);

        //screen_number
        btn_get_screen_number.setOnClickListener(this);


        //hdmi in
        btn_get_hdmiin_status.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_close:
                smdt.shutDown();
                break;
            case R.id.button_reboot:
                smdt.smdtReboot("reboot");
                break;
            case R.id.button_three_poweronoff:
                int i = smdt.smdtSetPowerOnOff((char) 0, (char) 1, (char) 0, (char) 1, (char) 3);
                Logs.d(tag + "414  " + i + " 间隔开关机");
                break;
            case R.id.set_power_off_on_time:
                String offTime = edit_hour.getText().toString();
                String onTime = edit_minute.getText().toString();
                String enable = edit_enable.getText().toString().trim();
                if (!(offTime.equals("") && onTime.equals("") && enable.equals(""))) {
                    smdt.smdtSetTimingSwitchMachine(offTime, onTime, enable);
                    Logs.d(tag + "422  " + offTime + " " + onTime + " " + enable + " 定时开关机");
                } else {
                    Toast.makeText(getApplicationContext(), "please input shutdown time", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.button_get_nettype:
                String netType = smdt.getCurrentNetType();
                editNetType.setText(netType);
                break;
            case R.id.button_silentInstall:
                Thread t = new Thread() {
                    @Override
                    public void run() {
                        String path = Environment.getExternalStorageDirectory().getPath().toString() + File.separator + "UpdateActivity.apk";
                        Log.d("lzl", "======button_silentInstall==========path:" + path);
                        if (!path.equals("")) {
                            smdt.smdtSilentInstall(path, getApplicationContext());
                            Message msg = new Message();
                            msg.what = 0x123;
                        } else {
                            Toast.makeText(getApplicationContext(), "please choose install apk", Toast.LENGTH_SHORT).show();
                        }
                    }
                };
                t.start();
                break;
            case R.id.button_takepictrue:
                Thread t2 = new Thread() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(MainActivity.this, ScreenActivity.class);
                        startActivity(intent);
                        Message msg = new Message();
                        msg.what = 0x123;
                        handler.sendEmptyMessage(msg.what);
                    }
                };
                t2.start();
                break;
            case R.id.btn_gpio:
                Intent intent = new Intent(this, GpioActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_openlog:
                smdt.smdtGetSystemLogcat("mnt/sdcard/");
                break;
            case R.id.btn_closelog:
                smdt.getLogcatInterface().stopLogcatManager();
                break;
            case R.id.btn_devices:
                Intent intent1 = new Intent(this, DevicesActivity.class);
                startActivity(intent1);
                break;
            case R.id.btn_uart:
                spin_dev = (Spinner) findViewById(R.id.spin_dev);
                spin_dev.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                        String str = arg0.getItemAtPosition(arg2).toString();
                        textView.setText(smdt.getUartPath(str));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                    }
                });
                break;
            case R.id.btn_update:
                /*
                String updateimg_path[] = {
	        			"/mnt/internal_sd/update.zip",
	        			"/mnt/external_sd/update.zip",
	        			"/mnt/usb_storage/USB_DISK1/udisk2/update.zip", 
	        			"/mnt/usb_storage/USB_DISK1/udisk0/update.zip",
	        			"/mnt/usb_storage/USB_DISK1/udisk1/update.zip",
	        			"/mnt/usb_storage/USB_DISK2/udisk0/update.zip", 
	        			"/mnt/usb_storage/USB_DISK0/udisk0/update.zip"
	        			};
	        	
	        	for(int i=0; i<updateimg_path.length; i++)
	        	{
	        		File file = new File(updateimg_path[i]);
	        		//Log.d("lzl", "============111==========sdPath:" + updateimg_path[i]);
	        		if(file.exists()){       	
							try {
								Log.d("lzl", "======================sdPath:" + updateimg_path[i]);
								smdt.smdtInstallPackage(getApplicationContext(), file);
							} catch (IOException e) {
								e.printStackTrace();
							}           			
	        		}else{
	        				//Toast.makeText(getApplicationContext(), "please choose update.zip", Toast.LENGTH_SHORT).show();
	        		}  
	        	}
	        	*/
                //String sdPath =Environment.getExternalStorageDirectory().getPath().toString() + File.separator + "update.zip";


                try {
                    //Log.d("lzl", "=========start smdtRebootRecovery=============");
                    smdt.smdtRebootRecovery();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.button_settime:
                String year = et_year.getText().toString();
                String month = et_month.getText().toString();
                String hourday = et_day.getText().toString();
                String hour = et_hour.getText().toString();
                String minute = et_minute.getText().toString();
                if (!(year.equals("") && (month.equals("") && (hourday.equals("") && (hour.equals("") && (minute.equals(""))))))) {
                    smdt.setTime(getApplicationContext(), Integer.valueOf(year), Integer.valueOf(month),
                            Integer.valueOf(hourday), Integer.valueOf(hour),
                            Integer.valueOf(minute));
                } else {
                    Toast.makeText(getApplicationContext(), "please input time!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.button_settimebynet:
                smdt.setTimeFromNetwork(true, MainActivity.this);
                break;
            case R.id.button_settimebynetoff:
                smdt.setTimeFromNetwork(false, MainActivity.this);
                break;
            case R.id.btn_wifi:
                Intent intent2 = new Intent(this, WifiActivity.class);
                startActivity(intent2);
                break;
            case R.id.btn_showstatus:
                smdt.smdtSetStatusBar(getApplicationContext(), true);
                break;
            case R.id.btn_hidestatus:
                smdt.smdtSetStatusBar(getApplicationContext(), false);
                break;
            case R.id.btn_one:
                Handler mHandler = new Handler();
                Runnable updateThread = new Runnable() {
                    public void run() {
                        smdt.setRotation("0");
                    }
                };
                mHandler.postDelayed(updateThread, 200);
                break;
            case R.id.btn_two:
                smdt.setRotation("90");
                break;
            case R.id.btn_three:
                smdt.setRotation("180");
                break;
            case R.id.btn_four:
                smdt.setRotation("270");
                break;
            case R.id.btn_openback:
                smdt.smdtSetLcdBackLight(1);
                break;
            case R.id.btn_closeback:
                smdt.smdtSetLcdBackLight(0);
                break;
            case R.id.btn_getback:
                Toast.makeText(getApplicationContext(), "lcd backLight state:" + smdt.smdtGetLcdLightStatus(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_bright:
                String bright = edit_setBright.getText().toString();
                if (!bright.equals("")) {
                    smdt.setBrightness(getContentResolver(), Integer.parseInt(bright));
                } else {
                    Toast.makeText(getApplicationContext(), "please input bright value", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_getBright:
                tv_bright.setText(smdt.getScreenBrightness(getApplicationContext()) + "");
                break;
            case R.id.btn_volumeadd:
                mAudioManager.adjustStreamVolume(STREAM_MUSIC, ADJUST_RAISE, FLAG_SHOW_UI);//澧炲姞闊抽噺
                break;
            case R.id.btn_volumedec:
                mAudioManager.adjustStreamVolume(STREAM_MUSIC, ADJUST_LOWER, FLAG_SHOW_UI);//鍑忓皯闊抽噺   STREAM_RING  STREAM_MUSIC  STREAM_ALARM
                break;
            case R.id.btn_mute:
                Log.d("lzl", "=smdt.getAndroidVersion():" + smdt.getAndroidVersion());
                if (smdt.getAndroidVersion().equals("4.4.4") || smdt.getAndroidVersion().equals("4.2.2")) {
                    smdt.setVolumeStates(2);
                } else {
                    //mAudioManager.adjustStreamVolume(STREAM_MUSIC,ADJUST_MUTE,FLAG_SHOW_UI);
                    Thread browseThread = new Thread() {
                        public void run() {
                            exec_Cmd("input keyevent 164");
                        }
                    };
                    browseThread.start();
                }
                break;
            case R.id.btn_normal:
                if (smdt.getAndroidVersion().equals("4.4.4") || smdt.getAndroidVersion().equals("4.2.2")) {
                    smdt.setVolumeStates(3);
                } else {
                    //mAudioManager.adjustStreamVolume(STREAM_MUSIC,ADJUST_UNMUTE,FLAG_SHOW_UI);   //KeyEvent.KEYCODE_VOLUME_MUTE
                    Thread browseThread = new Thread() {
                        public void run() {
                            exec_Cmd("input keyevent 164");
                        }
                    };
                    browseThread.start();
                }
                break;
            case R.id.btn_setVolume:
                String volume = edit_values.getText().toString();
                if (!volume.equals("")) {
                    smdt.smdtSetVolume(getApplicationContext(), Integer.parseInt(volume));
                    Toast.makeText(getApplicationContext(), smdt.smdtSetVolume(getApplicationContext(), Integer.parseInt(volume)) + "", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "please input volume", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_getVolume:
                txt_volume_show.setText(smdt.smdtGetVolume(getApplicationContext()) + "");
                break;
            case R.id.btn_getmac:
                tv_Mac.setText(smdt.smdtGetEthMacAddress() + "");
                break;
            case R.id.btn_setIP:
                String ip = tv_setIP.getText().toString();
                if (!ip.equals("")) {
                    smdt.smdtSetEthIPAddress(ip, "255.255.255.0", "192.9.50.1", "202.96.134.133");
                } else {
                    Toast.makeText(getApplicationContext(), "please input ip", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_getIP:
                tv_IP.setText(smdt.smdtGetEthIPAddress() + "");
                break;
            case R.id.btn_USB:
                if (ed_usb_num.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "please input usb number!", Toast.LENGTH_SHORT).show();
                    return;
                }
                tv_usb.setText(smdt.smdtGetUSBPath(getApplicationContext(), Integer.parseInt(ed_usb_num.getText().toString())));
                break;
            case R.id.btn_sd:
                tv_sd.setText(smdt.smdtGetSDcardPath(getApplicationContext()));
                break;
            case R.id.btn_watchdog:
                smdt.smdtWatchDogEnable((char) 1);
                break;
            case R.id.btn_closedog:
                smdt.smdtWatchDogEnable((char) 0);
                break;
            case R.id.btn_feeddog:
                smdt.smdtWatchDogFeed();
                break;
            case R.id.btn_openUSB:
                if (tv_usbNum.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "usb number is null", Toast.LENGTH_SHORT).show();
                    return;
                }
                smdt.smdtSetUsbPower(1, Integer.parseInt(tv_usbNum.getText().toString()), 1);
                break;
            case R.id.btn_closeUSB:
                if (tv_usbNum.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "usb number is null", Toast.LENGTH_SHORT).show();
                    return;
                }
                smdt.smdtSetUsbPower(1, Integer.parseInt(tv_usbNum.getText().toString()), 0);
                break;
            case R.id.btn_getEth:
                boolean isEnable = smdt.smdtGetEthernetState();
                if (isEnable) {
                    tv_eth.setText("open");
                } else {
                    tv_eth.setText("close");
                }
                break;
            case R.id.btn_openEthPower:
                smdt.smdtSetControl(11, 1);
                break;
            case R.id.btn_closeEthPower:
                smdt.smdtSetControl(11, 0);
                break;
            case R.id.btn_screenWidth:
                int val = smdt.smdtGetScreenWidth(getApplicationContext());
                tv_screenWidth.setText(val + "");
                break;
            case R.id.btn_screenHeight:
                val = smdt.smdtGetScreenHeight(getApplicationContext());
                tv_screenHeight.setText(val + "");
                break;
            case R.id.btn_write_eeprom:
                String strEEprom = et_eeprom_data.getText().toString();
                if (et_deviceId.getText().toString().equals("")
                        || et_areaId.getText().toString().equals("")
                        || et_start_addr.getText().toString().equals("")
                        || et_size.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "please check input paremete!", Toast.LENGTH_SHORT).show();
                    return;
                }

                smdt.smdtWriteExtROM(Integer.parseInt(et_deviceId.getText().toString()),
                        Integer.parseInt(et_areaId.getText().toString()),
                        Integer.parseInt(et_start_addr.getText().toString()),
                        Integer.parseInt(et_size.getText().toString()),
                        strEEprom.getBytes());
                break;
            case R.id.btn_read_eeprom:
                if (et_deviceId.getText().toString().equals("")
                        || et_areaId.getText().toString().equals("")
                        || et_start_addr.getText().toString().equals("")
                        || et_size.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "please check input paremete!", Toast.LENGTH_SHORT).show();
                    return;
                }

                byte[] data = smdt.smdtReadExtROM(Integer.parseInt(et_deviceId.getText().toString()),
                        Integer.parseInt(et_areaId.getText().toString()),
                        Integer.parseInt(et_start_addr.getText().toString()),
                        Integer.parseInt(et_size.getText().toString()));
                if (data != null) {
                    String s = new String(data);
                    tv_read_eeprom.setText(s);
                } else {
                    tv_read_eeprom.setText("");
                }
                break;
            case R.id.btn_get_api_version:
                tv_api_version.setText(smdt.smdtGetAPIVersion());
                break;
            case R.id.btn_3G_open:
                smdt.smdtSetControl(9, 1);
                break;
            case R.id.btn_3G_close:
                smdt.smdtSetControl(9, 0);
                break;
            case R.id.btn_3G_reset:
                try {
                    smdt.smdtSetControl(10, 0);
                    Thread.sleep(1000);
                    smdt.smdtSetControl(10, 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                break;
            case R.id.btn_led_open:
                smdt.smdtSetControl(5, 1);
                break;
            case R.id.btn_led_close:
                if (smdt == null) {
                    Log.d("lzl", "============smdt is null======");
                } else
                    smdt.smdtSetControl(5, 0);
                break;

            case R.id.btn_headsetmic_open:
                smdt.setHeadsetMicOnOff(1);
                break;
            case R.id.btn_headsetmic_close:
                if (smdt == null) {
                    Log.d("lzl", "============smdt is null======");
                } else
                    smdt.setHeadsetMicOnOff(0);
                break;
            case R.id.btn_unmount:
                if (smdt == null) {
                    Log.d("lzl", "============smdt is null======");
                } else
                    smdt.unmountVolume(et_unmount_path.getText().toString(), true, false);
                break;
            case R.id.btn_get_screen_number:
                int screen_number = smdt.getScreenNumber();
                tv_screen_number.setText(screen_number + "");
                break;

            case R.id.btn_get_hdmiin_status:
                int ret = smdt.getHdmiinStatus();
                Toast.makeText(getApplicationContext(), "hdmi status:" + ret, Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }

    }

    public void smdtRebootRecovery(Context context) {
        File RECOVERY_DIR = new File("/cache/recovery");
        File COMMAND_FILE = new File(RECOVERY_DIR, "command");
        String SHOW_TEXT = "--show_text";
        RECOVERY_DIR.mkdirs();
        // In case we need it                   COMMAND_FILE.delete();
        // In case it's not writable
        try {
            FileWriter command = new FileWriter(COMMAND_FILE);
            command.write(SHOW_TEXT);
            command.write("\n");
            command.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        pm.reboot("recovery");
    }

    public final int exec_Cmd(String paramString) {

        try {
            java.lang.Process localProcess = Runtime.getRuntime().exec("sh");
            Object localObject = localProcess.getOutputStream();
            DataOutputStream localDataOutputStream = new DataOutputStream((OutputStream) localObject);
            String str = String.valueOf(paramString);
            localObject = str + "\n";
            localDataOutputStream.writeBytes((String) localObject);
            localDataOutputStream.flush();
            localDataOutputStream.writeBytes("exit\n");
            localDataOutputStream.flush();
            localProcess.waitFor();
            localObject = localProcess.exitValue();

        } catch (Exception localException) {
            localException.printStackTrace();
        }

        return 0;
    }

    public void smdtInstallPackage(Context context, File packageFile) throws IOException {
        File RECOVERY_DIR = new File("/cache/recovery");
        File COMMAND_FILE = new File(RECOVERY_DIR, "command");
        String filename = packageFile.getCanonicalPath();
        String arg = "--update_package=" + filename;
        String SHOW_TEXT = "--show_text";
        RECOVERY_DIR.mkdirs();
        FileWriter command = new FileWriter(COMMAND_FILE);
        try {
            //command.write(arg);
            command.write(SHOW_TEXT);
            command.write("\n");
            command.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        pm.reboot("recovery");


    }


    public String getMipsDualResolution(Activity ctx) {
        DisplayManager mDisplayManager = (DisplayManager) ctx.getSystemService(Context.DISPLAY_SERVICE);
        Display[] displays = mDisplayManager.getDisplays();
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();


        if (displays.length == 1) {
            displays[0].getRealMetrics(mDisplayMetrics);
            int width = mDisplayMetrics.widthPixels;
            int height = mDisplayMetrics.heightPixels;
            Log.i("DeviceUtils", "length == 1 getMipsDualResolution: lvds w" + width);
            Log.i("DeviceUtils", "length == 1 getMipsDualResolution: lvds h" + height);
            return "LVDS_" + width + "*" + height;
        } else if (displays.length == 2) {
            displays[0].getRealMetrics(mDisplayMetrics);
            int width_1 = mDisplayMetrics.widthPixels;
            int height_1 = mDisplayMetrics.heightPixels;

            displays[1].getRealMetrics(mDisplayMetrics);
            int width_2 = mDisplayMetrics.widthPixels;
            int height_2 = mDisplayMetrics.heightPixels;
            Log.i("DeviceUtils", "getMipsDualResolution: lvds w" + width_1);
            Log.i("DeviceUtils", "getMipsDualResolution: lvds h" + height_1);
            Log.i("DeviceUtils", "getMipsDualResolution: hdmi w" + width_2);
            Log.i("DeviceUtils", "getMipsDualResolution: hdmi h" + height_2);
            return "LVDS_" + width_1 + "*" + height_1 + "," + "HDMI_" + width_2 + "*" + height_2;
        }

        return null;
    }

}

