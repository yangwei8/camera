package com.leautolink.leautocamera.ui.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.leautolink.leautocamera.R;
import com.leautolink.leautocamera.ui.adapter.BluetoothListAdapter;
import com.leautolink.leautocamera.utils.AnimationUtils;
import com.leautolink.leautocamera.utils.Logger;
import com.leautolink.leautocamera.utils.WifiAdmin;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

@EActivity(R.layout.activity_diaglog)
public class DiaglogBluetoothActivity extends Activity {
    private static final String TAG = "DiaglogBluetoothActivity";
    private static final String BT_START_NAME = "Le-DVR";

    @ViewById(R.id.lv_wifi_list)
    ListView lv_wifi_list;

    @ViewById(R.id.bt_button)
    Button bt_button;

    @ViewById(R.id.iv_loading)
    ImageView loading;

    @ViewById(R.id.tv_no_edr)
    TextView tv_no_edr;

    private WifiAdmin admin;

    private BluetoothListAdapter bluetoothListAdapter;

    private Handler handler;
    public static final String ISSELECTED = "isSelectted";

    private BluetoothAdapter mBluetoothAdapter;

    private List<BluetoothDevice> devices;

    /**
     * 蓝牙扫描结果的广播接受者
     */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // 获得已经搜索到的蓝牙设备
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String name = device.getName();
                Logger.i("TAG","device name : "+device.getName());
                if (!TextUtils.isEmpty(name)){
                    name=name.trim();
                    if(name.startsWith(BT_START_NAME)){
                        Logger.i(TAG, "device.name->|" + device.getName() + "|=State->" + device.getBondState());
                        devices.add(device);
                    }
                }
                // 搜索到的不是已经绑定的蓝牙设备
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    // 显示在TextView上

                }
                // 搜索完成
            } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                Logger.i("TAG","ACTION_DISCOVERY_FINISHED ： " + devices.size());
                if (devices.size()==0){
                    showNoEdr();
                }else {
                    showList();
                }

            }
        }
    };

    @AfterViews
    void init() {
        handler = new Handler();
        devices = new ArrayList<>();
        setFinishOnTouchOutside(false);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // 注册用以接收到已搜索到的蓝牙设备的receiver
        IntentFilter mFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, mFilter);
        // 注册搜索完时的receiver
        mFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, mFilter);
        admin = new WifiAdmin(this);

        refreshBluetoothList(0);

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        //解除注册
        unregisterReceiver(mReceiver);
    }

    private void refreshBluetoothList(int delay) {
        lv_wifi_list.setVisibility(View.INVISIBLE);
        tv_no_edr.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);
        AnimationUtils.rotate(loading);
        admin.startScan();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (devices!=null){
                    devices.clear();
                }
                startBluetoothScan();
            }
        }, delay);
    }

    private void startBluetoothScan() {
        // 如果正在搜索，就先取消搜索
        stopBluetoothScan();
        // 开始搜索蓝牙设备,搜索到的蓝牙设备通过广播返回
        mBluetoothAdapter.startDiscovery();
    }

    private void stopBluetoothScan(){
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
    }


    //    private void wifiList(){
//        wifiScanResult = admin.getmWifiManager().getScanResults();
//        if (newSacn != null) {
//            newSacn.clear();
//        } else {
//            newSacn = new ArrayList<>();
//        }
//        for (ScanResult scanResult : wifiScanResult) {
//            if (WifiAdmin.isConnectCamera(scanResult.SSID, scanResult.BSSID)) {
//                newSacn.add(scanResult);
//            }
//        }
//        if (newSacn.size()>0){
//            showList();
//        }else {
//            showNoEdr();
//        }
//    }
    @UiThread
    void showNoEdr() {
        tv_no_edr.setVisibility(View.VISIBLE);
        AnimationUtils.cancelAnmation(loading);
        loading.setVisibility(View.GONE);
        lv_wifi_list.setVisibility(View.INVISIBLE);
    }


    @UiThread
    void showList() {
        AnimationUtils.cancelAnmation(loading);
        loading.setVisibility(View.GONE);
        lv_wifi_list.setVisibility(View.VISIBLE);
        if (bluetoothListAdapter == null) {
            bluetoothListAdapter = new BluetoothListAdapter(this, devices);
            lv_wifi_list.setAdapter(bluetoothListAdapter);
            lv_wifi_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    Intent intent = new Intent();
//                    ScanResult result = newSacn.get(position);
//                    intent.putExtra(Constant.WIFI_SSID, result);
//                    intent.putExtra(ISSELECTED, true);
//                    setResult(0, intent);
//                    finish();
                }
            });
        } else {
            bluetoothListAdapter.notifyDataSetChanged();
        }
    }

    @Click(R.id.bt_button)
    void clickRefresh() {
        refreshBluetoothList(2000);
    }

    @Click(R.id.iv_close_diaglog)
    void closeDialog() {
//        Intent intent = new Intent();
//        intent.putExtra(ISSELECTED,false);
//        setResult(0, intent);
//        finish();
    }

    @Override
    public void onBackPressed() {
        closeDialog();
    }
}
