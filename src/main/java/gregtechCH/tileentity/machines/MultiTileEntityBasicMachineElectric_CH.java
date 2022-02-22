package gregtechCH.tileentity.machines;

import gregapi.tileentity.energy.ITileEntityEnergyElectricityAcceptor;
import gregtechCH.tileentity.energy.MultiTileEntityBasicMachine_CH;

public class MultiTileEntityBasicMachineElectric_CH extends MultiTileEntityBasicMachine_CH implements ITileEntityEnergyElectricityAcceptor {
    @Override public String getTileEntityName() {return "gtch.multitileentity.machine.basic.electric";}
}
