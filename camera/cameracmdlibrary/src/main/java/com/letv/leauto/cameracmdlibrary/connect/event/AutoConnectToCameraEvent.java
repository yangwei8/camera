package com.letv.leauto.cameracmdlibrary.connect.event;

/**
 * 记录仪发送session成功
 */
public class AutoConnectToCameraEvent {
    int isConnectCamera;

    public AutoConnectToCameraEvent(int isConnectCamera) {
        this.isConnectCamera = isConnectCamera;
    }

    public int isConnectCamera() {
        return isConnectCamera;
    }
}
