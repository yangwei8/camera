package com.leautolink.leautocamera.ui.activity;

import android.database.Cursor;
import android.graphics.Color;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.leautolink.leautocamera.R;
import com.leautolink.leautocamera.callback.DelFileCallBack;
import com.leautolink.leautocamera.config.Config;
import com.leautolink.leautocamera.domain.ListingInfo;
import com.leautolink.leautocamera.event.IsSelectEvent;
import com.leautolink.leautocamera.event.MostDelInfoEvent;
import com.leautolink.leautocamera.event.MultiDelInfosSuccessEvent;
import com.leautolink.leautocamera.ui.adapter.GalleryPagerAdapter;
import com.leautolink.leautocamera.ui.base.BaseFragment;
import com.leautolink.leautocamera.ui.base.BaseFragmentActivity;
import com.leautolink.leautocamera.ui.fragment.GalleryPagerFragment;
import com.leautolink.leautocamera.ui.fragment.GalleryPagerFragment_;
import com.leautolink.leautocamera.ui.view.customview.CustomViewPager;
import com.leautolink.leautocamera.utils.CleanListUtils;
import com.leautolink.leautocamera.utils.DelUtils;
import com.leautolink.leautocamera.utils.Logger;
import com.leautolink.leautocamera.utils.SequenceUtils;
import com.leautolink.leautocamera.utils.UrlUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by tianwei1 on 2016/3/12.
 */
@EActivity(R.layout.activity_local_gallery)
public class LocalGalleryActivity extends BaseFragmentActivity implements View.OnClickListener {
    //常量
    private static final int TAB_COUNT = 2;
    private static final String TAG = "LocalGalleryActivity";

    //初始化View
    @ViewById(R.id.navigation_bar_left_ib)
    ImageButton mIvBtnGoBack;
    @ViewById(R.id.ll_tab)
    LinearLayout ll_tab;
    @ViewById(R.id.navigation_bar_right_bt)
    Button navigation_bar_right_bt;
    @ViewById(R.id.navigation_bar_title)
    TextView mTvTitle;
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

    @Extra
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


    @AfterViews
    void init() {
        isFirstCreateActivity = true;
        initView();
        initData();
        initListener();
    }

    private void initView() {
        mTvTitle.setText(getResources().getString(R.string.localalbum));
        navigation_bar_right_bt.setVisibility(View.GONE);
        navigation_bar_right_bt.setTextColor(Color.rgb(20, 150, 177));
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
                mTvTitle.setText(getResources().getString(R.string.localphoto));
                getVideoDataFromLocal();
                mVp.setCurrentItem(1);
                getPhotoDataFromLocal();
            } else {
                mTvTitle.setText(getResources().getString(R.string.localvideo));
                getVideoDataFromLocal();
            }
            mVp.setPagingEnabled(false);

        }
    }

    private void initAdapter() {

        if (mAdapter == null) {
            Logger.i(TAG, "initViewPagerAdapter");
            mAdapter = new GalleryPagerAdapter(getSupportFragmentManager(), mFragmengts);
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
        mIvBtnGoBack.setOnClickListener(this);
        navigation_bar_right_bt.setOnClickListener(this);
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
     * 从本地获取视频的数据
     * 还需要验证的方法
     */
    public void getOldVideoDataFromLocal() {
        Cursor cursor = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        Logger.i(TAG, "cursor:" + cursor.getCount());
        if (cursor != null && cursor.getCount() > 0) {
            List<ListingInfo.FileInfo> fileInfos = new ArrayList<ListingInfo.FileInfo>();
            List<String> fileNames = new ArrayList<String>();
            mSequencedVideoFileInfos = new ArrayList<ListingInfo.FileInfo>();
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor
                        .getColumnIndexOrThrow(MediaStore.Video.Media._ID));

                String album = cursor
                        .getString(cursor
                                .getColumnIndexOrThrow(MediaStore.Video.Media.ALBUM));
                String artist = cursor
                        .getString(cursor
                                .getColumnIndexOrThrow(MediaStore.Video.Media.ARTIST));
                String displayName = cursor
                        .getString(cursor
                                .getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));
                String mimeType = cursor
                        .getString(cursor
                                .getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
                String path = cursor
                        .getString(cursor
                                .getColumnIndexOrThrow(MediaStore.Video.Media.DATA));

                long size = cursor
                        .getLong(cursor
                                .getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
                String title = cursor
                        .getString(cursor
                                .getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
                long duration = cursor
                        .getInt(cursor
                                .getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));

                if (title.contains("Leauto")) {
                    Logger.e(TAG, "title：" + title + "---duration：" + duration + "--path:" + path + "--displayName:" + displayName);
//                    String filename, String starttime, String filesize, String length, boolean isVideo, boolean isPhoto, String localFileUrl
                    fileNames.add(displayName);
                    ListingInfo.FileInfo fileInfo = new ListingInfo.FileInfo(displayName, title.substring(7, title.length() - 1), Long.toString(size), Long.toString(duration), true, false, Config.LOCAL_URL_PREFIX + path);
                    fileInfos.add(fileInfo);
                }
            }
            cursor.close();
            cursor = null;
            Logger.i(TAG, "排序前：" + fileInfos.toString());
            Object[] filenames = fileNames.toArray();
            new SequenceUtils().sequenceFileNames(filenames);

            for (int i = 0; i < fileNames.size(); i++) {
                int index = fileNames.indexOf(filenames[i].toString());
                mSequencedVideoFileInfos.add(fileInfos.get(index));
            }
            fileNames.clear();
            fileNames = null;
            fileInfos.clear();
            fileInfos = null;
            Logger.i(TAG, "排序后：" + mSequencedVideoFileInfos.toString());
            mVideoListingInfo = new ListingInfo("video", true, mSequencedVideoFileInfos);

            //只初始化Event页的数据
            ((GalleryPagerFragment) mFragmengts.get(0)).setData(0, mVideoListingInfo);
            if (isFirstCreateActivity) {
                initAdapter();
                isFirstCreateActivity = false;
            }
            ((GalleryPagerFragment) mFragmengts.get(0)).initAdapter();
        }
    }

    private void getVideoDataFromLocal() {
        showLoading(getResources().getString(R.string.loading));
        String[] videoNames = getFileNamesFromLocal(UrlUtils.getLocalFileUrl(this, "video"));
        if (0 != videoNames.length) {
            isNoVideoData = false;
            mSequencedVideoFileInfos = new ArrayList<ListingInfo.FileInfo>();
            //排序
            new SequenceUtils().sequenceFileNames(videoNames);
            for (int i = 0; i < videoNames.length; i++) {
                ListingInfo.FileInfo fileInfo = new ListingInfo.FileInfo(videoNames[i], videoNames[i].substring(7, 22), "", "01:00", true, false, UrlUtils.getLocalUrl(this, "video", videoNames[i]));
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
        String[] photoNames = getFileNamesFromLocal(UrlUtils.getLocalFileUrl(this, "photo"));
        if (0 != photoNames.length) {
            isNoPhotoData = false;
            mSequencedPhotoFileInfos = new ArrayList<ListingInfo.FileInfo>();
            //排序
            new SequenceUtils().sequenceFileNames(photoNames);
            for (int i = 0; i < photoNames.length; i++) {
                ListingInfo.FileInfo fileInfo = new ListingInfo.FileInfo(photoNames[i], photoNames[i].substring(7, 24), "", "", false, true, UrlUtils.getLocalUrl(this, "photo", photoNames[i]));
                mSequencedPhotoFileInfos.add(fileInfo);
            }
            mPhotoListingInfo = new ListingInfo("photo", true, mSequencedPhotoFileInfos);
            if (isFirstCreateActivity) {
                initAdapter();
                isFirstCreateActivity = false;
            }
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
     * 从本地获取某个文件夹中的所有文件的文件名
     *
     * @param targetFilePath
     * @return 文件名的数组
     */
    private String[] getFileNamesFromLocal(String targetFilePath) {
        File targetFile = new File(targetFilePath);

        return targetFile.list();
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

    public boolean isSelecting() {
        return selecting;
    }

    @UiThread
    public void setSelecting(boolean selecting) {
        this.selecting = selecting;
        if (selecting) {
            gallery_bottom_bar.setVisibility(View.VISIBLE);
            navigation_bar_right_bt.setText(getResources().getString(R.string.cancel_download));
        } else {
            navigation_bar_right_bt.setText(getResources().getString(R.string.choose));
            gallery_bottom_bar.setVisibility(View.GONE);
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

        Glide.get(this).clearMemory();
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
            case R.id.navigation_bar_left_ib:
                releaseResources();
                this.finish();
                break;
            case R.id.navigation_bar_right_bt:
                if ((mVp.getCurrentItem() == 0 && isNoVideoData) || (mVp.getCurrentItem() == 1 && isNoPhotoData)) {
                    return;
                }
//                setSelecting(!selecting);
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
//        ListingInfo listingInfo = getCurrenFileInfos(mVp.getCurrentItem());
//        List<ListingInfo.FileInfo> fileInfos = listingInfo.getListing();
//        ComparatorInteger comparatorInteger = new ComparatorInteger();
//        Collections.sort(delSuccessIndexs, comparatorInteger);
//
//        for (int index : delSuccessIndexs) {
//            fileInfos.remove(index);
//        }
//        if(fileInfos.size() == 0){
//            showNoDataView(true);
//        }

        if (delSuccessIndexs != null && getCurrenFileInfos(mVp.getCurrentItem()) != null) {
            if (delSuccessIndexs.size() == getCurrenFileInfos(mVp.getCurrentItem()).getListing().size()) {
                showNoDataView(true);
            }
        }

        EventBus.getDefault().post(new MultiDelInfosSuccessEvent(delSuccessIndexs, type));

//        listingInfo.setListing(fileInfos);
//        if (mFragmengts != null) {
//            ((GalleryPagerFragment) mFragmengts.get(mVp.getCurrentItem())).setData(mVp.getCurrentItem(), listingInfo);
//            ((GalleryPagerFragment) mFragmengts.get(mVp.getCurrentItem())).initAdapter();
//        }
        setSelecting(false);
        hideLoading();
        showToastSafe(getResources().getString(R.string.delete_success));

    }

    @Background
    void dels(final List<ListingInfo.FileInfo> files) {
        if (selectedIndexs.size() > 0) {
            final int currentDelIndex = selectedIndexs.get(0);
            final ListingInfo.FileInfo file = files.get(currentDelIndex);
            DelUtils.deleteLocalSingle(this, UrlUtils.getDeletLocalFileUrl(LocalGalleryActivity.this, getCurrenFileInfos(mVp.getCurrentItem()).getType(), file.getFilename()), new DelFileCallBack() {

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
}
