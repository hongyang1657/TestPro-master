package com.byids.hy.testpro.activity;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
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
import com.byids.hy.testpro.MyEventBus2;
import com.byids.hy.testpro.PullDownMenuListener;
import com.byids.hy.testpro.R;
import com.byids.hy.testpro.TCPLongSocketCallback;
import com.byids.hy.testpro.TcpLongSocket;
import com.byids.hy.testpro.View.MyCustomViewPager;
import com.byids.hy.testpro.adapter.MyFragmentAdapter;
import com.byids.hy.testpro.adapter.RoomNameBaseAdapter;
import com.byids.hy.testpro.fragment.MyFragment;
import com.byids.hy.testpro.utils.CommandJsonUtils;
import com.byids.hy.testpro.utils.Encrypt;
import com.byids.hy.testpro.utils.RunningTimeDialog;
import com.byids.hy.testpro.utils.VibratorUtil;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.videogo.openapi.EZOpenSDK;
import android.support.v4.app.FragmentManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hy on 2016/8/15.
 */
public class MyMainActivity extends FragmentActivity {
    private String TAG = "result";
    private static final String SWITCH_ROOM_DIALOG = "1";
    private static final String SCROLL_FRAGMENT_START = "2";    //滑动viewpager
    private static final String SCROLL_FRAGMENT_END = "3";      //结束滑动viewpager

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
    private int[] ivBackList1 = {R.mipmap.back_10, R.mipmap.back_12, R.mipmap.back_13, R.mipmap.back_14};
    private int[] ivBackList2 = {R.mipmap.back_5, R.mipmap.back_6, R.mipmap.back_8, R.mipmap.back_9};
    private int[] ivBackList3 = {R.mipmap.back_1, R.mipmap.back_2, R.mipmap.back_3, R.mipmap.back_4};
    private int[] ivBackList = {R.mipmap.back_10, R.mipmap.back_12, R.mipmap.back_13, R.mipmap.back_14, R.mipmap.back_5, R.mipmap.back_6, R.mipmap.back_8, R.mipmap.back_9, R.mipmap.back_1, R.mipmap.back_2, R.mipmap.back_3, R.mipmap.back_4};

    //房间名数组
    private String[] roomNameList = null;
    private HomeAttr homeAttr = new HomeAttr();

    private MyFragment myFragment1;

    private String ip; //home  ip地址
    private String uname;
    private String pwd;
    private String hid;

    //----------------socket---------------------
    public static final int DEFAULT_PORT = 57816;
    private TcpLongSocket tcplongSocket;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setContentView(R.layout.my_main_layout);
        //注册EventBus
        EventBus.getDefault().register(this);
        reciveIntent();
        initView();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);  //反注册EventBus
    }

    private void reciveIntent() {
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
        }
    }



    private class ConnectTcp implements TCPLongSocketCallback {


        @Override
        public void connected() {
            Log.i("MAIN", String.valueOf(tcplongSocket.getConnectStatus()));
            VibratorUtil.Vibrate(MyMainActivity.this, 300);
            JSONObject checkCommandData = new JSONObject();
            try {
                checkCommandData.put("kong", "keys");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String checkJson = CommandJsonUtils.getCommandJson(1, checkCommandData, hid, uname, pwd, String.valueOf(System.currentTimeMillis()));
            Log.i("result", "check" + checkJson);
            tcplongSocket.writeDate(Encrypt.encrypt(checkJson));

        }

        @Override
        public void receive(byte[] buffer) {
            Log.i("result", "---收到         数据-----");
            if ("Hello client" == buffer.toString()) {
                Log.i("result", "心跳" + String.valueOf(tcplongSocket.getConnectStatus()));
            }

        }

        @Override
        public void disconnect() {
            tcplongSocket.close();
        }
    }

    private void initView() {

        //获取房间信息
        roomNameList = getIntent().getStringArrayExtra("roomNameList");
        homeAttr = (HomeAttr) getIntent().getSerializableExtra("homeAttr");
        //Log.i(TAG, "initView: " + homeAttr.getRooms().get(0).getRoomAttr().getLight().getActive());
        //Log.i(TAG, "initView: " + homeAttr);


        //初始化activity给fragment传递的数据
        for (int i = 0; i < roomNameList.length; i++) {
            myFragment1 = new MyFragment(i, roomNameList[i], ivBackList, homeAttr.getRooms().get(i).getRoomAttr());         //房间id,房间名数组，背景图片数组，活跃的控件数组
            pullMenu(myFragment1);
            viewList.add(myFragment1);
        }

        WindowManager wm = this.getWindowManager();
        width = wm.getDefaultDisplay().getWidth();
        height = wm.getDefaultDisplay().getHeight();
        //Log.i(TAG, "initView: ____________________" + width + "___________________" + height);

        ivMusic = (ImageView) findViewById(R.id.iv_music);
        ivMedia = (ImageView) findViewById(R.id.iv_media);
        ivMusic.setOnClickListener(mediaListener);
        ivMedia.setOnClickListener(mediaListener);
        int h = (int) ((int) height * 0.03);
        int w = (int) ((int) width * 0.035);
        int h1 = (int) ((int) height * 0.021);
        ivMusic.setPadding(0, h, w, 0);
        ivMedia.setPadding(0, h1, w, 0);
        viewPager = (MyCustomViewPager) findViewById(R.id.id_vp);


        adapter = new MyFragmentAdapter(getSupportFragmentManager(), viewList);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(roomNameList.length + 1);  //多设置一页
        viewPager.addOnPageChangeListener(pagerChangeListener);

        initDialog();     //初始化dialog二级页面
    }


    //初始化dialog二级页面
    private void initDialog() {
        Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/xiyuanti.ttf");
        dialogSwitchRoom = new Dialog(this, R.style.CustomDialog);
        View view = LayoutInflater.from(this).inflate(R.layout.switch_room_dialog, null);
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


    //-----------------------------接受fragment传来的消息--------------------------------
    @Subscribe
    public void onEventMainThread(MyEventBus event) {
        String msg = event.getmMsg();
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();

        switch (event.getmMsg()) {
            case SWITCH_ROOM_DIALOG:     //选择房间dialog
                dialogSwitchRoom.show();
                break;

        }
    }


    private int flagLight = 0;
    private static final String CONTROL_PROTOCOL_HDL = "hdl";
    private static final String MACHINE_NAME_LIGHT = "light";
    private static final String LIGHT_VALUE_100 = "100";
    private static final String LIGHT_VALUE_0 = "0";
    private static final String IS_SERVER_AUTO = "0";
    private static final String CONTROL_SENCE_ALL = "all";
    private static final String HOUSE_DB_NAME_KETING = "keting";
    View.OnClickListener mediaListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.iv_music:
                    Toast.makeText(MyMainActivity.this, "音乐", Toast.LENGTH_SHORT).show();
                    //测试控制灯
                    if (flagLight == 0) {
                        JSONObject lightOnCommandData = new JSONObject();
                        JSONObject controlData = new JSONObject();
                        try {
                            lightOnCommandData.put("controlProtocol", "hdl");
                            lightOnCommandData.put("machineName", "light");
                            lightOnCommandData.put("controlData", controlData);
                            controlData.put("lightValue", "100");
                            controlData.put("isServerAUTO", "0");
                            lightOnCommandData.put("controlSence", "all");
                            lightOnCommandData.put("houseDBName", "keting");
                            String lightJson = CommandJsonUtils.getCommandJson(0, lightOnCommandData, hid, uname, pwd, String.valueOf(System.currentTimeMillis()));
                            tcplongSocket.writeDate(Encrypt.encrypt(lightJson));
                            Log.i(TAG, "onClick: ------lightjson------------------------" + lightJson);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        flagLight = 1;
                    } else if (flagLight == 1) {
                        JSONObject lightOffCommandData = new JSONObject();
                        JSONObject controlData = new JSONObject();
                        try {
                            lightOffCommandData.put("controlProtocol", "hdl");
                            lightOffCommandData.put("machineName", "light");
                            lightOffCommandData.put("controlData", controlData);
                            controlData.put("lightValue", "0");
                            controlData.put("isServerAUTO", "0");
                            lightOffCommandData.put("controlSence", "all");
                            lightOffCommandData.put("houseDBName", "keting");
                            String lightJson = CommandJsonUtils.getCommandJson(0, lightOffCommandData, hid, uname, pwd, String.valueOf(System.currentTimeMillis()));
                            tcplongSocket.writeDate(Encrypt.encrypt(lightJson));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        flagLight = 0;
                    }
                    break;
                case R.id.iv_media:
                    Toast.makeText(MyMainActivity.this, "媒体", Toast.LENGTH_SHORT).show();
                    flagLight = controlOnOrOff(flagLight,CONTROL_PROTOCOL_HDL,MACHINE_NAME_LIGHT,LIGHT_VALUE_100,LIGHT_VALUE_0,IS_SERVER_AUTO,CONTROL_SENCE_ALL,HOUSE_DB_NAME_KETING);
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
                    ivMusic.setVisibility(View.GONE);
                    ivMedia.setVisibility(View.GONE);
                    //设置viewpager不能滑动
                    viewPager.setCanScroll(false);
                } else if (b == true && isIconShow == true) {     //下拉菜单隐藏时
                    ivMusic.setVisibility(View.VISIBLE);
                    ivMedia.setVisibility(View.VISIBLE);
                    viewPager.setCanScroll(true);
                } else if (b == false && isIconShow == true) {
                    ivMusic.setVisibility(View.VISIBLE);
                    ivMedia.setVisibility(View.VISIBLE);
                } else if (b == true && isIconShow == false) {
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
        ivMusic.setVisibility(View.GONE);
        ivMedia.setVisibility(View.GONE);
    }

    //滑动结束后，控件显现
    private void downScrollViewPager() {
        if (b1 == false) {
            ivMusic.setVisibility(View.GONE);
            ivMedia.setVisibility(View.GONE);
        } else if (b1 == true) {
            showAnimation();
            ivMusic.setVisibility(View.VISIBLE);
            ivMedia.setVisibility(View.VISIBLE);
        }

    }

    //控件滑出显示的动画
    private void showAnimation() {
        ObjectAnimator.ofFloat(ivMusic, "translationX", 200, -10, 0).setDuration(800).start();
        ObjectAnimator oa1 = ObjectAnimator.ofFloat(ivMedia, "translationX", 250, -10, 0);
        oa1.setDuration(1000);
        //oa1.setStartDelay(100);
        oa1.start();

    }

    /**
     *    发送tcpSocket，控制部分的封装，二段开关
     * @param flag             标记开关  0,1
     * @param controlProtocol  产品名称  hdl..
     * @param machineName      模块名称  light..
     * @param value1          需要设定的值  100..
     * @param value2          需要设定的值  0..
     * @param isServerAUTO     是否自动   0..
     * @param controlSence     控制信号   all..
     * @param houseDBName      房间名，拼音  keting..
     */
    private int controlOnOrOff(int flag,String controlProtocol, String machineName, String value1,String value2, String isServerAUTO, String controlSence, String houseDBName) {
        if (flag == 0) {
            JSONObject lightOnCommandData = new JSONObject();
            JSONObject controlData = new JSONObject();
            try {
                lightOnCommandData.put("controlProtocol", controlProtocol);
                lightOnCommandData.put("machineName", machineName);
                lightOnCommandData.put("controlData", controlData);
                controlData.put("lightValue", value1);
                controlData.put("isServerAUTO", isServerAUTO);
                lightOnCommandData.put("controlSence", controlSence);
                lightOnCommandData.put("houseDBName", houseDBName);
                String lightJson = CommandJsonUtils.getCommandJson(0, lightOnCommandData, hid, uname, pwd, String.valueOf(System.currentTimeMillis()));
                tcplongSocket.writeDate(Encrypt.encrypt(lightJson));
                Log.i(TAG, "onClick: ------lightjson------------------------" + lightJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            flag = 1;
            return flag;
        } else if (flag == 1) {
            JSONObject lightOffCommandData = new JSONObject();
            JSONObject controlData = new JSONObject();
            try {
                lightOffCommandData.put("controlProtocol", controlProtocol);
                lightOffCommandData.put("machineName", machineName);
                lightOffCommandData.put("controlData", controlData);
                controlData.put("lightValue", value2);
                controlData.put("isServerAUTO", isServerAUTO);
                lightOffCommandData.put("controlSence", controlSence);
                lightOffCommandData.put("houseDBName", houseDBName);
                String lightJson = CommandJsonUtils.getCommandJson(0, lightOffCommandData, hid, uname, pwd, String.valueOf(System.currentTimeMillis()));
                tcplongSocket.writeDate(Encrypt.encrypt(lightJson));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            flag = 0;
        }
        return flag;
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
