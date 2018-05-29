package com.smdt.androidapi.socket;

import android.os.Handler;
import android.os.Message;

import com.smdt.androidapi.base.AsyncTaskExecutor;
import com.smdt.androidapi.utils.Constant;
import com.smdt.androidapi.utils.GetIpAddress;
import com.smdt.androidapi.utils.Logs;
import com.smdt.androidapi.utils.TimeUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Description: tcp服务器端用来接收客户端发来的数据
 * AUTHOR: Champion Dragon
 * created at 2018/1/13
 **/

public class TCPThread {
    private String tag = "TCPThread";
    private ServerSocket serverSocket = null;
    private Socket socket = null;
    private Handler handler = null;
    private boolean hasStartReceive = false;
    private recTcpThread receiveThread = null;


    public boolean isReceive() {
        return hasStartReceive;
    }

    public TCPThread(Handler handler) {
        this.handler = handler;
    }


    public String stopReceive() {
        if (!hasStartReceive) {
            return "已经断开，不用频繁操作";
        }

        hasStartReceive = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            /*最多允许连接50个*/
            serverSocket = new ServerSocket(Integer.valueOf(Constant.port));
            GetIpAddress.getLocalIpAddress(serverSocket);//测试用
        } catch (Exception e) {
            Logs.e(tag + " 75 " + e.toString());
        }


        if (receiveThread == null) {
            receiveThread = new recTcpThread();
            receiveThread.start();
        }
        hasStartReceive = true;
        /*返回开始接收成功的信号*/
        Logs.v(tag + "85  tcp开始接收");
    }


    /*服务器端应该是多线程的，因为一个服务器可能会有多个客户端连接在服务器上*/
    class recTcpThread extends Thread {
        @Override
        public void run() {
            while (hasStartReceive) {
                try {
                    socket = serverSocket.accept();
                    Logs.d(socket.getInetAddress() + " 对方ip地址 时间" + TimeUtil.getSystem(Constant.formatPhoto));
//                    new clientThread(socket).start();
                    AsyncTaskExecutor.getinstance().submit(new clientThread(socket));
                } catch (IOException e) {
                    Logs.e(tag + " 99 " + e);
                    try {
                        socket.close();
                    } catch (IOException e1) {
                        Logs.e(tag + " 103 " + e1);
                    }
                }
            }
        }
    }


    class clientThread extends Thread {
        private Socket socket;

        public clientThread(Socket soc) {
            socket = soc;
        }

        @Override
        public void run() {
            String result = "";

            try {
                //获取输入流，并读取客户端信息
                while (true) {
                    /*设置超时时间，防止线程阻塞*/
                    socket.setSoTimeout(19000);
                    InputStream inputStream = socket.getInputStream();
                    DataInputStream dataInputStream = new DataInputStream(
                            inputStream);
                    byte[] b = new byte[1024];
                    int length = dataInputStream.read(b);
                    if (length > 0) {
                        byte[] data = new byte[length];
                        System.arraycopy(b, 0, data, 0, length);
                        result = new String(data);
                        Logs.d("接收到的信息： " + result);
                        Logs.i("客户端ip " + socket.getInetAddress());
                        result = judgement(result);
                        Logs.e(tag + "136 发送数据  " + result);
                        PrintWriter pw = new PrintWriter(socket.getOutputStream());
                        pw.write(result);
                        pw.flush();
                    }
                /*短连接每次接收一条信息后就关闭socket和线程*/
                    socket.close();
                    this.interrupt();
                }

//                InputStream inputStream = socket.getInputStream();
//                InputStreamReader isr = new InputStreamReader(inputStream);
//                BufferedReader br = new BufferedReader(isr);
//                String s = null;
//                String result = null;
//
//                Logs.e("5  " + br.readLine());
//                StringBuffer strBuffer = new StringBuffer();
//                while ((s = br.readLine()) != null) {
//                    strBuffer.append(s);
//                }
//                Logs.d("接收的数据" + strBuffer.toString());
//


//                    socket.shutdownInput();//关闭输入流,获取输出流，响应客户端的请求
                //关闭资源
//                    isr.close();
//                    br.close();
//                    pw.close();
//                    socket.close();


            } catch (Exception e) {
                Logs.e(tag + "  168  " + e + "  时间 " + TimeUtil.getSystem(Constant.formatPhoto));
                try {
                  /*  连接超时后,关闭socket和线程（释放cpu)*/
                    socket.close();
                    this.interrupt();
                } catch (IOException e1) {
                    Logs.e(tag + "  172  " + e1);
                }
            }
        }
    }


    /*通过发送过来的数据作相应的处理*/
    private String judgement(String str) {
        JSONObject jb, resJson;
        resJson = new JSONObject();
        try {
            JSONObject json = new JSONObject(str);
            String signWay = json.getString(Constant.sign);

            resJson.put(Constant.result, Constant.success);

            String params;
            switch (signWay) {
                case Constant.signdiaCreate:
                    params = json.getString(Constant.params);
                    jb = new JSONObject(params);
                    Logs.i(jb.getString(Constant.diacode) + "  tcpThread183  " + jb.getString(Constant.diastr));

                    Message msg = handler.obtainMessage(Constant.diaCreate);
                    msg.obj = params;
                    handler.sendMessage(msg);

                    break;

                case Constant.signdiaCreateByTime:
                    params = json.getString(Constant.params);
                    jb = new JSONObject(params);
                    Logs.v(jb.getString(Constant.diacode) + "  tcpThread202  " + jb.getString(Constant.diastr)
                            + "   " + Integer.valueOf(jb.getString(Constant.diaDisTime)));

                    Message msg2 = handler.obtainMessage(Constant.diaCreateByTime);
                    msg2.obj = params;
                    handler.sendMessage(msg2);

                    break;

                case Constant.signdiaDis:
                    handler.sendEmptyMessage(Constant.diaDis);
                    break;

                case Constant.signUpdateApk:

                    params = json.getString(Constant.apkname);
                    Logs.i(tag + "231 " + params);

                    Message msg3 = handler.obtainMessage(Constant.UpdateApk);
                    msg3.obj = params;
                    handler.sendMessage(msg3);

                    break;

                case Constant.signtextDis:
                    handler.sendEmptyMessage(Constant.textDis);
                    break;

                case Constant.signtextCreate:

                    params = json.getString(Constant.diastr);
                    Logs.i(tag + "246 " + params);

                    Message msg4 = handler.obtainMessage(Constant.UpdateApk);
                    msg4.obj = params;
                    handler.sendMessage(msg4);

                    break;

                case Constant.signtextCreateByTime:
                    params = json.getString(Constant.params);

                    Logs.v(tag+" 257  " +params);

                    Message msg5 = handler.obtainMessage(Constant.textDisByTime);
                    msg5.obj = params;
                    handler.sendMessage(msg5);

                    break;

                default:
                    resJson.put(Constant.result, Constant.fail);
                    resJson.put(Constant.errorstr, "signway is empty");
            }
        } catch (JSONException e) {
            try {
                resJson.put(Constant.result, Constant.fail);
                resJson.put(Constant.errorstr, e.toString());
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        return resJson.toString();
    }


}
