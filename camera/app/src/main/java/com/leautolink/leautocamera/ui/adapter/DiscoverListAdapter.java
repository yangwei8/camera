package com.leautolink.leautocamera.ui.adapter;

import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.leautolink.leautocamera.R;
import com.leautolink.leautocamera.domain.respone.DiscoverInfos;
import com.leautolink.leautocamera.net.http.OkHttpRequest;
import com.leautolink.leautocamera.net.http.RequestTag.RequestTag;
import com.leautolink.leautocamera.net.http.httpcallback.PostCallBack;
import com.leautolink.leautocamera.ui.base.BaseActivity;
import com.leautolink.leautocamera.ui.base.BaseFragment;
import com.leautolink.leautocamera.utils.Logger;
import com.leautolink.leautocamera.utils.LoginManager;
import com.leautolink.leautocamera.utils.glideutils.GlideCircleTransform;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by lixinlei on 16/6/30.
 */
public class DiscoverListAdapter  extends BaseAdapter{

    private List<DiscoverInfos> discoverInfoses;

    private BaseActivity context;

    private BaseFragment fragment;

    private ListView listView;

    private Handler handler;

    public DiscoverListAdapter(BaseActivity context , List<DiscoverInfos> discoverInfoses , ListView listView , BaseFragment fragment) {
        this.discoverInfoses = discoverInfoses;
        this.context = context;
        this.fragment = fragment;
        this.listView = listView;
        this.handler = new Handler(context.getMainLooper());
    }

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
            convertView = View.inflate(context,R.layout.discover_fragment_list_item, null);
            holder = new ViewHolder();
            holder.backgroundImage = (ImageView) convertView.findViewById(R.id.iv_discover_image);
            holder.iv_head_icon = (ImageView) convertView.findViewById(R.id.iv_head_icon);
            holder.name = (TextView) convertView.findViewById(R.id.tv_name);
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
        if (fragment!=null) {
            Glide.with(fragment).load(discoverInfo.getThumbnail())
                    .placeholder(R.drawable.img_default)
                    .crossFade()
                    .into(holder.backgroundImage);
            Glide.with(fragment).load(discoverInfo.getUser().getPhoto())
                    .placeholder(R.drawable.header_defaut_icon).transform(new GlideCircleTransform(context))
                    .crossFade()
                    .into(holder.iv_head_icon);
        }else {
            Glide.with(context).load(discoverInfo.getThumbnail())
                    .placeholder(R.drawable.img_default)
                    .crossFade()
                    .into(holder.backgroundImage);
            Glide.with(context).load(discoverInfo.getUser().getPhoto())
                    .placeholder(R.drawable.header_defaut_icon).transform(new GlideCircleTransform(context))
                    .crossFade()
                    .into(holder.iv_head_icon);
        }
        holder.name.setText(discoverInfo.getUser().getUsername());
        holder.tv_title.setText(discoverInfo.getTitle());
        holder.tv_browse_num.setText(discoverInfo.getBrowseCount() + "");
        holder.tv_up_num.setText(discoverInfo.getUpCount() + "");
        holder.tv_comment_num.setText(discoverInfo.getCommentCount() + "");
        holder.tv_up_num.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (LoginManager.isLogin(context)) {
//                    updateUpCount(discoverInfo.getId(), LoginManager.getUid(context),position);
//                } else {
//                    LoginManager.login(context, new LoginManager.LoginCallBack() {
//                        @Override
//                        public void onSuccess() {
//                            updateUpCount(discoverInfo.getId(), LoginManager.getUid(context),position);
//                        }
//
//                        @Override
//                        public void onFailer() {
//                            context.showToastSafe("登录失败");
//                        }
//                    });
//                }
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

    public static class ViewHolder {
        ImageView backgroundImage;
        ImageView iv_video_flag;
        ImageView iv_head_icon;
        TextView name;
        TextView tv_title;
        TextView tv_browse_num;
        TextView tv_up_num;
        TextView tv_comment_num;
    }

    private void  updateUpCount(int id ,String userId , final int pos ){

        Map<String , String> headers = new HashMap<>();
        headers.put("token", LoginManager.getSsoTk(context));
        Map<String , String> params = new HashMap<>();
        params.put("id" , id+"");
        params.put("userId" , userId);
        OkHttpRequest.post(RequestTag.UP_TAG, RequestTag.UP_TAG_URL, headers, params, new PostCallBack() {
            @Override
            public void onFailure(Call call, IOException e) {
                context.showToastSafe(context.getResources().getString(R.string.please_net_check));
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    Logger.e(response.body().string() + "    : haha");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateView(pos);
                    }
                });
            }

            @Override
            public void onError(int errorCode) {
                context.showToastSafe(context.getResources().getString(R.string.server_busy));
            }
        });
    }

}
