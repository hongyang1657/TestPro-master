package com.byids.hy.testpro.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Typeface;
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
import com.byids.hy.testpro.PullDownMenuListener;
import com.byids.hy.testpro.R;
import com.byids.hy.testpro.TCPLongSocketCallback;
import com.byids.hy.testpro.TcpLongSocket;
import com.byids.hy.testpro.View.MyCustomViewPager;
import com.byids.hy.testpro.adapter.ControlLightBaseAdapter;
import com.byids.hy.testpro.adapter.MyFragmentAdapter;
import com.byids.hy.testpro.adapter.RoomNameBaseAdapter;
import com.byids.hy.testpro.fragment.MyFragment;
import com.byids.hy.testpro.newBean.AllJsonData;
import com.byids.hy.testpro.service.TcpConnectService;
import com.byids.hy.testpro.service.UDPBroadcastService;
import com.byids.hy.testpro.utils.AES;
import com.byids.hy.testpro.utils.CommandJsonUtils;
import com.byids.hy.testpro.utils.Encrypt;
import com.byids.hy.testpro.utils.LongLogCatUtil;
import com.byids.hy.testpro.utils.NetworkStateUtil;
import com.byids.hy.testpro.utils.VibratorUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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
    private int[] ivBackList = { R.mipmap.back_13,R.mipmap.back_10, R.mipmap.back_5,R.mipmap.back_6,R.mipmap.back_2, R.mipmap.back_14, R.mipmap.back_8, R.mipmap.back_9,R.mipmap.back_12,  R.mipmap.back_1, R.mipmap.back_3, R.mipmap.back_4};


    //房间名数组kk
    private String[] roomNameList = new String[]{"客厅","主卧","次卧","厨房","餐厅","卫浴"};
    private String[] roomDBNameList = new String[]{"keting","woshi","ciwo","chufang","canting","weiyu"};
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
    private AllJsonData allJsonData = null;        //房间json数据总类

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


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:         //外网
                    Log.i("hongyang", "run:外网连接状态 "+tcpLongSocketWAN.getConnectStatus());
                    if (tcpLongSocketWAN.getConnectStatus()){
                        EventBus.getDefault().post(new MyEventBus2("12"));     //外网连接
                        Toast.makeText(MyMainActivity.this, "连接上外网...", Toast.LENGTH_SHORT).show();
                    }else {
                        EventBus.getDefault().post(new MyEventBus2("22"));
                        Toast.makeText(MyMainActivity.this, "连接断开...", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 2:         //内网
                    Log.i("hongyang", "run:内网连接状态 "+tcplongSocket.getConnectStatus());
                    if (tcplongSocket.getConnectStatus()){
                        Toast.makeText(MyMainActivity.this, "连接上内网...", Toast.LENGTH_SHORT).show();
                        EventBus.getDefault().post(new MyEventBus2("11"));    //内网连接
                    }else {
                        Toast.makeText(MyMainActivity.this, "连接断开...", Toast.LENGTH_SHORT).show();
                        EventBus.getDefault().post(new MyEventBus2("22"));
                    }
                    //appConnection.start();       //开启后台断线重连线程
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
                    handler.sendMessageDelayed(message,3000);
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
                        handler.sendMessageDelayed(message1,3000);
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
        setContentView(R.layout.my_main_layout);
        //注册EventBus
        EventBus.getDefault().register(this);
        reciveIntent();
        initView();    //初始化界面，布局
        //startTcpService();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);  //反注册EventBus
        //connectTcp = false;    //关闭线程
        Log.i(TAG, "onDestroy: ----------------主界面退出--------------onDestroy---------------------------");
        if (dialogExit!=null){
            //dialogExit.dismiss();
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

    //接收登录页面传递过来的数据
    /*private void reciveIntent() {
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

            *//*new Thread() {
                @Override
                public void run() {
                    super.run();
                    while (connectTcp) {
                        try {
                            sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        tcplongSocket.writeDate(Encrypt.encrypt("0"));
                    }
                }
            }.start();*//*
        }
    }*/

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
        allJsonData = (AllJsonData) getIntent().getSerializableExtra("allJsonData");
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
                if (host_ip==null||host_ip.equals("")||needWANConnect){
                    Log.i("hongyang", "reciveIntent: 内网不通，尝试连接外网");
                    Log.i("hy_result", "run: 外网连接用的token:"+token);
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
                    handler.sendMessageDelayed(message,3000);
                }
            }
        }.start();

    }

    //初始化各个房间fragment的数据
    private void initFragment(){
        /*if (null!=allJsonData){
            Log.i(TAG, "reciveIntent: alljson:"+allJsonData.getCommandUser().getHid());
            allJsonData.getCommandData().getProfile().getRooms().getArray().size();
            int roomsIndex = allJsonData.getCommandData().getProfile().getRooms().getArray().size();
            Log.i(TAG, "initFragment: roomsIndex房间个数:"+roomsIndex);
            for (int i=0;i<roomsIndex;i++){
                roomNameList[i] =
            }*/

            //activity给fragment传递数据
            for (int i = 0; i < roomNameList.length; i++) {
                myFragment1 = new MyFragment(i, roomNameList[i], roomDBNameList[i], ivBackList, token, uname, pwd);         //房间id,房间名数组，房间拼音名数组，背景图片数组，活跃的控件数组,hid,uname,pwd
                pullMenu(myFragment1);
                viewList.add(myFragment1);
            }
        //}
    }

    //SharedPreferences本地储存用户信息
    private void saveUserInform(String userName,String password){
        SharedPreferences sp = getSharedPreferences("user_inform",MODE_PRIVATE);        //文件名，文件类型
        sp.edit().putString("userName",userName).putString("password",password).commit();
        Log.i("hongyang", "reciveIntent: -----------存储uname，pwd-------------"+uname+"-----"+pwd);
    }

    //----------------------内网通信的Tcp Socket连接--------------------------
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
        }

        @Override
        public void receive(byte[] buffer) {             //接收主机信息

            if (buffer.length>32 && buffer.length<200){
                //LongLogCatUtil.logE("result",testDecryptByte(buffer));
                judgeVerify(testDecryptByte(buffer));
            }else {
                Log.e(TAG, "receive: 收到的数据不是加密数据或数据损坏");
            }

        }

        @Override
        public void disconnect() {
            tcplongSocket.close();
            EventBus.getDefault().post(new MyEventBus2("22"));
        }

        private void judgeVerify(String strRec){
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

            Log.i(TAG, "connected: -----token-------"+token);
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
            byte[] tokenBytes = token.getBytes();
            //Log.i(TAG, "onClick: --------"+byteStringLog(tokenBytes));

            byte[] toConnectTcpLong = new byte[data.length+tokenBytes.length];
            System.arraycopy(data,0,toConnectTcpLong,0,data.length);
            System.arraycopy(tokenBytes,0,toConnectTcpLong,data.length,tokenBytes.length);


            Log.i("hy_result", "onClick: ----------------------------"+byteStringLog(toConnectTcpLong));

            tcpLongSocketWAN.writeDate(toConnectTcpLong);
        }

        @Override
        public void receive(byte[] buffer) {

            Log.i(TAG, "receive: buffer.length:"+buffer.length);
            Log.i(TAG, "receive: 收到的buffer："+byteStringLog(buffer));
            //取最后一位，0：验证token成功；1：验证失败
            if (buffer.length>0){
                Log.i(TAG, "receive: **********收到的buffer**********："+new String(buffer));
                switch (buffer[buffer.length-1]){
                    case -56:
                        Log.i(TAG, "receive: ---验证token成功---");
                        Message message = new Message();
                        message.what = 1;
                        handler.sendMessageDelayed(message,3000);       //连接内网成功
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
                    case 1:
                        Log.i(TAG, "receive: ---验证token失败---");
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
        appConnection = new AppConnection();
        appConnection.start();

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
    private void initSettingDialog() {
        dialogSetting = new Dialog(this, R.style.CustomDialog);
        viewSetting = LayoutInflater.from(this).inflate(R.layout.secondary_dialog, null);
        tvSettingTitle = (TextView) viewSetting.findViewById(R.id.tv_setting_title);
        tvSettingIP = (TextView) viewSetting.findViewById(R.id.tv_setting_ip);
        tvSettingExit = (TextView) viewSetting.findViewById(R.id.tv_setting_exit);
        tvSettingTitle.setTypeface(typeFace);
        tvSettingIP.setTypeface(typeFace);
        tvSettingExit.setTypeface(typeFace);
        //获取ip地址
        NetworkStateUtil networkStateUtil = new NetworkStateUtil();
        String phoneIP = networkStateUtil.getPhoneIp();
        tvSettingIP.setText(phoneIP);

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

    //初始化------------------------灯光--------------------------二级界面
    private int[] lightLoopIconList = {R.mipmap.light_control_3x_0,R.mipmap.light_control_3x_1,R.mipmap.light_control_3x_2,
            R.mipmap.light_control_3x_3,R.mipmap.light_control_3x_4,R.mipmap.light_control_3x_5,R.mipmap.light_control_3x_6,
            R.mipmap.light_control_3x_7,R.mipmap.light_control_3x_8,R.mipmap.light_control_3x_9,R.mipmap.light_control_3x_10,
            R.mipmap.light_control_3x_11};
    private String[] lightLoopNameList = {"中心主灯","沙发顶灯","辅灯","测试灯"};
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

    private void initLightDialog(){
        dialogLight = new Dialog(this, R.style.CustomDialog);
        viewLight = LayoutInflater.from(this).inflate(R.layout.light_dialog, null);
        tvLightTitle = (TextView) viewLight.findViewById(R.id.tv_light_title);
        tvLightAll = (TextView) viewLight.findViewById(R.id.tv_light_all);
        lvLightControl = (ListView) viewLight.findViewById(R.id.lv_light_control);
        rgLightAll = (RadioGroup) viewLight.findViewById(R.id.rg_control_light_channel_all);

        tvLightTitle.setTypeface(typeFace);
        tvLightAll.setTypeface(typeFace);
        controlLightBaseAdapter = new ControlLightBaseAdapter(this,lightLoopIconList,lightLoopNameList);
        lvLightControl.setAdapter(controlLightBaseAdapter);

        dialogLight.setContentView(viewLight);
        dialogLight.setCanceledOnTouchOutside(true);//点击外部，弹框消失
        dialogLight.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {   //dialog消失时触发监听
                ObjectAnimator.ofFloat(rlMain, "scaleX", 0.92f, 1f).setDuration(500).start();
                ObjectAnimator.ofFloat(rlMain, "scaleY", 0.92f, 1f).setDuration(500).start();
                //ObjectAnimator.ofFloat(viewSecurity,"translationY",0,400).setDuration(500).start();
                //ObjectAnimator.ofFloat(ivBlackFront, "alpha", 0.7f, 0f).setDuration(500).start();

            }
        });
        WindowManager.LayoutParams params = dialogLight.getWindow().getAttributes();
        params.width = width;
        params.height = (int) (height*0.8);   //设置dialog的宽高
        Window mWindow = dialogLight.getWindow();
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
                ObjectAnimator.ofFloat(rlMain, "scaleX", 0.92f, 1f).setDuration(500).start();
                ObjectAnimator.ofFloat(rlMain, "scaleY", 0.92f, 1f).setDuration(500).start();
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
                isConnectWAN = false;
                if (tcpLongSocketWAN!=null){
                    tcpLongSocketWAN.close();    //关闭外网的连接
                }
                if (tcplongSocket!=null){
                    tcplongSocket.close();      //关闭内网连接
                }

                isOnMainActivity = false;     //退出该activity
                appConnection.interrupt();
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
        //Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        Log.i(TAG, "onEventMainThread: 接收到的json" + msg);
        //发送tcp Socket
        if (msg!=SWITCH_ROOM_DIALOG && msg!=SETTING_DIALOG && msg!=DOOR_LOCK_DIALOG && msg!=SECURITY_DIALOG && msg!=LIGHT_DIALOG ) {
            if (tcplongSocket!=null){
                tcplongSocket.writeDate(Encrypt.encrypt(msg));     //内网发送控制命令
            }
            if (tcpLongSocketWAN!=null){
                //拼接外网控制命令
                Log.i("hy_result", "onEventMainThread: -------------"+msg);
                tcpLongSocketWAN.writeDate(jointWANControlCommend(Encrypt.encrypt(msg).length,Encrypt.encrypt(msg)));  //外网发送控制命令,前面拼接#byids + 4 + 控制命令的长度 + 控制命令
            }
        }

        switch (event.getmMsg()) {
            case SWITCH_ROOM_DIALOG:     //选择房间dialog
                dialogSwitchRoom.show();
                break;
            case SETTING_DIALOG:     //弹出设置dialog
                initExitDialog();    //确认是否退出的三级页面
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
            case LIGHT_DIALOG:
                ObjectAnimator.ofFloat(rlMain, "scaleX", 1f, 0.92f).setDuration(500).start();
                ObjectAnimator obj3 = new ObjectAnimator().ofFloat(rlMain, "scaleY", 1f, 0.92f).setDuration(500);
                obj3.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        dialogLight.show();
                        ObjectAnimator.ofFloat(viewLight,"translationY",(float) (height*0.8),0).setDuration(500).start();
                    }
                });
                obj3.start();
                break;
        }
    }


    View.OnClickListener mediaListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.rl_music:
                    musicOkhttpGet(1,musicRecommendUrl);      //获取推荐歌曲列表

                    Toast.makeText(MyMainActivity.this, "音乐", Toast.LENGTH_SHORT).show();
                    ObjectAnimator.ofFloat(rlMain, "scaleX", 1f, 0.92f).setDuration(500).start();
                    ObjectAnimator obj2 = new ObjectAnimator().ofFloat(rlMain, "scaleY", 1f, 0.92f).setDuration(500);
                    obj2.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            ObjectAnimator.ofFloat(viewMusic,"translationY",(float) (height*0.8),0).setDuration(500).start();
                            dialogMusic.show();
                        }
                    });
                    obj2.start();
                    break;
                case R.id.rl_media:
                    Toast.makeText(MyMainActivity.this, "媒体", Toast.LENGTH_SHORT).show();
                    Log.i("getConnect_hy", "onClick: "+tcplongSocket.getConnectStatus());
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
                    rlMusic.setVisibility(View.GONE);
                    rlMedia.setVisibility(View.GONE);
                    //设置viewpager不能滑动
                    viewPager.setCanScroll(false);
                } else if (b == true && isIconShow == true) {     //下拉菜单隐藏时
                    rlMusic.setVisibility(View.VISIBLE);
                    rlMedia.setVisibility(View.VISIBLE);
                    viewPager.setCanScroll(true);
                } else if (b == false && isIconShow == true) {
                    rlMusic.setVisibility(View.VISIBLE);
                    rlMedia.setVisibility(View.VISIBLE);
                } else if (b == true && isIconShow == false) {
                    rlMusic.setVisibility(View.GONE);
                    rlMedia.setVisibility(View.GONE);
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
        EventBus.getDefault().post(new MyEventBus2(SCROLL_FRAGMENT_START));
        rlMusic.setVisibility(View.GONE);
        rlMedia.setVisibility(View.GONE);
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
        ObjectAnimator.ofFloat(rlMusic, "translationX", 200, -10, 0).setDuration(800).start();
        ObjectAnimator oa1 = ObjectAnimator.ofFloat(rlMedia, "translationX", 250, -10, 0);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==event.KEYCODE_BACK){
            exit();
            return false;
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
            Toast.makeText(MyMainActivity.this, "再按一下退出程序", Toast.LENGTH_SHORT).show();
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
