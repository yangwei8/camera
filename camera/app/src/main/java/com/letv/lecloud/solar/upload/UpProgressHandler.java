package com.letv.lecloud.solar.upload;

/**
 * Created by wiky on 15/12/25.
 */
public interface UpProgressHandler {
    /**
     * 用户自定义进度处理类必须实现的方法
     *
     * @param percent 上传进度，取值范围[0, 1.0]int
     * @param speed 上传速度，字节/s,若speed=-1，表示未计算出速度（未知）
     */
    void onProgress(double percent, int speed);
}
