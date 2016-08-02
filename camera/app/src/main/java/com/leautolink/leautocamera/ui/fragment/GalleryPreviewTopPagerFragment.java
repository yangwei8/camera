package com.leautolink.leautocamera.ui.fragment;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.leautolink.leautocamera.R;
import com.leautolink.leautocamera.config.Constant;
import com.leautolink.leautocamera.domain.ListingInfo;
import com.leautolink.leautocamera.event.GoToDownloadCameraVideoEvent;
import com.leautolink.leautocamera.event.ScreenStateEvent;
import com.leautolink.leautocamera.event.VideoIsOnUsingEvent;
import com.leautolink.leautocamera.ui.base.BaseFragment;
import com.leautolink.leautocamera.utils.DateUtils;
import com.leautolink.leautocamera.utils.Logger;
import com.leautolink.leautocamera.utils.UrlUtils;
import com.leautolink.leautocamera.utils.mediaplayermanager.MedIaPlayerManager;
import com.leautolink.leautocamera.utils.mediaplayermanager.imls.CloudPlayer;
import com.leautolink.leautocamera.utils.mediaplayermanager.interfaces.IPlayer;
import com.leautolink.leautocamera.utils.mediaplayermanager.interfaces.IPlayerListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import de.greenrobot.event.EventBus;


/**
 * Created by tianwei1 on 2016/3/8.
 */
@EFragment(R.layout.fragment_gallery_preview_top)
public class GalleryPreviewTopPagerFragment extends BaseFragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, IPlayerListener {
    private static final String TAG = "GalleryPreviewTopPagerFragment";
    //View相关
    @ViewById(R.id.video_layout)
    RelativeLayout mVideoLayout;
    @ViewById(R.id.ll_loading_container)
    LinearLayout mLoadingContainer;
    @ViewById(R.id.iv_plane)
    ImageView mIvPlane;
    @ViewById(R.id.tv_loading_text)
    TextView mTvLoadingText;
    @ViewById(R.id.iv_top_thumb)
    ImageView mIvThumb;
    @ViewById(R.id.iv_play)
    ImageView mIvPlay;
    @ViewById(R.id.iv_pause)
    ImageView mIvPause;
    @ViewById(R.id.sb_progress)
    SeekBar mSbProgress;
    @ViewById(R.id.tv_current_length)
    TextView mTvCurrentLength;

    //数据相关
    private ListingInfo.FileInfo mFileInfo;
    private int mI;
    private String mType;

    //播放器相关
//    private List<MediaSource> sourceList;
//    private PlayController mPlayController;
//    private MediaPlayer mMp;
    private IPlayer mPlayer;
    private int mDuration;
    private Handler mHandler;
    //播放状态相关
    private boolean isFirstPlay;
    private boolean isShowLoading = false;
//    private boolean isOnPlaying;
//    private boolean isOnPause;
//    private boolean isOnSuspend;
//    private boolean isOnStop;
//    private boolean isOnCompletion;

    private boolean isReleased;
    //是否拖动进度条
    private boolean isTrackingTouch = false;
    //是否左右滑动状态
    private boolean isSliding;

    public void onEventMainThread(ScreenStateEvent event) {
        Logger.e(TAG, "event.isScreenOn():" + event.isScreenOn());
        if (!event.isScreenOn()) {
            if (isShowLoading) {
                releaseResourcesWhenSlide();
            } else if (mPlayer != null && (mPlayer.isPlaying() || mPlayer.isPause())) {
                mPlayer.suspend();
            } /*else if (mPlayController != null && (isOnPlaying || isOnPause)) {
                mPlayController.suspend();
            } */
        } else {
            mPlayer.resume();
        }
    }

    /**
     * 如果视频正在播放，这时点击下载，那么暂停播放
     *
     * @param event
     */
    public void onEventMainThread(GoToDownloadCameraVideoEvent event) {
        if (event.isGoToDownloadCameraVideo()) {
            if (mPlayer != null && mPlayer.isPlaying()) {
                pauseVideo();
            }
        }
    }


    @AfterViews
    public void init() {
        isFirstPlay = true;
//        mPlayer = new LeHighPlayer();
        mPlayer = MedIaPlayerManager.createPlayer();
        initView();
        initData();
        initListener();
    }

    @Override
    public void onPause() {
        super.onPause();

       /* if (mPlayController != null && (isOnPlaying || isOnPause)) {
            mPlayController.suspend();
        }*/
        if (mPlayer != null && (mPlayer.isPlaying() || mPlayer.isPause())) {
            mPlayer.suspend();
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        /*if (mPlayController != null && (isOnPlaying || isOnPause)) {
            mPlayController.resume();
        }*/
        if (mPlayer != null && mPlayer.isSuspend()) {
            mPlayer.resume();
        }
    }

    private void initView() {

        if (mFileInfo != null) {
            if (mFileInfo.isVideo()) {
                mIvPlay.setVisibility(View.VISIBLE);
//                mTvCurrentLength.setVisibility(View.VISIBLE);
            } else if (mFileInfo.isPhoto()) {
                mIvPlay.setVisibility(View.GONE);
                mSbProgress.setVisibility(View.GONE);
//                mTvCurrentLength.setVisibility(View.GONE);
            }
            mTvCurrentLength.setVisibility(View.GONE);
        }
    }

    private void initData() {
        if (mFileInfo != null) {
            if (mFileInfo.isPhoto()) {
                if (TextUtils.isEmpty(mFileInfo.getFilesize())) {//本地
                    Logger.i(TAG, mI + "---initData local url :" + mFileInfo.getLocalFileUrl());
                    Glide.with(this).load(mFileInfo.getLocalFileUrl()).placeholder(R.drawable.img_default).into(mIvThumb);
                } else {
                    Logger.i(TAG, mI + "---initData httpUrl :" + UrlUtils.getCameraMvideoHttpUrl(mType, mFileInfo.getFilename()));
                    Glide.with(this).load(UrlUtils.getCameraMvideoHttpUrl(mType, mFileInfo.getFilename())).placeholder(R.drawable.img_default).into(mIvThumb);
                }
            } else if (mFileInfo.isVideo()) {
                if (!TextUtils.isEmpty(mFileInfo.getLocalFileUrl())) {
                    Logger.i(TAG, mI + "---initData local url :" + mFileInfo.getLocalFileUrl());
                    Glide.with(this).load(mFileInfo.getLocalFileUrl()).placeholder(R.drawable.img_default).into(mIvThumb);
                } else {
                    Logger.i(TAG, mI + "---initData httpUrl :" + UrlUtils.getCameraMvideoHttpUrl(mType, mFileInfo.getFileThumbname()));
//                    initVideoLength();
                    Glide.with(this).load(UrlUtils.getCameraHttpThumbUrl(mType, mFileInfo.getFileThumbname())).placeholder(R.drawable.img_default).into(mIvThumb);
                }
            }
        }
    }

    public void setData(int i, String type, ListingInfo.FileInfo fileInfo) {
        mI = i;
        mType = type;
        mFileInfo = fileInfo;
    }


    private void initListener() {
        if (mFileInfo != null) {
            if (mFileInfo.isVideo()) {
                initVideoListener();
            } else if (mFileInfo.isPhoto()) {
                initPhotoListener();
            }
        }
    }

    /**
     * 初始化Video相关的监听
     */
    private void initVideoListener() {
        mIvPlay.setOnClickListener(this);
        mVideoLayout.setOnClickListener(this);
        mIvPause.setOnClickListener(this);
        mSbProgress.setOnSeekBarChangeListener(this);
    }

    /**
     * 初始化Photo相关的监听
     */
    private void initPhotoListener() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_play:
                playVideo();
                break;
            case R.id.video_layout:
                if (mPlayer != null && mPlayer.isPlaying()) {
                    pauseVideo();
                }
                break;
            case R.id.iv_pause:
                play();
                break;
        }
    }

    /**
     * 更新播放时间和进度条
     */
    private void initHandler() {
        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                if (mPlayer != null && (mPlayer.isResume() || mPlayer.isPlaying()) && (!isTrackingTouch)) {
                    int currentPosition = mPlayer.getCurrentPosition();
                    mSbProgress.setProgress(currentPosition);
                    updateText(currentPosition);
                    mHandler.sendEmptyMessageDelayed(0, 1000);
                }
            }
        };
       /* mHandler = new Handler() {
            public void handleMessage(Message msg) {
                if (mMp != null && isOnPlaying && !isTrackingTouch) {
                    int currentPosition = mMp.getCurrentPosition();
                    mSbProgress.setProgress(currentPosition);
                    updateText(currentPosition);
                    mHandler.sendEmptyMessageDelayed(0, 1000);
                }
            }
        };*/
    }


    /**
     * 更新两边的时间text
     */
    private void updateText(int currentPlayedPosition) {

//        if (isOnPlaying) {
        Logger.e(TAG, "currentPlayedPosition" + currentPlayedPosition);
//            String palyedTime = null;
        String palyedTime = DateUtils.formatHMS(String.valueOf(currentPlayedPosition + 1000));
        if (TextUtils.isEmpty(mFileInfo.getLocalFileUrl())) {
            mTvCurrentLength.setText(palyedTime + " / " + DateUtils.formatHMS(String.valueOf(mDuration)));
        } else {
            mTvCurrentLength.setText(palyedTime + " / " + DateUtils.formatHMS(String.valueOf(mDuration)));
        }
//        }
       /* if (null != mPlayController && isOnPlaying) {
            Logger.e(TAG, "currentPlayedPosition" + currentPlayedPosition);
//            String palyedTime = null;
            String palyedTime = DateUtils.formatHMS(String.valueOf(currentPlayedPosition + 1000));
            if (TextUtils.isEmpty(mFileInfo.getLocalFileUrl())) {
                mTvCurrentLength.setText(palyedTime + " / " + DateUtils.formatHMS(String.valueOf(mDuration)));
            } else {
                mTvCurrentLength.setText(palyedTime + " / " + DateUtils.formatHMS(String.valueOf(mDuration)));
            }
        }*/
    }

    /**
     * 暂停视频
     */
    private void pauseVideo() {
       /* if (mPlayController != null && isOnPlaying) {
            mPlayController.pause();
//            mMp.suspend();
            Logger.e(TAG, "pauseVideo");
            isOnPause = true;
            isOnPlaying = false;
            isOnStop = false;
            isOnCompletion = false;
            if (!isTrackingTouch) {
                mIvPause.setVisibility(View.VISIBLE);
            }
        }*/
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
//            mMp.suspend();
            Logger.e(TAG, "pauseVideo");
//            resetPlayerStatus();
//            isOnPause = true;
            if (!isTrackingTouch) {
                mIvPause.setVisibility(View.VISIBLE);
            }
        }
    }


    /**
     * 播放视频
     */
    private void playVideo() {
        Logger.e(TAG, "playVideo");
        isReleased = false;
        EventBus.getDefault().post(new VideoIsOnUsingEvent(false, false));
        Constant.isLive = false;
        isSliding = false;
//        isShowLoading = true;
        mVideoLayout.setVisibility(View.VISIBLE);
        mIvPlay.setVisibility(View.GONE);
        mIvThumb.setVisibility(View.GONE);
      /*  if (mPlayController == null) {//第一次播放
            initPlayerController();
        } else if (mPlayController != null && isOnPause) {//暂停时播放
            play();
        }*/
        if (isFirstPlay) {//第一次播放
            isFirstPlay = false;
            initPlayerController();
        } else if (mPlayer != null && mPlayer.isPause()) {//暂停时播放
            play();
        }
    }


    /**
     * 初始化媒体播放器
     */
    private void initPlayerController() {
        Logger.e(TAG, "initPlayerController");
        showLoadingPlane();
        String videoUrl = null;
        if (TextUtils.isEmpty(mFileInfo.getLocalFileUrl())) {
//            videoUrl = UrlUtils.getCameraVideoRtspUrl(mType, mFileInfo.getFilename());
            videoUrl = UrlUtils.getCameraSvideoHttpUrl(mType, mFileInfo.getFilename());
        } else {
            videoUrl = mFileInfo.getLocalFileUrl();
        }

        Logger.e(TAG, "videlUrl:" + videoUrl);

        mPlayer.setPlayerListener(this);
//        mPlayer.play(mActivity, mVideoLayout, videoUrl, LeHighPlayer.PLAY_TYPE_MP4, LeHighPlayer.ENCODE_TYPE_SOFT);
        mPlayer.play(mActivity, mVideoLayout, videoUrl, MedIaPlayerManager.PLAY_TYPE_VOD, MedIaPlayerManager.ENCODE_TYPE_SOFT);

       /* mPlayController = new PlayController(mActivity, mVideoLayout);
        mPlayController.setPlayerListener(this);
        sourceList = new ArrayList<MediaSource>();
        MediaSource source = new MediaSource();
        String videoUrl = null;
        if (TextUtils.isEmpty(mFileInfo.getLocalFileUrl())) {
//            videoUrl = UrlUtils.getCameraVideoRtspUrl(mType, mFileInfo.getFilename());
            videoUrl = UrlUtils.getCameraSvideoHttpUrl(mType, mFileInfo.getFilename());
        } else {
            videoUrl = mFileInfo.getLocalFileUrl();
        }


        Logger.e(TAG, "initPlayerController():" + videoUrl);
        // 设置播放源,必须设置
        source.setSource(videoUrl);
        // 设置媒体类型,必须设置,默认为点播
        source.setType(MediaSource.TYPE_VOD);
        // 设置媒体名称,可以不设置
//        source.setName(channel.getName() + (walkCde ? "_CDE" : "_RAW"));
        // 设置是否加密(true则要经过LinkShell, false则反之),设置是否中转(true则要通过CDE, false则反之)
        source.setEncrypt(false).setTransfer(false);
        sourceList.add(source);
        this.mPlayController.play(sourceList);*/
    }

    /**
     * 暂停之后重新播放视频
     */
    public void play() {

        if (mPlayer != null) {
            if ((mPlayer.isResume() || mPlayer.isPause()) && !mPlayer.isComplete()) {
                mPlayer.playAfterPause();
                Logger.e(TAG, "play");
                mIvPause.setVisibility(View.GONE);
                mHandler.sendEmptyMessage(0);
            }
        }
       /* if (mPlayController != null && isOnPause && !isOnCompletion) {
            mPlayController.start();
            Logger.e(TAG, "play");
            isOnPlaying = true;
            isOnPause = false;
            isOnStop = false;
            isOnCompletion = false;
            mIvPause.setVisibility(View.GONE);
            mHandler.sendEmptyMessage(0);
        }*/
    }


   /* @Override
    public void onLoadingStart(MediaPlayer mp) {
        mMp = mp;
        Logger.e(TAG, "onLoadingStart mp=" + mp);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mMp = mp;

        Logger.i(TAG, "onPrepared");
        if (!isSliding) {

            //Modify by donghongwei 20160402 for LECAMERA-387 begin
            if (mPlayController != null) {
                mPlayController.start();
            }
            //Modify by donghongwei 20160402 for LECAMERA-387 end

            isOnPlaying = true;
            isOnPause = false;
            isOnStop = false;
            isOnCompletion = false;

            mSbProgress.setVisibility(View.VISIBLE);

            mDuration = mMp.getDuration();
            Logger.i(TAG, "视频的长度：" + mDuration);
            mSbProgress.setMax(mDuration);
            initHandler();

            //开始记时，更新进度
            if (mHandler != null)
                mHandler.sendEmptyMessage(0);
            hideLoadingPlane();
        }

    }

    @Override
    public void onBufferingUpdate(int percent) {

    }

    @Override
    public void onSeekComplete() {

    }*/

    //进度条相关回调
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        updateText(mSbProgress.getProgress());
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        isTrackingTouch = true;

        if (mPlayer != null) {
            //播放的时候快进快退
            if (mPlayer.isPlaying()) {
                pauseVideo();
            }
            //暂停的时候快进快退
            if (mPlayer.isPause()) {
                mIvPause.setVisibility(View.GONE);
            }
        }
        showLoadingPlane();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        isTrackingTouch = false;
        mPlayer.seekTo(seekBar.getProgress());
        /*if (mMp != null) {
            mMp.seekTo(seekBar.getProgress());
            mMp.setOnSeekCompleteListener(this);
        }*/

    }

    //快进快退完成回调
   /* @Override
    public void onSeekComplete(MediaPlayer mediaPlayer) {
        play();
        hideLoadingPlane();
        isTrackingTouch = false;
        if (mHandler != null) {
            mHandler.sendEmptyMessageDelayed(0, 1000);
        }
    }

    @Override
    public boolean onCompletion() {

        Logger.e(TAG, "onCompletion");
        isOnPlaying = false;
        isOnPause = false;
        isOnStop = false;
        isOnCompletion = true;

        mIvPlay.setVisibility(View.VISIBLE);
        mIvThumb.setVisibility(View.VISIBLE);
        initSeekBarProgress();
        mSbProgress.setVisibility(View.GONE);
        mVideoLayout.setVisibility(View.GONE);
        initVideoLength();
        cancelHandler();
        releaseMediaResources();

        return false;
    }

    @Override
    public void onInfo(int what, long extra) {

    }

    @Override
    public boolean onError(int what, String extra) {

        hideLoadingPlane();
        showToastSafe("无法播放该视频文件：" + what);
        Logger.i(TAG, "onError:" + "what:" + what + "-extra:" + extra);
        return true;
    }*/

    private void showLoadingPlane() {
        Logger.i(TAG, "showLoadingPlane");
        isShowLoading = true;
        mLoadingContainer.setVisibility(View.VISIBLE);
        mIvPlane.setVisibility(View.VISIBLE);
        mTvLoadingText.setVisibility(View.VISIBLE);
        Animation hyperspaceJumpAnimation = android.view.animation.AnimationUtils.loadAnimation(
                mActivity, R.anim.loading_animation);
        mIvPlane.startAnimation(hyperspaceJumpAnimation);
    }

    private void hideLoadingPlane() {
        Logger.e(TAG, "isShowing :" + isShowLoading + "mIvPlane :" + mIvPlane + "vi :");
        if (isShowLoading && mIvPlane != null && mIvPlane.getVisibility() == View.VISIBLE) {
            Logger.i(TAG, "hideLoadingDialog");
            mIvPlane.clearAnimation();
            mIvPlane.setVisibility(View.GONE);
            mTvLoadingText.setVisibility(View.GONE);
            mLoadingContainer.setVisibility(View.GONE);
            isShowLoading = false;
            if (!isReleased) {
                //可以下载，不可以删除
                EventBus.getDefault().post(new VideoIsOnUsingEvent(false, true));
            }
        }
    }

    /**
     * 初始化视频长度信息
     */
    private void initVideoLength() {
        if (mTvCurrentLength != null && mTvCurrentLength.getVisibility() == View.VISIBLE) {
            if (!TextUtils.isEmpty(mFileInfo.getLocalFileUrl())) {
                mTvCurrentLength.setText("00:00 / " + mFileInfo.getLength());
            } else
                mTvCurrentLength.setText("00:00 / 01:00");
        }
    }

    /**
     * 初始化SeekBar的进度
     */
    private void initSeekBarProgress() {
        if (mSbProgress != null && mSbProgress.getVisibility() == View.VISIBLE) {
            mSbProgress.setProgress(0);
        }
    }

    /**
     * 释放媒体资源
     */
    private void releaseMediaResources() {
        if (mPlayer != null) {
            Logger.e(TAG, "releaseMediaResources");
            mPlayer.release();
            isFirstPlay = true;
        }

        /*if (mMp != null) {
            Logger.e(TAG, "releaseMediaResources");
//            if (!isOnCompletion && mMp.isPlaying()) {
//                mMp.stop();
//                isOnStop = true;
//            }
            mMp.release();
            mMp = null;
        }
        if (sourceList != null) {
            sourceList.clear();
            sourceList = null;
        }
        if (mPlayController != null) {
            mPlayController.removeViewFromContainer();
            mPlayController = null;
        }*/
        //可以下载也可以删除
        EventBus.getDefault().post(new VideoIsOnUsingEvent(true, true));
    }

   /* private void resetPlayerStatus() {
        isOnSuspend = false;
        isOnPlaying = false;
        isOnPause = false;
        isOnStop = false;
        isOnCompletion = false;
    }*/

    private void cancelHandler() {
        if (mHandler != null) {
            mHandler.removeMessages(0);
            mHandler = null;
        }
    }

    @Override
    public void releaseResources() {
        isReleased = true;
        releaseMediaResources();
        hideLoadingPlane();
        cancelHandler();
//        resetPlayerStatus();
        Constant.isLive = true;
    }

    /**
     * 滑动的时候释放资源
     */
    public void releaseResourcesWhenSlide() {
        Logger.i(TAG, mI + "---releaseResourcesWhenSlide");
        isSliding = true;
        releaseResources();
        resetView();
//        initVideoLength();

    }

    private void resetView() {
        if (mFileInfo != null) {
            if (mFileInfo.isVideo()) {
                mIvPlay.setVisibility(View.VISIBLE);
                mIvThumb.setVisibility(View.VISIBLE);
                mIvPause.setVisibility(View.GONE);
                mVideoLayout.setVisibility(View.GONE);
                mSbProgress.setVisibility(View.GONE);
                mTvCurrentLength.setVisibility(View.GONE);
            }
        }
    }


    @Override
    public void onPrepared() {
//        if (!isSliding) {
//            mSbProgress.setVisibility(View.VISIBLE);
//            mDuration = mPlayer.getDuration();
//            Logger.i(TAG, "视频的长度：" + mDuration);
//            mSbProgress.setMax(mDuration);
//            initHandler();
//
//            //开始记时，更新进度
//            if (mHandler != null)
//                mHandler.sendEmptyMessage(0);
//            hideLoadingPlane();
//        }
    }

    @Override
    public void onFirstPic() {
        Logger.e(TAG, "onFirstPic");
//        if (!isOnSuspend) {
//            resetPlayerStatus();
//            isOnPlaying = true;
        if (mPlayer != null && !isSliding) {
            mSbProgress.setVisibility(View.VISIBLE);
            mTvCurrentLength.setVisibility(View.VISIBLE);
            mDuration = mPlayer.getDuration();
            Logger.i(TAG, "视频的长度：" + mDuration);
            mSbProgress.setMax(mDuration);
            initHandler();

            //开始记时，更新进度
            if (mHandler != null)
                mHandler.sendEmptyMessage(0);
            hideLoadingPlane();
        }
    }

    @Override
    public void onBufferingStart() {

    }

    @Override
    public void onBufferingUpdate(int percent) {

    }

    @Override
    public void onBufferingEnd() {

    }

    @Override
    public void onComplete() {
        Logger.e(TAG, "onCompletion");
//        resetPlayerStatus();
//        isOnCompletion = true;

//        mIvPlay.setVisibility(View.VISIBLE);
//        mIvThumb.setVisibility(View.VISIBLE);
//        initSeekBarProgress();
//        mSbProgress.setVisibility(View.GONE);
//        mVideoLayout.setVisibility(View.GONE);
//        initVideoLength();
        resetView();
        cancelHandler();
        releaseMediaResources();
    }

    @Override
    public void onError(int what, String extra) {
        resetView();
        hideLoadingPlane();
        showToastSafe("无法播放该视频文件：" + what);
        Logger.i(TAG, "onError:" + "what:" + what + "-extra:" + extra);
    }

    @Override
    public void onInfo(int what, long extra) {

    }

    @Override
    public void onBlock() {

    }

    @Override
    public void onSeekComplete() {
        play();
        hideLoadingPlane();
        isTrackingTouch = false;
        if (mHandler != null) {
            mHandler.sendEmptyMessageDelayed(0, 1000);
        }
    }
}
