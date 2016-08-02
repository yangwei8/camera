package com.leautolink.leautocamera.ui.activity;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.leautolink.leautocamera.R;
import com.leautolink.leautocamera.config.Constant;
import com.leautolink.leautocamera.ui.base.BaseActivity;
import com.leautolink.leautocamera.utils.SpUtils;
import com.letv.leauto.cameracmdlibrary.connect.Bluetooth.BTRemoteCamHelper;
import com.letv.leauto.cameracmdlibrary.connect.model.CameraMessage;
import com.letv.leauto.cameracmdlibrary.connect.model.CameraMessageCallback;
import com.letv.leauto.cameracmdlibrary.connect.model.CommandID;

import org.json.JSONObject;

import java.util.List;
import java.util.Set;

public class ResetPasswordActivity extends BaseActivity implements View.OnClickListener {

    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final String TAG = "ResetPasswordActivity";

    private static final String BT_START_NAME = "Le-DVR";
    private BluetoothDevice mDevice =null;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothManager mBluetoothManager;
    BTRemoteCamHelper mBTRemoteCamHelper;
    public static final int HANDLER_BT_ENABLE = 1;
    public static final int HANDLER_BT_SCAN = 2;
    public static final int HANDLER_BT_STOP_SCAN = 3;
    public static final int HANDLER_BT_NOT_FOUND_BT = 4;
    public static final int HANDLER_BT_PAIR = 5;
    public static final int HANDLER_BT_START_SESSION = 6;
    public static final int HANDLER_BT_SEND_CMD = 7;
    public static final int HANDLER_BT_SEND_CMD_TIMEOUT = 8;
    public static final int HANDLER_BT_SEND_RESET_PWD_SUCCESS = 9;
    public static final int HANDLER_BT_UN_PAIR = 10;
    public static final int HANDLER_BT_SCAN_TIMEOUT = 11;
    public static final int HANDLER_BT_DELAYED_TIME = 100;
    public static final int HANDLER_BT_SCAN_DELAYED_TIME = 1000;
    public static final int HANDLER_BT_ENABLE_DELAYED_TIME = 2000;
    public static final int HANDLER_BT_RESETPWD_TIMEOUT=30*1000;
    public static final int HANDLER_BT_CAN_NOT_SCAN_TIME = 20*1000;

    private TextView mTvNotice;
    private Button mBtnClose;
    private Button mBtnNo;
    private Button mBtnYes;

    private List<BluetoothDevice> devices;



    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            Log.e(TAG, "mHandler->" + msg.what);
            if(isFinishing()){
                return;
            }
            switch (msg.what) {
                case HANDLER_BT_ENABLE:
                    enable();
                    break;
                case HANDLER_BT_SCAN:
                    startScanning();
                    break;
                case HANDLER_BT_STOP_SCAN:
                    stopScanning();
                    break;
                case HANDLER_BT_PAIR:
                    startPairing();
                    break;
                case HANDLER_BT_START_SESSION:
                    mBTRemoteCamHelper = BTRemoteCamHelper.getRemoteCam(mDevice);
                    mBTRemoteCamHelper.startSession(mHandler);
                    break;
                case HANDLER_BT_SEND_CMD:
                    resetCameraWIFIPassword();
                    break;
                case HANDLER_BT_NOT_FOUND_BT:
                    hideLoading();
                    showToastSafe(getString(R.string.base_activity_not_scan_bt));
                    break;
                case HANDLER_BT_SEND_CMD_TIMEOUT:
                    resetPWDfailed();
                    break;
                case HANDLER_BT_SEND_RESET_PWD_SUCCESS:
                    resetPWDSuccess();
                    break;
                case HANDLER_BT_UN_PAIR:
                    unpairDevice();
                    break;
                case HANDLER_BT_SCAN_TIMEOUT:
                    showToastSafe(getString(R.string.base_activity_not_scan_bt));
                    break;
            }
        }
    };
    public void enable() {
        Log.i(TAG, "check bt enable");
        if(mBluetoothAdapter!=null){
            if (!mBluetoothAdapter.isEnabled()) {
                mHandler.removeMessages(HANDLER_BT_ENABLE);
                mHandler.sendEmptyMessageDelayed(HANDLER_BT_ENABLE,
                        HANDLER_BT_ENABLE_DELAYED_TIME);
                mBluetoothAdapter.enable();
            } else {
                mHandler.sendEmptyMessageDelayed(HANDLER_BT_SCAN,HANDLER_BT_SCAN_DELAYED_TIME);
            }
        }
    }

    private void startScanning() {
        if(mBluetoothAdapter!=null) {
            Log.i(TAG, "startScanning");
            if(mBluetoothAdapter.isDiscovering()){
                mBluetoothAdapter.cancelDiscovery();
                mHandler.sendEmptyMessageDelayed(HANDLER_BT_SCAN, HANDLER_BT_SCAN_DELAYED_TIME);
            }else {
                mBluetoothAdapter.startDiscovery();
                mHandler.sendEmptyMessageDelayed(HANDLER_BT_NOT_FOUND_BT,
                        HANDLER_BT_CAN_NOT_SCAN_TIME);
            }
        }
    }



    public void stopScanning() {
        if (mBluetoothAdapter.isDiscovering()) {
            Log.e(TAG, "stopScanning");
            mBluetoothAdapter.cancelDiscovery();
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public boolean startPairing() {
        int bondState = mDevice.getBondState();
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        Log.i(TAG,"pair->"+mDevice+"  state:"+mDevice.getBondState());
        if (bondState == BluetoothDevice.BOND_NONE) {
            if (!mDevice.createBond()) {
                return false;
            }
        }
        return true;
    }

    private void unpairDevice() {
        try {
            Set<BluetoothDevice> st =  mBluetoothAdapter.getBondedDevices();
            for(BluetoothDevice device: st){
                if(device != null && device.getName().startsWith(BT_START_NAME)) {
                    if (!device.equals(mDevice)) {
                        device.removeBond();
                    }
                }
            }
            if(mDevice!=null)
                mDevice.removeBond();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private final BroadcastReceiver BluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            String name = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);
            short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
            Log.i(TAG, "BluetoothReceiver=>"+action+"| device="+device+"| name="+name+"|rssi="+rssi);
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                if (TextUtils.isEmpty(name)) {
                    name=device.getName();
                    if (TextUtils.isEmpty(name)) {
                        return;
                    }
                }
                name=name.trim();
                if(mDevice==null && name.startsWith(BT_START_NAME)){
                    Log.i(TAG, "device.name->|" + device.getName() + "|=State->" + device.getBondState());
                    mHandler.removeMessages(HANDLER_BT_SCAN);
                    mHandler.sendEmptyMessage(HANDLER_BT_STOP_SCAN);
                    mHandler.removeMessages(HANDLER_BT_NOT_FOUND_BT);
                    mDevice=device;
                    if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                        mHandler.sendEmptyMessageDelayed(HANDLER_BT_START_SESSION,
                            HANDLER_BT_DELAYED_TIME);
                    }else {
                        mHandler.sendEmptyMessageDelayed(HANDLER_BT_PAIR, HANDLER_BT_DELAYED_TIME);
                    }
                }
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                int bondState = intent
                        .getIntExtra(BluetoothDevice.EXTRA_BOND_STATE,
                                BluetoothDevice.ERROR);
                if((device!=null)&&(device.equals(mDevice))){
                    if (bondState == BluetoothDevice.BOND_BONDED) {
                        Log.i(TAG, mDevice.getName() + "<->" + mDevice + "-bondState-" + bondState);
                        mHandler.removeMessages(HANDLER_BT_PAIR);
                        mHandler.sendEmptyMessageDelayed(HANDLER_BT_START_SESSION,
                                HANDLER_BT_DELAYED_TIME);
                    }else if(device.getBondState() == BluetoothDevice.BOND_NONE) {
                        if(mDevice==null && mDevice.getName().startsWith(BT_START_NAME)){
                            // mHandler.sendEmptyMessageDelayed(HANDLER_BT_PAIR, HANDLER_BT_DELAYED_TIME);
                        }
                    }
                }
            }else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                if(mDevice == null) {
                    mHandler.sendEmptyMessage(HANDLER_BT_NOT_FOUND_BT);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpwd);
        initView();
        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        unpairDevice();
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(BluetoothReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(BluetoothReceiver);
        if(mBTRemoteCamHelper!=null)
            mBTRemoteCamHelper.stopSession();
        super.onDestroy();
    }
    /**
     * 初始化View
     */
    private void initView() {
        mTvNotice = (TextView) findViewById(R.id.tv_notice);
        mBtnClose = (Button) findViewById(R.id.btn_close);
        mBtnNo = (Button) findViewById(R.id.btn_no);
        mBtnYes = (Button) findViewById(R.id.btn_yes);
        mBtnNo.setOnClickListener(this);
        mBtnYes.setOnClickListener(this);
        mBtnClose.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_no:
                finish();
                break;
            case R.id.btn_yes:
                showLoading(getString(R.string.base_activity_loading_reset_pwd));
                mHandler.sendEmptyMessage(HANDLER_BT_ENABLE);
                break;
            case R.id.btn_close:
                finish();
                break;
        }
    }

    void resetCameraWIFIPassword(){
        showLoading(getString(R.string.base_activity_loading_reset_pwd));
        resetWIFIPWD(Constant.DEFAULT_CAMERA_WIFI_PWD);
    }

    private void resetWIFIPWD(final String pwd){
        CameraMessage updatePwd = new CameraMessage(CommandID.AMBA_SET_WIFI_SETTING, new CameraMessageCallback() {
            @Override
            public void onReceiveErrorMessage(CameraMessage cameraMessage, JSONObject jsonObject) {
                Log.e(TAG," resetWIFIPWD   onReceiveErrorMessage");
                hideLoading();
                showToastSafe(getString(R.string.base_activity_toast_reset_pwd_failed));
                mHandler.removeMessages(HANDLER_BT_SEND_CMD_TIMEOUT);
            }

            @Override
            public void onReceiveMessage(CameraMessage cameraMessage, JSONObject jsonObject) {
                Log.i(TAG," resetWIFIPWD   onReceiveMessage");
                hideLoading();
                savePwd(pwd);
                reStartCAMWifi();
                mHandler.removeMessages(HANDLER_BT_SEND_CMD_TIMEOUT);
                mHandler.sendEmptyMessage(HANDLER_BT_SEND_RESET_PWD_SUCCESS);
            }

            @Override
            public void onReceiveNotification(JSONObject jsonObject) {
                Log.e(TAG, " resetWIFIPWD   onReceiveNotification : " + jsonObject);
                hideLoading();
                mHandler.removeMessages(HANDLER_BT_SEND_CMD_TIMEOUT);
            }
        });
        updatePwd.put("param" , "AP_PASSWD=" + pwd + "\\nAP_PUBLIC="+"no");
        updatePwd.put("type", "password");
        mBTRemoteCamHelper.sendCommand(updatePwd);
        mHandler.sendEmptyMessageDelayed(HANDLER_BT_SEND_CMD_TIMEOUT,
                HANDLER_BT_RESETPWD_TIMEOUT);
    }

    void savePwd(String pwd){
        SpUtils.getInstance(this).setValue(Constant.WIFI_PWD, pwd);
    }
    private void resetPWDSuccess(){
        mBtnNo.setVisibility(View.GONE);
        mBtnYes.setVisibility(View.GONE);
        mBtnClose.setVisibility(View.VISIBLE);
        mTvNotice.setText(R.string.dialog_reset_pwd_success);
    }

    private void resetPWDfailed(){
        hideLoading();
        showToastSafe(getString(R.string.base_activity_toast_reset_pwd_failed));
    }

    private void reStartCAMWifi(){
        CameraMessage reStartWifi = new CameraMessage(CommandID.AMBA_WIFI_RESTART, new CameraMessageCallback() {
            @Override
            public void onReceiveErrorMessage(CameraMessage cameraMessage, JSONObject jsonObject) {
                Log.e(TAG," reStartWifi   onReceiveErrorMessage");
            }
            @Override
            public void onReceiveMessage(CameraMessage cameraMessage, JSONObject jsonObject) {
                Log.i(TAG," reStartWifi   onReceiveMessage");
            }
            @Override
            public void onReceiveNotification(JSONObject jsonObject) {
                Log.e(TAG, " reStartWifi   onReceiveNotification : " + jsonObject);
            }
        });
        mBTRemoteCamHelper.sendCommand(reStartWifi);
    }

    @Override
    public void onBackPressed() {
        hideLoading();
        mHandler.sendEmptyMessage(HANDLER_BT_STOP_SCAN);
        super.onBackPressed();
    }
}
