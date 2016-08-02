package com.letv.leauto.cameracmdlibrary.connect.event;

/**
 * Created by tianwei on 16/7/28.
 */
public class EventOrPhotoRemovedNotificationEvent {
    private String param;

    public EventOrPhotoRemovedNotificationEvent(String param) {
        this.param = param;
    }

    public String getParam() {
        return param;
    }
}
