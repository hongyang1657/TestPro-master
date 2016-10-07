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
 * Created by gqgz2 on 2016/9/29.
 */
public class CameraDataBaseAdapter extends BaseAdapter{

    private Context context;
    private Typeface typeFace;
    private LayoutInflater inflater;
    private String[] data;
    private int mSelect;

    public CameraDataBaseAdapter(Context context,String[] data) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.length;
    }

    @Override
    public Object getItem(int position) {
        return data[position];
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
            holder.tvData = (TextView) convertView.findViewById(R.id.tv_camera_data);
            holder.tvData.setTypeface(typeFace);
            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();
        holder.tvData.setText(data[position]);
        if (mSelect==position){
            holder.tvData.setBackgroundColor(context.getResources().getColor(R.color.colorWhite));    //点击项变色
        }else {
            holder.tvData.setBackgroundColor(context.getResources().getColor(R.color.colorLoginBack));   //其他的设为正常色
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
        TextView tvData;
    }
}
