package com.byids.hy.testpro.activity.custom_scene_activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.byids.hy.testpro.MyEventBus;
import com.byids.hy.testpro.R;
import com.byids.hy.testpro.View.MyEventBusLight;
import com.byids.hy.testpro.adapter.CustomLightBaseAdapter;
import com.byids.hy.testpro.customSceneBean.DetailCustomScene;
import com.byids.hy.testpro.customSceneBean.LightDetail;
import com.byids.hy.testpro.customSceneBean.LoopLight;
import com.byids.hy.testpro.newBean.RoomDevMesg;
import com.byids.hy.testpro.utils.CommandJsonUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
    @BindView(R.id.lv_custom_light)
    ListView lvCustomLight;
    @BindView(R.id.ll_custom_light_title)
    LinearLayout llCustomLightTitle;

    private String roomDBName;       //房间名
    private String token;
    private String uname;
    private String pwd;
    private String protocol;

    private Typeface typeFace;
    private List<TextView> tvList = new ArrayList<>();
    private boolean[] customLight = {false, false, false, false, false, false};
    private String[] lightLoopNameList;
    private CustomLightBaseAdapter adapter;
    private RoomDevMesg roomDevMesg;
    private int loopNum;
    private boolean isSWChecked = false;
    private boolean isCloseAllLight;        //判断是否关掉所有灯
    private LightDetail lightDetail;
    private LoopLight loopLight = new LoopLight();
    private List<LoopLight> loopLightArray = new ArrayList<>();
    private DetailCustomScene detailCustomScene;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setTheme(R.style.SwitchColorStyle);
        setContentView(R.layout.custom_light_layout);
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);  //反注册EventBus
    }

    private void initView() {
        roomDBName = getIntent().getStringExtra("roomDBName");
        token = getIntent().getStringExtra("token");
        uname = getIntent().getStringExtra("uname");
        pwd = getIntent().getStringExtra("pwd");
        protocol = getIntent().getStringExtra("protocol");
        lightDetail = new LightDetail();
        //初始化list
        for (int i=0;i<6;i++){
            lightValueList.add(i,-100);
            loopLight.setLoopLightIndex(i);
            loopLight.setLoopLightValue(0);
            loopLightArray.add(i,loopLight);
        }

        WindowManager wm = this.getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        ViewGroup.LayoutParams params = llCustomLightTitle.getLayoutParams();
        params.height = (int) (height*0.08);       //
        params.width = width;
        llCustomLightTitle.setLayoutParams(params);

        roomDevMesg = (RoomDevMesg) getIntent().getSerializableExtra("roomDevMesg");
        loopNum = roomDevMesg.getLight().getArray().size();
        lightLoopNameList = new String[loopNum];

        for (int i = 0; i < loopNum; i++) {
            lightLoopNameList[i] = roomDevMesg.getLight().getArray().get(i).getSoftware_mesg().getDisplay_name();
        }
        setTextType();
        rbCustomLightChannelAll1.setOnClickListener(rbClickListener);
        rbCustomLightChannelAll2.setOnClickListener(rbClickListener);
        rbCustomLightChannelAll3.setOnClickListener(rbClickListener);
        adapter = new CustomLightBaseAdapter(this, lightLoopNameList);
        lvCustomLight.setAdapter(adapter);

        //传入设定好的灯光的值
        detailCustomScene = (DetailCustomScene) getIntent().getSerializableExtra("detailCustomScene");
        int allLight = detailCustomScene.getLightDetail().getAllLight();
        switch (allLight){
            case 0:         //allLight亮度值设为0
                //初始状态，不改变界面
                break;
            case 1:         //allLight亮度值设为1
                adapter.turnAllSwitch(true);
                currentRbNum = 1;
                swCustomLightChannelAll.setChecked(true);
                isSWChecked = true;
                rbCustomLightChannelAll1.setChecked(true);
                adapter.turnAllLightValue(1);
                break;
            case 2:         //allLight亮度值设为2
                adapter.turnAllSwitch(true);
                currentRbNum = 2;
                swCustomLightChannelAll.setChecked(true);
                isSWChecked = true;
                rbCustomLightChannelAll2.setChecked(true);
                adapter.turnAllLightValue(2);
                break;
            case 3:         //allLight亮度值设为3
                adapter.turnAllSwitch(true);
                currentRbNum = 3;
                swCustomLightChannelAll.setChecked(true);
                isSWChecked = true;
                rbCustomLightChannelAll3.setChecked(true);
                adapter.turnAllLightValue(3);
                break;
            case -1:        //各回路亮度值不同
                int[] lightValueList = new int[loopNum];
                for (int i=0;i<loopNum;i++){
                    lightValueList[i] = detailCustomScene.getLightDetail().getArray().get(i).getLoopLightValue();
                    Log.i("light_hy", "initView: lightValue:"+lightValueList[i]);
                }
                adapter.turnLoopLightValue(lightValueList);
                break;
            case -2:        //初始状态，不做改变
                break;
            default:
                break;
        }
        Log.i("light_hy", "initView: allLight:"+allLight);

        //---------switch点击事件-----------
        swCustomLightChannelAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rgCustomLightChannelAll.clearCheck();
                if (!isSWChecked){
                    isSWChecked = true;
                    isCloseAllLight = false;
                    adapter.turnAllSwitch(true);
                    rbCustomLightChannelAll1.setChecked(true);
                    /*switch (currentRbNum){
                        case 1:
                            rbCustomLightChannelAll1.setChecked(true);
                            break;
                        case 2:
                            rbCustomLightChannelAll2.setChecked(true);
                            break;
                        case 3:
                            rbCustomLightChannelAll3.setChecked(true);
                            break;
                    }*/
                }else {
                    isSWChecked = false;
                    isCloseAllLight = true;
                    //各回路灯光全关
                    adapter.turnAllSwitch(false);
                    rgCustomLightChannelAll.clearCheck();
                }
            }
        });
        //--------switch状态监听--------
        /*swCustomLightChannelAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //rgCustomLightChannelAll.clearCheck();
                if (isChecked) {
                    //各回路灯光全开
                    isSWChecked = true;
                    *//*adapter.turnAllSwitch(true);
                    switch (currentRbNum){
                        case 1:
                            rbCustomLightChannelAll1.setChecked(true);
                            break;
                        case 2:
                            rbCustomLightChannelAll2.setChecked(true);
                            break;
                        case 3:
                            rbCustomLightChannelAll3.setChecked(true);
                            break;
                    }*//*

                } else {
                    //rgCustomLightChannelAll.clearCheck();
                    //各回路灯光全关
                    *//*adapter.turnAllSwitch(false);
                    rgCustomLightChannelAll.clearCheck();*//*
                    isSWChecked = false;
                }
            }
        });*/
        /*rgCustomLightChannelAll.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_custom_light_channel_all_1:
                        break;
                    case R.id.rb_custom_light_channel_all_2:
                        break;
                    case R.id.rb_custom_light_channel_all_3:
                        break;
                }
            }
        });*/

    }

    @OnClick({R.id.iv_custom_light_back, R.id.tv_custom_light_save})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_custom_light_back:
                Intent intent = new Intent();
                intent.putExtra("isSave",false);
                setResult(1, intent);
                finish();
                break;
            case R.id.tv_custom_light_save:
                saveLightValue();
                Intent intent1 = new Intent();
                intent1.putExtra("isSave",true);
                Bundle bundle = new Bundle();
                bundle.putSerializable("lightDetail",lightDetail);
                intent1.putExtras(bundle);
                setResult(1, intent1);
                finish();
                break;
        }
    }

    //保存各回路灯光亮度值
    private void saveLightValue(){
        if (isSWChecked){
            switch (rgCustomLightChannelAll.getCheckedRadioButtonId()){
                case R.id.rb_custom_light_channel_all_1:
                    lightDetail.setAllLight(1);
                    Log.i("light_hy", "saveLightValue: "+lightDetail.getAllLight());
                    break;
                case R.id.rb_custom_light_channel_all_2:
                    lightDetail.setAllLight(2);
                    break;
                case R.id.rb_custom_light_channel_all_3:
                    lightDetail.setAllLight(3);
                    break;
                default:
                    lightDetail.setAllLight(-1);    //表示不同回路灯光亮度不同
                    break;
            }
        }else {
            lightDetail.setAllLight(-1);
        }
        if (loopLight!=null){
            lightDetail.setArray(loopLightArray);
        }
        if (isCloseAllLight){
            lightDetail.setAllLight(0);
        }
    }

    private int currentRbNum = 1;
    View.OnClickListener rbClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.rb_custom_light_channel_all_1:
                    controlLight(protocol,"light","30","0","all");
                    adapter.turnAllSwitch(true);
                    currentRbNum = 1;
                    swCustomLightChannelAll.setChecked(true);
                    isSWChecked = true;
                    rbCustomLightChannelAll1.setChecked(true);
                    adapter.turnAllLightValue(1);
                    break;
                case R.id.rb_custom_light_channel_all_2:
                    controlLight(protocol,"light","60","0","all");
                    adapter.turnAllSwitch(true);
                    currentRbNum = 2;
                    swCustomLightChannelAll.setChecked(true);
                    isSWChecked = true;
                    rbCustomLightChannelAll2.setChecked(true);
                    adapter.turnAllLightValue(2);
                    break;
                case R.id.rb_custom_light_channel_all_3:
                    controlLight(protocol,"light","100","0","all");
                    adapter.turnAllSwitch(true);
                    currentRbNum = 3;
                    swCustomLightChannelAll.setChecked(true);
                    isSWChecked = true;
                    rbCustomLightChannelAll3.setChecked(true);
                    adapter.turnAllLightValue(3);
                    break;
            }
        }
    };


    int position;
    int value;
    int length;
    int loopLength = 0;
    int lightValue1 = 0;
    int lightValue2 = 0;
    int lightValue3 = 0;
    int lightValue4 = 0;
    int lightValue5 = 0;
    int lightValue6 = 0;
    List<Integer> lightValueList = new ArrayList<>(6);

    @Subscribe
    public void onEventMainThread(MyEventBusLight event) {
        position = event.getPosition();
        value = event.getMsg2();
        length = event.getLength();
        Log.i("light_hy", "onEventMainThread,position: "+position+"----value:"+value+"------length:"+length);

        switch (position){
            case 0:
                lightValue1 = value;
                setLoopLightBean(value,position);
                lightValueList.remove(position);
                lightValueList.add(position,value);
                break;
            case 1:
                lightValue2 = value;
                setLoopLightBean(value,position);
                lightValueList.remove(position);
                lightValueList.add(position,value);
                break;
            case 2:
                lightValue3 = value;
                setLoopLightBean(value,position);
                lightValueList.remove(position);
                lightValueList.add(position,value);
                break;
            case 3:
                lightValue4 = value;
                setLoopLightBean(value,position);
                lightValueList.remove(position);
                lightValueList.add(position,value);
                break;
            case 4:
                lightValue5 = value;
                setLoopLightBean(value,position);
                lightValueList.remove(position);
                lightValueList.add(position,value);
                break;
            case 5:
                lightValue6 = value;
                setLoopLightBean(value,position);
                lightValueList.remove(position);
                lightValueList.add(position,value);
                break;
            default:
                break;
        }
        for (int i=0;i<lightValueList.size();i++){
            Log.i("light_hy", "onEventMainThread:lightValueList: "+lightValueList.get(i));
        }

        Log.i("light_hy", "---lightValue1-6:"+lightValue1+"---:"+lightValue2+"---:"+lightValue3+"---:"+lightValue4+"---:"+lightValue5+"---:"+lightValue6);
        if ((lightValue1==1||lightValue1==0)&&(lightValue2==1||lightValue2==0)&&(lightValue3==1||lightValue3==0)&&(lightValue4==1||lightValue4==0)&&
                (lightValue5==1||lightValue5==0)&&(lightValue6==1||lightValue6==0)&&isSWChecked){
            Log.i("light_hy", "onEventMainThread: 微弱");
            rbCustomLightChannelAll1.setChecked(true);
        }else if ((lightValue1==2||lightValue1==0)&&(lightValue2==2||lightValue2==0)&&(lightValue3==2||lightValue3==0)&&(lightValue4==2||lightValue4==0)&&
                (lightValue5==2||lightValue5==0)&&(lightValue6==2||lightValue6==0)&&isSWChecked){
            Log.i("light_hy", "onEventMainThread: 舒适");
            rbCustomLightChannelAll2.setChecked(true);
        }else if ((lightValue1==3||lightValue1==0)&&(lightValue2==3||lightValue2==0)&&(lightValue3==3||lightValue3==0)&&(lightValue4==3||lightValue4==0)&&
                (lightValue5==3||lightValue5==0)&&(lightValue6==3||lightValue6==0)&&isSWChecked){
            Log.i("light_hy", "onEventMainThread: 明亮");
            rbCustomLightChannelAll3.setChecked(true);
        }else if (value==10){
            loopLength--;
        }else if (value==11){
            loopLength++;
        } else {
            rgCustomLightChannelAll.clearCheck();
        }


        if (loopLength==0){
            swCustomLightChannelAll.setChecked(false);
            isSWChecked = false;
        }else if (loopLength==length){
            swCustomLightChannelAll.setChecked(true);
            isSWChecked = true;
        }
    }

    private void setLoopLightBean(int value,int position){
        loopLight = new LoopLight();
        loopLight.setLoopLightIndex(position);
        loopLight.setLoopLightName(lightLoopNameList[position]);
        if (value==10){
            loopLight.setLoopLightValue(0);
        }else if (value==11){
            loopLight.setLoopLightValue(1);
            Log.i("light_hy", "setLoopLightBean: ");
        }else {
            loopLight.setLoopLightValue(value);
        }
        loopLightArray.remove(position);
        loopLightArray.add(position,loopLight);
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

    //设置字体
    private void setTextType() {
        typeFace = Typeface.createFromAsset(getAssets(), "fonts/xiyuanti.ttf");
        tvList.add(tvCustomLightSave);
        tvList.add(tvCustomLightTitle);
        tvList.add(tvCustomLightChannelAll);
        tvList.add(rbCustomLightChannelAll1);
        tvList.add(rbCustomLightChannelAll2);
        tvList.add(rbCustomLightChannelAll3);
        for (int i = 0; i < tvList.size(); i++) {
            tvList.get(i).setTypeface(typeFace);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==event.KEYCODE_BACK){
            Intent intent = new Intent();
            intent.putExtra("isSave",false);
            setResult(1, intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
