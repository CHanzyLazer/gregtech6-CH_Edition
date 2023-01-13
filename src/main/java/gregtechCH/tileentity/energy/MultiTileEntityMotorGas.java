package gregtechCH.tileentity.energy;

import gregtechCH.tileentity.ITileEntityNameCompat;
import gregtechCH.tileentity.cores.motors.MTEC_Motor;
import gregtechCH.tileentity.cores.motors.MTEC_MotorGas;
import net.minecraftforge.fluids.IFluidHandler;

/**
 * @author CHanzy
 */
public class MultiTileEntityMotorGas extends TileEntityBase10Motor implements IFluidHandler, ITileEntityNameCompat {
    @Override protected MTEC_Motor getNewCoreMotor2() {return new MTEC_MotorGas(this);}
    
    @Override public String getTileEntityName() {return "gt.multitileentity.generator.motor_gas";}
    @Override public String getTileEntityNameCompat() {return "gt.multitileentity.generator.gasmotor";}
}
