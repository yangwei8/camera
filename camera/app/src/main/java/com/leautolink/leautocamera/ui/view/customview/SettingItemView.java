package com.leautolink.leautocamera.ui.view.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.leautolink.leautocamera.R;
import com.leautolink.leautocamera.utils.Logger;

/**
 * Created by tianwei on 16/4/6.
 */
public class SettingItemView extends RelativeLayout {

    private ImageView mIvIcon;
    private TextView mTvText;
    private ImageView mIvArrow;

    public SettingItemView(Context context) {
        super(context);
        initView(context);
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SettingItemView);
        Drawable src = a.getDrawable(R.styleable.SettingItemView_src);
        String text = a.getString(R.styleable.SettingItemView_text);
        boolean arrow_visiable = a.getBoolean(R.styleable.SettingItemView_arrow_visiable, true);
        if (src != null) {
            mIvIcon.setImageDrawable(src);
        }
        if (!TextUtils.isEmpty(text)) {
            mTvText.setText(text);
        }
        mIvArrow.setVisibility((arrow_visiable ? VISIBLE : GONE));
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        View.inflate(context, R.layout.item_setting, SettingItemView.this);
        mIvIcon = (ImageView) this.findViewById(R.id.iv_icon);
        mTvText = (TextView) this.findViewById(R.id.tv_text);
        mIvArrow = (ImageView) this.findViewById(R.id.iv_arrow);
    }

    public ImageView getIvArrow() {
        return mIvArrow;
    }

    public void setIcon(int resId) {
        mIvIcon.setImageResource(resId);
    }

    public void setIcon(Context context ,String url) {
        Glide.with(context).load(url).into(mIvIcon);
    }

    public void setText(String text) {
        mTvText.setText(text);
    }

    public String getText() {
        return mTvText.getText().toString();
    }
}
