package gregtechCH.tileentity.sensors;

import gregapi.data.BI;
import gregapi.old.Textures;
import gregapi.render.IIconContainer;
import gregapi.tileentity.connectors.MultiTileEntityWireLaser;
import gregapi.tileentity.delegate.DelegatorTileEntity;
import gregapi.tileentity.machines.MultiTileEntitySensorTE;
import gregtechCH.data.LH_CH;
import net.minecraft.tileentity.TileEntity;

import static gregapi.data.CS.CA_YELLOW_255;


public class MultiTileEntityLaserometerKilo extends MultiTileEntitySensorTE {
	static {LH_CH.add("gt.tooltip.sensor.laserometerkilo", "Measures Power transmitted through Laser Fiber (In Kilo-LU)");}
	@Override public String getSensorDescription() {return LH_CH.get("gt.tooltip.sensor.laserometerkilo");}
	
	@Override
	public long getCurrentValue(DelegatorTileEntity<TileEntity> aDelegator) {
		if (aDelegator.mTileEntity instanceof MultiTileEntityWireLaser) return ((MultiTileEntityWireLaser)aDelegator.mTileEntity).mTransferredLast/1000;
		return 0;
	}
	
	@Override
	public long getCurrentMax(DelegatorTileEntity<TileEntity> aDelegator) {
		if (aDelegator.mTileEntity instanceof MultiTileEntityWireLaser) return 65535;
		return 0;
	}
	
	@Override public short[] getSymbolColor() {return CA_YELLOW_255;}
	@Override public IIconContainer getSymbolIcon() {return BI.CHAR_KILOLU;}
	@Override public IIconContainer getTextureFront() {return sTextureFront;}
	@Override public IIconContainer getTextureBack () {return sTextureBack;}
	@Override public IIconContainer getTextureSide () {return sTextureSide;}
	@Override public IIconContainer getOverlayFront() {return sOverlayFront;}
	@Override public IIconContainer getOverlayBack () {return sOverlayBack;}
	@Override public IIconContainer getOverlaySide () {return sOverlaySide;}
	
	public static IIconContainer
	sTextureFront   = new Textures.BlockIcons.CustomIcon("machines/redstone/sensors/laserometerkilo/colored/front"),
	sTextureBack    = new Textures.BlockIcons.CustomIcon("machines/redstone/sensors/laserometerkilo/colored/back"),
	sTextureSide    = new Textures.BlockIcons.CustomIcon("machines/redstone/sensors/laserometerkilo/colored/side"),
	sOverlayFront   = new Textures.BlockIcons.CustomIcon("machines/redstone/sensors/laserometerkilo/overlay/front"),
	sOverlayBack    = new Textures.BlockIcons.CustomIcon("machines/redstone/sensors/laserometerkilo/overlay/back"),
	sOverlaySide    = new Textures.BlockIcons.CustomIcon("machines/redstone/sensors/laserometerkilo/overlay/side");
	
	@Override public String getTileEntityName() {return "gt.multitileentity.redstone.sensors.laserometerkilo";}
}
