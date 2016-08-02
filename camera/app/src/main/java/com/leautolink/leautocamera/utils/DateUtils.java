package com.leautolink.leautocamera.utils;

import java.text.SimpleDateFormat;

/**
 * 日期时间工具
 * Created by tianwei1 on 2015/11/27.
 */
public class DateUtils {
    /**
     * 格式化时间 小时:分;秒 HH:mm:ss
     *
     * @return
     */
    public static String formatHMS(String time) {
        long preFormatTime = Long.valueOf(time);
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");

        String formatedTime = formatter.format(preFormatTime);
        return formatedTime;
    }
}
