package com.leautolink.leautocamera.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.leautolink.leautocamera.R;
import com.leautolink.leautocamera.callback.CustomDialogCallBack;
import com.leautolink.leautocamera.ui.view.customview.UnTouchableSeekBar;


/**
 * 自定义Dialog工具类
 * Created by tianwei1 on 2015/12/10.
 */
public class CustomDialogUtils {
    private static Dialog mDialog;
    private static View mView;
    private static Activity mActivity;
    private static TextView mTvMsg;
    private static TextView mTvPercentage;

    private static TextView mTvCurrentTotal;
    private static UnTouchableSeekBar mSbProgress;
    private static Button mBtnCancel;

    private static CustomDialogCallBack mCallBack;

    /**
     * 显示Dialog
     *
     * @param activity
     */
    public static void showDialog(Activity activity, CustomDialogCallBack callback) {
        if (mDialog == null) {
            mActivity = activity;
            mCallBack = callback;
            initView();
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mDialog = new AlertDialog.Builder(mActivity)
                            .setView(mView)
                            .setCancelable(false)
                            .show();
                }
            });
        }
    }


    private static void initView() {
        mView = View.inflate(mActivity, R.layout.dialog_download, null);
        mTvMsg = (TextView) mView.findViewById(R.id.tv_dialog_batch_download_msg);
        mTvPercentage = (TextView) mView.findViewById(R.id.tv_dialog_batch_download_percentage);
        mTvCurrentTotal = (TextView) mView.findViewById(R.id.tv_dialog_batch_download_current_total);
        mSbProgress = (UnTouchableSeekBar) mView.findViewById(R.id.utsb_dialog_batch_download_progress);
        mBtnCancel = (Button) mView.findViewById(R.id.btn_dialog_batch_download_cancel);
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallBack != null)
                    hideCustomDialog();
                mCallBack.onCancel();
            }
        });
    }

    public static void setMsg(Activity activity, final String msg) {
        mActivity=activity;
        initView();
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTvMsg.setText(msg);
            }
        });
    }

    public static void setPercentage(final String percentage) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTvPercentage.setText(percentage);
            }
        });
    }

    public static void setCurrentTotal(final String currentTotal) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTvCurrentTotal.setText(currentTotal);
            }
        });
    }

    public static void setSeekBarMax(final int max) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSbProgress.setMax(max);
            }
        });
    }

    public static void setProgress(final int current) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSbProgress.setProgress(current);
            }
        });
    }

    public static void hideCustomDialog() {
        if (mDialog != null) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mDialog.dismiss();
                    mDialog = null;
                }
            });
        }
    }
}
