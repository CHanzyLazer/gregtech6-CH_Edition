package gregtechCH.tileentity.cores;


/*
 * 包含管理 BasicMachine 的 core 应该继承的接口
 * 可以通过类似多继承的写法将多种功能合并进一个 core 中方便调用
 * 也可以使用多个 core 来分别实现其中的每个子项
 * TE 继承并将自身作为 core 传入来指定每个子项使用何种 core
 * 目前两种写法都保留
 **/
public interface IMTEC_BasicMachine {
    public boolean doWorkCheck(long aTimer);
    public void doWorkActive(long aTimer);
    public void doWorkInactive(long aTimer);
    public void doWorkFinal(long aTimer);

    public static class Util {
        public static void doWork(IMTEC_BasicMachine aCore, long aTimer) {
            if (aCore.doWorkCheck(aTimer))
                aCore.doWorkActive(aTimer);
            else
                aCore.doWorkInactive(aTimer);
            aCore.doWorkFinal(aTimer);
        }
    }
}
