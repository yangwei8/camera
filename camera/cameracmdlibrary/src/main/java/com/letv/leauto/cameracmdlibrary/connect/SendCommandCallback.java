package com.letv.leauto.cameracmdlibrary.connect;


import com.letv.leauto.cameracmdlibrary.connect.model.CameraMessage;

import org.json.JSONObject;

/**
 * Created by lixinlei on 15/9/22.
 */
public interface SendCommandCallback {
    void onFail(CameraMessage cameraMessage, JSONObject jsonObject);

    void onSuccess(CameraMessage cameraMessage, JSONObject jsonObject);
}

