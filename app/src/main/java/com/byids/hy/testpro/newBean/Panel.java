package com.byids.hy.testpro.newBean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by gqgz2 on 2016/12/24.
 */

public class Panel implements Serializable {
    private int active;
    private List<PanelAttr> array;

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public List<PanelAttr> getArray() {
        return array;
    }

    public void setArray(List<PanelAttr> array) {
        this.array = array;
    }
}
