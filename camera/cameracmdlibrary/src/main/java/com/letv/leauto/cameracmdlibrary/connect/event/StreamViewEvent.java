package com.letv.leauto.cameracmdlibrary.connect.event;

/**
 * Created by lixinlei on 15/9/29.
 */
public class StreamViewEvent {

    private int type;

    public StreamViewEvent(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
