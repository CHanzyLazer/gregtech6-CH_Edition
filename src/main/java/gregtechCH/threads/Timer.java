package gregtechCH.threads;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author CHanzy
 * 自用的计时类型
 */
public abstract class Timer {
    // 使用读写锁控制，通过周期检测的方式来判断是否需要停止
    public abstract class StoppableAndTimeoutable implements Runnable {
        protected StoppableAndTimeoutable() {mBeginTime = System.currentTimeMillis();}
        private boolean mStop = false;
        private final long mBeginTime;
        private final ReentrantReadWriteLock mRWL = new ReentrantReadWriteLock(); // 这个对象的读写锁
        
        // 使用读写锁获取和修改数值（直接 synchronized 也可，这里只作为读写锁的例子）
        public boolean getStop() {
            mRWL.readLock().lock();
            boolean tOut = mStop;
            mRWL.readLock().unlock();
            return tOut;
        }
        public void setStop(boolean aStop) {
            mRWL.writeLock().lock();
            mStop = aStop;
            mRWL.writeLock().unlock();
        }
        
        @Override
        public void run() {
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
