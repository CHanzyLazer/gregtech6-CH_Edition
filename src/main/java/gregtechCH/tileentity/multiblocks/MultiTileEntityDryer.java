package gregtechCH.tileentity.multiblocks;

import gregapi.tileentity.multiblocks.TileEntityBase10MultiBlockMachine;
import gregtechCH.tileentity.cores.basicmachines.MTEC_MultiBlockDryer;
import gregtechCH.tileentity.cores.basicmachines.MTEC_MultiBlockMachine;
import net.minecraft.nbt.NBTTagCompound;

import static gregapi.data.CS.NBT_DESIGN;

/**
 * stuff from GT6U
 **/
public class MultiTileEntityDryer extends TileEntityBase10MultiBlockMachine {
    @Override protected MTEC_MultiBlockMachine getNewCoreMultiBlock() {return new MTEC_MultiBlockDryer(this);}
    
    public short mDryerWalls = 18002;
    
    @Override
    public void readFromNBT2(NBTTagCompound aNBT) {
        super.readFromNBT2(aNBT);
        if (aNBT.hasKey(NBT_DESIGN)) mDryerWalls = aNBT.getShort(NBT_DESIGN);
    }
    
    @Override public String getTileEntityName() {return "gt.multitileentity.multiblock.largedryer";}
}
