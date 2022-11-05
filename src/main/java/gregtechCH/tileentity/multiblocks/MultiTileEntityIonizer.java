package gregtechCH.tileentity.multiblocks;

import gregapi.tileentity.multiblocks.TileEntityBase10MultiBlockMachine;
import gregtechCH.tileentity.cores.basicmachines.MTEC_MultiBlockIonizer;
import gregtechCH.tileentity.cores.basicmachines.MTEC_MultiBlockMachine;

/**
 * stuff from GT6U
 */
public class MultiTileEntityIonizer extends TileEntityBase10MultiBlockMachine {
    @Override protected MTEC_MultiBlockMachine getNewCoreMultiBlock() {return new MTEC_MultiBlockIonizer(this);}
    
    @Override public String getTileEntityName() {return "gt.multitileentity.multiblock.ionizer";}
}
