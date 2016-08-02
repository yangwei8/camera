package com.leautolink.leautocamera.domain.request;

import com.letv.leauto.cameracmdlibrary.utils.HashUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lixinlei on 16/3/14.
 */
public class BaseUrlRequest {
    private String baseUrl = "http://api.letvcloud.com/getplayurl.php?";
    private String user;
    private String video;
    private String vtype;
    private String ts;
    private String sign;
    protected Map<String,String> params;

    public BaseUrlRequest(String videoID){
        params = new HashMap<>();
        this.user = "an3pvuygo6";
        this.video = videoID;
        this.vtype = "mp4";
        params.put("user",user);
        params.put("video",video);
        params.put("vtype",vtype);
    }
    public String getUrl(){
        long ts = System.currentTimeMillis();
        this.ts = ts+"";
        params.put("ts",ts+"");
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



    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getVtype() {
        return vtype;
    }

    public void setVtype(String vtype) {
        this.vtype = vtype;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
