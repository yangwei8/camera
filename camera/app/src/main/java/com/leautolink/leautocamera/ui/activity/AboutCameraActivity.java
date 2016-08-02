package com.leautolink.leautocamera.ui.activity;

import android.support.design.widget.AppBarLayout;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import com.leautolink.leautocamera.R;
import com.leautolink.leautocamera.ui.base.BaseActivity;
import com.letv.leauto.cameracmdlibrary.connect.model.CameraMessage;
import com.letv.leauto.cameracmdlibrary.connect.model.CameraMessageCallback;
import com.letv.leauto.cameracmdlibrary.connect.model.CommandID;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.WindowFeature;
import org.json.JSONObject;

@WindowFeature({Window.FEATURE_NO_TITLE})
@EActivity(R.layout.activity_about_camera)
public class AboutCameraActivity extends BaseActivity {
    /***************************
     * 导航条相关的属性
     ******************************/
    @ViewById(R.id.appBar)
    AppBarLayout appBar;
    @ViewById(R.id.navigation_bar_left_ib)
    ImageButton navigation_bar_left_ib;
    @ViewById(R.id.navigation_bar_title)
    TextView navigation_bar_title;
    @ViewById(R.id.navigation_bar_right_ib)
    ImageButton navigation_bar_right_ib;

    @Extra
    String deviceInfo;

    @ViewById
    TextView tv_device_name,tv_xing_hao,tv_uuid,tv_wifi_address,tv_bluetooth_address,tv_hard_version,tv_fw_version;

    @AfterViews
    void init(){
        getDeviceInfo();
        setTitle();
    }

    private void setTitle() {
        navigation_bar_title.setText(R.string.about);
    }

    /**
     * 获取设备信息
     */
    private void getDeviceInfo() {
        CameraMessage getDeviceInfo = new CameraMessage(CommandID.AMBA_GET_DEVINFO, new CameraMessageCallback() {
            @Override
            public void onReceiveErrorMessage(CameraMessage cameraMessage, JSONObject jsonObject) {

            }
            @Override
            public void onReceiveMessage(CameraMessage cameraMessage, JSONObject jsonObject) {
                setDeviceInfo(jsonObject);
                setFWVersion(jsonObject.optString("fw_ver"));
            }
            @Override
            public void onReceiveNotification(JSONObject jsonObject) {

            }
        });
        remoteCamHelper.sendCommand(getDeviceInfo);
    }
    @UiThread
    void setDeviceInfo(JSONObject jsonObject) {
//        tv_device_name.setText(jsonObject.optString("brand"));
        tv_xing_hao.setText(jsonObject.optString("model"));
        tv_uuid.setText(jsonObject.optString("uuid"));
        tv_wifi_address.setText(jsonObject.optString("WiFi-MAC"));
        tv_bluetooth_address.setText(jsonObject.optString("BT-MAC"));
        tv_hard_version.setText(jsonObject.optString("hw_ver"));
        tv_fw_version.setText(jsonObject.optString("fw_ver"));
    }

    @UiThread
    void  setFWVersion(String fwversion){

        tv_fw_version.setText(fwversion);
    }
    @Click(R.id.navigation_bar_left_ib)
    void goBack(){
        this.finish();
    }

}
