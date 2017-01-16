package com.byids.hy.testpro.customSceneBean;

import java.io.Serializable;

/**
 * Created by gqgz2 on 2017/1/6.
 */

public class LoopLight implements Serializable{
    private String loopLightName;
    private int loopLightIndex;
    private int loopLightValue;

    public String getLoopLightName() {
        return loopLightName;
    }

    public void setLoopLightName(String loopLightName) {
        this.loopLightName = loopLightName;
    }

    public int getLoopLightIndex() {
        return loopLightIndex;
    }

    public void setLoopLightIndex(int loopLightIndex) {
        this.loopLightIndex = loopLightIndex;
    }

    public int getLoopLightValue() {
        return loopLightValue;
    }

    public void setLoopLightValue(int loopLightValue) {
        this.loopLightValue = loopLightValue;
    }
}
