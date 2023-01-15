package gregtechCH.tileentity.cores.basicmachines;


/**
 * @author CHanzy
 * 包含管理 BasicMachine 的 core 应该继承的接口
 * 可以使用继承来实现 TE 继承时重写其中的某些子项
 * 也可以使用多个 core 来分别实现其中的每个子项
 * TE 继承并将自身作为 core 传入来指定每个子项使用何种 core
 */
public interface IMTEC_BasicMachine {
    public boolean doWorkCheck(long aTimer);
    public void doWorkActive(long aTimer);
    public void doWorkInactive(long aTimer);
    public void doWorkFinal(long aTimer);

    public static class Util {
        public static void doWork(IMTEC_BasicMachine aCore, long aTimer) {
            if (aCore.doWorkCheck(aTimer)) {
                aCore.doWorkActive(aTimer);
            }
            else {
                aCore.doWorkInactive(aTimer);
            }
            aCore.doWorkFinal(aTimer);
        }
    }
}
