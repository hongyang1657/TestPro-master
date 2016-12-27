package com.byids.hy.testpro.newBean;

import java.io.Serializable;

/**
 * Created by gqgz2 on 2016/10/28.
 */

public class CommandUser implements Serializable {
    private String hid;
    private String loginName;
    private int password;

    public String getHid() {
        return hid;
    }

    public void setHid(String hid) {
        this.hid = hid;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public int getPassword() {
        return password;
    }

    public void setPassword(int password) {
        this.password = password;
    }
}
