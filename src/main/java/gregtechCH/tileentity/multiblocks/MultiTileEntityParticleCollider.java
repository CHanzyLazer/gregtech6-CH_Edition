package gregtechCH.tileentity.multiblocks;

import gregapi.tileentity.multiblocks.TileEntityBase10MultiBlockMachine;
import gregtechCH.tileentity.cores.basicmachines.MTEC_MultiBlockMachine;
import gregtechCH.tileentity.cores.basicmachines.MTEC_MultiBlockParticleCollider;

import static gregapi.data.CS.T;

/**
 * stuff from GT6U
 */
public class MultiTileEntityParticleCollider extends TileEntityBase10MultiBlockMachine {
    @Override protected MTEC_MultiBlockMachine getNewCoreMultiBlock() {return new MTEC_MultiBlockParticleCollider(this);}
    
    @Override public boolean refreshStructureOnActiveStateChange() {return T;}
    @Override public String getTileEntityName() {return "gt.multitileentity.multiblock.particlecollider";}
}

