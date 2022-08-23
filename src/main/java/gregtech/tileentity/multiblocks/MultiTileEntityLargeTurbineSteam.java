package gregtech.tileentity.multiblocks;

import gregapi.tileentity.multiblocks.IMultiBlockFluidHandler;
import gregtechCH.fluid.IFluidHandler_CH;
import gregtechCH.tileentity.ITileEntityNameCompat;
import gregtechCH.tileentity.cores.motors.MTEC_LargeMotor;
import gregtechCH.tileentity.cores.motors.MTEC_LargeMotorSteam;
import gregtechCH.tileentity.multiblocks.TileEntityBase11LargeMotor;

public class MultiTileEntityLargeTurbineSteam extends TileEntityBase11LargeMotor implements IMultiBlockFluidHandler, IFluidHandler_CH, ITileEntityNameCompat {
	@Override protected MTEC_LargeMotor getNewCoreLargeMotor2() {return new MTEC_LargeMotorSteam(this);}

	@Override public String getTileEntityName() {return "gt.multitileentity.multiblock.turbine.steam";}
	@Override public String getTileEntityNameCompat() {return "gtch.multitileentity.multiblock.turbine.steam";}
}
