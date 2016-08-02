package com.leautolink.leautocamera.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.leautolink.leautocamera.upgrade.UpgradeAbility;
import com.leautolink.leautocamera.utils.FirmwareUtil;

/**
 * Created by liushengli on 2016/4/3.
 */
public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";
    private static final int POLL_INTERVAL = 1000 * 60 * 5 ;
    public static final String PREF_IS_ALARM_ON = "isAlarmOn";
    @Override
    public void onReceive(Context context, Intent intent) {
        //Log.i(TAG, "onReceive: " + intent.getAction());
        FirmwareUtil.checkOtaUpdateInterval(context);
        new UpgradeAbility(context).checkAppUpdateInterval();
    }

    public static void setAlarm(Context context, boolean isOn){
        Intent i = new Intent(context, AlarmReceiver.class);
        i.setAction("com.le.action.ALARM_CHECK");
        PendingIntent pi = PendingIntent.getBroadcast(
                context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)
                context.getSystemService(Context.ALARM_SERVICE);
        if (isOn) {
            alarmManager.setRepeating(AlarmManager.RTC,
                    System.currentTimeMillis(), POLL_INTERVAL, pi);
        } else {
            alarmManager.cancel(pi);
            pi.cancel();
        }
    }
}