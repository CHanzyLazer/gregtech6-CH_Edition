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

package gregtech.tileentity.multiblocks;

import gregapi.data.FL;
import gregapi.data.LH;
import gregapi.data.TD;
import gregapi.fluid.FluidTankGT;
import gregapi.tileentity.delegate.DelegatorTileEntity;
import gregapi.tileentity.energy.ITileEntityEnergy;
import gregapi.tileentity.machines.ITileEntityAdjacentOnOff;
import gregapi.tileentity.multiblocks.TileEntityBase10MultiBlockMachine;
import gregapi.util.ST;
import gregapi.util.WD;
import gregtechCH.tileentity.cores.basicmachines.MTEC_MultiBlockCryoDistillationTower;
import gregtechCH.tileentity.cores.basicmachines.MTEC_MultiBlockDistillationTower;
import gregtechCH.tileentity.cores.basicmachines.MTEC_MultiBlockMachine;
import gregtechCH.tileentity.multiblocks.IDistillationTower;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.Fluid;

import java.util.List;

import static gregapi.data.CS.*;

/**
 * @author Gregorius Techneticies
 */
public class MultiTileEntityCryoDistillationTower extends TileEntityBase10MultiBlockMachine implements IDistillationTower {
	@Override protected MTEC_MultiBlockMachine getNewCoreMultiBlock() {return new MTEC_MultiBlockCryoDistillationTower(this);}
	protected MTEC_MultiBlockCryoDistillationTower coreM() {return (MTEC_MultiBlockCryoDistillationTower)mCoreMultiBlock;}
	
	@Override
	public void readFromNBT2(NBTTagCompound aNBT) {
		super.readFromNBT2(aNBT);
		if (aNBT.hasKey(NBT_DESIGN)) coreM().mDesign = aNBT.getShort(NBT_DESIGN);
	}
	
	@Override
	public void addToolTipsSided(List<String> aList, ItemStack aStack, boolean aF3_H) {
		String tSideNames = ""; boolean temp = F;
		if (mEnergyTypeAccepted != TD.Energy.TU) {
		for (byte tSide : ALL_SIDES_VALID) if (FACE_CONNECTED[tSide][mEnergyInputs])    {tSideNames += (temp?", ":"")+LH.get(LH.FACES[tSide]); temp = T;}
		LH.addEnergyToolTips(this, aList, mEnergyTypeAccepted, null, tSideNames, null);
		}
	}
	
	@Override
	public void updateAdjacentToggleableEnergySources() {
		int tX = getOffsetXN(mFacing) - 1, tZ = getOffsetZN(mFacing) - 1;
		for (int i = 0; i < 3; i++) for (int j = 0; j < 3; j++) {
			DelegatorTileEntity<TileEntity> tDelegator = WD.te(worldObj, tX+i, yCoord-2, tZ+j, SIDE_TOP, F);
			if (tDelegator.mTileEntity instanceof ITileEntityAdjacentOnOff && tDelegator.mTileEntity instanceof ITileEntityEnergy && ((ITileEntityEnergy)tDelegator.mTileEntity).isEnergyEmittingTo(mEnergyTypeAccepted, tDelegator.mSideOfTileEntity, T)) {
				((ITileEntityAdjacentOnOff)tDelegator.mTileEntity).setAdjacentOnOff(getStateOnOff());
			}
		}
	}
	
	@Override
	public void doOutputItems() {
		ST.moveAll(delegator(FACING_TO_SIDE[mFacing][mItemAutoOutput]), WD.te(worldObj, getOffsetXN(mFacing, 3), yCoord, getOffsetZN(mFacing, 3), mFacing, F));
	}
	
	@Override
	public void doOutputFluids() {
		for (FluidTankGT tTank : mTanksOutput) {
			Fluid tFluid = tTank.fluid();
			if (tFluid != null && tTank.has()) {
				DelegatorTileEntity<TileEntity> tDelegator = null;
				if (FL.is(tFluid, "helium")) {
					tDelegator = WD.te(worldObj, getOffsetXN(mFacing, 3), yCoord+7, getOffsetZN(mFacing, 3), mFacing, F);
				} else if (FL.is(tFluid, "neon")) {
					tDelegator = WD.te(worldObj, getOffsetXN(mFacing, 3), yCoord+6, getOffsetZN(mFacing, 3), mFacing, F);
				} else if (FL.is(tFluid, "nitrogen")) {
					tDelegator = WD.te(worldObj, getOffsetXN(mFacing, 3), yCoord+5, getOffsetZN(mFacing, 3), mFacing, F);
				} else if (FL.is(tFluid, "oxygen")) {
					tDelegator = WD.te(worldObj, getOffsetXN(mFacing, 3), yCoord+4, getOffsetZN(mFacing, 3), mFacing, F);
				} else if (FL.is(tFluid, "argon")) {
					tDelegator = WD.te(worldObj, getOffsetXN(mFacing, 3), yCoord+3, getOffsetZN(mFacing, 3), mFacing, F);
				} else if (FL.is(tFluid, "carbondioxide", "sulfurdioxide")) {
					tDelegator = WD.te(worldObj, getOffsetXN(mFacing, 3), yCoord+2, getOffsetZN(mFacing, 3), mFacing, F);
				} else {
					tDelegator = WD.te(worldObj, getOffsetXN(mFacing, 3), yCoord+1, getOffsetZN(mFacing, 3), mFacing, F);
				}
				
				if (FL.move(tTank, tDelegator) > 0) updateInventory();
			}
		}
	}
	
	@Override public String getTileEntityName() {return "gt.multitileentity.multiblock.cryodistillationtower";}
}
