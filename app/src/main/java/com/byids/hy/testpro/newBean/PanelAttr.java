package com.byids.hy.testpro.newBean;

import java.io.Serializable;

/**
 * Created by gqgz2 on 2016/12/24.
 */

public class PanelAttr implements Serializable {
    private PanelSoftwareMesg software_mesg;

    public PanelSoftwareMesg getSoftware_mesg() {
        return software_mesg;
    }

    public void setSoftware_mesg(PanelSoftwareMesg software_mesg) {
        this.software_mesg = software_mesg;
    }
}
