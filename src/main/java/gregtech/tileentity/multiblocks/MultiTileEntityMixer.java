/**
 * Copyright (c) 2019 Gregorius Techneticies
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

import gregapi.tileentity.delegate.DelegatorTileEntity;
import gregapi.tileentity.energy.ITileEntityEnergy;
import gregapi.tileentity.machines.ITileEntityAdjacentOnOff;
import gregapi.tileentity.multiblocks.TileEntityBase10MultiBlockMachine;
import gregapi.util.WD;
import gregtechCH.tileentity.cores.MTEC_MultiBlockMixer;
import gregtechCH.tileentity.cores.MTEC_MultiblockMachine;
import net.minecraft.tileentity.TileEntity;

import static gregapi.data.CS.*;

/**
 * @author Gregorius Techneticies
 */
public class MultiTileEntityMixer extends TileEntityBase10MultiBlockMachine {
	@Override protected MTEC_MultiblockMachine getNewCoreMultiBlock() {return new MTEC_MultiBlockMixer(this);}
	
	@Override
	public void updateAdjacentToggleableEnergySources() {
		DelegatorTileEntity<TileEntity>
		tDelegator = WD.te(worldObj, getOffsetXN(mFacing), yCoord-1, getOffsetZN(mFacing), SIDE_TOP, F);
		if (tDelegator.mTileEntity instanceof ITileEntityAdjacentOnOff && tDelegator.mTileEntity instanceof ITileEntityEnergy && ((ITileEntityEnergy)tDelegator.mTileEntity).isEnergyEmittingTo(mEnergyTypeAccepted, tDelegator.mSideOfTileEntity, T)) {
			((ITileEntityAdjacentOnOff)tDelegator.mTileEntity).setAdjacentOnOff(getStateOnOff());
		}
		tDelegator = WD.te(worldObj, getOffsetXN(mFacing), yCoord+2, getOffsetZN(mFacing), SIDE_BOTTOM, F);
		if (tDelegator.mTileEntity instanceof ITileEntityAdjacentOnOff && tDelegator.mTileEntity instanceof ITileEntityEnergy && ((ITileEntityEnergy)tDelegator.mTileEntity).isEnergyEmittingTo(mEnergyTypeAccepted, tDelegator.mSideOfTileEntity, T)) {
			((ITileEntityAdjacentOnOff)tDelegator.mTileEntity).setAdjacentOnOff(getStateOnOff());
		}
	}
	
	@Override public String getTileEntityName() {return "gt.multitileentity.multiblock.mixer";}
}
