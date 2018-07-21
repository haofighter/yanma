package com.szxb.buspay.util.rx;

import com.szxb.buspay.db.entity.bean.QRScanMessage;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * 作者: Tangren on 2017/7/31
 * 包名：com.szxb.utils.rx
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class RxBus {

    private static volatile RxBus instance;
    private final Subject<Object, Object> bus;

    private RxBus() {
        bus = new SerializedSubject<>(PublishSubject.create());
    }

    /**
     * 单例RxBus
     *
     * @return RxBus
     */
    public static RxBus getInstance() {
        if (null == instance) {
            synchronized (RxBus.class) {
                if (null == instance) {
                    instance = new RxBus();
                }
            }
        }
        return instance;
    }

    /**
     * 发送一个新事件
     *
     * @param o
     */
    public void send(QRScanMessage o) {
        bus.onNext(o);
    }

    /**
     * 返回特定类型的被观察者
     *
     * @param eventType eventType
     * @param <T>
     * @return
     */
    public <T> Observable<T> toObservable(Class<T> eventType) {
        return bus.ofType(eventType);
    }

    /**
     * 判断是否有订阅者
     */
    public boolean hasObservers() {
        return bus.hasObservers();
    }

}
