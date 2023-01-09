package gregtechCH.config.data;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import gregapi.tileentity.machines.MultiTileEntityBasicMachine;
import gregapi.util.UT;
import gregtechCH.config.adapter.ClazzAdapter;
import gregtechCH.config.adapter.ParametersAdapter;
import gregtechCH.config.adapter.RecipeAdapter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DataMultiTileEntity extends DataJson {
    
    public static class Recipe {
        public Object[] value = null;
        public String[] name = null;
    }
    public static class Clazz {
        public Class<?> value = null;
    }
    
    public abstract static class MTEObject {
        public int ID;
        public String localised = null;
        public String categoricalName = null;
        public Integer creativeTabID = null;
        @SerializedName("class")
        public Clazz clazz = null;
        public Integer blockMetaData = null;
        public Integer stackSize = null;
        public String block = null;
        public Recipe recipe = null;
        
        public Object[] recipe() {return recipe == null ? null : recipe.value;}
        @SuppressWarnings("unchecked")
        public Class<? extends TileEntity> clazz() {return clazz == null ? null : (Class<? extends TileEntity>)clazz.value;}
    }
    
    public static class ReplaceObject extends MTEObject {
        public NBTTagCompound parametersMerge = UT.NBT.make();
        public List<String> parametersRemove = new LinkedList<>();
    }
    public static class AppendObject extends MTEObject {
        public NBTTagCompound parameters = null;
        
        public AppendObject() {
            // 提供必要的默认值避免空指针错误
            creativeTabID = 20001;
            clazz = new Clazz();
            clazz.value = MultiTileEntityBasicMachine.class;
            blockMetaData = 0;
            stackSize = 64;
            block = "machine";
        }
    }
    public static class AppendBeforeObject extends AppendObject {
        public int beforeID;
    }
    public static class AppendAfterObject extends AppendObject {
        public int afterID;
    }
    
    // 指定需要的序列化和反序列化器
    @Override
    public GsonBuilder getGsonBuilder() {
        return super.getGsonBuilder()
                .registerTypeAdapter(Clazz.class, new ClazzAdapter())
                .registerTypeAdapter(NBTTagCompound.class, new ParametersAdapter())
                .registerTypeAdapter(Recipe.class, new RecipeAdapter())
                ;
    }
    
    // DATA
    public List<ReplaceObject> replace = new ArrayList<>();
    public List<Integer> remove = new ArrayList<>();
    public List<AppendBeforeObject> appendBefore = new ArrayList<>();
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
