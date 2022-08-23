package gregtechCH.tileentity.cores;

import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * @author CHanzy
 * 包含管理 ToolTip 的 core 应该继承的接口
 * 可以使用继承来实现 TE 继承时重写其中的某些子项
 * 也可以使用多个 core 来分别实现其中的每个子项
 * TE 继承并将自身作为 core 传入来指定每个子项使用何种 core
 **/
public interface IMTEC_ToolTips {
    public void toolTipsMultiblock(List<String> aList);
    public void toolTipsRecipe(List<String> aList);
    public void toolTipsEnergy(List<String> aList);
    public void addToolTipsSided(List<String> aList, ItemStack aStack, boolean aF3_H); // greg's
    public void toolTipsUseful(List<String> aList);
    public void toolTipsImportant(List<String> aList);
    public void toolTipsHazard(List<String> aList);
    public void toolTipsOther(List<String> aList, ItemStack aStack, boolean aF3_H);

    // 通用调用
    public static class Util {
        public static void addToolTips(IMTEC_ToolTips aCore, List<String> aList, ItemStack aStack, boolean aF3_H) {
            aCore.toolTipsMultiblock(aList);
            aCore.toolTipsRecipe(aList);
            aCore.toolTipsEnergy(aList);
            aCore.addToolTipsSided(aList, aStack, aF3_H);
            aCore.toolTipsUseful(aList);
            aCore.toolTipsImportant(aList);
            aCore.toolTipsHazard(aList);
            aCore.toolTipsOther(aList, aStack, aF3_H);
        }
    }
}
