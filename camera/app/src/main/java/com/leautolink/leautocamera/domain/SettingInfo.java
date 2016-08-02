package com.leautolink.leautocamera.domain;

/**
 * Created by lixinlei on 15/11/26.
 */
public class SettingInfo {
    /**是否记录声音*/
    private String micphone;
    /**是否开机录像*/
    private String record_mode;
    /**是否显示水印*/
    private String stamp;
    /**视频分辨率*/
    private String video_resolution;
    /**灵敏度灵敏性*/
    private String event_sensitivity;


    public String getMicphone() {
        return micphone;
    }

    public void setMicphone(String micphone) {
        this.micphone = micphone;
    }

    public String getRecord_mode() {
        return record_mode;
    }

    public void setRecord_mode(String record_mode) {
        this.record_mode = record_mode;
    }

    public String getStamp() {
        return stamp;
    }

    public void setStamp(String stamp) {
        this.stamp = stamp;
    }

    public String getVideo_resolution() {
        return video_resolution;
    }

    public void setVideo_resolution(String video_resolution) {
        this.video_resolution = video_resolution;
    }

    public String getEvent_sensitivity() {
        return event_sensitivity;
    }

    public void setEvent_sensitivity(String event_sensitivity) {
        this.event_sensitivity = event_sensitivity;
    }
}
