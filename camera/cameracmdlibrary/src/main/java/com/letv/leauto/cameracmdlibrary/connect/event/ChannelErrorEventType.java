package com.letv.leauto.cameracmdlibrary.connect.event;

/**
 * Created by lixinlei on 15/11/5.
 */
public class ChannelErrorEventType {
    /**无效的TOKEN*/
    public final static int CMD_CHANNEL_ERROR_INVALID_TOKEN = -4;

    /**socket连接失败*/
    public final static int CMD_CHANNEL_ERROR_CAN_NOT_CONNENT_SOCKET = 0;

    /**连接超时*/
    public final static int CMD_CHANNEL_ERROR_TIMEOUT = 1;

    /**读数据超时*/
    public final static int CMD_CHANNEL_ERROR_READ_TIMEOUT = 2;

    /**soket获取数据失败*/
    public final static int CMD_CHANNEL_ERROR_BROKEN_CHANNEL = 3;

    /**soket写入数据失败*/
    public final static int CMD_CHANNEL_ERROR_BROKEN_CHANNEL_WRITE = 4;

    /**start session 失败 */
    public final static int CMD_CHANNEL_ERROR_START_SESSION_FAILE = 4;

}
