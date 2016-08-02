package com.leautolink.leautocamera.utils;

import android.util.Log;

/**
 * Created by shimeng on 16/7/15.
 */

public class VideoUtil {
    /**
     * 视频裁剪
     *
     * @param srcFilePath       输入文件
     * @param outputFilePath    输出文件
     * @param startTime 开始时间(s)
     * @param endTime  时间间隔(s)
     */
    public static int cutVideo(String srcFilePath, String outputFilePath, float startTime, float endTime) {
        //ffmpeg -ss 10 -t 16 -i 1.mp4 -codec copy 2.mp4
       // String cmd = "ffmpeg -ss %s -t %s -i %s -codec copy %s";
       // String cmdStr = String.format(cmd, startTime, duration, srcFilePath, outputFilePath);
       // String[] cmdArray = cmdStr.split(" ");
        String[] cmdArray = {"test",srcFilePath,outputFilePath,startTime+"",endTime+""};
       int result = ffmpegcore(cmdArray.length, cmdArray);
        Log.i("VideoUtil", "result=" + result);
        return result;
    }

    /**
     * 通过这个native方法调用ffmpeg的主函数
     *
     * @param argc    命令的个数，已空格为分割
     * @param cmdLine 命令行的数组
     * @return
     */
    public static native int ffmpegcore(int argc, String[] cmdLine);

    static {
        System.loadLibrary("videoutil");
    }
}
