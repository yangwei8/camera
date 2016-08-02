package com.leautolink.leautocamera.ui.activity;

import android.text.format.Formatter;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.leautolink.leautocamera.R;
import com.leautolink.leautocamera.ui.base.BaseActivity;
import com.leautolink.leautocamera.ui.view.customview.SelectButton;
import com.leautolink.leautocamera.ui.view.customview.SettingItemView;
import com.leautolink.leautocamera.upgrade.UpgradeAbility;
import com.leautolink.leautocamera.utils.AsyncTaskUtil;
import com.leautolink.leautocamera.utils.Logger;
import com.leautolink.leautocamera.utils.SdCardUtils;
import com.leautolink.leautocamera.utils.SpUtils;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.File;

/**
 * Created by tianwei on 16/7/1.
 */
@EActivity(R.layout.activity_common_settings)
public class CommonSettingsActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "CommonSettingsActivity";
    private static final int DELETE_FAILED = 0;
    private static final int DELETE_SUCCEED = 1;

    @ViewById(R.id.navigation_bar_title)
    TextView mTvBarTitle;
    @ViewById(R.id.navigation_bar_left_ib)
    ImageButton mIbBarLeft;
    @ViewById(R.id.sb_receive_push_msg)
    SelectButton mSbReceivePushMsg;
    @ViewById(R.id.sb_comment_notification)
    SelectButton mSbCommentNotification;
    @ViewById(R.id.rl_clean_cache)
    RelativeLayout mRlCleanCache;
    @ViewById(R.id.tv_cache_size)
    TextView mTvCacheSize;
    @ViewById(R.id.siv_about_app)
    SettingItemView mSivAboutApp;

    private PushAgent mPushAgent;

    @AfterViews
    void init() {
        initViews();
        initDatas();
        initListeners();
    }

    private void initViews() {
        mTvBarTitle.setText(R.string.setting);
        updateReceiveMsgUI();
        updateAboutAppUI();
    }

    private void initDatas() {
        mPushAgent = PushAgent.getInstance(this);
        getCacheSize();
    }

    private void initListeners() {
        mIbBarLeft.setOnClickListener(this);
        mRlCleanCache.setOnClickListener(this);
        mSivAboutApp.setOnClickListener(this);
        mSbReceivePushMsg.setOnCheckedChangeListener(new SelectButton.OnCheckedChangeListener() {
            @Override
            public void checkedChange(boolean checked) {
                if (checked) {
                    if (!mPushAgent.isEnabled()) {
                        mPushAgent.enable(new IUmengRegisterCallback() {
                            @Override
                            public void onRegistered(String s) {
                                Logger.e(TAG, "推送已开启 device_token:" + s);
                                SpUtils.putBoolean(CommonSettingsActivity.this, "pushEnable", true);
                            }
                        });
                    }
                } else {
                    if (mPushAgent.isEnabled()) {
                        Logger.e(TAG, "推送已关闭");
                        mPushAgent.disable();
                        SpUtils.putBoolean(CommonSettingsActivity.this, "pushEnable", false);
                    }
                }
            }
        });
        mSbCommentNotification.setOnCheckedChangeListener(new SelectButton.OnCheckedChangeListener() {
            @Override
            public void checkedChange(boolean checked) {

            }
        });
    }

    private void updateReceiveMsgUI() {
        mSbReceivePushMsg.setIsChecked(SpUtils.getBoolean(this, "pushEnable", true));
    }

    private void updateAboutAppUI() {
        if (UpgradeAbility.hasNewApp(this)) {
            mSivAboutApp.getIvArrow().setImageResource(R.drawable.new_version);
        }
    }

    private void getCacheSize() {
        mTvCacheSize.setText("0.00 B");
        String editedVideoCachePath = SdCardUtils.getSDCardRootPath(this) + File.separator + "cache";
        String glideCachePath = Glide.getPhotoCacheDir(this).getAbsolutePath();
        Logger.e(TAG, "editedVideoCachePath : " + editedVideoCachePath);
        Logger.e(TAG, "glideCachePath : " + glideCachePath);
        AsyncTaskUtil.newInstance().execute(new AsyncTaskUtil.AsyncTaskListener() {
            @Override
            public void onPreExecute() {

            }

            @Override
            public Object doInBackground(Object... params) {
                File editedVideoCacheFile = new File((String) params[0]);
                File glideCacheFile = new File((String) params[1]);
                long editedVideoSize = 0l;
                long glideCacheSize = 0l;
                if (editedVideoCacheFile != null && editedVideoCacheFile.exists()) {
                    editedVideoSize = SdCardUtils.getFileSize( editedVideoCacheFile);
                }
                if (glideCacheFile != null && glideCacheFile.exists()) {
                    glideCacheSize = SdCardUtils.getFileSize(Glide.getPhotoCacheDir(CommonSettingsActivity.this));
                }
                String sizeString = Formatter.formatFileSize(CommonSettingsActivity.this, (editedVideoSize + glideCacheSize));
                Logger.e(TAG, "sizeString:" + sizeString);
                return sizeString;
            }

            @Override
            public void onProgressUpdate(int progress) {

            }

            @Override
            public void onPostExecute(Object result) {
                mTvCacheSize.setText((String) result);
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onCancelled(Object result) {

            }
        }, editedVideoCachePath, glideCachePath);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.navigation_bar_left_ib:
                finish();
                break;
            case R.id.rl_clean_cache:
                cleanCache();
                break;
            case R.id.siv_about_app:
                goAboutApp();
                break;
        }
    }

    private void cleanCache() {
        String editedVideoCachePath = SdCardUtils.getSDCardRootPath(this) + File.separator + "cache";
        String glideCachePath = Glide.getPhotoCacheDir(this).getAbsolutePath();
        AsyncTaskUtil.newInstance().execute(new AsyncTaskUtil.AsyncTaskListener() {
            @Override
            public void onPreExecute() {
                showLoading(getResources().getString(R.string.gcing));
            }

            @Override
            public Object doInBackground(Object... params) {
                File editedVideoCacheFile = new File((String) params[0]);
                File glideCacheFile = new File((String) params[1]);
                if (editedVideoCacheFile != null && editedVideoCacheFile.exists()) {
                    SdCardUtils.deleteFile(editedVideoCacheFile);
                }
                if (glideCacheFile != null && glideCacheFile.exists()) {
                    SdCardUtils.deleteFile(glideCacheFile);
                }
                if (SdCardUtils.getFileSize(editedVideoCacheFile) != 0 || SdCardUtils.getFileSize(glideCacheFile) != 0) {
                    return DELETE_FAILED;
                } else {
                    return DELETE_SUCCEED;
                }
            }

            @Override
            public void onProgressUpdate(int progress) {

            }

            @Override
            public void onPostExecute(Object result) {
                hideLoading();
                if ((int) result == DELETE_SUCCEED) {
                    showToastSafe(getResources().getString(R.string.gcfinish));
                    Logger.e(TAG, "清理完毕");
                    mTvCacheSize.setText("0.00 B");
                } else if ((int) result == DELETE_FAILED) {
                    showToastSafe(getResources().getString(R.string.gcfail));
                    Logger.e(TAG, "清理失败");
                }
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onCancelled(Object result) {

            }
        }, editedVideoCachePath, glideCachePath);
    }

    private void goAboutApp() {
        AboutAppActivity_.intent(this).start();
    }

}
