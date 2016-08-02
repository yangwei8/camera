package com.leautolink.leautocamera.upgrade;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpStatus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.leautolink.leautocamera.net.http.CacheUtils;
import com.leautolink.leautocamera.utils.DownloadUtils;
import com.leautolink.leautocamera.utils.SdCardUtils;

public class DownLoadApk {
	
	private UpdateData updateData;

	private Handler handler;
	private Context context;
	private File apkFile;
	private boolean stopDownload;

	private final static int CONNECTION_TIMEOUT = 30000;
	private final static int READ_TIMEOUT = CONNECTION_TIMEOUT;
	private String apkName ;
	public static final int UPDATE_NOT = 0;
	public static final int UPDATE_OPTIONAL = 1;
	public static final int UPDATE_FORCE = 2;
	private boolean mBackground;
	public DownLoadApk(Context context, Handler handler,boolean background){
		this.context = context;
		this.handler = handler;
		mBackground = background;
	}
	
	public void downloadApk() {
		new DownloadApkThread().start();
	}
	
	public class DownloadApkThread extends Thread {
		@Override
		public void run() {
			File file = null;
			Log.v("csl", "Environment.getExternalStorageState() " + Environment.getExternalStorageState());
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				String path = SdCardUtils.getSDCardRootPath(context) + "/";
				file = new File(path);
				if (!file.exists()) {
					file.mkdir();
				}
				apkName = DownloadUtils.getDownLoadApkName(updateData.getVersion());
			}else{
                if(!mBackground)
				    handler.sendEmptyMessage(UpgradeAbility.UPDATE_FAIL_CLIENT);
				return;
			}
			HttpURLConnection connection = null;
			try {
				URL url = new URL(updateData.getUrl());
				connection = (HttpURLConnection) url.openConnection();
				connection.setConnectTimeout(CONNECTION_TIMEOUT);
				connection.setReadTimeout(READ_TIMEOUT);
				InputStream is = connection.getInputStream();
				if (connection.getResponseCode() ==  HttpStatus.SC_OK){
					int length = connection.getContentLength();
                    if(!mBackground)
					    handler.sendMessage(handler.obtainMessage(UpgradeAbility.PROGRESS_SET_MAX, 0, length));
					apkFile = new File(file, apkName);
					FileOutputStream fos = new FileOutputStream(apkFile);
					byte buffer[] = new byte[2048];
					int len = 0, count = 0;
					while ((len = is.read(buffer, 0, buffer.length)) != -1 && !stopDownload){
						fos.write(buffer, 0, len);
                        if(!mBackground)
						    handler.sendMessage(handler.obtainMessage(UpgradeAbility.PROGRESS_SET_PROGRESS, count+=len, length));
					}
					is.close();
					fos.close();
					
					if (stopDownload){
						apkFile.delete();
					}else{
                        if(!mBackground){
                            installApk();
                        }
                        if(updateData!=null) {
                            CacheUtils cacheUtils = CacheUtils.getInstance(context);
                            cacheUtils.putString(UpgradeAbility.DOWNLOADED_VERSION, updateData.getVersion());
							cacheUtils.putString(UpgradeAbility.DOWNLOADED_RELEASE_NOTES, updateData.getNote());
                        }
					}
				}else{
                    if(!mBackground)
					    handler.sendEmptyMessage(UpgradeAbility.UPDATE_FAIL_SERVER);
				}
			}catch (IOException e) {
				e.printStackTrace();
                if(!mBackground)
				    handler.sendEmptyMessage(UpgradeAbility.UPDATE_FAIL_CLIENT);
			}finally{
				if (connection != null)
				connection.disconnect();
                if(!mBackground)
				    handler.sendEmptyMessage(UpgradeAbility.CURRENT_DIALOG_DISMISS);
			}
		}
	};

	private void installApk() {
		if (apkFile == null || !apkFile.exists()) {
			return;
		}
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.parse("file://" + apkFile.getAbsolutePath()), "application/vnd.android.package-archive");
		((Activity)context).startActivityForResult(intent, 0);
	}

	public boolean isStopDownload() {
		return stopDownload;
	}

	public void setStopDownload(boolean stopDownload) {
		this.stopDownload = stopDownload;
	}
	public UpdateData getUpdateData() {
		return updateData;
	}

	public void setUpdateData(UpdateData updateData) {
		this.updateData = updateData;
	}

}
