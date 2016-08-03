package com.leautolink.leautocamera.ui.fragment;


import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.reflect.TypeToken;
import com.leautolink.leautocamera.R;
import com.leautolink.leautocamera.domain.respone.ActivityInfo;
import com.leautolink.leautocamera.domain.respone.BannerInfo;
import com.leautolink.leautocamera.domain.respone.BaseInfo;
import com.leautolink.leautocamera.domain.respone.DiscoverInfos;
import com.leautolink.leautocamera.domain.respone.VideoListInfo;
import com.leautolink.leautocamera.event.NeedUpdateDiscoverListEvent;
import com.leautolink.leautocamera.net.http.GsonUtils;
import com.leautolink.leautocamera.net.http.OkHttpRequest;
import com.leautolink.leautocamera.net.http.RequestTag.RequestTag;
import com.leautolink.leautocamera.net.http.httpcallback.GetCallBack;
import com.leautolink.leautocamera.net.http.httpcallback.PostCallBack;
import com.leautolink.leautocamera.ui.activity.DetailsActivity_;
import com.leautolink.leautocamera.ui.adapter.DiscoverListHeaderAdapter;
import com.leautolink.leautocamera.ui.base.BaseFragment;
import com.leautolink.leautocamera.ui.view.customview.IndexSwipeRefreshLayout;
import com.leautolink.leautocamera.ui.view.customview.MyListView;
import com.leautolink.leautocamera.utils.DisplayUtils;
import com.leautolink.leautocamera.utils.FormatUtils;
import com.leautolink.leautocamera.utils.Logger;
import com.leautolink.leautocamera.utils.SpUtils;
import com.leautolink.leautocamera.utils.glideutils.GlideCircleTransform;
import com.letv.loginsdk.bean.UserBean;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

@EFragment(R.layout.fragment_discover)
public class DiscoverFragment extends BaseFragment {

    @ViewById(R.id.srl_pull_refresh)
    IndexSwipeRefreshLayout swipeRefreshLayout;
    @ViewById(R.id.lv_listView)
    MyListView listView;

    private List<ActivityInfo> activityInfos;
    private List<VideoListInfo.ClouldVideoInfo> clouldVideoInfos;

    private List<DiscoverInfos> discoverInfoses;

    private ImageHandler handler = new ImageHandler(new WeakReference<DiscoverFragment>(this));

    private View headerView;
    private ViewPager viewPager;
    private MyAdapter myAdapter;
    private RadioGroup rb_indicate;

    private int classifyId = 1;

    private int currentPageIndex = 1;
    private UserBean mUserBean;
    private int pageCount = 10;
    private BannerInfo bannerInfo;

    private List<String> picUrls;
    private List<String> jumpUrls;

    private final static int PULL = 0 ;
    private final static int UP = 1 ;
    private boolean isFirst = true;
    private int clickItemIndex = -1;

    private boolean isFragmentVisible;

    private Rect viewpageRect;


    @AfterViews
    void init() {
        activityInfos = new ArrayList<>();
        clouldVideoInfos = new ArrayList<>();
        discoverInfoses = new ArrayList<>();
        if (1 == classifyId) {
            headerView = inflater.inflate(R.layout.discover_fragment_list_header, null);
            viewPager = (ViewPager) headerView.findViewById(R.id.viewPager);
            rb_indicate = (RadioGroup) headerView.findViewById(R.id.rb_indicate);
            //开始轮播效果
//        handler.sendEmptyMessageDelayed(ImageHandler.MSG_UPDATE_IMAGE, ImageHandler.MSG_DELAY);

            listView.addHeaderView(headerView);
            listView.touchView(headerView);
            viewpageRect = listView.getTouchViewRect();
//            swipeRefreshLayout

            getBannerInfo();
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPageIndex = 1;
                getListInfo(PULL);
            }
        });

        listView.setOnItemClickListener(new ListViewOnItemClickListener());
        listView.initBottomView();
        listView.setMyPullUpListViewCallBack(new MyListView.MyPullUpListViewCallBack() {
            @Override
            public void scrollBottomState() {
//                showToastSafe("开始加载");
                currentPageIndex++;
                getListInfo(UP);
            }
        });


        Logger.e("classid  : " + classifyId + "   " + "    init");

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isFragmentVisible = isVisibleToUser;
        if (isVisibleToUser) {
            if (!isFirst||classifyId != 1) {
                getListInfo(PULL);
            }
        } else {

            //相当于Fragment的onPause
        }
    }


    private class ListViewOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            int count = listView.getHeaderViewsCount();
            clickItemIndex = position - count;
            DiscoverInfos discoverInfos = discoverInfoses.get(clickItemIndex);
            DetailsActivity_.intent(DiscoverFragment.this).videoID(discoverInfos.getId()).upCount(discoverInfos.getUpCount()).commentCount(discoverInfos.getCommentCount())
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
    }

    public void setClassifyId(int classifyId) {
        this.classifyId = classifyId;
    }


    public void onEventMainThread(NeedUpdateDiscoverListEvent event){
        Logger.i("Discover", "index -->| " + clickItemIndex + " size -->| " + discoverInfoses.size());
        if (clickItemIndex>=0&&discoverInfoses!=null&&discoverInfoses.size()>0&&isFragmentVisible) {
            DiscoverInfos info = event.getDiscoverInfos();
            Logger.i("Discover", "index -->| " + clickItemIndex + " size -->| " + discoverInfoses.size()  +  " seeCount -->| " + info.getBrowseCount());
            discoverInfoses.get(clickItemIndex).setBrowseCount(info.getBrowseCount());
            discoverInfoses.get(clickItemIndex).setCommentCount(info.getCommentCount());
            discoverInfoses.get(clickItemIndex).setUpCount(info.getUpCount());
            myAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 获取点播List
     */
    private void getListInfo(int pullOrUp) {

        Map<String, String> params = new HashMap<>();
        JSONObject jsonObject = new JSONObject();
        try {
//            jsonObject.put("page", currentPageIndex );
            jsonObject.put("rows", pageCount);
            jsonObject.put("classifyId", classifyId);
            if (UP == pullOrUp) {
                if (discoverInfoses != null && discoverInfoses.size() >= 1) {
                    jsonObject.put("lastResourceId", discoverInfoses.get(discoverInfoses.size() - 1).getId());
                    jsonObject.put("lastResourceLevel", discoverInfoses.get(discoverInfoses.size() - 1).getLevel());
                    jsonObject.put("lastResourceUploadDate", discoverInfoses.get(discoverInfoses.size() - 1).getUploadDate());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Logger.e("Index  : " + currentPageIndex) ;
        Logger.e("classifyId  : " + jsonObject.toString()) ;
        params.put("params",jsonObject.toString());
        OkHttpRequest.post(RequestTag.TAB_DETAIL_TAG, RequestTag.TAB_DETAIL_URL, params, new PostCallBack() {
            @Override
            public void onFailure(Call call, IOException e) {
                listView.loadingEnd();
                currentPageIndex();
                stopSwipRefresh();
            }

            @Override
            public void onResponse(Call call, Response response) {
                listView.loadingEnd();
                stopSwipRefresh();
                try {
                    String res = response.body().string();
                    Logger.e(res + "  --------");

                    BaseInfo<DiscoverInfos> discoverInfosBaseInfo = GsonUtils.getDefault().fromJson(res, new TypeToken<BaseInfo<DiscoverInfos>>() {
                    }.getType());

                    if (null != discoverInfosBaseInfo && discoverInfosBaseInfo.getCode() == 200) {
                        if (currentPageIndex == 1) {
                            if (discoverInfoses != null) {
                                discoverInfoses.clear();
                            }
                            discoverInfoses = discoverInfosBaseInfo.getRows();
                        } else {
                            discoverInfoses.addAll(discoverInfosBaseInfo.getRows());
                        }
                        if (discoverInfosBaseInfo.getRows().size() < pageCount) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    listView.stopLoadingMore();

                                }
                            });
                        }
                        initList();
                        isFirst = false;
                    } else {
                        showToastSafe(discoverInfosBaseInfo.getMsg());
                        currentPageIndex();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(int errorCode) {
                stopSwipRefresh();
                listView.loadingEnd();
                currentPageIndex();
                Logger.e(errorCode + "  --------");
            }
        });

    }

    private void currentPageIndex() {
        if (currentPageIndex==1){
            return;
        }
        currentPageIndex--;
    }

    @UiThread
    void stopSwipRefresh(){
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }
    @UiThread
    void initList() {

        if (myAdapter == null) {
            myAdapter = new MyAdapter();
            listView.setAdapter(myAdapter);
        } else {
            myAdapter.notifyDataSetChanged();
        }
    }
//    private void addDianboToLiving(VideoListInfo videoListInfo){
//        List<VideoListInfo.ClouldVideoInfo> clouldVideoInfos = videoListInfo.getData();
//
//        for(VideoListInfo.ClouldVideoInfo clouldVideoInfo: clouldVideoInfos){
//            ActivityInfo activityInfo = new ActivityInfo()
//        }
//    }


    @UiThread
    void refreshList() {
        myAdapter.notifyDataSetChanged();
    }

    @UiThread
    void stopRefresh() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void releaseResources() {

    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return discoverInfoses.size();
        }

        @Override
        public Object getItem(int position) {

            return discoverInfoses.get(position);

        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.discover_fragment_list_item, null);
                holder = new ViewHolder();
                holder.backgroundImage = (ImageView) convertView.findViewById(R.id.iv_discover_image);
                initVideoSize(holder.backgroundImage);//设置背景的比例为16:9
                holder.iv_head_icon = (ImageView) convertView.findViewById(R.id.iv_head_icon);
                holder.name = (TextView) convertView.findViewById(R.id.tv_name);
                holder.tv_location = (TextView) convertView.findViewById(R.id.tv_location);
                holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                holder.tv_browse_num = (TextView) convertView.findViewById(R.id.tv_browse_num);
                holder.tv_up_num = (TextView) convertView.findViewById(R.id.tv_up_num);
                holder.tv_comment_num = (TextView) convertView.findViewById(R.id.tv_comment_num);
                holder.iv_video_flag = (ImageView) convertView.findViewById(R.id.iv_video_flag);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final DiscoverInfos discoverInfo = discoverInfoses.get(position);
            Glide.with(DiscoverFragment.this).load(discoverInfo.getThumbnail())
                    .placeholder(R.drawable.img_default)
                    .crossFade()
                    .into(holder.backgroundImage);
            Glide.with(DiscoverFragment.this).load(discoverInfo.getUser().getPhoto())
                    .placeholder(R.drawable.header_defaut_icon).transform(new GlideCircleTransform(mActivity))
                    .crossFade()
                    .into(holder.iv_head_icon);
            holder.name.setText(discoverInfo.getUser().getUsername());
            holder.tv_location.setText(discoverInfo.getLocation());
            holder.tv_title.setText(discoverInfo.getTitle());
            holder.tv_browse_num.setText(FormatUtils.formatCount(discoverInfo.getBrowseCount()));
            holder.tv_up_num.setText(FormatUtils.formatCount(discoverInfo.getUpCount()));
            holder.tv_comment_num.setText(FormatUtils.formatCount(discoverInfo.getCommentCount()));
            holder.tv_up_num.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    if (SpUtils.getInstance(mActivity).getBoolValue("islogin")) {
//                        updateUpCount(discoverInfo.getId(), SpUtils.getInstance(mActivity).getStringValue("uid"),position);
//                    } else {
//                        new LoginSdk().login(mActivity, new LoginSuccessCallBack() {
//                            @Override
//                            public void loginSuccessCallBack(LoginSuccessState loginSuccessState, LetvBaseBean bean) {
//                                if (loginSuccessState == LoginSuccessState.LOGINSUCCESS) {
//                                    //登录成功
//                                    mUserBean = (UserBean) bean;
//                                    saveUserInfo();
//                                }
//                            }
//                        });
//                    }
                }
            });

            if (discoverInfo.getType() == 0){
                holder.iv_video_flag.setVisibility(View.GONE);
            }else {
                holder.iv_video_flag.setVisibility(View.VISIBLE);
            }

            return convertView;
        }

        public void updateView(int itemIndex) {
            int headerSize = listView.getHeaderViewsCount();
            //得到第一个可显示控件的位置，
            int visiblePosition = listView.getFirstVisiblePosition()-headerSize;
            //只有当要更新的view在可见的位置时才更新，不可见时，跳过不更新
            if (itemIndex - visiblePosition >= 0) {
                //得到要更新的item的view
                View view = listView.getChildAt(itemIndex - visiblePosition);
                ViewHolder holder = (ViewHolder) view.getTag();
                int num = Integer.parseInt(holder.tv_up_num.getText().toString());
                num++;
                holder.tv_up_num.setText(num + "");
                discoverInfoses.get(itemIndex).setUpCount(num);
            }
        }
    }
    private void initVideoSize(View view) {
        int width = DisplayUtils.getScreenWidth(mActivity.getApplicationContext());
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
//        int px = (int) DisplayUtils.dip2px(mActivity.getApplicationContext(), 10);
//        params.setMargins(px, 0, px, 0);
        params.width = width;
        params.height = width * 9 / 16;
        view.setLayoutParams(params);
    }

    public static class ViewHolder {
        ImageView backgroundImage;
        ImageView iv_video_flag;
        ImageView iv_head_icon;
        TextView name;
        TextView tv_location;
        TextView tv_title;
        TextView tv_browse_num;
        TextView tv_up_num;
        TextView tv_comment_num;


    }

    private static class ImageHandler extends Handler {

        /**
         * 请求更新显示的View。
         */
        protected static final int MSG_UPDATE_IMAGE = 1;
        /**
         * 请求暂停轮播。
         */
        protected static final int MSG_KEEP_SILENT = 2;
        /**
         * 请求恢复轮播。
         */
        protected static final int MSG_BREAK_SILENT = 3;
        /**
         * 记录最新的页号，当用户手动滑动时需要记录新页号，否则会使轮播的页面出错。
         * 例如当前如果在第一页，本来准备播放的是第二页，而这时候用户滑动到了末页，
         * 则应该播放的是第一页，如果继续按照原来的第二页播放，则逻辑上有问题。
         */
        protected static final int MSG_PAGE_CHANGED = 4;

        //轮播间隔时间
        protected static final long MSG_DELAY = 2000;

        //使用弱引用避免Handler泄露.这里的泛型参数可以不是Activity，也可以是Fragment等
        private WeakReference<DiscoverFragment> weakReference;
        private int currentItem = 0;
        private boolean isFirstSetCurrentPage = true;

        protected ImageHandler(WeakReference<DiscoverFragment> wk) {
            weakReference = wk;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//            Logger.e("receive message " + msg.what);
            DiscoverFragment activity = weakReference.get();
            if (activity == null) {
                //Activity已经回收，无需再处理UI了
                return;
            }
            //检查消息队列并移除未发送的消息，这主要是避免在复杂环境下消息出现重复等问题。
            if (activity.handler.hasMessages(MSG_UPDATE_IMAGE)) {
                activity.handler.removeMessages(MSG_UPDATE_IMAGE);
            }
            switch (msg.what) {
                case MSG_UPDATE_IMAGE:
                    currentItem++;
                    activity.viewPager.setCurrentItem(currentItem);
                    //准备下次播放
                    activity.handler.sendEmptyMessageDelayed(MSG_UPDATE_IMAGE, MSG_DELAY);
                    break;
                case MSG_KEEP_SILENT:
                    //只要不发送消息就暂停了
                    break;
                case MSG_BREAK_SILENT:
                    activity.handler.sendEmptyMessageDelayed(MSG_UPDATE_IMAGE, MSG_DELAY);
                    break;
                case MSG_PAGE_CHANGED:
                    //记录当前的页号，避免播放的时候页面显示不正确。
                    currentItem = msg.arg1;
                    if (isFirstSetCurrentPage) {
                        isFirstSetCurrentPage = false;
                        activity.handler.sendEmptyMessageDelayed(MSG_UPDATE_IMAGE, MSG_DELAY);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @UiThread
    void saveUserInfo(){
        SpUtils.getInstance(mActivity).setValue("islogin", true);
        inflateLoginedInfos();
    }

    @UiThread
    void updateItem(int pos){
        myAdapter.updateView(pos);
    }

    private void inflateLoginedInfos() {
        String headPicUrl = null;
        String userNickName = null;
        if (mUserBean != null) {
            headPicUrl = mUserBean.getPicture200x200();
            userNickName = mUserBean.getNickname();
            SpUtils.getInstance(mActivity).setValue("ssoTk", mUserBean.getSsoTK());
            SpUtils.getInstance(mActivity).setValue("uid", mUserBean.getUid());
            SpUtils.getInstance(mActivity).setValue("headPicUrl", headPicUrl);
            SpUtils.getInstance(mActivity).setValue("userName", userNickName);
        } else {//没有联网的时候从缓存中读取数据
            headPicUrl = SpUtils.getString(mActivity, "headPicUrl", "");
            userNickName = SpUtils.getString(mActivity, "userName", "");
        }
    }
    private void  updateUpCount(int id ,String userId , final int pos ){

        Map<String , String> headers = new HashMap<>();
        headers.put("token", SpUtils.getInstance(mActivity).getStringValue("ssoTk"));
        Map<String , String> params = new HashMap<>();
        params.put("id" , id+"");
        params.put("userId" , userId);
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
                updateItem(pos);
            }

            @Override
            public void onError(int errorCode) {
                Logger.e(" onError   : haha : " + errorCode);

            }
        });
    }

    private void getBannerInfo(){
        OkHttpRequest.getString(RequestTag.BANNER_TAG, RequestTag.BANNER_TAG_URL, new GetCallBack() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Object response) {
                String res = (String) response;
                bannerInfo = GsonUtils.getDefault().fromJson(res, BannerInfo.class);

                List<BannerInfo.BannerData.BannerContent> bannerContents = bannerInfo.getData().getBlockContent();
                if (picUrls == null){
                    picUrls = new ArrayList<String>();
                }else {
                    picUrls .clear();
                }
                if (jumpUrls == null){
                    jumpUrls = new ArrayList<String>();
                }else {
                    jumpUrls .clear();
                }
                for (BannerInfo.BannerData.BannerContent banner :
                        bannerContents) {
                    picUrls.add(banner.getMobilePic());
                    jumpUrls.add(banner.getUrl());
                }
                initBannerAdapter();
            }

            @Override
            public void onError(String error) {

            }
        });
    }
    @UiThread
    void initBannerAdapter() {
        for (int i = 0; i < picUrls.size(); i++) {
            inflater.inflate(R.layout.indicate_radio_btn, rb_indicate);
        }
        viewPager.setAdapter(new DiscoverListHeaderAdapter(mActivity, this, picUrls,jumpUrls));
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            //配合Adapter的currentItem字段进行设置。
            @Override
            public void onPageSelected(int arg0) {
                ((RadioButton) rb_indicate.getChildAt(arg0 % picUrls.size())).setChecked(true);
                handler.sendMessage(Message.obtain(handler, ImageHandler.MSG_PAGE_CHANGED, arg0, 0));
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            //覆写该方法实现轮播效果的暂停和恢复
            @Override
            public void onPageScrollStateChanged(int arg0) {
                switch (arg0) {
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        handler.sendEmptyMessage(ImageHandler.MSG_KEEP_SILENT);
                        break;
                    case ViewPager.SCROLL_STATE_IDLE:
                        handler.sendEmptyMessageDelayed(ImageHandler.MSG_UPDATE_IMAGE, ImageHandler.MSG_DELAY);
                        break;
                    default:
                        break;
                }
            }
        });
        viewPager.setCurrentItem((Integer.MAX_VALUE / 2) - 3);//默认在中间，使用户看不到边界
        getListInfo(PULL);
    }





}