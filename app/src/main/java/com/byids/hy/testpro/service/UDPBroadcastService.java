package com.byids.hy.testpro.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.byids.hy.testpro.utils.UDPSocket;

/**
 * 在wifi情况下后台连续发送udp广播，寻找
 * Created by gqgz2 on 2016/11/11.
 */

public class UDPBroadcastService extends Service{
    private static final String TAG = "result_udp_hy";
    private static final String TAG_UDP_SERVICE = "udp_service";
    private UDPSocket udpSocket;
    private String udpCheck;
    private String ip;
    private UDPBinder mUDPBinder = new UDPBinder();
    private boolean isUdpThreadOn;
    private boolean isSendingUDP = true;     //是否正在发送udp



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mUDPBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate:--------------创建Udp service------------------- ");
        udpSocket = new UDPSocket("主机在吗？");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onCreate:--------------启动Udp service------------------- ");
        new Thread(){
            @Override
            public void run() {
                super.run();
                while (isSendingUDP){
                    //Log.i(TAG, "run: --------------开始循环发送udp-------------"+isUdpThreadOn);
                    udpSocket.sendEncryptUdp();
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    udpCheck = udpSocket.getUdpCheck();
                    ip = udpSocket.getIp();
                    if (udpCheck.equals("ip")){
                        Log.i(TAG, "udp 收到的主机ip："+ip);
                    }else{
                        Log.i(TAG, "udp 找不到ip了，可能是关闭了网络，停止发送udp");
                        isSendingUDP = false;
                    }
                    try {
                        sleep(8000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onCreate:--------------销毁Udp service------------------- ");
    }

    //与Activity通信
    public class UDPBinder extends Binder{
        private boolean isSendUDPOn = true;

        public String getHostIp(){
            /*if (ip.equals("192.168.10.220")){                    //这里做判断是为了屏蔽拜爱展厅的主机（192.168.10.220）
                return "";
            }else {
                return ip;
            }*/
            return ip;
        }

        public String getUdpCheck(){
            /*if (ip.equals("192.168.10.220")){                    //这里做判断是为了屏蔽拜爱展厅的主机（192.168.10.220）
                return "";
            }else {
                return udpCheck;
            }*/
            return udpCheck;
        }

        public void startSendUDP(){
            //后台开启UDP Socket，每隔一段时间发送udp广播，寻找主机

        }

        public void stopSendUDP(){
            this.isSendUDPOn = false;
        }
    }
}
