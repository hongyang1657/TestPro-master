package com.byids.hy.testpro.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.byids.hy.testpro.R;
import com.byids.hy.testpro.View.MyEventBusLight;
import com.byids.hy.testpro.customSceneBean.LoopLight;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by gqgz2 on 2017/1/5.
 */

public class CustomLightBaseAdapter extends BaseAdapter{

    private Context context;
    private LayoutInflater inflater;
    private Typeface typeFace;
    private String[] lightLoopNameList;
    private boolean isOpen = false;
    private int lightValue = 1;
    private int pos;
    private LoopLight loopLight = new LoopLight();
    private boolean isSwitchOpen = false;
    private int loopLightValue;
    private int[] lightValueList = {0,0,0,0,0,0};

    public CustomLightBaseAdapter(Context context, String[] lightLoopNameList) {
        this.context = context;
        this.lightLoopNameList = lightLoopNameList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return lightLoopNameList.length;
    }

    @Override
    public Object getItem(int position) {
        return lightLoopNameList[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView==null){
            convertView = inflater.inflate(R.layout.custom_light_item,null);
            holder = new ViewHolder();
            typeFace = Typeface.createFromAsset(context.getAssets(),"fonts/xiyuanti.ttf");
            holder.tvCustomLightLoop = (TextView) convertView.findViewById(R.id.tv_custom_light_loop);
            holder.swCustomLightLoop = (Switch) convertView.findViewById(R.id.sw_custom_light_loop);
            holder.rgCustomLightLoop = (RadioGroup) convertView.findViewById(R.id.rg_custom_light_loop);
            holder.rbCustomLightLoop1 = (RadioButton) convertView.findViewById(R.id.rb_custom_light_loop_1);
            holder.rbCustomLightLoop2 = (RadioButton) convertView.findViewById(R.id.rb_custom_light_loop_2);
            holder.rbCustomLightLoop3 = (RadioButton) convertView.findViewById(R.id.rb_custom_light_loop_3);
            holder.tvCustomLightLoop.setTypeface(typeFace);
            holder.rbCustomLightLoop1.setTypeface(typeFace);
            holder.rbCustomLightLoop2.setTypeface(typeFace);
            holder.rbCustomLightLoop3.setTypeface(typeFace);
            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();
        /*holder.rbCustomLightLoop1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new MyEventBusLight(position,1,lightLoopNameList.length));
            }
        });
        holder.rbCustomLightLoop2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new MyEventBusLight(position,2,lightLoopNameList.length));
            }
        });
        holder.rbCustomLightLoop3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new MyEventBusLight(position,3,lightLoopNameList.length));
            }
        });*/


        holder.tvCustomLightLoop.setText(lightLoopNameList[position]);
        final ViewHolder finalHolder = holder;
        holder.swCustomLightLoop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    finalHolder.rgCustomLightLoop.setVisibility(View.VISIBLE);
                    EventBus.getDefault().post(new MyEventBusLight(position,11,lightLoopNameList.length));     //打开闭全部灯光的选项
                    finalHolder.rbCustomLightLoop1.setChecked(true);
                    isSwitchOpen = true;
                }else {
                    finalHolder.rgCustomLightLoop.clearCheck();
                    finalHolder.rgCustomLightLoop.setVisibility(View.GONE);
                    EventBus.getDefault().post(new MyEventBusLight(position,10,lightLoopNameList.length));     //关闭全部灯光的选项
                    isSwitchOpen = false;
                    //finalHolder.rbCustomLightLoop1.setChecked(true);
                }
            }
        });
        holder.rgCustomLightLoop.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_custom_light_loop_1:
                        loopLightValue = 1;
                        EventBus.getDefault().post(new MyEventBusLight(position,1,lightLoopNameList.length));
                        break;
                    case R.id.rb_custom_light_loop_2:
                        loopLightValue = 2;
                        EventBus.getDefault().post(new MyEventBusLight(position,2,lightLoopNameList.length));
                        break;
                    case R.id.rb_custom_light_loop_3:
                        loopLightValue = 3;
                        EventBus.getDefault().post(new MyEventBusLight(position,3,lightLoopNameList.length));
                        break;
                }
            }
        });

        holder.swCustomLightLoop.setChecked(isOpen);
        holder.swCustomLightLoop.clearFocus();
        switch (lightValue){
            case 1:
                holder.rbCustomLightLoop1.setChecked(true);
                break;
            case 2:
                holder.rbCustomLightLoop2.setChecked(true);
                break;
            case 3:
                holder.rbCustomLightLoop3.setChecked(true);
                break;
        }

        Log.i("hy_light", "getView: lightValueList:"+lightValueList[position]);
        if (lightValueList[position]==1){
            holder.swCustomLightLoop.setChecked(true);
            holder.rbCustomLightLoop1.setChecked(true);
        }else if (lightValueList[position]==2){
            holder.swCustomLightLoop.setChecked(true);
            holder.rbCustomLightLoop2.setChecked(true);
        }else if (lightValueList[position]==3){
            holder.swCustomLightLoop.setChecked(true);
            holder.rbCustomLightLoop3.setChecked(true);
        }

        return convertView;
    }


    //打开所有的switch
    public void turnAllSwitch(boolean isOpen){
        this.isOpen = isOpen;
        notifyDataSetChanged();
    }

    //根据全部的选择，改变回路的灯光亮度设定
    public void turnAllLightValue(int lightValue){
        this.lightValue = lightValue;
        notifyDataSetChanged();
    }

    //根据用户之前保存的数据，显示各个回路显示的值
    public void turnLoopLightValue(int[] lightValueList){
        this.lightValueList = lightValueList;
        notifyDataSetChanged();
    }


    class ViewHolder{
        TextView tvCustomLightLoop;
        Switch swCustomLightLoop;
        RadioGroup rgCustomLightLoop;
        RadioButton rbCustomLightLoop1;
        RadioButton rbCustomLightLoop2;
        RadioButton rbCustomLightLoop3;
    }
}
