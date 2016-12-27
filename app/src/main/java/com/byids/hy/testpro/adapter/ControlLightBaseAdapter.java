package com.byids.hy.testpro.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.byids.hy.testpro.R;

/**
 * Created by gqgz2 on 2016/12/24.
 */

public class ControlLightBaseAdapter extends BaseAdapter{
    private Context context;
    private LayoutInflater inflater;
    private Typeface typeFace;
    private int[] lightLoopIconList;
    private String[] lightLoopNameList;

    public ControlLightBaseAdapter(Context context, int[] lightLoopIconList, String[] lightLoopNameList) {
        this.context = context;
        this.lightLoopIconList = lightLoopIconList;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView==null){
            convertView = inflater.inflate(R.layout.control_light_huilu_item,null);
            holder = new ViewHolder();
            typeFace = Typeface.createFromAsset(context.getAssets(),"fonts/xiyuanti.ttf");
            holder.ivLightControlX = (ImageView) convertView.findViewById(R.id.iv_light_control_x);
            holder.tvLightControlX = (TextView) convertView.findViewById(R.id.tv_light_control_x);
            holder.rgLightControlX = (RadioGroup) convertView.findViewById(R.id.rg_control_light_channel_x);
            holder.rbLightControlClose = (RadioButton) convertView.findViewById(R.id.rb_control_light_channel_x_close);
            holder.rbLightControl1 = (RadioButton) convertView.findViewById(R.id.rb_control_light_channel_x_1);
            holder.rbLightControl2 = (RadioButton) convertView.findViewById(R.id.rb_control_light_channel_x_2);
            holder.rbLightControl3 = (RadioButton) convertView.findViewById(R.id.rb_control_light_channel_x_3);
            holder.tvLightControlX.setTypeface(typeFace);
            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();
        holder.ivLightControlX.setImageResource(lightLoopIconList[position]);
        holder.tvLightControlX.setText(lightLoopNameList[position]);
        return convertView;
    }

    class ViewHolder{
        ImageView ivLightControlX;
        TextView tvLightControlX;
        RadioGroup rgLightControlX;
        RadioButton rbLightControlClose;
        RadioButton rbLightControl1;
        RadioButton rbLightControl2;
        RadioButton rbLightControl3;
    }
}
