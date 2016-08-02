package com.leautolink.leautocamera.event;

import com.leautolink.leautocamera.domain.respone.DiscoverInfos;

/**
 * Created by lixinlei on 16/7/15.
 */
public class NeedUpdateDiscoverListEvent {
    private DiscoverInfos discoverInfos;
    public NeedUpdateDiscoverListEvent(DiscoverInfos discoverInfos) {
        this.discoverInfos = discoverInfos;
    }

    public DiscoverInfos getDiscoverInfos() {
        return discoverInfos;
    }
}
