package com.leautolink.leautocamera.domain;

/**
 * Created by lixinlei on 16/4/7.
 */
public class AddFileInfo {
//    {"type":"normal",
//      "filename":"Leauto_20210906_151300A.MP4",
//      "starttime":"2021-09-0615:13:00",
//      "filesize":"97280KB",
//      "length":"60",
//      "width":"1920",
//      "height":"1080",
//      "HDR":"1"}
    private String type;
    private String filename;
    private String starttime;
    private String filesize;
    private String length;
    private String width;
    private String height;
    private String HDR;

    public String getType() {
        return type;
    }

    public String getFilename() {
        return filename;
    }

    public String getStarttime() {
        return starttime;
    }

    public String getFilesize() {
        return filesize;
    }

    public String getLength() {
        return length;
    }

    public String getWidth() {
        return width;
    }

    public String getHeight() {
        return height;
    }

    public String getHDR() {
        return HDR;
    }
}
