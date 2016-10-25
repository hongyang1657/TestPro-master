package com.byids.hy.testpro.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.byids.hy.testpro.Bean.HomeAttr;
import com.byids.hy.testpro.R;
import com.byids.hy.testpro.utils.AES;
import com.byids.hy.testpro.utils.ByteUtils;
import com.byids.hy.testpro.utils.HomeJsonDataUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * Created by gqgz2 on 2016/10/21.
 */
public class LaunchActivity extends Activity{

    private static final int LAUNCH_DELAY = 2000;
    private ImageView ivLaunch;

    public static final int DEFAULT_PORT = 57816;//端口号
    public static final String LOG_TAG = "WifiBroadcastActivity";
    private byte[] buffer = new byte[MAX_DATA_PACKET_LENGTH];
    private static final int MAX_DATA_PACKET_LENGTH = 100;
    private String udpCheck = "";
    private String ip;    //接收到的ip地址

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    Log.i("result", "handleMessage:ggggggggggggggggggggggggggggggg "+msg.obj);
                    Log.i("result", "secondLogin: -----------------------------------"+userName+"------------"+password+"------------"+ip);
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
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setContentView(R.layout.launch_layout);
        ivLaunch = (ImageView) findViewById(R.id.iv_launch);

        //延迟2秒跳转
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sp = getSharedPreferences("homeJson",MODE_PRIVATE);
                String a = sp.getString("homeJson","");
                    if (a==null||a==""){
                        Log.i("result", "run: ============第一次登陆，跳转登陆页面==============");
                        Intent intent = new Intent(LaunchActivity.this,NewLoginActivity.class);
                        startActivity(intent);
                        finish();
                }else{
                    secondLogin();  //跳转主界面
                }
            }
        },LAUNCH_DELAY);
    }

    //用同一账号第二次登陆直接跳转主界面
    String userName;
    String password;
    String[] roomNameList;
    String[] roomDBNameList;
    String roomAttr;
    HomeAttr homeAttrBean;
    String hid;
    private void secondLogin(){
        SharedPreferences sp1 = getSharedPreferences("user_inform",MODE_PRIVATE);
        userName = sp1.getString("userName","");
        password = sp1.getString("password","");

        SharedPreferences sp = getSharedPreferences("homeJson",MODE_PRIVATE);
        HomeJsonDataUtils homeJsonDataUtils = new HomeJsonDataUtils();
        homeJsonDataUtils.doJsonParse(sp.getString("homeJson",""));      //获取SharedPreferences保存的json数据并解析
        hid = homeJsonDataUtils.getHid();
        roomNameList = homeJsonDataUtils.getRoomNameList();
        roomDBNameList = homeJsonDataUtils.getRoomDBNameList();
        roomAttr = homeJsonDataUtils.getRoomAttr();
        homeAttrBean = homeJsonDataUtils.getHomeAttrBean();
        test(hid);


    }

    //测试  包装发送udp
    public void test(String hid){

        String udpJson="{\"command\":\"find\",\"data\":{\"hid\":\""+hid+"\",\"loginName\":\"byids\"}}";
        Log.i("result", "test: ------------------"+udpJson);
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

                    /*
                    7.4    连接udp，
                     */
                    ip = receiveData.getAddress().toString().substring(1);   //ip地址
                    Log.i("result", "run: --------===========----------------"+ip);

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
    }
}
