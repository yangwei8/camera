package com.leautolink.leautocamera.net.http.httpcallback;

import java.io.IOException;

import okhttp3.Call;

/**
 * Created by tianwei1 on 2016/3/5.
 */
public interface GetBeanCallBack<T> {
    void onFailure(Call call, IOException e);
    void onResponse(T t);
}
