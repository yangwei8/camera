package com.leautolink.leautocamera.domain.respone;

/**
 * Created by tianwei on 16/6/23.
 */
public class UploadVideoResponseInfo {


    /**
     * size : 20971520
     * appkey : test
     * downloadUrl : http://blank
     * fileid : b5efc3cd41522781d51151ec2e99ce39f6cbca7f
     * mime : video/3gpp
     * complete : true
     * nodeId : 3000
     * upload : 1
     */

    private ResultBean result;
    /**
     * result : {"size":20971520,"appkey":"test","downloadUrl":"http://blank","fileid":"b5efc3cd41522781d51151ec2e99ce39f6cbca7f","mime":"video/3gpp","complete":true,"nodeId":"3000","upload":1}
     * msg : ok
     * code : 2000
     */

    private String msg;
    private int code;

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public static class ResultBean {
        private int size;
        private String appkey;
        private String downloadUrl;
        private String fileid;
        private String mime;
        private boolean complete;
        private String nodeId;
        private int upload;

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public String getAppkey() {
            return appkey;
        }

        public void setAppkey(String appkey) {
            this.appkey = appkey;
        }

        public String getDownloadUrl() {
            return downloadUrl;
        }

        public void setDownloadUrl(String downloadUrl) {
            this.downloadUrl = downloadUrl;
        }

        public String getFileid() {
            return fileid;
        }

        public void setFileid(String fileid) {
            this.fileid = fileid;
        }

        public String getMime() {
            return mime;
        }

        public void setMime(String mime) {
            this.mime = mime;
        }

        public boolean isComplete() {
            return complete;
        }

        public void setComplete(boolean complete) {
            this.complete = complete;
        }

        public String getNodeId() {
            return nodeId;
        }

        public void setNodeId(String nodeId) {
            this.nodeId = nodeId;
        }

        public int getUpload() {
            return upload;
        }

        public void setUpload(int upload) {
            this.upload = upload;
        }
    }
}
