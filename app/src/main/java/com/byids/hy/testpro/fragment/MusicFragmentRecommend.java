package com.byids.hy.testpro.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.byids.hy.testpro.R;
import com.byids.hy.testpro.adapter.MusicHotSongsAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by gqgz2 on 2016/11/29.
 */

public class MusicFragmentRecommend extends Fragment {


    @BindView(R.id.iv_music_my_collection)
    ImageView ivMusicMyCollection;
    @BindView(R.id.tv_music_my_collection)
    TextView tvMusicMyCollection;
    @BindView(R.id.ll_music_my_collection)
    LinearLayout llMusicMyCollection;
    @BindView(R.id.iv_music_songmenu1)
    ImageView ivMusicSongmenu1;
    @BindView(R.id.tv_music_songmenu1)
    TextView tvMusicSongmenu1;
    @BindView(R.id.ll_music_songmenu1)
    LinearLayout llMusicSongmenu1;
    @BindView(R.id.iv_music_songmenu2)
    ImageView ivMusicSongmenu2;
    @BindView(R.id.tv_music_songmenu2)
    TextView tvMusicSongmenu2;
    @BindView(R.id.ll_music_songmenu2)
    LinearLayout llMusicSongmenu2;
    @BindView(R.id.iv_music_songmenu3)
    ImageView ivMusicSongmenu3;
    @BindView(R.id.tv_music_songmenu3)
    TextView tvMusicSongmenu3;
    @BindView(R.id.ll_music_songmenu3)
    LinearLayout llMusicSongmenu3;
    @BindView(R.id.tv_hot_songs_recommend)
    TextView tvHotSongsRecommend;
    @BindView(R.id.lv_music_hot_recommend)
    ListView lvMusicHotRecommend;

    private MusicHotSongsAdapter adapter;
    private int[] hotMusicImg = {};
    private String[] hotMusicName = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};    //热歌歌名数组
    private String[] hotMusicSinger = {"aa", "aa", "aa", "aa", "aa", "aa", "aa", "aa", "aa", "aa"};  //热歌歌手数组

    public MusicFragmentRecommend() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.music_recommend_layout, null);
        ButterKnife.bind(this, view);
        lvMusicHotRecommend = (ListView) view.findViewById(R.id.lv_music_hot_recommend);
        initView();
        return view;
    }

    private void initView() {
        adapter = new MusicHotSongsAdapter(getContext(), hotMusicName, hotMusicSinger);
        lvMusicHotRecommend.setAdapter(adapter);
    }

    @OnClick({R.id.ll_music_my_collection, R.id.ll_music_songmenu1, R.id.ll_music_songmenu2, R.id.ll_music_songmenu3})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_music_my_collection:
                break;
            case R.id.ll_music_songmenu1:
                break;
            case R.id.ll_music_songmenu2:
                break;
            case R.id.ll_music_songmenu3:
                break;
        }
    }

}
