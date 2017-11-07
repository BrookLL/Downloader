package com.riverlet.downloader;

import java.io.File;

/**
 * Author liujian
 * Email: riverlet.liu@qq.com
 * Date: 2017/10/26.
 * Despribe:
 */

public abstract class DownloadCallback {
    //下载进度回调
    public void onProgress(String fileName, long total, long current, int percentage) {

    }

    //完成回调
    public abstract void onComplete(File file);

    //失败回调
    public void onFailure() {
    }

    //错误回调
    public void onError(int errorCode, String errorMessage) {
    }
}
