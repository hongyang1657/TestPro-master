package com.byids.hy.testpro.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.byids.hy.testpro.R;

/**
 * Created by gqgz2 on 2016/10/14.
 */
public class PickTimeAdapter extends BaseAdapter{

    private int flag;
    private Context context;
    private LayoutInflater inflater;
    private String[] timesList = null;
    private String[] roomsName = null;
    private Typeface typeFace;
    private int mSelect;

    //flag为0：加载视频片段          flag为1：加载房间名list
    public PickTimeAdapter(int flag, Context context, String[] timesList,String[] roomsName) {
        this.flag = flag;
        this.context = context;
        this.timesList = timesList;
        this.roomsName = roomsName;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if (flag==0){
            return timesList.length;
        }else if (flag==1){
            return roomsName.length;
        }else{
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        if (flag==0){
            return timesList[position];
        }else if (flag==1){
            return roomsName[position];
        }else{
            return 0;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView==null){
            convertView = inflater.inflate(R.layout.camera_data_item,null);
            holder = new ViewHolder();
            typeFace = Typeface.createFromAsset(context.getAssets(),"fonts/xiyuanti.ttf");
            holder.textView = (TextView) convertView.findViewById(R.id.tv_camera_data);
            holder.textView.setTypeface(typeFace);
            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();
        if (flag==0){
            holder.textView.setText(timesList[position]);
        }else if (flag==1){
            holder.textView.setText(roomsName[position]);
        }
        if (mSelect==position){
            //holder.textView.setBackgroundColor(context.getResources().getColor(R.color.colorAlphaBlack1));    //点击项变色
            holder.textView.setBackgroundResource(R.drawable.camera_switch_list);
        }else {
            holder.textView.setBackgroundColor(context.getResources().getColor(R.color.colorAlpha));   //其他的设为正常色
        }
        return convertView;
    }

    //传入点击item 改变颜色
    public void changeSelected(int position){
        if (position!=mSelect){
            mSelect = position;
            notifyDataSetChanged();
        }
    }

    public void changdata(String[] timesList){
        this.timesList = timesList;
        notifyDataSetChanged();
    }

    class ViewHolder{
        TextView textView;
    }
}
