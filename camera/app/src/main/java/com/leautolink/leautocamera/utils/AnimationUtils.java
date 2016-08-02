package com.leautolink.leautocamera.utils;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;

/**
 * Created by lixinlei on 15/11/19.
 */
public class AnimationUtils {

    public static void alphaAndScale(View view){
        Animation scaleAnimation = new ScaleAnimation(1.0f, 1.1f,1.0f,1.1f);
        //设置动画时间
        scaleAnimation.setDuration(500);
        //初始化 Alpha动画
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.1f);
        //动画集
//        AnimationSet set = new AnimationSet(true);
//        set.addAnimation(scaleAnimation);
//        set.addAnimation(alphaAnimation);
        //设置动画时间 (作用到每个动画).
        alphaAnimation.setDuration(1500);
        alphaAnimation.setRepeatMode(Animation.REVERSE);
        alphaAnimation.setRepeatCount(Integer.MAX_VALUE);
        view.startAnimation(alphaAnimation);

    }

    public static void rotate(View view){
        RotateAnimation ra = new RotateAnimation(0, 359, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5F);
        LinearInterpolator li = new LinearInterpolator();
        ra.setInterpolator(li);
        ra.setRepeatCount(Integer.MAX_VALUE);
        ra.setDuration(1000);
        view.startAnimation(ra);
    }

    public static void cancelAnmation(View view){
        view.clearAnimation();
    }


}
