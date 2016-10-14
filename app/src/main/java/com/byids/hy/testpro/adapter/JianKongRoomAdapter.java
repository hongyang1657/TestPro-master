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
 * Created by gqgz2 on 2016/10/12.
 */
public class JianKongRoomAdapter extends BaseAdapter{

    private Context context;
    private LayoutInflater inflater;
    private String[] rooms;
    private Typeface typeFace;

    public JianKongRoomAdapter(Context context,String[] rooms) {
        inflater = LayoutInflater.from(context);
        this.rooms = rooms;
        this.context = context;
    }

    @Override
    public int getCount() {
        return rooms.length;
    }

    @Override
    public Object getItem(int position) {
        return rooms[position];
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
        holder.textView.setText(rooms[position]);
        return convertView;
    }


    class ViewHolder{
        TextView textView;
    }
}
