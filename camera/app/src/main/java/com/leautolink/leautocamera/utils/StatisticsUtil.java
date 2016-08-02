package com.leautolink.leautocamera.utils;

import android.content.Context;

import com.leautolink.leautocamera.R;
import com.letv.tracker.msg.bean.Version;
import com.letv.tracker2.agnes.Agnes;
import com.letv.tracker2.agnes.App;
import com.letv.tracker2.agnes.Event;
import com.letv.tracker2.enums.AppType;
import com.letv.tracker2.enums.EventType;

import org.androidannotations.api.BackgroundExecutor;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liushengli on 2016/4/7.
 */
public class StatisticsUtil {
    private static StatisticsUtil mSelf;
    private static Map<String, String> page_uuid = new HashMap<>();
    private static final String APP_NAME = "LeDashCam-Android";
    private Context mContext;
    private int mMajor = -1;
    private int mMinor = -1;
    private int mPatch = -1;

    public static StatisticsUtil getInstance() {
        if (mSelf == null) {
            mSelf = new StatisticsUtil();
        }
        return mSelf;
    }

    private App getApp() {
        App app = null;
        if (AppType.isExsited(APP_NAME)) {
            app = Agnes.getInstance().getApp(AppType.valueOf(APP_NAME));
        } else {
            app = Agnes.getInstance().getApp(APP_NAME);
        }
        return app;
    }

    public void init(Context ctx) {
        mContext = ctx;
        Agnes agnes = Agnes.getInstance();
        //agnes.getConfig( ).enableLog();
        agnes.setContext(ctx);
    }

    public int getMajorVer() {
        if (mMajor >= 0) {
            return mMajor;
        }
        try {
            String versionName = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
            String[] digits = versionName.split("\\.");
            if (digits.length >= 1) {
                mMajor = Integer.parseInt(digits[0]);
            } else {
                mMajor = 0;
            }
        } catch (Exception e) {
            mMajor = 0;
            e.printStackTrace();
        }
        return mMajor;
    }

    public int getMinorVer() {
        if (mMinor >= 0) {
            return mMinor;
        }
        try {
            String versionName = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
            String[] digits = versionName.split("\\.");
            if (digits.length >= 2) {
                mMinor = Integer.parseInt(digits[1]);
            } else {
                mMinor = 0;
            }
        } catch (Exception e) {
            mMinor = 0;
            e.printStackTrace();
        }
        return mMinor;
    }

    public int getPatchVer() {
        if (mPatch >= 0) {
            return mPatch;
        }
        try {
            String versionName = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
            String[] digits = versionName.split("\\.");
            if (digits.length >= 3) {
                mPatch = Integer.parseInt(digits[2]);
            } else {
                mPatch = 0;
            }
        } catch (Exception e) {
            mPatch = 0;
            e.printStackTrace();
        }
        return mPatch;
    }

    private void setAppVersion(App app) {
        Version appVersion = app.getVersion();
        appVersion.setVersion(this.getMajorVer(), this.getMinorVer(), this.getPatchVer());
    }

    public void recordAppStart() {
        BackgroundExecutor.execute(new BackgroundExecutor.Task("", 0, "") {
                                       @Override
                                       public void execute() {
                                           try {
                                               App app = getApp();
                                               setAppVersion(app);
                                               app.run();
                                               Agnes.getInstance().report(app);
                                           } catch (Throwable e) {
                                               e.printStackTrace();
                                           }
                                       }
                                   }
        );
    }

    public static Long getCurrantTime() {
        Calendar c = Calendar.getInstance();
        Long currantTime = c.getTimeInMillis();
        return currantTime;
    }

    public void recordActivityStart(final String activityId) {
        BackgroundExecutor.execute(new BackgroundExecutor.Task("", 0, "") {
                                       @Override
                                       public void execute() {
                                           try {
                                               App app = getApp();
                                               setAppVersion(app);
                                               Event e = app.createEvent(EventType.acStart);
                                               Long currantTime = getCurrantTime();
                                               String page_uuid_str = activityId + "_" + currantTime;
                                               page_uuid.put(activityId, page_uuid_str);
                                               e.addProp("activityId", activityId);
                                               e.addProp("page_uuid", page_uuid_str);
                                               Agnes.getInstance().report(e);
                                           } catch (Throwable e) {
                                               e.printStackTrace();
                                           }
                                       }
                                   }
        );
    }

    public void recordActivityEnd(final String activityId) {
        BackgroundExecutor.execute(new BackgroundExecutor.Task("", 0, "") {
                                       @Override
                                       public void execute() {
                                           try {
                                               App app = getApp();
                                               setAppVersion(app);
                                               Event e = app.createEvent(EventType.acEnd);
                                               e.addProp("activityId", activityId);
                                               if (page_uuid.get(activityId) != null) {
                                                   e.addProp("page_uuid", page_uuid.get(activityId));
                                                   Agnes.getInstance().report(e);
                                                   page_uuid.remove(activityId);
                                               }
                                           } catch (Throwable e) {
                                               e.printStackTrace();
                                           }
                                       }
                                   }
        );
    }

    public void recordConnectDevice(final String deviceName, final String model, final String versionCode, final String snId,
                                    final String wifiMac, final String btMac, final String hwCode) {
        BackgroundExecutor.execute(new BackgroundExecutor.Task("", 0, "") {
                                       @Override
                                       public void execute() {
                                           try {
                                               App app = getApp();
                                               setAppVersion(app);
                                               Event e = app.createEvent(EventType.Connect);
                                               e.addProp("DeviceName", mContext.getResources().getString(R.string.app_name));
                                               e.addProp("Model", model);
                                               e.addProp("VersionCode", versionCode);
                                               e.addProp("SNid", snId);
                                               e.addProp("WiFiMac", wifiMac.replaceAll(":", ""));
                                               e.addProp("BTMac", btMac.replaceAll(":", ""));
                                               e.addProp("HardwareCode", hwCode);
                                               Agnes.getInstance().report(e);
                                           } catch (Throwable e) {
                                               e.printStackTrace();
                                           }
                                       }
                                   }
        );
    }

}
