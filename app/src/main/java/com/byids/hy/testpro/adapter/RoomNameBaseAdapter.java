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
 * Created by gqgz2 on 2016/9/28.
 */
public class RoomNameBaseAdapter extends BaseAdapter{

    private Context context;
    private String[] roomNameList = null;
    private LayoutInflater inflater;
    private Typeface typeFace;
    private int mSelect;

    public RoomNameBaseAdapter(Context context, String[] roomNameList) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.roomNameList = roomNameList;
    }

    @Override
    public int getCount() {
        return roomNameList.length;
    }

    @Override
    public Object getItem(int position) {
        return roomNameList[position];
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
            holder.tvRoomName = (TextView) convertView.findViewById(R.id.tv_room_name_item);
            holder.tvRoomName.setTypeface(typeFace);
            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();
        holder.tvRoomName.setText(roomNameList[position]);
        if (mSelect==position){
            holder.tvRoomName.setTextColor(context.getResources().getColor(R.color.colorTextActive));    //点击项变色
        }else {
            holder.tvRoomName.setTextColor(context.getResources().getColor(R.color.colorText));   //其他的设为正常色
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

    class ViewHolder{
        TextView tvRoomName;
    }
}
