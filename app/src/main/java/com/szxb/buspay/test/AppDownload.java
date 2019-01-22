package com.szxb.buspay.test;

import android.content.Context;
import android.util.Log;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * 作者：Tangren on 2018-09-19
 * 包名：com.szxb.buspay.test
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class AppDownload {

    private OnDownProgress onDownProgress;

    public void setOnDownProgress(OnDownProgress progress) {
        this.onDownProgress = progress;
    }

    private String remote_File_Noexist = "远程文件不存在";
    private String local_Bigger_Remote = "本地文件大于远程文件";
    private String download_From_Break_Success = "断点下载文件成功";
    private String download_From_Break_Failed = "断点下载文件失败";
    private String download_New_Success = "全新下载文件成功";
    private String download_New_Failed = "全新下载文件失败";

    public FTPClient ftpClient = new FTPClient();
    private String ftpURL, username, pwd, ftpport;
    private Context mContext;

    public AppDownload(Context mContext, String _ftpURL, String _username, String _pwd, String _ftpport) {
        //设置将过程中使用到的命令输出到控制台
        this.mContext = mContext;
        ftpURL = _ftpURL;
        username = _username;
        pwd = _pwd;
        ftpport = _ftpport;
        this.ftpClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
    }

    public AppDownload() {
        this.ftpClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
    }

    /** */
    /**
     * 连接到FTP服务器
     *
     * @param hostname 主机名
     * @param port     端口
     * @param username 用户名
     * @param password 密码
     * @return 是否连接成功
     * @throws IOException
     */
    public boolean connect(String hostname, int port, String username, String password) throws IOException {
        ftpClient.connect(hostname, port);
        ftpClient.setControlEncoding("GBK");
        if (FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
            if (ftpClient.login(username, password)) {
                return true;
            }
        }
        disconnect();
        return false;
    }

    /**
     * 从FTP服务器上下载文件,支持断点续传，上传百分比汇报
     *
     * @param remote 远程文件路径
     * @param local  本地文件路径
     * @return 上传的状态
     * @throws IOException
     */
    public void download(String remote, String local) throws IOException {
        //设置被动模式
        ftpClient.enterLocalPassiveMode();
        //设置以二进制方式传输
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

        //检查远程文件是否存在
        FTPFile[] files = ftpClient.listFiles(remote);

        if (files.length != 1) {
            System.out.println("远程文件不存在");
            errorCallBack(remote_File_Noexist);
            return;
        }

        Log.d("AppDownload",
            "download(AppDownload.java:104)文件存在");

        long lRemoteSize = files[0].getSize();
        File f = new File(local);
        //本地存在文件，进行断点下载
        if (f.exists()) {
            long localSize = f.length();
            //判断本地文件大小是否大于远程文件大小
            if (localSize >= lRemoteSize) {
                System.out.println("本地文件大于远程文件，下载中止");
                errorCallBack(local_Bigger_Remote);
                return;
            }

            //进行断点续传，并记录状态
            FileOutputStream out = new FileOutputStream(f, true);
            ftpClient.setRestartOffset(localSize);
            InputStream in = ftpClient.retrieveFileStream(remote);
            byte[] bytes = new byte[1024];
            long step = lRemoteSize / 100;
            long process = localSize / step;
            int c;

            while ((c = in.read(bytes)) != -1) {
                out.write(bytes, 0, c);
                localSize += c;
                long nowProcess = localSize / step;
                if (nowProcess > process) {
                    process = nowProcess;
                    if (process % 10 == 0) {
                        progressCallBack(process);
                    }
                    System.out.println("下载进度：" + process);
                    //TODO 更新文件下载进度,值存放在process变量中
                }
            }
            in.close();
            out.close();
            boolean isDo = ftpClient.completePendingCommand();
            if (isDo) {
                finishCallBack("sdcard/BJLogger/" + f.getName());
            } else {
                errorCallBack(download_From_Break_Failed);
            }
        } else {
            OutputStream out = new FileOutputStream(f);
            InputStream in = ftpClient.retrieveFileStream(remote);
            byte[] bytes = new byte[1024];
            long step = lRemoteSize / 100;
            long process = 0;
            long localSize = 0L;
            int c;
            while ((c = in.read(bytes)) != -1) {
                out.write(bytes, 0, c);
                localSize += c;
                long nowProcess = localSize / step;
                if (nowProcess > process) {
                    process = nowProcess;
                    if (process % 10 == 0) {
                        progressCallBack(process);
                    }
                    System.out.println("下载进度：" + process);
                    //TODO 更新文件下载进度,值存放在process变量中
                }
            }
            in.close();
            out.close();
            boolean upNewStatus = ftpClient.completePendingCommand();
            if (upNewStatus) {
                finishCallBack("sdcard/BJLogger/" + f.getName());
            } else {
                errorCallBack(download_New_Failed);
            }
        }
    }

    private void disconnect() throws IOException {
        if (ftpClient.isConnected()) {
            ftpClient.disconnect();
        }
    }

    private void errorCallBack(String result) {
        if (onDownProgress != null) {
            onDownProgress.onDownloadError(result);
        }
    }

    private void progressCallBack(long progress) {
        if (onDownProgress != null) {
            onDownProgress.onProgress(progress);
        }
    }

    private void finishCallBack(String filePath) {
        if (onDownProgress != null) {
            onDownProgress.onFinish(filePath);
        }
    }

}
