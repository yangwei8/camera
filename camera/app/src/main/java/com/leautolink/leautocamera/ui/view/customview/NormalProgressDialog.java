package com.leautolink.leautocamera.ui.view.customview;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.leautolink.leautocamera.R;

/**
 * Created by lixinlei on 16/7/27.
 */
public class NormalProgressDialog {

    private NormalDialog normalDialog;

    private TextView tv_tip_message;
    private TextView tv_postion_total;
    private ProgressBar pb_progress;
    private int max;

    public NormalProgressDialog(Context context  ,  DialogInterface.OnClickListener listener) {
        normalDialog = creatProgressDialog(context,listener);
    }

    private NormalDialog creatProgressDialog(Context context, final  DialogInterface.OnClickListener listener){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.normal_progress_dialog,null);
        tv_tip_message = (TextView) view.findViewById(R.id.tv_tip_message);
        tv_postion_total = (TextView) view.findViewById(R.id.tv_postion_total);
        pb_progress = (ProgressBar) view.findViewById(R.id.pb_progress);
        pb_progress.setIndeterminate(false);
        return new NormalDialog.Builder(context).setContentView(view).setNegativeButton(R.drawable.dialog_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (listener!=null) {
                    listener.onClick(dialog,which);
                }
            }
        }).setTitle(context.getResources().getString(R.string.message)).create();
    }

    public void dismiss(){
        normalDialog.dismiss();
        normalDialog=null;
    }

    public NormalProgressDialog setMax(int max){
        this.max = max;
        pb_progress.setMax(max);
        return this;
    }

    public NormalProgressDialog setTipMessage(String tipMessage){
        tv_tip_message.setText(tipMessage);
        return this;
    }

    public NormalProgressDialog setTitle(String title){
        if (normalDialog!=null){
            normalDialog.setTitle(title);
        }
        return this;
    }

    public NormalProgressDialog setProgress(final int currentPostion){
        pb_progress.setProgress(currentPostion);
        tv_postion_total.post(new Runnable() {
            @Override
            public void run() {
                tv_postion_total.setText(currentPostion + "%");
            }
        });
        return this;
    }

    public void show(){
        if (normalDialog!=null){
            normalDialog.show();
        }
    }

    public NormalProgressDialog setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
        if (normalDialog!=null){
            normalDialog.setCanceledOnTouchOutside(canceledOnTouchOutside);
        }
        return this;
    }

//    public interface  OnClickListener{
//        void confirm();
//        void cancel();
//    }

}
