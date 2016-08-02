//package com.leautolink.leautocamera.net.http;
//
//import com.leautolink.leautocamera.net.http.httpcallback.DownLoadCallBack;
//
//import java.io.File;
//import java.io.IOException;
//import java.io.RandomAccessFile;
//
//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.Response;
//
///**
// * 多线程下载
// * Created by tianwei1 on 2016/3/4.
// */
//public class MultiTheadDownload {
//    //下载的线程数
//    private static int threadCount = 3;
//    //下载区块的大小
//    private static long blockSize = 0;
//
//    /**
//     * 设置下载的线程数
//     *
//     * @param count
//     */
//    public static void setThreadCount(int count) {
//        if (count < 0) throw new IllegalArgumentException("threadCount is too small");
//        if (count > Integer.MAX_VALUE)
//            throw new IllegalArgumentException("threadCount is too large");
//        threadCount = count;
//    }
//
//    /**
//     * 开始下载
//     *
//     * @param targetPath
//     * @param response
//     * @param downLoadCallBack
//     * @throws IOException
//     */
//    public static void start(String url, String targetPath, Response response, DownLoadCallBack downLoadCallBack) throws IOException {
//        long length = response.body().contentLength();
//        blockSize = length / threadCount;
//        File targetFile = new File(targetPath);
//        if (!targetFile.exists()) {
//            targetFile.mkdirs();
//        }
//        RandomAccessFile raf = new RandomAccessFile(targetFile, "rwd");
//        raf.setLength(length);
//        //开启线程下载
//        for (int i = 0; i < threadCount; i++) {
//            //每个线程下载开始和结束的位置
//            long startIndex = i * blockSize;
//            long endIndex = (i + 1) * blockSize - 1;
//            if (i == threadCount - 1) {
//                endIndex = length - 1;
//            }
//            //开启线程去下载
//
//        }
//    }
//
//    private static class DownLoadThread extends Thread {
//        private int mThreadId;
//        private int mStartIndex;
//        private int mEndIndex;
//        private String mUrl;
//
//        public DownLoadThread(String url, int threadId, int startIndex, int endIndex) {
//            mUrl = url;
//            mThreadId = threadId;
//            mStartIndex = startIndex;
//            mEndIndex = endIndex;
//        }
//
//        public void run() {
//            OkHttpClient mClient = OkHttpRequest.getDefault();
//            Request request = new Request.Builder()
//                    .url(mUrl)
//                    .tag(mThreadId)
//                    .build();
//            mClient.newCall(request).enqueue(new Callback() {`
//                @Override
//                public void onFailure(Call call, IOException e) {
//                    downLoadCallBack.onFailure(call, e);
//                }
//
//                @Override
//                public void onResponse(Call call, Response response) throws IOException {
//                    if (response.isSuccessful()) {
//                        MultiTheadDownload.setThreadCount(threadCount);
//                        MultiTheadDownload.start(url, targetPath, response, downLoadCallBack);
//                    }
//                }
//            });
//        }
//    }
//
//}
