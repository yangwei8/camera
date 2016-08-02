package com.letv.leauto.cameracmdlibrary.connect.event;

/**
 * Created by tianwei1 on 2016/1/7.
 */
public class CheckSdStatusFinished {
    private boolean mIsCheckSdStatusFinished;
    public CheckSdStatusFinished(boolean isCheckSdStatusFinished) {
        mIsCheckSdStatusFinished = isCheckSdStatusFinished;
    }

    public boolean isCheckSdStatusFinished() {
        return mIsCheckSdStatusFinished;
    }
}
