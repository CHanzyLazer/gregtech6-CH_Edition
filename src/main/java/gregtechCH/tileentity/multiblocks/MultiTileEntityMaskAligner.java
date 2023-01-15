package gregtechCH.tileentity.multiblocks;

import gregapi.tileentity.delegate.DelegatorTileEntity;
import gregapi.tileentity.energy.ITileEntityEnergy;
import gregapi.tileentity.machines.ITileEntityAdjacentOnOff;
import gregapi.tileentity.multiblocks.TileEntityBase10MultiBlockMachine;
import gregapi.util.WD;
import gregtechCH.tileentity.cores.basicmachines.MTEC_MultiBlockMachine;
import gregtechCH.tileentity.cores.basicmachines.MTEC_MultiBlockMaskAligner;
import net.minecraft.tileentity.TileEntity;

import static gregapi.data.CS.*;

/**
 * @author Gregorius Techneticies, YueSha, CHanzy
 * stuff from GT6U
 */
public class MultiTileEntityMaskAligner extends TileEntityBase10MultiBlockMachine {
    @Override protected MTEC_MultiBlockMachine getNewCoreMultiBlock() {return new MTEC_MultiBlockMaskAligner(this);}
    
    @Override
    public void updateAdjacentToggleableEnergySources() {
        DelegatorTileEntity<TileEntity> tDelegator = WD.te(worldObj, getOffsetXN(mFacing), yCoord-1, getOffsetZN(mFacing), SIDE_TOP, F);
        if (tDelegator.mTileEntity instanceof ITileEntityAdjacentOnOff && tDelegator.mTileEntity instanceof ITileEntityEnergy && ((ITileEntityEnergy)tDelegator.mTileEntity).isEnergyEmittingTo(mEnergyTypeAccepted, tDelegator.mSideOfTileEntity, T)) {
            ((ITileEntityAdjacentOnOff)tDelegator.mTileEntity).setAdjacentOnOff(getStateOnOff());
        }
    }
    @Override public boolean refreshStructureOnActiveStateChange() {return T;}
    
    @Override public String getTileEntityName() {return "gt.multitileentity.multiblock.maskaligner";}
}
