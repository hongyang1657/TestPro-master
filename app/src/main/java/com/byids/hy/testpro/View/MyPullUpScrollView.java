package com.byids.hy.testpro.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ScrollView;
import android.widget.Scroller;

import com.byids.hy.testpro.PullUpMenuListener;
import com.byids.hy.testpro.ScrollViewListener;

/**
 * Created by hy on 2016/8/19.
 * com.byids.hy.testpro.View.MyPullUpScrollView
 */
public class MyPullUpScrollView extends ScrollView{

    private PullUpMenuListener pullUpMenuListener;
    private View inner;
    private ScrollViewListener scrollViewListener;
    private Scroller mScroller;


    //移动因子, 是一个百分比, 比如手指移动了100px, 那么View就只移动50px
    //目的是达到一个延迟的效果
    private static final float MOVE_FACTOR = 0.5f;

    //松开手指后, 界面回到正常位置需要的动画时间
    private static final int ANIM_TIME = 300;

    //ScrollView的子View， 也是ScrollView的唯一一个子View
    private View contentView;

    //手指按下时的Y值, 用于在移动时计算移动距离
    //如果按下时不能上拉和下拉， 会在手指移动时更新为当前手指的Y值
    private float startY;

    //用于记录正常的布局位置
    private Rect originalRect = new Rect();

    //手指按下时记录是否可以继续下拉
    private boolean canPullDown = false;

    //手指按下时记录是否可以继续上拉
    private boolean canPullUp = false;

    //在手指滑动的过程中记录是否移动了布局
    private boolean isMoved = false;

    public MyPullUpScrollView(Context context) {
        super(context);
        mScroller = new Scroller(context);
    }

    public MyPullUpScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScroller = new Scroller(context);
    }

    public MyPullUpScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScroller = new Scroller(context);
    }

    //自定义方法  可以设定滚动的时间
    public void smoothScrollToSlow(int fx,int fy,int duration){
        int dx = fx - getScrollX();
        int dy = fy - getScrollY();
        smoothScrollBySlow(dx,dy,duration);
    }

    //调用此方法设置滚动的相对偏移
    public void smoothScrollBySlow(int dx, int dy,int duration) {
        //设置mScroller的滚动偏移量
        mScroller.startScroll(getScrollX(), getScrollY(), dx, dy,duration);//scrollView使用的方法（因为可以触摸拖动）
//      mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx, dy, duration);  //普通view使用的方法
        invalidate();//这里必须调用invalidate()才能保证computeScroll()会被调用，否则不一定会刷新界面，看不到滚动效果
    }

    @Override
    public void computeScroll() {
        //先判断mScroller滚动是否完成
        if (mScroller.computeScrollOffset()) {
            //这里调用View的scrollTo()完成实际的滚动
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            //必须调用该方法，否则不一定能看到滚动效果
            postInvalidate();
        }
        super.computeScroll();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (contentView == null) {
            return super.dispatchTouchEvent(ev);
        }

        int action = ev.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:

                //判断是否可以上拉和下拉
                canPullDown = isCanPullDown();
                canPullUp = isCanPullUp();

                //记录按下时的Y值
                startY = ev.getY();
                break;

            case MotionEvent.ACTION_UP:

                if(!isMoved) break;  //如果没有移动布局， 则跳过执行

                // 开启动画
                TranslateAnimation anim = new TranslateAnimation(0, 0, contentView.getTop(),
                        originalRect.top);
                anim.setDuration(ANIM_TIME);

                contentView.startAnimation(anim);

                // 设置回到正常的布局位置
                contentView.layout(originalRect.left, originalRect.top,
                        originalRect.right, originalRect.bottom);

                //将标志位设回false
                canPullDown = false;
                canPullUp = false;
                isMoved = false;

                break;
            case MotionEvent.ACTION_MOVE:

                //在移动的过程中， 既没有滚动到可以上拉的程度， 也没有滚动到可以下拉的程度
                if(!canPullDown && !canPullUp) {
                    startY = ev.getY();
                    canPullDown = isCanPullDown();
                    canPullUp = isCanPullUp();

                    break;
                }

                //计算手指移动的距离
                float nowY = ev.getY();
                int deltaY = (int) (nowY - startY);

                //是否应该移动布局
                boolean shouldMove =
                        (canPullDown && deltaY > 0)    //可以下拉， 并且手指向下移动
                                || (canPullUp && deltaY< 0)    //可以上拉， 并且手指向上移动
                                || (canPullUp && canPullDown); //既可以上拉也可以下拉（这种情况出现在ScrollView包裹的控件比ScrollView还小）

                if(shouldMove){
                    //计算偏移量
                    int offset = (int)(deltaY * MOVE_FACTOR);

                    //随着手指的移动而移动布局
                    contentView.layout(originalRect.left, originalRect.top + offset,
                            originalRect.right, originalRect.bottom + offset);

                    isMoved = true;  //记录移动了布局
                }

                break;
            default:
                break;
        }

        return super.dispatchTouchEvent(ev);    //可以滑动上拉菜单  ，可以点击
    }

    //设置两个ScrollView联动监听
    public void setOnConnectionListener(PullUpMenuListener pullUpMenuListener){
        this.pullUpMenuListener = pullUpMenuListener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (pullUpMenuListener != null) {
            //*********两个ScrollView联动的倍率是3，这里如果做修改，MyFragment里的数值都需要修改***********
            pullUpMenuListener.onScrollConnection(this,l/3,t/3,oldl,oldt);
        }
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChanged(this, l, t, oldl, oldt);
        }
    }

    public void commOnTouchEvent(MotionEvent ev) {
        scrollViewListener.onCommOnTouchEvent(this,ev);
    }



    @SuppressLint("MissingSuperCall")
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();          //
        if (getChildCount()>0){
            inner = getChildAt(0);
            contentView = getChildAt(0);         //
        }
    }

    //
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(contentView == null) return;

        //ScrollView中的唯一子控件的位置信息, 这个位置信息在整个控件的生命周期中保持不变
        originalRect.set(contentView.getLeft(), contentView.getTop(), contentView
                .getRight(), contentView.getBottom());
    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener){
        this.scrollViewListener = scrollViewListener;
    }

    //添加滑动阻尼   velocityY/3
    @Override
    public void fling(int velocityY) {
        super.fling(velocityY/2);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //Log.i("result", "onInterceptTouchEvent:=======上拉Scroll预处理========= ");
        return super.onInterceptTouchEvent(ev);
        //return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (inner==null){
            return super.onTouchEvent(ev);
            //return false;
        }else {
            commOnTouchEvent(ev);
        }
        //Log.i("result", "onTouchEvent: =======上拉Scroll处理事件============");
        return super.onTouchEvent(ev);
        //return false;
    }


    /**
     * 判断是否滚动到顶部
     */
    private boolean isCanPullDown() {
        return getScrollY() == 0 ||
                contentView.getHeight() < getHeight() + getScrollY();
    }

    /**
     * 判断是否滚动到底部
     */
    private boolean isCanPullUp() {
        return  contentView.getHeight() <= getHeight() + getScrollY();
    }

}
