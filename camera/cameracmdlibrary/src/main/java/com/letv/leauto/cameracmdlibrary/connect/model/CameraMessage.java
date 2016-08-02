package com.letv.leauto.cameracmdlibrary.connect.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * 发送命令的message
 *
 * Created by lixinlei on 15/11/5.
 */
public class CameraMessage {
    /** command id */
    private int command;
    /** callback of this command */
    private CameraMessageCallback messageCallback;
    /** params of command */
    private Map<String, Object> param;



    /** 需不需要全局的处理错误信息 */
    private boolean isNeedSelfError = false;

    public CameraMessage() {
        this.param = new HashMap();
    }

    /**
     * 初始化 message
     * @param command 命令 id
     */
    public CameraMessage(int command) {
        this.command = command;
        this.param = new HashMap();
    }

    /**
     * 初始化 message
     * @param command   命令 id
     * @param cameraMessageCallback  回调接口
     */
    public CameraMessage(int command, CameraMessageCallback cameraMessageCallback) {
        this.command = command;
        this.param = new HashMap();
        this.messageCallback = cameraMessageCallback;
    }

    public Object get(String param) {
        return this.param.get(param);
    }

    public CameraMessageCallback getCallback() {
        return this.messageCallback;
    }

    public int getCommand() {
        return this.command;
    }

    /**
     * 获取当前命令的String
     */
    public String getMessageContent() {
        JSONObject localJSONObject;
        try {
            localJSONObject = new JSONObject();
            localJSONObject.put("msg_id", this.command);
            Iterator localIterator = this.param.keySet().iterator();
            while (localIterator.hasNext()) {
                String str = (String) localIterator.next();
                localJSONObject.put(str, this.param.get(str));
            }
            //防止在toString的时候将value中的路径/转换成\/所以用/replace
            return localJSONObject.toString().replace("\\/","/");
        } catch (JSONException localJSONException) {
            localJSONException.printStackTrace();
        }
        return null;
    }

    /***
     * 添加参数
     * @param key   map 的 key
     * @param value map 的 value
     */
    public void put(String key, Object value) {
        this.param.put(key, value);
    }


    /**
     *  添加 key 为param 的value
     * @param value map 的 value
     */
    public void putParam(Object value) {
        this.put("param", value);
    }

    public boolean isNeedSelfError() {
        return isNeedSelfError;
    }

    public void setIsNeedSelfError(boolean isNeedSelfError) {
        this.isNeedSelfError = isNeedSelfError;
    }
}
