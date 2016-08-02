package com.leautolink.leautocamera.event;

import java.util.List;

/**
 * Created by tianwei1 on 2016/3/14.
 */
public class BatchDelSucceedEvent {
    private List<Integer> mRemovedList;
    private String mType;

    public BatchDelSucceedEvent(List<Integer> removedList, String type) {
        mRemovedList = removedList;
        mType = type;
    }

    public List<Integer> getRemovedList() {
        return mRemovedList;
    }

    public String getType() {
        return mType;
    }
}
