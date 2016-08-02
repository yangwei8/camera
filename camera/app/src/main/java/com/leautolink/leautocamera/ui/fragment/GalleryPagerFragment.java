package com.leautolink.leautocamera.ui.fragment;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.leautolink.leautocamera.R;
import com.leautolink.leautocamera.domain.ListingInfo;
import com.leautolink.leautocamera.ui.adapter.GalleryRecyclerAdapter;
import com.leautolink.leautocamera.ui.base.BaseFragment;
import com.leautolink.leautocamera.utils.CleanListUtils;
import com.leautolink.leautocamera.utils.Logger;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

/**
 * 相册ViewPager的Fragment
 * Created by tianwei1 on 2016/3/6.
 */
@EFragment(R.layout.fragment_gallerypager)
public class GalleryPagerFragment extends BaseFragment {
    private static final String TAG = "GalleryPagerFragment";
    //初始化View
    @ViewById(R.id.recycler_view)
    RecyclerView mRv;
    //数据相关
    private ListingInfo mListingInfo;
    private GalleryRecyclerAdapter mAdapter;
    private int mI;

    @AfterViews
    void init() {
        initView();
        initData();
//        initAdapter();
        initListener();
    }

    private void initView() {
    }

    private void initData() {

    }

    private void initListener() {

    }

    @UiThread
    public void initAdapter() {
        if (mListingInfo == null) {
            Logger.i(TAG, mI + "--mListingInfo:" + mListingInfo);
            return;
        }
        if (mAdapter != null) {
            mAdapter.releaseResources();
            mAdapter = null;
        }
//        if (mAdapter == null) {
        Logger.i(TAG, mI + "--initRecyclerAdapter");
        mAdapter = new GalleryRecyclerAdapter(mActivity, this, mListingInfo);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mActivity, 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return 1;
            }
        });
        if (mRv != null) {
            mRv.setLayoutManager(gridLayoutManager);
            mRv.setAdapter(mAdapter);
        }
//        }
//        else {
//            Logger.i(TAG, mI + "--notifyRecyclerAdapter");
//            Logger.i(TAG, mI + "--mListingInfo:" + mListingInfo.toString());
//            mAdapter.notifyDataSetChanged();
//        }
    }

    public void setData(int i, ListingInfo listingInfo) {
        mI = i;
        Logger.i(TAG, mI + "---setData" + "---listingInfo:" + listingInfo);
        mListingInfo = listingInfo;
    }

    public ListingInfo getData() {
        return mListingInfo;
    }

    @Override
    protected void onUserVisibleHint(boolean isVisibleToUser) {
        super.onUserVisibleHint(isVisibleToUser);
        Logger.i(TAG, mI + "--onUserVisibleHint:" + isVisibleToUser);
    }

    @Override
    public void releaseResources() {
        Logger.i(TAG, "releaseResources()");
        if (mAdapter != null)
            mAdapter.releaseResources();
        //释放ListingInfo
        CleanListUtils.releaseListingInfo(mListingInfo);
    }
}
