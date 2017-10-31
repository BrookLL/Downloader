package com.riverlet.downloader;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Author liujian
 * Email: riverlet.liu@qq.com
 * Date: 2017/10/27.
 * Despribe:
 */

public class RecordManager {
    private static final String TAG = "RecordManager";
    private ExecutorService excutorService = Executors.newSingleThreadExecutor();
    private static RecordManager instance;
    private JSONObject cache;

    private RecordManager() {
        init();
    }

    private void init() {
        String data = read();
        if (TextUtils.isEmpty(data)) {
            cache = new JSONObject();
        } else {
            try {
                cache = new JSONObject(data);
            } catch (JSONException e) {
                e.printStackTrace();
                cache = new JSONObject();
            }
        }

    }

    public static RecordManager getInstance() {
        if (instance == null) {
            synchronized (RecordManager.class) {
                if (instance == null) {
                    instance = new RecordManager();
                }
            }
        }
        return instance;
    }

    public void put(String key, Object value) {
        try {
            cache.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        excutorService.execute(new Runnable() {
            @Override
            public void run() {
                write(cache.toString());
            }
        });
    }

    public Object get(String key) {
        Object o = null;
        try {
            o = cache.get(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return o;
    }

    public String getString(String key) {
        String o = null;
        try {
            o = cache.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return o;
    }

    public long getLong(String key) {
        long o = 0L;
        try {
            o = cache.getLong(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return o;
    }


    private void write(String data) {
        File file = new File(DownloadConfig.getRootPath(), "download_record");
        if (!file.getParentFile().exists()) {
            file.mkdirs();
        }
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(file), 1024);
            bufferedWriter.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.flush();
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String read() {
        File file = new File(DownloadConfig.getRootPath(), "download_record");
        if (!file.getParentFile().exists()) {
            file.mkdirs();
            return null;
        }
        String readString = "";
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));

            String currentLine;
            while ((currentLine = bufferedReader.readLine()) != null) {
                readString += currentLine;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.d(TAG,"read:"+readString);
        return readString;
    }
}
