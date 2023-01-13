package gregtechCH.tileentity.cores.dust;

import net.minecraft.item.ItemStack;

/**
 * 内部拥有 MTEC_Dusts 的实体继承此接口，用于方便通过实体获取 core
 * 并方便表明 core 需要的实体接口
 */
public interface IMTEC_HasDusts {
    // 返回内部的 core
    MTEC_Dusts dust();
    // 返回存储 Dust 的对应槽位的物品
    ItemStack getItem(int aIdx);
    // 设置对应存储槽的物品
    void setItem(int aIdx, ItemStack aItem);
    // 向对应槽中添加物品，返回是否成功
    boolean addItem(int aIdx, ItemStack aItem);
}
