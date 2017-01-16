package com.byids.hy.testpro.View;

/**
 * Created by gqgz2 on 2017/1/6.
 */

public class MyEventBusLight {
    private int position;
    private int msg;
    private int length;

    public MyEventBusLight(int position,int msg,int length) {
        this.position = position;
        this.msg = msg;
        this.length = length;
    }

    public int getPosition() {
        return position;
    }

    public int getMsg2() {
        return msg;
    }

    public int getLength() {
        return length;
    }
}
