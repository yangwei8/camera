package com.leautolink.leautocamera.ui.fragment;

import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.leautolink.leautocamera.R;
import com.leautolink.leautocamera.domain.ListingInfo;
import com.leautolink.leautocamera.ui.base.BaseFragment;
import com.leautolink.leautocamera.utils.Logger;
import com.leautolink.leautocamera.utils.UrlUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

/**
 * Created by tianwei1 on 2016/3/8.
 */
@EFragment(R.layout.fragment_gallery_preview_list)
public class GalleryPreviewListPagerFragment extends BaseFragment {

    private static final java.lang.String TAG = "GalleryPreviewListPagerFragment";
    //View相关
    @ViewById(R.id.iv_list_thumb)
    ImageView mIvListThumb;

    //数据相关
    //数据相关
    private ListingInfo.FileInfo mFileInfo;
    private int mI;
    private String mType;

    @AfterViews
    public void init() {
        initView();
        initData();
        initListener();
    }

    private void initView() {

    }

    private void initData() {
        if (mFileInfo != null) {
            if (!TextUtils.isEmpty(mFileInfo.getLocalFileUrl())) {//本地
                Logger.i(TAG, mI + "---initData local url :" + mFileInfo.getLocalFileUrl());
                Glide.with(this).load(mFileInfo.getLocalFileUrl()).placeholder(R.drawable.img_default).into(mIvListThumb);
            } else {
                Logger.i(TAG, mI + "---initData httpThumbUrl :" + UrlUtils.getCameraHttpThumbUrl(mType, mFileInfo.getFileThumbname()));
                Glide.with(this).load(UrlUtils.getCameraHttpThumbUrl(mType, mFileInfo.getFileThumbname())).placeholder(R.drawable.img_default).into(mIvListThumb);
            }
        }
    }


    private void initListener() {

    }

    public void setData(int i, String type, ListingInfo.FileInfo fileInfo) {
        mI = i;
        mType = type;
        mFileInfo = fileInfo;
    }


    @Override
    public void releaseResources() {

    }
}
