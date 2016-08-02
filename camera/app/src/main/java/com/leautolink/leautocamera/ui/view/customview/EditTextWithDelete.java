package com.leautolink.leautocamera.ui.view.customview;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.leautolink.leautocamera.R;

import java.util.regex.PatternSyntaxException;

/**
 * Created by lixinlei on 15/12/1.
 */
public class EditTextWithDelete extends EditText implements View.OnFocusChangeListener {
    private Drawable imgEnable;
    private Context context;
    
    public EditTextWithDelete(Context context) {
        super(context);
        this.context = context;
        init();
    }
    
    public EditTextWithDelete(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        init();
    }
    
    public EditTextWithDelete(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }
    private void init() {
        //获取图片资源
        imgEnable = context.getResources().getDrawable(R.drawable.edit_delete);
        addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }
            
            @Override
            public void afterTextChanged(Editable s) {
                setDrawable();
                //                Toast.makeText(context, getText(), 10).show();
            }
        });
        setDrawable();
    }
    
    /**
     * 设置删除图片
     */
    private void setDrawable() {
        if (length() == 0) {
            setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        } else {
            setCompoundDrawablesWithIntrinsicBounds(null, null, imgEnable, null);
        }
    }
    
    /**
     * event.getX() 获取相对应自身左上角的X坐标
     * event.getY() 获取相对应自身左上角的Y坐标
     * getWidth() 获取控件的宽度
     * getTotalPaddingRight() 获取删除图标左边缘到控件右边缘的距离
     * getPaddingRight() 获取删除图标右边缘到控件右边缘的距离
     * getWidth() - getTotalPaddingRight() 计算删除图标左边缘到控件左边缘的距离
     * getWidth() - getPaddingRight() 计算删除图标右边缘到控件左边缘的距离
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (imgEnable != null && event.getAction() == MotionEvent.ACTION_UP) {
            int x = (int) event.getX();
            //判断触摸点是否在水平范围内
            boolean isInnerWidth = (x > (getWidth() - getTotalPaddingRight())) &&
            (x < (getWidth() - getPaddingRight()));
            //获取删除图标的边界，返回一个Rect对象
            Rect rect = imgEnable.getBounds();
            //获取删除图标的高度
            int height = rect.height();
            int y = (int) event.getY();
            //计算图标底部到控件底部的距离
            int distance = (getHeight() - height) / 2;
            //判断触摸点是否在竖直范围内(可能会有点误差)
            //触摸点的纵坐标在distance到（distance+图标自身的高度）之内，则视为点中删除图标
            boolean isInnerHeight = (y > distance) && (y < (distance + height));
            
            if (isInnerWidth && isInnerHeight) {
                setText("");
            }
            
        }
        
        return super.onTouchEvent(event);
    }
    
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
    
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            setDrawable();
        } else {
            setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }
    }
    
    public static String stringFilter(String str)throws PatternSyntaxException {
        // 只允许字母、数字和汉字
        return str.replaceAll(" ","").trim();
    }
    //    @Override
    //    public void onTextChanged(CharSequence ss, int start, int before, int count) {
    //        String editable = getText().toString();
    //        String str = stringFilter(editable.toString());
    //        if(!editable.equals(str)){
    //            setText(str);
    //            //设置新的光标所在位置
    //            setSelection(str.length());
    //        }
    //        return super();
    //    }
    
    
    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        String editable = getText().toString();
        String str = stringFilter(editable.toString());
        if(!editable.equals(str)){
            setText(str);
            //设置新的光标所在位置
            setSelection(str.length());
        }
    }
    
    public class MyTextWatcher implements TextWatcher {
        
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            
        }
        
        @Override
        public void afterTextChanged(Editable s) {
            
        }
    }
    
}

