package com.letv.leauto.cameracmdlibrary.connect.socket;

import android.text.TextUtils;

import com.letv.leauto.cameracmdlibrary.common.Config;
import com.letv.leauto.cameracmdlibrary.common.Constant;
import com.letv.leauto.cameracmdlibrary.connect.event.ChannelErrorEventType;
import com.letv.leauto.cameracmdlibrary.connect.event.EventBusHelper;
import com.letv.leauto.cameracmdlibrary.connect.event.OtherPhoneConnectedEvent;
import com.letv.leauto.cameracmdlibrary.connect.model.CameraMessage;
import com.letv.leauto.cameracmdlibrary.connect.model.CameraMessageCallback;
import com.letv.leauto.cameracmdlibrary.connect.model.CommandID;
import com.letv.leauto.cameracmdlibrary.utils.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lixinlei on 15/11/5.
 */
public class CMDSocketConnect {
    private static final String TAG = CMDSocketConnect.class.getSimpleName();
    private boolean isNeedReadThread = true;

    /**
     * 同步锁
     */
    private Object rxLock = new Object();
    /**
     * 读取数据的超时时间
     */
    private final int RX_TIME_OUT = 20000;

    /**
     * 连接超时时间
     */
    private final int CONN_TIME_OUT = 10000;
    /**
     * connect to  camera
     */
    private Socket socket;

    /**
     * the ip of server
     */
    private String hostName;

    /**
     * the port of server
     */
    private int portNum;

    /**
     * 手机的ip
     */
    private String localIpAddress;

    /**
     * receviver message from camera
     */
    private InputStream socketInputStream;

    /**
     * send message to camera
     */
    private OutputStream socketOutputStream;

    private CameraMessage currentCameraMessage;

    /**
     * 是否已经连接
     */
    private boolean isConnected = false;
    /**
     * session id
     */
    private int sessionId = 0;

    /**
     * 是否需要 自动开启session
     */
    private boolean isNeedAutoStartSession = false;
    /**
     * 是否接收完成
     */
    private boolean replyReceived;


    /**
     * 读取数据的字节数组
     */
    private byte[] mBuffer = new byte[1024];

    /**
     * 开启一个线程去监听socket 读取数据
     */
    public void startIO() {
        (new Thread(new QueueRunnable())).start();
    }


    /**
     * init socket connect
     */
    public boolean connect() {
        if (null != socket) {
            try {
                socket.close();
                isConnected = false;
            } catch (IOException e) {
                Logger.d(TAG, "connect() -->| socket close error!"+e);
            }
            socket = null;
        }
        socket = new Socket();
        try {
            isNeedReadThread = true;
            Logger.d(TAG,"connect() -->| Connect socket ip=" + Config.CAMERA_IP + "  port=" + portNum);
            socket.connect(new InetSocketAddress(Config.CAMERA_IP, portNum), CONN_TIME_OUT);
            socketInputStream = socket.getInputStream();
            socketOutputStream = socket.getOutputStream();
            localIpAddress = socket.getLocalAddress().getHostAddress();
            Constant.phoneIP = localIpAddress;
            isConnected = true;
            startIO();
            return true;
        } catch (Exception e) {
            isConnected = false;
            isNeedReadThread = false;
            Logger.d(TAG,"connect() -->|  Socket connect error!"+e);
            EventBusHelper.postChannelError(ChannelErrorEventType.CMD_CHANNEL_ERROR_TIMEOUT);
        }
        return false;
    }

    /**
     * close socket
     */
    public void close() {
        try {
            isNeedReadThread = false;
            if (socketOutputStream != null) {
                socketOutputStream.close();
                socketOutputStream = null;
            }
            if (socketInputStream != null) {
                socketInputStream.close();
                socketInputStream = null;
            }
            if (socket != null) {
                socket.close();
                socket = null;
            }
            isConnected = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置 ip  和  端口
     */
    public void setIP(String hostName, int port) {
        this.hostName = hostName;
        this.portNum = port;
    }


    /**
     * 是否连接到camera
     */
    public boolean isConnected() {
        return isConnected;
    }

    /*****************************
     * 发送命令
     **********************************/
    public synchronized boolean sendCommand(CameraMessage cameraMessage) {
        if (cameraMessage == null) {
            return false;
        }
        int command = cameraMessage.getCommand();
        currentCameraMessage = cameraMessage;
        if (checkSessionID(command)) {
            replyReceived = false;
            cameraMessage.put("token", Constant.token);
            Logger.d(TAG,"sendCommand() -->| Request :" + cameraMessage.getMessageContent());
            writeToChannel(cameraMessage.getMessageContent().getBytes());
            return waitForReply();
        } else {
            return false;
        }
    }

    private boolean waitForReply() {
        try {
            synchronized (rxLock) {
                rxLock.wait(RX_TIME_OUT);
            }
            if (!replyReceived) {
                replyReceived = true;
                //FIXME
                EventBusHelper.postChannelError(ChannelErrorEventType.CMD_CHANNEL_ERROR_READ_TIMEOUT);
                return false;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 写数据
     *
     * @param bytes
     */
    private void writeToChannel(byte[] bytes) {
        try {
            socketOutputStream.write(bytes);
        } catch (IOException e) {
            isConnected = false;
            isNeedReadThread = false;
            e.printStackTrace();

            Logger.d(TAG,"CMD writeToChannel() -->| error : "+"写数据异常");

            //FIXME
            EventBusHelper.postChannelError(ChannelErrorEventType.CMD_CHANNEL_ERROR_BROKEN_CHANNEL_WRITE);
        }
    }

    /**
     * 读取数据
     */
    protected String readFromChannel() {
        try {
            if (socketInputStream != null) {
                int size = socketInputStream.read(mBuffer);
                String responeS = new String(mBuffer, 0, size);
                Logger.d(TAG,"readFromChannel() -->| 读取到的原始数据 : " + responeS);
                return replaceBlank(responeS);
            }
        } catch (Exception e) {
            Logger.d(TAG,"readFromChannel() -->| error : "+e);
            isConnected = false;
            isNeedReadThread = false;
            //FIXME
            EventBusHelper.postChannelError(ChannelErrorEventType.CMD_CHANNEL_ERROR_BROKEN_CHANNEL);
        }
        return null;
    }

    private boolean checkSessionID(int command) {
        if (!isConnected) {
            if (!connect())
                return false;
        }
        if (sessionId > 0 || CommandID.AMBA_START_SESSION == command) {
            return true;
        }

        //如果没有开启session，并且 isNeedAutoStartSession ＝ true 则开启session ， 然后从新发送上一次的命令
        if (isNeedAutoStartSession) {
                final CameraMessage localCameraMessage = currentCameraMessage;
                sendCommand(new CameraMessage(CommandID.AMBA_START_SESSION, new CameraMessageCallback() {
                    @Override
                    public void onReceiveErrorMessage(CameraMessage cameraMessage, JSONObject jsonObject) {

                    }

                    @Override
                    public void onReceiveMessage(CameraMessage cameraMessage, JSONObject jsonObject) {
                        sendCommand(localCameraMessage);
                    }

                @Override
                public void onReceiveNotification(JSONObject jsonObject) {

                }
            }));
        }
        return false;
    }

    /****************************************************************************/

    public boolean setClntInfo(String type) {
        CameraMessage cameraMessage = new CameraMessage(CommandID.AMBA_SET_CLINT_INFO);
        cameraMessage.put("type", type);
        cameraMessage.put("param", localIpAddress);
        return sendCommand(cameraMessage);
    }
    /**
     * 用来读取数据的线程
     */
    class QueueRunnable implements Runnable {
        private static final String regex = "\\{[^\\{\\}]*(\\{[^\\{\\}]*\\}[^\\{\\}]*)*(\\[\\{[^\\{\\}]*\\}\\][^\\{\\}]*)*\\}";

        //服务器产生事件，主动通知客户端
        private void handleNotification(JSONObject parser) {
            try {
                if (parser.getInt("msg_id") == CommandID.AMBA_NOTIFICATION) {
                    String type = parser.getString("type");
                    if (currentCameraMessage.getCallback() != null) {
                        currentCameraMessage.getCallback().onReceiveNotification(parser);
                    }
                    EventBusHelper.postCameraNotification(parser);
                }
            } catch (Exception e) {
                System.out.print(e.getMessage());
            }
        }

        //服务器响应成功字段，则执行该方法
        private void handleResponse(final JSONObject parser) {
            try {
                final int rval = parser.getInt("rval");
                int msgId = parser.getInt("msg_id");
                final CameraMessage tempCameraMessage = currentCameraMessage;
                Logger.d(TAG,"handleResponse()  -->| rval = " + rval + ", msgId= " + msgId);
                String str;
                if (rval != 0) {
                    switch (rval) {
                        case ChannelErrorEventType.CMD_CHANNEL_ERROR_INVALID_TOKEN:
                            sessionId = 0;
                            Constant.token = 0;
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    EventBusHelper.postChannelError(ChannelErrorEventType.CMD_CHANNEL_ERROR_INVALID_TOKEN, parser);
                                }
                            }).start();
                            break;
                        default:
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    if (tempCameraMessage.getCallback() != null) {
                                        tempCameraMessage.getCallback().onReceiveErrorMessage(tempCameraMessage, parser);
                                    }
                                    if (!tempCameraMessage.isNeedSelfError()) {
                                        EventBusHelper.postChannelError(rval);
                                    }
                                }
                            }).start();

                            break;
                    }

                } else {
                    switch (msgId) {
                        case CommandID.AMBA_START_SESSION:
                            str = parser.getString("param");
                            Pattern p = Pattern.compile("\\d+");
                            Matcher m = p.matcher(str);
                            if (m.find()) {
                                sessionId = Integer.parseInt(m.group(0));
                            }
                            Constant.token = sessionId;
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    if (tempCameraMessage.getCallback() != null) {
                                        tempCameraMessage.getCallback().onReceiveMessage(tempCameraMessage, parser);
                                    }
                                }
                            }).start();
                            break;
                        case CommandID.AMBA_STOP_SESSION:
                            sessionId = 0;
                            Constant.token = sessionId;
                            break;
                        default:
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    if (tempCameraMessage.getCallback() != null) {
                                        tempCameraMessage.getCallback().onReceiveMessage(tempCameraMessage, parser);
                                    }
                                }
                            }).start();

                            //FIXME    如果需要全局的接收成功通知   请在此处进行操作
//                            EventBus.getDefault().post(new ChannelEvent(msgId, parser, currentBaseRequest));
                            break;
                    }
                }
            } catch (JSONException e) {
                Logger.d(TAG,"handleResponse() -->| error : "+e);
            }
        }

        public void run() {
            try {
                String msg;
                while (isNeedReadThread) {
                    String msgTemp = readFromChannel();
                    msg = replaceBlank(msgTemp);
                    Logger.d(TAG,"run()  -->| Respone : " + msg);
                    if (TextUtils.isEmpty(msg))
                       break;
                    if (msg.indexOf("}{") != -1) {
                        String[] msgJsonStr = msg.split("\\}\\{");
                        for (int k = 0; k < msgJsonStr.length; k++) {
                            if (k == 0) {
                                msg = msgJsonStr[k] + "}";
                                dispatchMessageResponse(msg);
                            } else if (k == msgJsonStr.length - 1) {
                                msg = "{" + msgJsonStr[k];
                                while (true) {
                                    try {
                                        new JSONObject(msg);
                                        break;
                                    } catch (JSONException e) {
                                        Logger.d(TAG,"run()-->| error :  MSG exist '}{' convert msg to json object error!");
                                         //因为一次只能读取1024byte，可能会读不全，那么就需要拼接起来
                                        String tempMsg = readFromChannel();
                                        if (tempMsg != null) {
                                            msg += tempMsg;
                                        }
                                    }
                                }
                                dispatchMessageResponse(msg);
                            } else {
                                msg = "{" + msgJsonStr[k] + "}";
                                dispatchMessageResponse(msg);
                            }
                        }
                    } else {
                        while (true) {
                            try {
                                new JSONObject(msg);
                                break;
                            } catch (JSONException e) {
                                Logger.d(TAG,"run() -->| error : MSG don't exist '}{' convert msg to json object error!");
                                Logger.d(TAG,"run() -->| error : JSON part: " + msg);
                                if (msg.startsWith("{")) {
                                    //因为一次只能读取1024byte，可能会读不全，那么就需要拼接起来
                                    msg += readFromChannel();
                                }else {
                                    Logger.d(TAG,"run() 读取到不合法的字段");
                                    break;
                                }
                            }
                        }
                        dispatchMessageResponse(msg);
                    }
                }
            } catch (Exception e) {
                Logger.d(TAG,"run() -->| error : QueueRunnable error!"+e);
                synchronized (rxLock) {
                    rxLock.notify();
                }
            }
        }

        /**
         * 分配接收到的是  response的值，还是notification的值
         *
         * @param paramString 接收到的完整的JSON串
         */
        private void dispatchMessageResponse(String paramString) {
            if (paramString == null) {
                Logger.d(TAG,"dispatchMessageResponse() -->|  paramString is null!");
                return;
            }

            try {
//                Logger.d("Respone: " + paramString);

                JSONObject jsonObject = new JSONObject(paramString);
                int i = jsonObject.optInt("msg_id");
                //TODO 如果发现是1793  是否保留上次的session
                if (i == CommandID.AMBA_QUERY_SESSION_HOLDER&&Constant.token!=0) {
                    if (currentCameraMessage.getCommand() != CommandID.AMBA_START_SESSION){
                        EventBusHelper.postOtherPhoneConnectedEvent(new OtherPhoneConnectedEvent());
                    }
                    return;
                }
                if (paramString.contains("rval")) {
                    if (!replyReceived) {
                        handleResponse(jsonObject);
                        replyReceived = true;
                        synchronized (rxLock) {
                            rxLock.notify();
                        }
                    }
                } else if (paramString.contains("268435457")) {
                    EventBusHelper.postGSenserEvent(paramString);
                } else if (paramString.contains("268435462")) {
                    EventBusHelper.postGPSInfo(paramString);
                }else {
                    handleNotification(jsonObject);
                }

            } catch (JSONException e) {
                Logger.d(TAG,"dispatchMessageResponse() error!");
            }
        }
    }

    /**
     * 正则
     */
    public String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        String s = dest.replace("null", "");
        String ss = s.replace("�", "");
        return ss;
    }
}
