package com.byids.hy.testpro.customSceneBean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by gqgz2 on 2017/1/6.
 */

public class LightDetail implements Serializable {
    private int allLightValue;
    private List<LoopLight> array;

    public int getAllLight() {
        return allLightValue;
    }

    public void setAllLight(int allLightValue) {
        this.allLightValue = allLightValue;
    }

    public List<LoopLight> getArray() {
        return array;
    }

    public void setArray(List<LoopLight> array) {
        this.array = array;
    }
}
