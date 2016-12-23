package com.byids.hy.testpro.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.byids.hy.testpro.Bean.RoomAttr;
import com.byids.hy.testpro.MyEventBus;
import com.byids.hy.testpro.MyEventBus2;
import com.byids.hy.testpro.PullDownMenuListener;
import com.byids.hy.testpro.PullUpMenuListener;
import com.byids.hy.testpro.R;
import com.byids.hy.testpro.ScrollViewListener;
import com.byids.hy.testpro.View.LightValueScrollView;
import com.byids.hy.testpro.View.MyCustomScrollView;
import com.byids.hy.testpro.View.MyPullUpScrollView;
import com.byids.hy.testpro.activity.MyMainActivity;
import com.byids.hy.testpro.activity.custom_scene_activity.CustomSceneMainActivity;
import com.byids.hy.testpro.utils.CommandJsonUtils;
import com.videogo.openapi.EZOpenSDK;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 382,770     android23以上能使用
 * Created by hy on 2016/8/15.
 */
@SuppressLint({"ValidFragment", "NewApi"})
public class MyFragment extends Fragment implements PullUpMenuListener,GestureDetector.OnGestureListener {
    public static final int LINEAR = R.id.linear;
    private String TAG = "result";
    private View view = null;
    private static final String SWITCH_ROOM_DIALOG = "1";
    private static final String SCROLL_FRAGMENT_START = "2";      //开始滑动viewpager
    private static final String SCROLL_FRAGMENT_END = "3";      //结束滑动viewpager
    
    private LinearLayout linearMenu;  //下拉菜单
    private LinearLayout linearClick;        //隐藏的，代替下拉菜单点击的四个按钮
    private TextView tvSet;
    private TextView tvMonitoring; //监控
    private TextView tvLock;
    private TextView tvSecurity;

    private int width;  //屏幕宽
    private int height;    //屏幕高
    private int btHeight;     //头部菜单的高度
    private int btMenuHeight;      //顶部控件的高度
    private int btHeight_X3;  //头部菜单高度的三倍  (因为两个ScrollView的联动为3倍率)
    private int AirConditionHeight;        //空调控制部分的布局高度
    private boolean isHeadShown = false;     //头菜单是否显示
    private boolean isInitPosition = true;      //界面是否在初始位置
    private int pagerScrollState;     //viewpager滑动状态

    private LinearLayout llScene;       //场景
    private LinearLayout linearPanel;    //场景控制部分
    private TextView tvLight;           //灯光
    private TextView tvCustomScene;          //自定义场景

    //电视
    private LinearLayout llTelevision;
    private TextView tvTelevision;

    //控制模块，灯光，窗帘
    private LinearLayout llLight;
    private TextView tvChuanglian;
    private LinearLayout llChuanglian;
    /*private TextView tvJuanLian;
    private LinearLayout llJuanlian;*/
    private TextView tvAirCondition;
    private LinearLayout llAirCondition;
    //上拉菜单初始化浮现的部分（最上面上三个部分）的高度
    private int tvSceneHeight;
    private int linearSceneHeight;
    private int tvLightHeight;
    private int initFloatHeight;      //打开app时浮现部分的高度（三个部分高度之和）

    private int lightValueHeight1;      //灯光刻度条黄条的长度
    private int lightValueHeight2;      //灯光刻度条四个灰条的长度
    private int horizontalHeight;      //horizontalScrollView的长度
    private int rlLightValueHeight;    //relativeLayout的长度
    private int lightAxisExWidth;       //数轴左右多出部分的长度
    private int lightAxisinitValue;      //数轴初始化滑到0刻度时需要滚动的数值
    private int linearPullUpScrollHeight;        //上拉菜单的长度

    private ImageView ivBackGround;   //背景图片
    private ImageView ivBackGroundTrans;      //切换背景图片
    private ImageView ivStateLight;     //状态指示灯

    private MyCustomScrollView scrollView;
    private int roomIndex;     //房间标示
    private String roomName;   //房间名
    private String roomDBName;    //房间拼音名
    private RoomAttr roomAttr;   //房间拥有的各个产品信息
    //private RoomDevMesg roomAttr;
    private String token;
    private String uname;
    private String pwd;

    private GestureDetector detector;
    //private MyMainActivity.MyOntouchListener listener;
    private MyMainActivity.MyOnTouchListener myOnTouchListener;
    private Activity activity;
    private RelativeLayout btPullMenu;
    private RelativeLayout linear;
    private ImageView ivHomeIcon;
    private TextView tvRoomNameTop;
    private RelativeLayout rlRoomName;
    private ImageView ivXiaoJianJian;

    //上拉菜单
    MyPullUpScrollView svPullUpMenu;
    PullDownMenuListener pullDownMenuListener;//自定义的下拉监听
    ViewGroup.LayoutParams params;
    LinearLayout linearPullUpScroll;  //上拉菜单整个布局

    private int scrollY; //下滑的距离
    private static final int size = 4;
    private float y;
    private int position;    //上拉菜单滑动位置
    private int mScrollY;    //滑动停止的位置
    private int lightSVPosition;    //灯光调光模块动态值
    private boolean isCanScroll = true;

    //---------------------控制部分 上---------------------
    private TextView tvRoomName;     //房间名部分
    private ImageView ivSwitchRoomIcon;
    //private TextView tvLocalTemp;       //当地的气温
    private Button btShezhi;
    private Button btJiankong;
    private Button btMensuo;
    private Button btBufang;

    //面板
    private TextView tvPanelTitle;
    private RelativeLayout rlPanel1;
    private ImageView ivPanel1;
    private TextView tvPanel1;

    private RelativeLayout rlPanel2;
    private ImageView ivPanel2;
    private TextView tvPanel2;

    private RelativeLayout rlPanel3;
    private ImageView ivPanel3;
    private TextView tvPanel3;

    private RelativeLayout rlPanel4;
    private ImageView ivPanel4;
    private TextView tvPanel4;


    //场景
    private TextView tvScene;
    private RelativeLayout rlXiuxian;
    private ImageView ivXiuxianOut;      //内部图片和文字
    private ImageView ivXiuxianIn;
    private TextView tvXiuxian;

    private RelativeLayout rlYule;
    private ImageView ivYule1;
    private ImageView ivYule2;
    private ImageView ivYule3;
    private TextView tvYule;

    private RelativeLayout rlJuhui;
    private ImageView ivJuhui1;
    private ImageView ivJuhui2;
    private TextView tvJuhui;

    private RelativeLayout rlLikai;
    private ImageView ivLikai;
    private TextView tvLikai;



    //灯光
    private LightValueScrollView hsLightValue;
    private RelativeLayout rlLightValue;
    private ImageView ivLightValue1;      //黄色指针
    private ImageView ivLightValue2;      //灰色指针
    private RelativeLayout rlLight;
    private ImageView ivLightSwitch;
    private TextView tvLightSwitch;
    private TextView tvLightValue;    //灯光亮度值

    //窗帘（布帘，纱帘），卷帘（左，右卷帘）
    private RelativeLayout rlBulian,rlJuanlian;
    private ImageView ivBulianHead,ivJuanlianHead;
    private ImageView ivBulian1,ivBulian2,ivBulian3,ivBulian4,ivBulian5,ivBulian6,
            ivJuanlian1,ivJuanlian2,ivJuanlian3,ivJuanlian4,ivJuanlian5,ivJuanlian6;
    private ImageView ivBulianHead1,ivJuanlianHead1;
    private TextView tvBulian,tvJuanlian;

    private RelativeLayout rlShalian,rlJuanlianR;
    private ImageView ivShalianHead,ivJuanlianHeadR;
    private ImageView ivShalian1,ivShalian2,ivShalian3,ivShalian4,ivShalian5,ivShalian6,
            ivJuanlian1R,ivJuanlian2R,ivJuanlian3R,ivJuanlian4R,ivJuanlian5R,ivJuanlian6R;
    private ImageView ivShalianHead1,ivJuanlianHead1R;
    private TextView tvShalian,tvJuanlianR;
    //窗帘,卷帘，全关 全开
    private RelativeLayout rlAll,rlStop,rlAllJuan,rlStopJuan;
    private ImageView ivAll,ivStop,ivAllJuan,ivStopJuan;
    private TextView tvAll,tvStop,tvAllJuan,tvStopJuan;

    //电视
    private ImageView ivTelevisionOn;
    private ImageView ivTelevisionOff;

    //空调
    private RelativeLayout rlKongtiao;
    private ImageView ivKongtiaoSwitch;
    private TextView tvKongtiaoSwitch;

    private TextView tvKongtiaoTemp;
    private RelativeLayout rlKongtiaoMoshi;
    private ImageView ivKongtiaoMoshi;
    private TextView tvKongtiaoMoshi;

    private SeekBar sbTemp;
    private RelativeLayout rlAirControl1;
    private RelativeLayout rlAirControl2;
    private RelativeLayout rlAirControl3;
    private TextView tvDetailTemp;
    private TextView tvAirControl1;
    private TextView tvAirControl2;
    private TextView tvAirControl3;

    private List<TextView> tvList = new ArrayList<TextView>();  //textview控件组，用于改变字体
    private Typeface typeFace;

    //控制部分 下
    private LinearLayout linearAirDetails;

    private Thread BGPThread;      //切换背景图片的线程
    private boolean isFragmemtFront = true;         //fragment是否可见，如果不可见了，可关闭切换背景图片的线程

    Random random1 = new Random();
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    //setBGPAnimation();
                    //svPullUpMenu.setLayoutParams(params);            //更新scrollView？
                    //Log.i(TAG, "handleMessage:切换背景图 ");
                    break;
                default:
                    break;
            }
        }
    };
    private Handler handlerAir = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //isCanScroll = true;   //动画结束后设置为可操作
            switch (msg.what){
                case 1:
                    linearAirDetails.setVisibility(View.GONE);
                    tvKongtiaoTemp.setText("  18°");
                    sbTemp.setProgress(0);
                    tvKongtiaoTemp.setTextColor(activity.getResources().getColor(R.color.colorText));
                    break;
                default:
                    break;
            }

        }
    };

    private static final String CONTROL_PROTOCOL_HDL = "hdl";
    private static final String MACHINE_NAME_LIGHT = "light";
    private static final String MACHINE_NAME_CURTAIN = "curtain";
    private static final String LIGHT_VALUE_100 = "100";
    private static final String LIGHT_VALUE_0 = "0";
    private static final String IS_SERVER_AUTO = "0";
    private static final String CONTROL_SENCE_ALL = "all";


    public MyFragment(){}

    private int[] backList;  //背景图片组
    public MyFragment(int roomIndex, String roomName, String roomDBName, int[] backList, String token, String uname, String pwd) {
        this.roomIndex = roomIndex;
        this.roomName = roomName;
        this.roomDBName = roomDBName;
        this.backList = backList;
        this.roomAttr = roomAttr;
        this.token = token;
        this.uname = uname;
        this.pwd = pwd;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_layout,null);

        EventBus.getDefault().register(this);
        initView();

        //initBackPicture();
        btPullMenu.setOnClickListener(pullMenuListener);
        //上拉菜单   子scrollview
        svPullUpMenu = (MyPullUpScrollView) view.findViewById(R.id.scroll_pull_up);
        linearPullUpScroll = (LinearLayout) view.findViewById(R.id.linear_pull_up_scroll);
        svPullUpMenu.setOnConnectionListener(this);
        svPullUpMenu.setScrollViewListener(pullDownListener);

        svPullUpMenu.setOnTouchListener(new View.OnTouchListener() {
            //判断ScrollView是否停下
            private int lastY = 0;
            private int touchEventId = -9983761;
            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    View scroller = (View) msg.obj;
                    if (msg.what == touchEventId) {
                        if (lastY == scroller.getScrollY()) {
                            handleStop(scroller);
                        } else {
                            handler.sendMessageDelayed(handler.obtainMessage(touchEventId, scroller), 15);
                            lastY = scroller.getScrollY();
                        }
                    }
                }
            };

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    handler.sendMessageDelayed(handler.obtainMessage(touchEventId, view), 5);
                }
                svPullUpMenu.getParent().requestDisallowInterceptTouchEvent(true);
                if (isCanScroll==true){
                    return false;
                }else
                return true;
            }

            //scrollView停止滚动
            private void handleStop(Object view) {
                ScrollView scroller = (ScrollView) view;
                mScrollY = scroller.getScrollY();
                //Log.i("scroll_hy", "handleStop: ----------mScrollY----------"+mScrollY);
                //Log.i("scroll_hy", "handleStop: ----------isInitPosition----------"+isInitPosition);

                if (isInitPosition==true){
                    if (mScrollY>(btHeight_X3/2)&&mScrollY<=btHeight_X3){
                        scrollToBottom();        //滑动到隐藏头
                        isHeadShown = false;
                        pullDown(true,true);

                    }else if(mScrollY<=(btHeight_X3/2)){
                        scrollToTop();           //滑动到显示头
                        isHeadShown = true;
                        pullDown(false,false);
                    }
                }else if (isInitPosition==false){        //弹簧效果
                    if(mScrollY<btHeight_X3){
                        scrollToBottom();        //滑动到隐藏头
                        isHeadShown = false;
                        pullDown(true,true);
                    }
                }
                isCanScroll = true;  //滑动动画停止后才能用手操作
            }
        });

        //android 23 以上能用
        /*svPullUpMenu.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                Log.i("kankan", "onScrollChange: i:"+i+"i1:"+i1+"i2:"+i2+"i3:"+i3);
                if (i1==btHeight_X3){
                    isInitPosition = true;
                }else if (i1>btHeight_X3){
                    isInitPosition = false;
                }
                position = i1;
            }
        });*/

        //兼容android 23以下
        svPullUpMenu.setScrollViewListenner(new MyPullUpScrollView.ScrollViewListenner() {
            @Override
            public void onScrollChanged(MyPullUpScrollView view, int l, int t, int oldl, int oldt) {
            //Log.i("scroll_hy", "onScrollChange: t:"+t);
                //Log.i("scroll_hy", "onScrollChange: btHeight_X3:::"+btHeight_X3);
                if (t==btHeight_X3){
                    isInitPosition = true;
                }else if (t>btHeight_X3){
                    isInitPosition = false;
                }
                position = t;
            }
        });



        //获取控件高度
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        linearMenu.measure(w, h);
        btHeight = linearMenu.getMeasuredHeight();         //头菜单的高度
        btHeight_X3 = btHeight*3;
        //Log.i(TAG, "onCreateView: ----------------bbbbbbbbbbbbbbbbbbbb------------------"+btHeight);

        //获取顶部控件的高度
        btPullMenu.measure(w,h);
        btMenuHeight = linearMenu.getMeasuredHeight();

        //获取浮现的三个部分的高度
        llScene.measure(w,h);
        linearPanel.measure(w,h);
        tvPanelTitle.measure(w,h);
        tvSceneHeight = llScene.getMeasuredHeight();
        linearSceneHeight = linearPanel.getMeasuredHeight();
        tvLightHeight = tvPanelTitle.getMeasuredHeight();
        initFloatHeight = tvSceneHeight+linearSceneHeight+tvLightHeight;  //app打开时下面浮动部分的高度
        //Log.i(TAG, "onCreateView:app打开时下面浮动部分的高度 "+tvSceneHeight+"-----------"+linearSceneHeight+"-----------------"+tvLightHeight);

        //整个上拉菜单的高度
        svPullUpMenu.measure(w,h);
        linearPullUpScrollHeight = svPullUpMenu.getMeasuredHeight();
        //Log.i(TAG, "onCreateView: 整个上拉菜单的高度"+linearPullUpScrollHeight);

        //获取灯光刻度条图片的长度
        ivLightValue1.measure(w,h);
        lightValueHeight1 = ivLightValue1.getMeasuredWidth();
        ivLightValue2.measure(w,h);
        lightValueHeight2 = ivLightValue2.getMeasuredWidth();
        lightAxisExWidth = (lightValueHeight1+lightValueHeight2)*9+3;        //初始化数轴左右多出部分的长度

        //获取灯光刻度条HorizontalScrollView控件的宽度
        hsLightValue.measure(w,h);
        horizontalHeight = hsLightValue.getMeasuredWidthAndState();
        //获取灯光刻度条rlLightValue控件的宽度
        ViewTreeObserver vto2 = rlLightValue.getViewTreeObserver();
        vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                rlLightValue.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                rlLightValueHeight = rlLightValue.getWidth();      //数轴可显示部分的长度
                //Log.i(TAG, "onCreateView: 控件的长度？？"+rlLightValueHeight);
                lightAxisinitValue = lightAxisExWidth - (rlLightValueHeight/2);
                //Log.i(TAG, "onCreateView: !!!!!!!!!!!!!!!!!!!!!"+lightAxisinitValue);
                //初始化hsScrollView的位置（指针指向0刻度）
                //Log.i(TAG, "run: ----------------------"+lightAxisinitValue);
                hsLightValue.scrollTo(lightAxisinitValue,0);

            }
        });

        //设置


        scrollView = (MyCustomScrollView) view.findViewById(R.id.id_scroll);
        //scrollView.setDownOnConnectionListener(this);
        /*scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });*/
        //获取屏幕宽高，设置图片大小一致
        WindowManager wm = this.getActivity().getWindowManager();
        width = wm.getDefaultDisplay().getWidth();
        height = wm.getDefaultDisplay().getHeight();
        params = ivBackGround.getLayoutParams();
        int statusHeight = getStatusBar();
        params.height = height - statusHeight+500;       //设置图片长度，使上拉时有图片上升的一个效果
        params.width = width;
        ivBackGround.setLayoutParams(params);
        ivBackGroundTrans.setLayoutParams(params);

        //设置头部控件高度和点击按钮一致
        ViewGroup.LayoutParams layoutParams = linearClick.getLayoutParams();
        layoutParams.height = btHeight;
        linearClick.setLayoutParams(layoutParams);

        //设置电视模块高度
        ViewGroup.LayoutParams lpTV = llTelevision.getLayoutParams();
        lpTV.height = linearSceneHeight;
        llTelevision.setLayoutParams(lpTV);

        //设置字体
        typeFace = Typeface.createFromAsset(getActivity().getAssets(),"fonts/xiyuanti.ttf");
        setFonts();
        tvRoomName.setText(roomName);    //设置房间名

        tvRoomNameTop.setText(roomName);
        ViewGroup.LayoutParams layoutParams1 = rlRoomName.getLayoutParams();
        layoutParams1.height = height-initFloatHeight+btHeight*2;     //设置房间textview的高度 = 屏幕高 - 浮出部分高 + 上部控件的高*2
        //layoutParams1.height = linearPullUpScrollHeight-initFloatHeight;     //设置房间textview的高度 = 屏幕高 - 浮出部分高 + 上部控件的高*2
        rlRoomName.setLayoutParams(layoutParams1);

        scrollToBottomInit();   //初始化ScrollView的位置

        //手势
        final GestureDetector mGestureDetector = new GestureDetector(getActivity(), this);
        myOnTouchListener = new MyMainActivity.MyOnTouchListener() {
            @Override
            public boolean onTouch(MotionEvent ev) {
                boolean result = mGestureDetector.onTouchEvent(ev);
                return result;
            }
        };

        ((MyMainActivity) getActivity()).registerMyOnTouchListener(myOnTouchListener);
        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        activity = getActivity();
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onResume() {
        super.onResume();
        isFragmemtFront = true;
        //initBackGround();     //初始化背景图片

    }

    @Override
    public void onPause() {
        super.onPause();
        isFragmemtFront = false;
        //Log.i(TAG, "onDestroyView: fragment暂停");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);  //反注册EventBus
        Log.i(TAG, "onDestroy: fragmentonDestroyfragmentonDestroyfragmentonDestroyfragmentonDestro");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    private void initView(){

        linear = (RelativeLayout) view.findViewById(R.id.linear);
        linearMenu = (LinearLayout) view.findViewById(R.id.linear_menu);
        linearClick = (LinearLayout) view.findViewById(R.id.linear_up_menu_click);

        ivBackGround = (ImageView) view.findViewById(R.id.id_iv);
        ivBackGroundTrans = (ImageView) view.findViewById(R.id.id_iv1);
        ivStateLight = (ImageView) view.findViewById(R.id.iv_state_light);     //连接状态指示灯

        ivHomeIcon = (ImageView) view.findViewById(R.id.iv_home_icon);
        tvRoomNameTop = (TextView) view.findViewById(R.id.tv_room_name);
        rlRoomName = (RelativeLayout) view.findViewById(R.id.rl_room_name);
        ivXiaoJianJian = (ImageView) view.findViewById(R.id.iv_xiaojianjian);
        btPullMenu = (RelativeLayout) view.findViewById(R.id.rl_bt_menu);
        tvSet = (TextView) view.findViewById(R.id.tv_set);
        tvMonitoring = (TextView) view.findViewById(R.id.tv_monitoring);
        tvLock = (TextView) view.findViewById(R.id.tv_lock);
        tvSecurity = (TextView) view.findViewById(R.id.tv_security);
        tvScene = (TextView) view.findViewById(R.id.tv_scene_name);
        tvCustomScene = (TextView) view.findViewById(R.id.tv_scene_custom);

        //控制部分  上
        tvRoomName = (TextView) view.findViewById(R.id.tv_blank);
        ivSwitchRoomIcon = (ImageView) view.findViewById(R.id.iv_switch_room_icon);
        //tvLocalTemp = (TextView) view.findViewById(R.id.tv_local_temp);
        btShezhi = (Button) view.findViewById(R.id.bt_shezhi);
        btJiankong = (Button) view.findViewById(R.id.bt_jiankong);
        btMensuo = (Button) view.findViewById(R.id.bt_mensuo);
        btBufang = (Button) view.findViewById(R.id.bt_bufang);
        btShezhi.setOnClickListener(clickListener);
        btJiankong.setOnClickListener(clickListener);
        btMensuo.setOnClickListener(clickListener);
        btBufang.setOnClickListener(clickListener);
//        linearClick.
        initControler();    //初始化控制部分  下

        initPanelIcon();     //初始化面板图标


    }

    //初始化不同房间的面板图标
    private void initPanelIcon(){
        switch (roomDBName){
            case "keting":
                setDifferentRoomsPanelIcon(R.mipmap.entertainment_dark_3x,R.mipmap.yule_mianban_dark_3x,R.mipmap.music_dark_3x,"休 闲","娱 乐","音 乐");
                break;
            case "woshi":
                setDifferentRoomsPanelIcon(R.mipmap.read_dark_3x,R.mipmap.game_dark_3x,R.mipmap.music_dark_3x,"阅 读","娱 乐","音 乐");
                break;
            case "ciwo":
                setDifferentRoomsPanelIcon(R.mipmap.read_dark_3x,R.mipmap.game_dark_3x,R.mipmap.music_dark_3x,"阅 读","娱 乐","音 乐");
                break;
            case "chufang":
                setDifferentRoomsPanelIcon(R.mipmap.clean_up_dark_3x,R.mipmap.cook_dark_3x,R.mipmap.music_dark_3x,"清 洁","烹 饪","音 乐");
                break;
            case "canting":
                setDifferentRoomsPanelIcon(R.mipmap.dinner_dark_3x,R.mipmap.snack_dark_3x,R.mipmap.afternoon_tea_dark_3x,"正 餐","夜 宵","午 茶");
                break;
            case "weiyu":
                setDifferentRoomsPanelIcon(R.mipmap.wash_dark_3x,R.mipmap.toilet_dark_3x,R.mipmap.shower_dark_3x,"洗 漱","如 厕","淋 浴");
                break;
        }
    }

    //不同的房间设置不同的面板图标    (一共四个，最后一个是离开，不用设置)
    private void setDifferentRoomsPanelIcon(int r1,int r2,int r3,String s1,String s2,String s3){
        ivPanel1.setImageResource(r1);
        ivPanel2.setImageResource(r2);
        ivPanel3.setImageResource(r3);
        tvPanel1.setText(s1);
        tvPanel2.setText(s2);
        tvPanel3.setText(s3);
    }

    private void initControler(){
        //初始化 浮现的部分
        tvPanelTitle = (TextView) view.findViewById(R.id.tv_panel_name);
        linearPanel = (LinearLayout) view.findViewById(R.id.linear_panel_dining);
        llScene = (LinearLayout) view.findViewById(R.id.ll_scene_name);

        //面板
        tvLight = (TextView) view.findViewById(R.id.tv_light);
        rlPanel1 = (RelativeLayout) view.findViewById(R.id.rl_panel_1);
        ivPanel1 = (ImageView) view.findViewById(R.id.iv_panel_1);
        tvPanel1 = (TextView) view.findViewById(R.id.tv_panel_1);
        rlPanel2 = (RelativeLayout) view.findViewById(R.id.rl_panel_2);
        ivPanel2 = (ImageView) view.findViewById(R.id.iv_panel_2);
        tvPanel2 = (TextView) view.findViewById(R.id.tv_panel_2);
        rlPanel3 = (RelativeLayout) view.findViewById(R.id.rl_panel_3);
        ivPanel3 = (ImageView) view.findViewById(R.id.iv_panel_3);
        tvPanel3 = (TextView) view.findViewById(R.id.tv_panel_3);
        rlPanel4 = (RelativeLayout) view.findViewById(R.id.rl_panel_4);
        ivPanel4 = (ImageView) view.findViewById(R.id.iv_panel_4);
        tvPanel4 = (TextView) view.findViewById(R.id.tv_panel_4);
        rlPanel1.setOnClickListener(controlListener);
        rlPanel2.setOnClickListener(controlListener);
        rlPanel3.setOnClickListener(controlListener);
        rlPanel4.setOnClickListener(controlListener);

        //场景
         rlXiuxian = (RelativeLayout) view.findViewById(R.id.rl_xiuxian);
         rlYule = (RelativeLayout) view.findViewById(R.id.rl_yule);
         rlJuhui = (RelativeLayout) view.findViewById(R.id.rl_juhui);
         rlLikai = (RelativeLayout) view.findViewById(R.id.rl_likai);
        //灯光
         rlLight = (RelativeLayout) view.findViewById(R.id.relative_light_switch);
        //窗帘
        rlBulian = (RelativeLayout) view.findViewById(R.id.rl_bulian);
        rlShalian = (RelativeLayout) view.findViewById(R.id.rl_shalian);
        rlAll = (RelativeLayout) view.findViewById(R.id.rl_quanguan);
        rlStop = (RelativeLayout) view.findViewById(R.id.rl_tingzhi);
        //卷帘
        /*rlJuanlian = (RelativeLayout) view.findViewById(R.id.rl_juanlian);
        rlJuanlianR = (RelativeLayout) view.findViewById(R.id.rl_juanlian_r);
        rlAllJuan = (RelativeLayout) view.findViewById(R.id.rl_quanguan_juanlian);
        rlStopJuan = (RelativeLayout) view.findViewById(R.id.rl_tingzhi_juanlian);*/
        //电视
        llTelevision = (LinearLayout) view.findViewById(R.id.ll_television);
        tvTelevision = (TextView) view.findViewById(R.id.tv_television);
        ivTelevisionOn = (ImageView) view.findViewById(R.id.iv_television_on);
        ivTelevisionOff = (ImageView) view.findViewById(R.id.iv_television_off);

        //空调
        rlKongtiao = (RelativeLayout) view.findViewById(R.id.rl_kongtiao_kaiguan);
        rlKongtiaoMoshi = (RelativeLayout) view.findViewById(R.id.rl_kongtiao_moshi);
        linearAirDetails = (LinearLayout) view.findViewById(R.id.linear_air_details);
        sbTemp = (SeekBar) view.findViewById(R.id.sb_air_temp);
        rlAirControl1 = (RelativeLayout) view.findViewById(R.id.rl_air_control_1);
        rlAirControl2 = (RelativeLayout) view.findViewById(R.id.rl_air_control_2);
        rlAirControl3 = (RelativeLayout) view.findViewById(R.id.rl_air_control_3);
        tvDetailTemp = (TextView) view.findViewById(R.id.tv_detail_temp);
        tvAirControl1 = (TextView) view.findViewById(R.id.tv_air_control_1);
        tvAirControl2 = (TextView) view.findViewById(R.id.tv_air_control_2);
        tvAirControl3 = (TextView) view.findViewById(R.id.tv_air_control_3);

        //---------------------------需要做动画的图片--------------------------
        //场景
        ivXiuxianOut = (ImageView) view.findViewById(R.id.iv_xiuxian_out);
        ivXiuxianIn = (ImageView) view.findViewById(R.id.iv_xiuxian_in);
        tvXiuxian = (TextView) view.findViewById(R.id.tv_xiuxian);
        ivYule1 = (ImageView) view.findViewById(R.id.iv_yule_1);
        ivYule2 = (ImageView) view.findViewById(R.id.iv_yule_2);
        ivYule3 = (ImageView) view.findViewById(R.id.iv_yule_3);
        tvYule = (TextView) view.findViewById(R.id.tv_yule);
        ivJuhui1 = (ImageView) view.findViewById(R.id.iv_juhui_1);
        ivJuhui2 = (ImageView) view.findViewById(R.id.iv_juhui_2);
        tvJuhui = (TextView) view.findViewById(R.id.tv_juhui);
        ivLikai = (ImageView) view.findViewById(R.id.iv_likai);
        tvLikai = (TextView) view.findViewById(R.id.tv_likai);
        //灯光
        hsLightValue = (LightValueScrollView) view.findViewById(R.id.hs_light_point);
        rlLightValue = (RelativeLayout) view.findViewById(R.id.relative_light_control);
        ivLightValue1 = (ImageView) view.findViewById(R.id.iv_light_point1);
        ivLightValue2 = (ImageView) view.findViewById(R.id.iv_light_point2);
        ivLightSwitch = (ImageView) view.findViewById(R.id.iv_light_switch);
        tvLightSwitch = (TextView) view.findViewById(R.id.tv_light_kaiguan);
        tvLightValue = (TextView) view.findViewById(R.id.tv_light_point);
        //窗帘 布帘
        ivBulianHead = (ImageView) view.findViewById(R.id.iv_bulian_head);
        ivBulian1 = (ImageView) view.findViewById(R.id.iv_bulian_1);
        ivBulian2 = (ImageView) view.findViewById(R.id.iv_bulian_2);
        ivBulian3 = (ImageView) view.findViewById(R.id.iv_bulian_3);
        ivBulian4 = (ImageView) view.findViewById(R.id.iv_bulian_4);
        ivBulian5 = (ImageView) view.findViewById(R.id.iv_bulian_5);
        ivBulian6 = (ImageView) view.findViewById(R.id.iv_bulian_6);
        ivBulianHead1 = (ImageView) view.findViewById(R.id.iv_bulian_head_1);
        tvBulian = (TextView) view.findViewById(R.id.tv_bulian);
        //纱帘
        ivShalianHead = (ImageView) view.findViewById(R.id.iv_shalian_head);
        ivShalian1 = (ImageView) view.findViewById(R.id.iv_shalian_1);
        ivShalian2 = (ImageView) view.findViewById(R.id.iv_shalian_2);
        ivShalian3 = (ImageView) view.findViewById(R.id.iv_shalian_3);
        ivShalian4 = (ImageView) view.findViewById(R.id.iv_shalian_4);
        ivShalian5 = (ImageView) view.findViewById(R.id.iv_shalian_5);
        ivShalian6 = (ImageView) view.findViewById(R.id.iv_shalian_6);
        ivShalianHead1 = (ImageView) view.findViewById(R.id.iv_shalian_head_2);
        tvShalian = (TextView) view.findViewById(R.id.tv_shalian);
        //左 卷帘
        ivJuanlianHead = (ImageView) view.findViewById(R.id.iv_juanlian_head);
        ivJuanlian1 = (ImageView) view.findViewById(R.id.iv_juanlian_1);
        ivJuanlian2 = (ImageView) view.findViewById(R.id.iv_juanlian_2);
        ivJuanlian3 = (ImageView) view.findViewById(R.id.iv_juanlian_3);
        ivJuanlian4 = (ImageView) view.findViewById(R.id.iv_juanlian_4);
        ivJuanlian5 = (ImageView) view.findViewById(R.id.iv_juanlian_5);
        ivJuanlian6 = (ImageView) view.findViewById(R.id.iv_juanlian_6);
        ivJuanlianHead1 = (ImageView) view.findViewById(R.id.iv_juanlian_head_1);
        tvJuanlian = (TextView) view.findViewById(R.id.tv_juanlian);
        //右 卷帘
        ivJuanlianHeadR = (ImageView) view.findViewById(R.id.iv_juanlian_head_r);
        ivJuanlian1R = (ImageView) view.findViewById(R.id.iv_juanlian_1_r);
        ivJuanlian2R = (ImageView) view.findViewById(R.id.iv_juanlian_2_r);
        ivJuanlian3R = (ImageView) view.findViewById(R.id.iv_juanlian_3_r);
        ivJuanlian4R = (ImageView) view.findViewById(R.id.iv_juanlian_4_r);
        ivJuanlian5R = (ImageView) view.findViewById(R.id.iv_juanlian_5_r);
        ivJuanlian6R = (ImageView) view.findViewById(R.id.iv_juanlian_6_r);
        ivJuanlianHead1R = (ImageView) view.findViewById(R.id.iv_juanlian_head_1_r);
        tvJuanlianR = (TextView) view.findViewById(R.id.tv_juanlian_r);
        //全开 全关
        ivAll = (ImageView) view.findViewById(R.id.iv_chuanglian_all);
        tvAll = (TextView) view.findViewById(R.id.tv_all);
        ivStop = (ImageView) view.findViewById(R.id.iv_chuanglian_stop);
        tvStop = (TextView) view.findViewById(R.id.tv_chuanglian_stop);
        ivAllJuan = (ImageView) view.findViewById(R.id.iv_chuanglian_all_juanlian);
        tvAllJuan = (TextView) view.findViewById(R.id.tv_all_juanlian);
        ivStopJuan = (ImageView) view.findViewById(R.id.iv_chuanglian_stop_juanlian);
        tvStopJuan = (TextView) view.findViewById(R.id.tv_chuanglian_stop_juanlian);
        //空调
        ivKongtiaoSwitch = (ImageView) view.findViewById(R.id.iv_kongtiao_kaiguan);
        tvKongtiaoSwitch = (TextView) view.findViewById(R.id.tv_kongtiao_kaiguan);
        tvKongtiaoTemp = (TextView) view.findViewById(R.id.tv_kongtiao_wendu);
        ivKongtiaoMoshi = (ImageView) view.findViewById(R.id.iv_kongtiao_sleep);
        tvKongtiaoMoshi = (TextView) view.findViewById(R.id.tv_kongtiao_sleep);

        //控制模块（灯，窗帘，空调等）
        llLight = (LinearLayout) view.findViewById(R.id.ll_light);
        llChuanglian = (LinearLayout) view.findViewById(R.id.ll_chuanglian);
        //llJuanlian = (LinearLayout) view.findViewById(R.id.ll_juanlian);
        llAirCondition = (LinearLayout) view.findViewById(R.id.ll_air_condition);
        tvChuanglian = (TextView) view.findViewById(R.id.tv_curtain);
        //tvJuanLian = (TextView) view.findViewById(R.id.tv_roller_curtain);
        tvAirCondition = (TextView) view.findViewById(R.id.tv_air_condition);

        //加入改字体的列表
        tvList.add(tvSet);
        tvList.add(tvMonitoring);
        tvList.add(tvLock);
        tvList.add(tvSecurity);
        tvList.add(tvRoomNameTop);
        tvList.add(tvRoomName);
        tvList.add(tvXiuxian);
        tvList.add(tvYule);
        tvList.add(tvJuhui);
        tvList.add(tvLikai);
        tvList.add(tvScene);
        tvList.add(tvCustomScene);
        tvList.add(tvLight);
        tvList.add(tvLightSwitch);
        tvList.add(tvLightValue);
        tvList.add(tvChuanglian);
        tvList.add(tvBulian);
        tvList.add(tvShalian);
        tvList.add(tvAll);
        tvList.add(tvStop);
        tvList.add(tvTelevision);
        //tvList.add(tvJuanLian);
        //tvList.add(tvJuanlian);
        //tvList.add(tvJuanlianR);
        //tvList.add(tvAllJuan);
        //tvList.add(tvStopJuan);
        tvList.add(tvAirCondition);
        tvList.add(tvKongtiaoSwitch);
        tvList.add(tvKongtiaoTemp);
        tvList.add(tvKongtiaoMoshi);
        tvList.add(tvDetailTemp);
        tvList.add(tvAirControl1);
        tvList.add(tvAirControl2);
        tvList.add(tvAirControl3);
        tvList.add(tvPanelTitle);
        tvList.add(tvPanel1);
        tvList.add(tvPanel2);
        tvList.add(tvPanel3);
        tvList.add(tvPanel4);
        //tvList.add(tvLocalTemp);


        //根据房间信息选择隐藏的控件
        /*if (roomAttr.getLight().getActive()==0){
            initRoomAttr(tvLight,llLight);
        }if (roomAttr.getCurtain().getActive()==0){
            initRoomAttr(tvChuanglian,llChuanglian);
            initRoomAttr(tvJuanLian,llJuanlian);
        }*/



        rlXiuxian.setOnClickListener(controlListener);
        rlYule.setOnClickListener(controlListener);
        rlJuhui.setOnClickListener(controlListener);
        rlLikai.setOnClickListener(controlListener);

        rlLight.setOnClickListener(controlListener);

        rlBulian.setOnClickListener(controlListener);
        rlShalian.setOnClickListener(controlListener);
        rlAll.setOnClickListener(controlListener);
        rlStop.setOnClickListener(controlListener);
        /*rlJuanlian.setOnClickListener(controlListener);
        rlJuanlianR.setOnClickListener(controlListener);
        rlAllJuan.setOnClickListener(controlListener);
        rlStopJuan.setOnClickListener(controlListener);*/

        rlKongtiao.setOnClickListener(controlListener);
        rlKongtiaoMoshi.setOnClickListener(controlListener);
        rlAirControl1.setOnClickListener(controlListener);
        rlAirControl2.setOnClickListener(controlListener);
        rlAirControl3.setOnClickListener(controlListener);
        tvRoomName.setOnClickListener(controlListener);
        ivSwitchRoomIcon.setOnClickListener(controlListener);
        ivTelevisionOn.setOnClickListener(controlListener);
        ivTelevisionOff.setOnClickListener(controlListener);
        tvCustomScene.setOnClickListener(controlListener);


        sbTemp.setOnSeekBarChangeListener(tempSeekBarListener);  //空调调温SeekBar
        //hsLightValue.setOnScrollChangeListener(hsChangeListener);       //android 23 以上才能用
        hsLightValue.setScrollViewListenner(hsChangeListener);

        //判断hsScrollView是否停下------------发送灯光的控制命令-------------
        hsLightValue.setOnTouchListener(new View.OnTouchListener() {
            private int lastX = 0;
            private int touchEventId = -9983762;
            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    View scroller = (View) msg.obj;
                    if (msg.what == touchEventId) {
                        if (lastX == scroller.getScrollX()) {
                            handleStop(scroller);
                        } else {
                            handler.sendMessageDelayed(handler.obtainMessage(touchEventId, scroller),15);
                            lastX = scroller.getScrollX();
                        }
                    }
                }
            };

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    handler.sendMessageDelayed(handler.obtainMessage(touchEventId, view), 5);
                }
                hsLightValue.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }

            private void handleStop(Object view) {
                HorizontalScrollView scroller = (HorizontalScrollView) view;
                int mScrollX = scroller.getScrollX();
                //Log.i(TAG, "handleStop: ----------mScrollX11----------"+mScrollX);
                //Log.i(TAG, "handleStop: ----------lightAxisinitValue----------"+lightAxisinitValue);

                if(mScrollX<=lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*0.5){
                    hsScrollMeasure(0);
                    controlLight(CONTROL_PROTOCOL_HDL,MACHINE_NAME_LIGHT,LIGHT_VALUE_0,IS_SERVER_AUTO,CONTROL_SENCE_ALL);
                }else if (mScrollX>lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*0.5 && mScrollX<=lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*1.5){
                    hsScrollMeasure(1);
                    controlLight(CONTROL_PROTOCOL_HDL,MACHINE_NAME_LIGHT,"5",IS_SERVER_AUTO,CONTROL_SENCE_ALL);
                }else if (mScrollX>lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*1.5 && mScrollX<=lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*2.5){
                    hsScrollMeasure(2);
                    controlLight(CONTROL_PROTOCOL_HDL,MACHINE_NAME_LIGHT,"10",IS_SERVER_AUTO,CONTROL_SENCE_ALL);
                }else if (mScrollX>lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*2.5 && mScrollX<=lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*3.5){
                    hsScrollMeasure(3);
                    controlLight(CONTROL_PROTOCOL_HDL,MACHINE_NAME_LIGHT,"15",IS_SERVER_AUTO,CONTROL_SENCE_ALL);
                }else if (mScrollX>lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*3.5 && mScrollX<=lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*4.5){
                    hsScrollMeasure(4);
                    controlLight(CONTROL_PROTOCOL_HDL,MACHINE_NAME_LIGHT,"20",IS_SERVER_AUTO,CONTROL_SENCE_ALL);
                }else if (mScrollX>lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*4.5 && mScrollX<=lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*5.5){
                    hsScrollMeasure(5);
                    controlLight(CONTROL_PROTOCOL_HDL,MACHINE_NAME_LIGHT,"25",IS_SERVER_AUTO,CONTROL_SENCE_ALL);
                }else if (mScrollX>lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*5.5 && mScrollX<=lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*6.5){
                    hsScrollMeasure(6);
                    controlLight(CONTROL_PROTOCOL_HDL,MACHINE_NAME_LIGHT,"30",IS_SERVER_AUTO,CONTROL_SENCE_ALL);
                }else if (mScrollX>lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*6.5 && mScrollX<=lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*7.5){
                    hsScrollMeasure(7);
                    controlLight(CONTROL_PROTOCOL_HDL,MACHINE_NAME_LIGHT,"35",IS_SERVER_AUTO,CONTROL_SENCE_ALL);
                }else if (mScrollX>lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*7.5 && mScrollX<=lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*8.5){
                    hsScrollMeasure(8);
                    controlLight(CONTROL_PROTOCOL_HDL,MACHINE_NAME_LIGHT,"40",IS_SERVER_AUTO,CONTROL_SENCE_ALL);
                }else if (mScrollX>lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*8.5 && mScrollX<=lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*9.5){
                    hsScrollMeasure(9);
                    controlLight(CONTROL_PROTOCOL_HDL,MACHINE_NAME_LIGHT,"45",IS_SERVER_AUTO,CONTROL_SENCE_ALL);
                }else if (mScrollX>lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*9.5 && mScrollX<=lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*10.5){
                    hsScrollMeasure(10);
                    controlLight(CONTROL_PROTOCOL_HDL,MACHINE_NAME_LIGHT,"50",IS_SERVER_AUTO,CONTROL_SENCE_ALL);
                }else if (mScrollX>lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*10.5 && mScrollX<=lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*11.5){
                    hsScrollMeasure(11);
                    controlLight(CONTROL_PROTOCOL_HDL,MACHINE_NAME_LIGHT,"55",IS_SERVER_AUTO,CONTROL_SENCE_ALL);
                }else if (mScrollX>lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*11.5 && mScrollX<=lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*12.5){
                    hsScrollMeasure(12);
                    controlLight(CONTROL_PROTOCOL_HDL,MACHINE_NAME_LIGHT,"60",IS_SERVER_AUTO,CONTROL_SENCE_ALL);
                }else if (mScrollX>lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*12.5 && mScrollX<=lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*13.5){
                    hsScrollMeasure(13);
                    controlLight(CONTROL_PROTOCOL_HDL,MACHINE_NAME_LIGHT,"65",IS_SERVER_AUTO,CONTROL_SENCE_ALL);
                }else if (mScrollX>lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*13.5 && mScrollX<=lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*14.5){
                    hsScrollMeasure(14);
                    controlLight(CONTROL_PROTOCOL_HDL,MACHINE_NAME_LIGHT,"70",IS_SERVER_AUTO,CONTROL_SENCE_ALL);
                }else if (mScrollX>lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*14.5 && mScrollX<=lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*15.5){
                    hsScrollMeasure(15);
                    controlLight(CONTROL_PROTOCOL_HDL,MACHINE_NAME_LIGHT,"75",IS_SERVER_AUTO,CONTROL_SENCE_ALL);
                }else if (mScrollX>lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*15.5 && mScrollX<=lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*16.5){
                    hsScrollMeasure(16);
                    controlLight(CONTROL_PROTOCOL_HDL,MACHINE_NAME_LIGHT,"80",IS_SERVER_AUTO,CONTROL_SENCE_ALL);
                }else if (mScrollX>lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*16.5 && mScrollX<=lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*17.5){
                    hsScrollMeasure(17);
                    controlLight(CONTROL_PROTOCOL_HDL,MACHINE_NAME_LIGHT,"85",IS_SERVER_AUTO,CONTROL_SENCE_ALL);
                }else if (mScrollX>lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*17.5 && mScrollX<=lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*18.5){
                    hsScrollMeasure(18);
                    controlLight(CONTROL_PROTOCOL_HDL,MACHINE_NAME_LIGHT,"90",IS_SERVER_AUTO,CONTROL_SENCE_ALL);
                }else if (mScrollX>lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*18.5 && mScrollX<=lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*19.5){
                    hsScrollMeasure(19);
                    controlLight(CONTROL_PROTOCOL_HDL,MACHINE_NAME_LIGHT,"95",IS_SERVER_AUTO,CONTROL_SENCE_ALL);
                }else if (mScrollX>lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*19.5){
                    hsScrollMeasure(20);
                    controlLight(CONTROL_PROTOCOL_HDL,MACHINE_NAME_LIGHT,LIGHT_VALUE_100,IS_SERVER_AUTO,CONTROL_SENCE_ALL);
                }

            }

            /*
            * lightValue 灯光亮度数值0~100
            * scrollTo  0~20   21个调光刻度
            *
            * */
            private void hsScrollMeasure(final int scrollTo){
                //Handler handler = new Handler();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        hsLightValue.smoothScrollTo(lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*scrollTo,0);
                    }
                });
            }
        });

    }

    //--------------------------------接受activity传来的消息---------------------------------------
    @Subscribe
    public void onEventMainThread(MyEventBus2 event) {
        /*if (msg=="jiankong"){
            Intent intent = new Intent(MyMainActivity.this,EzCameraActivity.class);
            startActivity(intent);
        }*/
        switch (event.getmMsg()){
            case SCROLL_FRAGMENT_START:          //开始切换viewpager页面
                //Log.i(TAG, "onEventMainThread: --------------onEventMainThread收到了消息--------------"+event.getmMsg());
                btPullMenu.setVisibility(View.GONE);
                //homeButtonSetGoneAnimator();
                break;
            case SCROLL_FRAGMENT_END:          //结束切换viewpager页面
                //Log.i(TAG, "onEventMainThread: --------------onEventMainThread收到了消息--------------"+event.getmMsg());
                btPullMenu.setVisibility(View.VISIBLE);
                //homeButtonSetVisableAnimator();
                ObjectAnimator.ofFloat(btPullMenu,"translationY",-btMenuHeight,0).setDuration(600).start();
                break;
            case "11":
                Log.i(TAG, "onEventMainThread: ------————————————————————————————————————————————————————————---内网连接---------");
                ivStateLight.setImageResource(R.drawable.state_light_connect);
                break;
            case "12":
                Log.i(TAG, "onEventMainThread: -----——————————————————————————————————————————————————————————----外网连接---------");
                ivStateLight.setImageResource(R.drawable.state_light_connect_4g);
                break;
            case "22":
                Log.i(TAG, "onEventMainThread: --————————————————————————————————————————————————————————-------断开连接---------");
                ivStateLight.setImageResource(R.drawable.state_light_disconnect);
                break;
        }
    }

    //--------------------------------------点击事件-------------------------------------------
    View.OnClickListener controlListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.rl_panel_1:
                    clickPanel(1);
                    setScaleAnimation(rlPanel1);
                    break;
                case R.id.rl_panel_2:
                    clickPanel(2);
                    setScaleAnimation(rlPanel2);
                    break;
                case R.id.rl_panel_3:
                    clickPanel(3);
                    setScaleAnimation(rlPanel3);
                    break;
                case R.id.rl_panel_4:
                    clickPanel(4);
                    setScaleAnimation(rlPanel4);
                    break;
                case R.id.tv_blank:      //点击弹出切换房间dialog
                    clickRoomName();
                    break;
                case R.id.iv_switch_room_icon:
                    clickRoomName();
                    break;
                case R.id.rl_xiuxian:
                    setScaleAnimation(rlXiuxian);
                    clickXiuxian();
                    setPanelIconColor(roomDBName);
                    break;
                case R.id.rl_yule:
                    setScaleAnimation(rlYule);
                    clickYule();
                    setPanelIconColor(roomDBName);
                    break;
                case R.id.rl_juhui:
                    setScaleAnimation(rlJuhui);
                    clickJuhui();
                    setPanelIconColor(roomDBName);
                    break;
                case R.id.rl_likai:
                    setScaleAnimation(rlLikai);
                    clickLikai();
                    setPanelIconColor(roomDBName);
                    break;
                case R.id.relative_light_switch:
                    setScaleAnimation(rlLight);
                    clickLightSwitch();
                    break;
                case R.id.rl_bulian:
                    setScaleAnimation(rlBulian);
                    clickBulian();
                    break;
                case R.id.rl_shalian:
                    setScaleAnimation(rlShalian);
                    clickShalian();
                    break;
                case R.id.rl_quanguan:
                    setScaleAnimation(rlAll);
                    clickAll();
                    break;
                case R.id.rl_tingzhi:
                    setCurtainStopAnimation(rlStop);
                    break;
                /*case R.id.rl_juanlian:
                    EventBus.getDefault().post(new MyEventBus(roomName+"juanlian"));
                    setScaleAnimation(rlJuanlian);
                    clickJuanlian();
                    break;
                case R.id.rl_juanlian_r:
                    EventBus.getDefault().post(new MyEventBus(roomName+"juanlian_r"));
                    setScaleAnimation(rlJuanlianR);
                    clickJuanlianR();
                    break;
                case R.id.rl_quanguan_juanlian:
                    EventBus.getDefault().post(new MyEventBus(roomName+"all_juanlian"));
                    setScaleAnimation(rlAllJuan);
                    clickAllJuan();
                    break;
                case R.id.rl_tingzhi_juanlian:
                    EventBus.getDefault().post(new MyEventBus(roomName+"stop_juanlian"));
                    setCurtainJuanStopAnimation(rlStopJuan);
                    break;*/
                case R.id.iv_television_on:
                    setScaleAnimation(ivTelevisionOn);
                    break;
                case R.id.iv_television_off:
                    setScaleAnimation(ivTelevisionOff);
                    break;
                case R.id.rl_kongtiao_kaiguan:
                    //EventBus.getDefault().post(new MyEventBus(roomName+"kongtiao"));
                    rlKongtiao.setClickable(false);   //点击后设为不可点击
                    ObjectAnimator.ofFloat(rlKongtiao,"scaleX",1f,0.8f,1f).setDuration(600).start();
                    ObjectAnimator objectAnimator = new ObjectAnimator().ofFloat(rlKongtiao,"scaleY",1f,0.8f,1f).setDuration(600);
                    objectAnimator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            clickAirSwitch();
                        }
                    });
                    objectAnimator.start();
                    break;
                case R.id.rl_kongtiao_moshi:
                    setScaleAnimation(rlKongtiaoMoshi);
                    Toast.makeText(activity, "睡眠 模式", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.rl_air_control_1:
                    setScaleAnimation(rlAirControl1);
                    Toast.makeText(activity, "制冷", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.rl_air_control_2:
                    setScaleAnimation(rlAirControl2);
                    Toast.makeText(activity, "风速", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.rl_air_control_3:
                    setScaleAnimation(rlAirControl3);
                    Toast.makeText(activity, "定时", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.tv_scene_custom:      //跳转场景自定义界面
                    Intent intent = new Intent(getContext(), CustomSceneMainActivity.class);
                    intent.putExtra("roomDBName",roomDBName);
                    startActivity(intent);
                    break;
            }
        }
    };

    //设置不同房间面板点击后变色
    private void clickPanel(int index){
        switch (roomDBName){
            case "keting":
                changePanelKeting(index);
                break;
            case "woshi":
                changePanelZhuwo(index);
                break;
            case "ciwo":
                changePanelZhuwo(index);
                break;
            case "chufang":
                changePanelChufang(index);
                break;
            case "canting":
                changePanelCanting(index);
                break;
            case "weiyu":
                changePanelWeiyu(index);
                break;
        }
    }


    //客厅面板点击变色
    private void changePanelKeting(int i){
        switch (i){
            case 1:
                controlPanel(CONTROL_PROTOCOL_HDL,"panel","1");
                ivPanel1.setImageResource(R.mipmap.entertainment_copy_3x);
                tvPanel1.setTextColor(activity.getResources().getColor(R.color.colorTextActive));
                ivPanel2.setImageResource(R.mipmap.yule_mianban_dark_3x);
                tvPanel2.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel3.setImageResource(R.mipmap.music_dark_3x);
                tvPanel3.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel4.setImageResource(R.mipmap.get_out_dark_3x);
                tvPanel4.setTextColor(activity.getResources().getColor(R.color.colorText));
                break;
            case 2:
                controlPanel(CONTROL_PROTOCOL_HDL,"panel","2");
                ivPanel1.setImageResource(R.mipmap.entertainment_dark_3x);
                tvPanel1.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel2.setImageResource(R.mipmap.yule_mianban_3x);
                tvPanel2.setTextColor(activity.getResources().getColor(R.color.colorTextActive));
                ivPanel3.setImageResource(R.mipmap.music_dark_3x);
                tvPanel3.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel4.setImageResource(R.mipmap.get_out_dark_3x);
                tvPanel4.setTextColor(activity.getResources().getColor(R.color.colorText));
                break;
            case 3:
                controlPanel(CONTROL_PROTOCOL_HDL,"panel","3");
                ivPanel1.setImageResource(R.mipmap.entertainment_dark_3x);
                tvPanel1.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel2.setImageResource(R.mipmap.yule_mianban_dark_3x);
                tvPanel2.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel3.setImageResource(R.mipmap.music_3x);
                tvPanel3.setTextColor(activity.getResources().getColor(R.color.colorTextActive));
                ivPanel4.setImageResource(R.mipmap.get_out_dark_3x);
                tvPanel4.setTextColor(activity.getResources().getColor(R.color.colorText));
                break;
            case 4:
                controlPanel(CONTROL_PROTOCOL_HDL,"panel","4");
                ivPanel1.setImageResource(R.mipmap.entertainment_dark_3x);
                tvPanel1.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel2.setImageResource(R.mipmap.yule_mianban_dark_3x);
                tvPanel2.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel3.setImageResource(R.mipmap.music_dark_3x);
                tvPanel3.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel4.setImageResource(R.mipmap.get_out_3x);
                tvPanel4.setTextColor(activity.getResources().getColor(R.color.colorTextActive));
                break;
        }
    }

    //主卧面板点击变色
    private void changePanelZhuwo(int i){
        switch (i){
            case 1:
                controlPanel(CONTROL_PROTOCOL_HDL,"panel","1");
                ivPanel1.setImageResource(R.mipmap.read_3x);
                tvPanel1.setTextColor(activity.getResources().getColor(R.color.colorTextActive));
                ivPanel2.setImageResource(R.mipmap.game_dark_3x);
                tvPanel2.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel3.setImageResource(R.mipmap.music_dark_3x);
                tvPanel3.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel4.setImageResource(R.mipmap.get_out_dark_3x);
                tvPanel4.setTextColor(activity.getResources().getColor(R.color.colorText));
                break;
            case 2:
                controlPanel(CONTROL_PROTOCOL_HDL,"panel","2");
                ivPanel1.setImageResource(R.mipmap.read_dark_3x);
                tvPanel1.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel2.setImageResource(R.mipmap.game_3x);
                tvPanel2.setTextColor(activity.getResources().getColor(R.color.colorTextActive));
                ivPanel3.setImageResource(R.mipmap.music_dark_3x);
                tvPanel3.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel4.setImageResource(R.mipmap.get_out_dark_3x);
                tvPanel4.setTextColor(activity.getResources().getColor(R.color.colorText));
                break;
            case 3:
                controlPanel(CONTROL_PROTOCOL_HDL,"panel","3");
                ivPanel1.setImageResource(R.mipmap.read_dark_3x);
                tvPanel1.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel2.setImageResource(R.mipmap.game_dark_3x);
                tvPanel2.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel3.setImageResource(R.mipmap.music_3x);
                tvPanel3.setTextColor(activity.getResources().getColor(R.color.colorTextActive));
                ivPanel4.setImageResource(R.mipmap.get_out_dark_3x);
                tvPanel4.setTextColor(activity.getResources().getColor(R.color.colorText));
                break;
            case 4:
                controlPanel(CONTROL_PROTOCOL_HDL,"panel","4");
                ivPanel1.setImageResource(R.mipmap.read_dark_3x);
                tvPanel1.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel2.setImageResource(R.mipmap.game_dark_3x);
                tvPanel2.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel3.setImageResource(R.mipmap.music_dark_3x);
                tvPanel3.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel4.setImageResource(R.mipmap.get_out_3x);
                tvPanel4.setTextColor(activity.getResources().getColor(R.color.colorTextActive));
                break;
        }
    }


    //厨房面板点击变色
    private void changePanelChufang(int i){
        switch (i){
            case 1:
                controlPanel(CONTROL_PROTOCOL_HDL,"panel","1");
                ivPanel1.setImageResource(R.mipmap.clean_up_3x);
                tvPanel1.setTextColor(activity.getResources().getColor(R.color.colorTextActive));
                ivPanel2.setImageResource(R.mipmap.cook_dark_3x);
                tvPanel2.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel3.setImageResource(R.mipmap.music_dark_3x);
                tvPanel3.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel4.setImageResource(R.mipmap.get_out_dark_3x);
                tvPanel4.setTextColor(activity.getResources().getColor(R.color.colorText));
                break;
            case 2:
                controlPanel(CONTROL_PROTOCOL_HDL,"panel","2");
                ivPanel1.setImageResource(R.mipmap.clean_up_dark_3x);
                tvPanel1.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel2.setImageResource(R.mipmap.cook_3x);
                tvPanel2.setTextColor(activity.getResources().getColor(R.color.colorTextActive));
                ivPanel3.setImageResource(R.mipmap.music_dark_3x);
                tvPanel3.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel4.setImageResource(R.mipmap.get_out_dark_3x);
                tvPanel4.setTextColor(activity.getResources().getColor(R.color.colorText));
                break;
            case 3:
                controlPanel(CONTROL_PROTOCOL_HDL,"panel","3");
                ivPanel1.setImageResource(R.mipmap.clean_up_dark_3x);
                tvPanel1.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel2.setImageResource(R.mipmap.cook_dark_3x);
                tvPanel2.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel3.setImageResource(R.mipmap.music_3x);
                tvPanel3.setTextColor(activity.getResources().getColor(R.color.colorTextActive));
                ivPanel4.setImageResource(R.mipmap.get_out_dark_3x);
                tvPanel4.setTextColor(activity.getResources().getColor(R.color.colorText));
                break;
            case 4:
                controlPanel(CONTROL_PROTOCOL_HDL,"panel","4");
                ivPanel1.setImageResource(R.mipmap.clean_up_dark_3x);
                tvPanel1.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel2.setImageResource(R.mipmap.cook_dark_3x);
                tvPanel2.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel3.setImageResource(R.mipmap.music_dark_3x);
                tvPanel3.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel4.setImageResource(R.mipmap.get_out_3x);
                tvPanel4.setTextColor(activity.getResources().getColor(R.color.colorTextActive));
                break;
        }
    }

    //餐厅面板点击变色
    private void changePanelCanting(int i){
        switch (i){
            case 1:
                controlPanel(CONTROL_PROTOCOL_HDL,"panel","1");
                ivPanel1.setImageResource(R.mipmap.dinner_3x);
                tvPanel1.setTextColor(activity.getResources().getColor(R.color.colorTextActive));
                ivPanel2.setImageResource(R.mipmap.snack_dark_3x);
                tvPanel2.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel3.setImageResource(R.mipmap.afternoon_tea_dark_3x);
                tvPanel3.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel4.setImageResource(R.mipmap.get_out_dark_3x);
                tvPanel4.setTextColor(activity.getResources().getColor(R.color.colorText));
                break;
            case 2:
                controlPanel(CONTROL_PROTOCOL_HDL,"panel","2");
                ivPanel1.setImageResource(R.mipmap.dinner_dark_3x);
                tvPanel1.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel2.setImageResource(R.mipmap.snack_3x);
                tvPanel2.setTextColor(activity.getResources().getColor(R.color.colorTextActive));
                ivPanel3.setImageResource(R.mipmap.afternoon_tea_dark_3x);
                tvPanel3.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel4.setImageResource(R.mipmap.get_out_dark_3x);
                tvPanel4.setTextColor(activity.getResources().getColor(R.color.colorText));
                break;
            case 3:
                controlPanel(CONTROL_PROTOCOL_HDL,"panel","3");
                ivPanel1.setImageResource(R.mipmap.dinner_dark_3x);
                tvPanel1.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel2.setImageResource(R.mipmap.snack_dark_3x);
                tvPanel2.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel3.setImageResource(R.mipmap.afternoon_tea_3x);
                tvPanel3.setTextColor(activity.getResources().getColor(R.color.colorTextActive));
                ivPanel4.setImageResource(R.mipmap.get_out_dark_3x);
                tvPanel4.setTextColor(activity.getResources().getColor(R.color.colorText));
                break;
            case 4:
                controlPanel(CONTROL_PROTOCOL_HDL,"panel","4");
                ivPanel1.setImageResource(R.mipmap.dinner_dark_3x);
                tvPanel1.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel2.setImageResource(R.mipmap.snack_dark_3x);
                tvPanel2.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel3.setImageResource(R.mipmap.afternoon_tea_dark_3x);
                tvPanel3.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel4.setImageResource(R.mipmap.get_out_3x);
                tvPanel4.setTextColor(activity.getResources().getColor(R.color.colorTextActive));
                break;
        }
    }

    //卫浴面板点击变色
    private void changePanelWeiyu(int i){
        switch (i){
            case 1:
                controlPanel(CONTROL_PROTOCOL_HDL,"panel","1");
                ivPanel1.setImageResource(R.mipmap.wash_3x);
                tvPanel1.setTextColor(activity.getResources().getColor(R.color.colorTextActive));
                ivPanel2.setImageResource(R.mipmap.toilet_dark_3x);
                tvPanel2.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel3.setImageResource(R.mipmap.shower_dark_3x);
                tvPanel3.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel4.setImageResource(R.mipmap.get_out_dark_3x);
                tvPanel4.setTextColor(activity.getResources().getColor(R.color.colorText));
                break;
            case 2:
                controlPanel(CONTROL_PROTOCOL_HDL,"panel","2");
                ivPanel1.setImageResource(R.mipmap.wash_dark_3x);
                tvPanel1.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel2.setImageResource(R.mipmap.toilet_3x);
                tvPanel2.setTextColor(activity.getResources().getColor(R.color.colorTextActive));
                ivPanel3.setImageResource(R.mipmap.shower_dark_3x);
                tvPanel3.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel4.setImageResource(R.mipmap.get_out_dark_3x);
                tvPanel4.setTextColor(activity.getResources().getColor(R.color.colorText));
                break;
            case 3:
                controlPanel(CONTROL_PROTOCOL_HDL,"panel","3");
                ivPanel1.setImageResource(R.mipmap.wash_dark_3x);
                tvPanel1.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel2.setImageResource(R.mipmap.toilet_dark_3x);
                tvPanel2.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel3.setImageResource(R.mipmap.shower_3x);
                tvPanel3.setTextColor(activity.getResources().getColor(R.color.colorTextActive));
                ivPanel4.setImageResource(R.mipmap.get_out_dark_3x);
                tvPanel4.setTextColor(activity.getResources().getColor(R.color.colorText));
                break;
            case 4:
                controlPanel(CONTROL_PROTOCOL_HDL,"panel","4");
                ivPanel1.setImageResource(R.mipmap.wash_dark_3x);
                tvPanel1.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel2.setImageResource(R.mipmap.toilet_dark_3x);
                tvPanel2.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel3.setImageResource(R.mipmap.shower_dark_3x);
                tvPanel3.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel4.setImageResource(R.mipmap.get_out_3x);
                tvPanel4.setTextColor(activity.getResources().getColor(R.color.colorTextActive));
                break;
        }
    }

    //设置点击场景后，面板图标变灰色
    private void setPanelIconColor(String roomDBName){
        switch (roomDBName){
            case "keting":
                ivPanel1.setImageResource(R.mipmap.entertainment_dark_3x);
                tvPanel1.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel2.setImageResource(R.mipmap.yule_mianban_dark_3x);
                tvPanel2.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel3.setImageResource(R.mipmap.music_dark_3x);
                tvPanel3.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel4.setImageResource(R.mipmap.get_out_dark_3x);
                tvPanel4.setTextColor(activity.getResources().getColor(R.color.colorText));
                break;
            case "woshi":
                ivPanel1.setImageResource(R.mipmap.read_dark_3x);
                tvPanel1.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel2.setImageResource(R.mipmap.game_dark_3x);
                tvPanel2.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel3.setImageResource(R.mipmap.music_dark_3x);
                tvPanel3.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel4.setImageResource(R.mipmap.get_out_dark_3x);
                tvPanel4.setTextColor(activity.getResources().getColor(R.color.colorText));
                break;
            case "ciwo":
                ivPanel1.setImageResource(R.mipmap.read_dark_3x);
                tvPanel1.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel2.setImageResource(R.mipmap.game_dark_3x);
                tvPanel2.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel3.setImageResource(R.mipmap.music_dark_3x);
                tvPanel3.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel4.setImageResource(R.mipmap.get_out_dark_3x);
                tvPanel4.setTextColor(activity.getResources().getColor(R.color.colorText));
                break;
            case "chufang":
                ivPanel1.setImageResource(R.mipmap.clean_up_dark_3x);
                tvPanel1.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel2.setImageResource(R.mipmap.cook_dark_3x);
                tvPanel2.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel3.setImageResource(R.mipmap.music_dark_3x);
                tvPanel3.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel4.setImageResource(R.mipmap.get_out_dark_3x);
                tvPanel4.setTextColor(activity.getResources().getColor(R.color.colorText));
                break;
            case "canting":
                ivPanel1.setImageResource(R.mipmap.dinner_dark_3x);
                tvPanel1.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel2.setImageResource(R.mipmap.snack_dark_3x);
                tvPanel2.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel3.setImageResource(R.mipmap.afternoon_tea_dark_3x);
                tvPanel3.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel4.setImageResource(R.mipmap.get_out_dark_3x);
                tvPanel4.setTextColor(activity.getResources().getColor(R.color.colorText));
                break;
            case "weiyu":
                ivPanel1.setImageResource(R.mipmap.wash_dark_3x);
                tvPanel1.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel2.setImageResource(R.mipmap.toilet_dark_3x);
                tvPanel2.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel3.setImageResource(R.mipmap.shower_dark_3x);
                tvPanel3.setTextColor(activity.getResources().getColor(R.color.colorText));
                ivPanel4.setImageResource(R.mipmap.get_out_dark_3x);
                tvPanel4.setTextColor(activity.getResources().getColor(R.color.colorText));
                break;

        }
    }

    //设置点击面板后，场景图标变灰色
    private void setSceneIconColor(){

    }



    //灯光亮度调节监听
    private boolean isLightOpen = false;
    LightValueScrollView.ScrollViewListenner hsChangeListener = new LightValueScrollView.ScrollViewListenner() {
        @Override
        public void onScrollChanged(LightValueScrollView view, int l, int t, int oldl, int oldt) {
            lightSVPosition = l;
            //Log.i(TAG, "onScrollChange: "+i);
            if(lightSVPosition<=lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*0.5){
                tvLightValue.setText("0");
                isLightOpen = false;
            }else if (lightSVPosition>lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*0.5 && lightSVPosition<=lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*1.5){
                tvLightValue.setText("5");
                isLightOpen = true;
            }else if (lightSVPosition>lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*1.5 && lightSVPosition<=lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*2.5){
                tvLightValue.setText("10");
                isLightOpen = true;
            }else if (lightSVPosition>lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*2.5 && lightSVPosition<=lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*3.5){
                tvLightValue.setText("15");
                isLightOpen = true;
            }else if (lightSVPosition>lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*3.5 && lightSVPosition<=lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*4.5){
                tvLightValue.setText("20");
                isLightOpen = true;
            }else if (lightSVPosition>lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*4.5 && lightSVPosition<=lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*5.5){
                tvLightValue.setText("25");
                isLightOpen = true;
            }else if (lightSVPosition>lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*5.5 && lightSVPosition<=lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*6.5){
                tvLightValue.setText("30");
                isLightOpen = true;
            }else if (lightSVPosition>lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*6.5 && lightSVPosition<=lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*7.5){
                tvLightValue.setText("35");
                isLightOpen = true;
            }else if (lightSVPosition>lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*7.5 && lightSVPosition<=lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*8.5){
                tvLightValue.setText("40");
                isLightOpen = true;
            }else if (lightSVPosition>lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*8.5 && lightSVPosition<=lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*9.5){
                tvLightValue.setText("45");
                isLightOpen = true;
            }else if (lightSVPosition>lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*9.5 && lightSVPosition<=lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*10.5){
                tvLightValue.setText("50");
                isLightOpen = true;
            }else if (lightSVPosition>lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*10.5 && lightSVPosition<=lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*11.5){
                tvLightValue.setText("55");
                isLightOpen = true;
            }else if (lightSVPosition>lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*11.5 && lightSVPosition<=lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*12.5){
                tvLightValue.setText("60");
                isLightOpen = true;
            }else if (lightSVPosition>lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*12.5 && lightSVPosition<=lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*13.5){
                tvLightValue.setText("65");
                isLightOpen = true;
            }else if (lightSVPosition>lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*13.5 && lightSVPosition<=lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*14.5){
                tvLightValue.setText("70");
                isLightOpen = true;
            }else if (lightSVPosition>lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*14.5 && lightSVPosition<=lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*15.5){
                tvLightValue.setText("75");
                isLightOpen = true;
            }else if (lightSVPosition>lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*15.5 && lightSVPosition<=lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*16.5){
                tvLightValue.setText("80");
                isLightOpen = true;
            }else if (lightSVPosition>lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*16.5 && lightSVPosition<=lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*17.5){
                tvLightValue.setText("85");
                isLightOpen = true;
            }else if (lightSVPosition>lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*17.5 && lightSVPosition<=lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*18.5){
                tvLightValue.setText("90");
                isLightOpen = true;
            }else if (lightSVPosition>lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*18.5 && lightSVPosition<=lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*19.5){
                tvLightValue.setText("95");
                isLightOpen = true;
            }else if (lightSVPosition>lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*19.5){
                tvLightValue.setText("100");
                isLightOpen = true;
            }
        }
    };

    /*View.OnScrollChangeListener hsChangeListener1 = new View.OnScrollChangeListener() {
        @Override
        public void onScrollChange(View view, int i, int i1, int i2, int i3) {
            lightSVPosition = i;
            //Log.i(TAG, "onScrollChange: "+i);
            if(lightSVPosition<=lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*0.5){
                tvLightValue.setText("0");
                isLightOpen = false;
            }else if (lightSVPosition>lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*0.5 && lightSVPosition<=lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*1.5){
                tvLightValue.setText("5");
                isLightOpen = true;
            }else if (lightSVPosition>lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*1.5 && lightSVPosition<=lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*2.5){
                tvLightValue.setText("10");
                isLightOpen = true;
            }else if (lightSVPosition>lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*2.5 && lightSVPosition<=lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*3.5){
                tvLightValue.setText("15");
                isLightOpen = true;
            }else if (lightSVPosition>lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*3.5 && lightSVPosition<=lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*4.5){
                tvLightValue.setText("20");
                isLightOpen = true;
            }else if (lightSVPosition>lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*4.5 && lightSVPosition<=lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*5.5){
                tvLightValue.setText("25");
                isLightOpen = true;
            }else if (lightSVPosition>lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*5.5 && lightSVPosition<=lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*6.5){
                tvLightValue.setText("30");
                isLightOpen = true;
            }else if (lightSVPosition>lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*6.5 && lightSVPosition<=lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*7.5){
                tvLightValue.setText("35");
                isLightOpen = true;
            }else if (lightSVPosition>lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*7.5 && lightSVPosition<=lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*8.5){
                tvLightValue.setText("40");
                isLightOpen = true;
            }else if (lightSVPosition>lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*8.5 && lightSVPosition<=lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*9.5){
                tvLightValue.setText("45");
                isLightOpen = true;
            }else if (lightSVPosition>lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*9.5 && lightSVPosition<=lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*10.5){
                tvLightValue.setText("50");
                isLightOpen = true;
            }else if (lightSVPosition>lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*10.5 && lightSVPosition<=lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*11.5){
                tvLightValue.setText("55");
                isLightOpen = true;
            }else if (lightSVPosition>lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*11.5 && lightSVPosition<=lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*12.5){
                tvLightValue.setText("60");
                isLightOpen = true;
            }else if (lightSVPosition>lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*12.5 && lightSVPosition<=lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*13.5){
                tvLightValue.setText("65");
                isLightOpen = true;
            }else if (lightSVPosition>lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*13.5 && lightSVPosition<=lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*14.5){
                tvLightValue.setText("70");
                isLightOpen = true;
            }else if (lightSVPosition>lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*14.5 && lightSVPosition<=lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*15.5){
                tvLightValue.setText("75");
                isLightOpen = true;
            }else if (lightSVPosition>lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*15.5 && lightSVPosition<=lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*16.5){
                tvLightValue.setText("80");
                isLightOpen = true;
            }else if (lightSVPosition>lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*16.5 && lightSVPosition<=lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*17.5){
                tvLightValue.setText("85");
                isLightOpen = true;
            }else if (lightSVPosition>lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*17.5 && lightSVPosition<=lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*18.5){
                tvLightValue.setText("90");
                isLightOpen = true;
            }else if (lightSVPosition>lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*18.5 && lightSVPosition<=lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*19.5){
                tvLightValue.setText("95");
                isLightOpen = true;
            }else if (lightSVPosition>lightAxisinitValue+(lightValueHeight1+lightValueHeight2)*19.5){
                tvLightValue.setText("100");
                isLightOpen = true;
            }
        }
    };*/

    //空调温度调节滑块监听
    SeekBar.OnSeekBarChangeListener tempSeekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            if (i<10){
                tvKongtiaoTemp.setText("16°");
                tvKongtiaoTemp.setTextColor(activity.getResources().getColor(R.color.colorPrimary));
            }else if (i>=10&&i<20){
                tvKongtiaoTemp.setText("17°");
                tvKongtiaoTemp.setTextColor(activity.getResources().getColor(R.color.colorPrimary));
            }else if (i>=20&&i<30){
                tvKongtiaoTemp.setText("18°");
                tvKongtiaoTemp.setTextColor(activity.getResources().getColor(R.color.colorBule));
            }else if (i>=30&&i<40){
                tvKongtiaoTemp.setText("19°");
                tvKongtiaoTemp.setTextColor(activity.getResources().getColor(R.color.colorBule));
            }else if (i>=40&&i<50){
                tvKongtiaoTemp.setText("20°");
                tvKongtiaoTemp.setTextColor(activity.getResources().getColor(R.color.colorBule));
            }else if (i>=50&&i<60){
                tvKongtiaoTemp.setText("21°");
                tvKongtiaoTemp.setTextColor(activity.getResources().getColor(R.color.colorGreen));
            }else if (i>=60&&i<70){
                tvKongtiaoTemp.setText("22°");
                tvKongtiaoTemp.setTextColor(activity.getResources().getColor(R.color.colorGreen));
            }else if (i>=70&&i<80){
                tvKongtiaoTemp.setText("23°");
                tvKongtiaoTemp.setTextColor(activity.getResources().getColor(R.color.colorGreen));
            }else if (i>=80&&i<90){
                tvKongtiaoTemp.setText("24°");
                tvKongtiaoTemp.setTextColor(activity.getResources().getColor(R.color.colorYellowGreen));
            }else if (i>=90&&i<100){
                tvKongtiaoTemp.setText("25°");
                tvKongtiaoTemp.setTextColor(activity.getResources().getColor(R.color.colorYellowGreen));
            }else if (i>=100&&i<110){
                tvKongtiaoTemp.setText("26°");
                tvKongtiaoTemp.setTextColor(activity.getResources().getColor(R.color.colorYellowGreen));
            }else if (i>=110&&i<120){
                tvKongtiaoTemp.setText("27°");
                tvKongtiaoTemp.setTextColor(activity.getResources().getColor(R.color.colorYellow));
            }else if (i>=120&&i<130){
                tvKongtiaoTemp.setText("28°");
                tvKongtiaoTemp.setTextColor(activity.getResources().getColor(R.color.colorYellow));
            }else if (i>=130&&i<140){
                tvKongtiaoTemp.setText("29°");
                tvKongtiaoTemp.setTextColor(activity.getResources().getColor(R.color.colorTextActive));
            }else if (i>=140){
                tvKongtiaoTemp.setText("30°");
                tvKongtiaoTemp.setTextColor(activity.getResources().getColor(R.color.colorTextActive));
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
    

    //初始化背景图片
    private void initBackGround(){
        //随机取出一张图片
        Bitmap bitmap = readBitMap(getActivity(),backList[random1.nextInt(backList.length)]);
        ivBackGround.setImageBitmap(bitmap);
        Bitmap bitmap1 = readBitMap(getActivity(),backList[random1.nextInt(backList.length)]);
        ivBackGroundTrans.setImageBitmap(bitmap1);

        //开线程  隔一段时间切换图片
        BGPThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isFragmemtFront==true){
                    try {
                        Thread.sleep(60000);
                        Message message = new Message();
                        message.what = 1;
                        handler.sendMessage(message);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        BGPThread.start();
    }

    private void initBackPicture(){
        Bitmap bitmap = readBitMap(getActivity(),backList[roomIndex]);
        ivBackGround.setImageBitmap(bitmap);
    }



    //设置字体
    private void setFonts(){
        for (int i=0;i<tvList.size();i++){
            tvList.get(i).setTypeface(typeFace);
        }
    }

    //设置切换图片的动画
    private int BGPFlag = 0;
    private void setBGPAnimation(){

        if (BGPFlag==0){
            ObjectAnimator animator1 = new ObjectAnimator().ofFloat(ivBackGround,"alpha",1f,0f).setDuration(1000);
            animator1.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {          //动画结束的监听
                    super.onAnimationEnd(animation);
                    Bitmap bitmap = readBitMap(activity,backList[random1.nextInt(backList.length)]);
                    ivBackGround.setImageBitmap(bitmap);
                }
            });
            animator1.start();

            ObjectAnimator.ofFloat(ivBackGroundTrans,"alpha",0f,1f).setDuration(1000).start();

            BGPFlag = 1;
        }else if (BGPFlag==1){
            ObjectAnimator.ofFloat(ivBackGround,"alpha",0f,1f).setDuration(1000).start();
            ObjectAnimator animator2 = new ObjectAnimator().ofFloat(ivBackGroundTrans,"alpha",1f,0f).setDuration(1000);
            animator2.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {          //动画结束的监听
                    super.onAnimationEnd(animation);
                    Bitmap bitmap = readBitMap(activity,backList[random1.nextInt(backList.length)]);
                    ivBackGroundTrans.setImageBitmap(bitmap);
                }
            });
            animator2.start();
            BGPFlag = 0;
        }
    }

    /**
     * 以最省内存的方式读取本地资源的图片
     * @param context
     * @param resId
     * @return
     */
    public static Bitmap readBitMap(Context context, int resId){
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        //获取资源图片
        InputStream is = context.getResources().openRawResource(resId);          //在锤子上测试，时间长了 java.io.FileNotFoundException
        return BitmapFactory.decodeStream(is,null,opt);
    }

    //---------------------设置点击按钮的缩放效果动画---------------------
    private void setScaleAnimation(View view){
        ObjectAnimator.ofFloat(view,"scaleX",1f,0.8f,1f).setDuration(600).start();
        ObjectAnimator.ofFloat(view,"scaleY",1f,0.8f,1f).setDuration(600).start();
    }
    private void setCurtainStopAnimation(View view){
        controlCurtain(CONTROL_PROTOCOL_HDL,MACHINE_NAME_CURTAIN,-1,1,-1,"all_both_pause");
        changeColorStop(R.mipmap.theme2_chuanglian_zanting_active_3x,R.color.colorTextActive);
        ObjectAnimator.ofFloat(view,"scaleX",1f,0.8f,1f).setDuration(600).start();
        ObjectAnimator objectAnimator = new ObjectAnimator().ofFloat(view,"scaleY",1f,0.8f,1f).setDuration(600);
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                changeColorStop(R.mipmap.theme2_chuanglian_zanting_3x,R.color.colorText);
            }
        });
        objectAnimator.start();
    }
    private void setCurtainJuanStopAnimation(View view){
        changeColorStopJuan(R.mipmap.theme2_chuanglian_zanting_active_3x,R.color.colorTextActive);
        ObjectAnimator.ofFloat(view,"scaleX",1f,0.8f,1f).setDuration(600).start();
        ObjectAnimator objectAnimator =new ObjectAnimator().ofFloat(view,"scaleY",1f,0.8f,1f).setDuration(600);
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                changeColorStopJuan(R.mipmap.theme2_chuanglian_zanting_3x,R.color.colorText);
            }
        });
        objectAnimator.start();
    }

    /*
    * ----------------------------按钮的点击动画-------------------------------
    * */
    //场景  -----------休闲-----------
    private void animationXiuxian(){
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator objectAnimatorOut = new ObjectAnimator().ofFloat(ivXiuxianOut,"rotation",0,-10,0);
        ivXiuxianOut.setPivotX(0f);
        ivXiuxianOut.setPivotY(100f);

        ObjectAnimator objectAnimatorIn = new ObjectAnimator().ofFloat(ivXiuxianIn,"rotation",0,-10,0);
        ivXiuxianIn.setPivotX(0f);
        ivXiuxianIn.setPivotY(100f);
        set.playTogether(objectAnimatorIn,objectAnimatorOut);
        set.setDuration(1500).start();
    }
    //场景 ------------娱乐-------------
    private void animationYule(){
        AnimatorSet set = new AnimatorSet();
    }

    //窗帘 -------------布帘---------------
    private void animationBulianOpen(){
        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(ivBulian1,"translationX",0,-5),
                ObjectAnimator.ofFloat(ivBulian2,"translationX",0,-10),
                ObjectAnimator.ofFloat(ivBulian3,"translationX",0,-15),
                ObjectAnimator.ofFloat(ivBulian4,"translationX",0,15),
                ObjectAnimator.ofFloat(ivBulian5,"translationX",0,10),
                ObjectAnimator.ofFloat(ivBulian6,"translationX",0,5)
        );
        set.setDuration(1000).start();
    }
    private void animationBulianClose(){
        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(ivBulian1,"translationX",5,0),
                ObjectAnimator.ofFloat(ivBulian2,"translationX",10,0),
                ObjectAnimator.ofFloat(ivBulian3,"translationX",15,0),
                ObjectAnimator.ofFloat(ivBulian4,"translationX",-15,0),
                ObjectAnimator.ofFloat(ivBulian5,"translationX",-10,0),
                ObjectAnimator.ofFloat(ivBulian6,"translationX",-5,0)
        );
        set.setDuration(1000).start();
    }


    //头部控件的点击事件
    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.bt_shezhi:
                    EventBus.getDefault().post(new MyEventBus("4"));
                    break;
                case R.id.bt_jiankong:
                    //延迟250毫秒跳转监控页面
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            EZOpenSDK.getInstance().openLoginPage();
                            //EventBus.getDefault().post(new MyEventBus("jiankong"));
                        }
                    },250);

                    break;
                case R.id.bt_mensuo:
                    EventBus.getDefault().post(new MyEventBus("5"));
                    break;
                case R.id.bt_bufang:
                    EventBus.getDefault().post(new MyEventBus("6"));
                    break;
            }
        }
    };

    //自定义设置的下拉菜单监听
    public void pullDown(boolean f,boolean isIconShow){
        pullDownMenuListener.pullDown(f,isIconShow);
    }
    public void scrollPager(){
        pullDownMenuListener.scrollPager();
    }

    public void setPullDownMenuListener(PullDownMenuListener pullDownMenuListener){
        this.pullDownMenuListener = pullDownMenuListener;
    }

    //上拉菜单滑动监听
    ScrollViewListener pullDownListener = new ScrollViewListener() {
        @Override
        public void onScrollChanged(MyPullUpScrollView scrollView, int x, int y, int oldx, int oldy) {
            int hideHeight = (initFloatHeight*2)+btHeight_X3;    //控件隐藏的临界点

            //加入手势，松开的时候 判断距离，选择菜单栏出现还是隐藏的动画
            scrollY = y;
            //Log.i("result", "onScrollChanged:-----------x-----------"+x+"----------y---------"+y);
            //Log.i(TAG, "onScrollChanged: -------scrollY--------"+scrollY);
            //判断向下滑的时候(y<控件的高度)    button消失
            if (scrollY>=btHeight_X3) {           //乘以三是因为手指移动和滑动是三倍率
                btPullMenu.setVisibility(View.VISIBLE);
                //homeButtonSetVisableAnimator();
                pullDown(true,true);
                //ObjectAnimator.ofFloat(btPullMenu,"alpha",0f,1f).setDuration(400).start();
            }else if (scrollY<btHeight_X3){
                btPullMenu.setVisibility(View.GONE);
                //homeButtonSetGoneAnimator();
                pullDown(true,false);
            }
            //向上滑动到一大半时，隐藏桌面两个小控件，房间名。
            if (scrollY<btHeight_X3){
                rlRoomName.setAlpha(1f);
                ivHomeIcon.setAlpha(1f);
                tvRoomNameTop.setAlpha(0f);
                //pullDown(false,true);
            }else if (scrollY<hideHeight && scrollY>=btHeight_X3){
                rlRoomName.setAlpha(1f);
                ivHomeIcon.setAlpha(1f);
                tvRoomNameTop.setAlpha(0f);
                pullDown(false,true);
            }else if (scrollY>=hideHeight && scrollY<hideHeight+10){
                rlRoomName.setAlpha(0.9f);
                ivHomeIcon.setAlpha(0.9f);
                tvRoomNameTop.setAlpha(0.1f);
            }else if (scrollY>=hideHeight+10 && scrollY<hideHeight+20){
                rlRoomName.setAlpha(0.8f);
                ivHomeIcon.setAlpha(0.8f);
                tvRoomNameTop.setAlpha(0.2f);
            }else if (scrollY>=hideHeight+20 && scrollY<hideHeight+30){
                rlRoomName.setAlpha(0.7f);
                ivHomeIcon.setAlpha(0.7f);
                tvRoomNameTop.setAlpha(0.3f);
            }else if (scrollY>=hideHeight+30 && scrollY<hideHeight+40){
                rlRoomName.setAlpha(0.6f);
                ivHomeIcon.setAlpha(0.6f);
                tvRoomNameTop.setAlpha(0.4f);
            }else if (scrollY>=hideHeight+40 && scrollY<hideHeight+50){
                rlRoomName.setAlpha(0.5f);
                ivHomeIcon.setAlpha(0.5f);
                tvRoomNameTop.setAlpha(0.5f);
            }else if (scrollY>=hideHeight+50 && scrollY<hideHeight+60){
                rlRoomName.setAlpha(0.4f);
                ivHomeIcon.setAlpha(0.4f);
                tvRoomNameTop.setAlpha(0.6f);
            }else if (scrollY>=hideHeight+60 && scrollY<hideHeight+70){
                rlRoomName.setAlpha(0.3f);
                ivHomeIcon.setAlpha(0.3f);
                tvRoomNameTop.setAlpha(0.7f);
            }else if (scrollY>=hideHeight+70 && scrollY<hideHeight+80){
                rlRoomName.setAlpha(0.2f);
                ivHomeIcon.setAlpha(0.2f);
                tvRoomNameTop.setAlpha(0.8f);
            }else if (scrollY>=hideHeight+80 && scrollY<hideHeight+90){
                rlRoomName.setAlpha(0.1f);
                ivHomeIcon.setAlpha(0.1f);
                tvRoomNameTop.setAlpha(0.9f);
            }else if (scrollY>=hideHeight+90 && scrollY<hideHeight+100){
                rlRoomName.setAlpha(0.05f);
                ivHomeIcon.setAlpha(0.05f);
                tvRoomNameTop.setAlpha(0.95f);
            }else if (scrollY>=hideHeight+100){
                rlRoomName.setAlpha(0f);
                ivHomeIcon.setAlpha(0f);
                tvRoomNameTop.setAlpha(1f);
                pullDown(true,false);
            }
        }

        @Override
        public void onCommOnTouchEvent(MyPullUpScrollView scrollView, MotionEvent ev) {
            int action = ev.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    y = ev.getY();
                    break;
                case MotionEvent.ACTION_UP:

                    break;
                case MotionEvent.ACTION_MOVE:
                    final float preY = y;
                    float nowY = ev.getY();
                    int deltaY = (int) (preY - nowY) / size;
                    int yy = linear.getTop() - deltaY;
                    break;
                default:
                    break;
            }
        }
    };


    //获取状态栏statusBar的高度
    private int getStatusBar(){
        Resources resources = getActivity().getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen","android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

    //点击事件
    boolean flag = true;
    View.OnClickListener pullMenuListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            isCanScroll = false;
            //隐藏button
            btPullMenu.setVisibility(View.GONE);
            //homeButtonSetGoneAnimator();
            //弹出菜单     smoothScroll
            if (flag==true){
                svPullUpMenu.smoothScrollToSlow(0,-btHeight_X3,1600);
                flag = false;
                pullDown(false,false);
            }
            if (isInitPosition==false){
                svPullUpMenu.smoothScrollToSlow(0,btHeight_X3,1000);
                flag = true;
                pullDown(true,true);
            }

        }
    };



    //滑动到隐藏头 初始化
    private void scrollToBottomInit(){
        isInitPosition = true;
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                svPullUpMenu.scrollTo(0,btHeight_X3);
                scrollView.scrollTo(0,btHeight);
                flag = true;
                btPullMenu.setVisibility(View.VISIBLE);
            }
        });
    }
    //滑动到隐藏头
    private void scrollToBottom(){
        //Log.i("scroll_animation", "scrollToBottom: ==============能显示动画吗");
        isInitPosition = true;
        svPullUpMenu.smoothScrollToSlow(0,btHeight_X3,400);
        //svPullUpMenu.smoothScrollTo(0,btHeight_X3);
        flag = true;
        btPullMenu.setVisibility(View.VISIBLE);
        //homeButtonSetVisableAnimator();
    }
    //滑动到显示头
    private void scrollToTop(){
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                svPullUpMenu.smoothScrollTo(0,-(btHeight_X3));
                flag = false;
            }
        });
    }

    //homeButton的出现和隐藏动画
    private void homeButtonSetVisableAnimator(){
        btPullMenu.setVisibility(View.VISIBLE);
        ObjectAnimator.ofFloat(btPullMenu,"alpha",0f,1f).setDuration(300).start();
    }
    private void homeButtonSetGoneAnimator(){
        ObjectAnimator objectAnimator = new ObjectAnimator().ofFloat(btPullMenu,"alpha",1f,0f).setDuration(300);
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                btPullMenu.setVisibility(View.GONE);
            }
        });
        objectAnimator.start();
    }

    //两个ScrollView联动
    @Override
    public void onScrollConnection(MyPullUpScrollView scrollView0, int x, int y, int oldx, int oldy) {
        scrollView.scrollTo(x,y);
    }

    @Override
    public void onScrollConnectionDown(MyCustomScrollView scrollView, int x, int y, int oldx, int oldy) {
        svPullUpMenu.scrollTo(x,y);
    }


    /*
     *    ---------------------------------点击图标变色---------------------------------
     */
    //---------------------------------------场景-----------------------------------------
    private void changeColorToActive1(int imgId1,int imgId2,int colorId){
        ivXiuxianOut.setImageResource(imgId1);
        ivXiuxianIn.setImageResource(imgId2);
        tvXiuxian.setTextColor(activity.getResources().getColor(colorId));
    }
    private void changeColorToActive2(int imgId1,int imgId2,int imgId3,int colorId){
        ivYule1.setImageResource(imgId1);
        ivYule2.setImageResource(imgId2);
        ivYule3.setImageResource(imgId3);
        tvYule.setTextColor(activity.getResources().getColor(colorId));
    }
    private void changeColorToActive3(int imgId1,int imgId2,int colorId){
        ivJuhui1.setImageResource(imgId1);
        ivJuhui2.setImageResource(imgId2);
        tvJuhui.setTextColor(activity.getResources().getColor(colorId));
    }
    private void changeColorToActive4(int imgId1,int colorId){
        ivLikai.setImageResource(imgId1);
        tvLikai.setTextColor(activity.getResources().getColor(colorId));
    }


    //点击事件
    private void clickRoomName(){
        EventBus.getDefault().post(new MyEventBus(SWITCH_ROOM_DIALOG));
    }
    private void clickXiuxian(){
        changeColorToActive1(R.mipmap.theme2_jiating_xiuxian_active_ani_3x,R.mipmap.theme2_jiating_xiuxian_active_ani_in_3x,R.color.colorTextActive);
        changeColorToActive2(R.mipmap.theme2_jiating_yule_1_3x,R.mipmap.theme2_jiating_yule_2_3x,R.mipmap.theme2_jiating_yule_3_3x,R.color.colorText);
        changeColorToActive3(R.mipmap.theme2_jiating_juhui_ani_3x,R.mipmap. theme2_jiating_juhui_ani_in_3x,R.color.colorText);
        changeColorToActive4(R.mipmap.theme2_jiating_likai_ani_3x,R.color.colorText);
    }
    private void clickYule(){
        changeColorToActive1(R.mipmap.theme2_jiating_xiuxian_ani_3x,R.mipmap.theme2_jiating_xiuxian_ani_in_3x,R.color.colorText);
        changeColorToActive2(R.mipmap.theme2_jiating_yule_active_1_3x,R.mipmap.theme2_jiating_yule_active_2_3x,R.mipmap.theme2_jiating_yule_active_3_3x,R.color.colorTextActive);
        changeColorToActive3(R.mipmap.theme2_jiating_juhui_ani_3x,R.mipmap.theme2_jiating_juhui_ani_in_3x,R.color.colorText);
        changeColorToActive4(R.mipmap.theme2_jiating_likai_ani_3x,R.color.colorText);
    }
    private void clickJuhui(){
        changeColorToActive1(R.mipmap.theme2_jiating_xiuxian_ani_3x,R.mipmap.theme2_jiating_xiuxian_ani_in_3x,R.color.colorText);
        changeColorToActive2(R.mipmap.theme2_jiating_yule_1_3x,R.mipmap.theme2_jiating_yule_2_3x,R.mipmap.theme2_jiating_yule_3_3x,R.color.colorText);
        changeColorToActive3(R.mipmap.theme2_jiating_juhui_active_ani_3x,R.mipmap.theme2_jiating_juhui_active_ani_in_3x,R.color.colorTextActive);
        changeColorToActive4(R.mipmap.theme2_jiating_likai_ani_3x,R.color.colorText);
    }
    private void clickLikai(){
        changeColorToActive1(R.mipmap.theme2_jiating_xiuxian_ani_3x,R.mipmap.theme2_jiating_xiuxian_ani_in_3x,R.color.colorText);
        changeColorToActive2(R.mipmap.theme2_jiating_yule_1_3x,R.mipmap.theme2_jiating_yule_2_3x,R.mipmap.theme2_jiating_yule_3_3x,R.color.colorText);
        changeColorToActive3(R.mipmap.theme2_jiating_juhui_ani_3x,R.mipmap.theme2_jiating_juhui_ani_in_3x,R.color.colorText);
        changeColorToActive4(R.mipmap.theme2_jiating_likai_active_ani_3x,R.color.colorTextActive);
    }
    private void clickLightSwitch(){
        EventBus.getDefault().post(new MyEventBus(""));

    }

    //----------------------------------窗帘-----------------------------------
    private void changeColorBulian(int imgIdHead,int imgId1,int colorId,String text){
        ivBulianHead.setImageResource(imgIdHead);
        ivBulian1.setImageResource(imgId1);
        ivBulian2.setImageResource(imgId1);
        ivBulian3.setImageResource(imgId1);
        ivBulian4.setImageResource(imgId1);
        ivBulian5.setImageResource(imgId1);
        ivBulian6.setImageResource(imgId1);
        ivBulianHead1.setImageResource(imgIdHead);
        tvBulian.setTextColor(activity.getResources().getColor(colorId));
        tvBulian.setText(text);
    }
    private void changeColorShalian(int imgIdHead,int imgId1,int colorId,String text){
        ivShalianHead.setImageResource(imgIdHead);
        ivShalian1.setImageResource(imgId1);
        ivShalian2.setImageResource(imgId1);
        ivShalian3.setImageResource(imgId1);
        ivShalian4.setImageResource(imgId1);
        ivShalian5.setImageResource(imgId1);
        ivShalian6.setImageResource(imgId1);
        ivShalianHead1.setImageResource(imgIdHead);
        tvShalian.setTextColor(activity.getResources().getColor(colorId));
        tvShalian.setText(text);
    }
    private void changeColorAll(int imgId1,int colorId,String text){
        ivAll.setImageResource(imgId1);
        tvAll.setTextColor(activity.getResources().getColor(colorId));
        tvAll.setText(text);
    }
    private void changeColorStop(int imgId1,int colorId){
        ivStop.setImageResource(imgId1);
        tvStop.setTextColor(activity.getResources().getColor(colorId));
    }

    private void changeColorJuanlian(int imgIdHead,int imgId1,int colorId,String text){
        ivJuanlianHead.setImageResource(imgIdHead);
        ivJuanlian1.setImageResource(imgId1);
        ivJuanlian2.setImageResource(imgId1);
        ivJuanlian3.setImageResource(imgId1);
        ivJuanlian4.setImageResource(imgId1);
        ivJuanlian5.setImageResource(imgId1);
        ivJuanlian6.setImageResource(imgId1);
        ivJuanlianHead1.setImageResource(imgIdHead);
        tvJuanlian.setTextColor(activity.getResources().getColor(colorId));
        tvJuanlian.setText(text);
    }
    private void changeColorJuanlianR(int imgIdHead,int imgId1,int colorId,String text){
        ivJuanlianHeadR.setImageResource(imgIdHead);
        ivJuanlian1R.setImageResource(imgId1);
        ivJuanlian2R.setImageResource(imgId1);
        ivJuanlian3R.setImageResource(imgId1);
        ivJuanlian4R.setImageResource(imgId1);
        ivJuanlian5R.setImageResource(imgId1);
        ivJuanlian6R.setImageResource(imgId1);
        ivJuanlianHead1R.setImageResource(imgIdHead);
        tvJuanlianR.setTextColor(activity.getResources().getColor(colorId));
        tvJuanlianR.setText(text);
    }
    private void changeColorAllJuan(int imgId1,int colorId,String text){
        ivAllJuan.setImageResource(imgId1);
        tvAllJuan.setTextColor(activity.getResources().getColor(colorId));
        tvAllJuan.setText(text);
    }
    private void changeColorStopJuan(int imgId1,int colorId){
        ivStopJuan.setImageResource(imgId1);
        tvStopJuan.setTextColor(activity.getResources().getColor(colorId));
    }

    //点击
    private int bulianFlag = 0;
    private int shalianFlag = 0;
    private int AllFlag = 0;
    private int StopFlag = 0;

    private int juanlianFlag = 0;
    private int juanlianRFlag = 0;
    private int AllFlagJuan = 0;
    private int StopFlagJuan = 0;

    private void clickBulian(){
        if (bulianFlag==0){
            //animationBulianOpen();   //开窗动画
            changeColorBulian(R.mipmap.theme2_chuanglian_head_active_3x,R.mipmap.theme2_chuanglian_solidline_active_3x,R.color.colorTextActive,"布帘 开");
            controlCurtain(CONTROL_PROTOCOL_HDL,MACHINE_NAME_CURTAIN,-1,1,-1,"1_right_open");
            bulianFlag = 1;
        }else if (bulianFlag==1){
            //animationBulianClose();   //关窗动画
            changeColorBulian(R.mipmap.theme2_chuanglian_head_3x,R.mipmap.chuanglian_solidline_3,R.color.colorText,"布帘 关");
            controlCurtain(CONTROL_PROTOCOL_HDL,MACHINE_NAME_CURTAIN,-1,1,-1,"1_right_close");
            bulianFlag = 0;
        }
    }
    private void clickShalian(){
        if (shalianFlag==0){
            changeColorShalian(R.mipmap.theme2_chuanglian_head_active_3x,R.mipmap.theme2_chuanglian_viualline_active_3x,R.color.colorTextActive,"纱帘 开");
            controlCurtain(CONTROL_PROTOCOL_HDL,MACHINE_NAME_CURTAIN,-1,1,-1,"1_left_open");
            shalianFlag = 1;
        }else if (shalianFlag==1){
            changeColorShalian(R.mipmap.theme2_chuanglian_head_3x,R.mipmap.theme2_chuanglian_viualline_3x,R.color.colorText,"纱帘 关");
            controlCurtain(CONTROL_PROTOCOL_HDL,MACHINE_NAME_CURTAIN,-1,1,-1,"1_left_close");
            shalianFlag = 0;
        }
    }
    private void clickAll(){
        if (AllFlag==0){
            controlCurtain(CONTROL_PROTOCOL_HDL,MACHINE_NAME_CURTAIN,-1,1,-1,"all_both_open");
            changeColorAll(R.mipmap.theme2_chuanglian_quanguan_open_3x,R.color.colorTextActive,"全 开");
            changeColorBulian(R.mipmap.theme2_chuanglian_head_active_3x,R.mipmap.theme2_chuanglian_solidline_active_3x,R.color.colorTextActive,"布帘 开");
            bulianFlag = 1;
            changeColorShalian(R.mipmap.theme2_chuanglian_head_active_3x,R.mipmap.theme2_chuanglian_viualline_active_3x,R.color.colorTextActive,"纱帘 开");
            shalianFlag = 1;
            AllFlag = 1;
        }else if (AllFlag==1){
            controlCurtain(CONTROL_PROTOCOL_HDL,MACHINE_NAME_CURTAIN,-1,1,-1,"all_both_close");
            changeColorAll(R.mipmap.theme2_chuanglian_quanguan_close_3x,R.color.colorText,"全 关");
            AllFlag = 0;
            changeColorBulian(R.mipmap.theme2_chuanglian_head_3x,R.mipmap.chuanglian_solidline_3,R.color.colorText,"布帘 关");
            bulianFlag = 0;
            changeColorShalian(R.mipmap.theme2_chuanglian_head_3x,R.mipmap.theme2_chuanglian_viualline_3x,R.color.colorText,"纱帘 关");
            shalianFlag = 0;
        }
    }

    private void clickJuanlian(){
        if (juanlianFlag==0){
            changeColorJuanlian(R.mipmap.theme2_chuanglian_head_active_3x,R.mipmap.theme2_juanlian_solidline_active_3x,R.color.colorTextActive,"左卷帘 开");
            juanlianFlag = 1;
        }else if (juanlianFlag==1){
            changeColorJuanlian(R.mipmap.theme2_chuanglian_head_3x,R.mipmap.theme2_juanlian_solidline_3x,R.color.colorText,"左卷帘 关");
            juanlianFlag = 0;
        }
    }
    private void clickJuanlianR(){
        if (juanlianRFlag==0){
            changeColorJuanlianR(R.mipmap.theme2_chuanglian_head_active_3x,R.mipmap.theme2_juanlian_solidline_active_3x,R.color.colorTextActive,"右卷帘 开");
            juanlianRFlag = 1;
        }else if (juanlianRFlag==1){
            changeColorJuanlianR(R.mipmap.theme2_chuanglian_head_3x,R.mipmap.theme2_juanlian_solidline_3x,R.color.colorText,"右卷帘 关");
            juanlianRFlag = 0;
        }
    }
    private void clickAllJuan(){
        if (AllFlagJuan==0){
            changeColorAllJuan(R.mipmap.theme2_chuanglian_quanguan_open_3x,R.color.colorTextActive,"全 开");
            AllFlagJuan = 1;
            changeColorJuanlian(R.mipmap.theme2_chuanglian_head_active_3x,R.mipmap.theme2_juanlian_solidline_active_3x,R.color.colorTextActive,"左卷帘 开");
            juanlianFlag = 1;
            changeColorJuanlianR(R.mipmap.theme2_chuanglian_head_active_3x,R.mipmap.theme2_juanlian_solidline_active_3x,R.color.colorTextActive,"右卷帘 开");
            juanlianRFlag = 1;
        }else if (AllFlagJuan==1){
            changeColorAllJuan(R.mipmap.theme2_chuanglian_quanguan_close_3x,R.color.colorText,"全 关");
            AllFlagJuan = 0;
            changeColorJuanlian(R.mipmap.theme2_chuanglian_head_3x,R.mipmap.theme2_juanlian_solidline_3x,R.color.colorText,"左卷帘 关");
            juanlianFlag = 0;
            changeColorJuanlianR(R.mipmap.theme2_chuanglian_head_3x,R.mipmap.theme2_juanlian_solidline_3x,R.color.colorText,"右卷帘 关");
            juanlianRFlag = 0;
        }
    }




    //----------------------------------------空调-----------------------------------------
    private void changeColorAirSwitch(int imgId,int colorId,String text){
        ivKongtiaoSwitch.setImageResource(imgId);
        tvKongtiaoSwitch.setTextColor(activity.getResources().getColor(colorId));
        tvKongtiaoSwitch.setText(text);
    }
    //点击
    private int airSwitchFlag = 0;
    private void clickAirSwitch(){
        isCanScroll = false;    //点击后设置为不可触摸滑动scrollView
        if (airSwitchFlag==0){
            changeColorAirSwitch(R.mipmap.theme2_kongtiao_fan_active_3x,R.color.colorTextActive,"空调 开");
            linearAirDetails.setVisibility(View.VISIBLE);
            int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            linearAirDetails.measure(w, h);
            AirConditionHeight = linearAirDetails.getMeasuredHeight();         //空调控制部分的高度
            Log.i(TAG, "onClick: ---------------------------"+AirConditionHeight);
            svPullUpMenu.smoothScrollBySlow(0,AirConditionHeight,2000);

            /*Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    svPullUpMenu.smoothScrollBySlow(0,AirConditionHeight,2000);
                }
            });*/

            //设置点击之后2秒内不能点击
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                        rlKongtiao.setClickable(true);
                        Message message = new Message();
                        message.what = 2;
                        handlerAir.sendMessage(message);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            airSwitchFlag = 1;
        }else if (airSwitchFlag==1){
            changeColorAirSwitch(R.mipmap.theme2_kongtiao_fan_3x,R.color.colorText,"空调 关");
            svPullUpMenu.smoothScrollBySlow(0,-AirConditionHeight,2000);


            //开个线程，睡2秒，隐藏空调调温部分
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                        rlKongtiao.setClickable(true);
                        Message message = new Message();
                        message.what = 1;
                        handlerAir.sendMessage(message);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            airSwitchFlag = 0;
        }
    }

    //-----------------根据不同的用户信息，使某些模块设置为GONE----------------
    private void initRoomAttr(TextView tv,View view){
        tv.setVisibility(View.GONE);
        view.setVisibility(View.GONE);
    }


    /**
     *    发送tcpSocket，控制部分的封装
     * @param controlProtocol  产品名称  hdl..
     * @param machineName      模块名称  light..
     * @param //value          需要设定的值  100..
     * @param //isServerAUTO     是否自动   0..
     * @param controlSence     控制信号   all..
     */
    private void controlPanel(String controlProtocol, String machineName,String controlSence) {
        JSONObject CommandData = new JSONObject();
        JSONObject controlData = new JSONObject();
        try {
            CommandData.put("controlProtocol", controlProtocol);
            CommandData.put("machineName", machineName);
            CommandData.put("controlData", controlData);
            controlData.put("", "");
            controlData.put("", "");
            CommandData.put("controlSence", controlSence);
            CommandData.put("houseDBName", roomDBName);
            String lightJson = CommandJsonUtils.getCommandJson(0, CommandData, token, uname, pwd, String.valueOf(System.currentTimeMillis()));
            EventBus.getDefault().post(new MyEventBus(lightJson));
            Log.i(TAG, "onClick: ------lightjson------" + lightJson);
        } catch (JSONException e) {
            e.printStackTrace();
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
            CommandData.put("houseDBName", roomDBName);
            String lightJson = CommandJsonUtils.getCommandJson(0, CommandData, token, uname, pwd, String.valueOf(System.currentTimeMillis()));
            EventBus.getDefault().post(new MyEventBus(lightJson));
            Log.i(TAG, "onClick: ------lightjson------" + lightJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void controlCurtain(String controlProtocol, String machineName, int pauseStauts,int salianStauts,int bulianStatus, String controlSence){
        JSONObject CommandData = new JSONObject();
        JSONObject controlData = new JSONObject();
        try {
            CommandData.put("controlProtocol", controlProtocol);
            CommandData.put("machineName", machineName);
            CommandData.put("controlData", controlData);
            controlData.put("pauseStauts", pauseStauts);
            controlData.put("salianStauts", salianStauts);
            controlData.put("bulianStatus", bulianStatus);
            CommandData.put("controlSence", controlSence);
            CommandData.put("houseDBName", roomDBName);
            String curtainJson = CommandJsonUtils.getCommandJson(0, CommandData, token, uname, pwd, String.valueOf(System.currentTimeMillis()));
            EventBus.getDefault().post(new MyEventBus(curtainJson));
            Log.i(TAG, "onClick: ------lightjson------" + curtainJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /*
     *    ----------------------------------------------------手势------------------------------------------------------
     */
    @Override
    public boolean onDown(MotionEvent motionEvent) {

        return true;
    }


    @Override
    public void onShowPress(MotionEvent motionEvent) {
         Log.i(TAG, "onShowPress: ---------");
        if (scrollY==0){
            svPullUpMenu.smoothScrollToSlow(0,btHeight_X3,600);
            flag = true;
            pullDown(true,true);
        }
    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        //Log.i(TAG, "onSingleTapUp: ---------");
        if (scrollY==0){
            svPullUpMenu.smoothScrollToSlow(0,btHeight_X3,600);
            flag = true;
            pullDown(true,true);

        }
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        //Log.i("result", "onScroll:---------------- "+motionEvent1);
        //Log.i(TAG, "onScroll: =================="+v);
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
        //Log.i(TAG, "onLongPress: ------------");
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        try {
            if (motionEvent.getX() - motionEvent1.getX() < -89) {
                flingLeft();
                return true;
            } else if (motionEvent.getX() - motionEvent1.getX() > 89) {
                flingRight();
                return true;
            }else if (motionEvent.getY() - motionEvent1.getY() > 89) {
                flingUp();       //上滑
                return true;
            }else if (motionEvent.getY() - motionEvent1.getY() < -89) {
                flingDown();       //下滑
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    public void flingLeft() {//自定义方法：处理向左滑动事件
        //Log.i("result", "flingLeft:------left-------");
    }

    public void flingRight() {//自定义方法：处理向右滑动事件
        //Log.i("result", "flingLeft:------right-------");
    }

    public void flingUp() {          //自定义方法：处理向上滑动事件
        /*Log.i(TAG, "flingUp: 上滑");
        scrollToBottom();        //滑动到隐藏头
        pullDown(true);*/
    }

    public void flingDown() {        //自定义方法：处理向下滑动事件
        /*scrollToTop();
        pullDown(false);
        Log.i(TAG, "flingUp: 下滑");*/
    }




}
