package com.smdt.androidapi.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Description: 测试获取的ip和端口号
 * AUTHOR: Champion Dragon
 * created at 2018/1/17
 **/
public class GetIpAddress {
    public static String IP;
    public static int PORT;

    public static String getIP() {
        return IP;
    }

    public static int getPort() {
        return PORT;
    }

    public static void getLocalIpAddress(ServerSocket serverSocket) {

        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    String mIP = inetAddress.getHostAddress().substring(0, 3);
                    if (mIP.equals("192")) {
                        IP = inetAddress.getHostAddress();    //获取本地IP
                        PORT = serverSocket.getLocalPort();    //获取本地的PORT
                        Logs.e("IP  "+ IP);
                        Logs.e("PORT "+ PORT);
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }


    public static String getWiredIP() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception e) {
            Logs.d("有线ip错误原因："+e);
        }
        return "有线ip地址没找到";
    }











}
