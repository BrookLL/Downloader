package com.riverlet.downloader;

import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Author liujian
 * Email: riverlet.liu@qq.com
 * Date: 2017/10/26.
 * Despribe:
 */

public class Downloader {
    public static final String TAG = "Downloader";

    public static final int OK = 200;
    public static final int FAILURE = 333;
    public static final int ERROR = 444;

    private DownloadCallback downloadCallback;
    private String url;
    private long offset = 0L;
    private boolean isCanResume = true;
    private boolean isPause = true;
    private boolean isFinished;
    private File file;

    private Downloader(String url, File file) {
        this.url = url;
        this.file = file;
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (isCanResume) {
            offset = RecordManager.getInstance().getLong(url);
            Log.d(TAG, url + "已下载：" + offset);
        }
    }

    public static Downloader newDownloader(String url) {
        if (TextUtils.isEmpty(url)) {
            throw new NullPointerException("URL can't be empty !");
        }
        String name = url.substring(url.lastIndexOf('/'));
        return newDownloader(url, name);
    }

    public static Downloader newDownloader(String url, String name) {
        return newDownloader(url, new File(DownloadConfig.getRootPath(), name));
    }

    public static Downloader newDownloader(String url, File file) {
        Downloader downloader = new Downloader(url, file);
        return downloader;
    }

    public Downloader download() {
        return download(offset, null);
    }

    private Downloader download(final long start, final Object total) {
        isPause = false;
        Observable<DownloadData> observable = Observable.create(new ObservableOnSubscribe<DownloadData>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<DownloadData> emitter) throws Exception {
                DownloadResponse response = HttpClient.getInstance().download(url, start, total);
                if (response.getCode() == OK) {
                    RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rwd");
                    randomAccessFile.seek(offset);
                    long total = response.getTotal() + offset;
                    long current = offset;
                    byte[] bytes = new byte[2 * 1024];
                    int length = 0;
                    int percentage = 0;
                    InputStream inputStream = response.getInputStream();
                    if (inputStream == null) {
                        Log.e(TAG, "InputStream is empty !");
                        emitter.onError(new Throwable("InputStream is empty !"));
                    }

                    DownloadData downloadData = new DownloadData();
                    downloadData.setTotal(total);

                    long lastSendTime = 0L;
                    try {
                        while ((length = inputStream.read(bytes)) != -1 && !isPause) {
                            current += length;
                            randomAccessFile.write(bytes, 0, length);
                            percentage = (int) (current * 100 / total);

                            if (System.currentTimeMillis() - lastSendTime > 500) {
                                downloadData.setCurrent(current);
                                downloadData.setPercentage(percentage);
                                emitter.onNext(downloadData);
                                lastSendTime = System.currentTimeMillis();
                                Log.d(TAG, "total:" + total + "...current:" + current);
                            }
                            if (percentage == 100) {
                                downloadData.setCurrent(current);
                                downloadData.setPercentage(percentage);
                                emitter.onNext(downloadData);
                                emitter.onComplete();
                                Log.d(TAG, "total:" + total + "...current:" + current);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        inputStream.close();
                        randomAccessFile.close();
                        if (isCanResume) {
                            if (current < total) {
                                offset = current;
                            } else {
                                offset = 0;
                            }
                            RecordManager.getInstance().put(url, offset);
                        }
                    }

                } else if (response.getCode() == FAILURE) {
                    emitter.onError(new Throwable("FAILURE"));
                } else if (response.getCode() == ERROR) {
                    emitter.onError(new Throwable("ERROR"));
                }
            }
        });
        final String fileName = file.getName();
        Observer<DownloadData> observer = new Observer<DownloadData>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "subscribe");
            }

            @Override
            public void onNext(DownloadData value) {
                if (downloadCallback != null) {
                    downloadCallback.onProgress(fileName, value.getTotal(), value.getCurrent(), value.getPercentage());
                    if (value.getPercentage() == 100) {
                        downloadCallback.onComplete(file);
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, e.toString());
                if (downloadCallback != null) {
                    if (TextUtils.equals(e.getMessage(), "ERROR")) {
                        downloadCallback.onError();
                    } else {
                        downloadCallback.onFailure();
                    }
                }
            }

            @Override
            public void onComplete() {
                isFinished = true;
                Log.d(TAG, "complete");
            }
        };
        //建立连接
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
        return this;
    }

    public void pause() {
        isPause = true;
    }

    public void restart() {
        download();
    }

    public boolean isPause() {
        return isPause;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public boolean isDownloading() {
        return !isFinished && !isPause;
    }

    public Downloader setDownloadCallback(DownloadCallback downloadCallback) {
        this.downloadCallback = downloadCallback;
        return this;
    }
}
