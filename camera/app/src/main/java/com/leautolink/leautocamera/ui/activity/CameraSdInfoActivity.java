package com.leautolink.leautocamera.ui.activity;

import android.view.Window;
import android.widget.TextView;

import com.leautolink.leautocamera.R;
import com.leautolink.leautocamera.ui.base.BaseActivity;
import com.leautolink.leautocamera.utils.FormatUtils;
import com.letv.leauto.cameracmdlibrary.common.Constant;
import com.letv.leauto.cameracmdlibrary.connect.CameraClient;
import com.letv.leauto.cameracmdlibrary.connect.SendCommandCallback;
import com.letv.leauto.cameracmdlibrary.connect.model.CameraMessage;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.WindowFeature;
import org.androidannotations.annotations.res.StringRes;
import org.json.JSONObject;

@WindowFeature({Window.FEATURE_NO_TITLE})
@EActivity(R.layout.activity_camera_sd_info)
public class CameraSdInfoActivity extends BaseActivity {
    /***************************
     * 导航条相关的属性
     ******************************/
    @ViewById(R.id.navigation_bar_title)
    TextView navigation_bar_title;


    @StringRes(R.string.no_sd_card)
    String no_sd_card;

    @ViewById
    TextView tv_total_space, tv_used_space, tv_free_space;

//    @ViewById(R.id.bt_formast_sd_card)
//    SelectButton bt_formast_sd_card;

    private CameraClient cameraClient;

    private Long totle;
    private Long free;

    @AfterViews
    void init() {
        navigation_bar_title.setText(R.string.idcard_name);
        cameraClient = new CameraClient();
        getSpaceInfo();
    }

    @Click(R.id.bt_formast_sd_card)
    void onClickformatSDCard() {
//        if (Constant.isSDCardPresent) {
            showDigLog(getResources().getString(R.string.idcard_format));
//        }else {
//            showToastSafe("记录仪存储卡不存在");
//        }
    }

    private void formatSDCard() {
        showLoading(getResources().getString(R.string.idcard_format_prograss));
        cameraClient.formatSDCard(new SendCommandCallback() {
            @Override
            public void onFail(CameraMessage paramCameraMessage, JSONObject paramJSONObject) {
                showToastSafe(getResources().getString(R.string.idcard_format_fail));
                hideLoading();
            }

            @Override
            public void onSuccess(CameraMessage paramCameraMessage, JSONObject paramJSONObject) {
                showToastSafe(getResources().getString(R.string.idcard_format_success));
                againGetSpaceInfo();
            }
        });
    }

    @UiThread
    void againGetSpaceInfo(){
        getSpaceInfo();
    }

    private void getSpaceInfo() {
        cameraClient.getSDCardSpace("total", new SendCommandCallback() {
            @Override
            public void onFail(CameraMessage paramCameraMessage, JSONObject paramJSONObject) {
                //                showToastSafe(paramJSONObject.toString());
                hideLoading();
                showToastSafe(no_sd_card);
            }

            @Override
            public void onSuccess(CameraMessage paramCameraMessage, JSONObject paramJSONObject) {
                totle = paramJSONObject.optLong("param");
                cameraClient.getSDCardSpace("free", new SendCommandCallback() {
                    @Override
                    public void onFail(CameraMessage paramCameraMessage, JSONObject paramJSONObject) {
                        //                        showToastSafe(paramJSONObject.toString());
                        hideLoading();
                        showToastSafe(no_sd_card);
                    }

                    @Override
                    public void onSuccess(CameraMessage paramCameraMessage, JSONObject paramJSONObject) {
                        hideLoading();
                        free = paramJSONObject.optLong("param");
                        setValue();
                    }
                });
            }
        });
    }

    @UiThread
    void setValue() {
        if (totle > 0) {
            double prop = (double) free / totle;
            tv_total_space.setText(FormatUtils.formatKBFileSize(totle));
            tv_used_space.setText(FormatUtils.formatKBFileSize(totle - free));
            tv_free_space.setText(FormatUtils.formatKBFileSize(free));
        }
    }
    private void showDigLog(String text) {
        showConfirmDialog(text, new OnDialogListener() {
            @Override
            public void onSure() {
                if (Constant.isSDCardPresent) {
                    formatSDCard();
                }else {
                    showToastSafe(getResources().getString(R.string.idcard_loss));
                }
            }

            @Override
            public void onCancel() {

            }
        });
    }


    @Click(R.id.navigation_bar_left_ib)
    void goBack() {
        this.finish();
    }

}
