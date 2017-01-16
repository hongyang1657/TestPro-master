package com.byids.hy.testpro.customSceneBean;

import java.io.Serializable;

/**
 * Created by gqgz2 on 2017/1/6.
 */

public class DetailCustomScene implements Serializable{
    private String sceneName;
    private int sceneIconIndex;
    private LightDetail lightDetail;
    private boolean isOpenCurtain = false;

    public String getSceneName() {
        return sceneName;
    }

    public void setSceneName(String sceneName) {
        this.sceneName = sceneName;
    }

    public int getSceneIconIndex() {
        return sceneIconIndex;
    }

    public void setSceneIconIndex(int sceneIconIndex) {
        this.sceneIconIndex = sceneIconIndex;
    }

    public LightDetail getLightDetail() {
        return lightDetail;
    }

    public void setLightDetail(LightDetail lightDetail) {
        this.lightDetail = lightDetail;
    }

    public boolean isOpenCurtain() {
        return isOpenCurtain;
    }

    public void setOpenCurtain(boolean openCurtain) {
        isOpenCurtain = openCurtain;
    }
}
