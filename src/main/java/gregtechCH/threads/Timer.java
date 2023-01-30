package gregtechCH.threads;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author CHanzy
 * 自用的计时类型
 */
public abstract class Timer {
    public abstract class StoppableAndTimeoutable implements Runnable {
        protected StoppableAndTimeoutable() {mBeginTime = System.currentTimeMillis();}
        private boolean mStop = false;
        private final long mBeginTime;
        
        public synchronized boolean getStop() {return mStop;}
        public synchronized void setStop(boolean aStop) {mStop = aStop;}
        
        @Override
        public final void run() {
            if (getStop()) return;
            int tTime = (int)(System.currentTimeMillis() - mBeginTime);
            if (tTime > mLimit) {
                doTimeOut(tTime);
                return;
            }
            // 注意需要串行加入计划
            synchronized (mScheduledThread) {
                mScheduledThread.schedule(this, 20, TimeUnit.MILLISECONDS); // 如果没有停止则在 20 ms 后重新检测
            }
        }
        public abstract void doTimeOut(int aTime);
    }
    
    protected final ScheduledExecutorService mScheduledThread = Executors.newScheduledThreadPool(2); // 可以避免忙等待
    protected StoppableAndTimeoutable mCurrentTimer;
    private final long mLimit;
    public Timer(long aLimit) {mLimit = aLimit;}
    
    // 子类重写来指定自定义的 SubTimer，自定义超时语句
    public abstract StoppableAndTimeoutable getSubTimer(Object aObject);
    
    public final void reset(Object aObject) {
        mCurrentTimer = getSubTimer(aObject);
        // 注意需要串行加入计划
        synchronized (mScheduledThread) {
            mScheduledThread.execute(mCurrentTimer);
        }
    }
    public final void check() {
        mCurrentTimer.setStop(true);
    }
    // 由于是可以实时创建的，提供一个关闭线程池的接口
    public final void close() {mScheduledThread.shutdown();}
}
