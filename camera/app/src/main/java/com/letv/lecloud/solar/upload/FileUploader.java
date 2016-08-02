package com.letv.lecloud.solar.upload;

import com.letv.lecloud.solar.http.CancellationHandler;
import com.letv.lecloud.solar.http.CompletionHandler;
import com.letv.lecloud.solar.http.ProgressHandler;
import com.letv.lecloud.solar.http.ResponseInfo;
import com.letv.lecloud.solar.http.HttpManager;
import com.letv.lecloud.solar.utils.FileID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.message.BasicHeader;

/**
 * Created by wiky on 15/12/18.
 */
final public class FileUploader implements Runnable {
    private final long size;

    private final UpResponseHandler upResponseHandler;
    private final UpProgressHandler upProgressHandler;
    private final CancellationHandler cancellationHandler;
    private final HttpManager httpManager;
    private final Configuration config;
    private final Header[] headers;
    private byte[] chunkBuffer;
    private String fileid;
    private RandomAccessFile file;
    private String filePath;
    private File f;
    private String token;
    private int realChipSize;
    private URI queryUri;
    private String uploadUrl = "";
    private double progress;
    private int lastSpeed;
    private long bytesWrittenSum;
    private long bytesWrittenLast;
    private long curTime;
    private long lastTime;
    private boolean isCancel;

    FileUploader(HttpManager httpManager, Configuration config, String filePath, String token,
                   final UpResponseHandler upResponseHandler, final UpProgressHandler upProgressHandler) {
        this.httpManager = httpManager;
        this.config = config;
        this.filePath  = filePath;
        this.f = new File(filePath);
        this.size = (int) f.length();
        this.headers = new Header[1];
        headers[0] = new BasicHeader("Content-Type", "application/octet-stream");
        this.file = null;
        this.upResponseHandler = upResponseHandler;
        this.upProgressHandler = upProgressHandler;

        this.token = token;
        progress = 0;
        lastSpeed = 0;
        bytesWrittenSum = 0;
        bytesWrittenLast = 0;
        isCancel = false;

        cancellationHandler = new CancellationHandler() {
            @Override
            public boolean isCancelled() {
                return isCancel;
            }
        };
    }

    public void cancelUpload(){
        isCancel = true;
        return;
    }

    private double PercentStrToDouble(String str){
        double value;
        try {
            value = Double.parseDouble(str);
        }catch (NumberFormatException e){
            return 0;
        }
        return value / 100;
    };


    private void putChip(int chipIndex, int chipSize, ProgressHandler progressHandler,
                         CompletionHandler completionHandler) {
        long offset = (chipIndex -1) * chipSize;
        long left = size - offset;
        int readSize = left < chipSize ? (int)left : chipSize;

        try {
            file.seek(offset);
            file.read(chunkBuffer, 0, readSize);
        } catch (IOException e) {
            upResponseHandler.onResponse(ResponseInfo.fileError(e), null);
            return;
        }

        URI uploadUri = null;
        try {
             uploadUri = new URI(String.format("%s&chip=%d&fstart=%d&fstop=%d",
                    uploadUrl,
                    chipIndex,
                    offset,
                    offset + readSize - 1));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        httpManager.postData(uploadUri, chunkBuffer, 0, readSize, headers,
                progressHandler, completionHandler, cancellationHandler);
    }


    @Override
    public void run() {
        try {
            this.fileid = FileID.calc(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            upResponseHandler.onResponse(ResponseInfo.fileError(e), null);
            return;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            upResponseHandler.onResponse(ResponseInfo.fileError(e), null);
            return;
        }

        try {
            file = new RandomAccessFile(f, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            upResponseHandler.onResponse(ResponseInfo.fileError(e), null);
            return;
        }
        // query文件上传进度
        try {
            queryUri = new URI(String.format("%s?appkey=%s&token=%s&fileid=%s&filename=%s&size=%s&ctype=%d&chip=10&chipsize=%d",
                    this.config.QUERY_URI,
                    this.config.appkey,
                    this.token,
                    this.fileid,
                    this.f.getName(),
                    this.size,
                    3,
                    this.config.DEFAULT_CHIP_SIZE
            ));
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        uploadUrl = "";
        uploadNextChip(0, 0);

    }

    private void uploadNextChip(final int chipIndex, final int retried){
        if(uploadUrl == ""){
            uploadQuery(retried);
            return;
        }

        CompletionHandler completeHandler = new CompletionHandler() {
            @Override
            public void complete(ResponseInfo info, JSONObject response){
                if (info.isOK()) {
                    JSONObject result = null;
                    JSONArray fileChips = null;
                    try {
                        result = response.getJSONObject("result");
                        if(result.optBoolean("complete") == true){
                            if(upProgressHandler != null){
                                upProgressHandler.onProgress(1.0, -1);
                            }
                            upResponseHandler.onResponse(info, response);
                        }else{
                            fileChips = result.getJSONArray("fileChips");
                            for (int i = 0; i < fileChips.length(); i++){
                                uploadNextChip(fileChips.optInt(i), 0);
                            }
                        }
                    } catch (JSONException e) {
                        upResponseHandler.onResponse(ResponseInfo.invalidResponse(e), response);
                    }

                    return; // 成功，返回
                }

                if( info.isNetworkBroken() && !info.isCancelled() && retried < (config.retryMax*3) ){
                    if( (retried % 3) == 0 && retried != 0 ){
                        uploadUrl = "";
                    }
                    uploadNextChip(chipIndex, retried + 1);

                    return; // 重试，返回
                }

                upResponseHandler.onResponse(info, response);
                return;
            }
        };
        final ProgressHandler progressHandler = new ProgressHandler() {
            @Override
            public void onProgress(int bytesWritten, double totalSize, long flag) {
//                Log.e("Solar", "written="+bytesWritten + " totalSize="+ totalSize + " duration="+ duration);
                if(flag == -1){
                    return;
                }
                if(bytesWrittenSum == 0 ){
                    curTime = System.currentTimeMillis();
                    lastTime = curTime;
                    bytesWrittenLast = bytesWrittenSum;
                }else{
                    curTime = System.currentTimeMillis();
                }
                bytesWrittenSum += bytesWritten;

                long dt = curTime - lastTime;
                if( dt < 500 ){
                    return;
                }

                double percent = (double) bytesWrittenSum / size;
                double g = progress + percent;

                g = (double)((int)(g*10000)) / 10000;
                if (g > 0.98) {
                    g = 0.98;
                }else if( g < 0.02) {
                    g = 0.02;
                }

                int speed = (int)((bytesWrittenSum - bytesWrittenLast) / dt * 1000);
                int tmpSpeed = lastSpeed == 0 ? speed : (speed * 13 + lastSpeed * 87) / 100 ;
//                Log.e("Solar", "percent="+g+" lastSpeed="+lastSpeed);
                lastTime = curTime;
                bytesWrittenLast = bytesWrittenSum;
                lastSpeed = tmpSpeed;

                if(upProgressHandler != null){
                    upProgressHandler.onProgress(g, tmpSpeed);
                }

            }
        };

        putChip(chipIndex, realChipSize, progressHandler, completeHandler);
    }

    private void uploadQuery(final int retried){
        CompletionHandler queryHandler = new CompletionHandler() {
            @Override
            public void complete(ResponseInfo info, JSONObject response){
                if (info.isOK()) {
                    JSONObject result = null;
                    JSONArray fileChips = null;
                    try {
                        result = response.getJSONObject("result");
                        if(result.optBoolean("complete") == true){
                            if(upProgressHandler != null){
                                upProgressHandler.onProgress(1.0, -1);
                            }
                            upResponseHandler.onResponse(info, response);
                        }else{
                            progress = PercentStrToDouble(result.optString("progress"));
                            if(progress != 0 && upProgressHandler != null){
                                upProgressHandler.onProgress(progress, -1);
                            }
                            uploadUrl = result.getString("uploadUrl");
                            realChipSize = result.getInt("chipSize");
                            chunkBuffer = new byte[realChipSize];
                            fileChips = result.getJSONArray("fileChips");
                            for (int i = 0; i < fileChips.length(); i++){
                                uploadNextChip(fileChips.optInt(i), retried);
                            }
                        }
                    } catch (JSONException e) {
                        upResponseHandler.onResponse(ResponseInfo.invalidResponse(e), response);
                    }
                    return;
                }
                if( info.isNetworkBroken() && !info.isCancelled() && retried < (config.retryMax*3)  ){
                    uploadNextChip(0, retried + 3);
                    return; // 重试，返回
                }

                upResponseHandler.onResponse(info, response);
            }
        };

        httpManager.get(queryUri, null, queryHandler);

    }
}
