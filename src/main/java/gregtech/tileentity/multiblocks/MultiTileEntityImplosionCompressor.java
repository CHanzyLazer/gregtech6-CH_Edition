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

import gregapi.data.CS.SFX;
import gregapi.tileentity.multiblocks.TileEntityBase10MultiBlockMachine;
import gregapi.util.UT;
import gregtechCH.tileentity.cores.MTEC_MultiBlockImplosionCompressor;
import gregtechCH.tileentity.cores.MTEC_MultiblockMachine;

/**
 * @author Gregorius Techneticies
 */
public class MultiTileEntityImplosionCompressor extends TileEntityBase10MultiBlockMachine {
	@Override protected MTEC_MultiblockMachine getNewCoreMultiBlock() {return new MTEC_MultiBlockImplosionCompressor(this);}
	
	@Override public void onProcessStarted() {UT.Sounds.send(SFX.MC_EXPLODE, this); super.onProcessStarted();}
	
	@Override public String getTileEntityName() {return "gt.multitileentity.multiblock.implosioncompressor";}
}
