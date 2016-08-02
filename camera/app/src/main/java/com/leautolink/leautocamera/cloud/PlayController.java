package com.leautolink.leautocamera.cloud;

import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.leautolink.leautocamera.config.Config;
import com.leautolink.leautocamera.config.Constant;
import com.leautolink.leautocamera.utils.SpUtils;
import com.letvcloud.cmf.MediaPlayer;
import com.letvcloud.cmf.MediaPlayer.OnBufferingUpdateListener;
import com.letvcloud.cmf.MediaPlayer.OnCompletionListener;
import com.letvcloud.cmf.MediaPlayer.OnDecoderChangedListener;
import com.letvcloud.cmf.MediaPlayer.OnErrorListener;
import com.letvcloud.cmf.MediaPlayer.OnInfoListener;
import com.letvcloud.cmf.MediaPlayer.OnLoadingStartListener;
import com.letvcloud.cmf.MediaPlayer.OnOverloadProtectedListener;
import com.letvcloud.cmf.MediaPlayer.OnPreparedListener;
import com.letvcloud.cmf.MediaPlayer.OnSeekCompleteListener;
import com.letvcloud.cmf.MediaSource;
import com.letvcloud.cmf.TextureVideoView;
import com.letvcloud.cmf.VideoView;
import com.letvcloud.cmf.utils.Logger;

import java.util.ArrayList;
import java.util.List;

public class PlayController {
    private static final String TAG = "PlayController";

    private final static int MSG_HANDLE_DELAY_CHANGE_SOURCE = 0x01;
    private final static int MSG_HANDLE_DELAY_CHANGE_DECODER = 0x02;
    private final static int DELAY_TIME_CHANGE_SOURCE = 300;
    private final static int DELAY_TIME_CHANGE_DECODER = 500;

    private final Context mContext;
    private final ViewGroup mVideoContainer;
    private final VideoHandler mVideoHandler;
    private final ViewGroup.LayoutParams mLayoutParams;
    private final List<MediaSource> mMediaSourceList = new ArrayList<MediaSource>();
    private final int mMaxVolume;

    private PlayerListener mVideoListener;
    public TextureVideoView mVideoView;
    private int mDecoderType;
    private int mDisplayMode;
    private MediaSource mMediaSource;

    private boolean mContainerHasView;
    private boolean mCurrentStartTag;


    public PlayController(Context context, ViewGroup videoContainer) {
        this(context, videoContainer, MediaPlayer.DECODER_TYPE_LEC_AUTO, VideoView.DISPLAY_MODE_MATCH_PARENT);
    }

    public PlayController(Context context, ViewGroup videoContainer, int decoderType, int displayMode) {
        if (context == null || videoContainer == null) {
            throw new IllegalArgumentException("Illegal Context or ViewGroup argument.");
        }
        this.mContext = context;
        this.mVideoContainer = videoContainer;
        this.mDecoderType = decoderType;
        this.mDisplayMode = displayMode;
        this.mVideoHandler = new VideoHandler(Looper.myLooper() == null ? Looper.getMainLooper() : Looper.myLooper(), this);
        this.mLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.mMaxVolume = ((AudioManager) this.mContext.getSystemService(Context.AUDIO_SERVICE)).getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        this.initVideoView();
    }

    private void initVideoView() {
        if (this.mVideoView != null) {
            if (this.mContainerHasView) {
                this.removeViewFromContainer();
                this.mContainerHasView = false;
            }
            this.mVideoView.stopPlayback();
        }

        this.mVideoView = TextureVideoView.create(this.mContext, this.mDecoderType, this.mDisplayMode);
//        mVideoView.useDefaultControlView(true);
        this.mVideoView.setOnLoadingStartListener(this.mLoadingStartListener);
        this.mVideoView.setOnPreparedListener(this.mPreparedListener);
        this.mVideoView.setOnCompletionListener(this.mCompletionListener);
        this.mVideoView.setOnSeekCompleteListener(this.mSeekCompleteListener);
        this.mVideoView.setOnBufferingUpdateListener(this.mBufferingUpdateListener);
        this.mVideoView.setOnInfoListener(this.mInfoListener);
        this.mVideoView.setOnErrorListener(this.mErrorListener);
        this.mVideoView.setOnDecoderChangedListener(this.mDecoderChangedListener);
        this.mVideoView.setOnOverloadProtectedListener(this.mOverloadProtectedListener);
    }

    public void setPlayerListener(PlayerListener listener) {
        this.mVideoListener = listener;
    }

    public void useDefaultControlView() {
        if (this.mVideoView != null) {
            this.mVideoView.useDefaultControlView(true);
        }
    }

    public int getDecoderType() {
        return this.mDecoderType;
    }

    public int getDisplayMode() {
        return this.mDisplayMode;
    }

    /**
     * 切换码器类型
     *
     * @param decoderType 0-LecPlayer解码自动切换; 1-LecPlayer软解; 2-SystemPlayer硬解
     */
    public void switchDecoder(int decoderType) {
        if (decoderType == this.mDecoderType) {
            Logger.i(TAG, "switchDecoder. the same decoder type, ignore.");
            return;
        }

        this.mVideoHandler.removeMessages(MSG_HANDLE_DELAY_CHANGE_DECODER);
        // this.mVideoHandler.removeMessages(MSG_HANDLE_DELAY_CHANGE_STREAM);
        Message msg = this.mVideoHandler.obtainMessage();
        msg.what = MSG_HANDLE_DELAY_CHANGE_DECODER;
        msg.arg1 = decoderType;
        this.mVideoHandler.sendMessageDelayed(msg, DELAY_TIME_CHANGE_DECODER);
    }

    public void switchDisplay(int displayMode) {
        if (displayMode == this.mDisplayMode) {
            Logger.i(TAG, "setDisplayMode. the same display mode, ignore.");
            return;
        }

        this.mVideoView.changeDisplay(displayMode);
        this.mDisplayMode = displayMode;
    }

    public void setVolume(int volume) {
        if (volume < 0 || volume > this.mMaxVolume) {
            return;
        }

        this.mVideoView.setVolume(volume, volume);
    }

    public boolean play(MediaSource source) {
        if (source == null || TextUtils.isEmpty(source.getSource())) {
            Logger.e(TAG, "Play failed, MediaSource is invalid.");
            return false;
        }

        this.reset();
        this.mMediaSource = source;
        this.mMediaSourceList.add(this.mMediaSource);

        if (this.mVideoView == null) {
            return false;
        }

        return this.renderCurrentView();
    }

    public boolean play(List<MediaSource> sourceList) {
        if (sourceList == null || sourceList.isEmpty()) {
            Logger.e(TAG, "Play failed, MediaSource list is empty.");
            return false;
        }

        this.reset();
        for (MediaSource source : sourceList) {
            if (source != null && !TextUtils.isEmpty(source.getSource())) {
                this.mMediaSourceList.add(source);
            }
        }
        if (this.mMediaSourceList.isEmpty()) {
            Logger.e(TAG, "Play failed, MediaSource list no valid item.");
            return false;
        }

        this.mMediaSource = this.mMediaSourceList.get(0);
        if (this.mVideoView == null) {
            return false;
        }

        return this.renderCurrentView();
    }

    public boolean play(int index) {
        MediaSource nextSource = this.getMediaSource(index);
        if (nextSource == null) {
            Logger.e(TAG, "Play failed, the %s index in the MediaSource list is empty.", index);
            return false;
        }

        this.mMediaSource = nextSource;
        if (this.mVideoView == null) {
            return false;
        }

        this.mVideoView.stopPlayback();
        return this.renderCurrentView();
    }

    public void reset() {
        this.mMediaSourceList.clear();
        this.mMediaSource = null;
    }

    public void stopPlayback() {
        if (this.mVideoView != null) {
            this.mVideoView.stopPlayback();
        }
    }

    public void start() {
        if (this.mVideoView != null) {
            this.mVideoView.start();
        }
    }

    public void pause() {
        if (this.mVideoView != null) {
            this.mVideoView.pause();
        }
    }

    public void suspend() {
        if (this.mVideoView != null) {
            this.mVideoView.suspend();
        }
    }

    public void resume() {
        if (this.mVideoView != null) {
            this.mVideoView.resume();
        }
    }

    public void seekTo(int msec) {
        if (this.mVideoView != null) {
            this.mVideoView.seekTo(msec);
        }
    }

    public boolean isPlaying() {
        return this.mVideoView != null ? this.mVideoView.isPlaying() : false;
    }

    public int getDuration() {
        return this.mVideoView != null ? this.mVideoView.getDuration() : 0;
    }

    public int getCurrentPosition() {
        return this.mVideoView != null ? this.mVideoView.getCurrentPosition() : 0;
    }

    public int getBufferPercentage() {
        return this.mVideoView != null ? this.mVideoView.getBufferPercentage() : 0;
    }

    public int getCachePercentage() {
        return this.mVideoView != null ? this.mVideoView.getCachePercentage() : 0;
    }

    public long getFps() {
        return this.mVideoView != null ? this.mVideoView.getFramesPerSecond() : 0;
    }

    public MediaSource getMediaSource() {
        return this.mMediaSource;
    }

    public List<MediaSource> getMediaSourceList() {
        return this.mMediaSourceList;
    }

    private void stopCurrentView(boolean removeView) {
        if (removeView && this.mContainerHasView) {
            this.removeViewFromContainer();
            this.mContainerHasView = false;
        }
        if (this.mCurrentStartTag) {
            this.mVideoView.stopPlayback();
            this.mCurrentStartTag = false;
        }
    }

    private boolean renderCurrentView() {
        if (this.mMediaSource == null) {
            Logger.e(TAG, "renderCurrentView. but the MediaSource is null.");
            return false;
        }

        // TODO
        this.mCurrentStartTag = true;
        if (!this.mContainerHasView) {
            this.mContainerHasView = true;
            this.addViewToContainer();
        }

        this.mVideoView.setVideoSource(this.mMediaSource);
        return true;
    }

    private boolean reRenderCurrentView() {
        this.stopCurrentView(true);
        return this.renderCurrentView();
    }

    private boolean renderNextView() {
        MediaSource nextSource = this.getNextMediaSource();
        if (nextSource == null) {
            return false;
        }

        this.stopCurrentView(true);
        this.mMediaSource = nextSource;
        return this.renderCurrentView();
    }

    protected MediaSource getNextMediaSource() {
        if (this.mMediaSourceList.isEmpty()) {
            return null;
        }

        int index = this.getCurrentItemIndex();
        if (index < 0) {
            return this.mMediaSourceList.get(0);
        }

        index++;
        return index < this.mMediaSourceList.size() ? this.mMediaSourceList.get(index) : null;
    }

    protected MediaSource getPreMediaSource() {
        if (this.mMediaSourceList.isEmpty()) {
            return null;
        }

        int index = this.getCurrentItemIndex();
        if (index < 0) {
            return this.mMediaSourceList.get(0);
        }

        index--;
        return index >= 0 ? this.mMediaSourceList.get(index) : null;
    }

    private int getCurrentItemIndex() {
        return this.mMediaSourceList.indexOf(this.mMediaSource);
    }

    private MediaSource getMediaSource(int index) {
        if (this.mMediaSourceList.isEmpty()) {
            return null;
        }

        if (index < 0 || index >= this.mMediaSourceList.size()) {
            return null;
        }

        return this.mMediaSourceList.get(index);
    }

    private void addViewToContainer() {
        ViewGroup parentView = (ViewGroup) this.mVideoView.getParent();
        if (parentView != null) {
            parentView.removeView(this.mVideoView);
            parentView = null;
        }

        if (this.mVideoContainer instanceof RelativeLayout) {
            RelativeLayout videoContainer = (RelativeLayout) this.mVideoContainer;
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            videoContainer.addView(this.mVideoView, layoutParams);
        } else {
            this.mVideoContainer.addView(this.mVideoView, this.mLayoutParams);
        }
    }

    public void removeViewFromContainer() {
        this.mVideoContainer.removeView(this.mVideoView);
    }

    private final OnLoadingStartListener mLoadingStartListener = new OnLoadingStartListener() {

        @Override
        public void onLoadingStart(MediaPlayer mp) {
            if (PlayController.this.mVideoListener != null) {
                if (Constant.isLive) {//Live
                    int liveDelay = SpUtils.getInt(mContext, "livedelay", 1000);
//                    int liveDelay =1000;
                    Logger.e(TAG, "live----liveDelay:" + liveDelay);
                    //设置最大延迟
                    mp.setMaxDelayTime(liveDelay);
                    //设置最大缓存
//                    mp.setCacheMaxSize(3000);
                    //设置优先使用tcp
                    mp.setRtspPreferTcp();
                    //设置预缓冲大小
                    mp.setCachePreSize(liveDelay - 200);
                    //设置高低水位
                    mp.setCacheWatermark(liveDelay + 500, ((liveDelay / 2) - 800) < 0 ? 0 : ((liveDelay / 2) - 800));
                    //设置声音
                    mp.setVolume(0, 0);
                } else {//PlayBack
                    Logger.e(TAG, "playback ：");
//                    mp.setCacheMaxSize(1000000);
//                        mp.setMaxDelayTime(sp.getInt("delayms",2000));
                    if (Constant.isGetPics) {
                        com.leautolink.leautocamera.utils.Logger.e("VideoEditActivity", "isGetPics: " + Constant.isGetPics);
                        mp.setParameter(2, 2, 0);
                        mp.setCacheMaxSize(500);
                        mp.setCacheWatermark(300, 0);
                    } else {
                        mp.setCachePreSize(2000);
                    }
//                    mp.setCacheWatermark(200, 500);
//                    mp.setRtspPreferTcp();
                }
                PlayController.this.mVideoListener.onLoadingStart(mp);
            }

        }
    };

    private final OnPreparedListener mPreparedListener = new OnPreparedListener() {

        @Override
        public void onPrepared(MediaPlayer mp) {
            mp.start();
            if (PlayController.this.mVideoListener != null) {
                PlayController.this.mVideoListener.onPrepared(mp);
            }
        }
    };

    private final OnCompletionListener mCompletionListener = new OnCompletionListener() {

        @Override
        public void onCompletion(MediaPlayer mp) {
            if (PlayController.this.mVideoListener != null) {
                if (PlayController.this.mVideoListener.onCompletion()) {
                    PlayController.this.reRenderCurrentView();
                }
            }
            PlayController.this.renderNextView();
        }
    };

    private final OnSeekCompleteListener mSeekCompleteListener = new OnSeekCompleteListener() {

        @Override
        public void onSeekComplete(MediaPlayer mp) {
//            mp.start();
            if (PlayController.this.mVideoListener != null) {
                PlayController.this.mVideoListener.onSeekComplete();
            }
        }
    };

    private final OnBufferingUpdateListener mBufferingUpdateListener = new OnBufferingUpdateListener() {

        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            if (PlayController.this.mVideoListener != null) {
                PlayController.this.mVideoListener.onBufferingUpdate(percent);
            }
        }
    };

    private final OnInfoListener mInfoListener = new OnInfoListener() {

        @Override
        public boolean onInfo(MediaPlayer mp, int what, long extra) {
            if (PlayController.this.mVideoListener != null) {

                if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                    PlayController.this.mVideoListener.onFirstPic();
                }

                PlayController.this.mVideoListener.onInfo(what, extra);
            }
            return true;
        }
    };

    private final OnErrorListener mErrorListener = new OnErrorListener() {

        @Override
        public boolean onError(MediaPlayer mp, int what, String extra) {
            if (PlayController.this.mVideoListener != null) {
                if (PlayController.this.mVideoListener.onError(what, extra)) {
                    return true;
                }
            }
            return false;
        }
    };

    private final OnDecoderChangedListener mDecoderChangedListener = new OnDecoderChangedListener() {

        @Override
        public void onDecoderChanged(int decoderType) {
            Logger.i(TAG, "onDecoderChanged. decoder type(%s)", decoderType);
        }
    };

    private final OnOverloadProtectedListener mOverloadProtectedListener = new OnOverloadProtectedListener() {

        @Override
        public boolean onOverloadProtected(int errorCode) {
            Logger.i(TAG, "onOverloadProtected. error code(%s)", errorCode);
            // 对CDN过载保护返回的errorCode进行判断处理...
            return true;// 返回true表示继续播放, 返回false表示不播放。
        }
    };

    private class VideoHandler extends Handler {
        private final PlayController mVideoPlayer;

        public VideoHandler(Looper looper, PlayController videoPlayer) {
            super(looper);
            this.mVideoPlayer = videoPlayer;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_HANDLE_DELAY_CHANGE_SOURCE:
                    Logger.d(TAG, "handleMessage. delay to change source.");
                    break;
                case MSG_HANDLE_DELAY_CHANGE_DECODER:
                    Logger.d(TAG, "handleMessage. delay to change decoder.");
                    this.mVideoPlayer.mDecoderType = msg.arg1;
                    this.mVideoPlayer.initVideoView();
                    this.mVideoPlayer.renderCurrentView();
                    break;
                default:
                    break;
            }
        }
    }

    public interface PlayerListener {
        void onLoadingStart(MediaPlayer mp);

        void onPrepared(MediaPlayer mp);

        void onFirstPic();

        void onBufferingUpdate(int percent);

        void onSeekComplete();

        boolean onCompletion();

        void onInfo(int what, long extra);

        boolean onError(int what, String extra);
    }
}
