package com.leautolink.leautocamera.utils.mediaplayermanager.interfaces;


/**
 * Created by tianwei on 16/6/16.
 */
public interface IPlayerListener {

    void onPrepared();

    void onFirstPic();

    void onBufferingStart();

    void onBufferingUpdate(int percent);

    void onBufferingEnd();

    void onComplete();

    void onError(int what, String extra);

    void onInfo(int what, long extra);

    void onBlock();

    void onSeekComplete();
}
