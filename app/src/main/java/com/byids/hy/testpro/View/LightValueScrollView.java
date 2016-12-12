package com.byids.hy.testpro.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

/**
 * com.byids.hy.testpro.View.LightValueScrollView
 * ********************灯光滑动条的********************
 * Created by gqgz2 on 2016/12/5.
 */

public class LightValueScrollView extends HorizontalScrollView{
    private Context context;
    private ScrollViewListenner listenner;
    private LightValueScrollView lightValueScrollView;

    public LightValueScrollView(Context context) {
        super(context);
        this.context = context;
    }

    public LightValueScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public LightValueScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        lightValueScrollView = this;
        return super.onTouchEvent(ev);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        // TODO Auto-generated method stub
        super.onScrollChanged(l, t, oldl, oldt);
        if (null != listenner) {
            this.listenner.onScrollChanged(lightValueScrollView, l, t, oldl, oldt);
        }
    }

    public interface ScrollViewListenner {
        public void onScrollChanged(LightValueScrollView view, int l, int t, int oldl, int oldt);
    }

    public void setScrollViewListenner(ScrollViewListenner listenner) {
        this.listenner = listenner;
    }
}
