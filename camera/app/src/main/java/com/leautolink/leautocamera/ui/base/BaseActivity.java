package com.leautolink.leautocamera.ui.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.leautolink.leautocamera.R;
import com.leautolink.leautocamera.application.LeautoCameraAppLication;
import com.leautolink.leautocamera.config.Constant;
import com.leautolink.leautocamera.event.BaseEvent;
import com.leautolink.leautocamera.event.CameraDisconnectEvent;
import com.leautolink.leautocamera.event.NetWorkBadEvent;
import com.leautolink.leautocamera.event.UDPTimeOutEvent;
import com.leautolink.leautocamera.receivers.HomeKeyReceiver;
import com.leautolink.leautocamera.ui.activity.HomeActivity_;
import com.leautolink.leautocamera.ui.view.customview.LoadingDiglog;
import com.leautolink.leautocamera.ui.view.customview.NormalDialog;
import com.leautolink.leautocamera.utils.Logger;
import com.leautolink.leautocamera.utils.SpUtils;
import com.leautolink.leautocamera.utils.StatisticsUtil;
import com.leautolink.leautocamera.utils.WifiAdminV2;
import com.letv.leauto.cameracmdlibrary.connect.RemoteCamHelper;
import com.letv.leauto.cameracmdlibrary.connect.event.OtherPhoneConnectedEvent;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.umeng.message.PushAgent;

import de.greenrobot.event.EventBus;

/**
 * Created by tianwei1 on 2016/2/25.
 */
public class BaseActivity extends Activity implements IToastSafe {

    protected SystemBarTintManager tintManager;


    private int tid;
    protected Handler handler;
    private LayoutInflater inflater;
    private Dialog loadingViewDialog;
    private long lastToastTime;
    private String lastToastText;
    private static final long TOAST_INTERNAL = 1000;

    protected RemoteCamHelper remoteCamHelper;

    protected NormalDialog alertDialog;


    /**
     * 是否是当前的activity
     */
    private boolean isCurrentActivity;

    private IntentFilter mIntentFilter;
    private BroadcastReceiver mHomeKeyReceiver = new HomeKeyReceiver(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //统计应用启动数据
        PushAgent.getInstance(this).onAppStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.colorPrimary);

        EventBus.getDefault().register(this);
        remoteCamHelper = RemoteCamHelper.getRemoteCam();
        tid = android.os.Process.myTid();
        handler = new Handler();
        inflater = LayoutInflater.from(this);

        mIntentFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(mHomeKeyReceiver, mIntentFilter);
    }

    public void onEvent(BaseEvent event) {

    }
    public void onEventMainThread(NetWorkBadEvent event) {
        if (isCurrentActivity()) {
            showToastSafe(getString(R.string.network_disable));
        }
    }
    public void onEventMainThread(OtherPhoneConnectedEvent event) {
        if (isCurrentActivity()) {
            showConfirmDialog(getString(R.string.other_phone_connected), R.drawable.dialog_yes, new OnDialogListener() {
                @Override
                public void onSure() {

                }

                @Override
                public void onCancel() {
                    WifiAdminV2 adminV2 = new WifiAdminV2(BaseActivity.this);
                    adminV2.forget(SpUtils.getInstance(BaseActivity.this).getStringValue(Constant.WIFI_SSID));
                    HomeActivity_.intent(BaseActivity.this).start();
                }
            });
        }
    }
    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public void onResume() {
        super.onResume();
        isCurrentActivity = true;
        StatisticsUtil.getInstance().recordActivityStart(this.getClass().getSimpleName());
    }

    public void onPause() {
        super.onPause();
        StatisticsUtil.getInstance().recordActivityEnd(this.getClass().getSimpleName());
    }

    @Override
    protected void onStop() {
        super.onStop();
        isCurrentActivity = false;
    }

    public void showConfirmDialog(final String text, final OnDialogConfirmListener listener) {
        this.showConfirmDialog(text, R.drawable.dialog_yes, new OnDialogListener() {
            @Override
            public void onSure() {
                listener.onDialogConfirm();
            }

            @Override
            public void onCancel() {
                listener.onDialogConfirm();
            }
        });
    }

    protected void dismissDialog() {
        if (alertDialog != null) {
            alertDialog.dismiss();
            alertDialog = null;
        }
    }


    public void showConfirmDialog(final String text, final OnDialogListener listener) {
        if (tid == Process.myTid()) {
            if (alertDialog == null) {
//                alertDialog = new MaterialDialog(BaseActivity.this);
//                alertDialog.setTitle(getString(R.string.base_activity_diglog_tip))
//                        .setMessage(text)
//                        .setPositiveButton(getString(R.string.base_activity_diglog_confirm), new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                dismissDialog();
//                                listener.onSure();
//                            }
//                        }).setNegativeButton(getString(R.string.base_activity_diglog_cancel), new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dismissDialog();
//                        listener.onCancel();
//                    }
//                }).show();
                NormalDialog.Builder builder = new NormalDialog.Builder(BaseActivity.this).setMessage(text).setPositiveButton(R.drawable.dialog_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismissDialog();
                        if (listener!=null){
                            listener.onSure();
                        }
                    }
                }).setNegativeButton(R.drawable.dialog_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismissDialog();
                        if (listener!=null){
                            listener.onCancel();
                        }
                    }
                });
                builder.setTitle(getResources().getString(R.string.message));
                alertDialog = builder.create();
                alertDialog.show();
            }
        } else {
            post(new Runnable() {
                @Override
                public void run() {
                    if (alertDialog == null) {
//                        alertDialog = new MaterialDialog(BaseActivity.this);
//                        alertDialog.setTitle(getString(R.string.base_activity_diglog_tip))
//                                .setMessage(text)
//                                .setPositiveButton(getString(R.string.base_activity_diglog_confirm), new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        dismissDialog();
//                                        listener.onSure();
//                                    }
//                                }).setNegativeButton(getString(R.string.base_activity_diglog_cancel), new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                dismissDialog();
//                                listener.onSure();
//                            }
//                        }).show();
                        NormalDialog.Builder builder = new NormalDialog.Builder(BaseActivity.this).setMessage(text).setPositiveButton(R.drawable.dialog_yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dismissDialog();
                                if (listener!=null){
                                    listener.onSure();
                                }
                            }
                        }).setNegativeButton(R.drawable.dialog_no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dismissDialog();
                                if (listener!=null){
                                    listener.onCancel();
                                }
                            }
                        });
                        builder.setTitle(getResources().getString(R.string.message));
                        alertDialog = builder.create();
                        alertDialog.show();
                    }
                }
            });
        }
    }

    public void showConfirmDialog(final String text, final int btnDrableId, final OnDialogListener listener) {
        if (tid == Process.myTid()) {
//            if (alertDialog == null) {
//                alertDialog = new MaterialDialog(BaseActivity.this).setMessage(text).setNegativeButton(btntext, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dismissDialog();
//                        if (listener != null) {
//                            listener.onCancel();
//                        }
//                    }
//                });
//                alertDialog.show();
//            }
            NormalDialog.Builder builder = new NormalDialog.Builder(this).setMessage(text).setPositiveButton(btnDrableId, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dismissDialog();
                    if (listener!=null){
                        listener.onCancel();
                    }
                }
            });
            builder.setTitle(getResources().getString(R.string.message));
            alertDialog = builder.create();
            alertDialog.show();
        } else {
            post(new Runnable() {
                @Override
                public void run() {
                    NormalDialog.Builder builder = new NormalDialog.Builder(BaseActivity.this).setMessage(text).setPositiveButton(R.drawable.dialog_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dismissDialog();
                            if (listener!=null){
                                listener.onCancel();
                            }
                        }
                    });
                    builder.setTitle(getResources().getString(R.string.message));
                    alertDialog = builder.create();
                    alertDialog.show();
                }
            });
        }
    }

    public void showConfirmDialog(final String text) {
        showConfirmDialog(text, R.drawable.dialog_no, null);
    }

    public void showToastSafe(final String text) {
        if (text == null)
            return;
        if ((System.currentTimeMillis() - lastToastTime) < TOAST_INTERNAL && text.equals(lastToastText))
            return;
        if (tid == Process.myTid()) {
            Toast.makeText(BaseActivity.this, text, Toast.LENGTH_SHORT).show();
        } else {
            post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(BaseActivity.this, text, Toast.LENGTH_SHORT).show();
                }
            });
        }
        lastToastTime = System.currentTimeMillis();
        lastToastText = text;
    }


    public void showToastSafe(int id) {
        showToastSafe(getString(id));
    }

    protected void showLoading() {
        showLoading(getString(R.string.base_activity_loading_text));
    }

    public void showLoading(final String tip) {
        if (tid == Process.myTid()) {
            if (isValidContext(BaseActivity.this)) {
                if (loadingViewDialog == null) {
                    loadingViewDialog = LoadingDiglog.createLoadingDialog(this, tip);
                    loadingViewDialog.setCanceledOnTouchOutside(false);
                }
                loadingViewDialog.show();
            }
        } else {
            post(new Runnable() {
                @Override
                public void run() {
                    if (isValidContext(BaseActivity.this)) {
                        if (loadingViewDialog == null) {
                            loadingViewDialog = LoadingDiglog.createLoadingDialog(BaseActivity.this, tip);
                            loadingViewDialog.setCanceledOnTouchOutside(false);
                        }
                        loadingViewDialog.show();
                    }
                }
            });
        }
    }

    public void hideLoading() {
        if (tid == Process.myTid()) {
            if (isValidContext(BaseActivity.this)) {
                if (loadingViewDialog != null && loadingViewDialog.isShowing()) {
                    loadingViewDialog.hide();
                }
            }
        } else {
            post(new Runnable() {
                @Override
                public void run() {
                    if (isValidContext(BaseActivity.this)) {
                        if (loadingViewDialog != null && loadingViewDialog.isShowing()) {
                            loadingViewDialog.hide();
                        }
                    }
                }
            });
        }
    }

    protected boolean isValidContext(Activity a) {
        if (Build.VERSION.SDK_INT >= 17) {
            if (a.isDestroyed() || a.isFinishing()) {
                return false;
            } else {
                return true;
            }
        } else {
            if (a.isFinishing()) {
                return false;
            } else {
                return true;
            }
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);

        if (loadingViewDialog != null) {
            loadingViewDialog.dismiss();
            loadingViewDialog = null;
        }
        releaseResources();
        super.onDestroy();

        unregisterReceiver(mHomeKeyReceiver);
    }

    public void releaseResources() {

    }

    public void onEventMainThread(UDPTimeOutEvent event) {

        Logger.e("BaseActivity========>  UDPTimeOutEvent ");
        ((LeautoCameraAppLication) getApplication()).resetStatus();
        HomeActivity_.intent(this).start();

    }

    public void onEventMainThread(CameraDisconnectEvent event) {

        Logger.e("BaseActivity========>  UDPTimeOutEvent ");
        ((LeautoCameraAppLication) getApplication()).resetStatus();
        HomeActivity_.intent(this).start();
    }

    /**
     * 是否是当前正在展示的activity
     */
    public boolean isCurrentActivity() {
        return isCurrentActivity;
    }

    public boolean isInMainThread() {
        return tid == Process.myTid();
    }

    public void post(Runnable r) {
        handler.post(r);
    }

    public interface OnDialogConfirmListener {
        void onDialogConfirm();
    }

    public interface OnDialogListener {
        void onSure();

        void onCancel();
    }


}
