package com.byids.hy.testpro.activity;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.byids.hy.testpro.Bean.HomeAttr;
import com.byids.hy.testpro.MyEventBus;
import com.byids.hy.testpro.PullDownMenuListener;
import com.byids.hy.testpro.R;
import com.byids.hy.testpro.View.MyCustomViewPager;
import com.byids.hy.testpro.adapter.MyFragmentAdapter;
import com.byids.hy.testpro.adapter.RoomNameBaseAdapter;
import com.byids.hy.testpro.fragment.MyFragment;
import com.videogo.openapi.EZOpenSDK;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hy on 2016/8/15.
 */
public class MyMainActivity extends FragmentActivity{
    private String TAG = "result";
    private static final String SWITCH_ROOM_DIALOG = "1";

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
    private ImageView ivMusic;
    private ImageView ivMedia;

    //二级页面dialog
    private Dialog dialogSwitchRoom;
    private ListView lvSwitchRoom;
    private TextView tvSwitchRoomCancel;
    private RoomNameBaseAdapter adapterRoomName;


    //背景图片
    private int[] ivBackList1 = {R.mipmap.back_10,R.mipmap.back_12,R.mipmap.back_13,R.mipmap.back_14};
    private int[] ivBackList2 = {R.mipmap.back_5,R.mipmap.back_6,R.mipmap.back_8,R.mipmap.back_9};
    private int[] ivBackList3 = {R.mipmap.back_1,R.mipmap.back_2,R.mipmap.back_3,R.mipmap.back_4};
    private int[] ivBackList = {R.mipmap.back_10,R.mipmap.back_12,R.mipmap.back_13,R.mipmap.back_14,R.mipmap.back_5,R.mipmap.back_6,R.mipmap.back_8,R.mipmap.back_9,R.mipmap.back_1,R.mipmap.back_2,R.mipmap.back_3,R.mipmap.back_4};

    //房间名数组
    private String[] roomNameList = null;
    private HomeAttr homeAttr = new HomeAttr();

    private MyFragment myFragment1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setContentView(R.layout.my_main_layout);
        //注册EventBus
        EventBus.getDefault().register(this);
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);  //反注册EventBus
    }

    private void initView(){

        //获取房间信息
        roomNameList = getIntent().getStringArrayExtra("roomNameList");
        homeAttr = (HomeAttr) getIntent().getSerializableExtra("homeAttr");
        Log.i(TAG, "initView: "+homeAttr.getRooms().get(0).getRoomAttr().getLight().getActive());
        Log.i(TAG, "initView: "+homeAttr);

        WindowManager wm = this.getWindowManager();
        width = wm.getDefaultDisplay().getWidth();
        height = wm.getDefaultDisplay().getHeight();
        Log.i(TAG, "initView: ____________________"+width+"___________________"+height);

        ivMusic = (ImageView) findViewById(R.id.iv_music);
        ivMedia = (ImageView) findViewById(R.id.iv_media);
        ivMusic.setOnClickListener(mediaListener);
        ivMedia.setOnClickListener(mediaListener);
        int h = (int) ((int) height*0.03);
        int w = (int) ((int) width*0.035);
        int h1 = (int) ((int) height*0.021);
        ivMusic.setPadding(0,h,w,0);
        ivMedia.setPadding(0,h1,w,0);
        viewPager = (MyCustomViewPager) findViewById(R.id.id_vp);

        //初始化activity给fragment传递的数据
        for (int i=0;i<roomNameList.length;i++){
            myFragment1 = new MyFragment(i,roomNameList[i],ivBackList,homeAttr.getRooms().get(i).getRoomAttr());         //房间id,房间名数组，背景图片数组，活跃的控件数组
            pullMenu(myFragment1);
            viewList.add(myFragment1);
        }


        adapter = new MyFragmentAdapter(getSupportFragmentManager(),viewList);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(roomNameList.length+1);  //多设置一页
        viewPager.addOnPageChangeListener(pagerChangeListener);

        initDialog();     //初始化dialog二级页面
    }

    //初始化dialog二级页面
    private void initDialog(){
        Typeface typeFace = Typeface.createFromAsset(getAssets(),"fonts/xiyuanti.ttf");
        dialogSwitchRoom = new Dialog(this,R.style.CustomDialog);
        View view = LayoutInflater.from(this).inflate(R.layout.switch_room_dialog,null);
        lvSwitchRoom = (ListView) view.findViewById(R.id.lv_rooms);
        tvSwitchRoomCancel = (TextView) view.findViewById(R.id.tv_cancel_switch_room);
        tvSwitchRoomCancel.setTypeface(typeFace);
        tvSwitchRoomCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSwitchRoom.hide();
            }
        });
        adapterRoomName = new RoomNameBaseAdapter(this,roomNameList);
        lvSwitchRoom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                viewPager.setCurrentItem(position);
                dialogSwitchRoom.hide();

            }
        });
        lvSwitchRoom.setAdapter(adapterRoomName);

        dialogSwitchRoom.setContentView(view);
        dialogSwitchRoom.setCanceledOnTouchOutside(true);//点击外部，弹框消失
        WindowManager.LayoutParams params = dialogSwitchRoom.getWindow().getAttributes();
        params.width = (int) (width*0.82);
        //params.height = 800;   //设置dialog的宽高
        Window mWindow = dialogSwitchRoom.getWindow();
        mWindow.setGravity(Gravity.BOTTOM);
        mWindow.setAttributes(params);
    }


    //-----------------------------接受fragment传来的消息--------------------------------
    @Subscribe
    public void onEventMainThread(MyEventBus event) {
        String msg = "onEventMainThread收到了消息：" + event.getmMsg();
        //Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        if (msg=="jiankong"){
            Intent intent = new Intent(MyMainActivity.this,CameraActivity.class);
            startActivity(intent);
        }
        switch (event.getmMsg()){
            case SWITCH_ROOM_DIALOG:   //选择房间dialog
                dialogSwitchRoom.show();
                break;
        }
    }


    View.OnClickListener mediaListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.iv_music:
                    Toast.makeText(MyMainActivity.this, "音乐", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.iv_media:
                    Toast.makeText(MyMainActivity.this, "媒体", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    private void pullMenu(MyFragment myF){
        //自定义的下拉监听
        myF.setPullDownMenuListener(new PullDownMenuListener() {
            @Override
            public void pullDown(boolean b,boolean isIconShow) {
                b1 = isIconShow;
                if (b==false&&isIconShow==false){      //下拉菜单出现时
                    //隐藏桌面小控件
                    ivMusic.setVisibility(View.GONE);
                    ivMedia.setVisibility(View.GONE);
                    //设置viewpager不能滑动
                    viewPager.setCanScroll(false);
                }else if (b==true&&isIconShow==true){     //下拉菜单隐藏时
                    ivMusic.setVisibility(View.VISIBLE);
                    ivMedia.setVisibility(View.VISIBLE);
                    viewPager.setCanScroll(true);
                }else if (b==false&&isIconShow==true){
                    ivMusic.setVisibility(View.VISIBLE);
                    ivMedia.setVisibility(View.VISIBLE);
                }else if (b==true&&isIconShow==false){
                    ivMusic.setVisibility(View.GONE);
                    ivMedia.setVisibility(View.GONE);
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
            Log.i(TAG, "onPageScrollStateChanged: =-=-=-=-=-=-=-="+state);
            pagerState = state;
            boolean isChange = false;
            if (state==2){
                isChange = true;
            }

            if (state==1){
                scrollViewPager();
            }
            if (state==0){
                downScrollViewPager();
            }
            //onMainListener.onMainAction(pagerState);   //Activity向Fragment通信
        }
    };
    //滑动viewpager时，控件消失
    private void scrollViewPager(){
        ivMusic.setVisibility(View.GONE);
        ivMedia.setVisibility(View.GONE);
    }
    //滑动结束后，控件显现
    private void downScrollViewPager(){
        if (b1==false){
            ivMusic.setVisibility(View.GONE);
            ivMedia.setVisibility(View.GONE);
        }else if (b1==true){
            showAnimation();
            ivMusic.setVisibility(View.VISIBLE);
            ivMedia.setVisibility(View.VISIBLE);
        }

    }
    //控件滑出显示的动画
    private void showAnimation(){
        ObjectAnimator.ofFloat(ivMusic,"translationX",200,-10,0).setDuration(800).start();
        ObjectAnimator oa1 = ObjectAnimator.ofFloat(ivMedia,"translationX",250,-10,0);
        oa1.setDuration(1000);
        //oa1.setStartDelay(100);
        oa1.start();

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


}
