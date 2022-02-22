package gregtechCH.vision;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregapi.block.multitileentity.IMultiTileEntity.IMTE_GetLightOpacity;

public interface IAlternateOpacity_CH extends IMTE_GetLightOpacity {
    // 注意仅在客户端调用
    // 用于使得自动调整不透光度的方块能即时调整
    /* 使用这个方法来调整不透光度 */
    @Override int getLightOpacity();
    /* 在改变透光度后需要主动更新亮度值 */
    @SideOnly(Side.CLIENT)
    boolean updateLightCheck();
    /* 检测改变透光度后重置（防止多次或者没有必要的额外亮度更新） */
    @SideOnly(Side.CLIENT)
    void updateLightReset();
}
