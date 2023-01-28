package gregtech.asm.transformers.replacements;

import codechicken.nei.NEIServerConfig;
import gregtechCH.threads.Timer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * @author CHanzy
 * 用来 asm 注入的类
 * 仅输出超时不进行报错
 */
public class TimerNEI extends Timer {
    public TimerNEI() {super(500);} // 依旧为 500 ms（注意这个版本的 asm 不支持添加 SIPUSH 和 LDC 等节点）
    
    @Override public StoppableAndTimeoutable getSubTimer(final Object aObject) {
        return new StoppableAndTimeoutable() {
            @Override public void doTimeOut(int aTime) {
                String tItemName = "'" + aObject.toString() + "'";
                if (aObject instanceof Item) tItemName += "(" + ((Item)aObject).getUnlocalizedName() + ")";
                else if (aObject instanceof ItemStack) tItemName += "(" + ((ItemStack)aObject).getUnlocalizedName() + ")";
                
                NEIServerConfig.logger.warn("Item "+tItemName+" takes too much time when loading (more than "+aTime+" ms)");
            }
        };
    }
}
