package com.letv.lecloud.solar.upload;


import com.letv.lecloud.solar.http.HttpManager;
import com.letv.lecloud.solar.http.ResponseInfo;
import com.letv.lecloud.solar.utils.AsyncRun;

import java.io.File;

/**
 * Created by wiky on 15/12/18.
 */
public final class UploadManager {
    private final Configuration config;
    private final HttpManager httpManager;


    public UploadManager() {
        this(new Configuration());
    }
    public UploadManager(Configuration config) {
        this.config = config;
        this.httpManager = new HttpManager(config.connectTimeout, config.responseTimeout);
    }

    /**
     * 上传文件
     *
     * @param filePath          上传的文件路径
     * @param token             上传凭证
     * @param upResponseHandler 上传完成的后续处理动作
     * @param upProgressHandler 进度更新
     */
    public FileUploader put(String filePath, String token, UpResponseHandler upResponseHandler, UpProgressHandler upProgressHandler) {

        FileUploader uploader = new FileUploader(httpManager, config, filePath,
                token, upResponseHandler, upProgressHandler);
        AsyncRun.run(uploader);

        return uploader;
    }

}
