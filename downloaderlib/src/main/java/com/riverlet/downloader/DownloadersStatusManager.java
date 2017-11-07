package com.riverlet.downloader;

import java.util.HashMap;
import java.util.Map;

/**
 * Author liujian
 * Email: riverlet.liu@mopo.com
 * Date: 2017/11/1.
 * Despribe:
 */

public class DownloadersStatusManager {
    public static final int READY = 101;
    public static final int DOWNLOADING = 102;
    public static final int ALREADY_EXIST = 103;
    public static final int PAUSE = 104;
    private static Map<String, Integer> downloadStausMap = new HashMap<>();

    /**
     * 储存下载状态
     *
     * @param url
     * @param status
     */
    public static void put(String url, int status) {
        downloadStausMap.put(url, status);
    }

    /**
     * 获取下载状态
     *
     * @param url
     * @return
     */
    public static int get(String url) {
        if (!downloadStausMap.containsKey(url)) {
            return READY;
        }
        return downloadStausMap.get(url);
    }
}
