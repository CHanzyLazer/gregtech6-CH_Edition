package gregtech.asm.transformers.replacements;

import codechicken.nei.NEIServerConfig;
import gregapi.util.ST;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * GTCH, 用来 asm 注入的类
 * 仅输出超时不进行报错
 */
public class Timer {
    // 使用读写锁控制，通过周期检测的方式来判断是否需要停止
    private static abstract class StoppableAndTimeoutable implements Runnable {
        StoppableAndTimeoutable(int aLimit, ScheduledExecutorService aScheduledThread) {mLimit = aLimit; mScheduledThread = aScheduledThread; mBeginTime = System.currentTimeMillis();}
        private final ScheduledExecutorService mScheduledThread; // reference of ScheduledThread, 用于在没有满足条件时继续回调
        private boolean mStop = false;
        private final int mLimit;
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
    private static class SubTimer extends StoppableAndTimeoutable {
        private final Object mItem;
        SubTimer(int aLimit, ScheduledExecutorService aScheduledThread, Object aItem) {super(aLimit, aScheduledThread); mItem = aItem;}
        @Override public void doTimeOut(int aTime) {
            String tItemName = "'" + mItem.toString() + "'";
            if (mItem instanceof Item) tItemName += "(" + ((Item)mItem).getUnlocalizedName() + ")";
            else if (mItem instanceof ItemStack) tItemName += "(" + ((ItemStack)mItem).getUnlocalizedName() + ")";
            
            NEIServerConfig.logger.warn("Item "+tItemName+" takes too much time when loading (more than "+aTime+" ms)");
        }
    }
    
    protected final ScheduledExecutorService mScheduledThread = Executors.newScheduledThreadPool(2); // 可以避免忙等待
    protected StoppableAndTimeoutable mCurrentTimer;
    
    public void reset(Object aItem) {
        mCurrentTimer = new SubTimer(500, mScheduledThread, aItem); // 依旧为 500 ms（注意这个版本的 asm 不支持添加 SIPUSH 和 LDA 等节点）
        // 注意需要串行加入计划
        synchronized (mScheduledThread) {
            mScheduledThread.execute(mCurrentTimer);
        }
    }
    public void check() {
        mCurrentTimer.setStop(true);
    }
}
