package com.smdt.androidapi.socket;

import android.os.Handler;
import android.os.Message;

import com.smdt.androidapi.base.BaseApplication;
import com.smdt.androidapi.utils.Constant;
import com.smdt.androidapi.utils.GetIpAddress;
import com.smdt.androidapi.utils.Logs;
import com.smdt.androidapi.utils.SmallUtil;
import com.smdt.androidapi.utils.SystemUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;


/**
 * Description: UDP连接的线程
 * AUTHOR: Champion Dragon
 * created at 2018/1/10
 **/

public class UDPThread {
    private String tag = "UDPThread";
    private DatagramSocket dSocket;
    private Handler handler = null;
    private boolean hasStartReceive = false;
    private ReceiveThread receiveThread = null;
    private int recvPort;//客户端的端口
    private InetAddress addr;//客户端ip地址


    /*判断是否断开接收*/
    public boolean isReceive() {
        return hasStartReceive;
    }

    public UDPThread(Handler handler) {
        this.handler = handler;
    }


    public String stopReceive() {
        if (!hasStartReceive) {
            return "已经断开，不用频繁操作";
        }

        hasStartReceive = false;
        dSocket.close();

                /*关闭线程*/
        if (receiveThread != null && receiveThread.isAlive()) {
            receiveThread.interrupt();
            receiveThread = null;
        }
        return "断开成功";
    }


    /*开始接收*/
    public void startReceive() {
        if (isReceive()) {
            return;
        }

        try {
            dSocket = new DatagramSocket(6789);
        } catch (SocketException e) {
            Logs.e(tag + " 72 " + e.toString());
        }

        if (receiveThread == null) {
            receiveThread = new ReceiveThread();
            receiveThread.start();
        }
        hasStartReceive = true;
        /*返回开始接收成功的信号*/
        Logs.v(tag + "81   udp开始接收");
    }


    class ReceiveThread extends Thread {
        @Override
        public void run() {
            byte[] recvBuf = new byte[999];
            final DatagramPacket dPacket = new DatagramPacket(recvBuf, recvBuf.length);

            try {
                while (hasStartReceive) {
                    BaseApplication.lock.acquire();
                    dSocket.receive(dPacket);//接收数据包
                    BaseApplication.lock.release();

                    String strRecv = new String(dPacket.getData(), 0, dPacket.getLength());
                    Logs.i(tag + "98  接收到的数据  " + strRecv);


                    recvPort = dPacket.getPort();//客户端的端口
                    addr = dPacket.getAddress();//客户端的ip地址
                    Logs.v(tag + " 100 ip地址:" + addr + " " + recvPort + "  " + dPacket.getAddress().getHostAddress());
                    /*对接收的数据做相应处理*/
                    judgement(strRecv);

//                    Bundle bundle = new Bundle();
//                    bundle.putString(KEYUDPRECIP, addr.getHostAddress());
//                    bundle.putInt(KEYUDPRECPORT, recvPort);
//                    bundle.putString(KEYUDPRECEIVE, strRecv);
//                    msg.setData(bundle);
                }
            } catch (IOException e) {
                Logs.e(tag + "  117  " + e);
            }
        }
    }

    private void judgement(String strRecv) {
        switch (strRecv) {
            case "getip":
                JSONObject jb = new JSONObject();
                try {
                    String ip = BaseApplication.getInstance().getIp();
                    if (ip.equals("0.0.0.0")) {
                        ip = GetIpAddress.getWiredIP();//有线（以太网）情况下获取IP
                    }
                    jb.put("ip", ip);
                    jb.put("port", Constant.port);
                    jb.put("id", SystemUtil.ID());
                    jb.put("n", SmallUtil.getnetmask());
                    jb.put("g", SmallUtil.getgateWay());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Logs.e(tag + "137  发送的数据  " + jb.toString());
                sendData(jb.toString());
                break;
            default:
                String result = JsonStr(strRecv);
                Logs.e(tag + "143 结果 " + result);
                sendData(result);
        }
    }

    /*解析Json数据*/
    private String JsonStr(String strRecv) {
        JSONObject jb, resJson;
        resJson = new JSONObject();
        try {
            JSONObject json = new JSONObject(strRecv);
            resJson.put(Constant.result, Constant.success);
            if (!SystemUtil.ID().equals(json.getString(Constant.devid))) {
                resJson.put(Constant.result, Constant.fail);
                resJson.put(Constant.errorstr, "deviceID not identical");
                return resJson.toString();
            }
            String signWay = json.getString(Constant.sign);
            String params;
            switch (signWay) {
                case Constant.signSetIP:

                    params = json.getString(Constant.params);
                    jb = new JSONObject(params);
                    Logs.i(jb.getString(Constant.IPstr) + "  166" + tag + jb.getString(Constant.netMaskstr));

                    Message msg = handler.obtainMessage(Constant.setip);
                    msg.obj = params;
                    handler.sendMessage(msg);
                    break;
                default:
                    resJson.put(Constant.result, Constant.fail);
                    resJson.put(Constant.errorstr, "signway is empty");
            }
        } catch (JSONException e) {
            try {
                resJson.put(Constant.result, Constant.fail);
                resJson.put(Constant.errorstr, "json error");
                Logs.e(tag+"180:"+e);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        return resJson.toString();
    }


    /*发送数据给服务端*/
    private void sendData(String s) {
        byte[] sendBuf = s.getBytes();
        try {
            addr = InetAddress.getByName("255.255.255.255");//防止客户端不在同一网段，通过广播回数据信息。
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        DatagramPacket sendPacket = new DatagramPacket(sendBuf, sendBuf.length, addr, recvPort);
        try {
            dSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
