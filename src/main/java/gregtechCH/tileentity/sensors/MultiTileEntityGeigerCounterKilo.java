package gregtechCH.tileentity.sensors;

import gregapi.data.BI;
import gregapi.item.IItemReactorRod;
import gregapi.old.Textures;
import gregapi.render.IIconContainer;
import gregapi.tileentity.delegate.DelegatorTileEntity;
import gregapi.tileentity.machines.MultiTileEntitySensorTE;
import gregapi.util.ST;
import gregapi.util.UT;
import gregtech.tileentity.energy.reactors.MultiTileEntityReactorCore;
import gregtech.tileentity.energy.reactors.MultiTileEntityReactorCore2x2;
import gregtechCH.data.LH_CH;
import net.minecraft.tileentity.TileEntity;

import static gregapi.data.CS.DYE_Purple;


public class MultiTileEntityGeigerCounterKilo extends MultiTileEntitySensorTE {
	static {LH_CH.add("gt.tooltip.sensor.geigercounterkilo", "Measures Neutron Energy Levels (In Thousands)");}
	@Override public String getSensorDescription() {return LH_CH.get("gt.tooltip.sensor.geigercounterkilo");}
	
	@Override
	public long getCurrentValue(DelegatorTileEntity<TileEntity> aDelegator) {
		if (aDelegator.mTileEntity instanceof MultiTileEntityReactorCore) {
			return UT.Code.sum(((MultiTileEntityReactorCore)aDelegator.mTileEntity).oNeutronCounts)/1000;
		}
		return 0;
	}
	
	@Override
	public long getCurrentMax(DelegatorTileEntity<TileEntity> aDelegator) {
		if (aDelegator.mTileEntity instanceof MultiTileEntityReactorCore) {
			MultiTileEntityReactorCore TE = (MultiTileEntityReactorCore)aDelegator.mTileEntity;
			int tMaximum = 0;
			tMaximum += TE.slotHas(0) && ST.item(TE.slot(0)) instanceof IItemReactorRod ? ((IItemReactorRod) ST.item(TE.slot(0))).getReactorRodNeutronMaximum(TE, 0, TE.slot(0)) : 0;
			if (TE instanceof MultiTileEntityReactorCore2x2) {
				tMaximum += TE.slotHas(1) && ST.item(TE.slot(1)) instanceof IItemReactorRod ? ((IItemReactorRod) ST.item(TE.slot(1))).getReactorRodNeutronMaximum(TE, 1, TE.slot(1)) : 0;
				tMaximum += TE.slotHas(2) && ST.item(TE.slot(2)) instanceof IItemReactorRod ? ((IItemReactorRod) ST.item(TE.slot(2))).getReactorRodNeutronMaximum(TE, 2, TE.slot(2)) : 0;
				tMaximum += TE.slotHas(3) && ST.item(TE.slot(3)) instanceof IItemReactorRod ? ((IItemReactorRod) ST.item(TE.slot(3))).getReactorRodNeutronMaximum(TE, 3, TE.slot(3)) : 0;
			}
			return tMaximum/1000;
		}
		return 0;
	}
	
	@Override public short[] getSymbolColor() {return DYE_Purple;}
	@Override public IIconContainer getSymbolIcon() {return BI.CHAR_KILONEUTRON;}
	@Override public IIconContainer getTextureFront() {return sTextureFront;}
	@Override public IIconContainer getTextureBack () {return sTextureBack;}
	@Override public IIconContainer getTextureSide () {return sTextureSide;}
	@Override public IIconContainer getOverlayFront() {return sOverlayFront;}
	@Override public IIconContainer getOverlayBack () {return sOverlayBack;}
	@Override public IIconContainer getOverlaySide () {return sOverlaySide;}
	
	public static IIconContainer
	sTextureFront   = new Textures.BlockIcons.CustomIcon("machines/redstone/sensors/geigercounterkilo/colored/front"),
	sTextureBack    = new Textures.BlockIcons.CustomIcon("machines/redstone/sensors/geigercounterkilo/colored/back"),
	sTextureSide    = new Textures.BlockIcons.CustomIcon("machines/redstone/sensors/geigercounterkilo/colored/side"),
	sOverlayFront   = new Textures.BlockIcons.CustomIcon("machines/redstone/sensors/geigercounterkilo/overlay/front"),
	sOverlayBack    = new Textures.BlockIcons.CustomIcon("machines/redstone/sensors/geigercounterkilo/overlay/back"),
	sOverlaySide    = new Textures.BlockIcons.CustomIcon("machines/redstone/sensors/geigercounterkilo/overlay/side");
	
	@Override public String getTileEntityName() {return "gt.multitileentity.redstone.sensors.geigercounterkilo";}
}
