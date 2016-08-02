package com.leautolink.leautocamera.ui.fragment;

import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.leautolink.leautocamera.R;
import com.leautolink.leautocamera.callback.DelFileCallBack;
import com.leautolink.leautocamera.domain.ListingInfo;
import com.leautolink.leautocamera.event.IsSelectEvent;
import com.leautolink.leautocamera.event.MostDelInfoEvent;
import com.leautolink.leautocamera.event.MultiDelInfosSuccessEvent;
import com.leautolink.leautocamera.ui.activity.HomeActivity;
import com.leautolink.leautocamera.ui.adapter.GalleryPagerAdapter;
import com.leautolink.leautocamera.ui.base.BaseFragment;
import com.leautolink.leautocamera.ui.view.customview.CustomViewPager;
import com.leautolink.leautocamera.ui.view.customview.MaterialDialog;
import com.leautolink.leautocamera.utils.CleanListUtils;
import com.leautolink.leautocamera.utils.DelUtils;
import com.leautolink.leautocamera.utils.Logger;
import com.leautolink.leautocamera.utils.SequenceUtils;
import com.leautolink.leautocamera.utils.UrlUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 相册页面
 * Created by tianwei on 16/4/6.
 */
@EFragment(R.layout.fragment_setting)
public class SettingFragment extends BaseFragment implements View.OnClickListener {
    //常量
    private static final int TAB_COUNT = 2;
    private static final String TAG = "SettingFragment";

    //初始化View
    @ViewById(R.id.ll_tab)
    LinearLayout ll_tab;
//    @ViewById(R.id.navigation_bar_right_bt)
//    Button navigation_bar_right_bt;

    @ViewById(R.id.activity_local_gallery_video_btn)
    Button mVideoBtn;
    @ViewById(R.id.activity_local_gallery_photo_btn)
    Button mPhotoBtn;
    @ViewById(R.id.activity_local_gallery_pager)
    CustomViewPager mVp;
    @ViewById(R.id.gallery_bottom_bar)
    LinearLayout gallery_bottom_bar;

    @ViewById(R.id.rl_no_data)
    RelativeLayout mRlNoData;

    //    @Extra
    String photoOrVideo;

    //数据相关
    private List<BaseFragment> mFragmengts;
    private GalleryPagerAdapter mAdapter;
    private ListingInfo mVideoListingInfo;
    private ListingInfo mPhotoListingInfo;
    private boolean isFirstCreateActivity;
    private List<ListingInfo.FileInfo> mSequencedVideoFileInfos;
    private List<ListingInfo.FileInfo> mSequencedPhotoFileInfos;
    //标记相关
    private boolean selecting = false;
    private boolean isNoVideoData = false;
    private boolean isNoPhotoData = false;

    private List<Integer> selectedIndexs;

    private List<Integer> delSuccessIndexs;

    private String type = "video";

    private MaterialDialog alertDialog;

    private HomeActivity mHomeActivity;

    @AfterViews
    void init() {
        isFirstCreateActivity = true;
        mHomeActivity = (HomeActivity) mActivity;
        initView();
        initData();
        initListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        ListingInfo currentListingInfo = getCurrenFileInfos(mVp.getCurrentItem());
        if (currentListingInfo == null || (currentListingInfo != null && currentListingInfo.getListing().size() == 0)) {
            showNoDataView(true);
        } else {
            showNoDataView(false);
        }
    }

    private void initView() {
//        navigation_bar_right_bt.setVisibility(View.VISIBLE);
//        navigation_bar_right_bt.setTextColor(Color.rgb(20, 150, 177));
        mVp.setOffscreenPageLimit(1);
        mVideoBtn.setTextColor(Color.rgb(20, 150, 177));
        mPhotoBtn.setTextColor(Color.rgb(155, 155, 155));
    }

    private void initData() {
        //初始化ViewPager中的Fragment

        if (mFragmengts == null)
            mFragmengts = new ArrayList<BaseFragment>();
        for (int i = 0; i < TAB_COUNT; i++) {
            GalleryPagerFragment cgpf = GalleryPagerFragment_.builder().build();
            mFragmengts.add(cgpf);
        }
        if (TextUtils.isEmpty(photoOrVideo)) {
            //从本地获取视频的数据
            getVideoDataFromLocal();
//        initAdapter();
        } else {
            ll_tab.setVisibility(View.GONE);
            if (photoOrVideo.equals("photo")) {
                mVp.setCurrentItem(1);
                getPhotoDataFromLocal();
            } else {
                getVideoDataFromLocal();
            }
            mVp.setPagingEnabled(false);

        }
    }

    private void initAdapter() {

        if (mAdapter == null) {
            Logger.i(TAG, "initViewPagerAdapter");
            mAdapter = new GalleryPagerAdapter(getChildFragmentManager(), mFragmengts);
            mVp.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
            Logger.i(TAG, "notifyViewPagerAdapter");
        }
    }

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
                        type = "video";
                        changeTabBtnTextColor(0);
                        getVideoDataFromLocal();
                        break;
                    case 1:
                        type = "photo";
                        changeTabBtnTextColor(1);
                        getPhotoDataFromLocal();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mVideoBtn.setOnClickListener(this);
        mPhotoBtn.setOnClickListener(this);
        mHomeActivity.navigation_bar_right_bt.setOnClickListener(this);
    }

    @UiThread
    void showNoDataView(boolean isNoData) {
        if (isNoData) {
            mRlNoData.setVisibility(View.VISIBLE);
        } else {
            mRlNoData.setVisibility(View.GONE);
        }
    }

    private void getVideoDataFromLocal() {
        showLoading(getResources().getString(R.string.loading));
        String[] videoNames = getFileNamesFromLocal(UrlUtils.getLocalFileUrl(mActivity, "video"));
        if (0 != videoNames.length) {
            isNoVideoData = false;
            mSequencedVideoFileInfos = new ArrayList<ListingInfo.FileInfo>();
            //排序
            new SequenceUtils().sequenceFileNames(videoNames);
            for (int i = 0; i < videoNames.length; i++) {
                ListingInfo.FileInfo fileInfo = new ListingInfo.FileInfo(videoNames[i], videoNames[i].substring(7, 22), "", "01:00", true, false, UrlUtils.getLocalUrl(mActivity, "video", videoNames[i]));
                mSequencedVideoFileInfos.add(fileInfo);
            }
            mVideoListingInfo = new ListingInfo("video", true, mSequencedVideoFileInfos);
            Logger.i(TAG, "mVideoListingInfo:" + mVideoListingInfo.toString());
            //只初始化视频页的数据
//            if(mFragmengts != null){
//                ((GalleryPagerFragment) mFragmengts.get(0)).setData(0, mVideoListingInfo);
//                ((GalleryPagerFragment) mFragmengts.get(0)).initAdapter();
//            }
            ((GalleryPagerFragment) mFragmengts.get(0)).setData(0, mVideoListingInfo);
            if (isFirstCreateActivity) {
                initAdapter();
                isFirstCreateActivity = false;
            }
            ((GalleryPagerFragment) mFragmengts.get(0)).initAdapter();
        } else {
            isNoVideoData = true;
            showNoDataView(true);
            if (isFirstCreateActivity) {
                initAdapter();
                isFirstCreateActivity = false;
            }
        }
        hideLoading();
    }

    /**
     * 获取本地照片数据
     */
    private void getPhotoDataFromLocal() {
        showLoading(getResources().getString(R.string.loading));
        String[] photoNames = getFileNamesFromLocal(UrlUtils.getLocalFileUrl(mActivity, "photo"));
        if (0 != photoNames.length) {
            isNoPhotoData = false;
            mSequencedPhotoFileInfos = new ArrayList<ListingInfo.FileInfo>();
            //排序
            new SequenceUtils().sequenceFileNames(photoNames);
            for (int i = 0; i < photoNames.length; i++) {
                ListingInfo.FileInfo fileInfo = new ListingInfo.FileInfo(photoNames[i], photoNames[i].substring(7, 24), "", "", false, true, UrlUtils.getLocalUrl(mActivity, "photo", photoNames[i]));
                mSequencedPhotoFileInfos.add(fileInfo);
            }
            mPhotoListingInfo = new ListingInfo("photo", true, mSequencedPhotoFileInfos);
            //初始化相册页的数据
            if (mFragmengts != null) {
                ((GalleryPagerFragment) mFragmengts.get(1)).setData(1, mPhotoListingInfo);
                ((GalleryPagerFragment) mFragmengts.get(1)).initAdapter();
            }
        } else {
            isNoPhotoData = true;
            showNoDataView(true);
//            initAdapter();


        }
        hideLoading();
    }

    /**
     * 从本地获取某个文件夹中的符合记录仪生成文件的文件名数组
     *
     * @param targetFilePath
     * @return 文件名的数组
     */
    private String[] getFileNamesFromLocal(String targetFilePath) {
        File targetFile = new File(targetFilePath);
        FilenameFilter filenameFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                if (filename.startsWith("Leauto_") && filename.length() >= 23) {
                    return true;
                }
                return false;
            }
        };
        return targetFile.list(filenameFilter);
    }

    /**
     * 改变TabBtn文字的颜色
     *
     * @param btnId
     */
    private void changeTabBtnTextColor(int btnId) {
        switch (btnId) {
            case 0:
                mVideoBtn.setTextColor(Color.rgb(20, 150, 177));
                mPhotoBtn.setTextColor(Color.rgb(155, 155, 155));
                break;
            case 1:
                mVideoBtn.setTextColor(Color.rgb(155, 155, 155));
                mPhotoBtn.setTextColor(Color.rgb(20, 150, 177));
                break;
        }
    }

    @UiThread
    public void setSelecting(boolean selecting) {
        this.selecting = selecting;
        if (selecting) {
            gallery_bottom_bar.setVisibility(View.VISIBLE);
            mHomeActivity.navigation_bar_right_bt.setText(getResources().getString(R.string.cancle));
            mHomeActivity.goneBottombar();
        } else {
            mHomeActivity.navigation_bar_right_bt.setText(getResources().getString(R.string.choose));
            gallery_bottom_bar.setVisibility(View.GONE);
            mHomeActivity.showBottombar();
        }
        EventBus.getDefault().post(new IsSelectEvent(selecting));
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
        CleanListUtils.releaseListingInfo(mVideoListingInfo);
        CleanListUtils.releaseListingInfo(mPhotoListingInfo);

        Glide.get(mActivity).clearMemory();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_local_gallery_video_btn:
                mVp.setCurrentItem(0);
                break;
            case R.id.activity_local_gallery_photo_btn:
                mVp.setCurrentItem(1);
                break;
            case R.id.navigation_bar_right_bt:
                if ((mVp.getCurrentItem() == 0 && isNoVideoData) || (mVp.getCurrentItem() == 1 && isNoPhotoData)) {
                    return;
                }
                setSelecting(!selecting);
                break;
            case R.id.ibtn_share://分享

                break;
        }
    }

    public void onEventMainThread(MostDelInfoEvent event) {
        if (delSuccessIndexs != null && delSuccessIndexs.size() > 0) {
            delSuccessIndexs.clear();
        } else {
            delSuccessIndexs = new ArrayList<>();
        }
        selectedIndexs = event.getDelIndexs();
    }

    @Click(R.id.ibtn_del)
    void startDels() {
        if (selectedIndexs == null || selectedIndexs.size() == 0) {
            showToastSafe(getResources().getString(R.string.dialog_choose_file));
            return;
        }
        showConfirmDialog(getResources().getString(R.string.delete_ok), new OnDialogConfirmListener() {
            @Override
            public void onDialogConfirm() {
                if (selectedIndexs.size() > 0) {
                    showLoading();
                    ListingInfo listInfo = getCurrenFileInfos(mVp.getCurrentItem());
                    List<ListingInfo.FileInfo> files = listInfo.getListing();
                    dels(files);
                } else {
                    showToastSafe(getResources().getString(R.string.dialog_choose_object));
                }
            }
        });

    }

    private ListingInfo getCurrenFileInfos(int index) {
        switch (index) {
            case 0:
                return mVideoListingInfo;
            case 1:
                return mPhotoListingInfo;
            default:
                return null;
        }
    }


    @UiThread
    void notifyDataSetChangeds() {
        if (delSuccessIndexs != null && getCurrenFileInfos(mVp.getCurrentItem()) != null) {
            if (delSuccessIndexs.size() == getCurrenFileInfos(mVp.getCurrentItem()).getListing().size()) {
                showNoDataView(true);
            }
        }

        EventBus.getDefault().post(new MultiDelInfosSuccessEvent(delSuccessIndexs, type));

        setSelecting(false);
        hideLoading();
        showToastSafe(getResources().getString(R.string.delete_success));
    }

    @Background
    void dels(final List<ListingInfo.FileInfo> files) {
        if (selectedIndexs.size() > 0) {
            final int currentDelIndex = selectedIndexs.get(0);
            final ListingInfo.FileInfo file = files.get(currentDelIndex);
            DelUtils.deleteLocalSingle(mActivity, UrlUtils.getDeletLocalFileUrl(mActivity, getCurrenFileInfos(mVp.getCurrentItem()).getType(), file.getFilename()), new DelFileCallBack() {

                @Override
                public void onFailure() {
                    selectedIndexs.remove(0);
                    dels(files);
                }

                @Override
                public void onSucceed() {
                    selectedIndexs.remove(0);
                    delSuccessIndexs.add(currentDelIndex);
                    dels(files);
                }
            });
        } else {
//            EventBus.getDefault().post(new MultiDelInfosSuccessEvent(delSuccessIndexs,mVp.getCurrentItem()));
            notifyDataSetChangeds();

//            hideLoading();
        }
    }

    @Click(R.id.navigation_bar_right_bt)
    void selectBtn() {
        setSelecting(!selecting);
    }


    @UiThread
    void showConfirmDialog(final String text, final OnDialogConfirmListener listener) {
        if (alertDialog == null) {
            alertDialog = new MaterialDialog(mActivity);
            alertDialog.setTitle(getString(R.string.base_activity_diglog_tip))
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

    private void dismissDialog() {
        if (alertDialog != null) {
            alertDialog.dismiss();
            alertDialog = null;
        }
    }

    public interface OnDialogConfirmListener {
        void onDialogConfirm();
    }
}
