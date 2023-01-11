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

package gregtech.tileentity.multiblocks;

import static gregapi.data.CS.*;

import java.util.Collection;
import java.util.List;

import gregapi.block.multitileentity.IMultiTileEntity.IMTE_RemovedByPlayer;
import gregapi.code.TagData;
import gregapi.data.BI;
import gregapi.old.Textures;
import gregapi.render.BlockTextureDefault;
import gregapi.render.BlockTextureMulti;
import gregapi.render.IIconContainer;
import gregapi.render.ITexture;
import gregapi.tileentity.data.ITileEntityGibbl;
import gregapi.tileentity.energy.ITileEntityEnergy;
import gregapi.tileentity.energy.ITileEntityEnergyDataCapacitor;
import gregapi.tileentity.multiblocks.IMultiBlockEnergy;
import gregapi.tileentity.multiblocks.IMultiBlockFluidHandler;
import gregapi.tileentity.multiblocks.ITileEntityMultiBlockController;
import gregapi.tileentity.multiblocks.MultiTileEntityMultiBlockPart;
import gregapi.tileentity.multiblocks.TileEntityBase10MultiBlockBase;
import gregtechCH.tileentity.cores.*;
import gregtechCH.tileentity.cores.boilers.IMTEC_BoilerTank;
import gregtechCH.tileentity.cores.boilers.MTEC_BoilerTank_Greg;
import gregtechCH.tileentity.cores.boilers.MTEC_LargeBoilerTank;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;

/**
 * @author Gregorius Techneticies
 */
public class MultiTileEntityLargeBoiler extends TileEntityBase10MultiBlockBase implements ITileEntityEnergy, ITileEntityGibbl, ITileEntityEnergyDataCapacitor, IMultiBlockEnergy, IMultiBlockFluidHandler, IFluidHandler, IMTE_RemovedByPlayer {
	protected MTEC_BoilerTank_Greg mCore;
	
	public short mBoilerWalls = 18002;
	
	@Override
	public void readFromNBT2(NBTTagCompound aNBT) {
		super.readFromNBT2(aNBT);
		if (aNBT.hasKey(NBT_DESIGN)) mBoilerWalls = aNBT.getShort(NBT_DESIGN);
		
		// GTCH, core init
		mCore = new MTEC_LargeBoilerTank(this);
		mCore.readFromNBT(aNBT);
	}
	
	@Override
	public void writeToNBT2(NBTTagCompound aNBT) {
		super.writeToNBT2(aNBT);
		
		mCore.writeToNBT(aNBT);
	}
	
	@Override
	public boolean checkStructure2() {
		int tX = getOffsetXN(mFacing), tY = yCoord, tZ = getOffsetZN(mFacing);
		if (worldObj.blockExists(tX-1, tY, tZ-1) && worldObj.blockExists(tX+1, tY, tZ-1) && worldObj.blockExists(tX-1, tY, tZ+1) && worldObj.blockExists(tX+1, tY, tZ+1)) {
			boolean tSuccess = T;
			
			if (getAir(tX, tY+1, tZ)) worldObj.setBlockToAir(tX, tY+1, tZ); else tSuccess = F;
			
			if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, tX-1, tY-1, tZ-1, 18101, getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) tSuccess = F;
			if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, tX  , tY-1, tZ-1, 18101, getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) tSuccess = F;
			if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, tX+1, tY-1, tZ-1, 18101, getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) tSuccess = F;
			if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, tX-1, tY-1, tZ  , 18101, getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) tSuccess = F;
			if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, tX  , tY-1, tZ  , 18101, getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) tSuccess = F;
			if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, tX+1, tY-1, tZ  , 18101, getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) tSuccess = F;
			if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, tX-1, tY-1, tZ+1, 18101, getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) tSuccess = F;
			if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, tX  , tY-1, tZ+1, 18101, getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) tSuccess = F;
			if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, tX+1, tY-1, tZ+1, 18101, getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) tSuccess = F;
			
			if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, tX-1, tY  , tZ-1, mBoilerWalls, getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_FLUID_IN)) tSuccess = F;
			if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, tX  , tY  , tZ-1, mBoilerWalls, getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_FLUID_IN)) tSuccess = F;
			if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, tX+1, tY  , tZ-1, mBoilerWalls, getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_FLUID_IN)) tSuccess = F;
			if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, tX-1, tY  , tZ  , mBoilerWalls, getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_FLUID_IN)) tSuccess = F;
			if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, tX  , tY  , tZ  , mBoilerWalls, getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_FLUID_IN)) tSuccess = F;
			if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, tX+1, tY  , tZ  , mBoilerWalls, getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_FLUID_IN)) tSuccess = F;
			if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, tX-1, tY  , tZ+1, mBoilerWalls, getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_FLUID_IN)) tSuccess = F;
			if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, tX  , tY  , tZ+1, mBoilerWalls, getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_FLUID_IN)) tSuccess = F;
			if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, tX+1, tY  , tZ+1, mBoilerWalls, getMultiTileEntityRegistryID(), 0, MultiTileEntityMultiBlockPart.ONLY_FLUID_IN)) tSuccess = F;
			
			if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, tX  , tY+2, tZ  , mBoilerWalls, getMultiTileEntityRegistryID(), 1, MultiTileEntityMultiBlockPart.ONLY_FLUID_OUT)) tSuccess = F;
			for (int i = 1; i < 3; i++) {
				if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, tX-1, tY+i, tZ-1, mBoilerWalls, getMultiTileEntityRegistryID(),                0, MultiTileEntityMultiBlockPart.ONLY_FLUID_OUT)) tSuccess = F;
				if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, tX  , tY+i, tZ-1, mBoilerWalls, getMultiTileEntityRegistryID(),   i == 1 ? 1 : 0, MultiTileEntityMultiBlockPart.ONLY_FLUID_OUT)) tSuccess = F;
				if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, tX+1, tY+i, tZ-1, mBoilerWalls, getMultiTileEntityRegistryID(),                0, MultiTileEntityMultiBlockPart.ONLY_FLUID_OUT)) tSuccess = F;
				if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, tX-1, tY+i, tZ  , mBoilerWalls, getMultiTileEntityRegistryID(),   i == 1 ? 1 : 0, MultiTileEntityMultiBlockPart.ONLY_FLUID_OUT)) tSuccess = F;
				
				if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, tX+1, tY+i, tZ  , mBoilerWalls, getMultiTileEntityRegistryID(),   i == 1 ? 1 : 0, MultiTileEntityMultiBlockPart.ONLY_FLUID_OUT)) tSuccess = F;
				if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, tX-1, tY+i, tZ+1, mBoilerWalls, getMultiTileEntityRegistryID(),                0, MultiTileEntityMultiBlockPart.ONLY_FLUID_OUT)) tSuccess = F;
				if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, tX  , tY+i, tZ+1, mBoilerWalls, getMultiTileEntityRegistryID(),   i == 1 ? 1 : 0, MultiTileEntityMultiBlockPart.ONLY_FLUID_OUT)) tSuccess = F;
				if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, tX+1, tY+i, tZ+1, mBoilerWalls, getMultiTileEntityRegistryID(),                0, MultiTileEntityMultiBlockPart.ONLY_FLUID_OUT)) tSuccess = F;
			}
			
			return tSuccess;
		}
		return isStructureOkay();
	}
	@Override public final void addToolTips(List<String> aList, ItemStack aStack, boolean aF3_H) {IMTEC_ToolTips.Util.addToolTips(mCore, aList, aStack, aF3_H); super.addToolTips(aList, aStack, aF3_H);}
	
	
	@Override
	public boolean isInsideStructure(int aX, int aY, int aZ) {
		int tX = getOffsetXN(mFacing), tY = yCoord, tZ = getOffsetZN(mFacing);
		return aX >= tX - 1 && aY >= tY - 1 && aZ >= tZ - 1 && aX <= tX + 1 && aY <= tY + 2 && aZ <= tZ + 1;
	}
	
	@Override
	public void onTick3(long aTimer, boolean aIsServerSide) {if (aIsServerSide) IMTEC_BoilerTank.Util.onTick(mCore);}
	
	@Override
	public long onToolClick2(String aTool, long aRemainingDurability, long aQuality, Entity aPlayer, List<String> aChatReturn, IInventory aPlayerInventory, boolean aSneaking, ItemStack aStack, byte aSide, float aHitX, float aHitY, float aHitZ) {
		long rReturn = super.onToolClick2(aTool, aRemainingDurability, aQuality, aPlayer, aChatReturn, aPlayerInventory, aSneaking, aStack, aSide, aHitX, aHitY, aHitZ);
		if (rReturn > 0) return rReturn;
		
		if (isClientSide()) return 0;
		
		return mCore.onToolClick(aTool, aRemainingDurability, aQuality, aPlayer, aChatReturn, aPlayerInventory, aSneaking, aStack, aSide, aHitX, aHitY, aHitZ);
	}
	
	@Override
	public void onMagnifyingGlassSuccess(List<String> aChatReturn, boolean aOldStructureOkay) {
		super.onMagnifyingGlassSuccess(aChatReturn, aOldStructureOkay);
		mCore.onMagnifyingGlass(aChatReturn);
	}
	
	@Override
	public boolean removedByPlayer(World aWorld, EntityPlayer aPlayer, boolean aWillHarvest) {
		mCore.onRemovedByPlayer(aWorld, aPlayer, aWillHarvest);
		return worldObj.setBlockToAir(xCoord, yCoord, zCoord);
	}
	
	@Override
	public void onExploded(Explosion aExplosion) {
		super.onExploded(aExplosion);
		mCore.onExploded(aExplosion);
	}
	
	@Override
	public void explode(boolean aInstant) {
		mCore.explode(aInstant, 1000.0);
	}
	
	@Override
	public boolean onTickCheck(long aTimer) {
		return mCore.onTickCheck(aTimer) || super.onTickCheck(aTimer);
	}
	
	@Override
	public void onTickResetChecks(long aTimer, boolean aIsServerSide) {
		super.onTickResetChecks(aTimer, aIsServerSide);
		mCore.onTickResetChecks(aTimer, aIsServerSide);
	}
	
	@Override
	public void setVisualData(byte aData) {
		mCore.setVisualData(aData);
	}
	
	// Icons
	public static IIconContainer sColoreds[] = new IIconContainer[] {
			new Textures.BlockIcons.CustomIcon("machines/tanks/boiler_steam/colored/bottom"),
			new Textures.BlockIcons.CustomIcon("machines/tanks/boiler_steam/colored/top"),
			new Textures.BlockIcons.CustomIcon("machines/tanks/boiler_steam/colored/side")
	}, sOverlays[] = new IIconContainer[] {
			new Textures.BlockIcons.CustomIcon("machines/tanks/boiler_steam/overlay/bottom"),
			new Textures.BlockIcons.CustomIcon("machines/tanks/boiler_steam/overlay/top"),
			new Textures.BlockIcons.CustomIcon("machines/tanks/boiler_steam/overlay/side")
	};
	
	@Override
	public ITexture getTexture2(Block aBlock, int aRenderPass, byte aSide, boolean[] aShouldSideBeRendered) {
		ITexture rTexture = super.getTexture2(aBlock, aRenderPass, aSide, aShouldSideBeRendered);
		return aSide != mFacing || rTexture == null ? rTexture : BlockTextureMulti.get(rTexture, BlockTextureDefault.get(BI.BAROMETER), BlockTextureDefault.get(BI.BAROMETER_SCALE[mCore.getBarometer()], CA_RED_64));
	}
	
	@Override public byte getVisualData() {return mCore.getVisualData();}
	@Override public byte getDefaultSide() {return SIDE_FRONT;}
	@Override public boolean[] getValidSides() {return SIDES_HORIZONTAL;}
	
	@Override public boolean isEnergyType(TagData aEnergyType, byte aSide, boolean aEmitting) {return mCore.isEnergyType(aEnergyType, aSide, aEmitting);}
	@Override public boolean isEnergyAcceptingFrom(TagData aEnergyType, byte aSide, boolean aTheoretical) {return mCore.isEnergyAcceptingFrom(aEnergyType, aSide, aTheoretical);}
	@Override public boolean isEnergyCapacitorType(TagData aEnergyType, byte aSide) {return mCore.isEnergyCapacitorType(aEnergyType, aSide);}
	@Override public long doInject(TagData aEnergyType, byte aSide, long aSize, long aAmount, boolean aDoInject) {return mCore.doInject(aEnergyType, aSide, aSize, aAmount, aDoInject);}
	@Override public long getEnergyDemanded(TagData aEnergyType, byte aSide, long aSize) {return mCore.getEnergyDemanded(aEnergyType, aSide, aSize);}
	@Override public long getEnergySizeInputRecommended(TagData aEnergyType, byte aSide) {return mCore.getEnergySizeInputRecommended(aEnergyType, aSide);}
	@Override public long getEnergySizeInputMin(TagData aEnergyType, byte aSide) {return mCore.getEnergySizeInputMin(aEnergyType, aSide);}
	@Override public long getEnergySizeInputMax(TagData aEnergyType, byte aSide) {return mCore.getEnergySizeInputMax(aEnergyType, aSide);}
	@Override public long getEnergyStored(TagData aEnergyType, byte aSide) {return mCore.getEnergyStored(aEnergyType, aSide);}
	@Override public long getEnergyCapacity(TagData aEnergyType, byte aSide) {return mCore.getEnergyCapacity(aEnergyType, aSide);}
	@Override public Collection<TagData> getEnergyTypes(byte aSide) {return mCore.getEnergyTypes(aSide);}
	@Override public Collection<TagData> getEnergyCapacitorTypes(byte aSide) {return mCore.getEnergyCapacitorTypes(aSide);}
	
	@Override protected IFluidTank getFluidTankFillable(MultiTileEntityMultiBlockPart aPart, byte aSide, FluidStack aFluidToFill) {return mCore.getFluidTankFillable(aSide, aFluidToFill);}
	@Override protected IFluidTank getFluidTankDrainable(MultiTileEntityMultiBlockPart aPart, byte aSide, FluidStack aFluidToDrain) {return mCore.getFluidTankDrainable(aSide, aFluidToDrain);}
	@Override protected IFluidTank[] getFluidTanks(MultiTileEntityMultiBlockPart aPart, byte aSide) {return mCore.getFluidTanks(aSide);}
	@Override protected IFluidTank getFluidTankFillable2(byte aSide, FluidStack aFluidToFill) {return mCore.getFluidTankFillable(aSide, aFluidToFill);}
	@Override protected IFluidTank getFluidTankDrainable2(byte aSide, FluidStack aFluidToDrain) {return mCore.getFluidTankDrainable(aSide, aFluidToDrain);}
	@Override protected IFluidTank[] getFluidTanks2(byte aSide) {return mCore.getFluidTanks(aSide);}
	
	@Override public long getGibblValue(byte aSide) {return mCore.getGibblValue(aSide);}
	@Override public long getGibblMax(byte aSide) {return mCore.getGibblMax(aSide);}
	
	@Override public boolean canDrop(int aInventorySlot) {return F;}
	
	@Override public String getTileEntityName() {return "gt.multitileentity.multiblock.boiler.steam";}
}