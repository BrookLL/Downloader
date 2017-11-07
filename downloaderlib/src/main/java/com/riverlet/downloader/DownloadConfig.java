package com.riverlet.downloader;

import android.os.Environment;

import java.io.File;

/**
 * Author liujian
 * Email: riverlet.liu@qq.com
 * Date: 2017/10/26.
 * Despribe:
 */

public class DownloadConfig {
    public static String rootPath = Environment.getExternalStorageDirectory().toString() + File.separator + Environment.DIRECTORY_DOWNLOADS + File.separator;

    /**
     * 设置下载根目录
     * @return
     */
    public static String getRootPath() {
        return rootPath;
    }

    /**
     * 获取下载根目录
     * @param rootPath
     */
    public static void setRootPath(String rootPath) {
        DownloadConfig.rootPath = rootPath;
    }
}
