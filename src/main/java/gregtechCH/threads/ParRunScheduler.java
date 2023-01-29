package gregtechCH.threads;

import gregapi.code.ArrayListNoNulls;
import gregapi.util.UT;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static gregapi.data.CS.*;
import static gregtechCH.threads.IRepeatRunnable.*;

/**
 * @author CHanzy
 * 将短时需要重复执行的并行任务自适应分组执行的调度器，解决 ParRunPool 不能处理短时任务的问题
 * 由于需要记录执行时的时间，这里需要包含 ParRunPool 来直接进行执行
 */
public class ParRunScheduler {
    protected final Set<Runnable> mTasks = new LinkedHashSet<>(); // TODO 直接改用 map 存储对应时间即可，因为 set 底层也是用的 map
    private final List<GroupedTask> mGrouped = new ArrayListNoNulls<>();
    private final ParRunPool mParRunPool; // reference of ParRunPool
    private final int mMaxThreadNumber;
    private final int mMinGroupSize; // 每组最小的数量
    private final int mMaxGroupedMulti; // 分组后最大的分组数目相比线程数目的倍数
    
    protected boolean mNeedRegroup = T; // 记录是否需要重新分组，用来避免频繁的重新分组
    
    public ParRunScheduler(ParRunPool aParRunPool, int aMinGroupSize, int aMaxGroupedMulti) {
        mParRunPool = aParRunPool;
        mMaxThreadNumber = Math.max(1, aParRunPool.mPoolSize);
        mMinGroupSize = Math.max(1, aMinGroupSize);
        mMaxGroupedMulti = Math.max(1, aMaxGroupedMulti);
    }
    
    // 添加和删除的接口
    public synchronized void add(@NotNull Runnable aTask) {
        mTasks.add(aTask);
        mNeedRegroup = T;
    }
    public synchronized void remove(@NotNull Runnable aTask) {
        OUT.println("removed task: " + aTask);
        mTasks.remove(aTask);
        if (aTask instanceof IRR_onRemove) ((IRR_onRemove)aTask).onRemove(); // Hook
        mNeedRegroup = T;
    }
    public synchronized void clear() {
        for (Runnable tTask : mTasks) if (tTask instanceof IRR_onRemove) ((IRR_onRemove)tTask).onRemove(); // Hook
        mTasks.clear();
        mNeedRegroup = T;
    }
    
    public synchronized final List<GroupedTask> getGroupedTasks() {
        if (mNeedRegroup) {
            mGrouped.clear();
            int tGroupedLen = Math.max(1, mTasks.size() / mMinGroupSize);
            if (tGroupedLen > mMaxThreadNumber) {
                tGroupedLen = Math.min(mMaxGroupedMulti, tGroupedLen / mMaxThreadNumber) * mMaxThreadNumber; // 保证分组数目是最大线程数的整数倍
            }
            // 直接进行分组，无论 tGroupedLen 是否是 1，因为需要保证在遍历过程中删除 mTasks 元素是安全的
            int tGroupSize = (int)UT.Code.divup(mTasks.size(), tGroupedLen);
            List<Runnable> tSubGroupedList = null;
            for (Runnable tTask : mTasks) {
                if (tSubGroupedList == null) {
                    tSubGroupedList = new ArrayListNoNulls<>(tGroupSize);
                    mGrouped.add(new GroupedTask(tSubGroupedList));
                }
                tSubGroupedList.add(tTask);
                if (tSubGroupedList.size() >= tGroupSize) tSubGroupedList = null;
            }
            mNeedRegroup = F;
        }
        return mGrouped;
    }
    
    // 提供直接执行全部的接口，因为已经存放了 pool
    public final void runAll() {mParRunPool.runAll(getGroupedTasks());} // 此方法一定不能加 synchronized ！！！！新线程执行后获取不到这个锁
    
    
    private class GroupedTask implements Runnable {
        private final Collection<? extends Runnable> mTaskGroup;
        GroupedTask(Collection<? extends Runnable> aTaskGroup) {mTaskGroup = aTaskGroup;}
        // 在执行之前检测是否已经死亡，死亡则需要移除并且不再执行，需要注意线程安全
        @Override
        public void run() {
            for (Runnable tTask : mTaskGroup) {
                if (tTask instanceof IRR_isDead && ((IRR_isDead)tTask).isDead()) remove(tTask);
                else tTask.run();
            }
        }
    }
}
