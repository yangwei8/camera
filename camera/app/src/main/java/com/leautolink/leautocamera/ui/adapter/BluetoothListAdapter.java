package com.leautolink.leautocamera.ui.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.leautolink.leautocamera.R;

import java.util.List;

/**
 * Created by lixinlei on 16/4/13.
 */
public class BluetoothListAdapter extends BaseAdapter{

    private Context  context;

    private List<BluetoothDevice> devices;

    public BluetoothListAdapter() {
    }

    public BluetoothListAdapter(Context context, List<BluetoothDevice> devices) {
        this.context = context;
        this.devices = devices;
    }

    @Override
    public int getCount() {
        return devices.size();
    }

    @Override
    public Object getItem(int position) {
        return devices.get(position);
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
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }
        BluetoothDevice device = devices.get(position);
        String name = device.getName();

        name = name.replace("Le-DVR", convertView.getResources().getString(R.string.myname));
        holder.textView.setText(name);
        if (device.getBondState() == BluetoothDevice.BOND_BONDED){
            holder.tv_introduce.setVisibility(View.VISIBLE);
        }else {
            holder.tv_introduce.setVisibility(View.GONE);
        }
        return convertView;
    }

    static class ViewHolder{
       public TextView textView;
       public TextView tv_introduce;
    }

}
