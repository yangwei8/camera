package com.letv.leauto.cameracmdlibrary.connect.model;

import org.json.JSONObject;

/**
 *
 * 发送完命令后的回调接口
 *
 * Created by lixinlei on 15/11/5.
 */


public  interface CameraMessageCallback
{
    void onReceiveErrorMessage(CameraMessage cameraMessage, JSONObject jsonObject);

    void onReceiveMessage(CameraMessage cameraMessage, JSONObject jsonObject);

    void onReceiveNotification(JSONObject jsonObject);
}
