package com.leautolink.leautocamera.application;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;

import com.leautolink.leautocamera.config.Constant;
import com.leautolink.leautocamera.connect.MyServer;
import com.leautolink.leautocamera.event.CameraDisconnectEvent;
import com.leautolink.leautocamera.event.UDPTimeOutEvent;
import com.leautolink.leautocamera.net.http.GsonUtils;
import com.leautolink.leautocamera.net.http.OkHttpRequest;
import com.leautolink.leautocamera.net.http.httpcallback.DownLoadCallBack;
import com.leautolink.leautocamera.services.CheckUpdateService;
import com.leautolink.leautocamera.ui.view.scaleview.ScaleCalculator;
import com.leautolink.leautocamera.utils.FileUtils;
import com.leautolink.leautocamera.utils.Logger;
import com.leautolink.leautocamera.utils.SpUtils;
import com.leautolink.leautocamera.utils.StatisticsUtil;
import com.leautolink.leautocamera.utils.WifiAdmin;
import com.leautolink.leautocamera.utils.WifiAdminV2;
import com.leautolink.leautocamera.utils.mediaplayermanager.MedIaPlayerManager;
import com.letv.leauto.cameracmdlibrary.common.Config;
import com.letv.leauto.cameracmdlibrary.connect.RemoteCamHelper;
import com.letv.leauto.cameracmdlibrary.connect.event.ConnectToCameraEvent;
import com.letv.leauto.cameracmdlibrary.connect.event.EventBusHelper;
import com.letv.leauto.cameracmdlibrary.connect.event.NotificationEvent;
import com.letv.leauto.cameracmdlibrary.utils.SystemUtils;
import com.letv.loginsdk.LetvLoginSdkManager;
import com.letvcloud.cmf.utils.DeviceUtils;

import org.androidannotations.api.BackgroundExecutor;

import java.io.IOException;
import java.util.Stack;

import de.greenrobot.event.EventBus;
import okhttp3.Call;

//import com.letv.leauto.umengsdklibrary.UmengSDK;

/**
 * Created by tianwei1 on 2016/2/25.
 */
public class LeautoCameraAppLication extends MultiDexApplication {
    private static final String TAG = "LeautoCameraAppLication";

    /*****************************集团登录SDK*********************************/
    /**
     * 平台标示,测试使用,发布时要换成自己的plat
     */
    public String platName = "car_recorder";
    /**
     * 从QQ开发平台上申请到的APP ID和APP KEY
     */
    public static String QQ_APP_ID = "1105570442";
    //    public static String QQ_APP_ID = "1105168737";
    public static String QQ_APP_KEY = "HFqCglKTA12QZ1EB";
//    public static String QQ_APP_KEY = "mq9Uq2NBVStG42xL";
    /**
     * 从新浪开发平台上申请到的App Key和应用的回调页
     */
    public static String SINA_APP_KEY = "3060325120";
    public static String REDIRECT_URL = "http://dashcam.leautolink.com/";// 应用的回调页
    /**
     * 从微信开发平台上申请到的APP ID和AppSecret
     */
//    public static String WX_APP_ID = "wx05009e5a71c72eba";
    public static String WX_APP_ID = "wxbc72885d8aea8007";
    //    public static String WX_APP_SECRET = "1e02922db9a922ea6b30332376d555bd";
    public static String WX_APP_SECRET = "c3a1865d7acc65225485f26b818c97b8";

    /*****************************集团登录SDK*********************************/

    /**
     * 是否需要切换ap
     */
    public static final boolean ISNEEDSWITCH = false;

    public static boolean CheckUDPServiceIsOk = false;

    /**
     * 记录仪连接成功
     */
    public static boolean isConnectCamera = false;
    /**
     * 手机切换ap完成，重新连接到记录仪成功
     */
    public static boolean isApConnectCamera = false;

    /**
     * 监听记录仪广播发出来的信息，用来获取记录仪连接上手机热点后，获取记录仪的ip信息
     */
    public static MyServer myServer;

    public static boolean sCmfInitSuccess;
    /**
     * 存放Activity的堆栈
     */
    private Stack<Activity> mActivityStack = mActivityStack = new Stack<>();


    private BroadcastReceiver receiver = new WifiStatusReceiver();
    private WifiAdminV2 wifiAdmin;


    @Override
    public void onCreate() {
        super.onCreate();

        startService(new Intent(this, CheckUpdateService.class));
        EventBus.getDefault().register(this);
        //初始化Gson
        GsonUtils.newInstance();
        //获取OkHttpClient实例
        OkHttpRequest.newInstance(this);

        //初始化集团登录SDK
        LetvLoginSdkManager.initSDK(this, platName, true, true, true, true, true, true);
        new LetvLoginSdkManager().initThirdLogin(QQ_APP_ID, QQ_APP_KEY, SINA_APP_KEY, REDIRECT_URL, WX_APP_ID, WX_APP_SECRET);
        new LetvLoginSdkManager().showPersonInfo(true);
//        UmengSDK.init(this);
        ScaleCalculator.init(this.getApplicationContext());

        StatisticsUtil.getInstance().init(this.getApplicationContext());
        StatisticsUtil.getInstance().recordAppStart();

        MedIaPlayerManager.init(this, MedIaPlayerManager.PLAYER_TYPE_CLOUD);

//        RemoteCamHelper.getRemoteCam().startSession();


        boolean isMainProcess = this.getPackageName().equals(DeviceUtils.getProcessName(this, android.os.Process.myPid()));
        if (isMainProcess) {
            wifiAdmin = new WifiAdminV2(getApplicationContext());
//            IntentFilter filter = new IntentFilter();
//            filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
//            filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
//            registerReceiver(receiver, filter);
            String ssid = SpUtils.getInstance(this).getStringValue(Constant.WIFI_SSID);
            if (!TextUtils.isEmpty(ssid)) {
                wifiAdmin.forget(ssid);
            }
            BackgroundExecutor.execute(new BackgroundExecutor.Task("", 0, "") {
                                           @Override
                                           public void execute() {
                                               try {
                                                   FileUtils.copyAssetsFileToSD(LeautoCameraAppLication.this, "instruction.html", "instructiontemp.html", getFilesDir().getAbsolutePath());
                                               } catch (Throwable e) {
                                                   Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
                                               }
                                           }

                                       }
            );
            downloadHtml();
        }


    }




    private void downloadHtml() {
        OkHttpRequest.downLoad("downhtml", "http://dashcam.leautolink.com/instruction.html", getFilesDir().getAbsolutePath(), "instructiontemp.html", SystemUtils.getSDAvailableSize(), new DownLoadCallBack() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onStart(long total) {

            }

            @Override
            public void onLoading(long current, long total) {

            }

            @Override
            public void onSucceed() {
            }

            @Override
            public void onSdCardLackMemory(long total, long avaiable) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(IOException e) {

            }
        });
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        EventBus.getDefault().unregister(this);
    }

    private class WifiStatusReceiver extends BroadcastReceiver {
        private WifiInfo wifiInfo;
        private NetworkInfo.DetailedState detailedState;

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                wifiInfo = (WifiInfo) intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
                detailedState = info.getDetailedState();
                if (detailedState == NetworkInfo.DetailedState.DISCONNECTED) {
                    Logger.e("=====>>Network DISCONNECTED!<<=====");
                    if (LeautoCameraAppLication.isConnectCamera) {
                        Constant.WIFI_NAME = "";
                        LeautoCameraAppLication.isConnectCamera = false;
                        RemoteCamHelper.getRemoteCam().closeChannel();
                        EventBusHelper.postConnectToCamera(new ConnectToCameraEvent(false));
                    }
                } else if (detailedState == NetworkInfo.DetailedState.CONNECTED) {
                    Logger.e("=====>>Network CONNECTED!<<=====");
                    boolean isConnectLeCamera = WifiAdmin.isConnectCamera(getApplicationContext(),wifiInfo);
                    if (isConnectLeCamera && !LeautoCameraAppLication.isConnectCamera) {
                        Constant.WIFI_NAME = wifiInfo.getSSID().replace("\"", "");
                        isConnectCamera = true;
                        SpUtils.getInstance(LeautoCameraAppLication.this).setValue(Constant.WIFI_SSID, Constant.WIFI_NAME);

                        RemoteCamHelper.getRemoteCam().startSession();
                        Logger.e("Save wifi ssid start socket session!");
                    }
//                    else {
//                        if (isConnectCamera) {
//                            Logger.e("EventBusHelper.postConnectToCamera(new ConnectToCameraEvent(false));");
//                            Constant.WIFI_NAME = "";
//                            isConnectCamera = false;
//                            RemoteCamHelper.getRemoteCam().closeChannel();
//                            EventBusHelper.postConnectToCamera(new ConnectToCameraEvent(false));
//                        }
//                    }
                }
            }
        }
    }

    public static boolean isConnectCamera() {
        return isConnectCamera;
    }

    public static boolean isIsApConnectCamera() {
        return isApConnectCamera;
    }

    public void onEventMainThread(ConnectToCameraEvent event) {

        isConnectCamera = event.isConnectCamera();
        if (!ISNEEDSWITCH) {
            isApConnectCamera = isConnectCamera;
        }
        if (!isConnectCamera()) {
            Logger.e(" ConnectToCameraEvent  In  Application  is : " + isConnectCamera());
            EventBus.getDefault().post(new CameraDisconnectEvent());
        }
        //FIXME  不切换ap需要添加该功能

    }

    public void onEventMainThread(NotificationEvent event) {
        if (event.getType() == NotificationEvent.SD_INSERTED) {
            com.letv.leauto.cameracmdlibrary.common.Constant.isSDCardPresent = true;
        } else if (event.getType() == NotificationEvent.SD_REMOVED) {
            com.letv.leauto.cameracmdlibrary.common.Constant.isSDCardPresent = false;
        }
    }

    public void onEventMainThread(UDPTimeOutEvent event) {
        if (LeautoCameraAppLication.isApConnectCamera) {
            resetStatus();
        }
    }


    public void resetStatus() {
        this.isApConnectCamera = false;
        this.isConnectCamera = false;
//        WifiApAdmin.closeWifiAp(this);
        Config.CAMERA_IP = "192.168.42.1";
        RemoteCamHelper.getRemoteCam().setWifiIP(Config.CAMERA_IP, Config.CMD_PORT_NUM, Config.DATA_PORT_NUM);
    }

    public void pushActivity(Activity activity) {
        if (mActivityStack != null && activity != null) {
            mActivityStack.add(activity);
        }
    }

    public void popActivity(Activity activity) {
        if (mActivityStack != null && activity != null) {
            activity.finish();
            mActivityStack.remove(activity);
            activity = null;
        }
    }

    public Activity getCurrentActivity() {
        Activity activity = null;
        if (mActivityStack != null) {
            activity = mActivityStack.lastElement();
        }
        return activity;
    }

    public void popAllActivitys() {
        if (mActivityStack != null && mActivityStack.size() > 0) {
            int size = mActivityStack.size();
            for (int i = 0; i < size; i++) {
                popActivity(mActivityStack.lastElement());
            }
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}
