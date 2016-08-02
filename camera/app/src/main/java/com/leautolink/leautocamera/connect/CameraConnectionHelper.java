package com.leautolink.leautocamera.connect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;

import com.leautolink.leautocamera.R;
import com.leautolink.leautocamera.application.LeautoCameraAppLication;
import com.leautolink.leautocamera.config.Constant;
import com.leautolink.leautocamera.event.ConnectTimeOutEvent;
import com.leautolink.leautocamera.event.ConnectionAuthenticateEvent;
import com.leautolink.leautocamera.module.WeakHandler;
import com.leautolink.leautocamera.utils.Logger;
import com.leautolink.leautocamera.utils.SpUtils;
import com.leautolink.leautocamera.utils.WifiAdminV2;
import com.letv.leauto.cameracmdlibrary.connect.RemoteCamHelper;
import com.letv.leauto.cameracmdlibrary.connect.event.ConnectToCameraEvent;
import com.letv.leauto.cameracmdlibrary.connect.event.EventBusHelper;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by lixinlei on 16/7/21.
 */
public class CameraConnectionHelper {

    private static final int CONNECTION_AUTHENTICATE_FAILED = 4101;
    private static final int CONNECTION_FAILED = 4100;
    private static final int CONNECTION_SUCCESS = 4098;
    private static final int CONNECTION_TIME_OUT = 4099;
    private static final int INITIAL_CAPACITY = 15;
    private static final int INIT_COMPLETE = 8199;

    private static final long MAX_CONECTION_TIME = 20000L;
    private static final long MAX_SCAN_CAMERA_TIME = 10000L;
    public static final int RECONNECT_TIMES = 2;
    private static final int SCAN_CAMERA_TIME_OUT = 1;
    private static final int SCAN_INTERVAL_TIME = 1200;
    private static final int SESSION_START_FAILED = 8197;
    private static final int SESSION_START_SUCCESS = 8198;
    private static final int START_SCAN_CAMERA_RESULT = 12290;
    private long TimeOutSpendTime;
    private long connectionStartTime;
    private boolean isBack;
    private boolean isConnectStart;
    private boolean isSearchTimeout;
    private boolean isStartSession;
    private ArrayList<ScanResult> mCameraList;
    private WifiConfiguration mCameraWifiConfig;
    private Context mContext;
    private WifiAdminV2 mWifiAdminV2;
    private int reconnectionCount;
    private int retryStartSession;
    private Runnable searchCameraTask;
    private long searchStartTime;
    private CameraConnectionHandler mHandler;
    private WifiStateReceiver receiver;

    private String ssid;
    private String pwd;

    public CameraConnectionHelper(final Context mContext) {
        this.reconnectionCount = 2;


        this.mContext = mContext;
        mWifiAdminV2 = new WifiAdminV2(mContext);
        this.mHandler = new CameraConnectionHandler(this);
        this.receiver = new WifiStateReceiver();
        final IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        intentFilter.addAction("android.net.wifi.supplicant.STATE_CHANGE");
        intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        intentFilter.setPriority(Integer.MAX_VALUE);
        mContext.registerReceiver(receiver, intentFilter);
    }

    static synchronized void setIsConnectStart(final CameraConnectionHelper cameraConnectionHelper, final boolean isConnectStart) {
        cameraConnectionHelper.isConnectStart = isConnectStart;
    }

    public void startConnection(String ssid, String pwd) {

        this.ssid = ssid;
        this.pwd = pwd;
        this.connectionStartTime = SystemClock.elapsedRealtime();
        if (this.mHandler != null) {
            Logger.i("debug_wifi", "set CONNECTION_TIME_OUT, retry times: " + this.reconnectionCount);
            this.mHandler.sendEmptyMessageDelayed(CONNECTION_TIME_OUT, MAX_CONECTION_TIME);
        }
        this.isConnectStart = true;

        if (this.mContext != null && !TextUtils.isEmpty(ssid) && !TextUtils.isEmpty(pwd)) {
            final WifiInfo connectionInfo = ((WifiManager) this.mContext.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo();
            final NetworkInfo networkInfo = ((ConnectivityManager) this.mContext.getSystemService(Context.CONNECTIVITY_SERVICE)).getNetworkInfo(1);
            if (networkInfo != null && connectionInfo != null && !TextUtils.isEmpty((CharSequence) connectionInfo.getSSID()) && networkInfo.isConnected() && !TextUtils.isEmpty(ssid) && ssid.equalsIgnoreCase(connectionInfo.getSSID().replace("\"", ""))) {
                Logger.i("debug_wifi", "wifi " + ssid + " alread connect, start session directly");
                this.startSession();
            } else {
                this.mCameraWifiConfig = null;
                if (this.mWifiAdminV2 != null) {
                    this.mCameraWifiConfig = this.mWifiAdminV2.getWifiConfigure(ssid);
                    if (this.mCameraWifiConfig != null && !this.mWifiAdminV2.isAuthenticateFailed(this.mCameraWifiConfig)) {
                        Logger.i("debug_wifi", "wifi " + ssid + " alread saved in the system config, just enable");
                        this.mWifiAdminV2.enableNetwork(this.mCameraWifiConfig);
                        return;
                    }
                    this.mWifiAdminV2.forget(ssid);
                    Logger.i("debug_wifi", "will connect: " + ssid + " | " + pwd);
                    this.mCameraWifiConfig = this.mWifiAdminV2.CreateWifiInfo(ssid, pwd, 3);
                    if (!this.mWifiAdminV2.addAndEnableNetwork(this.mCameraWifiConfig)) {
                        Message.obtain(this.mHandler, CONNECTION_FAILED).sendToTarget();
                    }
                }
            }
        }
    }

    private void startSession() {
        if (!this.isStartSession) {
            this.isStartSession = true;
            RemoteCamHelper.getRemoteCam().startSession();
        }
    }

    private boolean isCameraConnected() {
        final WifiInfo connectionInfo = ((WifiManager) this.mContext.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo();
        boolean b = false;
        if (connectionInfo != null) {
            b = false;
            if (isSsidAndPwdEmpty()) {
                final String bssid = connectionInfo.getBSSID();
                final String mSsid = connectionInfo.getSSID();
                final boolean empty = TextUtils.isEmpty(bssid);
                b = false;
                if (!empty) {
                    final boolean empty2 = TextUtils.isEmpty(mSsid);
                    b = false;
                    if (!empty2) {

                        final boolean empty4 = TextUtils.isEmpty(ssid);
                        b = false;
                        if (!empty4) {
                            final boolean equalsIgnoreCase2 = mSsid.replace("\"", "").equalsIgnoreCase(ssid);
                            b = false;
                            if (equalsIgnoreCase2) {
                                b = true;
                                Constant.WIFI_NAME = ssid;
                                LeautoCameraAppLication.isConnectCamera = true;
                                SpUtils.getInstance(mContext).setValue(Constant.WIFI_SSID, Constant.WIFI_NAME);
                            }
                        }
                    }
                }
            }
        }
        return b;
    }
    private void disconnecedCamera() {
        if (LeautoCameraAppLication.isConnectCamera) {
            Constant.WIFI_NAME = "";
            LeautoCameraAppLication.isConnectCamera = false;
            RemoteCamHelper.getRemoteCam().closeChannel();
            EventBusHelper.postConnectToCamera(new ConnectToCameraEvent(false));
        }

    }
    public void reset() {
        this.isStartSession = false;
    }

    public void close() {
        Logger.i("debug_wifi","  －－－－－－－－－wifi连接close－－－－－－－－  ");
        this.isBack = true;
        this.isStartSession = false;
        if (this.receiver != null && this.mContext != null) {
            this.mContext.unregisterReceiver((BroadcastReceiver) this.receiver);
        }
        this.stopConnectCamera();
//        this.listener = null;
        this.receiver = null;
    }
    private void connectionTimeOut() {
        if (this.reconnectionCount == 0 || this.isStartSession) {
            this.TimeOutSpendTime = 0L;
//            if (this.listener != null) {
//                this.listener.connectionTimeOut();
//            }
            EventBus.getDefault().post(new ConnectTimeOutEvent());
            return;
        }
        this.TimeOutSpendTime += SystemClock.elapsedRealtime() - this.connectionStartTime;
        Logger.i("debug_wifi", "receive CONNECTION_TIME_OUT, rest retry times: " + this.reconnectionCount + " spend time: " + (SystemClock.elapsedRealtime() - this.connectionStartTime));
        --this.reconnectionCount;
        this.isConnectStart = false;
        this.startConnection(ssid,pwd);
    }
    private void connectionFaild(final boolean b, final int s) {
//        if (this.listener != null) {
//            this.listener.connectionFaild(b, s);
//        }
    }
    private void stopConnectCamera() {
        Logger.i("debug_wifi", "Stop CONNECTION_TIME_OUT, rest retry times: " + this.reconnectionCount);
        if (this.mHandler != null) {
            this.reconnectionCount = 2;
            this.mHandler.removeMessages(CONNECTION_TIME_OUT);
        }
    }
    private void connectionAuthenticateFailed() {
        if (isSsidAndPwdEmpty()) {
            this.mWifiAdminV2.forget(ssid);
        }
        stopConnectCamera();
        EventBus.getDefault().post(new ConnectionAuthenticateEvent());
//        if (this.listener != null) {
//            this.listener.connectionAuthenticateFailed(this.mCamera);
//        }
    }

    private boolean isSsidAndPwdEmpty() {
        return !TextUtils.isEmpty(ssid)&&!TextUtils.isEmpty(pwd);
    }

    private class CameraConnectionHandler extends WeakHandler<CameraConnectionHelper> {
        public CameraConnectionHandler(final CameraConnectionHelper cameraConnectionHelper) {
            super(cameraConnectionHelper);
        }

        public void handleMessage(final Message message) {
            super.handleMessage(message);
            final CameraConnectionHelper cameraConnectionHelper = this.getOwner();
            if (cameraConnectionHelper == null || cameraConnectionHelper.isBack) {
                return;
            }
            switch (message.what) {
                default: {
                }
                case CONNECTION_TIME_OUT: {
                    Logger.i("debug_wifi", "CONNECTION_TIME_OUT");
                    cameraConnectionHelper.connectionTimeOut();
                    break;
                }
                case CONNECTION_FAILED: {
                    Logger.i("debug_wifi", "CONNECTION_FAILED");
                    CameraConnectionHelper.this.stopConnectCamera();
                    cameraConnectionHelper.connectionFaild(true, R.string.connect_fail);
                    break;
                }
                case CONNECTION_AUTHENTICATE_FAILED: {
                    Logger.i("debug_wifi", "CONNECTION_AUTHENTICATE_FAILED");
                    CameraConnectionHelper.this.stopConnectCamera();
                    cameraConnectionHelper.connectionAuthenticateFailed();
                    break;
                }
                case SESSION_START_FAILED: {//startSession失败
//                    AntsSportsCameraSocketConnection.getInstance().stop();
//                    cameraConnectionHelper.startSessionFailed(message.arg1);
                    break;
                }
                case SESSION_START_SUCCESS: {//startSession成功
//                    Logger.i("debug_wifi", "SESSION_START_SUCCESS");
//                    cameraConnectionHelper.startSessionSuccess();
                    break;
                }
                case CONNECTION_SUCCESS: {
                    Logger.i("debug_wifi", "CONNECTION_SUCCESS");
                    CameraConnectionHelper.this.stopConnectCamera();
                    cameraConnectionHelper.startSession();
                    break;
                }
                case 3: {
//                    Logger.i("debug_wifi", "AMBA_GET_ALL_CURRENT_SETTINGS");
//                    cameraConnectionHelper.initCameraSetting((JSONObject) message.obj);
                    break;
                }
                case 2: {
//                    Logger.i("debug_wifi", "AMBA_SET_SETTING");
//                    cameraConnectionHelper.startVF();
                    break;
                }
                case INIT_COMPLETE: {
//                    Logger.i("debug_wifi", "INIT_COMPLETE");
//                    cameraConnectionHelper.initComplete();
                    break;
                }
                case START_SCAN_CAMERA_RESULT: {
//                    CameraConnectionHelper.this.startScanCamera();
                    break;
                }
            }
        }
    }

    private class WifiStateReceiver extends BroadcastReceiver {
        public void onReceive(final Context context, final Intent intent) {

            Logger.i("debug_wifi", "wifi receive -->| : " + intent.getAction());

            if (intent.getAction().equals("android.net.wifi.WIFI_STATE_CHANGED")) {
                Logger.i("debug_wifi", "wifi stat: " + intent.getIntExtra("wifi_state", 4));
            } else if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
                if (((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getNetworkInfo(1).isConnected()) {
                    if (CameraConnectionHelper.this.isCameraConnected()) {
                        Message.obtain(mHandler, CONNECTION_SUCCESS).sendToTarget();
                        CameraConnectionHelper.setIsConnectStart(CameraConnectionHelper.this, false);
                        return;
                    }
                    if (CameraConnectionHelper.this.isConnectStart) {
                        CameraConnectionHelper.this.startConnection(ssid, pwd);
                        return;
                    }
                }
            } else if (intent.getAction().equals("android.net.wifi.supplicant.STATE_CHANGE")) {
                final WifiInfo connectionInfo = ((WifiManager) CameraConnectionHelper.this.mContext.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo();
                final SupplicantState supplicantState = connectionInfo.getSupplicantState();
                boolean b = false;
                String s;
                if (supplicantState == SupplicantState.ASSOCIATED) {
                    s = "\u5173\u8054AP\u5b8c\u6210";
                } else if (supplicantState.toString().equals("AUTHENTICATING")) {
                    s = "\u6b63\u5728\u9a8c\u8bc1";
                    b = false;
                } else if (supplicantState == SupplicantState.ASSOCIATING) {
                    s = "\u6b63\u5728\u5173\u8054AP...";
                    b = false;
                } else if (supplicantState == SupplicantState.COMPLETED) {
                    s = "\u5df2\u8fde\u63a5: " + connectionInfo.getSSID();
                    b = false;
                } else if (supplicantState == SupplicantState.DISCONNECTED) {
                    s = "\u5df2\u65ad\u5f00";
                    b = true;
                    CameraConnectionHelper.this.disconnecedCamera();
                } else if (supplicantState == SupplicantState.DORMANT) {
                    s = "\u6682\u505c\u6d3b\u52a8";
                    b = false;
                } else if (supplicantState == SupplicantState.FOUR_WAY_HANDSHAKE) {
                    s = "\u56db\u8def\u63e1\u624b\u4e2d...";
                    b = false;
                } else if (supplicantState == SupplicantState.GROUP_HANDSHAKE) {
                    s = "GROUP_HANDSHAKE";
                    b = false;
                } else if (supplicantState == SupplicantState.INACTIVE) {
                    s = "\u4f11\u7720\u4e2d...";
                    b = false;
                } else if (supplicantState == SupplicantState.INVALID) {
                    s = "\u65e0\u6548";
                    b = false;
                } else if (supplicantState == SupplicantState.SCANNING) {
                    s = "\u626b\u63cf\u4e2d...";
                    b = false;
                    CameraConnectionHelper.this.disconnecedCamera();
                } else if (supplicantState == SupplicantState.UNINITIALIZED) {
                    s = "\u672a\u521d\u59cb\u5316";
                    b = false;
                } else {
                    s = String.valueOf(supplicantState);
                    b = false;
                }
                final int intExtra = intent.getIntExtra("supplicantError", -1);
                Logger.i("debug_wifi", "status: " + s + "      intExtra:  "+intExtra);
                if (intExtra == 1) {
                    final String ssid = CameraConnectionHelper.this.ssid;
                    WifiConfiguration wifiConfigure = null;
                    if (!TextUtils.isEmpty(ssid)) {
                        wifiConfigure = CameraConnectionHelper.this.mWifiAdminV2.getWifiConfigure(ssid);
                    }
                    Logger.i("debug_wifi", "Received ERROR_AUTHENTICATING, isAuthenticateFailed: " + CameraConnectionHelper.this.mWifiAdminV2.isAuthenticateFailed(wifiConfigure));
                }
                if (b && intExtra == 1) {
                    CameraConnectionHelper.this.isConnectStart = false;
                    CameraConnectionHelper.this.stopConnectCamera();
                    Message.obtain(CameraConnectionHelper.this.mHandler, CONNECTION_AUTHENTICATE_FAILED).sendToTarget();
                    Logger.i("debug_wifi", "wifi password error.");
                }
            }
        }
    }



}
