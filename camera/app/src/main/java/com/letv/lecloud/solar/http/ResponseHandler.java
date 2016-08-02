package com.letv.lecloud.solar.http;

import android.os.Looper;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.ResponseHandlerInterface;
import com.letv.lecloud.solar.common.Constants;

import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URI;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NoHttpResponseException;
import cz.msebera.android.httpclient.conn.ConnectTimeoutException;


/**
 * 定义请求回复处理方法
 */
public final class ResponseHandler extends AsyncHttpResponseHandler {
    /**
     * 请求的地址
     */
    private String host;
    /**
     * 请求进度处理器
     */
    private ProgressHandler progressHandler;
    /**
     * 请求完成处理器
     */
    private CompletionHandler completionHandler;
    /**
     * 请求开始时间
     */
    private long reqStartTime;

    /**
     * 服务器端口
     */
    private int port = -1;

    private String path = null;

    private volatile long sent = 0;

    public ResponseHandler(URI uri, CompletionHandler completionHandler, ProgressHandler progressHandler) {
        super(Looper.getMainLooper());
        this.host = uri.getHost();
        this.port = uri.getPort();
        this.path = uri.getPath();
        this.completionHandler = completionHandler;
        this.progressHandler = progressHandler;
    }

    private static ResponseInfo buildResponseInfo(int statusCode, Header[] headers, byte[] responseBody,
                                                  String host, String path, int port, double duration, long sent, Throwable error) {

        if (error != null && error instanceof CancellationHandler.CancellationException) {
            return ResponseInfo.cancelled();
        }

        if (headers != null) {
            for (Header h : headers) {
              }
        }

        String err = null;
        if (statusCode != 200) {
            if (responseBody != null) {
//                try {
//                    err = new String(responseBody, Constants.UTF_8);
//                    JSONObject obj = new JSONObject(err);
//                    err = obj.optString("error", err);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
            } else {
                if (error != null) {
                    err = error.getMessage();
                    if (err == null) {
                        err = error.toString();
                    }
                }
            }
        } else {
            if (error != null) {
                err = error.getMessage();
                if (err == null) {
                    err = error.toString();
                }
            }
        }

        if (statusCode == 0 || statusCode == 502 || statusCode == 404) {
            statusCode = ResponseInfo.NetworkError;
            String msg = error.getMessage();
            if (error instanceof IOException) {
                if (msg != null && msg.indexOf("UnknownHostException") == 0) {
                    statusCode = ResponseInfo.UnknownHost;
                } else if (msg != null && msg.indexOf("Broken pipe") == 0) {
                    statusCode = ResponseInfo.NetworkConnectionLost;
                } else if (error instanceof NoHttpResponseException) {
                    statusCode = ResponseInfo.NetworkConnectionLost;
                } else if (error instanceof SocketTimeoutException) {
                    statusCode = ResponseInfo.TimedOut;
                } else if (error instanceof ConnectTimeoutException || error instanceof SocketException) {
                    statusCode = ResponseInfo.CannotConnectToHost;
                }
            }
        }

        return new ResponseInfo(statusCode, host, path, port, duration, sent, err);
    }

    private static JSONObject buildJsonResp(byte[] body) throws Exception {
        String str = new String(body, Constants.UTF_8);
        return new JSONObject(str);
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        double duration = (System.currentTimeMillis() - reqStartTime) / 1000.0;
        JSONObject obj = null;
        Exception exception = null;
        try {
            obj = buildJsonResp(responseBody);
            int code = obj.getInt("code");
            if(code != 2000 && code != 2002){
                exception = new Exception(String.format("response code = %d, msg = %s", code, obj.optString("msg")));
            }
        } catch (Exception e) {
            exception = e;
        }
        ResponseInfo info = buildResponseInfo(statusCode, headers, null, host, path, port, duration, sent, exception);
        completionHandler.complete(info, obj);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        double duration = (System.currentTimeMillis() - reqStartTime) / 1000.0;
        ResponseInfo info = buildResponseInfo(statusCode, headers, responseBody, host, path, port, duration, sent, error);
        completionHandler.complete(info, null);
    }

    public void onProgress(int bytesWritten, int totalSize) {
        this.sent += bytesWritten;
        if (progressHandler != null) {
            progressHandler.onProgress(bytesWritten, totalSize, -1);
        }
    }

    public void onProgress(long bytesWritten, long totalSize) {
        onProgress((int) bytesWritten, (int) totalSize);
    }

    @Override
    public void onStart() {
        this.reqStartTime = System.currentTimeMillis();
        super.onStart();
    }

    @Override
    public void onPostProcessResponse(ResponseHandlerInterface instance, HttpResponse response) {
    }
}
