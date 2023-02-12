package gregtechCH.tileentity.misc;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregapi.block.multitileentity.MultiTileEntityRegistry;
import gregapi.data.MT;
import gregapi.data.OP;
import gregapi.data.TD;
import gregapi.network.INetworkHandler;
import gregapi.network.IPacket;
import gregapi.oredict.OreDictMaterial;
import gregapi.oredict.OreDictPrefix;
import gregapi.render.*;
import gregapi.tileentity.data.ITileEntitySurface;
import gregapi.tileentity.notick.TileEntityBase03MultiTileEntities;
import gregapi.util.UT;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
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
public class MultiTileEntityDeposit extends TileEntityBase03MultiTileEntities implements IMTE_OnRegistration, ITileEntitySurface, IMTE_IsSideSolid, IMTE_GetExplosionResistance, IMTE_GetBlockHardness, IMTE_GetLightOpacity, IMTE_SyncDataByte, IMTE_OnToolClick {
    private OreDictMaterial mMaterial = MT.Coal; // 开采会得到的矿物的材料
    private OreDictPrefix mPrefix = null; // 方块具体的表面材质种类，仅客户端有效
    protected byte mDesign = 0; // 决定矿藏的材质类型
    // 根据 mDesign 获取具体的 prefix 种类
    protected void updatePrefix() {
        switch (mDesign) {
        case 0:  {mPrefix = OP.oreBedrock; break;}
        case 1:  {mPrefix = OP.oreSmall; break;}
        default: {mPrefix = null; break;}
        }
    }
    
    @Override
    public void readFromNBT2(NBTTagCompound aNBT) {
        super.readFromNBT2(aNBT);
        if (aNBT.hasKey(NBT_MATERIAL)) mMaterial = OreDictMaterial.get(aNBT.getString(NBT_MATERIAL));
        if (aNBT.hasKey(NBT_DESIGN)) mDesign = aNBT.getByte(NBT_DESIGN);
        updatePrefix();
    }
    @Override
    public void writeToNBT2(NBTTagCompound aNBT) {
        super.writeToNBT2(aNBT);
        aNBT.setByte(NBT_DESIGN, mDesign);
    }
    @Override
    public final NBTTagCompound writeItemNBT(NBTTagCompound aNBT) {
        aNBT = super.writeItemNBT(aNBT);
        aNBT.setByte(NBT_DESIGN, mDesign);
        return aNBT;
    }
    
    // 目前用于 debug
    @Override
    public long onToolClick(String aTool, long aRemainingDurability, long aQuality, Entity aPlayer, List<String> aChatReturn, IInventory aPlayerInventory, boolean aSneaking, ItemStack aStack, byte aSide, float aHitX, float aHitY, float aHitZ) {
        if (isClientSide()) return 0;
        if (aTool.equals(TOOL_wrench)) {
            ++mDesign;
            if (mDesign > 2) mDesign = 0;
            sendClientData(F, null); // 只进行图像更新，不需要 sendAll
            return 10000;
        }
        return 0;
    }
    
    // 提供比较简单的放置方块的接口
    public static MultiTileEntityRegistry MTE_REGISTRY = null;
    @Override public void onRegistration(MultiTileEntityRegistry aRegistry, short aID) {MTE_REGISTRY = aRegistry;}
    public static boolean setBlock(World aWorld, int aX, int aY, int aZ, short aMetaData) {
        return MTE_REGISTRY.mBlock.placeBlock(aWorld, aX, aY, aZ, SIDE_UP, aMetaData, null, F, T);
    }
    
    // 数据同步，材质改变时及时更新
    @Override public IPacket getClientDataPacket(boolean aSendAll) {return getClientDataPacketByte(aSendAll, mDesign);}
    @Override public boolean receiveDataByte(byte aData, INetworkHandler aNetworkHandler) {mDesign = aData; updatePrefix(); return T;}
    
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
