package com.byids.hy.testpro.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.byids.hy.testpro.R;
import com.byids.hy.testpro.adapter.MusicHotSongsAdapter;

/**
 * 音乐排行榜4个分类的listView 复用一个Fragment
 * Created by gqgz2 on 2016/11/30.
 */

public class MusicFragmentListView extends Fragment{

    private ListView listView;
    private MusicHotSongsAdapter adapter;
    private String[] hotMusicName = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};    //热歌歌名数组
    private String[] hotMusicSinger = {"aa", "aa", "aa", "aa", "aa", "aa", "aa", "aa", "aa", "aa"};  //热歌歌手数组

    public MusicFragmentListView() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.music_fragment_lv,container,false);
        initView(view);
        return view;
    }
    private void initView(View view){
        listView = (ListView) view.findViewById(R.id.lv_music_list);
        adapter = new MusicHotSongsAdapter(getContext(),hotMusicName,hotMusicSinger);
        listView.setAdapter(adapter);
    }
}
