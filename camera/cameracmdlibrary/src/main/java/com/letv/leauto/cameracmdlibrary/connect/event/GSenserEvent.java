package com.letv.leauto.cameracmdlibrary.connect.event;

/**
 * Created by lixinlei on 16/2/29.
 */
public class GSenserEvent {
    private String jsonObject;

    //    public GPSEvent(JSONObject jsonObject) {
//        this.jsonObject = jsonObject;
//    }
    public GSenserEvent(String jsonObject) {
        this.jsonObject = jsonObject;
    }

    public String getJsonObject() {
        return jsonObject;
    }
}
