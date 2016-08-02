package com.leautolink.leautocamera.config;

/**
 * Created by tianwei1 on 2016/2/29.
 */
public class Config {
    /**
     * Http请求的baseurl
     */
    public static final String HTTP_BASE_URL = "http://";

    public static final String UPLOAD_SERVER_URL = "http://leting.leauto.com/newupload";
    public static final String SHARE_FILE_URL = "http://leting.leauto.com/img/uploadfile/camera/";

    public static final String LEMI_FORUM_URL = "http://bbs.le.com/forum.php?mod=forumdisplay&action=list&fid=1445";
    public static final String DATA_UP_URL = "http://recv.bigdata.leautolink.com/apis/kafka/input?topic=vehicle_edr_test";
//    public static final String UPLOAD_SERVER_URL = "http://192.168.100.14:8080/upload";


    /**
     * camera的ip
     */
//    public static final String CAMERA_IP = "192.168.42.1";
    /**
     * 服务器的Ip
     */
    public static final String SERVER_IP = "";
    /**
     * 发送命令的端口
     */
    public static final int CMD_PORT_NUM = 7878;
    /**
     * 传输文件的端口
     */
    public static final int DATA_PORT_NUM = 8787;

    /**
     * wifi SSID名字的前缀
     */
//    public static final String WIFI_SSID_PREFIX = "LeTV-CarDV";
    /**wifi BSSID名字的前缀*/
//    public static final String WIFI_BSSID_PREFIX = "6C:FA:A7";

    /**
     * camera SD的地址
     */
    public static final String CAMERA_ROOT_PATH = "/tmp/SD0/";
    /**
     * 本地文件url的前缀
     */
    public static final String LOCAL_URL_PREFIX = "file://";


    /**
     * camera EVENT 事件的地址
     */

    public static final String CAMERA_EVENT_PATH = CAMERA_ROOT_PATH + "EVENT/";
    public static final String CAMERA_EVENT_MVIDEO_PATH = CAMERA_ROOT_PATH + "EVENT/M_video";
    public static final String CAMERA_EVENT_SVIDEO_PATH = CAMERA_ROOT_PATH + "EVENT/S_video/";
    public static final String CAMERA_EVENT_THUMB_PATH = CAMERA_ROOT_PATH + "EVENT/Thumb/";
    public static final String HTTP_EVENT_THUMB_PATH = "http://" + com.letv.leauto.cameracmdlibrary.common.Config.CAMERA_IP + "/EVENT/Thumb/";
    public static final String HTTP_EVENT_MVIDEO_PATH = "http://" + com.letv.leauto.cameracmdlibrary.common.Config.CAMERA_IP + "/EVENT/M_video/";
    public static final String HTTP_EVENT_SVIDEO_PATH = "http://" + com.letv.leauto.cameracmdlibrary.common.Config.CAMERA_IP + "/EVENT/S_video/";

    public static final String RTSP_EVENT_MVIDEO_PATH = "rtsp://" + com.letv.leauto.cameracmdlibrary.common.Config.CAMERA_IP + "/tmp/SD0/EVENT/M_video/";


    /**
     * camera NORMAL 事件的地址
     */
    public static final String CAMERA_NORMAL_PATH = CAMERA_ROOT_PATH + "NORMAL/";
    public static final String CAMERA_NORMAL_MVIDEO_PATH = CAMERA_ROOT_PATH + "NORMAL/M_video";
    public static final String CAMERA_NORMAL_SVIDEO_PATH = CAMERA_ROOT_PATH + "NORMAL/S_video/";
    public static final String CAMERA_NORMAL_THUMB_PATH = CAMERA_ROOT_PATH + "NORMAL/Thumb/";
    public static final String HTTP_NORMAL_THUMB_PATH = "http://" + com.letv.leauto.cameracmdlibrary.common.Config.CAMERA_IP + "/NORMAL/Thumb/";
    public static final String HTTP_NORMAL_MVIDEO_PATH = "http://" + com.letv.leauto.cameracmdlibrary.common.Config.CAMERA_IP + "/NORMAL/M_video/";
    public static final String HTTP_NORMAL_SVIDEO_PATH = "http://" + com.letv.leauto.cameracmdlibrary.common.Config.CAMERA_IP + "/NORMAL/S_video/";

    public static final String RTSP_NORMAL_MVIDEO_PATH = "rtsp://" + com.letv.leauto.cameracmdlibrary.common.Config.CAMERA_IP + "/tmp/SD0/NORMAL/M_video/";

    /**
     * camera PHOTO 事件的地址
     */

    public static final String CAMREA_PHOTO_PATH = CAMERA_ROOT_PATH + "PHOTO/M_photo";
    public static final String CAMREA_PHOTO_THUMB_PATH = CAMERA_ROOT_PATH + "PHOTO/Thumb/";

    public static final String HTTP_PHOTO_PATH = "http://" + com.letv.leauto.cameracmdlibrary.common.Config.CAMERA_IP + "/PHOTO/M_photo/";
    public static final String HTTP_PHOTO_THUMB_PATH = "http://" + com.letv.leauto.cameracmdlibrary.common.Config.CAMERA_IP + "/PHOTO/Thumb/";

    /**
     * camera DATA 事件的地址
     */
    public static final String CAMERA_DATA_PATH = CAMERA_ROOT_PATH +"DATA";
    public static final String HTTP_DATA_PATH = "http://" + com.letv.leauto.cameracmdlibrary.common.Config.CAMERA_IP  + "/SD0/DATA/";
}
