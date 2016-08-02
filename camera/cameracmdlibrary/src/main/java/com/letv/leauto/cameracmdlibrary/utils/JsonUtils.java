package com.letv.leauto.cameracmdlibrary.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 解析缩略图、视频、图片的Json
 * Created by tianwei1 on 2015/11/23.
 */
public class JsonUtils {

    private static final String TAG = "JsonUtils";

    /**
     * 从JsonObgect中解析出名字和日期
     *
     * @param jsonObject
     * @return 所有当前文件夹中的文件的名字和时间
     * @throws JSONException
     */
    public static Map<String, String> parseJsonObject(JSONObject jsonObject) throws JSONException {
        //存放解析后的信息的map
        Map<String, String> map = new LinkedHashMap<String, String>();
        JSONArray jsonArray = jsonObject.getJSONArray("listing");
        for (int i = 0; i < jsonArray.length(); i++) {
            parseString(map, jsonArray.getString(i));
        }

        return map;
    }

    /**
     * 从{"Leauto_20140101_201515T.JPG": "2014-01-01 20:15:16"}中获取文件名和时间
     *
     * @param map
     * @param jString
     */
    private static void parseString(Map<String, String> map, String jString) {
        Logger.e(TAG, jString);
        int index = jString.indexOf(':');
        String name = jString.substring(2, index - 1);
        String time = jString.substring(index + 2, jString.lastIndexOf('"'));
        Logger.e(TAG, "name is***" + name + "***time is ***" + time);


        map.put(name, time);

    }

    /**
     * 解析sd卡的json
     *
     * @param jsonObject
     * @return
     * @throws JSONException
     */
    public static boolean parseSdJsonObject(JSONObject jsonObject) throws JSONException {
        JSONArray jsonArray = jsonObject.getJSONArray("info");
        JSONObject presentObject = jsonArray.getJSONObject(0);
        String present = (String) presentObject.get("present");
        if ("yes".equals(present)) {
            return true;
        }
        return false;
    }
}
