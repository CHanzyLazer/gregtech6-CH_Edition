package gregtechCH.tileentity.sensors;

import gregapi.data.BI;
import gregapi.old.Textures;
import gregapi.render.IIconContainer;
import gregapi.tileentity.data.ITileEntityGibbl;
import gregapi.tileentity.delegate.DelegatorTileEntity;
import gregapi.tileentity.machines.MultiTileEntitySensorTE;
import gregtechCH.data.LH_CH;
import net.minecraft.tileentity.TileEntity;

import static gregapi.data.CS.CA_YELLOW_255;


public class MultiTileEntityGibblometerKilo extends MultiTileEntitySensorTE {
	static {LH_CH.add("gt.tooltip.sensor.gibblometerkilo", "Measures Compression (In Kilo-Gibbl)");}
	@Override public String getSensorDescription() {return LH_CH.get("gt.tooltip.sensor.gibblometerkilo");}
	
	@Override public long getCurrentValue(DelegatorTileEntity<TileEntity> aDelegator) {if (aDelegator.mTileEntity instanceof ITileEntityGibbl) return ((ITileEntityGibbl)aDelegator.mTileEntity).getGibblValue(aDelegator.mSideOfTileEntity) / 1000000; return 0;}
	@Override public long getCurrentMax  (DelegatorTileEntity<TileEntity> aDelegator) {if (aDelegator.mTileEntity instanceof ITileEntityGibbl) return ((ITileEntityGibbl)aDelegator.mTileEntity).getGibblMax  (aDelegator.mSideOfTileEntity) / 1000000; return 0;}
	
	@Override public short[] getSymbolColor() {return CA_YELLOW_255;}
	@Override public IIconContainer getSymbolIcon() {return BI.CHAR_KILOGIBBL;}
	@Override public IIconContainer getTextureFront() {return sTextureFront;}
	@Override public IIconContainer getTextureBack () {return sTextureBack;}
	@Override public IIconContainer getTextureSide () {return sTextureSide;}
	@Override public IIconContainer getOverlayFront() {return sOverlayFront;}
	@Override public IIconContainer getOverlayBack () {return sOverlayBack;}
	@Override public IIconContainer getOverlaySide () {return sOverlaySide;}
	
	public static IIconContainer
	sTextureFront   = new Textures.BlockIcons.CustomIcon("machines/redstone/sensors/gibblometerkilo/colored/front"),
	sTextureBack    = new Textures.BlockIcons.CustomIcon("machines/redstone/sensors/gibblometerkilo/colored/back"),
	sTextureSide    = new Textures.BlockIcons.CustomIcon("machines/redstone/sensors/gibblometerkilo/colored/side"),
	sOverlayFront   = new Textures.BlockIcons.CustomIcon("machines/redstone/sensors/gibblometerkilo/overlay/front"),
	sOverlayBack    = new Textures.BlockIcons.CustomIcon("machines/redstone/sensors/gibblometerkilo/overlay/back"),
	sOverlaySide    = new Textures.BlockIcons.CustomIcon("machines/redstone/sensors/gibblometerkilo/overlay/side");
	
	@Override public String getTileEntityName() {return "gt.multitileentity.redstone.sensors.gibblometerkilo";}
}
