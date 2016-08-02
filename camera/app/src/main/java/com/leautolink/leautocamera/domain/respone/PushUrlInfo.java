package com.leautolink.leautocamera.domain.respone;

import java.util.List;

/**
 * Created by lixinlei on 16/3/11.
 */
public class PushUrlInfo {
    private int liveNum;

    private List<LiveMachineInfo> lives;

    public int getLiveNum() {
        return liveNum;
    }

    public List<LiveMachineInfo> getLives() {
        return lives;
    }

    public class LiveMachineInfo{
        private String machine;
        private String pushUrl;
        private String status;
        private String streamId;

        public String getMachine() {
            return machine;
        }

        public String getPushUrl() {
            return pushUrl;
        }

        public String getStatus() {
            return status;
        }

        public String getStreamId() {
            return streamId;
        }
    }

}
