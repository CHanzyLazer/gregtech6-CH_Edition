/**
 * Copyright (c) 2020 GregTech-6 Team
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

package gregapi.tileentity.multiblocks;

import static gregapi.data.CS.*;

import java.util.List;

import gregapi.data.LH;
import gregapi.data.TD;
import gregapi.tileentity.delegate.DelegatorTileEntity;
import gregapi.tileentity.machines.MultiTileEntityBasicMachine;
import gregapi.util.UT;
import gregtechCH.tileentity.cores.basicmachines.MTEC_MultiBlockMachine;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;

/**
 * @author Gregorius Techneticies
 * 
 * Some Defaults for MultiBlock Machines.
 */
public abstract class TileEntityBase10MultiBlockMachine extends MultiTileEntityBasicMachine implements IMultiBlockFluidHandler, IMultiBlockInventory, IMultiBlockEnergy {
	public boolean mStructureChanged = F;
	protected MTEC_MultiBlockMachine mCoreMultiBlock;
	
	// 用 private 封装防止意料外的修改
	private boolean mStructureOkay = F;
	@Override public final boolean isStructureOkay() {return mStructureOkay;}
	// GTCH, 用于子类重写实现在结构改变时更新不透明度
	private void setStructureOkay(boolean aStructureOkay) {
		if (aStructureOkay == mStructureOkay) return;
		int tOldOpacity = getLightOpacity();
		mStructureOkay = aStructureOkay;
		setStructureOkay2(tOldOpacity);
	}
	// 与原本的 on... 不同，一定是结构完整度发生了改变才会调用
	protected void setStructureOkay2(int aOldOpacity) {/**/}
	
	@Override
	public void readFromNBT2(NBTTagCompound aNBT) {
		super.readFromNBT2(aNBT);
		if (aNBT.hasKey(NBT_STATE+".str")) mStructureOkay = aNBT.getBoolean(NBT_STATE+".str"); // NBT 修改会有统一的更新和优化，不需要在这里再次调用
		
		// GTCH, core init
		mCoreMultiBlock = getNewCoreMultiBlock();
	}
	// 用于子类重写
	protected abstract MTEC_MultiBlockMachine getNewCoreMultiBlock();
	
	@Override
	public void writeToNBT2(NBTTagCompound aNBT) {
		super.writeToNBT2(aNBT);
		UT.NBT.setBoolean(aNBT, NBT_STATE+".str", mStructureOkay);
	}
	
	@Override public final void toolTipsMultiblock(List<String> aList) {mCoreMultiBlock.toolTipsMultiblock(aList);}
	
	@Override
	public long onToolClickMultiBlock(String aTool, long aRemainingDurability, long aQuality, Entity aPlayer, List<String> aChatReturn, IInventory aPlayerInventory, boolean aSneaking, ItemStack aStack, byte aSide, float aHitX, float aHitY, float aHitZ, ChunkCoordinates aFrom) {
		long rReturn = super.onToolClick2(aTool, aRemainingDurability, aQuality, aPlayer, aChatReturn, aPlayerInventory, aSneaking, aStack, aSide, aHitX, aHitY, aHitZ);
		if (rReturn > 0) return rReturn;
		return 0;
	}
	
	// 放大镜永远都要强制检测结构
	public void onMagnifyingGlass(List<String> aChatReturn) {
		boolean tOldStructureOkay = isStructureOkay();
		if (checkStructureOnly(T)) {
			onMagnifyingGlassSuccess(aChatReturn, tOldStructureOkay);
		} else {
			onMagnifyingGlassFail(aChatReturn, tOldStructureOkay);
		}
	}
	public void onMagnifyingGlassSuccess(List<String> aChatReturn, boolean aOldStructureOkay) {
		aChatReturn.add(aOldStructureOkay ? "Structure is formed already!" : "Structure did form just now!");
	}
	public void onMagnifyingGlassFail(List<String> aChatReturn, boolean aOldStructureOkay) {
		aChatReturn.add("Structure did not form!");
	}
	
	@Override
	public boolean onTickCheck(long aTimer) {
		if (refreshStructureOnActiveStateChange() && (mActive != oActive || mRunning != oRunning)) checkStructure(T);
		return super.onTickCheck(aTimer);
	}
	
	@Override
	public boolean checkStructure(boolean aForceReset) {
		boolean tOut = checkStructureOnly(aForceReset);
		if (isServerSide()) mStructureChanged = F;
		return tOut;
	}
	@Override
	public boolean checkStructureOnly(boolean aForceReset) {
		if (isClientSide()) return mStructureOkay;
		if ((mStructureChanged || aForceReset) && mStructureOkay != checkStructure2()) {
			setStructureOkay(!mStructureOkay);
			updateClientData();
		}
		return mStructureOkay;
	}
	
	@Override
	public void addToolTipsSided(List<String> aList, ItemStack aStack, boolean aF3_H) {
		if (mEnergyTypeAccepted != TD.Energy.TU) LH.addEnergyToolTips(this, aList, mEnergyTypeAccepted, null, null, null);
	}
	
	@Override public void onFacingChange(byte aPreviousFacing) {onStructureChange();}
	@Override public final byte getDirectionData() {return (byte)((mFacing & 7) | (mStructureOkay ? 8 : 0));}
	@Override public final void setDirectionData(byte aData) {mFacing = (byte)(aData & 7); setStructureOkay((aData & 8) != 0);}
	
	@Override public void updateAdjacentToggleableEnergySources() {/**/}
	
	@Override public boolean doDefaultStructuralChecks() {return T;}
	
	@Override public void onStructureChange() {mStructureChanged = T;}
	
	public final boolean checkStructure2() {return mCoreMultiBlock.checkStructure2();}
	public boolean refreshStructureOnActiveStateChange() {return F;}
	
	@Override public final boolean isInsideStructure(int aX, int aY, int aZ) {return mCoreMultiBlock.isInsideStructure(aX, aY, aZ);}
	@Override public final DelegatorTileEntity<IInventory> getItemInputTarget(byte aSide) {return mCoreMultiBlock.getItemInputTarget(aSide);}
	@Override public final DelegatorTileEntity<TileEntity> getItemOutputTarget(byte aSide) {return mCoreMultiBlock.getItemOutputTarget(aSide);}
	@Override public final DelegatorTileEntity<IFluidHandler> getFluidInputTarget(byte aSide) {return mCoreMultiBlock.getFluidInputTarget(aSide);}
	@Override public final DelegatorTileEntity<IFluidHandler> getFluidOutputTarget(byte aSide, Fluid aOutput) {return mCoreMultiBlock.getFluidOutputTarget(aSide, aOutput);}
	@Override public abstract String getTileEntityName();
	
	@Override protected IFluidTank getFluidTankFillable     (MultiTileEntityMultiBlockPart aPart, byte aSide, FluidStack aFluidToFill) {return getFluidTankFillable2(aSide, aFluidToFill);}
	@Override protected IFluidTank getFluidTankDrainable    (MultiTileEntityMultiBlockPart aPart, byte aSide, FluidStack aFluidToDrain) {return getFluidTankDrainable2(aSide, aFluidToDrain);}
	@Override protected IFluidTank[] getFluidTanks          (MultiTileEntityMultiBlockPart aPart, byte aSide) {return getFluidTanks2(aSide);}
	
	@Override public int[] getAccessibleSlotsFromSide       (MultiTileEntityMultiBlockPart aPart, byte aSide) {return getAccessibleSlotsFromSide2(aSide);}
	@Override public boolean canInsertItem                  (MultiTileEntityMultiBlockPart aPart, int aSlot, ItemStack aStack, byte aSide) {return canInsertItem2(aSlot, aStack, aSide);}
	@Override public boolean canExtractItem                 (MultiTileEntityMultiBlockPart aPart, int aSlot, ItemStack aStack, byte aSide) {return canExtractItem2(aSlot, aStack, aSide);}
	@Override public int getSizeInventory                   (MultiTileEntityMultiBlockPart aPart) {return getSizeInventory();}
	@Override public ItemStack getStackInSlot               (MultiTileEntityMultiBlockPart aPart, int aSlot) {return getStackInSlot(aSlot);}
	@Override public ItemStack decrStackSize                (MultiTileEntityMultiBlockPart aPart, int aSlot, int aDecrement) {return decrStackSize(aSlot, aDecrement);}
	@Override public ItemStack getStackInSlotOnClosing      (MultiTileEntityMultiBlockPart aPart, int aSlot) {return getStackInSlotOnClosing(aSlot);}
	@Override public void setInventorySlotContents          (MultiTileEntityMultiBlockPart aPart, int aSlot, ItemStack aStack) {setInventorySlotContents(aSlot, aStack);}
	@Override public String getInventoryName                (MultiTileEntityMultiBlockPart aPart) {return getInventoryName();}
	@Override public boolean hasCustomInventoryName         (MultiTileEntityMultiBlockPart aPart) {return hasCustomInventoryName();}
	@Override public int getInventoryStackLimit             (MultiTileEntityMultiBlockPart aPart) {return getInventoryStackLimit();}
	@Override public void markDirty                         (MultiTileEntityMultiBlockPart aPart) {markDirty();}
	@Override public boolean isUseableByPlayer              (MultiTileEntityMultiBlockPart aPart, EntityPlayer aPlayer) {return isUseableByPlayer(aPlayer);}
	@Override public void openInventory                     (MultiTileEntityMultiBlockPart aPart) {openInventory();}
	@Override public void closeInventory                    (MultiTileEntityMultiBlockPart aPart) {closeInventory();}
	@Override public boolean isItemValidForSlot             (MultiTileEntityMultiBlockPart aPart, int aSlot, ItemStack aStack) {return isItemValidForSlot(aSlot, aStack);}
	
	@Override public final String getTileEntityNameCompat() {return mCoreMultiBlock.getTileEntityNameCompat();}
}
