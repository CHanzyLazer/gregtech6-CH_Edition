package gregtechCH.tileentity.sensors;

import gregapi.data.BI;
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
import net.minecraft.tileentity.TileEntity;

import static gregapi.data.CS.V;
import static gregtechCH.data.CS_CH.CA_BLUE_LIGHT_50;

/**
 * @author Gregorius Techneticies, CHanzy
 */
public class MultiTileEntityVoltageometer extends MultiTileEntitySensorTE {
    static {LH_CH.add("gt.tooltip.sensor.voltageometer", "Measures Voltage (In EU)");}
    @Override public String getSensorDescription() {return LH_CH.get("gt.tooltip.sensor.voltageometer");}
    
    @Override
    public long getCurrentValue(DelegatorTileEntity<TileEntity> aDelegator) {
        if (aDelegator.mTileEntity instanceof ITileEntityElectric) return ((ITileEntityElectric)aDelegator.mTileEntity).getVoltageValue(aDelegator.mSideOfTileEntity);
        
        if (EnergyCompat.IC_ENERGY && EnergyNet.instance != null) {
            TileEntity tTileEntity = EnergyNet.instance.getTileEntity(aDelegator.mWorld, aDelegator.mX, aDelegator.mY, aDelegator.mZ);
            if (tTileEntity != null) {
                NodeStats tStats = EnergyNet.instance.getNodeStats(tTileEntity);
                if (tStats != null) {
                    if (tTileEntity instanceof IEnergyConductor) return (long)tStats.getEnergyOut();
                    if (tTileEntity instanceof IEnergyEmitter  ) return (long)tStats.getEnergyOut();
                    if (tTileEntity instanceof IEnergyAcceptor ) return (long)tStats.getEnergyIn();
                }
            }
        }
        return 0;
    }
    
    @Override
    public long getCurrentMax(DelegatorTileEntity<TileEntity> aDelegator) {
        if (aDelegator.mTileEntity instanceof ITileEntityElectric) return ((ITileEntityElectric)aDelegator.mTileEntity).getVoltageMax(aDelegator.mSideOfTileEntity);
        
        if (EnergyCompat.IC_ENERGY) {
            TileEntity tTileEntity = aDelegator.mTileEntity instanceof IEnergyTile || EnergyNet.instance == null ? aDelegator.mTileEntity : EnergyNet.instance.getTileEntity(aDelegator.mWorld, aDelegator.mX, aDelegator.mY, aDelegator.mZ);
            if (tTileEntity instanceof IEnergyConductor ) return (long)((IEnergyConductor)tTileEntity).getConductorBreakdownEnergy();
            if (tTileEntity instanceof IEnergySink      ) return V[((IEnergySink)tTileEntity).getSinkTier()];
            if (tTileEntity instanceof IEnergySource    ) return V[((IEnergySource)tTileEntity).getSourceTier()];
        }
        return 0;
    }
    
    @Override public short[] getSymbolColor() {return CA_BLUE_LIGHT_50;}
    @Override public IIconContainer getSymbolIcon() {return BI.CHAR_EU;}
    @Override public IIconContainer getTextureFront() {return sTextureFront;}
    @Override public IIconContainer getTextureBack () {return sTextureBack;}
    @Override public IIconContainer getTextureSide () {return sTextureSide;}
    @Override public IIconContainer getOverlayFront() {return sOverlayFront;}
    @Override public IIconContainer getOverlayBack () {return sOverlayBack;}
    @Override public IIconContainer getOverlaySide () {return sOverlaySide;}
    
    public static IIconContainer
        sTextureFront   = new Textures.BlockIcons.CustomIcon("machines/redstone/sensors/voltageometer/colored/front"),
        sTextureBack    = new Textures.BlockIcons.CustomIcon("machines/redstone/sensors/voltageometer/colored/back"),
        sTextureSide    = new Textures.BlockIcons.CustomIcon("machines/redstone/sensors/voltageometer/colored/side"),
        sOverlayFront   = new Textures.BlockIcons.CustomIcon("machines/redstone/sensors/voltageometer/overlay/front"),
        sOverlayBack    = new Textures.BlockIcons.CustomIcon("machines/redstone/sensors/voltageometer/overlay/back"),
        sOverlaySide    = new Textures.BlockIcons.CustomIcon("machines/redstone/sensors/voltageometer/overlay/side");
    
    @Override public String getTileEntityName() {return "gt.multitileentity.redstone.sensors.voltageometer";}
}
