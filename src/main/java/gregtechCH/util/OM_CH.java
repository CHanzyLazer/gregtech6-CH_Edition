package gregtechCH.util;

import gregapi.code.ArrayListNoNulls;
import gregapi.oredict.OreDictItemData;
import gregapi.oredict.OreDictMaterialStack;
import gregapi.util.OM;
import net.minecraft.item.ItemStack;

import java.util.List;

public class OM_CH {
    // 将 ItemStack 转换为材料的 list，考虑了 ItemStack 的数目以及获取到的 OreDictMaterialStack 需要经过拷贝的问题
    public static List<OreDictMaterialStack> stack(ItemStack aItemStack) {
        if (aItemStack == null || aItemStack.stackSize <= 0) return new ArrayListNoNulls<>();
        
        OreDictItemData tData = OM.anydata(aItemStack);
        if (tData == null) return new ArrayListNoNulls<>();
        List<OreDictMaterialStack> tDataMaterials = tData.getAllMaterialStacks();
        if (tDataMaterials.isEmpty()) return new ArrayListNoNulls<>();
        
        List<OreDictMaterialStack> rList = new ArrayListNoNulls<>(tDataMaterials.size());
        for (OreDictMaterialStack tStack : tDataMaterials) {
            OreDictMaterialStack tStackAdd = tStack.clone();
            tStackAdd.mAmount *= aItemStack.stackSize;
            rList.add(tStackAdd);
        }
        return rList;
    }
}
