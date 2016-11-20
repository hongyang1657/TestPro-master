package com.byids.hy.testpro.activity;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
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
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.byids.hy.testpro.R;
import com.byids.hy.testpro.adapter.CameraDataBaseAdapter;
import com.byids.hy.testpro.adapter.JianKongRoomAdapter;
import com.byids.hy.testpro.adapter.PickTimeAdapter;
import com.squareup.picasso.Picasso;
import com.videogo.exception.BaseException;
import com.videogo.openapi.EZOpenSDK;
import com.videogo.openapi.EZPlayer;
import com.videogo.openapi.bean.EZCameraInfo;
import com.videogo.openapi.bean.EZCloudRecordFile;
import com.videogo.openapi.bean.EZDeviceRecordFile;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by gqgz2 on 2016/9/29.
 */
public class EzCameraActivity extends BaseActivity{

    private static String TAG = "result";
    private int width;
    private int height;
    private SurfaceHolder surfaceHolder;
    private List<EZCameraInfo> cameraList = new ArrayList<EZCameraInfo>();
    private String[] cameraNames;
    private EZOpenSDK ezOpenSDK;
    private EZPlayer ezPlayer;
    private EZPlayer ezPlayerCloud = null;
    private String cameraName;
    private String cameraId;
    private String picUrl;
    private String captureUrl;
    private String[] cameraDate = {"今天","昨天","前天","1","1","1","1"};
    private Typeface typeFace;

    private LinearLayout llSwitchButtonLeft;
    private TextView tvSwitchVcr;
    private TextView tvSwitchRoom;
    private LinearLayout llCameraList;
    private ListView lvCamera;


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
    private List<EZCloudRecordFile> EzCloudFileList;       //云视频文件列表
    private List<EZDeviceRecordFile> EzDeviceFileList;     //sd卡视频文件列表
    private EZCloudRecordFile ezCloudRecordFile;
    private EZDeviceRecordFile ezDeviceRecordFile;
    private Calendar mStartTime = null;
    private Calendar mEndTime = null;

    private ListView lvPickTime;      //选择监控片段的listview
    private PickTimeAdapter pickTimeAdapter;
    private String[] timesPickList = {"1","2","3","4","5","6","7","8","9","10"};

    private boolean isFullScreen = false;
    private boolean isStartPlay;    //是否开始实时预览
    private boolean isHandlerMessage;
    private boolean isSetSurfaceHold;
    private boolean isCloudPlay = false;
    private boolean isDevicePlay = false;
    private PopupWindow popupWindow;
    private ListView lvRoom;
    private JianKongRoomAdapter roomAdapter;
    private int cameraNumber;
    private int cameraIndex = 0;   //当前播放的摄像头
    private int cameraDatePosition;    //选择的录像日期 posirion
    private LayoutInflater inflater = null;
    private int month;
    private int day;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:        //初始化摄像头
                    tvCameraRoomName.setText(cameraName);
                    Picasso.with(EzCameraActivity.this).load(captureUrl).into(ivCapture);
                    playCamera(msg.arg1);
                    loadingAnimation();
                    if (inflater==null){
                        initPopupWindow();
                    }
                    break;
                case 2:
                    Log.i(TAG, "getCloudRecord: +++++++++++++++++++++++++"+EzCloudFileList.size());
                    Log.i(TAG, "run: ******************************"+EzCloudFileList.size());

                    if (EzCloudFileList!=null){
                        isCloudPlay = ezPlayerCloud.startPlayback(ezCloudRecordFile);
                    }
                    if (EzDeviceFileList!=null){
                        isDevicePlay = ezPlayerCloud.startPlayback(ezDeviceRecordFile);
                    }


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
                    Log.i(TAG, "handleMessage: ###################还未播放############125");       //还未播放
                    break;
                case 126:
                    Log.i(TAG, "handleMessage: ##################还未播放#############126");       //还未播放
                    break;
                case 127:
                    Log.i(TAG, "handleMessage: ##############快开始播放了#################127");      //快开始播放了
                    break;
                case 102:
                    ivCameraLoading.setVisibility(View.GONE);
                    setCurrentProgress(); //设置进度条
                    Log.i(TAG, "handleMessage: ################视屏开始播放###############102");       //视屏开始播放
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setContentView(R.layout.view_null);
    }

    private void initView(){

        ezOpenSDK = EZOpenSDK.getInstance();
        sfvCamera = (SurfaceView) findViewById(R.id.sfv_camera);
        ivCapture = (ImageView) findViewById(R.id.iv_capture);
        ivShoushi = (ImageView) findViewById(R.id.iv_shoushi);
        llPlayStatus = (LinearLayout) findViewById(R.id.ll_play_status);
        ivPlay = (ImageView) findViewById(R.id.iv_play);
        sbJiankong = (SeekBar) findViewById(R.id.sb_jiankong);
        ivCameraLoading = (ImageView) findViewById(R.id.iv_camera_loading);
        //pickTimeAdapter = new PickTimeAdapter(EzCameraActivity.this,timesPickList);  //传入选择的那一天的录像片段
        lvPickTime.setAdapter(pickTimeAdapter);
        //选择查看当天的视频录像片段
        lvPickTime.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pickTimeAdapter.changeSelected(position);
            }
        });

        typeFace = Typeface.createFromAsset(getAssets(),"fonts/xiyuanti.ttf");
        tvCameraRoomName.setTypeface(typeFace);

        sbJiankong.setOnSeekBarChangeListener(seekBarListener);
        //获取当前日期
        getCurrentDate();
        cameraDataBaseAdapter = new CameraDataBaseAdapter(this,cameraDate);
        lvJiankong.setAdapter(cameraDataBaseAdapter);
        lvJiankong.setOnItemClickListener(new AdapterView.OnItemClickListener() {         //选择查看监控的日期
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cameraDatePosition = position;
                setCalendarDay(mStartTime.get(Calendar.DAY_OF_MONTH)-position);
                ezPlayer.stopRealPlay();         //暂停播放
                if (ezPlayerCloud!=null){
                    ezPlayerCloud.stopPlayback();
                }
                getCloudRecord(position);    //获取云视频记录

                lvPickTime.setVisibility(View.VISIBLE);
                pickTimeAdapter.changdata(new String[]{"a","a","a","a","a","a"});
                cameraDataBaseAdapter.changeSelected(position);

            }
        });

        SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
        Log.i("result", "onCreate: +"+pref.getString("token",""));
        ezOpenSDK.setAccessToken(pref.getString("token",""));
        initCameraPlayer(cameraIndex);  //初始化第一个摄像头

        //设置Calendar时间    13号
        //setCalendarDay(13);
        mStartTime = Calendar.getInstance();
        mEndTime = Calendar.getInstance();
    }

    //获取当前日期
    private void getCurrentDate(){
        Calendar calendar = Calendar.getInstance();
        month = calendar.get(Calendar.MONTH)+1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
    }

    private void setCalendarDay(int dayOfMonth){

        //设置Calendar时间
        mStartTime.set(Calendar.AM_PM, 0);
        mStartTime.set(mStartTime.get(Calendar.YEAR), mStartTime.get(Calendar.MONTH), dayOfMonth, 0, 0, 0); //设定开始时间

        mEndTime.set(Calendar.AM_PM, 0);
        mEndTime.set(mEndTime.get(Calendar.YEAR), mEndTime.get(Calendar.MONTH),dayOfMonth, 23, 59, 59);   //设定结束时间
    }

    //初始化视频播放
    private void initCameraPlayer(final int i){
        surfaceHolder = sfvCamera.getHolder();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    cameraList = ezOpenSDK.getCameraList(0,10);
                    //EZUserInfo uinfo = ezOpenSDK.getUserInfo();   //用户信息
                    cameraNumber = cameraList.size();   //摄像头个数
                    cameraNames = new String[cameraNumber];   //初始化摄像头名字数组

                    for (int i=0;i<cameraNumber;i++){
                        cameraNames[i] = cameraList.get(i).getCameraName();    //获取所有摄像头名
                    }
                    cameraName = cameraList.get(i).getCameraName();
                    cameraId = cameraList.get(i).getCameraId();
                    //String cameraDeviceId = cameraList.get(0).getDeviceId();  //摄像头设备id
                    //String cameraDeviceName = cameraList.get(0).getDeviceName();  //摄像头设备名字
                    String cameraDeviceSerial = cameraList.get(i).getDeviceSerial();   //摄像头设备序列号
                    EZCameraInfo cameraInfo = ezOpenSDK.getCameraInfo(cameraDeviceSerial);
                    int channelNo = cameraInfo.getChannelNo();
                    captureUrl = ezOpenSDK.capturePicture(cameraDeviceSerial,channelNo);    //截图图片url

                } catch (BaseException e) {
                    e.printStackTrace();
                }
                Message message = new Message();
                message.what = 1;
                message.arg1 = i;
                handler.sendMessage(message);
            }
        }).start();
    }

    private void initPopupWindow(){
        inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentview = inflater.inflate(R.layout.pop_room_layout, null);
        lvRoom = (ListView) contentview.findViewById(R.id.lv_room);
        lvRoom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                isCloudPlay = false;
                isDevicePlay = false;
                cameraIndex = position;
                popupWindow.dismiss();
                ezPlayer.stopRealPlay();         //停止播放实时预览
                if (ezPlayerCloud!=null){
                    ezPlayerCloud.stopPlayback();     //停止播放视频记录
                }
                isStartPlay = false;
                initCameraPlayer(cameraIndex);
            }
        });
        roomAdapter = new JianKongRoomAdapter(this,cameraNames);
        lvRoom.setAdapter(roomAdapter);
        popupWindow = new PopupWindow(contentview, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.setOutsideTouchable(true);

    }

    private void initTimesPickPopWindow(){

    }



    public void cameraClick(View v){
        switch (v.getId()){
            case R.id.iv_capture:
                playCamera(cameraIndex);
                loadingAnimation();
                break;
            case R.id.iv_shoushi:
                setfullscreen();          //设置全屏或退出全屏
                break;
            case R.id.iv_play:
                if (isStartPlay==true){
                    ezPlayer.stopRealPlay();         //暂停播放
                    isStartPlay = false;
                    ivCapture.setVisibility(View.VISIBLE);   //截图设为可见
                    ivPlay.setImageResource(R.mipmap.play_3x);
                }else if (isStartPlay==false){
                    playCamera(cameraIndex);
                    loadingAnimation();
                }
                break;
            case R.id.sfv_camera:
                setfullscreen();          //设置全屏或退出全屏
                break;
            /*case R.id.tv_camera_room_name:

                popupWindow.showAtLocation(getLayoutInflater().inflate(R.layout.camera_main_layout,null), Gravity.CENTER,0,0);
                break;*/
            default:
                break;
        }
    }

    //播放监控 i为第几个房间
    private void playCamera(int i){
        ivCapture.setVisibility(View.INVISIBLE);   //截图设为不可见
        ezPlayer = ezOpenSDK.createPlayer(EzCameraActivity.this,cameraList.get(i).getCameraId());    //视频播放
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
            lvJiankong.setVisibility(View.GONE);
            tvCameraRoomName.setVisibility(View.GONE);
            llPlayStatus.setVisibility(View.GONE);
            ivShoushi.setVisibility(View.VISIBLE);
            if (isCloudPlay==false){
                ezPlayer.stopRealPlay();         //暂停播放
                playCamera(cameraIndex);     //播放
                loadingAnimation();
            }
            /*if (isStartPlay==false){
                ezPlayerCloud.stopPlayback();
                getCloudRecord(cameraDatePosition);
            }*/

            isFullScreen = true;
        }else if (isFullScreen==true){
            //返回选择界面
            lvJiankong.setVisibility(View.VISIBLE);
            tvCameraRoomName.setVisibility(View.VISIBLE);
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



    //-----------------------------------获取云视频---------------------------------------
    private void getCloudRecord(final int i){
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    EzDeviceFileList = ezOpenSDK.searchRecordFileFromDevice(cameraId,mStartTime,mEndTime);
                    if (EzDeviceFileList!=null){
                        ezDeviceRecordFile = EzDeviceFileList.get(i);
                    }

                    EzCloudFileList = ezOpenSDK.searchRecordFileFromCloud(cameraId,mStartTime,mEndTime);
                    if (EzCloudFileList!=null&&EzCloudFileList.size()!=0){
                        ezCloudRecordFile = EzCloudFileList.get(i);
                    }

                    ezPlayerCloud = ezOpenSDK.createPlayer(EzCameraActivity.this,cameraId);    //视频播放
                    ezPlayerCloud.setHandler(cameraHandler);
                    ezPlayerCloud.setSurfaceHold(surfaceHolder);
                    if (EzCloudFileList!=null){
                        Log.i(TAG, "run: ******************************"+EzCloudFileList.size());
                    }
                    if (EzDeviceFileList!=null){
                        Log.i(TAG, "run: &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&"+EzDeviceFileList.size());
                    }
                    Message message = new Message();
                    message.what = 2;
                    handler.sendMessage(message);

                } catch (BaseException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /*//------------------------------------获取sd卡录像--------------------------------------
    private void getDeviceRecord(final int i){
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    EzDeviceFileList = ezOpenSDK.searchRecordFileFromDevice(cameraId,mStartTime,mEndTime);
                    ezDeviceRecordFile = EzDeviceFileList.get(i);
                    ezPlayerCloud = ezOpenSDK.createPlayer(EzCameraActivity.this,cameraId);    //视频播放
                    ezPlayerCloud.setHandler(cameraHandler);
                    ezPlayerCloud.setSurfaceHold(surfaceHolder);
                    Log.i(TAG, "run: ******************************"+EzDeviceFileList.size());
                    Message message = new Message();
                    message.what = 2;
                    handler.sendMessage(message);

                } catch (BaseException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }*/

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
            lvJiankong.setVisibility(View.VISIBLE);
            tvCameraRoomName.setVisibility(View.VISIBLE);
            llPlayStatus.setVisibility(View.VISIBLE);
            ivShoushi.setVisibility(View.GONE);
            isFullScreen = false;
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
