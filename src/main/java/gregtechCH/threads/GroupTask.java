package gregtechCH.threads;

import gregapi.code.ArrayListNoNulls;
import gregapi.util.UT;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static gregapi.data.CS.*;
import static gregtechCH.config.ConfigForge.*;

/**
 * @author CHanzy
 * 用来自动将任务分组的数据结构
 * 此类需要和 GroupTaskPool 相互绑定，从而直接提供 GroupTaskPool 需要的类来执行
 */
public class GroupTask<Task extends Runnable> {
    protected final Set<Task> mTasks = new LinkedHashSet<>();
    private final List<SubGroupTask> mGrouped = new ArrayListNoNulls<>();
    private final GroupTaskPool mGroupTaskPool; // reference of GroupTaskPool
    private final int mMaxThreadNumber;
    private final int mMinGroupSize; // 每组最小的数量
    private final int mMaxGroupedMulti; // 分组后最大的分组数目相比线程数目的倍数
    
    protected boolean mNeedRegroup = T; // 记录是否需要重新分组，用来避免频繁的重新分组
    
    public GroupTask(GroupTaskPool aGroupTaskPool) {this(aGroupTaskPool, DATA_GTCH.minGroupSize, DATA_GTCH.maxGroupedMulti);}
    public GroupTask(GroupTaskPool aGroupTaskPool, int aMinGroupSize, int aMaxGroupedMulti) {
        mGroupTaskPool = aGroupTaskPool;
        mMaxThreadNumber = Math.max(1, aGroupTaskPool.mPoolSize);
        mMinGroupSize = Math.max(1, aMinGroupSize);
        mMaxGroupedMulti = Math.max(1, aMaxGroupedMulti);
    }
    
    // 添加和删除的接口
    public void add(@NotNull Task aTask) {
        mTasks.add(aTask);
        mNeedRegroup = T;
    }
    public void remove(@NotNull Task aTask) {
        mTasks.remove(aTask);
        mNeedRegroup = T;
    }
    public void clear() {
        mTasks.clear();
        mNeedRegroup = T;
    }
    
    public final List<SubGroupTask> getGroupedTasks() {
        if (mNeedRegroup) {
            mGrouped.clear();
            int tGroupedLen = Math.max(1, mTasks.size() / mMinGroupSize);
            if (tGroupedLen > mMaxThreadNumber) {
                tGroupedLen = Math.min(mMaxGroupedMulti, tGroupedLen / mMaxThreadNumber) * mMaxThreadNumber; // 保证分组数目是最大线程数的整数倍
            }
            if (tGroupedLen > 1) {
                // 进行分组
                int tGroupSize = (int)UT.Code.divup(mTasks.size(), tGroupedLen);
                List<Runnable> tSubGroupedList = null;
                for (Runnable tTask : mTasks) {
                    if (tSubGroupedList == null) {
                        tSubGroupedList = new ArrayListNoNulls<>(tGroupSize);
                        mGrouped.add(new SubGroupTask(mGroupTaskPool, tSubGroupedList));
                    }
                    tSubGroupedList.add(tTask);
                    if (tSubGroupedList.size() >= tGroupSize) tSubGroupedList = null;
                }
            } else {
                // 只有一个线程，不需要遍历分组
                mGrouped.add(new SubGroupTask(mGroupTaskPool, mTasks));
            }
            mNeedRegroup = F;
        }
        return mGrouped;
    }
    
    // 提供直接按照分组执行的接口，因为已经存放了 pool
    public final void runGrouped() {mGroupTaskPool.runAll(getGroupedTasks());}
    
    
    private static class SubGroupTask extends GroupTaskPool.CountRunnable {
        private final Collection<? extends Runnable> mTasks;
        SubGroupTask(GroupTaskPool aGroupTaskPool, Collection<? extends Runnable> aTasks) {aGroupTaskPool.super(); mTasks = aTasks;}
        @Override public void run2() {for (Runnable tTask : mTasks) tTask.run();}
    }
}
