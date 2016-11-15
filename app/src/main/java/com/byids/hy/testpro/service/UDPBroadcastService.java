package com.byids.hy.testpro.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.byids.hy.testpro.utils.UDPSocket;

/**
 * Created by gqgz2 on 2016/11/11.
 */

public class UDPBroadcastService extends Service{
    private static final String TAG = "result";
    private UDPSocket udpSocket;
    private String udpCheck;
    private String ip;
    private UDPBinder mUDPBinder = new UDPBinder();


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
        //后台开启UDP Socket，每隔一段时间发送udp广播，寻找主机
        new Thread(){
            @Override
            public void run() {
                super.run();
                int i = 0;
                while (i==0){
                    Log.i(TAG, "run: --------------开始循环发送udp-------------");
                    udpSocket.sendEncryptUdp();
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    udpCheck = udpSocket.getUdpCheck();
                    ip = udpSocket.getIp();
                    if (udpCheck=="ip"||udpCheck.equals("ip")){
                        Log.i(TAG, "run: --------ip------------ip---------------ip-------------ip-----------ip-------收到的主机ip："+ip);
                    }
                    try {
                        sleep(9000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    i = 1;
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
        public String getHostIp(){
            return ip;
        }

        public String getUdpCheck(){
            return udpCheck;
        }
    }
}
