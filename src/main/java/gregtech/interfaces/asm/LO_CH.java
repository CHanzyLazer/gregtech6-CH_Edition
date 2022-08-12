package gregtech.interfaces.asm;

import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

// 在这里提供一些 GT 方块不透光度（LightOpacity）的接口，需要自身注入来使得这些接口有效
// 也提供 ASM 中需要使用到的接口，因此此目录需要不被 ASM 排除
public class LO_CH {
    // 用来方便的指示是否开启这个 ASM
    public static boolean isEnableAsmBlockGtLightOpacity() {return false;}
    public static NibbleArray getLightOpacityNA(ExtendedBlockStorage aEBS) {return null;}
    // 保证获取到的 NA 都是相同的
    public static NibbleArray createLightOpacityNA() {return new NibbleArray(4096, 4);}
    public static NibbleArray createLightOpacityNA(byte[] aData) {assert aData.length == 2048; return new NibbleArray(aData, 4);}
    // 在需要时创建和清除这个 NA
    public static void initLightOpacityNA(ExtendedBlockStorage aEBS, NibbleArray aNA) {} // 不知为何不能返回 NA？
    public static void clearLightOpacityNA(ExtendedBlockStorage aEBS) {}
}
