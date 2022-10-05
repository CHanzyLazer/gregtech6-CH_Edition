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

package gregtechCH.tileentity.sensors;

import gregapi.data.BI;
import gregapi.old.Textures;
import gregapi.render.IIconContainer;
import gregapi.tileentity.delegate.DelegatorTileEntity;
import gregapi.tileentity.machines.MultiTileEntitySensorTE;
import gregtechCH.data.LH_CH;
import gregtechCH.tileentity.data.ITileEntityFlowrate;
import net.minecraft.tileentity.TileEntity;

import static gregapi.data.CS.CA_CYAN_255;

/* 目前仅能检测 GT 流体管道的流速 */
public class MultiTileEntityFlowometerBucket extends MultiTileEntitySensorTE {
	static {LH_CH.add("gt.tooltip.sensor.flowometerbucket", "Measures Fluid Flow Rate (In Cubic Meters per Tick)");}
	@Override public String getSensorDescription() {return LH_CH.get("gt.tooltip.sensor.flowometerbucket");}
	
	@Override
	public long getCurrentValue(DelegatorTileEntity<TileEntity> aDelegator) {
		if (aDelegator.mTileEntity instanceof ITileEntityFlowrate) return ((ITileEntityFlowrate)aDelegator.mTileEntity).getFlowrateValue(aDelegator.mSideOfTileEntity)/1000;
		return 0;
	}
	
	@Override
	public long getCurrentMax(DelegatorTileEntity<TileEntity> aDelegator) {
		if (aDelegator.mTileEntity instanceof ITileEntityFlowrate) return ((ITileEntityFlowrate)aDelegator.mTileEntity).getFlowrateMax(aDelegator.mSideOfTileEntity)/1000;
		return 0;
	}
	
	@Override public short[] getSymbolColor() {return CA_CYAN_255;}
	@Override public IIconContainer getSymbolIcon() {return BI.CHAR_BPT;}
	@Override public IIconContainer getTextureFront() {return sTextureFront;}
	@Override public IIconContainer getTextureBack () {return sTextureBack;}
	@Override public IIconContainer getTextureSide () {return sTextureSide;}
	@Override public IIconContainer getOverlayFront() {return sOverlayFront;}
	@Override public IIconContainer getOverlayBack () {return sOverlayBack;}
	@Override public IIconContainer getOverlaySide () {return sOverlaySide;}
	
	public static IIconContainer
	sTextureFront   = new Textures.BlockIcons.CustomIcon("machines/redstone/sensors/flowometerbucket/colored/front"),
	sTextureBack    = new Textures.BlockIcons.CustomIcon("machines/redstone/sensors/flowometerbucket/colored/back"),
	sTextureSide    = new Textures.BlockIcons.CustomIcon("machines/redstone/sensors/flowometerbucket/colored/side"),
	sOverlayFront   = new Textures.BlockIcons.CustomIcon("machines/redstone/sensors/flowometerbucket/overlay/front"),
	sOverlayBack    = new Textures.BlockIcons.CustomIcon("machines/redstone/sensors/flowometerbucket/overlay/back"),
	sOverlaySide    = new Textures.BlockIcons.CustomIcon("machines/redstone/sensors/flowometerbucket/overlay/side");
	
	@Override public String getTileEntityName() {return "gt.multitileentity.redstone.sensors.flowmeterbucket";}
}
