package gregtech.tileentity.multiblocks;

import gregapi.tileentity.multiblocks.IMultiBlockFluidHandler;
import gregtechCH.tileentity.ITileEntityNameCompat;
import gregtechCH.tileentity.cores.motors.MTEC_LargeMotor;
import gregtechCH.tileentity.cores.motors.MTEC_LargeMotorGas;
import gregtechCH.tileentity.multiblocks.TileEntityBase11LargeMotor;
import net.minecraftforge.fluids.IFluidHandler;

public class MultiTileEntityLargeTurbineGas extends TileEntityBase11LargeMotor implements IMultiBlockFluidHandler, IFluidHandler, ITileEntityNameCompat {
	@Override protected MTEC_LargeMotor getNewCoreLargeMotor2() {return new MTEC_LargeMotorGas(this);}
	
	@Override public String getTileEntityName() {return "gt.multitileentity.multiblock.turbine.gas";}
	@Override public String getTileEntityNameCompat() {return "gtch.multitileentity.multiblock.turbine.gas";}
}
