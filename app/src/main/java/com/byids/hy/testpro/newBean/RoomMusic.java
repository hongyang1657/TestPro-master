package com.byids.hy.testpro.newBean;

import java.io.Serializable;

/**
 * Created by gqgz2 on 2016/10/26.
 */

public class RoomMusic implements Serializable {
    private int active;
    private String area;

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
}
