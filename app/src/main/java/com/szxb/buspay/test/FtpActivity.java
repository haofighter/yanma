package com.szxb.buspay.test;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.szxb.buspay.R;
import com.szxb.mlog.SLog;

/**
 * 作者：Tangren on 2018-09-19
 * 包名：com.szxb.buspay.test
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class FtpActivity extends AppCompatActivity implements OnDownProgress {

    private AppDownload appDownload;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appDownload = new AppDownload();


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    appDownload.setOnDownProgress(FtpActivity.this);
                    boolean ftpuser = appDownload.connect("222.135.141.247", 21, "YTJY-GJSF-SVR\\ftp", "f1234!@#$tp");
                    SLog.d("FtpActivity(run.java:35)ftp连接成功="+ftpuser);

                    appDownload.download("test.apk",Environment.getExternalStorageDirectory() + "/"+"test6.apk");

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("FtpActivity",
                            "run(FtpActivity.java:39)" + e.toString());
                }

            }
        }).start();

    }

    @Override
    public void onProgress(long progress) {
        Log.d("FtpActivity",
                "onProgress(FtpActivity.java:44)" + progress);
    }

    @Override
    public void onFinish(String filePath) {
        Log.d("FtpActivity",
                "onFinish(FtpActivity.java:51)" + filePath);
    }

    @Override
    public void onDownloadError(String exception) {
        Log.d("FtpActivity",
                "onDownloadError(FtpActivity.java:57)" + exception);
    }
}
