package gregtechCH.util;

import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregapi.oredict.OreDictMaterial;
import gregapi.oredict.OreDictPrefix;
import gregapi.render.BlockTextureDefault;
import gregapi.render.IIconContainer;
import gregapi.util.UT;
import gregtechCH.data.CS_CH;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static gregapi.data.CS.*;
import static gregtechCH.config.ConfigForge_CH.DATA_GTCH;

public class UT_CH {
    // 提供一些 STL 常用的或者我需要用到的而 java 未提供的接口

    public static class STL {
        public static <Entry> void resize(List<Entry> rList, int aNewSize, Class<? extends Entry> aDefaultEntryClass){
            if (aNewSize < 0)
                throw new ArrayIndexOutOfBoundsException(aNewSize);
            int oSize = rList.size();
            if (oSize == aNewSize) return;
            if (aNewSize < oSize) {
                rList.subList(aNewSize, oSize).clear();
            } else {
                try {
                    for (int tSize = oSize; tSize != aNewSize; ++tSize) {
                        rList.add(aDefaultEntryClass.newInstance());
                    }
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        public static <Entry> Entry adaptiveGet(List<Entry> rList, int aIdx, Class<? extends Entry> aDefaultEntryClass) {
            if (aIdx >= rList.size()) resize(rList, aIdx+1, aDefaultEntryClass);
            return rList.get(aIdx);
        }

        public static LinkedList<Long> toList(long[] aArray) {
            LinkedList<Long> tList = new LinkedList<>();
            for(long tEntry : aArray) tList.addLast(tEntry);
            return tList;
        }
        // 限制大小的版本，优先保留尾端
        public static LinkedList<Long> toList(long[] aArray, int aMaxSize) {
            LinkedList<Long> tList = new LinkedList<>();
            if (aMaxSize <= 0) return tList;
            for (long tEntry : aArray) {
                tList.addLast(tEntry);
                while (tList.size() > aMaxSize) tList.pollFirst();
            }
            return tList;
        }
        // 简化调用和重复代码的暴力转换，java 的泛型不太行只能重写好多份
        public static long[] toLongArray(byte[] aArray) {
            long[] tOut = new long[aArray.length];
            for (int i = 0; i < aArray.length; ++i) tOut[i] = aArray[i];
            return tOut;
        }
        public static long[] toLongArray(short[] aArray) {
            long[] tOut = new long[aArray.length];
            for (int i = 0; i < aArray.length; ++i) tOut[i] = aArray[i];
            return tOut;
        }
        public static long[] toLongArray(int[] aArray) {
            long[] tOut = new long[aArray.length];
            for (int i = 0; i < aArray.length; ++i) tOut[i] = aArray[i];
            return tOut;
        }

    }

    public static class Code {
        // 提供一些其他类型的 combine
        public static int combine(short aValue1, short aValue2) {return (0xffff & aValue1) | aValue2 << 16;}

        // 返回玩家所在的坐标
        @SideOnly(Side.CLIENT)
        public static ChunkCoordinates getPlayerChunkCoord(@NotNull Entity aPlayer) {
            return new ChunkCoordinates(((int)Math.round(aPlayer.posX))>>4, ((int)Math.round(aPlayer.posY))>>4, ((int)Math.round(aPlayer.posZ))>>4);
        }
        // 返回玩家视角的单位向量
        @SideOnly(Side.CLIENT)
        public static Vec3 getPlayerViewVec3(@NotNull Entity aPlayer) {
            return Vec3.createVectorHelper(
                    -Math.sin(aPlayer.rotationYaw / 180.0D * Math.PI) * Math.cos(aPlayer.rotationPitch / 180.0D * Math.PI),
                    -Math.sin(aPlayer.rotationPitch / 180.0D * Math.PI),
                    Math.cos(aPlayer.rotationYaw / 180.0D * Math.PI) * Math.cos(aPlayer.rotationPitch / 180.0D * Math.PI));
        }

        public final static float RENDER_LENGTH = 0.01F;
        public final static float RENDER_EPS = 0.001F;
        // 抹去 RENDER_LENGTH 的向下取整
        public static int renderFloor(double afloat) {
            return (int)Math.floor(afloat + RENDER_LENGTH);
        }
        // 抹去 RENDER_LENGTH 的向上取整
        public static int renderCeil(double afloat) {
            return (int)Math.ceil(afloat - RENDER_LENGTH);
        }

        public static long effNormalize(int aEff) {
            int tEff40 = (aEff / 250) * 10000 / 40;
            int tEff30 = (aEff / 333) * 10000 / 30;
            return UT.Code.bind_(0, 10000, (aEff - tEff40 < aEff - tEff30) ? tEff40 : tEff30);
        }

        public static long effNormalizeRound(int aEff) {
            int[] tEffArray = {
                    (aEff / 250) * 10000 / 40,
                    (aEff / 333) * 10000 / 30,
                    (int) UT.Code.divup(aEff, 250) * 10000 / 40,
                    (int) UT.Code.divup(aEff, 333) * 10000 / 30};
            int tMinEff = tEffArray[0];
            for (int i = 1; i < 4; ++i) {
                if (Math.abs(tEffArray[i] - aEff) < Math.abs(tMinEff - aEff)) tMinEff = tEffArray[i];
            }
            return UT.Code.bind_(0, 10000, tMinEff);
        }

        // 颜色计算部分
        public static int getMarkRGB(int aRGB) {
            return getMarkRGB(aRGB, DATA_GTCH.markRatio);
        }
        // RGB 和 HSV 的相互转换
        // RGB 和 HSV 都是 0-1 的浮点数
        public static void RGB2HSV(final float[] aRGB, float[] rHSV) {
            float tMax = Math.max(aRGB[0], Math.max(aRGB[1], aRGB[2]));
            float tMin = Math.min(aRGB[0], Math.min(aRGB[1], aRGB[2]));
            float tDelta = tMax - tMin;
            if (tMax == tMin) {
                rHSV[0] = 0.0F;
            } else
            if (tMax == aRGB[0]) {
                float tGB = aRGB[1] - aRGB[2];
                rHSV[0] = ( (tGB/tDelta) + (tGB>0F?0.0F:6.0F) )/6.0F;
            } else
            if (tMax == aRGB[1]) {
                rHSV[0] = (((aRGB[2] - aRGB[0])/tDelta) + 2.0F)/6.0F;
            } else
            if (tMax == aRGB[2]) {
                rHSV[0] = (((aRGB[0] - aRGB[1])/tDelta) + 4.0F)/6.0F;
            }
            rHSV[1] = (tMax == 0.0F) ? 0.0F : tDelta/tMax;
            rHSV[2] = tMax;
        }
        public static void HSV2RGB(float[] rRGB, final float[] aHSV) {
            float tC = aHSV[2] * aHSV[1];
            float tX = tC * (1.0F - Math.abs((aHSV[0]*6.0F)%2.0F - 1.0F));
            float tM = aHSV[2] - tC;
            if (aHSV[0]*6.0F < 1.0F) {
                rRGB[0] = tC; rRGB[1] = tX; rRGB[2] = 0.0F;
            } else
            if (aHSV[0]*6.0F < 2.0F) {
                rRGB[0] = tX; rRGB[1] = tC; rRGB[2] = 0.0F;
            } else
            if (aHSV[0]*6.0F < 3.0F) {
                rRGB[0] = 0.0F; rRGB[1] = tC; rRGB[2] = tX;
            } else
            if (aHSV[0]*6.0F < 4.0F) {
                rRGB[0] = 0.0F; rRGB[1] = tX; rRGB[2] = tC;
            } else
            if (aHSV[0]*6.0F < 5.0F) {
                rRGB[0] = tX; rRGB[1] = 0.0F; rRGB[2] = tC;
            } else {
                rRGB[0] = tC; rRGB[1] = 0.0F; rRGB[2] = tX;
            }
            rRGB[0] += tM; rRGB[1] += tM; rRGB[2] += tM;
        }
        // 使用 RGB 到 HSV 相互转换的方式，计算更加准确的变暗变亮值
        // 输入负值来变暗
        public static int getBrighterRGB(int aRGB, float aAmount) {
            short[] tRGBArray = getRGBArray(aRGB);
            float[] tRGB = {tRGBArray[0]/255F, tRGBArray[1]/255F, tRGBArray[2]/255F};
            float[] tHSV = new float[3];
            RGB2HSV(tRGB, tHSV);
            tHSV[2] += aAmount;
            tHSV[2] = Math.min(1.0F, Math.max(tHSV[2], 0.0F));
            HSV2RGB(tRGB, tHSV);
            tRGBArray[0] = (short) Math.round(tRGB[0]*255); tRGBArray[1] = (short) Math.round(tRGB[1]*255); tRGBArray[2] = (short) Math.round(tRGB[2]*255);
            return UT.Code.getRGBInt(tRGBArray);
        }
        public static short[] getRGBArray(int aColors) {
            return new short[] {(short)((aColors >>> 16) & 255), (short)((aColors >>> 8) & 255), (short)(aColors & 255)};
        }
        // 默认变暗，加入亮度检测
        public static int getMarkRGB(int aRGB, float aAmount) {
            short[] tRGBArray = getRGBArray(aRGB);
            float[] tRGB = {tRGBArray[0]/255F, tRGBArray[1]/255F, tRGBArray[2]/255F};
            float[] tHSV = new float[3];
            RGB2HSV(tRGB, tHSV);
            aAmount = Math.min(1.0F, Math.max(aAmount, 0.0F));
            if (tHSV[2] < aAmount) {
                tHSV[2] += aAmount;
            } else {
                tHSV[2] -= aAmount;
            }
            HSV2RGB(tRGB, tHSV);
            tRGBArray[0] = (short) Math.round(tRGB[0]*255); tRGBArray[1] = (short) Math.round(tRGB[1]*255); tRGBArray[2] = (short) Math.round(tRGB[2]*255);
            return UT.Code.getRGBInt(tRGBArray);
        }

        // 使用向上整除来保证一定能够达到后一个颜色
        public static int mixRGBInt(int aRGB1, int aRGB2) {
            short[] tFrom = getRGBArray(aRGB1);
            short[] tTo = getRGBArray(aRGB2);
            tTo[0] -= tFrom[0]; tTo[1] -= tFrom[1]; tTo[2] -= tFrom[2];
            tFrom[0] += (short)((tTo[0]>=0)?UT.Code.divup(tTo[0], 2):-UT.Code.divup(Math.abs(tTo[0]), 2));
            tFrom[1] += (short)((tTo[1]>=0)?UT.Code.divup(tTo[1], 2):-UT.Code.divup(Math.abs(tTo[1]), 2));
            tFrom[2] += (short)((tTo[2]>=0)?UT.Code.divup(tTo[2], 2):-UT.Code.divup(Math.abs(tTo[2]), 2));
            return UT.Code.getRGBInt(tFrom);
        }

        // overlay 使用的颜色通用值
        public static int getOverlayRGB(int aRGBPaint) {
            // 使用按比例 RGB 混合白色的方法来限制染色深浅
            return getMixRGBInt(DYE_INT_White, aRGBPaint, 1F-DATA_GTCH.mixBaseRatio);
        }
        // 将 paint 混合颜色的方法放在这里方便一起修改
        public static int getPaintRGB(int aRGBOrigin, int aRGBPaint) {
            return getMixRGBIntSic(aRGBOrigin, aRGBPaint, DATA_GTCH.mixBaseRatio, DATA_GTCH.mixPaintRatio);
        }
        // 直接将 RGB 混合，相比原本可以指定后一个 RGB 的占比权重
        public static int getMixRGBInt(int aRGB1, int aRGB2, float aWeight) {
            // 优化特殊输入
            if (aWeight <= 0.0F) return aRGB1;
            if (aWeight >= 1.0F) return aRGB2;

            short[] tRGBArray = getRGBArray(aRGB1);
            float[] tRGB1 = {tRGBArray[0]/255F, tRGBArray[1]/255F, tRGBArray[2]/255F};
            tRGBArray = getRGBArray(aRGB2);
            float[] tRGB2 = {tRGBArray[0]/255F, tRGBArray[1]/255F, tRGBArray[2]/255F};
            aWeight = Math.min(1.0F, Math.max(aWeight, 0.0F));
            tRGB1[0] += (tRGB2[0] - tRGB1[0]) * aWeight;
            tRGB1[1] += (tRGB2[1] - tRGB1[1]) * aWeight;
            tRGB1[2] += (tRGB2[2] - tRGB1[2]) * aWeight;

            tRGBArray[0] = (short) Math.round(tRGB1[0]*255); tRGBArray[1] = (short) Math.round(tRGB1[1]*255); tRGBArray[2] = (short) Math.round(tRGB1[2]*255);
            return UT.Code.getRGBInt(tRGBArray);
        }
        // 使用科学染色方法得到混合颜色，应该可以得到更好的结果
        public static int getMixRGBIntSic(int aRGBBase, int aRGBPaint, float aWeightBase, float aWeightPaint) {
            // 优化特殊输入
            if (aWeightBase >= 1.0F && aWeightPaint <= 0.0F) return aRGBBase;
            if (aWeightPaint >= 1.0F && aWeightBase <= 0.0F) return aRGBPaint;

            short[] tRGBArray = getRGBArray(aRGBBase);
            float[] tRGBBase = {tRGBArray[0]/255F, tRGBArray[1]/255F, tRGBArray[2]/255F};
            tRGBArray = getRGBArray(aRGBPaint);
            float[] tRGBPaint = {tRGBArray[0]/255F, tRGBArray[1]/255F, tRGBArray[2]/255F};
            aWeightBase = Math.min(1.0F, Math.max(aWeightBase, 0.0F));
            aWeightPaint = Math.min(1.0F, Math.max(aWeightPaint, 0.0F));

            // 按照 overlay 染色方法计算带有权重的底色颜色
            tRGBBase[0] *= (tRGBPaint[0] + (1F - tRGBPaint[0]) * aWeightBase);
            tRGBBase[1] *= (tRGBPaint[1] + (1F - tRGBPaint[1]) * aWeightBase);
            tRGBBase[2] *= (tRGBPaint[2] + (1F - tRGBPaint[2]) * aWeightBase);
            // 此时计算结果期望的亮度值
            float[] tHSV = new float[3];
            RGB2HSV(tRGBBase, tHSV);
            float tV = tHSV[2];
            RGB2HSV(tRGBPaint, tHSV);
            tV += (tHSV[2] - tV) * aWeightPaint;
            // 染料颜色直接按照权重比例放缩
            tRGBPaint[0] *= aWeightPaint;
            tRGBPaint[1] *= aWeightPaint;
            tRGBPaint[2] *= aWeightPaint;
            // 直接两者进行混合，注意混合后需要保证亮度不变
            tRGBBase[0] += tRGBPaint[0]; tRGBBase[0] *= 0.5;
            tRGBBase[1] += tRGBPaint[1]; tRGBBase[1] *= 0.5;
            tRGBBase[2] += tRGBPaint[2]; tRGBBase[2] *= 0.5;
            // 使用 HSV 来保证亮度值不变
            RGB2HSV(tRGBBase, tHSV);
            tHSV[2] = tV;
            HSV2RGB(tRGBBase, tHSV);

            tRGBArray[0] = (short) Math.round(tRGBBase[0]*255); tRGBArray[1] = (short) Math.round(tRGBBase[1]*255); tRGBArray[2] = (short) Math.round(tRGBBase[2]*255);
            return UT.Code.getRGBInt(tRGBArray);
        }

        // 将两个 RGB 按照 HSV 来混合，并且可以指定后一个 RGB 的 HSV 占比权重
        public static int getMixRGBIntHSV(int aRGB1, int aRGB2, float aH, float aS, float aV) {
            short[] tRGBArray = getRGBArray(aRGB1);
            float[] tRGB1 = {tRGBArray[0]/255F, tRGBArray[1]/255F, tRGBArray[2]/255F};
            tRGBArray = getRGBArray(aRGB2);
            float[] tRGB2 = {tRGBArray[0]/255F, tRGBArray[1]/255F, tRGBArray[2]/255F};
            float[] tHSV1 = new float[3], tHSV2 = new float[3];
            RGB2HSV(tRGB1, tHSV1);
            RGB2HSV(tRGB2, tHSV2);
            aH = Math.min(1.0F, Math.max(aH, 0.0F));
            aS = Math.min(1.0F, Math.max(aS, 0.0F));
            aV = Math.min(1.0F, Math.max(aV, 0.0F));

            // 对 H 值特殊处理，考虑颜色的 S 和 V 值作为权重
            if (Math.abs(tHSV2[0] - tHSV1[0]) < 0.5F) {
                // 正常情况，还是要先计算分母防止除零
                float tDiver = (1.0F-aH)*tHSV1[1]*tHSV1[2] + aH*tHSV2[1]*tHSV2[2];
                tHSV1[0] = (tDiver > 0.0F) ? (tHSV1[0]*(1.0F-aH)*tHSV1[1]*tHSV1[2] + tHSV2[0]*aH*tHSV2[1]*tHSV2[2]) / tDiver : 0.0F;
            } else {
                if (tHSV2[0] > 0.5F) {
                    // HSV2 较大，应用周期条件
                    tHSV2[0] -= 1.0F;
                } else {
                    // HSV1 较大，应用周期条件
                    tHSV1[0] -= 1.0F;
                }
                float tDiver = (1.0F-aH)*tHSV1[1]*tHSV1[2] + aH*tHSV2[1]*tHSV2[2];
                tHSV1[0] = (tDiver > 0.0F) ? (tHSV1[0]*(1.0F-aH)*tHSV1[1]*tHSV1[2] + tHSV2[0]*aH*tHSV2[1]*tHSV2[2]) / tDiver : 0.0F;
                // 可能结果超过边界，再次应用周期条件
                if (tHSV1[0] < 0.0F) tHSV1[0] += 1.0F;
            }
            tHSV1[1] += (tHSV2[1] - tHSV1[1]) * aS;
            tHSV1[2] += (tHSV2[2] - tHSV1[2]) * aV;
            HSV2RGB(tRGB1, tHSV1);
            tRGBArray[0] = (short) Math.round(tRGB1[0]*255); tRGBArray[1] = (short) Math.round(tRGB1[1]*255); tRGBArray[2] = (short) Math.round(tRGB1[2]*255);
            return UT.Code.getRGBInt(tRGBArray);
        }

    }

    public static class NBT {
        // 用来得到 item 存储 nbt 的数字类型，因为无论如何 item nbt 都会移除 0 值，因此检测到零值则将其减一
        public static long toItemNumber(long aInt) {
            if (aInt > 0) return aInt;
            if (aInt == 0) return -1;
            throw new RuntimeException("initial number of ItemNumber must be non-negative");
        }

        // 反向操作，用来将 item nbt 中存储的数据再次转换为需要的数据
        public static long getItemNumber(long aInt) {
            if (aInt >= 0) return aInt;
            if (aInt == -1) return 0;
            throw new RuntimeException("ItemNumber must be non-negative or -1");
        }

        /* 方便的存储数组 NBT 的方法，自动选用最小的体积存储，统一使用 byteArray 存储，如果全是零则会移除 NBT 标签*/
        public static NBTTagCompound setNumberArray(NBTTagCompound aNBT, Object aTag, long[] aValues) {
            long tMaxAbsValue = 0;
            for (long tValue : aValues) tMaxAbsValue = Math.max(tMaxAbsValue, Math.abs(tValue));
            if (tMaxAbsValue == 0) {aNBT.removeTag(aTag.toString()); return aNBT;}
            byte[] tBytes; // 按照数据长度存储，最后一位存储数据类型数据
            if (tMaxAbsValue > Integer.MAX_VALUE) {
                tBytes = new byte[(aValues.length<<3)+1];
                for (int i = 0; i < aValues.length; ++i) {
                    tBytes[i<<3]        = UT.Code.toByteL(aValues[i], 0);
                    tBytes[(i<<3)+1]    = UT.Code.toByteL(aValues[i], 1);
                    tBytes[(i<<3)+2]    = UT.Code.toByteL(aValues[i], 2);
                    tBytes[(i<<3)+3]    = UT.Code.toByteL(aValues[i], 3);
                    tBytes[(i<<3)+4]    = UT.Code.toByteL(aValues[i], 4);
                    tBytes[(i<<3)+5]    = UT.Code.toByteL(aValues[i], 5);
                    tBytes[(i<<3)+6]    = UT.Code.toByteL(aValues[i], 6);
                    tBytes[(i<<3)+7]    = UT.Code.toByteL(aValues[i], 7);
                }
                tBytes[aValues.length<<3] = (byte)CS_CH.NumberType.LONG.ordinal();
            } else
            if (tMaxAbsValue > Short.MAX_VALUE) {
                tBytes = new byte[(aValues.length<<2)+1];
                for (int i = 0; i < aValues.length; ++i) {
                    tBytes[i<<2]        = UT.Code.toByteI((int)aValues[i], 0);
                    tBytes[(i<<2)+1]    = UT.Code.toByteI((int)aValues[i], 1);
                    tBytes[(i<<2)+2]    = UT.Code.toByteI((int)aValues[i], 2);
                    tBytes[(i<<2)+3]    = UT.Code.toByteI((int)aValues[i], 3);
                }
                tBytes[aValues.length<<2] = (byte)CS_CH.NumberType.INT.ordinal();
            } else
            if (tMaxAbsValue > Byte.MAX_VALUE) {
                tBytes = new byte[(aValues.length<<1)+1];
                for (int i = 0; i < aValues.length; ++i) {
                    tBytes[i<<1]        = UT.Code.toByteS((short)aValues[i], 0);
                    tBytes[(i<<1)+1]    = UT.Code.toByteS((short)aValues[i], 1);
                }
                tBytes[aValues.length<<1] = (byte)CS_CH.NumberType.SHORT.ordinal();
            } else
            {
                tBytes = new byte[aValues.length+1];
                for (int i = 0; i < aValues.length; ++i) tBytes[i] = (byte)aValues[i];
                tBytes[aValues.length] = (byte)CS_CH.NumberType.BYTE.ordinal();
            }
            aNBT.setByteArray(aTag.toString(), tBytes);
            return aNBT;
        }
        /* 对应专门的读取方法 */
        public static long[] getNumberArray(NBTTagCompound aNBT, Object aTag) {
            if (!aNBT.hasKey(aTag.toString())) return new long[0];
            byte[] tBytes = aNBT.getByteArray(aTag.toString());
            if (tBytes.length <= 1) return new long[0];
            CS_CH.NumberType tType = CS_CH.NumberType.values()[tBytes[tBytes.length-1]];
            long[] tValues;
            switch (tType) {
            case LONG:
                tValues = new long[(tBytes.length-1)>>3];
                for (int i = 0; i < tValues.length; ++i) tValues[i] = UT.Code.combine(tBytes[i<<3], tBytes[(i<<3)+1], tBytes[(i<<3)+2], tBytes[(i<<3)+3], tBytes[(i<<3)+4], tBytes[(i<<3)+5], tBytes[(i<<3)+6], tBytes[(i<<3)+7]);
                return tValues;
            case INT:
                tValues = new long[(tBytes.length-1)>>2];
                for (int i = 0; i < tValues.length; ++i) tValues[i] = UT.Code.combine(tBytes[i<<2], tBytes[(i<<2)+1], tBytes[(i<<2)+2], tBytes[(i<<2)+3]);
                return tValues;
            case SHORT:
                tValues = new long[(tBytes.length-1)>>1];
                for (int i = 0; i < tValues.length; ++i) tValues[i] = UT.Code.combine(tBytes[i<<1], tBytes[(i<<1)+1]);
                return tValues;
            case BYTE: default:
                tValues = new long[tBytes.length-1];
                for (int i = 0; i < tValues.length; ++i) tValues[i] = tBytes[i];
                return tValues;
            }
        }
    }

    // 用于直接选择材质是否要保留环境光遮蔽，光照等
    public static class Texture {
        public static BlockTextureDefault BlockTextureDefaultAO(OreDictMaterial aMaterial, int aTextureSetIndex, int aRGBa, boolean aGlow, boolean aEnableAO) {
            return CODE_CLIENT||CODE_UNCHECKED?new BlockTextureDefault(aMaterial, aTextureSetIndex, UT.Code.getRGBaArray(aRGBa), aGlow, aEnableAO):null;
        }
        public static BlockTextureDefault BlockTextureDefaultNoAO(OreDictMaterial aMaterial, int aTextureSetIndex) {
            return CODE_CLIENT||CODE_UNCHECKED?new BlockTextureDefault(aMaterial, aTextureSetIndex, UT.Code.getRGBaArray(UNCOLORED), F, F):null;
        }
        public static BlockTextureDefault BlockTextureDefaultNoAO(OreDictMaterial aMaterial, int aTextureSetIndex, int aRGBa) {
            return CODE_CLIENT||CODE_UNCHECKED?new BlockTextureDefault(aMaterial, aTextureSetIndex, UT.Code.getRGBaArray(aRGBa), F, F):null;
        }
        public static BlockTextureDefault BlockTextureDefaultNoAO(OreDictMaterial aMaterial, int aTextureSetIndex, int aRGBa, boolean aGlow) {
            return CODE_CLIENT||CODE_UNCHECKED?new BlockTextureDefault(aMaterial, aTextureSetIndex, UT.Code.getRGBaArray(aRGBa), aGlow, F):null;
        }
        public static BlockTextureDefault BlockTextureDefaultNoAO(OreDictMaterial aMaterial, OreDictPrefix aPrefix, short[] aRGBa) {
            return CODE_CLIENT||CODE_UNCHECKED?new BlockTextureDefault(aMaterial, aPrefix, aRGBa, F, F):null;
        }
        public static BlockTextureDefault BlockTextureDefaultNoAO(OreDictMaterial aMaterial, OreDictPrefix aPrefix, short[] aRGBa, boolean aGlow) {
            return CODE_CLIENT||CODE_UNCHECKED?new BlockTextureDefault(aMaterial, aPrefix, aRGBa, aGlow, F):null;
        }
        public static BlockTextureDefault BlockTextureDefaultNoAO(IIconContainer aIcon) {
            return CODE_CLIENT||CODE_UNCHECKED?new BlockTextureDefault(aIcon, UNCOLORED, F, F, F, F):null;
        }
        public static BlockTextureDefault BlockTextureDefaultNoAO(IIconContainer aIcon, int aRGBa) {
            return CODE_CLIENT||CODE_UNCHECKED?new BlockTextureDefault(aIcon, aRGBa, F, F, F, F):null;
        }
        public static BlockTextureDefault BlockTextureDefaultNoAO(IIconContainer aIcon, int aRGBa, boolean aGlow) {
            return CODE_CLIENT||CODE_UNCHECKED?new BlockTextureDefault(aIcon, aRGBa, F, aGlow, F, F):null;
        }
    }

    // 用于调用原版 private 的函数，使用和 builtbroken:VoltzEngine 中 BlockUtility 一样的方法但是拷贝代码来避免包含过多第三方库
    public static class Hack {
        public static final String[] CHUNK_RELIGHT_BLOCK = new String[]{"relightBlock", "func_76615_h"};
        public static final String[] CHUNK_PROPAGATE_SKY_LIGHT_OCCLUSION = new String[]{"propagateSkylightOcclusion", "func_76595_e"};
        public static final String[] WORLD_COMPUTE_LIGHT_VALUE = new String[]{"computeLightValue", "func_98179_a"};
        public static final String[] WORLD_WORLD_ACCESSES = new String[]{"worldAccesses", "field_73021_x"};
        public static final String[] RENDER_WORLD_RENDERS = new String[]{"worldRenderers", "field_72765_l"};
        public static final String[] RENDER_RENDER_CHUNKS_WIDE = new String[]{"renderChunksWide", "field_72766_m"};
        public static final String[] RENDER_RENDER_CHUNKS_TALL = new String[]{"renderChunksTall", "field_72763_n"};
        public static final String[] RENDER_RENDER_CHUNKS_DEEP = new String[]{"renderChunksDeep", "field_72764_o"};
        @Deprecated public static final String[] RENDER_WORLD_RENDERS_TO_UPDATES = new String[]{"worldRenderersToUpdate", "field_72767_j"}; // 打包后不可用，域名不对
        public static final String[] CHUNKCACHE_CHUNKARRAY = new String[]{"chunkArray", "field_72817_c"};
        public static final String[] CHUNKCACHE_CHUNKX = new String[]{"chunkX", "field_72818_a"};
        public static final String[] CHUNKCACHE_CHUNKZ = new String[]{"chunkZ", "field_72816_b"};

        public static void relightBlock(Chunk aChunk, int aX, int aY, int aZ) {
            try {
                Method m = ReflectionHelper.findMethod(Chunk.class, null, CHUNK_RELIGHT_BLOCK, new Class[]{Integer.TYPE, Integer.TYPE, Integer.TYPE});
                m.invoke(aChunk, aX, aY, aZ);
            } catch (Exception e) {
                e.printStackTrace(ERR);
            }
        }

        public static void propagateSkylightOcclusion(Chunk aChunk, int aX, int aZ) {
            try {
                Method m = ReflectionHelper.findMethod(Chunk.class, null, CHUNK_PROPAGATE_SKY_LIGHT_OCCLUSION, new Class[]{Integer.TYPE, Integer.TYPE});
                m.invoke(aChunk, aX, aZ);
            } catch (Exception e) {
                e.printStackTrace(ERR);
            }
        }

        public static int computeLightValue(World aWorld, int aX, int aY, int aZ, EnumSkyBlock aEnumSkyBlock) {
            try {
                Method m = ReflectionHelper.findMethod(World.class, null, WORLD_COMPUTE_LIGHT_VALUE, new Class[]{Integer.TYPE, Integer.TYPE});
                return (int)m.invoke(aWorld, aX, aY, aZ, aEnumSkyBlock);
            } catch (Exception e) {
                e.printStackTrace(ERR);
                return 0;
            }
        }

        public static Chunk getChunk(ChunkCache aChunkCache, int aX, int aZ) {
            try {
                Chunk[][] tChunkArray = ReflectionHelper.getPrivateValue(ChunkCache.class, aChunkCache, CHUNKCACHE_CHUNKARRAY);
                int tChunkX = ReflectionHelper.getPrivateValue(ChunkCache.class, aChunkCache, CHUNKCACHE_CHUNKX);
                int tChunkZ = ReflectionHelper.getPrivateValue(ChunkCache.class, aChunkCache, CHUNKCACHE_CHUNKZ);
                return tChunkArray[(aX>>4) - tChunkX][(aZ>>4) - tChunkZ];
            } catch (Exception e) {
                e.printStackTrace(ERR);
                return null;
            }
        }

        @SuppressWarnings("rawtypes")
        public static List getWorldAccesses(World aWorld) {
            try {
                return ReflectionHelper.getPrivateValue(World.class, aWorld, WORLD_WORLD_ACCESSES);
            } catch (Exception e) {
                e.printStackTrace(ERR);
                return new ArrayList<>();
            }
        }

        @Deprecated // 弃用，减少反射使用来增加稳定性
        public static WorldRenderer getWorldRenderer(RenderGlobal aRender, int aX, int aY, int aZ) {
            try {
                WorldRenderer[] tWorldRenderers = ReflectionHelper.getPrivateValue(RenderGlobal.class, aRender, RENDER_WORLD_RENDERS);
                int tRenderChunksWide = ReflectionHelper.getPrivateValue(RenderGlobal.class, aRender, RENDER_RENDER_CHUNKS_WIDE);
                int tRenderChunksTall = ReflectionHelper.getPrivateValue(RenderGlobal.class, aRender, RENDER_RENDER_CHUNKS_TALL);
                int tRenderChunksDeep = ReflectionHelper.getPrivateValue(RenderGlobal.class, aRender, RENDER_RENDER_CHUNKS_DEEP);

                aX = MathHelper.bucketInt(aX, 16);
                aY = MathHelper.bucketInt(aY, 16);
                aZ = MathHelper.bucketInt(aZ, 16);
                aX %= tRenderChunksWide; if (aX < 0) aX += tRenderChunksWide;
                aY %= tRenderChunksTall; if (aY < 0) aY += tRenderChunksTall;
                aZ %= tRenderChunksDeep; if (aZ < 0) aZ += tRenderChunksDeep;

                return tWorldRenderers[(aZ * tRenderChunksTall + aY) * tRenderChunksWide + aX];
            } catch (Exception e) {
                e.printStackTrace(ERR);
                return null;
            }
        }

        @Deprecated // 弃用，减少反射使用来增加稳定性
        @SuppressWarnings("rawtypes")
        public static List getWorldRenderersToUpdate(RenderGlobal aRender) {
            try {
                return ReflectionHelper.getPrivateValue(RenderGlobal.class, aRender, RENDER_WORLD_RENDERS_TO_UPDATES);
            } catch (Exception e) {
                e.printStackTrace(ERR);
                return new ArrayList<>();
            }
        }
    }

    // 用于亮度更新的通用方法，只进行亮度更新但是不破坏方块
    public static class Light {
        // 尝试优化成能使用 CopyOnWrite 的形式
        // 世界某点的光源亮度发生变化后进行更新（和原版同样逻辑但是不考虑透光度的改变，如果透光度改变则需要再次调用透光度更新函数）
        public static void updateLightValue(World aWorld, int aX, int aY, int aZ) {
            // 非法输入检测
            if (aX < -30000000 || aZ < -30000000 || aX >= 30000000 || aZ >= 30000000 || aY < 0 || aY >= 256) return;

            Chunk tChunk = aWorld.getChunkFromBlockCoords(aX, aZ);

            // 改变亮度不会影响 sky 所以只用更新 block
            aWorld.updateLightByType(EnumSkyBlock.Block, aX, aY, aZ);

            // 和原版一样保持这个判断来进行优化
            if (tChunk.func_150802_k()) aWorld.markBlockRangeForRenderUpdate(aX, aY, aZ, aX, aY, aZ);

            tChunk.isModified = true;
        }

        // 世界某点的不透光度发生变化后进行更新（和原版同样逻辑但是不考虑光源亮度的变化，若亮度发生变化则需要再次调用光源亮度更新函数）
        public static void updateLightOpacity(int aOldOpacity, World aWorld, int aX, int aY, int aZ) {
            // 非法输入检测
            if (aX < -30000000 || aZ < -30000000 || aX >= 30000000 || aZ >= 30000000 || aY < 0 || aY >= 256) return;

            Chunk tChunk = aWorld.getChunkFromBlockCoords(aX, aZ);
            int tXChunk = aX & 15;
            int tZChunk = aZ & 15;
            // 由于不添加或删除方块，原则上不需要修改 precipitationHeightMap，heightMap，generateSkylightMap
            int tHeight = tChunk.getHeightValue(tXChunk, tZChunk);
            Block tBlock = aWorld.getBlock(aX, aY, aZ);
            int tOpacity = tBlock.getLightOpacity(aWorld, aX, aY, aZ);

            // 为了方便调用这里还是判断一下相同不透光度的情况
            if (tOpacity == aOldOpacity && (aOldOpacity != LIGHT_OPACITY_MAX)) return;

            // 还是需要 relight
            if (tOpacity > 0) {
                if (aY >= tHeight) {
                    Hack.relightBlock(tChunk, tXChunk, aY + 1, tZChunk);
                }
            } else
            if (aY == tHeight - 1) {
                Hack.relightBlock(tChunk, tXChunk, aY, tZChunk);
            }
            // 天空亮度会影响整个纵列
            if (tOpacity < aOldOpacity || tChunk.getSavedLightValue(EnumSkyBlock.Sky, tXChunk, aY, tZChunk) > 0) {
                Hack.propagateSkylightOcclusion(tChunk, tXChunk, tZChunk);
            }

            // 上面更新了 sky 所以只需要更新 block 即可
            aWorld.updateLightByType(EnumSkyBlock.Block, aX, aY, aZ);

            // 和原版一样保持这个判断来进行优化
            if (tChunk.func_150802_k()) aWorld.markBlockRangeForRenderUpdate(aX, aY, aZ, aX, aY, aZ);

            tChunk.isModified = true;
        }
        public static void updateLightOpacity(World aWorld, int aX, int aY, int aZ) {updateLightOpacity(LIGHT_OPACITY_MAX, aWorld, aX, aY, aZ);}

    }
}
