package com.byids.hy.testpro.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.byids.hy.testpro.R;
import com.byids.hy.testpro.adapter.MusicHotSongsAdapter;

/**
 * Created by gqgz2 on 2016/11/29.
 */

public class MusicFragmentSearch extends Fragment{

    private EditText etSearch;
    private ImageView ivSearch;
    private ListView lvSearch;
    private MusicHotSongsAdapter adapter;
    private String[] hotMusicName = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};    //热歌歌名数组
    private String[] hotMusicSinger = {"aa", "aa", "aa", "aa", "aa", "aa", "aa", "aa", "aa", "aa"};  //热歌歌手数组

    public MusicFragmentSearch() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.music_fragment_search,container,false);
        initView(view);
        return view;
    }

    private void initView(View view){
        etSearch = (EditText) view.findViewById(R.id.et_music_search);
        ivSearch = (ImageView) view.findViewById(R.id.iv_music_search);
        lvSearch = (ListView) view.findViewById(R.id.lv_music_search);
        adapter = new MusicHotSongsAdapter(getContext(),hotMusicName,hotMusicSinger);
        lvSearch.setAdapter(adapter);
    }
}
