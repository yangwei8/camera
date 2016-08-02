package com.leautolink.leautocamera.ui.fragment;


import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.leautolink.leautocamera.R;
import com.leautolink.leautocamera.domain.respone.ShareAndUpCountInfo;
import com.leautolink.leautocamera.interfaces.ILoginOnActivityResult;
import com.leautolink.leautocamera.net.http.GsonUtils;
import com.leautolink.leautocamera.net.http.OkHttpRequest;
import com.leautolink.leautocamera.net.http.RequestTag.RequestTag;
import com.leautolink.leautocamera.net.http.httpcallback.PostCallBack;
import com.leautolink.leautocamera.ui.activity.CommonSettingsActivity_;
import com.leautolink.leautocamera.ui.activity.DiaglogBluetoothActivity_;
import com.leautolink.leautocamera.ui.activity.InstallGuideActivity_;
import com.leautolink.leautocamera.ui.activity.LemiForumActivity_;
import com.leautolink.leautocamera.ui.activity.ShareAndUpActivity;
import com.leautolink.leautocamera.ui.activity.ShareAndUpActivity_;
import com.leautolink.leautocamera.ui.activity.TiroHelpActivity_;
import com.leautolink.leautocamera.ui.base.BaseFragment;
import com.leautolink.leautocamera.ui.view.customview.CallDialog;
import com.leautolink.leautocamera.ui.view.customview.SettingItemView;
import com.leautolink.leautocamera.utils.FirmwareUtil;
import com.leautolink.leautocamera.utils.Logger;
import com.leautolink.leautocamera.utils.LoginManager;
import com.leautolink.leautocamera.utils.SpUtils;
import com.letv.leauto.cameracmdlibrary.common.Constant;
import com.letv.loginsdk.activity.PersonalInfoActivity;
import com.letv.loginsdk.bean.UserBean;
import com.letv.loginsdk.view.CircleImageView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;


@EFragment(R.layout.fragment_user)
public class UserFragment extends BaseFragment implements View.OnClickListener, ILoginOnActivityResult {
    private static final String TAG = "UserFragment";

    //未登录
    @ViewById(R.id.un_login)
    RelativeLayout mUnLogin;
    @ViewById(R.id.ibtn_head_unsign)
    ImageButton mIbtnLogin;
    //登录
    @ViewById(R.id.had_login)
    RelativeLayout mHadLogin;
    @ViewById(R.id.rl_userinfo_container)
    RelativeLayout mRlUserInfoContainer;
    @ViewById(R.id.civ_pic)
    CircleImageView mCivUserHead;
    @ViewById(R.id.tv_username)
    TextView mTvUserNickName;
    @ViewById(R.id.tv_shared_count)
    TextView mTvShareCount;
    @ViewById(R.id.tv_like_count)
    TextView mTvLikeCount;
    //设置选项相关
    @ViewById(R.id.siv_reset_pwd)
    SettingItemView mSivResetPwd;
    @ViewById(R.id.siv_update_fw)
    SettingItemView mSivUpdateFw;
    @ViewById(R.id.siv_lemi_community)
    SettingItemView mSivLemiCommunity;
    @ViewById(R.id.siv_tiro_help)
    SettingItemView mSivTiroHelp;
    @ViewById(R.id.siv_install_guide)
    SettingItemView mSivInstallGuide;
    @ViewById(R.id.siv_contact_us)
    SettingItemView mSivContactUs;
    @ViewById(R.id.siv_common_settings)
    SettingItemView mSivCommonSettings;


    //集团登录相关
    private final int LOGINSUCCESS = 0;
    private final int LOGINFAILED = 1;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOGINSUCCESS://登录成功
                    Logger.e(TAG, "登录成功");
                    SpUtils.putBoolean(mActivity, "islogin", true);
                    showHadLoginView(true);
                    getLikedCount();
                    break;
                case LOGINFAILED://登录失败
                    break;
            }
        }
    };


    @AfterViews
    void init() {
        initView();
        initData();
        initListener();
        getLikedCount();
    }

    private void initView() {
        if (LoginManager.isLogin(mActivity)) {
            showHadLoginView(true);
        } else {
            showHadLoginView(false);
        }
    }

    private void initData() {
        if (LoginManager.isLogin(mActivity)) {
            getSharedCount();
            getLikedCount();
        }
    }


    /**
     * 获取用户点赞的个数
     */
    private void getLikedCount() {
        if (LoginManager.isLogin(mActivity)) {

            Map<String, String> params = new HashMap<>();
            params.put("userId", LoginManager.getUid(mActivity));
            Map<String, String> headers = new HashMap<>();
            headers.put("token", LoginManager.getSsoTk(mActivity));
            OkHttpRequest.post(RequestTag.SHARE_AND_UP_TAG, RequestTag.SHARE_AND_UP_TAG_URL, headers, params, new PostCallBack() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) {
                    try {
                        String s = response.body().string();
                        Logger.e(s);
                        ShareAndUpCountInfo shareAndUpCountInfo = GsonUtils.fromJson(s, ShareAndUpCountInfo.class);

                        if (shareAndUpCountInfo.getCode() == 200) {

                            initShareAndUpCount(shareAndUpCountInfo.getMap());


                        } else {
                            showToastSafe(shareAndUpCountInfo.getMsg());
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(int errorCode) {

                }
            });
        }
    }

    @UiThread
    void initShareAndUpCount(ShareAndUpCountInfo.ShareAndUpCount map) {

        mTvShareCount.setText("(" + map.getShareCount() + ")");
        mTvLikeCount.setText("(" + map.getUpCount() + ")");
    }

    /**
     * 获取用户分享的个数
     */
    private void getSharedCount() {

    }

    private void initListener() {
        mSivResetPwd.setOnClickListener(this);
        mSivUpdateFw.setOnClickListener(this);
        mSivLemiCommunity.setOnClickListener(this);
        mSivTiroHelp.setOnClickListener(this);
        mSivInstallGuide.setOnClickListener(this);

        mSivContactUs.setOnClickListener(this);
        mSivCommonSettings.setOnClickListener(this);

        mIbtnLogin.setOnClickListener(this);
        mRlUserInfoContainer.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.e(TAG,"onResume");
        if (LoginManager.isLogin(mActivity)) {
            showHadLoginView(true);
            getLikedCount();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_head_unsign:
                goLogin();
                break;
            case R.id.rl_userinfo_container://查看用户详细信息
                goUserDetails();
                break;
            case R.id.siv_reset_pwd://重置wifi密码
                resetWifiPwd();
                break;
            case R.id.siv_update_fw://固件更新
                updateFw();
                break;
            case R.id.siv_lemi_community://乐迷社区
                goLemiCommunity();
                break;
            case R.id.siv_tiro_help://新手帮助
                goTiroHelp();
                break;
            case R.id.siv_install_guide://安装指南
                goInstallGuide();
                break;
            case R.id.siv_contact_us://联系我们
                goContactUs();
                break;
            case R.id.siv_common_settings://通用设置
                goCommonSettings();
                break;
        }
    }

    private void resetWifiPwd() {
//        startActivity(new Intent(this.mActivity.getApplicationContext(), ResetPasswordActivity.class));

        DiaglogBluetoothActivity_.intent(this).start();

    }

    private void updateFw() {
        if (!Constant.isSDCardPresent) {
            showToastSafe(R.string.no_sd_card);
            return;
        }

        if (!FirmwareUtil.canLocalUpgrade(mActivity)) {
            showToastSafe(R.string.is_new);
            return;
        }
        FirmwareUtil.checkLocalFile(mActivity);
    }

    private void goLemiCommunity() {
        LemiForumActivity_.intent(this).start();
    }

    private void goTiroHelp() {
        TiroHelpActivity_.intent(this).start();
    }

    private void goInstallGuide() {
        InstallGuideActivity_.intent(this).type("install").start();
    }

    private void goContactUs() {
        showCallDialog();
    }

    private void goCommonSettings() {
        CommonSettingsActivity_.intent(this).start();
    }

    private void showCallDialog() {
        CallDialog callDialog = new CallDialog(this.mActivity, R.style.dialog);
        callDialog.show();
    }


    /**
     * 登录
     */
    private void goLogin() {
//        new LoginSdk().login(mActivity, new LoginSuccessCallBack() {
//            @Override
//            public void loginSuccessCallBack(LoginSuccessState loginSuccessState, LetvBaseBean bean) {
//                if (loginSuccessState == LoginSuccessState.LOGINSUCCESS) {
//                    //登录成功
//                    mUserBean = (UserBean) bean;
//                    mHandler.sendEmptyMessageDelayed(LOGINSUCCESS, 0);
//                }
//            }
//        });

        LoginManager.login(mActivity, new LoginManager.LoginCallBack() {
            @Override
            public void onSuccess(UserBean userBean) {
                mHandler.sendEmptyMessageDelayed(LOGINSUCCESS, 0);
            }

            @Override
            public void onFailer() {

            }
        });
    }

    /**
     * 查看用户详细信息
     */
    private void goUserDetails() {
        String uid = LoginManager.getUid(mActivity);
        String ssoTk = LoginManager.getSsoTk(mActivity);

        if ((!TextUtils.isEmpty(uid)) && (!TextUtils.isEmpty(ssoTk))) {
            PersonalInfoActivity.lunch(mActivity, uid, ssoTk);
        }
    }


    /**
     * 展示已登录页面
     */
    private void showHadLoginView(boolean show) {
        if (show) {
            mUnLogin.setVisibility(View.GONE);
            mHadLogin.setVisibility(View.VISIBLE);
            mTvShareCount.setVisibility(View.VISIBLE);
            mTvLikeCount.setVisibility(View.VISIBLE);
            inflateLoginedInfos();
        } else {
            mHadLogin.setVisibility(View.GONE);
            mUnLogin.setVisibility(View.VISIBLE);
            mTvShareCount.setVisibility(View.GONE);
            mTvLikeCount.setVisibility(View.GONE);
        }
    }

    /**
     * 填充用户信息
     */
    private void inflateLoginedInfos() {
        String headPicUrl = LoginManager.getHeadPicUrl(mActivity);
        String userNickName = LoginManager.getNicename(mActivity);
//        if (mUserBean != null) {
//            headPicUrl = mUserBean.getPicture200x200();
//            userNickName = mUserBean.getNickname();
//            SpUtils.putString(mActivity, "headPicUrl", headPicUrl);
//            SpUtils.putString(mActivity, "userName", userNickName);
//            SpUtils.putString(mActivity, "uid", mUserBean.getUid());
//            SpUtils.putString(mActivity, "ssoTk", mUserBean.getSsoTK());
//        } else {//没有联网的时候从缓存中读取数据
//            headPicUrl = SpUtils.getString(mActivity, "headPicUrl", "");
//            userNickName = SpUtils.getString(mActivity, "userName", "");
//        }
        Logger.e(TAG, "headPicUrl:" + headPicUrl);
        Logger.e(TAG, "userNickName:" + userNickName);
        if (!TextUtils.isEmpty(LoginManager.getNicename(mActivity))) {
            mTvUserNickName.setText(userNickName);
        }
        if (!TextUtils.isEmpty(headPicUrl)) {
            Glide.with(this).load(headPicUrl).into(mCivUserHead);
        }
    }

    @Click(R.id.ll_share_count)
    void goShareActivity() {
        if (LoginManager.isLogin(mActivity)) {
            goSharepage();
        } else {
            LoginManager.login(mActivity, new LoginManager.LoginCallBack() {
                @Override
                public void onSuccess(UserBean userBean) {
                    mHandler.sendEmptyMessageDelayed(LOGINSUCCESS, 0);
                    goSharepage();
                }

                @Override
                public void onFailer() {
                    showToastSafe(getResources().getString(R.string.login_fail));
                }
            });
        }
    }

    private void goSharepage() {
        if (TextUtils.isEmpty(mTvLikeCount.getText().toString())) {
            return;
        }
        if (Integer.parseInt(mTvShareCount.getText().toString().replace("(", "").replace(")", "")) > 0) {
            ShareAndUpActivity_.intent(UserFragment.this).type(ShareAndUpActivity.TYPE_SHARE).start();
        } else {
            showToastSafe(mActivity.getString(R.string.you_not_have_share_file));
        }
    }

    @Click(R.id.ll_like_count)
    void goUpActivity() {
        if (LoginManager.isLogin(mActivity)) {
            goUppage();
        } else {
            LoginManager.login(mActivity, new LoginManager.LoginCallBack() {
                @Override
                public void onSuccess(UserBean userBean) {
                    mHandler.sendEmptyMessageDelayed(LOGINSUCCESS, 0);
                    goUppage();
                }

                @Override
                public void onFailer() {
                    showToastSafe(getResources().getString(R.string.login_fail));
                }
            });
        }
    }

    private void goUppage() {
        if (TextUtils.isEmpty(mTvLikeCount.getText().toString())) {
            return;
        }
        if (Integer.parseInt(mTvLikeCount.getText().toString().replace("(", "").replace(")", "")) > 0) {
            ShareAndUpActivity_.intent(UserFragment.this).type(ShareAndUpActivity.TYPE_UP).start();
        } else {
            showToastSafe(mActivity.getString(R.string.you_not_have_up_file));
        }
    }


    @Override
    public void releaseResources() {
    }

    @Override
    public void onLogOut() {
        Logger.e(TAG, "onLogOut－－退出登录");
        SpUtils.putBoolean(mActivity, "islogin", false);
        showHadLoginView(false);
    }

    public void refreshNickName(String newNickName) {
        mTvUserNickName.setText(newNickName);
    }

    public void refreshHeadPicUrl(String newHeadPicUrl) {
        Glide.with(this).load(newHeadPicUrl).into(mCivUserHead);
    }
}
