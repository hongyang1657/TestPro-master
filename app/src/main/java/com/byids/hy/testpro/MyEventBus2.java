package com.byids.hy.testpro;

/**
 * Created by gqgz2 on 2016/9/19.
 * EventBus activity向Fragment传递数据
 */
public class MyEventBus2 {
    private String mMsg;

    public MyEventBus2(String msg){
        mMsg = msg;
    }

    public String getmMsg() {
        return mMsg;
    }
}
