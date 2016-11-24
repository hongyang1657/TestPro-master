package com.byids.hy.testpro.activity;

import android.animation.ObjectAnimator;
import android.content.SharedPreferences;
import android.graphics.Typeface;
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
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.byids.hy.testpro.R;
import com.byids.hy.testpro.adapter.CameraDataBaseAdapter;
import com.byids.hy.testpro.adapter.JianKongRoomAdapter;
import com.byids.hy.testpro.adapter.PickTimeAdapter;
import com.videogo.exception.BaseException;
import com.videogo.openapi.EZOpenSDK;
import com.videogo.openapi.EZPlayer;
import com.videogo.openapi.bean.EZCameraInfo;
import com.videogo.openapi.bean.EZCloudRecordFile;
import com.videogo.openapi.bean.EZDeviceRecordFile;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.byids.hy.testpro.R.id.iv_camera_back;

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
    private Typeface typeFace;

    private LinearLayout llSwitchButton;
    private ImageView ivCameraBack;
    private TextView tvSwitchVcr;
    private TextView tvSwitchRoom;
    private RelativeLayout llCameraList;
    private ListView lvCamera;


    //private ListView lvJiankong;
    private ListView lvVideoPart;     //录像片段listview

    private CameraDataBaseAdapter cameraDataBaseAdapter;
    //private TextView tvCameraRoomName;
    private SurfaceView sfvCamera;
    //private ImageView ivCapture;
    //private ImageView ivShoushi;
    private LinearLayout llPlayStatus;
    private ImageView ivKuaitui;
    private ImageView ivPlay;
    private ImageView ivKuaijin;
    private SeekBar sbJiankong;
    private ImageView ivCameraLoading;
    private TextView tvCurrentlyTime;     //当前时间进度
    private TextView tvTotalTime;       //视频总时间
    private List<EZCloudRecordFile> EzCloudFileList;       //云视频文件列表
    private List<EZDeviceRecordFile> EzDeviceFileList;     //sd卡视频文件列表
    private EZCloudRecordFile ezCloudRecordFile;
    private EZDeviceRecordFile ezDeviceRecordFile;
    private Calendar mStartTime = null;
    private Calendar mEndTime = null;

    //private ListView lvPickTime;      //选择监控片段的listview
    private PickTimeAdapter pickTimeAdapter = null;
    private PickTimeAdapter pickRoomsAdapter = null;
    private String[] timesPickList;
    private String[] roomsName;

    private boolean isLvVideoPartShow = false;
    private boolean isLvVideoRoomsShow = false;
    private boolean isFullScreen = false;
    private boolean isStartPlay;    //是否开始实时预览
    private boolean isHandlerMessage;
    private boolean isSetSurfaceHold;
    private boolean isPlayPreview = true;      //true：实时预览；false：录像
    private boolean isCloudPlay = false;
    private boolean isDevicePlay = false;
    private boolean isPlayingVideo;
    private PopupWindow popupWindow;
    private ListView lvRoom;
    private JianKongRoomAdapter roomAdapter;
    private int cameraNumber;
    private int cameraIndex = 1;   //当前播放的摄像头
    private int cameraDatePosition;    //选择的录像日期 posirion
    private LayoutInflater inflater = null;
    private int month;
    private int day;
    private String[] dateList;       //录像时间信息数组

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:        //初始化摄像头
                    int cameraIndexs = msg.arg1;
                    roomsName = (String[]) msg.obj;
                    tvSwitchRoom.setText(roomsName[cameraIndexs]);      //默认选择第一个房间
                    playCamera(cameraIndex);
                    loadingAnimation();

                    //选择查看当天的视频录像片段
                    Log.i("hongyang", "handleMessage:-------kaishishijian "+mStartTime.getTime().toString()+"jieshushijian"+mEndTime.getTime().toString()+"ezopensdk"+ezOpenSDK);
                    try {
                        EzDeviceFileList = ezOpenSDK.searchRecordFileFromDevice(cameraId,mStartTime,mEndTime);
                        if (EzDeviceFileList!=null){
                            //此摄像头有本地录像
                            Log.i("hongyang", "handleMessage: ---------------------------此摄像头的录像个数:"+EzDeviceFileList.size()+"-------====id:===="+cameraId);
                            Log.i("hongyang", "handleMessage:@@@@@ 第一个录像的开始时间："+EzDeviceFileList.get(0).getStartTime().getTime().toString()+"@@@@结束时间："+EzDeviceFileList.get(0).getStopTime().getTime().toString());
                            dateList = new String[EzDeviceFileList.size()];
                            dateList = getDeviceFileInfo(EzDeviceFileList);       //获取各个录像片段的信息（开始时间，结束时间，录像片段个数等）
                        }else if (EzDeviceFileList==null){
                            //此摄像头没有本地录像
                            Log.i("hongyang", "handleMessage:------此摄像头没有录像信息，可能没有内存卡------- ");
                            dateList = new String[]{"无历史录像"};
                        }
                    } catch (BaseException e) {
                        e.printStackTrace();
                        Log.e("hongyang", "handleMessage:---------------获取EzDeviceFileList的错误码 ："+e.getErrorCode()+"----错误信息"+e.toString());
                        cameraIndex++;
                        initCameraPlayer(cameraIndex);
                    }

                    if (pickTimeAdapter==null){       //第一次点击时初始化listview
                        Log.i("hongyang", "handleMessage: __________________________________初始化listview______________________________________");
                        //1-------------------初始化选择视频片段的listview----------------------
                        pickTimeAdapter = new PickTimeAdapter(0,EzCameraActivity.this,dateList,roomsName);  //传入选择的那一天的录像片段
                        lvVideoPart.setAdapter(pickTimeAdapter);

                        lvVideoPart.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                pickTimeAdapter.changeSelected(position);
                                loadingAnimation();  //旋转等待
                                cameraDatePosition = position;
                                ezPlayer.stopRealPlay();         //暂停播放
                                if (ezPlayerCloud!=null){
                                    ezPlayerCloud.stopPlayback();
                                }
                                getDeviceRecord(position);     //获取本地sd卡录像


                                //getCloudRecord(position);    //获取云视频记录
                                //lvPickTime.setVisibility(View.VISIBLE);
                                //pickTimeAdapter.changdata(new String[]{"a","a","a","a","a","a"});
                                //cameraDataBaseAdapter.changeSelected(position);
                            }
                        });
                    }else {
                        pickTimeAdapter.changdata(dateList);
                    }


                    if (pickRoomsAdapter==null){
                        //2----------------------初始化选择切换摄像头的listview------------------------
                        pickRoomsAdapter = new PickTimeAdapter(1,EzCameraActivity.this,dateList,roomsName);   //传入摄像头列表
                        lvCamera.setAdapter(pickRoomsAdapter);
                        lvCamera.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                loadingAnimation();  //旋转等待
                                pickRoomsAdapter.changeSelected(position);
                                tvSwitchRoom.setText(roomsName[position]);  //设置显示的播放监控的房间名
                                pickTimeAdapter.changdata(dateList);        //改变选择录像片段listView的item

                                //测试，切换房间
                                isCloudPlay = false;
                                isDevicePlay = false;
                                cameraIndex = position;
                                ezPlayer.stopRealPlay();         //停止播放实时预览
                                if (ezPlayerCloud!=null){
                                    ezPlayerCloud.stopPlayback();     //停止播放视频记录
                                }
                                isStartPlay = false;
                                initCameraPlayer(cameraIndex);
                            }
                        });
                    }

                    break;
                case 2:
                    /*Log.i(TAG, "getCloudRecord: +++++++++++++++++++++++++"+EzCloudFileList.size());
                    Log.i(TAG, "run: ******************************"+EzCloudFileList.size());*/

                    /*if (EzCloudFileList!=null){
                        isCloudPlay = ezPlayerCloud.startPlayback(ezDeviceRecordFile);
                    }*/
                    Log.i("hongyang", "handleMessage: -----11111111111111111111111111111111开始播放录像！！！！！！！！！！！！！！！！！-------");
                    if (EzDeviceFileList!=null){
                        Log.i("hongyang", "handleMessage: -----开始播放录像！！！！！！！！！！！！！！！！！-------");
                        isDevicePlay = ezPlayerCloud.startPlayback(ezDeviceRecordFile);
                    }
                    break;
                case 3:
                    Log.i("hongyang", "handleMessage: ----------"+msg.arg1);

                    sbJiankong.setProgress(msg.arg1);
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
            Log.i("hongyang", "handleMessage: ^^^^^^^^^^Message:"+msg.toString());
            switch (msg.what){
                case 125:
                    Log.i("hongyang", "handleMessage: ###################还未播放############125");       //还未播放
                    break;
                case 102:
                    Log.i("hongyang", "handleMessage: ################实时视屏开始播放###############102");       //视屏开始播放
                    isPlayPreview = true;       //开始播放实时预览
                    ivCameraLoading.setVisibility(View.GONE);
                    llPlayStatus.setVisibility(View.GONE);    //进度条消失
                    break;
                case 207:
                    Log.i("hongyang", "handleMessage: ################历史录像视屏开始播放###############207");
                    isPlayPreview = false;       //开始播放录像
                    ivCameraLoading.setVisibility(View.GONE);
                    //显示进度条
                    llPlayStatus.setVisibility(View.VISIBLE);
                    setCurrentProgress(cameraDatePosition);      //设置进度条
                    break;
                /*case 201:
                    Log.i("hongyang", "handleMessage: ################这个历史录像视屏播放完毕###############201");
                    isPlayingVideo = false;
                    Toast.makeText(EzCameraActivity.this, "这个录像视频播放完毕", Toast.LENGTH_SHORT).show();
                    break;*/
                case 208:
                    Log.i("hongyang", "handleMessage: ################这个历史录像视屏播放完毕###############208");
                    isPlayingVideo = false;
                    Toast.makeText(EzCameraActivity.this, "这个录像视频播放完毕208", Toast.LENGTH_SHORT).show();
                    break;
                default:
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
        llSwitchButton = (LinearLayout) findViewById(R.id.ll_switch_button);
        llCameraList = (RelativeLayout) findViewById(R.id.ll_camera_list);
        ivCameraBack = (ImageView) findViewById(iv_camera_back);
        sfvCamera = (SurfaceView) findViewById(R.id.sfv_camera);
        //ivCapture = (ImageView) findViewById(R.id.iv_capture);
        //ivShoushi = (ImageView) findViewById(R.id.iv_shoushi);
        llPlayStatus = (LinearLayout) findViewById(R.id.ll_play_status);
        ivPlay = (ImageView) findViewById(R.id.iv_play);
        sbJiankong = (SeekBar) findViewById(R.id.sb_jiankong);
        ivCameraLoading = (ImageView) findViewById(R.id.iv_camera_loading);
        tvSwitchVcr = (TextView) findViewById(R.id.tv_switch_vcr);
        tvSwitchRoom = (TextView) findViewById(R.id.tv_switch_room);
        lvVideoPart = (ListView) findViewById(R.id.lv_camera_list);
        lvCamera = (ListView) findViewById(R.id.lv_camera_list_rooms);
        tvCurrentlyTime = (TextView) findViewById(R.id.tv_time_progress);
        tvTotalTime = (TextView) findViewById(R.id.tv_time_length);

        //设置字体
        typeFace = Typeface.createFromAsset(getAssets(),"fonts/xiyuanti.ttf");
        tvSwitchVcr.setTypeface(typeFace);
        tvSwitchRoom.setTypeface(typeFace);

        sbJiankong.setOnSeekBarChangeListener(seekBarListener);  //设置进度条
        //获取当前日期
        getCurrentDate();
        //cameraDataBaseAdapter = new CameraDataBaseAdapter(this,cameraDate);

        SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
        Log.i("result", "onCreate: +"+pref.getString("token",""));
        ezOpenSDK.setAccessToken(pref.getString("token",""));


        //设置Calendar时间    13号
        //setCalendarDay(21);
        mStartTime = Calendar.getInstance();
        mEndTime = Calendar.getInstance();
        setCalendarDay(mStartTime.get(Calendar.DAY_OF_MONTH));
        Log.i("hongyang", "initView: !!!!!!!!!!!!!!!!!"+mStartTime.getTime().toString());
        initCameraPlayer(cameraIndex);  //初始化第一个摄像头
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
        mStartTime.set(mStartTime.get(Calendar.YEAR), mStartTime.get(Calendar.MONTH), dayOfMonth-5, 0, 0, 0);  //设定开始时间,5天前（）

        mEndTime.set(Calendar.AM_PM, 0);
        mEndTime.set(mEndTime.get(Calendar.YEAR), mEndTime.get(Calendar.MONTH),dayOfMonth, 23, 59, 59);   //设定结束时间
        Log.i("hongyang", "setCalendarDay: 设定的时间："+mStartTime.getTime().toString()+"----------"+mEndTime.getTime().toString());
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
                    Log.i("hongyang", "run: --------摄像头数--------："+cameraNumber+"------");
                    cameraNames = new String[cameraNumber];   //初始化摄像头名字数组

                    for (int i=0;i<cameraNumber;i++){
                        cameraNames[i] = cameraList.get(i).getCameraName();    //获取所有摄像头名
                        Log.i("hongyang", "run: --------摄像头名--------："+cameraNames[i]+"------");
                    }
                    cameraName = cameraList.get(i).getCameraName();
                    cameraId = cameraList.get(i).getCameraId();
                    Log.i("hongyang", "run: --------这个摄像头id--------："+cameraId+"------");
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
                message.obj = cameraNames;
                handler.sendMessage(message);
            }
        }).start();
    }

    /*private void initPopupWindow(){
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

    }*/

    private void initTimesPickPopWindow(){

    }



    public void cameraClick(View v){
        switch (v.getId()){

            case R.id.iv_play:
                /*if (isStartPlay==true){
                    ezPlayer.stopRealPlay();         //暂停播放
                    isStartPlay = false;
                    //ivCapture.setVisibility(View.VISIBLE);   //截图设为可见
                    ivPlay.setImageResource(R.mipmap.play_3x);
                }else if (isStartPlay==false){
                    playCamera(cameraIndex);
                    loadingAnimation();
                }*/

                break;
            case R.id.sfv_camera:
                setfullscreen();          //设置全屏或退出全屏
                break;
            case R.id.tv_switch_vcr:      //选择录像片段


                if (isLvVideoPartShow){
                    tvSwitchVcr.setBackgroundResource(R.drawable.camera_switch_button);
                    lvVideoPart.setVisibility(View.GONE);
                    isLvVideoPartShow = false;
                }else if (!isLvVideoPartShow){
                    tvSwitchVcr.setBackgroundResource(R.drawable.camera_switch_button_selected);
                    tvSwitchRoom.setBackgroundResource(R.drawable.camera_switch_button_right);
                    lvVideoPart.setVisibility(View.VISIBLE);
                    lvCamera.setVisibility(View.GONE);
                    isLvVideoPartShow = true;
                    isLvVideoRoomsShow = false;
                }
                break;
            case R.id.tv_switch_room:     //选择房间

                if (isLvVideoRoomsShow){
                    tvSwitchRoom.setBackgroundResource(R.drawable.camera_switch_button_right);
                    lvCamera.setVisibility(View.GONE);
                    isLvVideoRoomsShow = false;
                }else if (!isLvVideoRoomsShow){
                    tvSwitchRoom.setBackgroundResource(R.drawable.camera_switch_button_right_selected);
                    tvSwitchVcr.setBackgroundResource(R.drawable.camera_switch_button);
                    lvCamera.setVisibility(View.VISIBLE);
                    lvVideoPart.setVisibility(View.GONE);
                    isLvVideoRoomsShow = true;
                    isLvVideoPartShow = false;
                }
                break;
            case R.id.iv_camera_back:
                ezPlayer.stopRealPlay();         //暂停播放
                ezOpenSDK.releasePlayer(ezPlayer);        //关闭视屏播放（释放内存）
                finish();
                break;
            default:
                break;
        }
    }

    //播放实时监控 i为第几个房间
    private void playCamera(int i){
        //ivCapture.setVisibility(View.INVISIBLE);   //截图设为不可见
        ezPlayer = ezOpenSDK.createPlayer(EzCameraActivity.this,cameraList.get(i).getCameraId());    //视频播放
        isHandlerMessage = ezPlayer.setHandler(cameraHandler);
        isSetSurfaceHold = ezPlayer.setSurfaceHold(surfaceHolder);
        Log.i("hongyang", "run: -----------isHandlerMessage-----------"+isHandlerMessage+"---------isSetSurfaceHold--------"+isSetSurfaceHold+ "-------isStartPlay------");
        isStartPlay = ezPlayer.startRealPlay();    //开始播放
        Log.i("hongyang", "playCamera: @@@@@@@@@@@@@@isStartPlay@@@@@@@@@@@@@"+isStartPlay);
        ivPlay.setImageResource(R.mipmap.camera_stop_3x);
    }
    //全屏，退出全屏
    private void setfullscreen(){
        if (!isFullScreen){
            //设为全屏
            if (!isPlayPreview){       //如果在播放录像的情况下，设置状态栏的消失
                llPlayStatus.setVisibility(View.GONE);
            }
            llSwitchButton.setVisibility(View.GONE);
            ivCameraBack.setVisibility(View.GONE);
            llCameraList.setVisibility(View.GONE);
            isFullScreen = true;
        }else if (isFullScreen){
            //返回选择界面
            if (!isPlayPreview){       //如果在播放录像的情况下，设置状态栏的出现
                llPlayStatus.setVisibility(View.VISIBLE);
            }
            llSwitchButton.setVisibility(View.VISIBLE);
            ivCameraBack.setVisibility(View.VISIBLE);
            llCameraList.setVisibility(View.VISIBLE);
            isFullScreen = false;
        }
    }

    //设置当前进度条进度
    private void setCurrentProgress(int position){
        //-------------获取录像视频的时长-------------设置时间显示-----------
        final int totalDuration = videoDuration[position]/1000;      //总时长（单位：秒）
        int hour = totalDuration/3600;          //每段视频的时长 小时
        int min = totalDuration/60%60;          //分钟
        int second = totalDuration%60;          //秒
        String h;
        String m;
        String s;
        Log.i("hongyang", "setCurrentProgress: -----------小时："+hour+"分钟："+min+"秒："+second);
        if (hour<10){
            h = "0"+hour;
        }else {
            h = String.valueOf(hour);
        }
        if (min<10){
            m = "0"+min;
        }else {
            m = String.valueOf(min);
        }
        if (second<10){
            s = "0"+second;
        }else {
            s = String.valueOf(second);
        }
        tvTotalTime.setText(h+":"+m+":"+s);

        /*isPlayingVideo = true;
        new Thread(){
            @Override
            public void run() {
                super.run();
                int sbProgress = 0;
                sbJiankong.setMax(totalDuration);
                while (isPlayingVideo){
                    try {
                        sleep(500);
                        Message message = new Message();
                        message.what = 3;
                        message.arg1 = sbProgress;
                        handler.sendMessage(message);
                        sbProgress++;           //发送进度，每隔半秒，进度加一
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();*/

        //getosdTime
        /*new Thread(){
            @Override
            public void run() {
                super.run();
                sbJiankong.setMax(totalDuration);
                while (isPlayingVideo){
                    try {
                        sleep(1000);
                        Calendar calendar = ezPlayerCloud.getOSDTime();

                        int hour = calendar.get(Calendar.HOUR_OF_DAY);
                        int min = calendar.get(Calendar.MINUTE);
                        int second = calendar.get(Calendar.SECOND);
                        Log.i("hongyang", "playCamera: 时间"+hour+"时"+min+"分"+second+"秒");
                        Message message = new Message();
                        message.what = 3;
                        //message.arg1 = sbProgress;
                        handler.sendMessage(message);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();*/



        //sbJiankong.setProgress(0);
        /*Calendar calendar = ezPlayerCloud.getOSDTime();  //获取当前时间
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int minOfDay = hour*60+min;
        Log.i("hongyang", "playCamera: 时间"+hour+"时"+min+"分"+second+"秒");*/
        //sbJiankong.setProgress(minOfDay);

    }

    //-----------------获取各个录像片段的信息（开始时间，结束时间，录像片段个数等）------------------
    private int[] videoDuration;
    private String[] getDeviceFileInfo(List<EZDeviceRecordFile> EzDeviceFileList){
        dateList = new String[EzDeviceFileList.size()];
        videoDuration = new int[EzDeviceFileList.size()];
        for (int i=0;i<EzDeviceFileList.size();i++){
            Date date = EzDeviceFileList.get(i).getStartTime().getTime();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");
            dateList[i] = simpleDateFormat.format(date);
            Log.i("hongyang", "getDeviceFileInfo: %%%%%%%%%%%%%%"+dateList[i]);
            //获取每个录像片段的时长
            int a = (int) EzDeviceFileList.get(i).getStopTime().getTime().getTime() - (int) EzDeviceFileList.get(i).getStartTime().getTime().getTime();
            Log.i("hongyang", ": ------获取每个录像片段的时长------"+a);
            videoDuration[i] = a;
        }
        Log.i("hongyang", "getDeviceFileInfo: %%%%%%%%%%%%%%"+dateList[0]);
        return dateList;
    }


    //------------------------------------获取sd卡录像--------------------------------------
    private void getDeviceRecord(final int i){
        new Thread(){
            @Override
            public void run() {
                super.run();
                //try {
                    Log.i("hongyang", "run: ---hahahahahahcameraId:"+cameraId+"----mStartTime:"+mStartTime.toString()+"----mEndTime:"+mEndTime.toString());

                    //EzDeviceFileList = ezOpenSDK.searchRecordFileFromDevice(cameraId,mStartTime,mEndTime);
                    Log.i("hongyang", "run: hahahahahah"+EzDeviceFileList.size());
                    ezDeviceRecordFile = EzDeviceFileList.get(i);
                    ezPlayerCloud = ezOpenSDK.createPlayer(EzCameraActivity.this,cameraId);    //视频播放
                    ezPlayerCloud.setHandler(cameraHandler);
                    ezPlayerCloud.setSurfaceHold(surfaceHolder);
                    Log.i(TAG, "run: ******************************"+EzDeviceFileList.size());
                    Message message = new Message();
                    message.what = 2;
                    handler.sendMessage(message);

                /*} catch (BaseException e) {
                    e.printStackTrace();
                }*/
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
            Log.i("hongyang", "onProgressChanged: ------------松手-------------"+seekBar.getProgress());
            //ezPlayerCloud.seekPlayback()
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
        if (keyCode==KeyEvent.KEYCODE_BACK&&!isFullScreen&&ezPlayer!=null){
            ezPlayer.stopRealPlay();         //暂停播放
            ezOpenSDK.releasePlayer(ezPlayer);        //关闭视屏播放（释放内存）
            ezPlayerCloud.stopPlayback();
            ezOpenSDK.releasePlayer(ezPlayerCloud);


        }else if (isFullScreen){    //如果在全屏页面返回选择页面
            //返回选择界面
            //lvJiankong.setVisibility(View.VISIBLE);
            //tvCameraRoomName.setVisibility(View.VISIBLE);
            if (!isPlayPreview){       //如果在播放录像的情况下，设置状态栏的出现
                llPlayStatus.setVisibility(View.VISIBLE);
            }
            llSwitchButton.setVisibility(View.VISIBLE);
            ivCameraBack.setVisibility(View.VISIBLE);
            llCameraList.setVisibility(View.VISIBLE);

            //ivShoushi.setVisibility(View.GONE);
            isFullScreen = false;
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
