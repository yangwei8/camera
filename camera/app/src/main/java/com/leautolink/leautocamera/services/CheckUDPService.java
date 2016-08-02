package com.leautolink.leautocamera.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.leautolink.leautocamera.application.LeautoCameraAppLication;
import com.leautolink.leautocamera.event.ConnectSuccessEvent;
import com.leautolink.leautocamera.event.UDPTimeOutEvent;
import com.leautolink.leautocamera.utils.Logger;
import com.letv.leauto.cameracmdlibrary.common.Config;
import com.letv.leauto.cameracmdlibrary.connect.RemoteCamHelper;
import com.letv.leauto.cameracmdlibrary.connect.model.CameraMessage;
import com.letv.leauto.cameracmdlibrary.connect.model.CameraMessageCallback;

import org.json.JSONObject;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;

public class CheckUDPService extends Service {
    private String rtspUrl;
    //定义ServerSocket的端口号
    private static final int SOCKET_PORT = 7876;

    private long olderTimer = 0;
    private long newTimer = 0;
    private static Timer timer;
    private static TimerTask task;
    private DatagramSocket serverSocket;
    private DatagramPacket datagramPacket;
    private Thread  thread;
    public static boolean isFirst = true;

    public CheckUDPService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            if (serverSocket == null) {
                serverSocket = new DatagramSocket(SOCKET_PORT);
                byte[] byts = new byte[1024];
                datagramPacket = new DatagramPacket(byts, byts.length);
                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        initMyServer();
                    }
                });
                thread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e("------------>onCreate  Exception");
            LeautoCameraAppLication.isApConnectCamera = false;
            isFirst = true;
            EventBus.getDefault().post(new UDPTimeOutEvent());
            e.printStackTrace();
        }


        //EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        Logger.i(" ===========>   onDestroy");
        //EventBus.getDefault().unregister(this);
        LeautoCameraAppLication.CheckUDPServiceIsOk = false;
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.i(" ===========>   onStartCommand");

        if (thread!=null&&!thread.isAlive()){
            thread = null;
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    initMyServer();
                }
            });
            thread.start();
        }
        LeautoCameraAppLication.CheckUDPServiceIsOk = true;
        return START_STICKY;
    }


    public void initMyServer() {
        try {
            Logger.e("start---------");
            //创建一个ServerSocket，用于监听客户端Socket的连接请求
            while (true) {
                Logger.i("initMyServer   :", " ===========>   接收到UDP包之前");
                //每当接收到客户端的Socket请求，服务器端也相应的创建一个Socket
                serverSocket.receive(datagramPacket);
                if (datagramPacket != null) {
                    Logger.i("===========>   接收到UDP包之后:isFirst="+isFirst);
                    if (isFirst) {
                        isFirst = false;
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
                                rtspUrl = "rtsp://" + Config.CAMERA_IP + "/live";
                                LeautoCameraAppLication.isApConnectCamera = true;
//                                RemoteCamHelper.getRemoteCam().checkSdCardIsPresent();
//                                RemoteCamHelper.getRemoteCam().SyncTime();
                                EventBus.getDefault().post(new ConnectSuccessEvent(rtspUrl));
                                try {
                                    serverSocket.setSoTimeout(3000);
                                } catch (SocketException e) {
                                    e.printStackTrace();
                                }
                                Logger.e("'sta' command execute success cancel timeout check timer!");
                                if(timer != null){
                                    timer.cancel();
                                }
                            }

                            @Override
                            public void onReceiveNotification(JSONObject jsonObject) {

                            }
                        });
                    }
                }
            }

        } catch (Exception e) {
            Logger.e("------------>initMyServer  Exception");
            serverSocket.close();
            serverSocket = null;
            LeautoCameraAppLication.isApConnectCamera = false;
            isFirst = true;
            EventBus.getDefault().post(new UDPTimeOutEvent());
            e.printStackTrace();

        }
    }

    public static void chekUdpTimeOut() {
        Logger.e("chekUdpTimeOut task isNull=" + (task == null));
        if (task != null) {
            Logger.e("Cancel pre timeout check timer!");
            timer = new Timer();
            timer.cancel();
        }
        task = new TimerTask() {
            @Override
            public void run() {
                Logger.e("Receive udp package 40s time out! isFirst=" + isFirst);
                if (isFirst) {
                    isFirst = true;
                    EventBus.getDefault().post(new UDPTimeOutEvent());
                }
            }
        };
        timer = new Timer();
        timer.schedule(task, 40000);
    }

//    public void onEventMainThread(ConnectToCameraEvent event) {
//        boolean isConnectCamera = event.isConnectCamera();
//        Logger.e("------------>ConnectToCameraEvent  isApConnectCamera=" + LeautoCameraAppLication.isApConnectCamera + ", isConnectCamera=" + isConnectCamera);
//        if (!LeautoCameraAppLication.isApConnectCamera && event.isConnectCamera()) {
//            chekUdpTimeOut();
//        }
//    }


//    class CheckUdpTask extends MyTimerCheck {
//
//        @Override
//        public void doTimerCheckWork() {
//
//        }
//
//        @Override
//        public void doTimeOutWork() {
//
//        }
//    }

//    class PingRunnable implements  Runnable{
//
//        @Override
//        public void run() {
//            while (true) {
//                String s = "";
//                s = PingUtils.Ping(Config.CAMERA_IP);
//                Log.i("ping", s);
//                if (s.equals("faild")) {
//                    EventBusHelper.postChannelError(ChannelErrorEventType.CMD_CHANNEL_ERROR_BROKEN_CHANNEL);
////                    return;
//                }
//            }
//        }
//    }

}
