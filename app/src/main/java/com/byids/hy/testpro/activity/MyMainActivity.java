package com.byids.hy.testpro.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.byids.hy.testpro.MyEventBus;
import com.byids.hy.testpro.MyEventBus2;
import com.byids.hy.testpro.MyEventBusControlLight;
import com.byids.hy.testpro.PullDownMenuListener;
import com.byids.hy.testpro.R;
import com.byids.hy.testpro.TCPLongSocketCallback;
import com.byids.hy.testpro.TcpLongSocket;
import com.byids.hy.testpro.View.MyCustomViewPager;
import com.byids.hy.testpro.View.MyEventBusPullMenu;
import com.byids.hy.testpro.adapter.ControlLightBaseAdapter;
import com.byids.hy.testpro.adapter.MyFragmentAdapter;
import com.byids.hy.testpro.adapter.RoomNameBaseAdapter;
import com.byids.hy.testpro.customSceneBean.AllCustomScene;
import com.byids.hy.testpro.fragment.MyFragment;
import com.byids.hy.testpro.newBean.CommandData;
import com.byids.hy.testpro.service.TcpConnectService;
import com.byids.hy.testpro.service.UDPBroadcastService;
import com.byids.hy.testpro.utils.AES;
import com.byids.hy.testpro.utils.CommandJsonUtils;
import com.byids.hy.testpro.utils.CustomCroutonUtil;
import com.byids.hy.testpro.utils.Encrypt;
import com.byids.hy.testpro.utils.LongLogCatUtil;
import com.byids.hy.testpro.utils.NetworkStateUtil;
import com.byids.hy.testpro.utils.VibratorUtil;
import com.byids.hy.testpro.utils.crouton.Crouton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.animation.ObjectAnimator.ofFloat;
import static java.lang.Integer.parseInt;

/**
 * 主界面：
 * 第一次登陆通过外网获取房间信息后
 * Created by hy on 2016/8/15.
 */
public class MyMainActivity extends FragmentActivity {
    private String TAG = "result";
    private boolean isOnMainActivity = true;
    private static final String SWITCH_ROOM_DIALOG = "1";
    private static final String SCROLL_FRAGMENT_START = "2";    //滑动viewpager
    private static final String SCROLL_FRAGMENT_END = "3";      //结束滑动viewpager
    private static final String SETTING_DIALOG = "4";     //打开设置二级界面
    private static final String DOOR_LOCK_DIALOG = "5";     //打开门锁二级界面
    private static final String SECURITY_DIALOG = "6";     //打开安防二级界面
    private static final String LIGHT_DIALOG = "7";         //打开灯光二级界面

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
    private RelativeLayout rlMusic;
    private RelativeLayout rlMedia;
    private RelativeLayout rlMain;  //主界面
    private ImageView ivBlackFront;    //黑色的遮罩
    private ImageView ivMusic;
    private ImageView ivSleepMoon;
    private ImageView ivSleepStars;
    private TextView tvMainBlank1;
    private TextView tvButtonComeHome;
    private TextView tvButtonGoOut;
    private TextView tvHouseOwnerName;
    private TextView tvHouseTimeLeft;
    private TextView tvHouseTimeRight;
    private TextView tvHouseTemp;


    //二级页面dialog
    private Dialog dialogSwitchRoom;
    private Dialog dialogSetting;
    private Dialog dialogLock;
    private Dialog dialogSecurity;
    private Dialog dialogMusic;
    private Dialog dialogLight;
    private ListView lvSwitchRoom;
    private TextView tvSwitchRoomCancel;
    private RoomNameBaseAdapter adapterRoomName;


    //背景图片
    private int[] ivBackList = { R.mipmap.back_1,R.mipmap.back_2, R.mipmap.back_3,R.mipmap.back_4,
            R.mipmap.back_5, R.mipmap.back_6, R.mipmap.back_7, R.mipmap.back_8,R.mipmap.back_9,
            R.mipmap.back_10};


    //房间名数组kk
    private String[] roomNameList;
    private String[] roomDBNameList;
    //private HomeAttr homeAttr = new HomeAttr();



    private MyFragment myFragment1;

    private String ip; //home  ip地址
    private boolean isFirstLogin;
    private boolean isByWANLogin;      //是否通过外网登陆
    private boolean needWANConnect;        //是否找不到正确的主机，需要外网连接
    private String uname = "";
    private String pwd = "";
    private String hid = "56e276f3736fb0872c69d876";        //换成主机发过来的token
    private String host_ip;     //主机地址
    private String token;     //外网登录服务器时给的token，只有当第二次登录时才会失效
    private CommandData commandData = null;        //房间json数据总类
    private String protocol = "byids-A";

    //----------------TCP socket内网通信---------------------
    public static final int DEFAULT_PORT = 57816;
    private TcpLongSocket tcplongSocket;
    private ConnectTcp connectTCP;
    private boolean connectTcp = true;      //为true时，不断向主机发送数据包，保持连接

    //---------------------TCP Socket外网通信------------------------
    private static final int DEFAULT_PORT_WAN = 30002;
    private static final String ip_WAN = "115.29.97.189";
    //private static final String ip_WAN = "192.168.10.230";
    private TcpLongSocket tcpLongSocketWAN;   //外网通信的Tcp Socket长连接

    //断线重连部分
    private AppConnection appConnection;
    private AppConnectionWAN appConnectionWAN;

    private CustomCroutonUtil customCroutonUtil;
    private boolean isConnectBreak = false;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:         //外网
                    Log.i("hongyang", "run:外网连接状态 "+tcpLongSocketWAN.getConnectStatus());
                    if (tcpLongSocketWAN.getConnectStatus()){
                        EventBus.getDefault().post(new MyEventBus2("12"));     //外网连接
                        if (isConnectBreak){
                            customCroutonUtil.makeTopToast("连接上外网",1000);
                            isConnectBreak = false;
                        }
                    }else {
                        EventBus.getDefault().post(new MyEventBus2("22"));
                        if (!isConnectBreak){
                            customCroutonUtil.makeTopToast("连接断开",1000);
                            isConnectBreak = true;
                        }
                    }
                    break;
                case 2:         //内网
                    Log.i("hongyang", "run:内网连接状态 "+tcplongSocket.getConnectStatus());
                    if (tcplongSocket.getConnectStatus()){
                        EventBus.getDefault().post(new MyEventBus2("11"));    //内网连接
                        if (isConnectBreak){
                            customCroutonUtil.makeTopToast("连接上内网",1000);
                            isConnectBreak = false;
                        }
                    }else {
                        EventBus.getDefault().post(new MyEventBus2("22"));
                        if (!isConnectBreak){
                            customCroutonUtil.makeTopToast("连接断开",1000);
                            isConnectBreak = true;
                        }
                    }
                    //appConnection.start();       //开启后台断线重连线程
                    break;
                case 3:
                    Toast.makeText(MyMainActivity.this, "主机未连接，请连上主机后重试", Toast.LENGTH_LONG).show();
                    break;
                case 4:
                    Toast.makeText(MyMainActivity.this, "Token无效", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }
    };

    private Handler handlerUI = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    /*ObjectAnimator.ofFloat(rlMusic,"alpha",0f,1f).setDuration(200).start();
                    ObjectAnimator.ofFloat(rlMedia,"alpha",0f,1f).setDuration(200).start();*/
                    rlMusic.setVisibility(View.VISIBLE);
                    rlMedia.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    setButtonsAnimationAppear();          //设置主界面信息控件的出现动画
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
            /*String a = udpBinder.getHostIp();
            Log.i(TAG, "onServiceConnected: ------获取后台接受的主机ip------"+a);*/
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    //开线程，监听连接是否中断
    private String strVerify = "";       //判断是否验证通过，如果不通过，则不需要重连
    class AppConnection extends Thread{
        @Override
        public void run() {
            super.run();
            int flag = 0;
            try {
                sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (isOnMainActivity && tcplongSocket!=null /*&& !strVerify.equals("Verify error")*/){
                if (tcplongSocket.getConnectStatus()){      //已经保持内网连接
                    Log.i("reconnection_hy", "run:已经保持内网连接 ,心跳"+tcplongSocket);
                    Log.i("reconnection_hy", "run: 有没有网:"+NetworkStateUtil.isNetworkAvailable(MyMainActivity.this));
                    flag = 0;
                }else if (!tcplongSocket.getConnectStatus()){
                    Log.i("reconnection_hy", "run: 内网连接中断......udp收到的check为："+udpBinder.getUdpCheck()+"-----"+udpBinder.getHostIp());
                    Message message = new Message();
                    message.what = 2;
                    handler.sendMessageDelayed(message,300);
                    flag++;

                    Log.i("reconnection_hy", "run: 有没有网:"+NetworkStateUtil.isNetworkAvailable(MyMainActivity.this));
                    if (NetworkStateUtil.isNetworkAvailable(MyMainActivity.this) && flag>1 ){      //断线超过5秒，则断线重连
                        //内网中断后，在连接上网络的情况下，断线重连
                        tcplongSocket.close();
                        //tcplongSocket.startConnect(host_ip,DEFAULT_PORT);
                        tcplongSocket = new TcpLongSocket(new ConnectTcp());
                        tcplongSocket.startConnect(host_ip, DEFAULT_PORT);
                        Message message1 = new Message();
                        message1.what = 2;
                        handler.sendMessageDelayed(message1,300);
                        try {
                            sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //外网断线重连
    class AppConnectionWAN extends Thread{
        @Override
        public void run() {
            super.run();
            int flag = 0;
            try {
                sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (isOnMainActivity && tcpLongSocketWAN!=null /*&& !strVerify.equals("Verify error")*/){
                if (tcpLongSocketWAN.getConnectStatus()){      //已经保持内网连接
                    Log.i("reconnection_hy", "run:已经保持外网连接 ,心跳"+tcpLongSocketWAN);
                    Log.i("reconnection_hy", "run: 有没有网:"+NetworkStateUtil.isNetworkAvailable(MyMainActivity.this));
                    flag = 0;
                }else if (!tcpLongSocketWAN.getConnectStatus()){
                    Log.i("reconnection_hy", "run: 外网连接中断......udp收到的check为："+udpBinder.getUdpCheck()+"-----"+udpBinder.getHostIp());
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessageDelayed(message,300);
                    flag++;

                    Log.i("reconnection_hy", "run: 有没有网:"+NetworkStateUtil.isNetworkAvailable(MyMainActivity.this));
                    if (NetworkStateUtil.isNetworkAvailable(MyMainActivity.this) && flag>1 ){      //断线超过5秒，则断线重连
                        //内网中断后，在连接上网络的情况下，断线重连
                        tcpLongSocketWAN.close();
                        //tcplongSocket.startConnect(host_ip,DEFAULT_PORT);
                        tcpLongSocketWAN = new TcpLongSocket(new ConnectTcpWAN());
                        tcpLongSocketWAN.startConnect(ip_WAN,DEFAULT_PORT_WAN);
                        Message message1 = new Message();
                        message1.what = 1;
                        handler.sendMessageDelayed(message1,300);
                        try {
                            sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC); //让音量键固定为媒体音量控制
        setContentView(R.layout.my_main_layout);
        //注册EventBus
        EventBus.getDefault().register(this);
        reciveIntent();
        initView();    //初始化界面，布局
        //startTcpService();

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("activity_cycle", "onStart: onStartonStart");
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        Log.i("activity_cycle", "onResume: onResumeonResume"+appConnection.isAlive());
        Crouton.cancelAllCroutons();
        /*isOnMainActivity = true;
        if (!appConnectionWAN.isAlive()){
            Log.i(TAG, "onResume: -=-isInterrupted????????????????????????");
            appConnectionWAN = new AppConnectionWAN();
            appConnectionWAN.start();
        }
        if (!appConnection.isAlive()){
            Log.i(TAG, "onResume: -=-isInterrupted????????????????????????");
            appConnection = new AppConnection();
            appConnection.start();

        }
        if (host_ip==null||host_ip.equals("")||needWANConnect||isByWANLogin){
            //外网tcp连接
            tcpLongSocketWAN = new TcpLongSocket(new ConnectTcpWAN());
            tcpLongSocketWAN.startConnect(ip_WAN,DEFAULT_PORT_WAN);
        }else {
            //内网Tcp连接
            connectTCP = new ConnectTcp();
            tcplongSocket = new TcpLongSocket(connectTCP);
            tcplongSocket.startConnect(host_ip, DEFAULT_PORT);
            Message message = new Message();
            message.what = 2;
            handler.sendMessageDelayed(message,300);
        }*/
    }
    
    /*@Override
    protected void onPause() {
        super.onPause();
        Log.i("activity_cycle", "onPause: onPauseonPause");
        //============================关闭连接socket，中断断线重连的线程==============================
        appConnection.interrupt();     //中断线程
        appConnectionWAN.interrupt();
        isOnMainActivity = false;
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    sleep(11000);
                    Log.i(TAG, "run:-------------1100000000000000------------ ");
                    isConnectWAN = false;
                    isConnectLAN = false;
                    if (tcpLongSocketWAN!=null){
                        tcpLongSocketWAN.close();    //关闭外网的连接
                    }
                    if (tcplongSocket!=null){
                        tcplongSocket.close();      //关闭内网连接
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }*/

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("activity_cycle", "onStop: onStoponStop");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("activity_cycle", "onDestroy: onDestroy");
        EventBus.getDefault().unregister(this);  //反注册EventBus
        Crouton.cancelAllCroutons();
        //connectTcp = false;    //关闭线程
        Log.i(TAG, "onDestroy: ----------------主界面退出--------------onDestroy---------------------------");
        if (dialogExit!=null){
            dialogExit.hide();
            dialogExit = null;
        }
        if (dialogSetting!=null){
            dialogSetting.hide();
            //dialogSetting.dismiss();
            dialogSetting = null;
        }
        //System.exit(0);          //确定该Activity销毁时就是程序退出时，才能调用该方法来关闭整个应用
        unbindService(connection);
    }


    //------------------------------新版的逻辑判断 外网连接或内网连接---------------------------------
    private void reciveIntent(){
        uname = getIntent().getStringExtra("uname");
        pwd = getIntent().getStringExtra("pwd");
        host_ip = getIntent().getStringExtra("host_ip");
        //host_ip = "192.168.10.167";
        //host_ip = "192.168.3.16";
        token = getIntent().getStringExtra("token");
        isFirstLogin = getIntent().getBooleanExtra("isFirstLogin",false);       //是否第一次登陆
        isByWANLogin = getIntent().getBooleanExtra("isByWANLogin",false);      //是否通过外网登陆
        needWANConnect = getIntent().getBooleanExtra("needWANConnect",false);   //是否需要外网连接
        commandData = (CommandData) getIntent().getSerializableExtra("commandData");
        Log.i(TAG, "run: 是否第一次登陆："+isFirstLogin+"     是否外网登陆的："+isByWANLogin);
        initFragment();     //初始化fragment数据
        if (isByWANLogin){
            SharedPreferences sp = getSharedPreferences("user_token",MODE_PRIVATE);        //文件名，文件类型
            sp.edit().putString("token",token).commit();
        }
        Log.i("hongyang", "reciveIntent: ---MyMainActivity--------获取uname:"+uname+"-------pwd:"+pwd);
        if (uname!=null){
            saveUserInform(uname,pwd);     //本地储存
        }
        Log.i("hongyang", "reciveIntent: !!!!!!!!!!!MainActivity接收到的host_ip:"+host_ip+"!!!!外网第一次登陆获取的token："+token);
        //host_ip = null;    //连外网,以后删除
        new Thread(){
            @Override
            public void run() {
                super.run();
                Log.i(TAG, "run: 是否通过外网登陆的："+isByWANLogin);
                if (host_ip==null||host_ip.equals("")||needWANConnect||isByWANLogin){
                    Log.i("hongyang", "reciveIntent: 内网不通，尝试连接外网");
                    Log.i("byid_hy_intent", "run: 外网连接用的token:"+token);
                    //外网Tcp连接
                    tcpLongSocketWAN = new TcpLongSocket(new ConnectTcpWAN());
                    tcpLongSocketWAN.startConnect(ip_WAN,DEFAULT_PORT_WAN);
                }else {
                    Log.i("hongyang", "reciveIntent: 连接内网");
                    //内网Tcp连接
                    connectTCP = new ConnectTcp();
                    tcplongSocket = new TcpLongSocket(connectTCP);
                    tcplongSocket.startConnect(host_ip, DEFAULT_PORT);
                    Message message = new Message();
                    message.what = 2;
                    handler.sendMessageDelayed(message,300);
                }
            }
        }.start();

    }

    //初始化各个房间fragment的数据
    private void initFragment(){

        if (null!=commandData){
            protocol = commandData.getProtocol();
            //Log.e(TAG, "reciveIntent: alljson:"+allJsonData.getCommandUser().getHid());
            int roomsIndex = commandData.getProfile().getRooms().getArray().size();
            roomNameList = new String[roomsIndex];
            roomDBNameList = new String[roomsIndex];
            for (int i=0;i<roomsIndex;i++){
                roomNameList[i] = commandData.getProfile().getRooms().getArray().get(i).getRoom_zh_name();
                roomDBNameList[i] = commandData.getProfile().getRooms().getArray().get(i).getRoom_db_name();
            }

            int[] randomNum = randomCommon(0,9,roomsIndex);
            //activity给fragment传递数据
            AllCustomScene allCustomScene = new AllCustomScene();
            for (int i = 0; i < roomsIndex; i++) {
                myFragment1 = new MyFragment(i,commandData, roomNameList[i], roomDBNameList[i], ivBackList, token, uname, pwd, protocol,allCustomScene,randomNum[i]);         //房间id,房间名数组，房间拼音名数组，背景图片数组，活跃的控件数组,hid,uname,pwd
                pullMenu(myFragment1);
                viewList.add(myFragment1);
            }
        }
    }

    //取随机数
    public static int[] randomCommon(int min, int max, int n){
        if (n > (max - min + 1) || max < min) {
            return null;
        }
        int[] result = new int[n];
        int count = 0;
        while(count < n) {
            int num = (int) (Math.random() * (max - min)) + min;
            boolean flag = true;
            for (int j = 0; j < n; j++) {
                if(num == result[j]){
                    flag = false;
                    break;
                }
            }
            if(flag){
                result[count] = num;
                count++;
            }
        }
        return result;
    }

    //SharedPreferences本地储存用户信息
    private void saveUserInform(String userName,String password){
        SharedPreferences sp = getSharedPreferences("user_inform",MODE_PRIVATE);        //文件名，文件类型
        sp.edit().putString("userName",userName).putString("password",password).commit();
        Log.i("hongyang", "reciveIntent: -----------存储uname，pwd-------------"+uname+"-----"+pwd);
    }

    //----------------------内网通信的Tcp Socket连接--------------------------
    private boolean isConnectLAN = false;
    private JSONObject objHeart;  //心跳数据
    private class ConnectTcp implements TCPLongSocketCallback {

        @Override
        public void connected() {
            Log.i(TAG, "connected: ============================连接上了内网============================");
            Log.i("MAIN", String.valueOf(tcplongSocket.getConnectStatus()));
            VibratorUtil.Vibrate(MyMainActivity.this, 300);
            JSONObject checkCommandData = new JSONObject();
            try {
                checkCommandData.put("kong", "keys");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String checkJson = CommandJsonUtils.getCommandJson(1, checkCommandData, token, uname, pwd, String.valueOf(System.currentTimeMillis()));
            Log.i("result", "check" + checkJson);
            tcplongSocket.writeDate(Encrypt.encrypt(checkJson));
            //============================发内网心跳数据=========================
            isConnectLAN = true;
            try {
                objHeart = new JSONObject("{\"hehe\":\"Hello Server\"}");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    while (isConnectLAN){
                        tcplongSocket.writeDate(Encrypt.encrypt(CommandJsonUtils.getCommandJson(100, objHeart, token, uname, pwd, String.valueOf(System.currentTimeMillis()))));
                        try {
                            sleep(3000);    //隔三秒发送心跳
                            Log.i("xintiao", "run: ------------我的内网心跳---------------");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
        }

        @Override
        public void receive(byte[] buffer) {             //接收主机信息

            if (buffer.length>32 && buffer.length<200){
                //LongLogCatUtil.logE("result",testDecryptByte(buffer));
                String verifyStr = judgeVerify(testDecryptByte(buffer));
                if ("Verify error".equals(verifyStr)){
                        tcplongSocket.close();    //验证失败，关闭内网连接
                    //外网Tcp连接
                    tcpLongSocketWAN = new TcpLongSocket(new ConnectTcpWAN());
                    tcpLongSocketWAN.startConnect(ip_WAN,DEFAULT_PORT_WAN);
                }
            }else {
                Log.e(TAG, "receive: 收到的数据不是加密数据或数据损坏");

            }

        }

        @Override
        public void disconnect() {
            tcplongSocket.close();
            isConnectLAN =  false;
            EventBus.getDefault().post(new MyEventBus2("22"));
        }

        private String judgeVerify(String strRec){
            String strJson = strRec.substring(0,strRec.length()-16);
            Log.i("judgeVerify", "judgeVerify: "+strJson);
            LongLogCatUtil.logE("MyMainActivity","MyMainActivity接收到的主机回复消息："+strJson);
            try {
                JSONObject jsonObject = new JSONObject(strJson);
                strVerify = jsonObject.getString("CommandData");
                Log.i(TAG, "judgeVerify:jsonObjectStr: "+strVerify);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return strVerify;
        }
    }

    private byte[] new_sendByte;
    private byte[] nnew_sendByte;
    private byte[] ivByte;
    private byte[] dataByte;
    private byte[] a;
    private String strRoomInfo;
    private String testDecryptByte(byte[] sendByte){
        new_sendByte = Arrays.copyOfRange(sendByte,12,sendByte.length);
        nnew_sendByte = Arrays.copyOfRange(new_sendByte,0,new_sendByte.length-4);
        //Log.i(TAG, "test: ************************newsendByte"+nnew_sendByte.length);
        //Log.i(TAG, "test: *************newsendByte**************"+byteStringLog(nnew_sendByte));
        ivByte = Arrays.copyOfRange(nnew_sendByte,nnew_sendByte.length-16,nnew_sendByte.length);
        dataByte = Arrays.copyOfRange(nnew_sendByte,0,nnew_sendByte.length-16);
        //Log.i(TAG, "test: *****************加密向量长度*******newsendByte"+ivByte.length);
        //Log.i(TAG, "test: *************newsendByte*****加密向量*********"+byteStringLog(ivByte));
        //Log.i(TAG, "test: *****************加密数据长度*******newsendByte"+dataByte.length);
        //Log.i(TAG, "test: *************newsendByte*****加密数据*********"+byteStringLog(dataByte));
        a = AES.decrypt(dataByte,ivByte);
        //Log.i(TAG, "testDecryptByte: dataByte:"+byteStringLog(dataByte));
        //Log.i(TAG, "testDecryptByte: a:"+byteStringLog(a));
        if (a!=null){
            strRoomInfo = new String(a);
        }
        //Log.i(TAG, "testDecryptByte: !!!!!!!!!!!!!!!!!!!!!!!!!!!!"+a.length+"!!!!!!!!"+strRoomInfo);
        return strRoomInfo;
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
    }

    //-------------------外网通信Tcp Socket连接----------------------
    private boolean isConnectWAN = false;
    private class ConnectTcpWAN implements TCPLongSocketCallback{

        @Override
        public void connected() {
            Log.i(TAG, "connected: ********连接上了外网************");

            VibratorUtil.Vibrate(MyMainActivity.this, 200);

            byte[] postHead = "#byids".getBytes();
            //Log.i(TAG, "onClick: ------------------"+byteStringLog(postHead));
            byte[] postType = new byte[]{2};
            byte[] postLenth = new byte[]{0,0,0,32};

            byte[] data3 = new byte[postHead.length+postType.length];
            System.arraycopy(postHead,0,data3,0,postHead.length);
            System.arraycopy(postType,0,data3,postHead.length,postType.length);

            byte[] data = new byte[data3.length+postLenth.length];
            System.arraycopy(data3,0,data,0,data3.length);
            System.arraycopy(postLenth,0,data,data3.length,postLenth.length);

            Log.i("byid_hy_intent", "connected: -----token-------"+token);
            /*String tokenStr = null;
            try {
                JSONObject obj = new JSONObject(token);
                tokenStr = obj.getString("token");
            } catch (JSONException e) {
                e.printStackTrace();
            }*/

            //把十六进制token转byte数组,两个一转，一共16组
            //byte[] tokenBytes = hexStr2Bytes(token);

            //token转byte数组,一个一转，一共32组
            byte[] tokenBytes = token.trim().getBytes();
            //Log.i(TAG, "onClick: --------"+byteStringLog(tokenBytes));

            byte[] toConnectTcpLong = new byte[data.length+tokenBytes.length];
            System.arraycopy(data,0,toConnectTcpLong,0,data.length);
            System.arraycopy(tokenBytes,0,toConnectTcpLong,data.length,tokenBytes.length);


            Log.i("hy_result", "onClick: ----------------------------"+byteStringLog(toConnectTcpLong));

            tcpLongSocketWAN.writeDate(toConnectTcpLong);
        }

        @Override
        public void receive(byte[] buffer) {

            Log.i("byid_hy_intent", "receive: buffer.length:"+buffer.length);
            Log.i(TAG, "receive: 收到的buffer："+byteStringLog(buffer));
            //取最后一位，0：验证token成功；1：验证失败
            if (buffer.length>0){
                Log.i(TAG, "receive: **********收到的buffer**********："+new String(buffer));
                switch (buffer[buffer.length-1]){
                    case -56:
                        Log.i(TAG, "receive: ---验证token成功---");
                        Message message = new Message();
                        message.what = 1;
                        handler.sendMessageDelayed(message,300);       //连接内网成功
                        isConnectWAN = true;
                        new Thread(){
                            @Override
                            public void run() {
                                super.run();
                                byte[] heartbeat = new byte[]{35,98,121,105,100,115,3};
                                //Log.i(TAG, "run: 心跳byte【】"+byteStringLog(heartbeat));

                                while (isConnectWAN){

                                    tcpLongSocketWAN.writeDate(heartbeat);
                                    try {
                                        sleep(3000);    //隔三秒发送心跳
                                        Log.i("xintiao", "run: ------------我的心跳---------------");
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }.start();
                        break;
                    case -112:
                        Message msg =  new Message();
                        msg.what = 3;
                        handler.sendMessage(msg);       //主机未连接
                        break;
                    case -55:
                        Message msg1 =  new Message();
                        msg1.what = 4;
                        handler.sendMessage(msg1);
                        break;
                    default:
                        break;
                }
            }

        }

        @Override
        public void disconnect() {
            Log.i(TAG, "disconnect: ------disconnect-----------disconnect-------------disconnect-----");
            EventBus.getDefault().post(new MyEventBus2("22"));
            tcpLongSocketWAN.close();
            isConnectWAN = false;
        }
    }

    //把十六进制token转byte数组
    public static byte[] hexStr2Bytes(String src) {
        int m = 0, n = 0;
        int l = src.length() / 2;
        System.out.println(l);
        byte[] ret = new byte[l];
        for (int i = 0; i < l; i++) {
            m = i * 2 + 1;
            n = m + 1;
            ret[i] = uniteBytes(src.substring(i * 2, m), src.substring(m, n));
        }
        return ret;
    }
    private static byte uniteBytes(String src0, String src1) {
        byte b0 = Byte.decode("0x" + src0).byteValue();
        b0 = (byte) (b0 << 4);
        byte b1 = Byte.decode("0x" + src1).byteValue();
        byte ret = (byte) (b0 | b1);
        return ret;
    }


    private void initView() {
        customCroutonUtil = new CustomCroutonUtil(MyMainActivity.this);

        appConnection = new AppConnection();
        appConnection.start();
        appConnectionWAN = new AppConnectionWAN();
        appConnectionWAN.start();

        Intent bindIntent = new Intent(this, UDPBroadcastService.class);
        bindService(bindIntent, connection, BIND_AUTO_CREATE); // 绑定服务

        typeFace = Typeface.createFromAsset(getAssets(), "fonts/xiyuanti.ttf");
        //获取房间信息
        //roomNameList = getIntent().getStringArrayExtra("roomNameList");
        //roomDBNameList = getIntent().getStringArrayExtra("roomDBNameList");
        //homeAttr = (HomeAttr) getIntent().getSerializableExtra("homeAttr");

        /*Rooms rooms = new Rooms();
        for (int i=0;i<rooms.getArray().size();i++){
            roomNameList[i] = rooms.getArray().get(i).getRoom_zh_name();
            roomDBNameList[i] = rooms.getArray().get(i).getRoom_db_name();
        }*/



        WindowManager wm = this.getWindowManager();
        width = wm.getDefaultDisplay().getWidth();
        height = wm.getDefaultDisplay().getHeight();
        //Log.i(TAG, "initView: ____________________" + width + "___________________" + height);

        ivBlackFront = (ImageView) findViewById(R.id.iv_black_front);
        rlMain = (RelativeLayout) findViewById(R.id.rl_main);
        rlMusic = (RelativeLayout) findViewById(R.id.rl_music);
        rlMedia = (RelativeLayout) findViewById(R.id.rl_media);
        ivMusic = (ImageView) findViewById(R.id.iv_music_icon);
        ivSleepMoon = (ImageView) findViewById(R.id.iv_sleep_moon);
        ivSleepStars = (ImageView) findViewById(R.id.iv_sleep_stars);
        tvMainBlank1 = (TextView) findViewById(R.id.tv_main_blank_1);
        tvButtonComeHome = (TextView) findViewById(R.id.tv_button_come_home);
        tvButtonGoOut = (TextView) findViewById(R.id.tv_button_go_out);
        tvHouseOwnerName = (TextView) findViewById(R.id.tv_house_owner_name);
        tvHouseTimeLeft = (TextView) findViewById(R.id.tv_house_time_left);
        tvHouseTimeRight = (TextView) findViewById(R.id.tv_house_time_right);
        tvHouseTemp = (TextView) findViewById(R.id.tv_house_temp);

        tvButtonComeHome.setTypeface(typeFace);
        tvButtonGoOut.setTypeface(typeFace);
        tvHouseOwnerName.setTypeface(typeFace);
        tvHouseTimeLeft.setTypeface(typeFace);
        tvHouseTimeRight.setTypeface(typeFace);
        tvHouseTemp.setTypeface(typeFace);

        tvButtonComeHome.setOnClickListener(mediaListener);
        tvButtonGoOut.setOnClickListener(mediaListener);
        rlMusic.setOnClickListener(mediaListener);
        rlMedia.setOnClickListener(mediaListener);
        int h = (int) ((int) height * 0.03);
        int w = (int) ((int) width * 0.035);
        int h1 = (int) ((int) height * 0.021);
        rlMusic.setPadding(0, h, w, 0);
        rlMedia.setPadding(0, h1, w, 0);
        viewPager = (MyCustomViewPager) findViewById(R.id.id_vp);


        adapter = new MyFragmentAdapter(getSupportFragmentManager(), viewList);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(roomNameList.length + 1);  //多设置一页
        viewPager.addOnPageChangeListener(pagerChangeListener);

        initDialog();     //初始化dialog二级页面
        initSettingDialog();    //初始化设置二级页面
        initLockDialog();
        initSecurityDialog();
        initMusicDialog();
        initLightDialog();
    }

    //开始执行service
    private void startTcpService(){
        Intent intentService = new Intent(this, TcpConnectService.class);
        startService(intentService);         //启动服务
    }


    //初始化选择房间dialog二级页面
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
    private TextView tvSettingClock;
    private TextView tvSettingHelp;
    private RelativeLayout rlSheZhi;
    private void initSettingDialog() {
        dialogSetting = new Dialog(this, R.style.CustomDialog);
        viewSetting = LayoutInflater.from(this).inflate(R.layout.secondary_dialog, null);
        tvSettingTitle = (TextView) viewSetting.findViewById(R.id.tv_setting_title);
        tvSettingIP = (TextView) viewSetting.findViewById(R.id.tv_setting_ip);
        tvSettingExit = (TextView) viewSetting.findViewById(R.id.tv_setting_exit);
        tvSettingClock = (TextView) viewSetting.findViewById(R.id.tv_setting_clock);
        tvSettingHelp = (TextView) viewSetting.findViewById(R.id.tv_setting_help);
        rlSheZhi = (RelativeLayout) viewSetting.findViewById(R.id.ll_shezhi_title);
        tvSettingTitle.setTypeface(typeFace);
        tvSettingIP.setTypeface(typeFace);
        tvSettingExit.setTypeface(typeFace);
        tvSettingClock.setTypeface(typeFace);
        tvSettingHelp.setTypeface(typeFace);
        rlSheZhi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSetting.dismiss();
            }
        });

        //设置连上的主机ip
        Log.e(TAG, "initSettingDialog: host_iphost_iphost_iphost_iphost_iphost_iphost_ip："+host_ip);
        if (host_ip==null||host_ip.equals("")){
            tvSettingIP.setText("外网");
        }else{
            tvSettingIP.setText(host_ip);
        }

        dialogSetting.setContentView(viewSetting);
        dialogSetting.setCanceledOnTouchOutside(true);//点击外部，弹框消失
        dialogSetting.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {   //dialog消失时触发监听
                ofFloat(rlMain, "scaleX", 0.92f, 1f).setDuration(500).start();
                ofFloat(rlMain, "scaleY", 0.92f, 1f).setDuration(500).start();
                //ObjectAnimator.ofFloat(viewSetting,"translationY",0,400).setDuration(500).start();
                //ObjectAnimator.ofFloat(ivBlackFront, "alpha", 0.7f, 0f).setDuration(500).start();

            }
        });
        WindowManager.LayoutParams params = dialogSetting.getWindow().getAttributes();
        params.width = width;
        params.height = height;   //设置dialog的宽高
        Window mWindow = dialogSetting.getWindow();
        mWindow.setGravity(Gravity.BOTTOM);
        mWindow.setAttributes(params);
    }

    //初始化--------------门锁------------------二级界面
    private View viewLock;
    private RelativeLayout rlDialogTitle;
    private TextView tvLockTitle;
    private TextView tvLockOff;
    private TextView tvLockOn;
    private void initLockDialog() {
        dialogLock = new Dialog(this, R.style.CustomDialog);
        viewLock = LayoutInflater.from(this).inflate(R.layout.lock_dialog, null);
        tvLockTitle = (TextView) viewLock.findViewById(R.id.tv_setting_title);
        tvLockOff = (TextView) viewLock.findViewById(R.id.tv_close_door);
        tvLockOn = (TextView) viewLock.findViewById(R.id.tv_open_door);
        rlDialogTitle = (RelativeLayout) viewLock.findViewById(R.id.ll_setting_title);
        tvLockTitle.setTypeface(typeFace);
        tvLockOff.setTypeface(typeFace);
        tvLockOn.setTypeface(typeFace);

        rlDialogTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogLock.dismiss();
            }
        });
        dialogLock.setContentView(viewLock);
        dialogLock.setCanceledOnTouchOutside(true);//点击外部，弹框消失
        dialogLock.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {   //dialog消失时触发监听
                ofFloat(rlMain, "scaleX", 0.92f, 1f).setDuration(500).start();
                ofFloat(rlMain, "scaleY", 0.92f, 1f).setDuration(500).start();
                //ObjectAnimator.ofFloat(viewLock,"translationY",0,400).setDuration(500).start();
                //ObjectAnimator.ofFloat(ivBlackFront, "alpha", 0.7f, 0f).setDuration(500).start();

            }
        });
        WindowManager.LayoutParams params = dialogLock.getWindow().getAttributes();
        params.width = width;
        params.height = height;   //设置dialog的宽高
        Window mWindow = dialogLock.getWindow();
        mWindow.setGravity(Gravity.BOTTOM);
        mWindow.setAttributes(params);
    }

    //初始化---------------------安防---------------------二级界面
    private View viewSecurity;
    private TextView tvSecurityTitle;
    private TextView tvSecurityOff;
    private TextView tvSecurityOn;
    private RelativeLayout rlAnFang;
    private void initSecurityDialog() {
        dialogSecurity = new Dialog(this, R.style.CustomDialog);
        viewSecurity = LayoutInflater.from(this).inflate(R.layout.security_dialog, null);
        tvSecurityTitle = (TextView) viewSecurity.findViewById(R.id.tv_setting_title);
        tvSecurityOff = (TextView) viewSecurity.findViewById(R.id.tv_security_off);
        tvSecurityOn = (TextView) viewSecurity.findViewById(R.id.tv_security_on);
        rlAnFang = (RelativeLayout) viewSecurity.findViewById(R.id.ll_anfang_title);
        tvSecurityTitle.setTypeface(typeFace);
        tvSecurityOff.setTypeface(typeFace);
        tvSecurityOn.setTypeface(typeFace);

        rlAnFang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSecurity.dismiss();
            }
        });
        dialogSecurity.setContentView(viewSecurity);
        dialogSecurity.setCanceledOnTouchOutside(true);//点击外部，弹框消失
        dialogSecurity.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {   //dialog消失时触发监听
                ofFloat(rlMain, "scaleX", 0.92f, 1f).setDuration(500).start();
                ofFloat(rlMain, "scaleY", 0.92f, 1f).setDuration(500).start();
                //ObjectAnimator.ofFloat(viewSecurity,"translationY",0,400).setDuration(500).start();
                //ObjectAnimator.ofFloat(ivBlackFront, "alpha", 0.7f, 0f).setDuration(500).start();

            }
        });
        WindowManager.LayoutParams params = dialogSecurity.getWindow().getAttributes();
        params.width = width;
        params.height = height;   //设置dialog的宽高
        Window mWindow = dialogSecurity.getWindow();
        mWindow.setGravity(Gravity.BOTTOM);
        mWindow.setAttributes(params);
    }

    //初始化------------------------灯光--------------------------二级界面
    private int[] lightLoopIconList = {R.mipmap.light_control_3x_0,R.mipmap.light_control_3x_1,R.mipmap.light_control_3x_2,
            R.mipmap.light_control_3x_3,R.mipmap.light_control_3x_4,R.mipmap.light_control_3x_5,R.mipmap.light_control_3x_6,
            R.mipmap.light_control_3x_7,R.mipmap.light_control_3x_8,R.mipmap.light_control_3x_9,R.mipmap.light_control_3x_10,
            R.mipmap.light_control_3x_11};
    private String[] lightLoopNameList = {"测试1","测试2","测试3","测试4"};
    private View viewLight;
    private TextView tvLightTitle;
    private TextView tvLightAll;
    private ListView lvLightControl;
    private ControlLightBaseAdapter controlLightBaseAdapter;
    private RadioGroup rgLightAll;
    private RadioButton rbLightAllClose;
    private RadioButton rbLightAll1;
    private RadioButton rbLightAll2;
    private RadioButton rbLightAll3;

    private boolean isDialogLightShow = false;
    private void initLightDialog(){
        dialogLight = new Dialog(this, R.style.CustomDialog);
        viewLight = LayoutInflater.from(this).inflate(R.layout.light_dialog, null);
        tvLightTitle = (TextView) viewLight.findViewById(R.id.tv_light_title);
        tvLightAll = (TextView) viewLight.findViewById(R.id.tv_light_all);
        lvLightControl = (ListView) viewLight.findViewById(R.id.lv_light_control);
        rgLightAll = (RadioGroup) viewLight.findViewById(R.id.rg_control_light_channel_all);
        rbLightAllClose = (RadioButton) viewLight.findViewById(R.id.rb_control_light_channel_all_close);
        rbLightAll1 = (RadioButton) viewLight.findViewById(R.id.rb_control_light_channel_all_1);
        rbLightAll2 = (RadioButton) viewLight.findViewById(R.id.rb_control_light_channel_all_2);
        rbLightAll3 = (RadioButton) viewLight.findViewById(R.id.rb_control_light_channel_all_3);

        tvLightTitle.setTypeface(typeFace);
        tvLightAll.setTypeface(typeFace);
        rbLightAllClose.setTypeface(typeFace);
        rbLightAll1.setTypeface(typeFace);
        rbLightAll2.setTypeface(typeFace);
        rbLightAll3.setTypeface(typeFace);
        /*rgLightAll.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_control_light_channel_all_close:
                        controlLightBaseAdapter.changeLightLoopValue(0);
                        break;
                    case R.id.rb_control_light_channel_all_1:
                        controlLightBaseAdapter.changeLightLoopValue(1);
                        break;
                    case R.id.rb_control_light_channel_all_2:
                        controlLightBaseAdapter.changeLightLoopValue(2);
                        break;
                    case R.id.rb_control_light_channel_all_3:
                        controlLightBaseAdapter.changeLightLoopValue(3);
                        break;
                }
            }
        });*/
        controlLightBaseAdapter = new ControlLightBaseAdapter(this,lightLoopIconList,lightLoopNameList,roomDBNameList[roomIndex],token,uname,pwd,protocol);
        lvLightControl.setAdapter(controlLightBaseAdapter);

        dialogLight.setContentView(viewLight);
        dialogLight.setCanceledOnTouchOutside(true);//点击外部，弹框消失
        dialogLight.setOnDismissListener(new DialogInterface.OnDismissListener() {      //dialog消失事件监听
            @Override
            public void onDismiss(DialogInterface dialog) {   //dialog消失时触发监听
                ofFloat(rlMain, "scaleX", 0.92f, 1f).setDuration(500).start();
                ofFloat(rlMain, "scaleY", 0.92f, 1f).setDuration(500).start();
                //消失后按钮全部设为不选中
                rbLightAllClose.setChecked(false);
                rbLightAll1.setChecked(false);
                rbLightAll2.setChecked(false);
                rbLightAll3.setChecked(false);
                isDialogLightShow = false;
                lightValue = new int[]{-1,-1,-1,-1,-1,-1};
            }
        });
        WindowManager.LayoutParams params = dialogLight.getWindow().getAttributes();
        params.width = width;
        params.height = (int) (height*0.8);   //设置dialog的宽高
        Window mWindow = dialogLight.getWindow();
        mWindow.setGravity(Gravity.BOTTOM);
        mWindow.setAttributes(params);
    }

    //全部回路灯光亮度调节点击事件
    public void allLightClick(View v){
        switch (v.getId()){
            case R.id.rb_control_light_channel_all_close:
                controlLightBaseAdapter.changeLightLoopValue(0);
                controlLight(protocol,"light","0","0","all");
                break;
            case R.id.rb_control_light_channel_all_1:
                controlLightBaseAdapter.changeLightLoopValue(1);
                controlLight(protocol,"light","30","0","all");
                break;
            case R.id.rb_control_light_channel_all_2:
                controlLightBaseAdapter.changeLightLoopValue(2);
                controlLight(protocol,"light","60","0","all");
                break;
            case R.id.rb_control_light_channel_all_3:
                controlLightBaseAdapter.changeLightLoopValue(3);
                controlLight(protocol,"light","100","0","all");
                break;
        }
    }

    private int position;
    private int value;
    private int length;
    private int[] lightValue = new int[]{-1,-1,-1,-1,-1,-1};
    @Subscribe
    public void onEventMainThread(MyEventBusControlLight event){

        position = event.getPosition();
        value = event.getMsg2();
        //length = event.getLength();
        Log.i(TAG, "onEventMainThread: lightValueposition:"+position+"---value:"+value);
        lightValue[position] = value;
        Log.i(TAG, "onEventMainThread: lightValue:"+lightValue[0]+"##"+lightValue[1]+"##"+lightValue[2]+"##"
                +lightValue[3]+"##"+lightValue[4]+"##"+lightValue[5]+"##");
        if ((lightValue[0]==1||lightValue[0]==-1)&&(lightValue[1]==1||lightValue[1]==-1)&&(lightValue[2]==1||lightValue[2]==-1)&&(lightValue[3]==1||lightValue[3]==-1)&&
                (lightValue[4]==1||lightValue[4]==-1)&&(lightValue[5]==1||lightValue[5]==-1)){
            Log.i("light_hy", "onEventMainThread: 微弱");
            rbLightAll1.setChecked(true);
        }else if ((lightValue[0]==2||lightValue[0]==-1)&&(lightValue[1]==2||lightValue[1]==-1)&&(lightValue[2]==2||lightValue[2]==-1)&&(lightValue[3]==2||lightValue[3]==-1)&&
                (lightValue[4]==2||lightValue[4]==-1)&&(lightValue[5]==2||lightValue[5]==-1)){
            Log.i("light_hy", "onEventMainThread: 舒适");
            rbLightAll2.setChecked(true);
        }else if ((lightValue[0]==3||lightValue[0]==-1)&&(lightValue[1]==3||lightValue[1]==-1)&&(lightValue[2]==3||lightValue[2]==-1)&&(lightValue[3]==3||lightValue[3]==-1)&&
                (lightValue[4]==3||lightValue[4]==-1)&&(lightValue[5]==3||lightValue[5]==-1)){
            Log.i("light_hy", "onEventMainThread: 明亮");
            rbLightAll3.setChecked(true);
        }else if ((lightValue[0]==0||lightValue[0]==-1)&&(lightValue[1]==0||lightValue[1]==-1)&&(lightValue[2]==0||lightValue[2]==-1)&&(lightValue[3]==0||lightValue[3]==-1)&&
                (lightValue[4]==0||lightValue[4]==-1)&&(lightValue[5]==0||lightValue[5]==-1)){
            Log.i("light_hy", "onEventMainThread: 关闭");
            rbLightAllClose.setChecked(true);
        }else {
            rgLightAll.clearCheck();
        }

    }

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
            CommandData.put("houseDBName", roomDBNameList[roomIndex]);
            String lightJson = CommandJsonUtils.getCommandJson(0, CommandData, token, uname, pwd, String.valueOf(System.currentTimeMillis()));
            EventBus.getDefault().post(new MyEventBus(lightJson));
            Log.e("result", "onClick: ------lightjson------" + lightJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //退出程序提示框dialog
    private Dialog dialogExit;
    private View viewExit;
    private TextView tvExitTip;
    private TextView tvExitContent;
    private TextView tvExit;
    private TextView tvNotExit;
    private void initExitDialog(){
        dialogExit = new Dialog(this, R.style.CustomDialog);
        viewExit = LayoutInflater.from(this).inflate(R.layout.exit_dialog_layout, null);
        tvExitTip = (TextView) viewExit.findViewById(R.id.tv_exit_tip);
        tvExitContent = (TextView) viewExit.findViewById(R.id.tv_exit_content);
        tvExit = (TextView) viewExit.findViewById(R.id.tv_exit);
        tvNotExit = (TextView) viewExit.findViewById(R.id.tv_not_exit);
        tvExitTip.setTypeface(typeFace);
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

    //---------------------------音乐dialog-------------------------

    private View viewMusic;
    private TextView tvMusicTitle;
    private TextView tvMusicArea;
    private TextView tvMusicSearch;
    private TextView tvMusicLibrary;
    private ImageView ivMusicPlayModel;
    private ImageView ivMusicPrevious;   //上一曲
    private ImageView ivMusicPlay;
    private ImageView ivMusicNext;  //下一曲
    private ImageView ivMusicRedHeart;  //红心歌曲
    private ListView lvMusicDialog;
    private void initMusicDialog() {
        dialogMusic = new Dialog(this, R.style.CustomDialog);
        viewMusic = LayoutInflater.from(this).inflate(R.layout.music_dialog, null);

        tvMusicTitle = (TextView) viewMusic.findViewById(R.id.tv_music_title);
        tvMusicArea = (TextView) viewMusic.findViewById(R.id.tv_music_area);
        tvMusicSearch = (TextView) viewMusic.findViewById(R.id.tv_music_search);
        tvMusicLibrary = (TextView) viewMusic.findViewById(R.id.tv_music_library);
        ivMusicPlayModel = (ImageView) viewMusic.findViewById(R.id.iv_music_play_model);
        ivMusicPrevious = (ImageView) viewMusic.findViewById(R.id.iv_music_previous);
        ivMusicPlay = (ImageView) viewMusic.findViewById(R.id.iv_music_play_pause);
        ivMusicNext = (ImageView) viewMusic.findViewById(R.id.iv_music_next);
        ivMusicRedHeart = (ImageView) viewMusic.findViewById(R.id.iv_music_red_heart);
        lvMusicDialog = (ListView) viewMusic.findViewById(R.id.lv_music_dialog);
        tvMusicTitle.setTypeface(typeFace);
        tvMusicArea.setTypeface(typeFace);
        tvMusicSearch.setTypeface(typeFace);
        tvMusicLibrary.setTypeface(typeFace);

        dialogMusic.setContentView(viewMusic);
        dialogMusic.setCanceledOnTouchOutside(true);//点击外部，弹框消失
        dialogMusic.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {   //dialog消失时触发监听
                ofFloat(rlMain, "scaleX", 0.92f, 1f).setDuration(500).start();
                ofFloat(rlMain, "scaleY", 0.92f, 1f).setDuration(500).start();
                //ObjectAnimator.ofFloat(viewSecurity,"translationY",0,400).setDuration(500).start();
                //ObjectAnimator.ofFloat(ivBlackFront, "alpha", 0.7f, 0f).setDuration(500).start();

            }
        });
        WindowManager.LayoutParams params = dialogMusic.getWindow().getAttributes();
        params.width = width;
        params.height = (int) (height*0.8);   //设置dialog的宽高
        Window mWindow = dialogMusic.getWindow();
        mWindow.setGravity(Gravity.BOTTOM);
        mWindow.setAttributes(params);
    }

    //-------------------------音乐一级页面点击事件--------------------------
    private int playFlag = 0;
    private int redHeartFlag = 0;
    public void musicClick(View view){
        switch (view.getId()){
            case R.id.tv_music_area:
                break;
            case R.id.tv_music_search:
                break;
            case R.id.tv_music_library:
                Intent intent = new Intent(MyMainActivity.this,MusicActivity.class);
                startActivity(intent);
                break;
            case R.id.iv_music_play_model:

                break;
            case R.id.iv_music_previous:
                break;
            case R.id.iv_music_play_pause:
                if (playFlag==0){
                    ivMusicPlay.setImageResource(R.mipmap.theme2_yinyue_zanting_3x);
                    playFlag = 1;
                }else if (playFlag==1){
                    ivMusicPlay.setImageResource(R.mipmap.theme2_yinyue_bofang_3x);
                    playFlag = 0;
                }
                break;
            case R.id.iv_music_next:
                break;
            case R.id.iv_music_red_heart:
                if (redHeartFlag==0){
                    ivMusicRedHeart.setImageResource(R.mipmap.theme2_music_heart_full_in_detail_view_3x);
                    redHeartFlag = 1;
                }else if (redHeartFlag==1){
                    ivMusicRedHeart.setImageResource(R.mipmap.theme2_music_heart_empty_in_detail_view_3x);
                    redHeartFlag = 0;
                }
                break;
        }
    }

    //---------------------二级页面的点击事件----------------------------
    public void secondaryClick(View view){
        switch (view.getId()){
            case R.id.tv_setting_clock:
                setScaleAnimation(tvSettingClock);
                break;
            case R.id.tv_setting_help:
                setScaleAnimation(tvSettingHelp);
                break;
            case R.id.tv_setting_exit:
                dialogExit.show();
                setScaleAnimation(tvSettingExit);
                break;
            case R.id.tv_close_door:
                customCroutonUtil.makeViewGroupTopToast("关门",1000, rlDialogTitle);
                setScaleAnimation(tvLockOff);
                break;
            case R.id.tv_open_door:
                customCroutonUtil.makeViewGroupTopToast("开门",1000, rlDialogTitle);
                setScaleAnimation(tvLockOn);
                break;
            case R.id.tv_security_off:
                customCroutonUtil.makeViewGroupTopToast("撤防",1000,rlAnFang);
                setScaleAnimation(tvSecurityOff);
                controlSecurity(protocol,"security",0,"all_close");
                break;
            case R.id.tv_security_on:
                customCroutonUtil.makeViewGroupTopToast("布防",1000, rlAnFang);
                setScaleAnimation(tvSecurityOn);
                controlSecurity(protocol,"security",255,"all_normal");
                break;
            case R.id.tv_not_exit:
                //Toast.makeText(MyMainActivity.this, "不退出", Toast.LENGTH_SHORT).show();
                dialogExit.hide();
                break;
            case R.id.tv_exit:
                //Toast.makeText(MyMainActivity.this, "退出该账号", Toast.LENGTH_SHORT).show();
                dialogExit.hide();
                dialogExit.dismiss();
                dialogSetting.dismiss();
                dialogExit = null;
                dialogSetting = null;
                SharedPreferences sp = getSharedPreferences("homeJson",MODE_PRIVATE);
                sp.edit().clear().commit();    //清除sharedPreference房间json数据
                SharedPreferences spUser = getSharedPreferences("user_inform",MODE_PRIVATE);
                spUser.edit().clear().commit();    //清除sharedPreference用户数据
                SharedPreferences spToken = getSharedPreferences("user_token",MODE_PRIVATE);
                spToken.edit().clear().commit();      //清除token
                SharedPreferences spCustom = getSharedPreferences("customSceneJson",MODE_PRIVATE);
                spCustom.edit().clear().commit();      //清除自定义场景信息
                SharedPreferences spJudgeFirst = getSharedPreferences("judgeFirstSave",MODE_PRIVATE);
                spJudgeFirst.edit().clear().commit();      //清除自定义场景信息
                isConnectWAN = false;
                isConnectLAN = false;
                if (tcpLongSocketWAN!=null){
                    tcpLongSocketWAN.close();    //关闭外网的连接
                }
                if (tcplongSocket!=null){
                    tcplongSocket.close();      //关闭内网连接
                }

                isOnMainActivity = false;     //退出该activity
                appConnection.interrupt();     //中断线程
                appConnectionWAN.interrupt();
                Intent intent = new Intent(MyMainActivity.this,NewLoginActivity.class);
                startActivity(intent);
                this.finish();
                break;
        }
    }


    //------------------------------------------------------音乐的网络操作---------------------------------------------------------
    private String musicId = "186017";      //三年二班
    private String musicTopTypeNew = "new";      //排行榜类型 new 新歌
    private String musicTopTypeHot = "hot";      //排行榜类型 hot 热歌
    private String musicTopTypeOrigin = "origin";      //排行榜类型 origin 原创
    private String musicTopTypeSoar = "soar";      //排行榜类型 soar 飙升

    private String musicRecommendUrl = "http://115.29.97.189:11912/recomm/music";        //推荐歌曲url    http://115.29.97.189:11912/
    private String musicRecommendPlayListUrl = "http://115.29.97.189:11912/recomm/playlist";       //推荐歌单url
    private String musicDetailSongUrl = "http://115.29.97.189:11912/detail/song/"+musicId;        //根据歌曲id，获取歌曲信息url
    private String musicTopListUrlNew = "http://115.29.97.189:11912/toplist/"+musicTopTypeNew;         //排行榜类型, new: 新歌, hot: 热歌, origin: 原创, soar: 飙升
    private String musicTopListUrlHot = "http://115.29.97.189:11912/toplist/"+musicTopTypeHot;
    private String musicTopListUrlOrigin = "http://115.29.97.189:11912/toplist/"+musicTopTypeOrigin;
    private String musicTopListUrlSoar = "http://115.29.97.189:11912/toplist/"+musicTopTypeSoar;

    private void musicOkhttpGet(final int type, String url){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(MyMainActivity.this, "音乐网络不可用", Toast.LENGTH_SHORT).show();
                Log.i("music_hy", "onFailure: "+e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String str = response.body().string();
                LongLogCatUtil.logE("music_hy", "onResponse:--------------"+str);
                switch (type){
                    case 1:
                        parseRecommendSongs(str);
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                }
            }
        });
    }

    //解析推荐歌曲json
    private List<String> musicNameList;
    private List<String> musicArtistList;
    private List<String> musicIdList;

    private void parseRecommendSongs(String strJson){
        musicNameList = new ArrayList<>();
        musicArtistList = new ArrayList<>();
        musicIdList = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(strJson);
            JSONArray array = obj.getJSONArray("music_list");
            for (int i=0;i<array.length();i++){
                JSONObject jsonObj = array.getJSONObject(i);
                musicNameList.add(jsonObj.getString("music_name"));
                musicArtistList.add(jsonObj.getString("music_artist"));
                musicIdList.add(jsonObj.getString("music_id"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //解析推荐歌单json
    private void parseRecommendList(String strJson){
        try {
            JSONArray array = new JSONArray(strJson);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    //-----------------------------接受fragment传来的消息--------------------------------
    //拼接外网控制命令    ----->  数据长度（4个byte）+ 数据内容
    private byte[] byidsHead = new byte[]{35,98,121,105,100,115,4};          //发送外网控制命令的数据头
    private byte[] jointWANControlCommend(int byte_data_length,byte[] LANControlCommend){
        //把int类型的控制命令长度转化成byte[4]
        byte[] byte_length = changeIntToByte4(byte_data_length);      //4位byte[] 表示数据长度
        byte[] a = byteMerger(byte_length,LANControlCommend);
        return byteMerger(byidsHead,a);
    }

    //把int类型的控制命令长度转化成byte[4]
    private static byte[] changeIntToByte4(int byte_data_length){
        int a = byte_data_length/(256*256*256);
        int b = byte_data_length/(256*256);
        int c = byte_data_length/256;
        int d = byte_data_length%256;
        byte[] data_length = {(byte) a,(byte)b,(byte)c,(byte)d};
        return data_length;
    }



    //拼接两个byte【】     拼接#byids（6个byte） + 4（1个byte） + 加密数据长度（4个byte）
    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2){
        byte[] byte_3 = new byte[byte_1.length+byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }




    @Subscribe
    public void onEventMainThread(MyEventBus event) {
        String msg = event.getmMsg();
        Log.i(TAG, "onEventMainThread: 接收到的json" + msg);
        //发送tcp Socket
        if (msg.length()>4) {
            if (tcplongSocket!=null){
                tcplongSocket.writeDate(Encrypt.encrypt(msg));     //内网发送控制命令
            }
            if (tcpLongSocketWAN!=null){
                //拼接外网控制命令
                Log.i("hy_result", "onEventMainThread: -------------"+msg);
                tcpLongSocketWAN.writeDate(jointWANControlCommend(Encrypt.encrypt(msg).length,Encrypt.encrypt(msg)));  //外网发送控制命令,前面拼接#byids + 4 + 控制命令的长度 + 控制命令
            }
        }else {
            switch (msg) {
                case SWITCH_ROOM_DIALOG:     //选择房间dialog
                    dialogSwitchRoom.show();
                    break;
                case SETTING_DIALOG:     //弹出设置dialog
                    initExitDialog();    //确认是否退出的三级页面
                    //ObjectAnimator.ofFloat(ivBlackFront, "alpha", 0f, 0.7f).setDuration(500).start();
                    ofFloat(rlMain, "scaleX", 1f, 0.92f).setDuration(500).start();
                    ObjectAnimator obj = new ObjectAnimator().ofFloat(rlMain, "scaleY", 1f, 0.92f).setDuration(500);
                    obj.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            ofFloat(viewSetting,"translationY",height,0).setDuration(500).start();
                            dialogSetting.show();
                        }
                    });
                    obj.start();
                    break;
                case DOOR_LOCK_DIALOG:       //弹出门锁dialog
                    //ObjectAnimator.ofFloat(ivBlackFront, "alpha", 0f, 0.7f).setDuration(500).start();
                    ofFloat(rlMain, "scaleX", 1f, 0.92f).setDuration(500).start();
                    ObjectAnimator obj1 = new ObjectAnimator().ofFloat(rlMain, "scaleY", 1f, 0.92f).setDuration(500);
                    obj1.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            ofFloat(viewLock,"translationY",height,0).setDuration(500).start();
                            dialogLock.show();
                        }
                    });
                    obj1.start();
                    break;
                case SECURITY_DIALOG:      //弹出安防dialog
                    //ObjectAnimator.ofFloat(ivBlackFront, "alpha", 0f, 0.7f).setDuration(500).start();
                    ofFloat(rlMain, "scaleX", 1f, 0.92f).setDuration(500).start();
                    ObjectAnimator obj2 = new ObjectAnimator().ofFloat(rlMain, "scaleY", 1f, 0.92f).setDuration(500);
                    obj2.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            ofFloat(viewSecurity,"translationY",height,0).setDuration(500).start();
                            dialogSecurity.show();
                        }
                    });
                    obj2.start();
                    break;

            }
            if (msg.substring(0,1).equals("7")){         //灯光控制不同回路dialog
                final String index = msg.substring(1,2);
                ObjectAnimator.ofFloat(rlMain, "scaleX", 1f, 0.92f).setDuration(500).start();
                ObjectAnimator obj3 = new ObjectAnimator().ofFloat(rlMain, "scaleY", 1f, 0.92f).setDuration(500);
                obj3.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        dialogLight.show();
                        isDialogLightShow = true;
                        controlLightBaseAdapter.changeLightLoopValue(10);     //设置按钮的选中
                        setDiffRoomsLight(index);       //设置不同房间灯光回路
                        ObjectAnimator obj = new ObjectAnimator().ofFloat(viewLight,"translationY",(float) (height*0.8),0).setDuration(500);
                        obj.start();
                    }
                });
                obj3.start();
            }
        }

    }

    private String[] lightLoopNameLists;
    private String[] lightDisplayType;
    private int[] lightLoopIconLists;
    private int roomIndex = 0; //房间下标
    private void setDiffRoomsLight(String index){
        roomIndex = parseInt(index);      //房间下标
        int lightLoop = commandData.getProfile().getRooms().getArray().get(roomIndex).getRoom_dev_mesg().getLight().getArray().size();
        lightLoopNameLists = new String[lightLoop];
        lightDisplayType = new String[lightLoop];
        lightLoopIconLists = new int[lightLoop];
        for (int i=0;i<lightLoop;i++){
            lightLoopNameLists[i] = commandData.getProfile().getRooms().getArray().get(roomIndex).getRoom_dev_mesg().getLight().getArray().get(i).getSoftware_mesg().getDisplay_name();
            lightDisplayType[i] = commandData.getProfile().getRooms().getArray().get(roomIndex).getRoom_dev_mesg().getLight().getArray().get(i).getSoftware_mesg().getDisplay_type();
            Log.e(TAG, "setDiffRoomsLight: 回路：" +lightLoopNameLists[i]+"--------图片编号："+lightDisplayType[i]);
            lightLoopIconLists[i] = lightLoopIconList[Integer.parseInt(lightDisplayType[i])-1];
        }

        controlLightBaseAdapter.setDiffRoomLight(lightLoopIconLists,lightLoopNameLists,roomDBNameList[roomIndex]);
    }


    private int sleepMode = 0;    //睡眠模式状态
    View.OnClickListener mediaListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.rl_music:
                    musicOkhttpGet(1,musicRecommendUrl);      //获取推荐歌曲列表
                    //Toast.makeText(MyMainActivity.this, "音乐", Toast.LENGTH_SHORT).show();
                    ofFloat(rlMain, "scaleX", 1f, 0.92f).setDuration(500).start();
                    ObjectAnimator obj2 = new ObjectAnimator().ofFloat(rlMain, "scaleY", 1f, 0.92f).setDuration(500);
                    obj2.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            ofFloat(viewMusic,"translationY",(float) (height*0.8),0).setDuration(500).start();
                            dialogMusic.show();
                        }
                    });
                    obj2.start();
                    break;
                case R.id.rl_media:
                    if (sleepMode==0){
                        customCroutonUtil.makeTopToast("开启睡眠模式",1000);
                        ivSleepStars.setVisibility(View.VISIBLE);
                        ObjectAnimator.ofFloat(ivSleepMoon,"translationX",0,14).setDuration(600).start();
                        ObjectAnimator.ofFloat(ivSleepMoon,"translationY",0,14).setDuration(600).start();
                        ObjectAnimator.ofFloat(ivSleepStars,"alpha",0,1).setDuration(600).start();
                        sleepMode = 1;
                    }else if (sleepMode==1){
                        customCroutonUtil.makeTopToast("关闭睡眠模式",1000);
                        ObjectAnimator.ofFloat(ivSleepMoon,"translationX",14,0).setDuration(600).start();
                        ObjectAnimator.ofFloat(ivSleepMoon,"translationY",14,0).setDuration(600).start();
                        ObjectAnimator obj = new ObjectAnimator().ofFloat(ivSleepStars,"alpha",1,0).setDuration(600);
                        obj.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                ivSleepStars.setVisibility(View.GONE);
                            }
                        });
                        obj.start();
                        sleepMode = 0;
                    }
                    break;
                case R.id.tv_button_come_home:
                    break;
                case R.id.tv_button_go_out:
                    break;
            }
        }
    };


    private int pullFalg = 1;;
    private void pullMenu(MyFragment myF) {
        //自定义的下拉监听
        myF.setPullDownMenuListener(new PullDownMenuListener() {
            @Override
            public void pullDown(boolean b, boolean isIconShow) {
                b1 = isIconShow;
                if (b == false && isIconShow == false) {      //下拉菜单出现时
                    //隐藏桌面小控件
                    rlMusic.setVisibility(View.GONE);
                    rlMedia.setVisibility(View.GONE);
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            Message message = new Message();
                            message.what = 2;
                            handlerUI.sendMessage(message);
                        }
                    }.start();
                    //设置viewpager不能滑动
                    viewPager.setCanScroll(false);
                    Log.i("hehehe", "pullDown: ------------顶部-----------");
                } else if (b == true && isIconShow == true) {     //下拉菜单隐藏时
                    rlMusic.setVisibility(View.VISIBLE);
                    rlMedia.setVisibility(View.VISIBLE);
                    setButtonsAnimationDisappear();       //设置主界面信息控件的消失动画
                    viewPager.setCanScroll(true);
                    Log.i("hehehe", "pullDown: ------------中部-----------");
                } else if (b == false && isIconShow == true) {             //回到初始位置
                    if (pullFalg==2){
                        /*Message msg = new Message();
                        msg.what = 1;
                        handlerUI.sendMessageDelayed(msg,200);*/
                        Log.i("hehehe", "pullDown: ------------回到中部-----------");
                        pullFalg = 1;
                    }
                } else if (b == true && isIconShow == false) {        //拉到顶
                    pullFalg = 2;
                    /*ObjectAnimator.ofFloat(rlMusic,"alpha",1f,0f).setDuration(200).start();
                    ObjectAnimator obj = new ObjectAnimator().ofFloat(rlMedia,"alpha",1f,0f).setDuration(200);
                    obj.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);*/
                            rlMusic.setVisibility(View.GONE);
                            rlMedia.setVisibility(View.GONE);
                     /*   }
                    });
                    obj.start();
*/
                    Log.i("hehehe", "pullDown: -------------拉到顶----------");
                }
            }

            @Override
            public void scrollPager() {

            }
        });
    }

    /*
    *  ==========================================================
    * */
    private boolean isScrolling = false;
    private int scrollLocalValue = 1;    //-1代表不确定
    @Subscribe
    public void onEventMainThread(MyEventBusPullMenu event){
        //Log.i("hehehehy", "onEventMainThread:是否正在滑动: "+event.isScrolling()+"!!!!!!!!!!"+event.getScrollLocal());
        isScrolling = event.isScrolling();
        if (event.getScrollLocal()!=-1){            //等于-1时不赋值
            scrollLocalValue = event.getScrollLocal();
        }
        //Log.i("hehehehy", "onEventMainThread:scrollLocalValue: "+scrollLocalValue);
        if (isScrolling){
            setButtonsAnimationDisappear();
        }
        if (scrollLocalValue==0){           //滑动到顶部并且滑动静止
            if (!isScrolling){
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        Message message = new Message();
                        message.what = 2;
                        handlerUI.sendMessage(message);
                    }
                }.start();
                Log.i("hehehehy", "onEventMainThread: ------------------------------滑动到顶部并且滑动静止"+isMainButtonsShow);
            }
        }else if (scrollLocalValue==1){     //滑动到初始位置并滑动静止
            if (!isScrolling){
                Log.i("hehehehy", "onEventMainThread: ------------------------------滑动到初始位置并滑动静止");
            }
        }
    }

    //设置主界面信息控件的出现动画
    private boolean isMainButtonsShow = false;
    private void setButtonsAnimationAppear(){
        if (!isMainButtonsShow){              //桌面小控件消失的时候才会出现显示动画
            ObjectAnimator.ofFloat(tvButtonComeHome,"alpha",0f,1f).setDuration(200).start();
            ObjectAnimator.ofFloat(tvButtonGoOut,"alpha",0f,1f).setDuration(200).start();
            ObjectAnimator.ofFloat(tvHouseTimeRight,"alpha",0f,1f).setDuration(200).start();
            ObjectAnimator.ofFloat(tvHouseOwnerName,"alpha",0f,1f).setDuration(200).start();
            ObjectAnimator.ofFloat(tvHouseTimeLeft,"alpha",0f,1f).setDuration(200).start();
            ObjectAnimator.ofFloat(tvHouseTemp,"alpha",0f,1f).setDuration(200).start();
            tvButtonComeHome.setVisibility(View.VISIBLE);
            tvButtonGoOut.setVisibility(View.VISIBLE);
            tvHouseTimeRight.setVisibility(View.VISIBLE);
            tvHouseOwnerName.setVisibility(View.VISIBLE);
            tvHouseTimeLeft.setVisibility(View.VISIBLE);
            tvHouseTemp.setVisibility(View.VISIBLE);
            isMainButtonsShow = true;
        }

    }

    //设置主界面信息控件的消失动画
    private void setButtonsAnimationDisappear(){
        tvButtonComeHome.setVisibility(View.GONE);
        tvButtonGoOut.setVisibility(View.GONE);
        tvHouseTimeRight.setVisibility(View.GONE);
        tvHouseOwnerName.setVisibility(View.GONE);
        tvHouseTimeLeft.setVisibility(View.GONE);
        tvHouseTemp.setVisibility(View.GONE);
        isMainButtonsShow = false;
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
            Log.i(TAG, "onPageScrollStateChanged: =-=-=-=-=-=-=-=" + state);
            pagerState = state;
            boolean isChange = false;
            if (state == 2) {
                isChange = true;
            }

            if (state == 1) {
                EventBus.getDefault().post(new MyEventBus2(SCROLL_FRAGMENT_START));     //开始滑动scrollView
                scrollViewPager();
            }
            if (state == 0) {
                if (obj.isRunning()){
                    obj.cancel();
                }
                EventBus.getDefault().post(new MyEventBus2(SCROLL_FRAGMENT_END));       //结束滑动scrollView
                downScrollViewPager();
            }
            //onMainListener.onMainAction(pagerState);   //Activity向Fragment通信
        }
    };

    //滑动viewpager时，控件消失
    private ObjectAnimator obj;
    private void scrollViewPager() {
        EventBus.getDefault().post(new MyEventBus2(SCROLL_FRAGMENT_START));
        ObjectAnimator.ofFloat(rlMusic,"alpha",1f,0f).setDuration(200).start();
        obj = new ObjectAnimator().ofFloat(rlMedia,"alpha",1f,0f).setDuration(200);
        obj.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                rlMusic.setVisibility(View.GONE);
                                rlMedia.setVisibility(View.GONE);
                            }
                        });
        obj.start();
    }

    //滑动结束后，控件显现
    private void downScrollViewPager() {
        if (b1 == false) {
            rlMusic.setVisibility(View.GONE);
            rlMedia.setVisibility(View.GONE);
        } else if (b1 == true) {
            showAnimation();
            rlMusic.setVisibility(View.VISIBLE);
            rlMedia.setVisibility(View.VISIBLE);
        }

    }

    //控件滑出显示的动画
    private void showAnimation() {
        ofFloat(rlMusic, "translationX", 200, -10, 0).setDuration(600).start();
        ObjectAnimator oa1 = ofFloat(rlMedia, "translationX", 250, -10, 0);
        oa1.setDuration(800);
        //oa1.setStartDelay(100);
        oa1.start();
        ObjectAnimator.ofFloat(rlMedia,"alpha",0f,1f).setDuration(600).start();
        ObjectAnimator.ofFloat(rlMusic,"alpha",0f,1f).setDuration(600).start();
    }


    //点击按钮缩放动画
    private void setScaleAnimation(View view){
        ofFloat(view,"scaleX",1f,0.8f,1f).setDuration(600).start();
        ofFloat(view,"scaleY",1f,0.8f,1f).setDuration(600).start();
    }

    private void controlSecurity(String controlProtocol, String machineName, int value, String controlSence) {
        JSONObject CommandData = new JSONObject();
        JSONObject controlData = new JSONObject();
        try {
            CommandData.put("controlProtocol", controlProtocol);
            CommandData.put("machineName", machineName);
            CommandData.put("controlData", controlData);
            controlData.put("status", value);
            CommandData.put("controlSence", controlSence);
            CommandData.put("houseDBName", "house");
            String securityJson = CommandJsonUtils.getCommandJson(0, CommandData, token, uname, pwd, String.valueOf(System.currentTimeMillis()));
            //EventBus.getDefault().post(new MyEventBus(securityJson));
            if (tcplongSocket!=null){
                tcplongSocket.writeDate(Encrypt.encrypt(securityJson));     //内网发送控制命令
            }
            if (tcpLongSocketWAN!=null){
                //拼接外网控制命令
                tcpLongSocketWAN.writeDate(jointWANControlCommend(Encrypt.encrypt(securityJson).length,Encrypt.encrypt(securityJson)));  //外网发送控制命令,前面拼接#byids + 4 + 控制命令的长度 + 控制命令
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    //音量控制命令
    private void controlVolume(double MusicVolume) {         //MusicVolume:0.000 ~ 1.000
        JSONObject CommandData = new JSONObject();
        try {
            CommandData.put("MusicVolume", MusicVolume);
            String volumeJson = CommandJsonUtils.getCommandJson(9, CommandData, token, uname, pwd, String.valueOf(System.currentTimeMillis()));
            EventBus.getDefault().post(new MyEventBus(volumeJson));
            Log.i(TAG, "onClick: ------volumeJson------" + volumeJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        double maxVolume = am.getStreamMaxVolume( AudioManager.STREAM_MUSIC );     //获取媒体音量最大值
        double currentVolume = am.getStreamVolume( AudioManager.STREAM_MUSIC );    //获取媒体当前音量
        DecimalFormat df = new DecimalFormat("######0.000");       //double值取小数点后3位

        if (keyCode==event.KEYCODE_BACK){
            exit();
            return false;
        }if (keyCode==KeyEvent.KEYCODE_VOLUME_UP){    //音量加
            Log.e(TAG, "onKeyDown:当前音量："+currentVolume+"------最大音量"+maxVolume +"调节为"+df.format(currentVolume/maxVolume));
            controlVolume(Double.parseDouble(df.format(currentVolume/maxVolume)));        //发送音量控制命令
        }if (keyCode==KeyEvent.KEYCODE_VOLUME_DOWN){   //音量减
            Log.e(TAG, "onKeyDown:当前音量："+currentVolume+"------最大音量"+maxVolume +"调节为"+df.format(currentVolume/maxVolume));
            controlVolume(Double.parseDouble(df.format(currentVolume/maxVolume)));
        }
        return super.onKeyDown(keyCode, event);
    }

    private boolean isExit = false;
    private Handler mhandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    private void exit(){
        if (isExit==false){
            isExit = true;
            customCroutonUtil.makeTopToast("再按一下退出程序",1000);
            mhandler.sendMessageDelayed(new Message(),2000);
        }else if (isExit==true){
            Intent stopIntent = new Intent(this, UDPBroadcastService.class);
            stopService(stopIntent); // 停止服务
            Intent intent = new Intent("com.byids.hy.testpro.activity.BaseActivity");
            intent.putExtra("closeAll", 1);
            sendBroadcast(intent);//发送广播
            finish();
            System.exit(0);
            //广播，关闭所有activity资源，退出程序
        }
    }
}
