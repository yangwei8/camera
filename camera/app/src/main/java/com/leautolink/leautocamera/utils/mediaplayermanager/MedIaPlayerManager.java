package com.leautolink.leautocamera.utils.mediaplayermanager;

import android.content.Context;

import com.leautolink.leautocamera.utils.mediaplayermanager.imls.CloudPlayer;
import com.leautolink.leautocamera.utils.mediaplayermanager.interfaces.IPlayer;

/**
 * Created by tianwei on 16/6/18.
 */
public class MedIaPlayerManager {

    private static int mType;
    public static final int PLAYER_TYPE_CLOUD = 0;
    public static final int PLAYER_TYPE_LEHIGH = 1;

    public static final int PLAY_TYPE_VOD = 2;
    public static final int PLAY_TYPE_LIVE = 3;
    public static final int ENCODE_TYPE_SOFT = 4;
    public static final int ENCODE_TYPE_HARD = 5;



    public static void init(Context context, int type) {
        mType = type;
        createPlayer().init(context);
    }


    public static IPlayer createPlayer() {
        IPlayer iPlayer = null;
        switch (mType) {
            case PLAYER_TYPE_CLOUD:
                iPlayer = createCloudPlayer();
                break;
            case PLAYER_TYPE_LEHIGH:
                iPlayer = createLeHighPlayer();
                break;
        }
        return iPlayer;
    }

    private static IPlayer createLeHighPlayer() {
//        return new LeHighPlayer();
        return null;
    }

    private static IPlayer createCloudPlayer() {
        return new CloudPlayer();
    }
}
