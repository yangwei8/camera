package com.leautolink.leautocamera.domain.respone;

/**
 * Created by lixinlei on 16/6/22.
 * 评论的实体
 */

public class CommentInfo {


    private String _id;
    private String content;
    private String vtime;
    private String ctime;
    private String city;
    private User user;

    public void setContent(String content) {
        this.content = content;
    }

    public void setVtime(String vtime) {
        this.vtime = vtime;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String get_id() {
        return _id;
    }

    public String getContent() {
        return content;
    }

    public String getVtime() {
        return vtime;
    }

    public String getCtime() {
        return ctime;
    }

    public String getCity() {
        return city;
    }

    public User getUser() {
        return user;
    }
}
