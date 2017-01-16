package com.byids.hy.testpro.newBean;

import java.io.Serializable;

/**
 * Created by gqgz2 on 2016/10/25.
 */

public class CommandData implements Serializable {
    private String ower;
    private String comments;
    private String address;
    private String telephone;
    private String Package;       //套餐标示
    private String protocol;
    private HomeProfile profile;

    public String getOwer() {
        return ower;
    }

    public void setOwer(String ower) {
        this.ower = ower;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getPackage() {
        return Package;
    }

    public void setPackage(String aPackage) {
        Package = aPackage;
    }

    public HomeProfile getProfile() {
        return profile;
    }

    public void setProfile(HomeProfile profile) {
        this.profile = profile;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
}
