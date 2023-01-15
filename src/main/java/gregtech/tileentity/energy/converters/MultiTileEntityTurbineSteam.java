package gregtech.tileentity.energy.converters;

import gregtechCH.fluid.IFluidHandler_CH;
import gregtechCH.tileentity.ITileEntityNameCompat;
import gregtechCH.tileentity.cores.motors.MTEC_Motor;
import gregtechCH.tileentity.cores.motors.MTEC_MotorSteam;
import gregtechCH.tileentity.energy.TileEntityBase10Motor;

/* change to my class */
public class MultiTileEntityTurbineSteam extends TileEntityBase10Motor implements IFluidHandler_CH, ITileEntityNameCompat {
	@Override protected MTEC_Motor getNewCoreMotor2() {return new MTEC_MotorSteam(this);}

	@Override public String getTileEntityName() {return "gt.multitileentity.turbines.rotation_steam";}
	@Override public String getTileEntityNameCompat() {return "gtch.multitileentity.turbines.rotation_steam";}
}
