package com.letv.lecloud.solar.http;


import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Objects;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestHandle;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.message.BasicHeader;

/**
 * 定义HTTP请求管理相关方法
 */
public final class HttpManager {
    private AsyncHttpClient client;

    public HttpManager(int connectTimeout, int responseTimeout) {
        client = new AsyncHttpClient();
        client.blockRetryExceptionClass( CancellationHandler.CancellationException.class );
        client.setConnectTimeout(connectTimeout * 1000);
        client.setResponseTimeout(responseTimeout * 1000);
        client.setUserAgent(UserAgent.instance().toString());
        client.setEnableRedirects(true);
    }

    public void cancelRequestByTAG(String TAG){
//        client.setMaxRetriesAndTimeout(0, 0);
        client.cancelRequestsByTAG(TAG, true);
    }

    /**
     * 以POST方法发送请求数据
     *
     * @param uri               请求的URI
     * @param data              发送的数据
     * @param offset            发送的数据起始字节索引
     * @param size              发送的数据字节长度
     * @param headers           发送的数据请求头部
     * @param progressHandler   发送数据进度处理对象
     * @param completionHandler 发送数据完成后续动作处理对象
     * @param c                 发送数据中途取消处理
     */
    public void postData(URI uri, byte[] data, int offset, int size, Header[] headers,
                         ProgressHandler progressHandler, final CompletionHandler completionHandler,
                         CancellationHandler c) {
        ByteArrayEntity entity = new ByteArrayEntity(data, offset, size, progressHandler, c);
        postEntity(uri, entity, headers, progressHandler, completionHandler);
    }

    public void get(URI uri, final ProgressHandler progressHandler,
                     final CompletionHandler completionHandler){
        final CompletionHandler wrapper = wrap(completionHandler);
        String s = uri.toString();

        ResponseHandler handler = new ResponseHandler(uri, wrapper, progressHandler);
        client.get(s, handler);
    }

    private void postEntity(URI uri, final HttpEntity entity, Header[] headers,
                            final ProgressHandler progressHandler,
                            final CompletionHandler completionHandler) {
        final CompletionHandler wrapper = wrap(completionHandler);
//        final Header[] h = reporter.appendStatHeaders(headers);

        String s = uri.toString();
        Header[] h;
        Header host = new BasicHeader("Host", uri.getHost());
        if (headers == null) {
            h = new Header[]{host};
        } else {
            h = new Header[headers.length + 1];
            System.arraycopy(headers, 0, h, 0, headers.length);
            h[headers.length] = host;
        }

        try {
            uri = new URI(s);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        ResponseHandler handler = new ResponseHandler(uri, wrapper, progressHandler);
        client.post(null, s, h, entity, null, handler);

    }

    /**
     * 以POST方式发送multipart/form-data格式数据
     *
     * @param uri               请求的URI
     * @param args              发送的数据
     * @param progressHandler   发送数据进度处理对象
     * @param completionHandler 发送数据完成后续动作处理对象
     * @param c                 发送数据中途取消处理
     */
    public void multipartPost(URI uri, PostArgs args, ProgressHandler progressHandler,
                              final CompletionHandler completionHandler, CancellationHandler c) {
        MultipartBuilder mbuilder = new MultipartBuilder();
        for (Map.Entry<String, String> entry : args.params.entrySet()) {
            mbuilder.addPart(entry.getKey(), entry.getValue());
        }
        if (args.data != null) {
            ByteArrayInputStream buff = new ByteArrayInputStream(args.data);
            try {
                mbuilder.addPart("file", args.fileName, buff, args.mimeType);
            } catch (IOException e) {
                completionHandler.complete(ResponseInfo.fileError(e), null);
                return;
            }
        } else {
            try {
                mbuilder.addPart("file", args.file, args.mimeType, "filename");
            } catch (IOException e) {
                completionHandler.complete(ResponseInfo.fileError(e), null);
                return;
            }
        }

        ByteArrayEntity entity = mbuilder.build(progressHandler, c);
        postEntity(uri, entity, null, progressHandler, completionHandler);
    }

    private CompletionHandler wrap(final CompletionHandler completionHandler) {
        return new CompletionHandler() {
            @Override
            public void complete(ResponseInfo info, JSONObject response) {
                completionHandler.complete(info, response);
//                if (info.isOK()) {
//                    reporter.updateSpeedInfo(info);
//                } else {
//                    reporter.updateErrorInfo(info);
//                }
            }
        };
    }
}
