package gregtechCH.tileentity.cores.electric;

import gregapi.code.TagData;
import gregtechCH.tileentity.data.ITileEntityElectric;

/**
 * @author CHanzy
 * 内部拥有 MTEC_ElectricWireBase 的实体继承此接口，用于方便通过实体获取 core
 * 并方便表明 core 需要的实体接口
 */
public interface IMTEC_HasElectricWire extends ITileEntityElectric {
    // 返回内部的 core
    MTEC_ElectricWireBase core();
    // 判断是否连接了这个方向
    boolean connected(byte aSide);
    // 此方向能否注入能量
    boolean isEnergyAcceptingFrom(TagData aEnergyType, byte aSide, boolean aTheoretical);
}
