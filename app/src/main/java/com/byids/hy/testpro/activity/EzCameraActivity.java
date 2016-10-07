package com.byids.hy.testpro.activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Typeface;
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
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.byids.hy.testpro.R;
import com.byids.hy.testpro.adapter.CameraDataBaseAdapter;
import com.squareup.picasso.Picasso;
import com.videogo.exception.BaseException;
import com.videogo.openapi.EZConstants;
import com.videogo.openapi.EZOpenSDK;
import com.videogo.openapi.EZPlayer;
import com.videogo.openapi.bean.EZCameraInfo;
import com.videogo.openapi.bean.EZCloudRecordFile;
import com.videogo.openapi.bean.EZUserInfo;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by gqgz2 on 2016/9/29.
 */
public class EzCameraActivity extends Activity{

    private static String TAG = "result";
    private SurfaceHolder surfaceHolder;
    private List<EZCameraInfo> cameraList = new ArrayList<EZCameraInfo>();
    private String[] cameraNames;
    private EZOpenSDK ezOpenSDK;
    private EZPlayer ezPlayer;
    private String cameraName;
    private String cameraId;
    private String picUrl;
    private String captureUrl;
    private String[] cameraDate = {"今天","昨天","前天","1","1","1","1","1","1","1","1","1","1","1"};
    private Typeface typeFace;

    private LinearLayout llCameraDataList;
    private ListView lvJiankong;
    private CameraDataBaseAdapter cameraDataBaseAdapter;
    private TextView tvCameraRoomName;
    private SurfaceView sfvCamera;
    private ImageView ivCapture;
    private ImageView ivShoushi;
    private LinearLayout llPlayStatus;
    private ImageView ivKuaitui;
    private ImageView ivPlay;
    private ImageView ivKuaijin;
    private SeekBar sbJiankong;
    private ImageView ivCameraLoading;
    private List<EZCloudRecordFile> EzCloudFileList;
    private Calendar mStartTime = null;
    private Calendar mEndTime = null;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    tvCameraRoomName.setText(cameraName);
                    Picasso.with(EzCameraActivity.this).load(captureUrl).into(ivCapture);
                    playCamera();
                    loadingAnimation();
                    break;
                case 2:
                    Log.i(TAG, "getCloudRecord: +++++++++++++++++++++++++"+EzCloudFileList.size());
                    Log.i(TAG, "run: ******************************"+EzCloudFileList.size());

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
            switch (msg.what){
                case 125:
                    Log.i(TAG, "handleMessage: ###############################125");       //还未播放
                    break;
                case 126:
                    Log.i(TAG, "handleMessage: ###############################126");       //还未播放
                    break;
                case 127:
                    Log.i(TAG, "handleMessage: ###############################127");      //快开始播放了
                    break;
                case 102:
                    ivCameraLoading.setVisibility(View.GONE);
                    setCurrentProgress(); //设置进度条
                    Log.i(TAG, "handleMessage: ###############################102");       //视屏开始播放
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setContentView(R.layout.camera_main_layout);
        initView();
    }
    private void initView(){
        ezOpenSDK = EZOpenSDK.getInstance();
        llCameraDataList = (LinearLayout) findViewById(R.id.ll_camera_data_list);
        lvJiankong = (ListView) findViewById(R.id.lv_jiankong);
        tvCameraRoomName = (TextView) findViewById(R.id.tv_camera_room_name);
        sfvCamera = (SurfaceView) findViewById(R.id.sfv_camera);
        ivCapture = (ImageView) findViewById(R.id.iv_capture);
        ivShoushi = (ImageView) findViewById(R.id.iv_shoushi);
        llPlayStatus = (LinearLayout) findViewById(R.id.ll_play_status);
        ivKuaitui = (ImageView) findViewById(R.id.iv_kuaitui);
        ivPlay = (ImageView) findViewById(R.id.iv_play);
        ivKuaijin = (ImageView) findViewById(R.id.iv_kuaijin);
        sbJiankong = (SeekBar) findViewById(R.id.sb_jiankong);
        ivCameraLoading = (ImageView) findViewById(R.id.iv_camera_loading);

        typeFace = Typeface.createFromAsset(getAssets(),"fonts/xiyuanti.ttf");
        tvCameraRoomName.setTypeface(typeFace);

        sbJiankong.setOnSeekBarChangeListener(seekBarListener);
        cameraDataBaseAdapter = new CameraDataBaseAdapter(this,cameraDate);
        lvJiankong.setAdapter(cameraDataBaseAdapter);
        lvJiankong.setOnItemClickListener(new AdapterView.OnItemClickListener() {         //选择查看监控的日期
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cameraDataBaseAdapter.changeSelected(position);
            }
        });

        surfaceHolder = sfvCamera.getHolder();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
                    Log.i("result", "onCreate: +"+pref.getString("token",""));
                    ezOpenSDK.setAccessToken(pref.getString("token",""));
                    cameraList = ezOpenSDK.getCameraList(0,10);
                    Log.i(TAG, "run: -----rrrrrrrrrrrrrrrr------------"+cameraList.get(0).getCameraName());
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

        getCloudRecord();
    }

    private boolean isFullScreen = false;
    private boolean isStartPlay;
    private boolean isHandlerMessage;
    private boolean isSetSurfaceHold;

    public void cameraClick(View v){
        switch (v.getId()){
            case R.id.iv_capture:
                playCamera();
                loadingAnimation();
                break;
            case R.id.iv_shoushi:
                setfullscreen();          //设置全屏或退出全屏
                break;
            case R.id.iv_kuaitui:
                break;
            case R.id.iv_play:
                if (isStartPlay==true){
                    ezPlayer.stopRealPlay();         //暂停播放
                    ivCapture.setVisibility(View.VISIBLE);   //截图设为可见
                    isStartPlay = false;
                    ivPlay.setImageResource(R.mipmap.play_3x);
                }else if (isStartPlay==false){
                    playCamera();
                    loadingAnimation();
                }
                break;
            case R.id.iv_kuaijin:
                break;
            case R.id.sfv_camera:
                setfullscreen();          //设置全屏或退出全屏
                break;
            default:
                break;
        }
    }

    //播放监控
    private void playCamera(){
        ivCapture.setVisibility(View.GONE);   //截图设为不可见
        ezPlayer = ezOpenSDK.createPlayer(EzCameraActivity.this,cameraId);    //视频播放
        isHandlerMessage = ezPlayer.setHandler(cameraHandler);
        isSetSurfaceHold = ezPlayer.setSurfaceHold(surfaceHolder);
        Log.i(TAG, "run: -----------isHandlerMessage-----------"+isHandlerMessage+"---------isSetSurfaceHold--------"+isSetSurfaceHold+ "-------isStartPlay------");
        isStartPlay = ezPlayer.startRealPlay();    //开始播放
        Log.i(TAG, "playCamera: @@@@@@@@@@@@@@isStartPlay@@@@@@@@@@@@@"+isStartPlay);
        ivPlay.setImageResource(R.mipmap.camera_stop_3x);
    }
    //全屏，退出全屏
    private void setfullscreen(){
        if (isFullScreen==false){
            //设为全屏
            llCameraDataList.setVisibility(View.GONE);
            llPlayStatus.setVisibility(View.GONE);

            ivShoushi.setVisibility(View.VISIBLE);
            isFullScreen = true;
        }else if (isFullScreen==true){
            //返回选择界面
            llCameraDataList.setVisibility(View.VISIBLE);
            llPlayStatus.setVisibility(View.VISIBLE);
            ivShoushi.setVisibility(View.GONE);
            isFullScreen = false;
        }
    }

    //设置当前进度条进度
    private void setCurrentProgress(){
        Calendar calendar = ezPlayer.getOSDTime();  //获取当前时间
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int minOfDay = hour*60+min;
        Log.i(TAG, "playCamera: 时间"+hour+"时"+min+"分"+second+"秒");
        sbJiankong.setProgress(minOfDay);
    }

    /*
     *  传入0~1440 （一天中的分钟数），换算成Calendar类型
     *
     */
    private void transCalendar(int minOfDay){
        //Calendar.getInstance().set();
    }

    //获取云视频
    private void getCloudRecord(){


        mStartTime = Calendar.getInstance();
        mStartTime.set(Calendar.AM_PM, 0);
        mStartTime.set(mStartTime.get(Calendar.YEAR), mStartTime.get(Calendar.MONTH),
                mStartTime.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        mEndTime = Calendar.getInstance();
        mEndTime.set(Calendar.AM_PM, 0);
        mEndTime.set(mEndTime.get(Calendar.YEAR), mEndTime.get(Calendar.MONTH),
                mEndTime.get(Calendar.DAY_OF_MONTH), 23, 59, 59);

        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    EzCloudFileList = EZOpenSDK.getInstance().searchRecordFileFromCloud(cameraId,mStartTime,mEndTime);
                    Log.i(TAG, "run: ******************************"+EzCloudFileList.size());
                    Message message = new Message();
                    message.what = 2;
                    handler.sendMessage(message);

                } catch (BaseException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }


    //进度条
    SeekBar.OnSeekBarChangeListener seekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            Log.i(TAG, "onProgressChanged: ------------松手-------------"+seekBar.getProgress());
        }
    };

    private void loadingAnimation(){
        ivCameraLoading.setVisibility(View.VISIBLE);
        ObjectAnimator objectAnimator = new ObjectAnimator().ofFloat(ivCameraLoading,"rotation",0f,-360f).setDuration(500);
        LinearInterpolator ll = new LinearInterpolator();
        objectAnimator.setRepeatCount(-1);
        objectAnimator.setInterpolator(ll);
        objectAnimator.start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK&&isFullScreen==false&&ezPlayer!=null){
            ezPlayer.stopRealPlay();         //暂停播放
            ezOpenSDK.releasePlayer(ezPlayer);        //关闭视屏播放（释放内存）
        }else if (isFullScreen==true){    //如果在全屏页面返回选择页面
            //返回选择界面
            llCameraDataList.setVisibility(View.VISIBLE);
            llPlayStatus.setVisibility(View.VISIBLE);
            ivShoushi.setVisibility(View.GONE);
            isFullScreen = false;
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
