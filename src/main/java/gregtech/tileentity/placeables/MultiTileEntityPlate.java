/**
 * Copyright (c) 2021 GregTech-6 Team
 *
 * This file is part of GregTech.
 *
 * GregTech is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GregTech is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GregTech. If not, see <http://www.gnu.org/licenses/>.
 */

package gregtech.tileentity.placeables;

import gregapi.data.OP;
import gregapi.data.TD;
import gregapi.old.Textures;
import gregapi.render.BlockTextureDefault;
import gregapi.render.IIconContainer;
import gregapi.tileentity.misc.MultiTileEntityPlaceable;
import gregapi.util.UT;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import static gregapi.block.multitileentity.IMultiTileEntity.IMTE_CanPlace;
import static gregapi.data.CS.*;

/**
 * @author Gregorius Techneticies
 */
public class MultiTileEntityPlate extends MultiTileEntityPlaceable implements IMTE_CanPlace {
	public static IIconContainer
	sTextureSides       = new Textures.BlockIcons.CustomIcon("machines/placeables/plate/sides"),
	sTextureTop         = new Textures.BlockIcons.CustomIcon("machines/placeables/plate/top");
	
	@Override
	public int getRenderPasses(Block aBlock, boolean[] aShouldSideBeRendered) {
		mTextureSides = BlockTextureDefault.get(sTextureSides, mMaterial.fRGBaSolid, F, mMaterial.contains(TD.Properties.GLOWING), F, F);
		mTextureTop   = BlockTextureDefault.get(sTextureTop  , mMaterial.fRGBaSolid, F, mMaterial.contains(TD.Properties.GLOWING), F, T);
		return (int)UT.Code.bind(mSize, 0, 4);
	}
	
	@Override
	public boolean setBlockBounds(Block aBlock, int aRenderPass, boolean[] aShouldSideBeRendered) {
		switch (aRenderPass) {
		case  0: return box(aBlock, 0.03125, PX_P[ 0], 0.03125, 0.46875, PX_P[mSize / 4 + (mSize % 4 > 0 ? 1 : 0)], 0.46875);
		case  1: return box(aBlock, 0.03125, PX_P[ 0], 0.53125, 0.46875, PX_P[mSize / 4 + (mSize % 4 > 1 ? 1 : 0)], 0.96875);
		case  2: return box(aBlock, 0.53125, PX_P[ 0], 0.03125, 0.96875, PX_P[mSize / 4 + (mSize % 4 > 2 ? 1 : 0)], 0.46875);
		case  3: return box(aBlock, 0.53125, PX_P[ 0], 0.53125, 0.96875, PX_P[mSize / 4                          ], 0.96875);
		}
		return T;
	}
	
	@Override public void setBlockBoundsBasedOnState(Block aBlock) {box(aBlock, 0, 0, 0, 1, UT.Code.divup(mSize, 4) / 16.0F, 1);}
	@Override public AxisAlignedBB getSelectedBoundingBoxFromPool () {return box(0, 0, 0, 1, UT.Code.divup(mSize, 4) / 16.0F, 1);}
	@Override public AxisAlignedBB getCollisionBoundingBoxFromPool() {return mSize < 4 ? null : box(0, 0, 0, 1, (mSize / 4) / 16.0F, 1);}
	
	@Override public ItemStack getPickBlock(MovingObjectPosition aTarget) {return OP.plate.mat(mMaterial, 1);}
	@Override public ItemStack getStackFromBlock(byte aSide) {return OP.plate.mat(mMaterial, 1);}
	
	@Override public String getTileEntityName() {return "gt.multitileentity.plate";}
	
	// GTCH, 用来在作为覆盖板时禁用放置，现已经在 greg 的放置方法中更加灵活的禁用了放置，所以不需要这个方法了
	@Override public boolean canPlace(ItemStack aStack, EntityPlayer aPlayer, World aWorld, int aX, int aY, int aZ, byte aSide, float aHitX, float aHitY, float aHitZ) {
//		if (aPlayer == null) return T;
//		if (DATA_GTCH.sneakingMountCover && aPlayer.isSneaking()) {
//			TileEntity tTileEntity = WD.te(aWorld, aX-OFFX[aSide], aY-OFFY[aSide], aZ-OFFZ[aSide], F);
//			if ((tTileEntity instanceof ITileEntityCoverable) && ((ITileEntityCoverable) tTileEntity).getCoverItem(aSide) == null) return F;
//		}
		return T;
	}
}
