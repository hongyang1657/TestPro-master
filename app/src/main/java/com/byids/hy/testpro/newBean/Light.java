package com.byids.hy.testpro.newBean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by gqgz2 on 2016/10/26.
 */

public class Light implements Serializable {
    private int active;
    //private String protocol;
    private List<LightAttr> array;

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    /*public String getProtocol() {
        return protocol;
    }*/

    /*public void setProtocol(String protocol) {
        this.protocol = protocol;
    }*/

    public List<LightAttr> getArray() {
        return array;
    }

    public void setArray(List<LightAttr> array) {
        this.array = array;
    }
}
