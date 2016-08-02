package com.leautolink.leautocamera.ui.view.scaleview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ScaleImageView extends ImageView {

    public ScaleImageView(Context context) {
        super(context);
    }

    public ScaleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScaleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ScaleCalculator.getInstance().scaleView(this);
    }
}
