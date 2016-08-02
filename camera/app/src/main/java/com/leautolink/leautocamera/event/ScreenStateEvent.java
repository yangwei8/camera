package com.leautolink.leautocamera.event;

/**
 * Created by tianwei on 16/4/7.
 */
public class ScreenStateEvent {
    private boolean isScreenOn;

    public ScreenStateEvent(boolean isScreenOn) {
        this.isScreenOn = isScreenOn;
    }

    public boolean isScreenOn() {
        return isScreenOn;
    }
}
