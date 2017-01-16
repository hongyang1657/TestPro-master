package com.byids.hy.testpro.activity.custom_scene_activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.byids.hy.testpro.MyEventBus;
import com.byids.hy.testpro.MyEventBusCustom;
import com.byids.hy.testpro.R;
import com.byids.hy.testpro.customSceneBean.AllCustomScene;
import com.byids.hy.testpro.customSceneBean.DetailCustomScene;
import com.byids.hy.testpro.customSceneBean.LightDetail;
import com.byids.hy.testpro.customSceneBean.RoomCustomScene;
import com.byids.hy.testpro.newBean.RoomDevMesg;
import com.byids.hy.testpro.utils.CommandJsonUtils;
import com.byids.hy.testpro.utils.LongLogCatUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
    private int roomIndex;
    private RoomDevMesg roomDevMesg;

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

    private int[] iconResListSelect = {R.mipmap.scene_custom_img_select_3x_1, R.mipmap.scene_custom_img_select_3x_2, R.mipmap.scene_custom_img_select_3x_3,
            R.mipmap.scene_custom_img_select_3x_4, R.mipmap.scene_custom_img_select_3x_5, R.mipmap.scene_custom_img_select_3x_6, R.mipmap.scene_custom_img_select_3x_7,
            R.mipmap.scene_custom_img_select_3x_8, R.mipmap.scene_custom_img_select_3x_9, R.mipmap.scene_custom_img_select_3x_10, R.mipmap.scene_custom_img_select_3x_11,
            R.mipmap.scene_custom_img_select_3x_12, R.mipmap.scene_custom_img_select_3x_13, R.mipmap.scene_custom_img_select_3x_14, R.mipmap.scene_custom_img_select_3x_15,
            R.mipmap.scene_custom_img_select_3x_16, R.mipmap.scene_custom_img_select_3x_17, R.mipmap.scene_custom_img_select_3x_18, R.mipmap.scene_custom_img_select_3x_19,
            R.mipmap.scene_custom_img_select_3x_20, R.mipmap.scene_custom_img_select_3x_21, R.mipmap.scene_custom_img_select_3x_22, R.mipmap.scene_custom_img_select_3x_23,
            R.mipmap.scene_custom_img_select_3x_24, R.mipmap.scene_custom_img_select_3x_25, R.mipmap.scene_custom_img_select_3x_26, R.mipmap.scene_custom_img_select_3x_27,};
    private int[] iconResList = {R.mipmap.scene_custom_img_3x_1, R.mipmap.scene_custom_img_3x_2, R.mipmap.scene_custom_img_3x_3,
            R.mipmap.scene_custom_img_3x_4, R.mipmap.scene_custom_img_3x_5, R.mipmap.scene_custom_img_3x_6, R.mipmap.scene_custom_img_3x_7,
            R.mipmap.scene_custom_img_3x_8, R.mipmap.scene_custom_img_3x_9, R.mipmap.scene_custom_img_3x_10, R.mipmap.scene_custom_img_3x_11,
            R.mipmap.scene_custom_img_3x_12, R.mipmap.scene_custom_img_3x_13, R.mipmap.scene_custom_img_3x_14, R.mipmap.scene_custom_img_3x_15,
            R.mipmap.scene_custom_img_3x_16, R.mipmap.scene_custom_img_3x_17, R.mipmap.scene_custom_img_3x_18, R.mipmap.scene_custom_img_3x_19,
            R.mipmap.scene_custom_img_3x_20, R.mipmap.scene_custom_img_3x_21, R.mipmap.scene_custom_img_3x_22, R.mipmap.scene_custom_img_3x_23,
            R.mipmap.scene_custom_img_3x_24, R.mipmap.scene_custom_img_3x_25, R.mipmap.scene_custom_img_3x_26, R.mipmap.scene_custom_img_3x_27};
    private int sceneIconIndex_1 = 0;
    private int sceneIconIndex_2 = 1;
    private int sceneIconIndex_3 = 2;
    private int sceneIconIndex_4 = 3;
    private String sceneName_1 = "未定义";
    private String sceneName_2 = "未定义";
    private String sceneName_3 = "未定义";
    private String sceneName_4 = "未定义";
    private boolean isOpenCurtain_1 = false;
    private boolean isOpenCurtain_2 = false;
    private boolean isOpenCurtain_3 = false;
    private boolean isOpenCurtain_4 = false;
    private LightDetail lightDetail_1 = null;
    private LightDetail lightDetail_2 = null;
    private LightDetail lightDetail_3 = null;
    private LightDetail lightDetail_4 = null;

    private RoomCustomScene roomCustomScene;
    private List<DetailCustomScene> arrayList = new ArrayList<>();
    private DetailCustomScene detailCustomScene = new DetailCustomScene();
    private DetailCustomScene detailCustomScene1;
    private DetailCustomScene detailCustomScene2;
    private DetailCustomScene detailCustomScene3;
    private DetailCustomScene detailCustomScene4;

    private String token;
    private String uname;
    private String pwd;
    private String protocol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setContentView(R.layout.custom_scene_main_layout);
        //EventBus.getDefault().register(this);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //EventBus.getDefault().unregister(this);  //反注册EventBus
    }

    private void initView() {
        roomCustomScene = new RoomCustomScene();
        roomDBName = getIntent().getStringExtra("roomDBName");
        roomIndex = getIntent().getIntExtra("roomIndex",0);
        token = getIntent().getStringExtra("token");
        uname = getIntent().getStringExtra("uname");
        pwd = getIntent().getStringExtra("pwd");
        protocol = getIntent().getStringExtra("protocol");
        roomDevMesg = (RoomDevMesg) getIntent().getSerializableExtra("roomDevMesg");
        //本地自定义场景的信息
        SharedPreferences sp = getSharedPreferences("customSceneJson",MODE_PRIVATE);
        String json = sp.getString("json","");
        //生成json看看
        LongLogCatUtil.logE("bean_hy", "initView:json: "+json);
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        AllCustomScene allCustomScene = gson.fromJson(json,AllCustomScene.class);
        RoomCustomScene roomCustomScene = allCustomScene.getArray().get(roomIndex);

        //RoomCustomScene roomCustomScene = (RoomCustomScene) getIntent().getSerializableExtra("roomCustomScene");
        detailCustomScene1 = roomCustomScene.getArray().get(0);
        detailCustomScene2 = roomCustomScene.getArray().get(1);
        detailCustomScene3 = roomCustomScene.getArray().get(2);
        detailCustomScene4 = roomCustomScene.getArray().get(3);

        ivCustomIcon1.setImageResource(iconResList[detailCustomScene1.getSceneIconIndex()]);
        ivCustomIcon2.setImageResource(iconResList[detailCustomScene2.getSceneIconIndex()]);
        ivCustomIcon3.setImageResource(iconResList[detailCustomScene3.getSceneIconIndex()]);
        ivCustomIcon4.setImageResource(iconResList[detailCustomScene4.getSceneIconIndex()]);
        ivCustomModification1.setText(detailCustomScene1.getSceneName());
        ivCustomModification2.setText(detailCustomScene2.getSceneName());
        ivCustomModification3.setText(detailCustomScene3.getSceneName());
        ivCustomModification4.setText(detailCustomScene4.getSceneName());

        for (int i=0;i<4;i++){
            arrayList.add(i,detailCustomScene);
        }
        intentToSecond = new Intent(CustomSceneMainActivity.this,CustomSceneSecondActivity.class);
        intentToSecond.putExtra("roomDBName",roomDBName);
        intentToSecond.putExtra("token",token);
        intentToSecond.putExtra("uname",uname);
        intentToSecond.putExtra("pwd",pwd);
        intentToSecond.putExtra("protocol",protocol);
        Bundle bundle = new Bundle();
        bundle.putSerializable("roomDevMesg",roomDevMesg);
        intentToSecond.putExtras(bundle);
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
                Intent intent = new Intent();
                intent.putExtra("sceneIconIndex1",sceneIconIndex_1);
                intent.putExtra("sceneIconIndex2",sceneIconIndex_2);
                intent.putExtra("sceneIconIndex3",sceneIconIndex_3);
                intent.putExtra("sceneIconIndex4",sceneIconIndex_4);
                intent.putExtra("sceneName1",sceneName_1);
                intent.putExtra("sceneName2",sceneName_2);
                intent.putExtra("sceneName3",sceneName_3);
                intent.putExtra("sceneName4",sceneName_4);
                intent.putExtra("isOpenCurtain1",isOpenCurtain_1);
                intent.putExtra("isOpenCurtain2",isOpenCurtain_2);
                intent.putExtra("isOpenCurtain3",isOpenCurtain_3);
                intent.putExtra("isOpenCurtain4",isOpenCurtain_4);
                Bundle bundle = new Bundle();
                bundle.putSerializable("roomCustomScene",roomCustomScene);
                intent.putExtras(bundle);
                setResult(1,intent);
                finish();
                break;
            case R.id.ll_custom1:
                EventBus.getDefault().post(new MyEventBusCustom("1"));  //发送自定义场景命令
                intentToSecond.putExtra("index",1);
                intentToSecond.putExtra("roomIndex",roomIndex);
                Bundle bundle1 = new Bundle();
                bundle1.putSerializable("detailCustomScene",detailCustomScene1);
                intentToSecond.putExtras(bundle1);
                startActivityForResult(intentToSecond,1);
                break;
            case R.id.ll_custom2:
                EventBus.getDefault().post(new MyEventBusCustom("2"));  //发送自定义场景命令
                intentToSecond.putExtra("index",2);
                intentToSecond.putExtra("roomIndex",roomIndex);
                Bundle bundle2 = new Bundle();
                bundle2.putSerializable("detailCustomScene",detailCustomScene2);
                intentToSecond.putExtras(bundle2);
                startActivityForResult(intentToSecond,2);
                break;
            case R.id.ll_custom3:
                EventBus.getDefault().post(new MyEventBusCustom("3"));  //发送自定义场景命令
                intentToSecond.putExtra("index",3);
                intentToSecond.putExtra("roomIndex",roomIndex);
                Bundle bundle3 = new Bundle();
                bundle3.putSerializable("detailCustomScene",detailCustomScene3);
                intentToSecond.putExtras(bundle3);
                startActivityForResult(intentToSecond,3);
                break;
            case R.id.ll_custom4:
                EventBus.getDefault().post(new MyEventBusCustom("4"));  //发送自定义场景命令
                intentToSecond.putExtra("index",4);
                intentToSecond.putExtra("roomIndex",roomIndex);
                Bundle bundle4 = new Bundle();
                bundle4.putSerializable("detailCustomScene",detailCustomScene4);
                intentToSecond.putExtras(bundle4);
                startActivityForResult(intentToSecond,4);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode){
            case 0:
                //没保存，自定义场景信息不变
                break;
            case 1:
                sceneIconIndex_1 = data.getIntExtra("iconNum",0);
                sceneName_1 = data.getStringExtra("sceneName");
                isOpenCurtain_1 = data.getBooleanExtra("isOpenCurtain",false);
                lightDetail_1 = (LightDetail) data.getSerializableExtra("lightDetail");
                //自定义场景存对象
                DetailCustomScene detailCustomScene1 = new DetailCustomScene();
                detailCustomScene1.setSceneName(sceneName_1);
                detailCustomScene1.setSceneIconIndex(sceneIconIndex_1);
                detailCustomScene1.setOpenCurtain(isOpenCurtain_1);
                detailCustomScene1.setLightDetail(lightDetail_1);
                arrayList.remove(0);
                arrayList.add(0,detailCustomScene1);

                ivCustomIcon1.setImageResource(iconResListSelect[sceneIconIndex_1]);
                ivCustomModification1.setText(sceneName_1);
                break;
            case 2:
                sceneIconIndex_2 = data.getIntExtra("iconNum",1);
                sceneName_2 = data.getStringExtra("sceneName");
                isOpenCurtain_2 = data.getBooleanExtra("isOpenCurtain",false);
                lightDetail_2 = (LightDetail) data.getSerializableExtra("lightDetail");
                //自定义场景存对象
                DetailCustomScene detailCustomScene2 = new DetailCustomScene();
                detailCustomScene2.setSceneName(sceneName_2);
                detailCustomScene2.setSceneIconIndex(sceneIconIndex_2);
                detailCustomScene2.setOpenCurtain(isOpenCurtain_2);
                detailCustomScene2.setLightDetail(lightDetail_2);
                arrayList.remove(1);
                arrayList.add(1,detailCustomScene2);

                ivCustomIcon2.setImageResource(iconResListSelect[sceneIconIndex_2]);
                ivCustomModification2.setText(sceneName_2);
                break;
            case 3:
                sceneIconIndex_3 = data.getIntExtra("iconNum",2);
                sceneName_3 = data.getStringExtra("sceneName");
                isOpenCurtain_3 = data.getBooleanExtra("isOpenCurtain",false);
                lightDetail_3 = (LightDetail) data.getSerializableExtra("lightDetail");
                //自定义场景存对象
                DetailCustomScene detailCustomScene3 = new DetailCustomScene();
                detailCustomScene3.setSceneName(sceneName_3);
                detailCustomScene3.setSceneIconIndex(sceneIconIndex_3);
                detailCustomScene3.setOpenCurtain(isOpenCurtain_3);
                detailCustomScene3.setLightDetail(lightDetail_3);
                arrayList.remove(2);
                arrayList.add(2,detailCustomScene3);

                ivCustomIcon3.setImageResource(iconResListSelect[sceneIconIndex_3]);
                ivCustomModification3.setText(sceneName_3);
                break;
            case 4:
                sceneIconIndex_4 = data.getIntExtra("iconNum",3);
                sceneName_4 = data.getStringExtra("sceneName");
                isOpenCurtain_4 = data.getBooleanExtra("isOpenCurtain",false);
                lightDetail_4 = (LightDetail) data.getSerializableExtra("lightDetail");
                //自定义场景存对象
                DetailCustomScene detailCustomScene4 = new DetailCustomScene();
                detailCustomScene4.setSceneName(sceneName_4);
                detailCustomScene4.setSceneIconIndex(sceneIconIndex_4);
                detailCustomScene4.setOpenCurtain(isOpenCurtain_4);
                detailCustomScene4.setLightDetail(lightDetail_4);
                arrayList.remove(3);
                arrayList.add(3,detailCustomScene4);

                ivCustomIcon4.setImageResource(iconResListSelect[sceneIconIndex_4]);
                ivCustomModification4.setText(sceneName_4);
                break;
            default:
                break;
        }
        roomCustomScene.setRoomDBName(roomDBName);
        roomCustomScene.setRoomIndex(roomIndex);
        roomCustomScene.setArray(arrayList);
    }

    //控制命令
    private void controlLight(String controlProtocol, String machineName, String value, String isServerAUTO, String controlSence) {
        JSONObject CommandData = new JSONObject();
        JSONObject controlData = new JSONObject();
        try {
            CommandData.put("controlProtocol", controlProtocol);
            CommandData.put("machineName", machineName);
            CommandData.put("controlData", controlData);
            controlData.put("lightValue", value);
            controlData.put("isServerAUTO", isServerAUTO);
            CommandData.put("controlSence", controlSence);
            CommandData.put("houseDBName", roomDBName);
            String lightJson = CommandJsonUtils.getCommandJson(0, CommandData, token, uname, pwd, String.valueOf(System.currentTimeMillis()));
            EventBus.getDefault().post(new MyEventBus(lightJson));
            Log.i("result", "onClick: ------lightjson------" + lightJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void controlCurtain(String controlProtocol, String machineName, int pauseStauts,int salianStauts,int bulianStatus, String controlSence){
        JSONObject CommandData = new JSONObject();
        JSONObject controlData = new JSONObject();
        try {
            CommandData.put("controlProtocol", controlProtocol);
            CommandData.put("machineName", machineName);
            CommandData.put("controlData", controlData);
            controlData.put("pauseStauts", pauseStauts);
            controlData.put("salianStauts", salianStauts);
            controlData.put("bulianStatus", bulianStatus);
            CommandData.put("controlSence", controlSence);
            CommandData.put("houseDBName", roomDBName);
            String curtainJson = CommandJsonUtils.getCommandJson(0, CommandData, token, uname, pwd, String.valueOf(System.currentTimeMillis()));
            EventBus.getDefault().post(new MyEventBus(curtainJson));
            Log.i("result", "onClick: ------curtainjson------" + curtainJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==event.KEYCODE_BACK){
            Intent intent = new Intent();
            intent.putExtra("sceneIconIndex1",sceneIconIndex_1);
            intent.putExtra("sceneIconIndex2",sceneIconIndex_2);
            intent.putExtra("sceneIconIndex3",sceneIconIndex_3);
            intent.putExtra("sceneIconIndex4",sceneIconIndex_4);
            intent.putExtra("sceneName1",sceneName_1);
            intent.putExtra("sceneName2",sceneName_2);
            intent.putExtra("sceneName3",sceneName_3);
            intent.putExtra("sceneName4",sceneName_4);
            intent.putExtra("isOpenCurtain1",isOpenCurtain_1);
            intent.putExtra("isOpenCurtain2",isOpenCurtain_2);
            intent.putExtra("isOpenCurtain3",isOpenCurtain_3);
            intent.putExtra("isOpenCurtain4",isOpenCurtain_4);
            Bundle bundle = new Bundle();
            bundle.putSerializable("roomCustomScene",roomCustomScene);
            intent.putExtras(bundle);
            setResult(1,intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
