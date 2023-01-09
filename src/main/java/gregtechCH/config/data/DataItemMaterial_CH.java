package gregtechCH.config.data;

import com.alibaba.fastjson.annotation.JSONField;
import gregapi.data.MD;
import gregapi.data.MT;
import gregapi.oredict.OreDictItemData;
import gregtechCH.config.serializer.ItemDataDeserializer_CH;
import gregtechCH.config.serializer.ItemDeserializer_CH;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class DataItemMaterial_CH extends DataJson_CH {
    public static class ItemMaterial {
        @JSONField(serialize = false, deserializeUsing = ItemDeserializer_CH.class)
        public ItemStack item;
        @JSONField(deserialize = false, name = "item")
        public String itemName;
        
        @JSONField(serialize = false, deserializeUsing = ItemDataDeserializer_CH.class)
        public OreDictItemData data;
        @JSONField(deserialize = false, name = "data")
        public String[] dataName;
        
        public ItemMaterial() {}
        public ItemMaterial(ItemStack aItem, String aItemName, OreDictItemData aData, String[] aDataName) {
            item = aItem; itemName = aItemName; data = aData; dataName = aDataName;
        }
        public ItemMaterial(String aItemName, String[] aDataName) {
            item = ItemDeserializer_CH.get(aItemName); itemName = aItemName; data = ItemDataDeserializer_CH.get(aDataName); dataName = aDataName;
        }
    }
    // 在 Init 阶段给物品设置材料，后续添加都会无效化，用于自定义修改物品材料
    public List<ItemMaterial> IM_Init = new ArrayList<>();
    // 在 PostInit 阶段给物品设置材料，已有材料的物品会无效，用于安全的添加物品材料
    public List<ItemMaterial> IM_PostInit = new ArrayList<>();
    
    public DataItemMaterial_CH() {}
    
    @Override
    public void initDefault() {
        IM_PostInit.clear();
        IM_Init.clear();
    }
    
    protected void setMember(DataItemMaterial_CH aData) {
        this.IM_Init = aData.IM_Init;
        this.IM_PostInit = aData.IM_PostInit;
    }
    @Override
    protected <Type extends DataJson_CH> void setMember(Type aData) {
        setMember((DataItemMaterial_CH)aData);
    }
}
