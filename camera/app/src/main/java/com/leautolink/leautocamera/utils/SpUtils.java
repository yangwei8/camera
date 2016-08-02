package com.leautolink.leautocamera.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by lixinlei on 16/4/16.
 */
public class SpUtils {
    private SharedPreferences sp = null;
    private SharedPreferences.Editor edit = null;

    private static Context mContext;
    private final static String  SP_FILE_NAME = "CONFIG";

    private static class LazyHolder {
        private static final SpUtils INSTANCE = new SpUtils(mContext);
    }
    public static SpUtils getInstance(Context context) {
        mContext = context;
        return LazyHolder.INSTANCE;
    }

    private SpUtils(Context context) {
        this(context.getApplicationContext().getSharedPreferences(SP_FILE_NAME,
                Context.MODE_PRIVATE));
    }

    private SpUtils(SharedPreferences sp) {
        this.sp = sp;
        edit = sp.edit();
    }
    public void setValue(String key, boolean value) {
        edit.putBoolean(key, value);
        edit.commit();
    }

    public void setValue(String key, float value) {
        edit.putFloat(key, value);
        edit.commit();
    }

    public void setValue(String key, int value) {
        edit.putInt(key, value);
        edit.commit();
    }

    public void setValue(String key, long value) {
        edit.putLong(key, value);
        edit.commit();
    }

    public void setValue(String key, String value) {
        edit.putString(key, value);
        edit.commit();
    }

    public boolean getBoolValue(String key) {
        return sp.getBoolean(key, false);
    }

    public float getFloatValue(String key) {
        return sp.getFloat(key, 0);
    }

    public int getIntegerValue(String key) {
        return sp.getInt(key, 0);
    }

    public long getLongValue(String key) {
        return sp.getLong(key, 0);
    }

    public String getStringValue(String key) {
        return sp.getString(key, "");
    }

    public void remove(String key) {
        edit.remove(key);
        edit.commit();
    }

    public void clear() {
        edit.clear();
        edit.commit();
    }

    public boolean contains(String s) {
        return sp.contains(s);
    }



    private static SharedPreferences getSp(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        return sp;
    }

    public static void putInt(Context context, String key, int value) {
        SharedPreferences sp = getSp(context);
        sp.edit().putInt(key, value).commit();
        sp = null;
    }

    public static int getInt(Context context, String key, int defaultValue) {
        SharedPreferences sp = getSp(context);
        int value = sp.getInt(key, defaultValue);
        sp = null;
        return value;
    }

    public static void putBoolean(Context context, String key, boolean value) {
        SharedPreferences sp = getSp(context);
        sp.edit().putBoolean(key, value).commit();
        sp = null;
    }

    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        SharedPreferences sp = getSp(context);
        boolean value = sp.getBoolean(key, defaultValue);
        sp = null;
        return value;
    }

    public static void putString(Context context, String key, String value) {
        SharedPreferences sp = getSp(context);
        sp.edit().putString(key, value).commit();
        sp = null;
    }

    public static String getString(Context context, String key, String defaultValue) {
        SharedPreferences sp = getSp(context);
        String value = sp.getString(key, defaultValue);
        sp = null;
        return value;
    }

}
