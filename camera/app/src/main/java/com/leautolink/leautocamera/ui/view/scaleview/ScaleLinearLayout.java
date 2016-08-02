package com.leautolink.leautocamera.ui.view.scaleview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * An LinnearLayout widget that can scale the child view of ListView according
 * to the screen.
 * Created by lizhennian on 2014/5/29.
 */
public class ScaleLinearLayout extends LinearLayout {
    public ScaleLinearLayout(Context context) {
        super(context);
    }

    public ScaleLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScaleLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ScaleCalculator.getInstance().scaleViewGroup(this);
    }
}
