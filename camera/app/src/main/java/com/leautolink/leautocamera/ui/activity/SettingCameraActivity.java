package com.leautolink.leautocamera.ui.activity;

import android.app.ProgressDialog;
import android.support.design.widget.AppBarLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.leautolink.leautocamera.R;
import com.leautolink.leautocamera.domain.AdasInfo;
import com.leautolink.leautocamera.domain.SettingInfo;
import com.leautolink.leautocamera.net.http.CacheUtils;
import com.leautolink.leautocamera.net.http.DownLoaderTask;
import com.leautolink.leautocamera.net.http.GsonUtils;
import com.leautolink.leautocamera.ui.base.BaseActivity;
import com.leautolink.leautocamera.ui.view.customview.SelectButton;
import com.leautolink.leautocamera.utils.FirmwareUtil;
import com.leautolink.leautocamera.utils.SpUtils;
import com.letv.leauto.cameracmdlibrary.common.Constant;
import com.letv.leauto.cameracmdlibrary.connect.CameraClient;
import com.letv.leauto.cameracmdlibrary.connect.SendCommandCallback;
import com.letv.leauto.cameracmdlibrary.connect.event.NotificationEvent;
import com.letv.leauto.cameracmdlibrary.connect.model.CameraMessage;
import com.letv.leauto.cameracmdlibrary.connect.model.CameraMessageCallback;
import com.letv.leauto.cameracmdlibrary.connect.model.CommandID;
import com.letv.leauto.cameracmdlibrary.utils.SystemUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

@EActivity(R.layout.activity_setting_camera)
public class SettingCameraActivity extends BaseActivity {
    @ViewById(R.id.appBar)
    AppBarLayout appBar;
    @ViewById(R.id.navigation_bar_left_ib)
    ImageButton navigation_bar_left_ib;
    @ViewById(R.id.navigation_bar_title)
    TextView navigation_bar_title;
    @ViewById(R.id.navigation_bar_right_ib)
    ImageButton navigation_bar_right_ib;

    @ViewById(R.id.bt_sound)
    SelectButton bt_sound;
    @ViewById(R.id.bt_record)
    SelectButton bt_record;
    @ViewById(R.id.bt_mark)
    SelectButton bt_mark;
    /**
     * 极客体验
     */
    @ViewById
    SelectButton bt_geek_experience, bt_notiy_knock, bt_notiy_offset, bt_notiy_start, bt_notiy_light;

    /**
     * 极客体验的容器
     */
    @ViewById(R.id.ll_geek_experience)
    LinearLayout ll_geek_experience;

    /**
     * about Camerax
     */
    @ViewById(R.id.rl_about_activity)
    RelativeLayout rl_about_activity;
    /**
     * update pwd
     */
    @ViewById(R.id.rl_update_pwd)
    RelativeLayout rl_update_pwd;
    /**
     * uppdate fw
     */
    @ViewById(R.id.rl_update_fw)
    RelativeLayout rl_update_fw;

    @ViewById
    RadioGroup rg_resolution, rg_sensitivity, rg_livedelay;
    @ViewById
    RadioButton rb_resolution_little, rb_resolution_mid, rb_resolution_mid_hdr, rb_resolution_lagre, rb_sens_low, rb_sens_mid, rb_sens_high, rb_delay_low, rb_delay_mid, rb_delay_high;


    @ViewById
    TextView tv_app_version;

//    @ViewById
//    EditText et_test;

    private CameraClient cameraClient;
    private SettingInfo settingInfo;
    private AdasInfo adasInfo;


    /**
     * 记录camera设置之前的状态  record   or  vf
     */
    private String cameraStatus = "";

    private boolean resolutionFristSet = true;
    private boolean sensitivityFristSet = true;
    private boolean liveDelayFirstSet = true;

    private boolean bt_geek_experienceSet = true;
    private boolean bt_notiy_knockSet = true;
    private boolean bt_notiy_offsetSet = true;
    private boolean bt_notiy_startSet = true;
    private boolean bt_notiy_lightSet = true;

    private ProgressDialog updateFwProgress;
    private String fwName = "";
    private boolean isUpLoad = false;
    private Long timesTamp;
    private String sign;
    private Map<String, String> params;
    private int errno;
    private String errMsg;
    private String versionCode;
    private String versionName;
    private String pkgUrl;
    private final String TAG = "HomeActivity";
    public String path;
    private DownLoaderTask downLoaderTask;
    public CacheUtils cacheUtils;
    private String resp;
    private static final int FOTA_RESP = 1;
    private static final int FOTA_NOT_RESP = 2;
    @Override
    public void onResume() {
        super.onResume();
        int delay = SpUtils.getInt(this, "livedelay", 1000);
        switch (delay) {
            case 1000:
                rb_delay_low.setChecked(true);
                break;
            case 2000:
                rb_delay_mid.setChecked(true);
                break;
            case 3000:
                rb_delay_high.setChecked(true);
                break;

        }
    }

    @AfterViews
    void init() {
        navigation_bar_title.setText(getResources().getString(R.string.camera_set));
        getCache();
        cameraClient = new CameraClient();
        getCurrentStatus();
        initAction();
        tv_app_version.setText(SystemUtils.getAppVersionName(this));
    }


    public CacheUtils getCache() {
        if (this.cacheUtils == null) {
            this.cacheUtils = cacheUtils.getInstance(getApplicationContext());
        }
        return this.cacheUtils;
    }

    /**
     * 获取到当前的设置
     */
    private void getCurrentStatus() {
        showLoading();
        CameraMessage currentStatus = new CameraMessage(CommandID.AMBA_GET_ALL, new CameraMessageCallback() {
            @Override
            public void onReceiveErrorMessage(CameraMessage cameraMessage, JSONObject jsonObject) {
                hideLoading();
            }

            @Override
            public void onReceiveMessage(CameraMessage cameraMessage, JSONObject jsonObject) {
                settingInfo = formatJsonStr(jsonObject);
                initViewStatus(settingInfo);
                getADASInfo();
//                hideLoading();
            }

            @Override
            public void onReceiveNotification(JSONObject jsonObject) {

            }
        });
        remoteCamHelper.sendCommand(currentStatus);
    }

    private void getADASInfo() {
        CameraMessage adasRequest = new CameraMessage(CommandID.AMBA_ADAS_INFO, new CameraMessageCallback() {
            @Override
            public void onReceiveErrorMessage(CameraMessage cameraMessage, JSONObject jsonObject) {
                hideLoading();
            }

            @Override
            public void onReceiveMessage(CameraMessage cameraMessage, JSONObject jsonObject) {
                adasInfo = GsonUtils.fromJson(jsonObject.toString(), AdasInfo.class);
                initViewAdas(adasInfo);
                hideLoading();
            }

            @Override
            public void onReceiveNotification(JSONObject jsonObject) {

            }
        });
        adasRequest.put("type", "get");
        remoteCamHelper.sendCommand(adasRequest);
    }

    @UiThread
    void initViewStatus(SettingInfo settingInfo) {
        bt_sound.setIsChecked("On".equals(settingInfo.getMicphone()));
        bt_record.setIsChecked("Auto".equals(settingInfo.getRecord_mode()));
        bt_mark.setIsChecked(!TextUtils.isEmpty(settingInfo.getStamp()));
        if (settingInfo.getVideo_resolution().contains("HDR")) {
            rb_resolution_mid_hdr.setChecked(true);
        } else if (settingInfo.getVideo_resolution().contains("1920x1080")) {
            rb_resolution_mid.setChecked(true);
        } else if (settingInfo.getVideo_resolution().contains("1280x720")) {
            rb_resolution_little.setChecked(true);
        } else {
            rb_resolution_lagre.setChecked(true);
        }
        if ("low".equals(settingInfo.getEvent_sensitivity())) {
            rb_sens_low.setChecked(true);
        } else if ("medium".equals(settingInfo.getEvent_sensitivity())) {
            rb_sens_mid.setChecked(true);
        } else if ("high".equals(settingInfo.getEvent_sensitivity())) {
            rb_sens_high.setChecked(true);
        } else {
            sensitivityFristSet = false;
        }
    }

    @UiThread
    void initViewAdas(AdasInfo adasInfo) {
        if (adasInfo.getFcmd().contains("off") && adasInfo.getFcws().contains("off") && adasInfo.getLdws().contains("off") && adasInfo.getLlw().contains("off")) {
            ll_geek_experience.setVisibility(View.GONE);
            bt_geek_experience.setIsChecked(false);
            bt_notiy_knock.setIsChecked(false);
            bt_notiy_start.setIsChecked(false);
            bt_notiy_light.setIsChecked(false);
            bt_notiy_offset.setIsChecked(false);
        } else {
            ll_geek_experience.setVisibility(View.VISIBLE);
            bt_geek_experience.setIsChecked(true);
            if (adasInfo.getFcws().contains("off")) {
                bt_notiy_knock.setIsChecked(false);
            } else {
                bt_notiy_knock.setIsChecked(true);
            }

            if (adasInfo.getFcmd().contains("off")) {
                bt_notiy_start.setIsChecked(false);
            } else {
                bt_notiy_start.setIsChecked(true);
            }
            if (adasInfo.getLlw().contains("off")) {
                bt_notiy_light.setIsChecked(false);
            } else {
                bt_notiy_light.setIsChecked(true);
            }

            if (adasInfo.getLdws().contains("off")) {
                bt_notiy_offset.setIsChecked(false);
            } else {
                bt_notiy_offset.setIsChecked(true);
            }
        }
    }

    private void initAction() {
        bt_sound.setOnCheckedChangeListener(new SelectButton.OnCheckedChangeListener() {
            @Override
            public void checkedChange(boolean checked) {
                setSound(checked);
            }
        });

        bt_record.setOnCheckedChangeListener(new SelectButton.OnCheckedChangeListener() {
            @Override
            public void checkedChange(boolean checked) {
                setAutoRecord(checked);
            }
        });
        bt_mark.setOnCheckedChangeListener(new SelectButton.OnCheckedChangeListener() {
            @Override
            public void checkedChange(boolean checked) {
                setStamp(checked);
            }
        });
        rg_resolution.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (!resolutionFristSet) {
                    setResolution(checkedId);
                }
                resolutionFristSet = false;
            }
        });

        rg_livedelay.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (!liveDelayFirstSet) {
                    setLiveDelay(checkedId);
                }
                liveDelayFirstSet = false;
            }
        });

        rg_sensitivity.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (!sensitivityFristSet) {
                    setSensitivity(checkedId);
                }
                sensitivityFristSet = false;
            }
        });
        bt_geek_experience.setOnCheckedChangeListener(new SelectButton.OnCheckedChangeListener() {
            @Override
            public void checkedChange(boolean checked) {
                if (checked) {
                    ll_geek_experience.setVisibility(View.VISIBLE);
                } else {
                    closeAllAdas();
                    ll_geek_experience.setVisibility(View.GONE);
                }
            }
        });

        bt_notiy_offset.setOnCheckedChangeListener(new SelectButton.OnCheckedChangeListener() {
            @Override
            public void checkedChange(boolean checked) {
                setAdasOffSet(checked);
            }
        });
        bt_notiy_light.setOnCheckedChangeListener(new SelectButton.OnCheckedChangeListener() {
            @Override
            public void checkedChange(boolean checked) {
                setAdasLight(checked);
            }
        });
        bt_notiy_knock.setOnCheckedChangeListener(new SelectButton.OnCheckedChangeListener() {
            @Override
            public void checkedChange(boolean checked) {
                setAdasKnock(checked);
            }
        });

        bt_notiy_start.setOnCheckedChangeListener(new SelectButton.OnCheckedChangeListener() {
            @Override
            public void checkedChange(boolean checked) {
                setAdasStart(checked);
            }
        });

    }

    private void setAdasStart(boolean checked) {
        String param = checked ? "medium" : "off";
        CameraMessage adasRequest = new CameraMessage(CommandID.AMBA_ADAS_INFO, new CameraMessageCallback() {
            @Override
            public void onReceiveErrorMessage(CameraMessage cameraMessage, JSONObject jsonObject) {
                hideLoading();
            }

            @Override
            public void onReceiveMessage(CameraMessage cameraMessage, JSONObject jsonObject) {
                adasInfo = GsonUtils.fromJson(jsonObject.toString(), AdasInfo.class);
                initViewAdas(adasInfo);
                hideLoading();
                showToastSafe(getResources().getString(R.string.set_success));
            }

            @Override
            public void onReceiveNotification(JSONObject jsonObject) {

            }
        });
        adasRequest.put("type", "set");
        adasRequest.put("fcmd", param);
        remoteCamHelper.sendCommand(adasRequest);

    }

    private void setAdasOffSet(boolean checked) {
        String param = checked ? "medium" : "off";
        CameraMessage adasRequest = new CameraMessage(CommandID.AMBA_ADAS_INFO, new CameraMessageCallback() {
            @Override
            public void onReceiveErrorMessage(CameraMessage cameraMessage, JSONObject jsonObject) {
                hideLoading();
            }

            @Override
            public void onReceiveMessage(CameraMessage cameraMessage, JSONObject jsonObject) {
                adasInfo = GsonUtils.fromJson(jsonObject.toString(), AdasInfo.class);
                initViewAdas(adasInfo);
                hideLoading();
                showToastSafe(getResources().getString(R.string.set_success));
            }

            @Override
            public void onReceiveNotification(JSONObject jsonObject) {

            }
        });
        adasRequest.put("type", "set");
        adasRequest.put("ldws", param);
        remoteCamHelper.sendCommand(adasRequest);

    }

    private void setAdasLight(boolean checked) {
        String param = checked ? "low" : "off";
        CameraMessage adasRequest = new CameraMessage(CommandID.AMBA_ADAS_INFO, new CameraMessageCallback() {
            @Override
            public void onReceiveErrorMessage(CameraMessage cameraMessage, JSONObject jsonObject) {
                hideLoading();
            }

            @Override
            public void onReceiveMessage(CameraMessage cameraMessage, JSONObject jsonObject) {
                adasInfo = GsonUtils.fromJson(jsonObject.toString(), AdasInfo.class);
                initViewAdas(adasInfo);
                showToastSafe(getResources().getString(R.string.set_success));
                hideLoading();
            }

            @Override
            public void onReceiveNotification(JSONObject jsonObject) {

            }
        });
        adasRequest.put("type", "set");
        adasRequest.put("llw", param);
        remoteCamHelper.sendCommand(adasRequest);
    }

    private void setAdasKnock(boolean checked) {
        String param = checked ? "low" : "off";
        CameraMessage adasRequest = new CameraMessage(CommandID.AMBA_ADAS_INFO, new CameraMessageCallback() {
            @Override
            public void onReceiveErrorMessage(CameraMessage cameraMessage, JSONObject jsonObject) {
                hideLoading();
            }

            @Override
            public void onReceiveMessage(CameraMessage cameraMessage, JSONObject jsonObject) {
                adasInfo = GsonUtils.fromJson(jsonObject.toString(), AdasInfo.class);
                initViewAdas(adasInfo);
                showToastSafe(getResources().getString(R.string.set_success));
                hideLoading();
            }

            @Override
            public void onReceiveNotification(JSONObject jsonObject) {

            }
        });
        adasRequest.put("type", "set");
        adasRequest.put("fcws", param);
        remoteCamHelper.sendCommand(adasRequest);
    }

    private void closeAllAdas() {
        CameraMessage adasRequest = new CameraMessage(CommandID.AMBA_ADAS_INFO, new CameraMessageCallback() {
            @Override
            public void onReceiveErrorMessage(CameraMessage cameraMessage, JSONObject jsonObject) {
                hideLoading();
            }

            @Override
            public void onReceiveMessage(CameraMessage cameraMessage, JSONObject jsonObject) {
                adasInfo = GsonUtils.fromJson(jsonObject.toString(), AdasInfo.class);
                initViewAdas(adasInfo);
                hideLoading();
            }

            @Override
            public void onReceiveNotification(JSONObject jsonObject) {

            }
        });
        adasRequest.put("type", "set");
        adasRequest.put("ldws", "off");
        adasRequest.put("fcws", "off");
        adasRequest.put("llw", "off");
        adasRequest.put("fcmd", "off");
        remoteCamHelper.sendCommand(adasRequest);

    }
    /***********设置命令************/

    /**
     * 是否录制声音
     */
    private void setSound(boolean isOpenAudio) {
        String param = isOpenAudio ? "On" : "Off";
        cameraClient.setSetting("micphone", param, new SendCommandCallback() {
            @Override
            public void onFail(CameraMessage cameraMessage, JSONObject jsonObject) {
                showToastSafe(getResources().getString(R.string.set_fail));
            }

            @Override
            public void onSuccess(CameraMessage cameraMessage, JSONObject jsonObject) {
                showToastSafe(getResources().getString(R.string.set_success));
            }
        }, false);
    }


    /**
     * 是否录开机自动录制
     */
    private void setAutoRecord(boolean isAuto) {
//        if (Constant.isSDCardPresent) {
        showLoading(getResources().getString(R.string.editing));
        String param = isAuto ? "Auto" : "Manual";
        cameraClient.setSetting("record_mode", param, new SendCommandCallback() {
            @Override
            public void onFail(CameraMessage cameraMessage, JSONObject jsonObject) {
                hideLoading();
                showToastSafe(getResources().getString(R.string.set_fail));
            }

            @Override
            public void onSuccess(CameraMessage cameraMessage, JSONObject jsonObject) {
                hideLoading();
                showToastSafe(getResources().getString(R.string.set_success));
            }
        }, false);
//        } else {
//            showToastSafe("记录仪存储卡不存在");
//        }
    }

    /**
     * 是否显示水印
     */
    private void setStamp(boolean isShow) {
        showLoading(getResources().getString(R.string.editing));
        String param = isShow ? "time|logo" : "";
        cameraClient.setSetting("stamp", param, new SendCommandCallback() {
            @Override
            public void onFail(CameraMessage paramCameraMessage, JSONObject paramJSONObject) {
                hideLoading();
                showToastSafe(getResources().getString(R.string.set_fail));
            }

            @Override
            public void onSuccess(CameraMessage paramCameraMessage, JSONObject paramJSONObject) {
                hideLoading();
                showToastSafe(getResources().getString(R.string.set_success));
            }
        }, false);
    }


    /**
     * 设置实时流延时
     */
    private void setLiveDelay(int checkedId) {
        showLoading(getResources().getString(R.string.editing));
        switch (checkedId) {
            case R.id.rb_delay_low:
                SpUtils.putInt(SettingCameraActivity.this, "livedelay", 1000);
                hideLoading();
                if (SpUtils.getInt(SettingCameraActivity.this, "livedelay", 3000) == 1000) {
                    showToastSafe(getResources().getString(R.string.edit_set1));
                } else {
                    showToastSafe(getResources().getString(R.string.edit_failure));
                }

                break;
            case R.id.rb_delay_mid:
                SpUtils.putInt(SettingCameraActivity.this, "livedelay", 2000);
                hideLoading();
                if (SpUtils.getInt(SettingCameraActivity.this, "livedelay", 3000) == 2000) {
                    showToastSafe(getResources().getString(R.string.edit_set2));
                } else {
                    showToastSafe(getResources().getString(R.string.edit_failure));
                }
                break;
            case R.id.rb_delay_high:
                SpUtils.putInt(SettingCameraActivity.this, "livedelay", 3000);
                hideLoading();
                if (SpUtils.getInt(SettingCameraActivity.this, "livedelay", 3000) == 3000) {
                    showToastSafe(getResources().getString(R.string.edit_set3));
                } else {
                    showToastSafe(getResources().getString(R.string.edit_failure));
                }
                break;
        }
    }


    /**
     * 设置分辨率
     */
    private void setResolution(int checkedId) {
        showLoading(getResources().getString(R.string.editing));
        String param = checkedId == R.id.rb_resolution_little ? "1280x720 30P 16:9" : checkedId == R.id.rb_resolution_mid_hdr ? "HDR 1920x1080 30P 16:9" : checkedId == R.id.rb_resolution_mid ? "1920x1080 30P 16:9" : "2304x1296 30P 16:9";
        cameraClient.setSetting("video_resolution", param, new SendCommandCallback() {
            @Override
            public void onFail(CameraMessage paramCameraMessage, JSONObject paramJSONObject) {
                hideLoading();
                showToastSafe(getResources().getString(R.string.set_fail));
            }

            @Override
            public void onSuccess(CameraMessage paramCameraMessage, JSONObject paramJSONObject) {
                hideLoading();
                showToastSafe(getResources().getString(R.string.set_success));
//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }
        }, false);
    }

    /**
     * 设置灵敏度
     */
    private void setSensitivity(int checkedId) {
        String param = checkedId == R.id.rb_sens_low ? "low" : checkedId == R.id.rb_sens_mid ? "medium" : checkedId == R.id.rb_sens_high ? "high" : "off";
        cameraClient.setSetting("event_sensitivity", param, new SendCommandCallback() {
            @Override
            public void onFail(CameraMessage cameraMessage, JSONObject jsonObject) {
                showToastSafe(getResources().getString(R.string.set_fail));
            }

            @Override
            public void onSuccess(CameraMessage cameraMessage, JSONObject jsonObject) {
                showToastSafe(getResources().getString(R.string.set_success));
            }
        }, false);
    }


    private SettingInfo formatJsonStr(JSONObject jsonObject) {

        String tempStr = jsonObject.optString("param");

        String js = tempStr.replace("{", "").replace("}", "").replace("[", "{").replace("]", "}");

        Gson gson = new Gson();

        SettingInfo settingInfo = gson.fromJson(js, SettingInfo.class);

        return settingInfo;
    }

    @Click(R.id.rl_update_fw)
    void updateFW() {
        //TODO:
        //1.检查是否需要更新固件
        //FirmwareUtil.checkOtaUpdate(this, true, true);
        if(!Constant.isSDCardPresent){
            showToastSafe(R.string.no_sd_card);
            return;
        }

        if(!FirmwareUtil.canLocalUpgrade(this)){
            showToastSafe(R.string.is_new);
            return;
        }
        FirmwareUtil.checkLocalFile(this);
        //2.如果需要则下载新的固件到手机SD卡
        //2.1把下载后的固件上传到camera的SD卡
        //2.2发送更新固件的命令
        //3.如果不需要则提示已经是最新的固件
        //fwName = "AmbaBootFW.bin";
        //fwName = "AmbaBootFW.bin";
//        String paths[] ={SdCardUtils.getSDCardRootPath(this) + "/AmbaBootFW.bin",SdCardUtils.getSDCardRootPath(this) + "/AmbaSysFW.bin"};
//        FirmwareUtil.upLoadFWToCamera(paths, this, remoteCamHelper);

//        if (Constant.isSDCardPresent) {

//        } else {
//            showToastSafe("记录仪存储卡不存在");
//        }
    }


    @Click(R.id.rl_about_activity)
    void goAboutActivity() {
        AboutCameraActivity_.intent(this).start();
    }

    @Click(R.id.rl_update_pwd)
    void goSettingPwd() {
        SettingPasswordActivity_.intent(this).start();
    }

    @Click(R.id.rl_sd_card_info)
    void goSDCardInfo() {
        if (Constant.isSDCardPresent) {
            CameraSdInfoActivity_.intent(this).start();
        } else {
            showToastSafe(getResources().getString(R.string.idcard_loss));
        }
    }
    /**
     * 1296主码流
     */
//    @Click(R.id.bt_test)
//    void bt_test() {
//        String bitrate = et_test.getText().toString();
//        if(!TextUtils.isEmpty(bitrate)){
//            cameraClient.setSetting("bitrate", "Pri|2304x1296 30P 16:9|"+bitrate, new SendCommandCallback() {
//                @Override
//                public void onFail(CameraMessage cameraMessage, JSONObject jsonObject) {
//                    showToastSafe("修改失败");
//
//                }
//
//                @Override
//                public void onSuccess(CameraMessage cameraMessage, JSONObject jsonObject) {
//                    showToastSafe("修改成功");
//
//                }
//            }, false);
//        }
//
//
//    }

    /**
     * 2M
     */
    @Click(R.id.bt_second_resolution_two)
    void bt_second_resolution_two() {
        cameraClient.setSetting("bitrate", "Sec|1280x720 30P 16:9|2000", new SendCommandCallback() {
            @Override
            public void onFail(CameraMessage cameraMessage, JSONObject jsonObject) {
                showToastSafe(getResources().getString(R.string.edit_failure2));
            }

            @Override
            public void onSuccess(CameraMessage cameraMessage, JSONObject jsonObject) {
                showToastSafe(getResources().getString(R.string.edit_success2));
            }
        }, false);
    }

    /**
     * 3M
     */
//    @Click(R.id.bt_second_resolution_three)
//    void bt_second_resolution_three() {
//        cameraClient.setSetting("bitrate", "Sec|1280x720 30P 16:9|3000", new SendCommandCallback() {
//            @Override
//            public void onFail(CameraMessage cameraMessage, JSONObject jsonObject) {
//                showToastSafe("修改失败");
//            }
//
//            @Override
//            public void onSuccess(CameraMessage cameraMessage, JSONObject jsonObject) {
//                showToastSafe("修改成功");
//            }
//        }, false);
//    }
    /**
     * 4M
     */
//    @Click(R.id.bt_second_resolution_foure)
//    void bt_second_resolution_foure() {
//        cameraClient.setSetting("bitrate", "Sec|1280x720 30P 16:9|4000", new SendCommandCallback() {
//            @Override
//            public void onFail(CameraMessage cameraMessage, JSONObject jsonObject) {
//                showToastSafe("修改失败");
//            }
//
//            @Override
//            public void onSuccess(CameraMessage cameraMessage, JSONObject jsonObject) {
//                showToastSafe("修改成功");
//            }
//        }, false);
//    }

    /**
     * 时间同步
     */
    @Click(R.id.rl_syn_time)
    void synTime() {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(new Date());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        cameraClient.setSetting("camera_clock", format.format(c1.getTime()), new SendCommandCallback() {
            @Override
            public void onFail(CameraMessage cameraMessage, JSONObject jsonObject) {
                showToastSafe(getResources().getString(R.string.time_error));
            }

            @Override
            public void onSuccess(CameraMessage cameraMessage, JSONObject jsonObject) {
                showToastSafe(getResources().getString(R.string.time_suceess));
            }
        }, false);
    }

    /**
     * 还原恢复出厂设置
     */
    @Click(R.id.rl_resume_camera)
    void clickReStoreFactory() {
        showEDiaLog(getResources().getString(R.string.return_factory));

    }

    private void showEDiaLog(String text) {
       showConfirmDialog(text, new OnDialogListener() {
           @Override
           public void onSure() {
               cameraClient.setSetting("default_setting", "on", new SendCommandCallback() {
                   @Override
                   public void onFail(CameraMessage cameraMessage, JSONObject jsonObject) {
                       showToastSafe(getResources().getString(R.string.factory_fail));
                   }

                   @Override
                   public void onSuccess(CameraMessage cameraMessage, JSONObject jsonObject) {
                       showToastSafe(getResources().getString(R.string.factory_success));
                   }
               }, false);
           }

           @Override
           public void onCancel() {

           }
       });
    }

    public void onEventMainThread(NotificationEvent notificationEvent) {
        int notificationEventType = notificationEvent.getType();
        if (notificationEventType == NotificationEvent.VF || notificationEventType == NotificationEvent.RECORD) {
            hideLoading();
        }
    }


    @Click(R.id.navigation_bar_left_ib)
    void goBack() {
        this.finish();
    }
}
