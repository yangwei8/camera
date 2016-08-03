package com.leautolink.leautocamera.ui.activity;

import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
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
import com.leautolink.leautocamera.domain.HomePhotoInfo;
import com.leautolink.leautocamera.domain.ListingInfo;
import com.leautolink.leautocamera.event.IsSelectEvent;
import com.leautolink.leautocamera.event.MostDelInfoEvent;
import com.leautolink.leautocamera.event.MultiDelInfosSuccessEvent;
import com.leautolink.leautocamera.net.http.GsonUtils;
import com.leautolink.leautocamera.net.http.OkHttpRequest;
import com.leautolink.leautocamera.net.http.httpcallback.DownLoadCallBack;
import com.leautolink.leautocamera.ui.adapter.GalleryPagerAdapter;
import com.leautolink.leautocamera.ui.base.BaseFragment;
import com.leautolink.leautocamera.ui.base.BaseFragmentActivity;
import com.leautolink.leautocamera.ui.fragment.GalleryPagerFragment;
import com.leautolink.leautocamera.ui.fragment.GalleryPagerFragment_;
import com.leautolink.leautocamera.ui.fragment.SettingFragment;
import com.leautolink.leautocamera.ui.view.customview.MaterialDialog;
import com.leautolink.leautocamera.utils.CleanListUtils;
import com.leautolink.leautocamera.utils.CustomDialogUtils;
import com.leautolink.leautocamera.utils.DelUtils;
import com.leautolink.leautocamera.utils.Logger;
import com.leautolink.leautocamera.utils.SdCardUtils;
import com.leautolink.leautocamera.utils.SequenceUtils;
import com.leautolink.leautocamera.utils.SystemDialogUtils;
import com.leautolink.leautocamera.utils.ToastUtils;
import com.leautolink.leautocamera.utils.UrlUtils;
import com.letv.leauto.cameracmdlibrary.connect.RemoteCamHelper;
import com.letv.leauto.cameracmdlibrary.connect.event.NotificationEvent;
import com.letv.leauto.cameracmdlibrary.connect.model.CameraMessage;
import com.letv.leauto.cameracmdlibrary.connect.model.CameraMessageCallback;
import com.letv.leauto.cameracmdlibrary.connect.model.CommandID;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import okhttp3.Call;

/**
 * Created by tianwei1 on 2016/3/6.
 */
@EActivity(R.layout.activity_cameragallery)
public class CameraGalleryActivity extends BaseFragmentActivity implements View.OnClickListener {
    //常量
    private static final int TAB_COUNT = 3;
    private static final String TAG = "CameraGalleryActivity";

    //初始化View
    @ViewById(R.id.navigation_bar_left_ib)
    ImageButton mIvBtnGoBack;
    @ViewById(R.id.navigation_bar_right_bt)
    Button navigation_bar_right_bt;

    @ViewById(R.id.navigation_bar_title)
    TextView mTvTitle;
    @ViewById(R.id.activity_camera_gallery_event_btn)
    Button mEventBtn;
    @ViewById(R.id.activity_camera_gallery_photo_btn)
    Button mPhotoBtn;
    @ViewById(R.id.activity_camera_gallery_normal_btn)
    Button mNormalBtn;
    @ViewById(R.id.activity_camera_gallery_pager)
    ViewPager mVp;
    @ViewById(R.id.gallery_bottom_bar)
    LinearLayout gallery_bottom_bar;
    @ViewById(R.id.ibtn_del)
    ImageButton ibtn_del;
    @ViewById(R.id.ibtn_download)
    ImageButton ibtn_download;
    @ViewById(R.id.rl_no_data)
    RelativeLayout mRlNoData;

    @Extra
    int eventOrPhoto = 1;

    //数据相关
    private List<BaseFragment> mFragmengts;
    private GalleryPagerAdapter mAdapter;
    private ListingInfo mEventListingInfo;
    private ListingInfo mPhotoListingInfo;
    private ListingInfo mNormalListingInfo;
    private MaterialDialog alertDialog2;
    //标记相关
    private boolean isFirstCreateActivity = false;
    private boolean selecting = false;
    private boolean isNoEventData = false;
    private boolean isNoPhotoData = false;
    private boolean isNoNormalData = false;
    private boolean isDownloading = false;

    private String type = "event";

    private List<Integer> selectedIndexs;

    private List<Integer> delSuccessIndexs;
    private List<Integer> downloadSuccessIndexs;

    public void onEventMainThread(NotificationEvent event) {
        if (isOnResume) {
            //无sd卡
            if (NotificationEvent.SD_REMOVED == event.getType()) {
                SystemDialogUtils.showSingleConfirmDialog(this, getResources().getString(R.string.diglog_title), getResources().getString(R.string.diglog_mess), getResources().getString(R.string.diglog_button), new SystemDialogCallBack() {
                    @Override
                    public void onSure() {

                    }

                    @Override
                    public void onCancel() {
                        CameraGalleryActivity.this.finish();
                    }
                });
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ListingInfo currentListingInfo = getCurrentFileInfos(mVp.getCurrentItem());
        if (currentListingInfo == null || (currentListingInfo != null && currentListingInfo.getListing().size() == 0)) {
            showNoDataView(true);
        } else {
            showNoDataView(false);
        }
    }

    @AfterViews
    void init() {
        ((LeautoCameraAppLication) getApplication()).pushActivity(this);
        isFirstCreateActivity = true;
        initView();
        initData();
        initListener();

    }


    /**
     * 初始化View
     */
    private void initView() {
        mTvTitle.setText(R.string.camera_album);
        mVp.setOffscreenPageLimit(2);
        navigation_bar_right_bt.setVisibility(View.VISIBLE);
        navigation_bar_right_bt.setTextColor(Color.rgb(20, 150, 177));
        showNoDataView(false);
        mEventBtn.setTextColor(Color.rgb(20, 150, 177));
        mPhotoBtn.setTextColor(Color.rgb(155, 155, 155));
        mNormalBtn.setTextColor(Color.rgb(155, 155, 155));
    }

    @UiThread
    void showNoDataView(boolean isNoData) {
        if (isNoData) {
            mRlNoData.setVisibility(View.VISIBLE);
        } else {
            mRlNoData.setVisibility(View.GONE);
        }
    }


    /**
     * 初始化数据
     */
    private void initData() {
        //初始化ViewPager中的Fragment

        if (mFragmengts == null)
            Logger.e("adsf", "new Fragments");
        mFragmengts = new ArrayList<BaseFragment>();
        for (int i = 0; i < TAB_COUNT; i++) {
            GalleryPagerFragment cgpf = GalleryPagerFragment_.builder().build();
            mFragmengts.add(cgpf);
        }

        //从记录仪上获取突发事件的数据
        getEventDataFromCamera();
    }


    /**
     * 初始化Adapter
     */
    @UiThread
    void initAdapter() {
        if (mAdapter == null) {
            Logger.i(TAG, "initViewPagerAdapter");
            mAdapter = new GalleryPagerAdapter(getSupportFragmentManager(), mFragmengts);
            mVp.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
            Logger.i(TAG, "notifyViewPagerAdapter");
        }
        if (eventOrPhoto == HomePhotoInfo.NORMAL_PIC) {
            mVp.setCurrentItem(1);
        }
    }

    /**
     * 初始化监听
     */
    private void initListener() {
        //ViewPager的滑动监听
        mVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Logger.i(TAG, "onPageSelected:" + position);
                if (selecting) {
                    setSelecting(false);
                }
                showNoDataView(false);
                switch (position) {
                    case 0:
                        type = "event";
                        changeTabBtnTextColor(0);
//                        CleanListUtils.releaseListingInfo(mEventListingInfo);
                        getEventDataFromCamera();
                        break;
                    case 1:
                        type = "photo";
                        changeTabBtnTextColor(1);
//                        CleanListUtils.releaseListingInfo(mPhotoListingInfo);
                        getPhotoDataFromCamera();
                        break;
                    case 2:
                        type = "normal";
                        changeTabBtnTextColor(2);
//                        CleanListUtils.releaseListingInfo(mNormalListingInfo);
                        getNormalDataFromCamera();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mEventBtn.setOnClickListener(this);
        mPhotoBtn.setOnClickListener(this);
        mNormalBtn.setOnClickListener(this);
        mIvBtnGoBack.setOnClickListener(this);
        navigation_bar_right_bt.setOnClickListener(this);

    }

    /**
     * 改变TabBtn文字的颜色
     *
     * @param btnId
     */
    private void changeTabBtnTextColor(int btnId) {
        switch (btnId) {
            case 0:
                mEventBtn.setTextColor(Color.rgb(20, 150, 177));
                mPhotoBtn.setTextColor(Color.rgb(155, 155, 155));
                mNormalBtn.setTextColor(Color.rgb(155, 155, 155));
                break;
            case 1:
                mEventBtn.setTextColor(Color.rgb(155, 155, 155));
                mPhotoBtn.setTextColor(Color.rgb(20, 150, 177));
                mNormalBtn.setTextColor(Color.rgb(155, 155, 155));
                break;
            case 2:
                mEventBtn.setTextColor(Color.rgb(155, 155, 155));
                mPhotoBtn.setTextColor(Color.rgb(155, 155, 155));
                mNormalBtn.setTextColor(Color.rgb(20, 150, 177));
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_camera_gallery_event_btn:
                mVp.setCurrentItem(0);
                break;
            case R.id.activity_camera_gallery_photo_btn:
                mVp.setCurrentItem(1);
                break;
            case R.id.activity_camera_gallery_normal_btn:
                mVp.setCurrentItem(2);
                break;
            case R.id.navigation_bar_left_ib:
                this.finish();
                break;
            case R.id.navigation_bar_right_bt:
                if ((mVp.getCurrentItem() == 0 && isNoEventData) || (mVp.getCurrentItem() == 1 && isNoPhotoData) || (mVp.getCurrentItem() == 2 && isNoNormalData)) {
                    return;
                }
                setSelecting(!selecting);
                break;
        }
    }

    /**
     * 从记录仪上获取突发视频的数据
     */
    private void getEventDataFromCamera() {
        Logger.i(TAG, "getEventDataFromCamera()");
        showLoading(getResources().getString(R.string.onprogress));
        CameraMessage getLSMessage = new CameraMessage(CommandID.AMBA_LS_NEW, new CameraMessageCallback() {
            @Override
            public void onReceiveErrorMessage(CameraMessage cameraMessage, JSONObject jsonObject) {
                Logger.e(TAG, "onReceiveErrorMessage" + jsonObject.toString());
                if (isFirstCreateActivity) {
                    initAdapter();
                    isFirstCreateActivity = false;
                }
                hideLoading();
            }

            @Override
            public void onReceiveMessage(CameraMessage cameraMessage, JSONObject jsonObject) {
                Logger.e(TAG, "onReceiveMessage:" + jsonObject.toString());
                mEventListingInfo = GsonUtils.fromJson(jsonObject.toString(), ListingInfo.class);
                //显示暂无数据
                if (mEventListingInfo != null && 0 == mEventListingInfo.getListing().size()) {
                    showNoDataView(true);
                    isNoEventData = true;
                    if (isFirstCreateActivity) {
                        initAdapter();
                        isFirstCreateActivity = false;
                    }
                } else {
                    showNoDataView(false);
                    isNoEventData = false;
                    if (mFragmengts == null) {
                        return;
                    }
                    //排序
                    new SequenceUtils().sequencePureDatas(mEventListingInfo.getListing());
                    Logger.i(TAG, "排序后的  EventListingInfo:" + mEventListingInfo.toString());
                    //只初始化Event页的数据
                    ((GalleryPagerFragment) mFragmengts.get(0)).setData(0, mEventListingInfo);
                    if (isFirstCreateActivity) {
                        initAdapter();
                        isFirstCreateActivity = false;
                    }
                    ((GalleryPagerFragment) mFragmengts.get(0)).initAdapter();
                }
                hideLoading();
            }

            @Override
            public void onReceiveNotification(JSONObject jsonObject) {

            }
        });
        getLSMessage.put("type", "event");

        RemoteCamHelper.getRemoteCam().sendCommand(getLSMessage);
    }

    /**
     * 从记录仪上获取手动记录的数据
     */
    private void getPhotoDataFromCamera() {
        Logger.i(TAG, "getPhotoDataFromCamera()");
        showLoading(getResources().getString(R.string.onprogress));
        CameraMessage getLSMessage = new CameraMessage(CommandID.AMBA_LS_NEW, new CameraMessageCallback() {
            @Override
            public void onReceiveErrorMessage(CameraMessage cameraMessage, JSONObject jsonObject) {
                Logger.e(TAG, "onReceiveErrorMessage" + jsonObject.toString());
                hideLoading();
            }

            @Override
            public void onReceiveMessage(CameraMessage cameraMessage, JSONObject jsonObject) {
                Logger.e(TAG, "onReceiveMessage:" + jsonObject.toString());
                //正序的
                mPhotoListingInfo = GsonUtils.fromJson(jsonObject.toString(), ListingInfo.class);
                //显示暂无数据
                if (mPhotoListingInfo != null && mPhotoListingInfo.getListing().size() == 0) {
                    isNoPhotoData = true;
                    showNoDataView(true);
                } else {
                    showNoDataView(false);
                    isNoPhotoData = false;
                    //排序
                    new SequenceUtils().sequencePureDatas(mPhotoListingInfo.getListing());
                    Logger.i(TAG, "排序后的   PhotoListingInfo:" + mPhotoListingInfo.toString());
                    //只初始化手动页的数据
                    ((GalleryPagerFragment) mFragmengts.get(1)).setData(1, mPhotoListingInfo);
                    ((GalleryPagerFragment) mFragmengts.get(1)).initAdapter();
                }
                hideLoading();
            }

            @Override
            public void onReceiveNotification(JSONObject jsonObject) {

            }
        });
        getLSMessage.put("type", "photo");

        RemoteCamHelper.getRemoteCam().sendCommand(getLSMessage);
    }

    /**
     * 从记录仪上获取普通视频的数据
     */
    private void getNormalDataFromCamera() {
        Logger.i(TAG, "getNormalDataFromCamera()");
        showLoading(getResources().getString(R.string.onprogress));
        CameraMessage getLSMessage = new CameraMessage(CommandID.AMBA_LS_NEW, new CameraMessageCallback() {
            @Override
            public void onReceiveErrorMessage(CameraMessage cameraMessage, JSONObject jsonObject) {
                Logger.e(TAG, "onReceiveErrorMessage" + jsonObject.toString());
                hideLoading();
            }

            @Override
            public void onReceiveMessage(CameraMessage cameraMessage, JSONObject jsonObject) {
                Logger.e(TAG, "onReceiveMessage:" + jsonObject.toString());
                mNormalListingInfo = GsonUtils.fromJson(jsonObject.toString(), ListingInfo.class);
                //显示暂无数据
                if (mNormalListingInfo != null && mNormalListingInfo.getListing().size() == 0) {
                    isNoNormalData = true;
                    showNoDataView(true);
                } else {
                    isNoNormalData = false;
                    showNoDataView(false);
                    //排序
                    new SequenceUtils().sequencePureDatas(mNormalListingInfo.getListing());
                    Logger.i(TAG, "排序后的  NormalListingInfo:" + mNormalListingInfo.toString());
                    //只初始化Noraml页的数据
                    if (mFragmengts != null) {
                        ((GalleryPagerFragment) mFragmengts.get(2)).setData(2, mNormalListingInfo);
                        ((GalleryPagerFragment) mFragmengts.get(2)).initAdapter();
                    }
                }
                hideLoading();
            }

            @Override
            public void onReceiveNotification(JSONObject jsonObject) {

            }
        });
        getLSMessage.put("type", "normal");

        RemoteCamHelper.getRemoteCam().sendCommand(getLSMessage);
    }

    @Override
    public void releaseResources() {
        Logger.i(TAG, "releaseResources");
        if (mFragmengts != null && mFragmengts.size() > 0) {

            int mFragmentsSize = mFragmengts.size();
            for (int i = 0; i < mFragmentsSize; i++) {
                GalleryPagerFragment cgpf = (GalleryPagerFragment) mFragmengts.remove(0);
                cgpf.releaseResources();
                if (!mFragmengts.contains(cgpf)) {
                    cgpf = null;
                }
            }
            if (0 == mFragmengts.size()) {
                mFragmengts = null;
            }
        }
        //释放EventListingInfo
        CleanListUtils.releaseListingInfo(mEventListingInfo);
        CleanListUtils.releaseListingInfo(mPhotoListingInfo);
        CleanListUtils.releaseListingInfo(mNormalListingInfo);
        ((LeautoCameraAppLication) getApplication()).popActivity(this);
        Glide.get(this).clearMemory();
    }


    private List<ListingInfo.FileInfo> mDownloadFileInfos;

    @Click(R.id.ibtn_download)
    void downloads() {
        if (isDownloading) {
            Logger.e(TAG, "下载中，不能再次下载");
            return;
        }
        isDownloading = true;
        mDownloadFileInfos = new ArrayList<>();
        if (selectedIndexs != null && selectedIndexs.size() > 0) {
            for (int i = 0; i < selectedIndexs.size(); i++) {
                ListingInfo.FileInfo fileInfo = getCurrentFileInfos(mVp.getCurrentItem()).getListing().get(selectedIndexs.get(i));
                //过滤已下载过的文件
                if (SdCardUtils.isExists(UrlUtils.getTargetPath(getCurrentFileInfos(mVp.getCurrentItem()).getType(), CameraGalleryActivity.this) + File.separator + fileInfo.getFilename())) {
                    continue;
                }
                mDownloadFileInfos.add(fileInfo);
            }
            if (mDownloadFileInfos.size() == 0) {
                showToastSafe(R.string.show_toast_safe1);
                isDownloading = false;
            } else if (mDownloadFileInfos.size() < selectedIndexs.size()) {
                showToastSafe(R.string.show_toast_safe2);
            }
        } else {
            showToastSafe(R.string.show_toast_safe3);
            isDownloading = false;
            return;
        }
        if (mDownloadFileInfos != null && mDownloadFileInfos.size() > 0) {
            Logger.i(TAG, "过滤已下载文件的下载列表：" + mDownloadFileInfos.toString());
            downloadSingle(mDownloadFileInfos);
        }
    }


    private void resetDataAfterBatchOption() {
        if (mDownloadFileInfos != null) {
            mDownloadFileInfos.clear();
            mDownloadFileInfos = null;
        }
        if (selectedIndexs != null) {
            selectedIndexs.clear();
            selectedIndexs = null;
        }
        currentExecuteCount = 0;
        succeedCount = 0;
        isCancelDels = false;
        isDownloading = false;
        setSelecting(false);
    }

    private int currentExecuteCount = 0;
    private int succeedCount = 0;

    private void downloadSingle(final List<ListingInfo.FileInfo> downloadFileInfos) {

        if (downloadFileInfos.size() > 0 && currentExecuteCount < downloadFileInfos.size()) {
            final ListingInfo.FileInfo currentDownloadFileInfo = downloadFileInfos.get(currentExecuteCount);
            OkHttpRequest.downLoad("batchdownload", UrlUtils.getCameraMvideoHttpUrl(getCurrentFileInfos(mVp.getCurrentItem()).getType(), currentDownloadFileInfo.getFilename()), UrlUtils.getTargetPath(getCurrentFileInfos(mVp.getCurrentItem()).getType(), CameraGalleryActivity.this), currentDownloadFileInfo.getFilename(), SdCardUtils.getSdSize(CameraGalleryActivity.this, SdCardUtils.TYPE_AVAIABLE), new DownLoadCallBack() {
                @Override
                public void onFailure(Call call, IOException e) {

                    if ((currentExecuteCount + 1) == downloadFileInfos.size()) {
                        CustomDialogUtils.hideCustomDialog();
                        ToastUtils.showToast(CameraGalleryActivity.this, getResources().getString(R.string.download_success) + succeedCount + getResources().getString(R.string.download_item1)+ downloadFileInfos.size() + getResources().getString(R.string.download_item2), ToastUtils.SHORT);
                        resetDataAfterBatchOption();
                    } else {
                        currentExecuteCount += 1;
                        downloadSingle(downloadFileInfos);
                    }
                }

                @Override
                public void onStart(long total) {
                    Logger.i(TAG, "onStart:" + total);
                    CustomDialogUtils.showDialog(CameraGalleryActivity.this, new CustomDialogCallBack() {
                        @Override
                        public void onCancel() {
                            OkHttpRequest.setCancel(true);
                        }
                    });
                }

                @Override
                public void onLoading(long current, long total) {
//                    CustomDialogUtils.setCurrentTotal((currentExecuteCount + 1) + "/" + downloadFileInfos.size());
                    CustomDialogUtils.setCurrentTotal(SdCardUtils.formateSize(CameraGalleryActivity.this, current) + "/" + SdCardUtils.formateSize(CameraGalleryActivity.this, total));
                    double percentage = ((double) current / total) * 100;
                    CustomDialogUtils.setSeekBarMax((int) total);
                    DecimalFormat df = new DecimalFormat("##.##");
                    String percentageStr = df.format((percentage));
                    CustomDialogUtils.setPercentage(percentageStr + "%");
                    CustomDialogUtils.setProgress((int) current);
                }

                @Override
                public void onSucceed() {
                    succeedCount += 1;
                    if ((currentExecuteCount + 1) == downloadFileInfos.size()) {
                        CustomDialogUtils.hideCustomDialog();
                        ToastUtils.showToast(CameraGalleryActivity.this, getResources().getString(R.string.download_success) + succeedCount + getResources().getString(R.string.download_item1)+ downloadFileInfos.size() + getResources().getString(R.string.download_item2), ToastUtils.SHORT);
                        Logger.e(TAG, "下载成功" + succeedCount + "个 / 共" + downloadFileInfos.size() + "个");
                        resetDataAfterBatchOption();
                    } else {
                        currentExecuteCount += 1;
                        Logger.e(TAG, "正在｀下载第" + currentExecuteCount + "个");
                        downloadSingle(downloadFileInfos);
                    }
                }

                @Override
                public void onSdCardLackMemory(long total, long avaiable) {
                    SystemDialogUtils.showSingleConfirmDialog(CameraGalleryActivity.this, getResources().getString(R.string.diglog_title), getResources().getString(R.string.storage_less), getResources().getString(R.string.diglog_button), new SystemDialogCallBack() {
                        @Override
                        public void onSure() {

                        }

                        @Override
                        public void onCancel() {
                            resetDataAfterBatchOption();
                            OkHttpRequest.cancelSameTagCall("batchdownload");
                        }
                    });
                }

                @Override
                public void onCancel() {

                    OkHttpRequest.cancelSameTagCall("batchdownload");
                    ToastUtils.showToast(CameraGalleryActivity.this, getResources().getString(R.string.download_cancel) + succeedCount + getResources().getString(R.string.download_item2), ToastUtils.SHORT);
                    //删除下载不完整的文件
                    OkHttpRequest.deleteIntactFile(UrlUtils.getTargetPath(getCurrentFileInfos(mVp.getCurrentItem()).getType(), CameraGalleryActivity.this) + "/" + downloadFileInfos.get(currentExecuteCount).getFilename());
                    resetDataAfterBatchOption();
                }

                @Override
                public void onError(IOException e) {
                    OkHttpRequest.cancelSameTagCall("batchdownload");
                    CustomDialogUtils.hideCustomDialog();
                    if (downloadFileInfos != null && downloadFileInfos.size() > 0) {
                        OkHttpRequest.deleteIntactFile(UrlUtils.getTargetPath(getCurrentFileInfos(mVp.getCurrentItem()).getType(), CameraGalleryActivity.this) + "/" + downloadFileInfos.get(currentExecuteCount).getFilename());
                    }
                    resetDataAfterBatchOption();
                    showToastSafe(getResources().getString(R.string.download_error));
                }
            });
        }

    }

    private boolean isCancelDels = false;

    @Click(R.id.ibtn_del)
    void startDels() {
        if (selectedIndexs == null || selectedIndexs.size() == 0) {
            showToastSafe(getResources().getString(R.string.dialog_choose_file));
            return;
        }
        showConfirmDialog(getResources().getString(R.string.delete_ok), new SettingFragment.OnDialogConfirmListener() {
            @Override
            public void onDialogConfirm() {
                if (selectedIndexs.size() > 0) {
                    showLoading();
                    ListingInfo listInfo = getCurrentFileInfos(mVp.getCurrentItem());
                    List<ListingInfo.FileInfo> files = listInfo.getListing();
                    dels(files);
                } else {
                    showToastSafe(getResources().getString(R.string.dialog_choose_object));
                }
            }
        });

    }

    @UiThread
    void showConfirmDialog(final String text, final SettingFragment.OnDialogConfirmListener listener) {
        if (alertDialog2 == null) {
            alertDialog2 = new MaterialDialog(this);
            alertDialog2.setTitle(getString(R.string.base_activity_diglog_tip))
                    .setMessage(text)
                    .setPositiveButton(getString(R.string.base_activity_diglog_confirm), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dismissDialog();
                            listener.onDialogConfirm();
                        }
                    }).setNegativeButton(getString(R.string.base_activity_diglog_cancel), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissDialog();
                }
            }).show();

        }
    }

    @Background
    void dels(final List<ListingInfo.FileInfo> files) {

if (selectedIndexs.size() > 0 && currentExecuteCount < selectedIndexs.size() && !isCancelDels) {

            final ListingInfo.FileInfo file = files.get(selectedIndexs.get(currentExecuteCount));
            Logger.i(TAG, "正在删除的文件为：" + file.getFilename());
            CustomDialogUtils.setMsg(CameraGalleryActivity.this,getResources().getString(R.string.delete));
            CustomDialogUtils.setSeekBarMax(selectedIndexs.size());
            CustomDialogUtils.setCurrentTotal((currentExecuteCount + 1) + " / " + selectedIndexs.size());
            CustomDialogUtils.setProgress(currentExecuteCount);
            double percentage = ((double) currentExecuteCount / selectedIndexs.size()) * 100;
            DecimalFormat df = new DecimalFormat("##.##");
            String percentageStr = df.format((percentage));
            CustomDialogUtils.setPercentage(percentageStr + "%");
            DelUtils.deleteCameraSingle(this, UrlUtils.getDeleteCameraFileUrl(type, file.getFilename()), new DelFileCallBack() {

                @Override
                public void onFailure() {
//                    selectedIndexs.remove(0);
                    currentExecuteCount += 1;
                    dels(files);
                }

                @Override
                public void onSucceed() {
//                    selectedIndexs.remove(0);
                    delSuccessIndexs.add(selectedIndexs.get(currentExecuteCount));
                    succeedCount += 1;
                    currentExecuteCount += 1;
                    dels(files);
                }
            });
        } else {
//            EventBus.getDefault().post(new MultiDelInfosSuccessEvent(delSuccessIndexs,mVp.getCurrentItem()));
            CustomDialogUtils.hideCustomDialog();
            setSelecting(false);
            hideLoading();
            showToastSafe(getResources().getString(R.string.delete_success) + succeedCount + getResources().getString(R.string.download_item1)+ selectedIndexs.size() + getResources().getString(R.string.download_item2));


            notifyDataSetChangeds();


        }
    }

    @UiThread
    void notifyDataSetChangeds() {
//        ListingInfo listingInfo = getCurrentFileInfos(mVp.getCurrentItem());
//        List<ListingInfo.FileInfo> fileInfos = listingInfo.getListing();
//        ComparatorInteger comparatorInteger = new ComparatorInteger();
//        Collections.sort(delSuccessIndexs, comparatorInteger);
//
//        for (int index : delSuccessIndexs) {
//            fileInfos.remove(index);
//        }


        if (delSuccessIndexs != null && getCurrentFileInfos(mVp.getCurrentItem()) != null) {
            if (delSuccessIndexs.size() == getCurrentFileInfos(mVp.getCurrentItem()).getListing().size()) {
                showNoDataView(true);
            }
        }


        EventBus.getDefault().post(new MultiDelInfosSuccessEvent(delSuccessIndexs, type));

//        listingInfo.setListing(fileInfos);
//        if (mFragmengts != null) {
//            ((GalleryPagerFragment) mFragmengts.get(mVp.getCurrentItem())).setData(mVp.getCurrentItem(), listingInfo);
//            ((GalleryPagerFragment) mFragmengts.get(mVp.getCurrentItem())).initAdapter();
//        }
//        setSelecting(false);

        resetDataAfterBatchOption();
    }
    public void dismissDialog() {
        if (alertDialog2 != null) {
            alertDialog2.dismiss();
            alertDialog2 = null;
        }
    }
    private ListingInfo getCurrentFileInfos(int index) {
        switch (index) {
            case 0:
                return mEventListingInfo;

            case 1:
                return mPhotoListingInfo;

            case 2:
                return mNormalListingInfo;
            default:
                return null;
        }
    }


    public boolean isSelecting() {
        return selecting;
    }

    @UiThread
    public void setSelecting(boolean selecting) {
        this.selecting = selecting;
        if (selecting) {
            gallery_bottom_bar.setVisibility(View.VISIBLE);
            navigation_bar_right_bt.setText(getResources().getString(R.string.base_activity_diglog_cancel));
        } else {
            navigation_bar_right_bt.setText(getResources().getString(R.string.base_activity_diglog_choose));
            gallery_bottom_bar.setVisibility(View.GONE);
        }
        EventBus.getDefault().post(new IsSelectEvent(selecting));
    }

    public void onEventMainThread(MostDelInfoEvent event) {
        if (delSuccessIndexs != null && delSuccessIndexs.size() > 0) {
            delSuccessIndexs.clear();
        } else {
            delSuccessIndexs = new ArrayList<>();
        }
        selectedIndexs = event.getDelIndexs();
        Logger.i(TAG, "接收到的被选择的list：" + selectedIndexs.toString());
    }

}
