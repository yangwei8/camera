package com.leautolink.leautocamera.ui.view.scaleview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * An FrameLayout widget that can scale the child view of ListView according to
 * the screen.
 * Created by lizhennian on 2014/5/30.
 */
public class ScaleFrameLayout extends FrameLayout {
    public ScaleFrameLayout(Context context) {
        super(context);
    }

    public ScaleFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScaleFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ScaleCalculator.getInstance().scaleViewGroup(this);
    }
}
