package com.leautolink.leautocamera.ui.activity;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.leautolink.leautocamera.R;
import com.leautolink.leautocamera.domain.respone.BaseInfo;
import com.leautolink.leautocamera.domain.respone.DiscoverInfos;
import com.leautolink.leautocamera.net.http.GsonUtils;
import com.leautolink.leautocamera.net.http.OkHttpRequest;
import com.leautolink.leautocamera.net.http.RequestTag.RequestTag;
import com.leautolink.leautocamera.net.http.httpcallback.PostCallBack;
import com.leautolink.leautocamera.ui.adapter.DiscoverListAdapter;
import com.leautolink.leautocamera.ui.base.BaseActivity;
import com.leautolink.leautocamera.ui.view.customview.MyListView;
import com.leautolink.leautocamera.utils.Logger;
import com.leautolink.leautocamera.utils.LoginManager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;


@EActivity(R.layout.activity_share_and_up)
public class ShareAndUpActivity extends BaseActivity {

    public final static int TYPE_SHARE = 1;

    public final static int TYPE_UP = 2;

    @ViewById(R.id.navigation_bar_title)
    TextView  navigation_bar_title;

    @ViewById(R.id.srl_pull_refresh)
    SwipeRefreshLayout srl_pull_refresh;

    @ViewById(R.id.lv_listView)
    MyListView lv_listView;

    private DiscoverListAdapter discoverListAdapter;

    @Extra
    int type;

    private int currentPage = 1;
    private int pageCount = 10;

    private String url;
    private String tag;
    private List<DiscoverInfos> discoverInfoses;

    @AfterViews
    void init(){
        if (type == TYPE_SHARE){
            navigation_bar_title.setText(getResources().getString(R.string.myshare));
            url = RequestTag.MY_SHARE_TAG_URL;
            tag = RequestTag.MY_SHARE_TAG;
        }else {
            navigation_bar_title.setText(getResources().getString(R.string.mycommet));
            url = RequestTag.MY_UP_TAG_URL;
            tag = RequestTag.MY_UP_TAG;
        }

        getShareOrUpInfos();

        srl_pull_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 1;
                getShareOrUpInfos();
            }
        });
        lv_listView.setMyPullUpListViewCallBack(new MyListView.MyPullUpListViewCallBack() {
            @Override
            public void scrollBottomState() {
                currentPage++;
                getShareOrUpInfos();
            }
        });

        lv_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                DiscoverInfos discoverInfos = discoverInfoses.get(position);
                DetailsActivity_.intent(ShareAndUpActivity.this).videoID(discoverInfos.getId()).upCount(discoverInfos.getUpCount()).commentCount(discoverInfos.getCommentCount())
                        .seeCount(discoverInfos.getBrowseCount())
                        .userIcon(discoverInfos.getUser().getPhoto())
                        .userName(discoverInfos.getUser().getUsername())
                        .thumbUrl(discoverInfos.getThumbnail())
                        .hightUrl(discoverInfos.getImageurlDetail())
                        .type(discoverInfos.getType())
                        .title(discoverInfos.getTitle())
                        .location(discoverInfos.getLocation())
                        .start();
            }
        });
        lv_listView.initBottomView();
    }


    @UiThread
    void stopRefresh(){
        if (srl_pull_refresh.isRefreshing()) {
            srl_pull_refresh.setRefreshing(false);
            OkHttpRequest.cancelAllCall();
        }
    }

    private void getShareOrUpInfos(){
        Map<String,String> headers = new HashMap<>();
        headers.put("token", LoginManager.getSsoTk(this));
        Map<String,Object> param = new HashMap<>();
        param.put("page", currentPage);
        param.put("rows", pageCount);
        param.put("userId", LoginManager.getUid(this));

        String json = GsonUtils.getDefault().toJson(param);
        Logger.e(LoginManager.getSsoTk(this) + "  :  " + currentPage + "   :  " + json);

        Map<String,String> params = new HashMap<>();
        params.put("params", json);



        OkHttpRequest.post(tag, url, headers, params, new PostCallBack() {
            @Override
            public void onFailure(Call call, IOException e) {
                stopRefresh();
                lv_listView.loadingEnd();
            }

            @Override
            public void onResponse(Call call, Response response) {
                stopRefresh();
                lv_listView.loadingEnd();
                try {
                    String jsonStr = response.body().string();
                    Logger.e(jsonStr);
                    BaseInfo<DiscoverInfos> discoverInfosBaseInfo = GsonUtils.getDefault().fromJson(jsonStr, new TypeToken<BaseInfo<DiscoverInfos>>() {
                    }.getType());

                    if (null != discoverInfosBaseInfo && discoverInfosBaseInfo.getCode() == 200) {
                        if (currentPage == 1) {
                            if (discoverInfoses != null) {
                                discoverInfoses.clear();
                            }else {
                                discoverInfoses = new ArrayList<DiscoverInfos>();
                            }

                        }
                        discoverInfoses.addAll(discoverInfosBaseInfo.getRows());
                        Logger.e("size -->| "+discoverInfosBaseInfo.getRows().size());
                        if (discoverInfosBaseInfo.getRows().size() < pageCount) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    lv_listView.stopLoadingMore();
                                }
                            });
                        }
                        initAdapter(discoverInfoses);
                    } else {
                        showToastSafe(discoverInfosBaseInfo.getMsg());
                        currentPage();
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int errorCode) {
                stopRefresh();
                lv_listView.loadingEnd();
                currentPage();
            }
        });
    }
    @UiThread
    void initAdapter(List<DiscoverInfos> rows) {
        if (discoverListAdapter==null){
            discoverListAdapter = new DiscoverListAdapter(this,rows,lv_listView,null);
            lv_listView.setAdapter(discoverListAdapter);
        }else {
            discoverListAdapter.notifyDataSetChanged();
        }
    }

    private void currentPage() {
        if (currentPage==1){
            return;
        }
        currentPage--;
    }

    @Click(R.id.navigation_bar_left_ib)
    void goBack(){
        finish();
    }
}
