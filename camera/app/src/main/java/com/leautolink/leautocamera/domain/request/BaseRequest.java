package com.leautolink.leautocamera.domain.request;

import com.letv.leauto.cameracmdlibrary.utils.HashUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lixinlei on 16/3/10.
 */
public class BaseRequest {
    private String baseUrl = "http://api.open.letvcloud.com/live/execute?";
    private String method;
    private long timestamp;
    private int userid;
    private String ver;
    private String sign;
    protected Map<String,String> params;

    public BaseRequest(String method) {
        params = new HashMap<>();
        this.method = method;
        this.userid = 825109;
        this.ver = "3.0";
        params.put("method",method);
        params.put("userid",userid+"");
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
        sign = HashUtils.toMD5(value+"4d460a4f1a446a0967ebcdb12e4b1bdc");
        return baseUrl+requestStr+"sign="+sign;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public Map<String, String> getParams() {
        return params;
    }
}
