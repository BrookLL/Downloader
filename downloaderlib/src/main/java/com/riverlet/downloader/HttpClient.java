package com.riverlet.downloader;

import android.util.Log;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Author liujian
 * Email: riverlet.liu@qq.com
 * Date: 2017/10/26.
 * Despribe:
 */

public class HttpClient {
    private static final String TAG = "HttpClient";
    private OkHttpClient okHttpClient;
    private static volatile HttpClient instance;

    private HttpClient() {
        okHttpClient = new OkHttpClient.Builder().build();
    }

    public static HttpClient getInstance() {
        if (instance == null) {
            synchronized (HttpClient.class) {
                if (instance == null) {
                    instance = new HttpClient();
                }
            }
        }
        return instance;
    }


    public DownloadResponse download(String url, long start, Object total) {
        DownloadResponse downloadResponse = new DownloadResponse();
        String range = "bytes=" + start + "-" + (total == null ? "" : total);
        Log.d(TAG, "range:" + range);
        try {
            Request request = new Request.Builder().
                    url(url).
                    addHeader("Range", range).
                    get().
                    build();
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                downloadResponse.setCode(Downloader.OK);
                downloadResponse.setInputStream(response.body().byteStream());
                downloadResponse.setTotal(response.body().contentLength());
            } else {
                downloadResponse.setCode(Downloader.FAILURE);
            }
        } catch (IOException e) {
            e.printStackTrace();
            downloadResponse.setCode(Downloader.ERROR);
            downloadResponse.setMessage("下载出错");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            downloadResponse.setCode(Downloader.ERROR);
            downloadResponse.setMessage("Url不合法");
        }
        return downloadResponse;
    }
}
