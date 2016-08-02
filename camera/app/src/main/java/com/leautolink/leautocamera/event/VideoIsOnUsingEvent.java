package com.leautolink.leautocamera.event;

/**
 * 视频是否在占用
 * Created by tianwei on 16/4/9.
 */
public class VideoIsOnUsingEvent {

    private boolean isVideoCanDelete;
    private boolean isVideoCanDownload;

    public VideoIsOnUsingEvent(boolean isVideoCanDelete, boolean isVideoCanDownload) {
        this.isVideoCanDelete = isVideoCanDelete;
        this.isVideoCanDownload = isVideoCanDownload;
    }

    public boolean isVideoCanDelete() {
        return isVideoCanDelete;
    }

    public boolean isVideoCanDownload() {
        return isVideoCanDownload;
    }
}
