package com.leautolink.leautocamera.utils;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * 输入法工具类
 * Created by tianwei on 16/7/22.
 */
public class InputMethodUtil {
    /**
     * 判断用户点击的区域是否在EditText范围内
     *
     * @param focusView
     * @param ev
     * @return
     */
    public static boolean isShouldHideInput(View focusView, MotionEvent ev) {
        boolean isShouldHideInput = false;
        if (focusView != null && (focusView instanceof EditText)) {//当前焦点在EditText上
            //测量结果存放在这个数组中
            int[] leftTop = {0, 0};
            //获取输入框在当前窗口的位置
            focusView.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int right = left + focusView.getWidth();
            int bottom = top + focusView.getHeight();
            if (ev.getX() > left && ev.getX() < right && ev.getY() > top && ev.getY() < bottom) {
                //点击的是EditText区域
                isShouldHideInput = false;
            } else {
                isShouldHideInput = true;
            }
        }
        return isShouldHideInput;
    }

    /**
     * 打开输入法，显示软键盘
     *
     * @param context
     * @param view
     */
    public static void showInput(Context context, View view) {
        if (view != null) {
            InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (manager != null)
                manager.showSoftInput(view, 0);
        }
    }

    /**
     * 关闭输入法，隐藏软键盘
     *
     * @param context
     * @param view
     */
    public static void hideInput(Context context, View view) {
        if (view != null) {
            InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (manager != null)
                manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
