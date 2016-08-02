package com.letv.leauto.cameracmdlibrary.connect;


public class CameraClient {
    public void formatSDCard(SendCommandCallback sendCommandCallback) {
        SendCommandTask formatSDCardTask = SendCommandTaskHelper.buildFormatSDCardTask().addCallback(sendCommandCallback);
        SendCommandTaskHelper.startTask(formatSDCardTask);
        
        //            SendCommandTaskHelper.startTask(stopVFTask);
    }
    
    //    public void startCheckVF(SendCommandCallback sendCommandCallback)
    //    {
    //        SendCommandTask localSendCommandTask1 = SendCommandTaskHelper.buildSartVFTask().addCallback(sendCommandCallback);
    //        SendCommandTask localSendCommandTask2 = SendCommandTaskHelper.buildStopVFTask().addPassTask(localSendCommandTask1).addCallback(sendCommandCallback).addPipeFilter(SendCommandTaskHelper.buildStopVFFilter());
    //        SendCommandTaskHelper.startTask(SendCommandTaskHelper.buildGetSettingTask("app_status").addCallback(sendCommandCallback).addPassTask(localSendCommandTask1).addFailTask(localSendCommandTask2).addPipeFilter(SendCommandTaskHelper.buildGetAppStatusFilter()));
    //    }
    public void takePhoto(SendCommandCallback sendCommandCallback) {
        SendCommandTask takePhotoTask = SendCommandTaskHelper.buildTakePhotoCommand().addCallback(sendCommandCallback);
        SendCommandTask startVFTask = SendCommandTaskHelper.buildSartVFTask().addPassTask(takePhotoTask).addCallback(sendCommandCallback).addPipeFilter(SendCommandTaskHelper.buildStartVFFilter());
        SendCommandTaskHelper.startTask(SendCommandTaskHelper.buildGetSettingTask("app_status").addCallback(sendCommandCallback).addPassTask(takePhotoTask).addFailTask(startVFTask).addPipeFilter(SendCommandTaskHelper.buildhCeckVFOrRecordFilter()));
    }
    
    
    public void getAllCurrentSetting(SendCommandCallback sendCommandCallback) {
        SendCommandTaskHelper.startTask(SendCommandTaskHelper.buildGetALlCurrentSettingTask().addCallback(sendCommandCallback));
    }
    
    public void getSDCardSpace(String type, SendCommandCallback sendCommandCallback) {
        SendCommandTaskHelper.startTask(SendCommandTaskHelper.buildGetSDCardSpaceTask(type).addCallback(sendCommandCallback));
    }
    
    public void getSetting(String type, SendCommandCallback sendCommandCallback) {
        SendCommandTaskHelper.startTask(SendCommandTaskHelper.buildGetSetting(type).addCallback(sendCommandCallback));
    }
    
    /**
     * 发送 设置 命令
     * @param type   设置的类型
     * @param param  设置的参数
     * @param sendCommandCallback  成功或失败的回调
     * @param isNeedStopVF  是否需要stopVF ，让camera处于idle状态
     * 注意： 设置成功后  如果是需要 stopVF 设置camera处于idle状态的话 ，在sendCommandCallback 的成功回调中需要设置回以前的状态
     *       Constant.CameraStatus 存储着idle之前的状态
     */
    public void setSetting(String type, String param, SendCommandCallback sendCommandCallback, boolean isNeedStopVF) {
        SendCommandTask settingTask = SendCommandTaskHelper.buildSetSettingTask(type, param).addCallback(sendCommandCallback);
        if (isNeedStopVF)
        {
            SendCommandTask stopVFTask = SendCommandTaskHelper.buildStopVFTask().addPassTask(settingTask).addCallback(sendCommandCallback).addPipeFilter(SendCommandTaskHelper.buildStopVFFilter());
            SendCommandTaskHelper.startTask(SendCommandTaskHelper.buildGetSettingTask("app_status").addCallback(sendCommandCallback).addPassTask(settingTask).addFailTask(stopVFTask).addPipeFilter(SendCommandTaskHelper.buildGetAppStatusFilter()));
            //            SendCommandTaskHelper.startTask(stopVFTask);
            
            return;
        }
        SendCommandTaskHelper.startTask(settingTask);
    }
}
