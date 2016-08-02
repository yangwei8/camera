package com.leautolink.leautocamera.domain.respone;

/**
 * Created by lixinlei on 16/6/21.
 */
public class User {
    private String uid;
    private String username;
    private String photo;
    private int isvip;

    public User(String uid, String username, String photo, int isvip) {
        this.uid = uid;
        this.username = username;
        this.photo = photo;
        this.isvip = isvip;
    }

    public String getUid() {
        return uid;
    }

    public String getUsername() {
        return username;
    }

    public String getPhoto() {
        return photo;
    }

    public boolean isvip() {
        return isvip == 1 ;
    }
}
