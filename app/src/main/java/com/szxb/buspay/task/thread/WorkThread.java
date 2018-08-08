package com.szxb.buspay.task.thread;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.szxb.buspay.BusApp;
import com.szxb.buspay.db.dao.BlackListCardDao;
import com.szxb.buspay.db.entity.bean.CntEntity;
import com.szxb.buspay.db.entity.bean.QRCode;
import com.szxb.buspay.db.entity.bean.QRScanMessage;
import com.szxb.buspay.db.entity.bean.card.ConsumeCard;
import com.szxb.buspay.db.entity.card.BlackListCard;
import com.szxb.buspay.db.entity.scan.ScanInfoEntity;
import com.szxb.buspay.db.manager.DBCore;
import com.szxb.buspay.db.manager.DBManager;
import com.szxb.buspay.http.JsonRequest;
import com.szxb.buspay.util.AppUtil;
import com.szxb.buspay.util.DateUtil;
import com.szxb.buspay.util.rx.RxBus;
import com.szxb.buspay.util.tip.BusToast;
import com.szxb.unionpay.entity.UnionPayEntity;
import com.yanzhenjie.nohttp.rest.Response;
import com.yanzhenjie.nohttp.rest.SyncRequestExecutor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.szxb.buspay.db.manager.DBCore.getDaoSession;

/**
 * 作者：Tangren on 2018-07-20
 * 包名：com.szxb.buspay.task.thread
 * 邮箱：996489865@qq.com
 * TODO:任务线程
 */

public class WorkThread extends Thread {

    private Object record;
    private List<BlackListCard> list;

    public WorkThread(String name) {
        super(name);
    }

    public WorkThread(String name, Object record) {
        super(name);
        this.record = record;
    }

    public WorkThread(String name, List<BlackListCard> list) {
        super(name);
        this.list = list;
    }


    @Override
    public void run() {
        super.run();
        String name = getName() == null ? "zibo" : getName();
        if (TextUtils.equals(name, "zibo")) {
            //淄博刷公交卡记录保存
            ConsumeCard consumeCard = (ConsumeCard) record;
            getDaoSession().getConsumeCardDao().insertOrReplace(consumeCard);

        } else if (TextUtils.equals(name, "weChat")) {
            //保存微信交易
            ScanInfoEntity infoEntity = (ScanInfoEntity) record;
            getDaoSession().getScanInfoEntityDao().insertOrReplaceInTx(infoEntity);

        } else if (TextUtils.equals(name, "union")) {
            //保存银联交易
            UnionPayEntity unionPayEntity = (UnionPayEntity) record;
            getDaoSession().getUnionPayEntityDao().insertOrReplaceInTx(unionPayEntity);

        } else if (TextUtils.equals(name, "black_list")) {
            //更新黑名单
            BlackListCardDao dao = DBCore.getDaoSession().getBlackListCardDao();
            dao.deleteAll();
            List<BlackListCard> bl = list;
            dao.insertOrReplaceInTx(bl);
        } else if (TextUtils.equals(name, "cnt")) {
            //汇总
            CntEntity cntEntity = DBManager.getCnt();
            RxBus.getInstance().send(new QRScanMessage(cntEntity, QRCode.CNT));

        } else if (TextUtils.equals(name, "export_log")) {
            //导出日志
            String posPath = Environment.getExternalStorageDirectory() + "/BJLogger";
            String sdDirectory = "/log";
            exportFile(posPath, sdDirectory);
        } else if (TextUtils.equals(name, "export_db")) {
            //导出数据库文件
            String posDirectory = "data/data/" + BusApp.getInstance().getPackageName() + "/databases";
            String sdDirectory = "/databases";
            exportFile(posDirectory, sdDirectory);
        } else if (TextUtils.equals(name, "pos_status_push")) {
            pushStatus();
        }

    }

    /**
     * 机具上报
     */
    private void pushStatus() {
        String url = "http://134.175.56.14/bipeqt/interaction/terminalStream";
        Map<String, Object> params = new HashMap<>();
        params.put("mchid", BusApp.getPosManager().getAppId());
        params.put("devno", BusApp.getPosManager().getPosSN());
        params.put("version", AppUtil.getVersionName(BusApp.getInstance()));
        params.put("custnum", BusApp.getPosManager().getBusNo());
        params.put("postdate", DateUtil.getCurrentDate());
        params.put("lineno", BusApp.getPosManager().getLineNo());
        params.put("type", "1");
        params.put("paytype", "0");
        JsonRequest request = new JsonRequest(url);
        request.add(params);
        Response<JSONObject> execute = SyncRequestExecutor.INSTANCE.execute(request);
        if (execute.isSucceed()) {
            Log.d("PosRequest",
                    "run(PosRequest.java:44)" + execute.get().toJSONString());
        }
    }

    /**
     * 复制文件到SD卡中
     *
     * @param posPath     pos路径
     * @param sdDirectory sd卡文件夹
     */
    private void exportFile(String posPath, String sdDirectory) {
        File file = new File(posPath);
        if (file.exists()) {
            String newPath = "/storage/sdcard1" + sdDirectory;
            boolean b = copyFolder(posPath, newPath);
            if (b) {
                BusToast.showToast(BusApp.getInstance(), "文件导出成功", true);
            } else {
                BusToast.showToast(BusApp.getInstance(), "导出失败", false);
            }
        } else {
            BusToast.showToast(BusApp.getInstance(), "文件不存在", false);
        }
    }


    /**
     * 复制整个文件夹内容
     *
     * @param oldPath String 原文件路径 如：c:/fqf
     * @param newPath String 复制后路径 如：f:/fqf/ff
     * @return boolean
     */
    public boolean copyFolder(String oldPath, String newPath) {
        boolean isok = true;
        try {
            (new File(newPath)).mkdirs(); //如果文件夹不存在 则建立新文件夹
            File a = new File(oldPath);
            String[] file = a.list();
            File temp = null;
            for (String aFile : file) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + aFile);
                } else {
                    temp = new File(oldPath + File.separator + aFile);
                }

                if (temp.isFile()) {
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(newPath + "/" +
                            (temp.getName()));
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ((len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                if (temp.isDirectory()) {//如果是子文件夹
                    copyFolder(oldPath + "/" + aFile, newPath + "/" + aFile);
                }
            }
        } catch (Exception e) {
            isok = false;
        }
        return isok;
    }

}
