package com.leautolink.leautocamera.config;

import android.os.Environment;

import java.io.File;

/**
 * Created by lixinlei on 15/11/5.
 */
public class Constant {

    /**
     * SharedPreferences  文件名字
     */
    public static final String WIFI_INFO = "WIFIINFO";
    /**
     * SharedPreferences  WIFI  SSID
     */
    public static final String WIFI_SSID = "WIFISSID";
    /**
     * SharedPreferences  WIFI  PWD
     */
    public static final String WIFI_PWD = "WIFIPWD";
    /**
     * 保存和Camera之间的  session  id
     */
    public static int token;

    /**
     * phone ip
     */
    public static String phoneIP = "0.0.0.0";

    /**
     * CameraStatus  的状态   idle 、 vf 、 record
     */
    public static String CameraStatus;

    /**
     * wifi的名字
     */
    public static String WIFI_NAME = "";


    /**
     * 是否是Live
     */
    public static boolean isLive;
    /**
     * 是否是获取视频图片
     */
    public static boolean isGetPics;

    /**
     * 是否处于调试状态
     */
    public static boolean isDebug = true;


    private static String path = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOWNLOADS) + "/LeAuto/";

    public static final String EDRROOTFWPATH = "/tmp/SD0/";

    public static final String DEFAULT_CAMERA_WIFI_PWD = "123456789";

    public static String getSDPath() {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdir();
        }
        return path;
    }
}
