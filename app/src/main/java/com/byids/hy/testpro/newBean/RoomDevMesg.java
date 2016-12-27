package com.byids.hy.testpro.newBean;

import java.io.Serializable;

/**
 * Created by gqgz2 on 2016/10/26.
 */

public class RoomDevMesg implements Serializable {
    private Light light;
    private Curtain curtain;
    private Panel panel;
    private RoomMusic music;

    public Light getLight() {
        return light;
    }

    public void setLight(Light light) {
        this.light = light;
    }

    public Curtain getCurtain() {
        return curtain;
    }

    public void setCurtain(Curtain curtain) {
        this.curtain = curtain;
    }

    public RoomMusic getMusic() {
        return music;
    }

    public void setMusic(RoomMusic music) {
        this.music = music;
    }

    public Panel getPanel() {
        return panel;
    }

    public void setPanel(Panel panel) {
        this.panel = panel;
    }
}
