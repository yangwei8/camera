package com.letv.lecloud.solar.upload;


public class Configuration {
    public static final long DEFAULT_CHIP_SIZE = 1 * 1024 * 1024; // 分片上传时候每片大小

    //    	public static final String QUERY_URI = "http://cloud.letv.com/uss/query";// 上传初始化的query_string (询问服务端)
    //public static final String QUERY_URI = "http://dev.cloud.letv.com/uss/query";// 上传初始化的query_string (询问服务端)
    public static final String QUERY_URI = "http://cloud.le.com/uss/query";// 上传初始化的query_string (询问服务端)--正式的

    public final String appkey = "tachograph";

    /**
     * 连接超时时间，单位 秒
     */
    public int connectTimeout = 10;

    /**
     * 服务器响应超时时间 单位 秒
     */
    public int responseTimeout = 60;

    /**
     * 上传失败重试次数，一次重试代表一次上传循环
     * 建议最小设置为 retryMax = 3
     */
    public int retryMax = 3;

}
