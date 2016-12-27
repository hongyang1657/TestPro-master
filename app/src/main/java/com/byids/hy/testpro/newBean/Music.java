package com.byids.hy.testpro.newBean;

import java.io.Serializable;

/**
 * Created by gqgz2 on 2016/10/25.
 */

public class Music implements Serializable {
    private int acitve;
    private String music_mach_type;
    private int muti_area_support;
    private int music_sound_card;
    private int voice_assistant_areaID;

    public int getActive() {
        return acitve;
    }

    public void setActive(int active) {
        this.acitve = active;
    }

    public String getMusic_mach_type() {
        return music_mach_type;
    }

    public void setMusic_mach_type(String music_mach_type) {
        this.music_mach_type = music_mach_type;
    }

    public int getMuti_area_support() {
        return muti_area_support;
    }

    public void setMuti_area_support(int muti_area_support) {
        this.muti_area_support = muti_area_support;
    }

    public int getMusic_sound_card() {
        return music_sound_card;
    }

    public void setMusic_sound_card(int music_sound_card) {
        this.music_sound_card = music_sound_card;
    }

    public int getVoice_assistant_areaID() {
        return voice_assistant_areaID;
    }

    public void setVoice_assistant_areaID(int voice_assistant_areaID) {
        this.voice_assistant_areaID = voice_assistant_areaID;
    }
}
