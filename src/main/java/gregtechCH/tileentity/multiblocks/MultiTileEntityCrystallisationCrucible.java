package gregtechCH.tileentity.multiblocks;

import gregapi.tileentity.multiblocks.TileEntityBase10MultiBlockMachine;
import gregtechCH.tileentity.cores.basicmachines.MTEC_MultiBlockCrystallisationCrucible;
import gregtechCH.tileentity.cores.basicmachines.MTEC_MultiBlockMachine;

/**
 * stuff from GT6U
 **/
public class MultiTileEntityCrystallisationCrucible extends TileEntityBase10MultiBlockMachine {
    @Override protected MTEC_MultiBlockMachine getNewCoreMultiBlock() {return new MTEC_MultiBlockCrystallisationCrucible(this);}
    
    @Override public String getTileEntityName() {return "gt.multitileentity.multiblock.largecrystallisationcrucible";}
}
