package com.byids.hy.testpro.utils;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.byids.hy.testpro.R;

/**
 * 耗时操作时弹出progressDialog工具类
 * Created by Administrator on 2016/5/12.
 */
public class RunningTimeDialog {
    public Dialog progressDialog;
    private ImageView ivLoding;

    public void runningTimeProgressDialog(Context context){
        progressDialog = new Dialog(context, R.style.CustomDialog);
        View view = LayoutInflater.from(context).inflate(R.layout.running_time_dialog,null);
        ivLoding = (ImageView) view.findViewById(R.id.iv_running_time);
        ObjectAnimator objectAnimator = new ObjectAnimator().ofFloat(ivLoding,"rotation",0f,-360f).setDuration(500);
        LinearInterpolator ll = new LinearInterpolator();    //线性动画（匀速）
        objectAnimator.setRepeatCount(-1);
        objectAnimator.setInterpolator(ll);
        objectAnimator.start();
        progressDialog.setContentView(view);
        WindowManager.LayoutParams params = progressDialog.getWindow().getAttributes();
        params.width = 400;
        //params.height = 800;   //设置dialog的宽高
        Window mWindow = progressDialog.getWindow();
        mWindow.setGravity(Gravity.CENTER);
        mWindow.setAttributes(params);
        progressDialog.show();
    }


    public ProgressDialog progressDialog1;

    public void runningTimeProgressDialog1(Context context){
        progressDialog1 = new ProgressDialog(context);
        progressDialog1.setTitle("Please wait...");
        progressDialog1.setMessage("Loading...");
        progressDialog1.setCancelable(true);
        progressDialog1.show();
    }
}
