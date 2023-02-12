package gregtechCH.tileentity.misc;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
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
import gregtechCH.data.LH_CH;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.List;

import static gregapi.block.multitileentity.IMultiTileEntity.*;
import static gregapi.data.CS.*;

/**
 * @author CHanzy
 * 新名称：矿藏，可以使用对应的钻头进行开采
 * 随着开采会不断增加开采难度，最后消失或者变成基岩
 */
public class MultiTileEntityDeposit extends TileEntityBase03MultiTileEntities implements IMTE_OnRegistration, ITileEntitySurface, IMTE_IsSideSolid, IMTE_GetExplosionResistance, IMTE_GetBlockHardness, IMTE_GetLightOpacity, IMTE_SyncDataByte, IMTE_SyncDataByteArray, IMTE_OnToolClick, IMTE_AddToolTips {
    // TODO 提供一个方法比较方便的注册矿藏，并且顺便注册假的合成表
    
    protected OreDictMaterial mMaterial = MT.Coal; // 用于显示的材料类型
    protected OreDictPrefix mPrefix = null; // 方块具体的表面材质种类，仅客户端有效
    protected int mType = 0; // 矿藏的石头种类
    protected byte mState = 0; // 决定矿藏的状态
    protected long mDurability = 0L;
    protected long[] mDurabilityList = ZL_LONG; // 记录某个 mState 下的耐久度
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
        mDurabilityList = new long[maxSate()+1];
        for (int i = 0; i < maxSate(); ++i) mDurabilityList[i] = aNBT.getLong(NBT_MAXDURABILITY+"."+i);
        mDurabilityList[mDurabilityList.length-1] = 0;
        
        if (aNBT.hasKey(NBT_MATERIAL)) mMaterial = OreDictMaterial.get(aNBT.getString(NBT_MATERIAL));
        if (aNBT.hasKey(NBT_DESIGN)) mState = aNBT.getByte(NBT_DESIGN);
        if (aNBT.hasKey(NBT_DURABILITY)) mDurability = UT.Code.bind(0, mDurabilityList[mState], aNBT.getLong(NBT_DURABILITY));
        updatePrefix();
    }
    @Override
    public void writeToNBT2(NBTTagCompound aNBT) {
        super.writeToNBT2(aNBT);
        aNBT.setByte(NBT_DESIGN, mState);
        UT.NBT.setNumber(aNBT, NBT_DURABILITY, mDurability<=0 ? -1 : mDurability); // 设为 -1 来防止没有 NBT_DURABILITY 条目，然后变成默认值
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
        aList.add(LH.Chat.CYAN + "Durability: " + LH.Chat.WHITE + LH_CH.getToolTipEfficiencySimple(UT.Code.units(mDurability, mDurabilityList[mState], 10000, F)));
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
            ItemStack tOre = dig(1);
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
        mDurability = mDurabilityList[mState];
        sendClientData(F, null); // 只进行图像更新，不需要 sendAll
    }
    // 挖掘并且返回挖掘的结果，如果失败返回 null
    public ItemStack dig(int aAbility) {
        if (aAbility < 0) return null;
        if (mState >= maxSate()) return null;
        if (mDurability <= 0) nextState();
        if (mState >= maxSate()) return null;
        // 各种结果的概率权重
        int tOre = 10, tStone = 89, tBedrock = 1;
        int tTotal = tOre + tStone + tBedrock;
        int tRand = rng(tTotal);
        if (tRand < tOre) {
            mDurability -= 100;
            if (mDurability <= 0) nextState();
            // 直接创建对应原矿
            return OP.oreRaw.mat(mMaterial, 1);
        } else
        if (tRand < tOre+tStone) {
            mDurability -= 10;
            if (mDurability <= 0) nextState();
            if (worldObj.provider.dimensionId == DIM_NETHER) {
                // Netherrack.
                return ST.make(Blocks.netherrack, 1, 0);
            } else if (WD.dimERE(worldObj)) {
                // Erebus Umberstone.
                return IL.ERE_Umbercobble.get(1);
            } else if (WD.dimATUM(worldObj)) {
                // Atum Limestone.
                return IL.ATUM_Limecobble.get(1);
            } else if (WD.dimBTL(worldObj)) {
                // Betweenlands Stones.
                return (tRand%2==0?IL.BTL_Pitstone:IL.BTL_Betweenstone).get(1);
            } else {
                // This might be the Overworld or some Overworld alike Dimension.
                return ST.make(BlocksGT.stones[mType], 1, 1);
            }
        } else {
            mDurability -= 10;
            if (mDurability <= 0) nextState();
            return OP.dust.mat(MT.Bedrock, 1);
        }
    }
    
    // 提供比较简单的放置方块的接口
    public static MultiTileEntityRegistry MTE_REGISTRY = null;
    @Override public void onRegistration(MultiTileEntityRegistry aRegistry, short aID) {MTE_REGISTRY = aRegistry;}
    public static boolean setBlock(World aWorld, int aX, int aY, int aZ, short aMetaData, byte aState, byte aDurability) {
        return MTE_REGISTRY.mBlock.placeBlock(aWorld, aX, aY, aZ, SIDE_UP, aMetaData, UT.NBT.make(NBT_DESIGN, aState, NBT_DURABILITY, aDurability), F, T);
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
