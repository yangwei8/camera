package com.leautolink.leautocamera.ui.activity;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.leautolink.leautocamera.R;
import com.leautolink.leautocamera.config.Constant;
import com.leautolink.leautocamera.ui.base.BaseActivity;
import com.leautolink.leautocamera.ui.view.customview.SliderView;
import com.leautolink.leautocamera.utils.AsyncTaskUtil;
import com.leautolink.leautocamera.utils.DateUtils;
import com.leautolink.leautocamera.utils.DisplayUtils;
import com.leautolink.leautocamera.utils.Logger;
import com.leautolink.leautocamera.utils.SdCardUtils;
import com.leautolink.leautocamera.utils.mediaplayermanager.MedIaPlayerManager;
import com.leautolink.leautocamera.utils.mediaplayermanager.interfaces.IPlayer;
import com.leautolink.leautocamera.utils.mediaplayermanager.interfaces.IPlayerListener;
import com.letvcloud.cmf.TextureVideoView;
import com.media.NativeFfmpegCmd;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by tianwei on 16/6/21.
 */
@EActivity(R.layout.activity_video_edit)
public class VideoEditActivity extends BaseActivity implements SliderView.OnTouchingListener, View.OnClickListener, IPlayerListener {
    private static final String TAG = "VideoEditActivity";
    private static final int TO_GET_PIC = 100;
    private static final int TO_SEEK_VIDEO = 101;
    //需要截取视频的长度
    private static final Long SHARED_DURATION = 16000l;
    //取图片的总数
    //private static final float CAP_PIC_COUNT = 10.0f;
    private static final long CAP_PIC_COUNT = 10;
    //当前取的图片的index
    private int mCurrentPicIndex = 1;

    @ViewById(R.id.navigation_bar_right_bt)
    Button mBtnBarRight;
    @ViewById(R.id.navigation_bar_title)
    TextView mTvBarTitle;
    @ViewById(R.id.navigation_bar_left_ib)
    ImageButton mIbBarLeft;
    @ViewById(R.id.video_layout)
    RelativeLayout mVideoLayout;
    @ViewById(R.id.iv_thumb_at_time)
    ImageView mIvThumbAtTime;
    @ViewById(R.id.iv_1)
    ImageView mIv1;
    @ViewById(R.id.iv_2)
    ImageView mIv2;
    @ViewById(R.id.iv_3)
    ImageView mIv3;
    @ViewById(R.id.iv_4)
    ImageView mIv4;
    @ViewById(R.id.iv_5)
    ImageView mIv5;
    @ViewById(R.id.iv_6)
    ImageView mIv6;
    @ViewById(R.id.iv_7)
    ImageView mIv7;
    @ViewById(R.id.iv_8)
    ImageView mIv8;
    @ViewById(R.id.iv_9)
    ImageView mIv9;
    @ViewById(R.id.iv_10)
    ImageView mIv10;
    @ViewById(R.id.ll_container)
    LinearLayout mLlContainer;
    @ViewById(R.id.sv_16_duration)
    SliderView mSv;
    //本地用户选择视频的url  含有file://
    @Extra
    String localVideoUrl;
    //本地用户选择视频的绝对路径  不含file://
    private String mSourceVideoPath;
    //存放10个bitmap
    private List<Bitmap> mBitmaps = new ArrayList<>(10);
    private boolean isCanSeek = true;
    private boolean isCutting = false;
    //视频的总长度
    //private float mVideoDuration;
    private long mVideoDuration;
    //视频裁剪工具
//    private VideoEditor mEditor;
    private int screenWidth;
    private long atTime;

    private IPlayer mPlayer;
    private Handler mHandler;


    private long startTime;
    private long endTime;
    private Map<Integer, Integer> bitMapsStartTime = new HashMap<Integer, Integer>();

    @AfterViews
    void init() {
        Logger.e(TAG, "需要分享的视频的url：" + localVideoUrl);

        mPlayer = MedIaPlayerManager.createPlayer();
        initViews();
        initDatas();
        initListeners();
    }


    private void initViews() {
        showLoading(getResources().getString(R.string.video_analy));
        mTvBarTitle.setText(getResources().getString(R.string.video_cut));
        mBtnBarRight.setVisibility(View.VISIBLE);
        mBtnBarRight.setText(getResources().getString(R.string.cut));
        mBtnBarRight.setTextColor(Color.rgb(20, 150, 177));
        initTopViewSize();
        Glide.with(this).load(localVideoUrl).placeholder(R.drawable.img_default).into(mIvThumbAtTime);
    }

    private void initTopViewSize() {
        int screenWidth = DisplayUtils.getScreenWidth(this);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mVideoLayout.getLayoutParams();
        params.width = screenWidth;
        params.height = screenWidth * 9 / 16;
        mVideoLayout.setLayoutParams(params);
        mIvThumbAtTime.setLayoutParams(params);
    }

    private void initDatas() {
        File TargetVideoPath = new File(getTargetVideoPath());
        if (TargetVideoPath.exists()) {
            TargetVideoPath.delete();
        }
        File TargetThumbPath = new File(getTargetThumbPath());
        if (TargetThumbPath.exists()) {
            TargetThumbPath.delete();
        }
        getScreenWidth();
        getSourceVideoPath();
        getVideoPics();
    }

    private void initListeners() {
        mSv.setOnTouchingListener(this);
        mBtnBarRight.setOnClickListener(this);
        mIbBarLeft.setOnClickListener(this);
        mLlContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mSv.onTouchEvent(event);
            }
        });
    }

    private void getSourceVideoPath() {
        mSourceVideoPath = localVideoUrl.substring(7);
        Logger.e(TAG, "videoPath:" + mSourceVideoPath);
    }

    private void getVideoPics() {
        Constant.isGetPics = true;
        Logger.e(TAG, "getVideoPics  localVideoUrl : " + localVideoUrl);
        mPlayer.setPlayerListener(this);
        mPlayer.play(this, mVideoLayout, localVideoUrl, MedIaPlayerManager.PLAY_TYPE_VOD, MedIaPlayerManager.ENCODE_TYPE_HARD);
    }

    private void initHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                int currentPosition = mPlayer.getCurrentPosition();
                switch (msg.what) {
                    case TO_GET_PIC:
                        if (currentPosition > 0) {
                            Logger.e(TAG, "currentPosition:" + currentPosition);
                            //视频已经开始播放了，可以截屏了
                            startCapturePics();
                        } else {
                            mHandler.sendEmptyMessage(TO_GET_PIC);
                        }
                        break;
                    case TO_SEEK_VIDEO:
                        int seekToPosition = (int) ((mVideoDuration / CAP_PIC_COUNT) * mCurrentPicIndex);
                        if (seekToPosition <= mVideoDuration) {
                            Logger.e(TAG, "seek to :" + seekToPosition);
                            mPlayer.seekTo(seekToPosition);
                        } else {
                            Constant.isGetPics = false;
                            mPlayer.seekTo(0);
                            showPics();

                            hideLoading();
                            endTime = System.currentTimeMillis();
                            Logger.e(TAG, "捕获图片完毕  end time :" + endTime);
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(endTime - startTime);
                            SimpleDateFormat format = new SimpleDateFormat("HH-mm-ss");
                            Logger.e(TAG, "总用时：" + format.format(calendar.getTimeInMillis()));
                        }
                        break;
                }
            }
        };
    }

    /**
     * 展示下面一行从视频中截取的图片
     */
    private void showPics() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mSv.getLayoutParams();
        params.width = (int) (screenWidth * SHARED_DURATION / mVideoDuration);
        mSv.setLayoutParams(params);
        mIv1.setImageBitmap(mBitmaps.get(0));
        mIv2.setImageBitmap(mBitmaps.get(1));
        mIv3.setImageBitmap(mBitmaps.get(2));
        mIv4.setImageBitmap(mBitmaps.get(3));
        mIv5.setImageBitmap(mBitmaps.get(4));
        mIv6.setImageBitmap(mBitmaps.get(5));
        mIv7.setImageBitmap(mBitmaps.get(6));
        mIv8.setImageBitmap(mBitmaps.get(7));
        mIv9.setImageBitmap(mBitmaps.get(8));
        mIv10.setImageBitmap(mBitmaps.get(9));

        for (int i = 0; i < CAP_PIC_COUNT; i++) {
            int bitMapsStartPosition = (int) ((mVideoDuration / CAP_PIC_COUNT) * (i + 1));
            bitMapsStartTime.put(i, bitMapsStartPosition);
        }
    }


    private void startCapturePics() {
        Logger.e(TAG, "startCapturePics mCurrentPicIndex :" + mCurrentPicIndex);
        TextureVideoView textureView = (TextureVideoView) mPlayer.getTextureView();
        Bitmap bitmap = textureView.getBitmap((int) (textureView.getVideoWidth()), (int) (textureView.getVideoHeight()));
        mBitmaps.add(bitmap);

        mCurrentPicIndex++;
        mHandler.sendEmptyMessage(TO_SEEK_VIDEO);
    }


    @Override
    public void onPrepared() {
        mVideoDuration = mPlayer.getDuration();
        Logger.e(TAG, "onPrepared  视频长度：" + mVideoDuration);
    }

    @Override
    public void onFirstPic() {
        Logger.e(TAG, "onFirstPic   isGetPics :" + Constant.isGetPics);
        if (Constant.isGetPics) {
            if (mCurrentPicIndex == 1) {
                startTime = System.currentTimeMillis();
                Logger.e(TAG, "开始捕获图片   start time :" + startTime);
                initHandler();
                if (mHandler != null) {
                    mHandler.sendEmptyMessage(TO_GET_PIC);
                }
            } else {
                mHandler.sendEmptyMessage(TO_GET_PIC);
            }
        } else {
            if (mPlayer != null) {
                Logger.e(TAG, "onFirstPic  pause");
                mPlayer.pause();
                mIvThumbAtTime.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onBufferingStart() {

    }

    @Override
    public void onBufferingUpdate(int percent) {

    }

    @Override
    public void onBufferingEnd() {

    }

    @Override
    public void onComplete() {

    }

    @Override
    public void onError(int what, String extra) {
    }

    @Override
    public void onInfo(int what, long extra) {

    }

    @Override
    public void onBlock() {

    }

    @Override
    public void onSeekComplete() {
        mPlayer.start();
        isCanSeek = true;
        Logger.e(TAG, "onSeekComplete  isCanSeek : " + isCanSeek);
    }

    /**
     * 获取截取的视频，以及按照一定rate从视频中截取的图片的文件夹的path
     *
     * @return
     */

    private String getCachePath() {
        String path = SdCardUtils.getSDCardRootPath(this) + File.separator + "cache";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    private String getSourceVideoName() {
        return localVideoUrl.substring(localVideoUrl.lastIndexOf("/") + 1);
    }

    /**
     * 以视频文件名命名的文件夹，用来放该视频每秒的缩略图和裁剪之后的视频
     *
     * @return
     */
    private String getCurrentFileCachePath() {
        String path = getCachePath() + File.separator + getSourceVideoName();
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    /**
     * 裁剪之后视频的存储path
     *
     * @return
     */
    private String getTargetVideoPath() {
        String path = getCurrentFileCachePath() + File.separator + "cuted.MP4";
        return path;
    }

    /**
     * 视频的缩略图
     *
     * @return
     */
    private String getTargetThumbPath() {
        String path = getCurrentFileCachePath() + File.separator + "thumb.JPG";
        return path;
    }

    private void getScreenWidth() {
        Resources resources = getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        screenWidth = dm.widthPixels;
    }

    private float calculatePreWidthRepresentSomeVideoLength() {
        Logger.e(TAG, "视频长度：" + mVideoDuration + "   屏幕宽度：" + screenWidth);
        return (mVideoDuration / screenWidth);
    }


    @Override
    public void onDown(int startx) {
    }

    @Override
    public void onMove(int left, int top, int right, int bottom, TextView tvCutDuration) {
        Logger.e(TAG, "left :" + left + " rate :" + calculatePreWidthRepresentSomeVideoLength() + "  isCanSeek :" + isCanSeek);
        if (isCanSeek) {
            isCanSeek = false;
            atTime = (long) (calculatePreWidthRepresentSomeVideoLength() * left);
            long endTime = atTime + SHARED_DURATION;
            endTime = (endTime > mVideoDuration ? (long) mVideoDuration : endTime);
            Logger.e(TAG, "atTime :" + atTime + "  endTime : " + endTime);
            tvCutDuration.setText(DateUtils.formatHMS(String.valueOf(atTime)) + "~" + DateUtils.formatHMS(String.valueOf(endTime)));
            mPlayer.seekTo((int) atTime);
        }

        //getVideoThumbAtTime(mSourceVideoPath, atTime);

    }
    void getVideoThumbAtTime(String SourceVideoPath,long startTime) {
        String sourceFile = SdCardUtils.getSDCardRootPath(this) + File.separator + "cache"+File.separator + getSourceVideoName()+ File.separator + "thumb.JPG";
        File file = new File(sourceFile);
        if (file.exists()) {
            Logger.i(TAG, "releaseResources:file=  " + file);
            file.delete();
        }
        MediaMetadataRetriever media = new MediaMetadataRetriever();
        Logger.e(TAG, "SourceVideoPath :" + SourceVideoPath );
        media.setDataSource(SourceVideoPath);
       // Bitmap  bitmap = media.getFrameAtTime(startTime);
        Bitmap  bitmap = media.getFrameAtTime(startTime,MediaMetadataRetriever.OPTION_CLOSEST);
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            try {
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                fos = null;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    fos = null;
                }
            }
        }
        bitmap.recycle();
    }
    @Override
    public void onUp() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.navigation_bar_left_ib:
                finish();
                break;
            case R.id.navigation_bar_right_bt:
                if (!isCutting) {
                    cut();
                }
                break;
        }
    }

    private void cut() {
        isCutting = true;
        //裁剪视频，裁剪完成后进入UploadVideoActivity
        if (atTime > mVideoDuration - SHARED_DURATION) {
            atTime = (long) (mVideoDuration - SHARED_DURATION);
        }
        getVideoThumbAtTime(mSourceVideoPath, atTime);
//        for (int i = 1; i < 10; i++) {
//            int a_time = bitMapsStartTime.get(i - 1);
//            int b_time = bitMapsStartTime.get(i);
//            Logger.e(TAG, "a_time :" + a_time + "  b_time : " + b_time + ",atTime :"+atTime);
//            if (i == 1 && atTime <= a_time) {
//                saveThumb2Sd(i - 1);
//                break;
//            } else if (atTime < b_time && atTime > a_time && (Math.abs(atTime - a_time) > SHARED_DURATION/2) && ((b_time-atTime-SHARED_DURATION)<0)) {
//                saveThumb2Sd(i);
//                break;
//            }else if (atTime < b_time && atTime > a_time && (Math.abs(atTime - a_time) <= SHARED_DURATION/2) && ((b_time-atTime-SHARED_DURATION)<0)) {
//                saveThumb2Sd(i-1);
//                break;
//            }else if (atTime < b_time && atTime > a_time && ((b_time-atTime-SHARED_DURATION)>0)) {
//                saveThumb2Sd(i - 1);
//                break;
//            }
//        }
        startCut();
    }

    /**
     * 保存缩略图到本地
     */
    @Background
    void saveThumb2Sd(int index) {
//        FileOutputStream fos = null;
//        try {
//            File file = new File(getTargetThumbPath());
//            if (file.exists()) {
//                file.delete();
//            }
//            fos = new FileOutputStream(getTargetThumbPath());
//            mBitmaps.get(index).compress(Bitmap.CompressFormat.JPEG, 100, fos);
//            try {
//                fos.flush();
//                fos.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
//                fos = null;
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } finally {
//            if (fos != null) {
//                try {
//                    fos.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } finally {
//                    fos = null;
//                }
//            }
//        }
    }


    private static String getTimeFromInt(Long time) {
        if (time <= 0) {
            return "00:00:00";
        }
        Long million = (time / 1000) / 60;
        Long secondnd = (time / 1000) % 60;
        Logger.e(TAG, "million：" + million + ",secondnd=" + secondnd);
        String f = null;
        String m = null;
        if (million >= 10) {
            f = million + "";
        } else if (million == 0) {
            f = "00";
        } else {
            f = "0" + million;
        }
        if (secondnd >= 10) {
            m = secondnd + "";
        } else if (secondnd == 0) {
            m = "00";
        } else {
            m = "0" + secondnd;
        }
        Logger.e(TAG, "f=" + f + ",m=" + m);
        return "00:" + f + ":" + m;
    }

    private void startCut() {
        Logger.e(TAG, "startCut");
        AsyncTaskUtil.newInstance().execute(new AsyncTaskUtil.AsyncTaskListener() {
            @Override
            public void onPreExecute() {
                showLoading(getResources().getString(R.string.cuting));
//                if (mEditor == null) {
//                    mEditor = new VideoEditor();
//                }
            }

            @Override
            public Object doInBackground(Object... params) {
                Logger.e(TAG, "开始裁剪的时间：" + atTime + ",SHARED_DURATION=" + SHARED_DURATION);
                Logger.e(TAG, "mSourceVideoPath：" + mSourceVideoPath + ",TargetVideoPath=" + getTargetVideoPath());
                // return VideoUtil.cutVideo(mSourceVideoPath, getTargetVideoPath(), (atTime / 1000), ((atTime + SHARED_DURATION) / 1000));
//                return mEditor.executeVideoCutOut(mSourceVideoPath, getTargetVideoPath(), (atTime / 1000), (SHARED_DURATION / 1000));
                String startTime = getTimeFromInt(atTime);
                String duration = getTimeFromInt(SHARED_DURATION);
                Logger.e(TAG, "startTime：" + startTime + ",duration=" + duration);
                NativeFfmpegCmd nativeThumbnail = new NativeFfmpegCmd();
                nativeThumbnail.getVideoOfCutting(mSourceVideoPath, startTime, duration, getTargetVideoPath());
                return 0;
            }

            @Override
            public void onProgressUpdate(int progress) {

            }

            @Override
            public void onPostExecute(Object result) {
                Logger.e(TAG, "裁剪结果：" + result);
                if (((int) result) == 0) {
                    Logger.e(TAG, "裁剪成功");
                    goUploadActivity();
                    finish();
                }
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onCancelled(Object result) {

            }
        });
    }


    private void goUploadActivity() {
        UploadFileActivity_.intent(this).extra("uploadFilePath", getTargetVideoPath()).extra("editedVideoThumbPath", getTargetThumbPath()).start();
//        UploadFileActivity_.intent(this).uploadFilePath(getTargetVideoPath()).mListingInfo(new ListingInfo("aa", true, null)).start();
//        UploadFileActivity_.intent(this).uploadFilePath(getTargetVideoPath()).start();
    }

    @Override
    public void releaseResources() {
        Constant.isGetPics = false;
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
        if (mBitmaps != null) {
            mBitmaps.clear();
            mBitmaps = null;
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
//        mEditor = null;
    }


}
