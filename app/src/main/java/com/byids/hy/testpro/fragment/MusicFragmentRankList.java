package com.byids.hy.testpro.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.byids.hy.testpro.R;
import com.byids.hy.testpro.adapter.MyFragmentAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gqgz2 on 2016/11/29.
 */

public class MusicFragmentRankList extends Fragment {


    private RadioButton rbMusicNewSongs;
    private RadioButton rbMusicHotSongs;
    private RadioButton rbMusicClassicsSongs;
    private RadioButton rbMusicNetSongs;
    private RadioGroup rgMusicRank;
    private ViewPager vpMusicRank;

    private List<Fragment> mViewList = new ArrayList<>();//页卡视图集合
    private MusicFragmentListView musicFragmentListView;

    public MusicFragmentRankList() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.music_rank_list_layout, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        rbMusicNewSongs = (RadioButton) view.findViewById(R.id.rb_music_new_songs);
        rbMusicHotSongs = (RadioButton) view.findViewById(R.id.rb_music_hot_songs);
        rbMusicClassicsSongs = (RadioButton) view.findViewById(R.id.rb_music_classics_songs);
        rbMusicNetSongs = (RadioButton) view.findViewById(R.id.rb_music_net_songs);
        vpMusicRank = (ViewPager) view.findViewById(R.id.vp_music_rank);
        rgMusicRank = (RadioGroup) view.findViewById(R.id.rg_music_rank);
        //radioGroup 选择事件监听
        rgMusicRank.setOnCheckedChangeListener(checkedChangeListener);

        for (int i=0;i<4;i++){
            musicFragmentListView = new MusicFragmentListView();
            mViewList.add(musicFragmentListView);
        }

        MyFragmentAdapter adapter = new MyFragmentAdapter(getFragmentManager(),mViewList);
        vpMusicRank.setAdapter(adapter);
        vpMusicRank.setOffscreenPageLimit(5);  //多设置一页
        vpMusicRank.addOnPageChangeListener(pageChangeListener);
    }

    //----------------------radioGroup 选择事件监听------------------------
    RadioGroup.OnCheckedChangeListener checkedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId){
                case R.id.rb_music_new_songs:
                    vpMusicRank.setCurrentItem(0,true);
                    break;
                case R.id.rb_music_hot_songs:
                    vpMusicRank.setCurrentItem(1,true);
                    break;
                case R.id.rb_music_classics_songs:
                    vpMusicRank.setCurrentItem(2,true);
                    break;
                case R.id.rb_music_net_songs:
                    vpMusicRank.setCurrentItem(3,true);
                    break;
                default:
                    break;
            }
        }
    };

    //----------------------viewpager 切换页面监听----------------------------
    ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            switch (position){
                case 0:
                    rbMusicNewSongs.setChecked(true);
                    break;
                case 1:
                    rbMusicHotSongs.setChecked(true);
                    break;
                case 2:
                    rbMusicClassicsSongs.setChecked(true);
                    break;
                case 3:
                    rbMusicNetSongs.setChecked(true);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
