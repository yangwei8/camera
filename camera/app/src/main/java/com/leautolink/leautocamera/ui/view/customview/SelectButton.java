package com.leautolink.leautocamera.ui.view.customview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;

import com.leautolink.leautocamera.R;


/**
 * Created by lixinlei on 15/11/25.
 */
public class SelectButton extends TextView implements GestureDetector.OnGestureListener {
    private boolean isChecked = false;
    private GestureDetector detector;
    private OnCheckedChangeListener checkedChangeListener;


    public SelectButton(Context context) {
        super(context);
        init();
    }
    public SelectButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SelectButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SelectButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init(){
//        this.setBackground(getResources().getDrawable(R.drawable.select_button_background));
        setClickable(true);
        setFocusable(true);
        detector = new GestureDetector(this);
        setText("  ");
        this.setBackgroundResource(R.drawable.select_button_background);
        checkedIsChange();
    }


    private void checkedIsChange(){
        if (!isChecked){
            this.setCompoundDrawablesWithIntrinsicBounds(R.drawable.select_button_not_checked, 0, 0, 0);
        } else {
            this.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.select_button_checked, 0);
        }
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener){
         this.checkedChangeListener = listener;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        if (this.isChecked!=isChecked) {
            this.isChecked = isChecked;
            checkedIsChange();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        detector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        this.isChecked = !isChecked;
        checkedIsChange();
        if (null!=checkedChangeListener) {
            checkedChangeListener.checkedChange(this.isChecked);
        }
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    public interface OnCheckedChangeListener{
        void checkedChange(boolean checked);
    }
}
