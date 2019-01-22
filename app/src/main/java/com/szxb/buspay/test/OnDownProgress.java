package com.szxb.buspay.test;

/**
 * 作者：Tangren on 2018-09-18
 * 包名：com.szxb.jcbus.interfaces
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public interface OnDownProgress {

    void onProgress(long progress);

    void onFinish(String filePath);

    void onDownloadError(String exception);
}
