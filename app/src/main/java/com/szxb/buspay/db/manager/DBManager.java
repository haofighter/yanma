package com.szxb.buspay.db.manager;

import android.database.Cursor;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.szxb.buspay.db.dao.BlackListEntityDao;
import com.szxb.buspay.db.dao.ConsumeCardDao;
import com.szxb.buspay.db.dao.LineInfoEntityDao;
import com.szxb.buspay.db.dao.MacKeyEntityDao;
import com.szxb.buspay.db.dao.PublicKeyEntityDao;
import com.szxb.buspay.db.dao.ScanInfoEntityDao;
import com.szxb.buspay.db.dao.UnionPayEntityDao;
import com.szxb.buspay.db.entity.bean.CntEntity;
import com.szxb.buspay.db.entity.bean.card.ConsumeCard;
import com.szxb.buspay.db.entity.card.LineInfoEntity;
import com.szxb.buspay.db.entity.scan.BlackListEntity;
import com.szxb.buspay.db.entity.scan.MacKeyEntity;
import com.szxb.buspay.db.entity.scan.PublicKeyEntity;
import com.szxb.buspay.db.entity.scan.ScanInfoEntity;
import com.szxb.buspay.util.DateUtil;
import com.szxb.mlog.SLog;
import com.szxb.unionpay.entity.UnionPayEntity;

import org.greenrobot.greendao.query.Query;

import java.util.List;

import static com.szxb.buspay.db.manager.DBCore.getDaoSession;

/**
 * 作者：Tangren on 2018-07-18
 * 包名：com.szxb.buspay.db.manager
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class DBManager {

    /**
     * 验码成功,保存数据
     *
     * @param object
     */
    public static void insert(JSONObject object, String mch_trx_id, String openID,
                              String qrCode, int pay_fee) {
        SLog.d("DBManager(insert.java:24)验码成功,保存数据：" + object.toJSONString());
        ScanInfoEntity infoEntity = new ScanInfoEntity();
        infoEntity.setStatus(1);//1表示未扣款
        infoEntity.setBiz_data_single(object.toJSONString());
        infoEntity.setMch_trx_id(mch_trx_id);
        infoEntity.setTime(DateUtil.getCurrentDate());
        infoEntity.setOpenid(openID);
        infoEntity.setQrcode(qrCode);
        infoEntity.setPay_fee(pay_fee);
        getDaoSession().getScanInfoEntityDao().insert(infoEntity);
    }

    /**
     * 更新黑名单
     *
     * @param memberList
     */
    public static void addBlackList(JSONArray memberList) {
        deleteOverDueBlackName();
        for (int i = 0; i < memberList.size(); i++) {
            JSONObject object = memberList.getJSONObject(i);
            BlackListEntity entity = new BlackListEntity();
            entity.setOpen_id(object.getString("open_id"));
            entity.setTime(object.getLong("time"));
            getDaoSession().insert(entity);
        }
    }


    /**
     * 删除过期的黑名单
     */
    private static void deleteOverDueBlackName() {
        BlackListEntityDao dao = getDaoSession().getBlackListEntityDao();
        List<BlackListEntity> list = dao.queryBuilder().where(BlackListEntityDao.Properties.Time
                .le(DateUtil.currentLong())).build().list();
        dao.deleteInTx(list);
    }

    /**
     * 根据线路号获取线路信息
     *
     * @param lineNo .
     * @return .
     */
    public static LineInfoEntity getLineInfoEntity(String lineNo) {
        LineInfoEntityDao dao = getDaoSession().getLineInfoEntityDao();
        return dao.queryBuilder().where(LineInfoEntityDao.Properties.Line.eq(lineNo)).limit(1).build().unique();
    }

    /**
     * 过滤相同二维码不能连刷(有效期内)
     * 如果存在返回true
     *
     * @param qrCode .
     * @return .
     */
    public static boolean filterSameQR(String qrCode) {
        ScanInfoEntityDao dao = getDaoSession().getScanInfoEntityDao();
        return dao.queryBuilder().where(ScanInfoEntityDao.Properties.Qrcode.eq(qrCode),
                ScanInfoEntityDao.Properties.Time.ge(DateUtil.getCurrentDateLastMi(5))).orderDesc()
                .count() > 0;
    }

    public static boolean filterOpenID(String open_id) {
        ScanInfoEntity unique = getDaoSession().getScanInfoEntityDao().queryBuilder().limit(1).orderDesc(ScanInfoEntityDao.Properties.Id).build().unique();
        if (unique != null) {
            if (!TextUtils.isEmpty(unique.getOpenid()))
                if (unique.getOpenid().equals(open_id)) {
                    return DateUtil.getMILLISECOND(DateUtil.getCurrentDate(), unique.getTime()) <= 6;
                }
        }
        return false;
    }

    /**
     * 是否属于黑名单
     *
     * @param openID
     * @return
     */
    public static boolean filterBlackName(String openID) {
        BlackListEntityDao dao = getDaoSession().getBlackListEntityDao();
        Query<BlackListEntity> build = dao.queryBuilder().where(BlackListEntityDao.Properties.Open_id.eq(openID),
                BlackListEntityDao.Properties.Time.ge(DateUtil.currentLong())).build();
        BlackListEntity blackEntity = build.unique();
        return blackEntity != null;
    }

    /**
     * 单票修改支付状态
     *
     * @param mch_trx_id 订单号
     * @param status     支付状态1:新订单，未支付，0:准实时扣款，2：批处理扣款成功 3：扣款失败后台处理,4：系统错误再次下次提交
     * @param result     返回码
     * @param tr_status  交易状态码
     */
    public static void updateTransInfo(String mch_trx_id, int status, String result, String tr_status) {
        ScanInfoEntityDao dao = getDaoSession().getScanInfoEntityDao();
        ScanInfoEntity entity = dao.queryBuilder().where(ScanInfoEntityDao.Properties.Mch_trx_id.eq(mch_trx_id)).build().unique();
        if (entity != null) {
            entity.setStatus(status);
            if (result != null)
                entity.setResult(result);
            if (tr_status != null)
                entity.setTr_status(tr_status);
            dao.update(entity);
        }
    }

    /***
     * 获取公钥

     * @param keyId 公钥ID
     * @return
     */
    public static String getPublicKey(String keyId) {
        PublicKeyEntityDao dao = getDaoSession().getPublicKeyEntityDao();
        PublicKeyEntity unique = dao.queryBuilder().where(PublicKeyEntityDao.Properties.Key_id.eq(keyId)).build().unique();
        if (unique != null)
            return unique.getPubkey();
        return "";
    }

    /**
     * 获取mac秘钥
     *
     * @param keyId .
     * @return .
     */
    public static String getMac(String keyId) {
        MacKeyEntityDao dao = getDaoSession().getMacKeyEntityDao();
        MacKeyEntity unique = dao.queryBuilder().where(MacKeyEntityDao.Properties.Key_id.eq(keyId)).unique();
        if (unique != null)
            return unique.getPubkey();
        return "";

    }

    /**
     * @param type 城市
     * @return 刷卡记录
     */
    public static List<ConsumeCard> queryCardRecord(String type) {
        if (TextUtils.equals(type, "zibo")) {
            ConsumeCardDao dao = getDaoSession().getConsumeCardDao();
            return dao.queryBuilder().orderDesc(ConsumeCardDao.Properties.Id).limit(50).list();
        }
        return null;
    }

    /**
     * @return 扫码记录
     */
    public static List<ScanInfoEntity> queryScanRecord() {
        ScanInfoEntityDao dao = getDaoSession().getScanInfoEntityDao();
        return dao.queryBuilder().orderDesc(ScanInfoEntityDao.Properties.Id).limit(50).list();
    }

    /**
     * @return 银联卡记录
     */
    public static List<UnionPayEntity> queryUnionPayRecord() {
        UnionPayEntityDao dao = getDaoSession().getUnionPayEntityDao();
        return dao.queryBuilder().orderDesc(UnionPayEntityDao.Properties.Id).limit(50).list();
    }

    /**
     * @return 拦截某种卡1分钟内连刷
     */
    public static boolean filterBrush() {
        ConsumeCardDao dao = getDaoSession().getConsumeCardDao();
        return dao.queryBuilder().orderDesc(ConsumeCardDao.Properties.Id).limit(50)
                .where(ConsumeCardDao.Properties.TransTime.ge(DateUtil.getCurrentDateLastMi(1, "yyyyMMddHHmmss")))
                .count() > 0;
    }


    public static CntEntity getCnt() {
        String icSql = " select sum(PAY_FEE) as pay from CONSUME_CARD";
        Cursor icCursor = getDaoSession().getDatabase().rawQuery(icSql, null);
        int pay_fee = icCursor.getColumnIndex("pay_fee");
        //刷卡总金额
        String icFee = "0";
        while (icCursor.moveToNext()) {
            icFee = icCursor.getString(pay_fee);
        }

        String wc_sql = " select sum(PAY_FEE) as no from SCAN_INFO_ENTITY";
        String union_sql = " select sum(PAY_FEE) as no from UNION_PAY_ENTITY";


        return null;
    }
}