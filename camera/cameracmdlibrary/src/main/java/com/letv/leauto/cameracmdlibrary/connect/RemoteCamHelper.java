package com.letv.leauto.cameracmdlibrary.connect;


import com.letv.leauto.cameracmdlibrary.common.Config;
import com.letv.leauto.cameracmdlibrary.common.Constant;
import com.letv.leauto.cameracmdlibrary.connect.event.AutoConnectToCameraEvent;
import com.letv.leauto.cameracmdlibrary.connect.event.ConnectToCameraEvent;
import com.letv.leauto.cameracmdlibrary.connect.event.EventBusHelper;
import com.letv.leauto.cameracmdlibrary.connect.event.SDSizeLackEvent;
import com.letv.leauto.cameracmdlibrary.connect.model.CameraMessage;
import com.letv.leauto.cameracmdlibrary.connect.model.CameraMessageCallback;
import com.letv.leauto.cameracmdlibrary.connect.model.CommandID;
import com.letv.leauto.cameracmdlibrary.connect.socket.CMDSocketConnect;
import com.letv.leauto.cameracmdlibrary.connect.socket.DownLoadCallBack;
import com.letv.leauto.cameracmdlibrary.connect.socket.SocketDataConnect;
import com.letv.leauto.cameracmdlibrary.connect.socket.UpLoadCallBack;
import com.letv.leauto.cameracmdlibrary.utils.HashUtils;
import com.letv.leauto.cameracmdlibrary.utils.JsonUtils;
import com.letv.leauto.cameracmdlibrary.utils.Logger;
import com.letv.leauto.cameracmdlibrary.utils.SdCardUtils;
import com.letv.leauto.cameracmdlibrary.utils.SystemUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by lixinlei on 15/11/5.
 */
public class RemoteCamHelper {
    private static final String TAG = "RemoteCamHelper";
    private static RemoteCamHelper remoteCamHelper;
    public static boolean isStartSessionSuccess = false;


    private CMDSocketConnect socketConnect;
    private SocketDataConnect socketDataConnect;


    /**
     * 上传或下载的路径
     */
    private String currentFilePath;


    private static final ExecutorService worker =
            Executors.newSingleThreadExecutor();

    public static RemoteCamHelper getRemoteCam() {
        if (remoteCamHelper == null) {
            remoteCamHelper = new RemoteCamHelper();
        }
        return remoteCamHelper;
    }

    private RemoteCamHelper() {
        if (socketConnect == null) {
            socketConnect = new CMDSocketConnect();
            socketDataConnect = new SocketDataConnect();
            setWifiIP(Config.CAMERA_IP, Config.CMD_PORT_NUM, Config.DATA_PORT_NUM);
        }
    }

    public RemoteCamHelper setWifiIP(String host, int cmdPort, int dataPort) {
        socketConnect.setIP(host, cmdPort);
        socketDataConnect.setIP(host, dataPort);
        return this;
    }

    public void sendCommand(final CameraMessage cameraMessage) {
        worker.execute(new Runnable() {
            public void run() {
                //检测channel是否连接
                if (!connectToCmdChannel())
                    return;
                if (!connectToDataChannel())
                    return;
                socketConnect.sendCommand(cameraMessage);
            }
        });
    }


    public void startSession() {
        Logger.d(TAG,"startSession() -->| #####startSession#####");
        worker.execute(new Runnable() {
            public void run() {
                //检测channel是否连接
                if (!connectToCmdChannel()){
                    Logger.d(TAG," startSession() -->| "+"*****startSession ERROR!*****");
                    EventBusHelper.postConnectToCamera(new AutoConnectToCameraEvent(0));
                    return;
                }
                socketConnect.sendCommand(new CameraMessage(CommandID.AMBA_START_SESSION, new CameraMessageCallback() {
                    @Override
                    public void onReceiveErrorMessage(CameraMessage cameraMessage, JSONObject jsonObject) {
                        String json = "";
                        if(json != null){
                            json = jsonObject.toString();
                        }
                        Logger.d(TAG," startSession() -->| "+"AMBA_START_SESSION error message=" + json);
                    }

                    @Override
                    public void onReceiveMessage(CameraMessage cameraMessage, JSONObject jsonObject) {
                        String json = "";
                        if(json != null){
                            json = jsonObject.toString();
                        }
                        Logger.d(TAG," startSession() -->| "+"AMBA_START_SESSION message=" + json);
                        EventBusHelper.postConnectToCamera(new ConnectToCameraEvent(true));
                        isStartSessionSuccess = true;
                        //记录仪授时
                        SyncTime();
                        //检测SD卡是否存在
                        checkSdCardIsPresent();

                    }

                    @Override
                    public void onReceiveNotification(JSONObject jsonObject) {
                        String json = "";
                        if(json != null){
                            json = jsonObject.toString();
                        }
                        Logger.d(TAG," startSession() -->| "+"AMBA_START_SESSION notification message=" + json);
                    }
                }));
            }
        });
    }
    public void startSession(final CameraMessageCallback cameraMessageCallback) {
        worker.execute(new Runnable() {
            public void run() {
                //检测channel是否连接
                if (!connectToCmdChannel())
                    return;
                socketConnect.sendCommand(new CameraMessage(CommandID.AMBA_START_SESSION, cameraMessageCallback));
            }
        });
    }

    /**
     * 检测SD卡是否存在
     */
    public void checkSdCardIsPresent() {

        CameraMessage getLSMessage = new CameraMessage(CommandID.AMBA_SDCARD_STATUS, new CameraMessageCallback() {
            @Override
            public void onReceiveErrorMessage(CameraMessage cameraMessage, JSONObject jsonObject) {
                Logger.d(TAG," checkSdCardIsPresent() -->| "+"checkSdCardIsPresent:" + cameraMessage);
            }

            @Override
            public void onReceiveMessage(CameraMessage cameraMessage, JSONObject jsonObject) {
                Logger.d(TAG," checkSdCardIsPresent() -->| "+ jsonObject.toString());
                try {
                    Constant.isSDCardPresent = JsonUtils.parseSdJsonObject(jsonObject);
                    Logger.d(TAG," checkSdCardIsPresent() -->| "+ "the present of sd card is :" + Constant.isSDCardPresent);
//                    EventBus.getDefault().post(new CheckSdStatusFinished(true));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onReceiveNotification(JSONObject jsonObject) {
                Logger.d(TAG," checkSdCardIsPresent() -->| "+jsonObject.toString());
            }
        });
        getLSMessage.put("type", "sd");

        RemoteCamHelper.getRemoteCam().sendCommand(getLSMessage);
    }

    /**
     * 给记录仪授时
     */
    public void SyncTime(){
        Calendar c1 = Calendar.getInstance();
        CameraClient cameraClient = new CameraClient();
        c1.setTime(new Date());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        cameraClient.setSetting("camera_clock", format.format(c1.getTime()), new SendCommandCallback() {
            @Override
            public void onFail(CameraMessage cameraMessage, JSONObject jsonObject) {
                Logger.d(TAG," SyncTime() -->| "+ "SyncTime  时间同步失败");
            }

            @Override
            public void onSuccess(CameraMessage cameraMessage, JSONObject jsonObject) {
                Logger.d(TAG," SyncTime() -->| "+ "SyncTime  时间同步成功");
            }
        }, false);
    }

    public void stopSession() {
        worker.execute(new Runnable() {
            public void run() {
                //检测channel是否连接
                if (!connectToCmdChannel())
                    return;
                socketConnect.sendCommand(new CameraMessage(CommandID.AMBA_STOP_SESSION));
            }
        });
    }

    public void upLoadPW(String path, UpLoadCallBack callBack) {
        if (!connectToDataChannel())
            return;
        socketDataConnect.putFile(path, callBack);
    }

    public void setClntInfo() {
        socketConnect.setClntInfo("TCP");
    }

    /**
     * 获取要下载文件的信息
     *
     * @param filePath
     * @param callback
     */
    public void getFile(final String filePath, final SendCommandCallback callback) {
        CameraMessage getFileMessage = new CameraMessage(CommandID.AMBA_GET_FILE, new CameraMessageCallback() {
            @Override
            public void onReceiveErrorMessage(CameraMessage cameraMessage, JSONObject jsonObject) {
                callback.onFail(cameraMessage, jsonObject);
            }

            @Override
            public void onReceiveMessage(final CameraMessage cameraMessage, final JSONObject jsonObject) {
                int index = filePath.lastIndexOf("/");
                final String name = filePath.substring(index);
                long size = jsonObject.optLong("size");
                Logger.d(TAG," getFile() -->| "+SdCardUtils.getPhotoPath() + name + "   ---");
                if (SystemUtils.getSDAvailableSize() > size) {
                    socketDataConnect.getFile(SdCardUtils.getPhotoPath() + name, (int) size, new DownLoadCallBack() {
                        @Override
                        public void onStart(String path) {

                        }

                        @Override
                        public void onEnd() {

                        }

                        @Override
                        public void onEnd(String d) {
                            callback.onSuccess(cameraMessage, jsonObject);
                        }

                        @Override
                        public void onFailure() {
                            callback.onFail(cameraMessage, jsonObject);
                        }

                        @Override
                        public void onProgress(int paramInt) {

                        }
                    });

                } else {
                    EventBusHelper.postSDSizeLackEvent(new SDSizeLackEvent());
                }
            }

            @Override
            public void onReceiveNotification(JSONObject jsonObject) {

            }
        });
        getFileMessage.put("fetch_size", 0);
        getFileMessage.put("offset", 0);
        getFileMessage.put("param", filePath);
        sendCommand(getFileMessage);
    }
    public void putFile(final String path, final CameraMessageCallback cameraMessageCallback) {
        putFile(path, null, cameraMessageCallback);
    }
    /**
     * 上传文件
     */
    public void putFile(final String path, final String md5, final CameraMessageCallback cameraMessageCallback) {
        worker.execute(new Runnable() {
            public void run() {
                if (!connectToCmdChannel() || !connectToDataChannel())
                    return;
                File file = new File(path);
                String md5String = md5;
                if (md5String == null || md5String.equalsIgnoreCase("")) {
                    try {
                        md5String = HashUtils.getMd5ByFile(file);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                currentFilePath = Constant.EDRROOTFWPATH + file.getName();
                CameraMessage cameraMessage = new CameraMessage(CommandID.AMBA_PUT_FILE, cameraMessageCallback);
                cameraMessage.put("param", Constant.EDRROOTFWPATH + file.getName());
                cameraMessage.put("size", file.length());
                cameraMessage.put("md5sum", md5String);
                cameraMessage.put("offset", 0);
                socketConnect.sendCommand(cameraMessage);
            }
        });
    }

    /**
     * 取消上传
     */
    public void cancelPutFile() {
//        worker.execute(new Runnable() {
//            public void run() {
//                if (!connectToCmdChannel())
//                    return;
        int xfer_size = socketDataConnect.cancelPutFile();
        CameraMessage cameraMessage = new CameraMessage(CommandID.AMBA_CANCLE_XFER, new CameraMessageCallback() {
            @Override
            public void onReceiveErrorMessage(CameraMessage cameraMessage, JSONObject jsonObject) {

            }

            @Override
            public void onReceiveMessage(CameraMessage cameraMessage, JSONObject jsonObject) {

            }

            @Override
            public void onReceiveNotification(JSONObject jsonObject) {

            }
        });
        cameraMessage.put("param", currentFilePath);
        cameraMessage.put("sent_size", xfer_size);
        cameraMessage.setIsNeedSelfError(true);
        sendCommand(cameraMessage);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        socketDataConnect.setIsConnect(false);

//            }
//        });
    }
    /**
     * 取消上传
     */
    public void cancelGetFile(String filePath,CameraMessageCallback cameraMessageCallback) {
//        worker.execute(new Runnable() {
//            public void run() {
//                if (!connectToCmdChannel())
//                    return;
        CameraMessage cameraMessage = new CameraMessage(CommandID.AMBA_CANCLE_XFER, cameraMessageCallback);
        cameraMessage.put("param", filePath);
        cameraMessage.setIsNeedSelfError(true);
        sendCommand(cameraMessage);

//            }
//        });
    }
    /**
     * 检查是否链接上了CMD
     *
     * @return
     */
    private boolean connectToCmdWIFI() {
        return connectToCmdWIFIWithRetry(1);
    }


    private boolean connectToCmdWIFIWithRetry(int retry) {
        // 检查是否已经连接
        if (socketConnect.isConnected()){
            Logger.d(TAG," connectToCmdWIFIWithRetry() -->| "+"connectToCmdWIFI socket has connect!");
            return true;
        }
        // 检查我们能否连接到wifi
        for(int i=0;i<retry;i++){
            if (socketConnect.connect()) {
                return true;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean isConnectNow() {
        return socketConnect.isConnected();
    }

    public void closeChannel() {
        socketConnect.close();
        socketDataConnect.close();
    }

    private boolean connectToDataWIFI() {
        if (socketDataConnect.isConnect())
            return true;
        // 检查我们能否连接到接受数据的Channel
        //wifi情况下设置  协议为  TCP
        socketConnect.setClntInfo("TCP");
        if (socketDataConnect.connect()) {
            return true;
        }
        return false;
    }


    private boolean connectToDataChannel() {
        return connectToDataWIFI();
    }

    private boolean connectToCmdChannel() {
        return connectToCmdWIFIWithRetry(5);
    }

}
