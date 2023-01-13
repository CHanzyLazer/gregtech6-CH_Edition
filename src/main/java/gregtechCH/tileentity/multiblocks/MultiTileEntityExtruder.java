package gregtechCH.tileentity.multiblocks;

import gregapi.tileentity.multiblocks.TileEntityBase10MultiBlockMachine;
import gregtechCH.tileentity.cores.basicmachines.MTEC_MultiBlockExtruder;
import gregtechCH.tileentity.cores.basicmachines.MTEC_MultiBlockMachine;

/**
 * @author Gregorius Techneticies, YueSha, CHanzy
 * stuff from GT6U
 */
public class MultiTileEntityExtruder extends TileEntityBase10MultiBlockMachine {
    @Override protected MTEC_MultiBlockMachine getNewCoreMultiBlock() {return new MTEC_MultiBlockExtruder(this);}
    
    @Override public String getTileEntityName() {return "gt.multitileentity.multiblock.extruder";}
}
