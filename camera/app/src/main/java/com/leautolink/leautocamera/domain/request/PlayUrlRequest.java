package com.leautolink.leautocamera.domain.request;

/**
 * Created by lixinlei on 16/3/14.
 */
public class PlayUrlRequest extends BaseRequest {

    private String activityId;
    public PlayUrlRequest(String method,String activityId) {
        super(method);
        this.activityId = activityId;
        params.put("activityId", activityId);
    }
}
