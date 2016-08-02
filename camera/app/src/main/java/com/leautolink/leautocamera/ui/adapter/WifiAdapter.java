package com.leautolink.leautocamera.ui.adapter;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.leautolink.leautocamera.R;
import com.leautolink.leautocamera.utils.WifiAdmin;

import java.util.List;

/**
 * Created by lixinlei on 16/4/13.
 */
public class WifiAdapter extends BaseAdapter{

    private Context  context;

    private List<ScanResult> scanResults;

    public WifiAdapter() {
    }

    public WifiAdapter(Context context, List<ScanResult> wifiInfos) {
        this.context = context;
        this.scanResults = wifiInfos;
    }

    @Override
    public int getCount() {
        return scanResults.size();
    }

    @Override
    public Object getItem(int position) {
        return scanResults.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView==null) {
            convertView = View.inflate(context, R.layout.wifi_list_item, null);
            holder = new ViewHolder();
            holder.textView = (TextView) convertView.findViewById(R.id.wifi_name);
            holder.tv_introduce = (TextView) convertView.findViewById(R.id.tv_introduce);
            holder.tv_introduce.setVisibility(View.VISIBLE);
            holder.tv_introduce.setText(convertView.getResources().getString(R.string.no_wifi_link));
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }
        if (WifiAdmin.isConnectCamera(context,scanResults.get(position).SSID,scanResults.get(position).BSSID)){

        }
        holder.textView.setText(scanResults.get(position).SSID);
        return convertView;
    }

    static class ViewHolder{
       public TextView textView;
       public TextView tv_introduce;

    }

}
