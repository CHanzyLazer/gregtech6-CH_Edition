package gregtechCH.threads;

/**
 * @author CHanzy
 * 为重复执行的 runnable 提供一些额外的 hooks，目前仅在 ParRunScheduler 中使用
 */
public interface IRepeatRunnable extends Runnable {
    // Hooks
    interface IRR_onRemove extends IRepeatRunnable {void onRemove();}
    interface IRR_isDead   extends IRepeatRunnable {boolean isDead();}
}
