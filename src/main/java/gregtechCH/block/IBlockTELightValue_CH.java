package gregtechCH.block;

import gregapi.block.multitileentity.IMultiTileEntity.IMTE_GetLightValue;
import net.minecraft.world.IBlockAccess;

// 拥有实体的方块，并且实体拥有自己光照需要实现这个接口
public interface IBlockTELightValue_CH {
    void setTELightValue(IMTE_GetLightValue aTE);
    int getLightValue(IBlockAccess aWorld, int aX, int aY, int aZ);
}
