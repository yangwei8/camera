package com.leautolink.leautocamera.domain.respone;

/**
 * Created by lixinlei on 16/6/23.
 */
public class VideoInfo extends BaseInfo<Object>{

    private PlayInfo obj;

    public PlayInfo getObj() {
        return obj;
    }

    public class PlayInfo {
        private String vtype;
        private String backUrl0;
        private String backUrl1;
        private String backUrl2;
        private String mainUrl;
        private String cdnUrl;


        public String getVtype() {
            return vtype;
        }

        public String getBackUrl0() {
            return backUrl0;
        }

        public String getBackUrl1() {
            return backUrl1;
        }

        public String getBackUrl2() {
            return backUrl2;
        }

        public String getMainUrl() {
            return mainUrl;
        }

        public String getCdnUrl() {
            return cdnUrl;
        }
    }
}
