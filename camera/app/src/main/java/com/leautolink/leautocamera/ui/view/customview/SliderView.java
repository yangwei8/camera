package com.leautolink.leautocamera.ui.view.customview;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.leautolink.leautocamera.R;

/**
 * Created by tianwei on 16/6/21.
 */
public class SliderView extends RelativeLayout {

    private int startX;
    private int screenWidth;
    private OnTouchingListener mOnTouchingListener;
    private TextView mTvCutDuration;

    public SliderView(Context context) {
        super(context);
        init(context);
    }

    public SliderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SliderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        screenWidth = dm.widthPixels;
        View.inflate(context, R.layout.item_slider_view, SliderView.this);
        mTvCutDuration = (TextView) this.findViewById(R.id.tv_cut_duration);
    }

    public void setOnTouchingListener(OnTouchingListener onTouchingListener) {
        mOnTouchingListener = onTouchingListener;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = (int) event.getRawX();
                mOnTouchingListener.onDown(startX);
                break;
            case MotionEvent.ACTION_MOVE:
                int newX = (int) event.getRawX();
                int dx = newX - startX;
                int left = this.getLeft() + dx;
                int right = this.getRight() + dx;
                int top = this.getTop();
                int bottom = this.getBottom();
                if (left < 0) {
                    left = 0;
                    right = left + getWidth();
                }
                if (right > screenWidth) {
                    right = screenWidth;
                    left = screenWidth - getWidth();
                }
                mOnTouchingListener.onMove(left, top, right, bottom, mTvCutDuration);
                this.layout(left, top, right, bottom);
                startX = (int) event.getRawX();

                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    public interface OnTouchingListener {
        void onDown(int startx);

        void onMove(int left, int top, int right, int bottom, TextView tvCutDuration);

        void onUp();

    }

}
