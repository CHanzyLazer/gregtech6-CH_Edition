package gregtech.asm.transformers.minecraft;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

import static gregtech.interfaces.asm.LO_CH.*;

/* This is a separate file so it class loads *while* minecraft loads,
   if we accessed world in the main transformer then we can miss out
   on the transformations.  Not an issue when accessing MC classes
   while transforming other mods though.
   GTCH, 我的修改统一放到另一个文件
 */
public class Replacements_CH {
    // 提供的实用接口
    public static void setLightOpacityData(ExtendedBlockStorage aEBS, byte[] aData) {
        initLightOpacityNA(aEBS, createLightOpacityNA(aData));
    }
    // 读取和写入 NBT
    public static void writeChunkToNbt(ExtendedBlockStorage aEBS, NBTTagCompound aNBT) {
        boolean tEmpty = true;
        NibbleArray tLONA = getLightOpacityNA(aEBS);
        if (tLONA != null) {
            // 对于全为 0 的情况进行“优化”，移除这个 NBT 标签
            long tMaxAbsValue = 0;
            for (byte tValue : tLONA.data) tMaxAbsValue = Math.max(tMaxAbsValue, Math.abs(tValue));
            if (tMaxAbsValue != 0) {
                tEmpty = false;
                aNBT.setByteArray("blockGTLightOpacity", tLONA.data);
            }
        }
        if (tEmpty) aNBT.removeTag("blockGTLightOpacity");
    }
    public static void readChunkFromNbt(ExtendedBlockStorage aEBS, NBTTagCompound aNBT) {
        if (aNBT.hasKey("blockGTLightOpacity")) {
            setLightOpacityData(aEBS, aNBT.getByteArray("blockGTLightOpacity"));
        } else {
            if (getLightOpacityNA(aEBS) != null) clearLightOpacityNA(aEBS); // 否则清空这个数据
        }
    }
    // 设置 byteArray，用于数据同步
    public static int setBlockGTLightOpacityData(ExtendedBlockStorage[] aEBSs, boolean p_149269_1_, int p_149269_2_, byte[] aData, int aOffset) {
        boolean tHasGTLO = false;
        // 简单优化，当一个区块全都为 null 时不同步此数据
        for (int tY=0; tY < aEBSs.length; ++tY) if (aEBSs[tY] != null && (!p_149269_1_ || !aEBSs[tY].isEmpty()) && (p_149269_2_ & (1<<tY)) != 0 && getLightOpacityNA(aEBSs[tY]) != null) {tHasGTLO = true; break;}
        if (tHasGTLO) for (int tY=0; tY < aEBSs.length; ++tY) if (aEBSs[tY] != null && (p_149269_2_ & (1<<tY)) != 0) {
            NibbleArray tNA = getLightOpacityNA(aEBSs[tY]);
            if (tNA == null) tNA = createLightOpacityNA(); // 目前为空时也需要传出零数组
            System.arraycopy(tNA.data, 0, aData, aOffset, tNA.data.length);
            aOffset += tNA.data.length;
        }
        return aOffset;
    }
    // 获取 byteArray, 用于数据同步
    public static int getBlockGTLightOpacityData(ExtendedBlockStorage[] aEBSs, int p_76607_2_, byte[] aData, int aOffset) {
        // 由于放置在了最后，如果数据没有了则说明整个区块都是 null，需要手动清空客户端数据
        if (aData.length < aOffset+1) {
            for (int tY=0; tY < aEBSs.length; ++tY) if ((p_76607_2_ & (1<<tY)) != 0 && aEBSs[tY] != null) {
                if (getLightOpacityNA(aEBSs[tY]) != null) clearLightOpacityNA(aEBSs[tY]);
            }
            return aOffset;
        }
        for (int tY=0; tY < aEBSs.length; ++tY) if ((p_76607_2_ & (1<<tY)) != 0 && aEBSs[tY] != null) {
            NibbleArray tNA = getLightOpacityNA(aEBSs[tY]);
            if (tNA == null) {tNA = createLightOpacityNA(); initLightOpacityNA(aEBSs[tY], tNA);} // 目前为空时需要传创建数组
            System.arraycopy(aData, aOffset, tNA.data, 0, tNA.data.length);
            aOffset += tNA.data.length;
        }
        return aOffset;
    }
}
