package com.leautolink.leautocamera.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.leautolink.leautocamera.R;
import com.leautolink.leautocamera.application.LeautoCameraAppLication;
import com.leautolink.leautocamera.callback.CustomDialogCallBack;
import com.leautolink.leautocamera.callback.DelFileCallBack;
import com.leautolink.leautocamera.callback.SystemDialogCallBack;
import com.leautolink.leautocamera.config.Config;
import com.leautolink.leautocamera.domain.ListingInfo;
import com.leautolink.leautocamera.domain.respone.UpLoadResponse;
import com.leautolink.leautocamera.event.DelSucceedEvent;
import com.leautolink.leautocamera.event.GoToDownloadCameraVideoEvent;
import com.leautolink.leautocamera.event.VideoIsOnUsingEvent;
import com.leautolink.leautocamera.net.http.GsonUtils;
import com.leautolink.leautocamera.net.http.OkHttpRequest;
import com.leautolink.leautocamera.net.http.httpcallback.DownLoadCallBack;
import com.leautolink.leautocamera.net.http.httpcallback.UploadFileCallBack;
import com.leautolink.leautocamera.receivers.ScreenStateReceiver;
import com.leautolink.leautocamera.ui.adapter.GalleryPreviewPagerAdapter;
import com.leautolink.leautocamera.ui.base.BaseFragment;
import com.leautolink.leautocamera.ui.base.BaseFragmentActivity;
import com.leautolink.leautocamera.ui.fragment.GalleryPreviewListPagerFragment;
import com.leautolink.leautocamera.ui.fragment.GalleryPreviewListPagerFragment_;
import com.leautolink.leautocamera.ui.fragment.GalleryPreviewTopPagerFragment;
import com.leautolink.leautocamera.ui.fragment.GalleryPreviewTopPagerFragment_;
import com.leautolink.leautocamera.ui.view.customview.LoadingDiglog;
import com.leautolink.leautocamera.utils.CustomDialogUtils;
import com.leautolink.leautocamera.utils.DelUtils;
import com.leautolink.leautocamera.utils.Logger;
import com.leautolink.leautocamera.utils.SdCardUtils;
import com.leautolink.leautocamera.utils.SystemDialogUtils;
import com.leautolink.leautocamera.utils.ToastUtils;
import com.leautolink.leautocamera.utils.UrlUtils;
import com.leautolink.leautocamera.utils.VideoFileSharePathUtils;
import com.letv.leauto.cameracmdlibrary.connect.event.NotificationEvent;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;
import okhttp3.Call;
import okhttp3.Response;


/**
 * 预览
 * Created by tianwei1 on 2016/3/8.
 */
public class GalleryPreviewActivity extends BaseFragmentActivity implements View.OnClickListener {

    private static final String TAG = "GalleryPreviewActivity";
    //View相关
    private AppBarLayout mBarLayout;
    private ImageButton mBarLeft;
    private TextView mBarTitle;
    private ViewPager mVpTop;
    private RelativeLayout mContainer;
    private ViewPager mVpList;
    //    private ImageButton mIbDownload;
//    private ImageButton mIbShare;
//    private ImageButton mIbDel;
    private RelativeLayout mAllContainer;

    //下载分享删除的view
    //本地照片
    private LinearLayout mLocalPhotoDsd;
    private ImageButton mIbtnLocalPhotoDel;
    private ImageButton mIbtnLocalPhotoShare;
    //本地视频
    private LinearLayout mLocalVideoDsd;
    private ImageButton mIbtnLocalVideoDel;
    private ImageButton mIbtnLocalVideoShare;
    //记录仪
    private LinearLayout mCameraDsd;
    private ImageButton mIbtnCameraDel;
    private ImageButton mIbtnCameraDownload;

    //数据相关
    private int mCurrentPosition = 0;
    private ListingInfo mListingInfo;
    private List<ListingInfo.FileInfo> mFileInfos;
    private ListingInfo.FileInfo mCurrentFileInfo;
    private List<BaseFragment> mTopFragments;
    private List<BaseFragment> mListFragments;
    private GalleryPreviewPagerAdapter mTopAdapter;
    private GalleryPreviewPagerAdapter mListAdapter;
    //分享相关
//    private UMImage image;
    //标记相关
    private boolean isDownloading;
    private boolean isVideoOnPlaying;

    //监听相关
    private ScreenStateReceiver mScreenStateReceiver;

    public void onEventMainThread(NotificationEvent event) {
        //无sd卡
        if (NotificationEvent.SD_REMOVED == event.getType()) {

            SystemDialogUtils.showSingleConfirmDialog(this,getResources().getString(R.string.diglog_title), getResources().getString(R.string.diglog_mess), getResources().getString(R.string.diglog_button), new SystemDialogCallBack() {
                @Override
                public void onSure() {

                }

                @Override
                public void onCancel() {
                    ((LeautoCameraAppLication) getApplication()).popAllActivitys();
                }
            });
        }
    }

    public void onEventMainThread(VideoIsOnUsingEvent event) {
        Logger.e(TAG, "delete: " + event.isVideoCanDelete());
        if (event.isVideoCanDelete()) {
            mIbtnCameraDel.setVisibility(View.VISIBLE);
        } else {
            mIbtnCameraDel.setVisibility(View.GONE);

        }
        if (event.isVideoCanDownload()) {
            mIbtnCameraDownload.setVisibility(View.VISIBLE);
        } else {
            mIbtnCameraDownload.setVisibility(View.GONE);
        }
    }

    /**
     * 启动screen状态广播接收器
     */
    private void startScreenBroadcastReceiver() {
        Logger.e(TAG, "startScreenBroadcastReceiver");
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mScreenStateReceiver, filter);
    }

    /**
     * 停止screen状态更新
     */
    public void stopScreenBroadcastReceiver() {
        if (mScreenStateReceiver != null) {
            Logger.e(TAG, "ScreenBroadcastReceiver");
            unregisterReceiver(mScreenStateReceiver);
            mScreenStateReceiver = null;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_preview);
        if (mScreenStateReceiver == null) {
            mScreenStateReceiver = new ScreenStateReceiver();
        }

        startScreenBroadcastReceiver();


        ((LeautoCameraAppLication) getApplication()).pushActivity(this);
        getDataFromIntent();

        initView();
        initData();
        initAdapter();
        initListener();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            hideView();
        }
        super.onWindowFocusChanged(hasFocus);
    }

    /**
     * 从Intent中获取数据
     */
    private void getDataFromIntent() {
        Intent intent = getIntent();
        mCurrentPosition = intent.getIntExtra("currentPosition", 0);
        mListingInfo = (ListingInfo) intent.getSerializableExtra("listingInfo");
        mFileInfos = mListingInfo.getListing();
        mCurrentFileInfo = mFileInfos.get(mCurrentPosition);
    }

    /**
     * 初始化View
     */
    private void initView() {
        mBarLayout = (AppBarLayout) findViewById(R.id.bar_layout);
        mBarLeft = (ImageButton) findViewById(R.id.navigation_bar_left_ib);
        mBarTitle = (TextView) findViewById(R.id.navigation_bar_title);
        mVpTop = (ViewPager) findViewById(R.id.vp_top);
        mAllContainer = (RelativeLayout) findViewById(R.id.fl_all_contarner);
        mContainer = (RelativeLayout) findViewById(R.id.rl_container);
        mVpList = (ViewPager) findViewById(R.id.vp_list);
//        mIbDownload = (ImageButton) findViewById(R.id.ibtn_download);
//        mIbShare = (ImageButton) findViewById(R.id.ibtn_share);
//        mIbDel = (ImageButton) findViewById(R.id.ibtn_del);

        //本地照片
        mLocalPhotoDsd = (LinearLayout) findViewById(R.id.ll_local_photo_dsd);
        mIbtnLocalPhotoDel = (ImageButton) findViewById(R.id.ibtn_local_photo_del);
        mIbtnLocalPhotoShare = (ImageButton) findViewById(R.id.ibtn_local_photo_share);
        //本地视频
        mLocalVideoDsd = (LinearLayout) findViewById(R.id.ll_local_video_dsd);
        mIbtnLocalVideoDel = (ImageButton) findViewById(R.id.ibtn_local_video_del);
        mIbtnLocalVideoShare = (ImageButton) findViewById(R.id.ibtn_local_video_share);
        //记录仪
        mCameraDsd = (LinearLayout) findViewById(R.id.ll_camera_dsd);
        mIbtnCameraDel = (ImageButton) findViewById(R.id.ibtn_camera_del);
        mIbtnCameraDownload = (ImageButton) findViewById(R.id.ibtn_camera_download);


        //根据是本地还是记录仪分别初始化不同的功能
        initDownloadShareDelVisiable();

        mVpTop.setOffscreenPageLimit(0);
        mVpList.setOffscreenPageLimit(1);
    }

    /**
     * 初始化下载分享删除功能的可见性
     */
    private void initDownloadShareDelVisiable() {
        if (mListingInfo.isLocal()) {
            mCameraDsd.setVisibility(View.GONE);
            if (mCurrentFileInfo.isVideo()) {
                mLocalVideoDsd.setVisibility(View.VISIBLE);
                mLocalPhotoDsd.setVisibility(View.GONE);
            } else {
                mLocalVideoDsd.setVisibility(View.GONE);
                mLocalPhotoDsd.setVisibility(View.VISIBLE);
            }
        } else {
            //记录仪
            mCameraDsd.setVisibility(View.VISIBLE);
            mLocalVideoDsd.setVisibility(View.GONE);
            mLocalPhotoDsd.setVisibility(View.GONE);
        }
//        mIbDel.setVisibility(View.VISIBLE);
    }

    /**
     * 初始化数据
     */
    private void initData() {

        Logger.i(TAG, "mCurrentPosition:" + mCurrentPosition);
        Logger.i(TAG, "mListingInfo type :" + mListingInfo.getType());

        if (mTopFragments == null) {
            mTopFragments = new ArrayList<BaseFragment>();
        }

        for (int i = 0; i < mFileInfos.size(); i++) {
            GalleryPreviewTopPagerFragment gptpf = GalleryPreviewTopPagerFragment_.builder().build();
            mTopFragments.add(gptpf);
        }

        if (mListFragments == null) {
            mListFragments = new ArrayList<BaseFragment>();
        }

        for (int i = 0; i < mFileInfos.size(); i++) {
            GalleryPreviewListPagerFragment gplpf = GalleryPreviewListPagerFragment_.builder().build();
            gplpf.setData(i, mListingInfo.getType(), mFileInfos.get(i));
            mListFragments.add(gplpf);
        }

        //只初始化TOP当前展示页的数据
        mBarTitle.setText(mCurrentFileInfo.getPreviewTitle());
//        mBarTitle.setText(mCurrentFileInfo.getFilename());
        ((GalleryPreviewTopPagerFragment) mTopFragments.get(mCurrentPosition)).setData(mCurrentPosition, mListingInfo.getType(), mCurrentFileInfo);
    }

    /**
     * 初始化Adapter
     */
    private void initAdapter() {

        //TopAdapder
        if (mTopAdapter == null) {
            mTopAdapter = new GalleryPreviewPagerAdapter(getSupportFragmentManager(), mTopFragments);
            mVpTop.setAdapter(mTopAdapter);
            mVpTop.setCurrentItem(mCurrentPosition);
        } else {
            mTopAdapter.notifyDataSetChanged();
        }

        //ListAdapter
        if (mListAdapter == null) {
            mListAdapter = new GalleryPreviewPagerAdapter(getSupportFragmentManager(), mListFragments);
            mVpList.setAdapter(mListAdapter);
            mVpList.setCurrentItem(mCurrentPosition);
        } else {
            mListAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 删除之后刷新View
     */

    private void initViewAfterDelete() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCurrentFileInfo = mFileInfos.get(mCurrentPosition);
                Logger.i(TAG, "initViewAfterDelete --mCurrentPosition:" + mCurrentPosition);
                mBarTitle.setText(mCurrentFileInfo.getPreviewTitle());
//                mBarTitle.setText(mCurrentFileInfo.getFilename());
                ((GalleryPreviewTopPagerFragment) mTopFragments.get(mCurrentPosition)).setData(mCurrentPosition, mListingInfo.getType(), mCurrentFileInfo);
                ((GalleryPreviewTopPagerFragment) mTopFragments.get(mCurrentPosition)).init();
            }
        });
    }

    /**
     * 初始化监听
     */
    private void initListener() {

        mBarLeft.setOnClickListener(this);

        mVpTop.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Logger.i(TAG, "mVpTop" + "--mCurrentPosition:" + mCurrentPosition + "--position:" + position);
                //下面的ListPager跟着动
                if (mCurrentPosition != position)
                    mVpList.setCurrentItem(position);

                //将上一个,下一个页面的播放状态初始化

                if (mCurrentPosition < position) {//向左滑动，需要将上一个页面的播放状态初始化
                    ((GalleryPreviewTopPagerFragment) mTopFragments.get(position - 1)).releaseResourcesWhenSlide();
                } else if (mCurrentPosition > position) {
                    ((GalleryPreviewTopPagerFragment) mTopFragments.get(position + 1)).releaseResourcesWhenSlide();
                }

                mCurrentPosition = position;
                mCurrentFileInfo = mFileInfos.get(mCurrentPosition);
                Logger.i(TAG, "onPageSelected --mCurrentPosition:" + mCurrentPosition);
                mBarTitle.setText(mCurrentFileInfo.getPreviewTitle());
//                mBarTitle.setText(mCurrentFileInfo.getFilename());
                ((GalleryPreviewTopPagerFragment) mTopFragments.get(mCurrentPosition)).setData(mCurrentPosition, mListingInfo.getType(), mCurrentFileInfo);
                ((GalleryPreviewTopPagerFragment) mTopFragments.get(mCurrentPosition)).init();


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mVpList.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Logger.i(TAG, "mVpList" + "--mCurrentPosition:" + mCurrentPosition + "--position:" + position);
                if (mCurrentPosition != position)
                    mVpTop.setCurrentItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mVpList.onTouchEvent(event);
            }
        });

        initDownloadShareDelListener();
    }

    /**
     * 初始化下载分享删除Listener
     */
    private void initDownloadShareDelListener() {
        if (mListingInfo.isLocal()) { //本地
            if (mCurrentFileInfo.isVideo()) {//视频
                mIbtnLocalVideoDel.setOnClickListener(this);
                mIbtnLocalVideoShare.setOnClickListener(this);
            } else {//照片
                mIbtnLocalPhotoDel.setOnClickListener(this);
                mIbtnLocalPhotoShare.setOnClickListener(this);
            }
        } else {
            //记录仪
            mIbtnCameraDownload.setOnClickListener(this);
            mIbtnCameraDel.setOnClickListener(this);
        }

    }

    @Override
    public void releaseResources() {
        Logger.i(TAG, "releaseResources");
        mCurrentPosition = 0;
//        CleanListUtils.releaseListingInfo(mListingInfo);
        mCurrentFileInfo = null;
//        mListingInfo = null;

        //释放TopFragments
        if (mTopFragments != null && mTopFragments.size() > 0) {

            int mFragmentsSize = mTopFragments.size();
            for (int i = 0; i < mFragmentsSize; i++) {
                GalleryPreviewTopPagerFragment gptpf = (GalleryPreviewTopPagerFragment) mTopFragments.remove(0);
                gptpf.releaseResources();
                if (!mTopFragments.contains(gptpf)) {
                    gptpf = null;
                }
            }
            if (0 == mTopFragments.size()) {
                mTopFragments = null;
            }
        }

        //释放ListFragment
        if (mListFragments != null && mListFragments.size() > 0) {

            int mFragmentsSize = mListFragments.size();
            for (int i = 0; i < mFragmentsSize; i++) {
                GalleryPreviewListPagerFragment gplpf = (GalleryPreviewListPagerFragment) mListFragments.remove(0);
                gplpf.releaseResources();
                if (!mListFragments.contains(gplpf)) {
                    gplpf = null;
                }
            }
            if (0 == mListFragments.size()) {
                mListFragments = null;
            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((LeautoCameraAppLication) getApplication()).popActivity(GalleryPreviewActivity.this);
                Glide.get(GalleryPreviewActivity.this).clearMemory();
                stopScreenBroadcastReceiver();
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.navigation_bar_left_ib:
                this.finish();
                break;
            case R.id.ibtn_camera_download://下载
                if (!isDownloading) {
                    if (mCurrentFileInfo.getFilename().contains(".MP4")) {
                        EventBus.getDefault().post(new GoToDownloadCameraVideoEvent(true));
                    }
                    downloadSingle();
                }
                break;
            case R.id.ibtn_local_photo_share://分享本地照片
                shareLocalPhoto();
                break;

            case R.id.ibtn_local_video_share://分享本地视频
                shareLocalVideo();
                break;
            case R.id.ibtn_local_photo_del://删除本地照片
                delSingle();
                break;
            case R.id.ibtn_local_video_del://删除本地视频
                delSingle();
                break;
            case R.id.ibtn_camera_del://删除记录仪文件
                delSingle();
                break;
        }
    }

    /**
     * 单个下载
     */
    private void downloadSingle() {
        //判断是否下载
        if (SdCardUtils.isExists(UrlUtils.getTargetPath(mListingInfo.getType(), this) + File.separator + mCurrentFileInfo.getFilename())) {
            showToastSafe(getResources().getString(R.string.download_success2));
            return;
        }
        isDownloading = true;
        OkHttpRequest.downLoad("downloadSingle", UrlUtils.getCameraMvideoHttpUrl(mListingInfo.getType(), mCurrentFileInfo.getFilename()), UrlUtils.getTargetPath(mListingInfo.getType(), this), mCurrentFileInfo.getFilename(), SdCardUtils.getSdSize(this, SdCardUtils.TYPE_AVAIABLE), new DownLoadCallBack() {
            @Override
            public void onFailure(Call call, IOException e) {
                isDownloading = false;
            }

            @Override
            public void onStart(long total) {

                CustomDialogUtils.showDialog(GalleryPreviewActivity.this, new CustomDialogCallBack() {
                    @Override
                    public void onCancel() {
                        OkHttpRequest.setCancel(true);
                    }
                });
            }

            @Override
            public void onLoading(long current, long total) {
                CustomDialogUtils.setCurrentTotal(SdCardUtils.formateSize(GalleryPreviewActivity.this, current) + "/" + SdCardUtils.formateSize(GalleryPreviewActivity.this, total));
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
                ToastUtils.showToast(GalleryPreviewActivity.this,getResources().getString(R.string.download_success), ToastUtils.SHORT);
                isDownloading = false;
            }

            @Override
            public void onSdCardLackMemory(long total, long avaiable) {

                SystemDialogUtils.showSingleConfirmDialog(GalleryPreviewActivity.this, getResources().getString(R.string.diglog_title), getResources().getString(R.string.storage_less),getResources().getString(R.string.diglog_button), new SystemDialogCallBack() {
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
                ToastUtils.showToast(GalleryPreviewActivity.this, getResources().getString(R.string.download_cacel1), ToastUtils.SHORT);
                //删除下载不完整的文件
                OkHttpRequest.deleteIntactFile(UrlUtils.getTargetPath(mListingInfo.getType(), GalleryPreviewActivity.this) + "/" + mCurrentFileInfo.getFilename());
                isDownloading = false;
            }

            @Override
            public void onError(IOException e) {

                CustomDialogUtils.hideCustomDialog();
                ToastUtils.showToast(GalleryPreviewActivity.this, getResources().getString(R.string.download_error), ToastUtils.SHORT);
                isDownloading = false;
            }
        });
    }

    Map<String, String> params;
    Map<String, String> map2;
    Map<String, String> map;
    private UpLoadResponse mUpLoadResponse;

    private void clearUploadMaps() {
        if (map != null) {
            map.clear();
            map = null;
        }
        if (params != null) {
            params.clear();
            params = null;
        }
        if (map2 != null) {
            map2.clear();
            map2 = null;
        }

    }

//    private void shareSingle() {
////        image = new UMImage(GalleryPreviewActivity.this, BitmapFactory.decodeFile(UrlUtils.getDeletLocalFileUrl(this, mListingInfo.getType(), mCurrentFileInfo.getFilename())));
//        image = new UMImage(GalleryPreviewActivity.this, BitmapFactory.decodeResource(getResources(), R.drawable.banner_01));
//        new ShareAction(GalleryPreviewActivity.this).setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE).setCallback(umShareListener)
//                .withMedia(image)
//                .withTargetUrl("http://www.leautolink.com/tirohelp.html")
////                .withText("我在使用高大上的乐视行车记录仪，快来围观我的美皂")
//                        //.withMedia(new UMEmoji(ShareActivity.this,"http://img.newyx.net/news_img/201306/20/1371714170_1812223777.gif"))
//                .share();
//
//    }

    private void shareSingleForServerNew() {
        params = new LinkedHashMap<>();
        map2 = new LinkedHashMap<>();
        map = new LinkedHashMap<>();
        params.put("modulename", "camera");
        params.put("status", "1");
        params.put("file", mCurrentFileInfo.getFilename());
        params.put("tag", "ext-gen328");
        map.put("file", mCurrentFileInfo.getFilename());
        params.put("data", GsonUtils.toJson(map));
        params.put("tokenid", "fc8e42eb8fd1474d8f040c18f442190b");
        map2.put("parameter", GsonUtils.toJson(params).replace("\\", "").replace("\"{\"", "{\"").replace("\"}\"", "\"}"));

        OkHttpRequest.uploadFile("upload", Config.UPLOAD_SERVER_URL, UrlUtils.getDeletLocalFileUrl(this, mListingInfo.getType(), mCurrentFileInfo.getFilename()), map2, new UploadFileCallBack() {


            @Override
            public void onStart(long total) {
                showLoading(getResources().getString(R.string.shareing));
            }

            @Override
            public void onLoading(long total, long current) {

            }


            @Override
            public void onFinish() {
                hideLoading();
            }


            @Override
            public void onResponse(Call call, Response response) {

                try {
                    String json = response.body().string();
                    Logger.i(TAG, "onResponse  :response:" + json);
                    if (json.contains("file")) {
                        mUpLoadResponse = GsonUtils.fromJson(json, UpLoadResponse.class);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                final SHARE_MEDIA[] displaylist = new SHARE_MEDIA[]
//                                        {
//                                                SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE
//                                        };
//                                image = new UMImage(GalleryPreviewActivity.this, mUpLoadResponse.getUrl());
//                                new ShareAction(GalleryPreviewActivity.this).setDisplayList(displaylist)
//                                        .withText("乐视行车记录仪")
//                                        .withTitle("我在使用高大上的乐视行车记录仪，快来围观我的照片")
//                                        .withTargetUrl(mUpLoadResponse.getUrl())
//                                        .withMedia(image)
//                                        .setListenerList(umShareListener, umShareListener)
////                                        .setShareboardclickCallback(new ShareBoardlistener() {
////                                            @Override
////                                            public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
////
////                                            }
////                                        })
//                                        .open();
//                                new ShareAction(GalleryPreviewActivity.this).setPlatform(SHARE_MEDIA.WEIXIN).setCallback(umShareListener)
//                                        .withMedia(image)
//                                        .withTargetUrl(mUpLoadResponse.getUrl())
//                                        .withText("我在使用高大上的乐视行车记录仪，快来围观我的照片")
//                                                //.withMedia(new UMEmoji(ShareActivity.this,"http://img.newyx.net/news_img/201306/20/1371714170_1812223777.gif"))
//                                        .share();
                                clearUploadMaps();
                            }
                        });
                    } else {
                        ToastUtils.showToast(GalleryPreviewActivity.this,getResources().getString(R.string.server_error), ToastUtils.SHORT);
                        clearUploadMaps();
                    }

                } catch (IOException e) {
                    clearUploadMaps();
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancel() {
                CustomDialogUtils.hideCustomDialog();
                showToastSafe(getResources().getString(R.string.share_cancel));
            }

            @Override
            public void onTimeOut() {
                CustomDialogUtils.hideCustomDialog();
            }

            @Override
            public void onFailure(Call call, IOException e) {
                showToastSafe(getResources().getString(R.string.share_error));
                CustomDialogUtils.hideCustomDialog();
            }

            @Override
            public void onError(Object e) {
                CustomDialogUtils.hideCustomDialog();
            }


        });
    }

    /**
     * 单个分享
     */
   /* private void shareSingleForServer() {


        Logger.i(TAG, "shareSingle");
        showShareLoadingDialog();
        params = new LinkedHashMap<>();
        map2 = new LinkedHashMap<>();
        map = new LinkedHashMap<>();
        params.put("modulename", "camera");
        params.put("status", "1");
        params.put("file", mCurrentFileInfo.getFilename());
        params.put("tag", "ext-gen328");
        map.put("file", mCurrentFileInfo.getFilename());
        params.put("data", GsonUtils.toJson(map));
        params.put("tokenid", "fc8e42eb8fd1474d8f040c18f442190b");
        map2.put("parameter", GsonUtils.toJson(params).replace("\\", "").replace("\"{\"", "{\"").replace("\"}\"", "\"}"));
        Logger.i(TAG, "上传的Json：" + GsonUtils.toJson(params).replace("\\", "").replace("\"{\"", "{\"").replace("\"}\"", "\"}"));

        OkHttpRequest.upLoadUseThirdJar(Config.UPLOAD_SERVER_URL, UrlUtils.getDeletLocalFileUrl(this, mListingInfo.getType(), mCurrentFileInfo.getFilename()), map2, 3000, 3000, "upload", new UpLoadCallBack() {

            @Override
            public void onFailure(Call call, Exception e) {
                clearUploadMaps();
                hideShareLoadingDialog();
                ToastUtils.showToast(GalleryPreviewActivity.this, "分享失败，请检查网络设置", ToastUtils.SHORT);
            }

            @Override
            public void onUIProgress(Progress progress) {
//                if (progress.getCurrentBytes() == progress.getTotalBytes()) {
//                    hideLoading();
//                }

                Logger.i(TAG, "onUIProgress： current:" + progress.getCurrentBytes() + "---total:" + progress.getTotalBytes());
            }

            @Override
            public void onResponse(Call call, Response response) {

                try {
                    final String json = response.body().string();
                    Logger.i(TAG, "onResponse  :response:" + json);
                    if (json.contains("file")) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mUpLoadResponse = GsonUtils.fromJson(json, UpLoadResponse.class);

                                final SHARE_MEDIA[] displaylist = new SHARE_MEDIA[]
                                        {
                                                SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE
                                        };
                                image = new UMImage(GalleryPreviewActivity.this, mUpLoadResponse.getUrl());
                                new ShareAction(GalleryPreviewActivity.this).setDisplayList(displaylist)
                                        .withText("乐视行车记录仪")
                                        .withTitle("我在使用高大上的乐视行车记录仪，快来围观我的照片")
                                        .withTargetUrl(mUpLoadResponse.getUrl())
                                        .withMedia(image)
                                        .setListenerList(umShareListener, umShareListener)
//                                        .setShareboardclickCallback(new ShareBoardlistener() {
//                                            @Override
//                                            public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
//
//                                            }
//                                        })
                                        .open();
//                                new ShareAction(GalleryPreviewActivity.this).setPlatform(SHARE_MEDIA.WEIXIN).setCallback(umShareListener)
//                                        .withMedia(image)
//                                        .withTargetUrl(mUpLoadResponse.getUrl())
//                                        .withText("我在使用高大上的乐视行车记录仪，快来围观我的照片")
//                                                //.withMedia(new UMEmoji(ShareActivity.this,"http://img.newyx.net/news_img/201306/20/1371714170_1812223777.gif"))
//                                        .share();
                                clearUploadMaps();
                                hideShareLoadingDialog();
                            }
                        });
                    } else {
                        ToastUtils.showToast(GalleryPreviewActivity.this, "对不起，服务器出现故障,请耐心等待", ToastUtils.SHORT);
                        clearUploadMaps();
                        hideShareLoadingDialog();
                    }

                } catch (IOException e) {
                    clearUploadMaps();
                    hideShareLoadingDialog();
                    e.printStackTrace();
                }
            }
        });
//        if (mUpLoadResponse == null) {
//            showToastSafe("分享失败，请检查网络设置");
//        } else {
//            image = new UMImage(this, mUpLoadResponse.getUrl());
//        }
    }*/

   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            showToastSafe("分享成功");
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            showToastSafe("分享失败，请检查网络设置");
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            showToastSafe("分享已取消");
        }
    };
*/

    /**
     * 单个删除
     */
    private void delSingle() {
        Logger.i(TAG, "mCurrentPosition:" + mCurrentPosition);
        if (mListingInfo.isLocal()) {
            DelUtils.deleteLocalSingleFile(this, UrlUtils.getDeletLocalFileUrl(this, mListingInfo.getType(), mCurrentFileInfo.getFilename()), "温馨提示", "确定要删除么？", "确定", "取消", new DelFileCallBack() {
                @Override
                public void onFailure() {
                    ToastUtils.showToast(GalleryPreviewActivity.this, getResources().getString(R.string.delete_fail), ToastUtils.SHORT);
                }

                @Override
                public void onSucceed() {
                    ToastUtils.showToast(GalleryPreviewActivity.this, getResources().getString(R.string.delete_success), ToastUtils.SHORT);
                    flashDataWhenDelete();
                }
            });
        } else {
            DelUtils.deleteCameraSingleFile(this, UrlUtils.getDeleteCameraFileUrl(mListingInfo.getType(), mCurrentFileInfo.getFilename()), "温馨提示", "确定要删除么？", "确定", "取消", new DelFileCallBack() {
                @Override
                public void onFailure() {
                    ToastUtils.showToast(GalleryPreviewActivity.this, getResources().getString(R.string.delete_fail), ToastUtils.SHORT);
                }

                @Override
                public void onSucceed() {
                    ToastUtils.showToast(GalleryPreviewActivity.this, getResources().getString(R.string.delete_success), ToastUtils.SHORT);
                    flashDataWhenDelete();
                }
            });
        }
    }

    private void flashDataWhenDelete() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //将删除的对象从List中移除
                Logger.i(TAG, "flashDataWhenDelete");

                mTopFragments.remove(mCurrentPosition);

                mListFragments.remove(mCurrentPosition);


                ListingInfo.FileInfo removed = mFileInfos.remove(mCurrentPosition);
                if (!mFileInfos.contains(removed)) {
                    Logger.i(TAG, "removed:" + removed.toString());
                    removed = null;
                }

                //告诉GalleryRecyclerAdapter有东西删除了，刷新列表
                EventBus.getDefault().post(new DelSucceedEvent(mCurrentPosition, mListingInfo.getType()));

                updateCurrentPosition();


                mListAdapter.notifyDataSetChanged();
                mTopAdapter.notifyDataSetChanged();
                Logger.i(TAG, "删除完成，mCurrentPosition：" + mCurrentPosition);
//                mVpList.setCurrentItem(mCurrentPosition);
            }
        });
    }

    /**
     * 更新CurrentPosition
     */
    private void updateCurrentPosition() {
        if (0 == mFileInfos.size()) {
            releaseResources();
            this.finish();
        } else if (mCurrentPosition == mFileInfos.size()) {
            mCurrentPosition -= 1;
            Logger.i(TAG, "mCurrentPosition 减一 :" + mCurrentPosition + "--mFileInfos.size():" + mFileInfos.size());
        } else {

            Logger.i(TAG, "Position 没有发生变化 mCurrentPosition:" + mCurrentPosition + "--mFileInfos.size():" + mFileInfos.size());
            initViewAfterDelete();
        }
    }

    /**
     * 播放视频的时候处理View
     *
     * @param isOnPlaying
     */
    public void progressViewWhenPlayingVideo(boolean isOnPlaying) {

        if (isOnPlaying) {
            hideView();
        } else {
            showView();
        }
    }

    private void hideView() {
        Logger.i(TAG, "hideView");
        mBarLayout.setVisibility(View.GONE);
        mAllContainer.setVisibility(View.GONE);
    }

    private void showView() {
        Logger.i(TAG, "showView");
        mBarLayout.setVisibility(View.VISIBLE);
        mAllContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {//横屏
            Logger.i(TAG, "横屏");
            hideView();
        } else {//竖屏
            Logger.i(TAG, "竖屏");
            showView();
        }
        super.onConfigurationChanged(newConfig);
    }

    private Dialog mLoadingViewDialog;

    private void showShareLoadingDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mLoadingViewDialog == null) {
                    mLoadingViewDialog = LoadingDiglog.createLoadingDialog(GalleryPreviewActivity.this, "分享中...");
                    mLoadingViewDialog.setCanceledOnTouchOutside(false);
                    mLoadingViewDialog.setCancelable(false);
                    mLoadingViewDialog.show();
                }
            }
        });
    }

    private void hideShareLoadingDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mLoadingViewDialog != null && mLoadingViewDialog.isShowing()) {
                    mLoadingViewDialog.dismiss();
                    mLoadingViewDialog = null;
                }
            }
        });
    }

    private String getSourcePhotoPath() {
        return mCurrentFileInfo.getLocalFileUrl().substring(7);
    }

    /**
     * 分享本地图片
     */
    private void shareLocalPhoto() {
        UploadFileActivity_.intent(this).extra("uploadFilePath", getSourcePhotoPath()).start();
    }

    /**
     * 分享本地视频
     */
    private void shareLocalVideo() {
        //如果该视频正在播放，那么停止播放并且释放播放器
        ((GalleryPreviewTopPagerFragment) mTopFragments.get(mCurrentPosition)).releaseResourcesWhenSlide();
        VideoFileSharePathUtils videoFileSharePathUtils = new VideoFileSharePathUtils();
        videoFileSharePathUtils.SetContext(this);
        String videoPath = mCurrentFileInfo.getLocalFileUrl().substring(7);
        MediaMetadataRetriever media = videoFileSharePathUtils.Media(videoPath);
        String time = media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        int videoDuration = Integer.parseInt(time);
        Logger.e(TAG, "videoDuration :" + videoDuration + ",time=" + time);
        if (videoDuration < 17000) {
            videoFileSharePathUtils.startCapturePics(videoPath);
            videoFileSharePathUtils.saveThumb2Sd(videoPath);
            UploadFileActivity_.intent(this).extra("uploadFilePath", videoPath).extra("editedVideoThumbPath", videoFileSharePathUtils.getTargetThumbPath(videoPath)).start();
        } else {
            VideoEditActivity_.intent(this).extra("localVideoUrl", mCurrentFileInfo.getLocalFileUrl()).start();
        }
    }

}
