package gregtechCH.tileentity.multiblocks;

import gregapi.tileentity.multiblocks.TileEntityBase10MultiBlockMachine;
import gregtechCH.tileentity.cores.basicmachines.MTEC_MultiBlockMachine;
import gregtechCH.tileentity.cores.basicmachines.MTEC_MultiBlockOilCleaner;

/**
 * stuff from GT6U
 **/
public class MultiTileEntityOilCleaner extends TileEntityBase10MultiBlockMachine {
    @Override protected MTEC_MultiBlockMachine getNewCoreMultiBlock() {return new MTEC_MultiBlockOilCleaner(this);}
    
    @Override public String getTileEntityName() {return "gt.multitileentity.multiblock.oilcleaner";}
}
