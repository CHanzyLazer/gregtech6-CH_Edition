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

import gregapi.damage.DamageSources;
import gregapi.tileentity.delegate.DelegatorTileEntity;
import gregapi.tileentity.energy.ITileEntityEnergy;
import gregapi.tileentity.machines.ITileEntityAdjacentOnOff;
import gregapi.tileentity.multiblocks.TileEntityBase10MultiBlockMachine;
import gregapi.util.WD;
import gregtechCH.tileentity.cores.basicmachines.MTEC_MultiBlockShredder;
import gregtechCH.tileentity.cores.basicmachines.MTEC_MultiBlockMachine;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;

import static gregapi.data.CS.*;

/**
 * @author Gregorius Techneticies
 */
public class MultiTileEntityShredder extends TileEntityBase10MultiBlockMachine {
	@Override protected MTEC_MultiBlockMachine getNewCoreMultiBlock() {return new MTEC_MultiBlockShredder(this);}
	
	@Override
	public void updateAdjacentToggleableEnergySources() {
		DelegatorTileEntity<TileEntity> tDelegator;
		if (SIDES_AXIS_X[mFacing]) {
			tDelegator = WD.te(worldObj, getOffsetXN(mFacing, 2), yCoord+1, zCoord - 3, SIDE_Z_POS, F);
			if (tDelegator.mTileEntity instanceof ITileEntityAdjacentOnOff && tDelegator.mTileEntity instanceof ITileEntityEnergy && ((ITileEntityEnergy)tDelegator.mTileEntity).isEnergyEmittingTo(mEnergyTypeAccepted, tDelegator.mSideOfTileEntity, T)) ((ITileEntityAdjacentOnOff)tDelegator.mTileEntity).setAdjacentOnOff(getStateOnOff());
			tDelegator = WD.te(worldObj, getOffsetXN(mFacing, 2), yCoord+1, zCoord + 3, SIDE_Z_NEG, F);
			if (tDelegator.mTileEntity instanceof ITileEntityAdjacentOnOff && tDelegator.mTileEntity instanceof ITileEntityEnergy && ((ITileEntityEnergy)tDelegator.mTileEntity).isEnergyEmittingTo(mEnergyTypeAccepted, tDelegator.mSideOfTileEntity, T)) ((ITileEntityAdjacentOnOff)tDelegator.mTileEntity).setAdjacentOnOff(getStateOnOff());
		} else {
			tDelegator = WD.te(worldObj, xCoord - 3, yCoord+1, getOffsetZN(mFacing, 2), SIDE_X_POS, F);
			if (tDelegator.mTileEntity instanceof ITileEntityAdjacentOnOff && tDelegator.mTileEntity instanceof ITileEntityEnergy && ((ITileEntityEnergy)tDelegator.mTileEntity).isEnergyEmittingTo(mEnergyTypeAccepted, tDelegator.mSideOfTileEntity, T)) ((ITileEntityAdjacentOnOff)tDelegator.mTileEntity).setAdjacentOnOff(getStateOnOff());
			tDelegator = WD.te(worldObj, xCoord + 3, yCoord+1, getOffsetZN(mFacing, 2), SIDE_X_NEG, F);
			if (tDelegator.mTileEntity instanceof ITileEntityAdjacentOnOff && tDelegator.mTileEntity instanceof ITileEntityEnergy && ((ITileEntityEnergy)tDelegator.mTileEntity).isEnergyEmittingTo(mEnergyTypeAccepted, tDelegator.mSideOfTileEntity, T)) ((ITileEntityAdjacentOnOff)tDelegator.mTileEntity).setAdjacentOnOff(getStateOnOff());
		}
	}
	
	@Override
	public void onWalkOver2(EntityLivingBase aEntity) {
		super.onWalkOver2(aEntity);
		if (mRunning && aEntity.posX >= getOffsetXN(mFacing, 2) - 1 && aEntity.posZ >= getOffsetZN(mFacing, 2) - 1 && aEntity.posX <= getOffsetXN(mFacing, 2) + 2 && aEntity.posZ <= getOffsetZN(mFacing, 2) + 2) {
			aEntity.attackEntityFrom(DamageSources.getShredderDamage(), 5.0F);
		}
	}
	
	@Override public boolean refreshStructureOnActiveStateChange() {return T;}
	
	@Override public String getTileEntityName() {return "gt.multitileentity.multiblock.shredder";}
}
