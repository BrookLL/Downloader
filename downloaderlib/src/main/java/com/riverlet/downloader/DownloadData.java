package com.riverlet.downloader;

import java.io.File;

/**
 * Author liujian
 * Email: riverlet.liu@qq.com
 * Date: 2017/10/26.
 * Despribe:
 */

public class DownloadData {
    private long total;
    private long current;
    private int percentage;
    private File file;

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getCurrent() {
        return current;
    }

    public void setCurrent(long current) {
        this.current = current;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
