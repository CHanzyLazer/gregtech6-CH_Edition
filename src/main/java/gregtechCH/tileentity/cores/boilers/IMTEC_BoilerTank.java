package gregtechCH.tileentity.cores.boilers;

/**
 * @author CHanzy
 * 包含管理 BoilerTank 的 core 应该继承的接口
 * 可以使用继承来实现 TE 继承时重写其中的某些子项
 * 也可以使用多个 core 来分别实现其中的每个子项
 * TE 继承并将自身作为 core 传入来指定每个子项使用何种 core
 **/
public interface IMTEC_BoilerTank {
    public void onTickConvert();
    public void onTickCoolDown();
    public void onTickEmitSteam();
    public void onTickSetBarometer();
    public void onTickExplodeCheck();

    // 通用调用
    public static class Util {
        public static void onTick(IMTEC_BoilerTank aCore) {
            aCore.onTickConvert();
            aCore.onTickCoolDown();
            aCore.onTickEmitSteam();
            aCore.onTickSetBarometer();
            aCore.onTickExplodeCheck();
        }
    }
}
