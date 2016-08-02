package com.leautolink.leautocamera.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.reflect.TypeToken;
import com.leautolink.leautocamera.R;
import com.leautolink.leautocamera.config.Constant;
import com.leautolink.leautocamera.domain.respone.BaseInfo;
import com.leautolink.leautocamera.domain.respone.CommentInfo;
import com.leautolink.leautocamera.domain.respone.DetaiVideoInfo;
import com.leautolink.leautocamera.domain.respone.DiscoverInfos;
import com.leautolink.leautocamera.domain.respone.User;
import com.leautolink.leautocamera.domain.respone.VideoInfo;
import com.leautolink.leautocamera.event.NeedUpdateDiscoverListEvent;
import com.leautolink.leautocamera.event.VideoIsOnUsingEvent;
import com.leautolink.leautocamera.net.http.GsonUtils;
import com.leautolink.leautocamera.net.http.OkHttpRequest;
import com.leautolink.leautocamera.net.http.RequestTag.RequestTag;
import com.leautolink.leautocamera.net.http.httpcallback.PostCallBack;
import com.leautolink.leautocamera.ui.adapter.CommentListAdapter;
import com.leautolink.leautocamera.ui.base.BaseActivity;
import com.leautolink.leautocamera.ui.view.customview.MyListView;
import com.leautolink.leautocamera.utils.AnimationUtils;
import com.leautolink.leautocamera.utils.DisplayUtils;
import com.leautolink.leautocamera.utils.FormatUtils;
import com.leautolink.leautocamera.utils.Logger;
import com.leautolink.leautocamera.utils.LoginManager;
import com.leautolink.leautocamera.utils.NetworkUtil;
import com.leautolink.leautocamera.utils.ShareContentText;
import com.leautolink.leautocamera.utils.SpUtils;
import com.leautolink.leautocamera.utils.glideutils.GlideCircleTransform;
import com.leautolink.leautocamera.utils.mediaplayermanager.MedIaPlayerManager;
import com.leautolink.leautocamera.utils.mediaplayermanager.interfaces.IPlayer;
import com.leautolink.leautocamera.utils.mediaplayermanager.interfaces.IPlayerListener;
import com.letv.loginsdk.bean.UserBean;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;
import okhttp3.Call;
import okhttp3.Response;

@EActivity(R.layout.activity_details)
public class DetailsActivity extends BaseActivity implements IPlayerListener {

    private static final String TAG = "DetailsActivity";
    @ViewById(R.id.toolbar)
    RelativeLayout toolbar;

    @ViewById(R.id.rl_user_info)
    RelativeLayout rl_user_info;

    @ViewById(R.id.video_container)
    RelativeLayout video_container;

    @ViewById(R.id.rl_text_content)
    RelativeLayout rl_text_content;

    @ViewById(R.id.ll__send_comment_content)
    LinearLayout ll__send_comment_content;

    @ViewById(R.id.v_line)
    View v_line;

    @ViewById(R.id.video_layout)
    RelativeLayout video_layout;

    @ViewById(R.id.iv_norecord_home)
    TextView iv_norecord_home;

    @ViewById(R.id.lv_comment_list)
    MyListView lv_comment_list;

    @ViewById(R.id.iv_head_icon)
    ImageView iv_head_icon;

    @ViewById(R.id.iv_top_thumb)
    ImageView iv_top_thumb;
    @ViewById(R.id.iv_play)
    ImageView iv_play;

    @ViewById(R.id.iv_up_icon)
    ImageView iv_up_icon;

    @ViewById(R.id.iv_refresh)
    ImageView iv_refresh;

    @ViewById(R.id.navigation_bar_title)
    TextView navigation_bar_title;
    @ViewById(R.id.tv_name)
    TextView tv_name;
    @ViewById(R.id.tv_location)
    TextView tv_location;

    @ViewById(R.id.tv_browse_num)
    TextView tv_browse_num;

    @ViewById(R.id.tv_up_num)
    TextView tv_up_num;
    @ViewById(R.id.tv_play_position)
    TextView tv_play_position;

    @ViewById(R.id.tv_comment_num)
    TextView tv_comment_num;

    @ViewById(R.id.et_input_comment)
    EditText et_input_comment;
    @ViewById(R.id.sb_play)
    ProgressBar sb_play;
    @ViewById(R.id.share_btn)
    ImageButton shareButton;

    private List<CommentInfo> commentInfoList;

    private CommentListAdapter commentListAdapter;

    @Extra
    int videoID;
    @Extra
    int upCount;
    @Extra
    int commentCount;
    @Extra
    int seeCount;
    @Extra
    String userIcon;
    @Extra
    String userName;
    @Extra
    String userAddress;
    @Extra
    String thumbUrl;
    @Extra
    String location;
    @Extra
    String hightUrl;
    @Extra
    String title;
    @Extra
    int type;

    private String upStr;
    /**
     * 播放地址
     */
    private String  playUrl;

    private int currentPageIndex = 1;

    private int pageCount = 10;
    private IPlayer mPlayer;
    private Timer mTimer;
    private TimerTask mTimerTask;

    private DiscoverInfos info;
    /**
     * 发现页面是否需要更新    点赞数   评论数   和    查看数
     */
    private boolean isNeedUpdateList = false;
    private int videoLength;


    private String  classifyId;
    @AfterViews
    void init() {
        initVideoSize();
        info = new DiscoverInfos();
        info.setUpCount(upCount);
        info.setCommentCount(commentCount);
        info.setBrowseCount(seeCount);
        Constant.isLive = false;
        getComment();
        initUpCount(upCount);
        tv_browse_num.setText(FormatUtils.formatCount(seeCount));
        navigation_bar_title.setText(title);
        setCommentCount(commentCount);

        tv_name.setText(userName);
        tv_location.setText(location);

        Glide.with(this).load(userIcon).placeholder(R.drawable.header_defaut_icon).transform(new GlideCircleTransform(this)).crossFade().into(iv_head_icon);
        lv_comment_list.initBottomView();
        lv_comment_list.setMyPullUpListViewCallBack(new MyListView.MyPullUpListViewCallBack() {
            @Override
            public void scrollBottomState() {
                currentPageIndex++;
                getComment();
            }
        });
        if (type == 0) {
            sb_play.setVisibility(View.GONE);
            iv_play.setVisibility(View.GONE);
            iv_top_thumb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String photoUrl = TextUtils.isEmpty(hightUrl) ? "" : hightUrl;
                    HighDefinitionPhotoActivity_.intent(DetailsActivity.this).url(thumbUrl).hightUrl(photoUrl).videoID(videoID).start();
                }
            });

        } else {
            mPlayer = MedIaPlayerManager.createPlayer();
            mPlayer.initPlayer(this, video_layout, MedIaPlayerManager.PLAY_TYPE_VOD);
            mPlayer.setPlayerListener(this);
            iv_play.setVisibility(View.VISIBLE);
        }
        Logger.i(TAG, "thumbUrl=" + thumbUrl + ",videoID=" + videoID);
        Glide.with(this).load(thumbUrl).crossFade().into(iv_top_thumb);
        shareButton.setClickable(true);
        if(NetworkUtil.isConnected(this)) {
            getVideoDetail();
        }else{
            showToastSafe(getResources().getString(R.string.net_error));
        }

    }

    private void getVideoDetail() {

        Map<String, String> param = new HashMap<>();
        param.put("id", videoID + "");
        param.put("userId", LoginManager.getUid(this));

        Logger.e(GsonUtils.toJson(param));
        OkHttpRequest.post(RequestTag.VIDEO_DETAIL_TAG, RequestTag.VIDEO_DETAIL_TAG_URL, param, new PostCallBack() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    String str = response.body().string();
                    Logger.e("getPlayUrl : " + str);
                    DetaiVideoInfo detailVideoInfo = GsonUtils.getDefault().fromJson(str, DetaiVideoInfo.class);
                    if (detailVideoInfo.getCode() == 200) {
                        DiscoverInfos discoverInfos = detailVideoInfo.getObj();
                        hightUrl = discoverInfos.getImageurlDetail();
                        classifyId = discoverInfos.getClassifyId();
                        updateView(discoverInfos);
                    } else {
                        showToastSafe(getResources().getString(R.string.video_error));
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int errorCode) {

            }
        });


    }


    void updateView(DiscoverInfos discoverInfos) {
        initUpCount(discoverInfos.getUpCount());
        setCommentCount(discoverInfos.getCommentCount());
        setSeeCount(discoverInfos.getBrowseCount());
        if (discoverInfos.isUp()) {
            setUpState(R.drawable.discover_fragment_uped_icon, false);
        } else {
            setUpState(R.drawable.discover_fragment_up_icon, true);
        }

    }

    @UiThread
    void setUpState(int discover_fragment_uped_icon, boolean enabled) {
        iv_up_icon.setImageResource(discover_fragment_uped_icon);
        iv_up_icon.setEnabled(enabled);
    }
    @UiThread
    void setSeeCount(int count) {
        tv_browse_num.setText(FormatUtils.formatCount(count));

    }

    @UiThread
    void setCommentCount(int commentNum) {
        String commentStr = getString(R.string.activity_details_comment_count, FormatUtils.formatCount(commentNum));
        tv_comment_num.setText(commentStr);
    }

    private void getPlayUrl() {
        Map<String, String> param = new HashMap<>();
        param.put("id", videoID + "");
        Map<String, Object> jsonStr = new HashMap<>();
        jsonStr.put("OS", "android");
        jsonStr.put("platform", "APP");
        param.put("params", GsonUtils.toJson(jsonStr));

        OkHttpRequest.post(RequestTag.PLAY_URL_TAG, RequestTag.PLAY_URL_TAG_URL, param, new PostCallBack() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    String str = response.body().string();
                    VideoInfo videoInfo = GsonUtils.getDefault().fromJson(str, VideoInfo.class);
                    if (videoInfo.getCode() == 200) {

                        VideoInfo.PlayInfo playInfo = videoInfo.getObj();

//                        VideoInfo videoInfo = baseInfo.getObj();
                        if (playInfo != null) {
                            initPlayer(playInfo);
                            info.setBrowseCount(++seeCount);
                            setSeeCount(seeCount);
                            isNeedUpdateList = true;
                        }
                    } else {
                        hideRefresh();
                        showToastSafe(getResources().getString(R.string.url_error));
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int errorCode) {

            }
        });


    }

    @UiThread
    void initPlayer(VideoInfo.PlayInfo playInfo) {
        playUrl = TextUtils.isEmpty(playInfo.getMainUrl()) ? playInfo.getCdnUrl() : playInfo.getMainUrl();
//        playUrl = "http://g3.letv.cn/vod/v2/MTIwLzMwLzk1L2xldHYtdXRzLzE0L3Zlcl8wMF8yMC0zMDA1MjQwMDktYXZjLTQ3MDIxMi1hYWMtMzIwMDEtODcyMTAwMC01NTcwMTk1MzQtMTg2OWJiZTcxOGE4ZDYyNTAwZmZkZGNkNjU5OWVlMTctMTQxOTIwNjM3NDc5NC5tcDQ=?b=510&mmsid=482168&tm=1441608410&key=dbf61bfa35e93159107689df92aca35d&key2=XXXX&platid=5&splatid=501&playid=0&tss=ios&vtype=13&cvid=458025769142&payff=0&pip=0839e03131f081f9d06ab69221fc4940&ctv=pc&m3v=1&termid=1&format=2&hwtype=un&ostype=Windows7&tag=letv&sign=letv&expect=3&p1=1&p2=10&p3=-&tn=0.654236747417599&pay=1&uinfo=AAAAAAAAAADTwl9ZEDxu16z6_MschMVurtJyUex5Qy2njLYBQp13-g==&iscpn=f9051&uuid=5A249CE95F8C37E802F7F6A27821A95E0D207EEB&token=null&uid=35978477&rateid=1000";
//        playUrl ="http://123.125.39.115/184/31/26/letv-test/20/ver_00_24-600040969-avc-1505921-aac-96000-60027-12132423-b46a7990fbc6de2fdb468c299b32fde4-1466678505391.mp4?crypt=3aa7f2e1817016&b=1615126&nlh=4096&nlt=60&bf=90&p2p=1&video_type=mp4&termid=0&tss=no&platid=14&splatid=1413&its=0&qos=4&fcheck=0&mltag=81&proxy=2099711127,2099711127,467476867&uid=1007439758.rp&keyitem=GOw_33YJAAbXYE-cnQwpfLlv_b2zAkYctFVqe5bsXQpaGNn3T1-vhw..&ntm=1466765400&nkey=3357b39f4ca1b101fea5db100fd3590b&nkey2=7fcecbcead96bb4f034c5149b33b651a&geo=CN-1-12-2&payff=0&cvid=0&playid=0&uip=123.125.26.242&vtype=51&tm=1466772549&mmsid=1000104&g3proxy_tm=1466754450&g3proxy_ercode=0&key=a8b0e4234e6efac9d94dd959b096617f&errc=0&gn=1302&vrtmcd=101&buss=81&cips=123.125.26.242";
       if (!TextUtils.isEmpty(playUrl)) {
           mPlayer.play(playUrl);
       }else {
           showToastSafe(getResources().getString(R.string.url_error2));
       }
    }

    /**
     * 播放视频
     */
    private void playVideo() {
        Logger.e("playVideo");
        EventBus.getDefault().post(new VideoIsOnUsingEvent(false, false));
        video_layout.setVisibility(View.VISIBLE);
        iv_play.setVisibility(View.GONE);
        iv_top_thumb.setVisibility(View.GONE);

    }

    private void getComment() {
        Map<String, String> params = new HashMap<>();
        params.put("id", videoID + "");
        Map<String, Object> jsonStr = new HashMap<>();
        jsonStr.put("page", currentPageIndex);
        jsonStr.put("rows", pageCount);
        jsonStr.put("source", 3);
        if (commentInfoList != null && commentInfoList.size() >= 1) {
            jsonStr.put("lastCommentid", commentInfoList.get(commentInfoList.size() - 1).get_id());
        }
        JSONObject localJSONObject = new JSONObject();
        Iterator localIterator = jsonStr.keySet().iterator();
        while (localIterator.hasNext()) {
            String str = (String) localIterator.next();
            try {
                localJSONObject.put(str, jsonStr.get(str));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        params.put("params", localJSONObject.toString());
        Logger.e(localJSONObject.toString());
        OkHttpRequest.post(RequestTag.COMMENT_TAG, RequestTag.COMMENT_TAG_URL, params, new PostCallBack() {
            @Override
            public void onFailure(Call call, IOException e) {
                lv_comment_list.loadingEnd();
                currentPageIndex();
            }

            @Override
            public void onResponse(Call call, Response response) {
                lv_comment_list.loadingEnd();
                try {
                    String str = response.body().string();
                    Logger.e(str + "     haha ");
                    BaseInfo<CommentInfo> commentInfoBaseInfo = GsonUtils.getDefault().fromJson(str, new TypeToken<BaseInfo<CommentInfo>>() {
                    }.getType());
                    if (commentInfoBaseInfo.getCode() == 200) {
                        if (currentPageIndex == 1) {
                            if (commentInfoList != null) {
                                commentInfoList.clear();
                            }
                            commentInfoList = commentInfoBaseInfo.getRows();
                            showOrHideNoComment();
                        } else {
                            commentInfoList.addAll(commentInfoBaseInfo.getRows());
                        }
                        if (commentInfoBaseInfo.getRows().size() < pageCount) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    lv_comment_list.stopLoadingMore();
                                }
                            });
                        }
                        initAdapter();
                    } else {
                        showToastSafe(commentInfoBaseInfo.getMsg());
                        currentPageIndex();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int errorCode) {
                lv_comment_list.loadingEnd();
                currentPageIndex();
            }
        });
    }

    @UiThread
    void showOrHideNoComment() {
        if(commentInfoList.size()==0){
            iv_norecord_home.setVisibility(View.VISIBLE);
        }else{
            iv_norecord_home.setVisibility(View.GONE);
        }
    }

    private void currentPageIndex() {
        if (currentPageIndex == 1) {
            return;
        }
        currentPageIndex--;
    }

    @UiThread
    void initAdapter() {

        if (commentListAdapter == null) {
            commentListAdapter = new CommentListAdapter(this, commentInfoList);
            lv_comment_list.setAdapter(commentListAdapter);
        } else {
            commentListAdapter.notifyDataSetChanged();
        }

    }

    private void initVideoSize() {
//        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) sb_play.getLayoutParams();
        int width = DisplayUtils.getScreenWidth(getApplicationContext());
        int height = DisplayUtils.getScreenHeight(getApplicationContext());
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) video_container.getLayoutParams();
        LinearLayout.LayoutParams sbPlayLayoutParams = (LinearLayout.LayoutParams) sb_play.getLayoutParams();
        int px = (int) DisplayUtils.dip2px(getApplicationContext(), 10);
        if (width<height) {//竖屏
            sb_play.setPadding(px,0,px,0);
            params.setMargins(px, 0, px, 0);
            params.width = width - (2 * px);
            params.height = width * 9 / 16;
        }else {//横屏
            height = height - 3*px;
            params.setMargins(0, 0, 0, 0);
            sb_play.setPadding(100, 0, 100, 0);
            if (width*9/16>height){
                params.width = height*16/9;
                int padding = (width-params.width)/2;
                sb_play.setPadding(padding,0,padding,0);
                params.height = height;
                params.gravity = Gravity.CENTER;
            }else {
                params.width = width;
                params.height = height*9/16;
                params.gravity = Gravity.CENTER;
            }
        }
        video_container.setLayoutParams(params);
    }

    @Override
    public void onPause() {
        super.onPause();
        OkHttpRequest.cancelAllCall();
        stopUpdateProgress();
    }

    @Click(R.id.bt_send_comment)
    void sendComment() {
        final String comment;
        comment = et_input_comment.getText().toString();
        if (LoginManager.isLogin(this)) {
            if (TextUtils.isEmpty(comment)) {
                showToastSafe(getResources().getString(R.string.input_null));
                return;
            } else {
                if (comment.length() >= 3 && comment.length() <= 140) {
                    senCommentRequest(comment);
                } else {
                    showToastSafe(getResources().getString(R.string.input_long));
                }
            }
        } else {
            LoginManager.login(this, new LoginManager.LoginCallBack() {
                @Override
                public void onSuccess(UserBean userBean) {
                    if (TextUtils.isEmpty(comment)) {
                        showToastSafe(getResources().getString(R.string.input_null));
                        return;
                    } else {
                        if (comment.length() >= 3 && comment.length() <= 140) {
                            senCommentRequest(comment);
                        } else {
                            showToastSafe(getResources().getString(R.string.input_long));
                        }
                    }
                }

                @Override
                public void onFailer() {
                    showToastSafe(getResources().getString(R.string.login_fail));
                }
            });
        }
    }


    private void senCommentRequest(final String comment) {
        Map<String, String> headers = new HashMap<>();
        Logger.e("TOKEN   : " + LoginManager.getSsoTk(this) + "    :   " + LoginManager.isLogin(this));
        headers.put("token", LoginManager.getSsoTk(this));
        Map<String, String> params = new HashMap<>();
        params.put("id", videoID + "");
        Map<String, Object> jsonStr = new HashMap<>();
        jsonStr.put("content", comment);
        jsonStr.put("source", 3);
        jsonStr.put("userId", LoginManager.getUid(this));
        JSONObject localJSONObject = new JSONObject();
        Iterator localIterator = jsonStr.keySet().iterator();
        while (localIterator.hasNext()) {
            String str = (String) localIterator.next();
            try {
                localJSONObject.put(str, jsonStr.get(str));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        params.put("params", localJSONObject.toString());

        OkHttpRequest.post(RequestTag.SEND_COMMENT_TAG, RequestTag.SEND_COMMENT_TAG_URL, headers, params, new PostCallBack() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    BaseInfo<Object> baseInfo = GsonUtils.getDefault().fromJson(response.body().string(), new TypeToken<BaseInfo<Object>>() {
                    }.getType());
                    if (baseInfo.getCode() == 105) {
                        showToastSafe(getResources().getString(R.string.black_list));
                    } else if (baseInfo.getCode() == 200) {
                        updateComment(comment);
                    } else {
                        showToastSafe(baseInfo.getMsg());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int errorCode) {

            }
        });
    }

    @UiThread
    void updateComment(String comment) {
        et_input_comment.setText("");
        CommentInfo commentInfo = new CommentInfo();
        commentInfo.setContent(comment);
        commentInfo.setVtime(getResources().getString(R.string.now));
        commentInfo.setUser(new User(LoginManager.getUid(DetailsActivity.this), LoginManager.getNicename(DetailsActivity.this), LoginManager.getHeadPicUrl(DetailsActivity.this), 0));
        commentInfoList.add(0, commentInfo);
        if (commentListAdapter == null) {
            commentListAdapter = new CommentListAdapter(this, commentInfoList);
            lv_comment_list.setAdapter(commentListAdapter);
        } else {
            commentListAdapter.notifyDataSetChanged();
        }
        commentCount++;
        isNeedUpdateList = true;
        info.setCommentCount(commentCount);
        setCommentCount(commentCount);
        iv_norecord_home.setVisibility(View.GONE);
    }

    @UiThread
    void initUpCount(int count) {
        String upStr = getString(R.string.activity_details_up_count, FormatUtils.formatCount(count));
        int index = upStr.indexOf(FormatUtils.formatCount(count));
        SpannableStringBuilder style = new SpannableStringBuilder(upStr);
        style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.lite_blue)), index, index + FormatUtils.formatCount(count).length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        tv_up_num.setText(style);
    }

    @Click(R.id.iv_play)
    void startPlayVideo() {
        Logger.e("sb_play padding -->| " + sb_play.getPaddingLeft() + "  :  " + sb_play.getPaddingRight() + "  height: " + sb_play.getHeight());
        if(!NetworkUtil.isConnected(this)) {
            showToastSafe(getResources().getString(R.string.net_error));
            return ;
        }

        if (!TextUtils.isEmpty(playUrl)){
            if (mPlayer.isPause()){
                mPlayer.playAfterPause();
                iv_play.setVisibility(View.GONE);
            }else {
                startRefresh();
                mPlayer.play(playUrl);
            }
            startUpdateProgress();
        }else {
            startRefresh();
            getPlayUrl();
        }
    }
    @Click(R.id.video_layout)
    void stopPlayVideo() {
        if (mPlayer.isPlaying()){
            mPlayer.pause();
            stopUpdateProgress();
            hideRefresh();
        }
    }

    @UiThread
    void startRefresh() {
        iv_play.setVisibility(View.GONE);
        iv_refresh.setVisibility(View.VISIBLE);
        AnimationUtils.rotate(iv_refresh);
    }

    @UiThread
    void hideRefresh() {
        iv_play.setVisibility(View.VISIBLE);
        iv_refresh.setVisibility(View.GONE);
        AnimationUtils.cancelAnmation(iv_refresh);
    }

    @Click(R.id.iv_up_icon)
    void updateUp() {
        if (LoginManager.isLogin(this)) {
            updateUpCount(videoID, LoginManager.getUid(this));
        } else {
            LoginManager.login(this, new LoginManager.LoginCallBack() {
                @Override
                public void onSuccess(UserBean userBean) {
                    getVideoDetail();
                    updateUpCount(videoID, LoginManager.getUid(DetailsActivity.this));
                }

                @Override
                public void onFailer() {
                    showToastSafe(getResources().getString(R.string.net_error));
                }
            });
        }
    }

    private void updateUpCount(int id, String userId) {

        Map<String, String> headers = new HashMap<>();
        headers.put("token", SpUtils.getInstance(this).getStringValue("ssoTk"));
        Map<String, String> params = new HashMap<>();
        params.put("id", id + "");
        params.put("userId", userId);
        OkHttpRequest.post(RequestTag.UP_TAG, RequestTag.UP_TAG_URL, headers, params, new PostCallBack() {
            @Override
            public void onFailure(Call call, IOException e) {
                Logger.e(" onFailure   : haha");

            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    Logger.e(response.body().string() + "    : haha");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                upCount++;
                setUpState(R.drawable.discover_fragment_uped_icon, false);
                initUpCount(upCount);
                isNeedUpdateList = true;
                info.setUpCount(upCount);
            }

            @Override
            public void onError(int errorCode) {
                Logger.e(" onError   : haha : " + errorCode);

            }
        });
    }

    @Click(R.id.navigation_bar_left_ib)
    void goBack() {
        if (isNeedUpdateList) {
            Logger.i("Discover" , "goback is excuse");
            EventBus.getDefault().post(new NeedUpdateDiscoverListEvent(info));
        }
        this.finish();
    }

    @Override
    public void onBackPressed() {
        goBack();
    }

    @Click(R.id.share_btn)
    void share() {
        tellServerVideoInfo();
    }
    /**
     * 通知后台详情页分享
     *
     */
    private void tellServerVideoInfo() {
        Map<String, String> param = new HashMap<>();
        param.put("id", videoID + "");
        param.put("userId", LoginManager.getUid(this));
        Log.i(TAG, GsonUtils.toJson(param));
        OkHttpRequest.post("shareResource", RequestTag.DETAIL_SHARE_URL, param, new PostCallBack() {
            @Override
            public void onFailure(Call call, IOException e) {
                showToastSafe(getResources().getString(R.string.return_fail));
                Log.i(TAG, "e : " + e);
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    String str = response.body().string();
                    Log.i(TAG, "str : " + str);
                    //DetaiVideoInfo detailVideoInfo = GsonUtils.getDefault().fromJson(str, DetaiVideoInfo.class);
                    if (response.code() == 200) {
                        createShareContentText(str);
                    } else {
                        showToastSafe(getResources().getString(R.string.get_url_error));
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int errorCode) {
                showToastSafe(getResources().getString(R.string.return_error));
            }
        });
    }
    private ShareContentText mShareContentText;
    /**
     * 创建分享内容bean
     *
     * @param responseString
     */
    private void createShareContentText(String responseString) {
        try {
            JSONObject jo = new JSONObject(responseString);
            String sharedUrl = jo.getString("obj");
            Logger.e(TAG, "文件分享后的H5 url ：" + sharedUrl);
            if (sharedUrl != null) {
                mShareContentText = new ShareContentText();
                mShareContentText.setTitle(title);
                mShareContentText.setContent(classifyId);
                mShareContentText.setURL(sharedUrl);
                mShareContentText.setLocation(location);
                if(type==0) {
                    mShareContentText.setImagResource(hightUrl);
                }else{
                    mShareContentText.setImagResource(thumbUrl);
                }
                goShareActivity();
            } else {
                showToastSafe(getResources().getString(R.string.return_url_error));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void goShareActivity() {
        Intent intent = new Intent(this, ShareActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("ShareImformation", mShareContentText);
        intent.putExtras(bundle);
        startActivity(intent);
    }



    @Override
    public void releaseResources() {
        super.releaseResources();
        OkHttpRequest.cancelAllCall();
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer=null;
        }
        // 取消Timer的所有子线程任务
        stopUpdateProgress();
    }

    private void stopUpdateProgress() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer=null;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            toolbar.setVisibility(View.GONE);
            rl_user_info.setVisibility(View.GONE);
            rl_text_content.setVisibility(View.GONE);
            ll__send_comment_content.setVisibility(View.GONE);
            lv_comment_list.setVisibility(View.GONE);
            v_line.setVisibility(View.GONE);
        }else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            toolbar.setVisibility(View.VISIBLE);
            rl_user_info.setVisibility(View.VISIBLE);
            rl_text_content.setVisibility(View.VISIBLE);
            ll__send_comment_content.setVisibility(View.VISIBLE);
            lv_comment_list.setVisibility(View.VISIBLE);
            v_line.setVisibility(View.VISIBLE);
        }
        initVideoSize();

        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onPrepared() {
        videoLength = mPlayer.getDuration();
        Logger.e("onPrepared : ---> " + videoLength + "    <-- ");
        sb_play.setMax(videoLength);
        sb_play.setVisibility(View.VISIBLE);
        sb_play.setEnabled(false);
        tv_play_position.setVisibility(View.VISIBLE);
        tv_play_position.setText("00:00/" + getTimeStr(mPlayer.getDuration()));
        startUpdateProgress();
    }

    private void startUpdateProgress() {
        if(mTimer==null) {
            mTimer = new Timer();
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    updateSeekbar(mPlayer.getCurrentPosition());
                    if (mPlayer.getDuration() == mPlayer.getCurrentPosition()) {
                        updateSeekbar(0);
                        mTimer.cancel();
                        return;
                    }
                }
            };
            mTimer.schedule(mTimerTask, 0, 10);
        }
    }


    @UiThread
    void updateSeekbar(int pos) {
        tv_play_position.setText(getTimeStr(pos)+"/"+getTimeStr(videoLength));
        sb_play.setProgress(pos);
    }

    @NonNull
    private String getTimeStr(int pos) {
        int time = (int) (pos/1000);
        String timeStr = "00:";
        if (time<10){
            timeStr+="0"+time;
        }else {
            timeStr+=time;
        }
        return timeStr;
    }

    @Override
    public void onFirstPic() {
        Logger.e("onFirstPic : ---> "+ "    <-- ");

    }

    @Override
    public void onBufferingStart() {
        Logger.e("onBufferingStart : ---> "+ "    <-- ");
    }

    @Override
    public void onBufferingUpdate(int percent) {
        Logger.e("onBufferingUpdate : ---> "+ "   <-- ");
    }

    @Override
    public void onBufferingEnd() {
        Logger.e("onBufferingEnd : ---> "+ "    <-- ");
    }

    @Override
    public void onComplete() {
        Logger.e("onComplete    onComplete");
        stopUpdateProgress();
        updateSeekbar(0);
        hideRefresh();
    }

    @Override
    public void onError(int what, String extra) {

    }

    @Override
    public void onInfo(int what, long extra) {
        Logger.e(TAG,"    onInfo   : ---> "+ "    <-- ");
        iv_play.setVisibility(View.GONE);
        iv_refresh.setVisibility(View.GONE);
        AnimationUtils.cancelAnmation(iv_refresh);
        iv_top_thumb.setVisibility(View.GONE);
    }

    @Override
    public void onBlock() {
        Logger.e(TAG,"    onBlock   : ---> "+ "    <-- ");
    }

    @Override
    public void onSeekComplete() {
        Logger.e(TAG,"    onSeekComplete   : ---> "+ "    <-- ");
    }



    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                //使EditText触发一次失去焦点事件
                v.setFocusable(false);
//                v.setFocusable(true); //这里不需要是因为下面一句代码会同时实现这个功能
                v.setFocusableInTouchMode(true);
                return true;
            }
        }
        return false;
    }

}
