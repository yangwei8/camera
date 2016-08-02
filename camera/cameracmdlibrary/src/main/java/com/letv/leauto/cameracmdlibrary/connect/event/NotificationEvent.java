package com.letv.leauto.cameracmdlibrary.connect.event;

/**
 * Created by lixinlei on 15/11/5.
 */
public class NotificationEvent {

    private int mType = -1;
    /**
     * 卡插入
     */
    public static final int SD_INSERTED = 0;
    /**
     * 卡拔出
     */
    public static final int SD_REMOVED = 1;
    /**
     * 文件写入错误
     */
    public static final int STREAM_ERROR = 2;
    /**
     * idel状态
     */
    public static final int IDLE = 3;
    /**
     * live状态
     */
    public static final int LIVE = 4;
    /**
     * vf状态
     */
    public static final int VF = 5;
    /**
     * record状态
     */
    public static final int RECORD = 6;
    /**
     * capture状态
     */
    public static final int CAPTURE = 7;
    /**
     * 未知错误
     */
    public static final int UNKOW = 8;
    /**
     * 正在恢复文件（录像时突然断电重启后会有此提示）
     */
    public static final int RECOVERING = 9;
    /**
     * 没有文件
     */
    public static final int NO_FILES = 10;
    /**
     * 低速卡
     */
    public static final int MEMORY_RUNOUT = 11;
    /**
     * 没有SD卡
     */
    public static final int NO_CARD = 12;
    /**
     * 卡写保护
     */
    public static final int CARD_PROTECTED = 13;
    /**
     * 卡满
     */
    public static final int CARD_FULL = 14;
    /**
     * 卡容量太小（小于4G的卡会有此提示)
     */
    public static final int SMALL_CARD = 15;
    /**
     * 文件数已达到最大。最多可有10000个视频主文件
     */
    public static final int MAXIMUM_FILE = 16;
    /**
     * 照片数已达最大。暂定为照片不超过500
     */
    public static final int MAXIMUM_PHOTO_AMOUNT = 17;
    /**
     * 卡读写错误
     */
    public static final int IO_ERROR = 18;
    /**
     * 拍照忙
     */
    public static final int PIV_BUSY = 19;
    /**
     * 不支持拍照
     */
    public static final int PIV_IS_DISALLOWED = 20;
    /**
     * 拍照错误
     */
    public static final int PIV_ERROR = 21;
    /**
     * 检测到车道偏移
     */
    public static final int LDWS = 22;
    /**
     * 前车预警
     */
    public static final int FCWS = 23;
    /**
     * 前车移动
     */
    public static final int FCMD = 24;
    /**
     * 检测到低亮度，提示开车灯
     */
    public static final int LLWS = 25;
    /**
     * 检测到物体移动
     */
    public static final int MOTION_DETECTED = 26;
    /**
     * 电池电量低
     */
    public static final int LOW_VOLTAGE = 27;
    /**
     * 紧急文件数已达最大。目前为50
     */
    public static final int EVENT_FILES_IS_FULL = 28;
    /**
     * 正在结束文件
     */
    public static final int VIDEO_IS_CLOSE = 29;
    /**
     * 系统忙
     */
    public static final int SYSTEM_BUSY = 30;
    /**
     * 卡未格式化
     */
    public static final int CARD_IS_UNFORMATED = 31;

    /**
     * 有新文件添加
     */
    public static final int FILE_ADD = 32;

    /**
     * 文件下载传输成功
     */
    public static final int GET_FILE_COMPLETE = 33;
    /**
     * 文件下载传输失败
     */
    public static final int GET_FILE_FAIL = 34;

    public NotificationEvent(int type) {
        mType = type;
    }

    public int getType() {
        return mType;
    }
}
