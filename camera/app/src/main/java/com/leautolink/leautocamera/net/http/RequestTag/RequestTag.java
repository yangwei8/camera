package com.leautolink.leautocamera.net.http.RequestTag;

/**
 * Created by lixinlei on 16/6/17.d
 */
public class RequestTag {
    //    private static final String BASE_URL ="http://camera.leautolink.com/";
//    private static final String BASE_URL = "http://10.112.33.31/";
    /**
     * 正式环境的域名
     */
    private static final String BASE_URL = "http://camera.leautolink.com/";
//    private static final String BASE_URL = "http://10.58.184.48:8080/lecamera/";

    /**
     * 上传视频之前请求token的url
     */
    public static final String UPLOAD_VIDEO_REQUEST_TOKEN_URL = BASE_URL + "api/resource/uploadToken.do";

    public static final String UPLOAD_VIDEO_TACHOGRAPH = BASE_URL + "api/resource/tachographVideo.do";


    /**
     * 上传图片的url
     */
    public static final String UPLOAD_PHOTO_URL = BASE_URL + "api/resource/uploadPicture.do";

    /**
     * 获取发现页的分类
     */
    public static final String TAB_TAG = "tab_tag";

    public static final String TAB_URL = BASE_URL + "api/classify/findClassifies.do";


    /**
     * 获取发现页的每条分类的详细信息
     */
    public static final String TAB_DETAIL_TAG = "tab_detail_tag";

    public static final String TAB_DETAIL_URL = BASE_URL + "api/resource/findResourcesByParam.do";


    /**
     * 点赞
     */
    public static final String UP_TAG = "up_tag";

    public static final String UP_TAG_URL = BASE_URL + "api/resource/upResourceById.do";

    /**
     * 评论列表
     */
    public static final String COMMENT_TAG = "up_tag";

    public static final String COMMENT_TAG_URL = BASE_URL + "api/resource/findCommentsByParam.do";


    /**
     * 添加评论
     */
    public static final String SEND_COMMENT_TAG = "send_comment_tag";

    public static final String SEND_COMMENT_TAG_URL = BASE_URL + "api/resource/addComment.do";

    /**
     * 获取Banner图
     */
    public static final String BANNER_TAG = "banner_tag";

    public static final String BANNER_TAG_URL = "http://static.api.letv.com/blockNew/get?id=7307&platform=mobile";
    /**
     * 获取播放URL
     */
    public static final String PLAY_URL_TAG = "play_tag";

    public static final String PLAY_URL_TAG_URL = BASE_URL + "api/resource/playUrl.do";

    /**
     * 获取我的分享
     */
    public static final String MY_SHARE_TAG = "share_tag";

    public static final String MY_SHARE_TAG_URL = BASE_URL + "api/resource/findResourcesShareByParam.do";

    /**
     * 获取我的点赞
     */
    public static final String MY_UP_TAG = "share_tag";

    public static final String MY_UP_TAG_URL = BASE_URL + "api/resource/findResourcesUpByParam.do";

    /**
     * 获取我的分享
     */
    public static final String SHARE_AND_UP_TAG = "share_adn_tag";

    public static final String SHARE_AND_UP_TAG_URL = BASE_URL + "api/resource/findResourcesShareAndUpCount.do";

    /**
     * 获取视频详情
     */
    public static final String VIDEO_DETAIL_TAG = "video_detail_tag";

    public static final String VIDEO_DETAIL_TAG_URL = BASE_URL + "api/resource/findResourceById.do";
    /**
     * 视频详情页分享
     */
    public static final String DETAIL_SHARE_URL = BASE_URL + "api/resource/shareResource.do";
    //public static final String DETAIL_SHARE_URL = "http://10.112.33.31/lecamera/api/resource/shareResource.do";

}
