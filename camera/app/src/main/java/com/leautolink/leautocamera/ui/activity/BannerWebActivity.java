package com.leautolink.leautocamera.ui.activity;

import android.graphics.Bitmap;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.leautolink.leautocamera.R;
import com.leautolink.leautocamera.ui.base.BaseActivity;
import com.leautolink.leautocamera.utils.Logger;
import com.letv.leauto.cameracmdlibrary.utils.SystemUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_banner_web)
public class BannerWebActivity extends BaseActivity {

    @ViewById(R.id.wv_web)
    WebView wv_web;

    @Extra
    String url;

    @AfterViews
    void init(){
        initWebView();
    }
    private void initWebView() {
        WebSettings settings = wv_web.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(false);
        settings.setUseWideViewPort(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        String ua = settings.getUserAgentString();
        Logger.e(ua+"LeAutoCamera:"+ SystemUtils.getAppVersionName(this));
        settings.setUserAgentString(ua+":LeAutoCamera:"+ SystemUtils.getAppVersionName(this));
        wv_web.loadUrl(url);
        wv_web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView webview, String url) {
                Logger.e(url);

                if( url.startsWith("sinaweibo://")) {
                    return false;
                }

                webview.loadUrl(url);
//

                return true;

        }
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
    }

    @Click(R.id.navigation_bar_left_ib)
    void goBack() {
        wv_web.destroy();
        this.finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (wv_web.canGoBack()) {
                wv_web.goBack();//返回上一页面
                return true;
            } else {
                this.goBack();
            }
        }
        return super.onKeyDown(keyCode, event);
    }



}
