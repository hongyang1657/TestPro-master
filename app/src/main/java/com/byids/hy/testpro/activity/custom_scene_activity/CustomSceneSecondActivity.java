package com.byids.hy.testpro.activity.custom_scene_activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.byids.hy.testpro.R;
import com.byids.hy.testpro.activity.BaseActivity;
import com.byids.hy.testpro.customSceneBean.AllCustomScene;
import com.byids.hy.testpro.customSceneBean.DetailCustomScene;
import com.byids.hy.testpro.customSceneBean.LightDetail;
import com.byids.hy.testpro.customSceneBean.RoomCustomScene;
import com.byids.hy.testpro.newBean.RoomDevMesg;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by gqgz2 on 2016/12/15.
 */

public class CustomSceneSecondActivity extends BaseActivity {

    @BindView(R.id.iv_custom_back_second)
    ImageView ivCustomBackSecond;
    @BindView(R.id.tv_custom_title_second)
    TextView tvCustomTitleSecond;
    @BindView(R.id.tv_custom_save_second)
    TextView tvCustomSaveSecond;
    @BindView(R.id.iv_custom_name)
    TextView ivCustomName;
    @BindView(R.id.ll_custom_second_name)
    LinearLayout llCustomSecondName;
    @BindView(R.id.tv_custom_icon)
    TextView tvCustomIcon;
    @BindView(R.id.ll_custom_second_icon)
    LinearLayout llCustomSecondIcon;
    @BindView(R.id.tv_custom_light)
    TextView tvCustomLight;
    @BindView(R.id.ll_custom_second_light)
    LinearLayout llCustomSecondLight;
    @BindView(R.id.tv_custom_curtain)
    TextView tvCustomCurtain;
    @BindView(R.id.ll_custom_second_curtain)
    LinearLayout llCustomSecondCurtain;
    @BindView(R.id.tv_custom_music)
    TextView tvCustomMusic;
    @BindView(R.id.ll_custom_second_music)
    LinearLayout llCustomSecondMusic;
    @BindView(R.id.tv_custom_television)
    TextView tvCustomTelevision;
    @BindView(R.id.ll_custom_second_television)
    LinearLayout llCustomSecondTelevision;
    @BindView(R.id.tv_custom_ac)
    TextView tvCustomAc;
    @BindView(R.id.ll_custom_second_ac)
    LinearLayout llCustomSecondAc;
    @BindView(R.id.sw_custom_curtain)
    Switch swCustomCurtain;
    @BindView(R.id.sw_custom_tv)
    Switch swCustomTv;
    @BindView(R.id.tv_custom_bianji1)
    TextView tvCustomBianji1;
    @BindView(R.id.tv_custom_bianji2)
    TextView tvCustomBianji2;
    @BindView(R.id.tv_custom_bianji3)
    TextView tvCustomBianji3;
    @BindView(R.id.tv_custom_bianji4)
    TextView tvCustomBianji4;
    @BindView(R.id.tv_custom_bianji5)
    TextView tvCustomBianji5;
    @BindView(R.id.iv_compile_done_1)
    ImageView ivCompileDone1;
    @BindView(R.id.iv_compile_done_2)
    ImageView ivCompileDone2;
    @BindView(R.id.iv_compile_done_3)
    ImageView ivCompileDone3;
    @BindView(R.id.iv_compile_done_4)
    ImageView ivCompileDone4;

    private int[] iconResListSelect = {R.mipmap.scene_custom_img_select_3x_1, R.mipmap.scene_custom_img_select_3x_2, R.mipmap.scene_custom_img_select_3x_3,
            R.mipmap.scene_custom_img_select_3x_4, R.mipmap.scene_custom_img_select_3x_5, R.mipmap.scene_custom_img_select_3x_6, R.mipmap.scene_custom_img_select_3x_7,
            R.mipmap.scene_custom_img_select_3x_8, R.mipmap.scene_custom_img_select_3x_9, R.mipmap.scene_custom_img_select_3x_10, R.mipmap.scene_custom_img_select_3x_11,
            R.mipmap.scene_custom_img_select_3x_12, R.mipmap.scene_custom_img_select_3x_13, R.mipmap.scene_custom_img_select_3x_14, R.mipmap.scene_custom_img_select_3x_15,
            R.mipmap.scene_custom_img_select_3x_16, R.mipmap.scene_custom_img_select_3x_17, R.mipmap.scene_custom_img_select_3x_18, R.mipmap.scene_custom_img_select_3x_19,
            R.mipmap.scene_custom_img_select_3x_20, R.mipmap.scene_custom_img_select_3x_21, R.mipmap.scene_custom_img_select_3x_22, R.mipmap.scene_custom_img_select_3x_23,
            R.mipmap.scene_custom_img_select_3x_24, R.mipmap.scene_custom_img_select_3x_25, R.mipmap.scene_custom_img_select_3x_26, R.mipmap.scene_custom_img_select_3x_27,};
    private static final int REQUEST_CODE_LIGHT = 1;
    private static final int REQUEST_CODE_ICON = 2;

    private TextView tvCustomNameTitle;
    private TextView tvCustomNameCancel;
    private TextView tvCustomNameEnter;
    private EditText etCustomName;


    private int sceneIndex;        //场景数字标识（1——4）
    private String roomDBName;       //房间名
    private String token;
    private String uname;
    private String pwd;
    private String protocol;
    private int roomIndex;
    private RoomDevMesg roomDevMesg;
    private Dialog dialogCustomName;
    private View viewDialog;
    private Typeface typeFace;
    private List<TextView> tvList = new ArrayList<>();
    private int width;     //屏幕宽高
    private int height;
    //场景的各个单品状态
    private String saveName;
    private int iconNum = 0;  //保存的自定义icon的下标
    private boolean isOpenCurtain = false;      //窗帘是否打开

    private LightDetail lightDetail;
    private DetailCustomScene detailCustomScene;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setTheme(R.style.SwitchColorStyle);
        setContentView(R.layout.custom_scene_second_layout);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dialogCustomName = null;
    }

    private void initView() {
        setTextType();      //设置字体
        //获取屏幕宽高
        WindowManager wm = this.getWindowManager();
        width = wm.getDefaultDisplay().getWidth();
        height = wm.getDefaultDisplay().getHeight();

        sceneIndex = getIntent().getIntExtra("index", 0);
        roomDBName = getIntent().getStringExtra("roomDBName");
        token = getIntent().getStringExtra("token");
        uname = getIntent().getStringExtra("uname");
        pwd = getIntent().getStringExtra("pwd");
        protocol = getIntent().getStringExtra("protocol");
        roomDevMesg = (RoomDevMesg) getIntent().getSerializableExtra("roomDevMesg");
        roomIndex = getIntent().getIntExtra("roomIndex",0);
        //detailCustomScene = (DetailCustomScene) getIntent().getSerializableExtra("detailCustomScene");
        //本地自定义场景的信息
        SharedPreferences sp = getSharedPreferences("customSceneJson",MODE_PRIVATE);
        String json = sp.getString("json","");
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        AllCustomScene allCustomScene = gson.fromJson(json,AllCustomScene.class);
        RoomCustomScene roomCustomScene = allCustomScene.getArray().get(roomIndex);
        //生成json看看
        Gson gson1 = new GsonBuilder().serializeNulls().create();
        String json1 = gson1.toJson(allCustomScene);
        detailCustomScene = roomCustomScene.getArray().get(sceneIndex-1);

        tvCustomTitleSecond.setText(detailCustomScene.getSceneName());
        saveName = detailCustomScene.getSceneName();
        iconNum = detailCustomScene.getSceneIconIndex();
        lightDetail = detailCustomScene.getLightDetail();
        swCustomCurtain.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isOpenCurtain = isChecked;
            }
        });
        swCustomCurtain.setChecked(detailCustomScene.isOpenCurtain());
    }

    //设置字体
    private void setTextType() {
        typeFace = Typeface.createFromAsset(getAssets(), "fonts/xiyuanti.ttf");
        tvList.add(tvCustomTitleSecond);
        tvList.add(tvCustomSaveSecond);
        tvList.add(ivCustomName);
        tvList.add(tvCustomIcon);
        tvList.add(tvCustomLight);
        tvList.add(tvCustomCurtain);
        tvList.add(tvCustomMusic);
        tvList.add(tvCustomTelevision);
        tvList.add(tvCustomAc);
        tvList.add(tvCustomBianji1);
        tvList.add(tvCustomBianji2);
        tvList.add(tvCustomBianji3);
        tvList.add(tvCustomBianji4);
        tvList.add(tvCustomBianji5);
        for (int i = 0; i < tvList.size(); i++) {
            tvList.get(i).setTypeface(typeFace);
        }
    }

    @OnClick({R.id.iv_custom_back_second, R.id.tv_custom_save_second, R.id.ll_custom_second_name, R.id.ll_custom_second_icon, R.id.ll_custom_second_light, R.id.ll_custom_second_music, R.id.ll_custom_second_ac})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_custom_back_second:
                Intent intent1 = new Intent();
                setResult(0,intent1);    //回复0，表示不改变自定义场景的信息
                finish();
                break;
            case R.id.tv_custom_save_second:
                SharedPreferences sharedPreferences = getSharedPreferences("judgeFirstSave",MODE_PRIVATE);
                sharedPreferences.edit().putBoolean("isFirstSave",true).commit();
                //本地自定义场景的信息
                List<DetailCustomScene> array = new ArrayList<>();
                DetailCustomScene dcs = new DetailCustomScene();
                dcs.setLightDetail(lightDetail);
                dcs.setSceneIconIndex(iconNum);
                dcs.setSceneName(saveName);
                dcs.setOpenCurtain(isOpenCurtain);

                SharedPreferences sp = getSharedPreferences("customSceneJson",MODE_PRIVATE);
                String json = sp.getString("json","");
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                AllCustomScene allCustomScene = gson.fromJson(json,AllCustomScene.class);
                RoomCustomScene roomCustomScene = allCustomScene.getArray().get(roomIndex);
                array = roomCustomScene.getArray();
                array.remove(sceneIndex-1);
                array.add(sceneIndex-1,dcs);
                roomCustomScene.setArray(array);
                Gson gson1 = new GsonBuilder().serializeNulls().create();
                String json1 = gson1.toJson(allCustomScene);
                SharedPreferences sp1 = getSharedPreferences("customSceneJson",MODE_PRIVATE);
                sp1.edit().putString("json",json1).commit();

                Intent intent = new Intent();
                intent.putExtra("iconNum",iconNum);
                intent.putExtra("sceneName",saveName);
                intent.putExtra("isOpenCurtain",isOpenCurtain);
                Bundle bundle1 = new Bundle();
                bundle1.putSerializable("lightDetail",lightDetail);
                intent.putExtras(bundle1);
                setResult(sceneIndex,intent);
                finish();
                break;
            case R.id.ll_custom_second_name:
                initCustomNameDialog();
                dialogCustomName.show();
                break;
            case R.id.ll_custom_second_icon:
                Intent intentToIcon = new Intent(this, CustomSceneSelectIconActivity.class);
                intentToIcon.putExtra("iconNum",iconNum);
                startActivityForResult(intentToIcon, REQUEST_CODE_ICON);
                break;
            case R.id.ll_custom_second_light:
                Intent intentToLight = new Intent(this, CustomSceneLightActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("roomDevMesg",roomDevMesg);
                bundle.putSerializable("detailCustomScene",detailCustomScene);
                intentToLight.putExtras(bundle);
                intentToLight.putExtra("roomDBName",roomDBName);
                intentToLight.putExtra("token",token);
                intentToLight.putExtra("uname",uname);
                intentToLight.putExtra("pwd",pwd);
                intentToLight.putExtra("protocol",protocol);
                startActivityForResult(intentToLight, REQUEST_CODE_LIGHT);
                break;
            case R.id.ll_custom_second_music:
                break;
            case R.id.ll_custom_second_ac:
                Intent intentToAc = new Intent(this,CustomSceneACActivity.class);
                startActivity(intentToAc);
                break;
        }
    }

    private void initCustomNameDialog() {
        dialogCustomName = new Dialog(this, R.style.CustomDialog);
        viewDialog = LayoutInflater.from(this).inflate(R.layout.custom_name_dialog_layout, null);
        tvCustomNameTitle = (TextView) viewDialog.findViewById(R.id.tv_custom_name_title);
        etCustomName = (EditText) viewDialog.findViewById(R.id.et_custom_name);
        tvCustomNameCancel = (TextView) viewDialog.findViewById(R.id.tv_custom_name_cancel);
        tvCustomNameEnter = (TextView) viewDialog.findViewById(R.id.tv_custom_name_enter);
        tvCustomNameTitle.setTypeface(typeFace);
        etCustomName.setTypeface(typeFace);
        tvCustomNameCancel.setTypeface(typeFace);
        tvCustomNameEnter.setTypeface(typeFace);
        tvCustomNameCancel.setOnClickListener(dialogListener);
        tvCustomNameEnter.setOnClickListener(dialogListener);
        etCustomName.setText(detailCustomScene.getSceneName());

        dialogCustomName.setContentView(viewDialog);
        dialogCustomName.setCanceledOnTouchOutside(false);//点击外部，弹框不消失
        WindowManager.LayoutParams params = dialogCustomName.getWindow().getAttributes();
        params.width = (int) (width * 0.7);
        params.height = (int) (height * 0.3);   //设置dialog的宽高
        Window mWindow = dialogCustomName.getWindow();
        mWindow.setGravity(Gravity.CENTER);
        mWindow.setAttributes(params);
    }

    View.OnClickListener dialogListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_custom_name_cancel:
                    dialogCustomName.hide();
                    break;
                case R.id.tv_custom_name_enter:
                    saveName = etCustomName.getText().toString().trim();
                    tvCustomTitleSecond.setText(saveName);
                    ivCompileDone1.setVisibility(View.VISIBLE);
                    //tvCustomBianji1.setVisibility(View.GONE);
                    dialogCustomName.hide();
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_LIGHT:
                if (data.getBooleanExtra("isSave",false)){
                    ivCompileDone3.setVisibility(View.VISIBLE);
                    //tvCustomBianji3.setVisibility(View.GONE);
                    lightDetail = (LightDetail) data.getSerializableExtra("lightDetail");

                }else {
                    //没保存
                }
                break;
            case REQUEST_CODE_ICON:
                if (data.getIntExtra("customIconNum",-1)!=-1){
                    //tvCustomBianji2.setVisibility(View.GONE);
                    ivCompileDone2.setVisibility(View.VISIBLE);
                    iconNum = data.getIntExtra("customIconNum",0);
                }else {
                    //没有保存
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==event.KEYCODE_BACK){
            Intent intent1 = new Intent();
            setResult(0,intent1);    //回复0，表示不改变自定义场景的信息
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
