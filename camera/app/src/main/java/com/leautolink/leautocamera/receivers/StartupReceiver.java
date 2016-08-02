package com.leautolink.leautocamera.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.leautolink.leautocamera.services.CheckUpdateService;

/**
 * Created by liushengli on 2016/4/3.
 */
public class StartupReceiver extends BroadcastReceiver {
    private static final String TAG = "StartupReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Received broadcast intent: " + intent.getAction());
        context.startService(new Intent(context, CheckUpdateService.class));
    }
}
