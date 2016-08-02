package com.leautolink.leautocamera.net.http.httpcallback;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by tianwei on 16/3/22.
 */
public interface UploadFileCallBack {

    void onStart(long total);


    void onLoading(long total, long current);

    /**
     * 上传完成回调
     */
    void onFinish();

    /**
     * 上传完成，服务器返回数据回调
     *
     * @param call
     * @param response
     */
    void onResponse(Call call, Response response);

    void onCancel();

    /**
     * 流读写超时回调
     */
    void onTimeOut();

    /**
     * 请求出错回调
     * @param call
     * @param e
     */
    void onFailure(Call call, IOException e);

    /**
     * IO出错或者请求返回码不在[200..300)回调
     * @param e
     */
    void onError(Object e);
}
