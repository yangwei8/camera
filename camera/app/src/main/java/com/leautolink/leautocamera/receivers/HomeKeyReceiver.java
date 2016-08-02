package com.leautolink.leautocamera.receivers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.leautolink.leautocamera.config.Constant;
import com.leautolink.leautocamera.utils.Logger;
import com.leautolink.leautocamera.utils.SpUtils;
import com.leautolink.leautocamera.utils.WifiAdminV2;

/**
 * Created by donghongwei on 16-4-13.
 * HOME按键广播监听
 */
public class HomeKeyReceiver extends BroadcastReceiver {
    private static final String SYSTEM_DIALOG_REASON_KEY = "reason";
    private static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

    private Activity mActivity;

    public HomeKeyReceiver(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Logger.e("HomeKeyReceiver action=" + action);
        if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
        String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
        Logger.e("HomeKeyReceiver reason=" + reason);
        if (!TextUtils.isEmpty(reason) && reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
//                ConnectedHelper.exitApp(mActivity);


            Logger.e("HomeKeyReceiver  被调用了" );

            WifiAdminV2 admin = new WifiAdminV2(mActivity);
            String ssid = SpUtils.getInstance(mActivity).getStringValue(Constant.WIFI_SSID);
            if (!TextUtils.isEmpty(ssid)) {
                admin.forget(ssid);
            }

        }
    }
}
}
