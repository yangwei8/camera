package com.letv.leauto.cameracmdlibrary.connect;

import com.letv.leauto.cameracmdlibrary.common.Constant;
import com.letv.leauto.cameracmdlibrary.connect.model.CommandID;

import org.json.JSONObject;

public class SendCommandTaskHelper
{
    /********************************视频和拍照********************************/
    public static SendCommandTask buildTakePhotoCommand()
    {
        SendCommandTask localSendCommandTask = new SendCommandTask(CommandID.AMBA_TAKE_PHOTO);
        return localSendCommandTask;
    }


    /********************************设置相关********************************/
    public static SendCommandTask buildCDCommand(String paramCommand)
    {
        SendCommandTask localSendCommandTask = new SendCommandTask(CommandID.AMBA_CD);
        localSendCommandTask.addParam("param", paramCommand);
        return localSendCommandTask;
    }

    public static SendCommandTask buildDeleteCommand(String paramCommand)
    {
        SendCommandTask localSendCommandTask = new SendCommandTask(CommandID.AMBA_DEL);
        localSendCommandTask.addParam("param", paramCommand);
        return localSendCommandTask;
    }

    public static SendCommandTask buildFormatSDCardTask()
    {
        SendCommandTask localSendCommandTask = new SendCommandTask(CommandID.AMBA_FORMAT_SD);
        localSendCommandTask.addParam("param", "C");
        return localSendCommandTask;
    }

    public static SendCommandTask buildGetALlCurrentSettingTask()
    {
        return new SendCommandTask(CommandID.AMBA_GET_ALL);
    }

    /**
     * 过滤当前app_status是否是idle状态
     */
    public static SendCommandTask.SendCommandPipeFilter buildGetAppStatusFilter()
    {
        return new SendCommandTask.SendCommandPipeFilter()
        {
            public SendCommandTask.FilterState doFilter(JSONObject jsonObject)
            {
                int i = jsonObject.optInt("msg_id");
                String str1 = jsonObject.optString("type");
                String str2 = jsonObject.optString("param");
                Constant.CameraStatus = str2;  //保存 状态   ， 用来设置完成后，恢复状态 start_record  or start_vf
                if ((i == 1) && ("app_status".equals(str1)))
                {
                    if ("idle".equals(str2)) { //如果是空闲状态的话
                        try {
                            Thread.sleep(700);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return SendCommandTask.FilterState.PASS;
                    }
                    return SendCommandTask.FilterState.FAIL;
                }
                return SendCommandTask.FilterState.NONE;
            }
        };
    }
    /**
     * 过滤当前app_status是否是vf状态
     */
    public static SendCommandTask.SendCommandPipeFilter buildhCeckVFOrRecordFilter()
    {
        return new SendCommandTask.SendCommandPipeFilter()
        {
            public SendCommandTask.FilterState doFilter(JSONObject jsonObject)
            {
                int i = jsonObject.optInt("msg_id");
                String str1 = jsonObject.optString("type");
                String str2 = jsonObject.optString("param");
                if ((i == 1) && ("app_status".equals(str1)))
                {
                    if ("vf".equals(str2)||"record".equals(str2)) { //如果是view find或者record状态
                        return SendCommandTask.FilterState.PASS;
                    }
                    return SendCommandTask.FilterState.FAIL;
                }
                return SendCommandTask.FilterState.NONE;
            }
        };
    }

    /**
     * 过滤当前是
     * @return
     */
    public static SendCommandTask.SendCommandPipeFilter buildCheckRecordFilter()
    {
        return new SendCommandTask.SendCommandPipeFilter()
        {
            public SendCommandTask.FilterState doFilter(JSONObject jsonObject)
            {
                int i = jsonObject.optInt("msg_id");
                String str1 = jsonObject.optString("type");
                String str2 = jsonObject.optString("param");
                if ((i == 1) && ("app_status".equals(str1)))
                {
                    if ("record".equals(str2)) //record状态
                        return SendCommandTask.FilterState.PASS;
                    return SendCommandTask.FilterState.FAIL;
                }
                return SendCommandTask.FilterState.NONE;
            }
        };
    }

    public static SendCommandTask buildGetSDCardSpaceTask(String type)
    {
        SendCommandTask localSendCommandTask = new SendCommandTask(CommandID.AMBA_GET_SPACE);
        localSendCommandTask.addParam("type", type);
        return localSendCommandTask;
    }

    public static SendCommandTask buildGetSetting(String type)
    {
        SendCommandTask localSendCommandTask = new SendCommandTask(CommandID.AMBA_GET_SETTING);
        localSendCommandTask.addParam("type", type);
        return localSendCommandTask;
    }

    public static SendCommandTask buildGetSettingTask(String type)
    {
        SendCommandTask localSendCommandTask = new SendCommandTask(1);
        localSendCommandTask.addParam("type", type);
        return localSendCommandTask;
    }

    public static SendCommandTask buildSetSettingTask(String type, String param)
    {
        return new SendCommandTask(CommandID.AMBA_SET_SETTING).addParam("type", type).addParam("param", param);
    }

    public static SendCommandTask.SendCommandPipeFilter buildStopVFFilter()
    {
        return new SendCommandTask.SendCommandPipeFilter()
        {
            public SendCommandTask.FilterState doFilter(JSONObject jsonObject)
            {
                int i = jsonObject.optInt("msg_id");
                String stateType = jsonObject.optString("type");
                String appState = jsonObject.optString("param");
                if (i == CommandID.AMBA_NOTIFICATION && "app_status".equals(stateType)&&"idle".equals(appState) ) {
                    return SendCommandTask.FilterState.PASS;
                }
                return SendCommandTask.FilterState.NONE;
            }
        };
    }



    public static SendCommandTask.SendCommandPipeFilter buildStopRecordFilter()
    {
        return new SendCommandTask.SendCommandPipeFilter()
        {
            public SendCommandTask.FilterState doFilter(JSONObject jsonObject)
            {
                int i = jsonObject.optInt("msg_id");
                int ravl = jsonObject.optInt("ravl");
                if ((i == CommandID.AMBA_RECORD_STOP) && (0==ravl)) {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return SendCommandTask.FilterState.PASS;
                }
                return SendCommandTask.FilterState.NONE;
            }
        };
    }
     //过滤AMBA_NOTIFICATION    如果通知返回的是 vf_stop   表明停止VF成功
//    public static SendCommandTask.SendCommandPipeFilter buildStopVFFilter()
//    {
//        return new SendCommandTask.SendCommandPipeFilter()
//        {
//            public SendCommandTask.FilterState doFilter(JSONObject jsonObject)
//            {
//                int i = jsonObject.optInt("msg_id");
//                String str = jsonObject.optString("type");
//                if ((i == CommandMsgId.AMBA_NOTIFICATION) && ("vf_stop".equals(str)))
//                    return SendCommandTask.FilterState.PASS;
//                return SendCommandTask.FilterState.NONE;
//            }
//        };
//    }
//
//    public static SendCommandTask.SendCommandPipeFilter buildStartVFFilter()
//    {
//        return new SendCommandTask.SendCommandPipeFilter()
//        {
//            public SendCommandTask.FilterState doFilter(JSONObject jsonObject)
//            {
//                int i = jsonObject.optInt("msg_id");
//                String str = jsonObject.optString("type");
//                if ((i == CommandMsgId.AMBA_RESETVF) && ("vf_start".equals(str)))
//                    return SendCommandTask.FilterState.PASS;
//                return SendCommandTask.FilterState.NONE;
//            }
//        };
//    }
    public static SendCommandTask.SendCommandPipeFilter buildStartVFFilter()
    {
        return new SendCommandTask.SendCommandPipeFilter()
        {
            public SendCommandTask.FilterState doFilter(JSONObject jsonObject)
            {
                int i = jsonObject.optInt("msg_id");
                int ravl = jsonObject.optInt("rval");
                if ((i == CommandID.AMBA_RESETVF) && (ravl==0)) {
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return SendCommandTask.FilterState.PASS;
                }
                return SendCommandTask.FilterState.NONE;
            }
        };
    }

    public static SendCommandTask buildStopVFTask()
    {
        return new SendCommandTask(CommandID.AMBA_STOP_VF);
    }
    public static SendCommandTask buildSartVFTask()
    {
        return new SendCommandTask(CommandID.AMBA_RESETVF);
    }

    /**
     * 停止录像状态
     * @return
     */
    public static SendCommandTask buildStopRecordTask()
    {
        return new SendCommandTask(CommandID.AMBA_RECORD_STOP);
    }

    public static void startTask(SendCommandTask paramSendCommandTask)
    {
        paramSendCommandTask.start();
    }
}
