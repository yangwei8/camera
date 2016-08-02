package com.leautolink.leautocamera.utils;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by tianwei1 on 2016/3/10.
 */
public class ToastUtils {
    public static final int SHORT = 0;
    public static final int LONG = 1;


    public static void showToast(final Activity activity, final String text, final int duration) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, text, duration).show();
            }
        });
    }
}
