package gregtech.asm.transformers.replacements;

import gregapi.block.BlockBase;
import gregapi.block.multitileentity.MultiTileEntityBlock;
import gregapi.block.prefixblock.PrefixBlock;
import gregapi.cover.ITileEntityCoverable;
import gregapi.tileentity.connectors.MultiTileEntityWireRedstoneInsulated;
import gregapi.util.ST;
import gregapi.util.UT;
import gregapi.util.WD;
import gregtechCH.block.IBlockDisableNEIDamageSearch;
import gregtechCH.config.ConfigForge;
import gregtechCH.item.IItemDisableNEIDamageSearch;
import gregtechCH.tileentity.connectors.ITEInterceptModConnectFluid;
import gregtechCH.tileentity.connectors.ITEInterceptModConnectItem;
import gregtechCH.util.WD_CH;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityBlockDustFX;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Random;

import static gregapi.data.CS.*;
import static gregtech.interfaces.asm.LO_CH.*;


/**
 * @author CHanzy
 * This is a separate file, so it class loads *while* minecraft loads,
 * if we accessed world in the main transformer then we can miss out
 * on the transformations.  Not an issue when accessing MC classes
 * while transforming other mods though.
 * 用于 asm 注入的方法
 */
public class Methods {
    // 提供的实用接口
    public static void setLightOpacityData(ExtendedBlockStorage aEBS, byte[] aData) {
        initLightOpacityNA(aEBS, createLightOpacityNA(aData));
    }
    // 读取和写入 NBT
    public static void writeChunkToNbt(ExtendedBlockStorage aEBS, NBTTagCompound aNBT) {
        boolean tEmpty = true;
        NibbleArray tLONA = getLightOpacityNA(aEBS);
        if (tLONA != null) {
            // 对于全为 0 的情况进行“优化”，移除这个 NBT 标签
            long tMaxAbsValue = 0;
            for (byte tValue : tLONA.data) tMaxAbsValue = Math.max(tMaxAbsValue, Math.abs(tValue));
            if (tMaxAbsValue != 0) {
                tEmpty = false;
                aNBT.setByteArray("blockGTLightOpacity", tLONA.data);
            }
        }
        if (tEmpty) aNBT.removeTag("blockGTLightOpacity");
    }
    public static void readChunkFromNbt(ExtendedBlockStorage aEBS, NBTTagCompound aNBT) {
        if (aNBT.hasKey("blockGTLightOpacity")) {
            setLightOpacityData(aEBS, aNBT.getByteArray("blockGTLightOpacity"));
        } else {
            if (getLightOpacityNA(aEBS) != null) clearLightOpacityNA(aEBS); // 否则清空这个数据
        }
    }
    // 设置 byteArray，用于数据同步
    public static int setBlockGTLightOpacityData(ExtendedBlockStorage[] aEBSs, boolean p_149269_1_, int p_149269_2_, byte[] aData, int aOffset) {
        boolean tHasGTLO = false;
        // 简单优化，当一个区块全都为 null 时不同步此数据
        for (int tY=0; tY < aEBSs.length; ++tY) if (aEBSs[tY] != null && (!p_149269_1_ || !aEBSs[tY].isEmpty()) && (p_149269_2_ & (1<<tY)) != 0 && getLightOpacityNA(aEBSs[tY]) != null) {tHasGTLO = true; break;}
        if (tHasGTLO) for (int tY=0; tY < aEBSs.length; ++tY) if (aEBSs[tY] != null && (p_149269_2_ & (1<<tY)) != 0) {
            NibbleArray tNA = getLightOpacityNA(aEBSs[tY]);
            if (tNA == null) tNA = createLightOpacityNA(); // 目前为空时也需要传出零数组
            System.arraycopy(tNA.data, 0, aData, aOffset, tNA.data.length);
            aOffset += tNA.data.length;
        }
        return aOffset;
    }
    // 获取 byteArray, 用于数据同步
    public static int getBlockGTLightOpacityData(ExtendedBlockStorage[] aEBSs, int p_76607_2_, byte[] aData, int aOffset) {
        // 由于放置在了最后，如果数据没有了则说明整个区块都是 null，需要手动清空客户端数据
        if (aData.length < aOffset+1) {
            for (int tY=0; tY < aEBSs.length; ++tY) if ((p_76607_2_ & (1<<tY)) != 0 && aEBSs[tY] != null) {
                if (getLightOpacityNA(aEBSs[tY]) != null) clearLightOpacityNA(aEBSs[tY]);
            }
            return aOffset;
        }
        for (int tY=0; tY < aEBSs.length; ++tY) if ((p_76607_2_ & (1<<tY)) != 0 && aEBSs[tY] != null) {
            NibbleArray tNA = getLightOpacityNA(aEBSs[tY]);
            if (tNA == null) {tNA = createLightOpacityNA(); initLightOpacityNA(aEBSs[tY], tNA);} // 目前为空时需要传创建数组
            System.arraycopy(aData, aOffset, tNA.data, 0, tNA.data.length);
            aOffset += tNA.data.length;
        }
        return aOffset;
    }
    
    
    public static Random getRand(Entity aEntity) {
        if (aEntity instanceof EntityLivingBase) return ((EntityLivingBase)aEntity).getRNG();
        return RNGSUS;
    }
    // 将原本的奔跑粒子效果替换为如下逻辑，仅对 GT 方块生效
    public static void spawnSprintingParticle(Entity aEntity, World aWorld, Block aBlock, int aX, int aY, int aZ) {
        double tX = aEntity.posX + ((double)getRand(aEntity).nextFloat() - 0.5D) * (double)aEntity.width;
        double tY = aEntity.boundingBox.minY + 0.1D;
        double tZ = aEntity.posZ + ((double)getRand(aEntity).nextFloat() - 0.5D) * (double)aEntity.width;
        double tVelX = -aEntity.motionX * 4.0D;
        double tVelY = 1.5D;
        double tVelZ = -aEntity.motionZ * 4.0D;
        if (aBlock instanceof MultiTileEntityBlock || aBlock instanceof PrefixBlock) {
            // 保证和原版的调用一致
            if (WD_CH.isClientSide(aWorld)) {
                Minecraft tMC = Minecraft.getMinecraft();
                if (tMC != null && tMC.renderViewEntity != null && tMC.effectRenderer != null) {
                    int tPS = tMC.gameSettings.particleSetting;
                    if (tPS == 1 && aWorld.rand.nextInt(3) == 0) tPS = 2;
                    if (tPS > 1) return; // 由设定禁用粒子效果
                    double tDisX = Minecraft.getMinecraft().renderViewEntity.posX - tX;
                    double tDisY = Minecraft.getMinecraft().renderViewEntity.posY - tY;
                    double tDisZ = Minecraft.getMinecraft().renderViewEntity.posZ - tZ;
                    if (tDisX*tDisX + tDisY*tDisY + tDisZ*tDisZ > 16.0D*16.0D) return;
                    Minecraft.getMinecraft().effectRenderer.addEffect((new EntityDiggingFX(aWorld, tX, tY, tZ, tVelX, tVelY, tVelZ, aBlock, aWorld.getBlockMetadata(aX, aY, aZ))).applyColourMultiplier(aX, aY, aZ));
                }
            }
        } else {
            aWorld.spawnParticle("blockcrack_" + Block.getIdFromBlock(aBlock) + "_" + aWorld.getBlockMetadata(aX, aY, aZ), tX, tY, tZ, tVelX, tVelY, tVelZ);
        }
    }
    // 将原本的摔落粒子效果替换为如下逻辑，仅对 GT 方块生效
    public static void spawnFallParticle(World aWorld, Block aBlock, int aX, int aY, int aZ, double aVelX, double aVelY, double aVelZ) {
        // aVelX = d7; aVelY = d6; aVelZ = d8;
        double tX = (float)aX + 0.5F;
        double tY = (float)aY + 1.0F;
        double tZ = (float)aZ + 0.5F;
        if (aBlock instanceof MultiTileEntityBlock || aBlock instanceof PrefixBlock) {
            // 保证和原版的调用一致
            if (WD_CH.isClientSide(aWorld)) {
                Minecraft tMC = Minecraft.getMinecraft();
                if (tMC != null && tMC.renderViewEntity != null && tMC.effectRenderer != null) {
                    int tPS = tMC.gameSettings.particleSetting;
                    if (tPS == 1 && aWorld.rand.nextInt(3) == 0) tPS = 2;
                    if (tPS > 1) return; // 由设定禁用粒子效果
                    double tDisX = Minecraft.getMinecraft().renderViewEntity.posX - tX;
                    double tDisY = Minecraft.getMinecraft().renderViewEntity.posY - tY;
                    double tDisZ = Minecraft.getMinecraft().renderViewEntity.posZ - tZ;
                    if (tDisX*tDisX + tDisY*tDisY + tDisZ*tDisZ > 16.0D*16.0D) return;
                    Minecraft.getMinecraft().effectRenderer.addEffect((new EntityBlockDustFX(aWorld, tX, tY, tZ, aVelX, aVelY, aVelZ, aBlock, aWorld.getBlockMetadata(aX, aY, aZ))).applyColourMultiplier(aX, aY, aZ));
                }
            }
        } else {
            aWorld.spawnParticle("blockdust_" + Block.getIdFromBlock(aBlock) + "_" + aWorld.getBlockMetadata(aX, aY, aZ), tX, tY, tZ, aVelX, aVelY, aVelZ);
        }
    }
    
    // 使用这些方法来间接获取需要的复杂对象，因为此版本的 asm 不能使用 LDC
    public static Class<MultiTileEntityBlock> getMultiTileEntityBlock() {return MultiTileEntityBlock.class;}
    public static Class<PrefixBlock> getPrefixBlock() {return PrefixBlock.class;}
    public static Class<BlockBase> getBlockBase() {return BlockBase.class;}
    public static String getForgeVersionUrl() {return "https://files.minecraftforge.net/net/minecraftforge/forge/promotions_slim.json";}
    
    // 插入自己的判断，这里在 bc 中，side 是 tile 相对 bc 管道的方向，和 GT 的方向相反
    public static boolean interceptModConnectItem(TileEntity aTile, ForgeDirection aSide) {
        if (aTile instanceof ITEInterceptModConnectItem) return ((ITEInterceptModConnectItem)aTile).interceptModConnectItem(OPOS[UT.Code.side(aSide)]);
        return false;
    }
    public static boolean interceptModConnectFluid(TileEntity aTile, ForgeDirection aSide) {
        if (aTile instanceof ITEInterceptModConnectFluid) return ((ITEInterceptModConnectFluid)aTile).interceptModConnectFluid(OPOS[UT.Code.side(aSide)]);
        return false;
    }
    
    // 自定义的原版物品的 doesSneakBypassUse
    public static boolean MCItemDoesSneakBypassUse(Item aItem, World aWorld, int aX, int aY, int aZ, EntityPlayer aPlayer) {
        // 红石火把和红石中继器作为覆盖版
        if (ConfigForge.DATA_GTCH.sneakingMountCover && (ST.block(aItem)==Blocks.redstone_torch || ST.block(aItem)==Blocks.unlit_redstone_torch || aItem==Items.repeater)) {
            TileEntity tTileEntity = WD.te(aWorld, aX, aY, aZ, F);
            if (tTileEntity instanceof MultiTileEntityWireRedstoneInsulated) return true; // 仅红石线缆会穿透 sneak
        }
        return false;
    }
    
    // 插入自己的判断，此物品是否关闭了 nei 的 damageSearch
    public static boolean disableNEIDamageSearch(Item item) {
        if (item instanceof IItemDisableNEIDamageSearch) return ((IItemDisableNEIDamageSearch)item).disableNEIDamageSearch();
        // 添加对于纯 Block 的支持
        if (item instanceof ItemBlock && ((ItemBlock)item).field_150939_a instanceof IBlockDisableNEIDamageSearch) return ((IBlockDisableNEIDamageSearch)((ItemBlock)item).field_150939_a).disableNEIDamageSearch();
        return false;
    }
}
