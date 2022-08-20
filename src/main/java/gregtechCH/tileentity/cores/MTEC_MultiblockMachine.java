package gregtechCH.tileentity.cores;

import gregapi.tileentity.delegate.DelegatorTileEntity;
import gregapi.tileentity.multiblocks.TileEntityBase10MultiBlockMachine;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.IFluidHandler;

import java.util.List;

public abstract class MTEC_MultiblockMachine {
    // the instance of TileEntityBase10MultiBlockMachine
    protected TileEntityBase10MultiBlockMachine mTE;
    public MTEC_MultiblockMachine(TileEntityBase10MultiBlockMachine aTE) {mTE = aTE;}

    /* main code */
    public abstract void toolTipsMultiblock(List<String> aList);
    public abstract boolean checkStructure2();

    public abstract boolean isInsideStructure(int aX, int aY, int aZ);
    public abstract DelegatorTileEntity<IInventory> getItemInputTarget(byte aSide);
    public abstract DelegatorTileEntity<TileEntity> getItemOutputTarget(byte aSide);
    public abstract DelegatorTileEntity<IFluidHandler> getFluidInputTarget(byte aSide);
    public abstract DelegatorTileEntity<IFluidHandler> getFluidOutputTarget(byte aSide, Fluid aOutput);

    public abstract String getTileEntityNameCompat();
}
