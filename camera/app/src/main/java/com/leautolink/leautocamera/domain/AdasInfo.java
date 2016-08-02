package com.leautolink.leautocamera.domain;

/**
 * Created by lixinlei on 16/4/6.
 */
public class AdasInfo {

//    {"rval":0,"msg_id":268435460,"type":"get","VertAngle":55,"HorizAngle":95,"AutoCalib":1"," +
//            ""hood":0,"horizon":0,"ldws":"medium|0","fcws":"medium|0","llw":"medium|4","fcmd":"medium|0"}
    /**车道偏移（off,low,medium,high）|检测到的次数*/
    private String ldws;
    /**前车碰撞预警（off,low,medium,high）|检测到的次数*/
    private String fcws;
    /**低光预警（off,low,medium,high）|检测到的次数*/
    private String llw;
    /**前车移动（off,low,medium,high）|检测到的次数*/
    private String fcmd;

    public String getLdws() {
        return ldws;
    }

    public String getFcws() {
        return fcws;
    }

    public String getLlw() {
        return llw;
    }

    public String getFcmd() {
        return fcmd;
    }
}
