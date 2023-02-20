package gregtechCH.tileentity.misc;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregapi.block.multitileentity.MultiTileEntityBlock;
import gregapi.block.multitileentity.MultiTileEntityRegistry;
import gregapi.data.LH;
import gregapi.data.MT;
import gregapi.data.TD;
import gregapi.network.INetworkHandler;
import gregapi.network.IPacket;
import gregapi.old.Textures;
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
import gregtechCH.code.Triplet;
import gregtechCH.data.LH_CH;
import gregtechCH.data.OP_CH;
import gregtechCH.util.ST_CH;
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
 * 基岩矿藏，材质不同且挖掘完成后变回基岩
 */
public class MultiTileEntityBedrockDeposit extends MultiTileEntityDeposit {
    // 达到最后一个状态后的行为，对于基岩矿则变成基岩
    @Override protected void onLastState() {
        if (mState < maxSate()) return;
        worldObj.setBlock(xCoord, yCoord, zCoord, Blocks.bedrock , 0, 3);
    }
    @Override public ITexture getTexture(Block aBlock, int aRenderPass, byte aSide, boolean[] aShouldSideBeRendered) {return aShouldSideBeRendered[aSide] ? BlockTextureMulti.get(BlockTextureCopied.get(Blocks.bedrock, 0), getTexturePrefix()) : null;}
    
    @Override public String getTileEntityName() {return "gt.multitileentity.deposit.bedrock";}
}
