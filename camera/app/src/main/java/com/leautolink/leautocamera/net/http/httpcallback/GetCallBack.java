package com.leautolink.leautocamera.net.http.httpcallback;

import java.io.IOException;

import okhttp3.Call;

/**
 * Http请求回调
 * Created by tianwei1 on 2016/3/4.
 */
public interface GetCallBack {
    void onFailure(Call call, IOException e);

    void onResponse(Call call, Object response);

    void onError(String error);
}
