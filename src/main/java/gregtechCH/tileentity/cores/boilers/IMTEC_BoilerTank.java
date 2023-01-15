package gregtechCH.tileentity.cores.boilers;

/**
 * @author CHanzy
 * 包含管理 BoilerTank 的 core 应该继承的接口
 * 可以使用继承来实现 TE 继承时重写其中的某些子项
 * 也可以使用多个 core 来分别实现其中的每个子项
 * TE 继承并将自身作为 core 传入来指定每个子项使用何种 core
 */
public interface IMTEC_BoilerTank {
    public void onTickConvert(long aTimer);
    public void onTickCoolDown(long aTimer);
    public void onTickEmitSteam(long aTimer);
    public void onTickSetBarometer(long aTimer);
    public void onTickExplodeCheck(long aTimer);

    // 通用调用
    public static class Util {
        public static void onTick(IMTEC_BoilerTank aCore, long aTimer) {
            aCore.onTickConvert(aTimer);
            aCore.onTickCoolDown(aTimer);
            aCore.onTickEmitSteam(aTimer);
            aCore.onTickSetBarometer(aTimer);
            aCore.onTickExplodeCheck(aTimer);
        }
    }
}
