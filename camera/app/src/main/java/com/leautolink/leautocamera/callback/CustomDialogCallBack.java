package com.leautolink.leautocamera.callback;

/**
 * 自定义Dialog的回调
 * Created by tianwei1 on 2015/12/10.
 */
public interface CustomDialogCallBack {
    /**
     * 更新msg
     * @param msg
     */
//    void onUpdateMsg(String msg);

    /**
     * 取消
     */
    void onCancel();

    /**
     * 更新进度
     * @param current
     */
//    void onUpdateProgress(long current);

    /**
     * 更新当前已经下载的个数和总的个数
     * @param currentTotal
     */
//    void onInitCurrentTotal(String currentTotal);

    /**
     * 初始化进度条的最大值
     * @param max
     */
//    void onInitSeekBar(int max);
}
