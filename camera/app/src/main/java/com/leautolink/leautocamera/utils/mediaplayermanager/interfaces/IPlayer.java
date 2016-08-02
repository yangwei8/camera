package com.leautolink.leautocamera.utils.mediaplayermanager.interfaces;

import android.content.Context;
import android.view.TextureView;
import android.view.ViewGroup;

/**
 * Created by tianwei on 16/6/15.
 */
public interface IPlayer {
    void init(Context context);

    void initPlayer(Context context, ViewGroup videoLayout, int playType);

    void play(Context context, ViewGroup videoLayout, String url, int playType, int encodeType);

    void setVolum(int volum);

    void play(String url);
    void start();

    void setPlayerListener(IPlayerListener playerListener);

//    void setSurfaceTextureListener();

    void playAfterPause();

    void pause();

    void suspend();

    void resume();

    void stop();


    void release();

    int getDuration();

    int getCurrentPosition();

    TextureView getTextureView();

    void seekTo(int position);

    boolean isPlaying();

    boolean isPause();

    boolean isSuspend();

    boolean isResume();

    boolean isComplete();

    boolean isReleased();
}
