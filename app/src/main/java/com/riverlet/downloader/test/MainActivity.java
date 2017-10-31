package com.riverlet.downloader.test;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.riverlet.downloader.DownloadCallback;
import com.riverlet.downloader.Downloader;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    String url = "http://ucdl.25pp.com/fs01/union_pack/Wandoujia_194547_web_seo_baidu_homepage.apk";

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView textView = (TextView) findViewById(R.id.text);
        final Downloader downloader = Downloader.newDownloader(url);
        downloader.setDownloadCallback(new DownloadCallback() {
            @Override
            public void onComplete(File file) {
                Log.d("MainActivity", "file:" + file.toString());
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                startActivity(intent);
            }

            @Override
            public void onProgress(String fileName, long total, long current, int percentage) {
                Log.d("MainActivity", "percentage:" + percentage);
                textView.setText(fileName + ":" + percentage + "%");
            }
        });

        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (downloader.isPause()) {
                    downloader.restart();
                } else {
                    downloader.pause();
                }
            }
        });

    }
}
