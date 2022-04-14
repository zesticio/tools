package com.zestic.core.thread;

import com.zestic.core.date.TimeInterval;

public class ConcurrencyTester {
    private final SyncFinisher sf;
    private final TimeInterval timeInterval;
    private long interval;

    public ConcurrencyTester(int threadSize) {
        this.sf = new SyncFinisher(threadSize);
        this.timeInterval = new TimeInterval();
    }

    public ConcurrencyTester test(Runnable runnable) {
        this.sf.clearWorker();

        timeInterval.start();
        this.sf//
            .addRepeatWorker(runnable)//
            .setBeginAtSameTime(true)// 同时开始
            .start();

        this.interval = timeInterval.interval();
        return this;
    }

    /*
     * 重置测试器，重置包括：
     *
     * <ul>
     *     <li>清空worker</li>
     *     <li>重置计时器</li>
     * </ul>
     *
     * @return this
     * @since 5.7.2
     */
    public ConcurrencyTester reset() {
        this.sf.clearWorker();
        this.timeInterval.restart();
        return this;
    }

    /*
     * 获取执行时间
     *
     * @return 执行时间，单位毫秒
     */
    public long getInterval() {
        return this.interval;
    }
}
