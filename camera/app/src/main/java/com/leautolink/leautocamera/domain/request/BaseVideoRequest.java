package com.leautolink.leautocamera.domain.request;

import com.letv.leauto.cameracmdlibrary.utils.HashUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lixinlei on 16/3/13.
 */
public class BaseVideoRequest {
    private String baseUrl = "http://api.letvcloud.com/open.php?";
    private String api;
    private long timestamp;
    private String user_unique;
    private String format;
    private String ver;
    private String sign;
    protected Map<String,String> params;

    public BaseVideoRequest(String api) {
        params = new HashMap<>();
        this.api = api;
        this.user_unique = "an3pvuygo6";
        this.ver = "2.0";
        this.format = "json";
        params.put("api",api);
        params.put("user_unique",user_unique+"");
        params.put("format",format);
        params.put("ver",ver+"");
    }
    public String getUrl(){
        long timetemp = System.currentTimeMillis();
        this.timestamp = timetemp;
        params.put("timestamp",timestamp+"");
        String value="";
        String requestStr="";
        if (null!=params&&params.size()>0) {
            Object[] key_arr = params.keySet().toArray();
            Arrays.sort(key_arr);
            for (Object key : key_arr) {
                value = value+key+params.get(key);
                requestStr = requestStr+key +"="+params.get(key)+"&";
            }
        }
        sign = HashUtils.toMD5(value + "4d460a4f1a446a0967ebcdb12e4b1bdc");
        return baseUrl+requestStr+"sign="+sign;
    }

}
