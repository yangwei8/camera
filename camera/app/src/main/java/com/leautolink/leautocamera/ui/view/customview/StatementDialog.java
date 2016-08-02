package com.leautolink.leautocamera.ui.view.customview;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.leautolink.leautocamera.R;

/**
 * Created by tianwei on 16/7/28.
 */
public class StatementDialog extends Dialog {
    public StatementDialog(Context context) {
        super(context);
    }

    public StatementDialog(Context context, int theme) {
        super(context, theme);
    }

    public StatementDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public static class Builder {
        private Context context;
        private StatementDialogListener listener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder listener(StatementDialogListener listener) {
            this.listener = listener;
            return this;
        }

        public StatementDialog create() {
            final StatementDialog dialog = new StatementDialog(context, R.style.normal_dialog);
            //R.layout.dialog_statement 是自定义的dialog的布局
            View layout = View.inflate(context, R.layout.dialog_statement, null);
            dialog.addContentView(layout, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            TextView tvStatement = (TextView) layout.findViewById(R.id.tv_statement);
            final CheckBox cbNotify = (CheckBox) layout.findViewById(R.id.cb_notify);
            TextView tvOk = (TextView) layout.findViewById(R.id.tv_ok);
            TextView tvExit = (TextView) layout.findViewById(R.id.tv_exit);
            tvStatement.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onShowStatement();
                }
            });
            tvOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onOk(dialog, cbNotify);
                }
            });
            tvExit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onExit(dialog);
                }
            });
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setContentView(layout);
            return dialog;
        }


        public interface StatementDialogListener {
            void onOk(StatementDialog dialog, CheckBox checkBox);

            void onExit(StatementDialog dialog);

            void onShowStatement();
        }
    }


}
