package com.byids.hy.testpro.activity;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.byids.hy.testpro.R;

/**
 * Created by gqgz2 on 2016/9/25.
 */
public class LoginExplainActivity extends BaseActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setContentView(R.layout.login_explain_layout);

    }
}
