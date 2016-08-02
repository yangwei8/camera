package com.leautolink.leautocamera.connect;

import android.os.SystemClock;

import com.leautolink.leautocamera.application.LeautoCameraAppLication;
import com.leautolink.leautocamera.callback.ApConnectedSuccessCallback;
import com.leautolink.leautocamera.event.ConnectSuccessEvent;
import com.leautolink.leautocamera.utils.Logger;
import com.letv.leauto.cameracmdlibrary.common.Config;
import com.letv.leauto.cameracmdlibrary.connect.RemoteCamHelper;
import com.letv.leauto.cameracmdlibrary.connect.model.CameraMessage;
import com.letv.leauto.cameracmdlibrary.connect.model.CameraMessageCallback;

import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by lixinlei on 16/3/4.
 */
public class MyServer {
    //定义ServerSocket的端口号
    private static final int SOCKET_PORT = 7876;

    private long  olderTimer = 0;
    private boolean  isFirst = true;

    public void initMyServer(final ApConnectedSuccessCallback apConnectedSuccessCallback) {
        try {
            Logger.e("start---------");
            olderTimer = System.currentTimeMillis();
            //创建一个ServerSocket，用于监听客户端Socket的连接请求
            DatagramSocket serverSocket = new DatagramSocket(SOCKET_PORT);
            byte[] byts = new byte[1024];
            DatagramPacket datagramPacket = new DatagramPacket(byts, byts.length);
            while (true) {
                //每当接收到客户端的Socket请求，服务器端也相应的创建一个Socket
                Logger.e("记录仪发送了udp");

                serverSocket.receive(datagramPacket);

                if (datagramPacket != null) {
                    long newTimer = System.currentTimeMillis();
                    if(isFirst){
                        isFirst = false;
                        olderTimer = newTimer;
                        Logger.e("-------连接成功");
                        Logger.e("-------" + datagramPacket.getAddress().getHostAddress() + "  -- " +
                                ((InetSocketAddress) datagramPacket.getSocketAddress()).getHostName());
                        Config.CAMERA_IP = datagramPacket.getAddress().getHostAddress();
                        RemoteCamHelper.getRemoteCam().setWifiIP(Config.CAMERA_IP, Config.CMD_PORT_NUM, Config.DATA_PORT_NUM);
                        RemoteCamHelper.getRemoteCam().startSession(new CameraMessageCallback() {
                            @Override
                            public void onReceiveErrorMessage(CameraMessage cameraMessage, JSONObject jsonObject) {

                            }

                            @Override
                            public void onReceiveMessage(CameraMessage cameraMessage, JSONObject jsonObject) {
                                String rstpUrl = "rtsp://" + Config.CAMERA_IP + "/live";
                                LeautoCameraAppLication.isApConnectCamera = true;
                                apConnectedSuccessCallback.successed();
                                EventBus.getDefault().post(new ConnectSuccessEvent(rstpUrl));
                            }

                            @Override
                            public void onReceiveNotification(JSONObject jsonObject) {

                            }
                        });
                    }else {
                        Logger.e(newTimer -olderTimer  +   "      shijian");
                        if (newTimer - olderTimer >20000&&LeautoCameraAppLication.isApConnectCamera){
                            olderTimer = newTimer;
                            RemoteCamHelper.getRemoteCam().setWifiIP(Config.CAMERA_IP, Config.CMD_PORT_NUM, Config.DATA_PORT_NUM);
                            RemoteCamHelper.getRemoteCam().startSession(new CameraMessageCallback() {
                                @Override
                                public void onReceiveErrorMessage(CameraMessage cameraMessage, JSONObject jsonObject) {

                                }

                                @Override
                                public void onReceiveMessage(CameraMessage cameraMessage, JSONObject jsonObject) {
                                    String rstpUrl = "rtsp://" + Config.CAMERA_IP + "/live";
                                    LeautoCameraAppLication.isApConnectCamera = true;
                                    apConnectedSuccessCallback.successed();
                                    EventBus.getDefault().post(new ConnectSuccessEvent(rstpUrl));
                                }

                                @Override
                                public void onReceiveNotification(JSONObject jsonObject) {

                                }
                            });
                        }
                    }

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
