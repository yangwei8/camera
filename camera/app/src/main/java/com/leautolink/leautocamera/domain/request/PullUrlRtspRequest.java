package com.leautolink.leautocamera.domain.request;

/**
 * Created by lixinlei on 16/3/11.
 */
public class PullUrlRtspRequest extends BaseRequest {

    private String activityId;

    public PullUrlRtspRequest(String method, String activityId) {
        super(method);
        this.activityId = activityId;
        params.put("activityId",activityId);
    }
}
