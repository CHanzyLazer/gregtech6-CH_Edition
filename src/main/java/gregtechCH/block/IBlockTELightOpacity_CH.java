package gregtechCH.block;

import gregapi.block.multitileentity.IMultiTileEntity.IMTE_GetLightOpacity;
import net.minecraft.world.IBlockAccess;

public interface IBlockTELightOpacity_CH {
    void setTELightOpacity(IMTE_GetLightOpacity aTE);
    int getLightOpacity(IBlockAccess aWorld, int aX, int aY, int aZ);
}
