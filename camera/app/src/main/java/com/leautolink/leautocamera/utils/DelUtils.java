package com.leautolink.leautocamera.utils;

import android.app.Activity;

import com.leautolink.leautocamera.R;
import com.leautolink.leautocamera.callback.DelFileCallBack;
import com.leautolink.leautocamera.callback.SystemDialogCallBack;
import com.letv.leauto.cameracmdlibrary.connect.RemoteCamHelper;
import com.letv.leauto.cameracmdlibrary.connect.model.CameraMessage;
import com.letv.leauto.cameracmdlibrary.connect.model.CameraMessageCallback;
import com.letv.leauto.cameracmdlibrary.connect.model.CommandID;

import org.json.JSONObject;

import java.io.File;

/**
 * 删除文件的助手
 * Created by tianwei1 on 2016/3/10.
 */
public class DelUtils {
    private static final String TAG = "DelUtils";

    public static void deleteCameraSingleFile(final Activity activity, final String path, final String title, final String message, final String ptbtntext, final String ngbtntext, final DelFileCallBack callback) {
        SystemDialogUtils.showConfirmDialog(activity, title, message, ptbtntext, ngbtntext, new SystemDialogCallBack() {
            @Override
            public void onSure() {
                //去删除
                SystemDialogUtils.dismissConfirmDialog(activity);
                SystemDialogUtils.showProgresssDialog(activity, activity.getResources().getString(R.string.deleteing));
                deleteCameraSingle(activity, path, callback);
            }

            @Override
            public void onCancel() {
                SystemDialogUtils.dismissConfirmDialog(activity);
            }
        });
    }

    public static void deleteCameraSingle(final Activity activity, String path, final DelFileCallBack callback) {
        Logger.i(TAG, "deleteCameraSingle path:" + path);
        //可以删除
        CameraMessage deleteMessage = new CameraMessage(CommandID.AMBA_DEL, new CameraMessageCallback() {
            @Override
            public void onReceiveErrorMessage(CameraMessage cameraMessage, JSONObject jsonObject) {
                SystemDialogUtils.dismissProgressDialog(activity);
                callback.onFailure();
                Logger.e(TAG, "onReceiveErrorMessage+删除失败：" + jsonObject.toString());
            }

            @Override
            public void onReceiveMessage(CameraMessage cameraMessage, JSONObject jsonObject) {
                SystemDialogUtils.dismissProgressDialog(activity);
                callback.onSucceed();
                Logger.e(TAG, "onReceiveMessage+删除成功：" + jsonObject.toString());
            }

            @Override
            public void onReceiveNotification(JSONObject jsonObject) {
            }
        });

        deleteMessage.put("param", path);

        RemoteCamHelper.getRemoteCam().sendCommand(deleteMessage);
    }

    /**
     * 删除单个本地文件
     *
     * @param activity
     * @param path
     * @param title
     * @param message
     * @param ptbtntext
     * @param ngbtntext
     * @param callback
     */
    public static void deleteLocalSingleFile(final Activity activity, final String path, final String title, final String message, final String ptbtntext, final String ngbtntext, final DelFileCallBack callback) {

        SystemDialogUtils.showConfirmDialog(activity, title, message, ptbtntext, ngbtntext, new SystemDialogCallBack() {
            @Override
            public void onSure() {
                //去删除
                SystemDialogUtils.dismissConfirmDialog(activity);
                SystemDialogUtils.showProgresssDialog(activity, activity.getResources().getString(R.string.deleteing));
                deleteLocalSingle(activity, path, callback);
            }

            @Override
            public void onCancel() {
                SystemDialogUtils.dismissConfirmDialog(activity);
            }
        });
    }

    public static void deleteLocalSingle(final Activity activity, final String path, final DelFileCallBack callback) {
        Logger.i(TAG, "deleteLocalSingle path:" + path);
        new Thread(new Runnable() {
            @Override
            public void run() {
                File file = new File(path);
                if (file.exists()) {
                    file.delete();
                    if (!file.exists()) {
                        Logger.e(TAG, "onSucceed+删除成功：" + path);
                        callback.onSucceed();
                    } else {
                        Logger.e(TAG, "onReceiveErrorMessage+删除失败：" + file.getAbsolutePath());
                        callback.onFailure();
                    }
                } else {
                    Logger.i(TAG, "deleteLocalSingle 文件不存在");
                }
            }
        }).start();
    }
}
