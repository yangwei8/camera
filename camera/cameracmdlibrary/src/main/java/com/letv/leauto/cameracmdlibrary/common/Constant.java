package com.letv.leauto.cameracmdlibrary.common;

import android.os.Environment;

import java.io.File;

/**
 * Created by lixinlei on 15/11/5.
 */
public class Constant {

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
     * sd卡是否插入
     */
    public static boolean isSDCardPresent = true;

    private static String path = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOWNLOADS) + "/LeAuto/";

    public static final String EDRROOTFWPATH = "/tmp/SD0/";

    public static String getSDPath() {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdir();
        }
        return path;
    }
}
