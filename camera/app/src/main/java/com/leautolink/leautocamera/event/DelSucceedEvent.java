package com.leautolink.leautocamera.event;

/**
 * 删除成功的Event
 * Created by tianwei1 on 2016/3/10.
 */
public class DelSucceedEvent {
    private int mRemovedPosition;
    private String mType;

    public DelSucceedEvent(int removedPosition, String type) {
        mRemovedPosition = removedPosition;
        mType = type;
    }

    public int getRemovedPosition() {
        return mRemovedPosition;
    }

    public String getType() {
        return mType;
    }
}
