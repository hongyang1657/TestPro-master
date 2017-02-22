package com.byids.hy.testpro.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.byids.hy.testpro.R;
import com.byids.hy.testpro.TCPLongSocketCallback;
import com.byids.hy.testpro.TcpLongSocket;
import com.byids.hy.testpro.newBean.AllJsonData;
import com.byids.hy.testpro.newBean.CommandData;
import com.byids.hy.testpro.service.UDPBroadcastService;
import com.byids.hy.testpro.utils.AES;
import com.byids.hy.testpro.utils.CommandJsonUtils;
import com.byids.hy.testpro.utils.Encrypt;
import com.byids.hy.testpro.utils.LongLogCatUtil;
import com.byids.hy.testpro.utils.NetworkStateUtil;
import com.byids.hy.testpro.utils.NewJsonParseUtils;
import com.byids.hy.testpro.utils.UDPSocket;
import com.byids.hy.testpro.utils.VibratorUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

/**
 * 启动页
 * Created by gqgz2 on 2016/10/21.
 */
public class LaunchActivity extends BaseActivity{

    private static final String TAG = "hy_launch_activity";
    private static final int LAUNCH_DELAY = 2000;
    private ImageView ivLaunch;

    public static final int DEFAULT_PORT = 57816;//端口号
    public static final String LOG_TAG = "WifiBroadcastActivity";
    private byte[] buffer = new byte[MAX_DATA_PACKET_LENGTH];
    private static final int MAX_DATA_PACKET_LENGTH = 100;
    private String udpCheck = "";
    private String ip;    //后台udp广播获取主机ip，如果能获取，表示可以本地内网连接，如果没能获取，走外网连接
    private UDPSocket udpSocket;
    private SharedPreferences sp;
    private SharedPreferences sp1;

    //房间信息
    private String userName;
    private String password;

    private String hid = "56e276f3736fb0872c69d876";
    private String allJson;
    private String tokenJson;
    private String token;
    private String saveToken;     //本地保存的token,用来非第一次登陆的验证

    //长连接
    private TcpLongSocket tcplongSocket = null;

    private boolean isConnect;
    private boolean isMobileState;
    private String phoneIP;
    private AllJsonData allJsonData;

    private boolean stopRec = false;
    private boolean isStacked = true;

    //外网登陆
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:            //内网登录获取房间信息后跳转
                    String strRoomInf = (String) msg.obj;
                    LongLogCatUtil.logE("result","launchActivity解密后的房间信息："+strRoomInf);     //多行打印logcat
                    //解析数据后跳转（测试，直接加载假数据跳转，以后删除）
                    Log.e(TAG, "handleMessage: 11111111111111111111111111111111111111111111111111111" );

                    if (strRoomInf!=null && "Verify error".equals(judgeVerify(strRoomInf))){
                        Toast.makeText(LaunchActivity.this, "内网验证没通过,尝试通过外网登录", Toast.LENGTH_SHORT).show();
                        Log.i("result", "handleMessage: 内网验证没通过,尝试通过外网登录");
                        Log.e(TAG, "handleMessage: 2222222222222222222222222222222222222222222222222" );
                        tcplongSocket.close();
                        stopRec = true;
                        secondLoginWAN();      //内网找不到主机，外网登陆
                    }else if (strRoomInf!=null && judgeRoomJson(strRoomInf)!=null){
                        //解析房间信息
                        String roomJson = strRoomInf.substring(0,strRoomInf.length()-16);
                        LongLogCatUtil.logE("result","房间json："+roomJson);
                        NewJsonParseUtils newJsonParseUtils = new NewJsonParseUtils(roomJson);
                        try {
                            allJsonData = newJsonParseUtils.newJsonParseLAN();
                        }catch (Exception e){
                            Log.e(TAG, "handleMessage: Gson出错："+e.toString());
                            tcplongSocket.close();
                            stopRec = true;
                            secondLoginWAN();      //内网找不到主机，外网登陆
                        }
                        Log.e(TAG, "handleMessage: allJsonData对象等于："+allJsonData);
                        Log.e(TAG, "handleMessage: 4444444444444444444444444444444444444444444444444" );
                        if (strRoomInf.length()>200){           //获取房间信息
                            stopRec = true;
                            Log.e(TAG, "handleMessage: 555555555555555555555555555555555555555555555" );
                            tcplongSocket.close();
                            Intent intentByLAN = new Intent(LaunchActivity.this,MyMainActivity.class);
                            Log.i("putExtra_hy", "secondLoginLANlaunch:"+userName+"---"+password+"---"+ip+"---"+saveToken);
                            intentByLAN.putExtra("uname",userName);
                            intentByLAN.putExtra("pwd",password);
                            intentByLAN.putExtra("host_ip",ip);
                            intentByLAN.putExtra("isFirstLogin",false);
                            intentByLAN.putExtra("token",saveToken);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("commandData",allJsonData.getCommandData());
                            intentByLAN.putExtras(bundle);
                            isStacked = false;
                            startActivity(intentByLAN);
                            finish();
                        }
                    }else if (strRoomInf==null){
                        tcplongSocket.close();
                        stopRec = true;
                        Toast.makeText(LaunchActivity.this, "内网验证没通过,尝试通过外网登录", Toast.LENGTH_SHORT).show();
                        Log.i("result", "handleMessage: launchActivity内网验证没通过,尝试通过外网登录");
                        Log.e(TAG, "handleMessage: 33333333333333333333333333333333333333333333333333333" );
                        secondLoginWAN();
                        break;
                    }

                    break;
                case 2:
                    //外网登陆获取token
                    tokenJson = (String) msg.obj;
                    Log.i(TAG, "handleMessage: handlllllllll"+tokenJson);
                    getToken(tokenJson);
                    break;
                case 3:      //外网登陆获取用户信息后跳转
                    VibratorUtil.Vibrate(LaunchActivity.this, 100);
                    allJson = (String) msg.obj;
                    NewJsonParseUtils newJsonParseUtils = new NewJsonParseUtils(allJson);    //Gson解析房间数据
                    CommandData commandData = newJsonParseUtils.newJsonParseWAN();

                    Log.i("byid_hy_intent", "从LaunchActivity传入给MyMainActivity的值：userName："+
                            userName+"---password："+password+"---ip："+ip+"---token："+token+"----commandData:"
                            +commandData+"------isFirstLogin:"+false+"-----isByWANLogin:"+true+"------");
                    //解析数据后跳转
                    Intent intentByWAN = new Intent(LaunchActivity.this,MyMainActivity.class);
                    intentByWAN.putExtra("uname",userName);
                    intentByWAN.putExtra("pwd",password);
                    intentByWAN.putExtra("token",token);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("commandData",commandData);
                    intentByWAN.putExtras(bundle);
                    intentByWAN.putExtra("isByWANLogin",true);
                    intentByWAN.putExtra("isFirstLogin",false);
                    intentByWAN.putExtra("needWANConnect",true);       //是否需要外网来尝试连接
                    isStacked = false;
                    startActivity(intentByWAN);
                    finish();
                    break;
                case 4:
                    finish();
                    System.exit(0);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setContentView(R.layout.launch_layout);
        ivLaunch = (ImageView) findViewById(R.id.iv_launch);

        //获取网络状态  ip地址
        isConnect = NetworkStateUtil.isNetworkAvailable(this);
        isMobileState = NetworkStateUtil.isNetworkMobileState(this);
        phoneIP = NetworkStateUtil.getPhoneIp();
        Log.i("result", "onCreate: -------当前是否有网络可用-------"+isConnect);
        Log.i("result", "onCreate: --------------是否为数据流量状态--------------"+isMobileState);
        Log.i("result", "onCreate: --------------本机ip地址--------------"+phoneIP);

        if (!isConnect){     //没联网时
            Toast.makeText(LaunchActivity.this, "请检查网络是否打开", Toast.LENGTH_LONG).show();
            Log.i(TAG, "onCreate: ----请检查网络是否打开-----");
            Message message = new Message();
            message.what = 4;
            handler.sendMessageDelayed(message,5000);
            return;
        }else {
            //手机网络可用时
            init();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!isMobileState){
            unbindService(connection);
        }
        if (tcplongSocket!=null){
            tcplongSocket.close();
        }
    }

    //判断是否验证通过
    private String judgeVerify(String strRec){
        String strVerify = null;
        String strJson = strRec.substring(0,strRec.length()-16);
        Log.i("judgeVerify", "judgeVerify: "+strJson);
        try {
            JSONObject jsonObject = new JSONObject(strJson);
            strVerify = jsonObject.getString("CommandData");
            Log.i(TAG, "judgeVerify:jsonObjectStr: "+strVerify);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return strVerify;
    }

    //判断是否房间信息
    private String judgeRoomJson(String strRec){
        String strRoomJson = null;
        String strJson = strRec.substring(0,strRec.length()-16);
        Log.i("strRoomJson", "strRoomJson: "+strJson);
        try {
            JSONObject jsonObject = new JSONObject(strJson);
            strRoomJson = jsonObject.getString("CommandUser");
            Log.i(TAG, "judgeVerify:jsonObjectStr: "+strRoomJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return strRoomJson;
    }

    private void init(){
        sp1 = getSharedPreferences("user_inform",MODE_PRIVATE);
        if (!isMobileState){      //在连接wifi的情况下，开启后台udp广播
            startUdpService();     //开启service 绑定service
        }
        //Log.i("hy_service", "init: "+udpBinder.getHostIp());
        //Log.i("hy_service", "init: "+udpBinder.getUdpCheck());

        //延迟3秒跳转
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //获取主机ip
                if (udpBinder!=null){
                    ip = udpBinder.getHostIp();
                    udpCheck = udpBinder.getUdpCheck();
                }
                Log.i(TAG, "run: 启动页获取udp接受到的主机ip："+ip+"-----接受到的主机check："+udpCheck);

                userName = sp1.getString("userName","");
                password = sp1.getString("password","");
                Log.i(TAG, "reciveIntent: -----------获取uname:"+userName+"，pwd:"+password+"-------------");

                //userName = null;     //为了模拟每次都是第一次登陆，以后删除
                //userName = "byidstest";     //模拟非第一次登陆
                //password = "byids";
                if ("".equals(userName)||userName==null){          //第一次登陆，必须外网，跳转登陆页面
                    Log.i(TAG, "run: ************************首次登陆，必须外网登陆，跳转登陆页************************");
                    Intent intent = new Intent(LaunchActivity.this,NewLoginActivity.class);
                    intent.putExtra("ip",ip);       //                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          `// 后台udp广播获取主机ip，如果能获取，表示可以本地内网连接，如果没能获取，走外网连接
                    Log.i(TAG, "run: --------------获取到主机ip没：-------------"+ip);
                    isStacked = false;
                    startActivity(intent);
                    finish();
                }else{          //非第一次登陆
                    //udpCheck = "外网测试，故意让内网不通，测完删除";
                    if (udpCheck.equals("ip")){      //内网连通
                        Toast.makeText(LaunchActivity.this, "内网找到主机，尝试通过内网登陆", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "run: ************************非首次登陆，找到主机，内网登陆************************");
                        secondLoginLAN();     //内网Tcp获取房间信息
                    }else {
                        //内网不通,走外网
                        Toast.makeText(LaunchActivity.this, "内网找不到主机通，外网登陆", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "run: ************************非首次登陆，内网不通，外网登陆************************");
                        secondLoginWAN();    //外网http获取房间信息
                    }
                }
            }
        },LAUNCH_DELAY);
        
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isStacked){
                    Log.i(TAG, "run: ----------------启动页卡住了，尝试外网登陆-----------------");
                    secondLoginWAN();    //外网http获取房间信息
                }
            }
        },10000);
    }

    //开启service，后台发送udp广播
    private void startUdpService(){
        Intent intentService = new Intent(this, UDPBroadcastService.class);
        startService(intentService);         //启动服务

        Intent bindIntent = new Intent(this, UDPBroadcastService.class);
        bindService(bindIntent, connection, BIND_AUTO_CREATE); // 绑定服务
    }


    //--------------非首次登录---------内网通过连接Tcp 获取房间信息-------------
    private ConnectTcp connectTcp;
    private void secondLoginLAN(){
        SharedPreferences spToken = getSharedPreferences("user_token",MODE_PRIVATE);
        saveToken = spToken.getString("token","");

        connectTcp = new ConnectTcp();
        tcplongSocket = new TcpLongSocket(connectTcp);
        tcplongSocket.startConnect(ip, DEFAULT_PORT);

    }


    //--------------非首次登录---------外网通过http请求获取房间信息-----------
    private void secondLoginWAN(){
        loginPost();  //http post
        //解析数据后跳转（测试，直接加载假数据跳转，以后删除）
    }


    //---------------------------------内网Tcp获取房间信息---------------------------------------
    private Message message;
    private class ConnectTcp implements TCPLongSocketCallback{

        @Override
        public void connected() {
            Log.i("MAIN", String.valueOf(tcplongSocket.getConnectStatus()));
            VibratorUtil.Vibrate(LaunchActivity.this, 100);
            JSONObject checkCommandData = new JSONObject();
            try {
                checkCommandData.put("kong", "keys");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String checkJson = CommandJsonUtils.getCommandJson(1, checkCommandData, saveToken, userName, password, String.valueOf(System.currentTimeMillis()));
            Log.i("result", "check" + checkJson);
            tcplongSocket.writeDate(Encrypt.encrypt(checkJson));

        }

        @Override
        public void receive(byte[] buffer) {             //接收主机信息
            /*
            * ***********************从主机获取了房间信息**********************用Gson处理******跳转
            * */
            if (buffer.length>12 && !stopRec){
                message = new Message();
                message.what = 1;
                message.obj = testDecryptByte(buffer);
                handler.sendMessage(message);
            }else {
                buffer = null;
            }
        }

        @Override
        public void disconnect() {
            tcplongSocket.close();
        }

    }

    //解密，返回json数据
    byte[] new_sendByte;
    byte[] nnew_sendByte;
    byte[] ivByte;
    byte[] dataByte;
    byte[] a;
    String strRoomInfo;
    private String testDecryptByte(byte[] sendByte){
        //Log.i(TAG, "test: ************************newsendByte"+sendByte.length);
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
        try {
            strRoomInfo = new String(a);
        }catch (Exception e){
            Log.i(TAG, "testDecryptByte: 错误"+e.toString());
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

    //-------------------------------外网http请求-----------------------------------
    //post登录，返回token
    private void loginPost(){
        //final String url = "http://192.168.10.230:20000/api/user/login";
        final String url = "http://115.29.97.189:20000/api/user/login";
        new Thread(){
            @Override
            public void run() {
                super.run();
                // 表单提交
                RequestBody formBody = new FormBody.Builder().add("num", userName).add("pwd", password).build();
                OkHttpClient client = new OkHttpClient();
                okhttp3.Request request = new okhttp3.Request.Builder().url(url).addHeader("content-type", "application/x-www-form-urlencoded").post(formBody).build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.i(TAG, "onFailure: -----------请求失败------------："+e.toString());
                    }

                    @Override
                    public void onResponse(Call call, okhttp3.Response response) throws IOException {
                        String token = response.body().string();
                        Log.i(TAG, "onResponse: token:"+token);
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
            Log.i(TAG, "getToken: 外网登陆获取的token："+token);
            Log.i("hy_result", "run: 外网登陆获取的token:"+token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //向服务器post token，获取房间信息
    private void postToken(final String token){
        //final String token1 = "00000000000000000000000000000000"; //暂时用
        //final String url = "http://192.168.10.230:20000/api/homeserver/profile";
        final String url = "http://115.29.97.189:20000/api/homeserver/profile";
        Log.e(TAG, "launchActivity_postToken: "+token);
        new Thread(){
            @Override
            public void run() {
                super.run();
                OkHttpClient client = new OkHttpClient();
                okhttp3.Request request = new okhttp3.Request.Builder().url(url).addHeader("content-type", "application/json").addHeader("token",token).build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.i(TAG, "onFailure: -----------请求失败------------："+e.toString());
                    }

                    @Override
                    public void onResponse(Call call, okhttp3.Response response) throws IOException {
                        String resp = response.body().string();
                        //Log.i(TAG, "onResponse: 房间信息："+resp);
                        LongLogCatUtil.logE(TAG,resp);
                        Message msg = new Message();
                        msg.what = 3;
                        msg.obj = resp;
                        handler.sendMessage(msg);
                    }
                });
            }
        }.start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==event.KEYCODE_BACK){
            System.exit(0);
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    //------------------内网第二次登陆直接跳转主界面---------------------
    /*private void oldSecondLoginLAN(){
        SharedPreferences sp1 = getSharedPreferences("user_inform",MODE_PRIVATE);
        userName = sp1.getString("userName","");
        password = sp1.getString("password","");
        //Log.i("result", "secondLogin:userName "+userName+"password"+password);

        SharedPreferences sp = getSharedPreferences("homeJson",MODE_PRIVATE);
        //Log.i("result", "secondLogin:---------- "+sp.getString("homeJson",""));
        HomeJsonDataUtils homeJsonDataUtils = new HomeJsonDataUtils();
        homeJsonDataUtils.doJsonParse(sp.getString("homeJson",""));      //获取SharedPreferences保存的json数据并解析
        hid = homeJsonDataUtils.getHid();
        roomNameList = homeJsonDataUtils.getRoomNameList();
        roomDBNameList = homeJsonDataUtils.getRoomDBNameList();
        roomAttr = homeJsonDataUtils.getRoomAttr();
        homeAttrBean = homeJsonDataUtils.getHomeAttrBean();


        Intent intent = new Intent(LaunchActivity.this, MyMainActivity.class);
        intent.putExtra("roomNameList",roomNameList);
        intent.putExtra("roomDBNameList",roomDBNameList);
        intent.putExtra("roomAttr",roomAttr);
        intent.putExtra("hid",hid);
        intent.putExtra("uname",userName);
        intent.putExtra("pwd",password);
        intent.putExtra("ip",ip);
        Bundle bundle = new Bundle();
        bundle.putSerializable("homeAttr",homeAttrBean);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();        //结束此activity，下一个activity返回时，直接退出
    }



    //测试  包装发送udp
    public void test(String hid){

        String udpJson="{\"command\":\"find\",\"data\":{\"hid\":\""+hid+"\",\"loginName\":\"byids\"}}";
        //Log.i("result", "test: ------------------"+udpJson);
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
        AES.byteStringLog(sendByte);
        //发送udp广播
        new BroadCastUdp(sendByte).start();
    }

    //发送UDP
    public class BroadCastUdp extends Thread {

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
                udpSocket.receive(receiveData);
            } catch (Exception e) {
// TODO Auto-generated catch block
                Log.e(LOG_TAG, e.toString());
                udpSocket.close();
            }
            if (null!=receiveData){

                if( 0!=receiveData.getLength() ) {
                    String codeString = new String( buffer, 0, receiveData.getLength() );

                    Log.i("result", "接收到数据为codeString: "+codeString);
                    udpCheck = codeString.substring(2,4);
                    Log.i("result", "接收到数据为: "+udpCheck);
                    Log.i("result","recivedataIP地址为："+receiveData.getAddress().toString().substring(1));//此为IP地址
                    //Log.i("result","recivedata_sock地址为："+receiveData.getAddress());//此为IP加端口号


                    //7.4    连接udp，

                    ip = receiveData.getAddress().toString().substring(1);   //ip地址
                    //Log.i("result", "run: --------===========----------------"+ip);

                }
            }else{
                try {
                    udpSocket.send(dataPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            udpSocket.close();

            Message message = new Message();
            message.what = 1;
            message.obj = ip;
            handler.sendMessage(message);
        }
    }*/
}
