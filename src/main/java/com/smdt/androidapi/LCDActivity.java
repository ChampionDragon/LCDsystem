package com.smdt.androidapi;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.smdt.SmdtManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import com.smdt.androidapi.base.BaseApplication;
import com.smdt.androidapi.fragment.ImageFragment;
import com.smdt.androidapi.fragment.VideoFragment;
import com.smdt.androidapi.lcd.AppInfoActivity;
import com.smdt.androidapi.lcd.OnOffSetActivity;
import com.smdt.androidapi.lcd.SDKInfoActivity;
import com.smdt.androidapi.lcd.SetActivity;
import com.smdt.androidapi.lcd.UpdateActivity;
import com.smdt.androidapi.lcd.WifiInfo;
import com.smdt.androidapi.listener.DiadisListener;
import com.smdt.androidapi.socket.TCPThread;
import com.smdt.androidapi.socket.UDPThread;
import com.smdt.androidapi.utils.Constant;
import com.smdt.androidapi.utils.DialogCustomUtil;
import com.smdt.androidapi.utils.DialogNotileUtil;
import com.smdt.androidapi.utils.Logs;
import com.smdt.androidapi.utils.NetConnectUtil;
import com.smdt.androidapi.utils.SmallUtil;
import com.smdt.androidapi.view.DialogCode;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LCDActivity extends FragmentActivity implements View.OnClickListener {
    private LinearLayout swiMenu, onoffMenu, checkMenu, setMenu;
    public SmdtManager smdt;//smdt是控制板的jdk对象有厂家提供
    private String tag = "LCDActivity";
    //Environment.DIRECTORY_PICTURES  系统的照片根目录
    private String filePath = Environment.
            getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath() + File.separator;
    private DialogCode codeDia;
    public static final String VideoFragment = "videofragment";
    public static final String ImageFragment = "imagefragment";
    private Fragment currentFragment;
    private UDPThread udpThread;
    private TCPThread tcpThread;
    private LinearLayout lcd;
    private CheckBox lcdll;
//    private FrameLayout frameLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lcd);
        initView();
        smdt = SmdtManager.create(getApplicationContext());
        /*每次调app就让其初始化默认碎片*/
        setDefaultFragment();
        Logs.d(tag + "   onCreate ");
        initUtcp();
        test();
    }

    //    初始化tcp和udp的连接
    private void initUtcp() {
        udpThread = new UDPThread(handler);
        udpThread.startReceive();
        tcpThread = new TCPThread(handler);
        new Thread(new Runnable() {
            @Override
            public void run() {
                tcpThread.startReceive();
            }
        }).start();
    }

    private void test() {
        /*六秒钟自动关闭二维码*/
//        handler.sendEmptyMessageDelayed(1, 6000);
//        Logs.d(TimeUtil.long2time(System.currentTimeMillis(), Constant.formatPhoto));
        /*每隔3秒钟更换二维码*/
//        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(new Runnable() {
//            @Override
//            public void run() {
//                handler.obtainMessage().sendToTarget();
//            }
//        }, 3, 3, TimeUnit.SECONDS);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constant.diaDis:
                    if (codeDia != null) {
                        codeDia.closeDia();
                        codeDia = null;
                        findViewById(R.id.fragment).setVisibility(View.VISIBLE);
                        currentFragment.onStart();
                    }
                    break;
                case Constant.diaCreate:
                    String params = (String) msg.obj;

                    if (params != null) {
                        try {
                            JSONObject jb = new JSONObject(params);
                            createCodeDialog(jb.getString(Constant.diacode), jb.getString(Constant.diastr));
                        } catch (JSONException e) {
                            Logs.e(tag + "109  " + e);
                        }
                    } else {
                        DialogNotileUtil.show(LCDActivity.this, "发送格式不对");
                    }
                    break;

                case Constant.diaCreateByTime:
                    String params2 = (String) msg.obj;

                    if (params2 != null) {
                        int time = 6;
                        try {
                            JSONObject jb = new JSONObject(params2);
                            createCodeDialog(jb.getString(Constant.diacode), jb.getString(Constant.diastr));
                            time = Integer.valueOf(jb.getString(Constant.diaDisTime));
                        } catch (JSONException e) {
                            Logs.e(tag + "120  " + e);
                        }
                        handler.sendEmptyMessageDelayed(Constant.diaDis, time * 1000);
                    } else {
                        DialogNotileUtil.show(LCDActivity.this, "发送格式不对");
                    }
                    break;
            }
        }
    };

    /*创建二维码*/
    private void createCodeDialog(String code, String str) {
//        Logs.w(tag + "152  codeDia  " + codeDia);
//        Logs.v(currentFragment.isHidden() + "   当前碎片状态");
        if (codeDia != null) {
            codeDia.updateCode(code);
            codeDia.updateTv(str);
        } else {
            codeDia = new DialogCode(this, code, str, new DiadisListener() {
                @Override
                public void dismiss() {
                    findViewById(R.id.fragment).setVisibility(View.VISIBLE);
                    currentFragment.onStart();
                }
            });
            findViewById(R.id.fragment).setVisibility(View.INVISIBLE);
            currentFragment.onPause();
        }

    }


    /*每次交互以后让它自动运行*/
    @Override
    protected void onResume() {
        super.onResume();
           /*延时二十秒检查网络状态*/
        new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (!NetConnectUtil.NetConnect(LCDActivity.this)) {
                    DialogNotileUtil.show(LCDActivity.this, "未连接到网络");
                }
            }
        }.sendEmptyMessageDelayed(1, 19999);


        /*延迟0.1秒判断是否暂停视频*/
        final Fragment fragmentByTag = getFragmentManager().findFragmentByTag(VideoFragment);
        new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Logs.i("视频fragment " + fragmentByTag);
                if (fragmentByTag != null) {
                    if (fragmentByTag.isHidden()) {
                        fragmentByTag.onPause();
                    }
                }
            }
        }.sendEmptyMessageDelayed(1, 111);

    }


    @Override
    protected void onStart() {
        super.onStart();
//        Logs.e(tag+"  onStart");
    }

    @Override
    protected void onPause() {
        super.onPause();
//        Logs.e(tag+"  onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
//        Logs.e(tag+"  onStop");
    }

    /*初始化fragment,默认开启图片广告碎片*/
    private void setDefaultFragment() {
//        FragmentTransaction transaction = getFragmentManager().beginTransaction();
//        imagefragment = new ImageFragment();
//        videofragment = new VideoFragment();
//        transaction.add(R.id.fragment, imagefragment).commit();
        ChangeFragment(ImageFragment);
    }

    public void ChangeFragment(String flag) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Fragment fragment = getFragmentManager().findFragmentByTag(flag);
        boolean isShowOrAdd = true;//true表示show，false表示add
        if (fragment == null) {
            isShowOrAdd = false;
            switch (flag) {
                case ImageFragment:
                    fragment = new ImageFragment();
                    break;
                case VideoFragment:
                    fragment = new VideoFragment();
                    break;
                default:
                    break;
            }
        }
        if (currentFragment != null) {
            currentFragment.onPause();
            transaction.hide(currentFragment);
        }
        if (isShowOrAdd) {
            transaction.show(fragment);
            fragment.onResume();
        } else {
            transaction.add(R.id.fragment, fragment, flag);
        }
        currentFragment = fragment;
        transaction.commit();
    }


    private void initView() {
        swiMenu = (LinearLayout) findViewById(R.id.lcdMode);
        swiMenu.setOnClickListener(this);
        onoffMenu = (LinearLayout) findViewById(R.id.lcdback);
        onoffMenu.setOnClickListener(this);
        checkMenu = (LinearLayout) findViewById(R.id.lcdcheck);
        checkMenu.setOnClickListener(this);
        setMenu = (LinearLayout) findViewById(R.id.lcdSet);
        setMenu.setOnClickListener(this);
        findViewById(R.id.videos_screen).setOnClickListener(this);
        lcd = (LinearLayout) findViewById(R.id.lcd);
        lcdll = (CheckBox) findViewById(R.id.lcdll);
        lcdll.setOnClickListener(this);
//        frameLayout= (FrameLayout) findViewById(R.id.fragment);
    }

    /*切换广告播放模式*/
    private void setSwiMode(View menu) {
//        java.lang.IllegalStateException: commit already called。
//        如果在你一个类中已经使用一个成员变量transaction去调用了一次commit()方法，
//        那么在其它外部类中就不能再使用一个成员变量transaction再次调用commit()方法。
//        我当时出错是因为在Activity中加载了LeftFrag并在Activity中commit了一次。
        final FragmentTransaction transaction = getFragmentManager().beginTransaction();
        PopupMenu popupMenu = new PopupMenu(this, menu);
        popupMenu.getMenuInflater().inflate(R.menu.swithmode, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.modeImage:
//                        transaction.replace(R.id.fragment, new ImageFragment()).commit();
                        ChangeFragment(ImageFragment);
                        break;
                    case R.id.modeVideo:
//                        transaction.replace(R.id.fragment, new VideoFragment()).commit();
                        ChangeFragment(VideoFragment);
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    /*系统设置的menu*/
    private void setSetMenu(View menu) {
        PopupMenu popupMenu = new PopupMenu(this, menu);
        popupMenu.getMenuInflater().inflate(R.menu.set, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.set_onoff:
                        SmallUtil.getActivity(LCDActivity.this, OnOffSetActivity.class);
                        break;
//                    case R.id.set_offical:
//                        SmallUtil.getActivity(LCDActivity.this, MainActivity.class);
//                        break;
                    case R.id.set_normal:
                        SmallUtil.getActivity(LCDActivity.this, SetActivity.class);
                        break;
                    case R.id.set_ftp:
                        SmallUtil.startAPP(Constant.ftpPackageName, LCDActivity.this);
                        BaseApplication.getInstance().onTerminate();
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    /*查看信息的menu*/
    private void setCheckMenu(View menu) {
        PopupMenu popupMenu = new PopupMenu(this, menu);
        popupMenu.getMenuInflater().inflate(R.menu.check, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.check_app:
                        SmallUtil.getActivity(LCDActivity.this, AppInfoActivity.class);
                        break;
                    case R.id.check_sdk:
                        SmallUtil.getActivity(LCDActivity.this, SDKInfoActivity.class);
                        break;
                    case R.id.check_update:
                        SmallUtil.getActivity(LCDActivity.this, UpdateActivity.class);
                        break;
                    case R.id.check_wifi:
                        SmallUtil.getActivity(LCDActivity.this, WifiInfo.class);
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    /*显现设置开关机的menu*/
    private void setOnoffMenu(View menu) {
        PopupMenu popmenu = new PopupMenu(this, menu);
        popmenu.getMenuInflater().inflate(R.menu.onoff, popmenu.getMenu());
        popmenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.onoff_reboot:
                        Dialog one = DialogCustomUtil.create("警告", "您确定要重启系统吗？",
                                LCDActivity.this, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        smdt.smdtReboot("reboot");
                                    }
                                });
                        one.show();
                        break;
                    case R.id.onoff_shutdown:
                        Dialog dialog = DialogCustomUtil.create("警告", "您确定要关闭系统吗？",
                                LCDActivity.this, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        smdt.shutDown();
                                    }
                                });
                        dialog.show();
                        break;
                }
                return false;
            }
        });
        popmenu.show();
    }


    /*截屏  这个功能必须要有签名才有效*/
    private void videoScreen() {
        SimpleDateFormat sdformats = new SimpleDateFormat(Constant.formatPhoto);
        String fileNames = sdformats.format(new Date(System.currentTimeMillis())) + ".png";
        smdt.smdtTakeScreenshot(filePath, fileNames, getApplicationContext());
        DialogNotileUtil.show(this, "文件保留成功,请在根目录\"Picture\"文件夹下查看。");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lcdback:
                setOnoffMenu(onoffMenu);
                break;
            case R.id.lcdcheck:
                setCheckMenu(checkMenu);
                break;
            case R.id.lcdSet:
                setSetMenu(setMenu);
                break;
            case R.id.lcdMode:
                setSwiMode(swiMenu);
                break;
            case R.id.videos_screen:
                videoScreen();
                break;
            case R.id.lcdll:
                if (lcdll.isChecked()) {
                    lcd.setVisibility(View.VISIBLE);
                } else {
                    lcd.setVisibility(View.GONE);
                }
                break;
        }
    }


    /* 用户在主界面按返回键提示"是否退出程序"*/
    @Override
    public void onBackPressed() {
        BaseApplication.getInstance().exitApp();
    }


}
