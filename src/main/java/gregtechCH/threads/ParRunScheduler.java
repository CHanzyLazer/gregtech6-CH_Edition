package gregtechCH.threads;

import gregapi.code.ArrayListNoNulls;
import gregapi.util.UT;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static gregapi.data.CS.*;
import static gregtechCH.threads.IRepeatRunnable.*;
import static gregtechCH.config.ConfigForge.*;

/**
 * @author CHanzy
 * 将短时需要重复执行的并行任务自适应分组执行的调度器，解决 ParRunPool 不能处理短时任务的问题
 * 由于需要记录执行时的时间，这里需要包含 ParRunPool 来直接进行执行
 */
public class ParRunScheduler {
    protected final Map<Runnable, Long> mTasks = new LinkedHashMap<>(); // task 和对应的执行时间，ns
    private final List<GroupedTask> mGrouped = new ArrayListNoNulls<>();
    private final int mMaxThreadNumber; // 最大线程数
    private final long mTargetRunTime;  // 目标执行时间 ns
    private final int mTolerate;        // 容忍度
    private final float mGrowthFactor;  // 使用线程数目的增长系数
    
    protected boolean mNeedRegroup = T; // 记录是否需要重新分组，用来避免频繁的重新分组
    protected int mUsedTolerate = 0;    // 已使用的容忍度
    protected int mUsedThreadNumber = 1;// 已使用的线程数
    
    // 输入的目标时间单位为 ms
    public ParRunScheduler(int aMaxThreadNumber, long aTargetRunTime, int aTolerate, float aGrowthFactor) {
        mMaxThreadNumber = Math.max(1, aMaxThreadNumber);
        mTargetRunTime = Math.max(1, aTargetRunTime) * 1000000L;
        mTolerate = Math.max(0, aTolerate);
        mGrowthFactor = Math.max(1.0F, aGrowthFactor);
    }
    
    // 添加和删除的接口
    public synchronized void add(@NotNull Runnable aTask) {
        mTasks.put(aTask, 0L);
        mNeedRegroup = T;
    }
    public synchronized void remove(@NotNull Runnable aTask) {
        mTasks.remove(aTask);
        if (aTask instanceof IRR_onRemove) ((IRR_onRemove)aTask).onRemove(); // Hook
        mNeedRegroup = T;
    }
    public synchronized void clear() {
        for (Runnable tTask : mTasks.keySet()) if (tTask instanceof IRR_onRemove) ((IRR_onRemove)tTask).onRemove(); // Hook
        mTasks.clear();
        mNeedRegroup = T;
    }
    
    public final List<GroupedTask> getGroupedTasks() {
        // 如果不需要重新分组，则需要判断每组时间是否符合要求，如果超过了容忍则进行重新分组
        if (!mNeedRegroup) {
            // 获取最长的运行时间
            long tMaxRunTime = 0;
            for (GroupedTask gt : mGrouped) if (gt.mTotalRunTime > tMaxRunTime) tMaxRunTime = gt.mTotalRunTime;
            // 考虑是否需要调整线程数
            if (mUsedThreadNumber < mMaxThreadNumber && tMaxRunTime > mTargetRunTime) ++mUsedTolerate;
            else if (mUsedThreadNumber > 1 && tMaxRunTime < mTargetRunTime / Math.ceil(mGrowthFactor)) --mUsedTolerate; // 耗时很短也需要考虑减少使用的线程数
            else mUsedTolerate = 0;
            // 调整使用的线程数目
            int rUsedThreadNumber = mUsedThreadNumber;
            if (mUsedTolerate > mTolerate) rUsedThreadNumber = Math.min(mMaxThreadNumber, (int)Math.ceil(mUsedThreadNumber * mGrowthFactor));
            if (mUsedTolerate < -mTolerate) rUsedThreadNumber = 1; // 减少直接减少到 1 即可
            if (rUsedThreadNumber != mUsedThreadNumber) {
                if (DATA_GTCH.debugging) OUT.println("DEBUG: Change Thread Number from " + mUsedThreadNumber + " to " + rUsedThreadNumber + ", MaxRunTime: " + tMaxRunTime + " ns");
                mNeedRegroup = T;
                mUsedThreadNumber = rUsedThreadNumber;
            }
        }
        // 重新分组
        if (mNeedRegroup) {
            mUsedTolerate = 0;
            // 先遍历 mGrouped 获取运行时间，因为可能在执行后 mTasks 和 GroupedTask 不一致
            double tMeanRunTime = 0.0;
            for (GroupedTask gt : mGrouped) {
                int i = 0;
                for (long tRunTime : gt.mTaskRunTime) {
                    Runnable tTask = gt.mTaskGroup.get(i);
                    if (mTasks.containsKey(tTask)) {
                        mTasks.put(tTask, tRunTime);
                        tMeanRunTime += tRunTime;
                    }
                    ++i;
                }
            }
            tMeanRunTime /= mUsedThreadNumber;
            // 根据运行时间来分组
            mGrouped.clear();
            int tGroupSize = (int)UT.Code.divup(mTasks.size(), mUsedThreadNumber) * 2; // 估计分组后的容量，减少扩容的损耗
            List<Runnable> tSubGroupedList = null;
            double tTime = 0.0;
            for (Map.Entry<Runnable, Long> tEntry : mTasks.entrySet()) {
                if (tSubGroupedList == null) {
                    tSubGroupedList = new ArrayListNoNulls<>(tGroupSize);
                    mGrouped.add(new GroupedTask(tSubGroupedList));
                }
                tSubGroupedList.add(tEntry.getKey());
                tTime += tEntry.getValue();
                if (tTime > tMeanRunTime) {
                    tSubGroupedList = null;
                    tTime = 0.0;
                }
            }
            mNeedRegroup = F;
        }
        return mGrouped;
    }
    
    
    private class GroupedTask implements Runnable {
        protected final List<Runnable> mTaskGroup;
        protected final List<Long> mTaskRunTime;  // in ns
        protected long mTotalRunTime = 0;         // in ns
        GroupedTask(@NotNull List<Runnable> aTaskGroup) {mTaskGroup = aTaskGroup; mTaskRunTime = new ArrayList<>(aTaskGroup.size());}
        @Override public void run() {
            mTaskRunTime.clear();
            mTotalRunTime = 0;
            for (Runnable tTask : mTaskGroup) {
                // 在执行之前检测是否已经死亡，死亡则需要移除并且不再执行，需要注意线程安全
                if (tTask instanceof IRR_isDead && ((IRR_isDead)tTask).isDead()) {
                    remove(tTask);
                } else {
                    // 统计耗时
                    long tRunTime = System.nanoTime();
                    tTask.run();
                    tRunTime = System.nanoTime() - tRunTime;
                    mTaskRunTime.add(tRunTime);
                    mTotalRunTime += tRunTime;
                }
            }
        }
    }
}
