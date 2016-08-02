package com.letv.leauto.cameracmdlibrary.connect.model;

/**
 * Created by lixinlei on 15/9/24.
 */
public class CommandID {

    public static final int AMBA_GET_SETTING   = 0x001;
    public static final int AMBA_SET_SETTING   = 0x002;
    public static final int AMBA_GET_ALL       = 0x003;
    public static final int AMBA_FORMAT_SD     = 0x004;
    public static final int AMBA_GET_SPACE     = 0x005;
    public static final int AMBA_GET_NUM_FILES = 0x006;
    public static final int AMBA_NOTIFICATION  = 0x007;
    public static final int AMBA_BURN_FW       = 0x008;
    public static final int AMBA_GET_OPTIONS   = 0x009;
    public static final int AMBA_GET_DEVINFO   = 0x00B;
    public static final int AMBA_POWER_MANAGE  = 0x00C;
    public static final int AMBA_BATTERY_LEVEL = 0x00D;
    public static final int AMBA_ZOOM          = 0x00E;
    public static final int AMBA_ZOOM_INFO     = 0x00F;
    public static final int AMBA_SET_BITRATE   = 0x010;
    /**start session*/
    public static final int AMBA_START_SESSION = 0x101;
    /**stop session*/
    public static final int AMBA_STOP_SESSION  = 0x102;
    public static final int AMBA_RESETVF       = 0x103;
    public static final int AMBA_STOP_VF       = 0x104;
    public static final int AMBA_SET_CLINT_INFO= 0x105;
    public static final int AMBA_RECORD_START  = 0x201;
    public static final int AMBA_RECORD_STOP   = 0x202;
    public static final int AMBA_RECORD_TIME   = 0x203;
    public static final int AMBA_FORCE_SPLIT   = 0x204;
    public static final int AMBA_TAKE_PHOTO    = 0x301;
    public static final int AMBA_STOP_PHOTO    = 0x302;
    public static final int AMBA_GET_THUMB     = 0x401;
    public static final int AMBA_GET_MEDIAINFO = 0x402;
    public static final int AMBA_SET_ATTRIBUTE = 0x403;
    public static final int AMBA_DEL           = 0x501;
    public static final int AMBA_LS            = 0x502;
    public static final int AMBA_LS_NEW        = 268435458;
    public static final int AMBA_CD            = 0x503;
    public static final int AMBA_PWD           = 0x504;
    public static final int AMBA_GET_FILE      = 0x505;
    public static final int AMBA_PUT_FILE      = 0x506;
    public static final int AMBA_CANCLE_XFER   = 0x507;
    public static final int AMBA_WIFI_RESTART   = 1537;
    public static final int AMBA_SET_WIFI_SETTING   = 1538;
    public static final int AMBA_GET_WIFI_SETTING   = 1539;
    public static final int AMBA_QUERY_SESSION_HOLDER   = 0x701;

    //SD卡的状态
    public static final int AMBA_SDCARD_STATUS   = 268435459;
    public static final int AMBA_ADAS_INFO   = 268435460;

    //GPS  INFO
    public static final int AMBA_GPS_INFO   = 268435462;
}
