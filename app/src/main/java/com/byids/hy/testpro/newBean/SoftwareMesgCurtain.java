package com.byids.hy.testpro.newBean;

import java.io.Serializable;

/**
 * Created by gqgz2 on 2016/10/26.
 */

public class SoftwareMesgCurtain implements Serializable {
    private String tag;
    private String type;
    private String displayer_name;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDisplayer_name() {
        return displayer_name;
    }

    public void setDisplayer_name(String displayer_name) {
        this.displayer_name = displayer_name;
    }
}
