package com.leautolink.leautocamera.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import com.leautolink.leautocamera.R;
import com.leautolink.leautocamera.callback.CustomDialogCallBack;
import com.leautolink.leautocamera.callback.SystemDialogCallBack;
import com.leautolink.leautocamera.domain.ListingInfo;
import com.leautolink.leautocamera.net.http.OkHttpRequest;
import com.leautolink.leautocamera.net.http.httpcallback.DownLoadCallBack;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

import okhttp3.Call;

/**
 * 下载的工具类
 * Created by tianwei1 on 2016/3/10.
 */
public class DownloadUtils {

    private static final java.lang.String TAG = "DownloadUtils";

    public static void downloadSingle(final String url, final String targetPath, final String fileName, final long avaiable, final DownLoadCallBack downLoadCallBack) {
        OkHttpRequest.downLoad("downloadSingle", url, targetPath, fileName, avaiable, downLoadCallBack);
    }

    /**
     * 单个下载
     */
    public static void downloadSingle(final Activity activity, final ListingInfo listingInfo, final ListingInfo.FileInfo currentfileinfo) {

        OkHttpRequest.downLoad("downloadSingle", UrlUtils.getCameraMvideoHttpUrl(listingInfo.getType(), currentfileinfo.getFilename()), UrlUtils.getTargetPath(listingInfo.getType(), activity), currentfileinfo.getFilename(), SdCardUtils.getSdSize(activity, SdCardUtils.TYPE_AVAIABLE), new DownLoadCallBack() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onStart(long total) {

                CustomDialogUtils.showDialog(activity, new CustomDialogCallBack() {
                    @Override
                    public void onCancel() {
                        OkHttpRequest.setCancel(true);
                    }
                });
            }

            @Override
            public void onLoading(long current, long total) {
                CustomDialogUtils.setCurrentTotal(SdCardUtils.formateSize(activity, current) + "/" + SdCardUtils.formateSize(activity, total));
                double percentage = ((double) current / total) * 100;
                CustomDialogUtils.setSeekBarMax((int) total);
                DecimalFormat df = new DecimalFormat("##.##");
                String percentageStr = df.format((percentage));
                CustomDialogUtils.setPercentage(percentageStr + "%");
                CustomDialogUtils.setProgress((int) current);
            }

            @Override
            public void onSucceed() {
                CustomDialogUtils.hideCustomDialog();
                ToastUtils.showToast(activity, activity.getResources().getString(R.string.download_success), ToastUtils.SHORT);
            }

            @Override
            public void onSdCardLackMemory(long total, long avaiable) {

                SystemDialogUtils.showSingleConfirmDialog(activity, activity.getResources().getString(R.string.diglog_title), activity.getResources().getString(R.string.sd_store_loss),activity.getResources().getString(R.string.diglog_button), new SystemDialogCallBack() {
                    @Override
                    public void onSure() {

                    }

                    @Override
                    public void onCancel() {
                        OkHttpRequest.cancelCurrentCall();
                    }
                });
            }

            @Override
            public void onCancel() {

                OkHttpRequest.cancelCurrentCall();
                ToastUtils.showToast(activity, "下载已取消", ToastUtils.SHORT);
                //删除下载不完整的文件
                OkHttpRequest.deleteIntactFile(UrlUtils.getTargetPath(listingInfo.getType(), activity) + "/" + currentfileinfo.getFilename());
            }

            @Override
            public void onError(IOException e) {
                CustomDialogUtils.hideCustomDialog();
                ToastUtils.showToast(activity,activity.getResources().getString(R.string.download_error), ToastUtils.SHORT);
            }
        });
    }

    private static int mCurrentExecuteCount = 0;
    private static int mDownloadSucceedCount = 0;

//    public static void batchDownload(final Activity activity, final ListingInfo listingInfo, List<ListingInfo.FileInfo> downloadFileInfos) {
//        Logger.i(TAG, "batchDownload:" + downloadFileInfos.toString());
//        download(activity, listingInfo, downloadFileInfos);
//    }

    private static void download(final Activity activity, final ListingInfo listingInfo, final List<ListingInfo.FileInfo> downloadFileInfos) {
        if (downloadFileInfos.size() > 0 && (mCurrentExecuteCount + 1) != downloadFileInfos.size()) {
            OkHttpRequest.downLoad("batchdownload", UrlUtils.getCameraMvideoHttpUrl(listingInfo.getType(), downloadFileInfos.get(mCurrentExecuteCount).getFilename()), UrlUtils.getTargetPath(listingInfo.getType(), activity), downloadFileInfos.get(mCurrentExecuteCount).getFilename(), SdCardUtils.getSdSize(activity, SdCardUtils.TYPE_AVAIABLE), new DownLoadCallBack() {
                @Override
                public void onFailure(Call call, IOException e) {
                    if (mCurrentExecuteCount + 1 == downloadFileInfos.size()) {
                        CustomDialogUtils.hideCustomDialog();
                        ToastUtils.showToast(activity, activity.getResources().getString(R.string.download_success) + mDownloadSucceedCount + activity.getResources().getString(R.string.download_item1)+ downloadFileInfos.size() +activity.getResources().getString(R.string.download_item2), ToastUtils.SHORT);
                        mDownloadSucceedCount = 0;
                        mCurrentExecuteCount = 0;
                        downloadFileInfos.clear();
                    }
                    mCurrentExecuteCount++;
                    download(activity, listingInfo, downloadFileInfos);
                }

                @Override
                public void onStart(long total) {
                    CustomDialogUtils.showDialog(activity, new CustomDialogCallBack() {
                        @Override
                        public void onCancel() {
                            OkHttpRequest.setCancel(true);
                            downloadFileInfos.clear();
                            mDownloadSucceedCount = 0;
                            mCurrentExecuteCount = 0;
                        }
                    });
                }

                @Override
                public void onLoading(long current, long total) {
                    CustomDialogUtils.setCurrentTotal((mCurrentExecuteCount + 1) + "/" + downloadFileInfos.size());
                    double percentage = ((double) current / total) * 100;
                    CustomDialogUtils.setSeekBarMax((int) total);
                    DecimalFormat df = new DecimalFormat("##.##");
                    String percentageStr = df.format((percentage));
                    CustomDialogUtils.setPercentage(percentageStr + "%");
                    CustomDialogUtils.setProgress((int) current);
                }

                @Override
                public void onSucceed() {
                    if (mCurrentExecuteCount + 1 == downloadFileInfos.size()) {
                        CustomDialogUtils.hideCustomDialog();
                        ToastUtils.showToast(activity, activity.getResources().getString(R.string.download_success) + mDownloadSucceedCount + activity.getResources().getString(R.string.download_item1)+ downloadFileInfos.size() + activity.getResources().getString(R.string.download_item2), ToastUtils.SHORT);
                        mDownloadSucceedCount = 0;
                        mCurrentExecuteCount = 0;
                        downloadFileInfos.clear();
                    }
                    mCurrentExecuteCount++;
                    mDownloadSucceedCount++;
                    download(activity, listingInfo, downloadFileInfos);
                }

                @Override
                public void onSdCardLackMemory(long total, long avaiable) {

                    SystemDialogUtils.showSingleConfirmDialog(activity, activity.getResources().getString(R.string.diglog_title),activity.getResources().getString(R.string.storage_less), activity.getResources().getString(R.string.diglog_button), new SystemDialogCallBack() {
                        @Override
                        public void onSure() {

                        }

                        @Override
                        public void onCancel() {
                            downloadFileInfos.clear();
                            mDownloadSucceedCount = 0;
                            mCurrentExecuteCount = 0;
                            OkHttpRequest.cancelSameTagCall("batchdownload");
                        }
                    });
                }

                @Override
                public void onCancel() {
                    downloadFileInfos.clear();
                    mDownloadSucceedCount = 0;
                    mCurrentExecuteCount = 0;
                    OkHttpRequest.cancelSameTagCall("batchdownload");
                    ToastUtils.showToast(activity, activity.getResources().getString(R.string.download_cancel) + mDownloadSucceedCount + activity.getResources().getString(R.string.download_item2), ToastUtils.SHORT);
                    //删除下载不完整的文件
                    OkHttpRequest.deleteIntactFile(UrlUtils.getTargetPath(listingInfo.getType(), activity) + "/" + downloadFileInfos.get(mCurrentExecuteCount).getFilename());
                }

                @Override
                public void onError(IOException e) {
                    CustomDialogUtils.hideCustomDialog();
                    ToastUtils.showToast(activity, activity.getResources().getString(R.string.download_error), ToastUtils.SHORT);
                }
            });
        }

    }

    public static String getDownLoadApkName(String version){
        return "LetvDashCam_"+version+".apk";
    }

    public static String getApkPath(){
        return Environment.getExternalStorageDirectory()+ File.separator + "LeDashCam" + File.separator;
    }

    public static void installApk(Context context , String path){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.parse("file://" + path), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

}
