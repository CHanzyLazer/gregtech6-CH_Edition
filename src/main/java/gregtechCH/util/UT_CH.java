package gregtechCH.util;

import cpw.mods.fml.relauncher.ReflectionHelper;
import gregapi.oredict.OreDictMaterial;
import gregapi.oredict.OreDictPrefix;
import gregapi.render.BlockTextureDefault;
import gregapi.render.IIconContainer;
import gregapi.util.UT;
import net.minecraft.block.Block;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.lang.reflect.Method;
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

        public static <Entry> Entry adaptive_get(List<Entry> rList, int aIdx, Class<? extends Entry> aDefaultEntryClass) {
            if (aIdx >= rList.size()) resize(rList, aIdx+1, aDefaultEntryClass);
            return rList.get(aIdx);
        }

    }

    public static class Code {
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
        // 直接将 RGB 混合，相比原本可以指定后一个 RGB 的占比权重，并且使用向上整除来保证一定能够达到后一个颜色
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
        public static final String[] WORLD_COMPUTE_LIGHT_VALUE = new String[]{"computeLightValue"};

        public static void relightBlock(Chunk aChunk, int aX, int aY, int aZ) {
            try {
                Method m = ReflectionHelper.findMethod(Chunk.class, null, CHUNK_RELIGHT_BLOCK, new Class[]{Integer.TYPE, Integer.TYPE, Integer.TYPE});
                m.invoke(aChunk, aX, aY, aZ);
            } catch (Exception var3) {
                var3.printStackTrace();
            }

        }

        public static void propagateSkylightOcclusion(Chunk aChunk, int aX, int aZ) {
            try {
                Method m = ReflectionHelper.findMethod(Chunk.class, null, CHUNK_PROPAGATE_SKY_LIGHT_OCCLUSION, new Class[]{Integer.TYPE, Integer.TYPE});
                m.invoke(aChunk, aX, aZ);
            } catch (Exception var3) {
                var3.printStackTrace();
            }
        }

        public static int computeLightValue(World aWorld, int aX, int aY, int aZ, EnumSkyBlock aEnumSkyBlock) {
            try {
                Method m = ReflectionHelper.findMethod(World.class, null, WORLD_COMPUTE_LIGHT_VALUE, new Class[]{Integer.TYPE, Integer.TYPE});
                return (int)m.invoke(aWorld, aX, aY, aZ, aEnumSkyBlock);
            } catch (Exception var3) {
                var3.printStackTrace();
                return 0;
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
