package com.byids.hy.testpro.View;

/**
 * Created by gqgz2 on 2017/1/9.
 */

public class MyEventBusLightSwitch {
    private boolean isOpen;
    private int position;

    public MyEventBusLightSwitch(int position,boolean isOpen) {
        this.position = position;
        this.isOpen = isOpen;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public int getPosition() {
        return position;
    }
}
