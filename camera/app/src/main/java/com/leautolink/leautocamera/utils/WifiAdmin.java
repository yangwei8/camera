package com.leautolink.leautocamera.utils;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import com.leautolink.leautocamera.R;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by lixinlei on 15/9/14.
 */
public class WifiAdmin {
    private static final String TAG = WifiApAdmin.class.getSimpleName();
    // 定义WifiManager对象
    private WifiManager mWifiManager;
    // 定义WifiInfo对象
    private WifiInfo mWifiInfo;
    // 扫描出的网络连接列表
    private List<ScanResult> mWifiList;
    // 网络连接列表
    private List<WifiConfiguration> mWifiConfiguration;
    // 定义一个WifiLock
    WifiManager.WifiLock mWifiLock;
    // 定义上下文对象
    private static Context mContext;

    // 构造器
    public WifiAdmin(Context context) {
        mContext = context;
        // 取得WifiManager对象
        mWifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        // 取得WifiInfo对象
        mWifiInfo = mWifiManager.getConnectionInfo();
    }

    // 打开WIFI
    public void openWifi() {
        //Modify by donghongwei 20160405 for LECAMERA-403 begin
        boolean isWifiEnabled = mWifiManager.isWifiEnabled();
        boolean isWifiApEnabled = WifiApAdmin.isWifiApEnabled(mWifiManager);
        Logger.e("openWifi isWifiEnabled=" + isWifiEnabled + ", isWifiApEnabled=" + isWifiApEnabled);
        if (!isWifiEnabled) {
            if (isWifiApEnabled) {
//                WifiApAdmin.closeWifiAp(mContext);
            }
            mWifiManager.setWifiEnabled(true);
        }
        //Modify by donghongwei 20160405 for LECAMERA-403 end
    }

    // 关闭WIFI
    public void closeWifi() {
        if (mWifiManager.isWifiEnabled()) {
            com.letv.leauto.cameracmdlibrary.utils.Logger.e("closeWifi isWifiEnabled");
            mWifiManager.setWifiEnabled(false);
        }
    }

    // 检查当前WIFI状态
    public int checkState() {
        return mWifiManager.getWifiState();
    }

    // 锁定WifiLock
    public void acquireWifiLock() {
        mWifiLock.acquire();
    }

    // 解锁WifiLock
    public void releaseWifiLock() {
        // 判断时候锁定
        if (mWifiLock.isHeld()) {
            mWifiLock.acquire();
        }
    }


    // 创建一个WifiLock
    public void creatWifiLock() {
        mWifiLock = mWifiManager.createWifiLock("LeAuto");
    }

    // 得到配置好的网络
    public List<WifiConfiguration> getConfiguration() {
        return mWifiConfiguration;
    }

    // 指定配置好的网络进行连接
    public void connectConfiguration(int index) {
        // 索引大于配置好的网络索引返回
        if (index > mWifiConfiguration.size()) {
            return;
        }
        // 连接配置好的指定ID的网络
        mWifiManager.enableNetwork(mWifiConfiguration.get(index).networkId,
                true);
    }

    public void startScan() {
        mWifiManager.startScan();
        // 得到扫描结果
        mWifiList = mWifiManager.getScanResults();
        // 得到配置好的网络连接
        mWifiConfiguration = mWifiManager.getConfiguredNetworks();
    }

    // 得到网络列表
    public List<ScanResult> getWifiList() {
        return mWifiList;
    }

    // 查看扫描结果
    public StringBuilder lookUpScan() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < mWifiList.size(); i++) {
            stringBuilder
                    .append("Index_" + new Integer(i + 1).toString() + ":");
            // 将ScanResult信息转换成一个字符串包
            // 其中把包括：BSSID、SSID、capabilities、frequency、level
            stringBuilder.append((mWifiList.get(i).SSID + "  " + mWifiList.get(i).BSSID).toString());
            stringBuilder.append("\n");
        }
        return stringBuilder;
    }

    //获取wifimanger
    public WifiManager getmWifiManager() {
        return mWifiManager;
    }

    // 得到MAC地址
    public String getMacAddress() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
    }

    // 得到接入点的SSID
    public String getSSID() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getSSID();
    }

    // 得到接入点的BSSID
    public String getBSSID() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
    }

    // 得到IP地址
    public int getIPAddress() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
    }

    // 得到连接的ID
    public int getNetworkId() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
    }

    // 得到WifiInfo的所有信息包
    public String getWifiInfo() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
    }

    // 添加一个网络并连接
    public boolean addNetwork(WifiConfiguration wcg) {
        int wcgID = mWifiManager.addNetwork(wcg);
        if (wcgID>=0) {
            mWifiManager.enableNetwork(wcgID, true);
            saveConfiguration();
            return reconnect();
        }
        return false;
    }

    private boolean reconnect() {
        final boolean reconnect = this.mWifiManager.reconnect();
        Logger.e( "reconnect: " + reconnect);
        return reconnect;
    }
    // 断开指定ID的网络
//    public void disconnectWifi(int netId) {
//        mWifiManager.disableNetwork(netId);
//        mWifiManager.disconnect();
//    }

    //然后是一个实际应用方法，只验证过没有密码的情况：
//    public WifiConfiguration CreateWifiInfo(String SSID, String Password, int Type) {
//        WifiConfiguration config = new WifiConfiguration();
//        config.allowedAuthAlgorithms.clear();
//        config.allowedGroupCiphers.clear();
//        config.allowedKeyManagement.clear();
//        config.allowedPairwiseCiphers.clear();
//        config.allowedProtocols.clear();
//        config.SSID = "\"" + SSID + "\"";
//
//        WifiConfiguration tempConfig = this.IsExsits(SSID);
//        if (tempConfig != null && !this.isAuthenticateFailed(tempConfig)) {
//            Logger.e("wifi " + SSID + " alread saved in the system config, just enable");
//            this.enableNetwork(tempConfig);
//            return null;
//        }
//        this.forget(SSID);
//        if (Type == 1) //WIFICIPHER_NOPASS
//        {
////            if(!TextUtils.isEmpty(Password)){
////                config.wepKeys[0] = "";
////                config.wepTxKeyIndex = 0;
////            }
//            config.hiddenSSID = true;
//            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//
//        }
//        if (Type == 2) //WIFICIPHER_WEP
//        {
//            config.wepKeys[0] = "\"" + Password + "\"";
//            config.hiddenSSID = true;
//            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
//            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
//            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
//            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
//            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
//            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//            config.wepTxKeyIndex = 0;
//        }
//        if (Type == 3) //WIFICIPHER_WPA
//        {
//            config.preSharedKey = "\"" + Password + "\"";
//            config.hiddenSSID = true;
//            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
//            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
//            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
//            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
////            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
//            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
//            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
//            config.status = WifiConfiguration.Status.ENABLED;
//
//        }
//        return config;
//    }
    public boolean enableNetwork(final WifiConfiguration wifiConfiguration) {
        Logger.e("debug_wifi", "enableNetwork " + wifiConfiguration.SSID);
        this.mWifiManager.enableNetwork(wifiConfiguration.networkId, true);
        this.saveConfiguration();
        return this.reconnect();
    }
    public boolean isAuthenticateFailed(final WifiConfiguration wifiConfiguration) {
        if (wifiConfiguration != null) {
            final Class<? extends WifiConfiguration> class1 = wifiConfiguration.getClass();
            try {
                final Field declaredField = class1.getDeclaredField("disableReason");
                declaredField.setAccessible(true);
                if (declaredField.getInt(wifiConfiguration) == 3) {
                    return true;
                }
            }
            catch (NoSuchFieldException ex) {
                ex.printStackTrace();
                return false;
            }
            catch (IllegalAccessException ex2) {
                ex2.printStackTrace();
                return false;
            }
            catch (IllegalArgumentException ex3) {
                ex3.printStackTrace();
                return false;
            }
        }
        return false;
    }
    public WifiConfiguration IsExsits(String SSID) {
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
        if (existingConfigs!=null) {
            for (WifiConfiguration existingConfig : existingConfigs) {
                if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                    return existingConfig;
                }
            }
        }
        return null;
    }


    public static boolean isConnectCamera(Context context,WifiInfo wifiInfo) {
        String str;
        if (wifiInfo == null){
            Logger.e("isConnectCamera wifiInfo is null!");
            return false;
        }

        str = wifiInfo.getSSID();
        Logger.e("isConnectCamera wifiInfo ssid=" + str);

        if (str == null){
            return false;
        }
        return isConnectCamera(context,str.replace("\"", ""), wifiInfo.getBSSID());

    }

    public static boolean isConnectCamera(Context context,String ssid, String bssid) {
        if (TextUtils.isEmpty(ssid))
            return false;
//        if (ssid.startsWith(Config.WIFI_SSID_PREFIX)&&bssid.toUpperCase(Locale.getDefault()).startsWith(Config.WIFI_BSSID_PREFIX)){
//            return true;
//        }
        if (ssid.contains(context.getResources().getString(R.string.wifi_name))) {
            return true;
        }
        return false;
    }

    public WifiConfiguration getWifiConfigure(String paramString) {
        List localList1;
        WifiConfiguration localWifiConfiguration;
        List localList2;
        localList2 = this.mWifiManager.getConfiguredNetworks();
        localList1 = localList2;
        if (localList1 != null) {
            for (int i = 0; i < localList1.size(); i++) {
                localWifiConfiguration = (WifiConfiguration) localList1.get(i);
                if (localWifiConfiguration.SSID != null && (localWifiConfiguration.SSID.equals("\"" + paramString + "\""))) {
                    return localWifiConfiguration;
                }
            }
        }
        return null;
    }

    private void disableAndRemoveWifi(String wifissid) {
        Logger.e("debug_wifi", "disableAndRemoveWifi: " + wifissid);
        WifiConfiguration localWifiConfiguration = getWifiConfigure(wifissid);
        if (localWifiConfiguration != null) {
            disconnectWifi(localWifiConfiguration.networkId);
            removeWifi(wifissid);
            saveConfiguration();
        }
    }

    public void disconnectWifi(int networkId) {
        Boolean localBoolean = Boolean.valueOf(this.mWifiManager.disconnect());
        Logger.e("debug_wifi", "disconnect network, result: " + localBoolean);
    }

    private void removeWifi(String paramString) {
        WifiConfiguration localWifiConfiguration = getWifiConfigure(paramString);
        if (localWifiConfiguration != null) {
            Boolean localBoolean = Boolean.valueOf(this.mWifiManager.removeNetwork(localWifiConfiguration.networkId));
            Logger.e("debug_wifi", "removeWifi ssid " + paramString + " result: " + localBoolean);
            saveConfiguration();
        }
    }

    public void saveConfiguration() {
        this.mWifiManager.saveConfiguration();
    }

    public void forget(String wifiSSid) {
        Method[] arrayOfMethod;
        boolean localBoolean = false;
        Logger.e("debug_wifi", "forget: " + wifiSSid);
        WifiConfiguration localWifiConfiguration = getWifiConfigure(wifiSSid);
        if (localWifiConfiguration != null) {
            arrayOfMethod = this.mWifiManager.getClass().getDeclaredMethods();
            int methodCount = arrayOfMethod.length;
            for (int j = 0; j < methodCount; j++) {
                Method localMethod = arrayOfMethod[j];
                if (localMethod.getName().equals("forget")) {
                    try {
                        Logger.e("debug_wifi", "forget: " + wifiSSid);
                        WifiManager localWifiManager = this.mWifiManager;
                        Object[] arrayOfObject = new Object[2];
                        arrayOfObject[0] = Integer.valueOf(localWifiConfiguration.networkId);
                        arrayOfObject[1] = null;
                        localMethod.invoke(localWifiManager, arrayOfObject);
                        localBoolean = true;

                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        localBoolean = false;
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                        localBoolean = false;
                    }
                }
            }
           if (!localBoolean){
              disableAndRemoveWifi(wifiSSid);
           }

        }
    }
}
//分为三种情况：1没有密码2用wep加密3用wpa加密
