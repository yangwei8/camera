package com.leautolink.leautocamera.ui.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.leautolink.leautocamera.R;
import com.leautolink.leautocamera.ui.activity.BannerWebActivity_;
import com.leautolink.leautocamera.utils.NetworkUtil;

import java.util.List;

/**
 * Created by lixinlei on 16/3/10.
 */
public class DiscoverListHeaderAdapter extends PagerAdapter {

    private List<String> picUrls;
    private List<String> jumpUrls;
    private Context context;
    private Fragment fragment;
    protected Handler handler_;
    public DiscoverListHeaderAdapter(Context context,Fragment fragment,List<String> picUrls , List<String> jumpUrls) {
        this.picUrls = picUrls;
        this.context = context;
        this.fragment = fragment;
        this.jumpUrls = jumpUrls;
        handler_ = new Handler(Looper.getMainLooper());
    }
    @Override
    public int getCount() {
        return picUrls.size()==0?0:Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
       return view == object;
    }

    public void destroyItem(ViewGroup container, int position,
                            Object object) {
        //Warning：不要在这里调用removeView
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //对ViewPager页号求模取出View列表中要显示的项
        position %= picUrls.size();
        if (position<0){
            position = picUrls.size()+position;
        }
        ImageView view =(ImageView) ( (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.image_item, null);;
        //如果View已经在之前添加到了一个父组件，则必须先remove，否则会抛出IllegalStateException。
        ViewParent vp =view.getParent();
        if (vp!=null){
            ViewGroup parent = (ViewGroup)vp;
            parent.removeView(view);
        }
        container.addView(view);
        Glide.with(fragment).load(picUrls.get(position)).placeholder(R.drawable.img_default).crossFade().into(view);
        final int finalPosition = position;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Uri uri = Uri.parse(jumpUrls.get(finalPosition));
//                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                context.startActivity(intent);
                if(NetworkUtil.isConnected(context)) {
                    BannerWebActivity_.intent(context).url(jumpUrls.get(finalPosition)).start();
                }else{
                    showToastSafe(context.getResources().getString(R.string.net_unknown));
                }

            }
        });
        //add listeners here if necessary
        return view;
    }

    public void showToastSafe(final String text) {
        handler_.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public int getItemPosition(Object object)   {
            return POSITION_NONE;
    }

}
