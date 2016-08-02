package com.leautolink.leautocamera.utils;

import android.content.Context;

import com.leautolink.leautocamera.config.Config;
import com.leautolink.leautocamera.config.Constant;

/**
 * Created by tianwei1 on 2016/3/7.
 */
public class UrlUtils {
    /**
     * 获取CameraHttpThumbUrl
     *
     * @param type
     * @param fileThumbName
     * @return
     */
    public static String getCameraHttpThumbUrl(String type, String fileThumbName) {
        String httpThumburl = null;
        if ("event".equals(type)) {
            httpThumburl = Config.HTTP_EVENT_THUMB_PATH + fileThumbName;
        } else if ("normal".equals(type)) {
            httpThumburl = Config.HTTP_NORMAL_THUMB_PATH + fileThumbName;
        } else if ("photo".equals(type)) {
            httpThumburl = Config.HTTP_PHOTO_THUMB_PATH + fileThumbName;
        }
        return httpThumburl;
    }

    /**
     * 获取主码流源文件Http的url
     *
     * @param type
     * @param fileName
     * @return
     */
    public static String getCameraMvideoHttpUrl(String type, String fileName) {
        String httpMvideoUrl = null;
        if ("event".equals(type)) {
            httpMvideoUrl = Config.HTTP_EVENT_MVIDEO_PATH + fileName;
        } else if ("normal".equals(type)) {
            httpMvideoUrl = Config.HTTP_NORMAL_MVIDEO_PATH + fileName;
        } else if ("photo".equals(type)) {
            httpMvideoUrl = Config.HTTP_PHOTO_PATH + fileName;
        }else if("data".equals(type)){
            httpMvideoUrl = Config.HTTP_DATA_PATH + fileName;
        }
        return httpMvideoUrl;
    }


    /**
     * 获取从码流源文件Http的url
     *
     * @param type
     * @param fileName
     * @return
     */
    public static String getCameraSvideoHttpUrl(String type, String fileName) {
        String httpSvideoUrl = null;
        fileName = fileName.replace("A.MP4", "B.MP4");
        if ("event".equals(type)) {
            httpSvideoUrl = Config.HTTP_EVENT_SVIDEO_PATH + fileName;
        } else if ("normal".equals(type)) {
            httpSvideoUrl = Config.HTTP_NORMAL_SVIDEO_PATH + fileName;
        } else if ("photo".equals(type)) {
            httpSvideoUrl = Config.HTTP_PHOTO_PATH + fileName;
        }
        return httpSvideoUrl;
    }

    /**
     * 获取保存文件的路径
     *
     * @param type
     * @param context
     * @return
     */
    public static String getTargetPath(String type, Context context) {
        String targetPath = null;
        if ("event".equals(type)) {
            targetPath = SdCardUtils.getMVideoPath(context);
        } else if ("normal".equals(type)) {
            targetPath = SdCardUtils.getMVideoPath(context);
        } else if ("photo".equals(type)) {
            targetPath = SdCardUtils.getPhotoPath(context);
        }else if("data".equals(type)){
            targetPath = SdCardUtils.getDataRootPath(context);
        }
        return targetPath;
    }
    /**
     * 获取DATA保存文件的路径
     *
     * @param fileName
     * @param context
     * @return
     */
    public static String getTargetDataPath(Context context,String fileName) {
        String targetPath = null;

        targetPath = SdCardUtils.getDataPath(context,fileName);

        return targetPath;
    }
    /**
     * 获取删除记录仪文件的路径
     *
     * @param type
     * @param fileName
     * @return
     */
    public static String getDeleteCameraFileUrl(String type, String fileName) {
        String targetPath = null;
        if ("event".equals(type)) {
            targetPath = Config.CAMERA_EVENT_MVIDEO_PATH + "/" + fileName;
        } else if ("normal".equals(type)) {
            targetPath = Config.CAMERA_NORMAL_MVIDEO_PATH + "/" + fileName;
        } else if ("photo".equals(type)) {
            targetPath = Config.CAMREA_PHOTO_PATH + "/" + fileName;
        }else if ("data".equals(type)) {
            targetPath = Config.CAMERA_DATA_PATH + "/" + fileName;
        }
        return targetPath;
    }

    /**
     * 获取记录仪视频的Rtsp Url
     *
     * @param type
     * @param fileName
     * @return
     */
    public static String getCameraVideoRtspUrl(String type, String fileName) {
        String videoRtspUrl = null;
        if ("event".equals(type)) {
            videoRtspUrl = Config.RTSP_EVENT_MVIDEO_PATH + fileName;
        } else if ("normal".equals(type)) {
            videoRtspUrl = Config.RTSP_NORMAL_MVIDEO_PATH + fileName;
        }
        return videoRtspUrl;
    }

    /**
     * 获取本地存放用户下载文件的文件夹路径
     *
     * @param context
     * @param type
     * @return
     */
    public static String getLocalFileUrl(Context context, String type) {
        String targetPath = null;
        if ("video".equals(type)) {
            targetPath = SdCardUtils.getMVideoPath(context);
        } else if ("photo".equals(type)) {
            targetPath = SdCardUtils.getPhotoPath(context);
        }
        return targetPath;
    }

    /**
     * 获取删除本地文件的路径
     *
     * @param context
     * @param type
     * @return
     */
    public static String getDeletLocalFileUrl(Context context, String type, String filename) {
        String targetPath = null;
        if ("video".equals(type)) {
            targetPath = SdCardUtils.getMVideoPath(context) + "/" + filename;
        } else if ("photo".equals(type)) {
            targetPath = SdCardUtils.getPhotoPath(context) + "/" + filename;
        }else if("data".equals(type)){
            targetPath = SdCardUtils.getDataRootPath(context)+ "/" + filename;;
        }
        return targetPath;
    }

    /**
     * 获取Glide加载本地数据的Url
     *
     * @param context
     * @param type
     * @param filename
     * @return
     */
    public static String getLocalUrl(Context context, String type, String filename) {
        String targetPath = null;
        if ("video".equals(type)) {
            targetPath = Config.LOCAL_URL_PREFIX + SdCardUtils.getMVideoPath(context) + "/" + filename;
        } else if ("photo".equals(type)) {
            targetPath = Config.LOCAL_URL_PREFIX + SdCardUtils.getPhotoPath(context) + "/" + filename;
        }
        return targetPath;
    }

    /**
     * 获取存放Fw的路径
     *
     * @param context
     * @return
     */
    public static String getFwPath(Context context) {
        return  SdCardUtils.getSDCardRootPath(context);
    }
}
