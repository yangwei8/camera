package com.leautolink.leautocamera.domain.request;

/**
 * Created by lixinlei on 16/3/13.
 */
public class StopLivingRequest extends BaseRequest {


    private String activityId;
    public StopLivingRequest(String method,String activityId) {
        super(method);
        this.activityId = activityId;
        params.put("activityId",activityId);
    }

}
