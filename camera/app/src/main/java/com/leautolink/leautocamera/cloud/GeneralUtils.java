package com.leautolink.leautocamera.cloud;

import com.letvcloud.cmf.utils.StringUtils;

public class GeneralUtils {
    private static final String TAG = "GeneralUtils";


    public static boolean isLetvStream(String originalUrl) {
        if (StringUtils.isEmpty(originalUrl)) {
            return false;
        }

        return originalUrl.contains("letv.com") || originalUrl.contains("letv.cn") || originalUrl.contains("video123456.com");
    }

    public static String formatTime(int time, StringBuilder stringBuilder) {
        stringBuilder.delete(0, stringBuilder.length());

        time = time / 1000;
        if (time < 60) {
            stringBuilder.append("00:");
            stringBuilder.append(time < 10 ? "0" + time : "" + time);
        } else {
            int mm = time / 60;
            int ss = time % 60;
            stringBuilder.append((mm < 10 ? "0" + mm : "" + mm) + ":");
            stringBuilder.append(ss < 10 ? "0" + ss : "" + ss);
        }

        return stringBuilder.toString();
    }

    /**
     * 获取初始化播放器时传入的参数
     *
     * @return
     */
    public static String getInitPlayerParams() {
        int appId = 751;
        int port = 7000;
        // &address=127.0.0.1 防止外部监听
        // &channel_default_multi=1&channel_max_count=3

        return "app_id=" + appId + "&port=" + port ;
                //+ "&priority_load_external_lib=1";
//        "&term_id=2&app_channel=unknown&custid=test";
    }

}
