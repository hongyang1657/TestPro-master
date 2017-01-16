package com.byids.hy.testpro;

/**
 * Created by gqgz2 on 2016/9/19.
 * fragment向activity传递数据
 */
public class MyEventBusCustom {
    private String mMsg;

    public MyEventBusCustom(String msg){
        mMsg = msg;
    }

    public String getmMsg() {
        return mMsg;
    }
}
