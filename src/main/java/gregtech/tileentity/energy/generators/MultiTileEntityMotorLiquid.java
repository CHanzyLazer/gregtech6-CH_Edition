package gregtech.tileentity.energy.generators;

import gregapi.tileentity.ITileEntityFunnelAccessible;
import gregapi.tileentity.ITileEntityTapAccessible;
import gregtechCH.tileentity.ITileEntityNameCompat;
import gregtechCH.tileentity.cores.motors.MTEC_Motor;
import gregtechCH.tileentity.cores.motors.MTEC_MotorLiquid;
import gregtechCH.tileentity.energy.TileEntityBase10Motor;
import net.minecraftforge.fluids.IFluidHandler;

/* change to my class */
public class MultiTileEntityMotorLiquid extends TileEntityBase10Motor implements IFluidHandler, ITileEntityNameCompat, ITileEntityFunnelAccessible, ITileEntityTapAccessible {
	@Override protected MTEC_Motor getNewCoreMotor2() {return new MTEC_MotorLiquid(this);}
	
	@Override public String getTileEntityName() {return "gt.multitileentity.generator.motor_liquid";}
	@Override public String getTileEntityNameCompat() {return "gtch.multitileentity.generator.motor_liquid";}
}
