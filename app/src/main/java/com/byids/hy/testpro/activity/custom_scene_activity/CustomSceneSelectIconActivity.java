package com.byids.hy.testpro.activity.custom_scene_activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.byids.hy.testpro.R;
import com.byids.hy.testpro.View.CustomIconGridView;
import com.byids.hy.testpro.activity.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by gqgz2 on 2016/12/16.
 */

public class CustomSceneSelectIconActivity extends BaseActivity {

    private CustomIconBaseAdapter adapter;
    private int iconPosition = 0;
    private Typeface typeFace;

    private int[] iconResList = {R.mipmap.scene_custom_img_3x_1, R.mipmap.scene_custom_img_3x_2, R.mipmap.scene_custom_img_3x_3,
            R.mipmap.scene_custom_img_3x_4, R.mipmap.scene_custom_img_3x_5, R.mipmap.scene_custom_img_3x_6, R.mipmap.scene_custom_img_3x_7,
            R.mipmap.scene_custom_img_3x_8, R.mipmap.scene_custom_img_3x_9, R.mipmap.scene_custom_img_3x_10, R.mipmap.scene_custom_img_3x_11,
            R.mipmap.scene_custom_img_3x_12, R.mipmap.scene_custom_img_3x_13, R.mipmap.scene_custom_img_3x_14, R.mipmap.scene_custom_img_3x_15,
            R.mipmap.scene_custom_img_3x_16, R.mipmap.scene_custom_img_3x_17, R.mipmap.scene_custom_img_3x_18, R.mipmap.scene_custom_img_3x_19,
            R.mipmap.scene_custom_img_3x_20, R.mipmap.scene_custom_img_3x_21, R.mipmap.scene_custom_img_3x_22, R.mipmap.scene_custom_img_3x_23,
            R.mipmap.scene_custom_img_3x_24, R.mipmap.scene_custom_img_3x_25, R.mipmap.scene_custom_img_3x_26, R.mipmap.scene_custom_img_3x_27};
    private int[] iconResListSelect = {R.mipmap.scene_custom_img_select_3x_1,R.mipmap.scene_custom_img_select_3x_2,R.mipmap.scene_custom_img_select_3x_3,
            R.mipmap.scene_custom_img_select_3x_4,R.mipmap.scene_custom_img_select_3x_5,R.mipmap.scene_custom_img_select_3x_6,R.mipmap.scene_custom_img_select_3x_7,
            R.mipmap.scene_custom_img_select_3x_8,R.mipmap.scene_custom_img_select_3x_9,R.mipmap.scene_custom_img_select_3x_10,R.mipmap.scene_custom_img_select_3x_11,
            R.mipmap.scene_custom_img_select_3x_12,R.mipmap.scene_custom_img_select_3x_13,R.mipmap.scene_custom_img_select_3x_14,R.mipmap.scene_custom_img_select_3x_15,
            R.mipmap.scene_custom_img_select_3x_16,R.mipmap.scene_custom_img_select_3x_17,R.mipmap.scene_custom_img_select_3x_18,R.mipmap.scene_custom_img_select_3x_19,
            R.mipmap.scene_custom_img_select_3x_20,R.mipmap.scene_custom_img_select_3x_21,R.mipmap.scene_custom_img_select_3x_22,R.mipmap.scene_custom_img_select_3x_23,
            R.mipmap.scene_custom_img_select_3x_24,R.mipmap.scene_custom_img_select_3x_25,R.mipmap.scene_custom_img_select_3x_26,R.mipmap.scene_custom_img_select_3x_27,};

    @BindView(R.id.iv_custom_icon_back)
    ImageView ivCustomIconBack;
    @BindView(R.id.tv_custom_icon_title)
    TextView tvCustomIconTitle;
    @BindView(R.id.tv_custom_selecter_icon_save)
    TextView tvCustomSelecterIconSave;
    @BindView(R.id.gv_select_icon)
    CustomIconGridView gvSelectIcon;

    private int iconNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setContentView(R.layout.custom_scene_select_icon_layout);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        typeFace = Typeface.createFromAsset(getAssets(), "fonts/xiyuanti.ttf");
        tvCustomIconTitle.setTypeface(typeFace);
        tvCustomSelecterIconSave.setTypeface(typeFace);
        iconNum = getIntent().getIntExtra("iconNum",0);

        adapter = new CustomIconBaseAdapter(this,iconResList,iconResListSelect);
        gvSelectIcon.setAdapter(adapter);
        gvSelectIcon.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.changeIconColor(position);
                iconPosition = position;
            }
        });

        adapter.changeIconColor(iconNum);
    }

    @OnClick({R.id.iv_custom_icon_back, R.id.tv_custom_selecter_icon_save})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_custom_icon_back:
                Intent intent = new Intent();
                intent.putExtra("customIconNum",-1);
                setResult(1,intent);
                finish();
                break;
            case R.id.tv_custom_selecter_icon_save:
                Intent intent1 = new Intent();
                intent1.putExtra("customIconNum",iconPosition);
                setResult(1,intent1);
                finish();
                break;
        }
    }

    class CustomIconBaseAdapter extends BaseAdapter {
        private Context context;
        private LayoutInflater inflater;
        private int[] iconResList;
        private int[] iconResSelectList;
        private Viewholder viewholder = null;
        private int selectPosition;

        public CustomIconBaseAdapter(Context context, int[] iconResList,int[] iconResSelectList) {
            inflater = LayoutInflater.from(context);
            this.context = context;
            this.iconResList = iconResList;
            this.iconResSelectList = iconResSelectList;
        }

        @Override
        public int getCount() {
            return iconResList.length;
        }

        @Override
        public Object getItem(int position) {
            return iconResList[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView==null){
                viewholder = new Viewholder();
                convertView = inflater.inflate(R.layout.custom_select_icon_item,null);
                viewholder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_custom_select_icon);
                //设置item长宽一致
                convertView.setLayoutParams(new AbsListView.LayoutParams((int) (parent.getWidth() / 4),(int) (parent.getWidth() /4.3 )));
                convertView.setTag(viewholder);
            }
            viewholder = (Viewholder) convertView.getTag();
            if (selectPosition==position){
                viewholder.ivIcon.setImageResource(iconResSelectList[position]);
                viewholder.ivIcon.setBackgroundResource(R.mipmap.select_icon_ring_3x);
            }else {
                viewholder.ivIcon.setImageResource(iconResList[position]);
                viewholder.ivIcon.setBackgroundResource(R.color.colorAlpha);
            }
            return convertView;
        }

        private void changeIconColor(int position){
            this.selectPosition = position;
            notifyDataSetChanged();
        }

        class Viewholder{
            ImageView ivIcon;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==event.KEYCODE_BACK){
            Intent intent = new Intent();
            intent.putExtra("customIconNum",-1);
            setResult(1,intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
