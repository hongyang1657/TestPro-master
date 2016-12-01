package com.byids.hy.testpro.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.byids.hy.testpro.R;
import com.byids.hy.testpro.fragment.MusicFragmentRankList;
import com.byids.hy.testpro.fragment.MusicFragmentRecommend;
import com.byids.hy.testpro.fragment.MusicFragmentSearch;

import java.util.ArrayList;
import java.util.List;


/*
*************** 音乐主界面 **************
* */
public class MusicActivity extends FragmentActivity {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private ImageView ivMusicback;

    private LayoutInflater mInflater;
    private List<String> mTitleList = new ArrayList<>();//页卡标题集合
    private MusicFragmentRecommend fragmentRecommend;
    private MusicFragmentRankList fragmentRankList;
    private MusicFragmentSearch fragmentSearch;

    private List<Fragment> mViewList = new ArrayList<>();//页卡视图集合

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setContentView(R.layout.music_activity_layout);
        init();
    }

    private void init(){
        mTabLayout = (TabLayout) findViewById(R.id.tab_music);
        mViewPager = (ViewPager) findViewById(R.id.vp_music);
        ivMusicback = (ImageView) findViewById(R.id.iv_music_back);
        ivMusicback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //mInflater = LayoutInflater.from(this);
        fragmentRecommend = new MusicFragmentRecommend();
        fragmentRankList = new MusicFragmentRankList();
        fragmentSearch = new MusicFragmentSearch();

        mViewList.add(fragmentRecommend);
        mViewList.add(fragmentRankList);
        mViewList.add(fragmentSearch);

        //添加页卡标题
        mTitleList.add("推荐");
        mTitleList.add("排行榜");
        mTitleList.add("搜索");

        mTabLayout.setTabMode(TabLayout.MODE_FIXED);//设置tab模式，当前为系统默认模式


        MyPagerAdapter mAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);//给ViewPager设置适配器
        mViewPager.setOffscreenPageLimit(4);  //多设置一页
        //mViewPager.addOnPageChangeListener(pagerChangeListener);
        mTabLayout.setupWithViewPager(mViewPager);//将TabLayout和ViewPager关联起来。
        mTabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorTextActive));    //设置tab选中项的颜色
        mTabLayout.setTabTextColors(getResources().getColor(R.color.colorBlack),getResources().getColor(R.color.colorTextActive));      //设置选中和未选中的字的颜色
        mTabLayout.setTabsFromPagerAdapter(mAdapter);//给Tabs设置适配器
    }

    class MyPagerAdapter extends FragmentPagerAdapter{

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return mViewList.get(position);
        }

        @Override
        public int getCount() {
            return mViewList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitleList.get(position);
        }
    }


}
