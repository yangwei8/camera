package com.letv.leauto.cameracmdlibrary.connect.event;

/**
 * Created by lixinlei on 16/3/1.
 */
public class GPSEndEvent {
    private String jsonObject;

    //    public GPSEvent(JSONObject jsonObject) {
//        this.jsonObject = jsonObject;
//    }
    public GPSEndEvent(String jsonObject) {
        this.jsonObject = jsonObject;
    }

    public String getJsonObject() {
        return jsonObject;
    }
}
