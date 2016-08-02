package com.leautolink.leautocamera.domain.request;

/**
 * Created by lixinlei on 16/3/13.
 */
public class CreateActivityRequest extends BaseRequest {
    private String  activityName;
    private String  startTime;
    private String  endTime;
    private String  coverImgUrl;
    private String  description;
    private String  liveNum;
    private String  codeRateTypes;
    private String  needRecord;
    private String  needTimeShift;
    private String  needFullView;
    private String  activityCategory;
    private String  playMode;

    public CreateActivityRequest(String method) {
        super(method);
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
        params.put("activityName",activityName);
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
        params.put("startTime",startTime);
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
        params.put("endTime",endTime);
    }

    public void setCoverImgUrl(String coverImgUrl) {
        this.coverImgUrl = coverImgUrl;
        params.put("coverImgUrl",coverImgUrl);
    }

    public void setDescription(String description) {
        this.description = description;
        params.put("description",description);
    }

    public void setLiveNum(String liveNum) {
        this.liveNum = liveNum;
        params.put("liveNum",liveNum);
    }

    public void setCodeRateTypes(String codeRateTypes) {
        this.codeRateTypes = codeRateTypes;
        params.put("codeRateTypes",codeRateTypes);
    }

    public void setNeedRecord(String needRecord) {
        this.needRecord = needRecord;
        params.put("needRecord",needRecord);
    }

    public void setNeedTimeShift(String needTimeShift) {
        this.needTimeShift = needTimeShift;
        params.put("needTimeShift",needTimeShift);
    }

    public void setNeedFullView(String needFullView) {
        this.needFullView = needFullView;
        params.put("needFullView",needFullView);
    }

    public void setActivityCategory(String activityCategory) {
        this.activityCategory = activityCategory;
        params.put("activityCategory",activityCategory);
    }

    public void setPlayMode(String playMode) {
        this.playMode = playMode;
        params.put("playMode",playMode);
    }
}
