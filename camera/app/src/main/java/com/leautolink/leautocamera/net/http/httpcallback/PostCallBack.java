package com.leautolink.leautocamera.net.http.httpcallback;

import com.leautolink.leautocamera.domain.respone.ErrorInfo;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by tianwei on 16/3/22.
 */
public interface PostCallBack {
    void onFailure(Call call, IOException e);

    void onResponse(Call call, Response response);

    void onError(int errorCode);
}
