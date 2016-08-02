package com.letv.lecloud.solar.http;

/**
 * 定义进度处理接口
 */
public interface ProgressHandler {
    /**
     * 用户自定义进度处理对象必须实现的接口方法
     *
     * @param bytesWritten 已经写入字节
     * @param totalSize    总字节数
     * @flag  标识，1-代表上传的回调，-1 代表请求结束的回调
     */
    void onProgress(int bytesWritten, double totalSize, long flag);
}
