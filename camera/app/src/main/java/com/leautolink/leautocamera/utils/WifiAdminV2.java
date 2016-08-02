// 
// Decompiled by Procyon v0.5.30
// 

package com.leautolink.leautocamera.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.leautolink.leautocamera.R;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class WifiAdminV2
{
    private static final String TAG = "WifiAdminV2";
    public static final int WIFI_CONNECTED = 1;
    public static final int WIFI_CONNECTING = 3;
    public static final int WIFI_CONNECT_FAILED = 2;
    private static Context mContext;
    private WifiManager.WifiLock mWifiLock;
    private WifiManager mWifiManager;
    
    public WifiAdminV2(final Context mContext) {
        this.mContext = null;
        this.mContext = mContext;
        this.mWifiManager = (WifiManager)mContext.getSystemService(mContext.WIFI_SERVICE);
    }
    
    private void disableAndRemoveWifi(final String s) {
       Log.e("debug_wifi", "disableAndRemoveWifi: " + s);
        final WifiConfiguration wifiConfigure = this.getWifiConfigure(s);
        if (wifiConfigure != null) {
            this.disconnectWifi(wifiConfigure.networkId);
            this.removeWifi(s);
            this.saveConfiguration();
        }
    }
    
    private void disconnectWifi(final int n) {
       Log.e("debug_wifi", "disconnect network, result: " + (Object)this.mWifiManager.disconnect());
    }
    public static boolean isConnectCamera(WifiInfo wifiInfo) {
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
        return isConnectCamera(str.replace("\"", ""), wifiInfo.getBSSID());

    }

    public static boolean isConnectCamera(String ssid, String bssid) {
        return  isCameraDevice(ssid,bssid);
    }
    public static boolean isCameraDevice(final ScanResult scanResult) {
        return scanResult != null && isCameraDevice(scanResult.SSID, scanResult.BSSID);
    }
    
    public static boolean isCameraDevice(final WifiInfo wifiInfo) {
        if (wifiInfo != null) {
            final String ssid = wifiInfo.getSSID();
            if (ssid != null) {
                return isCameraDevice(ssid.replace("\"", ""), wifiInfo.getBSSID());
            }
        }
        return false;
    }
    
    private static boolean isCameraDevice(final String s, final String s2) {
        return s != null && s2 != null && (s.startsWith(mContext.getResources().getString(R.string.wifi_name)));
    }
    
    private boolean reconnect() {
        final boolean reconnect = mWifiManager.reconnect();
        Log.e("debug_wifi", "reconnect: " + reconnect);
        return reconnect;
    }
    
    private void removeWifi(final String s) {
        final WifiConfiguration wifiConfigure = this.getWifiConfigure(s);
        if (wifiConfigure != null) {
           Log.e("debug_wifi", "removeWifi ssid " + s + " result: " + (Object)this.mWifiManager.removeNetwork(wifiConfigure.networkId));
            this.saveConfiguration();
        }
    }
    
    public WifiConfiguration CreateWifiInfo(final String s, final String s2, final int n) {
        final WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.allowedAuthAlgorithms.clear();
        wifiConfiguration.allowedGroupCiphers.clear();
        wifiConfiguration.allowedKeyManagement.clear();
        wifiConfiguration.allowedPairwiseCiphers.clear();
        wifiConfiguration.allowedProtocols.clear();
        wifiConfiguration.SSID = "\"" + s + "\"";
        if (n == 1) {
            wifiConfiguration.wepKeys[0] = "";
            wifiConfiguration.allowedKeyManagement.set(0);
            wifiConfiguration.wepTxKeyIndex = 0;
        }
        else {
            if (n == 2) {
                wifiConfiguration.hiddenSSID = true;
                wifiConfiguration.wepKeys[0] = "\"" + s2 + "\"";
                wifiConfiguration.allowedAuthAlgorithms.set(1);
                wifiConfiguration.allowedGroupCiphers.set(3);
                wifiConfiguration.allowedGroupCiphers.set(2);
                wifiConfiguration.allowedGroupCiphers.set(0);
                wifiConfiguration.allowedGroupCiphers.set(1);
                wifiConfiguration.allowedKeyManagement.set(0);
                wifiConfiguration.wepTxKeyIndex = 0;
                return wifiConfiguration;
            }
            if (n == 3) {
                wifiConfiguration.preSharedKey = "\"" + s2 + "\"";
                wifiConfiguration.hiddenSSID = true;
                wifiConfiguration.allowedAuthAlgorithms.set(0);
                wifiConfiguration.allowedGroupCiphers.set(2);
                wifiConfiguration.allowedKeyManagement.set(1);
                wifiConfiguration.allowedPairwiseCiphers.set(1);
                wifiConfiguration.allowedGroupCiphers.set(3);
                wifiConfiguration.allowedPairwiseCiphers.set(2);
                wifiConfiguration.status = 2;
                return wifiConfiguration;
            }
        }
        return wifiConfiguration;
    }
    
    public void acquireWifiLock() {
        this.mWifiLock.acquire();
    }
    
    public boolean addAndEnableNetwork(final WifiConfiguration wifiConfiguration) {
        Log.e("debug_wifi", "addAndEnableNetwork " + wifiConfiguration.SSID);
        try {
            final int addNetwork = this.mWifiManager.addNetwork(wifiConfiguration);
            if (addNetwork >= 0) {
                this.mWifiManager.enableNetwork(addNetwork, true);
                this.saveConfiguration();
                return this.reconnect();
            }
           Log.e("debug_wifi", "WifiManager add network " + wifiConfiguration.SSID + " failed");
            return false;
        }
        catch (IllegalArgumentException ex) {
            ex.printStackTrace();
           Log.e("debug_wifi", "WifiManager add network " + wifiConfiguration.SSID + " IllegalArgumentException");
            return false;
        }
    }
    
    public boolean addAndEnableNetwork(final String s) {
        return this.addAndEnableNetwork(this.createWifiInfo(s));
    }
    
    public int checkState() {
        return this.mWifiManager.getWifiState();
    }
    
    public void closeWifi() {
       Log.e("debug_wifi", "close wifi");
        if (this.mWifiManager.isWifiEnabled()) {
            this.mWifiManager.setWifiEnabled(false);
        }
    }
//
//    public void connectConfiguration(final int n) {
//        final List configuredNetworks = this.mWifiManager.getConfiguredNetworks();
//        if (configuredNetworks == null || n > configuredNetworks.size()) {
//            return;
//        }
//        this.mWifiManager.enableNetwork(configuredNetworks.get(n).networkId, true);
//    }
    
//    public void conntectToEnableWifi() {
//        this.startScan();
//        ScanResult scanResult = null;
//        final List configuredNetworks = this.mWifiManager.getConfiguredNetworks();
//        final List scanResults = this.mWifiManager.getScanResults();
//        if (configuredNetworks != null && scanResults != null) {
//            final HashMap<Object, WifiConfiguration> hashMap = new HashMap<Object, WifiConfiguration>();
//            for (final WifiConfiguration wifiConfiguration : configuredNetworks) {
//                if (wifiConfiguration.SSID != null) {
//                    hashMap.put(wifiConfiguration.SSID.substring(1, -1 + wifiConfiguration.SSID.length()), wifiConfiguration);
//                }
//            }
//            for (final ScanResult scanResult2 : scanResults) {
//                if (!isCameraDevice(scanResult2) && hashMap.containsKey(scanResult2.SSID)) {
//                    if (scanResult == null) {
//                        scanResult = scanResult2;
//                    }
//                    else {
//                        if (scanResult2.level <= scanResult.level) {
//                            continue;
//                        }
//                        scanResult = scanResult2;
//                    }
//                }
//            }
//            if (scanResult != null) {
//                this.enableNetwork(hashMap.get(scanResult.SSID));
//            }
//        }
//    }
    
    public void creatWifiLock() {
        this.mWifiLock = this.mWifiManager.createWifiLock("Test");
    }
    
    public WifiConfiguration createWifiInfo(final String s) {
        final WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.status = 1;
        wifiConfiguration.priority = 40;
        wifiConfiguration.allowedKeyManagement.set(0);
        wifiConfiguration.allowedProtocols.set(1);
        wifiConfiguration.allowedProtocols.set(0);
        wifiConfiguration.allowedAuthAlgorithms.clear();
        wifiConfiguration.allowedPairwiseCiphers.set(2);
        wifiConfiguration.allowedPairwiseCiphers.set(1);
        wifiConfiguration.allowedGroupCiphers.set(0);
        wifiConfiguration.allowedGroupCiphers.set(1);
        wifiConfiguration.allowedGroupCiphers.set(3);
        wifiConfiguration.allowedGroupCiphers.set(2);
        wifiConfiguration.SSID = "\"" + s + "\"";
        return wifiConfiguration;
    }
    
    public void disconnect() {
        final WifiInfo connectionInfo = ((WifiManager)this.mContext.getSystemService(mContext.WIFI_SERVICE)).getConnectionInfo();
        if (connectionInfo != null) {
            this.disconnectWifi(connectionInfo.getNetworkId());
        }
    }
    
    public boolean enableNetwork(final WifiConfiguration wifiConfiguration) {
       Log.e("debug_wifi", "enableNetwork " + wifiConfiguration.SSID);
        this.mWifiManager.enableNetwork(wifiConfiguration.networkId, true);
        this.saveConfiguration();
        return this.reconnect();
    }
    
    public void forget(final String s) {
       Log.e("debug_wifi", "forget: " + s);
        final WifiConfiguration wifiConfigure = this.getWifiConfigure(s);
        if (wifiConfigure != null) {
            final Method[] declaredMethods = this.mWifiManager.getClass().getDeclaredMethods();
            Boolean b = false;
            for (final Method method : declaredMethods) {
                if (method.getName().equals("forget")) {
                    b = true;
                    try {
                        Logger.e("debug_wifi","forget wifi success");
                        method.invoke(this.mWifiManager, wifiConfigure.networkId, null);
                    }
                    catch (Exception ex) {
                        Logger.e("debug_wifi","forget wifi exception  -->| " + ex.toString());
                        b = false;
                    }
                    break;
                }
            }
            if (!b) {
                this.disableAndRemoveWifi(s);
            }
        }
    }
    
    public String getBSSID() {
        final WifiInfo connectionInfo = this.mWifiManager.getConnectionInfo();
        if (connectionInfo == null) {
            return "NULL";
        }
        return connectionInfo.getBSSID();
    }
    
    public List<WifiConfiguration> getConfiguration() {
        return (List<WifiConfiguration>)this.mWifiManager.getConfiguredNetworks();
    }
    
    public int getIPAddress() {
        final WifiInfo connectionInfo = this.mWifiManager.getConnectionInfo();
        if (connectionInfo == null) {
            return 0;
        }
        return connectionInfo.getIpAddress();
    }
    
    public String getMacAddress() {
        final WifiInfo connectionInfo = this.mWifiManager.getConnectionInfo();
        if (connectionInfo == null) {
            return "NULL";
        }
        return connectionInfo.getMacAddress();
    }
    
    public int getNetworkId() {
        final WifiInfo connectionInfo = this.mWifiManager.getConnectionInfo();
        if (connectionInfo == null) {
            return 0;
        }
        return connectionInfo.getNetworkId();
    }
    
    public String getSSID() {
        final WifiInfo connectionInfo = this.mWifiManager.getConnectionInfo();
        if (connectionInfo == null) {
            return "NULL";
        }
        return connectionInfo.getSSID().replace("\"", "");
    }
    
    public WifiConfiguration getWifiConfigure(final String s) {
        List<WifiConfiguration> configuredNetworks;
        while (true) {
            try {
                configuredNetworks = (List<WifiConfiguration>)this.mWifiManager.getConfiguredNetworks();
                if (configuredNetworks == null) {
                    return null;
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
                configuredNetworks = null;
                continue;
            }
            break;
        }
        for (final WifiConfiguration wifiConfiguration : configuredNetworks) {
            if (wifiConfiguration.SSID != null && wifiConfiguration.SSID.equals("\"" + s + "\"")) {
                return wifiConfiguration;
            }
        }
        return null;
    }
    
    public WifiInfo getWifiInfo() {
        return this.mWifiManager.getConnectionInfo();
    }
    
    public List<ScanResult> getWifiList() {
        return (List<ScanResult>)this.mWifiManager.getScanResults();
    }
    
    public boolean isAuthenticateFailed(final WifiConfiguration wifiConfiguration) {
        if (wifiConfiguration != null) {
            final Class<? extends WifiConfiguration> class1 = wifiConfiguration.getClass();
            try {
                final Field declaredField = class1.getDeclaredField("disableReason");
                declaredField.setAccessible(true);
                Logger.e("debug_wifi","isAuthenticateFailed  --> | "+declaredField.getInt(wifiConfiguration));
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
    
    public int isWifiConnected(final Context context) {
        int n = 1;
        final NetworkInfo networkInfo = ((ConnectivityManager)context.getSystemService(context.CONNECTIVITY_SERVICE)).getNetworkInfo(n);
        Log.v("WifiAdminV2", "isConnectedOrConnecting = " + networkInfo.isConnectedOrConnecting());
        Log.d("WifiAdminV2", "wifiNetworkInfo.getDetailedState() = " + networkInfo.getDetailedState());
        if (networkInfo.getDetailedState() == NetworkInfo.DetailedState.OBTAINING_IPADDR || networkInfo.getDetailedState() == NetworkInfo.DetailedState.CONNECTING) {
            n = 3;
        }
        else if (networkInfo.getDetailedState() != NetworkInfo.DetailedState.CONNECTED) {
            Log.d("WifiAdminV2", "getDetailedState() == " + networkInfo.getDetailedState());
            return 2;
        }
        return n;
    }
    
    public boolean isWifiEnabled() {
        return this.mWifiManager.isWifiEnabled();
    }
    
    public StringBuilder lookUpScan() {
        final List scanResults = this.mWifiManager.getScanResults();
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < scanResults.size(); ++i) {
            sb.append("Index_" + new Integer(i + 1).toString() + ":");
            sb.append(scanResults.get(i).toString());
            sb.append("/n");
        }
        return sb;
    }
    
    public void openWifi() {
       Log.e("debug_wifi", "open wifi");
        try {
            if (!this.mWifiManager.isWifiEnabled()) {
                this.mWifiManager.setWifiEnabled(true);
            }
        }
        catch (ActivityNotFoundException ex) {}
    }
    
    public void releaseWifiLock() {
        if (this.mWifiLock.isHeld()) {
            this.mWifiLock.acquire();
        }
    }
    
    public void saveConfiguration() {
        this.mWifiManager.saveConfiguration();
    }
    
    public void startScan() {
       Log.e("debug_wifi", "start scan wifi");
        this.mWifiManager.startScan();
    }
    
    public void toggleMobileData(final Context context, final boolean b) {
        final ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(context.CONNECTIVITY_SERVICE);
        try {
            final Field declaredField = Class.forName(connectivityManager.getClass().getName()).getDeclaredField("mService");
            declaredField.setAccessible(true);
            final Object value = declaredField.get(connectivityManager);
            final Method declaredMethod = Class.forName(value.getClass().getName()).getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
            declaredMethod.setAccessible(true);
            declaredMethod.invoke(value, b);
        }
        catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        catch (NoSuchFieldException ex2) {
            ex2.printStackTrace();
        }
        catch (SecurityException ex3) {
            ex3.printStackTrace();
        }
        catch (NoSuchMethodException ex4) {
            ex4.printStackTrace();
        }
        catch (IllegalArgumentException ex5) {
            ex5.printStackTrace();
        }
        catch (IllegalAccessException ex6) {
            ex6.printStackTrace();
        }
        catch (InvocationTargetException ex7) {
            ex7.printStackTrace();
        }
    }
}
