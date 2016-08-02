//package com.leautolink.leautocamera.utils;
//
//import android.app.Activity;
//import android.net.wifi.ScanResult;
//import android.net.wifi.WifiManager;
//
//import com.leautolink.leautocamera.application.LeautoCameraAppLication;
//import com.leautolink.leautocamera.callback.ConnectWifiCallBack;
//import com.leautolink.leautocamera.config.Constant;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by lixinlei on 15/12/1.
// */
//
//public class ConnectHelper {
//
//    public static void connect2CameraWifi(final Activity activity, ConnectWifiCallBack callBack) {
//        boolean b = false;
//        callBack.startConnectWifi();
//        WifiAdmin admin = new WifiAdmin(activity);
//        admin.openWifi();
//        long startTimeOpenWifi = System.currentTimeMillis();
//        while (admin.checkState() != WifiManager.WIFI_STATE_ENABLED) {
//            try {
//                Thread.sleep(200);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            long endTime = System.currentTimeMillis();
//            if (endTime - startTimeOpenWifi > 10000) {
//                callBack.endConnectWifi(false);
//                return;
//            }
//        }
//        List<ScanResult> wifiList = null;
//        List<ScanResult> tempList = null;
////        SharedPreferences sp = activity.getSharedPreferences("wifiinfo", activity.MODE_PRIVATE);
////        String pwd = sp.getString("wifiPwd", "123456789");
//        String pwd = SpUtils.getInstance(activity.getApplicationContext()).getStringValue(Constant.WIFI_PWD);
//
//
//        int count = 0;
//        while (count < 50 && (wifiList == null || tempList == null || tempList.size() == 0)) {
//            count++;
//            admin.startScan();
//            try {
//                Thread.sleep(200);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            wifiList = admin.getWifiList();
//
//            tempList = new ArrayList<>();
//            for (int i = 0; i < wifiList.size(); i++) {
//                ScanResult tempWifi = wifiList.get(i);
//                if (WifiAdmin.isConnectCamera(tempWifi.SSID, tempWifi.BSSID)) {
//                    tempList.add(tempWifi);
//
//                    if (tempWifi.capabilities.contains("WPA2-PSK-CCMP")) {
//                        b = admin.addNetwork(admin.CreateWifiInfo(tempWifi.SSID, pwd, 3));
//                    } else {
//                        b = admin.addNetwork(admin.CreateWifiInfo(tempWifi.SSID, "", 1));
//                    }
//
//                    long startTime = System.currentTimeMillis();
//                    while (true) {
//                        if (LeautoCameraAppLication.isConnectCamera()) {
//                            b = true;
//                            break;
//                        }
//                        long endTime = System.currentTimeMillis();
//                        if (endTime - startTime > 8000) {
//                            b = false;
//                            break;
//                        }
//                    }
//                    if (b) {
//                        break;
//                    }
//                }
//            }
//
//            callBack.endConnectWifi(b);
//
//        }
//    }
//}
