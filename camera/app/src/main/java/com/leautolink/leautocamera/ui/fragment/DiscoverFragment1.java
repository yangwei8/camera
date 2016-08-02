package com.leautolink.leautocamera.ui.fragment;


import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.leautolink.leautocamera.R;
import com.leautolink.leautocamera.domain.request.AllActivityRequest;
import com.leautolink.leautocamera.domain.request.BaseVideoRequest;
import com.leautolink.leautocamera.domain.respone.ActivityInfo;
import com.leautolink.leautocamera.domain.respone.VideoListInfo;
import com.leautolink.leautocamera.net.http.GsonUtils;
import com.leautolink.leautocamera.net.http.OkHttpRequest;
import com.leautolink.leautocamera.net.http.httpcallback.GetCallBack;
import com.leautolink.leautocamera.ui.base.BaseFragment;
import com.leautolink.leautocamera.ui.view.customview.MyListView;
import com.leautolink.leautocamera.utils.Logger;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

@EFragment(R.layout.fragment_discover)
public class DiscoverFragment1 extends BaseFragment {

    @ViewById(R.id.srl_pull_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @ViewById(R.id.lv_listView)
    MyListView listView;

    private List<ActivityInfo> activityInfos;
    private List<VideoListInfo.ClouldVideoInfo> clouldVideoInfos;
    private ImageHandler handler = new ImageHandler(new WeakReference<DiscoverFragment1>(this));

    private View headerView;
    private ViewPager viewPager;
    private MyAdapter myAdapter;
    private RadioGroup rb_indicate;

    @AfterViews
    void init() {
        activityInfos = new ArrayList<>();
        clouldVideoInfos = new ArrayList<>();
        getActivityInfo();
        headerView = inflater.inflate(R.layout.discover_fragment_list_header, null);
        viewPager = (ViewPager) headerView.findViewById(R.id.viewPager);
        rb_indicate = (RadioGroup) headerView.findViewById(R.id.rb_indicate);
        ImageView imageView1 = (ImageView) inflater.inflate(R.layout.image_item, null);
        ImageView imageView2 = (ImageView) inflater.inflate(R.layout.image_item, null);
        ImageView imageView3 = (ImageView) inflater.inflate(R.layout.image_item, null);
        ArrayList<ImageView> imageViews = new ArrayList<>();
        imageView1.setImageResource(R.drawable.banner_01);
        imageViews.add(imageView1);
        imageView2.setImageResource(R.drawable.banner_02);
        imageViews.add(imageView2);
        imageView3.setImageResource(R.drawable.banner_03);
        imageViews.add(imageView3);
        for (int i = 0; i < imageViews.size(); i++) {
            inflater.inflate(R.layout.indicate_radio_btn, rb_indicate);
        }
//        viewPager.setAdapter(new DiscoverListHeaderAdapter(mActivity,this,imageViews));
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            //配合Adapter的currentItem字段进行设置。
            @Override
            public void onPageSelected(int arg0) {
                ((RadioButton) rb_indicate.getChildAt(arg0 % 3)).setChecked(true);
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
        viewPager.setCurrentItem(Integer.MAX_VALUE / 2);//默认在中间，使用户看不到边界
        //开始轮播效果
//        handler.sendEmptyMessageDelayed(ImageHandler.MSG_UPDATE_IMAGE, ImageHandler.MSG_DELAY);

        listView.addHeaderView(headerView);
        myAdapter = new MyAdapter();
        listView.setAdapter(myAdapter);
        listView.touchView(headerView);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getActivityInfo();
            }
        });

        listView.setOnItemClickListener(new ListViewOnItemClickListener());

    }


    private class ListViewOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position >= 1) {
                if (position > activityInfos.size()) {
                    VideoListInfo.ClouldVideoInfo clouldVideoInfo = clouldVideoInfos.get(position - 1 - activityInfos.size());
                    if (("10").equals(clouldVideoInfo.getStatus())) {
//                        ShowLiveActivity_.intent(mActivity).activityId(clouldVideoInfo.getVideo_unique()).activityName(clouldVideoInfo.getVideo_name()).isLiving(false).start();
                    } else {
                        showToastSafe(getResources().getString(R.string.video_play_error));
                    }
                } else {
                    ActivityInfo activityInfo = activityInfos.get(position - 1);
//                    ShowLiveActivity_.intent(mActivity).activityId(activityInfo.getActivityId()).activityName(activityInfo.getActivityName()).isLiving(true).start();
                }

            }
        }
    }

    /**
     * 获取点播List
     *
     * @param infos
     */
    private void getVideoInfo(final List<ActivityInfo> infos) {
//       BaseUrlRequest baseUrlRequest = new BaseUrlRequest();
//
//       OkHttpRequest.get(baseUrlRequest.getUrl(), new GetCallBack() {
//           @Override
//           public void onFailure(Call call, IOException e) {
//
//           }
//
//           @Override
//           public void onResponse(Call call, Response response) {
//               try {
//                   ListVideoUrlInfo listVideoUrlInfo = GsonUtils.fromJson(response.body().string(), ListVideoUrlInfo.class);
//                   Logger.i("ssss",listVideoUrlInfo.getData().getVideo_list().getVideo_1().getMain_url());
//               } catch (IOException e) {
//                   e.printStackTrace();
//               }
//           }
//
//           @Override
//           public void onError(ErrorInfo errorInfo) {
//
//           }
//       });


        BaseVideoRequest videoRequest = new BaseVideoRequest("video.list");
        OkHttpRequest.getString("getVideoInfo", videoRequest.getUrl(), new GetCallBack() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Object response) {
                VideoListInfo videoListInfo = GsonUtils.fromJson((String) response, VideoListInfo.class);
                if (clouldVideoInfos != null) {
                    clouldVideoInfos.clear();
                }
                if (activityInfos != null) {
                    activityInfos.clear();
                }
                activityInfos = infos;
                clouldVideoInfos = videoListInfo.getData();
                refreshList();
            }

            @Override
            public void onError(String error) {

            }
        });

    }
//    private void addDianboToLiving(VideoListInfo videoListInfo){
//        List<VideoListInfo.ClouldVideoInfo> clouldVideoInfos = videoListInfo.getData();
//
//        for(VideoListInfo.ClouldVideoInfo clouldVideoInfo: clouldVideoInfos){
//            ActivityInfo activityInfo = new ActivityInfo()
//        }
//    }

    /**
     * 获取直播List
     */
    private void getActivityInfo() {
        AllActivityRequest request = new AllActivityRequest("letv.cloudlive.activity.search", "1");
        Logger.e(request.getUrl());
        OkHttpRequest.getString("getActivityInfo", request.getUrl(), new GetCallBack() {
            @Override
            public void onFailure(Call call, IOException e) {
                stopRefresh();
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Object response) {
                stopRefresh();
                Gson gson = new Gson();
                String json = (String) response;
                Logger.e(json);
                if (activityInfos != null) {
                    activityInfos.clear();
                }
                List<ActivityInfo> infos = gson.fromJson(json,
                        new TypeToken<List<ActivityInfo>>() {
                        }.getType());
//                    refreshList();
                getVideoInfo(infos);
            }

            @Override
            public void onError(String error) {
                stopRefresh();
            }
        });
    }


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
            return activityInfos.size() + clouldVideoInfos.size();
        }

        @Override
        public Object getItem(int position) {
            if (position >= activityInfos.size()) {
                return clouldVideoInfos.get(position - activityInfos.size());
            } else {
                return activityInfos.get(position);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.discover_fragment_list_item, null);
                holder = new ViewHolder();
                holder.backgroundImage = (ImageView) convertView.findViewById(R.id.iv_discover_image);
                holder.name = (TextView) convertView.findViewById(R.id.tv_name);
                convertView.setTag(holder);//绑定ViewHolder对象
            } else {
                holder = (ViewHolder) convertView.getTag();//取出ViewHolder对象                  }
            }
            if (position >= activityInfos.size()) {
                VideoListInfo.ClouldVideoInfo clouldVideoInfo = clouldVideoInfos.get(position - activityInfos.size());
                String imagePath = "";
                if (!TextUtils.isEmpty(clouldVideoInfo.getInit_pic())) {
                    imagePath = clouldVideoInfo.getInit_pic();
                } else {
                    imagePath = clouldVideoInfo.getImg();
                }
                Glide.with(DiscoverFragment1.this).load(imagePath)
                        .placeholder(R.drawable.img_default)
                        .crossFade()
                        .into(holder.backgroundImage);

                holder.name.setText(clouldVideoInfo.getVideo_name().replace(".MP4", ""));
            } else {
                ActivityInfo activityInfo = activityInfos.get(position);
                Glide.with(DiscoverFragment1.this).load(activityInfo.getCoverImgUrl())
                        .placeholder(R.drawable.img_default)
                        .crossFade()
                        .into(holder.backgroundImage);
                holder.name.setText(activityInfo.getActivityName());
            }

            return convertView;
        }
    }

    public static class ViewHolder {
        ImageView backgroundImage;
        TextView name;
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
        protected static final long MSG_DELAY = 5000;

        //使用弱引用避免Handler泄露.这里的泛型参数可以不是Activity，也可以是Fragment等
        private WeakReference<DiscoverFragment1> weakReference;
        private int currentItem = 0;
        private boolean isFirstSetCurrentPage = true;

        protected ImageHandler(WeakReference<DiscoverFragment1> wk) {
            weakReference = wk;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//            Logger.e("receive message " + msg.what);
            DiscoverFragment1 activity = weakReference.get();
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


}