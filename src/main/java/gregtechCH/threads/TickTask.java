package gregtechCH.threads;

import gregapi.tileentity.ITileEntityErrorable;
import gregapi.tileentity.ITileEntityUnloadable;

import static gregapi.data.CS.*;
import static gregtechCH.threads.IRepeatRunnable.*;

/**
 * @author CHanzy
 * 提供专门的 tick 任务
 * 可以出错，消亡，以及消亡后需要需要进行一些操作
 */
public abstract class TickTask<TE extends ITileEntityUnloadable & ITileEntityErrorable> implements IRR_onRemove, IRR_isDead {
    protected final TE mTE;
    private boolean mError = F;
    @Override public final boolean isDead() {return mError || mTE.isDead();}
    @Override public final void onRemove() {if (!mError) onRemove2();}
    public TickTask(TE aTE) {mTE = aTE;}
    
    @Override
    public final void run() {
        try {
            run2();
        } catch (Throwable e) {
            mError = T;
            mTE.setError(errorMessage() + e);
            synchronized (ERR) {e.printStackTrace(ERR);}
        }
    }
    // 重写 hashCode 以及 equals 来方便移除
    @Override public int hashCode() {return mTE.hashCode();}
    @Override public boolean equals(Object o) {
        if (this == o) return T;
        if (o == null) return F;
        if (o instanceof TickTask<?>) return mTE.equals(((TickTask<?>)o).mTE);
        return F;
    }
    
    // stuff to override
    protected abstract void onRemove2();
    protected abstract void run2();
    protected abstract String errorMessage();
}
