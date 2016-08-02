package com.leautolink.leautocamera.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.leautolink.leautocamera.R;
import com.leautolink.leautocamera.domain.respone.CommentInfo;
import com.leautolink.leautocamera.utils.glideutils.GlideCircleTransform;

import java.util.List;

/**
 * Created by lixinlei on 16/4/13.
 */
public class CommentListAdapter extends BaseAdapter{

    private Context  context;

    private List<CommentInfo> commentInfoList;

    public CommentListAdapter() {
    }

    public CommentListAdapter(Context context, List<CommentInfo> commentInfoList) {
        this.context = context;
        this.commentInfoList = commentInfoList;
    }

    @Override
    public int getCount() {
        return commentInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return commentInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView==null) {
            convertView = View.inflate(context, R.layout.comment_list_item, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.time = (TextView) convertView.findViewById(R.id.tv_time);
            holder.comment = (TextView) convertView.findViewById(R.id.tv_comment);
            holder.icon = (ImageView) convertView.findViewById(R.id.iv_photo_icon);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }
        CommentInfo commentInfo = null;
        if (commentInfoList!=null){
            commentInfo = commentInfoList.get(position);
            holder.name.setText(commentInfo.getUser().getUsername());
            holder.time.setText(commentInfo.getVtime());
            holder.comment.setText(commentInfo.getContent());
            Glide.with(context).load(commentInfo.getUser().getPhoto()).placeholder(R.drawable.header_defaut_icon).transform(new GlideCircleTransform(context)).crossFade().into(holder.icon);
        }
        return convertView;
    }

    static class ViewHolder{
       public TextView name;
       public TextView time;
       public TextView comment;
       public ImageView icon;


    }

}
