package com.leautolink.leautocamera.ui.view.scaleview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;

public class ScaleSeekBar extends SeekBar {

    public ScaleSeekBar(Context context) {
        super(context);
    }

    public ScaleSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScaleSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ScaleCalculator.getInstance().scaleView(this);
    }
}
