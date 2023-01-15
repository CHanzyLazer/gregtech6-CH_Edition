package gregtechCH.tileentity.cores.electric;

import gregapi.tileentity.energy.ITileEntityEnergy;
import gregapi.tileentity.energy.ITileEntityEnergyDataCapacitor;
import gregapi.tileentity.machines.ITileEntityRunningActively;
import gregapi.tileentity.machines.ITileEntitySwitchableMode;
import gregapi.tileentity.machines.ITileEntitySwitchableOnOff;

/**
 * @author CHanzy
 * 内部拥有 MTEC_ElectricStorage 的实体继承此接口，用于方便通过实体获取 core
 * 并方便表明 core 需要的实体接口，以及需要实现的覆盖版控制接口
 */
public interface IMTEC_HasElectricStorage extends ITileEntityEnergy, ITileEntityEnergyDataCapacitor, ITileEntityRunningActively, ITileEntitySwitchableOnOff, ITileEntitySwitchableMode {
    // 返回内部的 core
    MTEC_ElectricStorage core();
}
