package com.byids.hy.testpro.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.byids.hy.testpro.Bean.HomeAttr;
import com.byids.hy.testpro.R;
import com.byids.hy.testpro.TCPLongSocketCallback;
import com.byids.hy.testpro.TcpLongSocket;
import com.byids.hy.testpro.service.UDPBroadcastService;
import com.byids.hy.testpro.utils.AES;
import com.byids.hy.testpro.utils.CommandJsonUtils;
import com.byids.hy.testpro.utils.Encrypt;
import com.byids.hy.testpro.utils.LongLogCatUtil;
import com.byids.hy.testpro.utils.NetworkStateUtil;
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
 * Created by gqgz2 on 2016/10/21.
 */
public class LaunchActivity extends Activity{

    private static final String TAG = "result";
    private static final int LAUNCH_DELAY = 3000;
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
    private String[] roomNameList;
    private String[] roomDBNameList;
    private String roomAttr;
    private HomeAttr homeAttrBean;
    private String hid = "56e276f3736fb0872c69d876";
    private String allJson;

    //长连接
    private TcpLongSocket tcplongSocket;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                /*case 1:
                    //Log.i("result", "handleMessage:ggggggggggggggggggggggggggggggg "+msg.obj);
                    //Log.i("result", "secondLogin: -----------------------------------"+userName+"------------"+password+"------------"+ip);
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
                    break;*/
                case 2:
                    //获取token
                    String token = (String) msg.obj;
                    Log.i(TAG, "handleMessage: handlllllllll"+token);
                    getToken(token);
                    break;
                case 3:      //获取用户信息
                    VibratorUtil.Vibrate(LaunchActivity.this, 1000);
                    allJson = (String) msg.obj;
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
            String a = udpBinder.getHostIp();
            Log.i(TAG, "onServiceConnected: -----获取后台接受的主机ip------"+a);
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
        boolean isConnect = NetworkStateUtil.isNetworkAvailable(this);
        boolean isMobileState = NetworkStateUtil.isNetworkMobileState(this);
        String phoneIP = NetworkStateUtil.getPhoneIp();
        Log.i("result", "onCreate: -------当前是否有网络可用-------"+isConnect);
        Log.i("result", "onCreate: --------------是否为数据流量状态--------------"+isMobileState);
        Log.i("result", "onCreate: --------------本机ip地址--------------"+phoneIP);

        if (!isConnect){     //没联网时
            Toast.makeText(this, "请检查网络是否打开", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "onCreate: ----请检查网络是否打开-----");
            return;
        }else {
            init();
        }
    }

    private void init(){
        sp1 = getSharedPreferences("user_inform",MODE_PRIVATE);
        startTcpService();     //开启service
        Intent bindIntent = new Intent(this, UDPBroadcastService.class);
        bindService(bindIntent, connection, BIND_AUTO_CREATE); // 绑定服务


        /*udpSocket = new UDPSocket("主机在吗？");
        udpSocket.sendEncryptUdp();*/

        //延迟3秒跳转
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //获取主机ip
                Log.i(TAG, "run: ^^^^^^^^启动页^^^^^^^^获取主机ip^^^^^^^^^启动页^^^^^^^"+udpBinder.getHostIp());
                ip = udpBinder.getHostIp();
                udpCheck = udpBinder.getUdpCheck();

                userName = sp1.getString("userName","");
                password = sp1.getString("password","");
                /*udpCheck = udpSocket.getUdpCheck();
                ip = udpSocket.getIp();
                Log.i(TAG, "onCreate: 如果是ip就表示找到主机了:"+udpCheck);
                Log.i(TAG, "onCreate: 主机的ip地址:"+ip);*/
                //userName = null;     //为了模拟每次都是第一次登陆，以后删除
                if (userName==null||userName==""){          //第一次登陆，必须外网，跳转登陆页面
                    Log.i(TAG, "run: ************************首次登陆，必须外网登陆，跳转登陆页************************");
                    Intent intent = new Intent(LaunchActivity.this,NewLoginActivity.class);
                    ip = null;     //删除
                    intent.putExtra("ip",ip);       //后台udp广播获取主机ip，如果能获取，表示可以本地内网连接，如果没能获取，走外网连接
                    Log.i(TAG, "run: --------------获取到主机ip没：-------------"+ip);
                    startActivity(intent);
                    finish();
                }else{          //非第一次登陆
                    udpCheck = "外网测试，故意让内网不通，测完删除";
                    if (udpCheck=="ip"||udpCheck.equals("ip")){      //内网连通
                        Toast.makeText(LaunchActivity.this, "找到主机，内网登陆", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "run: ************************非首次登陆，找到主机，内网登陆************************");
                        secondLoginLAN();    //内网Tcp获取房间信息
                    }else {
                        //内网不通,走外网
                        Toast.makeText(LaunchActivity.this, "内网不通，外网登陆", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "run: ************************非首次登陆，内网不通，外网登陆************************");
                        secondLoginWAN();    //外网http获取房间信息
                    }
                }
            }
        },LAUNCH_DELAY);
    }

    //开启service，后台发送udp广播
    private void startTcpService(){
        Intent intentService = new Intent(this, UDPBroadcastService.class);
        startService(intentService);         //启动服务
    }


    //内网通过连接Tcp 获取房间信息
    private void secondLoginLAN(){
        tcplongSocket = new TcpLongSocket(new ConnectTcp());
        tcplongSocket.startConnect(ip, DEFAULT_PORT);
        //解析数据后跳转（测试，直接加载假数据跳转，以后删除）
        /*Intent intent = new Intent(LaunchActivity.this,MyMainActivity.class);
        startActivity(intent);*/
    }


    //外网通过http请求获取房间信息
    private void secondLoginWAN(){
        loginPost();  //http post
        //解析数据后跳转（测试，直接加载假数据跳转，以后删除）
        /*Intent intent = new Intent(LaunchActivity.this,MyMainActivity.class);
        startActivity(intent);*/
    }


    //---------------------------------内网Tcp获取房间信息---------------------------------------
    private class ConnectTcp implements TCPLongSocketCallback{

        @Override
        public void connected() {
            Log.i("MAIN", String.valueOf(tcplongSocket.getConnectStatus()));
            VibratorUtil.Vibrate(LaunchActivity.this, 500);
            JSONObject checkCommandData = new JSONObject();
            try {
                checkCommandData.put("kong", "keys");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String checkJson = CommandJsonUtils.getCommandJson(1, checkCommandData, hid, userName, password, String.valueOf(System.currentTimeMillis()));
            Log.i("result", "check" + checkJson);
            tcplongSocket.writeDate(Encrypt.encrypt(checkJson));

        }

        @Override
        public void receive(byte[] buffer) {             //接收主机信息
            /*
            * ******************************从主机获取了房间信息*******************************用Gson处理
            * */
            String strRoomInfo = testDecryptByte(buffer);
            LongLogCatUtil.logE("result",strRoomInfo);     //多行打印logcat
        }

        @Override
        public void disconnect() {
            tcplongSocket.close();
        }
    }

    //解密，返回json数据
    private String testDecryptByte(byte[] sendByte){
        byte[] new_sendByte = Arrays.copyOfRange(sendByte,12,sendByte.length);
        byte[] nnew_sendByte = Arrays.copyOfRange(new_sendByte,0,new_sendByte.length-4);
        Log.i(TAG, "test: ************************newsendByte"+nnew_sendByte.length);
        Log.i(TAG, "test: *************newsendByte**************"+byteStringLog(nnew_sendByte));
        byte[] ivByte = Arrays.copyOfRange(nnew_sendByte,nnew_sendByte.length-16,nnew_sendByte.length);
        byte[] dataByte = Arrays.copyOfRange(nnew_sendByte,0,nnew_sendByte.length-16);
        Log.i(TAG, "test: *****************加密向量长度*******newsendByte"+ivByte.length);
        Log.i(TAG, "test: *************newsendByte*****加密向量*********"+byteStringLog(ivByte));
        Log.i(TAG, "test: *****************加密数据长度*******newsendByte"+dataByte.length);
        Log.i(TAG, "test: *************newsendByte*****加密数据*********"+byteStringLog(dataByte));
        byte[] a = AES.decrypt(dataByte,ivByte);
        String strRoomInfo = new String(a);
        Log.i(TAG, "testDecryptByte: !!!!!!!!!!!!!!!!!!!!!!!!!!!!"+a.length+"!!!!!!!!"+strRoomInfo);
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
        final String url = "http://192.168.3.102:2000/api/user/login";
        new Thread(){
            @Override
            public void run() {
                super.run();
                // 表单提交
                RequestBody formBody = new FormBody.Builder().add("num", "100000").add("pwd", "smile2014").build();
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
            String token = obj.getString("token");      //获取token
            postToken(token);
            Log.i(TAG, "getToken: ^^^^^^^^^^^^"+token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //向服务器post token，获取房间信息
    private void postToken(final String token){
        final String token1 = "00000000000000000000000000000000"; //暂时用
        final String url = "http://192.168.3.102:2000/api/user/profile";
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
                        Log.i(TAG, "onResponse: 房间信息："+resp);
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
