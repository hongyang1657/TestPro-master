package com.byids.hy.testpro.Bean;

import java.io.Serializable;

/**
 * Created by asus on 2016/1/19.
 */
public class Aircondition implements Serializable {

    private int active;


    private String protocol;


    public void setActive(int active){

        this.active = active;

    }

    public int getActive(){

        return this.active;

    }

    public void setProtocol(String protocol){

        this.protocol = protocol;

    }

    public String getProtocol(){

        return this.protocol;

    }
}
