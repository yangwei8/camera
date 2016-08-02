package com.leautolink.leautocamera.ui.view.scaleview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * An TextView widget that can scale the child view of ListView according to the
 * screen.
 * Created by lizhennian on 2014/5/29.
 */
public class ScaleTextView extends TextView {
    public ScaleTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setTextSize(this.getTextSize());
    }

    public ScaleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTextSize(this.getTextSize());
    }

    public ScaleTextView(Context context) {
        super(context);
        this.setTextSize(this.getTextSize());
    }

    @Override
    public void setTextSize(float textSize) {
        this.setTextSize(0, textSize);
    }

    @Override
    public void setTextSize(int unit, float textSize) {
        textSize = ScaleCalculator.getInstance().scaleTextSize(textSize);
        super.setTextSize(unit, textSize);
    }
}
