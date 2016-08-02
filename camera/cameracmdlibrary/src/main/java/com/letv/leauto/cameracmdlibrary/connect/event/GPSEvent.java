package com.letv.leauto.cameracmdlibrary.connect.event;

/**
 * Created by lixinlei on 16/2/26.
 */
public class GPSEvent {

    private String jsonObject;

//    public GPSEvent(JSONObject jsonObject) {
//        this.jsonObject = jsonObject;
//    }
    public GPSEvent(String jsonObject) {
        this.jsonObject = jsonObject;
    }

    public String getJsonObject() {
        return jsonObject;
    }
}
