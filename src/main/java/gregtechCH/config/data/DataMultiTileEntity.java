package gregtechCH.config.data;

import com.alibaba.fastjson.annotation.JSONField;
import gregapi.tileentity.machines.MultiTileEntityBasicMachine;
import gregapi.util.UT;
import gregtechCH.config.serializer.ClassDeserializer;
import gregtechCH.config.serializer.ClassSerializer;
import gregtechCH.config.serializer.ParametersDeserializer;
import gregtechCH.config.serializer.ParametersSerializer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DataMultiTileEntity extends DataJson {
    public static abstract class MTEObject extends DataHasRecipe {
        @JSONField(ordinal = 3)
        public int ID;
        @JSONField(ordinal = 1)
        public String localised = null;
        @JSONField(ordinal = 2)
        public String categoricalName = null;
        @JSONField(ordinal = 4)
        public Integer creativeTabID = null;
        @JSONField(ordinal = 5, name = "class", deserializeUsing = ClassDeserializer.class, serializeUsing = ClassSerializer.class)
        public Class<? extends TileEntity> teClass = null;
        @JSONField(ordinal = 6)
        public Integer blockMetaData = null;
        @JSONField(ordinal = 7)
        public Integer stackSize = null;
        @JSONField(ordinal = 8)
        public String block = null;
    }
    
    public static class ReplaceObject extends MTEObject {
        @JSONField(ordinal = 9, deserializeUsing = ParametersDeserializer.class, serializeUsing = ParametersSerializer.class)
        public NBTTagCompound parametersMerge = UT.NBT.make();
        @JSONField(ordinal = 10)
        public List<String> parametersRemove = new LinkedList<>();
        
        public ReplaceObject() {}
    }
    public static class AppendObject extends MTEObject {
        @JSONField(ordinal = 9, deserializeUsing = ParametersDeserializer.class, serializeUsing = ParametersSerializer.class)
        public NBTTagCompound parameters = null;
        
        public AppendObject() {
            // 提供必要的默认值避免空指针错误
            creativeTabID = 20001;
            teClass = MultiTileEntityBasicMachine.class;
            blockMetaData = 0;
            stackSize = 64;
            block = "machine";
        }
    }
    public static class AppendBeforeObject extends AppendObject {
        @JSONField(ordinal = 0)
        public int beforeID;
    
        public AppendBeforeObject() {}
    }
    public static class AppendAfterObject extends AppendObject {
        @JSONField(ordinal = 0)
        public int afterID;
    
        public AppendAfterObject() {}
    }
    
    // DATA
    @JSONField(ordinal = 0)
    public List<ReplaceObject> replace = new ArrayList<>();
    @JSONField(ordinal = 1)
    public List<Integer> remove = new ArrayList<>();
    @JSONField(ordinal = 2)
    public List<AppendBeforeObject> appendBefore = new ArrayList<>();
    @JSONField(ordinal = 3)
    public List<AppendAfterObject> appendAfter = new ArrayList<>();
    
    public DataMultiTileEntity() {}
    
    @Override
    public void initDefault() {
        replace.clear();
        remove.clear();
        appendBefore.clear();
        appendAfter.clear();
    }
    
    protected void setMember(DataMultiTileEntity aData) {
        this.replace = aData.replace;
        this.remove = aData.remove;
        this.appendBefore = aData.appendBefore;
        this.appendAfter = aData.appendAfter;
    }
    @Override
    protected <Type extends DataJson> void setMember(Type aData) {
        setMember((DataMultiTileEntity)aData);
    }
}
