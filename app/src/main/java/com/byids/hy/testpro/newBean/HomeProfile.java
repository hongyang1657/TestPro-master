package com.byids.hy.testpro.newBean;

import org.json.JSONObject;

/**
 *
 * Created by gqgz2 on 2016/10/25.
 */

public class HomeProfile {
    private Music music;
    private Camera camera;
    private Door door;
    private Security security;
    private Rooms rooms;

    public Music getMusic() {
        return music;
    }

    public void setMusic(Music music) {
        this.music = music;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public Door getDoor() {
        return door;
    }

    public void setDoor(Door door) {
        this.door = door;
    }

    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    public Rooms getRooms() {
        return rooms;
    }

    public void setRooms(Rooms rooms) {
        this.rooms = rooms;
    }
}
