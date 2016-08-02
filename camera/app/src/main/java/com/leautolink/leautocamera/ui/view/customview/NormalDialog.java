package com.leautolink.leautocamera.ui.view.customview;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leautolink.leautocamera.R;

//import com.letv.leauto.customuilibrary.CustomAlertDialog;

/**
 * Created by lixinlei on 16/7/19.
 */
public class NormalDialog extends Dialog {


    public NormalDialog(Context context) {
        super(context);
    }

    public NormalDialog(Context context, int theme) {
        super(context, theme);
    }

    protected NormalDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    /**
     * Helper class for creating a custom alert_dialog_custom
     */
    public static class Builder {

        private Context context;
        private String title;
        private String message;
        private Drawable positiveButtonDrawable;
        private Drawable negativeButtonDrawable;
        private View contentView;

        private OnClickListener
                positiveButtonClickListener,
                negativeButtonClickListener;

        public Builder(Context context) {
            this.context = context;
        }

        /**
         * Set the Dialog message from String
         * @param message
         * @return
         */
        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        /**
         * Set the Dialog message from resource
         * @param message
         * @return
         */
        public Builder setMessage(int message) {
            this.message = (String) context.getText(message);
            return this;
        }

        /**
         * Set the Dialog title from resource
         * @param title
         * @return
         */
        public Builder setTitle(int title) {
            this.title = (String) context.getText(title);
            return this;
        }

        /**
         * Set the Dialog title from String
         * @param title
         * @return
         */
        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        /**
         * Set a custom content view for the Dialog.
         * If a message is set, the contentView is not
         * added to the Dialog...
         * @param v
         * @return
         */
        public Builder setContentView(View v) {
            this.contentView = v;
            return this;
        }

        /**
         * Set the positive button resource and it's listener
         * @param positiveButtonDrawable
         * @param listener
         * @return
         */
        public Builder setPositiveButton(int positiveButtonDrawable,
                                         OnClickListener listener) {
            this.positiveButtonDrawable = ContextCompat.getDrawable(context,positiveButtonDrawable);
            this.positiveButtonClickListener = listener;
            return this;
        }

        /**
         * Set the positive button text and it's listener
         * @param positiveButtonDrawable
         * @param listener
         * @return
         */
        public Builder setPositiveButton(Drawable positiveButtonDrawable,
                                         OnClickListener listener) {
            this.positiveButtonDrawable = positiveButtonDrawable;
            this.positiveButtonClickListener = listener;
            return this;
        }

        /**
         * Set the negative button resource and it's listener
         * @param negativeButtonDrawable
         * @param listener
         * @return
         */
        public Builder setNegativeButton(int negativeButtonDrawable,
                                         OnClickListener listener) {
            this.negativeButtonDrawable = ContextCompat.getDrawable(context,negativeButtonDrawable);

            this.negativeButtonClickListener = listener;
            return this;
        }

        /**
         * Set the negative button text and it's listener
         * @param negativeButtonDrawable
         * @param listener
         * @return
         */
        public Builder setNegativeButton(Drawable negativeButtonDrawable,
                                         OnClickListener listener) {
            this.negativeButtonDrawable = negativeButtonDrawable;
            this.negativeButtonClickListener = listener;
            return this;
        }

        /**
         * Create the custom alert_dialog_custom
         */
        public NormalDialog create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the alert_dialog_custom with the custom Theme
            final NormalDialog dialog = new NormalDialog(context,
                    R.style.normal_dialog);
            View layout = inflater.inflate(R.layout.normal_dialog, null);
            dialog.addContentView(layout, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            // set the alert_dialog_custom title
            ((TextView) layout.findViewById(R.id.title)).setText(title);
            dialog.setCanceledOnTouchOutside(false);
            // set the confirm button
            if (positiveButtonDrawable != null) {
                ((ImageButton) layout.findViewById(R.id.positiveButton))
                        .setImageDrawable(positiveButtonDrawable);
                if (positiveButtonClickListener != null) {
                    ((ImageButton) layout.findViewById(R.id.positiveButton))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    positiveButtonClickListener.onClick(
                                            dialog,
                                            DialogInterface.BUTTON_POSITIVE);
                                }
                            });
                }
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.positiveButton).setVisibility(
                        View.GONE);
            }
            // set the cancel button
            if (negativeButtonDrawable != null) {
                ((ImageButton) layout.findViewById(R.id.negativeButton))
                        .setImageDrawable(negativeButtonDrawable);
                if (negativeButtonClickListener != null) {
                    ((ImageButton) layout.findViewById(R.id.negativeButton))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    negativeButtonClickListener.onClick(
                                            dialog,
                                            DialogInterface.BUTTON_NEGATIVE);
                                }
                            });
                }
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.negativeButton).setVisibility(
                        View.GONE);
            }
            // set the content message
            if (message != null) {
                ((TextView) layout.findViewById(R.id.message)).setText(message);
            } else if (contentView != null) {
                // if no message set
                // add the contentView to the alert_dialog_custom body
                ((LinearLayout) layout.findViewById(R.id.content))
                        .removeAllViews();
                ((LinearLayout) layout.findViewById(R.id.content))
                        .addView(contentView,
                                new ViewGroup.LayoutParams(
                                        ViewGroup.LayoutParams.WRAP_CONTENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            dialog.setContentView(layout);
            return dialog;
        }

    }
}
