package com.byids.hy.testpro.activity.custom_scene_activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.byids.hy.testpro.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by gqgz2 on 2016/12/14.
 */

public class CustomSceneMainActivity extends Activity {

    private Typeface typeFace;
    private Intent intentToSecond;
    private String roomDBName;

    @BindView(R.id.iv_custom_back)
    ImageView ivCustomBack;
    @BindView(R.id.tv_custom_title)
    TextView tvCustomTitle;
    @BindView(R.id.tv_custom_save)
    TextView tvCustomSave;
    @BindView(R.id.iv_custom_icon1)
    ImageView ivCustomIcon1;
    @BindView(R.id.iv_custom_modification1)
    TextView ivCustomModification1;
    @BindView(R.id.ll_custom1)
    LinearLayout llCustom1;
    @BindView(R.id.iv_custom_icon2)
    ImageView ivCustomIcon2;
    @BindView(R.id.iv_custom_modification2)
    TextView ivCustomModification2;
    @BindView(R.id.ll_custom2)
    LinearLayout llCustom2;
    @BindView(R.id.iv_custom_icon3)
    ImageView ivCustomIcon3;
    @BindView(R.id.iv_custom_modification3)
    TextView ivCustomModification3;
    @BindView(R.id.ll_custom3)
    LinearLayout llCustom3;
    @BindView(R.id.iv_custom_icon4)
    ImageView ivCustomIcon4;
    @BindView(R.id.iv_custom_modification4)
    TextView ivCustomModification4;
    @BindView(R.id.ll_custom4)
    LinearLayout llCustom4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setContentView(R.layout.custom_scene_main_layout);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        roomDBName = getIntent().getStringExtra("roomDBName");

        intentToSecond = new Intent(CustomSceneMainActivity.this,CustomSceneSecondActivity.class);
        typeFace = Typeface.createFromAsset(getAssets(),"fonts/xiyuanti.ttf");
        tvCustomSave.setTypeface(typeFace);
        tvCustomTitle.setTypeface(typeFace);
        ivCustomModification1.setTypeface(typeFace);
        ivCustomModification2.setTypeface(typeFace);
        ivCustomModification3.setTypeface(typeFace);
        ivCustomModification4.setTypeface(typeFace);
    }

    @OnClick({R.id.iv_custom_back, R.id.tv_custom_save, R.id.ll_custom1, R.id.ll_custom2, R.id.ll_custom3, R.id.ll_custom4})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_custom_back:
                finish();
                break;
            case R.id.tv_custom_save:
                break;
            case R.id.ll_custom1:
                Toast.makeText(this, "1", Toast.LENGTH_SHORT).show();
                intentToSecond.putExtra("roomDBName",roomDBName);
                intentToSecond.putExtra("index",1);
                startActivity(intentToSecond);
                break;
            case R.id.ll_custom2:
                Toast.makeText(this, "2", Toast.LENGTH_SHORT).show();
                intentToSecond.putExtra("roomDBName",roomDBName);
                intentToSecond.putExtra("index",2);
                startActivity(intentToSecond);
                break;
            case R.id.ll_custom3:
                Toast.makeText(this, "3", Toast.LENGTH_SHORT).show();
                intentToSecond.putExtra("roomDBName",roomDBName);
                intentToSecond.putExtra("index",3);
                startActivity(intentToSecond);
                break;
            case R.id.ll_custom4:
                Toast.makeText(this, "4", Toast.LENGTH_SHORT).show();
                intentToSecond.putExtra("roomDBName",roomDBName);
                intentToSecond.putExtra("index",4);
                startActivity(intentToSecond);
                break;
        }
    }
}
