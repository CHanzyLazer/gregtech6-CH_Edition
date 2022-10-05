package gregtechCH.tileentity;

import net.minecraft.world.IBlockAccess;

// 在渲染更新之后调用，主要用于给需要立即重新渲染的实体调用，也可以执行其他的操作
public interface ITEAfterUpdateRender {
    void doAfterUpdateRender_CH(IBlockAccess aWorld, int aX, int aY, int aZ);
}
