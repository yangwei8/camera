package com.leautolink.leautocamera.domain.respone;

/**
 * Created by lixinlei on 16/6/17.
 */
public class DiscoverInfos {
    private int id;
    private String mid;
    private String title;
    private int upCount;
    private int level;
    private int commentCount;
    private int browseCount;
    /**
     * 0  图片   、   1  视频
     */
    private int type;
    private long uploadDate;
    private String classifyId;
    private String imageurlDetail;
    private String thumbnail;
    private String location;

    private User user;

    private boolean up;

    public boolean isUp() {
        return up;
    }

    public int getId() {
        return id;
    }

    public String getMid() {
        return mid;
    }

    public String getTitle() {
        return title;
    }

    public String getImageurlDetail() {
        return imageurlDetail;
    }

    public String getThumbnail() {
        return thumbnail;
    }


    public int getUpCount() {
        return upCount;
    }

    public void setUpCount(int upCount) {
        this.upCount = upCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public int getBrowseCount() {
        return browseCount;
    }

    public void setBrowseCount(int browseCount) {
        this.browseCount = browseCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getType() {
        return type;
    }

    public long getUploadDate() {
        return uploadDate;
    }

    public String getClassifyId() {
        return classifyId;
    }

    public User getUser() {
        return user;
    }

    public int getLevel() {
        return level;
    }

    public String getLocation() {
        return location;
    }
}
