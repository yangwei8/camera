package com.leautolink.leautocamera.event;

/**
 * 去下载记录仪视频的event
 * Created by tianwei on 16/4/9.
 */
public class GoToDownloadCameraVideoEvent {
    private boolean isGoToDownloadCameraVideo;

    public GoToDownloadCameraVideoEvent(boolean isGoToDownloadCameraVideo) {
        this.isGoToDownloadCameraVideo = isGoToDownloadCameraVideo;
    }

    public boolean isGoToDownloadCameraVideo() {
        return isGoToDownloadCameraVideo;
    }
}
