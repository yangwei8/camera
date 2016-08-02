package com.leautolink.leautocamera.ui.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.leautolink.leautocamera.R;
import com.leautolink.leautocamera.application.LeautoCameraAppLication;
import com.leautolink.leautocamera.event.CameraDisconnectEvent;
import com.leautolink.leautocamera.ui.adapter.FragmentTabAdapter;
import com.leautolink.leautocamera.ui.base.BaseFragmentActivity;
import com.leautolink.leautocamera.ui.fragment.CameraFragment;
import com.leautolink.leautocamera.ui.fragment.CameraFragment_;
import com.leautolink.leautocamera.ui.fragment.DicaFragment;
import com.leautolink.leautocamera.ui.fragment.DicaFragment_;
import com.leautolink.leautocamera.ui.fragment.DiscoverFragment;
import com.leautolink.leautocamera.ui.fragment.SettingFragment;
import com.leautolink.leautocamera.ui.fragment.SettingFragment_;
import com.leautolink.leautocamera.ui.fragment.UserFragment;
import com.leautolink.leautocamera.ui.fragment.UserFragment_;
import com.leautolink.leautocamera.ui.view.customview.popmenu.PopMenu;
import com.leautolink.leautocamera.ui.view.customview.popmenu.PopMenuItem;
import com.leautolink.leautocamera.ui.view.customview.popmenu.PopMenuItemListener;
import com.leautolink.leautocamera.upgrade.UpgradeAbility;
import com.leautolink.leautocamera.utils.CameraDataUtils;
import com.leautolink.leautocamera.utils.ConnectedHelper;
import com.leautolink.leautocamera.utils.Logger;
import com.leautolink.leautocamera.utils.LoginManager;
import com.leautolink.leautocamera.utils.NetworkUtil;
import com.leautolink.leautocamera.utils.SpUtils;
import com.leautolink.leautocamera.utils.WifiAdminV2;
import com.letv.leauto.cameracmdlibrary.common.Constant;
import com.letv.leauto.cameracmdlibrary.connect.RemoteCamHelper;
import com.letv.leauto.cameracmdlibrary.connect.model.CameraMessage;
import com.letv.leauto.cameracmdlibrary.connect.model.CameraMessageCallback;
import com.letv.loginsdk.bean.DataHull;
import com.letv.loginsdk.bean.PersonalInfoBean;
import com.letv.loginsdk.constant.LoginConstant;
import com.letv.loginsdk.network.task.GetResponseTask;
import com.letv.loginsdk.network.volley.VolleyRequest;
import com.letv.loginsdk.network.volley.VolleyResponse;
import com.letv.loginsdk.network.volley.toolbox.SimpleResponse;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.WindowFeature;
import org.androidannotations.api.BackgroundExecutor;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@EActivity(R.layout.activity_home)
@WindowFeature({Window.FEATURE_NO_TITLE})
public class HomeActivity extends BaseFragmentActivity {
    private static final String TAG = "HomeActivity";
    @ViewById(R.id.appBar)
    AppBarLayout appBar;
    @ViewById(R.id.navigation_bar_left_ib)
    ImageButton navigation_bar_left_ib;
    @ViewById(R.id.navigation_bar_title)
    TextView navigation_bar_title;
    @ViewById(R.id.tv_dis_conncect)
    TextView tv_dis_conncect;
    @ViewById(R.id.navigation_bar_right_ib)
    ImageButton navigation_bar_right_ib;
    @ViewById(R.id.navigation_bar_right_bt)
    public Button navigation_bar_right_bt;

    @ViewById(R.id.rg_tabs)
    RadioGroup rg_tabs;

    @ViewById(R.id.tab_rb_edr)
    RadioButton tab_rb_edr;
    @ViewById(R.id.tab_rb_photo)
    RadioButton tab_rb_photo;
    @ViewById(R.id.tab_rb_user)
    RadioButton tab_rb_user;
    @ViewById(R.id.rb_share_btn)
    RadioButton rb_share_btn;


    private CameraFragment cameraFragment;
    private SettingFragment settingFragment;
    private UserFragment userFragment;
    private DiscoverFragment discoverFragment;
    private DicaFragment dicaFragment;
    private List<Fragment> list;
    private FragmentTabAdapter fragmentTabAdapter;


    private static final int WIFI_DIAGLOG_CODE = 0;
    private PopMenu mPopMenu;

    //private Intent checkUdpService;


    @Override
    public void onResume() {
        super.onResume();
        switch (fragmentTabAdapter.getCurrentTab()) {
            case 1:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                break;
            default:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(NetworkUtil.ping()) {
            autoUpLoadDataTask();
        }
    }
    /**
     * 启动自动上传数据
     */
    public void autoUpLoadDataTask() {
        BackgroundExecutor.execute(new BackgroundExecutor.Task("", 500, "") {
                                       @Override
                                       public void execute() {
                                           try {
                                               CameraDataUtils cameraDataUtils = new CameraDataUtils();
                                               cameraDataUtils.UploadDataZipFiles(HomeActivity.this);
                                           } catch (Throwable e) {
                                               Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
                                           }
                                       }

                                   }
        );
    }
    @AfterViews
    void init() {
        //初始化推送服务
//        initPush();
        this.checkUpgrade();
        navigation_bar_left_ib.setVisibility(View.GONE);
        navigation_bar_right_ib.setVisibility(View.GONE);
        initMenu();
        //checkUdpService = new Intent(this, CheckUDPService.class);
        //startService(checkUdpService);
//        discoverFragment = DiscoverFragment_.builder().build();
        dicaFragment = DicaFragment_.builder().build();
        cameraFragment = CameraFragment_.builder().build();
        userFragment = UserFragment_.builder().build();
        settingFragment = SettingFragment_.builder().build();
        if (list == null) {
            list = new ArrayList<>();
        } else {
            list.clear();
        }
//        list.add(discoverFragment);
        list.add(dicaFragment);
        list.add(cameraFragment);
        list.add(settingFragment);
        list.add(userFragment);
        fragmentTabAdapter = new FragmentTabAdapter(this, list, R.id.main_content, rg_tabs);
        //切换fragment的事件监听
        fragmentTabAdapter.setOnRgsExtraCheckedChangedListener(new FragmentTabAdapter.OnRgsExtraCheckedChangedListener() {
            @Override
            public boolean OnRgsExtraCheckedChanged(RadioGroup radioGroup, int checkedId, int index) {
                Logger.e(checkedId + "  :   " + index);
                switch (index) {
                    case 0:
                        navigation_bar_title.setText(getResources().getString(R.string.find));
                        navigation_bar_right_ib.setVisibility(View.GONE);
                        navigation_bar_right_bt.setVisibility(View.GONE);
                        showAppBar();
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                        break;
                    case 1:
                        navigation_bar_right_ib.setVisibility(View.GONE);
                        navigation_bar_right_bt.setVisibility(View.GONE);
                        navigation_bar_title.setText(getResources().getString(R.string.camera));
                        hideAppBar();
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        break;
                    case 2:
//                        if (!mPopMenu.isShowing()) {
//                            mPopMenu.show();
//                        }
                        break;
                    case 3:
                        navigation_bar_title.setText(getResources().getString(R.string.album));
                        navigation_bar_right_ib.setVisibility(View.GONE);
                        navigation_bar_right_bt.setVisibility(View.VISIBLE);
                        navigation_bar_right_bt.setText(getResources().getString(R.string.choose));
                        navigation_bar_right_bt.setTextColor(Color.rgb(20, 150, 177));
                        showAppBar();
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        break;

                    case 4:
//                        navigation_bar_title.setText("我");
//                        navigation_bar_right_ib.setVisibility(View.GONE);
//                        navigation_bar_right_bt.setVisibility(View.GONE);
//                        navigation_bar_left_ib.setVisibility(View.GONE);
//                        showAppBar();
                        hideAppBar();
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        break;
                    default:
                        break;
                }

                return true;
            }
        });
    }

    /**
     * 初始化推送服务
     */
    private void initPush() {
        PushAgent mPushAgent = PushAgent.getInstance(this);
        Logger.e(TAG, "推送情况：" + mPushAgent.isEnabled());
        //如果推送开关是开启的，那么就开启推送服务
        if (SpUtils.getBoolean(this, "pushEnable", true)) {
            if (!mPushAgent.isEnabled()) {
                mPushAgent.enable(new IUmengRegisterCallback() {
                    @Override
                    public void onRegistered(String s) {
                        Logger.e(TAG, "推送已开启 device_token:" + s);
                        SpUtils.putBoolean(HomeActivity.this, "pushEnable", true);
                    }
                });
            }
        } else {
            if (mPushAgent.isEnabled()) {
                Logger.e(TAG, "推送已关闭");
                mPushAgent.disable();
            }
        }
    }

    private void initMenu() {
        mPopMenu = new PopMenu.Builder().attachToActivity(HomeActivity.this)
                .addMenuItem(new PopMenuItem(getResources().getString(R.string.photo_share), getResources().getDrawable(R.drawable.btn_share)))
                .addMenuItem(new PopMenuItem(getResources().getString(R.string.video_share), getResources().getDrawable(R.drawable.btn_share)))
                .columnCount(2)
                .setOnItemClickListener(new PopMenuItemListener() {
                    @Override
                    public void onItemClick(PopMenu popMenu, int position) {
                        if (position == 0) {
                            goLocalGallery("photo");
                        } else if (position == 1) {
                            goLocalGallery("video");
                        }

                    }
                })
                .build();

    }


    private void goLocalGallery(String type) {
        LocalGalleryActivity_.intent(this).photoOrVideo(type).start();
    }

    private void showTab() {
        if (fragmentTabAdapter.getCurrentTab() != 0) {
//            fragmentTabAdapter.showTab(0);
            rg_tabs.check(R.id.tab_rb_discover);
            hideAppBar();
        }
    }

    public void showTab(int index) {
        if (fragmentTabAdapter != null) {
            Logger.e("GalleryRecyclerAdapter", "index:" + index);
            fragmentTabAdapter.selectedFragment(index);
            rg_tabs.check(index);
        }
    }

    public void onEventMainThread(CameraDisconnectEvent event) {
        LeautoCameraAppLication.isApConnectCamera = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Click(R.id.navigation_bar_right_ib)
    void goSetting() {
        SettingCameraActivity_.intent(this).start();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            tintManager.setStatusBarTintEnabled(false);
            this.appBar.setVisibility(View.GONE);
            this.rg_tabs.setVisibility(View.GONE);
            WindowManager.LayoutParams attrs = getWindow().getAttributes();
            attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(attrs);
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            tintManager.setStatusBarTintEnabled(true);
            this.rg_tabs.setVisibility(View.VISIBLE);
            this.appBar.setVisibility(View.VISIBLE);
            WindowManager.LayoutParams attrs = getWindow().getAttributes();
            attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(attrs);
            getWindow().clearFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        super.onConfigurationChanged(newConfig);
    }

//    public void onEventMainThread(ConnectSuccessEvent event) {
//        navigation_bar_right_ib.setVisibility(View.VISIBLE);
//    }

    @Override
    public void releaseResources() {

    }
//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//        switch (event.getKeyCode()) {
//            case KeyEvent.KEYCODE_BACK:
//            case KeyEvent.KEYCODE_MENU:
//                WifiApAdmin.closeWifiAp(this);
//                break;
//            default:
//                break;
//        }
//        return super.dispatchKeyEvent(
//
// event);
//    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitBy2Click();
        }
        return false;
    }

    /**
     * 双击退出函�
     */
    private static Boolean isExit = false;

    private void exitBy2Click() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true;
            showToastSafe(getResources().getString(R.string.exit));
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false;
                }
            }, 2000);

        } else {
            //DeviceDiscovery.getInstance().stopDiscovery();
            ConnectedHelper.exitApp(this);
//            android.os.Process.killProcess(Process.myPid());
//            System.exit(0);
        }
    }

    public void hideBottombar() {
        rb_share_btn.setVisibility(View.GONE);
        rg_tabs.setVisibility(View.INVISIBLE);
        rg_tabs.setClickable(false);
    }

    public void goneBottombar() {
        rb_share_btn.setVisibility(View.GONE);
        rg_tabs.setVisibility(View.GONE);
        rg_tabs.setClickable(false);
    }


    public void showAppBar() {
        if (appBar.getVisibility() == View.GONE) {
            appBar.setVisibility(View.VISIBLE);
            tv_dis_conncect.setVisibility(View.GONE);
            navigation_bar_right_ib.setVisibility(View.GONE);
        }
    }

    public void showAppBarWithDisConnect() {
        if (appBar.getVisibility() == View.GONE) {
            appBar.setVisibility(View.VISIBLE);
            tv_dis_conncect.setVisibility(View.VISIBLE);
            navigation_bar_right_ib.setVisibility(View.VISIBLE);
        }
    }

    public void hideAppBar() {
        appBar.setVisibility(View.GONE);
        tv_dis_conncect.setVisibility(View.GONE);
        navigation_bar_right_ib.setVisibility(View.GONE);
    }

    public void showBottombar() {
        rg_tabs.setVisibility(View.VISIBLE);
        rg_tabs.setClickable(true);
        rb_share_btn.setVisibility(View.VISIBLE);
    }

    @Click(R.id.tv_dis_conncect)
    void disConnect() {
        final WifiAdminV2 admin = new WifiAdminV2(this);

        showConfirmDialog(getResources().getString(R.string.stoplink), new OnDialogListener() {
            @Override
            public void onSure() {
//                cameraFragment.cameraConnectionHelperClose();
                admin.forget(SpUtils.getInstance(HomeActivity.this).getStringValue(com.leautolink.leautocamera.config.Constant.WIFI_SSID));

                showBottombar();
                showAppBar();
            }

            @Override
            public void onCancel() {

            }
        });
    }

    @Click(R.id.rb_share_btn)
    void shareMenu() {
        if (!mPopMenu.isShowing()) {
            mPopMenu.show();
        }
    }

    /**
     * 发命令让记录仪切换为ap状态
     */
    private void apCamera() {
        CameraMessage cameraMessage = new CameraMessage(1543, new CameraMessageCallback() {
            @Override
            public void onReceiveErrorMessage(CameraMessage cameraMessage, JSONObject jsonObject) {

            }

            @Override
            public void onReceiveMessage(CameraMessage cameraMessage, JSONObject jsonObject) {
                Constant.token = 0;
                RemoteCamHelper.getRemoteCam().closeChannel();
            }

            @Override
            public void onReceiveNotification(JSONObject jsonObject) {

            }
        });
        cameraMessage.put("type", "ap");
        remoteCamHelper.sendCommand(cameraMessage);
    }

    private void checkUpgrade() {
        UpgradeAbility upgardeAbility = new UpgradeAbility(this);
        upgardeAbility.checkUpgrade(false, false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == LoginConstant.LOGOUTFROMPERSONINFO) {
            if (userFragment != null) {
                userFragment.onLogOut();
            }
        }
        if (resultCode == WIFI_DIAGLOG_CODE) {
            cameraFragment.onActivityResult(requestCode, resultCode, data);
        }
        //查看用户名，头像是否更新，如果更新，则及时刷新我的页面的显示
        if (LoginManager.isLogin(this)) {
            GetResponseTask.getGetResponseTaskInstance().getUserInfoByUid(LoginManager.getUid(this), new SimpleResponse<PersonalInfoBean>() {
                @Override
                public void onCacheResponse(VolleyRequest<PersonalInfoBean> request, PersonalInfoBean result, DataHull hull, VolleyResponse.CacheResponseState state) {
                    if (state == VolleyResponse.CacheResponseState.SUCCESS) {
                        String nickName = result.getNickname();
                        String headPicUrl = result.getPicture200x200();
                        Logger.e(TAG, "当前用户名：" + nickName + "   " + "当前头像的rul：" + headPicUrl);
                        if (!nickName.equals(LoginManager.getNicename(HomeActivity.this))) {
                            LoginManager.setNickName(HomeActivity.this, nickName);
                            if (userFragment != null) {
                                userFragment.refreshNickName(nickName);
                            }
                        }

                        if (!headPicUrl.equals(LoginManager.getHeadPicUrl(HomeActivity.this))) {
                            LoginManager.setHeadPicUrl(HomeActivity.this, headPicUrl);
                            if (userFragment != null) {
                                userFragment.refreshHeadPicUrl(headPicUrl);
                            }
                        }
                    }
                }
            });
        }
    }


}
