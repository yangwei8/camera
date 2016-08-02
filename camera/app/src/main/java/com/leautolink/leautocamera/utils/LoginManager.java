package com.leautolink.leautocamera.utils;

import android.app.Activity;
import android.content.Context;

import com.letv.loginsdk.LoginSdk;
import com.letv.loginsdk.LoginSdkLogout;
import com.letv.loginsdk.bean.LetvBaseBean;
import com.letv.loginsdk.bean.PersonalInfoBean;
import com.letv.loginsdk.bean.UserBean;
import com.letv.loginsdk.callback.LoginSuccessCallBack;

/**
 * Created by tianwei on 16/6/22.
 */
public class LoginManager {
    public static void login(final Activity activity, final LoginCallBack loginCallBack) {
        new LoginSdk().login(activity, new LoginSuccessCallBack() {
            @Override
            public void loginSuccessCallBack(LoginSuccessState loginSuccessState, LetvBaseBean bean) {
                if (loginSuccessState == LoginSuccessState.LOGINSUCCESS) {
                    //登录成功
                    UserBean userBean = (UserBean) bean;
                    saveInfoToSp(activity, userBean);
                    loginCallBack.onSuccess(userBean);
                } else {
                    loginCallBack.onFailer();
                }
            }
        });
    }

    private static void saveInfoToSp(Context context, UserBean userBean) {
        SpUtils.putBoolean(context, "islogin", true);
        SpUtils.putString(context, "headPicUrl", userBean.getPicture200x200());
        SpUtils.putString(context, "userName", userBean.getNickname());
        SpUtils.putString(context, "uid", userBean.getUid());
        SpUtils.putString(context, "ssoTk", userBean.getSsoTK());
    }

    public static boolean isLogin(Context context) {
        return SpUtils.getBoolean(context, "islogin", false);
    }

    public static String getHeadPicUrl(Context context) {
        return SpUtils.getString(context, "headPicUrl", "");
    }

    public static String getNicename(Context context) {
        return SpUtils.getString(context, "userName", "");
    }

    public static String getUid(Context context) {
        return SpUtils.getString(context, "uid", "");
    }

    public static String getSsoTk(Context context) {
        return SpUtils.getString(context, "ssoTk", "");
    }

    public static void setNickName(Context context, String value) {
        SpUtils.putString(context, "userName", value);
    }

    public static void setHeadPicUrl(Context context, String url) {
        SpUtils.putString(context, "headPicUrl", url);
    }

    /**
     * 主动调用退出
     *
     * @param context
     */
    public static void logout(Context context) {
        new LoginSdkLogout().logout(context);
        SpUtils.putBoolean(context, "islogin", false);
    }

    public interface LoginCallBack {
        void onSuccess(UserBean userBean);

        void onFailer();
    }

}
