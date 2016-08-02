package com.leautolink.leautocamera.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.bumptech.glide.Glide;
import com.google.gson.reflect.TypeToken;
import com.leautolink.leautocamera.R;
import com.leautolink.leautocamera.config.Config;
import com.leautolink.leautocamera.domain.respone.BaseInfo;
import com.leautolink.leautocamera.domain.respone.GetTokenInfo;
import com.leautolink.leautocamera.domain.respone.TabInfos;
import com.leautolink.leautocamera.domain.respone.UploadVideoResponseInfo;
import com.leautolink.leautocamera.net.http.GsonUtils;
import com.leautolink.leautocamera.net.http.OkHttpRequest;
import com.leautolink.leautocamera.net.http.RequestTag.RequestTag;
import com.leautolink.leautocamera.net.http.httpcallback.GetCallBack;
import com.leautolink.leautocamera.net.http.httpcallback.PostCallBack;
import com.leautolink.leautocamera.net.http.httpcallback.UploadFileCallBack;
import com.leautolink.leautocamera.ui.base.BaseActivity;
import com.leautolink.leautocamera.utils.InputMethodUtil;
import com.leautolink.leautocamera.utils.LocationUtils;
import com.leautolink.leautocamera.utils.Logger;
import com.leautolink.leautocamera.utils.LoginManager;
import com.leautolink.leautocamera.utils.ShareContentText;
import com.letv.lecloud.solar.http.ResponseInfo;
import com.letv.lecloud.solar.upload.FileUploader;
import com.letv.lecloud.solar.upload.UpProgressHandler;
import com.letv.lecloud.solar.upload.UpResponseHandler;
import com.letv.lecloud.solar.upload.UploadManager;
import com.letv.loginsdk.bean.UserBean;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Response;


/**
 * 上传视频或者图片
 * Created by tianwei on 16/6/22.
 */
@EActivity(R.layout.activity_upload_file)
public class UploadFileActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "UploadFileActivity";

    @ViewById(R.id.navigation_bar_right_bt)
    Button mBtnBarRight;
//    @ViewById(R.id.navigation_bar_left_tv)
//    TextView mTvBarLeft;
    @ViewById(R.id.navigation_bar_left_ib)
    ImageButton mIbBarLeft;
    @ViewById(R.id.navigation_bar_title)
    TextView mTvBarTitle;
    @ViewById(R.id.et_theme)
    EditText mEtTheme;
    @ViewById(R.id.iv_thumb)
    ImageView mIvThumb;
    @ViewById(R.id.rg_classfy)
    RadioGroup mRgClassfy;
    @ViewById(R.id.rb_1)
    RadioButton mRb1;
    @ViewById(R.id.rb_2)
    RadioButton mRb2;
    @ViewById(R.id.rb_3)
    RadioButton mRb3;
    @ViewById(R.id.rb_4)
    RadioButton mRb4;
    @ViewById(R.id.rb_5)
    RadioButton mRb5;
    @ViewById(R.id.rb_6)
    RadioButton mRb6;

    @ViewById(R.id.pb_progress_top)
    ProgressBar mPbProgressTop;
    @ViewById(R.id.pb_progress)
    ProgressBar mPbProgress;
    @ViewById(R.id.tv_progress)
    TextView mTvProgress;
    @ViewById(R.id.tv_location)
    TextView tv_location;


    @Extra
    String uploadFilePath;
    @Extra
    String editedVideoThumbPath;


    //上传视频相关
    private FileUploader mFileUploader;
    private UploadManager mUploadManager;
    private String mTheme;
    private boolean isUploading;
//    LocationManager lm;

    //上传视频相关bean
    /**
     * 获取分类的bean
     */
    private List<TabInfos> mTabInfos;
    /**
     * 获取token的bean
     */
    private GetTokenInfo mGetTokenInfo;
    /**
     * 上传视频成功之后的bean
     */
    private UploadVideoResponseInfo mUploadVideoResponseInfo;

    private ShareContentText mShareContentText;
    private LocationUtils locationUtils;


    private String address;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (isUploading) {
                return true;
            }
            View focusView = getCurrentFocus();
            if (InputMethodUtil.isShouldHideInput(focusView, ev)) {
                InputMethodUtil.hideInput(UploadFileActivity.this, focusView);
            }
            return super.dispatchTouchEvent(ev);
        }
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }


    @AfterViews
    void init() {
//        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Logger.e(TAG, "需要上传的文件的path：" + uploadFilePath+",显示的图片:"+editedVideoThumbPath);
        initViews();
        initDatas();
        initListeners();
        setAddress(getResources().getString(R.string.locationing));

        locationUtils = new LocationUtils(this);
        locationUtils.setEndLocation(new LocationUtils.OnEndLocation() {
            @Override
            public void onLocationChanged(AMapLocation location) {
                //SDK定位成功
                if (location != null) {
                    if (location.getErrorCode() == 0) {
                        if (!TextUtils.isEmpty(location.getAddress()) && TextUtils.isEmpty(address)) {
                            address = location.getAddress();
                            Logger.e(location.getAddress());
                            setAddress(address);
                        }

                    } else {
                        //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                        Logger.e("AmapError", "location Error, ErrCode:"
                                + location.getErrorCode() + ", errInfo:"
                                + location.getErrorInfo());
                    }
                }
            }

            @Override
            public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
                //地理位置反编码完毕

                if (rCode == 1000) {
                    if (result != null && result.getRegeocodeAddress() != null
                            && result.getRegeocodeAddress().getFormatAddress() != null && TextUtils.isEmpty(address)) {
                        Logger.e(result.getRegeocodeAddress().getFormatAddress()
                                + "附近");

                        address = result.getRegeocodeAddress().getFormatAddress();
                        setAddress(address);


                    } else {
                        Logger.e("没有搜索到该位置信息");
                    }
                } else {
                    Logger.e("坐标反编码出错 errorCode ： " + rCode);

                }

            }
        });
        locationUtils.startLocation();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @UiThread
    void setAddress(String loc) {
        tv_location.setText(loc);
    }

    private Map<String, String> getLocation() {
        Map<String, String> myLoacation = new HashMap<>();
//        Criteria criteria = new Criteria();
//        criteria.setCostAllowed(false); //设置位置服务免费
//
//        criteria.setAccuracy(Criteria.ACCURACY_COARSE); //设置水平位置精度
//        //getBestProvider 只有允许访问调用活动的位置供应商将被返回
//        String providerName = lm.getBestProvider(criteria, true);
//
//        if (providerName != null) {
//            Location location = lm.getLastKnownLocation(providerName);
//            Log.i(TAG, "-------" + location);
//            if (location != null) {
//                //获取维度信息
//                double latitude = location.getLatitude();
//                //获取经度信息
//                double longitude = location.getLongitude();
//                myLoacation.put("latitude", latitude + "");
//                myLoacation.put("longitude", longitude + "");
//                Logger.e(TAG, "定位方式： " + providerName + ",  维度：" + latitude + ",  经度：" + longitude + ",myLoacation=" + myLoacation);
//            }
//        } else {
//            Toast.makeText(this, "1.请检查网络连接 \n2.请打开我的位置", Toast.LENGTH_SHORT).show();
//        }
        return myLoacation;
    }

    private void initViews() {

        if (!TextUtils.isEmpty(uploadFilePath)) {
            mTvProgress.setVisibility(View.GONE);
            mBtnBarRight.setVisibility(View.VISIBLE);
            mBtnBarRight.setText(getResources().getString(R.string.publish));
            mBtnBarRight.setTextColor(Color.rgb(20, 150, 177));
//            mTvBarLeft.setVisibility(View.VISIBLE);
//            mTvBarLeft.setText("相册");
            mTvBarTitle.setText("");
            if (uploadFilePath.endsWith(".JPG") || uploadFilePath.endsWith(".jpg")) {
                Glide.with(this).load(Config.LOCAL_URL_PREFIX + uploadFilePath).placeholder(R.drawable.img_default).into(mIvThumb);
            }else {
                Glide.with(this).load(Config.LOCAL_URL_PREFIX + editedVideoThumbPath).placeholder(R.drawable.img_default).into(mIvThumb);
            }
        } else {
            //文件路径不正确
            showToastSafe(getResources().getString(R.string.file_dir_error));
            finish();
        }
    }

    private void initDatas() {
        getClassfy();
    }

    /**
     * 从网络获取分类信息
     */
    private void getClassfy() {
        Logger.e(TAG, "getClassfy");
        showLoading(getResources().getString(R.string.get_mess));
        OkHttpRequest.getString("getClassfy", RequestTag.TAB_URL, new GetCallBack() {
            @Override
            public void onFailure(Call call, IOException e) {
                hideLoading();
                showToastSafe(getResources().getString(R.string.get_mess_error));
                Logger.e(TAG, "获取分类信息失败  onFailure：" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Object response) {
                try {
                    Logger.e(TAG, "获取分类信息  onResponse：" + response.toString());
                    BaseInfo<TabInfos> baseInfo = GsonUtils.getDefault().fromJson((String) response, new TypeToken<BaseInfo<TabInfos>>() {
                    }.getType());
                    if (baseInfo != null && baseInfo.getCode() == 200) {
                        mTabInfos = baseInfo.getRows();
                        inflateClassfyInfo();
                    } else {
                        hideLoading();
                        showToastSafe(getResources().getString(R.string.server_fail));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showToastSafe(getResources().getString(R.string.get_mess_error));
                }
            }

            @Override
            public void onError(String error) {
                hideLoading();
                showToastSafe(getResources().getString(R.string.get_mess_error));
                Logger.e(TAG, "获取分类信息失败  onError：" + error);
            }
        });
    }

    /**
     * 根据网络数据填充分类
     */
    @UiThread
    void inflateClassfyInfo() {
        if (mTabInfos != null) {
            mRb1.setText(mTabInfos.get(0).getName());
            mRb2.setText(mTabInfos.get(1).getName());
            mRb3.setText(mTabInfos.get(2).getName());
            mRb4.setText(mTabInfos.get(3).getName());
            mRb5.setText(mTabInfos.get(4).getName());
            mRb6.setText(mTabInfos.get(5).getName());
        }
        hideLoading();
    }

    private void initListeners() {
        mBtnBarRight.setOnClickListener(this);
       // mTvBarLeft.setOnClickListener(this);
        mIbBarLeft.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.navigation_bar_right_bt:
                if (!isUploading) {
                    checkThemeIsEmpty();
                }
                break;
            case R.id.navigation_bar_left_ib:
            case R.id.navigation_bar_left_tv:
                finish();
                break;
        }
    }

    /**
     * 检测主题是否为空
     */
    private void checkThemeIsEmpty() {
        mTheme = mEtTheme.getText().toString().trim();
        if(mTheme!=null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(mTheme);
            mTheme = m.replaceAll("");
        }
        if (TextUtils.isEmpty(mTheme)) {
            showToastSafe(getResources().getString(R.string.main_head));
        } else {
            checkIsLogin();
        }
    }

    /**
     * 发布文件
     */
    private void publishFile() {
        if (!TextUtils.isEmpty(uploadFilePath)) {
            isUploading = true;
            mTvProgress.setVisibility(View.VISIBLE);
            if (uploadFilePath.endsWith(".JPG") || uploadFilePath.endsWith(".jpg")) {
                //上传图片
                publishPhoto();
            } else {
                publishVideo();
            }
        } else {
            showToastSafe(getResources().getString(R.string.file_dir_fail));
            finish();
        }
    }

    /**
     * 发布图片
     */
    private void publishPhoto() {

        Map<String, String> fileParams = new LinkedHashMap<>();
        fileParams.put("file", getFileName());
        Map<String, Object> param = new LinkedHashMap<>();
        param.put("userId", LoginManager.getUid(UploadFileActivity.this));
        param.put("userName", LoginManager.getNicename(UploadFileActivity.this));
        param.put("userPhoto", LoginManager.getHeadPicUrl(UploadFileActivity.this));
        param.put("title", mTheme);
        param.put("classifyId", getClassfyId());
        Map<String, String> location = getLocation();

        String loactionStr = TextUtils.isEmpty(address) ? getResources().getString(R.string.unknown) : address;
        param.put("location", loactionStr);
        String jsonParam = GsonUtils.toJson(param);
        fileParams.put("params", jsonParam);

        Logger.e(TAG, "publishPhoto 上传的参数：" + GsonUtils.toJson(fileParams));

        Map<String, String> headers = new HashMap<>();
        headers.put("token", LoginManager.getSsoTk(UploadFileActivity.this));
        OkHttpRequest.uploadFile("publishPhoto", RequestTag.UPLOAD_PHOTO_URL, uploadFilePath, headers, fileParams, new UploadFileCallBack() {
            @Override
            public void onStart(long total) {
                setProgressMax((int) total);
                Logger.e(TAG, "onStart");
            }

            @Override
            public void onLoading(long total, long current) {
                double percentage = ((double) current / total) * 100;
                DecimalFormat df = new DecimalFormat("##.##");
                String percentageStr = df.format((percentage));
                updateProgress((int) current, percentageStr);
                Log.i(TAG, "onLoading:  " + "current:" + current + ",percentageStr=" + percentageStr);
            }

            @Override
            public void onFinish() {
                isUploading = false;
                Logger.e(TAG, "onFinish");
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    isUploading = false;
                    String responseString = response.body().string();
                    Logger.e(TAG, "onResponse  responseString:" + responseString);
                    if (response.code() == 200) {
                        createShareContentText(responseString);
                    } else {
                        showToastSafe(getResources().getString(R.string.image_upload_fail));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onTimeOut() {
                isUploading = false;
                Logger.e(TAG, "onTimeOut");
            }

            @Override
            public void onFailure(Call call, IOException e) {
                isUploading = false;
                Logger.e(TAG, "onFailure:" + e.getMessage());
            }

            @Override
            public void onError(Object e) {
                isUploading = false;
                Logger.e(TAG, "onError:" + e.toString());
            }
        });
    }

    /**
     * 发布视频
     */
    private void publishVideo() {
        getUploadVideoToken();
    }

    /**
     * 检测是否登录
     */
    private void checkIsLogin() {

        if (!LoginManager.isLogin(this)) {
            Logger.e(TAG, "checkIsLogin: " + "未登录");
            LoginManager.login(this, new LoginManager.LoginCallBack() {
                @Override
                public void onSuccess(UserBean userBean) {
                    Logger.e(TAG, "登录成功");
                    publishFile();
                }

                @Override
                public void onFailer() {
                    Logger.e(TAG, "登录失败");
                    showToastSafe(getResources().getString(R.string.login_error));
                }
            });
        } else {
            Logger.e(TAG, "checkIsLogin: " + "已登录");
            publishFile();
        }
    }


    /**
     * 获取上传视频的token
     */
    private void getUploadVideoToken() {
        Logger.e(TAG, "getToken");
        Map<String, String> params = new HashMap<>();
        params.put("id", "-1");
        params.put("userId", LoginManager.getUid(UploadFileActivity.this));
        Map<String, String> headers = new HashMap<>();
        headers.put("token", LoginManager.getSsoTk(UploadFileActivity.this));
        OkHttpRequest.post("getToken", RequestTag.UPLOAD_VIDEO_REQUEST_TOKEN_URL, headers, params, new PostCallBack() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Logger.e(TAG, "getToken onFailure:" + e.getMessage());
                        showToastSafe(getResources().getString(R.string.net_check));
                    }

                    @Override
                    public void onResponse(Call call, Response response) {
                        try {
                            String json = response.body().string();
                            Logger.e(TAG, "getToken onResponse :" + json);
                            mGetTokenInfo = GsonUtils.fromJson(json, GetTokenInfo.class);
                            if (mGetTokenInfo != null && mGetTokenInfo.getCode() == 200) {
//                                SpUtils.putInt(UploadFileActivity.this, "videoId", mGetTokenInfo.getMap().getId());
                                uploadVideo();
                            } else if (mGetTokenInfo != null && mGetTokenInfo.getCode() == 105) {
                                showToastSafe(getResources().getString(R.string.in_black_list));
                                return;
                            } else {
                                showToastSafe(getResources().getString(R.string.get_token_fail));
                            }
                        } catch (
                                IOException e
                                )

                        {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(int errorCode) {
                        Logger.e(TAG, "getToken onFailure:" + errorCode);
                    }
                }

        );
    }


    /**
     * 上传视频
     */
    private void uploadVideo() {
        Logger.e(TAG, "uploadVideo:"+uploadFilePath);
        File file = new File(uploadFilePath);
        if (file.exists()) {
            if (mUploadManager == null) {
                //初始化上传相关类
                mUploadManager = new UploadManager();
            }
            mFileUploader = mUploadManager.put(uploadFilePath, mGetTokenInfo.getMap().getToken(),

                    new UpResponseHandler() {
                        @Override
                        public void onResponse(ResponseInfo info, JSONObject response) {
                            Logger.e(TAG, "onResponse:" + info.toString() + "       responseJson : " + response);
                            if (info.isOK()) {
                                showToastSafe(getResources().getString(R.string.upload_success));
                                Logger.e(TAG, "上传成功");
                                mUploadVideoResponseInfo = GsonUtils.fromJson(response.toString(), UploadVideoResponseInfo.class);
                                if (mUploadVideoResponseInfo != null) {
                                    tellServerVideoInfo(mUploadVideoResponseInfo.getResult().getFileid());
                                }
                            } else {
                                showToastSafe(getResources().getString(R.string.upload_fail));
                                Logger.e(TAG, "上传失败");
                            }
                        }
                    },
                    new UpProgressHandler() {
                        @Override
                        public void onProgress(double percent, int speed) {
                            Log.i(TAG, "onProgress:  " + "percent:" + percent + ",speed=" + speed);
                            mPbProgressTop.setProgress((int) (percent * 100));
                            mPbProgress.setProgress((int) (percent * 100));
                            if(percent==1.0){
                                mTvProgress.setText("100%");
                            }else {
                                mTvProgress.setText(String.format("%.2f%%", percent * 100));
                            }
                        }
                    });
        } else {
            showToastSafe(getResources().getString(R.string.no_upload_video));
        }
    }

    /**
     * 通知晓威服务器上传成功之后的视频信息
     *
     * @param fileId
     */
    private void tellServerVideoInfo(String fileId) {
        Map<String, String> paramsTotal = new LinkedHashMap<>();
        paramsTotal.put("id", Integer.toString(mGetTokenInfo.getMap().getId()));
        Map<String, Object> param = new LinkedHashMap<>();
        param.put("userId", LoginManager.getUid(UploadFileActivity.this));
        param.put("userName", LoginManager.getNicename(UploadFileActivity.this));
        param.put("userPhoto", LoginManager.getHeadPicUrl(UploadFileActivity.this));
        param.put("title", mTheme);
        param.put("classifyId", getClassfyId());
        param.put("fileid", fileId);
        param.put("filename", getFileName());
        String loactionStr = TextUtils.isEmpty(address) ?getResources().getString(R.string.unknown): address;
        param.put("location", loactionStr);
        String jsonParam = GsonUtils.toJson(param);
        Logger.e(TAG, "tellServerVideoInfo    paramsJson:" + jsonParam);
        paramsTotal.put("params", jsonParam);
        Map<String, String> headers = new HashMap<>();
        headers.put("token", LoginManager.getSsoTk(UploadFileActivity.this));
        OkHttpRequest.post("tellServerVideoInfo", RequestTag.UPLOAD_VIDEO_TACHOGRAPH, headers, paramsTotal, new PostCallBack() {
            @Override
            public void onFailure(Call call, IOException e) {
                isUploading = false;
                Logger.e(TAG, "转码失败:" + e.getMessage());
                showToastSafe(getResources().getString(R.string.transform_fail));
            }

            @Override
            public void onResponse(Call call, Response response) {
                isUploading = false;
                showToastSafe(getResources().getString(R.string.transform_success));
                try {
                    String responseString = response.body().string();
                    Logger.e(TAG, "转码成功   onResponse  responseString:" + responseString);
                    createShareContentText(responseString);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int errorCode) {
                isUploading = false;
                Logger.e(TAG, "转码失败 : " + errorCode);
                showToastSafe(getResources().getString(R.string.transform_fail));
            }
        });
    }

    /**
     * 创建分享内容bean
     *
     * @param responseString
     */
    private void createShareContentText(String responseString) {
        try {
            JSONObject jo = new JSONObject(responseString);
            String sharedUrl = jo.getString("obj");
            Logger.e(TAG, "文件分享后的H5 url ：" + sharedUrl);
            if (sharedUrl != null) {
                mShareContentText = new ShareContentText();
                mShareContentText.setTitle(mTheme);
                mShareContentText.setContent(mTabInfos.get((getClassfyId() - 1)).getName());
                mShareContentText.setURL(sharedUrl);
                String loactionStr = TextUtils.isEmpty(address) ? getResources().getString(R.string.unknown) : address;
                mShareContentText.setLocation(loactionStr);
                if (uploadFilePath.endsWith(".JPG") || uploadFilePath.endsWith(".jpg")) {
                    mShareContentText.setImagResource(uploadFilePath);
                } else {
                    mShareContentText.setImagResource(editedVideoThumbPath);
                }
                goShareActivity();
            } else {
                showToastSafe(getResources().getString(R.string.server_reurl_error));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void goShareActivity() {
        Intent intent = new Intent(UploadFileActivity.this, ShareActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("ShareImformation", mShareContentText);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    /**
     * 获取用户选择的分类id
     */
    private Integer getClassfyId() {
        Integer classfyId = null;
        switch (mRgClassfy.getCheckedRadioButtonId()) {
            case R.id.rb_1:
                if (mTabInfos != null) {
                    classfyId = mTabInfos.get(0).getId();
                } else {
                    classfyId = 1;
                }
                break;
            case R.id.rb_2:
                if (mTabInfos != null) {
                    classfyId = mTabInfos.get(1).getId();
                } else {
                    classfyId = 2;
                }
                break;
            case R.id.rb_3:
                if (mTabInfos != null) {
                    classfyId = mTabInfos.get(2).getId();
                } else {
                    classfyId = 3;
                }
                break;
            case R.id.rb_4:
                if (mTabInfos != null) {
                    classfyId = mTabInfos.get(3).getId();
                } else {
                    classfyId = 4;
                }
                break;
            case R.id.rb_5:
                if (mTabInfos != null) {
                    classfyId = mTabInfos.get(4).getId();
                } else {
                    classfyId = 5;
                }
                break;
            case R.id.rb_6:
                if (mTabInfos != null) {
                    classfyId = mTabInfos.get(5).getId();
                } else {
                    classfyId = 6;
                }
                break;
        }
        return classfyId;
    }

    /**
     * 获取要上传文件的名称
     *
     * @return
     */
    private String getFileName() {
        Logger.i(TAG, "uploadFilePath=" + uploadFilePath);
        String fileName = uploadFilePath.substring(uploadFilePath.lastIndexOf("/") + 1);
        Logger.i(TAG, "fileName=" + fileName);
        return fileName;
    }

    @UiThread
    void setProgressMax(int max) {
        mPbProgress.setMax(max);
        mPbProgressTop.setMax(max);
    }

    @UiThread
    void updateProgress(int current, String present) {
        Logger.i(TAG, "updateProgress:  " + "current:" + current + ",present=" + present);
        mPbProgress.setProgress(current);
        mPbProgressTop.setProgress(current);
        mTvProgress.setText(present + "%");
    }


    @Override
    public void releaseResources() {
        Logger.i(TAG, "releaseResources");
//        SpUtils.putInt(this, "videoId", -1);
        OkHttpRequest.cancelCurrentCall();
        mFileUploader = null;
        mUploadManager = null;
        mGetTokenInfo = null;
        if (mTabInfos != null) {
            mTabInfos.clear();
            mTabInfos = null;
        }
        mUploadVideoResponseInfo = null;
        mTheme = null;
        mShareContentText = null;
        File file = new File(Config.LOCAL_URL_PREFIX + uploadFilePath);
        if (file.exists()) {
            Logger.i(TAG, "releaseResources:file=  " + file);
            file.delete();
        }
        uploadFilePath  = null;
        editedVideoThumbPath = null;
    }
}
