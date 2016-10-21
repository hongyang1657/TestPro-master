package com.byids.hy.testpro.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.byids.hy.testpro.Bean.HomeAttr;
import com.byids.hy.testpro.MyEventBus;
import com.byids.hy.testpro.MyEventBus2;
import com.byids.hy.testpro.PullDownMenuListener;
import com.byids.hy.testpro.R;
import com.byids.hy.testpro.TCPLongSocketCallback;
import com.byids.hy.testpro.TcpLongSocket;
import com.byids.hy.testpro.View.MyCustomViewPager;
import com.byids.hy.testpro.adapter.MyFragmentAdapter;
import com.byids.hy.testpro.adapter.RoomNameBaseAdapter;
import com.byids.hy.testpro.fragment.MyFragment;
import com.byids.hy.testpro.utils.CommandJsonUtils;
import com.byids.hy.testpro.utils.Encrypt;
import com.byids.hy.testpro.utils.RunningTimeDialog;
import com.byids.hy.testpro.utils.VibratorUtil;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.videogo.openapi.EZOpenSDK;
import android.support.v4.app.FragmentManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hy on 2016/8/15.
 */
public class MyMainActivity extends FragmentActivity {
    private String TAG = "result";
    private static final String SWITCH_ROOM_DIALOG = "1";
    private static final String SCROLL_FRAGMENT_START = "2";    //滑动viewpager
    private static final String SCROLL_FRAGMENT_END = "3";      //结束滑动viewpager
    private static final String SETTING_DIALOG = "4";     //打开设置二级界面
    private static final String DOOR_LOCK_DIALOG = "5";     //打开门锁二级界面
    private static final String SECURITY_DIALOG = "6";     //打开安防二级界面

    private Typeface typeFace;
    private MyCustomViewPager viewPager;
    private MyFragmentAdapter adapter;
    private List<Fragment> viewList = new ArrayList<Fragment>();
    private GestureDetector gestureDetector;
    private boolean b1 = true;       //下拉菜单隐藏为true，出现为false
    private int pagerState;
    private int width;
    private int height;

    //几个控件
    private TextView tvRoom;//房间名
    private ImageView ivMusic;
    private ImageView ivMedia;
    private RelativeLayout rlMain;  //主界面
    private ImageView ivBlackFront;    //黑色的遮罩

    //二级页面dialog
    private Dialog dialogSwitchRoom;
    private Dialog dialogSetting;
    private Dialog dialogLock;
    private Dialog dialogSecurity;
    private ListView lvSwitchRoom;
    private TextView tvSwitchRoomCancel;
    private RoomNameBaseAdapter adapterRoomName;


    //背景图片
    private int[] ivBackList1 = {R.mipmap.back_10, R.mipmap.back_12, R.mipmap.back_13, R.mipmap.back_14};
    private int[] ivBackList2 = {R.mipmap.back_5, R.mipmap.back_6, R.mipmap.back_8, R.mipmap.back_9};
    private int[] ivBackList3 = {R.mipmap.back_1, R.mipmap.back_2, R.mipmap.back_3, R.mipmap.back_4};
    private int[] ivBackList = {R.mipmap.back_10, R.mipmap.back_12, R.mipmap.back_13, R.mipmap.back_14, R.mipmap.back_5, R.mipmap.back_6, R.mipmap.back_8, R.mipmap.back_9, R.mipmap.back_1, R.mipmap.back_2, R.mipmap.back_3, R.mipmap.back_4};

    //房间名数组kk
    private String[] roomNameList = null;
    private String[] roomDBNameList = null;
    private HomeAttr homeAttr = new HomeAttr();

    private MyFragment myFragment1;

    private String ip; //home  ip地址
    private String uname;
    private String pwd;
    private String hid;

    //----------------socket---------------------
    public static final int DEFAULT_PORT = 57816;
    private TcpLongSocket tcplongSocket;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setContentView(R.layout.my_main_layout);
        //注册EventBus
        EventBus.getDefault().register(this);
        reciveIntent();
        initView();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);  //反注册EventBus
        view = null;
        viewExit = null;
        viewList = null;
        viewLock = null;
        viewSecurity = null;
        viewSetting = null;
        dialogExit.setContentView(R.layout.view_null);
        dialogSetting.setContentView(R.layout.view_null);
        dialogSwitchRoom.setContentView(R.layout.view_null);
        dialogSecurity.setContentView(R.layout.view_null);
        dialogLock.setContentView(R.layout.view_null);
        dialogExit.setContentView(R.layout.view_null);
        setContentView(R.layout.view_null);
    }

    //接收登录页面传递过来的数据
    private void reciveIntent() {
        uname = getIntent().getStringExtra("uname");
        pwd = getIntent().getStringExtra("pwd");
        hid = getIntent().getStringExtra("hid");
        ip = getIntent().getStringExtra("ip");
        if (ip == null) {
            Toast.makeText(MyMainActivity.this, "找不到主机", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MyMainActivity.this, "找到主机", Toast.LENGTH_LONG).show();
            tcplongSocket = new TcpLongSocket(new ConnectTcp());
            tcplongSocket.startConnect(ip, DEFAULT_PORT);
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    while (true) {
                        try {
                            sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        tcplongSocket.writeDate(Encrypt.encrypt("0"));
                    }
                }
            }.start();
        }
    }

    /*@Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "MyMain Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.byids.hy.testpro.activity/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "MyMain Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.byids.hy.testpro.activity/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }*/


    private class ConnectTcp implements TCPLongSocketCallback {


        @Override
        public void connected() {
            Log.i("MAIN", String.valueOf(tcplongSocket.getConnectStatus()));
            VibratorUtil.Vibrate(MyMainActivity.this, 300);
            JSONObject checkCommandData = new JSONObject();
            try {
                checkCommandData.put("kong", "keys");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String checkJson = CommandJsonUtils.getCommandJson(1, checkCommandData, hid, uname, pwd, String.valueOf(System.currentTimeMillis()));
            Log.i("result", "check" + checkJson);
            tcplongSocket.writeDate(Encrypt.encrypt(checkJson));

        }

        @Override
        public void receive(byte[] buffer) {
            Log.i("result", "---收到         数据-----");
            if ("Hello client" == buffer.toString()) {
                Log.i("result", "心跳" + String.valueOf(tcplongSocket.getConnectStatus()));
                Log.i(TAG, "receive: 连接状态**********" + tcplongSocket.getConnectStatus());
            }

        }

        @Override
        public void disconnect() {
            tcplongSocket.close();
        }
    }

    private void initView() {

        typeFace = Typeface.createFromAsset(getAssets(), "fonts/xiyuanti.ttf");
        //获取房间信息
        roomNameList = getIntent().getStringArrayExtra("roomNameList");
        roomDBNameList = getIntent().getStringArrayExtra("roomDBNameList");
        homeAttr = (HomeAttr) getIntent().getSerializableExtra("homeAttr");
        //Log.i(TAG, "initView: " + homeAttr.getRooms().get(0).getRoomAttr().getLight().getActive());
        //Log.i(TAG, "initView: " + homeAttr);


        //初始化activity给fragment传递的数据
        for (int i = 0; i < roomNameList.length; i++) {
            myFragment1 = new MyFragment(i, roomNameList[i], roomDBNameList[i], ivBackList, homeAttr.getRooms().get(i).getRoomAttr(), hid, uname, pwd);         //房间id,房间名数组，房间拼音名数组，背景图片数组，活跃的控件数组,hid,uname,pwd
            pullMenu(myFragment1);
            viewList.add(myFragment1);
        }

        WindowManager wm = this.getWindowManager();
        width = wm.getDefaultDisplay().getWidth();
        height = wm.getDefaultDisplay().getHeight();
        //Log.i(TAG, "initView: ____________________" + width + "___________________" + height);

        ivBlackFront = (ImageView) findViewById(R.id.iv_black_front);
        rlMain = (RelativeLayout) findViewById(R.id.rl_main);
        ivMusic = (ImageView) findViewById(R.id.iv_music);
        ivMedia = (ImageView) findViewById(R.id.iv_media);
        ivMusic.setOnClickListener(mediaListener);
        ivMedia.setOnClickListener(mediaListener);
        int h = (int) ((int) height * 0.03);
        int w = (int) ((int) width * 0.035);
        int h1 = (int) ((int) height * 0.021);
        ivMusic.setPadding(0, h, w, 0);
        ivMedia.setPadding(0, h1, w, 0);
        viewPager = (MyCustomViewPager) findViewById(R.id.id_vp);


        adapter = new MyFragmentAdapter(getSupportFragmentManager(), viewList);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(roomNameList.length + 1);  //多设置一页
        viewPager.addOnPageChangeListener(pagerChangeListener);

        initDialog();     //初始化dialog二级页面
        initSettingDialog();    //初始化设置二级页面
        initLockDialog();
        initSecurityDialog();

    }


    //初始化dialog二级页面
    private View view;
    private void initDialog() {
        dialogSwitchRoom = new Dialog(this, R.style.CustomDialog);
        view = LayoutInflater.from(this).inflate(R.layout.switch_room_dialog, null);
        lvSwitchRoom = (ListView) view.findViewById(R.id.lv_rooms);
        tvSwitchRoomCancel = (TextView) view.findViewById(R.id.tv_cancel_switch_room);
        tvSwitchRoomCancel.setTypeface(typeFace);
        tvSwitchRoomCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSwitchRoom.hide();
            }
        });
        adapterRoomName = new RoomNameBaseAdapter(this, roomNameList);
        lvSwitchRoom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                scrollViewPager();  //滑动时隐藏控件
                viewPager.setCurrentItem(position);
                dialogSwitchRoom.hide();

            }
        });
        lvSwitchRoom.setAdapter(adapterRoomName);

        dialogSwitchRoom.setContentView(view);
        dialogSwitchRoom.setCanceledOnTouchOutside(true);//点击外部，弹框消失
        WindowManager.LayoutParams params = dialogSwitchRoom.getWindow().getAttributes();
        params.width = (int) (width * 0.82);
        //params.height = 800;   //设置dialog的宽高
        Window mWindow = dialogSwitchRoom.getWindow();
        mWindow.setGravity(Gravity.BOTTOM);
        mWindow.setAttributes(params);
    }

    //初始化dialog -------------------设置-------------------页面
    private View viewSetting;
    private TextView tvSettingTitle;
    private TextView tvSettingIP;
    private TextView tvSettingExit;
    private void initSettingDialog() {
        dialogSetting = new Dialog(this, R.style.CustomDialog);
        viewSetting = LayoutInflater.from(this).inflate(R.layout.secondary_dialog, null);
        tvSettingTitle = (TextView) viewSetting.findViewById(R.id.tv_setting_title);
        tvSettingIP = (TextView) viewSetting.findViewById(R.id.tv_setting_ip);
        tvSettingExit = (TextView) viewSetting.findViewById(R.id.tv_setting_exit);
        tvSettingTitle.setTypeface(typeFace);
        tvSettingIP.setTypeface(typeFace);
        tvSettingExit.setTypeface(typeFace);

        dialogSetting.setContentView(viewSetting);
        dialogSetting.setCanceledOnTouchOutside(true);//点击外部，弹框消失
        dialogSetting.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {   //dialog消失时触发监听
                ObjectAnimator.ofFloat(rlMain, "scaleX", 0.92f, 1f).setDuration(500).start();
                ObjectAnimator.ofFloat(rlMain, "scaleY", 0.92f, 1f).setDuration(500).start();
                //ObjectAnimator.ofFloat(viewSetting,"translationY",0,400).setDuration(500).start();
                //ObjectAnimator.ofFloat(ivBlackFront, "alpha", 0.7f, 0f).setDuration(500).start();

            }
        });
        WindowManager.LayoutParams params = dialogSetting.getWindow().getAttributes();
        params.width = width;
        params.height = (int) (height*0.6);   //设置dialog的宽高
        Window mWindow = dialogSetting.getWindow();
        mWindow.setGravity(Gravity.BOTTOM);
        mWindow.setAttributes(params);
    }

    //初始化--------------门锁------------------二级界面
    private View viewLock;
    private TextView tvLockTitle;
    private TextView tvLockOff;
    private TextView tvLockOn;
    private void initLockDialog() {
        dialogLock = new Dialog(this, R.style.CustomDialog);
        viewLock = LayoutInflater.from(this).inflate(R.layout.lock_dialog, null);
        tvLockTitle = (TextView) viewLock.findViewById(R.id.tv_setting_title);
        tvLockOff = (TextView) viewLock.findViewById(R.id.tv_close_door);
        tvLockOn = (TextView) viewLock.findViewById(R.id.tv_open_door);
        tvLockTitle.setTypeface(typeFace);
        tvLockOff.setTypeface(typeFace);
        tvLockOn.setTypeface(typeFace);

        dialogLock.setContentView(viewLock);
        dialogLock.setCanceledOnTouchOutside(true);//点击外部，弹框消失
        dialogLock.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {   //dialog消失时触发监听
                ObjectAnimator.ofFloat(rlMain, "scaleX", 0.92f, 1f).setDuration(500).start();
                ObjectAnimator.ofFloat(rlMain, "scaleY", 0.92f, 1f).setDuration(500).start();
                //ObjectAnimator.ofFloat(viewLock,"translationY",0,400).setDuration(500).start();
                //ObjectAnimator.ofFloat(ivBlackFront, "alpha", 0.7f, 0f).setDuration(500).start();

            }
        });
        WindowManager.LayoutParams params = dialogLock.getWindow().getAttributes();
        params.width = width;
        params.height = (int) (height*0.6);   //设置dialog的宽高
        Window mWindow = dialogLock.getWindow();
        mWindow.setGravity(Gravity.BOTTOM);
        mWindow.setAttributes(params);
    }

    //初始化---------------------安防---------------------二级界面
    private View viewSecurity;
    private TextView tvSecurityTitle;
    private TextView tvSecurityOff;
    private TextView tvSecurityOn;
    private void initSecurityDialog() {
        dialogSecurity = new Dialog(this, R.style.CustomDialog);
        viewSecurity = LayoutInflater.from(this).inflate(R.layout.security_dialog, null);
        tvSecurityTitle = (TextView) viewSecurity.findViewById(R.id.tv_setting_title);
        tvSecurityOff = (TextView) viewSecurity.findViewById(R.id.tv_security_off);
        tvSecurityOn = (TextView) viewSecurity.findViewById(R.id.tv_security_on);
        tvSecurityTitle.setTypeface(typeFace);
        tvSecurityOff.setTypeface(typeFace);
        tvSecurityOn.setTypeface(typeFace);

        dialogSecurity.setContentView(viewSecurity);
        dialogSecurity.setCanceledOnTouchOutside(true);//点击外部，弹框消失
        dialogSecurity.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {   //dialog消失时触发监听
                ObjectAnimator.ofFloat(rlMain, "scaleX", 0.92f, 1f).setDuration(500).start();
                ObjectAnimator.ofFloat(rlMain, "scaleY", 0.92f, 1f).setDuration(500).start();
                //ObjectAnimator.ofFloat(viewSecurity,"translationY",0,400).setDuration(500).start();
                //ObjectAnimator.ofFloat(ivBlackFront, "alpha", 0.7f, 0f).setDuration(500).start();

            }
        });
        WindowManager.LayoutParams params = dialogSecurity.getWindow().getAttributes();
        params.width = width;
        params.height = (int) (height*0.6);   //设置dialog的宽高
        Window mWindow = dialogSecurity.getWindow();
        mWindow.setGravity(Gravity.BOTTOM);
        mWindow.setAttributes(params);
    }

    //退出程序提示框dialog
    private Dialog dialogExit;
    private View viewExit;
    private TextView tvExitContent;
    private TextView tvExit;
    private TextView tvNotExit;
    private void initExitDialog(){
        dialogExit = new Dialog(this, R.style.CustomDialog);
        viewExit = LayoutInflater.from(this).inflate(R.layout.exit_dialog_layout, null);
        tvExitContent = (TextView) viewExit.findViewById(R.id.tv_exit_content);
        tvExit = (TextView) viewExit.findViewById(R.id.tv_exit);
        tvNotExit = (TextView) viewExit.findViewById(R.id.tv_not_exit);
        tvExitContent.setTypeface(typeFace);
        tvExit.setTypeface(typeFace);
        tvNotExit.setTypeface(typeFace);

        dialogExit.setContentView(viewExit);
        dialogExit.setCanceledOnTouchOutside(false);//点击外部，弹框不消失
        WindowManager.LayoutParams params = dialogExit.getWindow().getAttributes();
        params.width = (int) (width*0.8);
        params.height = (int) (height*0.3);   //设置dialog的宽高
        Window mWindow = dialogExit.getWindow();
        mWindow.setGravity(Gravity.CENTER);
        mWindow.setAttributes(params);
    }

    //二级页面的点击事件
    public void secondaryClick(View view){
        switch (view.getId()){
            case R.id.tv_setting_exit:
                Toast.makeText(MyMainActivity.this, "退出程序", Toast.LENGTH_SHORT).show();
                dialogExit.show();
                setScaleAnimation(tvSettingExit);
                break;
            case R.id.tv_close_door:
                Toast.makeText(MyMainActivity.this, "关门", Toast.LENGTH_SHORT).show();
                setScaleAnimation(tvLockOff);
                break;
            case R.id.tv_open_door:
                Toast.makeText(MyMainActivity.this, "开门", Toast.LENGTH_SHORT).show();
                setScaleAnimation(tvLockOn);
                break;
            case R.id.tv_security_off:
                Toast.makeText(MyMainActivity.this, "撤防", Toast.LENGTH_SHORT).show();
                setScaleAnimation(tvSecurityOff);
                break;
            case R.id.tv_security_on:
                Toast.makeText(MyMainActivity.this, "布防", Toast.LENGTH_SHORT).show();
                setScaleAnimation(tvSecurityOn);
                break;
            case R.id.tv_not_exit:
                Toast.makeText(MyMainActivity.this, "不退出", Toast.LENGTH_SHORT).show();
                dialogExit.hide();
                break;
            case R.id.tv_exit:
                Toast.makeText(MyMainActivity.this, "退出该账号", Toast.LENGTH_SHORT).show();
                dialogExit.hide();
                Intent intent = new Intent(MyMainActivity.this,NewLoginActivity.class);
                startActivity(intent);
                this.finish();
                break;
        }
    }



    //-----------------------------接受fragment传来的消息--------------------------------
    @Subscribe
    public void onEventMainThread(MyEventBus event) {
        String msg = event.getmMsg();
        //Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        Log.i(TAG, "onEventMainThread: 接收到的json" + msg);
        //发送tcp Socket
        if (msg!=SWITCH_ROOM_DIALOG && msg!=SETTING_DIALOG && msg!=DOOR_LOCK_DIALOG && msg!=SECURITY_DIALOG) {
            tcplongSocket.writeDate(Encrypt.encrypt(msg));
        }

        switch (event.getmMsg()) {
            case SWITCH_ROOM_DIALOG:     //选择房间dialog
                dialogSwitchRoom.show();
                break;
            case SETTING_DIALOG:     //弹出设置dialog
                initExitDialog();  //确认是否退出的三级页面
                //ObjectAnimator.ofFloat(ivBlackFront, "alpha", 0f, 0.7f).setDuration(500).start();
                ObjectAnimator.ofFloat(rlMain, "scaleX", 1f, 0.92f).setDuration(500).start();
                ObjectAnimator obj = new ObjectAnimator().ofFloat(rlMain, "scaleY", 1f, 0.92f).setDuration(500);
                obj.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        ObjectAnimator.ofFloat(viewSetting,"translationY",(float) (height*0.6),0).setDuration(500).start();
                        dialogSetting.show();
                    }
                });
                obj.start();
                break;
            case DOOR_LOCK_DIALOG:       //弹出门锁dialog
                //ObjectAnimator.ofFloat(ivBlackFront, "alpha", 0f, 0.7f).setDuration(500).start();
                ObjectAnimator.ofFloat(rlMain, "scaleX", 1f, 0.92f).setDuration(500).start();
                ObjectAnimator obj1 = new ObjectAnimator().ofFloat(rlMain, "scaleY", 1f, 0.92f).setDuration(500);
                obj1.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        ObjectAnimator.ofFloat(viewLock,"translationY",(float) (height*0.6),0).setDuration(500).start();
                        dialogLock.show();
                    }
                });
                obj1.start();
                break;
            case SECURITY_DIALOG:      //弹出安防dialog
                //ObjectAnimator.ofFloat(ivBlackFront, "alpha", 0f, 0.7f).setDuration(500).start();
                ObjectAnimator.ofFloat(rlMain, "scaleX", 1f, 0.92f).setDuration(500).start();
                ObjectAnimator obj2 = new ObjectAnimator().ofFloat(rlMain, "scaleY", 1f, 0.92f).setDuration(500);
                obj2.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        ObjectAnimator.ofFloat(viewSecurity,"translationY",(float) (height*0.6),0).setDuration(500).start();
                        dialogSecurity.show();
                    }
                });
                obj2.start();
                break;

        }
    }


    View.OnClickListener mediaListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.iv_music:
                    Toast.makeText(MyMainActivity.this, "音乐", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.iv_media:
                    Toast.makeText(MyMainActivity.this, "媒体", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    private void pullMenu(MyFragment myF) {
        //自定义的下拉监听
        myF.setPullDownMenuListener(new PullDownMenuListener() {
            @Override
            public void pullDown(boolean b, boolean isIconShow) {
                b1 = isIconShow;
                if (b == false && isIconShow == false) {      //下拉菜单出现时
                    //隐藏桌面小控件
                    ivMusic.setVisibility(View.GONE);
                    ivMedia.setVisibility(View.GONE);
                    //设置viewpager不能滑动
                    viewPager.setCanScroll(false);
                } else if (b == true && isIconShow == true) {     //下拉菜单隐藏时
                    ivMusic.setVisibility(View.VISIBLE);
                    ivMedia.setVisibility(View.VISIBLE);
                    viewPager.setCanScroll(true);
                } else if (b == false && isIconShow == true) {
                    ivMusic.setVisibility(View.VISIBLE);
                    ivMedia.setVisibility(View.VISIBLE);
                } else if (b == true && isIconShow == false) {
                    ivMusic.setVisibility(View.GONE);
                    ivMedia.setVisibility(View.GONE);
                }

            }

            @Override
            public void scrollPager() {

            }
        });
    }

    //viewPager滑动监听
    private int roomPostion = 0;  //房间号
    ViewPager.OnPageChangeListener pagerChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            //Log.i(TAG, "onPageScrolled: --------------"+position);
        }

        @Override
        public void onPageSelected(int position) {
            //传出position
            roomPostion = position;
            adapterRoomName.changeSelected(position);   //传递当前的页面位置position
        }

        //state状态  0：无触碰；  1：拖动；   2：松手
        @Override
        public void onPageScrollStateChanged(int state) {
            //Log.i(TAG, "onPageScrollStateChanged: =-=-=-=-=-=-=-=" + state);
            pagerState = state;
            boolean isChange = false;
            if (state == 2) {
                isChange = true;
            }

            if (state == 1) {
                EventBus.getDefault().post(new MyEventBus2(SCROLL_FRAGMENT_START));
                scrollViewPager();
            }
            if (state == 0) {
                EventBus.getDefault().post(new MyEventBus2(SCROLL_FRAGMENT_END));
                downScrollViewPager();
            }
            //onMainListener.onMainAction(pagerState);   //Activity向Fragment通信
        }
    };

    //滑动viewpager时，控件消失
    private void scrollViewPager() {
        ivMusic.setVisibility(View.GONE);
        ivMedia.setVisibility(View.GONE);
    }

    //滑动结束后，控件显现
    private void downScrollViewPager() {
        if (b1 == false) {
            ivMusic.setVisibility(View.GONE);
            ivMedia.setVisibility(View.GONE);
        } else if (b1 == true) {
            showAnimation();
            ivMusic.setVisibility(View.VISIBLE);
            ivMedia.setVisibility(View.VISIBLE);
        }

    }

    //控件滑出显示的动画
    private void showAnimation() {
        ObjectAnimator.ofFloat(ivMusic, "translationX", 200, -10, 0).setDuration(800).start();
        ObjectAnimator oa1 = ObjectAnimator.ofFloat(ivMedia, "translationX", 250, -10, 0);
        oa1.setDuration(1000);
        //oa1.setStartDelay(100);
        oa1.start();

    }


    //点击按钮缩放动画
    private void setScaleAnimation(View view){
        ObjectAnimator.ofFloat(view,"scaleX",1f,0.8f,1f).setDuration(600).start();
        ObjectAnimator.ofFloat(view,"scaleY",1f,0.8f,1f).setDuration(600).start();
    }

    private ArrayList<MyOnTouchListener> onTouchListeners = new ArrayList<MyOnTouchListener>(10);

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        for (MyOnTouchListener listener : onTouchListeners) {
            listener.onTouch(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    public void registerMyOnTouchListener(MyOnTouchListener myOnTouchListener) {
        onTouchListeners.add(myOnTouchListener);
    }

    public void unregisterMyOnTouchListener(MyOnTouchListener myOnTouchListener) {
        onTouchListeners.remove(myOnTouchListener);
    }

    public interface MyOnTouchListener {
        public boolean onTouch(MotionEvent ev);
    }


}
