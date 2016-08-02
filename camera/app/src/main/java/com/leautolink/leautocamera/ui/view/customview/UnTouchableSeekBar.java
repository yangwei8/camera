package com.leautolink.leautocamera.ui.view.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

/**
 * Created by tianwei1 on 2015/12/10.
 */
public class UnTouchableSeekBar extends SeekBar {
    public UnTouchableSeekBar(Context context) {
        super(context);
    }

    public UnTouchableSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UnTouchableSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }
}
