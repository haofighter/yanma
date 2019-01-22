package com.szxb.buspay.test;

import android.util.Log;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * 作者: Tangren on 2017/7/27
 * 包名：com.szxb.ftp
 * 邮箱：996489865@qq.com
 * TODO:FTP工具类
 */

public class FTP2 {

    private String url;
    private int port;
    private String username;
    private String password;
    private String path;
    private String ftpPath;
    private String[] fileName;
    private String sinfileName;
    private int retryCount = 1;
    private List<InputStream> input;


    private String[] ftpPaths;
    private String packgeName;

    public FTP2 builder(String url) {
        this.url = url;
        return this;
    }

    public FTP2 setPort(int port) {
        this.port = port;
        return this;
    }

    public FTP2 setLogin(String username, String psw) {
        this.username = username;
        this.password = psw;
        return this;
    }

    public FTP2 setPath(String path) {
        this.path = path;
        return this;
    }

    public FTP2 setFTPPath(String ftpPath) {
        this.ftpPath = ftpPath;
        return this;
    }

    public FTP2 setRetry(int retryCount) {
        this.retryCount = retryCount;
        return this;
    }

    public FTP2 setFileName(String sinfileName) {
        this.sinfileName = sinfileName;
        return this;
    }

    public FTP2 setFileName(String[] fileName) {
        this.fileName = fileName;
        return this;
    }

    public FTP2 setInput(List<InputStream> input) {
        this.input = input;
        return this;
    }

    public FTP2 setPackgeName(String packgeName) {
        this.packgeName = packgeName;
        return this;
    }


    public boolean download() {
        Log.d("FTP",
                "download(FTP.java:93)ftp 开始下载");
        boolean success = false;
        FTPClient ftp = new FTPClient();
        File file = null;
        BufferedOutputStream buffOut = null;
        int reply;
        ftp.setConnectTimeout(12000);
        try {
            ftp.connect(url, port);// 连接FTP服务器
            ftp.login(username, password);// 登录
            //连接的状态码
            reply = ftp.getReplyCode();
            ftp.setDataTimeout(12000);
            //判断是否连接上ftp
            if (!FTPReply.isPositiveCompletion(reply)) {
                Log.d("FTP",
                        "call(FTP.java:159)FTP连接失败");
                ftp.disconnect();
                return false;
            }
            file = new File(path + sinfileName);
            Log.d("FTP",
                    "call(FTP.java:165)FTP连接成功");
            success = download(file, ftp);
            if (!success) {
                for (int i = 0; i < retryCount; i++) {
                    Log.d("FTP",
                            "download(FTP.java:117)重试第" + (retryCount + 1) + "次");
                    success = download(file, ftp);
                    if (success) break;
                }
            }

            ftp.logout();
            ftp.disconnect();
            //判断是否退出成功，不成功就再断开连接。
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                    Log.d("FTP",
                            "call(FTP.java:109)" + ioe.toString());
                    throw new RuntimeException("FTP disconnect fail!", ioe);
                }
            }
        } catch (IOException e) {
            success = false;
            if (file != null && file.exists()) {
                file.delete();
            }
            Log.d("FTP",
                    "call(FTP.java:192)FTP异常" + e.toString());
            e.printStackTrace();
        }
        return success;
    }

    private boolean download(File file, FTPClient ftp) throws IOException {
        BufferedOutputStream buffOut;
        boolean success;
//        File file = new File(path + sinfileName);
        if (file.exists()) {
            Log.d("FTP",
                    "call(FTP.java:86)存在,先删除");
            file.delete();
        }

        ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
        ftp.setBufferSize(8 * 1024);
        ftp.setControlEncoding("UTF-8");
        ftp.enterLocalPassiveMode();
        buffOut = new BufferedOutputStream(new FileOutputStream(path + sinfileName), 20 * 1024);
        success = ftp.retrieveFile(ftpPath, buffOut);
        Log.d("FTP",
                "call(FTP.java:98)检索文件是否成功=" + success);
        buffOut.flush();
        buffOut.close();
        return success;
    }
}
