package com.leautolink.leautocamera.net.http.httpcallback;

import java.io.IOException;

import okhttp3.Call;

/**
 * Created by tianwei1 on 2016/3/4.
 */
public interface DownLoadCallBack {
    void onFailure(Call call, IOException e);

    void onStart(long total);

    void onLoading(long current, long total);

    void onSucceed();

    void onSdCardLackMemory(long total, long avaiable);

    void onCancel();

    void onError(IOException e);
}
