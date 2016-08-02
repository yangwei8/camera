package com.leautolink.leautocamera.net.http;

import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;

import com.leautolink.leautocamera.event.NetWorkBadEvent;
import com.leautolink.leautocamera.net.http.httpcallback.DownLoadCallBack;
import com.leautolink.leautocamera.net.http.httpcallback.GetBeanCallBack;
import com.leautolink.leautocamera.net.http.httpcallback.GetCallBack;
import com.leautolink.leautocamera.net.http.httpcallback.PostCallBack;
import com.leautolink.leautocamera.net.http.httpcallback.UploadFileCallBack;
import com.leautolink.leautocamera.net.http.request.ProgressRequestBody;
import com.leautolink.leautocamera.utils.Logger;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * OkHttp请求类
 * Created by tianwei1 on 2016/3/4.
 */
public class OkHttpRequest {
    private static final java.lang.String TAG = "OkHttpRequest";
    private static OkHttpClient mClient;
    private static OkHttpClient.Builder mBuilder;
    private static boolean isCancel = false;
    private static Call mCall;
    private static List<Call> mCalls = new ArrayList<Call>();
    private static ProgressRequestBody progerssRequestBody;
    private static ConnectionPool connectionPool;
    private static Context mContext;


    /**
     * 创建OkHttpClient实例
     */
    public static void newInstance(Context context) {
        mContext = context;
        if (mClient == null) {
            synchronized (OkHttpRequest.class) {
                if (mClient == null) {
                    connectionPool = new ConnectionPool();
                    mBuilder = new OkHttpClient.Builder();
                    mBuilder.connectionPool(connectionPool);
                    mClient = mBuilder.build();
                }
            }
        }
//        getBean("","", new TypeToken<List<Object>>(){}.getType(),new GetCallBack(){
//            @Override
//            public void onFailure(Call call, IOException e) {
//
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) {
//
//            }
//        });
    }

    public static ConnectionPool getConnectionPool() {
        return connectionPool;
    }

    /**
     * 获取OkHttpClient单例
     *
     * @return
     */
    public static OkHttpClient getDefault() {
        return mClient;
    }

    /**
     * 设置Read超时
     *
     * @param timeout
     * @param unit
     */
    public static void setReadTimeout(long timeout, TimeUnit unit) {
        if (mBuilder != null) {
            mBuilder.connectTimeout(timeout, unit);
        }
    }

    /**
     * 设置连接超时
     *
     * @param timeout
     * @param unit
     */
    public static void setConnectTimeout(long timeout, TimeUnit unit) {
        if (mBuilder != null) {
            mBuilder.connectTimeout(timeout, unit);
        }
    }

    /**
     * 设置Write超时
     *
     * @param timeout
     * @param unit
     */
    public static void setWriteTimeout(long timeout, TimeUnit unit) {
        if (mBuilder != null) {
            mBuilder.writeTimeout(timeout, unit);
        }
    }

    /**
     * 无headers的get请求
     *
     * @param tag
     * @param url
     * @param getCallBack
     */
    public static void get(Object tag, String url, GetCallBack getCallBack) {
        get(tag, url, null, getCallBack);

    }

    /**
     * base 的 get请求
     *
     * @param tag
     * @param url
     * @param headers
     * @param getCallBack
     */
    public static void get(Object tag, String url, Map<String, String> headers, final GetCallBack getCallBack) {
        Logger.i(TAG, "get url :" + url);
        setCancel(false);
        if (mClient != null) {
            Request.Builder requestBuilder = new Request.Builder();
            requestBuilder.tag(tag);
            requestBuilder.url(url);
            appendHeaders(requestBuilder, headers);
            Request request = requestBuilder.build();
            mCall = mClient.newCall(request);
            mCalls.add(mCall);
            mCall.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    getCallBack.onFailure(call, e);
                    Logger.e(TAG, "get onFailure:" + e.getMessage());
                    EventBus.getDefault().post(new NetWorkBadEvent());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        getCallBack.onResponse(call, response);
                    } else {
                        getCallBack.onError(response.toString());
                    }
                }
            });
        }
    }

    /**
     * 无headers 的get 请求获取String
     *
     * @param tag
     * @param url
     * @param getCallBack
     */
    public static void getString(Object tag, String url, GetCallBack getCallBack) {
        getString(tag, url, null, getCallBack);
    }


    /**
     * get请求获取String
     *
     * @param tag
     * @param url
     * @param headers
     * @param getCallBack
     */
    public static void getString(Object tag, String url, Map<String, String> headers, final GetCallBack getCallBack) {
        Logger.i(TAG, "getString url :" + url);
        setCancel(false);
        if (mClient != null) {
            Request.Builder requestBuilder = new Request.Builder();
            requestBuilder.tag(tag);
            requestBuilder.url(url);
            appendHeaders(requestBuilder, headers);
            Request request = requestBuilder.build();
            mCall = mClient.newCall(request);
            mCalls.add(mCall);
            mCall.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    getCallBack.onFailure(call, e);
                    Logger.e(TAG, "get onFailure:" + e.getMessage());
                    EventBus.getDefault().post(new NetWorkBadEvent());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        getCallBack.onResponse(call, response.body().string());
                    } else {
                        getCallBack.onError(response.toString());
                    }
                }
            });
        }
    }

    /**
     * 无headers 的  将Json转为Bean
     *
     * @param tag
     * @param url
     * @param beanClass
     * @param getBeanCallBack
     * @param <T>
     */
    public static <T> void getBean(Object tag, String url, final Class<T> beanClass, GetBeanCallBack<T> getBeanCallBack) {
        getBean(tag, url, null, beanClass, getBeanCallBack);
    }


    /**
     * 将Json转为Bean
     *
     * @param tag
     * @param url
     * @param headers
     * @param beanClass
     * @param getBeanCallBack
     * @param <T>
     */
    public static <T> void getBean(Object tag, String url, Map<String, String> headers, final Class<T> beanClass, final GetBeanCallBack<T> getBeanCallBack) {
        Logger.i(TAG, "getBean url:" + url);
        setCancel(false);
        if (mClient != null) {
            Request.Builder requestBuilder = new Request.Builder();
            requestBuilder.tag(tag);
            requestBuilder.url(url);
            appendHeaders(requestBuilder, headers);
            Request request = requestBuilder.build();
            mCall = mClient.newCall(request);
            mCalls.add(mCall);
            mCall.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Logger.e(TAG, "getBean onFailure" + e.getMessage());
                    EventBus.getDefault().post(new NetWorkBadEvent());
                    getBeanCallBack.onFailure(call, e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        getBeanCallBack.onResponse(GsonUtils.fromJson(response.body().string(), beanClass));
                    }
                }
            });
        }
    }

    /**
     * 无headers 无 params的post请求
     *
     * @param tag
     * @param url
     * @param postCallBack
     */
    public static void post(Object tag, String url, PostCallBack postCallBack) {
        post(tag, url, null, postCallBack);
    }

    /**
     * 无 headers 的 post请求
     *
     * @param tag
     * @param url
     * @param params
     * @param postCallBack
     */
    public static void post(Object tag, String url, Map<String, String> params, PostCallBack postCallBack) {
        post(tag, url, null, params, postCallBack);
    }

    /**
     * post请求
     *
     * @param tag
     * @param url
     * @param headers
     * @param params
     * @param postCallBack
     */
    public static void post(Object tag, String url, Map<String, String> headers, Map<String, String> params, final PostCallBack postCallBack) {
        Logger.i(TAG, "post url :" + url);
        setCancel(false);
        RequestBody requestBody = null;
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        if (params == null || params.size() == 0) {
            requestBody = formBodyBuilder.build();
        } else {
            for (Map.Entry<String, String> me : params.entrySet()) {
                formBodyBuilder.add(me.getKey(), me.getValue());
            }
            requestBody = formBodyBuilder.build();
        }
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.tag(tag);
        requestBuilder.url(url);
        appendHeaders(requestBuilder, headers);
        requestBuilder.post(requestBody);
        Request request = requestBuilder.build();
        mCall = mClient.newCall(request);
        mCalls.add(mCall);
        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Logger.e(TAG, "post onFailure :" + e.toString());
                postCallBack.onFailure(call, e);
                EventBus.getDefault().post(new NetWorkBadEvent());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Logger.i(TAG, "post onResponse");
                    postCallBack.onResponse(call, response);
                } else {
                    Logger.i(TAG, "post onError: " + response.code());
                    postCallBack.onError(response.code());
                }
            }
        });
    }

    /**
     * 无 headers 的 post提交Json
     *
     * @param tag
     * @param url
     * @param json
     * @param postCallBack
     */
    public static void postJson(Object tag, String url, String json, PostCallBack postCallBack) {
        postJson(tag, url, null, json, postCallBack);
    }

    /**
     * post提交Json
     *
     * @param tag
     * @param url
     * @param headers
     * @param json
     * @param postCallBack
     */
    public static void postJson(Object tag, String url, Map<String, String> headers, String json, final PostCallBack postCallBack) {
        Logger.i(TAG, "postJson url :" + url);
        setCancel(false);
        RequestBody postBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), json);
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.tag(tag);
        requestBuilder.url(url);
        appendHeaders(requestBuilder, headers);
        requestBuilder.post(postBody);
        Request request = requestBuilder.build();
        mCall = mClient.newCall(request);
        mCalls.add(mCall);
        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Logger.e(TAG, "postJson onFailure :" + e.toString());
                postCallBack.onFailure(call, e);
                EventBus.getDefault().post(new NetWorkBadEvent());

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Logger.i(TAG, "postJson onResponse");
                    postCallBack.onResponse(call, response);
                } else {
                    Logger.i(TAG, "postJson onError: " + response.code());
                    postCallBack.onError(response.code());
                }
            }
        });
    }

    /**
     * 文件上传
     *
     * @param tag
     * @param url
     * @param uploadFileCallBack
     */
    public static void uploadFile(Object tag, String url, String filePath, UploadFileCallBack uploadFileCallBack) {
        uploadFile(tag, url, filePath, null, uploadFileCallBack);
    }

    /**
     * 文件上传
     *
     * @param tag
     * @param url
     * @param params
     * @param uploadFileCallBack
     */
    public static void uploadFile(Object tag, String url, String filePath, Map<String, String> params, UploadFileCallBack uploadFileCallBack) {
        uploadFile(tag, url, filePath, null, params, uploadFileCallBack);
    }


    /**
     * 文件上传
     *
     * @param tag
     * @param url
     * @param filePath
     * @param headers
     * @param params
     * @param uploadFileCallBack
     */
    public static void uploadFile(Object tag, String url, String filePath, Map<String, String> headers, Map<String, String> params, final UploadFileCallBack uploadFileCallBack) {
        Logger.i(TAG, "uploadFile url :" + url);
        setCancel(false);
        File file = new File(filePath);
        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        addParams(multipartBuilder, params);
        addFiles(multipartBuilder, new Pair<String, File>("file", file));
        progerssRequestBody = new ProgressRequestBody(multipartBuilder.build(), uploadFileCallBack);
        Request.Builder builder = new Request.Builder();
        appendHeaders(builder, headers);
        Request request = builder.tag(tag)
                .url(url)
                .post(progerssRequestBody).build();

        mCall = mClient.newCall(request);
        mCalls.add(mCall);
        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Logger.e(TAG, "uploadFile onFailure :" + e.toString());
                uploadFileCallBack.onFailure(call, e);
                EventBus.getDefault().post(new NetWorkBadEvent());

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Logger.i(TAG, "uploadFile onResponse");
                    uploadFileCallBack.onResponse(call, response);
                } else {
                    Logger.i(TAG, "uploadFile onError: " + response.code());
                    uploadFileCallBack.onError(response.code());
                }
            }
        });
    }


    private static void appendHeaders(Request.Builder builder, Map<String, String> headers) {
        Headers.Builder headerBuilder = new Headers.Builder();
        if (headers == null || headers.isEmpty()) return;

        for (String key : headers.keySet()) {
            headerBuilder.add(key, headers.get(key));
        }
        builder.headers(headerBuilder.build());
    }

    private static void addParams(MultipartBody.Builder builder, Map<String, String> params) {
        if (params != null && !params.isEmpty()) {
            for (String key : params.keySet()) {
                builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + key + "\""),
                        RequestBody.create(null, params.get(key)));
            }
        }
    }

    private static void addFiles(MultipartBody.Builder builder, Pair<String, File>... files) {
        if (files != null) {
            RequestBody fileBody;
            for (int i = 0; i < files.length; i++) {
                Pair<String, File> filePair = files[i];
                String fileKeyName = filePair.first;
                File file = filePair.second;
                String fileName = file.getName();
                fileBody = RequestBody.create(MediaType.parse(guessMimeType(fileName)), file);
                builder.addPart(Headers.of("Content-Disposition",
                                "form-data; name=\"" + fileKeyName + "\"; filename=\"" + fileName + "\""),
                        fileBody);
            }
        } else {
            throw new IllegalArgumentException("File can not be null");
        }
    }

    private static String guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }


    /**
     * post请求
     *
     * @param url
     * @param getCallBack
     */
//    public static void post(String url, final GetCallBack getCallBack) {
//        Logger.i(TAG, "get url :" + url);
//        setCancel(false);
//        if (mClient != null) {
//            final RequestBody requestBody = new FormBody.Builder()
//                    .build();
//            Request request = new Request.Builder()
//                    .url(url)
//                    .post(requestBody)
//                    .build();
//            mCall = mClient.newCall(request);
//            mCalls.add(mCall);
//            mCall.enqueue(new Callback() {
//                @Override
//                public void onFailure(Call call, IOException e) {
//                    getCallBack.onFailure(call, e);
//                    Logger.e(TAG, "get onFailure:" + e.getMessage());
//                }
//
//                @Override
//                public void onResponse(Call call, Response response) throws IOException {
//                    if (response.isSuccessful()) {
//                        getCallBack.onResponse(call, response);
//                    } else {
//                        getCallBack.onError(GsonUtils.fromJson(response.body().string(), ErrorInfo.class));
//                    }
//                }
//            });
//        }
//    }

    /**
     * 下载
     *
     * @param tag
     * @param url
     * @param targetPath
     * @param fileName
     * @param avaiable
     * @param downLoadCallBack
     */
    public static void downLoad(Object tag, final String url, final String targetPath, final String fileName, final long avaiable, final DownLoadCallBack downLoadCallBack) {
        Logger.i(TAG, "downLoad url:" + url);
        setCancel(false);
        if (TextUtils.isEmpty(url)) throw new NullPointerException("url can't be null");
        if (TextUtils.isEmpty(targetPath))
            throw new NullPointerException("targetPath can't be null");
        if (mClient != null) {
            Logger.i(TAG, "-----");
            final Request request = new Request.Builder()
                    .tag(tag)
                    .url(url)
                    .build();
            mCall = mClient.newCall(request);
            mCalls.add(mCall);
            Logger.i(TAG, "-----url="+url+",targetPath="+targetPath+",fileName="+fileName);
            mCall.enqueue(new Callback() {
                              @Override
                              public void onFailure(Call call, IOException e) {
                                  Logger.e(TAG, "downLoad onFailure:" + e.getMessage());
                                  downLoadCallBack.onFailure(call, e);
                                  EventBus.getDefault().post(new NetWorkBadEvent());
                              }

                              @Override
                              public void onResponse(Call call, Response response) {
                                  Logger.i(TAG, "-----response.isSuccessful()="+response.isSuccessful());
                                  if (response.isSuccessful()) {
                                      File targetFile = new File(targetPath);
                                      if (!targetFile.exists()) {
                                          targetFile.mkdirs();
                                      }
                                      Logger.i(TAG, "-----targetFile="+targetFile);
                                      long total = response.body().contentLength();
                                      int current = 0;
                                      BufferedInputStream bis = null;
                                      BufferedOutputStream bos = null;
                                      //判断内存卡剩余内存的大小
                                      if (avaiable > total) {
                                          try {
                                              Logger.i(TAG, "downLoad onStart total:" + total);
                                              downLoadCallBack.onStart(total);

                                              InputStream is = response.body().byteStream();
                                              bis = new BufferedInputStream(is);
                                              FileOutputStream fos = new FileOutputStream(targetFile.getAbsolutePath() + "/" + fileName);
                                              Logger.i(TAG, "文件存储：" + targetFile.getAbsolutePath() + "/" + fileName);
                                              bos = new BufferedOutputStream(fos);
                                              byte[] buffer = new byte[1024];
                                              int len = 0;
                                              while (!isCancel && ((len = bis.read(buffer)) != -1)) {
                                                  current += len;
                                                  Logger.i(TAG, "downLoad onLoading current:" + current);
                                                  bos.write(buffer, 0, len);
                                                  downLoadCallBack.onLoading(current, total);
                                              }
                                              if (current < total) {
                                                  Logger.i(TAG, "downLoad onCancel");
                                                  downLoadCallBack.onCancel();
                                              }
                                          } catch (IOException e) {
                                              Logger.e(TAG, "读写流出错：" + e.getMessage());
                                              downLoadCallBack.onError(e);
                                          } finally {
                                              try {
                                                  if (bos != null) {
                                                      bos.close();
                                                      bos = null;
                                                  }
                                              } catch (IOException e) {
                                                  e.printStackTrace();
                                                  downLoadCallBack.onError(e);
                                              }
                                              try {
                                                  if (bis != null) {
                                                      bis.close();
                                                      bis = null;
                                                  }
                                              } catch (IOException e) {
                                                  e.printStackTrace();
                                                  downLoadCallBack.onError(e);
                                              }
                                              if (current == total) {
                                                  Logger.i(TAG, "downLoad onSucceed" + "文件保存在" + targetPath + "/" + fileName);
                                                  downLoadCallBack.onSucceed();
                                              }
                                          }
                                      } else {
                                          Logger.i(TAG, "downLoad onSdCardLackMemory");
                                          downLoadCallBack.onSdCardLackMemory(total, avaiable);
                                      }
                                  }
                              }
                          }

            );
        }
    }

    /**
     * 设置是否取消
     *
     * @param cancel
     */

    public static void setCancel(boolean cancel) {
        isCancel = cancel;
        if (progerssRequestBody != null) {
            progerssRequestBody.setIsCancel(cancel);
        }
    }


    /**
     * 取消相同的tag请求
     *
     * @param tag
     */
    public static void cancelSameTagCall(Object tag) {
        if (mClient != null) {
            Dispatcher dispatcher = mClient.dispatcher();
            for (Call call : dispatcher.queuedCalls()) {
                if (tag.equals(call.request().tag())) {
                    call.cancel();
                }
            }
            for (Call call : dispatcher.runningCalls()) {
                if (tag.equals(call.request().tag())) {
                    call.cancel();
                }
            }
            setCancel(true);
        } else {
            Logger.e(TAG, "cancelSameTagCall mClient 为 null");
        }
    }

    /**
     * 取消当前的Call
     */
    public static void cancelCurrentCall() {
        if (mCall != null && !mCall.isCanceled()) {
            mCall.cancel();
            if (mCall.isCanceled()) {
                setCancel(true);
                Logger.i(TAG, "cancelCurrentCall:" + isCancel);
                mCall = null;
            }
        }
    }

    /**
     * 取消所有请求
     */
    public static void cancelAllCall() {

        if (mCalls != null && mCalls.size() > 0) {

            int mCallsSize = mCalls.size();
            for (int i = 0; i < mCallsSize; i++) {
                Call call = mCalls.get(0);
                if (call != null && !call.isCanceled()) {
                    call.cancel();
                    if (call.isCanceled()) {
                        mCalls.remove(call);
                        if (!mCalls.contains(call)) {
                            call = null;
                        }
                    }
                }
            }
            setCancel(true);
        }
    }


    /**
     * 删除下载不完整的文件
     *
     * @param path
     */
    public static void deleteIntactFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
            if (!file.exists()) {
                Logger.i(TAG, "下载不成功，已将文件删除--" + path);
            } else {
                Logger.e(TAG, "下载不成功，文件未成功删除--" + path);
            }
        }
    }
//
//    /**
//     * 上传文件
//     *
//     * @param url
//     * @param sourceFilePath
//     * @param params
//     * @param writeTimeOut
//     * @param readTimeOut
//     * @param tag
//     * @param upLoadCallBack
//     */
//    public static void upLoadUseThirdJar(String url, String sourceFilePath, Map<String, String> params, int writeTimeOut, final int readTimeOut, Object tag, final UpLoadCallBack upLoadCallBack) {
//        File file = new File(sourceFilePath);
//        OkHttpProxy.upload()
//                .url(url)
//                .tag(tag)
//                .file(new Pair<String, File>("file", file))
//                .setParams(params)
//                .addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
//                .setWriteTimeOut(writeTimeOut)
//                .setReadTimeOut(readTimeOut)
//                .start(new UploadListener() {
//                    @Override
//                    public void onSuccess(Response response) {
//                        Logger.e(TAG, "upLoadUseThirdJar onSuccess");
//                    }
//
//                    @Override
//                    public void onFailure(Exception e) {
//                        Logger.e(TAG, "upLoadUseThirdJar onFailure  :" + e.getMessage());
//                    }
//
//                    @Override
//                    public void onUIProgress(Progress progress) {
//                        upLoadCallBack.onUIProgress(progress);
//                    }
//
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//                        Logger.e(TAG, "upLoadUseThirdJar onFailure  :" + e.getMessage());
//                        upLoadCallBack.onFailure(call, e);
//                    }
//
//                    @Override
//                    public void onResponse(Call call, Response response) throws IOException {
//                        Logger.e(TAG, "upLoadUseThirdJar onResponse");
//                        upLoadCallBack.onResponse(call, response);
//                    }
//                });
//    }

//    /**
//     * 取消上传
//     *
//     * @param tag
//     */
//    public static void cancelUpload(Object tag) {
//        OkHttpProxy.cancel(tag);
//    }
}
