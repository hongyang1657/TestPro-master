package com.byids.hy.testpro.customSceneBean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by gqgz2 on 2017/1/6.
 */

public class RoomCustomScene implements Serializable{
    private String roomDBName;
    private int roomIndex;
    private List<DetailCustomScene> array;

    public String getRoomDBName() {
        return roomDBName;
    }

    public void setRoomDBName(String roomDBName) {
        this.roomDBName = roomDBName;
    }

    public int getRoomIndex() {
        return roomIndex;
    }

    public void setRoomIndex(int roomIndex) {
        this.roomIndex = roomIndex;
    }

    public List<DetailCustomScene> getArray() {
        return array;
    }

    public void setArray(List<DetailCustomScene> array) {
        this.array = array;
    }
}
