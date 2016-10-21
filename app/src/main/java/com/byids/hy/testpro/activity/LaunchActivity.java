package com.byids.hy.testpro.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.byids.hy.testpro.R;

/**
 * Created by gqgz2 on 2016/10/21.
 */
public class LaunchActivity extends Activity{

    private static final int LAUNCH_DELAY = 2000;
    private ImageView ivLaunch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setContentView(R.layout.launch_layout);
        ivLaunch = (ImageView) findViewById(R.id.iv_launch);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LaunchActivity.this,NewLoginActivity.class);
                startActivity(intent);
                finish();

            }
        },LAUNCH_DELAY);
    }


}
