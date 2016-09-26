package com.byids.hy.testpro.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.byids.hy.testpro.Bean.Aircondition;
import com.byids.hy.testpro.Bean.Alarmclock;
import com.byids.hy.testpro.Bean.Camera;
import com.byids.hy.testpro.Bean.Camera_indoor;
import com.byids.hy.testpro.Bean.Cinemaroom;
import com.byids.hy.testpro.Bean.Curtain;
import com.byids.hy.testpro.Bean.EzvizCamera;
import com.byids.hy.testpro.Bean.Hiddendoor;
import com.byids.hy.testpro.Bean.HomeAttr;
import com.byids.hy.testpro.Bean.Ibeacon;
import com.byids.hy.testpro.Bean.Light;
import com.byids.hy.testpro.Bean.Lightbelt;
import com.byids.hy.testpro.Bean.Lock;
import com.byids.hy.testpro.Bean.Music;
import com.byids.hy.testpro.Bean.Outdoorwaterflow;
import com.byids.hy.testpro.Bean.Panel;
import com.byids.hy.testpro.Bean.RoomAttr;
import com.byids.hy.testpro.Bean.Rooms;
import com.byids.hy.testpro.Bean.Securityalarm;
import com.byids.hy.testpro.Bean.Sence;
import com.byids.hy.testpro.R;
import com.byids.hy.testpro.View.LoginHScrollView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by gqgz2 on 2016/9/23.
 * 登录页面
 */
public class NewLoginActivity extends Activity{

    private String TAG = "result";
    private ImageView ivBackground;
    private HorizontalScrollView horizontalScrollView;
    private LoginHScrollView loginHScrollView;
    private ImageView ivLogo;
    private ImageView ivPhone;
    private ImageView ivVideo;
    private ImageView tvDenglu;
    private ImageView tvTiyan;
    private ImageView ivLoginBack;
    private TextView tvHehe;
    private RelativeLayout llAndroid;
    private RelativeLayout ivAndroidModel;
    private EditText etUserName;
    private EditText etPassword;
    private TextView tvTopHeight;    //假手机上边框高度
    private TextView tvBottomHeight;    //下边框高度
    private TextView tvLeftWidth;
    private TextView tvRightWidth;
    private LinearLayout llLitterLogin;  //小登录页面
    private ImageView ivLoginPageMain;

    private boolean isSecondPage = false;
    private int width;
    private int height;   //屏幕宽高
    private float ivWidth;

    private String userName;
    private String password;
    private RequestQueue requestQueue;
    HomeAttr homeAttrBean;
    Rooms rs;
    List<Rooms> roomsList = new ArrayList<Rooms>();
    RoomAttr ra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setContentView(R.layout.login_1_layout);
        requestQueue = Volley.newRequestQueue(NewLoginActivity.this);
        initView();
    }

    private void initView(){
        //获取屏幕宽高，设置图片大小一致
        WindowManager wm = this.getWindowManager();
        width = wm.getDefaultDisplay().getWidth();
        height = wm.getDefaultDisplay().getHeight();

        ivBackground = (ImageView) findViewById(R.id.iv_login_bg);
        horizontalScrollView = (HorizontalScrollView) findViewById(R.id.hs_bg);
        loginHScrollView = (LoginHScrollView) findViewById(R.id.hs_login);
        ivLogo = (ImageView) findViewById(R.id.iv_logo);
        ivPhone = (ImageView) findViewById(R.id.iv_phone);
        ivVideo = (ImageView) findViewById(R.id.iv_video);
        tvDenglu = (ImageView) findViewById(R.id.iv_denglu);
        tvTiyan = (ImageView) findViewById(R.id.iv_tiyan);
        ivLoginBack = (ImageView) findViewById(R.id.iv_login_back);
        tvHehe = (TextView) findViewById(R.id.tv_hehe);
        llAndroid = (RelativeLayout) findViewById(R.id.ll_android);
        ivAndroidModel = (RelativeLayout) findViewById(R.id.iv_android_model);
        etUserName = (EditText) findViewById(R.id.et_username);
        etPassword = (EditText) findViewById(R.id.et_password);
        tvTopHeight = (TextView) findViewById(R.id.tv_top_height);
        tvBottomHeight = (TextView) findViewById(R.id.tv_bottom_height);
        tvLeftWidth = (TextView) findViewById(R.id.tv_left_width);
        tvRightWidth = (TextView) findViewById(R.id.tv_right_width);
        llLitterLogin = (LinearLayout) findViewById(R.id.ll_litter_login);
        ivLoginPageMain = (ImageView) findViewById(R.id.iv_login_page_main);


        ivLogo.setPadding((int) (height*0.015),(int) (height*0.015),0,0);
        //初始化控件位置
        ivLoginBack.setVisibility(View.GONE);  //初始化设为不可见
        ViewGroup.LayoutParams params0 = tvHehe.getLayoutParams();
        params0.height = (int) (height*0.029);   //手机距离顶部的距离
        params0.width = (int) (width*0.7);     //手机距离左边的距离
        tvHehe.setLayoutParams(params0);

        //设置假手机全屏
        ViewGroup.LayoutParams params = llAndroid.getLayoutParams();
        params.height = height;
        params.width = width;
        llAndroid.setLayoutParams(params);

        //设置假手机大小
        ViewGroup.LayoutParams params1 = ivAndroidModel.getLayoutParams();
        params1.height = (int) (height*0.88);
        params1.width = (int)(height*0.88*0.486);
        ivAndroidModel.setLayoutParams(params1);

        //四个边距
        ViewGroup.LayoutParams params2 = tvTopHeight.getLayoutParams();
        params2.height = (int) (height*0.88*0.122);
        params2.width = 1;
        tvTopHeight.setLayoutParams(params2);

        ViewGroup.LayoutParams params3 = tvBottomHeight.getLayoutParams();
        params3.height = (int) (height*0.88*0.099);
        params3.width = 1;
        tvBottomHeight.setLayoutParams(params3);

        ViewGroup.LayoutParams params4 = tvLeftWidth.getLayoutParams();
        params4.height = 1;
        params4.width = (int)(height*0.88*0.486*0.066);
        tvLeftWidth.setLayoutParams(params4);

        ViewGroup.LayoutParams params5 = tvRightWidth.getLayoutParams();
        params5.height = 1;
        params5.width = (int)(height*0.88*0.486*0.072);
        tvRightWidth.setLayoutParams(params5);

        //获取控件宽度
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        ivBackground.measure(w, h);
        ivWidth = ivBackground.getMeasuredWidth();

        LinearInterpolator ll = new LinearInterpolator();
        float a = width-ivWidth;
        ObjectAnimator objectAnimator = new ObjectAnimator().ofFloat(ivBackground,"translationX",0f,a,0f).setDuration(70000);
        objectAnimator.setInterpolator(ll);
        objectAnimator.setRepeatCount(-1);
        objectAnimator.start();

        horizontalScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //设置不可滑动
                return true;
            }
        });
        loginHScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //设置不可滑动
                return true;
            }
        });
    }
    public void loginClick(View v){
        switch (v.getId()){
            case R.id.iv_denglu:   //滑到第二页
                ivLoginBack.setAlpha(0f);
                ivLoginBack.setVisibility(View.VISIBLE);  //返回按钮设为可见
                llLitterLogin.setAlpha(0f);
                llLitterLogin.setVisibility(View.VISIBLE);
                fadeOutAnimation(ivLoginBack,0f,1f,500,ivLoginBack,false);
                fadeOutAnimation(llLitterLogin,0f,1f,500,llLitterLogin,false);
                fadeOutAnimation(ivLoginPageMain,1f,0f,500,ivLoginPageMain,true);
                loginHScrollView.smoothScrollTo(1000,0);
                isSecondPage = true;
                break;
            case R.id.iv_tiyan:
                break;
            case R.id.iv_phone:

                break;
            case R.id.iv_video:
                break;
            case R.id.iv_login_back:
                ivLoginPageMain.setAlpha(0f);
                ivLoginPageMain.setVisibility(View.VISIBLE);
                fadeOutAnimation(ivLoginBack,1f,0f,500,ivLoginBack,true);
                fadeOutAnimation(ivLoginPageMain,0f,1f,500,ivLoginPageMain,false);
                fadeOutAnimation(llLitterLogin,1f,0f,500,llLitterLogin,true);
                loginHScrollView.smoothScrollTo(0,0);
                isSecondPage = false;
                break;
            case R.id.iv_android_model:
                if (isSecondPage==true){
                    break;
                }else {
                    ivLoginBack.setAlpha(0f);
                    ivLoginBack.setVisibility(View.VISIBLE);  //返回按钮设为可见
                    llLitterLogin.setAlpha(0f);
                    llLitterLogin.setVisibility(View.VISIBLE);
                    fadeOutAnimation(ivLoginBack,0f,1f,500,ivLoginBack,false);
                    fadeOutAnimation(llLitterLogin,0f,1f,500,llLitterLogin,false);
                    fadeOutAnimation(ivLoginPageMain,1f,0f,500,ivLoginPageMain,true);
                    loginHScrollView.smoothScrollTo(1000,0);
                    isSecondPage = true;
                }
                break;
            case R.id.bt_login:
                doLogin();  //登录

                break;
            case R.id.tv_cant_login:
                Intent intent = new Intent(NewLoginActivity.this,LoginExplainActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    //登录页面的渐隐渐现动画
    private void fadeOutAnimation(View view, float from, float to, long duration, final View v, final boolean b){
        ObjectAnimator objectAnimator = new ObjectAnimator().ofFloat(view,"alpha",from,to).setDuration(duration);
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (b==true){
                    v.setVisibility(View.GONE);
                }else if (b==false){
                    v.setVisibility(View.VISIBLE);
                }
            }
        });
        objectAnimator.start();
    }

    //-----------------------------登录----------------------------
    private void doLogin(){
        userName = etUserName.getText().toString().trim();
        password = etPassword.getText().toString().trim();    //获取用户名和密码
        Toast.makeText(NewLoginActivity.this, "用户名"+userName+","+"密码"+password, Toast.LENGTH_SHORT).show();
        if (TextUtils.isEmpty(userName)|| TextUtils.isEmpty(password)) {
            Toast.makeText(this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }else {
            postAndInitData();
        }

    }

    //根据用户名密码，返回用户家庭的json数据
    private void postAndInitData(){
        String url="http://115.29.97.189:2737/api/login";
        Map<String,String> map=new HashMap<String, String>();
        map.put("uname", userName);
        map.put("pwd", password);
        JSONObject jsonObject = new JSONObject(map);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, "onResponse:--- "+response.toString());
                //Toast.makeText(NewLoginActivity.this, "返回的json："+response.toString(), Toast.LENGTH_SHORT).show();
                doJsonParse(response.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "onErrorResponse: --"+error.getMessage());
                Toast.makeText(NewLoginActivity.this, "错误信息:"+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(request);
    }

    //解析服务器返回的json数据
    private void doJsonParse(String jsonData){
        try {
            JSONObject obj1 = new JSONObject(jsonData);
            Iterator iterator = obj1.keys();
            String hid = (String) iterator.next();   //获取动态json的key值（hid）

            Log.i(TAG, "doJsonParse: --------------------"+hid);
            JSONObject obj2 = obj1.getJSONObject(hid);
            String address = obj2.getString("address");//客户地址
            String comment = obj2.getString("comment");//注释
            String owner = obj2.getString("owner");//所有人
            JSONObject profile = obj2.getJSONObject("profile");
            JSONObject homeAttr = profile.getJSONObject("homeAttr");
            String reboot_cmd = homeAttr.getString("reboot_cmd");//重启指令

            //-------------------------- home 信息-----------------------------
            //萤石
            JSONObject ezviz = homeAttr.getJSONObject("ezviz");
            int ezvizActive = ezviz.getInt("active");
            EzvizCamera EzCBean = new EzvizCamera();
            EzCBean.setActive(ezvizActive);
            //门锁
            JSONObject lock = homeAttr.getJSONObject("lock");
            int lockActive = lock.getInt("active");
            String lock_protocol = lock.getString("protocol");
            Lock lockBean = new Lock();
            lockBean.setActive(lockActive);
            lockBean.setProtocol(lock_protocol);
            //影藏门
            JSONObject hiddendoor = homeAttr.getJSONObject("hiddendoor");
            int hiddendoorActive = hiddendoor.getInt("active");
            String hiddendoor_protocol = hiddendoor.getString("protocol");
            String hiddendoor_uuid = hiddendoor.getString("ibeacon_uuid");
            String hiddendoor_major = hiddendoor.getString("ibeacon_major");
            String hiddendoor_minor = hiddendoor.getString("ibeacon_minor");
            String hiddendoor_pwd = hiddendoor.getString("pwd");
            Hiddendoor hiddendoorBean = new Hiddendoor();
            hiddendoorBean.setActive(hiddendoorActive);
            hiddendoorBean.setProtocol(hiddendoor_protocol);
            hiddendoorBean.setIbeacon_uuid(hiddendoor_uuid);
            hiddendoorBean.setIbeacon_major(hiddendoor_major);
            hiddendoorBean.setIbeacon_minor(hiddendoor_minor);
            hiddendoorBean.setPwd(hiddendoor_pwd);
            //摄像机
            JSONObject camera = homeAttr.getJSONObject("camera");
            int cameraActive = camera.getInt("active");
            String camera_ip = camera.getString("pri_ip");
            String camera_domain = camera.getString("pub_domain");
            String camera_uname = camera.getString("uname");
            String camera_pwd = camera.getString("pwd");
            Camera cameraBean = new Camera();
            cameraBean.setActive(cameraActive);
            cameraBean.setPri_ip(camera_ip);
            cameraBean.setPub_domain(camera_domain);
            cameraBean.setUname(camera_uname);
            cameraBean.setPwd(camera_pwd);
            //安防
            JSONObject securityalarm = homeAttr.getJSONObject("securityalarm");
            String securityalarm_protocol = securityalarm.getString("protocol");
            int securityalarm_active = securityalarm.getInt("active");
            Securityalarm SABean = new Securityalarm();
            SABean.setActive(securityalarm_active);
            SABean.setProtocol(securityalarm_protocol);
            //闹钟
            JSONObject alarmclock = homeAttr.getJSONObject("alarmclock");
            int alarmclock_active = alarmclock.getInt("active");
            Alarmclock ACBean = new Alarmclock();
            ACBean.setActive(alarmclock_active);
            //户外喷泉
            JSONObject outdoorwaterflow = homeAttr.getJSONObject("outdoorwaterflow");
            String outdoorwaterflow_protocol = outdoorwaterflow.getString("protocol");
            int outdoorwaterflow_active = outdoorwaterflow.getInt("active");
            Outdoorwaterflow OWBean = new Outdoorwaterflow();
            OWBean.setActive(outdoorwaterflow_active);
            OWBean.setProtocol(outdoorwaterflow_protocol);
            //影音室
            JSONObject cinemaroom = homeAttr.getJSONObject("cinemaroom");
            int cinemaroom_active = cinemaroom.getInt("active");
            Cinemaroom CRBean = new Cinemaroom();
            CRBean.setActive(cinemaroom_active);
            //音乐
            JSONObject music = homeAttr.getJSONObject("music");
            String music_protocol = music.getString("protocol");
            int music_active = music.getInt("active");
            Music musicBean = new Music();
            musicBean.setActive(music_active);
            musicBean.setProtocol(music_protocol);

            //添加home数据
            homeAttrBean = new HomeAttr();
            homeAttrBean.setEzvizCamera(EzCBean);
            homeAttrBean.setLock(lockBean);
            homeAttrBean.setHiddendoor(hiddendoorBean);
            homeAttrBean.setCamera(cameraBean);
            homeAttrBean.setSecurityalarm(SABean);
            homeAttrBean.setAlarmclock(ACBean);
            homeAttrBean.setOutdoorwaterflow(OWBean);
            homeAttrBean.setMusic(musicBean);
            homeAttrBean.setCinemaroom(CRBean);

            //--------------------房间信息------------------------
            JSONArray rooms = homeAttr.getJSONArray("rooms");
            String roomAttr = profile.getString("homeAttr");      //房间json数据
            doParseRooms(roomAttr);//解析房间数据

            int roomsNum = roomsList.size();//房间数量
            String[] roomNameList = new String[roomsNum];//房间名字数组
            for (int i=0;i<roomsNum;i++){
                JSONObject roomsObj = rooms.getJSONObject(i);
                String roomName = roomsObj.getString("roomName");
                roomNameList[i] = roomName;
            }
            Intent intent = new Intent(NewLoginActivity.this, MyMainActivity.class);
            intent.putExtra("roomNameList",roomNameList);
            intent.putExtra("roomAttr",roomAttr);
            intent.putExtra("hid",hid);
            intent.putExtra("uname",userName);
            intent.putExtra("pwd",password);
            /*if (udpCheck.equals("ip")) {
                intent.putExtra("ip",ip);
            }*/
            Bundle bundle = new Bundle();
            bundle.putSerializable("homeAttr",homeAttrBean);
            intent.putExtras(bundle);
            startActivity(intent);
            finish();        //结束此activity，下一个activity返回时，直接退出
            //overridePendingTransition(R.anim.zoom_in,R.anim.zoom_out);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //解析房间数据
    private void doParseRooms(String mRoomAttr){
        //roomsList = new ArrayList<Rooms>();

        try {
            JSONObject object = new JSONObject(mRoomAttr);
            JSONArray rooms = object.getJSONArray("rooms");
            for (int i=0;i<rooms.length();i++){
                rs = new Rooms();
                ra = new RoomAttr();

                Panel panelBean = new Panel();
                Ibeacon ibeaconBean = new Ibeacon();
                Camera_indoor camera_indoorBean = new Camera_indoor();
                Aircondition airconditionBean = new Aircondition();
                Curtain curtainBean = new Curtain();
                Lightbelt lightbeltBean = new Lightbelt();
                Light lightBean = new Light();
                Sence senceBean = new Sence();

                JSONObject roomObj = rooms.getJSONObject(i);
                String roomDBName = roomObj.getString("roomDBName");//房间拼音名
                String roomName = roomObj.getString("roomName");
                JSONObject roomAttr = roomObj.getJSONObject("roomAttr");

                //floorheater
                JSONObject floorheater = roomAttr.getJSONObject("floorheater");
                String floorheater_protocol = floorheater.getString("protocol");
                int floorheater_active = floorheater.getInt("active");

                //panel
                JSONObject panel = roomAttr.getJSONObject("panel");
                if (roomDBName=="keting"||roomDBName=="shufang"||roomDBName=="woshi"){
                    String panel1 = panel.getString("panel1");
                    String panel2 = panel.getString("panel2");
                    String panel3 = panel.getString("panel3");
                    String panel4 = panel.getString("panel4");
                    panelBean.setPanel1(panel1);
                    panelBean.setPanel2(panel2);
                    panelBean.setPanel3(panel3);
                    panelBean.setPanel4(panel4);
                }
                String panel_protocol = panel.getString("protocol");
                int panel_active = panel.getInt("active");
                panelBean.setProtocol(panel_protocol);
                panelBean.setActive(panel_active);
                ra.setPanel(panelBean);

                //ibeacon
                JSONObject ibeacon = roomAttr.getJSONObject("ibeacon");
                if (roomDBName=="keting"||roomDBName=="shufang"||roomDBName=="woshi"){
                    String minor = ibeacon.getString("minor");
                    String major = ibeacon.getString("major");
                    String uuid = ibeacon.getString("uuid");
                    ibeaconBean.setMajor(major);
                    ibeaconBean.setMinor(minor);
                    ibeaconBean.setUuid(uuid);
                }
                int ibeacon_active = ibeacon.getInt("active");
                ibeaconBean.setActive(ibeacon_active);
                ra.setIbeacon(ibeaconBean);

                //camera_indoor
                JSONObject camera_indoor = roomAttr.getJSONObject("camera_indoor");
                String channel_id = camera_indoor.getString("channel_id");
                int camera_indoor_active = camera_indoor.getInt("active");
                camera_indoorBean.setActive(camera_indoor_active);
                camera_indoorBean.setChannel_id(channel_id);
                ra.setCamera_indoor(camera_indoorBean);

                //aircondition
                JSONObject aircondition = roomAttr.getJSONObject("aircondition");
                String aircondition_protocol = aircondition.getString("protocol");
                int aircondition_active = aircondition.getInt("active");
                airconditionBean.setActive(aircondition_active);
                airconditionBean.setProtocol(aircondition_protocol);
                ra.setAircondition(airconditionBean);

                //curtain
                JSONObject curtain = roomAttr.getJSONObject("curtain");
                String right = curtain.getString("right");
                String left = curtain.getString("left");
                String curtain_protocol = curtain.getString("protocol");
                int curtain_active = curtain.getInt("active");
                curtainBean.setProtocol(curtain_protocol);
                curtainBean.setActive(curtain_active);
                curtainBean.setLeft(left);
                curtainBean.setRight(right);
                ra.setCurtain(curtainBean);

                //lightbelt
                JSONObject lightbelt = roomAttr.getJSONObject("lightbelt");
                String lightbelt_protocol = lightbelt.getString("protocol");
                int lightbelt_active = lightbelt.getInt("active");
                lightbeltBean.setActive(lightbelt_active);
                lightbeltBean.setProtocol(lightbelt_protocol);
                ra.setLightbelt(lightbeltBean);

                //light
                JSONObject light = roomAttr.getJSONObject("light");
                String light_protocol = light.getString("protocol");
                int light_active = light.getInt("active");
                lightBean.setProtocol(light_protocol);
                lightBean.setActive(light_active);
                ra.setLight(lightBean);

                //sence
                JSONObject sence = roomAttr.getJSONObject("sence");
                int sence_active = sence.getInt("active");
                senceBean.setActive(sence_active);
                ra.setSence(senceBean);


                rs.setRoomDBName(roomDBName);
                rs.setRoomName(roomName);
                rs.setRoomAttr(ra);

                roomsList.add(rs);//房间信息加入数组
            }
            homeAttrBean.setRooms(roomsList);

        } catch (JSONException e) {
            Log.i(TAG, "doParseRooms: "+e);
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i("result", "onKeyDown: ------------------");
        if(keyCode == KeyEvent.KEYCODE_BACK && isSecondPage==true){
            ivLoginPageMain.setAlpha(0f);
            ivLoginPageMain.setVisibility(View.VISIBLE);
            fadeOutAnimation(ivLoginBack,1f,0f,500,ivLoginBack,true);
            fadeOutAnimation(ivLoginPageMain,0f,1f,500,ivLoginPageMain,false);
            fadeOutAnimation(llLitterLogin,1f,0f,500,llLitterLogin,true);
            loginHScrollView.smoothScrollTo(0,0);
            isSecondPage = false;
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}