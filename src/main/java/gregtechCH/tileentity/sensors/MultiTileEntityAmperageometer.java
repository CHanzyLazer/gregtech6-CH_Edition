package gregtechCH.tileentity.sensors;

import gregapi.data.BI;
import gregapi.data.LH;
import gregapi.old.Textures;
import gregapi.render.IIconContainer;
import gregapi.tileentity.delegate.DelegatorTileEntity;
import gregapi.tileentity.energy.EnergyCompat;
import gregapi.tileentity.machines.MultiTileEntitySensorTE;
import gregtechCH.data.LH_CH;
import gregtechCH.tileentity.data.ITileEntityElectric;
import ic2.api.energy.EnergyNet;
import ic2.api.energy.NodeStats;
import ic2.api.energy.tile.*;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import java.util.List;

import static gregapi.data.CS.CA_BLUE_255;
import static gregapi.data.CS.V;

/**
 * @author Gregorius Techneticies, CHanzy
 */
public class MultiTileEntityAmperageometer extends MultiTileEntitySensorTE {
    static {LH_CH.add("gt.tooltip.sensor.amperageometer", "Measures Amperage (Package number)");}
    @Override public String getSensorDescription() {return LH_CH.get("gt.tooltip.sensor.amperageometer");}
    
    @Override
    public long getCurrentValue(DelegatorTileEntity<TileEntity> aDelegator) {
        if (aDelegator.mTileEntity instanceof ITileEntityElectric) return ((ITileEntityElectric)aDelegator.mTileEntity).getAmperageValue(aDelegator.mSideOfTileEntity);
        // ic2 的电流永远是 1
        if (EnergyCompat.IC_ENERGY && EnergyNet.instance != null) {
            TileEntity tTileEntity = EnergyNet.instance.getTileEntity(aDelegator.mWorld, aDelegator.mX, aDelegator.mY, aDelegator.mZ);
            if (tTileEntity != null) {
                NodeStats tStats = EnergyNet.instance.getNodeStats(tTileEntity);
                if (tStats != null) {
                    if (tTileEntity instanceof IEnergyConductor) return tStats.getEnergyOut()>0.0 ? 1 : 0;
                    if (tTileEntity instanceof IEnergyEmitter  ) return tStats.getEnergyOut()>0.0 ? 1 : 0;
                    if (tTileEntity instanceof IEnergyAcceptor ) return tStats.getEnergyIn() >0.0 ? 1 : 0;
                }
            }
        }
        return 0;
    }
    
    @Override
    public long getCurrentMax(DelegatorTileEntity<TileEntity> aDelegator) {
        if (aDelegator.mTileEntity instanceof ITileEntityElectric) return ((ITileEntityElectric)aDelegator.mTileEntity).getAmperageMax(aDelegator.mSideOfTileEntity);
        // ic2 的电流永远是 1
        if (EnergyCompat.IC_ENERGY) {
            TileEntity tTileEntity = aDelegator.mTileEntity instanceof IEnergyTile || EnergyNet.instance == null ? aDelegator.mTileEntity : EnergyNet.instance.getTileEntity(aDelegator.mWorld, aDelegator.mX, aDelegator.mY, aDelegator.mZ);
            if (tTileEntity instanceof IEnergyConductor ) return 1;
            if (tTileEntity instanceof IEnergySink      ) return 1;
            if (tTileEntity instanceof IEnergySource    ) return 1;
        }
        return 0;
    }
    
    @Override public short[] getSymbolColor() {return CA_BLUE_255;}
    @Override public IIconContainer getSymbolIcon() {return BI.CHAR_A;}
    @Override public IIconContainer getTextureFront() {return sTextureFront;}
    @Override public IIconContainer getTextureBack () {return sTextureBack;}
    @Override public IIconContainer getTextureSide () {return sTextureSide;}
    @Override public IIconContainer getOverlayFront() {return sOverlayFront;}
    @Override public IIconContainer getOverlayBack () {return sOverlayBack;}
    @Override public IIconContainer getOverlaySide () {return sOverlaySide;}
    
    // TODO 专属材质
    public static IIconContainer
        sTextureFront   = new Textures.BlockIcons.CustomIcon("machines/redstone/sensors/electrometer/colored/front"),
        sTextureBack    = new Textures.BlockIcons.CustomIcon("machines/redstone/sensors/electrometer/colored/back"),
        sTextureSide    = new Textures.BlockIcons.CustomIcon("machines/redstone/sensors/electrometer/colored/side"),
        sOverlayFront   = new Textures.BlockIcons.CustomIcon("machines/redstone/sensors/electrometer/overlay/front"),
        sOverlayBack    = new Textures.BlockIcons.CustomIcon("machines/redstone/sensors/electrometer/overlay/back"),
        sOverlaySide    = new Textures.BlockIcons.CustomIcon("machines/redstone/sensors/electrometer/overlay/side");
    
    @Override public String getTileEntityName() {return "gt.multitileentity.redstone.sensors.amperageometer";}
}
