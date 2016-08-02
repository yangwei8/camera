package com.leautolink.leautocamera.utils;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;

import com.leautolink.leautocamera.callback.SystemDialogCallBack;
import com.leautolink.leautocamera.ui.view.customview.LoadingDiglog;
import com.leautolink.leautocamera.ui.view.customview.MaterialDialog;

/**
 * 系统弹框的工具类
 * Created by tianwei1 on 2016/3/10.
 */
public class SystemDialogUtils {

    private static MaterialDialog alertDialog;
    private static Dialog loadingDialog;

    /**
     * 只有单个确认按钮的Dialog
     *
     * @param activity
     * @param title
     * @param message
     * @param btntext
     * @param callBack
     */
    public static void showSingleConfirmDialog(final Activity activity, final String title, final String message, final String btntext, final SystemDialogCallBack callBack) {
        if (alertDialog == null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    alertDialog = new MaterialDialog(activity).setTitle(title).setMessage(message).setCanceledOnTouchOutside(false).setNegativeButton(btntext, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dismissConfirmDialog(activity);
                            if (callBack != null) {
                                callBack.onCancel();
                            }
                        }
                    });
                    alertDialog.show();
                }
            });
        }
    }

    /**
     * 确认Dialog
     *
     * @param activity
     * @param title
     * @param message
     * @param ptbtntext
     * @param ngbtntext
     * @param callBack
     */
    public static void showConfirmDialog(final Activity activity, final String title, final String message, final String ptbtntext, final String ngbtntext, final SystemDialogCallBack callBack) {
        if (alertDialog == null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    alertDialog = new MaterialDialog(activity).setTitle(title).setMessage(message).setCanceledOnTouchOutside(false).setNegativeButton(ngbtntext, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dismissConfirmDialog(activity);
                            if (callBack != null) {
                                callBack.onCancel();
                            }
                        }
                    }).setPositiveButton(ptbtntext, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            callBack.onSure();
                        }
                    });
                    alertDialog.show();
                }
            });
        }
    }

    public static void showProgresssDialog(final Activity activity, final String message) {
        LoadingDiglog.showLoadingDialog(activity, message);
    }

    public static void dismissConfirmDialog(Activity activity) {
        if (alertDialog != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    alertDialog.dismiss();
                    alertDialog = null;
                }
            });
        }
    }

    public static void dismissProgressDialog(Activity activity) {
        LoadingDiglog.dismissLoadingDialog(activity);
    }

}
