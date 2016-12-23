package com.byids.hy.testpro.activity.custom_scene_activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.byids.hy.testpro.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.byids.hy.testpro.R.id.rb_custom_light_channel_1_1;
import static com.byids.hy.testpro.R.id.rb_custom_light_channel_1_2;
import static com.byids.hy.testpro.R.id.rb_custom_light_channel_1_3;

/**
 * Created by gqgz2 on 2016/12/19.
 */

public class CustomSceneLightActivity extends Activity {


    @BindView(R.id.iv_custom_light_back)
    ImageView ivCustomLightBack;
    @BindView(R.id.tv_custom_light_title)
    TextView tvCustomLightTitle;
    @BindView(R.id.tv_custom_light_save)
    TextView tvCustomLightSave;
    @BindView(R.id.tv_custom_light_channel_all)
    TextView tvCustomLightChannelAll;
    @BindView(R.id.sw_custom_light_channel_all)
    Switch swCustomLightChannelAll;
    @BindView(R.id.rb_custom_light_channel_all_1)
    RadioButton rbCustomLightChannelAll1;
    @BindView(R.id.rb_custom_light_channel_all_2)
    RadioButton rbCustomLightChannelAll2;
    @BindView(R.id.rb_custom_light_channel_all_3)
    RadioButton rbCustomLightChannelAll3;
    @BindView(R.id.rg_custom_light_channel_all)
    RadioGroup rgCustomLightChannelAll;
    @BindView(R.id.tv_custom_light_channel_1)
    TextView tvCustomLightChannel1;
    @BindView(R.id.sw_custom_light_channel_1)
    Switch swCustomLightChannel1;
    @BindView(rb_custom_light_channel_1_1)
    RadioButton rbCustomLightChannel11;
    @BindView(rb_custom_light_channel_1_2)
    RadioButton rbCustomLightChannel12;
    @BindView(rb_custom_light_channel_1_3)
    RadioButton rbCustomLightChannel13;
    @BindView(R.id.rg_custom_light_channel_1)
    RadioGroup rgCustomLightChannel1;
    @BindView(R.id.tv_custom_light_channel_2)
    TextView tvCustomLightChannel2;
    @BindView(R.id.sw_custom_light_channel_2)
    Switch swCustomLightChannel2;
    @BindView(R.id.rb_custom_light_channel_2_1)
    RadioButton rbCustomLightChannel21;
    @BindView(R.id.rb_custom_light_channel_2_2)
    RadioButton rbCustomLightChannel22;
    @BindView(R.id.rb_custom_light_channel_2_3)
    RadioButton rbCustomLightChannel23;
    @BindView(R.id.rg_custom_light_channel_2)
    RadioGroup rgCustomLightChannel2;
    @BindView(R.id.tv_custom_light_channel_3)
    TextView tvCustomLightChannel3;
    @BindView(R.id.sw_custom_light_channel_3)
    Switch swCustomLightChannel3;
    @BindView(R.id.rb_custom_light_channel_3_1)
    RadioButton rbCustomLightChannel31;
    @BindView(R.id.rb_custom_light_channel_3_2)
    RadioButton rbCustomLightChannel32;
    @BindView(R.id.rb_custom_light_channel_3_3)
    RadioButton rbCustomLightChannel33;
    @BindView(R.id.rg_custom_light_channel_3)
    RadioGroup rgCustomLightChannel3;
    @BindView(R.id.tv_custom_light_channel_4)
    TextView tvCustomLightChannel4;
    @BindView(R.id.sw_custom_light_channel_4)
    Switch swCustomLightChannel4;
    @BindView(R.id.rb_custom_light_channel_4_1)
    RadioButton rbCustomLightChannel41;
    @BindView(R.id.rb_custom_light_channel_4_2)
    RadioButton rbCustomLightChannel42;
    @BindView(R.id.rb_custom_light_channel_4_3)
    RadioButton rbCustomLightChannel43;
    @BindView(R.id.rg_custom_light_channel_4)
    RadioGroup rgCustomLightChannel4;
    @BindView(R.id.tv_custom_light_channel_5)
    TextView tvCustomLightChannel5;
    @BindView(R.id.sw_custom_light_channel_5)
    Switch swCustomLightChannel5;
    @BindView(R.id.rb_custom_light_channel_5_1)
    RadioButton rbCustomLightChannel51;
    @BindView(R.id.rb_custom_light_channel_5_2)
    RadioButton rbCustomLightChannel52;
    @BindView(R.id.rb_custom_light_channel_5_3)
    RadioButton rbCustomLightChannel53;
    @BindView(R.id.rg_custom_light_channel_5)
    RadioGroup rgCustomLightChannel5;

    private Typeface typeFace;
    private List<TextView> tvList = new ArrayList<>();
    private boolean[] customLight = {false,false,false,false,false,false};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setTheme(R.style.SwitchColorStyle);
        setContentView(R.layout.custom_light_layout);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setTextType();
        swCustomLightChannelAll.setOnCheckedChangeListener(swCheckedChangeListener);
        swCustomLightChannel1.setOnCheckedChangeListener(swCheckedChangeListener);
        swCustomLightChannel2.setOnCheckedChangeListener(swCheckedChangeListener);
        swCustomLightChannel3.setOnCheckedChangeListener(swCheckedChangeListener);
        swCustomLightChannel4.setOnCheckedChangeListener(swCheckedChangeListener);
        swCustomLightChannel5.setOnCheckedChangeListener(swCheckedChangeListener);
    }

    @OnClick({R.id.iv_custom_light_back, R.id.tv_custom_light_save})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_custom_light_back:
                finish();
                break;
            case R.id.tv_custom_light_save:
                Intent intent = new Intent();
                //intent.putExtra()
                setResult(1,intent);
                finish();
                break;
        }
    }

    CompoundButton.OnCheckedChangeListener swCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()){
                case R.id.sw_custom_light_channel_all:
                    if (isChecked){
                        swCustomLightChannel1.setChecked(true);
                        swCustomLightChannel2.setChecked(true);
                        swCustomLightChannel3.setChecked(true);
                        swCustomLightChannel4.setChecked(true);
                        swCustomLightChannel5.setChecked(true);
                    }else {
                        swCustomLightChannel1.setChecked(false);
                        swCustomLightChannel2.setChecked(false);
                        swCustomLightChannel3.setChecked(false);
                        swCustomLightChannel4.setChecked(false);
                        swCustomLightChannel5.setChecked(false);
                    }
                    break;
                case R.id.sw_custom_light_channel_1:
                    if (isChecked){
                        rgCustomLightChannel1.setVisibility(View.VISIBLE);
                    }else {
                        rgCustomLightChannel1.setVisibility(View.GONE);
                    }
                    break;
                case R.id.sw_custom_light_channel_2:
                    if (isChecked){
                        rgCustomLightChannel2.setVisibility(View.VISIBLE);
                    }else {
                        rgCustomLightChannel2.setVisibility(View.GONE);
                    }
                    break;
                case R.id.sw_custom_light_channel_3:
                    if (isChecked){
                        rgCustomLightChannel3.setVisibility(View.VISIBLE);
                    }else {
                        rgCustomLightChannel3.setVisibility(View.GONE);
                    }
                    break;
                case R.id.sw_custom_light_channel_4:
                    if (isChecked){
                        rgCustomLightChannel4.setVisibility(View.VISIBLE);
                    }else {
                        rgCustomLightChannel4.setVisibility(View.GONE);
                    }
                    break;
                case R.id.sw_custom_light_channel_5:
                    if (isChecked){
                        rgCustomLightChannel5.setVisibility(View.VISIBLE);
                    }else {
                        rgCustomLightChannel5.setVisibility(View.GONE);
                    }
                    break;
            }
        }
    };


    //设置字体
    private void setTextType() {
        typeFace = Typeface.createFromAsset(getAssets(), "fonts/xiyuanti.ttf");
        tvList.add(tvCustomLightSave);
        tvList.add(tvCustomLightTitle);
        tvList.add(tvCustomLightChannelAll);
        tvList.add(tvCustomLightChannel1);
        tvList.add(tvCustomLightChannel2);
        tvList.add(tvCustomLightChannel3);
        tvList.add(tvCustomLightChannel4);
        tvList.add(tvCustomLightChannel5);
        for (int i = 0; i < tvList.size(); i++) {
            tvList.get(i).setTypeface(typeFace);
        }
    }

}
