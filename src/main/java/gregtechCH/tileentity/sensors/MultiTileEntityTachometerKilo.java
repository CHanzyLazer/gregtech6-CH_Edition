package gregtechCH.tileentity.sensors;

import gregapi.data.BI;
import gregapi.old.Textures;
import gregapi.render.IIconContainer;
import gregapi.tileentity.connectors.MultiTileEntityAxle;
import gregapi.tileentity.delegate.DelegatorTileEntity;
import gregapi.tileentity.machines.MultiTileEntitySensorTE;
import gregtech.tileentity.energy.transformers.MultiTileEntityGearBox;
import gregtechCH.data.LH_CH;
import net.minecraft.tileentity.TileEntity;

import static gregapi.data.CS.CA_GREEN_255;

/**
 * @author Gregorius Techneticies, CHanzy
 */
public class MultiTileEntityTachometerKilo extends MultiTileEntitySensorTE {
	static {LH_CH.add("gt.tooltip.sensor.tachometerkilo", "Measures Axle Rotations (In Kilo-RU)");}
	@Override public String getSensorDescription() {return LH_CH.get("gt.tooltip.sensor.tachometerkilo");}
	
	@Override
	public long getCurrentValue(DelegatorTileEntity<TileEntity> aDelegator) {
		if (aDelegator.mTileEntity instanceof MultiTileEntityAxle   ) return ((MultiTileEntityAxle   )aDelegator.mTileEntity).mTransferredLast/1000;
		if (aDelegator.mTileEntity instanceof MultiTileEntityGearBox) return ((MultiTileEntityGearBox)aDelegator.mTileEntity).mTransferredLast/1000;
		return 0;
	}
	
	@Override
	public long getCurrentMax(DelegatorTileEntity<TileEntity> aDelegator) {
		if (aDelegator.mTileEntity instanceof MultiTileEntityAxle   ) return (((MultiTileEntityAxle)aDelegator.mTileEntity).mPower * ((MultiTileEntityAxle)aDelegator.mTileEntity).mSpeed) / 1000;
		if (aDelegator.mTileEntity instanceof MultiTileEntityGearBox) return (((MultiTileEntityGearBox)aDelegator.mTileEntity).mMaxThroughPut * 16) / 1000;
		return 0;
	}
	
	@Override public short[] getSymbolColor() {return CA_GREEN_255;}
	@Override public IIconContainer getSymbolIcon() {return BI.CHAR_KILORU;}
	@Override public IIconContainer getTextureFront() {return sTextureFront;}
	@Override public IIconContainer getTextureBack () {return sTextureBack;}
	@Override public IIconContainer getTextureSide () {return sTextureSide;}
	@Override public IIconContainer getOverlayFront() {return sOverlayFront;}
	@Override public IIconContainer getOverlayBack () {return sOverlayBack;}
	@Override public IIconContainer getOverlaySide () {return sOverlaySide;}
	
	public static IIconContainer
	sTextureFront   = new Textures.BlockIcons.CustomIcon("machines/redstone/sensors/tachometerkilo/colored/front"),
	sTextureBack    = new Textures.BlockIcons.CustomIcon("machines/redstone/sensors/tachometerkilo/colored/back"),
	sTextureSide    = new Textures.BlockIcons.CustomIcon("machines/redstone/sensors/tachometerkilo/colored/side"),
	sOverlayFront   = new Textures.BlockIcons.CustomIcon("machines/redstone/sensors/tachometerkilo/overlay/front"),
	sOverlayBack    = new Textures.BlockIcons.CustomIcon("machines/redstone/sensors/tachometerkilo/overlay/back"),
	sOverlaySide    = new Textures.BlockIcons.CustomIcon("machines/redstone/sensors/tachometerkilo/overlay/side");
	
	@Override public String getTileEntityName() {return "gt.multitileentity.redstone.sensors.tachometerkilo";}
}
