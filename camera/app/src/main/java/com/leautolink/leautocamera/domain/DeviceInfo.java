package com.leautolink.leautocamera.domain;

import android.app.Activity;
import android.content.Context;

import com.leautolink.leautocamera.utils.FirmwareUtil;
import com.leautolink.leautocamera.utils.StatisticsUtil;
import com.letv.leauto.cameracmdlibrary.common.Constant;
import com.letv.leauto.cameracmdlibrary.connect.RemoteCamHelper;
import com.letv.leauto.cameracmdlibrary.connect.model.CameraMessage;
import com.letv.leauto.cameracmdlibrary.connect.model.CameraMessageCallback;
import com.letv.leauto.cameracmdlibrary.connect.model.CommandID;

import org.json.JSONObject;

import java.lang.ref.WeakReference;

/**
 * Created by liushengli on 2016/4/7.
 */
public class DeviceInfo {
    private static DeviceInfo mSelf;
    private RemoteCamHelper remoteCamHelper;
    private String brand="";
    private String model="";
    private String uuid="";
    private String WiFi_MAC="";
    private String BT_MAC="";
    private String hw_ver="";
    private String fw_ver="";
    private WeakReference<Context> mContext;
    public static DeviceInfo getInstance(){
        if(mSelf==null){
            mSelf = new DeviceInfo();
        }
        return mSelf;
    }
    private DeviceInfo(){
        remoteCamHelper = RemoteCamHelper.getRemoteCam();
    }
    private void setContext(){

    }
    public void getDeviceInfo() {
        CameraMessage getDeviceInfo = new CameraMessage(CommandID.AMBA_GET_DEVINFO, new CameraMessageCallback() {
            @Override
            public void onReceiveErrorMessage(CameraMessage cameraMessage, JSONObject jsonObject) {

            }
            @Override
            public void onReceiveMessage(CameraMessage cameraMessage, JSONObject jsonObject) {
                setDeviceInfo(jsonObject);
                if(mContext.get()!=null) {
                    FirmwareUtil.saveDeviceInfo(mContext.get());
                    if(mContext.get() instanceof Activity && Constant.isSDCardPresent)
                        FirmwareUtil.checkLocalFile((Activity) mContext.get());

                    StatisticsUtil.getInstance().recordConnectDevice(DeviceInfo.getInstance().getBrand(),
                            DeviceInfo.getInstance().getModel(),
                            DeviceInfo.getInstance().getFwVer(),
                            DeviceInfo.getInstance().getUuid(),
                            DeviceInfo.getInstance().getWiFiMAC(),
                            DeviceInfo.getInstance().getBTMAC(),
                            DeviceInfo.getInstance().getHwVer()
                            );
                }
            }
            @Override
            public void onReceiveNotification(JSONObject jsonObject) {

            }
        });
        if(remoteCamHelper!=null)
            remoteCamHelper.sendCommand(getDeviceInfo);
    }
    void setDeviceInfo(JSONObject jsonObject) {
        brand = jsonObject.optString("brand");
        model = jsonObject.optString("model");
        uuid = jsonObject.optString("uuid");
        WiFi_MAC = jsonObject.optString("WiFi-MAC");
        BT_MAC = jsonObject.optString("BT-MAC");
        hw_ver = jsonObject.optString("hw_ver");
        fw_ver = jsonObject.optString("fw_ver");
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public String getUuid() {
        return uuid;
    }

    public String getWiFiMAC() {
        return WiFi_MAC;
    }

    public String getBTMAC() {
        return BT_MAC;
    }

    public String getHwVer() {
        return hw_ver;
    }

    public String getFwVer() {
        return fw_ver;
    }

    public WeakReference<Context> getContext() {
        return mContext;
    }

    public void setContext(WeakReference<Context> mContext) {
        this.mContext = mContext;
    }
}
