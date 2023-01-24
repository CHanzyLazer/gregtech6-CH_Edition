package gregtechCH.threads;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

import static gregapi.data.CS.T;


/**
 * @author CHanzy
 * 专门用于 LongTimeTask 的 GroupTask
 */
public class GroupLongTimeTask extends GroupTask<ILongTimeTask> {
    public GroupLongTimeTask(GroupTaskPool aGroupTaskPool) {super(aGroupTaskPool);}
    public GroupLongTimeTask(GroupTaskPool aGroupTaskPool, int aMinGroupSize, int aMaxGroupedMulti) {super(aGroupTaskPool, aMinGroupSize, aMaxGroupedMulti);}
    
    // 重写移除的构造函数
    @Override
    public void remove(@NotNull ILongTimeTask aTask) {
        super.remove(aTask);
        aTask.onRemove();
    }
    // 提供新的直接移除死亡 task 的接口
    public void clearDeadTask() {
        Iterator<ILongTimeTask> tIt = mTasks.iterator();
        while (tIt.hasNext()) {
            ILongTimeTask tTask = tIt.next();
            if (tTask.isDead()) {
                tIt.remove();
                mNeedRegroup = T;
                tTask.onRemove();
            }
        }
    }
    @Override
    public void clear() {
        for (ILongTimeTask tTask : mTasks) tTask.onRemove();
        super.clear();
    }
}
