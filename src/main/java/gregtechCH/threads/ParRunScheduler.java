package gregtechCH.threads;

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
    private final List<GroupedTask> mGrouped = new ArrayList<>();
    private final int mMaxThreadNumber; // 最大线程数
    private final long mTargetRunTime;  // 目标执行时间 ns
    private final int mTolerate;        // 容忍度
    private final float mGrowthFactor;  // 使用线程数目的增长系数
    
    protected boolean mNeedRegroup = T; // 记录是否需要重新分组，用来避免频繁的重新分组
    protected int mChangeThreadTolerate = 0, mRegroupTolerate = 0; // 已使用的容忍度，分别是调整线程数目的和重新分组的
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
        // 如果不需要重新分组，则需要判断每组时间是否符合要求，如果超过了容忍则进行重新分组。同样只在每 10 tick 考虑一次
        if (!mNeedRegroup && SERVER_TIME%10 == 0 && !mGrouped.isEmpty()) {
            // 获取最长最短的运行时间（可能需要考虑第二最短运行时间，由于重新分组基本没有太多损耗，因此不考虑）
            long tMaxRunTime = 0, tMinRunTime = Long.MAX_VALUE >> 1;
            for (GroupedTask gt : mGrouped) {
                if (gt.mTotalRunTime > tMaxRunTime) tMaxRunTime = gt.mTotalRunTime;
                if (gt.mTotalRunTime < tMinRunTime) tMinRunTime = gt.mTotalRunTime;
            }
            // 考虑是否需要调整线程数
            if (mUsedThreadNumber < mMaxThreadNumber && tMaxRunTime > mTargetRunTime) ++mChangeThreadTolerate;
            else if (mUsedThreadNumber > 1 && tMaxRunTime < mTargetRunTime / Math.ceil(mGrowthFactor)) --mChangeThreadTolerate; // 耗时很短也需要考虑减少使用的线程数
            else mChangeThreadTolerate = 0;
            // 调整使用的线程数目
            int rUsedThreadNumber = mUsedThreadNumber;
            if (mChangeThreadTolerate > mTolerate) rUsedThreadNumber = Math.min(mMaxThreadNumber, (int)Math.ceil(mUsedThreadNumber * mGrowthFactor));
            if (mChangeThreadTolerate < -mTolerate) rUsedThreadNumber = 1; // 减少直接减少到 1 即可
            if (rUsedThreadNumber != mUsedThreadNumber) {
                if (DATA_GTCH.debugging) OUT.println("DEBUG: Change Thread Number from " + mUsedThreadNumber + " to " + rUsedThreadNumber + ", MaxRunTime: " + tMaxRunTime + " ns");
                mNeedRegroup = T;
                mUsedThreadNumber = rUsedThreadNumber;
            }
            // 考虑是否需要重新分组（最长最短时间差距过大）
            if (tMaxRunTime > tMinRunTime * 2) ++mRegroupTolerate;
            else mRegroupTolerate = 0;
            if (mRegroupTolerate > mTolerate) {
                if (DATA_GTCH.debugging) OUT.println("DEBUG: Regroup for More Uniform RunTime, MaxRunTime: " + tMaxRunTime + " ns, MinRunTime: " + tMinRunTime + " ns");
                mNeedRegroup = T;
            }
        }
        // 重新分组
        if (mNeedRegroup) {
            if (DATA_GTCH.debugging) OUT.println("DEBUG: Regrouping ParRunTasks(" + this + ")...");
            mChangeThreadTolerate = 0;
            mRegroupTolerate = 0;
            // 先遍历 mGrouped 获取运行时间，因为可能在执行后 mTasks 和 GroupedTask 不一致
            double tMeanRunTime = 0.0;
            for (GroupedTask gt : mGrouped) for (int i = 0; i < gt.mTaskRunTime.size(); ++i) {
                long tRunTime = gt.mTaskRunTime.get(i);
                Runnable tTask = gt.mTaskGroup.get(i);
                if (mTasks.containsKey(tTask)) {
                    mTasks.put(tTask, tRunTime);
                    tMeanRunTime += tRunTime;
                }
            }
            tMeanRunTime /= mUsedThreadNumber;
            if (tMeanRunTime <= 0.0) mUsedThreadNumber = 1;
            // 根据运行时间来分组
            mGrouped.clear();
            int tGroupSize = mUsedThreadNumber <= 1 ? mTasks.size() : (int)UT.Code.divup(mTasks.size(), mUsedThreadNumber) * 2; // 估计分组后的容量，减少扩容的损耗
            List<Runnable> tSubGroupedList = null;
            double tTime = 0.0;
            for (Map.Entry<Runnable, Long> tEntry : mTasks.entrySet()) {
                if (tSubGroupedList == null) {
                    tSubGroupedList = new ArrayList<>(tGroupSize);
                    mGrouped.add(new GroupedTask(tSubGroupedList));
                }
                tSubGroupedList.add(tEntry.getKey());
                tTime += tEntry.getValue();
                if (tTime > tMeanRunTime && mGrouped.size() < mUsedThreadNumber) {
                    tSubGroupedList = null;
                    tTime = 0.0;
                }
            }
            mNeedRegroup = F;
            if (DATA_GTCH.debugging) {
                OUT.println("DEBUG: Regrouping ParRunTasks(" + this + ") Finished, Info: ");
                OUT.println("    UsedThreadNumber: " + mUsedThreadNumber);
                OUT.printf ("    MeanRunTime: %.4f ms%n", tMeanRunTime*0.000001);
                OUT.println("    Tasks Size: " + mTasks.size());
            }
        }
        return mGrouped;
    }
    
    
    private class GroupedTask implements Runnable {
        protected final List<Runnable> mTaskGroup;
        protected final List<Long> mTaskRunTime;  // in ns
        protected long mTotalRunTime = 0;         // in ns
        GroupedTask(@NotNull List<Runnable> aTaskGroup) {mTaskGroup = aTaskGroup; mTaskRunTime = new ArrayList<>(aTaskGroup.size());}
        @Override public void run() {
            // 如果总时间为 0 且队列不为空，则全部都需要统计
            final boolean tStatAll = mTotalRunTime == 0 && !mTaskGroup.isEmpty();
            if (tStatAll) mTaskRunTime.clear();
            // 遍历执行任务
            for (int i = 0; i < mTaskGroup.size(); ++i) {
                Runnable tTask = mTaskGroup.get(i);
                // 在执行之前检测是否已经死亡，死亡则需要移除并且不再执行，需要注意线程安全
                if (tTask instanceof IRR_isDead && ((IRR_isDead)tTask).isDead()) {
                    remove(tTask);
                } else {
                    // 如果需要统计全部耗时，则进行常规的统计
                    if (tStatAll) {
                        long tRunTime = System.nanoTime();
                        tTask.run();
                        tRunTime = System.nanoTime() - tRunTime;
                        mTotalRunTime += tRunTime;
                        mTaskRunTime.add(tRunTime);
                    } else
                    // 如果已经全部统计过了则只需要每次更新其中的 1/10
                    if (i%10 == SERVER_TIME%10) {
                        long tRunTime = System.nanoTime();
                        tTask.run();
                        tRunTime = System.nanoTime() - tRunTime;
                        // 总时间进行更新
                        mTotalRunTime -= mTaskRunTime.get(i);
                        mTotalRunTime += tRunTime;
                        // 每个任务的时间进行更新
                        mTaskRunTime.set(i, tRunTime);
                    } else {
                        tTask.run();
                    }
                }
            }
        }
    }
}
