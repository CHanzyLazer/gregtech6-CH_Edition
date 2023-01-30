package gregtechCH.threads;

import java.util.Collection;
import java.util.concurrent.*;

import static gregapi.data.CS.OUT;

/**
 * @author CHanzy
 * 并行执行 Runnable 的 pool
 * 通过输入 Collection 来并行执行，并且会等待所有的任务执行完毕，并且会在执行超时的情况下进行警告
 * 由于使用了 CountDownLatch 进行计数来实现等待任务执行完毕，因此使用时需要提交较为长时的任务来减少损耗
 */
public class ParRunPool {
    private final ExecutorService mThreadPool;
    private final Timer mTimer;
    private final int mPoolSize;
    public final int getPoolSize() {return mPoolSize;}
    
    public ParRunPool(int aPoolSize) {this(aPoolSize, 100);}
    public ParRunPool(int aPoolSize, int aWarnTime) {
        mPoolSize = aPoolSize;
        mThreadPool = Executors.newFixedThreadPool(aPoolSize);
        mTimer = new Timer(aWarnTime) {
            @Override public StoppableAndTimeoutable getSubTimer(final Object aObject) {
                return new StoppableAndTimeoutable() {
                    @Override public void doTimeOut(int aTime) {OUT.println("WARNING: GroupTaskPool '" + aObject + "' just takes too much time to runAll (more than " + aTime +" ms)");}
                };
            }
        };
    }
    
    // 无论如何都无法避免子类使用了不同的 CountDownLatch 从而死循环，因此只能使用 lambda 表达式来构造 “临时子类”
    public void runAll(Collection<? extends Runnable> aTasks) {
        final CountDownLatch tLatch = new CountDownLatch(aTasks.size());
        for (final Runnable tTask : aTasks) mThreadPool.execute(()->{try {tTask.run();} finally {tLatch.countDown();}});
        mTimer.reset(this);
        try {tLatch.await();} catch (InterruptedException ignored) {}
        mTimer.check();
    }
    
    public void close() {
        mThreadPool.shutdown();
        mTimer.close();
    }
}
