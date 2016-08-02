package com.leautolink.leautocamera.ui.activity;

import android.graphics.Bitmap;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

import com.leautolink.leautocamera.R;
import com.leautolink.leautocamera.config.Config;
import com.leautolink.leautocamera.ui.base.BaseActivity;
import com.leautolink.leautocamera.utils.Logger;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/**
 * Created by tianwei on 16/4/7.
 */
@EActivity(R.layout.activity_lemi_forum)
public class LemiForumActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "LemiForumActivity";
    @ViewById(R.id.navigation_bar_left_ib)
    ImageButton navigation_bar_left_ib;
    @ViewById(R.id.navigation_bar_title)
    TextView navigation_bar_title;

    @ViewById(R.id.wv_lemi)
    WebView wv_lemi;

    @AfterViews
    void init() {
        initView();
        initWebView();
        initListener();
    }

    private void initView() {
        navigation_bar_title.setText(getResources().getString(R.string.lefans));
    }

    private void initWebView() {
        WebSettings settings = wv_lemi.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(true);
        settings.setUseWideViewPort(true);
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        wv_lemi.loadUrl(Config.LEMI_FORUM_URL);

        wv_lemi.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                showLoading(getResources().getString(R.string.loading));
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                hideLoading();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                wv_lemi.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Logger.e(TAG, "onReceivedError  errorCode:" + errorCode);
                hideLoading();
                showToastSafe(getResources().getString(R.string.net_check));
            }
        });
    }

    private void initListener() {
        navigation_bar_left_ib.setOnClickListener(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (wv_lemi.canGoBack()) {
                wv_lemi.goBack();//返回上一页面
                return true;
            } else {
                this.finish();//退出本Activity
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.navigation_bar_left_ib:
                this.finish();
                break;
        }
    }
}
