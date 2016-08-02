package com.letv.leauto.cameracmdlibrary.connect.socket;

/**
 * Created by lixinlei on 15/10/29.
 */
public abstract class DownLoadCallBack {
    public abstract void onStart(String path);
    public abstract void onEnd();
    public abstract void onEnd(String md5);
    public abstract void onFailure();
    public abstract void onProgress(int paramInt);
}
