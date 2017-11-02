package com.riverlet.downloader;

import java.util.LinkedList;
import java.util.Queue;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Author liujian
 * Email: riverlet.liu@qq.com
 * Date: 2017/10/27.
 * Despribe:
 */

public class DownloadQueenManager {

    private static volatile DownloadQueenManager instance;
    private static Queue<Downloader> downloaderQueue = new LinkedList<>();
    private boolean stop = false;
    private long time;

    private DownloadQueenManager() {
        start();
    }

    public static DownloadQueenManager getInstance() {
        if (instance == null) {
            synchronized (DownloadQueenManager.class) {
                if (instance == null) {
                    instance = new DownloadQueenManager();
                }
            }
        }
        return instance;
    }

    public void add(Downloader downloader) {
        if (downloader == null) {
            return;
        }
        downloaderQueue.add(downloader);
    }

    public void add(String url) {
        add(url, null);
    }

    public void add(String url, DownloadCallback downloadCallback) {
        Downloader downloader = Downloader.newDownloader(url);
        downloader.setDownloadCallback(downloadCallback);
        downloaderQueue.add(downloader);
    }

    private void start() {
        stop = false;
        observable.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(observer);
    }

    Observable<Downloader> observable = Observable.create(new ObservableOnSubscribe<Downloader>() {
        @Override
        public void subscribe(ObservableEmitter<Downloader> emitter) throws Exception {
            while (!stop) {
                if (downloaderQueue.size() > 0) {
                    Downloader downloader = downloaderQueue.poll();
                    emitter.onNext(downloader);
                    time = System.currentTimeMillis();
                } else {
                    if (System.currentTimeMillis() - time > 30 * 1000) {
                        stop = true;
                    }
                }
            }
        }
    });

    Observer<Downloader> observer = new Observer<Downloader>() {
        @Override
        public void onSubscribe(Disposable d) {

        }

        @Override
        public void onNext(Downloader downloader) {
            downloader.download();
        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onComplete() {

        }
    };
}
