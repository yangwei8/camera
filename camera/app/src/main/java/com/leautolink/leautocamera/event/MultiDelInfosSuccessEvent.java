package com.leautolink.leautocamera.event;

import java.util.List;

/**
 * Created by lixinlei on 16/3/12.
 */
public class MultiDelInfosSuccessEvent {
    private List<Integer> delSuccessIndexs;
    private String type;
    public MultiDelInfosSuccessEvent(List<Integer> delSuccessIndexs,String type) {
        this.delSuccessIndexs = delSuccessIndexs;
        this.type=type;
    }

    public List<Integer> getDelSuccessIndexs() {
        return delSuccessIndexs;
    }

    public String getType() {
        return type;
    }
}
