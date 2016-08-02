package com.leautolink.leautocamera.ui.fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.leautolink.leautocamera.R;
import com.leautolink.leautocamera.application.LeautoCameraAppLication;
import com.leautolink.leautocamera.callback.ApConnectedSuccessCallback;
import com.leautolink.leautocamera.connect.CameraConnectionHelper;
import com.leautolink.leautocamera.connect.MyServer;
import com.leautolink.leautocamera.domain.AddFileInfo;
import com.leautolink.leautocamera.domain.DeviceInfo;
import com.leautolink.leautocamera.domain.HomePhotoInfo;
import com.leautolink.leautocamera.domain.ListingInfo;
import com.leautolink.leautocamera.domain.respone.FileRemovedInfo;
import com.leautolink.leautocamera.event.ConnectSuccessEvent;
import com.leautolink.leautocamera.event.ConnectTimeOutEvent;
import com.leautolink.leautocamera.event.ConnectionAuthenticateEvent;
import com.leautolink.leautocamera.event.UDPTimeOutEvent;
import com.leautolink.leautocamera.net.http.GsonUtils;
import com.leautolink.leautocamera.ui.activity.CameraGalleryActivity_;
import com.leautolink.leautocamera.ui.activity.DiaglogActivity;
import com.leautolink.leautocamera.ui.activity.DiaglogActivity_;
import com.leautolink.leautocamera.ui.activity.HomeActivity;
import com.leautolink.leautocamera.ui.base.BaseFragment;
import com.leautolink.leautocamera.ui.view.customview.EditTextWithDelete;
import com.leautolink.leautocamera.ui.view.customview.MyButton;
import com.leautolink.leautocamera.ui.view.customview.NormalDialog;
import com.leautolink.leautocamera.utils.AnimationUtils;
import com.leautolink.leautocamera.utils.CameraDataUtils;
import com.leautolink.leautocamera.utils.Logger;
import com.leautolink.leautocamera.utils.SpUtils;
import com.leautolink.leautocamera.utils.ValidateUtils;
import com.leautolink.leautocamera.utils.WifiAdmin;
import com.leautolink.leautocamera.utils.WifiAdminV2;
import com.leautolink.leautocamera.utils.WifiApAdmin;
import com.leautolink.leautocamera.utils.mediaplayermanager.MedIaPlayerManager;
import com.leautolink.leautocamera.utils.mediaplayermanager.interfaces.IPlayer;
import com.leautolink.leautocamera.utils.mediaplayermanager.interfaces.IPlayerListener;
import com.letv.leauto.cameracmdlibrary.common.Constant;
import com.letv.leauto.cameracmdlibrary.connect.DeviceDiscovery;
import com.letv.leauto.cameracmdlibrary.connect.RemoteCamHelper;
import com.letv.leauto.cameracmdlibrary.connect.event.AddEventFileEvent;
import com.letv.leauto.cameracmdlibrary.connect.event.AutoConnectToCameraEvent;
import com.letv.leauto.cameracmdlibrary.connect.event.ConnectToCameraEvent;
import com.letv.leauto.cameracmdlibrary.connect.event.EventOrPhotoRemovedNotificationEvent;
import com.letv.leauto.cameracmdlibrary.connect.event.NotificationEvent;
import com.letv.leauto.cameracmdlibrary.connect.model.CameraMessage;
import com.letv.leauto.cameracmdlibrary.connect.model.CameraMessageCallback;
import com.letv.leauto.cameracmdlibrary.connect.model.CommandID;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.api.BackgroundExecutor;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@EFragment(R.layout.fragment_camera)
public class CameraFragment extends BaseFragment implements IPlayerListener {
    private static final String TAG = "CameraFragmrnt";
    private static final int WIFI_SETTING_CODE = 0;
    /**
     * 引导页
     */
    @ViewById(R.id.ll_normal_page)
    LinearLayout ll_normal_page;

    /**
     * 引导页
     */
    @ViewById(R.id.rl_connect_page)
    RelativeLayout rl_connect_page;

    /**
     * 播放器的容器
     */
    @ViewById(R.id.video_layout)
    RelativeLayout video_layout;

    @ViewById(R.id.rl_container)
    RelativeLayout rl_container;

    @ViewById(R.id.rl_btn_container)
    RelativeLayout rl_btn_container;

    @ViewById(R.id.tv_new_incident)
    TextView tv_new_incident;

    @ViewById(R.id.ll_new_container)
    LinearLayout ll_new_container;
    /**
     * 转圈的动画容器
     */
    @ViewById(R.id.ll_connect_to_camera)
    LinearLayout ll_connect_to_camera;
    /**
     * 转圈的动画图片
     */
    @ViewById(R.id.iv_refresh)
    ImageView iv_refresh;

    @ViewById(R.id.iv_close_connect)
    ImageView iv_close_connect;

    @ViewById(R.id.bt_take_photo)
    MyButton bt_take_photo;

    @ViewById(R.id.bt_go_photo)
    MyButton bt_go_photo;


    @ViewById(R.id.bt_click_connect)
    Button bt_click_connect;

    @ViewById(R.id.iv_loading_icon)
    ImageView iv_loading_icon;

    @ViewById(R.id.v_circle_twinkle)
    View v_circle_twinkle;

    @ViewById(R.id.tv_find_edr)
    TextView tv_find_edr;
    @ViewById(R.id.tv_little_tip)
    TextView tv_little_tip;
    @ViewById(R.id.tv_little_tip_detail)
    TextView tv_little_tip_detail;
    @ViewById
    RelativeLayout rl_first, rl_two, rl_three;

    @ViewById(R.id.tv_connecting)
    TextView tv_connecting;

    @ViewById
    ImageView iv_first, iv_two, iv_three, iv_is_record, iv_first_icon, iv_two_icon, iv_three_icon;
    @ViewById
    TextView tv_first_text, tv_two_text, tv_three_text, tv_no_photo_video, tv_tip;
    //    private PlayController mPlayController;
    private boolean mIsFirstEnter = true;
    private boolean mIsFinish = false;
    private boolean photoIsShow;
//    private MediaPlayer mMediaPlayer;
//    private List<MediaSource> sourceList;

    private String photoName;

    /**
     * 当前Camera是否在录制视频
     */
    private String cameraStatus;
    private String filePath;


    private RemoteCamHelper remoteCamHelper;
    //    private ListingInfo mEventListingInfo;
    private ListingInfo mPhotoListingInfo;
    private List<String> filesName = new ArrayList<>();
    private List<HomePhotoInfo> homePhotoInfos = new ArrayList<>();
    private List<HomePhotoInfo> remoevdHomeInfos = new ArrayList<>();
    private String ssid = "";
    private WifiAdminV2 admin;
    //    private WifiApAdmin wifiAp;
    private String wifiName;


    private NormalDialog inputPasswordDiglog;
    private List<ScanResult> newSacn;

    //phoneIsAp是否执行
    private static boolean isPhoneIsAp = false;
    //cameraIsAp是否执行
    private static boolean isCameraIsAP = false;
    private static boolean FRAGMENTISHIDEN = false;

    private int loadingLenght;
    private Timer timer;
    private IPlayer mPlayer;

    private String liveUrl = "rtsp://" + com.letv.leauto.cameracmdlibrary.common.Config.CAMERA_IP + "/live";


    private CameraConnectionHelper cameraConnectionHelper;

    /**
     * 密码输入框
     */
    private EditTextWithDelete inputPassword_editTextWithDelete;
    /**
     * 密码提示语
     */
    private TextView inputPassword_tv_tip;
    private String tempSSID;


    @Override
    public void onResume() {
        super.onResume();
        showPhoto();
        if (LeautoCameraAppLication.isConnectCamera) {
            startReFreshAnimation();
            DeviceDiscovery.getInstance().startDiscovery();
            if (mPlayer != null) {
                mPlayer.start();
                if (TextUtils.isEmpty(cameraStatus)) {
                    getCameraStatus();
                } else {
                    initRecordedIcon();
                }
            }
        } else {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            if (mPlayer != null) {
                mPlayer.stop();
            }
        }
        Logger.e("onResume  ------------->");
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

        } else {
            //相当于Fragment的onPause
            if (mPlayer != null) {
                mPlayer.stop();
            }
        }
    }

    @AfterViews
    void init() {
        mPlayer = MedIaPlayerManager.createPlayer();
        admin = new WifiAdminV2(mActivity.getApplicationContext());
        com.leautolink.leautocamera.config.Constant.isLive = true;
        mPlayer.initPlayer(mActivity, video_layout, MedIaPlayerManager.PLAY_TYPE_LIVE);
        remoteCamHelper = RemoteCamHelper.getRemoteCam();
        mPlayer.setPlayerListener(this);
        ll_connect_to_camera.setVisibility(View.GONE);
        if (RemoteCamHelper.isStartSessionSuccess) {
            DeviceInfo.getInstance().setContext(new WeakReference<Context>(CameraFragment.this.getActivity()));
            DeviceInfo.getInstance().getDeviceInfo();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Logger.e(TAG, "onPause");
        Log.e(TAG, " <==============    onPause");

        FRAGMENTISHIDEN = true;
        hideReFreshAnimation();

        cameraStatus = "";
        initRecordedIcon();
//        mPlayer.suspend();
       /* if (this.mPlayController != null) {
            //Modify by donghongwei 20160405 for LECAMERA-402 begin
            this.mPlayController.stopPlayback();
            //Modify by donghongwei 20160405 for LECAMERA-402 begin
        }*/
        DeviceDiscovery.getInstance().stopDiscovery();
    }

    @Background
    void startDiscovery() {
        DeviceDiscovery.getInstance().startDiscovery();
    }

    public void onEventMainThread(ConnectSuccessEvent event) {
        if (timer != null) {
            timer.cancel();
            timer = null;
            loadingLenght = 99;
            tv_connecting.setVisibility(View.GONE);
        }
        isPhoneIsAp = false;
//        statrtPlay();
        DeviceInfo.getInstance().setContext(new WeakReference<Context>(CameraFragment.this.getActivity()));
        DeviceInfo.getInstance().getDeviceInfo();
        bt_take_photo.setEnabled(true);
        bt_go_photo.setEnabled(true);
    }

    public void onEventMainThread(UDPTimeOutEvent event) {
        Logger.e("CameraFragment UDPTimeOutEvent CheckUDPServiceIsOk=" + LeautoCameraAppLication.CheckUDPServiceIsOk);
        isPhoneIsAp = false;
        FRAGMENTISHIDEN = false;

        SpUtils.getInstance(mActivity.getApplicationContext()).getStringValue(com.leautolink.leautocamera.config.Constant.WIFI_SSID);

        new Thread(new Runnable() {
            @Override
            public void run() {
                admin.forget(ssid);
            }
        }).start();

        if (LeautoCameraAppLication.CheckUDPServiceIsOk) {
//            mActivity.stopService(new Intent(mActivity, CheckUDPService.class));
        }
    }


    /**
     * 初始化成功   startSession成功 ， 获取sd卡的状态成功
     *
     * @param event
     */
    public void onEventMainThread(ConnectToCameraEvent event) {
        Logger.e("onEventMainThread isConnectCamera=" + event.isConnectCamera());
        if (event.isConnectCamera() && admin.isWifiEnabled()) {
            statrtPlay();
            connectSuccess();
            startReFreshAnimation();
            DeviceInfo.getInstance().setContext(new WeakReference<Context>(CameraFragment.this.getActivity()));
            DeviceInfo.getInstance().getDeviceInfo();
            bt_take_photo.setEnabled(true);
            bt_go_photo.setEnabled(true);
            Logger.e("onEventMainThread FRAGMENTISHIDEN=" + FRAGMENTISHIDEN);
            autoDownLoadDataTask();
        } else {
            resumeConnectPage();
            if (mPlayer != null) {
//                if (mPlayer.isPlaying()){
                mPlayer.stop();
//                }
            }
        }
    }

    /**
     * 自动连接超时
     *
     * @param event
     */
    public void onEventMainThread(ConnectTimeOutEvent event) {
        resumeConnectPage();
    }

    /**
     * 密码错误
     *
     * @param event
     */
    public void onEventMainThread(ConnectionAuthenticateEvent event) {
        Logger.e("debug_wifi","密码错误 ， 认证失败");
        creatInputDialog(tempSSID,getResources().getString(R.string.passwork_incorrect));
    }

    /**
     * 获取到SD卡里的DATA数据
     */
    private void getSDCardDataFromCamera() {
        CameraDataUtils cameraDataUtils = new CameraDataUtils();
        cameraDataUtils.getSdCardDataFromCamera((HomeActivity) mActivity);
    }

    /**
     * 启动自动下载数据
     */
    public void autoDownLoadDataTask() {
        BackgroundExecutor.execute(new BackgroundExecutor.Task("", 500, "") {
                                       @Override
                                       public void execute() {
                                           try {
                                               getSDCardDataFromCamera();
                                           } catch (Throwable e) {
                                               Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
                                           }
                                       }

                                   }
        );
    }

    public void onEventMainThread(AutoConnectToCameraEvent event) {
        Logger.e("onEventMainThread isConnectCamera=" + event.isConnectCamera());
        if (event.isConnectCamera() == 0) {
//            admin.closeWifi();
        }
    }

    @Background(delay = 1000)
    void autoConnectTask() {
//        autoConnectToCamera();
    }


    @Override
    public void releaseResources() {

    }


    private void phoneIsAp() {
        ssid = admin.getSSID();
        Logger.e("PHONE IS AP   " + ssid);
        if (admin.isConnectCamera(ssid, admin.getBSSID()) || WifiApAdmin.isWifiApEnabled((WifiManager) mActivity.getSystemService(Context.WIFI_SERVICE))) {
//            Logger.e("开始切换");
////            switchAp();
            if (!isPhoneIsAp) {
                isPhoneIsAp = true;
                if (timer == null) {
                    timer = new Timer();
                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            loadingLenght++;
                            if (loadingLenght < 100) {
                                updateLoading(loadingLenght);
                            } else {
                                if (timer != null) {
                                    timer.cancel();
                                    timer = null;
                                }
                            }
                        }
                    };
                    timer.scheduleAtFixedRate(task, 0, 200);
                }
                apStatrt();
            }
        }
    }


    @UiThread
    void updateLoading(int length) {
        tv_connecting.setText(getResources().getString(R.string.link) + length + getString(R.string.signal));
        if (tv_connecting.getVisibility() == View.GONE) {
            tv_connecting.setVisibility(View.VISIBLE);
        }
    }

    private void cameraIsAp() {
        getPhotoDataFromCamera();
        handler_.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPlayer.play(liveUrl);
            }
        }, 1000L);
    }


    @Click(R.id.bt_take_photo)
    void takePhoto() {
        if (LeautoCameraAppLication.isApConnectCamera) {
            if (Constant.isSDCardPresent) {
                bt_take_photo.setEnabled(false);
                showLoadingIgnoreKeyBack(getResources().getString(R.string.passing));
                handler_.postDelayed(runnable, 30000);
                CameraMessage takePhoto = new CameraMessage(CommandID.AMBA_TAKE_PHOTO, new CameraMessageCallback() {
                    @Override
                    public void onReceiveErrorMessage(CameraMessage cameraMessage, JSONObject jsonObject) {
                        hideLoading();
                        canTakePhotoIconInMainThread();
                        showToastSafe(getResources().getString(R.string.take_photo_error));
                    }

                    @Override
                    public void onReceiveMessage(CameraMessage cameraMessage, JSONObject jsonObject) {
                        String path = jsonObject.optString("param");
                        filePath = "/tmp/SD0" + path.split(":")[1].replace("\\", "/");
                        int index = filePath.lastIndexOf("/");
                        photoName = filePath.substring(index);
                        String thumbPhoto = photoName.replace("A", "T").substring(1);
                        HomePhotoInfo homePhotoInfo = new HomePhotoInfo(thumbPhoto, HomePhotoInfo.NORMAL_PIC);
                        homePhotoInfos.add(0, homePhotoInfo);


                        canTakePhotoIconInMainThread();
                        hideLoading();
                        showPhoto();
                        handler_.removeCallbacks(runnable);

//                        CameraMessage setInfo = new CameraMessage(CommandID.AMBA_SET_CLINT_INFO, new CameraMessageCallback() {
//                            @Override
//                            public void onReceiveErrorMessage(CameraMessage cameraMessage, JSONObject jsonObject) {
//                                hideLoading();
//                                canTakePhotoIconInMainThread();
//                                showToastSafe("拍照失败");
//                            }
//
//                            @Override
//                            public void onReceiveMessage(CameraMessage cameraMessage, JSONObject jsonObject) {
//                                remoteCamHelper.getFile(filePath, new SendCommandCallback() {
//                                    @Override
//                                    public void onFail(CameraMessage cameraMessage, JSONObject jsonObject) {
//                                        canTakePhotoIconInMainThread();
//                                    }
//
//                                    @Override
//                                    public void onSuccess(CameraMessage cameraMessage, JSONObject jsonObject) {
//                                        canTakePhotoIconInMainThread();
//                                        hideLoading();
//                                        showPhoto();
//                                        handler_.removeCallbacks(runnable);
////                                if (!photoIsShow) {
////                                }
//                                    }
//                                });
//                            }
//
//                            @Override
//                            public void onReceiveNotification(JSONObject jsonObject) {
//                            }
////                        });
//                       setInfo.put("type", "TCP");
//                        setInfo.put("param", Constant.phoneIP);
//                        remoteCamHelper.sendCommand(setInfo);
                    }

                    @Override
                    public void onReceiveNotification(JSONObject jsonObject) {
                    }
                });
                remoteCamHelper.sendCommand(takePhoto);
            } else {
                showToastSafe(getResources().getString(R.string.idcard_loss));
            }
        } else {
            bt_take_photo.setEnabled(false);
            showToastSafe(mActivity.getString(R.string.phone_and_camera_not_connect));
        }
    }


    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            hideLoading();
            timeOutCancel(filePath);
        }
    };


    private void timeOutCancel(String filePath) {
        remoteCamHelper.cancelGetFile(filePath, new CameraMessageCallback() {
            @Override
            public void onReceiveErrorMessage(CameraMessage cameraMessage, JSONObject jsonObject) {
                if (jsonObject.optString("rval").equals("-13")) {
                    hideLoading();
                    canTakePhotoIconInMainThread();
                }
            }

            @Override
            public void onReceiveMessage(CameraMessage cameraMessage, JSONObject jsonObject) {
                hideLoading();
                canTakePhotoIconInMainThread();
            }

            @Override
            public void onReceiveNotification(JSONObject jsonObject) {

            }
        });
    }


    @Click(R.id.bt_go_photo)
    void goCameraGallery() {
        if (LeautoCameraAppLication.isApConnectCamera) {
            if (Constant.isSDCardPresent) {
                CameraGalleryActivity_.intent(this).start();
            } else {
                showToastSafe(getResources().getString(R.string.idcard_loss));
            }
        } else {
            showToastSafe(mActivity.getString(R.string.phone_and_camera_not_connect));
        }
    }

    @UiThread
    void showPhoto() {
        if (homePhotoInfos.size() > 0) {
            showOrHideTip(false);
        } else {
            showOrHideTip(true);
            return;
        }
        needRefreshPhoto(homePhotoInfos.size());
        if (homePhotoInfos.size() == 1) {
            HomePhotoInfo hPI1 = homePhotoInfos.get(0);
            Glide.with(this).load(hPI1.getPath())
                    .placeholder(R.drawable.img_default)
                    .crossFade()
                    .into(iv_first);
            tv_first_text.setText(hPI1.getName());
            if (hPI1.getType() == HomePhotoInfo.EVENT_PIC) {
                iv_first_icon.setVisibility(View.VISIBLE);
            } else {
                iv_first_icon.setVisibility(View.GONE);
            }


        } else if (homePhotoInfos.size() == 2) {
            HomePhotoInfo hPI1 = homePhotoInfos.get(0);
            HomePhotoInfo hPI2 = homePhotoInfos.get(1);
            Glide.with(this).load(hPI1.getPath())
                    .placeholder(R.drawable.img_default)
                    .crossFade()
                    .into(iv_first);
            Glide.with(this).load(hPI2.getPath())
                    .placeholder(R.drawable.img_default)
                    .crossFade()
                    .into(iv_two);
            tv_first_text.setText(hPI1.getName());
            tv_two_text.setText(hPI2.getName());
            if (hPI1.getType() == HomePhotoInfo.EVENT_PIC) {
                iv_first_icon.setVisibility(View.VISIBLE);
            } else {
                iv_first_icon.setVisibility(View.GONE);
            }
            if (hPI2.getType() == HomePhotoInfo.EVENT_PIC) {
                iv_two_icon.setVisibility(View.VISIBLE);
            } else {
                iv_two_icon.setVisibility(View.GONE);
            }
        } else if (homePhotoInfos.size() >= 3) {
            HomePhotoInfo hPI1 = homePhotoInfos.get(0);
            HomePhotoInfo hPI2 = homePhotoInfos.get(1);
            HomePhotoInfo hPI3 = homePhotoInfos.get(2);
            Glide.with(this).load(hPI1.getPath())
                    .placeholder(R.drawable.img_default)
                    .crossFade()
                    .into(iv_first);
            Glide.with(this).load(hPI2.getPath())
                    .placeholder(R.drawable.img_default)
                    .crossFade()
                    .into(iv_two);
            Glide.with(this).load(hPI3.getPath())
                    .placeholder(R.drawable.img_default)
                    .crossFade()
                    .into(iv_three);
            tv_first_text.setText(hPI1.getName());
            tv_two_text.setText(hPI2.getName());
            tv_three_text.setText(hPI3.getName());
            if (hPI1.getType() == HomePhotoInfo.EVENT_PIC) {
                iv_first_icon.setVisibility(View.VISIBLE);
            } else {
                iv_first_icon.setVisibility(View.GONE);
            }
            if (hPI2.getType() == HomePhotoInfo.EVENT_PIC) {
                iv_two_icon.setVisibility(View.VISIBLE);
            } else {
                iv_two_icon.setVisibility(View.GONE);
            }
            if (hPI3.getType() == HomePhotoInfo.EVENT_PIC) {
                iv_three_icon.setVisibility(View.VISIBLE);
            } else {
                iv_three_icon.setVisibility(View.GONE);
            }
        }
    }

    private void needRefreshPhoto(int size) {
        switch (size) {
            case 1:
                rl_first.setVisibility(View.VISIBLE);
                rl_two.setVisibility(View.INVISIBLE);
                rl_three.setVisibility(View.INVISIBLE);
                break;
            case 2:
                rl_first.setVisibility(View.VISIBLE);
                rl_two.setVisibility(View.VISIBLE);
                rl_three.setVisibility(View.INVISIBLE);
                break;

            case 3:
                rl_first.setVisibility(View.VISIBLE);
                rl_two.setVisibility(View.VISIBLE);
                rl_three.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void showOrHideTip(boolean isNeed) {
        if (isNeed) {
            tv_no_photo_video.setVisibility(View.VISIBLE);
            tv_tip.setVisibility(View.VISIBLE);
            ll_new_container.setVisibility(View.GONE);
        } else {
            tv_no_photo_video.setVisibility(View.GONE);
            tv_tip.setVisibility(View.GONE);
            ll_new_container.setVisibility(View.VISIBLE);
        }
    }

    public void onEventBackgroundThread(EventOrPhotoRemovedNotificationEvent event) {
        Logger.e(TAG, "删除event 或者  photo了 param :" + event.getParam());
        Logger.e(TAG, "list :" + homePhotoInfos.toString());
        if (!TextUtils.isEmpty(event.getParam())) {
            String jsonStr = event.getParam().replace("[", "").replace("]", "");
            FileRemovedInfo removedInfo = GsonUtils.fromJson(jsonStr, FileRemovedInfo.class);
            String thumbName = removedInfo.getFilename();
            if (thumbName.endsWith("A.MP4")) {
                thumbName = thumbName.replace("A.MP4", "T.JPG");
            } else if (thumbName.endsWith("A.JPG")) {
                thumbName = thumbName.replace("A.JPG", "T.JPG");
            }
            if (homePhotoInfos != null && homePhotoInfos.size() > 0) {
                for (HomePhotoInfo info : homePhotoInfos) {
                    if (info.getOrginalName().equals(thumbName)) {
                        remoevdHomeInfos.add(info);
                    }
                }
                Logger.e(TAG, "removedHomeInfos :" + remoevdHomeInfos.toString());
                homePhotoInfos.removeAll(remoevdHomeInfos);
                remoevdHomeInfos.clear();
            }
        }
    }


    public void onEventMainThread(AddEventFileEvent event) {
        Logger.e("记录仪有新文件产生了");
        if (!TextUtils.isEmpty(event.getParam())) {
            String jsonStr = event.getParam().replace("[", "").replace("]", "");
            AddFileInfo addfileInfo = GsonUtils.fromJson(jsonStr, AddFileInfo.class);
            String thumbPhoto;
            HomePhotoInfo homePhotoInfo;
            if (addfileInfo.getFilename().contains("MP4")) {
                thumbPhoto = addfileInfo.getFilename().replace("A.MP4", "T.JPG");
                homePhotoInfo = new HomePhotoInfo(thumbPhoto, HomePhotoInfo.EVENT_PIC);
            }else {
                thumbPhoto = addfileInfo.getFilename().replace("A.JPG", "T.JPG");
                homePhotoInfo = new HomePhotoInfo(thumbPhoto, HomePhotoInfo.NORMAL_PIC);
            }
            homePhotoInfos.add(0, homePhotoInfo);
            showPhoto();
        }
    }

    @Click({R.id.iv_first, R.id.iv_two, R.id.iv_three})
    void goToPhoto(View view) {
        switch (view.getId()) {
            case R.id.iv_first:
                if (homePhotoInfos.size() >= 1) {
                    goPage(homePhotoInfos.get(0));
                }
                break;
            case R.id.iv_two:
                if (homePhotoInfos.size() >= 2) {
                    goPage(homePhotoInfos.get(1));
                }
                break;
            case R.id.iv_three:
                if (homePhotoInfos.size() >= 3) {
                    goPage(homePhotoInfos.get(2));
                }
                break;
        }
    }

    private void goPage(HomePhotoInfo homePhotoInfo) {
        if (Constant.isSDCardPresent) {
            if (homePhotoInfo.getType() == HomePhotoInfo.EVENT_PIC) {
                CameraGalleryActivity_.intent(this).eventOrPhoto(HomePhotoInfo.EVENT_PIC).start();
            } else {
                CameraGalleryActivity_.intent(this).eventOrPhoto(HomePhotoInfo.NORMAL_PIC).start();
            }
        } else {
            showToastSafe(getResources().getString(R.string.idcard_loss));
        }
    }

    /**
     * 获取到当前的状态
     */
    private void getCameraStatus() {
        CameraMessage getCameraStatus = new CameraMessage(CommandID.AMBA_GET_SETTING, new CameraMessageCallback() {
            @Override
            public void onReceiveErrorMessage(CameraMessage cameraMessage, JSONObject jsonObject) {

            }

            @Override
            public void onReceiveMessage(CameraMessage cameraMessage, JSONObject jsonObject) {
                cameraStatus = jsonObject.optString("param");
                if (mPlayer.isPlaying()) {
                    initRecordedIcon();
                }
            }

            @Override
            public void onReceiveNotification(JSONObject jsonObject) {

            }
        });
        getCameraStatus.put("type", "app_status");
        remoteCamHelper.sendCommand(getCameraStatus);
    }

    @UiThread
    void initRecordedIcon() {
        if ("record".equals(cameraStatus)) {
            iv_is_record.setVisibility(View.VISIBLE);
        } else {
            iv_is_record.setVisibility(View.GONE);
        }
    }

    /**
     * 从记录仪上获取突发视频的数据
     */
    private void getEventDataFromCamera() {
        Logger.i(TAG, "getEventDataFromCamera()");
        CameraMessage getLSMessage = new CameraMessage(CommandID.AMBA_LS_NEW, new CameraMessageCallback() {
            @Override
            public void onReceiveErrorMessage(CameraMessage cameraMessage, JSONObject jsonObject) {
                Logger.e(TAG, "onReceiveErrorMessage" + jsonObject.toString());

            }

            @Override
            public void onReceiveMessage(CameraMessage cameraMessage, JSONObject jsonObject) {
                Logger.e(TAG, "onReceiveMessage:" + jsonObject.toString());
//                mEventListingInfo = GsonUtils.fromJson(jsonObject.toString(), ListingInfo.class);
//                Logger.i(TAG, "EventListingInfo:" + mEventListingInfo.toString());
                //只初始化Event页的数据
            }

            @Override
            public void onReceiveNotification(JSONObject jsonObject) {

            }
        });
        getLSMessage.put("type", "event");

        remoteCamHelper.sendCommand(getLSMessage);
    }

    /**
     * 从记录仪上获取手动记录的数据
     */
    private void getPhotoDataFromCamera() {
        Logger.i(TAG, "getPhotoDataFromCamera()");
        CameraMessage getLSMessage = new CameraMessage(CommandID.AMBA_LS_NEW, new CameraMessageCallback() {
            @Override
            public void onReceiveErrorMessage(CameraMessage cameraMessage, JSONObject jsonObject) {
                Logger.e(TAG, "onReceiveErrorMessage" + jsonObject.toString());

            }

            @Override
            public void onReceiveMessage(CameraMessage cameraMessage, JSONObject jsonObject) {
                Logger.e(TAG, "onReceiveMessage:" + jsonObject.toString());
                mPhotoListingInfo = GsonUtils.fromJson(jsonObject.toString(), ListingInfo.class);
                //shimeng add for protect null point,20160416,begin
                if (null != mPhotoListingInfo) {
                    List<ListingInfo.FileInfo> fileInfos = mPhotoListingInfo.getListing();
                    int count = Math.min(3, fileInfos.size());
                    homePhotoInfos.clear();
                    for (int i = fileInfos.size(); i > fileInfos.size() - 3 && i > 0; i--) {
                        String thumbName = fileInfos.get(i - 1).getFilename().replace("A", "T");
                        HomePhotoInfo homePI = new HomePhotoInfo(thumbName, HomePhotoInfo.NORMAL_PIC);
                        homePhotoInfos.add(homePI);
                    }
                    showPhoto();
                    Logger.i(TAG, "PhotoListingInfo:" + mPhotoListingInfo.toString());
                }
                //shimeng add for protect null point,20160416,end
            }

            @Override
            public void onReceiveNotification(JSONObject jsonObject) {

            }
        });
        getLSMessage.put("type", "photo");

        RemoteCamHelper.getRemoteCam().sendCommand(getLSMessage);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            this.rl_container.setVisibility(View.GONE);
        } else {
            this.rl_container.setVisibility(View.VISIBLE);
            if (LeautoCameraAppLication.isConnectCamera) {
                ((HomeActivity) mActivity).hideBottombar();
                ((HomeActivity) mActivity).showAppBarWithDisConnect();
            } else {
                ((HomeActivity) mActivity).hideAppBar();
                ((HomeActivity) mActivity).showBottombar();
            }

        }
        super.onConfigurationChanged(newConfig);
    }

    @UiThread
    void canTakePhotoIconInMainThread() {
        bt_take_photo.setEnabled(true);
    }

    /**
     * 初始化播放器
     */
  /*  private void initPlayerController(boolean walkCde) {
        if (!LeautoCameraAppLication.sCmfInitSuccess) {
            return;
        }
        sourceList = new ArrayList<MediaSource>();
        MediaSource source = new MediaSource();
        // 设置播放源,必须设置
        Logger.e("rtsp://" + com.letv.leauto.cameracmdlibrary.common.Config.CAMERA_IP + "/live");
        source.setSource("rtsp://" + com.letv.leauto.cameracmdlibrary.common.Config.CAMERA_IP + "/live");
        // 设置媒体类型,必须设置,默认为点播
        source.setType(MediaSource.TYPE_LIVE);
        source.setEncrypt(false).setTransfer(walkCde);
        sourceList.add(source);
        mPlayController.play(sourceList);
    }*/
    private void startReFreshAnimation() {
        if (ll_connect_to_camera.getVisibility() == View.GONE) {
            ll_connect_to_camera.setVisibility(View.VISIBLE);
            AnimationUtils.rotate(iv_refresh);
        }
    }

    @UiThread
    void hideReFreshAnimation() {
        if (ll_connect_to_camera.getVisibility() == View.VISIBLE) {
            ll_connect_to_camera.setVisibility(View.GONE);
            AnimationUtils.cancelAnmation(iv_refresh);
        }
    }

    @UiThread
    void stopAndStartPlay() {
        Logger.e(TAG, "stopAndStartPlay");

        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.play(liveUrl);
        }

//        mPlayer.play(mActivity, video_layout, liveUrl, LeHighPlayer.PLAY_TYPE_RTSP, LeHighPlayer.ENCODE_TYPE_SOFT);
//        mPlayer.play(mActivity, video_layout, liveUrl, MedIaPlayerManager.PLAY_TYPE_LIVE, MedIaPlayerManager.ENCODE_TYPE_SOFT);

    }

    @UiThread
    void statrtPlay() {
        getCameraStatus();
        cameraIsAp();
    }


    public void onEventMainThread(NotificationEvent event) {

        if (event.getType() == NotificationEvent.IDLE) {
            mPlayer.stop();
          /*  if (this.mPlayController != null) {
                this.mPlayController.stopPlayback();
            }*/
            cameraStatus = "idle";
            initRecordedIcon();
        } else {
            if (event.getType() == NotificationEvent.VF || event.getType() == NotificationEvent.RECORD) {
                if (!mPlayer.isPlaying()) {
                    mPlayer.play(liveUrl);
                }
                if (event.getType() == NotificationEvent.VF) {
                    cameraStatus = "vf";
                }
                if (event.getType() == NotificationEvent.RECORD) {
                    cameraStatus = "record";
                }
                initRecordedIcon();
            }
        }

        if (event.getType() == NotificationEvent.SD_INSERTED) {
            getPhotoDataFromCamera();
        }
        if (event.getType() == NotificationEvent.GET_FILE_FAIL) {
            Logger.e("失败！！！！！！！！！！！！！！！！！！！！！！！！！！！");
            hideLoading();
            showToastSafe(getResources().getString(R.string.download_to_phone_error));
            if (runnable != null) {
                handler_.removeCallbacks(runnable);
            }
        }
        if (event.getType() == NotificationEvent.GET_FILE_COMPLETE) {
            hideLoading();
            showToastSafe(getResources().getString(R.string.take_photo_and_download));
            if (runnable != null) {
                handler_.removeCallbacks(runnable);
            }
        }
    }

    @UiThread
    void inputWifiPassword(final ScanResult scanResult) {
//        inputPasswordDiglog
        tempSSID = scanResult.SSID;
        creatInputDialog(scanResult.SSID, "");
    }

    private void creatInputDialog(String title, String tipText) {
        String tip = TextUtils.isEmpty(tipText) ? getResources().getString(R.string.input_password) : tipText;
        if (inputPassword_tv_tip != null) {
            inputPassword_tv_tip.setText(tip);
            inputPassword_editTextWithDelete.setText("");
        }
        if (inputPasswordDiglog == null) {
            NormalDialog.Builder builder = new NormalDialog.Builder(mActivity);
            View view = inflater.inflate(R.layout.wifi_input_password, null);
            inputPassword_editTextWithDelete = (EditTextWithDelete) view.findViewById(R.id.wifi_password);
            inputPassword_tv_tip = (TextView) view.findViewById(R.id.tv_input_password);
            builder.setTitle(title);
            builder.setContentView(view);
            builder.setNegativeButton(R.drawable.dialog_no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    inputPasswordDiglog.dismiss();
                    resumeConnectPage();
                }
            });
            builder.setPositiveButton(R.drawable.dialog_yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String password = inputPassword_editTextWithDelete.getText().toString();
                    if (ValidateUtils.checkEdrPasswordLength(password, 8, 20)) {
                        inputPasswordDiglog.dismiss();
                        loadingConnectPage();
                        connectWifi(tempSSID, password);
                        SpUtils.getInstance(mActivity).setValue(com.leautolink.leautocamera.config.Constant.WIFI_PWD, password);
                    } else {
                        inputPassword_tv_tip.setText(getResources().getString(R.string.input_password_true));
                    }
                }
            });
            inputPasswordDiglog = builder.create();
        } else {
            inputPasswordDiglog.setTitle(title);
        }
        inputPasswordDiglog.show();
    }

    private boolean connectWifi(String ssid, String password) {
//        admin.disconnect();
//        WifiConfiguration mWifiConfig = null;
//        mWifiConfig = admin.getWifiConfigure(ssid);
//        if (mWifiConfig != null && !admin.isAuthenticateFailed(mWifiConfig)) {
//            Logger.e(TAG, "wifi " + ssid + " 已经保存过链接配置 ，只需要链接");
//            admin.enableNetwork(mWifiConfig);
//            return true;
//        }
//        admin.forget(ssid);
//        mWifiConfig = admin.CreateWifiInfo(ssid, password, 3);
//        boolean b = admin.addAndEnableNetwork(mWifiConfig);//admin.addNetwork(admin.CreateWifiInfo(ssid, password, 3));
//        Logger.e(TAG, "ConnectWifi  SSID-->| " + ssid + " password -->| " + password + " canConnect -->| " + b);
//        return b;
        if (cameraConnectionHelper == null) {
            cameraConnectionHelper = new CameraConnectionHelper(mActivity.getApplication());
        }
        cameraConnectionHelper.reset();
        cameraConnectionHelper.startConnection(ssid, password);
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cameraConnectionHelperClose();
    }

    public void cameraConnectionHelperClose() {
        if (cameraConnectionHelper != null) {
            cameraConnectionHelper.close();
            cameraConnectionHelper = null;
        }
    }

    /**
     * 创建socket服务，监听记录仪连接成功的广播
     */
    @Background
    void switchAp() {
        if (LeautoCameraAppLication.myServer == null) {
            LeautoCameraAppLication.myServer = new MyServer();
            LeautoCameraAppLication.myServer.initMyServer(new ApConnectedSuccessCallback() {
                @Override
                public void successed() {
                    statrtPlay();
                    DeviceInfo.getInstance().setContext(new WeakReference<Context>(CameraFragment.this.getActivity()));
                    DeviceInfo.getInstance().getDeviceInfo();
                }

                @Override
                public void fail() {

                }
            });
        }
    }

    /**
     * 发命令让记录仪切换为sta状态
     */
    private void apStatrt() {
        final String password = "1234567890";
        CameraMessage cameraMessage = new CameraMessage(1543, new CameraMessageCallback() {
            @Override
            public void onReceiveErrorMessage(CameraMessage cameraMessage, JSONObject jsonObject) {

            }

            @Override
            public void onReceiveMessage(CameraMessage cameraMessage, JSONObject jsonObject) {
//                wifiAp.startWifiAp(ssid, password);
                Constant.token = 0;
                RemoteCamHelper.getRemoteCam().closeChannel();
            }

            @Override
            public void onReceiveNotification(JSONObject jsonObject) {

            }
        });
        cameraMessage.put("type", "sta");
        cameraMessage.put("ssid", ssid);
        cameraMessage.put("password", password);
        cameraMessage.put("times", 30);
        remoteCamHelper.sendCommand(cameraMessage);
        Logger.e("Send 'sta' command to recorder!");
//        CheckUDPService.chekUdpTimeOut();
    }


    @Click(R.id.bt_click_connect)
    void startConnect() {
        admin.openWifi();
        admin.startScan();
        loadingConnectPage();
        getWifiList();
    }

    private void getWifiList() {
        boolean isCanConnect = false;
        List<String> wifiSSIDList = new ArrayList<>();
        final List<ScanResult> scanResults = admin.getWifiList();
        if (newSacn != null) {
            newSacn.clear();
        } else {
            newSacn = new ArrayList<>();
        }
        for (ScanResult scanResult : scanResults) {
            if (WifiAdmin.isConnectCamera(getActivity(),scanResult.SSID, scanResult.BSSID)) {
                newSacn.add(scanResult);
                wifiSSIDList.add(scanResult.SSID.replace("\"", ""));
            }
        }

        wifiName = SpUtils.getInstance(mActivity).getStringValue(com.leautolink.leautocamera.config.Constant.WIFI_SSID);
        String wifiPassword = "123456789";
        if (!TextUtils.isEmpty(wifiName)) {
            wifiPassword = SpUtils.getInstance(mActivity).getStringValue(com.leautolink.leautocamera.config.Constant.WIFI_PWD);
        }

        Logger.d(TAG, "wifiName -->| " + wifiName + " wifipwd -->| " + wifiPassword + " isContains -->| " + wifiSSIDList.contains(wifiName));

        if (wifiSSIDList.contains(wifiName)) {
            isCanConnect = connectWifi(wifiName, wifiPassword);
            tempSSID = wifiName;
        } else if (newSacn.size() == 1) {
            inputWifiPassword(newSacn.get(0));
            return;
        }
        Logger.e(TAG, wifiName + " isCanConnect  -->| " + isCanConnect);
        if (!isCanConnect) {
            //TODO 显示wifi列表
            showWifiList(true);
        }

    }

    private void loadingConnectPage() {
        bt_click_connect.setVisibility(View.INVISIBLE);
        bt_click_connect.setClickable(false);
        bt_click_connect.setEnabled(false);
        iv_loading_icon.setVisibility(View.VISIBLE);
        tv_find_edr.setVisibility(View.GONE);
        tv_little_tip.setVisibility(View.VISIBLE);
        tv_little_tip_detail.setVisibility(View.VISIBLE);
        AnimationUtils.rotate(iv_loading_icon);
        iv_close_connect.setVisibility(View.VISIBLE);
        ((HomeActivity) mActivity).hideBottombar();
    }

    @UiThread
    void resumeConnectPage() {
        bt_go_photo.setEnabled(false);
        bt_take_photo.setEnabled(false);
        iv_first_icon.setVisibility(View.GONE);
        iv_two_icon.setVisibility(View.GONE);
        iv_three_icon.setVisibility(View.GONE);
        rl_connect_page.setVisibility(View.VISIBLE);
        bt_click_connect.setVisibility(View.VISIBLE);
        bt_click_connect.setClickable(true);
        bt_click_connect.setEnabled(true);
        iv_loading_icon.setVisibility(View.GONE);
        tv_find_edr.setVisibility(View.VISIBLE);
        tv_little_tip.setVisibility(View.GONE);
        tv_little_tip_detail.setVisibility(View.GONE);
        iv_close_connect.setVisibility(View.GONE);
        AnimationUtils.cancelAnmation(iv_loading_icon);
        ((HomeActivity) mActivity).showBottombar();
        ((HomeActivity) mActivity).hideAppBar();
        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @UiThread
    void connectSuccess() {
        bt_go_photo.setEnabled(true);
        bt_take_photo.setEnabled(true);
        ((HomeActivity) mActivity).hideBottombar();
        ((HomeActivity) mActivity).showAppBarWithDisConnect();
        rl_connect_page.setVisibility(View.GONE);
        bt_click_connect.setVisibility(View.VISIBLE);
        iv_loading_icon.setVisibility(View.GONE);
        tv_find_edr.setVisibility(View.VISIBLE);
        tv_little_tip.setVisibility(View.GONE);
        tv_little_tip_detail.setVisibility(View.GONE);
        iv_close_connect.setVisibility(View.GONE);
        AnimationUtils.cancelAnmation(iv_loading_icon);
        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }


    @Click(R.id.tv_find_edr)
    void clickFindEdr() {
        showWifiList(false);
    }


    private void showWifiList(boolean isAutoConnect) {
        DiaglogActivity_.intent(this).isAutoConnect(isAutoConnect).startForResult(0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            if (data.getBooleanExtra(DiaglogActivity.ISSELECTED, false)) {
                ScanResult result = data.getParcelableExtra(com.leautolink.leautocamera.config.Constant.WIFI_SSID);
                inputWifiPassword(result);
            } else {
                resumeConnectPage();
            }
        }
    }

    @Click(R.id.iv_close_connect)
    void closeConnect() {
        if (!TextUtils.isEmpty(wifiName)) {
            admin.forget(wifiName);
            cameraConnectionHelper.close();
        }
        resumeConnectPage();
//        connectSuccess();
    }


    @Override
    public void onPrepared() {
        Logger.e(TAG, "onPrepared....");
        initRecordedIcon();
    }

    @Override
    public void onFirstPic() {
    }

    @Override
    public void onBufferingStart() {

    }

    @Override
    public void onBufferingUpdate(int percent) {

    }

    @Override
    public void onBufferingEnd() {

    }

    @Override
    public void onComplete() {

    }

    @Override
    public void onError(int what, String extra) {
        Logger.e("CDEOnError. what: " + what + "extra: " + extra);
        if (201 == what) {
            if (LeautoCameraAppLication.isConnectCamera) {
                stopAndStartPlay();
            }
        }
    }

    @Override
    public void onInfo(int what, long extra) {
        hideReFreshAnimation();
        Logger.e(TAG, "onInfo....");
    }

    @Override
    public void onBlock() {

    }

    @Override
    public void onSeekComplete() {

    }
}
