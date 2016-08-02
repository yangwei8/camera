package com.leautolink.leautocamera.net.http;

import android.content.Context;
import android.content.SharedPreferences;

public class CacheUtils {

	private static final String CACHE_FILE_NAME = "LeCamera";
	private static CacheUtils cacheUtils;
	private static SharedPreferences mSharedPreferences;

	public static CacheUtils getInstance(Context context) {
		synchronized (CacheUtils.class) {
			if (cacheUtils == null) {
				cacheUtils = new CacheUtils();
				if(mSharedPreferences == null) {
					mSharedPreferences = context.getSharedPreferences(CACHE_FILE_NAME, Context.MODE_PRIVATE);
				}
			}
		}
		return cacheUtils;
	}

	public void putBoolean(String key, boolean value) {
		mSharedPreferences.edit().putBoolean(key, value).commit();
	}

	public boolean getBoolean(String key, boolean defValue) {
		return mSharedPreferences.getBoolean(key, defValue);
	}

	public void putInt(String key, int value) {
		mSharedPreferences.edit().putInt(key, value).commit();
	}

	public int getInt(String key, int defValue) {
		return mSharedPreferences.getInt(key, defValue);
	}

	public long getLong(String key, long defValue) {
		return mSharedPreferences.getLong(key, defValue);
	}

	public void putLong(String key, long value) {
		mSharedPreferences.edit().putLong(key, value).commit();
	}

	public void putString(String key, String value) {
		mSharedPreferences.edit().putString(key, value).commit();
	}


	public String getString(String key, String defValue) {
		return mSharedPreferences.getString(key, defValue);
	}
}
