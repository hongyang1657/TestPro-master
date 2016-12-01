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
 * Created by gqgz2 on 2016/11/29.
 */

public class MusicHotSongsAdapter extends BaseAdapter{
    private LayoutInflater inflater;
    private Context context;
    private Typeface typeFace;
    private String[] hotMusicName;
    private String[] hotMusicSinger;


    public MusicHotSongsAdapter(Context context,String[] hotMusicName,String[] hotMusicSinger) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.hotMusicName = hotMusicName;
        this.hotMusicSinger = hotMusicSinger;
    }

    @Override
    public int getCount() {
        return hotMusicName.length;
    }

    @Override
    public Object getItem(int position) {
        return hotMusicName[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView==null){
            convertView = inflater.inflate(R.layout.music_hot_recommend_item,null);
            holder = new ViewHolder();
            typeFace = Typeface.createFromAsset(context.getAssets(),"fonts/xiyuanti.ttf");
            holder.tvHotSongs = (TextView) convertView.findViewById(R.id.tv_music_hot_songs);
            holder.tvHotSongsSinger = (TextView) convertView.findViewById(R.id.tv_music_hot_songs_singer);
            holder.tvHotSongs.setTypeface(typeFace);
            holder.tvHotSongsSinger.setTypeface(typeFace);
            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();
        holder.tvHotSongs.setText(hotMusicName[position]);
        holder.tvHotSongsSinger.setText(hotMusicSinger[position]);
        return convertView;
    }

    class ViewHolder{
        TextView tvHotSongs;
        TextView tvHotSongsSinger;
    }
}
