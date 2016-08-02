package com.leautolink.leautocamera.domain.respone;

import com.leautolink.leautocamera.config.Config;

/**
 * Created by tianwei1 on 2016/3/14.
 */
public class UpLoadResponse {

    /**
     * modulename : camera
     * status : 1
     * tag : ext-gen328
     * file : camera/Leauto_20210906_17000900A.JPG
     * data : {"file":"camera/Leauto_20210906_17000900A.JPG"}
     * tokenid : fc8e42eb8fd1474d8f040c18f442190b
     */

    private String modulename;
    private int status;
    private String tag;
    private String file;
    private String url;

    public String getUrl() {
        return Config.SHARE_FILE_URL  + file;
    }

    /**
     * file : camera/Leauto_20210906_17000900A.JPG
     */

    private DataEntity data;
    private String tokenid;

    public void setModulename(String modulename) {
        this.modulename = modulename;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public void setData(DataEntity data) {
        this.data = data;
    }

    public void setTokenid(String tokenid) {
        this.tokenid = tokenid;
    }

    public String getModulename() {
        return modulename;
    }

    public int getStatus() {
        return status;
    }

    public String getTag() {
        return tag;
    }

    public String getFile() {
        return file;
    }

    public DataEntity getData() {
        return data;
    }

    public String getTokenid() {
        return tokenid;
    }

    public static class DataEntity {
        private String file;

        public void setFile(String file) {
            this.file = file;
        }

        public String getFile() {
            return file;
        }
    }
}
