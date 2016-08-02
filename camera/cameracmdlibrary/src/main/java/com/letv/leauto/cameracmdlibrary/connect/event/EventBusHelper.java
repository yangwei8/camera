package com.letv.leauto.cameracmdlibrary.connect.event;

import com.letv.leauto.cameracmdlibrary.utils.Logger;

import org.json.JSONObject;

import de.greenrobot.event.EventBus;

/**
 * Created by lixinlei on 15/11/5.
 */
public class EventBusHelper {

    /***
     * 发送错误的通知
     *
     * @param errcode    错误码
     * @param jsonObject 错误的信息
     */
    public static void postChannelError(int errcode, JSONObject jsonObject) {
        EventBus.getDefault().post(new ChannelErrorEvent(errcode, jsonObject));
    }

    /***
     * 发送错误的通知
     *
     * @param errcode 错误码
     */
    public static void postChannelError(int errcode) {
        EventBus.getDefault().post(new ChannelErrorEvent(errcode));
    }

    /***
     * 发送GPS的通知
     *
     * @param info GPS Info
     */
    public static void postGPSInfo(String info) {
        EventBus.getDefault().post(new GPSEvent(info));
    }

    /***
     * 发送GPS的通知
     *
     * @param info GPS Info
     */
    public static void postGSenserEvent(String info) {
        EventBus.getDefault().post(new GSenserEvent(info));
    }

    /***
     * 发送Camera的通知
     *
     * @param jsonObject camera 的通知信息
     */
    public static void postCameraNotification(JSONObject jsonObject) {
        Logger.e(jsonObject.toString());

        String type = jsonObject.optString("type");
        String param = jsonObject.optString("param");

        if ("Info".equals(type)) {
            if ("Inserted".equals(param)) {//卡插入
                createAndPostNotificationEvent(NotificationEvent.SD_INSERTED);
            } else if ("Removed".equals(param)) {//卡拔出
                createAndPostNotificationEvent(NotificationEvent.SD_REMOVED);
            }
        } else if ("File_Added".equals(type)){
            if (param.contains("event")||param.contains("photo")) {
                createAndPostAddEventNotificationEvent(param);
            }
        } else if ("File_Reovmed".equals(type)) {
            if (param.contains("event") || param.contains("photo")) {
                createAndPostEventOrPhotoRemovedNotificationEvent(param);
            }
        } else if ("Error".equals(type)) {
            if ("Stream Error!".equals(param)) {
                createAndPostNotificationEvent(NotificationEvent.STREAM_ERROR);
            }
        } else if ("app_status".equals(type)) {
            if ("idle".equals(param)) {
                createAndPostNotificationEvent(NotificationEvent.IDLE);
            } else if ("live".equals(param)) {
                createAndPostNotificationEvent(NotificationEvent.LIVE);
            } else if ("vf".equals(param)) {
                createAndPostNotificationEvent(NotificationEvent.VF);
            } else if ("record".equals(param)) {
                createAndPostNotificationEvent(NotificationEvent.RECORD);
            } else if ("capture".equals(param)) {
                createAndPostNotificationEvent(NotificationEvent.CAPTURE);
            }
        } else if ("warning".equals(type)) {
            if ("unkow".equals(param)) {
                createAndPostNotificationEvent(NotificationEvent.UNKOW);
            } else if ("Recovering".equals(param)) {
                createAndPostNotificationEvent(NotificationEvent.RECOVERING);
            } else if ("No Files".equals(param)) {
                createAndPostNotificationEvent(NotificationEvent.NO_FILES);
            } else if ("Memory Runout".equals(param)) {
                createAndPostNotificationEvent(NotificationEvent.MEMORY_RUNOUT);
            } else if ("No Card".equals(param)) {
                createAndPostNotificationEvent(NotificationEvent.NO_CARD);
            } else if ("Card Protected".equals(param)) {
                createAndPostNotificationEvent(NotificationEvent.CARD_PROTECTED);
            } else if ("Card Full".equals(param)) {
                createAndPostNotificationEvent(NotificationEvent.CARD_FULL);
            } else if ("Small Card".equals(param)) {
                createAndPostNotificationEvent(NotificationEvent.SMALL_CARD);
            } else if ("Maximum File".equals(param)) {
                createAndPostNotificationEvent(NotificationEvent.MAXIMUM_FILE);
            } else if ("Maximum Photo Amount".equals(param)) {
                createAndPostNotificationEvent(NotificationEvent.MAXIMUM_PHOTO_AMOUNT);
            } else if ("IO Error!".equals(param)) {
                createAndPostNotificationEvent(NotificationEvent.IO_ERROR);
            } else if ("PIV busy".equals(param)) {
                createAndPostNotificationEvent(NotificationEvent.PIV_BUSY);
            } else if ("PIV is disallowed".equals(param)) {
                createAndPostNotificationEvent(NotificationEvent.PIV_IS_DISALLOWED);
            } else if ("PIV error".equals(param)) {
                createAndPostNotificationEvent(NotificationEvent.PIV_ERROR);
            } else if ("LDWS".equals(param)) {
                createAndPostNotificationEvent(NotificationEvent.LDWS);
            } else if ("FCWS".equals(param)) {
                createAndPostNotificationEvent(NotificationEvent.FCWS);
            } else if ("FCMD".equals(param)) {
                createAndPostNotificationEvent(NotificationEvent.FCMD);
            } else if ("LLWS".equals(param)) {
                createAndPostNotificationEvent(NotificationEvent.LLWS);
            } else if ("Motion detected".equals(param)) {
                createAndPostNotificationEvent(NotificationEvent.MOTION_DETECTED);
            } else if ("Low voltage".equals(param)) {
                createAndPostNotificationEvent(NotificationEvent.LOW_VOLTAGE);
            } else if ("Event files is full".equals(param)) {
                createAndPostNotificationEvent(NotificationEvent.EVENT_FILES_IS_FULL);
            } else if ("Video is close".equals(param)) {
                createAndPostNotificationEvent(NotificationEvent.VIDEO_IS_CLOSE);
            } else if ("system busy".equals(param)) {
                createAndPostNotificationEvent(NotificationEvent.SYSTEM_BUSY);
            } else if ("Card is unformated".equals(param)) {
                createAndPostNotificationEvent(NotificationEvent.CARD_IS_UNFORMATED);
            }
        } else if ("get_file_fail".equals(type)) {
            createAndPostNotificationEvent(NotificationEvent.GET_FILE_FAIL);
        } else if ("get_file_complete".equals(type)) {
            createAndPostNotificationEvent(NotificationEvent.GET_FILE_COMPLETE);
        } else if ("unkown".equals(type)) {
            if ("unkown".equals(param)) {
                createAndPostNotificationEvent(NotificationEvent.UNKOW);
            }
        }

    }

    /**
     * 创建并发送突发事件，照片删除的通知
     *
     * @param param
     */
    private static void createAndPostEventOrPhotoRemovedNotificationEvent(String param) {
        EventBus.getDefault().post(new EventOrPhotoRemovedNotificationEvent(param));
    }

    /**
     * 创建并发送NotificationEvent
     */
    private static void createAndPostNotificationEvent(int type) {
        EventBus.getDefault().post(new NotificationEvent(type));
    }

    /**
     * 创建并发送新增EVENT事件的EVENT
     */
    private static void createAndPostAddEventNotificationEvent(String param) {
        EventBus.getDefault().post(new AddEventFileEvent(param));
    }

    /***
     * 发送 StreamViewEvent 的通知
     *
     * @param type camera 的通知信息
     */
    public static void postStreamViewEvent(int type) {

        EventBus.getDefault().post(new StreamViewEvent(type));
    }


    /**
     * 发送 内存 不足的通知
     */
    public static void postSDSizeLackEvent(SDSizeLackEvent sdSizeLackEvent) {
        EventBus.getDefault().post(sdSizeLackEvent);
    }


    /**
     * 发送连接到 camera wifi 的事件
     */
    public static void postConnectToCamera(ConnectToCameraEvent connectToCameraEvent) {
        EventBus.getDefault().post(connectToCameraEvent);
    }

    /**
     * 连接成功
     */
    public static void postConnectToCamera(AutoConnectToCameraEvent connectToCameraEvent) {
        EventBus.getDefault().post(connectToCameraEvent);
    }

    public static void postOtherPhoneConnectedEvent(OtherPhoneConnectedEvent otherPhoneConnectedEvent) {
        EventBus.getDefault().post(otherPhoneConnectedEvent);
    }
}
