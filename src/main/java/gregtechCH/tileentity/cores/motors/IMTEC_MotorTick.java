package gregtechCH.tileentity.cores.motors;

/**
 * @author CHanzy
 * 包含管理 Motor 每个 tick 行为的 core 应该继承的接口
 * 可以使用继承来实现 TE 继承时重写其中的某些子项
 * 也可以使用多个 core 来分别实现其中的每个子项
 * TE 继承并将自身作为 core 传入来指定每个子项使用何种 core
 **/
public interface IMTEC_MotorTick {
    public void onTickConvert();
    public boolean onTickCheckOverload();
    public void onTickDoOverload();
    public boolean onTickCheckActive();
    public void onTickDoActive();
    public boolean onTickCheckPreheat();
    public void onTickDoPreheat();
    public boolean onTickCheckCooldown();
    public void onTickDoCooldown();
    public void onTickDoElse();
    public void onTickExplodeCheck(long aTimer);
    public boolean onTickStopCheck();


    public static class Util {
        public static void onTick(IMTEC_MotorTick aCore, long aTimer) {
            // 停机检测
            if (aCore.onTickStopCheck()) return; // 如果停机则不执行后续操作
            // 转换能量
            aCore.onTickConvert();
            // 状态判断
            if (aCore.onTickCheckOverload()) {
                // 超载情况
                aCore.onTickDoOverload();
            } else
            if (aCore.onTickCheckPreheat()) {
                // 预热情况
                aCore.onTickDoPreheat();
            } else
            if (aCore.onTickCheckCooldown()) {
                // 冷却情况
                aCore.onTickDoCooldown();
            } else
            if (aCore.onTickCheckActive()) {
                // 输出情况
                aCore.onTickDoActive();
            } else {
                // 其他情况
                aCore.onTickDoElse();
            }
            // 淋雨损坏等（在最后可以保证一定是经过正确的检测的状态）
            aCore.onTickExplodeCheck(aTimer);
        }
    }
}
