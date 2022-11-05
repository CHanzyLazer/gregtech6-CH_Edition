package gregtechCH.tileentity.multiblocks;

import gregapi.tileentity.multiblocks.TileEntityBase10MultiBlockMachine;
import gregtechCH.tileentity.cores.basicmachines.MTEC_MultiBlockAquaticFarm;
import gregtechCH.tileentity.cores.basicmachines.MTEC_MultiBlockMachine;

/**
 * stuff from GT6U
 **/
public class MultiTileEntityAquaticFarm extends TileEntityBase10MultiBlockMachine {
    @Override protected MTEC_MultiBlockMachine getNewCoreMultiBlock() {return new MTEC_MultiBlockAquaticFarm(this);}
    
    @Override public String getTileEntityName() {return "gt.multitileentity.multiblock.aquaticfarm";}
}
