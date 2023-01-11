package gregtechCH.tileentity.cores.boilers;


/**
 * 内部拥有 MTEC_BoilerTank 的实体继承此接口，用于方便通过实体获取 core
 * 并方便表明 core 需要的实体接口
 */
public interface IMTEC_HasBoilerTank {
    // 返回内部的 core
    MTEC_BoilerTank core();
    // 获取朝向
    byte facing();
}
