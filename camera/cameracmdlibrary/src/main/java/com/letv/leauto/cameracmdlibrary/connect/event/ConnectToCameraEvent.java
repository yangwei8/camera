package com.letv.leauto.cameracmdlibrary.connect.event;

/**
 * 记录仪发送session成功
 */
public class ConnectToCameraEvent {
    boolean isConnectCamera;

    public ConnectToCameraEvent(boolean isConnectCamera) {
        this.isConnectCamera = isConnectCamera;
    }

    public boolean isConnectCamera() {
        return isConnectCamera;
    }
}
