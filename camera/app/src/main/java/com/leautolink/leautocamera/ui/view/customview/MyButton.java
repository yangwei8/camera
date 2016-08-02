package com.leautolink.leautocamera.ui.view.customview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.Button;

import com.leautolink.leautocamera.R;
import com.leautolink.leautocamera.utils.DrawableUtils;

/**
 * Created by lixinlei on 16/3/5.
 */
public class MyButton extends Button {
    public MyButton(Context context) {
        super(context);
    }

    public MyButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }



    public MyButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MyButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        Drawable[] src = getCompoundDrawables();
        this.setCompoundDrawablesWithIntrinsicBounds(null, DrawableUtils.tintDrawable(src[1], getResources().getColorStateList(R.color.camera_fragment_button_selector)), null, null);
    }
}
