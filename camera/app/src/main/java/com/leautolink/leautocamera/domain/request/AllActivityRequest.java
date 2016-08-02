package com.leautolink.leautocamera.domain.request;

/**
 * Created by lixinlei on 16/3/14.
 */
public class AllActivityRequest extends BaseRequest {
    private String activityStatus;

    public AllActivityRequest(String method,String activityStatus) {
        super(method);
        this.activityStatus = activityStatus;
        params.put("activityStatus",activityStatus);
    }


}
