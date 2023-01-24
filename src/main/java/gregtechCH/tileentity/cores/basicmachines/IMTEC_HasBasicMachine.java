package gregtechCH.tileentity.cores.basicmachines;

/**
 * @author CHanzy
 * 内部拥有 MTEC_BasicMachine 的实体继承此接口，用于方便通过实体获取 core
 * 并方便表明 core 需要的实体接口
 */
public interface IMTEC_HasBasicMachine {
    // 返回内部的 core
    MTEC_BasicMachine core();
}
