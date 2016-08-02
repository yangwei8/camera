package com.leautolink.leautocamera.utils;

import android.text.TextUtils;

/**
 * Created by lixinlei on 16/6/17.
 */
public class ValidateUtils {

    public static boolean checkEdrPasswordLength(String pwd,int min , int max){
        if (TextUtils.isEmpty(pwd)){
            return false;
        }

        if (pwd.length()>min && pwd.length()<max){
            return true;
        }

        return false;
    }


}
