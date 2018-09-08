package com.smdt.androidapi;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.smdt.SmdtManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import com.smdt.androidapi.base.BaseApplication;
import com.smdt.androidapi.fragment.ImageFragment;
import com.smdt.androidapi.fragment.VideoFragment;
import com.smdt.androidapi.lcd.ApkActivity;
import com.smdt.androidapi.lcd.AppInfoActivity;
import com.smdt.androidapi.lcd.OnOffSetActivity;
import com.smdt.androidapi.lcd.SDKInfoActivity;
import com.smdt.androidapi.lcd.SetActivity;
import com.smdt.androidapi.lcd.WifiInfo;
import com.smdt.androidapi.listener.DiadisListener;
import com.smdt.androidapi.listener.OnDoubleClickListener;
import com.smdt.androidapi.socket.TCPThread;
import com.smdt.androidapi.socket.UDPThread;
import com.smdt.androidapi.utils.Constant;
import com.smdt.androidapi.utils.DialogCustomUtil;
import com.smdt.androidapi.utils.DialogText;
import com.smdt.androidapi.utils.GetIpAddress;
import com.smdt.androidapi.utils.Logs;
import com.smdt.androidapi.utils.NetConnectUtil;
import com.smdt.androidapi.utils.SmallUtil;
import com.smdt.androidapi.utils.TimeUtil;
import com.smdt.androidapi.utils.ToastUtil;
import com.smdt.androidapi.view.DialogCode;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LCDActivity extends FragmentActivity implements View.OnClickListener {
    private LinearLayout swiMenu, onoffMenu, checkMenu, setMenu;
    public SmdtManager smdt;//smdt是控制板的jdk对象有厂家提供
    private String tag = "LCDActivity";
    //Environment.DIRECTORY_PICTURES  系统的照片根目录
    private String filePath = Environment.
            getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath() + File.separator;
    private DialogCode codeDia;//二维码弹框
    private DialogText textDia;//文字弹框
    public static final String VideoFragment = "videofragment";
    public static final String ImageFragment = "imagefragment";
    private Fragment currentFragment;
    private UDPThread udpThread;
    private TCPThread tcpThread;
    private LinearLayout lcd;
    private String IP, gateWay, netmask;
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

       /* 设置双击监听*/
        findViewById(R.id.fragment).setOnTouchListener(new OnDoubleClickListener(new OnDoubleClickListener.DoubleClickCallback() {
            @Override
            public void onDoubleClick() {
                if (lcd.isShown()) {
                    lcd.setVisibility(View.GONE);
                } else {
                    lcd.setVisibility(View.VISIBLE);
                }
                Logs.i(TimeUtil.long2time(System.currentTimeMillis(), Constant.formatPhoto));
            }
        }));
    }

    /*窗口界面的设置*/
    private void initWindow() {
        //隐藏虚拟按键，并且全屏(无法更改）
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                    | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
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
        /*在以太网连接的情况下获取网络数据*/
//        boolean EthernetState = smdt.smdtGetEthernetState();//判断以太网连接的状态
//        Logs.v("太网连接的状态: " + EthernetState);
//        if (EthernetState) {
//            Logs.e("以太网Ip: " + smdt.smdtGetEthIPAddress());
//            Logs.i("以太网Mac: " + smdt.smdtGetEthMacAddress());
//        }
        /*获取wifi的Ip*/
        String devIp = BaseApplication.getInstance().getIp();
        if (devIp.equals("0.0.0.0")) {
            devIp = GetIpAddress.getWiredIP();//有线（以太网）情况下获取IP
            Logs.i(tag + "142以太网IP" + devIp);
        }
        /*从数据库读取IP判断和现在的设备ip是否相同，防止用户拔掉网线设备ip改变*/
        if (BaseApplication.sp.getBoolean(Constant.changeIP)) {
            IP = BaseApplication.sp.getString(Constant.IP);
            Logs.d("判断现在IP是否和上次保存的IP一致："+IP.equals(devIp)+"\n 现在的ip:"+devIp+"数据库保留的ip:"+IP);
            if (!IP.equals(devIp)) {
                netmask = BaseApplication.sp.getString(Constant.netmask);
                gateWay = BaseApplication.sp.getString(Constant.gateWay);
                Logs.v(tag+"149:"+netmask+"  "+gateWay);
                setIP(IP, netmask);
                SetGateway(gateWay);
            }
        }
        /*获取Root权限*/
//        String apkRoot = "chmod 777 " + getPackageCodePath();//getPackageCodePath()来获得当前应用程序对应的 apk 文件的路径
//        boolean b = RootCommand("");
//        Logs.v("获取Root权限:" + b);

        /*通过SDKz自带方法设置IP*/
//        try {
//            smdt.smdtSetEthIPAddress("192.168.1.100", "255.255.255.0", "192.9.50.1", "202.96.134.133");
//        } catch (Exception e) {
//            Logs.e(tag + "154: " + e);
//        }
//        Logs.d(tag + "155以太网IP" + smdt.smdtGetEthIPAddress());

        /*开机后强制设置 ip、netmask*/
//        String com="ifconfig eth0 192.168.2.210 netmask 255.255.255.0";
//        try{
//            Process suProcess = Runtime.getRuntime().exec("su");//root权限
//            DataOutputStream os = new DataOutputStream(suProcess.getOutputStream());
//            os.writeBytes(com+ "\n");
//            os.flush();
//            os.writeBytes("exit\n");
//            os.flush();
//        }catch (IOException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        textDia = new DialogText("测试\naaaaaa\nbbbbbbb\ndasdasdsadafgkjgkjhkllsdasdasdasd", this);
        /*六秒钟自动关闭二维码*/
//        handler.sendEmptyMessageDelayed(1, 6000);
//        Logs.d(TimeUtil.long2time(System.currentTimeMillis(), Constant.formatPhoto));
        /*每隔3秒钟更换二维码*/
//        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(new Runnable() {
//            @Override
//            public void run() {
//                handler.obtainMessage().sendToTarget();
//            }
//        }, 6, 6, TimeUnit.SECONDS);
    }

    /*设置IP和子网掩码*/
    private void setIP(String ip, String netmask) {
         /*通过shell命令设置IP和netmask*/
        RootCommand("ifconfig eth0 " + ip + " netmask " + netmask);
            /*通过shell命令查看IP和netmask*/
        String result = RootCommand("ifconfig eth0");
        if (!result.contains(ip)) {
            setIP(ip, netmask);
        }
    }

    /*设置网关*/
    private void SetGateway(String gateway) {
       /*通过shell命令设置gateWay*/
        RootCommand("route add default gw " + gateway + " dev eth0");
        /*通过shell命令查看gateWay*/
        String result = RootCommand("ip route show");
        if (!result.contains(gateway)) {
            SetGateway(gateway);
        }
    }


    /**
     * 应用程序运行命令获取 Root权限，设备必须已破解(获得ROOT权限)
     *
     * @param command 命令：String apkRoot="chmod 777 "+getPackageCodePath(); RootCommand(apkRoot);
     * @return 读取的数据
     */
    public String RootCommand(String command) {
        String result = "";
        Process process = null;
        DataOutputStream os = null;
        DataInputStream is = null;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            int aa = process.waitFor();
//            Logs.w("waitFor():" + aa);
            is = new DataInputStream(process.getInputStream());
            byte[] buffer = new byte[is.available()];
//            Logs.d("大小" + buffer.length);
            is.read(buffer);
            String out = new String(buffer);
            result = out;
            Logs.e(tag + "245返回:" + out);
        } catch (Exception e) {
            e.printStackTrace();
            Logs.e(tag + "205:\n" + e);
            return e.toString();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Logs.e(tag + "217:\n" + e);
            }
            process.destroy();
        }
        return result;
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
                        initWindow();
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
                        ToastUtil.showLong("发送格式不对");
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
                        ToastUtil.showLong("发送格式不对");
                    }
                    break;

                case Constant.UpdateApk:
                    String params3 = (String) msg.obj;
                    UpdateApk(params3);
                    break;

                case Constant.textCreate:
                    String params4 = (String) msg.obj;
                    createTextDialog(params4);
                    break;

                case Constant.textDis:
                    closeTextDialog();
                    break;

                case Constant.textDisByTime:
                    String params5 = (String) msg.obj;

                    if (params5 != null) {
                        int time = 6;
                        try {
                            JSONObject jb = new JSONObject(params5);
                            createTextDialog(jb.getString(Constant.diastr));
                            time = Integer.valueOf(jb.getString(Constant.diaDisTime));
                        } catch (JSONException e) {
                            Logs.e(tag + "200  " + e);
                        }
                        handler.sendEmptyMessageDelayed(Constant.textDis, time * 1000);
                    } else {
                        ToastUtil.showLong("发送格式不对");
                    }
                    break;
                case Constant.setip:
                    String ipset = (String) msg.obj;
                    BaseApplication.sp.putBoolean(Constant.changeIP, true);
                    try {
                        JSONObject jb = new JSONObject(ipset);
                        IP = jb.getString(Constant.IPstr);
                        netmask = jb.getString(Constant.netMaskstr);
                        gateWay = jb.getString(Constant.gateWaystr);
                        setIP(IP, netmask);
                        SetGateway(gateWay);
                        /*向数据库保存ip,netmask,gaetway*/
                        BaseApplication.sp.putString(Constant.IP,IP);
                        BaseApplication.sp.putString(Constant.netmask,netmask);
                        BaseApplication.sp.putString(Constant.gateWay,gateWay);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    break;
                default:
//                    if (new Random().nextInt(2) == 1) {
//                        createTextDialog("测试\naaaaaa\nbbbbbbb\ndasdasdsadafgkjgkjhkllsdasdasdasd"
//                                + TimeUtil.long2time(System.currentTimeMillis(), Constant.formatPhoto));
//                    } else {
//                        closeTextDialog();
//                    }

                    break;
            }
        }
    };

    /*通过上位机更新APK*/
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
                    initWindow();
                }
            });
            findViewById(R.id.fragment).setVisibility(View.INVISIBLE);
            currentFragment.onPause();
        }
    }

    /*创建文字弹框*/
    private void createTextDialog(String s) {
        if (textDia != null) {
            textDia.updateTv(s);
        } else {
            textDia = new DialogText(s, this);
        }
    }

    /*删除文字弹框*/
    private void closeTextDialog() {
        if (textDia != null) {
            textDia.closeDia();
            textDia = null;
            initWindow();
        }
    }


    /*每次交互以后让它自动运行*/
    @Override
    protected void onResume() {
        super.onResume();

        //窗口界面的设置
        initWindow();

           /*延时二十秒检查网络状态*/
        new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (!NetConnectUtil.NetConnect(LCDActivity.this)) {
                    ToastUtil.showLong("未连接到网络");
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
        popupMenu.setOnDismissListener(dismiss);
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
        popupMenu.setOnDismissListener(dismiss);
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
//                        SmallUtil.getActivity(LCDActivity.this, UpdateActivity.class);
                        SmallUtil.getActivity(LCDActivity.this, ApkActivity.class);
                        break;
                    case R.id.check_wifi:
                        SmallUtil.getActivity(LCDActivity.this, WifiInfo.class);
                        break;
                }
                return false;
            }
        });
        popupMenu.setOnDismissListener(dismiss);
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
                    case R.id.onoff_out:
                        Dialog two = DialogCustomUtil.create("警告", "您确定要退出APP吗？",
                                LCDActivity.this, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        BaseApplication.getInstance().onTerminate();
                                    }
                                });
                        two.show();
                        break;
                }
                return false;
            }
        });
        popmenu.setOnDismissListener(dismiss);
        popmenu.show();
    }


    /*截屏  这个功能必须要有签名才有效*/
    private void videoScreen() {
        SimpleDateFormat sdformats = new SimpleDateFormat(Constant.formatPhoto);
        String fileNames = sdformats.format(new Date(System.currentTimeMillis())) + ".png";
        smdt.smdtTakeScreenshot(filePath, fileNames, getApplicationContext());
        ToastUtil.showLong("文件保留成功,请在根目录\"Picture\"文件夹下查看。");
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
        }
    }


    /* 用户在主界面按返回键提示"是否退出程序"*/
    @Override
    public void onBackPressed() {
        BaseApplication.getInstance().exitApp();
    }

    PopupMenu.OnDismissListener dismiss = new PopupMenu.OnDismissListener() {
        @Override
        public void onDismiss(PopupMenu menu) {
            initWindow();
        }
    };


}
