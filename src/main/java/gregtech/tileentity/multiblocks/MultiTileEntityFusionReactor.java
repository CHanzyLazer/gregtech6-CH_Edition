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

import gregapi.tileentity.energy.ITileEntityEnergy;
import gregapi.tileentity.multiblocks.TileEntityBase10MultiBlockMachine;
import gregapi.util.WD;
import gregtechCH.tileentity.cores.basicmachines.MTEC_MultiBlockFusionReactor;
import gregtechCH.tileentity.cores.basicmachines.MTEC_MultiBlockMachine;

import static gregapi.data.CS.*;

/**
 * @author Gregorius Techneticies
 */
public class MultiTileEntityFusionReactor extends TileEntityBase10MultiBlockMachine {
	@Override protected MTEC_MultiBlockMachine getNewCoreMultiBlock() {return new MTEC_MultiBlockFusionReactor(this);}
	
	@Override
	public void doOutputEnergy() {
		int tX = getOffsetXN(mFacing, 2), tY = yCoord, tZ = getOffsetZN(mFacing, 2);
		for (byte tSide : ALL_SIDES_HORIZONTAL) if (ITileEntityEnergy.Util.insertEnergyInto(mEnergyTypeEmitted, mOutputEnergy, 1, this, WD.te(worldObj, tX+OFFX[tSide]*10, tY, tZ+OFFZ[tSide]*10, OPOS[tSide], F)) > 0) return;
	}
	
	@Override public boolean refreshStructureOnActiveStateChange() {return T;}
	
	@Override public String getTileEntityName() {return "gt.multitileentity.multiblock.fusionreactor";}
}
