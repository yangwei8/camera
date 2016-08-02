package com.leautolink.leautocamera.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.leautolink.leautocamera.R;
import com.leautolink.leautocamera.config.Constant;
import com.leautolink.leautocamera.ui.adapter.WifiAdapter;
import com.leautolink.leautocamera.utils.AnimationUtils;
import com.leautolink.leautocamera.utils.WifiAdmin;
import com.leautolink.leautocamera.utils.WifiAdminV2;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

@EActivity(R.layout.activity_diaglog)
public class DiaglogActivity extends Activity {

    @ViewById(R.id.lv_wifi_list)
    ListView lv_wifi_list ;

    @ViewById(R.id.bt_button)
    Button bt_button ;

    @ViewById(R.id.iv_loading)
    ImageView loading;

    @ViewById(R.id.tv_no_edr)
    TextView tv_no_edr;

    @Extra
    boolean isAutoConnect;

    private List<ScanResult> wifiScanResult ;
    private WifiAdminV2 admin;
    private ArrayList<ScanResult> newSacn;

    private WifiAdapter wifiAdapter;

    private Handler handler;
    public static final String  ISSELECTED = "isSelectted";


    @AfterViews
    void init(){
        handler = new Handler();
        setFinishOnTouchOutside(false);
        admin = new WifiAdminV2(this);
        if (WifiAdminV2.isConnectCamera(admin.getSSID(),admin.getBSSID())){
            admin.forget(admin.getSSID());
        }
        if (isAutoConnect){
            loading.setVisibility(View.GONE);
            wifiScanResult = admin.getWifiList();
            if (wifiScanResult.size()>0){
                refreshWifiList(0);
            }
        }else {
            refreshWifiList(2000);
        }


    }

    private void refreshWifiList(int delay) {
        lv_wifi_list.setVisibility(View.INVISIBLE);
        tv_no_edr.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);
        AnimationUtils.rotate(loading);
        admin.openWifi();
        admin.startScan();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                wifiList();
            }
        },delay);
    }

    private void wifiList(){
        wifiScanResult = admin.getWifiList();
        if (newSacn != null) {
            newSacn.clear();
        } else {
            newSacn = new ArrayList<>();
        }
        for (ScanResult scanResult : wifiScanResult) {
            if (WifiAdmin.isConnectCamera(DiaglogActivity.this,scanResult.SSID, scanResult.BSSID)) {
                newSacn.add(scanResult);
            }
        }
        if (newSacn.size()>0){
            showList();
        }else {
            showNoEdr();
        }
    }
    @UiThread
    void showNoEdr(){
        tv_no_edr.setVisibility(View.VISIBLE);
        AnimationUtils.cancelAnmation(loading);
        loading.setVisibility(View.GONE);
        lv_wifi_list.setVisibility(View.INVISIBLE);
    }


    @UiThread
    void showList(){
        AnimationUtils.cancelAnmation(loading);
        loading.setVisibility(View.GONE);
        lv_wifi_list.setVisibility(View.VISIBLE);
        if (wifiAdapter == null) {
            wifiAdapter = new WifiAdapter(this,newSacn);
            lv_wifi_list.setAdapter(wifiAdapter);
            lv_wifi_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent();
                    ScanResult result = newSacn.get(position);
                    intent.putExtra(Constant.WIFI_SSID, result);
                    intent.putExtra(ISSELECTED, true);
                    setResult(0, intent);
                    finish();
                }
            });
        }else {
            wifiAdapter.notifyDataSetChanged();
        }
    }

    @Click(R.id.bt_button)
    void clickRefresh(){
        refreshWifiList(2000);
    }

    @Click(R.id.iv_close_diaglog)
    void closeDialog(){
        Intent intent = new Intent();
        intent.putExtra(ISSELECTED,false);
        setResult(0, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        closeDialog();
    }
}
