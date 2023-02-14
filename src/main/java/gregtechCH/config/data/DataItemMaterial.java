package gregtechCH.config.data;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import gregapi.oredict.OreDictItemData;
import gregtechCH.config.adapter.ItemDataAdapter;
import gregtechCH.config.adapter.ItemAdapter;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static gregapi.data.CS.F;
import static gregapi.data.CS.T;

/**
 * @author CHanzy
 */
public class DataItemMaterial extends DataJson {
    
    public static class ItemMaterial {
        public ItemStack item = null;
        public OreDictItemData data = null;
    }
    
    // 指定需要的序列化和反序列化器
    @Override
    public GsonBuilder getGsonBuilder() {
        return super.getGsonBuilder()
                .registerTypeAdapter(ItemStack.class, new ItemAdapter())
                .registerTypeAdapter(OreDictItemData.class, new ItemDataAdapter())
                ;
    }
    
    // 在 Init 阶段给物品设置材料，后续添加都会无效化，用于自定义修改物品材料
    public List<ItemMaterial> IM_Init = new ArrayList<>();
    // 在 PostInit 阶段给物品设置材料，已有材料的物品会无效，用于安全的添加物品材料
    public List<ItemMaterial> IM_PostInit = new ArrayList<>();
    
    public DataItemMaterial() {}
    
    @Override
    public void initDefault() {
        IM_PostInit.clear();
        IM_Init.clear();
    }
    
    protected void setMember(DataItemMaterial aData) {
        this.IM_Init = aData.IM_Init;
        this.IM_PostInit = aData.IM_PostInit;
    }
    @Override
    protected <Type extends DataJson> void setMember(Type aData) {
        setMember((DataItemMaterial)aData);
    }
}
