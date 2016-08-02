package com.leautolink.leautocamera.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;

import com.leautolink.leautocamera.R;
import com.leautolink.leautocamera.application.LeautoCameraAppLication;
import com.leautolink.leautocamera.domain.DeviceInfo;
import com.leautolink.leautocamera.net.http.CacheUtils;
import com.leautolink.leautocamera.net.http.DownLoaderTask;
import com.leautolink.leautocamera.net.http.LeSignature;
import com.leautolink.leautocamera.net.http.OkHttpRequest;
import com.leautolink.leautocamera.net.http.httpcallback.GetCallBack;
import com.leautolink.leautocamera.ui.base.IToastSafe;
import com.leautolink.leautocamera.ui.view.customview.NormalDialog;
import com.leautolink.leautocamera.ui.view.customview.NormalProgressDialog;
import com.letv.leauto.cameracmdlibrary.common.Constant;
import com.letv.leauto.cameracmdlibrary.connect.RemoteCamHelper;
import com.letv.leauto.cameracmdlibrary.connect.model.CameraMessage;
import com.letv.leauto.cameracmdlibrary.connect.model.CameraMessageCallback;
import com.letv.leauto.cameracmdlibrary.connect.model.CommandID;
import com.letv.leauto.cameracmdlibrary.connect.socket.UpLoadCallBack;
import com.letv.leauto.customuilibrary.CustomAlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import okhttp3.Call;

/**
 * Created by liushengli on 2016/4/2.
 */
public class FirmwareUtil {
    private static final String TAG = "AlarmReceiver";
    private static final int SYSTEM_BUSY = -21;
    private static final String DEFAULT_VERSION = "V5201RCN01C011002B03211S";
    private static boolean isUpLoad = false;
    private static CountDownLatch mLatch;
    private static boolean isUpLoadSuccess = false;
    //    private static final long REQUEST_INTERVAL = 1000*60*60*8;
    private static final long REQUEST_INTERVAL = 1000 * 10;
    private static final String LAST_REQUEST_TIME_FIELD = "lastCheckedTime";
    private static final String MAC_FIELD = "mac";
    private static final String VERSIONCODE_FIELD = "versionCode";
    private static final String LOCAL_VERSIONCODE_FIELD = "localVersionCode";
    private static String mDownloadedVersionCode;
    private static String mMD5String = "";

    public static void checkOtaUpdateInterval(final Context ctx) {
        Logger.i(TAG, "checkOtaUpdateInterval   -->| ");
        CacheUtils cacheUtils = CacheUtils.getInstance(ctx);
        Long time = cacheUtils.getLong(LAST_REQUEST_TIME_FIELD, 0);
        boolean isWifiConn = false;
        ConnectivityManager connManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        String extra = mWifi.getExtraInfo();
        if (mWifi != null && mWifi.isConnected() && extra != null && !extra.contains(ctx.getResources().getString(R.string.wifi_name))) {
            isWifiConn = true;
        }
        Logger.i(TAG, "versionCode   -->| " + isWifiConn);

        String versionCode = cacheUtils.getString(VERSIONCODE_FIELD, "");
        Logger.i(TAG, "versionCode   -->| " + versionCode + " : " + !versionCode.equalsIgnoreCase("") + "  :  " + ((System.currentTimeMillis() - time) > REQUEST_INTERVAL));
        if ((System.currentTimeMillis() - time) > REQUEST_INTERVAL && isWifiConn && !versionCode.equalsIgnoreCase("")) {
            Log.i(TAG, "checkOtaUpdate ");
            checkOtaUpdate(ctx, false, false);
        }
    }

    public static void checkOtaUpdate(final Context ctx, final boolean bShowMsg, final boolean bShowUI) {
        if (bShowUI && !LeautoCameraAppLication.isIsApConnectCamera()) {
            return;
        }
        if (bShowUI && !Constant.isSDCardPresent) {
            sdcardNotPressent(ctx);
            return;
        }
        HashMap<String, String> params = new HashMap<String, String>();
        bindParams(params, ctx);
        CacheUtils cacheUtils = CacheUtils.getInstance(ctx);
        Long timesTamp = System.currentTimeMillis();
        String sign = LeSignature.getSignature("ak_j7riPS3hvMxzApPjCXol",
                "sk_kIo27lGerVXS7t8KL5lN", params, timesTamp);
        String mac = cacheUtils.getString(MAC_FIELD, "");
        String versionCode = cacheUtils.getString(VERSIONCODE_FIELD, DEFAULT_VERSION);
        final String req = "http://ota.scloud.letv.com/api/v2/lecar/upgradeProfile?model=DVR1S&deviceType=lecar&_sign="
                + sign
                + "&versionCode=" + versionCode + "&_ak=ak_j7riPS3hvMxzApPjCXol&versionType=1&_time="
                + timesTamp
                /*+"&deviceId=C80E77536DDB";*/
                + "&deviceId=" + mac;

        Logger.i(TAG, "Request -->| " + req);
        OkHttpRequest.getString(TAG, req, new GetCallBack() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (bShowUI)
                    showNetErrorMsg(ctx);
            }

            @Override
            public void onResponse(Call call, final Object response) {
                final String resp = response.toString();
                Logger.i(TAG, "CheckOtaUpdate -->| " + resp);
                if (null != resp && !"".equals(resp)) {
                    getUrl(resp, ctx, bShowMsg, bShowUI);
                }
                CacheUtils cacheUtils = CacheUtils.getInstance(ctx);
                cacheUtils.putLong(LAST_REQUEST_TIME_FIELD, System.currentTimeMillis());
            }

            @Override
            public void onError(String error) {
                if (bShowUI)
                    showNetErrorMsg(ctx);
            }
        });
    }

    private static void showNetErrorMsg(final Context ctx) {
        if (!(ctx instanceof Activity))
            return;

        Activity activity = (Activity) ctx;
        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (ctx instanceof IToastSafe)
                    ((IToastSafe) ctx).showToastSafe(ctx.getString(R.string.network_failed));
            }
        });
    }

    private static void bindParams(HashMap<String, String> params, Context ctx) {
        CacheUtils cacheUtils = CacheUtils.getInstance(ctx);
        String mac = cacheUtils.getString(MAC_FIELD, "");
        String versionCode = cacheUtils.getString(VERSIONCODE_FIELD, DEFAULT_VERSION);
        params.put("deviceId", mac);
        //params.put("deviceId", "C80E77536DDB");
        params.put("deviceType", "lecar");
        params.put("model", "DVR1S");
        params.put("versionCode", versionCode);
        //params.put("versionCode", DEFAULT_VERSION);
        params.put("versionType", "1");
    }

    private static void getUrl(String resp, final Context ctx, boolean bShowMsg, final boolean bShowUI) {
        try {
            JSONObject jsonObject = new JSONObject(resp);
            int errno = jsonObject.optInt("errno");
            if (errno == 10000) {
                JSONObject data = jsonObject.optJSONObject("data");
                String pkgUrl = data.optString("pkgUrl");
                mMD5String = data.optString("pkgMd5");
                mDownloadedVersionCode = data.optString("versionCode");
                doUpdate(pkgUrl, ctx, bShowUI);
            } else if (errno == 10003) {
                if (bShowMsg) {
                    Activity activity = (Activity) ctx;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((IToastSafe) ctx).showToastSafe(ctx.getResources().getString(R.string.no_refresh));
                        }
                    });
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static boolean bUpgradingDialog = false;

    public static void checkLocalFile(final Activity activity) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (canLocalUpgrade(activity) && !bUpgradingDialog) {
                    bUpgradingDialog = true;
//                    CustomAlertDialog dialog = new CustomAlertDialog.Builder(activity).setTitle("确认").setMessage("已经下载了升级的固件，希望立即进行固件升级吗?")
//                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                    updateFirmware(activity);
//                                    bUpgradingDialog = false;
//                                }
//                            })
//                            .setNegativeButton("否", new DialogInterface.OnClickListener() {
//
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                    Log.d(TAG, "onClick 2 = " + which);
//                                    bUpgradingDialog = false;
//                                }
//                            })
//                            .create();

                    NormalDialog dialog = new NormalDialog.Builder(activity).setTitle(activity.getResources().getString(R.string.message)).setMessage(activity.getResources().getString(R.string.mechine)).setPositiveButton(R.drawable.dialog_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            updateFirmware(activity);
                            bUpgradingDialog = false;
                        }
                    }).setNegativeButton(R.drawable.dialog_no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Log.d(TAG, "onClick 2 = " + which);
                            bUpgradingDialog = false;
                        }
                    }).create();
                    dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                        @Override
                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                            bUpgradingDialog = false;
                            return false;
                        }
                    });
                    dialog.show();
                }
            }
        });
    }

    public static boolean canLocalUpgrade(Context ctx) {
        CacheUtils cacheUtils = CacheUtils.getInstance(ctx);
        String mac = cacheUtils.getString(MAC_FIELD, "");
        String localVersionCode = cacheUtils.getString(LOCAL_VERSIONCODE_FIELD, "");
        boolean versionGreater = false;
        if (DeviceInfo.getInstance().getWiFiMAC().replace(":", "").equalsIgnoreCase(mac)
                && versionGreater(localVersionCode, DeviceInfo.getInstance().getFwVer())) {
            versionGreater = true;
        }
        final String path = SdCardUtils.getSDCardRootPath(ctx) + "/AmbaSysFW.bin";
        if (new File(path).exists() && versionGreater) {
            return true;
        }
        return false;
    }

    private static boolean versionGreater(String versionDownloaded, String versionDevice) {
        if (TextUtils.isEmpty(versionDownloaded) || TextUtils.isEmpty(versionDevice)) {
            return false;
        }
        int cIndex = versionDownloaded.lastIndexOf('C');
        int bIndex = versionDownloaded.lastIndexOf('B');
        if (cIndex < 0 || bIndex < 0) {
            return false;
        }
        int majorDownloaded = Integer.parseInt(versionDownloaded.substring(cIndex + 1, bIndex));
        int minorDownloaded = Integer.parseInt(versionDownloaded.substring(bIndex + 1, bIndex + 6));
        cIndex = versionDevice.lastIndexOf('C');
        bIndex = versionDevice.lastIndexOf('B');
        if (cIndex < 0 || bIndex < 0) {
            return false;
        }
        int majorDevice = Integer.parseInt(versionDevice.substring(cIndex + 1, bIndex));
        int minorDevice = Integer.parseInt(versionDevice.substring(bIndex + 1, bIndex + 6));
        if (majorDownloaded < majorDevice) {
            return false;
        }
        if (majorDownloaded == majorDevice && minorDownloaded <= minorDevice) {
            return false;
        }
        return true;
    }

    public static void onDownloadCompleted(Context ctx, boolean bUpdate) {
        CacheUtils cacheUtils = CacheUtils.getInstance(ctx);

        if (mDownloadedVersionCode != null) {
            cacheUtils.putString(LOCAL_VERSIONCODE_FIELD, mDownloadedVersionCode);
        }

        if (bUpdate) {
            updateFirmware((Activity) ctx);
        }
    }

    public static void saveDeviceInfo(Context ctx) {
        CacheUtils cacheUtils = CacheUtils.getInstance(ctx);
        String mac = DeviceInfo.getInstance().getWiFiMAC();
        String version = DeviceInfo.getInstance().getFwVer();
        if (!TextUtils.isEmpty(mac) && !TextUtils.isEmpty(version)) {
            cacheUtils.putString(MAC_FIELD, mac.replace(":", ""));
            cacheUtils.putString(VERSIONCODE_FIELD, version);
        }
    }

    private static void updateFirmware(Activity activity) {

        if (LeautoCameraAppLication.isIsApConnectCamera()) {
            List<Pair<String, String>> list = new ArrayList<>();
            String paths[] = {SdCardUtils.getSDCardRootPath(activity) + "/AmbaBootFW.bin", SdCardUtils.getSDCardRootPath(activity) + "/AmbaSysFW.bin"};
            List<String> keys = new ArrayList<>();
            keys.add("md5.boot");
            keys.add("md5.system");

            List<String> md5s = getFileMd5(activity, keys);
            int index = 0;
            for (String path : paths) {
                if (new File(path).exists()) {
                    list.add(new Pair<>(path, md5s.get(index)));
                    index++;
                }
            }
            FirmwareUtil.upLoadFWToCamera(list, activity, RemoteCamHelper.getRemoteCam());
        } else {
            final String path = SdCardUtils.getSDCardRootPath(activity) + "/AmbaSysFW.bin";
            if (new File(path).exists()) {
                ((IToastSafe) activity).showToastSafe(activity.getResources().getString(R.string.request_link));
            }
        }
    }

    private static List<String> getFileMd5(Context ctx, List<String> keys) {
        List<String> ret = new ArrayList<>(keys.size());
        String configFile = SdCardUtils.getSDCardRootPath(ctx) + "/system/build.prop";
        File file = new File(configFile);
        if (file.exists()) {
            FileInputStream fin = null;
            try {
                fin = new FileInputStream(file);
                BufferedReader reader = new BufferedReader(new InputStreamReader(fin));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    Log.d(TAG, "config line:");
                    String[] keyVal = line.split("=");
                    if (keyVal.length == 2) {
                        int index = 0;
                        for (String key : keys) {
                            if (key.equalsIgnoreCase(keyVal[0])) {
                                ret.add(index, keyVal[1]);
                                index++;
                                //Log.d(TAG, String.format("key:%s, val:%s", keyVal[0], keyVal[1]));
                            }

                        }
                    }
                }
                reader.close();
                fin.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    private static void doUpdate(String pkgUrl, Context ctx, final boolean bShowUI) {
        CacheUtils cacheUtils = CacheUtils.getInstance(ctx);
        String localVersion = cacheUtils.getString(LOCAL_VERSIONCODE_FIELD, "");
        String path = SdCardUtils.getSDCardRootPath(ctx) + "/AmbaSysFW.bin";
        boolean localExists = false;
        if (new File(path).exists() && !localVersion.equalsIgnoreCase("") && localVersion.equalsIgnoreCase(mDownloadedVersionCode)) {
            localExists = true;
        }
        if (bShowUI && localExists) {
            if (Constant.isSDCardPresent)
                checkLocalFile((Activity) ctx);
            else {
                sdcardNotPressent(ctx);
            }
        } else if (bShowUI || !localExists) {
            path = SdCardUtils.getSDCardRootPath(ctx) + "/";
            showDownLoadDialog(path, pkgUrl, ctx, bShowUI);
            mopo(path, ctx, bShowUI);
        }
    }

    private static void sdcardNotPressent(final Context ctx) {
        if (ctx instanceof Activity) {
            final Activity activity = (Activity) ctx;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((IToastSafe) ctx).showToastSafe(activity.getResources().getString(R.string.request_sd));
                }
            });
        }
    }

    private static void showDownLoadDialog(final String path, final String pkgUrl, final Context ctx, final boolean bShowUI) {
        if (bShowUI) {
            if (ctx instanceof Activity) {
                final Activity activity = (Activity) ctx;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String msg =activity.getResources().getString(R.string.net_mess);
                        ConnectivityManager connManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                        String extra = mWifi.getExtraInfo();
                        if (mWifi != null && mWifi.isConnected() && extra != null && extra.contains(activity.getResources().getString(R.string.wifi_name))) {
                            msg = "";
                        }
                        new CustomAlertDialog.Builder(ctx).setTitle(activity.getResources().getString(R.string.yes)).setMessage(msg + activity.getResources().getString(R.string.up_info))
                                .setPositiveButton(activity.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        Log.d(TAG, "onClick 1 = " + which);
                                        doDownLoadWork(path, pkgUrl, ctx, bShowUI);
                                    }
                                }).setNegativeButton(activity.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Log.d(TAG, "onClick 2 = " + which);
                            }
                        }).create().show();
                    }
                });
            }
        } else {
            doDownLoadWork(path, pkgUrl, ctx, bShowUI);
        }
    }

    private static void doDownLoadWork(String path, String pkgUrl, Context ctx, final boolean bShowUI) {
        DownLoaderTask downLoaderTask = new DownLoaderTask(pkgUrl, path, mMD5String, ctx, bShowUI);
        downLoaderTask.execute();
    }

    private static boolean mopo(String path, Context ctx, final boolean bShowUI) {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            file(path);
            return true;
        } else if (bShowUI) {
            new CustomAlertDialog.Builder(ctx)
                    .setTitle(ctx.getResources().getString(R.string.notify))
                    .setMessage(
                            ctx.getResources().getString(R.string.error_info))
                    .setPositiveButton(ctx.getResources().getString(R.string.ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    dialog.dismiss();
                                }

                            }).create().show();
        }

        return false;
    }

    /**
     * 创建文件夹
     */
    private static void file(String path) {
        File destDir = new File(path);
        Log.d(TAG, destDir.getAbsolutePath());
        if (!destDir.exists()) {
            Log.d(TAG, "false");
            destDir.mkdirs();
        }
    }

    public static class UploadTask extends AsyncTask<Void, Integer, Long> {
        private List<Pair<String, String>> mPaths;
        private Activity mActivity;
        private RemoteCamHelper mRemoteCamHelper;

        public UploadTask(List<Pair<String, String>> paths, final Activity activity, final RemoteCamHelper remoteCamHelper) {
            mPaths = paths;
            mActivity = activity;
            mRemoteCamHelper = remoteCamHelper;
        }

        @Override
        protected Long doInBackground(Void... params) {
            final String lastPath = mPaths.get(mPaths.size() - 1).first;
            for (final Pair<String, String> path : mPaths) {
                File fwPath = new File(path.first);
                if (fwPath.exists()) {
                    sendFileWithRetry(path, 5, path.first.equalsIgnoreCase(lastPath));
                }
            }
            return null;
        }

        private void sendFileWithRetry(final Pair<String, String> path, int retryTime, final boolean reboot) {
            for (int i = 0; i < retryTime; i++) {
                mLatch = new CountDownLatch(1);
                CameraMessage setInfo = new CameraMessage(CommandID.AMBA_SET_CLINT_INFO, new CameraMessageCallback() {

                    @Override
                    public void onReceiveErrorMessage(CameraMessage cameraMessage, JSONObject jsonObject) {
                    }

                    @Override
                    public void onReceiveMessage(CameraMessage cameraMessage, JSONObject jsonObject) {
                        mActivity.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                isUpLoadSuccess = false;
                                putFileInfo(path, mActivity, mRemoteCamHelper, reboot);
                            }
                        });
                    }

                    @Override
                    public void onReceiveNotification(JSONObject jsonObject) {
                    }
                });
                setInfo.put("type", "TCP");
                setInfo.put("param", Constant.phoneIP);
                mRemoteCamHelper.sendCommand(setInfo);
                try {
                    mLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (isUpLoadSuccess) {
                    break;
                }
            }

        }

        @Override
        protected void onPostExecute(Long result) {
            isUpLoad = false;
        }
    }

    /**
     * 上传新的固件到camera
     */
    private static void upLoadFWToCamera(List<Pair<String, String>> paths, final Activity activity, final RemoteCamHelper remoteCamHelper) {

        if (!isUpLoad) {
            isUpLoad = true;
            UploadTask task = new UploadTask(paths, activity, remoteCamHelper);
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private static void putFileInfo(final Pair<String, String> path, final Activity activity, final RemoteCamHelper remoteCamHelper, final boolean bReboot) {
        Log.d(TAG, String.format("putFileInfo path:%s, md5:%s", path.first, path.second));
        remoteCamHelper.putFile(path.first, path.second, new CameraMessageCallback() {
            @Override
            public void onReceiveErrorMessage(CameraMessage paramCameraMessage, JSONObject paramJSONObject) {
                int rval = paramJSONObject.optInt("rval");
                if (rval == SYSTEM_BUSY) {
                    isUpLoadSuccess = false;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (mLatch != null) {
                        mLatch.countDown();
                    }
                } else {
                    isUpLoadSuccess = true;
                    if (mLatch != null) {
                        mLatch.countDown();
                    }
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((IToastSafe) activity).showToastSafe(activity.getResources().getString(R.string.error_info2));
                        }
                    });
                }
            }

            @Override
            public void onReceiveMessage(CameraMessage paramCameraMessage, JSONObject paramJSONObject) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updatePW(path.first, activity, remoteCamHelper);
                    }
                });
            }

            @Override
            public void onReceiveNotification(JSONObject paramJSONObject) {
                try {
                    String type = paramJSONObject.getString("type");
                    if (type.equals("put_file_failed")) {
                        ((IToastSafe) activity).showToastSafe(activity.getResources().getString(R.string.upload_file_fail));
                    } else if (type.equals("put_file_complete")) {
                        ((IToastSafe) activity).showToastSafe(activity.getResources().getString(R.string.upload_file_success));
                        new File(path.first).delete();
                        if (bReboot) {
                            startUpdate(path.first, activity, remoteCamHelper);
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
//                                    new CustomAlertDialog.Builder(activity).setTitle("通知").setMessage("行车记录仪正在重启，估计需要几分钟，请听到“开始录像”的语音后，请重新进行连接。")
//                                            .setPositiveButton("退出", new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialog, int which) {
//                                                    dialog.dismiss();
//                                                }
//                                            }).create().show();

                                    new NormalDialog.Builder(activity).setTitle(activity.getResources().getString(R.string.notify_title)).setMessage(activity.getResources().getString(R.string.waitforstart))
                                            .setPositiveButton(R.drawable.dialog_yes, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    WifiAdminV2 adminV2 = new WifiAdminV2(activity.getApplicationContext());
                                                    adminV2.forget(adminV2.getSSID());
                                                    dialog.dismiss();
                                                }
                                            }).create().show();

                                }
                            });
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                isUpLoadSuccess = true;
                if (mLatch != null) {
                    mLatch.countDown();
                }
            }
        });
    }

    private static void updatePW(final String path, final Activity activity, final RemoteCamHelper remoteCamHelper) {
        remoteCamHelper.upLoadPW(path, new UpLoadCallBack() {
            @Override
            public void onStart() {
                String filename = path.substring(path.lastIndexOf("/") + 1);
                showProgressDialog(activity, String.format(activity.getResources().getString(R.string.upload_to_device), filename), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isUpLoad = false;
                        dialog.dismiss();
                        cancelPutFile(remoteCamHelper);
                        isUpLoadSuccess = true;
                        if (mLatch != null) {
                            mLatch.countDown();
                        }
                    }
                });
            }

            @Override
            public void onEnd() {
                isUpLoad = false;
                dismissProgress(activity);
            }

            @Override
            public void onFailure() {
                isUpLoad = false;
                dismissProgress(activity);
                cancelPutFile(remoteCamHelper);
                ((IToastSafe) activity).showToastSafe(activity.getResources().getString(R.string.error_info3));
            }

            @Override
            public void onProgress(int paramInt) {
                updateProgress(activity, paramInt);
            }
        });
    }

    private static void cancelPutFile(RemoteCamHelper remoteCamHelper) {
        isUpLoad = false;
        remoteCamHelper.cancelPutFile();
    }

    private static void startUpdate(String path, final Activity activity, final RemoteCamHelper remoteCamHelper) {
        CameraMessage updateFw = new CameraMessage(CommandID.AMBA_BURN_FW, new CameraMessageCallback() {
            @Override
            public void onReceiveErrorMessage(CameraMessage cameraMessage, JSONObject jsonObject) {

            }

            @Override
            public void onReceiveMessage(CameraMessage cameraMessage, JSONObject jsonObject) {

            }

            @Override
            public void onReceiveNotification(JSONObject jsonObject) {
                try {
                    String type = jsonObject.getString("type");
                    if (type.equals("fw_upgrade_complete")) {
                        ((IToastSafe) activity).showToastSafe(activity.getResources().getString(R.string.update_success));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        String filename = path.substring(path.lastIndexOf("/") + 1);
        updateFw.put("param", Constant.EDRROOTFWPATH + filename);
        remoteCamHelper.sendCommand(updateFw);
//        CacheUtils cacheUtils = CacheUtils.getInstance(activity);
//        cacheUtils.putString(LOCAL_VERSIONCODE_FIELD, "");

    }

//    private static CustomProgressDialog updateFwProgress;
    private static NormalProgressDialog updateFwProgress;

    private static void updateProgress(Activity activity, final int curr) {
        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                updateFwProgress.setProgress(curr);
            }
        });

    }

    private static void dismissProgress(Activity activity) {
        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                updateFwProgress.dismiss();
            }
        });
    }

    private static void showProgressDialog(final Activity activity, final String msg,
                                           final DialogInterface.OnClickListener listener) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (updateFwProgress != null) {
                    updateFwProgress.dismiss();
                }
//                CustomProgressDialog.Builder customBuilder = new CustomProgressDialog.Builder(activity);
//                updateFwProgress = customBuilder.setMax(100)
//                        .setIndeterminate(false)
//                        .setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
//                        .setTitle("消息")
//                        .setMessage(msg)
//                        .setNegativeButton("取消", listener).create();
//                updateFwProgress.show();

                updateFwProgress = new NormalProgressDialog(activity,listener).setMax(100).setTipMessage(msg);
                updateFwProgress.show();




//                updateFwProgress = new ProgressDialog(activity);
//                updateFwProgress.setTitle(title);
//                updateFwProgress.setMax(100);
//                updateFwProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//                updateFwProgress.setCancelable(false);
//                updateFwProgress.setButton(DialogInterface.BUTTON_NEGATIVE,
//                        "取消", listener);
//                updateFwProgress.show();
            }
        });

    }
}
