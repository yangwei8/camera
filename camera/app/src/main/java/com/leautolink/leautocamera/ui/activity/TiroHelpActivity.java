package com.leautolink.leautocamera.ui.activity;

import android.graphics.Bitmap;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

import com.leautolink.leautocamera.R;
import com.leautolink.leautocamera.callback.CustomDialogCallBack;
import com.leautolink.leautocamera.callback.SystemDialogCallBack;
import com.leautolink.leautocamera.net.http.OkHttpRequest;
import com.leautolink.leautocamera.net.http.httpcallback.DownLoadCallBack;
import com.leautolink.leautocamera.ui.base.BaseActivity;
import com.leautolink.leautocamera.utils.CustomDialogUtils;
import com.leautolink.leautocamera.utils.Logger;
import com.leautolink.leautocamera.utils.SdCardUtils;
import com.leautolink.leautocamera.utils.SystemDialogUtils;
import com.leautolink.leautocamera.utils.ToastUtils;
import com.leautolink.leautocamera.utils.UrlUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.text.DecimalFormat;

import okhttp3.Call;

/**
 * Created by tianwei on 16/5/12.
 */
@EActivity(R.layout.activity_tiro_help)
public class TiroHelpActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "TiroHelpActivity";
    @ViewById(R.id.navigation_bar_left_ib)
    ImageButton navigation_bar_left_ib;
    @ViewById(R.id.navigation_bar_title)
    TextView navigation_bar_title;
    @ViewById(R.id.wv_tiro_help)
    WebView mWebView;

    @AfterViews
    void init() {
        initView();
        initListener();
    }

    private void initView() {
        navigation_bar_title.setText(getResources().getString(R.string.new_help));
        initWebView();
    }

    private void initWebView() {
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(true);
        settings.setUseWideViewPort(true);
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        String url = "file:///android_asset/tirohelp/tirohelp.html";

        mWebView.loadUrl(url);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
//                showLoading("加载中...");
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
//                hideLoading();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                mWebView.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Logger.e(TAG, "onReceivedError  errorCode:" + errorCode);
//                hideLoading();
                showToastSafe(getResources().getString(R.string.net_check));
            }
        });

        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {

                Logger.e("sfasdf", "url:" + url);
                OkHttpRequest.downLoad("fwdownload", url, UrlUtils.getFwPath(TiroHelpActivity.this), "AmbaSysFW.zip", SdCardUtils.getSdSize(TiroHelpActivity.this, SdCardUtils.TYPE_AVAIABLE), new DownLoadCallBack() {
                    @Override
                    public void onFailure(Call call, IOException e) {
//                        CustomDialogUtils.hideCustomDialog();
//                        ToastUtils.showToast(TiroHelpActivity.this, "下载成功", ToastUtils.SHORT);
                    }

                    @Override
                    public void onStart(long total) {
                        CustomDialogUtils.showDialog(TiroHelpActivity.this, new CustomDialogCallBack() {
                            @Override
                            public void onCancel() {
                                OkHttpRequest.setCancel(true);
                            }
                        });
                    }

                    @Override
                    public void onLoading(long current, long total) {
                        CustomDialogUtils.setCurrentTotal(SdCardUtils.formateSize(TiroHelpActivity.this, current) + "/" + SdCardUtils.formateSize(TiroHelpActivity.this, total));
                        double percentage = ((double) current / total) * 100;
                        CustomDialogUtils.setSeekBarMax((int) total);
                        DecimalFormat df = new DecimalFormat("##.##");
                        String percentageStr = df.format((percentage));
                        CustomDialogUtils.setPercentage(percentageStr + "%");
                        CustomDialogUtils.setProgress((int) current);
                    }

                    @Override
                    public void onSucceed() {
                        CustomDialogUtils.hideCustomDialog();

                        ToastUtils.showToast(TiroHelpActivity.this, getResources().getString(R.string.download_success), ToastUtils.SHORT);
//                        unZip();
                    }

                    @Override
                    public void onSdCardLackMemory(long total, long avaiable) {

                        SystemDialogUtils.showSingleConfirmDialog(TiroHelpActivity.this, "温馨提示", "手机SD卡存储空间不足！", "知道了", new SystemDialogCallBack() {
                            @Override
                            public void onSure() {

                            }

                            @Override
                            public void onCancel() {
                                OkHttpRequest.cancelCurrentCall();
                            }
                        });
                    }

                    @Override
                    public void onCancel() {
                        OkHttpRequest.cancelCurrentCall();
                        ToastUtils.showToast(TiroHelpActivity.this,getResources().getString(R.string.download_cacel1), ToastUtils.SHORT);
                        //删除下载不完整的文件
//                        OkHttpRequest.deleteIntactFile(UrlUtils.getTargetPath(mListingInfo.getType(), TiroHelpActivity.this) + "/" + mCurrentFileInfo.getFilename());
                    }

                    @Override
                    public void onError(IOException e) {
                        CustomDialogUtils.hideCustomDialog();
                        ToastUtils.showToast(TiroHelpActivity.this, getResources().getString(R.string.download_error), ToastUtils.SHORT);
                    }
                });
            }
        });
    }

    private void initListener() {
        navigation_bar_left_ib.setOnClickListener(this);
    }

//    @UiThread
//    void unZip() {
//        ZipExtractorTask task = new ZipExtractorTask(UrlUtils.getFwPath(TiroHelpActivity.this), UrlUtils.getFwPath(TiroHelpActivity.this), TiroHelpActivity.this, true, true);
//        task.execute();
//    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebView.canGoBack()) {
                mWebView.goBack();//返回上一页面
                return true;
            } else {
                this.finish();//退出本Activity
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.navigation_bar_left_ib:
                this.finish();
                break;
        }
    }
}
