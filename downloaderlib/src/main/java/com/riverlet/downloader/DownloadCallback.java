package com.riverlet.downloader;

import java.io.File;

/**
 * Author liujian
 * Email: riverlet.liu@qq.com
 * Date: 2017/10/26.
 * Despribe:
 */

public abstract class DownloadCallback {
    public void onProgress(String fileName,long total, long current, int percentage) {

    }

    public abstract void onComplete(File file);

    public void onFailure() {
    }

    public void onError() {
    }
}
