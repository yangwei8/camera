package com.leautolink.leautocamera.domain.respone;

import java.util.List;

/**
 * Created by lixinlei on 16/6/22.
 */
public class BannerInfo {
    private int code;
    private BannerData data;

    public class BannerData{
        private List<BannerContent> blockContent;

        public class BannerContent{
            private String mobilePic;
            private String url;

            public String getMobilePic() {
                return mobilePic;
            }

            public String getUrl() {
                return url;
            }
        }

        public List<BannerContent> getBlockContent() {
            return blockContent;
        }
    }

    public int getCode() {
        return code;
    }

    public BannerData getData() {
        return data;
    }
}
