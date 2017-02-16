package com.byids.hy.testpro.utils;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.byids.hy.testpro.R;
import com.byids.hy.testpro.utils.crouton.Configuration;
import com.byids.hy.testpro.utils.crouton.Crouton;

/**
 * Created by gqgz2 on 2017/2/6.
 *
 * ****************自定义的用Crouton实现的类似Toast的工具类****************
 *
 */

public class CustomCroutonUtil {
    private View customView = null;
    private Activity activity = null;
    private TextView tvCustomToast;

    public CustomCroutonUtil(Activity activity) {
        this.activity = activity;
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        customView = layoutInflater.inflate(R.layout.custom_toast_layout,null);
        tvCustomToast = (TextView) customView.findViewById(R.id.tv_custom_toast);
        Typeface typeface = Typeface.createFromAsset(activity.getAssets(), "fonts/xiyuanti.ttf");
        tvCustomToast.setTypeface(typeface);
    }

    public void makeTopToast(String toastContent,int duration){
        tvCustomToast.setText(toastContent);
        Configuration configuration = new Configuration.Builder().setDuration(duration).build();
        Crouton.make(activity,customView).setConfiguration(configuration).show();
    }

    //在控件下方显示
    public void makeViewGroupTopToast(String toastContent,int duration, ViewGroup viewGroup){
        tvCustomToast.setText(toastContent);
        Configuration configuration = new Configuration.Builder().setDuration(duration).build();
        Crouton.make(activity,customView,viewGroup).setConfiguration(configuration).show();
    }
}
