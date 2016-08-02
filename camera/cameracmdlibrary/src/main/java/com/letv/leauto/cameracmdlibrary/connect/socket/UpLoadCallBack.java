package com.letv.leauto.cameracmdlibrary.connect.socket;

/**
 * Created by lixinlei on 15/10/29.
 */
public abstract class UpLoadCallBack {
    public abstract void onStart();
    public abstract void onEnd();
    public abstract void onFailure();
    public abstract void onProgress(int paramInt);
}
