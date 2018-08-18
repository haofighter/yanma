package com.szxb.buspay.module;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.szxb.buspay.BusApp;
import com.szxb.buspay.R;
import com.szxb.buspay.db.entity.bean.CntEntity;
import com.szxb.buspay.db.entity.bean.MainEntity;
import com.szxb.buspay.db.entity.bean.QRCode;
import com.szxb.buspay.db.entity.bean.QRScanMessage;
import com.szxb.buspay.db.entity.bean.card.ConsumeCard;
import com.szxb.buspay.db.entity.card.LineInfoEntity;
import com.szxb.buspay.db.entity.scan.ScanInfoEntity;
import com.szxb.buspay.db.manager.DBManager;
import com.szxb.buspay.interfaces.OnKeyListener;
import com.szxb.buspay.module.init.PosInit;
import com.szxb.buspay.task.key.LoopKeyTask;
import com.szxb.buspay.task.thread.ThreadScheduledExecutorUtil;
import com.szxb.buspay.task.thread.WorkThread;
import com.szxb.buspay.util.AppUtil;
import com.szxb.buspay.util.Util;
import com.szxb.buspay.util.WriteRecordToSD;
import com.szxb.buspay.util.adapter.HomeParentAdapter;
import com.szxb.buspay.util.adapter.RecordAdapter;
import com.szxb.buspay.util.rx.RxBus;
import com.szxb.buspay.util.tip.BusToast;
import com.szxb.java8583.module.manager.BusllPosManage;
import com.szxb.mlog.SLog;
import com.szxb.unionpay.entity.UnionPayEntity;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.szxb.buspay.db.manager.DBManager.queryScanRecord;
import static com.szxb.buspay.util.Config.POSITION_BUS_RECORD;
import static com.szxb.buspay.util.Config.POSITION_CHECK_NET;
import static com.szxb.buspay.util.Config.POSITION_CNT;
import static com.szxb.buspay.util.Config.POSITION_EXPORT_1_M;
import static com.szxb.buspay.util.Config.POSITION_EXPORT_7;
import static com.szxb.buspay.util.Config.POSITION_EXPORT_DB;
import static com.szxb.buspay.util.Config.POSITION_EXPORT_LOG;
import static com.szxb.buspay.util.Config.POSITION_READ_PARAM;
import static com.szxb.buspay.util.Config.POSITION_SCAN_RECORD;
import static com.szxb.buspay.util.Config.POSITION_TIME;
import static com.szxb.buspay.util.Config.POSITION_UNION_RECORD;
import static com.szxb.buspay.util.Config.POSITION_UPDATE_PARAMS;
import static com.szxb.buspay.util.Util.fen2Yuan;
import static com.szxb.buspay.util.Util.hex2Int;
import static com.szxb.buspay.util.Util.string2Int;

/**
 * 作者: Tangren on 2017/7/17
 * 包名：com.szxb.buspay.base
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public abstract class BaseActivity extends AppCompatActivity implements OnKeyListener {

    //菜单view
    protected RecyclerView recyclerView;

    private TextView net_status;

    //记录view
    protected RecyclerView recycler_view_record;
    protected View main_record;
    protected TextView record_type;

    //汇总view
    protected TextView record_type_cnt;
    protected View main_cnt;
    protected TextView ic_card_swipe_cnt, ic_card_amount_cnt, ic_card_up_status;
    protected TextView scan_cnt, scan_amount_cnt, scan_up_status;
    protected TextView union_cnt, union_amount_cnt, union_un_status;
    protected TextView sum_cnt, sum_amount_cnt, sum_up_status;
    protected TextView time_cnt, time_amount_cnt;

    //参数信息
    protected View main_info;
    protected TextView main_info_type;
    protected TextView city_code, ten_mach_id, union_mch_id;
    protected TextView line_name, driver_no, union_pos_sn;
    protected TextView pos_sn, black_version, black_cnt;

    private void initCntView() {
        ic_card_swipe_cnt = (TextView) findViewById(R.id.ic_card_swipe_cnt);
        ic_card_amount_cnt = (TextView) findViewById(R.id.ic_card_amount_cnt);
        ic_card_up_status = (TextView) findViewById(R.id.ic_card_up_status);

        scan_cnt = (TextView) findViewById(R.id.scan_cnt);
        scan_amount_cnt = (TextView) findViewById(R.id.scan_amount_cnt);
        scan_up_status = (TextView) findViewById(R.id.scan_up_status);

        union_cnt = (TextView) findViewById(R.id.union_cnt);
        union_amount_cnt = (TextView) findViewById(R.id.union_amount_cnt);
        union_un_status = (TextView) findViewById(R.id.union_un_status);

        sum_cnt = (TextView) findViewById(R.id.sum_cnt);
        sum_amount_cnt = (TextView) findViewById(R.id.sum_amount_cnt);
        sum_up_status = (TextView) findViewById(R.id.sum_up_status);

        time_cnt = (TextView) findViewById(R.id.time_cnt);
        time_amount_cnt = (TextView) findViewById(R.id.time_amount_cnt);
    }

    private void initMainInfoView() {
        main_info = findViewById(R.id.main_info);
        main_info_type = (TextView) findViewById(R.id.main_info_type);

        city_code = (TextView) findViewById(R.id.city_code);
        ten_mach_id = (TextView) findViewById(R.id.ten_mach_id);
        union_mch_id = (TextView) findViewById(R.id.union_mch_id);

        line_name = (TextView) findViewById(R.id.line_name);
        driver_no = (TextView) findViewById(R.id.driver_no);
        union_pos_sn = (TextView) findViewById(R.id.union_pos_sn);

        pos_sn = (TextView) findViewById(R.id.pos_sn);
        black_version = (TextView) findViewById(R.id.black_version);
        black_cnt = (TextView) findViewById(R.id.black_cnt);
    }

    //签到view
    protected View main_sign;

    protected HomeParentAdapter mAdapter;
    protected List<MainEntity> mList = new ArrayList<>();

    protected abstract int rootView();

    protected TranslateAnimation mShowAnimation;
    protected TranslateAnimation mHiddenAnimation;


    //是否在查询记录界面
    private boolean childViewShow = false;
    private RecordAdapter recordAdapter;
    private List<MainEntity> mRecordList = new ArrayList<>();

    protected void initView() {
        net_status = (TextView) findViewById(R.id.net_status);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recycler_view_record = (RecyclerView) findViewById(R.id.recycler_view_record);
        main_record = findViewById(R.id.main_record);
        record_type = (TextView) findViewById(R.id.record_type);
        record_type_cnt = (TextView) findViewById(R.id.record_type_cnt);
        main_cnt = findViewById(R.id.main_cnt);
        main_sign = findViewById(R.id.main_sign);

        initCntView();
        initMainInfoView();
    }

    protected abstract void initData();

    protected abstract void message(QRScanMessage message);

    private LoopKeyTask instance;

    private Subscription subscribe;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT > 21) {
            int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            getWindow().getDecorView().setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
        }
        super.onCreate(savedInstanceState);
        setContentView(rootView());
        initView();

        initRx();

        initData();

        initList();

        instance = LoopKeyTask.getInstance();
        instance.setOnKeyListener(this);
        instance.startLoopKey();

        initRecyclerData();

        initAnimation();

    }

    private void initRx() {
        subscribe = RxBus.getInstance().toObservable(QRScanMessage.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<QRScanMessage>() {
                    @Override
                    public void call(QRScanMessage qrScanMessage) {
                        message(qrScanMessage);
                        if (qrScanMessage.getResult() == QRCode.STOP_CNT) {
                            //关闭菜单页
                            onKeyCancel();
                        } else if (qrScanMessage.getResult() == QRCode.CNT) {
                            //汇总
                            showCnt(qrScanMessage.getCntEntity());
                        } else if (qrScanMessage.getResult() == QRCode.NET_STATUS) {
                            //网络状态发送改变
                            hasNetWork(AppUtil.checkNetStatus());
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        SLog.d("BaseActivity(call.java:115)Rx异常>>" + throwable.toString());
                    }
                });

    }


    protected abstract void initList();

    //初始化MENU数据
    private void initRecyclerData() {
        mAdapter = new HomeParentAdapter(getApplicationContext(), mList, recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        mAdapter.setItemChecked(0);

        recordAdapter = new RecordAdapter(getApplicationContext(), mRecordList, recycler_view_record);
        recycler_view_record.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recycler_view_record.setAdapter(recordAdapter);
        recycler_view_record.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
    }

    //菜单初始化动画
    private void initAnimation() {
        mShowAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mShowAnimation.setDuration(500);

        mHiddenAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                -1.0f);
        mHiddenAnimation.setDuration(500);
    }


    @Override
    public void onKeyDown() {
        if (main_sign.getVisibility() == View.VISIBLE) {
            return;
        }
        if (!childViewShow) {
            //如果不是交易查询界面
            if (recyclerView.getVisibility() == View.GONE) {
                setViewStatus(recyclerView, true);
            } else {
                mAdapter.downKey();
            }
        } else {
            recordAdapter.downKey();
        }
    }

    @Override
    public void onKeyUp() {
        if (main_sign.getVisibility() == View.VISIBLE) {
            return;
        }
        if (!childViewShow) {
            if (recyclerView.getVisibility() == View.GONE) {
                setViewStatus(recyclerView, true);
            } else {
                mAdapter.upKey();
            }
        } else {
            recordAdapter.upKey();
        }
    }

    @Override
    public void onKeyOk() {
        if (childViewShow || main_sign.getVisibility() == View.VISIBLE) {
            return;
        }
        int position = mAdapter.getCurrentItemPosition();
        //刷卡记录
        if (position == POSITION_BUS_RECORD) {
            onKeyCancel();
            childViewShow = true;
            setViewStatus(main_record, true);
            record_type.setText("刷卡记录");
            mRecordList.clear();
            mRecordList.add(new MainEntity("卡号", "余额", "金额", "交易时间"));
            List<ConsumeCard> zibo = DBManager.queryCardRecord("zibo");
            if (zibo != null) {
                for (ConsumeCard consumeCard : zibo) {
                    MainEntity mainEntity = new MainEntity();
                    mainEntity.setType(0);
                    mainEntity.setStatus(consumeCard.getUpStatus());
                    mainEntity.setTime(consumeCard.getTransTime());
                    mainEntity.setCard_id(consumeCard.getCardNo());
                    if (TextUtils.equals(consumeCard.getCardType(), "0A")) {
                        //次数卡
                        mainEntity.setCard_money(hex2Int(consumeCard.getCardBalance()) + "");
                        mainEntity.setPay_money(hex2Int(consumeCard.getPayFee()) + "");
                    } else {
                        mainEntity.setCard_money(fen2Yuan(string2Int(consumeCard.getCardBalance())));
                        mainEntity.setPay_money(fen2Yuan(string2Int(consumeCard.getPayFee())));
                    }
                    mRecordList.add(mainEntity);
                }
            }
            recordAdapter.position = 0;
            recordAdapter.setItemChecked(0);
            recordAdapter.refreshData(mRecordList);
        } else if (position == POSITION_SCAN_RECORD) {//扫码记录
            onKeyCancel();
            childViewShow = true;
            setViewStatus(main_record, true);
            record_type.setText("扫码记录");
            mRecordList.clear();
            mRecordList.add(new MainEntity("用户ID", "交易状态", "金额", "交易时间"));
            List<ScanInfoEntity> scanInfoEntityList = queryScanRecord();
            for (ScanInfoEntity scanInfo : scanInfoEntityList) {
                MainEntity mainEntity = new MainEntity();
                mainEntity.setType(1);
                mainEntity.setStatus(scanInfo.getStatus());
                mainEntity.setTime(scanInfo.getTime());
                mainEntity.setCard_id(scanInfo.getOpenid());
                //扫码无法获取用户余额此,处为上传跟支付状态
                mainEntity.setCard_money(Util.scanStatus(scanInfo.getStatus()));
                mainEntity.setPay_money(fen2Yuan(scanInfo.getPay_fee()));
                mRecordList.add(mainEntity);
            }
            recordAdapter.position = 0;
            recordAdapter.setItemChecked(0);
            recordAdapter.refreshData(mRecordList);
        } else if (position == POSITION_UNION_RECORD) {//银联卡记录
            onKeyCancel();
            childViewShow = true;
            setViewStatus(main_record, true);
            record_type.setText("银联卡记录");
            mRecordList.clear();
            mRecordList.add(new MainEntity("卡号", "交易状态", "金额", "交易时间"));
            List<UnionPayEntity> unionPayEntityList = DBManager.queryUnionPayRecord();
            for (UnionPayEntity unionPayEntity : unionPayEntityList) {
                MainEntity mainEntity = new MainEntity();
                mainEntity.setType(1);
                mainEntity.setStatus(unionPayEntity.getUpStatus());
                mainEntity.setTime(unionPayEntity.getTime());
                mainEntity.setCard_id(unionPayEntity.getMainCardNo());
                //银联卡无法获取用户余额此,处为扣款状态
                mainEntity.setCard_money(Util.unionPayStatus(unionPayEntity.getResCode()));
                mainEntity.setPay_money(fen2Yuan(string2Int(unionPayEntity.getPayFee())));
                mRecordList.add(mainEntity);
            }
            recordAdapter.position = 0;
            recordAdapter.setItemChecked(0);
            recordAdapter.refreshData(mRecordList);
        } else if (position == POSITION_CNT) {//当天汇总
            onKeyCancel();
            childViewShow = true;
            setViewStatus(main_cnt, true);
            record_type_cnt.setText("当天汇总");
            ThreadScheduledExecutorUtil.getInstance().getService().submit(new WorkThread("cnt"));
        } else if (position == POSITION_UPDATE_PARAMS) {//手动更新参数
            BusToast.showToast(BusApp.getInstance(), "开始更新请稍后", true);
            LineInfoEntity lineInfoEntity = BusApp.getPosManager().getLineInfoEntity();
            PosInit init = new PosInit();
            init.init();
            if (lineInfoEntity != null) {
                init.download(lineInfoEntity.getFileName());
            }
            if (BusApp.getPosManager().isSuppUnionPay()) {
                init.downUnionPayParamFile(true);
            }
            onKeyCancel();
        } else if (position == POSITION_EXPORT_DB) {//数据库导出
            ThreadScheduledExecutorUtil.getInstance().getService().submit(new WorkThread("export_db"));
        } else if (position == POSITION_EXPORT_LOG) {//日志导出
            ThreadScheduledExecutorUtil.getInstance().getService().submit(new WorkThread("export_log"));
        } else if (position == POSITION_CHECK_NET) {//检测网络
            boolean b = AppUtil.checkNetStatus();
            BusToast.showToast(getApplicationContext(), b ? "网络已连接" : "未检测到网络", b);
            onKeyCancel();
        } else if (position == POSITION_TIME) {//校准时间
            BusToast.showToast(getApplicationContext(), "开始校准时间", true);
            ThreadScheduledExecutorUtil.getInstance().getService().submit(new WorkThread("reg_time"));
            onKeyCancel();
        } else if (position == POSITION_EXPORT_7) {//导出7天记录
            export(7);
        } else if (position == POSITION_EXPORT_1_M) {//导出1个月记录
            export(30);
        } else if (position == POSITION_READ_PARAM) {//参数信息
            onKeyCancel();
            childViewShow = true;
            setViewStatus(main_info, true);
            main_info_type.setText("参数查询");

            city_code.setText(String.format("城市代码\n%1$s", BusApp.getPosManager().getCityCode()));
            ten_mach_id.setText(String.format("腾讯商户号\n%1$s", BusApp.getPosManager().getAppId()));
            union_mch_id.setText(String.format("银联商户号\n%1$s", BusllPosManage.getPosManager().getMchId()));
            line_name.setText(String.format("线路名\n%1$s", BusApp.getPosManager().getChinese_name()));
            driver_no.setText(String.format("司机号\n%1$s", BusApp.getPosManager().getDriverNo()));
            union_pos_sn.setText(String.format("银联POS编号\n%1$s", BusllPosManage.getPosManager().getPosSn()));
            pos_sn.setText(String.format("车辆编号\n%1$s", BusApp.getPosManager().getBusNo()));
            black_version.setText(String.format("黑名单版本\n%1$s", BusApp.getPosManager().getBlackVersion()));
            long[] longs = DBManager.queryBlackListCnt();
            black_cnt.setText(String.format("黑名单数量\n%1$d|%2$d", longs[0], longs[1]));
        }
    }

    /**
     * 导出数据
     *
     * @param day 天数
     */
    private void export(int day) {
        String scanPath = "/storage/sdcard1/scan";
        String cardPath = "/storage/sdcard1/card";
        String unionPath = "/storage/sdcard1/union";
        WriteRecordToSD.getInstance().writer(day, scanPath, cardPath, unionPath);
    }

    @Override
    public void onKeyCancel() {
        if (recyclerView.getVisibility() == View.VISIBLE) {
            setViewStatus(recyclerView, false);
        }

        if (main_record.getVisibility() == View.VISIBLE) {
            setViewStatus(main_record, false);
            childViewShow = false;
        }

        if (main_cnt.getVisibility() == View.VISIBLE) {
            setViewStatus(main_cnt, false);
            childViewShow = false;
        }

        if (main_info.getVisibility() == View.VISIBLE) {
            setViewStatus(main_info, false);
            childViewShow = false;
        }
    }

    private void showCnt(CntEntity cntEntity) {
        ic_card_swipe_cnt.setText(cntEntity.ic_card_swipe_cnt[0]);
        ic_card_amount_cnt.setText(cntEntity.ic_card_swipe_cnt[1]);
        ic_card_up_status.setText(cntEntity.ic_card_swipe_cnt[2]);

        scan_cnt.setText(cntEntity.scan_cnt[0]);
        scan_amount_cnt.setText(cntEntity.scan_cnt[1]);
        scan_up_status.setText(cntEntity.scan_cnt[2]);

        union_cnt.setText(cntEntity.union_cnt[0]);
        union_amount_cnt.setText(cntEntity.union_cnt[1]);
        union_un_status.setText(cntEntity.union_cnt[2]);

        sum_cnt.setText(cntEntity.cnt[0]);
        sum_amount_cnt.setText(cntEntity.cnt[1]);
        sum_up_status.setText(cntEntity.cnt[2]);

        time_cnt.setText(cntEntity.time_cnt[0]);
        time_amount_cnt.setText(cntEntity.time_cnt[1]);
    }


    /**
     * @param view   view
     * @param isShow 是否显示
     */
    private void setViewStatus(View view, boolean isShow) {
        if (isShow) {
            //显示
            view.setVisibility(View.VISIBLE);
            view.startAnimation(mShowAnimation);
        } else {
            //隐藏
            view.setVisibility(View.GONE);
            view.startAnimation(mHiddenAnimation);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        hasNetWork(AppUtil.checkNetStatus());
    }

    private void hasNetWork(boolean b) {
        net_status.setVisibility(b ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance.cancel();
        onKeyCancel();
        if (subscribe != null && !subscribe.isUnsubscribed()) {
            subscribe.unsubscribe();
        }
    }

}
