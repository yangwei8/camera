package com.letv.leauto.cameracmdlibrary.connect.Bluetooth;


import com.letv.leauto.cameracmdlibrary.common.Config;
import com.letv.leauto.cameracmdlibrary.common.Constant;
import com.letv.leauto.cameracmdlibrary.connect.Bluetooth.socket.BluetoothSocketConnect;
import com.letv.leauto.cameracmdlibrary.connect.event.CheckSdStatusFinished;
import com.letv.leauto.cameracmdlibrary.connect.event.ConnectToCameraEvent;
import com.letv.leauto.cameracmdlibrary.connect.event.EventBusHelper;
import com.letv.leauto.cameracmdlibrary.connect.model.CameraMessage;
import com.letv.leauto.cameracmdlibrary.connect.model.CameraMessageCallback;
import com.letv.leauto.cameracmdlibrary.connect.model.CommandID;
import com.letv.leauto.cameracmdlibrary.utils.JsonUtils;
import com.letv.leauto.cameracmdlibrary.utils.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.greenrobot.event.EventBus;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

public class BTRemoteCamHelper {
    public static final int HANDLER_BT_SEND_CMD = 7;
    private static final String TAG = "BTRemoteCamHelper";
    private static BTRemoteCamHelper remoteCamHelper;
    private static BluetoothDevice mDevice;

    private BluetoothSocketConnect socketConnect;

    private static final ExecutorService worker =
            Executors.newSingleThreadExecutor();

    public static BTRemoteCamHelper getRemoteCam(BluetoothDevice device) {
        if (remoteCamHelper == null) {
            mDevice = device;
            remoteCamHelper = new BTRemoteCamHelper();
        }
        return remoteCamHelper;
    }

    private BTRemoteCamHelper() {
        if (socketConnect == null) {
            socketConnect = new BluetoothSocketConnect();
            setBluetoothDevice(mDevice);
        }
    }

    public BTRemoteCamHelper setBluetoothDevice(BluetoothDevice device) {
        socketConnect.setBluetoothDevice(device);
        return this;
    }

    public void sendCommand(final CameraMessage cameraMessage) {
        worker.execute(new Runnable() {
            public void run() {
                //检测channel是否连接
                if (!connectToCmdChannel())
                    return;
                socketConnect.sendCommand(cameraMessage);
            }
        });
    }

    public void startSession(final Handler handler) {
        worker.execute(new Runnable() {
            public void run() {
                //检测channel是否连接
                if (!connectToCmdChannel())
                    return;
                socketConnect.sendCommand(new CameraMessage(CommandID.AMBA_START_SESSION, new CameraMessageCallback() {
                    @Override
                    public void onReceiveErrorMessage(CameraMessage cameraMessage, JSONObject jsonObject) {
                        Log.e(TAG,"onReceiveErrorMessage----->"+jsonObject.toString());
                    }

                    @Override
                    public void onReceiveMessage(CameraMessage cameraMessage, JSONObject jsonObject) {
                        if(handler!=null)
                            handler.sendEmptyMessage(HANDLER_BT_SEND_CMD);
                    }

                    @Override
                    public void onReceiveNotification(JSONObject jsonObject) {
                        Log.e(TAG,"onReceiveNotification----->"+jsonObject.toString());
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

    public void setClntInfo() {
        socketConnect.setClntInfo("TCP");
    }

    /**
     * 检查是否链接上了CMD
     *
     * @return
     */
    private boolean connectToCmdBT() {
        // 检查是否已经连接
        if (socketConnect.isConnected())
            return true;
        // 检查我们能否连接到BT
        if (socketConnect.connect()) {
            return true;
        }
        return false;
    }


    public boolean isConnectNow() {
        return socketConnect.isConnected();
    }

    public void closeChannel() {
        socketConnect.close();
    }

    private boolean connectToCmdChannel() {
        return connectToCmdBT();
    }

}
