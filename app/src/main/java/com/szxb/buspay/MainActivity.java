package com.szxb.buspay;

import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.szxb.buspay.db.entity.bean.MainEntity;
import com.szxb.buspay.db.entity.bean.QRCode;
import com.szxb.buspay.db.entity.bean.QRScanMessage;
import com.szxb.buspay.interfaces.OnReceiverMessageListener;
import com.szxb.buspay.module.BaseActivity;
import com.szxb.buspay.module.WeakHandler;
import com.szxb.buspay.task.card.LoopCardThread;
import com.szxb.buspay.task.scan.LoopScanThread;
import com.szxb.buspay.task.thread.ThreadScheduledExecutorUtil;
import com.szxb.buspay.util.AppUtil;
import com.szxb.buspay.util.DateUtil;

import java.util.concurrent.TimeUnit;

import static com.szxb.buspay.util.Util.fen2Yuan;

public class MainActivity extends BaseActivity implements OnReceiverMessageListener {

    private WeakHandler.MyHandler mHandler;
    private TextView time, station_name, prices, version_name, bus_no;
    private TextView sign_time, sign_version, sign_bus_no;
    private TextView net_status;


    @Override
    protected int rootView() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        super.initView();
        time = (TextView) findViewById(R.id.currentTime);
        station_name = (TextView) findViewById(R.id.station_name);
        prices = (TextView) findViewById(R.id.prices);
        version_name = (TextView) findViewById(R.id.version_name);
        bus_no = (TextView) findViewById(R.id.bus_no);

        sign_time = (TextView) findViewById(R.id.sign_time);
        sign_version = (TextView) findViewById(R.id.sign_version);
        sign_bus_no = (TextView) findViewById(R.id.sign_bus_no);
        net_status = (TextView) findViewById(R.id.net_status);
    }

    @Override
    protected void initData() {
        mHandler = new WeakHandler.MyHandler(this);
        initDate();
        initDatas();
        ThreadScheduledExecutorUtil.getInstance().getService().scheduleAtFixedRate(new LoopScanThread(), 1000, 200, TimeUnit.MILLISECONDS);
        ThreadScheduledExecutorUtil.getInstance().getService().scheduleAtFixedRate(new LoopCardThread(), 1000, 200, TimeUnit.MILLISECONDS);

    }

    private void initDatas() {
        String driverNo = BusApp.getPosManager().getDriverNo();
        if (TextUtils.equals(driverNo, String.format("%08d", 0))) {
            main_sign.setVisibility(View.VISIBLE);
        }
        if (BusApp.getPosManager().getLineInfoEntity() != null) {
            prices.setText("票价:" + fen2Yuan(BusApp.getPosManager().getBasePrice()) + "元");
            station_name.setText(BusApp.getPosManager().getChinese_name());
        }
        sign_time.setText(DateUtil.getCurrentDate("yyyy-MM-dd"));
        version_name.setText("[" + AppUtil.getVersionName(getApplicationContext()) + "]");
        sign_version.setText("[" + AppUtil.getVersionName(getApplicationContext()) + "]");
        sign_bus_no.setText(BusApp.getPosManager().getBusNo());
        bus_no.setText(String.format("车辆号:%1$s\n司机号:%2$s",
                BusApp.getPosManager().getBusNo(), BusApp.getPosManager().getDriverNo()));

    }

    @Override
    protected void message(QRScanMessage message) {
        switch (message.getResult()) {
            case QRCode.REFRESH_VIEW:
                prices.setText("票价:" + fen2Yuan(BusApp.getPosManager().getBasePrice()) + "元");
                station_name.setText(BusApp.getPosManager().getChinese_name());
                bus_no.setText(String.format("车辆号:%1$s\n司机号:%2$s",
                        BusApp.getPosManager().getBusNo(), BusApp.getPosManager().getDriverNo()));
                sign_bus_no.setText(BusApp.getPosManager().getBusNo());
                break;
            case QRCode.SIGN:
                String driverNo = BusApp.getPosManager().getDriverNo();
                main_sign.setVisibility(TextUtils.equals(driverNo, String.format("%08d", 0)) ? View.VISIBLE : View.GONE);
                main_sign.startAnimation(TextUtils.equals(driverNo, String.format("%08d", 0)) ? mShowAnimation : mHiddenAnimation);
                bus_no.setText(String.format("车辆号:%1$s\n司机号:%2$s",
                        BusApp.getPosManager().getBusNo(), driverNo));
                break;
            default:
                break;
        }
    }

    @Override
    protected void initList() {
        mList.add(new MainEntity("查看刷卡记录"));
        mList.add(new MainEntity("查看扫码记录"));
        mList.add(new MainEntity("查看银联卡记录"));
        mList.add(new MainEntity("查询当天汇总"));
        mList.add(new MainEntity("上传记录"));
        mList.add(new MainEntity("数据库导出"));
        mList.add(new MainEntity("日志导出"));
        mList.add(new MainEntity("检测网络"));
        mList.add(new MainEntity("校准时间"));
        mList.add(new MainEntity("检查上传状态"));
        mList.add(new MainEntity("导出7天记录"));
        mList.add(new MainEntity("导出1个月记录"));
        mList.add(new MainEntity("导出3个月记录"));
    }


    private void initDate() {
        ThreadScheduledExecutorUtil.getInstance().getService().scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(QRCode.TIMER);
            }
        }, 0, 1, TimeUnit.SECONDS);
    }


    @Override
    public void handlerMessage(Message message) {
        switch (message.what) {
            case QRCode.TIMER:
                String currentTime = String.format("%1$s", DateUtil.getMainTime());
                time.setText(currentTime);
                break;
            default:

                break;
        }
    }
}
