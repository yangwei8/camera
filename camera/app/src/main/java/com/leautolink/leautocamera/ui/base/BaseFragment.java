package com.leautolink.leautocamera.ui.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.leautolink.leautocamera.R;
import com.leautolink.leautocamera.event.BaseEvent;
import com.leautolink.leautocamera.ui.view.customview.LoadingDiglog;
import com.leautolink.leautocamera.utils.Logger;
import com.leautolink.leautocamera.utils.StatisticsUtil;

import de.greenrobot.event.EventBus;

/**
 * Created by tianwei1 on 2016/2/25.
 */
public abstract class BaseFragment extends Fragment {
    private static final String TAG = "BaseFragment";

    protected Activity mActivity;
    private Dialog loadingViewDialog;
    protected Handler handler_;
    protected LayoutInflater inflater;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
        inflater = LayoutInflater.from(activity);
        Logger.i(TAG, "onAttach");
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Logger.i(TAG, "onActivityCreated");

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Logger.i(TAG, "setUserVisibleHint");
        onUserVisibleHint(isVisibleToUser);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        handler_ = new Handler(Looper.getMainLooper());
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.e(TAG,"onResume");
        StatisticsUtil.getInstance().recordActivityStart(this.getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        StatisticsUtil.getInstance().recordActivityEnd(this.getClass().getSimpleName());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroy() {

        releaseResources();
        super.onDestroy();
        Logger.i(TAG, "onDestroy");
    }


    /**
     * 释放资源
     */
    public abstract void releaseResources();


    protected void onUserVisibleHint(boolean isVisibleToUser) {

    }


    public void onEvent(BaseEvent event) {

    }

    /*******************************
     * 加载数据和界面跳转动画
     ************************************/
    protected void showLoading(final String tip) {
        handler_.post(new Runnable() {
            @Override
            public void run() {
                if (isValidContext(mActivity)) {
                    if (isValidContext(mActivity)) {
                        if (loadingViewDialog == null) {
                            loadingViewDialog = LoadingDiglog.createLoadingDialog(mActivity, tip);
                            loadingViewDialog.setCanceledOnTouchOutside(false);
                        }
                        loadingViewDialog.setCancelable(true);
                        loadingViewDialog.show();
                    }
                }
            }
        });
    }

    /*******************************
     * 加载数据和界面跳转动画
     ************************************/
    protected void showLoadingIgnoreKeyBack(final String tip) {
        handler_.post(new Runnable() {
            @Override
            public void run() {
                if (isValidContext(mActivity)) {
                    if (isValidContext(mActivity)) {
                        if (loadingViewDialog == null) {
                            loadingViewDialog = LoadingDiglog.createLoadingDialog(mActivity, tip);
                            loadingViewDialog.setCanceledOnTouchOutside(false);
                        }
                        loadingViewDialog.setCancelable(false);
                        loadingViewDialog.show();
                    }
                }
            }
        });
    }

    protected void showLoading() {
        showLoading(getResources().getString(R.string.nowloading));
    }

    public void showToastSafe(final String text) {
        handler_.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mActivity, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void hideLoading() {
        handler_.post(new Runnable() {
            @Override
            public void run() {
                if (isValidContext(mActivity)) {
                    if (loadingViewDialog != null && loadingViewDialog.isShowing()) {
                        loadingViewDialog.hide();
                    }
                }
            }
        });
    }

    public void showToastSafe(int id) {
        showToastSafe(getString(id));
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
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
}
