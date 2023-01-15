package gregtechCH.tileentity.multiblocks;

import gregapi.tileentity.multiblocks.TileEntityBase10MultiBlockMachine;
import gregtechCH.tileentity.cores.basicmachines.MTEC_MultiBlockMachine;
import gregtechCH.tileentity.cores.basicmachines.MTEC_MultiBlockMatterReplicator;

/**
 * @author Gregorius Techneticies, YueSha, CHanzy
 * stuff from GT6U
 */
public class MultiTileEntityMatterReplicator extends TileEntityBase10MultiBlockMachine {
    @Override protected MTEC_MultiBlockMachine getNewCoreMultiBlock() {return new MTEC_MultiBlockMatterReplicator(this);}
    
    @Override public String getTileEntityName() {return "gt.multitileentity.multiblock.matterreplicator";}
}
