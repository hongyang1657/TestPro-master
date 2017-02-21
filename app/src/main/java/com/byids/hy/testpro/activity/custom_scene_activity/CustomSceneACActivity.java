package com.byids.hy.testpro.activity.custom_scene_activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.byids.hy.testpro.R;
import com.byids.hy.testpro.activity.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by gqgz2 on 2017/1/17.
 */

public class CustomSceneACActivity extends BaseActivity {

    @BindView(R.id.iv_custom_ac_back)
    ImageView ivCustomAcBack;
    @BindView(R.id.tv_custom_ac_title)
    TextView tvCustomAcTitle;
    @BindView(R.id.tv_custom_ac_save)
    TextView tvCustomAcSave;
    @BindView(R.id.sw_custom_ac)
    Switch swCustomAc;
    @BindView(R.id.rb_custom_ac_auto)
    RadioButton rbCustomAcAuto;
    @BindView(R.id.rb_custom_ac_hot)
    RadioButton rbCustomAcHot;
    @BindView(R.id.rb_custom_ac_cold)
    RadioButton rbCustomAcCold;
    @BindView(R.id.rg_custom_ac)
    RadioGroup rgCustomAc;
    @BindView(R.id.tv_custom_ac_minus)
    TextView tvCustomAcMinus;
    @BindView(R.id.tv_custom_ac_temp)
    TextView tvCustomAcTemp;
    @BindView(R.id.tv_custom_ac_plus)
    TextView tvCustomAcPlus;
    @BindView(R.id.tv_custom_ac_kaiguan)
    TextView tvCustomAcKaiguan;
    @BindView(R.id.rl_custom_ac_temp)
    RelativeLayout rlCustomAcTemp;

    private Typeface typeFace;
    private boolean isACOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setTheme(R.style.SwitchColorStyle);
        setContentView(R.layout.custom_scene_ac_layout);
        ButterKnife.bind(this);
        initView();
    }

    @OnClick({R.id.sw_custom_ac, R.id.iv_custom_ac_back, R.id.tv_custom_ac_save, R.id.rb_custom_ac_auto, R.id.rb_custom_ac_hot, R.id.rb_custom_ac_cold, R.id.tv_custom_ac_minus, R.id.tv_custom_ac_plus})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_custom_ac_back:
                Intent intent = new Intent();
                intent.putExtra("customIconNum",-1);
                setResult(1,intent);
                finish();
                break;
            case R.id.tv_custom_ac_save:
                Intent intent1 = new Intent();
                intent1.putExtra("customIconNum",1);
                setResult(1,intent1);
                finish();
                break;
            case R.id.rb_custom_ac_auto:
                swCustomAc.setChecked(true);
                isACOn = true;
                break;
            case R.id.rb_custom_ac_hot:
                swCustomAc.setChecked(true);
                isACOn = true;
                break;
            case R.id.rb_custom_ac_cold:
                swCustomAc.setChecked(true);
                isACOn = true;
                break;
            case R.id.tv_custom_ac_minus:
                int temp = Integer.parseInt(tvCustomAcTemp.getText().toString());
                if (temp != 16) {
                    temp--;
                    tvCustomAcTemp.setText(""+temp);
                }
                break;
            case R.id.tv_custom_ac_plus:
                int temp1 = Integer.parseInt(tvCustomAcTemp.getText().toString());
                if (temp1 != 30) {
                    temp1++;
                    tvCustomAcTemp.setText(""+temp1);
                }
                break;
            case R.id.sw_custom_ac:
                if (isACOn) {
                    isACOn = false;
                    rgCustomAc.clearCheck();
                } else {
                    isACOn = true;
                    rbCustomAcAuto.setChecked(true);
                }
                break;
        }
    }

    private void initView() {
        typeFace = Typeface.createFromAsset(getAssets(), "fonts/xiyuanti.ttf");
        tvCustomAcTitle.setTypeface(typeFace);
        tvCustomAcSave.setTypeface(typeFace);
        tvCustomAcTemp.setTypeface(typeFace);
        tvCustomAcMinus.setTypeface(typeFace);
        tvCustomAcPlus.setTypeface(typeFace);
        tvCustomAcKaiguan.setTypeface(typeFace);
        rbCustomAcAuto.setTypeface(typeFace);
        rbCustomAcCold.setTypeface(typeFace);
        rbCustomAcHot.setTypeface(typeFace);

        WindowManager wm = this.getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        Log.i("ac_hy", "initView: width" + width);
        ViewGroup.LayoutParams layoutParams = rlCustomAcTemp.getLayoutParams();
        layoutParams.height = width / 3;
        rlCustomAcTemp.setLayoutParams(layoutParams);
    }


}
