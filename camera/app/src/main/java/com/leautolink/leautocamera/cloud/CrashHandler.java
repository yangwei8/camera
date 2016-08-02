package com.leautolink.leautocamera.cloud;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.widget.Toast;

import com.leautolink.leautocamera.R;
import com.letvcloud.cmf.utils.FileHelper;
import com.letvcloud.cmf.utils.Logger;
import com.letvcloud.cmf.utils.NetworkUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CrashHandler implements UncaughtExceptionHandler {
    private static final String TAG = "CrashHandler";

    private static CrashHandler sSingleton = new CrashHandler();

    // 用于格式化日期,作为日志文件名的一部分
    private final DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    // 系统默认的UncaughtException处理类
    private UncaughtExceptionHandler mDefaultHandler;
    private Context mContext;
    private OnCrashExitListener mOnCrashExitListener;

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        return sSingleton;
    }

    /**
     * 初始化
     * @param context
     */
    public void init(Context context) {
        this.mContext = context;
        // 获取系统默认的UncaughtException处理器
        this.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!this.handleException(ex) && this.mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            this.mDefaultHandler.uncaughtException(thread, ex);
        } else {
            if (this.mOnCrashExitListener != null) {
                this.mOnCrashExitListener.onCrashExit();
            }

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Logger.e(TAG, "uncaughtException. " + e.toString());
            }

            // 退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
            if (LogCollector.getInstance() != null) {
                LogCollector.getInstance().cancel();
            }
            System.exit(1);
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(CrashHandler.this.mContext, R.string.application_error, Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();

        Map<String, String> infoMap = this.collectInfo(ex, this.mContext);
        // this.saveCrashInfoToFile(ex, infoMap);
        return true;
    }

    /**
     * 收集应用和设备参数信息
     * @param ctx
     * @return
     */
    public Map<String, String> collectInfo(Throwable ex, Context ctx) {
        Throwable cause = ex;
        while (cause != null) {
            cause.printStackTrace();
            cause = cause.getCause();
        }

        Map<String, String> infoMap = new HashMap<String, String>();
        try {
            PackageInfo pi = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                infoMap.put("versionName", versionName);
                infoMap.put("versionCode", versionCode);
                Logger.i(TAG, "collectInfo. packageName: %s, versionName: %s, versionCode: %s", ctx.getPackageName(), versionName, versionCode);
            }
        } catch (Exception e) {
            Logger.e(TAG, "collectInfo. " + e.toString());
        }

        String ipAddress = NetworkUtils.getIP();
        String wlanMac = NetworkUtils.getWlanMac("wlan");
        String ethMac = NetworkUtils.getEthMac("eth");
        infoMap.put("ipAddress", ipAddress);
        infoMap.put("wlanMac", wlanMac);
        infoMap.put("ethMac", ethMac);
        Logger.i(TAG, "collectInfo. ipAddress: %s, wlanMac: %s, ethMac: %s", ipAddress, wlanMac, ethMac);

        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infoMap.put(field.getName(), field.get(null).toString());
                Logger.i(TAG, "collectInfo. %s : %s", field.getName(), field.get(null));
            } catch (Exception e) {
                Logger.e(TAG, "collectInfo. " + e.toString());
            }
        }

        return infoMap;
    }

    /**
     * 保存错误信息到文件中
     * @param ex
     * @param infoMap
     * @return 返回文件名称,便于将文件传送到服务器
     */
    private String saveCrashInfoToFile(Throwable ex, Map<String, String> infoMap) {
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : infoMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace();
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        try {
            long timestamp = System.currentTimeMillis();
            String time = this.formatter.format(new Date());
            String fileName = "crash-" + time + "-" + timestamp + ".log";
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                FileOutputStream fos = new FileOutputStream(FileHelper.getSdPath(this.mContext, "cdeLogs").getAbsolutePath() + File.separator + fileName);
                fos.write(sb.toString().getBytes());
                fos.close();
            }
            return fileName;
        } catch (Exception e) {
            Logger.e(TAG, "saveCrashInfoToFile" + e.toString());
        }
        return null;
    }

    public interface OnCrashExitListener {
        void onCrashExit();
    }

    public void setOnCrashExitListener(OnCrashExitListener listener) {
        this.mOnCrashExitListener = listener;
    }
}
