package com.leautolink.leautocamera.event;

/**
 * Created by lixinlei on 16/3/12.
 */
public class ConnectSuccessEvent {
    private   String  rtspUrl ;

    public ConnectSuccessEvent(String rtspUrl) {
        this.rtspUrl = rtspUrl;
    }

    public String getRtspUrl() {
        return rtspUrl;
    }
}
