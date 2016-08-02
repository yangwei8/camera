package com.letv.lecloud.solar.upload;

import com.letv.lecloud.solar.http.ResponseInfo;

import org.json.JSONObject;

/**
 * Created by wiky on 15/12/25.
 */
public interface UpResponseHandler {
    /**
     * 用户自定义的内容上传完成后处理动作必须实现的方法
     *
     * @param info     上传结束返回日志信息
     * @param response 上传结束的回复内容
     */
    void onResponse(ResponseInfo info, JSONObject response);
}
