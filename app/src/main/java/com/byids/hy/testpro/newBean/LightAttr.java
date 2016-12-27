package com.byids.hy.testpro.newBean;

import java.io.Serializable;

/**
 * Created by gqgz2 on 2016/10/26.
 */

public class LightAttr implements Serializable {
    private int active;
    private String protocol;
    private int number;
    private SoftwareMesgLight software_mesg;
    private HardwareMesgLight hardware_mesg;

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

    public SoftwareMesgLight getSoftware_mesg() {
        return software_mesg;
    }

    public void setSoftware_mesg(SoftwareMesgLight software_mesg) {
        this.software_mesg = software_mesg;
    }

    public HardwareMesgLight getHardware_mesg() {
        return hardware_mesg;
    }

    public void setHardware_mesg(HardwareMesgLight hardware_mesg) {
        this.hardware_mesg = hardware_mesg;
    }
}
