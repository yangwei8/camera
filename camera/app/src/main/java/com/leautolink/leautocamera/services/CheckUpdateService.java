package com.leautolink.leautocamera.services;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.leautolink.leautocamera.receivers.AlarmReceiver;

/**
 * Created by liushengli on 2016/4/3.
 */
public class CheckUpdateService extends Service {

    String tag="CheckUpdateService";
    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(tag, "Service created...");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Toast.makeText(this, "Service destroyed...", Toast.LENGTH_LONG).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(tag, "onStartCommand...");
        AlarmReceiver.setAlarm(this, true);
        return START_STICKY;
    }
}