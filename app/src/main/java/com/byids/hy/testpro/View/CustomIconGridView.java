package com.byids.hy.testpro.View;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * com.byids.hy.testpro.View.CustomIconGridView
 * Created by gqgz2 on 2016/12/16.
 */

public class CustomIconGridView extends GridView{
    public CustomIconGridView(Context context) {
        super(context);
    }

    public CustomIconGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomIconGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
