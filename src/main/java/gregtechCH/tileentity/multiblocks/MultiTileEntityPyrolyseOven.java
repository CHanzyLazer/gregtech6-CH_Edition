package gregtechCH.tileentity.multiblocks;

import gregapi.tileentity.multiblocks.TileEntityBase10MultiBlockMachine;
import gregtechCH.tileentity.cores.basicmachines.MTEC_MultiBlockMachine;
import gregtechCH.tileentity.cores.basicmachines.MTEC_MultiBlockPyrolyseOven;

/**
 * stuff from GT6U
 **/
public class MultiTileEntityPyrolyseOven extends TileEntityBase10MultiBlockMachine {
    @Override protected MTEC_MultiBlockMachine getNewCoreMultiBlock() {return new MTEC_MultiBlockPyrolyseOven(this);}
    
    @Override public String getTileEntityName() {return "gt.multitileentity.multiblock.pyrolyseoven";}
}
