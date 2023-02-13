package gregtechCH.tileentity.misc;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregapi.block.multitileentity.MultiTileEntityBlock;
import gregapi.block.multitileentity.MultiTileEntityRegistry;
import gregapi.data.*;
import gregapi.network.INetworkHandler;
import gregapi.network.IPacket;
import gregapi.oredict.OreDictMaterial;
import gregapi.oredict.OreDictPrefix;
import gregapi.render.BlockTextureCopied;
import gregapi.render.BlockTextureDefault;
import gregapi.render.BlockTextureMulti;
import gregapi.render.ITexture;
import gregapi.tileentity.data.ITileEntitySurface;
import gregapi.tileentity.notick.TileEntityBase03MultiTileEntities;
import gregapi.util.ST;
import gregapi.util.UT;
import gregapi.util.WD;
import gregtechCH.code.Triplet;
import gregtechCH.data.LH_CH;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static gregapi.block.multitileentity.IMultiTileEntity.*;
import static gregapi.data.CS.*;
import static gregtechCH.data.CS_CH.*;
import static gregtechCH.tileentity.misc.MultiTileEntityDeposit.StateAttribute.ZL_SA;

/**
 * @author CHanzy
 * 新名称：矿藏，可以使用对应的钻头进行开采
 * 随着开采会不断增加开采难度，最后消失或者变成基岩
 */
public class MultiTileEntityDeposit extends TileEntityBase03MultiTileEntities implements IMTE_OnRegistration, ITileEntitySurface, IMTE_IsSideSolid, IMTE_GetExplosionResistance, IMTE_GetBlockHardness, IMTE_GetLightOpacity, IMTE_SyncDataByte, IMTE_SyncDataByteArray, IMTE_OnToolClick, IMTE_AddToolTips {
    // 每个 state 的通用属性，用于方便调用以及添加时使用
    public static class StateAttribute {
        static final StateAttribute[] ZL_SA = new StateAttribute[0];
        
        final long mMaxDurability; // 满耐久
        final int mLevel; // 需要的“挖掘等级”
        final List<Triplet<ItemStack, Integer, Integer>> mOreList; // <ore, prob, durability> 矿石，概率权重，消耗的耐久度
        final int mTotalProb;
        
        public StateAttribute(long aMaxDurability, int aLevel) {
            mMaxDurability = Math.max(0, aMaxDurability);
            mLevel = Math.max(0, aLevel);
            mOreList = Collections.emptyList();
            mTotalProb = 0;
        }
        public StateAttribute(long aMaxDurability, int aLevel, ItemStack aFirstOre, int aFirstProb, int aFirstDurability, Object... aOreList) {
            mMaxDurability = Math.max(0, aMaxDurability);
            mLevel = Math.max(0, aLevel);
            List<Triplet<ItemStack, Integer, Integer>> tOreProbList = new ArrayList<>(aOreList.length/2 + 1);
            tOreProbList.add(new Triplet<>(aFirstOre, Math.max(0, aFirstProb), Math.max(0, aFirstDurability)));
            for (int i=2; i<aOreList.length; i+=3) tOreProbList.add(new Triplet<>((ItemStack)aOreList[i-2], Math.max(0, (Integer)aOreList[i-1]), Math.max(0, (Integer)aOreList[i])));
            mOreList = Collections.unmodifiableList(tOreProbList);
            int tTotalProb = 0;
            for (Triplet<ItemStack, Integer, Integer> tTriplet : mOreList) tTotalProb += tTriplet.b;
            mTotalProb = tTotalProb;
        }
        StateAttribute(long aMaxDurability, int aLevel, List<Triplet<ItemStack, Integer, Integer>> aOreProbList) {
            mMaxDurability = Math.max(0, aMaxDurability);
            mLevel = Math.max(0, aLevel);
            mOreList = Collections.unmodifiableList(aOreProbList);
            int tTotalProb = 0;
            for (Triplet<ItemStack, Integer, Integer> tTriplet : mOreList) tTotalProb += tTriplet.b;
            mTotalProb = tTotalProb;
        }
        
        // NBT 读写
        public void save(NBTTagCompound rNBT, String aKey) {
            if (mMaxDurability <= 0 && mOreList.isEmpty()) {rNBT.removeTag(aKey); return;}
            NBTTagCompound tNBT = UT.NBT.make();
            UT.NBT.setNumber(tNBT, NBT_MAXDURABILITY, mMaxDurability);
            UT.NBT.setNumber(tNBT, NBT_LEVEL, mLevel);
            if (!mOreList.isEmpty()) {
                NBTTagList tNBTOreList = new NBTTagList();
                for (Triplet<ItemStack, Integer, Integer> tTriplet : mOreList) tNBTOreList.appendTag(UT.NBT.make("ore", ST.save(tTriplet.a), "prob", tTriplet.b, "dur", tTriplet.c));
                tNBT.setTag("ore.list", tNBTOreList);
            }
            rNBT.setTag(aKey, tNBT);
        }
        public static StateAttribute load(NBTTagCompound aNBT, String aKey) {
            if (!aNBT.hasKey(aKey)) return new StateAttribute(0, 0);
            NBTTagCompound tNBT = aNBT.getCompoundTag(aKey);
            if (tNBT.hasNoTags()) return new StateAttribute(0, 0);
            
            long tMaxDurability = tNBT.getLong(NBT_MAXDURABILITY);
            int tLevel = tNBT.getInteger(NBT_LEVEL);
            if (!tNBT.hasKey("ore.list")) return new StateAttribute(tMaxDurability, tLevel);
            NBTBase tTag = tNBT.getTag("ore.list");
            if (!(tTag instanceof NBTTagList)) return new StateAttribute(tMaxDurability, tLevel);
            NBTTagList tNBTOreList = (NBTTagList) tTag;
            if (tNBTOreList.tagCount() == 0) return new StateAttribute(tMaxDurability, tLevel);
            
            List<Triplet<ItemStack, Integer, Integer>> tOreProbList = new ArrayList<>(tNBTOreList.tagCount());
            for (int i = 0; i < tNBTOreList.tagCount(); ++i) {
                NBTTagCompound tNBTOre = tNBTOreList.getCompoundTagAt(i);
                tOreProbList.add(new Triplet<>(ST.load(tNBTOre, "ore"), tNBTOre.getInteger("prob"), tNBTOre.getInteger("dur")));
            }
            return new StateAttribute(tMaxDurability, tLevel, tOreProbList);
        }
    }
    // 提供一个方法比较方便的注册矿藏，并且顺便注册假的合成表
    public static void addDeposit(int aID, int aCreativeTabID, StateAttribute[] aStateAttributes, MultiTileEntityRegistry aRegistry, MultiTileEntityBlock aBlock, Class<? extends TileEntity> aClass, OreDictMaterial aMat) {
        NBTTagCompound rNBT = UT.NBT.make(NBT_MATERIAL, aMat);
        if (aStateAttributes.length > 0) {
            for (int i=0; i<aStateAttributes.length; ++i) aStateAttributes[i].save(rNBT, NBT_ATTRIBUTE+"."+i);
            UT.NBT.setNumber(rNBT, NBT_DURABILITY, aStateAttributes[0].mMaxDurability);
        }
        aRegistry.add(RegType.GTCH, aMat.getLocal() + " Deposit", "Untyped", aID, aCreativeTabID, aClass, 0, 64, aBlock, rNBT);
    }
    
    /* Main Code */
    protected OreDictMaterial mMaterial = MT.Coal;  // 用于显示的材料类型
    protected OreDictPrefix mPrefix = null;         // 方块具体的表面材质种类，仅客户端有效
    protected byte mState = 0;                      // 决定矿藏的状态
    
    protected long mDurability = 0L;
    protected StateAttribute[] mStateAttributes = ZL_SA;
    
    // 记录正在挖掘的矿物
    protected ItemStack mOre = NI;
    protected long mProgress = 0, mMaxProgress = 0;
    
    // 根据 mDesign 获取具体的 prefix 种类
    protected void updatePrefix() {
        switch (mState) {
        case 0:  {mPrefix = OP.oreBedrock; break;}
        case 1:  {mPrefix = OP.oreSmall; break;}
        default: {mPrefix = null; break;}
        }
    }
    protected byte maxSate() {return 2;}
    
    @Override
    public void readFromNBT2(NBTTagCompound aNBT) {
        super.readFromNBT2(aNBT);
        mStateAttributes = new StateAttribute[maxSate()+1];
        for (int i = 0; i < maxSate(); ++i) mStateAttributes[i] = StateAttribute.load(aNBT, NBT_ATTRIBUTE+"."+i);
        mStateAttributes[mStateAttributes.length-1] = new StateAttribute(0, 0); // 仅用于防止数组越界
        
        if (aNBT.hasKey(NBT_MATERIAL)) mMaterial = OreDictMaterial.get(aNBT.getString(NBT_MATERIAL));
        if (aNBT.hasKey(NBT_DESIGN)) mState = aNBT.getByte(NBT_DESIGN);
        if (aNBT.hasKey(NBT_DURABILITY)) mDurability = UT.Code.bind(0, mStateAttributes[mState].mMaxDurability, aNBT.getLong(NBT_DURABILITY));
        
        if (aNBT.hasKey(NBT_INV_OUT)) mOre = ST.load(aNBT, NBT_INV_OUT);
        if (aNBT.hasKey(NBT_PROGRESS)) mProgress = aNBT.getLong(NBT_PROGRESS);
        if (aNBT.hasKey(NBT_MAXPROGRESS)) mMaxProgress = aNBT.getLong(NBT_MAXPROGRESS);
        updatePrefix();
    }
    @Override
    public void writeToNBT2(NBTTagCompound aNBT) {
        super.writeToNBT2(aNBT);
        aNBT.setByte(NBT_DESIGN, mState);
        UT.NBT.setNumber(aNBT, NBT_DURABILITY, mDurability<=0 ? -1 : mDurability); // 设为 -1 来防止没有 NBT_DURABILITY 条目，然后变成默认值
    
        ST.save(aNBT, NBT_INV_OUT, mOre);
        UT.NBT.setNumber(aNBT, NBT_PROGRESS, mProgress);
        UT.NBT.setNumber(aNBT, NBT_MAXPROGRESS, mMaxProgress);
    }
    @Override
    public final NBTTagCompound writeItemNBT(NBTTagCompound aNBT) {
        aNBT = super.writeItemNBT(aNBT);
        aNBT.setByte(NBT_DESIGN, mState);
        UT.NBT.setNumber(aNBT, NBT_DURABILITY, mDurability<=0 ? -1 : mDurability); // 设为 -1 来防止没有 NBT_DURABILITY 条目，然后变成默认值
        return aNBT;
    }
    
    
    @Override
    public void addToolTips(List<String> aList, ItemStack aStack, boolean aF3_H) {
        aList.add(LH.Chat.CYAN + "mState: " + LH.Chat.WHITE + mState);
        aList.add(LH.Chat.CYAN + "Durability: " + LH.Chat.WHITE + LH_CH.getToolTipEfficiencySimple(UT.Code.units(mDurability, mStateAttributes[mState].mMaxDurability, 10000, F)));
    }
    
    // 目前用于 debug
    @Override
    public long onToolClick(String aTool, long aRemainingDurability, long aQuality, Entity aPlayer, List<String> aChatReturn, IInventory aPlayerInventory, boolean aSneaking, ItemStack aStack, byte aSide, float aHitX, float aHitY, float aHitZ) {
        if (isClientSide()) return 0;
        if (aTool.equals(TOOL_wrench)) {
            nextState();
            return 10000;
        }
        if (aTool.equals(TOOL_monkeywrench)) {
            ItemStack tOre = dig(1, 10);
            if (tOre != null) {
                if (!(aPlayer instanceof EntityPlayer) || !UT.Inventories.addStackToPlayerInventory((EntityPlayer) aPlayer, tOre)) ST.place(getWorld(), getOffset(aSide, 1), tOre);
            }
            return 10000;
        }
        return 0;
    }
    
    public void nextState() {
        ++mState;
        if (mState > maxSate()) mState = 0; // “周期边界条件”
        mDurability = mStateAttributes[mState].mMaxDurability;
        sendClientData(F, null); // 只进行图像更新，不需要 sendAll
    }
    // 挖掘并且返回挖掘的结果，如果失败返回 null
    public ItemStack dig(int aLevel, int aEnergy) {
        // 状态判断
        if (mState >= maxSate()) {onLastState(); return null;}
        if (mDurability <= 0) nextState();
        if (mState >= maxSate()) {onLastState(); return null;}
        // 挖掘等级判断
        if (aEnergy <= 0 || aLevel < mStateAttributes[mState].mLevel) return null;
        // 增加挖掘进度
        mProgress += aEnergy;
        // 挖掘完成判断
        ItemStack tOutput = null;
        if (mProgress >= mMaxProgress) {
            tOutput = mOre;
            mProgress -= mMaxProgress;
            // 耐久度扣除
            mDurability -= mMaxProgress;
            if (mDurability <= 0) nextState();
            if (mState >= maxSate()) {onLastState(); return null;},p,p,p
            // 随机选择下次的输出
            if (mStateAttributes[mState].mTotalProb > 0) {
                int tRand = rng(mStateAttributes[mState].mTotalProb);
                int tUpper = 0;
                for (Triplet<ItemStack, Integer, Integer> tTriplet : mStateAttributes[mState].mOreList) {
                    tUpper += tTriplet.b;
                    if (tUpper > tRand) {
                        mOre = ST.copy(tTriplet.a);
                        mMaxProgress = tTriplet.c;
                        break;
                    }
                }
            }
        }
        return tOutput;
    }
    // 达到最后一个状态后的行为，默认为设为空气，可供子类重写（例如对于基岩矿则变成基岩）
    protected void onLastState() {
        if (mState < maxSate()) return;
        setToAir();
    }
    
    // 提供比较简单的放置方块的接口
    public static MultiTileEntityRegistry MTE_REGISTRY = null;
    @Override public void onRegistration(MultiTileEntityRegistry aRegistry, short aID) {MTE_REGISTRY = aRegistry;}
    public static boolean setBlock(World aWorld, int aX, int aY, int aZ, short aMetaData, byte aState, byte aDurability) {
        return MTE_REGISTRY.mBlock.placeBlock(aWorld, aX, aY, aZ, SIDE_UP, aMetaData, UT.NBT.make(NBT_DESIGN, aState, NBT_DURABILITY, aDurability), F, F);
    }
    
    // 数据同步，材质改变时及时更新
    @Override public IPacket getClientDataPacket(boolean aSendAll) {
        return aSendAll ?
            getClientDataPacketByteArray(
                aSendAll, mState,
                UT.Code.toByteL(mDurability, 0),
                UT.Code.toByteL(mDurability, 1),
                UT.Code.toByteL(mDurability, 2),
                UT.Code.toByteL(mDurability, 3),
                UT.Code.toByteL(mDurability, 4),
                UT.Code.toByteL(mDurability, 5),
                UT.Code.toByteL(mDurability, 6),
                UT.Code.toByteL(mDurability, 7)
            ) :
            getClientDataPacketByte(aSendAll, mState);
    }
    @Override public boolean receiveDataByte(byte aData, INetworkHandler aNetworkHandler) {mState = aData; updatePrefix(); return T;}
    @Override public boolean receiveDataByteArray(byte[] aData, INetworkHandler aNetworkHandler) {
        mState = aData[0]; updatePrefix();
        if (aData.length > 8) mDurability = UT.Code.combine(aData[1], aData[2], aData[3], aData[4], aData[5], aData[6], aData[7], aData[8]);
        return T;
    }
    
    // 方块属性
    @Override public boolean setBlockBounds(Block aBlock, int aRenderPass, boolean[] aShouldSideBeRendered) {return F;}
    @Override public int getRenderPasses(Block aBlock, boolean[] aShouldSideBeRendered) {return 1;}
    @Override public int getLightOpacity() {return LIGHT_OPACITY_MAX;}
    @Override public float getExplosionResistance2() {return Blocks.bedrock.getExplosionResistance(null);}
    @Override public float getBlockHardness() {return -1;}
    @Override public boolean isSurfaceSolid(byte aSide) {return T;}
    @Override public boolean isSurfaceOpaque(byte aSide) {return T;}
    @Override public boolean isSideSolid(byte aSide) {return T;}
    
    @Override public ITexture getTexture(Block aBlock, int aRenderPass, byte aSide, boolean[] aShouldSideBeRendered) {return aShouldSideBeRendered[aSide] ? BlockTextureMulti.get(BlockTextureCopied.get(Blocks.bedrock, 0), mPrefix == null ? null : BlockTextureDefault.get(mMaterial, mPrefix, mMaterial.contains(TD.Properties.GLOWING))) : null;}
    @SideOnly(Side.CLIENT) @Override public int colorMultiplier() {return UT.Code.getRGBInt(mMaterial.fRGBaSolid);}
    
    @Override public String getTileEntityName() {return "gt.multitileentity.deposit";}
}
