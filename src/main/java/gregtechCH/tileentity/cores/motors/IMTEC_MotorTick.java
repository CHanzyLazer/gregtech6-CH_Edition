package gregtechCH.tileentity.cores.motors;

/**
 * @author CHanzy
 * 包含管理 Motor 每个 tick 行为的 core 应该继承的接口
 * 可以使用继承来实现 TE 继承时重写其中的某些子项
 * 也可以使用多个 core 来分别实现其中的每个子项
 * TE 继承并将自身作为 core 传入来指定每个子项使用何种 core
 **/
public interface IMTEC_MotorTick {
    public void onTickConvert(long aTimer);
    public boolean onTickCheckOverload(long aTimer);
    public void onTickDoOverload(long aTimer);
    public boolean onTickCheckActive(long aTimer);
    public void onTickDoActive(long aTimer);
    public boolean onTickCheckPreheat(long aTimer);
    public void onTickDoPreheat(long aTimer);
    public boolean onTickCheckCooldown(long aTimer);
    public void onTickDoCooldown(long aTimer);
    public void onTickDoElse(long aTimer);
    public void onTickExplodeCheck(long aTimer);
    public boolean onTickStopCheck(long aTimer);


    public static class Util {
        public static void onTick(IMTEC_MotorTick aCore, long aTimer) {
            // 停机检测
            if (aCore.onTickStopCheck(aTimer)) return; // 如果停机则不执行后续操作
            // 转换能量
            aCore.onTickConvert(aTimer);
            // 状态判断
            if (aCore.onTickCheckOverload(aTimer)) {
                // 超载情况
                aCore.onTickDoOverload(aTimer);
            } else
            if (aCore.onTickCheckPreheat(aTimer)) {
                // 预热情况
                aCore.onTickDoPreheat(aTimer);
            } else
            if (aCore.onTickCheckCooldown(aTimer)) {
                // 冷却情况
                aCore.onTickDoCooldown(aTimer);
            } else
            if (aCore.onTickCheckActive(aTimer)) {
                // 输出情况
                aCore.onTickDoActive(aTimer);
            } else {
                // 其他情况
                aCore.onTickDoElse(aTimer);
            }
            // 淋雨损坏等（在最后可以保证一定是经过正确的检测的状态）
            aCore.onTickExplodeCheck(aTimer);
        }
    }
}
