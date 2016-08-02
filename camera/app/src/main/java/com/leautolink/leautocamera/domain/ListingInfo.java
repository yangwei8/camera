package com.leautolink.leautocamera.domain;

import com.leautolink.leautocamera.utils.Logger;

import java.io.Serializable;
import java.util.List;
//     {
//        "rval":0,
//        "msg_id":268435458,
//        "type":"event",
//        "listing":[
//        {
//        "filename":"Leauto_20210821_133256A.MP4",
//        "starttime":"2021-08-21 13:33:26",
//        "filesize":"51200KB"
//        }
//                     ],
//        "Amount":1,
//        "TotalSize":"51200KB"
//        }


/**
 * list命令返回的bean
 * Created by tianwei1 on 2016/2/29.
 */
public class ListingInfo implements Serializable {
    private static final String TAG = "ListingInfo";

    public ListingInfo(String type, boolean isLocal, List<FileInfo> listing) {
        this.listing = listing;
        this.type = type;
        this.isLocal = isLocal;
    }

    /**
     * rval : 0
     * msg_id : 268435458
     * type : event
     * listing : [{"filename":"Leauto_20210821_133256A.MP4","starttime":"2021-08-21 13:33:26","filesize":"51200KB"}]
     * Amount : 1
     * TotalSize : 51200KB
     */

    private int location;
    private int rval;
    private int msg_id;
    private String type;
    private int Amount;
    private String TotalSize;
    private boolean isLocal;

    public boolean isLocal() {
        return isLocal;
    }

    /**
     * filename : Leauto_20210821_133256A.MP4
     * starttime : 2021-08-21 13:33:26
     * filesize : 51200KB
     */

    private List<FileInfo> listing;

    public void setRval(int rval) {
        this.rval = rval;
    }

    public void setMsg_id(int msg_id) {
        this.msg_id = msg_id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAmount(int Amount) {
        this.Amount = Amount;
    }

    public void setTotalSize(String TotalSize) {
        this.TotalSize = TotalSize;
    }

    public void setListing(List<FileInfo> listing) {
        this.listing = listing;
    }

    public int getRval() {
        return rval;
    }

    public int getMsg_id() {
        return msg_id;
    }

    public String getType() {
        return type;
    }

    public int getAmount() {
        return Amount;
    }

    public String getTotalSize() {
        return TotalSize;
    }

    public List<FileInfo> getListing() {
        return listing;
    }

    public static class FileInfo implements Serializable {
        private String filename;
        private String starttime;
        private String filesize;
        private String length;
        private boolean isVideo;
        private boolean isPhoto;
        private String fileThumbname;
        private String localFileUrl;
        private boolean isChecked = false;

        public boolean isChecked() {
            return isChecked;
        }

        public void setIsChecked(boolean isChecked) {
            this.isChecked = isChecked;
        }

        public FileInfo(String filename, String starttime, String filesize, String length, boolean isVideo, boolean isPhoto, String localFileUrl) {
            this.filename = filename;

            this.starttime = starttime;
            this.filesize = filesize;
            this.length = length;
            this.isVideo = isVideo;
            this.isPhoto = isPhoto;
            this.localFileUrl = localFileUrl;

        }


        public String getLocalFileUrl() {
            return localFileUrl;
        }

        public String getFileThumbname() {
            if (filename.endsWith(".MP4")) {
                fileThumbname = filename.replace("A.MP4", "T.JPG");
            } else if (filename.endsWith(".JPG")) {
                fileThumbname = filename.replace("A.JPG", "T.JPG");
            }
            return fileThumbname;
        }

        public String getLength() {
            return length;
        }

        public void setLength(String length) {
            this.length = length;
        }


        public boolean isVideo() {
            justFlieType();
            return isVideo;
        }

        public boolean isPhoto() {
            justFlieType();
            return isPhoto;
        }

        private void justFlieType() {
            if (filename.endsWith(".MP4")) {
                isVideo = true;
            } else if (filename.endsWith(".JPG")) {
                isPhoto = true;
            }
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public void setStarttime(String starttime) {

            this.starttime = starttime;
        }

        public void setFilesize(String filesize) {
            this.filesize = filesize;
        }

        public String getFilename() {
            return filename;
        }

        /**
         * 获取预览界面title显示的时间
         * @return
         */
        public String getPreviewTitle(){
            String title ="";
            if (isVideo()) {
                title = getFilename().substring(7, 22);
            } else if (isPhoto()) {
                title = getFilename().substring(7, 24);
            }
            return title;
        }

        public String getStarttime() {
//            StringBuffer sb = new StringBuffer();
////            sb.append(starttime);
//            if (starttime.indexOf("_", 0) == 8) {
//                sb.append(starttime.substring(0, 4));
//                sb.append("-");
//                sb.append(starttime.substring(4, 6));
//                sb.append("-");
//                sb.append(starttime.substring(6, 8));
//                sb.append(" ");
//                sb.append(starttime.substring(9, 11));
//                sb.append(":");
//                sb.append(starttime.substring(11, 13));
//                sb.append(":");
//                sb.append(starttime.substring(13, 15));
//            } else {
//                sb.append(starttime);
//                if (sb.indexOf(" ", 0) != 10) {
//                    sb.insert(10, " ");
//                }
//            }
//            starttime = sb.toString();
            if (isVideo) {
                starttime = getFilename().substring(7, 22);
            } else if (isPhoto) {
                starttime = getFilename().substring(7, 24);
            }
            Logger.e(TAG, "starttime: " + starttime);
            return starttime;
        }

        public String getFilesize() {
            return filesize;
        }

        @Override
        public String toString() {
            return "FileInfo{" +
                    "filename='" + filename + '\'' +
                    ", starttime='" + starttime + '\'' +
                    ", filesize='" + filesize + '\'' +
                    ", length='" + length + '\'' +
                    ", isVideo=" + isVideo +
                    ", isPhoto=" + isPhoto +
                    ", fileThumbname='" + fileThumbname + '\'' +
                    ", localFileUrl='" + localFileUrl + '\'' +
                    ", isChecked=" + isChecked +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ListingInfo{" +
                "rval=" + rval +
                ", msg_id=" + msg_id +
                ", type='" + type + '\'' +
                ", Amount=" + Amount +
                ", TotalSize='" + TotalSize + '\'' +
                ", listing=" + listing +
                '}';
    }
}