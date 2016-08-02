package com.leautolink.leautocamera.utils.mediaplayermanager.imls;

import android.content.Context;
import android.view.ViewGroup;

import com.leautolink.leautocamera.cloud.GeneralUtils;
import com.leautolink.leautocamera.cloud.LogCollector;
import com.leautolink.leautocamera.cloud.PlayController;
import com.leautolink.leautocamera.config.Constant;
import com.leautolink.leautocamera.ui.view.scaleview.ScaleCalculator;
import com.leautolink.leautocamera.utils.mediaplayermanager.MedIaPlayerManager;
import com.leautolink.leautocamera.utils.mediaplayermanager.interfaces.IPlayer;
import com.leautolink.leautocamera.utils.mediaplayermanager.interfaces.IPlayerListener;
import com.letvcloud.cmf.CmfHelper;
import com.letvcloud.cmf.MediaPlayer;
import com.letvcloud.cmf.MediaSource;
import com.letvcloud.cmf.TextureVideoView;
import com.letvcloud.cmf.utils.DeviceUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tianwei on 16/6/15.
 */
public class CloudPlayer implements IPlayer, PlayController.PlayerListener {
    private Context mContext;
    private PlayController mPlayController;
    private MediaPlayer mMp;
    private IPlayerListener mPlayListener;
    private List<MediaSource> sourceList;
    /**
     * 直播 MedIaPlayerManager.PLAY_TYPE_LIVE 、 录播 MedIaPlayerManager.PLAY_TYPE_VOD
     */
    private int playType;


    private boolean isOnSuspend;
    private boolean isOnPause;
    private boolean isOnResume;
    private boolean isOnComplete;
    private boolean isOnRelease;

    @Override
    public void init(Context context) {
        mContext = context;
        boolean isMainProcess = mContext.getPackageName().equals(DeviceUtils.getProcessName(mContext, android.os.Process.myPid()));
        if (isMainProcess) {
            // 必要的初始化操作
//            CrashHandler crashHandler = CrashHandler.getInstance();
//            crashHandler.setOnCrashExitListener(new CrashHandler.OnCrashExitListener() {
//
//                @Override
//                public void onCrashExit() {
//                    CmfHelper.exit();
//                }
//            });
//            crashHandler.init(this.getApplicationContext());
            LogCollector.getInstance(mContext).launch();
            ScaleCalculator.init(mContext);
            // 初始化, 成功后进行后面动作
            CmfHelper.init(mContext, GeneralUtils.getInitPlayerParams());
            CmfHelper.getInstance().setOnStartStatusChangeListener(new CmfHelper.OnStartStatusChangeListener() {
                @Override
                public void onLinkShellStartComplete(int i) {

                }

                @Override
                public void onCdeStartComplete(int i) {

                }

                @Override
                public void onMediaServiceDisconnected() {

                }
            });
            CmfHelper.getInstance().start();
        }
    }

    @Override
    public void initPlayer(Context context, ViewGroup videoLayout, int playType) {
        resetPlayerStatus();
        mContext = context;
        this.playType = playType;
        if (mPlayController == null) {
            mPlayController = new PlayController(mContext, videoLayout);
            mPlayController.setPlayerListener(this);
        }
    }

    @Override
    public void play(Context context, ViewGroup videoLayout, String url, int playType, int encodeType) {
        resetPlayerStatus();
        mContext = context;
        if (mPlayController == null) {
            mPlayController = new PlayController(mContext, videoLayout);
            mPlayController.setPlayerListener(this);

        }

        if (sourceList == null) {
            sourceList = new ArrayList<MediaSource>();
            MediaSource source = new MediaSource();
            // 设置播放源,必须设置
            source.setSource(url);
            // 设置媒体类型,必须设置,默认为点播
            if (playType == MedIaPlayerManager.PLAY_TYPE_LIVE) {
                Constant.isLive = true;
                source.setType(MediaSource.TYPE_LIVE);
            } else if (playType == MedIaPlayerManager.PLAY_TYPE_VOD) {
                Constant.isLive = false;
                source.setType(MediaSource.TYPE_VOD);
            }

            // 设置媒体名称,可以不设置
//        source.setName(channel.getName() + (walkCde ? "_CDE" : "_RAW"));
            // 设置是否加密(true则要经过LinkShell, false则反之),设置是否中转(true则要通过CDE, false则反之)
            source.setEncrypt(false).setTransfer(false);
            sourceList.add(source);
        }
        mPlayController.play(sourceList);
    }

    @Override
    public void setVolum(int volum) {
        if (mPlayController != null) {
            mPlayController.setVolume(volum);
        }
    }

    public void play(String url) {
        MediaSource source = new MediaSource();
        // 设置播放源,必须设置
        source.setSource(url);
        // 设置媒体类型,必须设置,默认为点播
        if (playType == MedIaPlayerManager.PLAY_TYPE_LIVE) {
            source.setType(MediaSource.TYPE_LIVE);
        } else if (playType == MedIaPlayerManager.PLAY_TYPE_VOD) {
            source.setType(MediaSource.TYPE_VOD);
        }

        // 设置媒体名称,可以不设置
//        source.setName(channel.getName() + (walkCde ? "_CDE" : "_RAW"));
        // 设置是否加密(true则要经过LinkShell, false则反之),设置是否中转(true则要通过CDE, false则反之)
        source.setEncrypt(false).setTransfer(false);
        mPlayController.play(source);
    }

    @Override
    public void start() {
        if (mPlayController != null)
            mPlayController.start();
    }

    @Override
    public void setPlayerListener(IPlayerListener playerListener) {
        mPlayListener = playerListener;
    }


    @Override
    public void playAfterPause() {
        if (mPlayController != null) {
            resetPlayerStatus();
            mPlayController.start();
        }
    }

    @Override
    public void pause() {
        if (mPlayController != null) {
            resetPlayerStatus();
            isOnPause = true;
            mPlayController.pause();
        }
    }

    @Override
    public void suspend() {
        if (mPlayController != null) {
            resetPlayerStatus();
            isOnSuspend = true;
            mPlayController.suspend();
        }
    }


    @Override
    public void resume() {
        if (mPlayController != null) {
            resetPlayerStatus();
            isOnResume = true;
            mPlayController.resume();
        }
    }

    @Override
    public void stop() {
        if (mPlayController != null) {
            resetPlayerStatus();
            mPlayController.stopPlayback();
        }
    }

    @Override
    public void release() {
        if (mMp != null) {
            mMp.release();
            mMp = null;
        }
        if (sourceList != null) {
            sourceList.clear();
            sourceList = null;
        }
        if (mPlayController != null) {
            mPlayController.removeViewFromContainer();
            resetPlayerStatus();
            isOnRelease = true;
            mPlayController = null;
        }
    }

    @Override
    public int getDuration() {
        int duration = 0;
        if (mMp != null) {
            return mMp.getDuration();
        }
        return duration;
    }

    @Override
    public int getCurrentPosition() {
        int currentPosition = 0;
        if (mPlayController != null) {
            currentPosition = mPlayController.getCurrentPosition();
        }
        return currentPosition;
    }

    @Override
    public TextureVideoView getTextureView() {
        if (mPlayController != null) {
            return mPlayController.mVideoView;
        }
        return null;
    }

    @Override
    public void seekTo(int position) {
        if (mPlayController != null) {
            mPlayController.seekTo(position);
        }
    }

    @Override
    public boolean isPlaying() {
        if (mPlayController != null) {
            return mPlayController.isPlaying();
        }
        return false;
    }

    @Override
    public boolean isPause() {
        return isOnPause;
    }

    @Override
    public boolean isSuspend() {
        return isOnSuspend;
    }

    @Override
    public boolean isResume() {
        return isOnResume;
    }

    @Override
    public boolean isComplete() {
        return isOnComplete;
    }

    @Override
    public boolean isReleased() {
        return isOnRelease;
    }

    @Override
    public void onLoadingStart(MediaPlayer mp) {
        mMp = mp;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mMp = mp;
        if (mPlayController != null) {
            mPlayController.start();
        }
        if (mPlayListener != null) {
            mPlayListener.onPrepared();
        }
    }

    @Override
    public void onFirstPic() {
        if (mPlayListener != null) {
            mPlayListener.onFirstPic();
        }
    }


    @Override
    public void onBufferingUpdate(int percent) {
        if (mPlayListener != null)
            mPlayListener.onBufferingUpdate(percent);
    }

    @Override
    public void onSeekComplete() {
        if (mPlayListener != null)
            mPlayListener.onSeekComplete();
    }

    @Override
    public boolean onCompletion() {
        if (mPlayListener != null) {
            resetPlayerStatus();
            isOnComplete = true;
            mPlayListener.onComplete();
        }
        return false;
    }

    @Override
    public void onInfo(int what, long extra) {
        if (mPlayListener != null) {
            mPlayListener.onInfo(what, extra);
        }
    }

    @Override
    public boolean onError(int what, String extra) {
        if (mPlayListener != null) {
            resetPlayerStatus();
            mPlayListener.onError(what, extra);
        }
        return true;
    }

    private void resetPlayerStatus() {
        isOnPause = false;
        isOnResume = false;
        isOnSuspend = false;
        isOnComplete = false;
        isOnRelease = false;
    }
}
