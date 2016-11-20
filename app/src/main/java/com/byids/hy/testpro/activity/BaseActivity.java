package com.byids.hy.testpro.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;


/**
 * Created by gqgz2 on 2016/11/18.
 */

public class BaseActivity extends Activity{
    private MyBaseActivityBroadcast myBaseActivityBroadcast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //动态注册广播
        myBaseActivityBroadcast = new MyBaseActivityBroadcast();
        IntentFilter intentFilter = new IntentFilter("com.byids.hy.testpro.activity.BaseActivity");
        registerReceiver(myBaseActivityBroadcast,intentFilter);
    }

    //在销毁的方法里注销广播
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myBaseActivityBroadcast);
    }

    public class MyBaseActivityBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //接收发送过来的广播内容
            int closeAll = intent.getIntExtra("closeAll", 0);
            if (closeAll == 1) {
                Log.i("result", "onReceive:销毁BaseActivity ");
                finish();//销毁BaseActivity
            }
        }
    }
}
