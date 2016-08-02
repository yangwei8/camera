package com.leautolink.leautocamera.domain.respone;

import java.util.List;

/**
 * Created by lixinlei on 16/3/14.
 */
public class VideoListInfo extends BaseRespone {
    private List<ClouldVideoInfo> data;

    public class ClouldVideoInfo{
        private String video_id;
        private String video_unique;
        private String video_name;
        private String img;
        private String init_pic;
        private String is_pay;
        private String video_duration;
        private String initial_size;
        private String error_code;
        private String error_desc;
        private String complete_time;
        private String add_time;
        private String isdrm;
        private String isdownload;
        private String video_desc;
        private String tag;
        private String file_md5;
        private String mid;
        private String status;

        public String getVideo_id() {
            return video_id;
        }

        public String getVideo_unique() {
            return video_unique;
        }

        public String getVideo_name() {
            return video_name;
        }

        public String getImg() {
            return img;
        }

        public String getInit_pic() {
            return init_pic;
        }

        public String getIs_pay() {
            return is_pay;
        }

        public String getVideo_duration() {
            return video_duration;
        }

        public String getInitial_size() {
            return initial_size;
        }

        public String getError_code() {
            return error_code;
        }

        public String getError_desc() {
            return error_desc;
        }

        public String getComplete_time() {
            return complete_time;
        }

        public String getAdd_time() {
            return add_time;
        }

        public String getIsdrm() {
            return isdrm;
        }

        public String getIsdownload() {
            return isdownload;
        }

        public String getVideo_desc() {
            return video_desc;
        }

        public String getTag() {
            return tag;
        }

        public String getFile_md5() {
            return file_md5;
        }

        public String getMid() {
            return mid;
        }

        public String getStatus() {
            return status;
        }
    }

    public List<ClouldVideoInfo> getData() {
        return data;
    }
}
