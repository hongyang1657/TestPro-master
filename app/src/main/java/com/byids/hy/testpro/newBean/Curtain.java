package com.byids.hy.testpro.newBean;

import java.util.List;

/**
 * Created by gqgz2 on 2016/10/26.
 */

public class Curtain {
    private int active;
    private String protocol;
    private List<CurtainAttr> array;

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public List<CurtainAttr> getArray() {
        return array;
    }

    public void setArray(List<CurtainAttr> array) {
        this.array = array;
    }
}
