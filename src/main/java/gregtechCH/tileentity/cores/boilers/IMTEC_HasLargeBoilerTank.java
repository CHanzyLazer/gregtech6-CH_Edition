package gregtechCH.tileentity.cores.boilers;


/**
 * 内部拥有 MTEC_LargeBoilerTank 的实体继承此接口，用于方便通过实体获取 core
 * 并方便表明 core 需要的实体接口
 */
public interface IMTEC_HasLargeBoilerTank extends IMTEC_HasBoilerTank {
    // 返回内部的 core
    MTEC_LargeBoilerTank core();
    // 多方快接口
    boolean checkStructureOnly(boolean aForce);
    boolean isStructureOkay();
}
