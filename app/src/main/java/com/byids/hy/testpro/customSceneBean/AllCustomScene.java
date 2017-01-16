package com.byids.hy.testpro.customSceneBean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by gqgz2 on 2017/1/6.
 */

public class AllCustomScene implements Serializable{
    private List<RoomCustomScene> array;

    public List<RoomCustomScene> getArray() {
        return array;
    }

    public void setArray(List<RoomCustomScene> array) {
        this.array = array;
    }
}
