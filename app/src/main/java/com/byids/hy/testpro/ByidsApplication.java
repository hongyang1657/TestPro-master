package com.byids.hy.testpro;

import android.app.Application;

import com.videogo.openapi.EZOpenSDK;

/**
 * Created by gqgz2 on 2016/9/21.
 */
public class ByidsApplication extends Application{

    private static String APP_KEY = "f5fb78450ed244cfaa577df6f993a572";

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化
        EZOpenSDK.initLib(this,APP_KEY,"");
    }
}
