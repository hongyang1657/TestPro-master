package com.byids.hy.testpro.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
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

import com.byids.hy.testpro.Bean.HomeAttr;
import com.byids.hy.testpro.Bean.RoomAttr;
import com.byids.hy.testpro.Bean.Rooms;
import com.byids.hy.testpro.R;
import com.byids.hy.testpro.View.LoginHScrollView;
import com.byids.hy.testpro.newBean.AllJsonData;
import com.byids.hy.testpro.service.UDPBroadcastService;
import com.byids.hy.testpro.utils.LongLogCatUtil;
import com.byids.hy.testpro.utils.NetworkStateUtil;
import com.byids.hy.testpro.utils.RunningTimeDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by gqgz2 on 2016/9/23.
 * 登录页面
 */
public class NewLoginActivity extends BaseActivity{

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
    private TextView tvCantLogin;
    private RelativeLayout llAndroid;
    private RelativeLayout ivAndroidModel;
    private EditText etUserName;
    private EditText etPassword;
    private TextView tvTopHeight;    //假手机上边框高度
    private TextView tvBottomHeight;    //下边框高度
    private TextView tvLeftWidth;
    private TextView tvRightWidth;
    private TextView tvTopHeight1;
    private TextView tvBottomHeight1;
    private TextView tvLeftWidth1;
    private TextView tvRightWidth1;
    private LinearLayout llLitterLogin;  //小登录页面
    private ImageView ivLoginPageMain;
    private ImageView ivLoginMap;
    private ImageView ivLoginPhoneNumber;
    private TextView tvTopHeightIn;

    private boolean isSecondPage = false;
    private boolean isPhonePage = false;
    private int width;
    private int height;   //屏幕宽高
    private float ivWidth;
    private int scrollWidth;   //可滑动的距离

    private String userName;
    private String password;
    HomeAttr homeAttrBean;
    Rooms rs;
    List<Rooms> roomsList = new ArrayList<Rooms>();
    RoomAttr ra;
    private RunningTimeDialog runningTimeDialog = new RunningTimeDialog();

    private Intent intentLogin;

    //---------------------------udp----------------------------
    public static final int DEFAULT_PORT = 57816;//端口号
    public static final String LOG_TAG = "WifiBroadcastActivity";
    private byte[] buffer = new byte[MAX_DATA_PACKET_LENGTH];
    private static final int MAX_DATA_PACKET_LENGTH = 100;
    private String udpCheck = "";
    private String ip;    //接收到的ip地址

    private String allJson;     //解析出的json
    private AllJsonData allJsonData;
    private boolean isConnect;       //网络是否可用
    private boolean isMobileState;      //是否为数据流量状态（外网）
    private String token;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    runningTimeDialog.runningTimeProgressDialog(NewLoginActivity.this);
                    //runningTimeDialog.runningTimeProgressDialog1(NewLoginActivity.this);
                    break;
                case 2:       //获取token
                    String token1 = (String) msg.obj;
                    getToken(token1);
                    break;
                case 3:      //获取用户信息
                    allJson = (String) msg.obj;
                    LongLogCatUtil.logE("hongyang","外网登陆获取："+allJson);
                    runningTimeDialog.progressDialog.dismiss();     //转圈圈消失
                    //跳转
                    Log.i("putExtra_hy", "secondLoginLAN:"+userName+"---"+password+"---"+ip+"---"+token);
                    intentLogin.putExtra("isFirstLogin",true);
                    intentLogin.putExtra("uname",userName);
                    intentLogin.putExtra("pwd",password);
                    intentLogin.putExtra("host_ip",ip);
                    startActivity(intentLogin);
                    finish();        //结束此activity，下一个activity返回时，直接退出
                    break;
                default:
                    break;
            }
        }
    };

    //-------------------activity与service通信----------------------
    private UDPBroadcastService.UDPBinder udpBinder;      //绑定后台udp
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            udpBinder = (UDPBroadcastService.UDPBinder) service;
            ip = udpBinder.getHostIp();
            Log.i(TAG, "onServiceConnected: -----获取后台接受的主机ip------"+ip);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "onServiceDisconnected: service断开绑定");
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setContentView(R.layout.login_1_layout);
        //绑定service，实现通信
        Intent bindIntent = new Intent(this, UDPBroadcastService.class);
        bindService(bindIntent, connection, BIND_AUTO_CREATE); // 绑定服务
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
        setContentView(R.layout.view_null);
    }

    private void initView(){
        //获取主机ip
        ip = getIntent().getStringExtra("ip");

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
        tvCantLogin = (TextView) findViewById(R.id.tv_cant_login);

        llAndroid = (RelativeLayout) findViewById(R.id.ll_android);
        ivAndroidModel = (RelativeLayout) findViewById(R.id.iv_android_model);
        etUserName = (EditText) findViewById(R.id.et_username);
        etPassword = (EditText) findViewById(R.id.et_password);
        tvTopHeight = (TextView) findViewById(R.id.tv_top_height);
        tvBottomHeight = (TextView) findViewById(R.id.tv_bottom_height);
        tvLeftWidth = (TextView) findViewById(R.id.tv_left_width);
        tvRightWidth = (TextView) findViewById(R.id.tv_right_width);
        tvTopHeight1 = (TextView) findViewById(R.id.tv_top_height_phone);
        tvBottomHeight1 = (TextView) findViewById(R.id.tv_bottom_height_phone);
        tvLeftWidth1 = (TextView) findViewById(R.id.tv_left_width_phone);
        tvRightWidth1 = (TextView) findViewById(R.id.tv_right_width_phone);
        llLitterLogin = (LinearLayout) findViewById(R.id.ll_litter_login);
        ivLoginPageMain = (ImageView) findViewById(R.id.iv_login_page_main);
        ivLoginMap = (ImageView) findViewById(R.id.iv_login_map);
        ivLoginPhoneNumber = (ImageView) findViewById(R.id.iv_login_phonenumber);
        tvTopHeightIn = (TextView) findViewById(R.id.tv_top_height_in);


        ivLogo.setPadding((int) (height*0.008),(int) (height*0.008),0,0);
        //初始化控件位置
        ivLoginBack.setVisibility(View.GONE);  //初始化设为不可见
        ViewGroup.LayoutParams params0 = tvHehe.getLayoutParams();
        params0.height = (int) (height*0.029);   //手机距离顶部的距离
        params0.width = (int) (width*0.7);     //手机距离左边的距离
        tvHehe.setLayoutParams(params0);
        scrollWidth = (int) (width*0.7);

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

        //安放地址信息
        ViewGroup.LayoutParams params6 = tvTopHeight1.getLayoutParams();
        params6.height = (int) (height*0.88*0.52);
        params6.width = 1;
        tvTopHeight1.setLayoutParams(params6);

        ViewGroup.LayoutParams params7 = tvBottomHeight1.getLayoutParams();
        params7.height = (int) (height*0.88*0.28);
        params7.width = 1;
        tvBottomHeight1.setLayoutParams(params7);

        ViewGroup.LayoutParams params8 = tvLeftWidth1.getLayoutParams();
        params8.height = 1;
        params8.width = (int)(height*0.88*0.486*0.090);
        tvLeftWidth1.setLayoutParams(params8);

        ViewGroup.LayoutParams params9 = tvRightWidth1.getLayoutParams();
        params9.height = 1;
        params9.width = (int)(height*0.88*0.486*0.107);
        tvRightWidth1.setLayoutParams(params9);

        //内部logo距离顶部高度
        ViewGroup.LayoutParams params10 = tvTopHeightIn.getLayoutParams();
        params10.height = (int) (height*0.88*0.19);
        params10.width = 1;
        tvTopHeightIn.setLayoutParams(params10);

        //获取控件宽度
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        ivBackground.measure(w, h);
        ivWidth = ivBackground.getMeasuredWidth();

        LinearInterpolator ll = new LinearInterpolator();
        float a = width-ivWidth;
        ObjectAnimator objectAnimator = new ObjectAnimator().ofFloat(ivBackground,"translationX",0f,a,0f).setDuration(120000);
        objectAnimator.setInterpolator(ll);
        objectAnimator.setRepeatCount(-1);
        objectAnimator.start();

        horizontalScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {   //设置不可滑动
                return true;
            }
        });
        loginHScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {   //设置不可滑动
                return true;
            }
        });

        //获取保存的用户名密码
        /*SharedPreferences sp = getSharedPreferences("user_inform",MODE_PRIVATE);
        etUserName.setText(sp.getString("userName","").toString());
        etPassword.setText(sp.getString("password","").toString());*/

    }

    public void loginClick(View v){
        switch (v.getId()){
            case R.id.iv_denglu:   //滑到第二页
                setScaleAnimation(tvDenglu);
                loginHScrollView.smoothScrollToSlow(scrollWidth,0,700);
                ivLoginBack.setAlpha(0f);
                ivLoginBack.setVisibility(View.VISIBLE);  //返回按钮设为可见
                llLitterLogin.setAlpha(0f);
                llLitterLogin.setVisibility(View.VISIBLE);
                fadeOutAnimation(ivLoginBack,0f,1f,500,ivLoginBack,false);
                fadeOutAnimation(llLitterLogin,0f,1f,500,llLitterLogin,false);
                fadeOutAnimation(ivLoginPageMain,1f,0f,500,ivLoginPageMain,true);
                isSecondPage = true;

                break;
            case R.id.iv_tiyan:

                Log.i(TAG, "loginClick: %%%%%%%%%%登录页%%%%%%%获取主机ip%%%%%%%%%%%登录页%%%%%%%%"+udpBinder.getHostIp());
                break;
            case R.id.iv_phone:
                setScaleAnimation(ivPhone);
                if (isPhonePage==false){
                    loginHScrollView.smoothScrollToSlow(scrollWidth,0,700);
                    ivLoginBack.setAlpha(0f);
                    ivLoginBack.setVisibility(View.VISIBLE);  //返回按钮设为可见
                    ivLoginMap.setAlpha(0f);
                    ivLoginMap.setVisibility(View.VISIBLE);
                    ivLoginPhoneNumber.setAlpha(0f);
                    ivLoginPhoneNumber.setVisibility(View.VISIBLE);
                    fadeOutAnimation(ivLoginBack,0f,1f,500,ivLoginBack,false);
                    fadeOutAnimation(ivLoginPageMain,1f,0f,500,ivLoginPageMain,true);
                    //fadeOutAnimation(ivLoginMap,0f,1f,500,ivLoginMap,false);
                    ObjectAnimator objectAnimator = new ObjectAnimator().ofFloat(ivLoginMap,"alpha",0f,1f).setDuration(500);
                    objectAnimator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            scaleMap();
                        }
                    });
                    objectAnimator.start();
                    ivLoginMap.setVisibility(View.VISIBLE);
                    isSecondPage = true;
                    isPhonePage = true;
                    break;
                }else if (isPhonePage==true){
                    break;
                }

            case R.id.iv_video:
                setScaleAnimation(ivVideo);
                break;
            case R.id.iv_login_back:
                setScaleAnimation(ivLoginBack);
                loginHScrollView.smoothScrollToSlow(0,0,700);
                ivLoginPageMain.setAlpha(0f);
                ivLoginPageMain.setVisibility(View.VISIBLE);
                fadeOutAnimation(ivLoginBack,1f,0f,500,ivLoginBack,true);
                fadeOutAnimation(ivLoginPageMain,0f,1f,500,ivLoginPageMain,false);
                fadeOutAnimation(llLitterLogin,1f,0f,500,llLitterLogin,true);
                fadeOutAnimation(ivLoginMap,1f,0f,500,ivLoginMap,true);
                fadeOutAnimation(ivLoginPhoneNumber,1f,0f,500,ivLoginPhoneNumber,true);
                isSecondPage = false;
                isPhonePage = false;
                break;
            case R.id.iv_android_model:
                if (isSecondPage==true){
                    break;
                }else {
                    loginHScrollView.smoothScrollToSlow(scrollWidth,0,700);
                    ivLoginBack.setAlpha(0f);
                    ivLoginBack.setVisibility(View.VISIBLE);  //返回按钮设为可见
                    llLitterLogin.setAlpha(0f);
                    llLitterLogin.setVisibility(View.VISIBLE);
                    fadeOutAnimation(ivLoginBack,0f,1f,500,ivLoginBack,false);
                    fadeOutAnimation(llLitterLogin,0f,1f,500,llLitterLogin,false);
                    fadeOutAnimation(ivLoginPageMain,1f,0f,500,ivLoginPageMain,true);
                    isSecondPage = true;
                }
                break;
            case R.id.bt_login:   //登陆
                //获取网络状态
                isConnect = NetworkStateUtil.isNetworkAvailable(this);
                isMobileState = NetworkStateUtil.isNetworkMobileState(this);
                if (isConnect){  //网络可用
                    //加载动画
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                        }
                    }.start();

                    doLogin();  //外网登录
                }else {
                    Toast.makeText(this, "当前网络不可用", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.tv_cant_login:
                Intent intent = new Intent(NewLoginActivity.this,LoginExplainActivity.class);
                startActivity(intent);
                break;
            case R.id.iv_login_phonenumber:
                //跳转到拨号界面
                Intent intentCall = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:02787743339"));
                intentCall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentCall);
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

    //地图缩放动画
    private void scaleMap(){
        ObjectAnimator.ofFloat(ivLoginMap,"scaleX",1f,0.9f,1f).setDuration(400).start();
        ObjectAnimator.ofFloat(ivLoginMap,"scaleY",1f,0.9f,1f).setDuration(400).start();
        ObjectAnimator.ofFloat(ivLoginPhoneNumber,"alpha",0f,1f).setDuration(400).start();
    }

    //按钮点击缩放
    private void setScaleAnimation(View view){
        ObjectAnimator.ofFloat(view,"scaleX",1f,0.6f,1f).setDuration(400).start();
        ObjectAnimator.ofFloat(view,"scaleY",1f,0.6f,1f).setDuration(400).start();
    }

    //-----------------------------登录----------------------------
    private void doLogin(){
        intentLogin = new Intent(NewLoginActivity.this, MyMainActivity.class);  //跳转的
        userName = etUserName.getText().toString().trim();
        password = etPassword.getText().toString().trim();    //获取用户名和密码
        if (TextUtils.isEmpty(userName)|| TextUtils.isEmpty(password)) {
            Toast.makeText(this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }else {         //外网请求
            loginPost();    //新版
            //postAndInitData();   //旧版，日后删除
        }
    }

    //SharedPreferences本地储存用户信息
    private void saveUserInform(String userName,String password){
        SharedPreferences sp = getSharedPreferences("user_inform",MODE_PRIVATE);        //文件名，文件类型
        sp.edit().putString("userName",userName).putString("password",password).commit();
    }
    /*private void saveHomeJson(String homeJson){
        SharedPreferences sp = getSharedPreferences("homeJson",MODE_PRIVATE);        //文件名，文件类型
        sp.edit().putString("homeJson",homeJson).commit();
    }*/

    //套包返回的json数据
    private void loginPost(){
        //final String url = "http://115.29.97.189:20000/api/user/login";
        final String url = "http://192.168.10.230:20000/api/user/login";
        new Thread(){
            @Override
            public void run() {
                super.run();
                // 表单提交
                Log.i(TAG, "run:!!!!!!!!! userName:"+userName);
                RequestBody formBody = new FormBody.Builder().add("num", userName).add("pwd", password).build();
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url).addHeader("content-type", "application/x-www-form-urlencoded").post(formBody).build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.i(TAG, "onFailure: -----------请求失败------------："+e.toString());

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String token = response.body().string();
                        Log.i(TAG, "onResponse: 获取新版token："+token);
                        Message msg = new Message();
                        msg.what = 2;
                        msg.obj = token;
                        handler.sendMessage(msg);
                    }
                });
            }
        }.start();
    }
    //套包登录获取token
    private void getToken(String response){
        try {
            JSONObject obj = new JSONObject(response);
            token = obj.getString("token");      //获取token
            postToken(token);
            //传一个token
            intentLogin.putExtra("token",token);
            Log.i(TAG, "getToken: 新版token："+token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //向服务器post token
    private void postToken(final String token){
        final String token1 = "00000000000000000000000000000000"; //暂时用
        //final String url = "http://115.29.97.189:20000/api/homeserver/profile";
        final String url = "http://192.168.10.230:20000/api/homeserver/profile";
        new Thread(){
            @Override
            public void run() {
                super.run();
                OkHttpClient client = new OkHttpClient();
                okhttp3.Request request = new okhttp3.Request.Builder().url(url).addHeader("content-type", "application/json").addHeader("token",token1).build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.i(TAG, "onFailure: -----------请求失败------------："+e.toString());
                    }

                    @Override
                    public void onResponse(Call call, okhttp3.Response response) throws IOException {
                        String resp = response.body().string();
                        //LongLogCatUtil.logE(TAG,"新版房间信息:"+resp);
                        Message msg = new Message();
                        msg.what = 3;
                        msg.obj = resp;
                        handler.sendMessage(msg);
                    }
                });
            }
        }.start();
    }


    /*//根据用户名密码，返回用户家庭的json数据 -------------旧版--------------
    private void postAndInitData(){
        String url="http://115.29.97.189:2737/api/login";
        Map<String,String> map=new HashMap<String, String>();
        map.put("uname", userName);
        map.put("pwd", password);
        JSONObject jsonObject = new JSONObject(map);
        Log.i(TAG, "postAndInitData:---------------------------------jsonObject----------------------------- "+jsonObject.toString());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String resp = response.toString();
                Log.i(TAG, "onResponse:用户家庭数据 "+resp);
                try {
                    JSONObject obj = new JSONObject(resp);
                    Iterator iterator = obj.keys();
                    String key = (String) iterator.next();       //获取key值，如果为error，则说明用户名或密码错误
                    if (key=="error"||key.equals("error")){
                        Toast.makeText(NewLoginActivity.this, "用户名或密码错误！", Toast.LENGTH_SHORT).show();
                        return;
                    }else {
                        //登录成功
                        Toast.makeText(NewLoginActivity.this, "用户名"+userName+","+"密码"+password, Toast.LENGTH_SHORT).show();

                        //saveUserInform(userName,password);    //保存用户名密码到本地
                        //saveHomeJson(response.toString());    //保存用户的房间信息
                        doJsonParse(response.toString());    //解析json
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "onErrorResponse: --"+error.getMessage());
                Toast.makeText(NewLoginActivity.this, "错误信息:"+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(request);
    }*/

    //解析服务器返回的json数据    -----------------旧版的数据解析-------------------
    /*private void doJsonParse(String jsonData){
        try {
            JSONObject obj1 = new JSONObject(jsonData);
            Iterator iterator = obj1.keys();
            String hid = (String) iterator.next();   //获取动态json的key值（hid）
            //test(hid);        //测试udp

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
            String[] roomDBNameList = new String[roomsNum];//房间名字数组
            for (int i=0;i<roomsNum;i++){
                JSONObject roomsObj = rooms.getJSONObject(i);
                String roomName = roomsObj.getString("roomName");
                String roomDBName = roomsObj.getString("roomDBName");
                roomNameList[i] = roomName;
                roomDBNameList[i] = roomDBName;
            }

            //旧版
            intentLogin.putExtra("roomNameList",roomNameList);
            intentLogin.putExtra("roomDBNameList",roomDBNameList);
            intentLogin.putExtra("roomAttr",roomAttr);


            //新版
            *//*int roomsNumNew = allJsonData.getCommandData().getProfile().getRooms().getArray().size();//房间数量
            String[] roomNameListNew = new String[roomsNumNew];//房间名字数组
            String[] roomDBNameListNew = new String[roomsNumNew];//房间名字数组
            for (int i=0;i<roomsNumNew;i++){
                roomNameListNew[i] = allJsonData.getCommandData().getProfile().getRooms().getArray().get(i).getRoom_zh_name();
                roomDBNameListNew[i] = allJsonData.getCommandData().getProfile().getRooms().getArray().get(i).getRoom_db_name();

            }

            intent.putExtra("roomNameList",roomNameListNew);
            intent.putExtra("roomDBNameList",roomDBNameListNew);
            intent.putExtra("roomAttr",roomAttr);*//*



            intentLogin.putExtra("host_ip",host_ip);
            intentLogin.putExtra("hid",hid);
            intentLogin.putExtra("uname",userName);
            intentLogin.putExtra("pwd",password);
            if (udpCheck.equals("ip")) {
                intentLogin.putExtra("ip",ip);
                Log.i(TAG, "doJsonParse: -------------put到MainActivity的ip-----------"+ip);
            }
            Bundle bundle = new Bundle();
            bundle.putSerializable("homeAttr",homeAttrBean); //旧版
            intentLogin.putExtras(bundle);

            //overridePendingTransition(R.anim.zoom_in,R.anim.zoom_out);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        runningTimeDialog.progressDialog.dismiss();
    }*/

    //测试  包装发送udp
    /*public void test(String hid){
        String udpJson="{\"command\":\"find\",\"data\":{\"hid\":\""+hid+"\",\"loginName\":\"byids\"}}";
        Log.i(TAG, "test: ------------------"+udpJson);
        byte[] enByte = AES.encrpt(udpJson);//加密
        if (enByte == null)
            return;
        byte[] lengthByte = ByteUtils.intToByteBigEndian(enByte.length);
        byte[] headByte = new byte[8];
        for (int i = 0;i<headByte.length;i++) {
            headByte[i] = 0x50;
        }
        byte[] tailByte = new byte[4];
        tailByte[0] = 0x0d;
        tailByte[1] = 0x0a;
        tailByte[2] = 0x0d;
        tailByte[3] = 0x0a;

        byte[] sendByte = ByteUtils.byteJoin(headByte,lengthByte,enByte,tailByte);

        //发送udp广播
        BroadCastUdp udp = new BroadCastUdp(sendByte);
        udp.start();
    }*/


    /*//测试解密byte[]
    private void testDecryptByte(byte[] sendByte){
        Log.i(TAG, "testDecryptByte: !!!!!解密前!!!!!!"+byteStringLog(sendByte));
        byte[] new_sendByte = Arrays.copyOfRange(sendByte,12,sendByte.length);
        byte[] nnew_sendByte = Arrays.copyOfRange(new_sendByte,0,new_sendByte.length-4);
        Log.i(TAG, "test: ************************newsendByte"+nnew_sendByte.length);
        Log.i(TAG, "test: *************newsendByte**************"+byteStringLog(nnew_sendByte));
        byte[] ivByte = Arrays.copyOfRange(nnew_sendByte,nnew_sendByte.length-16,nnew_sendByte.length);
        byte[] dataByte = Arrays.copyOfRange(nnew_sendByte,0,nnew_sendByte.length-16);
        Log.i(TAG, "test: *****************加密向量*******newsendByte"+ivByte.length);
        Log.i(TAG, "test: *************newsendByte*****加密向量*********"+byteStringLog(ivByte));
        Log.i(TAG, "test: *****************加密数据*******newsendByte"+dataByte.length);
        Log.i(TAG, "test: *************newsendByte*****加密数据*********"+byteStringLog(dataByte));
        byte[] a = AES.decrypt(dataByte,ivByte);
        Log.i(TAG, "testDecryptByte: ！！！！！！！！！！解密数据byte[]"+byteStringLog(a));
        Log.i(TAG, "testDecryptByte: !!!!!!!!!!!!!!解密数据长度!!!!!!!!!!!!!!"+a.length+"!!!!解密数据：!!!!"+new String(a));
    }



    //测试，用来显示byte[]
    private String byteStringLog(byte[] bs){
        String log = new String();
        for (int i = 0;i<bs.length;i++){
            int bi = (int)bs[i];
            log=log+" "+ String.valueOf(bi);
        }
        System.out.println(log);
        return log;
    }*/


    //发送UDP
    /*public class BroadCastUdp extends Thread {
        DatagramPacket dataPacket = null;
        DatagramPacket receiveData= null;
        private byte[] dataByte;
        private DatagramSocket udpSocket;
        public BroadCastUdp(byte[] sendByte) {
            this.dataByte = sendByte;
        }
        public void run() {
            try {
                //udpSocket = new DatagramSocket(DEFAULT_PORT);
                if (udpSocket==null){
                    udpSocket = new DatagramSocket(null);
                    udpSocket.setReuseAddress(true);
                    udpSocket.bind(new InetSocketAddress(DEFAULT_PORT));
                }
                dataPacket = new DatagramPacket(buffer, MAX_DATA_PACKET_LENGTH);
                receiveData= new DatagramPacket(buffer,MAX_DATA_PACKET_LENGTH);
                if (this.dataByte == null){
                    return;
                }
                byte[] data = this.dataByte;
                dataPacket.setData(data);
                dataPacket.setLength(data.length);
                dataPacket.setPort(DEFAULT_PORT);

                InetAddress broadcastAddr;
                broadcastAddr = InetAddress.getByName("255.255.255.255");
                dataPacket.setAddress(broadcastAddr);
            } catch (Exception e) {
                Log.e(LOG_TAG, e.toString());
                udpSocket.close();
            }
            // while( start ){
            try {
                udpSocket.send(dataPacket);
                sleep(10);
            } catch (Exception e) {
                Log.e(LOG_TAG, e.toString());
                udpSocket.close();
            }
            try {
                udpSocket.receive(receiveData);
                //udpSocket.receive(receiveData);
            } catch (Exception e) {
// TODO Auto-generated catch block
                Log.e(LOG_TAG, e.toString());
                udpSocket.close();
            }
            if (null!=receiveData){

                if( 0!=receiveData.getLength() ) {
                    String codeString = new String( buffer, 0, receiveData.getLength() );
                    String a = receiveData.getAddress().getHostAddress();  //主机ip地址
                    int b = receiveData.getPort();      //主机端口号
                    Log.i(TAG, "run: --------------主机的IP地址--------------"+a+"-------"+b);
                    Log.i("result", "接收到数据为codeString: "+codeString);
                    udpCheck = codeString.substring(2,4);
                    Log.i("result", "接收到数据为: "+udpCheck);
                    Log.i("result","recivedataIP地址为："+receiveData.getAddress().toString().substring(1));//此为IP地址
                    //Log.i("result","recivedata_sock地址为："+receiveData.getAddress());//此为IP加端口号

                    //ip = receiveData.getAddress().toString().substring(1);   //ip地址
                    ip = receiveData.getAddress().getHostAddress();        //发送udp广播，收到的主机的ip地址
                }
            }else{
                try {
                    udpSocket.send(dataPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            udpSocket.close();
        }
    }*/


    //解析房间数据
    /*private void doParseRooms(String mRoomAttr){
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
    }*/

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i("result", "onKeyDown: ------------------");
        if(keyCode == KeyEvent.KEYCODE_BACK && isSecondPage==true){
            loginHScrollView.smoothScrollToSlow(0,0,700);
            ivLoginPageMain.setAlpha(0f);
            ivLoginPageMain.setVisibility(View.VISIBLE);
            fadeOutAnimation(ivLoginBack,1f,0f,500,ivLoginBack,true);
            fadeOutAnimation(ivLoginPageMain,0f,1f,500,ivLoginPageMain,false);
            fadeOutAnimation(llLitterLogin,1f,0f,500,llLitterLogin,true);
            fadeOutAnimation(ivLoginMap,1f,0f,500,ivLoginMap,true);
            fadeOutAnimation(ivLoginPhoneNumber,1f,0f,500,ivLoginPhoneNumber,true);
            isSecondPage = false;
            isPhonePage = false;
            return false;
        }else if (keyCode==KeyEvent.KEYCODE_DEL){

        }else {
            Log.i(TAG, "onKeyDown: -===================毁所有内存=========================");
            System.exit(0);      //销毁所有内存
        }
        return super.onKeyDown(keyCode, event);
    }
}
