//package com.leautolink.leautocamera.utils.mediaplayermanager.imls;
//
//import android.content.Context;
//import android.graphics.SurfaceTexture;
//import android.os.Bundle;
//import android.os.Handler;
//import android.view.Surface;
//import android.view.TextureView;
//import android.view.ViewGroup;
//
//import com.leautolink.leautocamera.utils.Logger;
//import com.leautolink.leautocamera.utils.mediaplayermanager.MedIaPlayerManager;
//import com.leautolink.leautocamera.utils.mediaplayermanager.interfaces.IPlayer;
//import com.leautolink.leautocamera.utils.mediaplayermanager.interfaces.IPlayerListener;
//import com.lxsj.sdk.player.Interface.LeVideoView;
//import com.lxsj.sdk.player.LeVideoViewBuilder;
//import com.lxsj.sdk.player.manager.LeMediaPlayerManager;
//import com.lxsj.sdk.player.manager.util.PlayerManagerProxy;
//import com.lxsj.sdk.player.util.Constants;
//
///**
// * Created by tianwei on 16/6/15.
// */
//public class LeHighPlayer implements IPlayer {
//
//    private static final String TAG = "LeHighPlayer";
//
//
//    private Context mContext;
//    private ViewGroup mVideoContainer;
//    //    private static LeHighPlayer mLeHighPlayer;
//    private IPlayerListener mPlayerListener;
//    private TextureView mTextureView;
//    private LeMediaPlayerManager mPlayerManager;
//    private SurfaceTexture mSurfaceTexture;
//    private LeOnPreparedListener mLeOnPreparedListener;
//    private LeOnFirstPicListener mLeOnFirstPicListener;
//    private LeOnBufferingStartListener mLeOnBufferingStartListener;
//    private LeOnBufferingUpdateListener mLeOnBufferingUpdateListener;
//    private LeOnBufferingEndListener mLeOnBufferingEndListener;
//    private LeOnCompletionListener mLeOnCompletionListener;
//    private LeOnSeekCompleteListener mLeOnSeekCompleteListener;
//    private LeOnBlockListener mLeOnBlockListener;
//    private LeOnErrorListener mLeOnErrorListener;
//    private MHoldCallback mHoldCallback;
//    private boolean isOnPlaying;
//    private boolean isOnSuspend;
//    //    private boolean isOnPause;
//    private boolean isOnResume;
//    private boolean isOnCompelte;
//
//    private boolean isOnRelased;
//
//    private String mUrl;
//
////    private LeHighPlayer() {
////    }
////
////    public static LeHighPlayer newInstance() {
////        if (mLeHighPlayer == null) {
////            synchronized (LeHighPlayer.class) {
////                if (mLeHighPlayer == null) {
////                    mLeHighPlayer = new LeHighPlayer();
////                }
////            }
////        }
////        return mLeHighPlayer;
////    }
//
//    @Override
//    public void init(Context context) {
//    }
//
//    @Override
//    public void initPlayer(Context context, ViewGroup videoLayout, int playType) {
//
//    }
//
//    @Override
//    public void play(Context context, ViewGroup videoLayout, String url, int playType, int encodeType) {
//        Logger.e(TAG, "play url :" + url);
//        resetPlayerStatus();
//        mContext = context;
//        mVideoContainer = videoLayout;
//        mUrl = url;
////
//        if (mPlayerManager == null) {
//            mPlayerManager = new LeMediaPlayerManager();
//            int result = -1;
//            if (playType == MedIaPlayerManager.PLAY_TYPE_MP4) {
//                result = mPlayerManager.build(mContext, LeVideoViewBuilder.Type.MP4);
//            } else if (playType == MedIaPlayerManager.PLAY_TYPE_LIVE) {
//                result = mPlayerManager.build(mContext, LeVideoViewBuilder.Type.RTSP);
//            }
//            if (result == 1) {
//                //软解码
//                if (encodeType == LeHighPlayer.ENCODE_TYPE_SOFT) {
//                    mPlayerManager.setEncodeType(Constants.EncodeType.SOFT);
//                } else if (encodeType == LeHighPlayer.ENCODE_TYPE_HARD) {
//                    mPlayerManager.setEncodeType(Constants.EncodeType.HARD);
//                }
//                mPlayerManager.setCmdHeader(new Handler());
//                initPlayerListener();
//                if (mTextureView == null) {
//                    mTextureView = new TextureView(mContext);
//                    mTextureView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//                    mVideoContainer.addView(mTextureView);
//                }
//                initSurfaceTextureListener();
//            } else {
//                Logger.e(TAG, "播放器创建失败  result：" + result);
//                mPlayerListener.onError(result, "创建播放器失败");
//            }
//        }
//    }
//
//    @Override
//    public void setVolum(int volum) {
//
//    }
//
//    @Override
//    public void play(String url) {
//
//    }
//
//    @Override
//    public void setPlayerListener(IPlayerListener playerListener) {
//        mPlayerListener = playerListener;
//    }
//
//    @Override
//    public void playAfterPause() {
//        if (mPlayerManager != null) {
//            resetPlayerStatus();
//            isOnPlaying = true;
//            mPlayerManager.start();
//        }
//    }
//
//    @Override
//    public void pause() {
//        if (mPlayerManager != null) {
//            resetPlayerStatus();
//            mPlayerManager.pause();
//        }
//    }
//
//    @Override
//    public void suspend() {
//        if (mPlayerManager != null) {
//            resetPlayerStatus();
//            isOnSuspend = true;
//            mPlayerManager.suspendPlayer();
//        }
//    }
//
//
//    @Override
//    public void resume() {
//        if (mPlayerManager != null) {
//            resetPlayerStatus();
//            isOnResume = true;
//            mPlayerManager.resumePlayer();
////            if (mPlayerManager != null) {
////                if (isOnSuspend) {
////                    mPlayerManager.setSurface(new Surface(mSurfaceTexture));
////                    mPlayerManager.resumePlayer();
////                    if (!isOnPause) {
////                        resetPlayerStatus();
////                        isOnPlaying = true;
////                    }
////                } else {
////                    preparePlay();
////                }
////            }
////            mPlayerManager.resumePlayer();
//        }
//    }
//
//    @Override
//    public void stop() {
//        resetPlayerStatus();
//    }
//
//    @Override
//    public void release() {
//        Logger.e(TAG, "releaseMediaResources");
//        resetPlayerStatus();
//        if (mVideoContainer != null && mTextureView != null) {
//            mVideoContainer.removeView(mTextureView);
//        }
//
//        if (mSurfaceTexture != null) {
//            mSurfaceTexture.release();
//        }
//
//        if (mPlayerManager != null) {
//            mPlayerManager.releasePlayer(true);
//        }
//
//        mLeOnPreparedListener = null;
//        mLeOnFirstPicListener = null;
//        mLeOnBufferingStartListener = null;
//        mLeOnBufferingUpdateListener = null;
//        mLeOnBufferingEndListener = null;
//        mLeOnCompletionListener = null;
//        mLeOnSeekCompleteListener = null;
//        mLeOnBlockListener = null;
//        mLeOnErrorListener = null;
//        mHoldCallback = null;
//        mTextureView = null;
//        mSurfaceTexture = null;
//        mPlayerManager = null;
//        mVideoContainer = null;
//
//        isOnRelased = true;
//    }
//
//    @Override
//    public int getDuration() {
//        int duration = 0;
//        if (mPlayerManager != null) {
//            duration = (int) mPlayerManager.getDuration();
//        }
//        return duration;
//    }
//
//    @Override
//    public int getCurrentPosition() {
//        int currectPostion = 0;
//        if (mPlayerManager != null) {
//            currectPostion = (int) mPlayerManager.getPosition();
//        }
//        return currectPostion;
//    }
//
//    @Override
//    public TextureView getTextureView() {
//        return mTextureView;
//    }
//
//    @Override
//    public void seekTo(int position) {
//        if (mPlayerManager != null) {
//            if (position > getCurrentPosition()) {//快进
//                mPlayerManager.seekTo(position, 0);
//            } else {
//                mPlayerManager.seekTo(position, 1);
//            }
//        }
//    }
//
//    @Override
//    public boolean isPlaying() {
//        return isOnPlaying;
//    }
//
//    @Override
//    public boolean isPause() {
//        if (mPlayerManager != null) {
//            return mPlayerManager.isPaused();
//        }
//        return false;
//    }
//
//    @Override
//    public boolean isSuspend() {
//        return isOnSuspend;
//    }
//
//    @Override
//    public boolean isResume() {
//        return isOnResume;
//    }
//
//    @Override
//    public boolean isComplete() {
//        return isOnCompelte;
//    }
//
//    @Override
//    public boolean isReleased() {
//        return isOnRelased;
//    }
//
//    /**
//     * 初始化播放器控制器监听
//     */
//    private void initSurfaceTextureListener() {
//        Logger.e(TAG, "initSurfaceTextureListener");
//        if (mHoldCallback == null) {
//            mHoldCallback = new MHoldCallback();
//        }
//        mTextureView.setSurfaceTextureListener(mHoldCallback);
//        mTextureView.setDrawingCacheEnabled(false);
//    }
//
//    /**
//     * 初始化播放器的监听
//     */
//    private void initPlayerListener() {
//        Logger.e(TAG, "initPlayerListener");
//        if (mLeOnPreparedListener == null) {
//            mLeOnPreparedListener = new LeOnPreparedListener();
//        }
//        if (mLeOnFirstPicListener == null) {
//            mLeOnFirstPicListener = new LeOnFirstPicListener();
//        }
//        if (mLeOnBufferingStartListener == null) {
//            mLeOnBufferingStartListener = new LeOnBufferingStartListener();
//        }
//        if (mLeOnBufferingUpdateListener == null) {
//            mLeOnBufferingUpdateListener = new LeOnBufferingUpdateListener();
//        }
//        if (mLeOnBufferingEndListener == null) {
//            mLeOnBufferingEndListener = new LeOnBufferingEndListener();
//        }
//        if (mLeOnCompletionListener == null) {
//            mLeOnCompletionListener = new LeOnCompletionListener();
//        }
//        if (mLeOnSeekCompleteListener == null) {
//            mLeOnSeekCompleteListener = new LeOnSeekCompleteListener();
//        }
//        if (mLeOnBlockListener == null) {
//            mLeOnBlockListener = new LeOnBlockListener();
//        }
//        if (mLeOnErrorListener == null) {
//            mLeOnErrorListener = new LeOnErrorListener();
//        }
//
//        mPlayerManager.setOnPreparedListener(mLeOnPreparedListener);
//        //获取第一帧画面回调
//        mPlayerManager.setOnFirstPicListener(mLeOnFirstPicListener);
//
//        mPlayerManager.setOnBufferingStartListener(mLeOnBufferingStartListener);
//        mPlayerManager.setOnBufferingUpdateListener(mLeOnBufferingUpdateListener);
//        mPlayerManager.setOnBufferingEndListener(mLeOnBufferingEndListener);
//
//        mPlayerManager.setOnCompletionListener(mLeOnCompletionListener);
//        mPlayerManager.setOnSeekCompleteListener(mLeOnSeekCompleteListener);
//
//        //卡顿回调
//        mPlayerManager.setOnBlockListener(mLeOnBlockListener);
//        //错误回调
//        mPlayerManager.setOnErrorListener(mLeOnErrorListener);
//    }
//
//    private class LeOnPreparedListener implements LeVideoView.OnPreparedListener {
//        @Override
//        public void callBack() {
//            Logger.e(TAG, "LeOnPreparedListener");
//            mPlayerManager.start();
//            mPlayerListener.onPrepared();
//        }
//    }
//
//    private class LeOnFirstPicListener implements LeVideoView.OnFirstPicListener {
//        @Override
//        public void callBack() {
//            Logger.e(TAG, "LeOnFirstPicListener");
//            resetPlayerStatus();
//            isOnPlaying = true;
//            mPlayerListener.onFirstPic();
//        }
//    }
//
//    private class LeOnBufferingStartListener implements LeVideoView.OnBufferingStartListener {
//        @Override
//        public void callBack() {
//            Logger.e(TAG, "LeOnBufferingStartListener");
//            mPlayerListener.onBufferingStart();
//        }
//    }
//
//    private class LeOnBufferingUpdateListener implements LeVideoView.OnBufferingUpdateListener {
//
//        @Override
//        public void callBack(int i) {
////            Logger.e(TAG, "LeOnBufferingUpdateListener:--" + i);
//            mPlayerListener.onBufferingUpdate(i);
//        }
//    }
//
//    private class LeOnBufferingEndListener implements LeVideoView.OnBufferingEndListener {
//        @Override
//        public void callBack() {
//            Logger.e(TAG, "LeOnBufferingEndListener");
//            mPlayerListener.onBufferingEnd();
//        }
//    }
//
//    private class LeOnCompletionListener implements LeVideoView.OnCompletionListener {
//        @Override
//        public void callBack() {
//            Logger.e(TAG, "LeOnCompletionListener");
//            resetPlayerStatus();
//            isOnCompelte = true;
//            mPlayerListener.onComplete();
//        }
//    }
//
//    private class LeOnSeekCompleteListener implements LeVideoView.OnSeekCompleteListener {
//        @Override
//        public void callBack() {
//            Logger.e(TAG, "LeOnSeekCompleteListener");
//            mPlayerListener.onSeekComplete();
//        }
//    }
//
//    private class LeOnBlockListener implements LeVideoView.OnBlockListener {
//
//        @Override
//        public void onBlock() {
//            Logger.e(TAG, "LeOnBlockListener");
//            mPlayerListener.onBlock();
//        }
//    }
//
//    private class LeOnErrorListener implements LeVideoView.OnErrorListener {
//        @Override
//        public void callBack(String s, int i) {
//            Logger.e(TAG, "LeOnErrorListener--" + "errorInfo :" + s + "---errorCode :" + i);
//            resetPlayerStatus();
//            mPlayerListener.onError(i, s);
//        }
//    }
//
//    private class MHoldCallback implements TextureView.SurfaceTextureListener {
//
//        @Override
//        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
//            Logger.e(TAG, "onSurfaceTextureAvailable");
//            mSurfaceTexture = surface;
//            preparePlay();
////            if (mPlayerManager != null) {
////                if (isOnSuspend) {
////                    mPlayerManager.setSurface(new Surface(mSurfaceTexture));
////                    mPlayerManager.resumePlayer();
////                    if (!isOnPause) {
////                        resetPlayerStatus();
////                        isOnPlaying = true;
////                    }
////                } else {
////                    preparePlay();
////                }
////            }
//        }
//
//        @Override
//        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
//            Logger.e(TAG, "onSurfaceTextureSizeChanged");
//            if (mPlayerManager != null) {
//                mPlayerManager.sizeChanged(width, height);
//            }
//        }
//
//        @Override
//        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
//            Logger.e(TAG, "onSurfaceTextureDestroyed");
////            if (mPlayerManager != null && (isOnPlaying || isOnPause)) {
////                mPlayerManager.suspendPlayer();
////                isOnPlaying = false;
////                isOnSuspend = true;
////            }
//            return false;
//        }
//
//        @Override
//        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
////            Logger.e(TAG, "onSurfaceTextureUpdated");
//        }
//    }
//
//    /**
//     * 准备播放
//     */
//    private void preparePlay() {
//        Logger.e(TAG, "preparePlay");
//        if (mSurfaceTexture != null) {
//            mPlayerManager.setSurface(new Surface(mSurfaceTexture));
//        }
//        toPlayVideo();
//    }
//
//    /**
//     * 去播放
//     */
//    private void toPlayVideo() {
//        Logger.e(TAG, "toPlayVideo videoUrl is :" + mUrl);
//        Bundle bundle = new Bundle();
//        bundle.putSerializable(PlayerManagerProxy.PLAY_MODE_KEY, LeMediaPlayerManager.PlayMode.PLAY_URL_NO_CDE);
//        bundle.putString(PlayerManagerProxy.PLAY_URL_KEY, mUrl);
//        mPlayerManager.playVideo(bundle);
//    }
//
//    private void resetPlayerStatus() {
//        isOnPlaying = false;
////        isOnPause = false;
//        isOnResume = false;
//        isOnSuspend = false;
//        isOnCompelte = false;
//        isOnRelased = false;
//    }
//
//
//}
