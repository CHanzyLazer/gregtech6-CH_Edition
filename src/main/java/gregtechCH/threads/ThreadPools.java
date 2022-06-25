package gregtechCH.threads;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPools {
    public static final ITaskNumberExecutor NONE_THREAD     = newSingleNoneExecutor();
    public static final ITaskNumberExecutor LIGHT_THREAD    = newSingleThreadExecutor();
    public static final ITaskNumberExecutor SOUND_THREAD    = newSingleThreadExecutor();
    public static final ITaskNumberExecutor MACHINE_THREAD  = newSingleThreadExecutor();

    // 直接照搬 Executor 的方法实现自定义的线程池
    public static ITaskNumberExecutor newSingleThreadExecutor() {return new TaskNumberExecutor (new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>()));}
    public static ITaskNumberExecutor newSingleNoneExecutor() {return new NoneThreadExecutor();}

    // 扩展原本的 Executor 来方便调用
    public interface ITaskNumberExecutor extends Executor {
        int getTaskNumber();
    }
    public static class TaskNumberExecutor implements ITaskNumberExecutor {
        ThreadPoolExecutor mThreadPoolExecutor;
        public TaskNumberExecutor(ThreadPoolExecutor aExecutor) {mThreadPoolExecutor = aExecutor;}

        @Override public void execute(@NotNull Runnable aRunnable) {mThreadPoolExecutor.execute(aRunnable);}
        @Override public int getTaskNumber() {return mThreadPoolExecutor.getQueue().size();}
    }
    public static class NoneThreadExecutor implements ITaskNumberExecutor {
        // 不会创建新的线程，直接在 execute 阶段执行
        @Override public void execute(@NotNull Runnable aRunnable) {aRunnable.run();}
        @Override public int getTaskNumber() {return 0;}
    }
}
