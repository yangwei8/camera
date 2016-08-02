package com.leautolink.leautocamera.ui.view.scaleview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class ScaleView extends View {

    public ScaleView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public ScaleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public ScaleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ScaleCalculator.getInstance().scaleView(this);
    }
}
