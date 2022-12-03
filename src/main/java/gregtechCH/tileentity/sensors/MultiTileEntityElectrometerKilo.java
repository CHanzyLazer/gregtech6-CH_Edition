package gregtechCH.tileentity.sensors;

import gregapi.data.BI;
import gregapi.old.Textures;
import gregapi.render.IIconContainer;
import gregapi.tileentity.connectors.MultiTileEntityWireElectric;
import gregapi.tileentity.delegate.DelegatorTileEntity;
import gregapi.tileentity.energy.EnergyCompat;
import gregapi.tileentity.machines.MultiTileEntitySensorTE;
import gregtech.tileentity.sensors.MultiTileEntityElectrometer;
import gregtechCH.data.LH_CH;
import ic2.api.energy.EnergyNet;
import ic2.api.energy.NodeStats;
import ic2.api.energy.tile.*;
import net.minecraft.tileentity.TileEntity;

import static gregapi.data.CS.CA_RED_255;
import static gregapi.data.CS.V;


public class MultiTileEntityElectrometerKilo extends MultiTileEntityElectrometer {
	static {LH_CH.add("gt.tooltip.sensor.electrometerkilo", "Measures Electricity Flow (In Kilo-EU)");}
	@Override public String getSensorDescription() {return LH_CH.get("gt.tooltip.sensor.electrometerkilo");}
	
	@Override
	public long getCurrentValue(DelegatorTileEntity<TileEntity> aDelegator) {
		return super.getCurrentValue(aDelegator)/1000;
	}
	
	@Override
	public long getCurrentMax(DelegatorTileEntity<TileEntity> aDelegator) {
		return super.getCurrentMax(aDelegator)/1000;
	}
	
	@Override public short[] getSymbolColor() {return CA_RED_255;}
	@Override public IIconContainer getSymbolIcon() {return BI.CHAR_KILOEU;}
	@Override public IIconContainer getTextureFront() {return sTextureFront;}
	@Override public IIconContainer getTextureBack () {return sTextureBack;}
	@Override public IIconContainer getTextureSide () {return sTextureSide;}
	@Override public IIconContainer getOverlayFront() {return sOverlayFront;}
	@Override public IIconContainer getOverlayBack () {return sOverlayBack;}
	@Override public IIconContainer getOverlaySide () {return sOverlaySide;}
	
	public static IIconContainer
	sTextureFront   = new Textures.BlockIcons.CustomIcon("machines/redstone/sensors/electrometerkilo/colored/front"),
	sTextureBack    = new Textures.BlockIcons.CustomIcon("machines/redstone/sensors/electrometerkilo/colored/back"),
	sTextureSide    = new Textures.BlockIcons.CustomIcon("machines/redstone/sensors/electrometerkilo/colored/side"),
	sOverlayFront   = new Textures.BlockIcons.CustomIcon("machines/redstone/sensors/electrometerkilo/overlay/front"),
	sOverlayBack    = new Textures.BlockIcons.CustomIcon("machines/redstone/sensors/electrometerkilo/overlay/back"),
	sOverlaySide    = new Textures.BlockIcons.CustomIcon("machines/redstone/sensors/electrometerkilo/overlay/side");
	
	@Override public String getTileEntityName() {return "gt.multitileentity.redstone.sensors.electrometerkilo";}
}
