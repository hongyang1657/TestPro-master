package com.byids.hy.testpro.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

import com.byids.hy.testpro.R;

/**
 * Created by gqgz2 on 2016/9/22.
 */
public class FullScreenCameraActivity extends Activity{

    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setContentView(R.layout.full_screen_camera);
        initView();
    }

    private void initView(){
        surfaceView = (SurfaceView) findViewById(R.id.sfv_camera_full_screen);
        surfaceHolder = surfaceView.getHolder();

    }
}
