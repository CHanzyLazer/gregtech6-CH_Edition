package gregtechCH.tileentity.multiblocks;

import gregapi.tileentity.multiblocks.TileEntityBase10MultiBlockMachine;
import gregtechCH.tileentity.cores.basicmachines.MTEC_MultiBlockMachine;
import gregtechCH.tileentity.cores.basicmachines.MTEC_MultiBlockRoasting;

/**
 * stuff from GT6U
 **/
public class MultiTileEntityRoasting extends TileEntityBase10MultiBlockMachine {
    @Override protected MTEC_MultiBlockMachine getNewCoreMultiBlock() {return new MTEC_MultiBlockRoasting(this);}
    
    @Override public String getTileEntityName() {return "gt.multitileentity.multiblock.roastingoven";}
}
