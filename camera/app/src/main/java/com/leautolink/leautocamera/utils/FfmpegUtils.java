package com.leautolink.leautocamera.utils;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

/**
 * Created by tianwei on 16/7/6.
 */
public class FfmpegUtils {

    private static final String TAG = "FfmpegUtils";
    private static FfmpegUtils mFfmpegUtils;

    private FfmpegUtils() {
    }

    /**
     * ffmpeg存放的路径（文件夹＋文件名）
     */
    private String mFfmpegBin;
    /**
     * ffmpeg命令执行的回调
     */
    private FfmpegCommandExecuteCallBack mCommandExecuteCallBack;

    public static FfmpegUtils getInstance() {
        if (mFfmpegUtils == null) {
            synchronized (FfmpegUtils.class) {
                if (mFfmpegUtils == null) {
                    mFfmpegUtils = new FfmpegUtils();
                }
            }
        }
        return mFfmpegUtils;
    }

    /**
     * 将assets目录下ffmpeg拷贝到/data/data/包名/bin下
     */
    public void copyFfmpegToBin(Context context) {
        try {
            File file = new File(context.getDir("bin", Context.MODE_PRIVATE), "ffmpeg");
            if (file.exists()) {
                file.delete();
            }
            long starttime = System.currentTimeMillis();
            String oldpath = "ffmpeg";
            AssetManager assetManager = context.getResources().getAssets();
            InputStream is = null;
            FileOutputStream fos = null;
            try {
                is = assetManager.open(oldpath);
                fos = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
                long stoptime = System.currentTimeMillis();
                Logger.e(TAG, "拷贝文件用时：" + String.valueOf(stoptime - starttime));
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            mFfmpegBin = file.getCanonicalPath();
            Logger.e(TAG, "ffmpeg 已经拷贝到：" + mFfmpegBin);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getFfmpegBin() {
        return mFfmpegBin;
    }

    /**
     * 执行ffmpeg命令
     *
     * @param commandList
     * @throws IOException
     */
    public void executeFfmpegCommand(List<String> commandList, FfmpegCommandExecuteCallBack commandExecuteCallBack) throws IOException, InterruptedException {
        mCommandExecuteCallBack = commandExecuteCallBack;
        enablePermissions();
        executeProcess(commandList);
    }

    /**
     * 使ffmpeg可执行
     *
     * @throws IOException
     */
    private void enablePermissions() throws IOException {
        Runtime.getRuntime().exec("chmod 777 " + mFfmpegBin);
    }

    /**
     * 开始执行ffmpeg命令
     *
     * @param cmds
     * @throws IOException
     * @throws InterruptedException
     */
    private void executeProcess(List<String> cmds) throws IOException, InterruptedException {
        for (String cmd : cmds) {
            cmd = String.format(Locale.US, "%s", cmd);
        }
        ProcessBuilder pb = new ProcessBuilder(cmds);
        pb.directory(new File(mFfmpegBin).getParentFile());

        StringBuffer cmdlog = new StringBuffer();
        for (String cmd : cmds) {
            cmdlog.append(cmd);
            cmdlog.append(' ');
        }
        Logger.e(TAG, "执行的ffmpeg命令：" + cmdlog.toString());
        Process process = pb.start();
        int exitVal = process.waitFor();
        mCommandExecuteCallBack.onExecuteCompleted(exitVal);
    }

    public interface FfmpegCommandExecuteCallBack {
        void onExecuteCompleted(int exitVal);
    }
}
