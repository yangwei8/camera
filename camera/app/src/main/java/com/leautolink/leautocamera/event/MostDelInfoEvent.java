package com.leautolink.leautocamera.event;

import java.util.List;

/**
 * Created by lixinlei on 16/3/12.
 */
public class MostDelInfoEvent {
    private List<Integer> delIndexs;

    public MostDelInfoEvent(List<Integer> delIndexs) {
        this.delIndexs = delIndexs;
    }

    public List<Integer> getDelIndexs() {
        return delIndexs;
    }
}
