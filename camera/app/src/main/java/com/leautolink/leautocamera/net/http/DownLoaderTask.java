package com.leautolink.leautocamera.net.http;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import com.leautolink.leautocamera.R;
import com.letv.leauto.cameracmdlibrary.utils.HashUtils;
import com.letv.leauto.customuilibrary.CustomProgressDialog;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class DownLoaderTask extends AsyncTask<Void, Integer, Long> {
	private final String TAG = "DownLoaderTask";
	private URL mUrl;
	private File mFile;
	private CustomProgressDialog mDialog;
	private CustomProgressDialog.Builder mCustomBuilder;
	private int mProgress = 0;
	private ProgressReportingOutputStream mOutputStream;
	private Context mContext;
	private String mRoot;
	private boolean mbShowUI;
	private String mMD5="";
	public DownLoaderTask(String url, String path, String MD5, Context context, boolean bShowUI){
		super();
		this.mMD5 = MD5;
		mbShowUI = bShowUI;
		if(mbShowUI && context!=null){
			mCustomBuilder = new CustomProgressDialog.Builder(context);
			//mDialog = new ProgressDialog(context);
			mContext = context;
		}
		else{
			mCustomBuilder = null;
		}
		mRoot = path;
		try {
			mUrl = new URL(url);
			String fileName = new File(mUrl.getFile()).getName();
			Log.d("HomePageActivity", "out=" + path + ", name=" + fileName);
			String[] fileNames = fileName.split("\\?",2);
			Log.d("HomePageActivity", "out=" + path + ", name=" + fileNames[0]);

			mFile = new File(path, fileNames[0]);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public String getFileName(){
		return mFile.getName();
	}
	
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		//super.onPreExecute();
		if(mbShowUI && mCustomBuilder!=null){
			mCustomBuilder.setTitle(R.string.notify_title);
			mCustomBuilder.setMessage(R.string.notify_download + mFile.getName() + "...");
			mCustomBuilder.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mCustomBuilder.setNegativeButton(R.string.notify_cancel, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					cancel(true);
				}
			});
			mDialog = mCustomBuilder.create();
			mDialog.show();
		}
	}

	@Override
	protected Long doInBackground(Void... params) {
		// TODO Auto-generated method stub
		return download();
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		// TODO Auto-generated method stub
		//super.onProgressUpdate(values);
		if(mDialog==null)
			return;
		if(values.length>1){
			int contentLength = values[1];
			if(contentLength==-1){
				mDialog.setIndeterminate(true);
			}
			else{
				mDialog.setMax(contentLength);
			}
		}
		else{
			mDialog.setProgress(values[0].intValue());
		}
	}

	@Override
	protected void onPostExecute(Long result) {
		// TODO Auto-generated method stub
		//super.onPostExecute(result);
		if(mDialog!=null&&mDialog.isShowing()){
			mDialog.dismiss();
		}
		if(isCancelled())
			return;
		//((SettingCameraActivity)mContext).showUnzipDialog();
		if (fileIsExists(mRoot+this.getFileName())) {
			String md5String = null;
			try {
				md5String = HashUtils.getMd5ByFile(new File(mRoot+this.getFileName()));
			} catch (Exception e) {
				e.printStackTrace();
			}
			//Log.d(TAG,String.format("file check md5 net:%s md5 local:%s",mMD5, md5String));
			if(mMD5==null || mMD5.equalsIgnoreCase("") || (md5String!=null && md5String.equalsIgnoreCase(mMD5))) {
				doZipExtractorWork();
			}
		}
	}

	public void doZipExtractorWork() {
		ZipExtractorTask task = new ZipExtractorTask(mRoot
				+ this.getFileName(), mRoot, mContext, true, mbShowUI);
		task.execute();
	}

	public boolean fileIsExists(String path) {
		try {
			//File f = new File(path + downLoaderTask.getFileName());
			File f = new File(path);
			if (!f.exists()) {
				return false;
			}

		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
		return true;
	}
	private long download(){
		URLConnection connection = null;
		int bytesCopied = 0;
		try {
			connection = mUrl.openConnection();
			int length = connection.getContentLength();
			if(mFile.exists()&&length == mFile.length()){
				Log.d(TAG, "file " + mFile.getName() + " already exits!!");
				return 0l;
			}
			mOutputStream = new ProgressReportingOutputStream(mFile);
			publishProgress(0,length);
			bytesCopied =copy(connection.getInputStream(),mOutputStream);
			if(bytesCopied!=length&&length!=-1){
				Log.e(TAG, "Download incomplete bytesCopied=" + bytesCopied + ", length" + length);
			}
			mOutputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bytesCopied;
	}
	private int copy(InputStream input, OutputStream output){
		byte[] buffer = new byte[1024*8];
		BufferedInputStream in = new BufferedInputStream(input, 1024*8);
		BufferedOutputStream out  = new BufferedOutputStream(output, 1024*8);
		int count =0,n=0;
		try {
			while((n=in.read(buffer, 0, 1024*8))!=-1){
				out.write(buffer, 0, n);
				count+=n;
			}
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return count;
	}
	private final class ProgressReportingOutputStream extends FileOutputStream {

		public ProgressReportingOutputStream(File file)
				throws FileNotFoundException {
			super(file);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void write(byte[] buffer, int byteOffset, int byteCount)
				throws IOException {
			// TODO Auto-generated method stub
			super.write(buffer, byteOffset, byteCount);
		    mProgress += byteCount;
		    publishProgress(mProgress);
		}
		
	}
}
