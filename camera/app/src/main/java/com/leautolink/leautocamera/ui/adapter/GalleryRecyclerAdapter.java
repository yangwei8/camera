package com.leautolink.leautocamera.ui.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.leautolink.leautocamera.R;
import com.leautolink.leautocamera.domain.ListingInfo;
import com.leautolink.leautocamera.event.DelSucceedEvent;
import com.leautolink.leautocamera.event.IsSelectEvent;
import com.leautolink.leautocamera.event.MostDelInfoEvent;
import com.leautolink.leautocamera.event.MultiDelInfosSuccessEvent;
import com.leautolink.leautocamera.net.http.OkHttpRequest;
import com.leautolink.leautocamera.ui.activity.GalleryPreviewActivity;
import com.leautolink.leautocamera.ui.activity.HomeActivity;
import com.leautolink.leautocamera.utils.CleanListUtils;
import com.leautolink.leautocamera.utils.Logger;
import com.leautolink.leautocamera.utils.UrlUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by tianwei1 on 2016/3/7.
 */
public class GalleryRecyclerAdapter extends RecyclerView.Adapter<GalleryRecyclerAdapter.GalleryRecyclerViewHolder> {
    private static final String TAG = "GalleryRecyclerAdapter";
    private static final int VIEW_TYPE_VIDEO = 0;
    private static final int VIEW_TYPE_PHOTO = 1;
//    private static final int VIEW_TYPE_GO_CAMERA_GALLERY = 3;

    private Activity mActivity;
    private Fragment mFragment;
    private ListingInfo mListingInfo;
    private List<ListingInfo.FileInfo> mFileInfos;
    private List<Integer> mSelectorIndexs;
    private ListingInfo.FileInfo mFileInfo;

    private boolean isSelect = false;
    /**
     * 与position对应的mIsAdd的值
     * 是否选中
     * 1为选中
     * 0为没有选中
     */
    private Map<Integer, Integer> mIsAddMap;


    public void onEventMainThread(DelSucceedEvent event) {
        Logger.e(TAG, "DelSucceedEvent");
        String type = event.getType();
        int removedPosition = event.getRemovedPosition();
        if (removedPosition >= 0) {
            if (mListingInfo.getType().equals(type)) {
                mFileInfos.remove(removedPosition);
                notifyDataSetChanged();
            }
        }
    }

    public void onEventMainThread(MultiDelInfosSuccessEvent event) {

        List<Integer> delSuccessIndexs = event.getDelSuccessIndexs();

        Logger.e(TAG, "MultiDelInfosSuccessEvent  delSuccessIndexs:" + delSuccessIndexs);
        if (delSuccessIndexs != null && delSuccessIndexs.size() > 0) {
            String type = event.getType();
            List<ListingInfo.FileInfo> mDelSucceedFileInfos = new ArrayList<>();
            for (int i = 0; i < delSuccessIndexs.size(); i++) {
                if (mListingInfo.getType().equals(type)) {
                    ListingInfo.FileInfo fileInfo = mFileInfos.get((int) delSuccessIndexs.get(i));
                    mDelSucceedFileInfos.add(fileInfo);
                }
            }
            mFileInfos.removeAll(mDelSucceedFileInfos);
            mDelSucceedFileInfos.clear();
            mDelSucceedFileInfos = null;
            notifyDataSetChanged();
        }
    }

    /**
     * 清除Map
     */
    private void clearMap() {
        if (mIsAddMap != null) {
            Logger.e(TAG, "clearMap()");
            mIsAddMap.clear();
            mIsAddMap = null;
        }
    }

    public void onEventMainThread(IsSelectEvent event) {
        isSelect = event.isSelecting();
        Logger.e(TAG, "isSelect:" + isSelect);
        if (isSelect) {
            mIsAddMap = new LinkedHashMap<Integer, Integer>();
            mSelectorIndexs = new ArrayList<>();
            for (int i = 0; i < mFileInfos.size(); i++) {
                mIsAddMap.put(i, 0);
            }
        } else {
            if (mSelectorIndexs != null) {
                mSelectorIndexs.clear();
                mSelectorIndexs = null;
            }
            clearMap();
        }
        notifyDataSetChanged();
    }


    public GalleryRecyclerAdapter(Activity activity, Fragment fragment, ListingInfo listingInfo) {
        EventBus.getDefault().register(this);
        mActivity = activity;

        mFragment = fragment;
        mListingInfo = listingInfo;
        mFileInfos = listingInfo.getListing();

    }

    @Override
    public int getItemViewType(int position) {
//        if (position == mFileInfos.size()) {//最后一个
//            return VIEW_TYPE_GO_CAMERA_GALLERY;
//        }
        mFileInfo = mFileInfos.get(position);
        if (mFileInfo.isVideo()) {
            return VIEW_TYPE_VIDEO;
        } else if (mFileInfo.isPhoto()) {
            return VIEW_TYPE_PHOTO;
        }

        return -1;
    }

    @Override
    public GalleryRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (VIEW_TYPE_VIDEO == viewType) {
            View itemVideoView = View.inflate(mActivity, R.layout.item_recycleview_viedo, null);
            return new GalleryRecyclerViewHolder(itemVideoView, VIEW_TYPE_VIDEO);
        } else if (VIEW_TYPE_PHOTO == viewType) {
            View itemPhotoView = View.inflate(mActivity, R.layout.item_recycleview_photo, null);
            return new GalleryRecyclerViewHolder(itemPhotoView, VIEW_TYPE_PHOTO);
        }
//        else if (VIEW_TYPE_GO_CAMERA_GALLERY == viewType) {
//            View itemGoCameraGallery = View.inflate(mActivity, R.layout.item_recyclerview_gocamera_gallery, null);
//            return new GalleryRecyclerViewHolder(itemGoCameraGallery, VIEW_TYPE_GO_CAMERA_GALLERY);
//        }
        return null;
    }

    @Override
    public void onBindViewHolder(final GalleryRecyclerViewHolder holder, final int position) {
        //跳转到记录仪相册
//        if (mListingInfo.isLocal() && position == getItemCount() - 1) {
//            holder.mIvGoCameraGallery.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    goCameraGallery();
//                }
//            });
//            return;
//        }
        if (isSelect) {
            holder.mCheckBox.setVisibility(View.VISIBLE);
            if (1 == mIsAddMap.get(position)) {
                holder.mCheckBox.setChecked(true);
            } else {
                holder.mCheckBox.setChecked(false);
            }
        } else {
            holder.mCheckBox.setVisibility(View.GONE);
        }


        if (mFileInfo.isVideo()) {
            if (mListingInfo.isLocal()) {
                Logger.i(TAG, "LocalFileUrl:" + mFileInfo.getLocalFileUrl());
                Glide.with(mFragment).load(mFileInfo.getLocalFileUrl()).placeholder(R.drawable.img_default).into(holder.mVideoIvThumb);
            } else {
                Logger.i(TAG, "HttpThumbUrl:" + UrlUtils.getCameraHttpThumbUrl(mListingInfo.getType(), mFileInfo.getFileThumbname()));
                Glide.with(mFragment).load(UrlUtils.getCameraHttpThumbUrl(mListingInfo.getType(), mFileInfo.getFileThumbname())).placeholder(R.drawable.img_default).into(holder.mVideoIvThumb);
            }
//            String length = mFileInfo.getLength();
//            if(length!=null){
//                if (length.equals("60")){
//                    length = "01:00";
//                }else {
//                    length = "00:"+length;
//                }
//                holder.mTvLength.setText(length);
//            }

            holder.mVideoTvTime.setText(mFileInfo.getStarttime());

            //点击跳转到预览界面
//            holder.mIvPlayIcon.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (!isSelect) {
//                        goPreview(position);
//                    } else {
//                        holder.mCheckBox.setChecked(!holder.mCheckBox.isChecked());
//                        if (holder.mCheckBox.isChecked()) {
//                            if (mListingInfo.isLocal()) {
//                                mSelectorIndexs.add(Integer.valueOf(position));
//                            } else {
//                                mSelectorIndexs.add(Integer.valueOf(mFileInfos.size() - position - 1));
//                            }
//                            mIsAddMap.put(position, 1);
//                        } else {
//                            if (mListingInfo.isLocal()) {
//                                mSelectorIndexs.remove(Integer.valueOf(position));
//                            } else {
//                                mSelectorIndexs.remove(Integer.valueOf(mFileInfos.size() - position - 1));
//                            }
//                            mIsAddMap.put(position, 0);
//                        }
//                        if (mSelectorIndexs.size()!=0){
//                            EventBus.getDefault().post(new MostDelInfoEvent(mSelectorIndexs));
//                        }
//                    }
//                }
//            });
            holder.mVideoIvThumb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isSelect) {
                        goPreview(position);
                    } else {
                        holder.mCheckBox.setChecked(!holder.mCheckBox.isChecked());


                        if (holder.mCheckBox.isChecked()) {
                            mSelectorIndexs.add(Integer.valueOf(position));
                            mIsAddMap.put(position, 1);
                        } else {
                            mSelectorIndexs.remove(Integer.valueOf(position));
                            mIsAddMap.put(position, 0);
                        }
                        if (mSelectorIndexs.size() > 0) {
                            EventBus.getDefault().post(new MostDelInfoEvent(mSelectorIndexs));
                            Logger.i(TAG, "recycler：" + mSelectorIndexs.toString());
                        }
                    }
                }
            });

        } else if (mFileInfo.isPhoto()) {

            if (mListingInfo.isLocal()) {
                Logger.i(TAG, "LocalFileUrl:" + mFileInfo.getLocalFileUrl());
                Glide.with(mFragment).load(mFileInfo.getLocalFileUrl()).placeholder(R.drawable.img_default).into(holder.mPhotoIvThumb);
            } else {
                Logger.i(TAG, "HttpThumbUrl:" + UrlUtils.getCameraHttpThumbUrl(mListingInfo.getType(), mFileInfo.getFileThumbname()));
                Glide.with(mFragment).load(UrlUtils.getCameraHttpThumbUrl(mListingInfo.getType(), mFileInfo.getFileThumbname())).placeholder(R.drawable.img_default).into(holder.mPhotoIvThumb);
            }
            holder.mPhotoTvTime.setText(mFileInfo.getStarttime());

            //点击跳转到预览界面
            holder.mPhotoIvThumb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isSelect) {
                        goPreview(position);
                    } else {
                        holder.mCheckBox.setChecked(!holder.mCheckBox.isChecked());


                        if (holder.mCheckBox.isChecked()) {
                            mSelectorIndexs.add(Integer.valueOf(position));
                            mIsAddMap.put(position, 1);
                        } else {
                            mSelectorIndexs.remove(Integer.valueOf(position));
                            mIsAddMap.put(position, 0);
                        }
                        if (mSelectorIndexs.size() > 0) {
                            EventBus.getDefault().post(new MostDelInfoEvent(mSelectorIndexs));
                        }
                    }
                }
            });
        }
        Logger.e(OkHttpRequest.getConnectionPool().connectionCount() + "       **********");

    }

    /**
     * 去预览
     *
     * @param currentPosition
     */

    private void goPreview(int currentPosition) {
        Intent intent = new Intent();
        intent.putExtra("currentPosition", currentPosition);
        intent.putExtra("listingInfo", mListingInfo);
        intent.setClass(mActivity, GalleryPreviewActivity.class);
        mActivity.startActivity(intent);
    }

    private void goCameraGallery() {
        Logger.e(TAG, "goCameraGallery");
        ((HomeActivity) mActivity).showTab(1);
    }


    @Override
    public int getItemCount() {
//        if (mListingInfo.isLocal()) {
//            return mFileInfos.size() + 1;
//        }
        return mFileInfos.size();
    }


    public class GalleryRecyclerViewHolder extends RecyclerView.ViewHolder {

        private ImageView mVideoIvThumb;
        private ImageView mPhotoIvThumb;
        private ImageView mIvPlayIcon;
        private TextView mTvLength;
        private TextView mVideoTvTime;
        private TextView mPhotoTvTime;
        private CheckBox mCheckBox;
        private ImageView mIvGoCameraGallery;


        public GalleryRecyclerViewHolder(View itemView, int viewType) {
            super(itemView);
            if (VIEW_TYPE_VIDEO == viewType) {
                mVideoIvThumb = (ImageView) itemView.findViewById(R.id.item_video_iv_photo);
                mIvPlayIcon = (ImageView) itemView.findViewById(R.id.item_video_iv_play);
                mTvLength = (TextView) itemView.findViewById(R.id.item_video_tv_length);
                mVideoTvTime = (TextView) itemView.findViewById(R.id.item_video_tv_time);
                mCheckBox = (CheckBox) itemView.findViewById(R.id.checkbox);
            } else if (VIEW_TYPE_PHOTO == viewType) {
                mPhotoIvThumb = (ImageView) itemView.findViewById(R.id.item_photo_iv_photo);
                mPhotoTvTime = (TextView) itemView.findViewById(R.id.item_photo_tv_time);
                mCheckBox = (CheckBox) itemView.findViewById(R.id.checkbox);
            }
//            else if (VIEW_TYPE_GO_CAMERA_GALLERY == viewType) {
//                mIvGoCameraGallery = (ImageView) itemView.findViewById(R.id.item_iv_go_camera_gallery);
//            }
        }
    }

    /**
     * 释放资源
     */
    public void releaseResources() {
        Logger.e(TAG, "releaseResources");
        EventBus.getDefault().unregister(this);
        CleanListUtils.releaseListingInfo(mListingInfo);
    }

}
