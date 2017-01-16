package com.byids.hy.testpro.newBean;

import java.io.Serializable;

/**
 * Created by gqgz2 on 2016/10/26.
 */

public class SoftwareMesgLight implements Serializable {
    private String display_name;
    private String display_type;

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getDisplay_type() {
        return display_type;
    }

    public void setDisplay_type(String display_type) {
        this.display_type = display_type;
    }
}
