package com.letv.leauto.cameracmdlibrary.connect;


import com.letv.leauto.cameracmdlibrary.connect.model.CameraMessage;
import com.letv.leauto.cameracmdlibrary.connect.model.CameraMessageCallback;

import org.json.JSONObject;

public class SendCommandTask
        implements CameraMessageCallback
{
    /**回调接口*/
    private SendCommandCallback sendCommandCallback;
    /**上条命令执行失败后执行的命令*/
    private SendCommandTask sendFailTask;
    /**命令对象*/
    private CameraMessage cameraMessage;
    /**上条命令执行成功之后执行的命令*/
    private SendCommandTask sendPassTask;
    /** 用来判断上条命令 执行的是否成功的过滤器 */
    private SendCommandPipeFilter sendCommandPipeFilter;

    public SendCommandTask(int commandId)
    {
        this.cameraMessage = new CameraMessage(commandId, this);
    }

    public SendCommandTask addCallback(SendCommandCallback sendCommandCallback)
    {
        this.sendCommandCallback = sendCommandCallback;
        return this;
    }

    public SendCommandTask addFailTask(SendCommandTask failTask)
    {
        this.sendFailTask = failTask;
        return this;
    }

    public SendCommandTask addParam(String key, Object value)
    {
        this.cameraMessage.put(key, value);
        return this;
    }

    public SendCommandTask addPassTask(SendCommandTask passTask)
    {
        this.sendPassTask = passTask;
        return this;
    }

    public SendCommandTask addPipeFilter(SendCommandPipeFilter sendCommandPipeFilter)
    {
        this.sendCommandPipeFilter = sendCommandPipeFilter;
        return this;
    }

    public void onReceiveErrorMessage(CameraMessage cameraMessage, JSONObject jsonObject)
    {
        if (this.sendCommandCallback != null)
            this.sendCommandCallback.onFail(cameraMessage, jsonObject);
    }

    public void onReceiveMessage(CameraMessage cameraMessage, JSONObject jsonObject)
    {
        if ((this.sendCommandCallback != null) && (this.sendCommandPipeFilter == null))
            this.sendCommandCallback.onSuccess(cameraMessage, jsonObject);
        if (this.sendCommandPipeFilter != null)
        {
            FilterState localFilterState = this.sendCommandPipeFilter.doFilter(jsonObject);
            if ((FilterState.PASS == localFilterState) && (this.sendPassTask != null))
                this.sendPassTask.start();
            if ((FilterState.FAIL == localFilterState) && (this.sendFailTask != null))
                this.sendFailTask.start();
        }
    }

    public void onReceiveNotification(JSONObject jsonObject)
    {
        if (this.sendCommandPipeFilter != null)
        {
            if ((FilterState.PASS == this.sendCommandPipeFilter.doFilter(jsonObject)) && (this.sendPassTask != null))
                this.sendPassTask.start();
            if ((FilterState.FAIL == this.sendCommandPipeFilter.doFilter(jsonObject)) && (this.sendFailTask != null))
                this.sendFailTask.start();
        }
    }

    public void start()
    {
        RemoteCamHelper.getRemoteCam().sendCommand(this.cameraMessage);
    }

    public enum FilterState
    {
        PASS, FAIL , NONE
    }

    public static abstract class SendCommandPipeFilter
    {
        public abstract SendCommandTask.FilterState doFilter(JSONObject jsonObject);
    }
}
