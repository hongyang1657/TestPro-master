package com.byids.hy.testpro.newBean;

import java.io.Serializable;

/**
 * Created by gqgz2 on 2016/10/26.
 */

public class HardwareMesgLight implements Serializable {
    private String subnet_id;
    private String device_id;
    private String type;
    private String length;
    private String serial_area_id;

    public String getSubnet_id() {
        return subnet_id;
    }

    public void setSubnet_id(String subnet_id) {
        this.subnet_id = subnet_id;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getSerial_area_id() {
        return serial_area_id;
    }

    public void setSerial_area_id(String serial_area_id) {
        this.serial_area_id = serial_area_id;
    }
}
