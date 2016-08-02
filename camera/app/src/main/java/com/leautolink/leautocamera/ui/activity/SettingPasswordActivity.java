package com.leautolink.leautocamera.ui.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.leautolink.leautocamera.R;
import com.leautolink.leautocamera.config.Constant;
import com.leautolink.leautocamera.ui.base.BaseActivity;
import com.leautolink.leautocamera.ui.view.customview.EditTextWithDelete;
import com.leautolink.leautocamera.utils.SpUtils;
import com.leautolink.leautocamera.utils.WifiAdmin;
import com.letv.leauto.cameracmdlibrary.connect.model.CameraMessage;
import com.letv.leauto.cameracmdlibrary.connect.model.CameraMessageCallback;
import com.letv.leauto.cameracmdlibrary.connect.model.CommandID;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

@EActivity(R.layout.activity_setting_password)
public class SettingPasswordActivity extends BaseActivity {
    /***************************
     * 导航条相关的属性
     ******************************/
    @ViewById(R.id.navigation_bar_left_ib)
    ImageButton navigation_bar_left_ib;
    @ViewById(R.id.navigation_bar_title)
    TextView navigation_bar_title;
    @ViewById(R.id.navigation_bar_right_ib)
    ImageButton navigation_bar_right_ib;


    @ViewById
    EditTextWithDelete et_pwd,et_sure_pwd;

//    @ViewById
//    TextView et_edr_name;

    @AfterViews
    void init(){
        setTitle();
    }

    protected void setTitle() {
        navigation_bar_title.setText(getResources().getString(R.string.new_password));
        navigation_bar_right_ib.setVisibility(View.GONE);
    }
    @Click(R.id.bt_sure)
    void updateCameraPassword(){
        final String pwd = et_pwd.getEditableText().toString();
        String surePwd = et_sure_pwd.getEditableText().toString();
        if(TextUtils.isEmpty(pwd)){
            et_pwd.setFocusable(true);
            et_pwd.setFocusableInTouchMode(true);
            et_pwd.requestFocus();
            et_pwd.findFocus();
            showToastSafe(getResources().getString(R.string.input_password));
        }else if (pwd.length()<8){
            showToastSafe(getResources().getString(R.string.password_long));
        }else if (TextUtils.isEmpty(surePwd)) {
            et_sure_pwd.setFocusable(true);
            et_sure_pwd.setFocusableInTouchMode(true);
            et_sure_pwd.requestFocus();
            et_sure_pwd.findFocus();
            showToastSafe(getResources().getString(R.string.input_password2));
        }else if(!pwd.equals(surePwd)){
            showToastSafe(getResources().getString(R.string.password_noequal));
        }else{
            showConfirmDialog(getResources().getString(R.string.is_password), new OnDialogConfirmListener() {
                @Override
                public void onDialogConfirm() {
                    showLoading(getResources().getString(R.string.on_edit));
                    update(pwd);
                }
            });
        }
    }

    private void update(final String pwd){
        CameraMessage updatePwd = new CameraMessage(CommandID.AMBA_SET_WIFI_SETTING, new CameraMessageCallback() {
            @Override
            public void onReceiveErrorMessage(CameraMessage cameraMessage, JSONObject jsonObject) {
                hideLoading();
                showToastSafe(getResources().getString(R.string.password_error2));
            }

            @Override
            public void onReceiveMessage(CameraMessage cameraMessage, JSONObject jsonObject) {
                savePwd(pwd);
                //                et_sure_pwd.setText("");
                //
                //              et_pwd.setText("");
                reStartWIFI();
                hideLoading();
                WifiAdmin admin = new WifiAdmin(SettingPasswordActivity.this);
                admin.openWifi();
            }

            @Override
            public void onReceiveNotification(JSONObject jsonObject) {

            }
        });
        //        "AP_SSID="+name+"\\nAP_PASSWD=" + pwd+"\\nAP_PUBLIC="+"no"
        updatePwd.put("param" , "AP_PASSWD=" + pwd+"\\nAP_PUBLIC="+"no");
        updatePwd.put("type","password");
        //        updatePwd.put("param" , "AP_SSID="+name+"\\nAP_PASSWD=" + pwd+"\\nAP_PUBLIC="+"no");

        remoteCamHelper.sendCommand(updatePwd);
    }

    private void reStartWIFI(){
//        disConnectWifi();
        CameraMessage reStart = new CameraMessage(CommandID.AMBA_WIFI_RESTART, new CameraMessageCallback() {
            @Override
            public void onReceiveErrorMessage(CameraMessage cameraMessage, JSONObject jsonObject) {
                hideLoading();
            }

            @Override
            public void onReceiveMessage(CameraMessage cameraMessage, JSONObject jsonObject) {
                hideLoading();
                //
            }

            @Override
            public void onReceiveNotification(JSONObject jsonObject) {

            }
        });
        remoteCamHelper.sendCommand(reStart);
    }

    @Background(delay = 8000)
    void connectCameraWifi(){
//        ConnectHelper.connect2CameraWifi(this);
    }

    @UiThread
    void savePwd(String pwd){
        SpUtils.getInstance(this).setValue(Constant.WIFI_PWD,pwd);
        SpUtils.getInstance(this).setValue(Constant.WIFI_NAME,pwd);
    }

    @Click(R.id.navigation_bar_left_ib)
    void goBack() {
        this.finish();
    }

//    private void disConnectWifi(){
//        WifiAdmin admin = new WifiAdmin(this);
//        admin.forget("CarDV-74BF");
////        if (WifiAdmin.isConnectCamera(admin.getSSID(), admin.getBSSID())) {
//////            admin.disconnectWifi(admin.getNetworkId());
////            admin.forget(admin.getSSID());
////        }
//    }

}
