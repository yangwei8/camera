package com.letv.leauto.cameracmdlibrary.connect.event;

/**
 * Created by lixinlei on 16/4/7.
 */
public class AddEventFileEvent {
    private String param;

    public AddEventFileEvent(String param) {
        this.param = param;
    }

    public String getParam() {
        return param;
    }
}
