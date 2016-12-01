package com.byids.hy.testpro.View;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * com.byids.hy.testpro.View.MusicHotListView
 * Created by gqgz2 on 2016/11/30.
 */

public class MusicHotListView extends ListView{
    public MusicHotListView(Context context) {
        super(context);
    }

    public MusicHotListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MusicHotListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //设置为Integer.MAX_VALUE>>2 是listview全部展开
        int measureSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE>>2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, measureSpec);
    }
}
