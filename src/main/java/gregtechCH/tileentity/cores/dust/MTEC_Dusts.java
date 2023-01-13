package gregtechCH.tileentity.cores.dust;

import gregapi.data.IL;
import gregapi.data.OP;
import gregapi.data.TD;
import gregapi.oredict.OreDictItemData;
import gregapi.oredict.OreDictMaterialStack;
import gregapi.oredict.OreDictPrefix;
import gregapi.tileentity.base.TileEntityBase01Root;
import gregapi.util.OM;
import gregapi.util.ST;
import gregtechCH.code.Triplet;
import gregtechCH.util.OM_CH;
import gregtechCH.util.UT_CH;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Arrays;
import java.util.List;

import static gregapi.data.CS.F;
import static gregapi.data.CS.T;

/**
 * @author CHanzy
 * 拥有粉末输出的包含此 core，可以让输出保持单元的状态
 */
public class MTEC_Dusts {
    protected final TileEntityBase01Root mTE; // reference of te
    public MTEC_Dusts(TileEntityBase01Root aTE, int aSize) {UT_CH.Debug.assertWhenDebug(aTE instanceof IMTEC_HasDusts); mTE = aTE; mDustBuffers = new OreDictMaterialStack[aSize]; Arrays.fill(mDustBuffers, null);}
    protected IMTEC_HasDusts te() {return (IMTEC_HasDusts)mTE;}
    
    public static final OreDictPrefix DUST_OP = OP.dust; // 暂定使用此 prefix
    public final OreDictMaterialStack[] mDustBuffers;
    
    /* NBT 读写 */
    public void readFromNBT(NBTTagCompound aNBT) {
        for (int i = 0; i < mDustBuffers.length; ++i) if (aNBT.hasKey("gt.buffer.dust."+i)) mDustBuffers[i] =  OreDictMaterialStack.load("gt.buffer.dust."+i, aNBT);
        
        // 原版兼容，将原版的灰烬槽中的灰烬全部转换成 dust 类型（尝试转换，如果失败则保留）
        for (int i = 0; i < mDustBuffers.length; ++i) if (needConvert(te().getItem(i))) {
            Triplet<Boolean, ItemStack, OreDictMaterialStack> tResult = convert(te().getItem(i), mDustBuffers[i]);
            if (tResult.a) {te().setItem(1, tResult.b); mDustBuffers[i] = tResult.c;}
        }
    }
    public void writeToNBT(NBTTagCompound aNBT) {
        for (int i = 0; i < mDustBuffers.length; ++i) {
            if (mDustBuffers[i] != null && mDustBuffers[i].mAmount <= 0) mDustBuffers[i] = null; // 存储前先考虑空值的情况
            if (mDustBuffers[i] != null) mDustBuffers[i].save("gt.buffer.dust."+i, aNBT);
        }
    }
    public static boolean needConvert(ItemStack aItemDust) {
        if (aItemDust == null || aItemDust.stackSize <= 0) return F;
        if (IL.RC_Firestone_Refined.equal(aItemDust, T, T) || IL.RC_Firestone_Cracked.equal(aItemDust, T, T)) return F; // 火石的情况，不进行转换
        OreDictItemData tData = OM.data(aItemDust);
        if (tData == null || tData.mPrefix == null) return F; // 不含有材料的其他物品，不进行转换
        return tData.mPrefix.contains(TD.Prefix.DUST_BASED) && tData.mPrefix != DUST_OP;
    }
    // 返回是否成功以及新的 ItemStack, DustBuffer
    public static Triplet<Boolean, ItemStack, OreDictMaterialStack> convert(ItemStack aItemDust, OreDictMaterialStack aDustBuffer) {
        ItemStack newItemDust = null;
        OreDictMaterialStack newDustBuffer = aDustBuffer == null ? null : aDustBuffer.clone();
        // 直接获取其 OreDictMaterialStack，考虑了 aItemStack 的 stack 数目以及 clone
        List<OreDictMaterialStack> tDustStacks = OM_CH.stack(aItemDust);
        for (OreDictMaterialStack tDustStack : tDustStacks) {
            // 尝试将新的物品并入
            ItemStack tItem = DUST_OP.mat(tDustStack.mMaterial, tDustStack.mAmount/DUST_OP.mAmount);
            if (tItem != null && tItem.stackSize > 0) {
                if (newItemDust == null) {
                    if (tItem.stackSize > tItem.getMaxStackSize()) return new Triplet<>(F, tItem, newDustBuffer);
                    newItemDust = tItem;
                } else
                if (ST.equal(tItem, newItemDust)) {
                    int tDifference = Math.min(tItem.stackSize, newItemDust.getMaxStackSize() - newItemDust.stackSize);
                    tItem.stackSize -= tDifference;
                    newItemDust.stackSize += tDifference;
                    if (tItem.stackSize > 0) return new Triplet<>(F, newItemDust, newDustBuffer);
                } else {
                    return new Triplet<>(F, newItemDust, newDustBuffer);
                }
            }
            // 处理余下的灰烬
            if (newDustBuffer == null) newDustBuffer = OM.stack(tDustStack.mMaterial, tDustStack.mAmount%DUST_OP.mAmount);
            else if (newDustBuffer.mMaterial == tDustStack.mMaterial) newDustBuffer.mAmount += tDustStack.mAmount%DUST_OP.mAmount;
            else return new Triplet<>(F, null, newDustBuffer);
        }
        // 成功转换，返回
        return new Triplet<>(T, newItemDust, newDustBuffer);
    }
    
    /* 提供接口 */
    // 将 ItemStack 添加到对应的 DustBuffer中，返回是否添加成功
    public boolean insert(int aIdx, ItemStack aItemStack) {
        // item 不是 dust 类型，不能添加
        if (!OM.prefixcontains(aItemStack, TD.Prefix.DUST_BASED)) return F;
        // 直接获取其 OreDictMaterialStack，考虑了 aItemStack 的 stack 数目以及 clone
        List<OreDictMaterialStack> tStacks = OM_CH.stack(aItemStack);
        if (tStacks.isEmpty()) return T;
        // 如果输出的材料多于一种，则无法放下 TODO 后续添加混合物时记得修改返回混合的粉末
        if (tStacks.size() > 1) return F;
        // 存储前先清空
        if (mDustBuffers[aIdx] != null && mDustBuffers[aIdx].mAmount <= 0) mDustBuffers[aIdx] = null;
        OreDictMaterialStack tDust = tStacks.get(0);
        if (tDust.mAmount <= 0) return T;
        if (mDustBuffers[aIdx] == null) {mDustBuffers[aIdx] = tDust; return T;}
        // 如果输出材料不等于原本的材料，尝试将其放入输出槽中实现切换燃料种类
        if (tDust.mMaterial != mDustBuffers[aIdx].mMaterial) {
            if (te().addItem(aIdx, item(aIdx))) {mDustBuffers[aIdx] = tDust; return T;}
            else return F;
        }
        // 匹配，直接增加数量
        mDustBuffers[aIdx].mAmount += tDust.mAmount; return T;
    }
    
    // 将对应槽中的 buffer 转换为 item 并添加，返回操作是否成功（一般不需要返回值）
    public boolean convert(int aIdx) {
        if (mDustBuffers[aIdx] == null) return F;
        long tConvertSize = mDustBuffers[aIdx].mAmount / DUST_OP.mAmount;
        if (tConvertSize <= 0) return F;
        if (te().addItem(aIdx, DUST_OP.mat(mDustBuffers[aIdx].mMaterial, tConvertSize))) {
            mDustBuffers[aIdx].mAmount = mDustBuffers[aIdx].mAmount % DUST_OP.mAmount;
            return T;
        }
        return F;
    }
    
    // 对应槽是否满了（可以转换就是满了）
    public boolean full(int aIdx) {return mDustBuffers[aIdx] != null && mDustBuffers[aIdx].mAmount >= DUST_OP.mAmount;}
    // 将对应槽的 buffer 转换为 item
    public ItemStack item(int aIdx) {return mDustBuffers[aIdx] == null ? null : OM.dust(mDustBuffers[aIdx].mMaterial, mDustBuffers[aIdx].mAmount);}
    // 清空对应 buffer
    public void kill(int aIdx) {mDustBuffers[aIdx] = null ;}
    // 返回对应槽
    public OreDictMaterialStack stack(int aIdx) {return mDustBuffers[aIdx];}
}
