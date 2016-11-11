package com.byids.hy.testpro.service;

import android.app.Service;
import android.content.Intent;
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


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
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
                while (true){
                    udpSocket.sendEncryptUdp();
                    udpCheck = udpSocket.getUdpCheck();
                    ip = udpSocket.getIp();
                    Log.i(TAG, "run: ----------收到的udpcheck:"+udpCheck+"----收到的主机ip："+ip);
                    try {
                        sleep(10000);
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
}
