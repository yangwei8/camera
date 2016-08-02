package com.leautolink.leautocamera.domain.respone;

/**
 * Created by lixinlei on 16/7/8.
 */
public class ShareAndUpCountInfo extends BaseInfo<Object>{
    private ShareAndUpCount map;

    public ShareAndUpCount getMap() {
        return map;
    }

    public class ShareAndUpCount{

        private int shareCount = 0;
        private int upCount = 0;

        public int getShareCount() {
            return shareCount;
        }

        public int getUpCount() {
            return upCount;
        }
    }


}
