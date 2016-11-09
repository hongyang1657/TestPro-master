package com.byids.hy.testpro.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by gqgz2 on 2016/10/31.
 */

public class TcpConnectService extends Service{

    private String TAG = "result";
    private TcpBinder mBinder = new TcpBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate:--------------创建service------------------- ");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onCreate:--------------启动service------------------- ");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onCreate:--------------销毁service------------------- ");
    }

    class TcpBinder extends Binder{

    }
}
