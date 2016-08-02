package com.leautolink.leautocamera.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import com.leautolink.leautocamera.application.LeautoCameraAppLication;
import com.leautolink.leautocamera.callback.ConnectWifiCallBack;
import com.leautolink.leautocamera.config.Constant;
import com.leautolink.leautocamera.services.CheckUDPService;
import com.letv.leauto.cameracmdlibrary.common.Config;
import com.letv.leauto.cameracmdlibrary.connect.RemoteCamHelper;
import com.letv.leauto.cameracmdlibrary.connect.model.CameraMessage;
import com.letv.leauto.cameracmdlibrary.connect.model.CameraMessageCallback;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by lixinlei on 15/12/1.
 */

public class ConnectedHelper {

    public static void connect2CameraWifi(final Activity activity, ConnectWifiCallBack callBack) {
        callBack.startConnectWifi();
        SharedPreferences sp = activity.getSharedPreferences(Constant.WIFI_INFO, activity.MODE_PRIVATE);
        String ssid = sp.getString(Constant.WIFI_SSID, "");
        Logger.e("-----------------ssid=" + ssid);
        if (TextUtils.isEmpty(ssid)) {
            callBack.endConnectWifi(false);
            return;
        } else {
            WifiAdmin admin = new WifiAdmin(activity);
            admin.openWifi();
            long startTimeOpenWifi = System.currentTimeMillis();
            while (admin.checkState() != WifiManager.WIFI_STATE_ENABLED) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                long endTime = System.currentTimeMillis();
                if (endTime - startTimeOpenWifi > 10000) {
                    callBack.endConnectWifi(false);
                    return;
                }
            }
            List<ScanResult> wifiList = admin.getmWifiManager().getScanResults();
            if (!ssid.equals(admin.getSSID())) {
                WifiConfiguration configuration = admin.getWifiConfigure(ssid);
                if (configuration != null) {
                    if (admin.getSSID()!=null) {
                        if (admin.getWifiConfigure(admin.getSSID())!=null) {
                            admin.disconnectWifi(admin.getWifiConfigure(admin.getSSID()).networkId);
                        }
                    }
                    boolean b = admin.getmWifiManager().enableNetwork(configuration.networkId, true);
                    long startTime = System.currentTimeMillis();
                    while (true) {
                        if (LeautoCameraAppLication.isConnectCamera()) {
                            b = true;
                            break;
                        }
                        long endTime = System.currentTimeMillis();
                        if (endTime - startTime > 20000) {
                            b = false;
                            break;
                        }
                    }
                    callBack.endConnectWifi(b);
                } else {
                    callBack.endConnectWifi(false);
                }
            }
        }

    }

    /**
     * HOME或者BACK时退出app
     * @param activity
     */
    public static void exitApp(final Activity activity){
        if (activity == null) {
            Logger.e("activity is null can't exit app!");
            return;
        }
        final String ssid = SpUtils.getInstance(activity.getApplicationContext()).getStringValue(Constant.WIFI_SSID);
        final WifiAdminV2 adminV2 = new WifiAdminV2(activity.getApplicationContext());
        adminV2.forget(ssid);
        Config.CAMERA_IP = "192.168.42.1";
        RemoteCamHelper.getRemoteCam().setWifiIP(Config.CAMERA_IP, Config.CMD_PORT_NUM, Config.DATA_PORT_NUM);
        CheckUDPService.isFirst = true;
        activity.stopService(new Intent(activity, CheckUDPService.class));
        activity.finish();
    }

    /**
     * 发命令让记录仪切换为ap状态
     */
    private static void apCamera() {
        CameraMessage cameraMessage = new CameraMessage(1543, new CameraMessageCallback() {
            @Override
            public void onReceiveErrorMessage(CameraMessage cameraMessage, JSONObject jsonObject) {

            }

            @Override
            public void onReceiveMessage(CameraMessage cameraMessage, JSONObject jsonObject) {
                com.letv.leauto.cameracmdlibrary.common.Constant.token = 0;
                RemoteCamHelper.getRemoteCam().closeChannel();
            }

            @Override
            public void onReceiveNotification(JSONObject jsonObject) {

            }
        });
        cameraMessage.put("type", "ap");
        RemoteCamHelper.getRemoteCam().sendCommand(cameraMessage);
    }
}

