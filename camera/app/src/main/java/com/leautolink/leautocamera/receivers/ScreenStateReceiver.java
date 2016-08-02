package com.leautolink.leautocamera.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.leautolink.leautocamera.event.ScreenStateEvent;
import com.leautolink.leautocamera.utils.Logger;

import de.greenrobot.event.EventBus;

/**
 * Created by tianwei on 16/4/7.
 */
public class ScreenStateReceiver extends BroadcastReceiver {

    private static final String TAG = "ScreenStateReceiver";


    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_SCREEN_OFF == intent.getAction()) {
            Logger.e(TAG, "SCREEN_OFF");
            EventBus.getDefault().post(new ScreenStateEvent(false));
        } else if (Intent.ACTION_SCREEN_ON == intent.getAction()) {
            Logger.e(TAG, "SCREEN_ON");
            EventBus.getDefault().post(new ScreenStateEvent(true));
        }
    }
}
