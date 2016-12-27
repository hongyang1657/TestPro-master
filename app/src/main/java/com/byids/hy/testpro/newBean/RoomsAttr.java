package com.byids.hy.testpro.newBean;

import java.io.Serializable;

/**
 * Created by gqgz2 on 2016/10/26.
 * 房间数组
 */

public class RoomsAttr implements Serializable {
    private int active;
    private String protocol;
    private String room_db_name;
    private String room_zh_name;
    private RoomDevMesg room_dev_mesg;

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

    public String getRoom_db_name() {
        return room_db_name;
    }

    public void setRoom_db_name(String room_db_name) {
        this.room_db_name = room_db_name;
    }

    public String getRoom_zh_name() {
        return room_zh_name;
    }

    public void setRoom_zh_name(String room_zh_name) {
        this.room_zh_name = room_zh_name;
    }

    public RoomDevMesg getRoom_dev_mesg() {
        return room_dev_mesg;
    }

    public void setRoom_dev_mesg(RoomDevMesg room_dev_mesg) {
        this.room_dev_mesg = room_dev_mesg;
    }
}
