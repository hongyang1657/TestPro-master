package com.byids.hy.testpro.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.byids.hy.testpro.Bean.EzvizCamera;
import com.byids.hy.testpro.R;
import com.squareup.picasso.Picasso;
import com.videogo.exception.BaseException;
import com.videogo.openapi.EZConstants;
import com.videogo.openapi.EZOpenSDK;
import com.videogo.openapi.EZPlayer;
import com.videogo.openapi.bean.EZCameraInfo;
import com.videogo.openapi.bean.EZUserInfo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by gqgz2 on 2016/9/21.
 */
public class CameraActivity extends Activity{

    private static String TAG = "result";
    private Button btRoom1;
    private Button btRoom2;
    private Button btSound;
    private Button btSpeak;
    private Button btTalk;
    private Button btRecord;

    private ImageView ivCapture;
    private SurfaceView svCamera;
    private SurfaceHolder surfaceHolder;
    private List<EZCameraInfo> cameraList = new ArrayList<EZCameraInfo>();
    private String[] cameraNames;
    private EZOpenSDK ezOpenSDK;
    private EZPlayer ezPlayer;
    private String cameraName;
    private String picUrl;
    private String captureUrl;
    private String cameraId;

    private Calendar calendar;


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    btRoom1.setText(cameraName);
                    //Picasso.with(CameraActivity.this).load(captureUrl).into(ivCapture);
                    break;
                default:
                    break;
            }
        }
    };
    //监控播放handler
    private Handler cameraHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setContentView(R.layout.camera_layout);
        initView();
    }

    private void initView(){
        ezOpenSDK = EZOpenSDK.getInstance();
        btRoom1 = (Button) findViewById(R.id.bt_room1);
        btRoom2 = (Button) findViewById(R.id.bt_room2);
        btSound = (Button) findViewById(R.id.bt_sound);
        btSpeak = (Button) findViewById(R.id.bt_speak);
        btTalk = (Button) findViewById(R.id.bt_talk);
        btRecord = (Button) findViewById(R.id.bt_record);
        //ivCapture = (ImageView) findViewById(R.id.iv_capture);
        svCamera = (SurfaceView) findViewById(R.id.sfv_camera);
        surfaceHolder = svCamera.getHolder();
        //surfaceHolder.addCallback(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                /*try {
                    cameraList = ezOpenSDK.getCameraList(0,2);
                    cameraName = new String[cameraList.size()];
                    for (int i=0;i<cameraList.size();i++){
                        cameraName[i] = cameraList.get(0).getCameraName();
                    }
                    Log.i(TAG, "initView: ----摄像头名----"+cameraName[0]);
                } catch (BaseException e) {
                    e.printStackTrace();
                }
                btRoom1.setText(cameraName[0]);*/
                try {
                    SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
                    Log.i("result", "onCreate: +"+pref.getString("token",""));
                    ezOpenSDK.setAccessToken(pref.getString("token",""));
                    cameraList = ezOpenSDK.getCameraList(0,10);
                    EZUserInfo uinfo = ezOpenSDK.getUserInfo();
                    cameraName = cameraList.get(0).getCameraName();
                    String cameraDeviceId = cameraList.get(0).getDeviceId();
                    String cameraDeviceName = cameraList.get(0).getDeviceName();
                    String cameraDeviceSerial = cameraList.get(0).getDeviceSerial();
                    cameraId = cameraList.get(0).getCameraId();
                    Log.i(TAG, "run: ------------cameraName--"+cameraName+"----cameraDeviceId--"+cameraDeviceId+"-------cameraDeviceName--"+
                            cameraDeviceName+"-------cameraDeviceSerial---"+cameraDeviceSerial+"-------cameraId----"+cameraId);
                    EZCameraInfo cameraInfo = ezOpenSDK.getCameraInfo(cameraDeviceSerial);
                    int channelNo = cameraInfo.getChannelNo();
                    picUrl = cameraInfo.getPicUrl();
                    captureUrl = EZOpenSDK.getInstance().capturePicture(cameraDeviceSerial,channelNo);



                } catch (BaseException e) {
                    e.printStackTrace();
                }
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        }).start();

    }

    private int soundFlag = 0;
    private int speakFlag = 0;
    private int videoFlag = 0;
    private int talkFlag = 0;
    public void cameraClick(View v){
        switch (v.getId()){
            case R.id.bt_room1:
                ezPlayer = ezOpenSDK.createPlayer(CameraActivity.this,cameraId);    //视频播放
                boolean isHandlerMessage = ezPlayer.setHandler(cameraHandler);
                boolean isSetSurfaceHold = ezPlayer.setSurfaceHold(surfaceHolder);

                Log.i(TAG, "run: -----------isHandlerMessage-----------"+isHandlerMessage+"---------isSetSurfaceHold--------"+isSetSurfaceHold+
                        "-------isStartPlay------");
                boolean isStartPlay = ezPlayer.startRealPlay();    //开始播放
                break;
            case R.id.bt_room2:
                Intent intent = new Intent(CameraActivity.this,FullScreenCameraActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_sound:
                if (soundFlag==0){
                    ezPlayer.openSound();
                    btSound.setText("声音开");
                    soundFlag = 1;
                }else if (soundFlag==1){
                    ezPlayer.closeSound();
                    btSound.setText("声音关");
                    soundFlag = 0;
                }
                break;
            case R.id.bt_speak:
                if (speakFlag==0){
                    boolean a = ezPlayer.startVoiceTalk();
                    Toast.makeText(CameraActivity.this, "对讲开"+a, Toast.LENGTH_SHORT).show();
                    speakFlag = 1;
                }else if (speakFlag==1){
                    boolean b = ezPlayer.stopVoiceTalk();
                    Toast.makeText(CameraActivity.this, "对讲关"+b, Toast.LENGTH_SHORT).show();
                    speakFlag = 0;
                }
                break;
            case R.id.bt_stop_play:
                ezPlayer.stopRealPlay();         //暂停播放
                ezOpenSDK.releasePlayer(ezPlayer);        //关闭视屏播放（释放内存）
                break;
            case R.id.bt_video_level:
                try {
                    if (videoFlag==0){
                        boolean a = ezPlayer.setVideoLevel(EZConstants.EZVideoLevel.VIDEO_LEVEL_FLUNET);
                        Toast.makeText(CameraActivity.this, "清晰度1"+a, Toast.LENGTH_SHORT).show();
                        videoFlag = 1;
                    }else if (videoFlag==1){
                        boolean a = ezPlayer.setVideoLevel(EZConstants.EZVideoLevel.VIDEO_LEVEL_BALANCED);
                        Toast.makeText(CameraActivity.this, "清晰度2"+a, Toast.LENGTH_SHORT).show();
                        videoFlag = 2;
                    }else if (videoFlag==2){
                        boolean a = ezPlayer.setVideoLevel(EZConstants.EZVideoLevel.VIDEO_LEVEL_HD);
                        Toast.makeText(CameraActivity.this, "清晰度3"+a, Toast.LENGTH_SHORT).show();
                        videoFlag = 0;
                    }

                } catch (BaseException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.bt_talk:
                if (talkFlag==0){
                    ezPlayer.setVoiceTalkStatus(true);
                    btTalk.setText("开始讲");
                    talkFlag = 1;
                }else if (talkFlag==1){
                    ezPlayer.setVoiceTalkStatus(false);
                    btTalk.setText("停止讲");
                    talkFlag = 0;
                }
                break;
            case R.id.bt_record:
                //ezOpenSDK.searchRecordFileFromCloud(cameraId,)
                break;
            default:
                break;
        }
    }


}
