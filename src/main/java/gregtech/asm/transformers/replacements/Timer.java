package gregtech.asm.transformers.replacements;

import codechicken.nei.NEIServerConfig;

/*
   GTCH, 用来 asm 注入的类
 */
public class Timer {
    private final int mLimit;
    private long mBeginTime;
    public Timer() {mLimit = 500;} // 默认依旧定为 500（注意这个版本的 asm 不支持添加 SIPUSH 和 LDA 等节点）
    
    public void reset() {mBeginTime = System.currentTimeMillis();}
    
    public void check(Object aItem) {
        int tTime = (int)(System.currentTimeMillis() - mBeginTime);
        if (tTime > mLimit) NEIServerConfig.logger.warn("Item '"+aItem+"' takes too much time when loading ("+tTime+" ms)");
    }
}
