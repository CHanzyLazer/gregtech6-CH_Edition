package gregtechCH.util;

import gregapi.oredict.OreDictMaterial;
import gregapi.render.BlockTextureDefault;
import gregapi.render.IIconContainer;
import gregapi.util.UT;

import static gregtechCH.config.ConfigForge_CH.*;
import static gregapi.data.CS.*;

public class UT_CH {
    public static class Code {
        public final static float RENDER_LENGTH = 0.01F;
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
        public static void RGB2HSV(float[] aRGB, float[] rHSV) {
            float tMax = Math.max(aRGB[0], Math.max(aRGB[1], aRGB[2]));
            float tMin = Math.min(aRGB[0], Math.min(aRGB[1], aRGB[2]));
            float tDelta = tMax - tMin;
            if (tMax == tMin) {
                rHSV[0] = 0.0F;
            } else
            if (tMax == aRGB[0]) {
                rHSV[0] = (((aRGB[1] - aRGB[2])/tDelta) % 6.0F)/6.0F;
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
        public static void HSV2RGB(float[] rRGB, float[] aHSV) {
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
            } else
            if (aHSV[0]*6.0F <= 6.0F) {
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

        // 将 paint 混合颜色的方法放在这里方便一起修改
        public static int getPaintRGB(int aRGBOrigin, int aRGBPaint) {
            // 放弃复杂的算法，直接 RGB 混合效果较好
            return getMixRGBInt(aRGBOrigin, aRGBPaint, DATA_GTCH.mixRatio);
        }

        // 直接将 RGB 混合，相比原本可以指定后一个 RGB 的占比权重
        public static int getMixRGBInt(int aRGB1, int aRGB2, float aWeight) {
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

        // 将两个 RGB 按照 HSV 来混合，并且可以指定后一个 RGB 的 HSV 占比权重
        @Deprecated
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

}
