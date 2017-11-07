package com.riverlet.downloader.test;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.riverlet.downloader.DownloadCallback;
import com.riverlet.downloader.DownloadConfig;
import com.riverlet.downloader.Downloader;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView fileNameText;
    private TextView progressText;
    private ProgressBar progressBar;
    private EditText urlEdit;
    private EditText fileEdit;
    private Downloader downloader;
    private boolean isOpen;
    private String defaultUrl = "http://ucdl.25pp.com/fs01/union_pack/Wandoujia_194547_web_seo_baidu_homepage.apk";

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
    }

    private void initView() {
        fileNameText = bindView(R.id.text_file_name);
        progressText = bindView(R.id.text_progress);
        progressBar = bindView(R.id.progress);
        urlEdit = bindView(R.id.edit_url);
        fileEdit = bindView(R.id.edit_file);
        urlEdit.setHint(defaultUrl);
        String fileName = defaultUrl.substring(defaultUrl.lastIndexOf('/') + 1);
        fileEdit.setText(DownloadConfig.getRootPath() + fileName);
        fileNameText.setText("文件名：" + fileName);

        bindView(R.id.btn_start).setOnClickListener(this);
        bindView(R.id.btn_pause).setOnClickListener(this);
        bindView(R.id.btn_open).setOnClickListener(this);

        final String sdcard = Environment.getExternalStorageDirectory().getPath() + File.separator;

        fileEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String path = s.toString();
                if (!path.startsWith(sdcard)) {
                    fileEdit.setText(sdcard);
                    fileEdit.setSelection(sdcard.length());
                    Toast.makeText(MainActivity.this, "请设置正确的文件路径", Toast.LENGTH_SHORT).show();
                }
            }
        });

        urlEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String url = editable.toString();
               if (url.lastIndexOf('/')>0){
                   String fileName = url.substring(url.lastIndexOf('/') + 1);
                   fileEdit.setText(DownloadConfig.getRootPath() + fileName);
                   fileNameText.setText("文件名：" + fileName);
               }
               if (url.length()==0){
                   String fileName = defaultUrl.substring(url.lastIndexOf('/') + 1);
                   fileEdit.setText(DownloadConfig.getRootPath() + fileName);
                   fileNameText.setText("文件名：" + fileName);
               }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
                download();
                break;
            case R.id.btn_pause:
                if (downloader != null) {
                    downloader.pause();
                }
                break;
            case R.id.btn_open:
                File file = new File(DownloadConfig.getRootPath());
                if (!file.exists()) {
                    file.mkdirs();
                }
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(Uri.fromFile(file), "*/*");
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void download() {
        String url = urlEdit.getText().toString();
        if (TextUtils.isEmpty(url)) {
            url = this.defaultUrl;
        }

        File file = new File(fileEdit.getText().toString());
        if (file.isDirectory()) {
            String fileName = url.substring(url.lastIndexOf('/') + 1);
            file = new File(file, fileName);
            fileEdit.setText(file.getPath());
        }

        if (downloader != null && TextUtils.equals(downloader.getUrl(), url)) {
            downloader.restart();
            return;
        }
        downloader = Downloader.newDownloader(url, file);
        downloader.setDownloadCallback(new DownloadCallback() {
            @Override
            public void onComplete(File file) {
                Log.d("MainActivity", "file:" + file.toString());
                if (isOpen) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                    startActivity(intent);
                }
            }

            @Override
            public void onProgress(String fileName, long total, long current, int percentage) {
                Log.d("MainActivity", "percentage:" + percentage);
                progressText.setText(percentage + "%");
                progressBar.setProgress(percentage);
                fileNameText.setText("文件名：" + fileName);
            }

            @Override
            public void onError(int errorCode, String errorMessage) {
                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
        downloader.download();
    }

    private <T extends View> T bindView(int id) {
        return (T) findViewById(id);
    }
}
