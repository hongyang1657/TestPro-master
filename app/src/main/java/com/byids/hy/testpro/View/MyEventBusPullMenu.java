package com.byids.hy.testpro.View;

/**
 * Created by gqgz2 on 2017/2/15.
 */

public class MyEventBusPullMenu {
    private boolean isScrolling;
    private int scrollLocal;         //scrollView滑动到的位置 :-1不确定 ， 0：顶部 。。。

    public MyEventBusPullMenu(boolean isScrolling,int scrollLocal) {
        this.isScrolling = isScrolling;
        this.scrollLocal = scrollLocal;
    }

    public boolean isScrolling() {
        return isScrolling;
    }

    public int getScrollLocal() {
        return scrollLocal;
    }
}
