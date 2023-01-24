package gregtechCH.threads;

/**
 * @author CHanzy
 * 长时任务的接口
 * 可以死亡以及在移除提供调用
 */
public interface ILongTimeTask extends Runnable {
    boolean isDead();
    void onRemove();
}
