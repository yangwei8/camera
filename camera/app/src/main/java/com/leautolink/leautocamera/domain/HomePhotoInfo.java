package com.leautolink.leautocamera.domain;

import com.leautolink.leautocamera.config.Config;


/**
 * Created by lixinlei on 16/4/7.
 */
public class HomePhotoInfo {
    /**
     * 拍照的缩略图图片  type
     */
    public static int NORMAL_PIC = 0;

    /**
     * 紧急事件的缩略图的图片  type
     */
    public static int EVENT_PIC = 1;


    private String name;
    /**
     * 可取值  NORMAL_PIC 、EVENT_PIC
     */
    private int type;


    public HomePhotoInfo(String name, int type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {

        String tempName = name.substring(11, 22);
        String newName = tempName.substring(0, 2) + "/" + tempName.substring(2, 4) + " " + tempName.substring(5, 7) + ":" + tempName.substring(7, 9) + ":" + tempName.substring(9);

        return newName;
    }

    public String getOrginalName() {
        return name;
    }


    public String getPath() {
        return type == 0 ? Config.HTTP_PHOTO_THUMB_PATH + name : Config.HTTP_EVENT_THUMB_PATH + name;
    }

    public int getType() {
        return type;
    }

    @Override
    public String toString() {
        return "HomePhotoInfo{" +
                "name='" + name + '\'' +
                ", type=" + type +
                '}';
    }
}
