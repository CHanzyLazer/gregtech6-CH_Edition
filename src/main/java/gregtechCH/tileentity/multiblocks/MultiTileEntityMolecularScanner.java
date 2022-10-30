package gregtechCH.tileentity.multiblocks;

import gregapi.data.CS.SFX;
import gregapi.tileentity.multiblocks.TileEntityBase10MultiBlockMachine;
import gregapi.util.UT;
import gregtechCH.tileentity.cores.basicmachines.MTEC_MultiBlockMachine;
import gregtechCH.tileentity.cores.basicmachines.MTEC_MultiBlockMolecularScanner;

/**
 * stuff from GT6U
 **/
public class MultiTileEntityMolecularScanner extends TileEntityBase10MultiBlockMachine {
    @Override protected MTEC_MultiBlockMachine getNewCoreMultiBlock() {return new MTEC_MultiBlockMolecularScanner(this);}
    
    @Override public void onProcessStarted() {UT.Sounds.send(SFX.MC_EXPLODE, this); super.onProcessStarted();} // TODO 这个可能有点吵了
    
    @Override public String getTileEntityName() {return "gt.multitileentity.multiblock.molecularscanner";}
}
