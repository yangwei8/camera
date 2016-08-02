package com.leautolink.leautocamera.cloud;

import android.content.Context;

import com.letvcloud.cmf.utils.FileHelper;
import com.letvcloud.cmf.utils.Logger;
import com.letvcloud.cmf.utils.StringUtils;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class LogCollector extends Thread {

    private static final String TAG = "LogCollector";

    private static LogCollector sSingleton;

    private final String mLogFileRootDir;
    private final long mLogFileSize;
    private final int mLogFileNum;
    private final DecimalFormat mDecimalFormater;

    private Process mLogProcess;
    private boolean mIsStart;
    private final String mLogFilePath;

    private LogCollector(String logFileRootDir, float logFileSize, int logFileNum) {
        File logFile = new File(logFileRootDir);
        if (!logFile.exists()) {
            logFile.mkdirs();
        }
        if (logFileSize <= 0) {
            logFileSize = 5;
        }
        if (logFileNum <= 0) {
            logFileNum = 30;
        }
        this.mLogFileRootDir = logFile.getAbsolutePath() + File.separator;
        this.mLogFilePath = this.mLogFileRootDir + "Log.0" + File.separator + "logcat.log";
        this.mLogFileSize = (long) (logFileSize * 1024);
        this.mLogFileNum = logFileNum;
        this.mDecimalFormater = new DecimalFormat("##.##");
    }

    /**
     * 日志文件默认保存在/sdcard/Android/packageName/files/cdeLogs/
     * @param context
     * @return
     */
    public static LogCollector getInstance(Context context) {
        if (context == null) {
            throw new IllegalArgumentException();
        }

        if (sSingleton == null) {
            synchronized (LogCollector.class) {
                if (sSingleton == null) {
                    sSingleton = new LogCollector(FileHelper.getSdPath(context, "cdeLogs").getAbsolutePath(), 10, 9);
                }
            }
        }
        return sSingleton;
    }

    /**
     * @param logFileRootDir
     *            日志文件根目录
     * @param logFileSize
     *            日志文件大小(单位M)
     * @param logFileNum
     *            日志文件数量
     * @return
     */
    public static LogCollector getInstance(String logFileRootDir, float logFileSize, int logFileNum) {
        if (StringUtils.isEmpty(logFileRootDir)) {
            throw new IllegalArgumentException();
        }

        if (sSingleton == null) {
            synchronized (LogCollector.class) {
                if (sSingleton == null) {
                    sSingleton = new LogCollector(logFileRootDir, logFileSize, logFileNum);
                }
            }
        }
        return sSingleton;
    }

    public static LogCollector getInstance() {
        return sSingleton;
    }

    @Override
    public void run() {
        this.initLogFileDir();
        this.startLogCollect();
    }

    private void initLogFileDir() {
        File log0File = new File(this.mLogFileRootDir + "Log.0");
        if (!log0File.exists()) {
            log0File.mkdirs();
            return;
        }

        File log1File = new File(this.mLogFileRootDir + "Log.1");
        if (!log1File.exists()) {
            FileHelper.renameFile(log0File, log1File);
            log0File.mkdirs();
            return;
        }

        File log2File = new File(this.mLogFileRootDir + "Log.2");
        if (log2File.exists()) {
            FileHelper.deleteFileOrDir(log2File, false);
        }
        FileHelper.renameFile(log1File, log2File);
        FileHelper.renameFile(log0File, log1File);
        log0File.mkdirs();
    }

    private void startLogCollect() {
        List<String> commandList = new ArrayList<String>();
        commandList.add("logcat");
        commandList.add("-v");
        commandList.add("threadtime");
        commandList.add("-f");
        commandList.add(this.mLogFilePath);
        commandList.add("-r");
        commandList.add(String.valueOf(this.mLogFileSize));
        commandList.add("-n");
        commandList.add(String.valueOf(this.mLogFileNum));
        try {
            this.mLogProcess = Runtime.getRuntime().exec(commandList.toArray(new String[commandList.size()]));
            Logger.i(TAG, "startLogCollect. each file size: %s, file max number: %s, file root dir: %s",
                    StringUtils.formatSize(this.mDecimalFormater, this.mLogFileSize * 1024), this.mLogFileNum + 1, this.mLogFileRootDir);
        } catch (Exception e) {
            Logger.e(TAG, "startLogCollect. " + e.toString());
        }
    }

    public void launch() {
        if (this.mIsStart) {
            return;
        }

        Logger.i(TAG, "launch.");
        this.mIsStart = true;
        this.start();
    }

    public void cancel() {
        if (!this.mIsStart) {
            return;
        }

        Logger.i(TAG, "cancel.");
        this.mIsStart = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                LogCollector.this.clearLogCache();
            }
        }).start();
    }

    public String getLogDir() {
        return this.mLogFileRootDir;
    }

    private void clearLogCache() {
        Process proc = null;
        List<String> commandList = new ArrayList<String>();
        commandList.add("logcat");
        commandList.add("-c");
        try {
            Thread.sleep(300);
            if (null != this.mLogProcess) {
                this.mLogProcess.destroy();
                this.mLogProcess = null;
            }

            proc = Runtime.getRuntime().exec(commandList.toArray(new String[commandList.size()]));
            Logger.i(TAG, "clearLogCache.");
        } catch (Exception e) {
            Logger.e(TAG, "clearLogCache. " + e.toString());
        } finally {
            try {
                if (proc != null) {
                    Thread.sleep(200);
                    proc.destroy();
                }
            } catch (Exception e) {
                Logger.e(TAG, "clearLogCache. " + e.toString());
            }
        }
    }

}
