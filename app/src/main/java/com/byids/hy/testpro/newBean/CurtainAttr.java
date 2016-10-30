package com.byids.hy.testpro.newBean;

import java.util.List;

/**
 * Created by gqgz2 on 2016/10/26.
 */

public class CurtainAttr {
    private int active;
    private String protocol;
    private int number;
    private SoftwareMesgCurtain software_mesg;

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public SoftwareMesgCurtain getSoftware_mesg() {
        return software_mesg;
    }

    public void setSoftware_mesg(SoftwareMesgCurtain software_mesg) {
        this.software_mesg = software_mesg;
    }
}
