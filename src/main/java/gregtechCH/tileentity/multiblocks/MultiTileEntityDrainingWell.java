package gregtechCH.tileentity.multiblocks;

import gregapi.tileentity.delegate.DelegatorTileEntity;
import gregapi.tileentity.energy.ITileEntityEnergy;
import gregapi.tileentity.machines.ITileEntityAdjacentOnOff;
import gregapi.tileentity.multiblocks.TileEntityBase10MultiBlockMachine;
import gregapi.util.WD;
import gregtechCH.tileentity.cores.basicmachines.MTEC_MultiBlockDrainingWell;
import gregtechCH.tileentity.cores.basicmachines.MTEC_MultiBlockMachine;
import net.minecraft.tileentity.TileEntity;

import static gregapi.data.CS.*;

/**
 * stuff from GT6U
 **/
public class MultiTileEntityDrainingWell extends TileEntityBase10MultiBlockMachine {
    @Override protected MTEC_MultiBlockMachine getNewCoreMultiBlock() {return new MTEC_MultiBlockDrainingWell(this);}
    
    
    @Override
    public void updateAdjacentToggleableEnergySources() {
        DelegatorTileEntity<TileEntity>
            tDelegator = WD.te(worldObj, getOffsetXN(mFacing), yCoord+3, getOffsetZN(mFacing), SIDE_TOP, F);
        if (tDelegator.mTileEntity instanceof ITileEntityAdjacentOnOff && tDelegator.mTileEntity instanceof ITileEntityEnergy && ((ITileEntityEnergy)tDelegator.mTileEntity).isEnergyEmittingTo(mEnergyTypeAccepted, tDelegator.mSideOfTileEntity, T)) {
            ((ITileEntityAdjacentOnOff)tDelegator.mTileEntity).setAdjacentOnOff(getStateOnOff());
        }
    }
    
    @Override public boolean refreshStructureOnActiveStateChange() {return T;}
    
    @Override public String getTileEntityName() {return "gt.multitileentity.multiblock.drainingwell";}
}
