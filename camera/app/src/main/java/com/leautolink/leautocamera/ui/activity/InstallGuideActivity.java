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
import com.leautolink.leautocamera.ui.base.BaseActivity;
import com.leautolink.leautocamera.utils.Logger;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

/**
 * Created by tianwei on 16/4/6.
 */
@EActivity(R.layout.activity_install_guide)
public class InstallGuideActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "InstallGuideActivity";

    @ViewById(R.id.navigation_bar_left_ib)
    ImageButton navigation_bar_left_ib;
    @ViewById(R.id.navigation_bar_title)
    TextView navigation_bar_title;
    @ViewById(R.id.wv_install)
    WebView wv_install;
    @Extra
    String type;

    private String mUrl;

    public static final int TYPE_INSTALL = 1;
    public static final int TYPE_EXONERATE = 2;

    @AfterViews
    void init() {
        initView();
        initListener();

    }

    private void initView() {
        if ("install".equals(type)) {
            navigation_bar_title.setText(getResources().getString(R.string.installing_guide));
        } else {
            navigation_bar_title.setText(getResources().getString(R.string.user_pro));
        }
        initWebView();
    }

    private void initWebView() {
        WebSettings settings = wv_install.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(true);
        settings.setUseWideViewPort(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        if ("install".equals(type)) {
            mUrl = "file:///android_asset/install_guide.html";
        } else {
            mUrl = "file://" + getFilesDir() + "/instruction.html";
        }


//        final String httpUrl = "http://dashcam.leautolink.com/instruction.html";


        wv_install.loadUrl(mUrl);

        wv_install.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView webview, String url) {
                webview.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//                wv_install.loadUrl(localUrl);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Logger.e(TAG, "onPageStarted  url:" + url);
                if (url.equals("http://www.leautolink.com/")) {
                    navigation_bar_title.setText(getResources().getString(R.string.lelink));
                } else if ("".equals(type) && url.equals(mUrl)) {
                    navigation_bar_title.setText(getResources().getString(R.string.user_pro));
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
    }

    private void initListener() {
        navigation_bar_left_ib.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.navigation_bar_left_ib:
                this.finish();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (wv_install.canGoBack()) {
                wv_install.goBack();//返回上一页面
                return true;
            } else {
                this.finish();//退出本Activity
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
