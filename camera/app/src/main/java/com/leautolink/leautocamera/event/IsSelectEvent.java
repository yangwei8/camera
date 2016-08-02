package com.leautolink.leautocamera.event;

/**
 * Created by lixinlei on 16/3/12.
 */
public class IsSelectEvent {
    private boolean selecting;

    public IsSelectEvent(boolean selecting) {
        this.selecting = selecting;
    }

    public boolean isSelecting() {
        return selecting;
    }

}
