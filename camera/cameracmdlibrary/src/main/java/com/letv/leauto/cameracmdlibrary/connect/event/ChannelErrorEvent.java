package com.letv.leauto.cameracmdlibrary.connect.event;

import org.json.JSONObject;

/**
 * Created by lixinlei on 15/11/5.
 */
public class ChannelErrorEvent {




    private int errCode;

    private JSONObject jsonObject;

    public ChannelErrorEvent(int errCode, JSONObject jsonObject) {
        this.errCode = errCode;
        this.jsonObject = jsonObject;
    }

    public ChannelErrorEvent(int errCode) {
        this.errCode = errCode;
    }

    public int getErrCode() {
        return errCode;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }
}
