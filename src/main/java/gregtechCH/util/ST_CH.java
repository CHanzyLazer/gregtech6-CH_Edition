package gregtechCH.util;

import cpw.mods.fml.common.registry.GameRegistry;
import gregapi.code.ModData;
import gregapi.util.ST;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import static gregapi.data.CS.F;
import static gregapi.data.CS.OUT;

/**
 * @author CHanzy
 * Extension of ST
 */
public class ST_CH {
    // 返回 Item 或者 ItemStack 的独立的名称，因为 id 会随着 forge 的自动分配发生改变，主要用于配置文件
    public static String uniqueName(Item aItem) {return GameRegistry.findUniqueIdentifierFor(aItem).toString();}
    public static String uniqueName(ItemStack aItem) {
        if (aItem == null) return null;
        int size = aItem.stackSize;
        if (size <= 0) return null;
        String itemName = GameRegistry.findUniqueIdentifierFor(aItem.getItem()).toString();
        if (itemName == null) return null;
        int damage = aItem.getItemDamage();
        if (size > 1) return itemName + ":" + damage + ":" + size;
        if (damage != 0) return itemName + ":" + damage;
        return itemName;
    }
    // 通过独立名称获取 item
    public static ItemStack make(String aUniqueName) {return make(aUniqueName, F);}
    public static ItemStack make(String aUniqueName, boolean aOut) {
        if (aUniqueName == null) return null;
        String[] namePair = aUniqueName.split(":", 4);
        if (namePair.length < 2) {
            if (aOut) OUT.println("Invalid ItemName: " + aUniqueName);
            return null;
        }
        return ST.make(new ModData(namePair[0], ""), namePair[1], namePair.length>=4 ? Integer.parseInt(namePair[3]) : 1, namePair.length >= 3 ? Integer.parseInt(namePair[2]) : 0);
    }
}
