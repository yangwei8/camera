package com.leautolink.leautocamera.ui.activity;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leautolink.leautocamera.R;
import com.leautolink.leautocamera.event.GetServerAppVersionEvent;
import com.leautolink.leautocamera.ui.base.BaseActivity;
import com.leautolink.leautocamera.upgrade.UpgradeAbility;
import com.letv.leauto.cameracmdlibrary.utils.SystemUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/**
 * Created by tianwei on 16/4/6.
 */
@EActivity(R.layout.activity_about_app)
public class AboutAppActivity extends BaseActivity implements View.OnClickListener {
    @ViewById(R.id.navigation_bar_left_ib)
    ImageButton navigation_bar_left_ib;
    @ViewById(R.id.navigation_bar_title)
    TextView navigation_bar_title;
    @ViewById(R.id.tv_app_version)
    TextView tv_app_version;
    @ViewById(R.id.ll_upgrade_app)
    LinearLayout ll_upgrade_app;
    @ViewById(R.id.iv_upgrade_app_version)
    ImageView iv_upgrade_app_version;
    @ViewById(R.id.tv_argument)
    TextView mTvArgument;

    @AfterViews
    void init() {
        initView();
        initData();
        initListener();
    }

    private void initListener() {
        navigation_bar_left_ib.setOnClickListener(this);
        mTvArgument.setOnClickListener(this);
    }


    private void initView() {
        navigation_bar_title.setText("关于");
        tv_app_version.setText(SystemUtils.getAppVersionName(this));
        updateUI();
    }

    private void initData() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.navigation_bar_left_ib:
                this.finish();
                break;
            case R.id.tv_argument:
                goArgumentActivity();
                break;
        }
    }

    /**
     * 去免责声明页面
     */
    private void goArgumentActivity() {
        InstallGuideActivity_.intent(this).type("").start();
    }

    @Click(R.id.ll_upgrade_app)
    void onUpgrade() {
        new UpgradeAbility(this).checkUpgrade(true, false);
    }

    public void onEventMainThread(GetServerAppVersionEvent event) {
        updateUI();
    }

    private void updateUI() {
        if (UpgradeAbility.hasNewApp(this)) {
            iv_upgrade_app_version.setVisibility(View.VISIBLE);
        }
    }
}
