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

    private Context context;
    private LayoutInflater inflater;
    private String[] timesList = null;
    private Typeface typeFace;
    private int mSelect;

    public PickTimeAdapter(Context context, String[] timesList) {
        this.context = context;
        this.timesList = timesList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return timesList.length;
    }

    @Override
    public Object getItem(int position) {
        return timesList[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView==null){
            convertView = inflater.inflate(R.layout.switch_room_item,null);
            holder = new ViewHolder();
            typeFace = Typeface.createFromAsset(context.getAssets(),"fonts/xiyuanti.ttf");
            holder.textView = (TextView) convertView.findViewById(R.id.tv_room_name_item);
            holder.textView.setTypeface(typeFace);
            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();
        holder.textView.setText(timesList[position]);
        if (mSelect==position){
            holder.textView.setBackgroundColor(context.getResources().getColor(R.color.colorWhite));    //点击项变色
        }else {
            holder.textView.setBackgroundColor(context.getResources().getColor(R.color.colorLoginBack));   //其他的设为正常色
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
