package gregtechCH.threads;

import cpw.mods.fml.relauncher.ReflectionHelper;
import gregtechCH.util.UT_CH;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Facing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

import java.util.Map;
import java.util.TreeMap;

// TODO 未完成，暂抛弃
@Deprecated
public class CopyOnWrite {
    // 改为线程局部变量
    private static final ThreadLocal<int[]> sLightUpdateBlockList = new ThreadLocal<int[]>() {@Override public int[] initialValue() {return new int[32768];}};

    // 实现区块数据的拷贝方法，需要加锁
    public synchronized static NibbleArray copyNibbleArray(NibbleArray aNibbleArray) {
        NibbleArray tNibbleArray = new NibbleArray(new byte[aNibbleArray.data.length], (int)ReflectionHelper.getPrivateValue(NibbleArray.class, aNibbleArray, "depthBits"));
        System.arraycopy(aNibbleArray.data, 0, tNibbleArray.data, 0, aNibbleArray.data.length);
        return tNibbleArray;
    }

    // 专用的用于实现 copyOnWrite 的光照数值缓存
    private static class LightArrayBuffer {
        private final World mWorld; private final boolean mIsSkyLight;
        private final Map<ChunkCoordinates, NibbleArray> mLightArrayMap = new TreeMap<>();
        public LightArrayBuffer(World aWorld, EnumSkyBlock aEnumSkyBlock) {mWorld = aWorld; mIsSkyLight = (aEnumSkyBlock == EnumSkyBlock.Sky);}
        // TODO 需要一个 get 方法来获取自己已有的更新

        // 将光照值设置到 buffer 中
        public void setLightValue(int aX, int aY, int aZ, int aL) {
            // 非法输入检测
            if (aX < -30000000 || aZ < -30000000 || aX >= 30000000 || aZ >= 30000000 || aY < 0 || aY >= 256) return;
            ChunkCoordinates tKey = new ChunkCoordinates(aX>>4, aY>>4, aZ>>4);
            if (!mLightArrayMap.containsKey(tKey)) {
                ExtendedBlockStorage tExBS = mWorld.getChunkFromChunkCoords(tKey.posX, tKey.posZ).getBlockStorageArray()[tKey.posY];
                // 没有的情况走原版的创建函数避免一些硬编码，但是可以不用拷贝数据
                if (tExBS == null) {
                    tExBS = new ExtendedBlockStorage(tKey.posY << 4, !mWorld.provider.hasNoSky);
                    mLightArrayMap.put(tKey, mIsSkyLight?tExBS.getSkylightArray():tExBS.getBlocklightArray());
                } else {
                    mLightArrayMap.put(tKey, copyNibbleArray(mIsSkyLight?tExBS.getSkylightArray():tExBS.getBlocklightArray()));
                }
            }
            mLightArrayMap.get(tKey).set(aX%15, aY%15, aZ%15, aL);
        }
        // 统一将光照值倒入公共变量中，需要加锁
        public synchronized void putLightValue() {
            for (Map.Entry<ChunkCoordinates, NibbleArray> tEntry : mLightArrayMap.entrySet()) {
                ChunkCoordinates tKey = tEntry.getKey();
                Chunk tChunk= mWorld.getChunkFromChunkCoords(tKey.posX, tKey.posZ);
                ExtendedBlockStorage tExBS = tChunk.getBlockStorageArray()[tKey.posY];
                // 没有的情况需要新建，并且需要初始化
                if (tExBS == null) {
                    tExBS = tChunk.getBlockStorageArray()[tKey.posY] = new ExtendedBlockStorage(tKey.posY << 4, !mWorld.provider.hasNoSky);
                    tChunk.generateSkylightMap();
                }
                // 需要使用反射来修改其值
                ReflectionHelper.setPrivateValue(ExtendedBlockStorage.class, tExBS, tEntry.getValue(), mIsSkyLight?"skylightArray":"blocklightArray");
            }
        }
    }

    public static boolean updateLightByType(World aWorld, EnumSkyBlock aEnumSkyBlock, int aX, int aY, int aZ) {
        if (!aWorld.doChunksNearChunkExist(aX, aY, aZ, 17)) {
            return false;
        }
        else {
            CopyOnWrite.LightArrayBuffer tLightArrayBuffer = new CopyOnWrite.LightArrayBuffer(aWorld, aEnumSkyBlock);
            int tPreIdx = 0;
            int tCenterIdx = 0;
            // getBrightness
            int tCenterOldLight = aWorld.getSavedLightValue(aEnumSkyBlock, aX, aY, aZ);
            int tCenterNewLight = UT_CH.Hack.computeLightValue(aWorld, aX, aY, aZ, aEnumSkyBlock);
            int tLZYX;
            int tX;
            int tY;
            int tZ;
            int tL;
            int tPreOldLight;
            int tDisX;
            int tDisY;
            int tDisZ;

            if (tCenterNewLight > tCenterOldLight) {
                CopyOnWrite.sLightUpdateBlockList.get()[tCenterIdx++] = 133152;
            }
            else if (tCenterNewLight < tCenterOldLight) {
                CopyOnWrite.sLightUpdateBlockList.get()[tCenterIdx++] = 133152 | tCenterOldLight << 18;

                while (tPreIdx < tCenterIdx) {
                    tLZYX = CopyOnWrite.sLightUpdateBlockList.get()[tPreIdx++];
                    tX = (tLZYX & 63) - 32 + aX;
                    tY = (tLZYX >> 6 & 63) - 32 + aY;
                    tZ = (tLZYX >> 12 & 63) - 32 + aZ;
                    tL = tLZYX >> 18 & 15;
                    tPreOldLight = aWorld.getSavedLightValue(aEnumSkyBlock, tX, tY, tZ);

                    if (tPreOldLight == tL) {
                        tLightArrayBuffer.setLightValue(tX, tY, tZ, 0);
                        if (tL > 0) {
                            tDisX = MathHelper.abs_int(tX - aX);
                            tDisY = MathHelper.abs_int(tY - aY);
                            tDisZ = MathHelper.abs_int(tZ - aZ);

                            if (tDisX + tDisY + tDisZ < 17) {
                                for (int tSide = 0; tSide < 6; ++tSide) {
                                    int tNewX = tX + Facing.offsetsXForSide[tSide];
                                    int tNewY = tY + Facing.offsetsYForSide[tSide];
                                    int tNewZ = tZ + Facing.offsetsZForSide[tSide];
                                    int tDecay = Math.max(1, aWorld.getBlock(tNewX, tNewY, tNewZ).getLightOpacity(aWorld, tNewX, tNewY, tNewZ));
                                    tPreOldLight = aWorld.getSavedLightValue(aEnumSkyBlock, tNewX, tNewY, tNewZ);

                                    if (tPreOldLight == tL - tDecay && tCenterIdx < CopyOnWrite.sLightUpdateBlockList.get().length) {
                                        CopyOnWrite.sLightUpdateBlockList.get()[tCenterIdx++] = tNewX - aX + 32 | tNewY - aY + 32 << 6 | tNewZ - aZ + 32 << 12 | tL - tDecay << 18;
                                    }
                                }
                            }
                        }
                    }
                }
                tPreIdx = 0;
            }

            // checkedPosition < toCheckCount

            while (tPreIdx < tCenterIdx) {
                tLZYX = CopyOnWrite.sLightUpdateBlockList.get()[tPreIdx++];
                tX = (tLZYX & 63) - 32 + aX;
                tY = (tLZYX >> 6 & 63) - 32 + aY;
                tZ = (tLZYX >> 12 & 63) - 32 + aZ;
                tL = aWorld.getSavedLightValue(aEnumSkyBlock, tX, tY, tZ);
                tPreOldLight = UT_CH.Hack.computeLightValue(aWorld, tX, tY, tZ, aEnumSkyBlock);

                if (tPreOldLight != tL) {
                    tLightArrayBuffer.setLightValue(tX, tY, tZ, tPreOldLight);
                    // TODO 可能要补上缺失的 mark rander 的部分

                    if (tPreOldLight > tL) {
                        tDisX = Math.abs(tX - aX);
                        tDisY = Math.abs(tY - aY);
                        tDisZ = Math.abs(tZ - aZ);
                        boolean flag = tCenterIdx < CopyOnWrite.sLightUpdateBlockList.get().length - 6;

                        if (tDisX + tDisY + tDisZ < 17 && flag) {
                            if (aWorld.getSavedLightValue(aEnumSkyBlock, tX - 1, tY, tZ) < tPreOldLight) {
                                CopyOnWrite.sLightUpdateBlockList.get()[tCenterIdx++] = tX - 1 - aX + 32 + (tY - aY + 32 << 6) + (tZ - aZ + 32 << 12);
                            }
                            if (aWorld.getSavedLightValue(aEnumSkyBlock, tX + 1, tY, tZ) < tPreOldLight) {
                                CopyOnWrite.sLightUpdateBlockList.get()[tCenterIdx++] = tX + 1 - aX + 32 + (tY - aY + 32 << 6) + (tZ - aZ + 32 << 12);
                            }
                            if (aWorld.getSavedLightValue(aEnumSkyBlock, tX, tY - 1, tZ) < tPreOldLight) {
                                CopyOnWrite.sLightUpdateBlockList.get()[tCenterIdx++] = tX - aX + 32 + (tY - 1 - aY + 32 << 6) + (tZ - aZ + 32 << 12);
                            }
                            if (aWorld.getSavedLightValue(aEnumSkyBlock, tX, tY + 1, tZ) < tPreOldLight) {
                                CopyOnWrite.sLightUpdateBlockList.get()[tCenterIdx++] = tX - aX + 32 + (tY + 1 - aY + 32 << 6) + (tZ - aZ + 32 << 12);
                            }
                            if (aWorld.getSavedLightValue(aEnumSkyBlock, tX, tY, tZ - 1) < tPreOldLight) {
                                CopyOnWrite.sLightUpdateBlockList.get()[tCenterIdx++] = tX - aX + 32 + (tY - aY + 32 << 6) + (tZ - 1 - aZ + 32 << 12);
                            }
                            if (aWorld.getSavedLightValue(aEnumSkyBlock, tX, tY, tZ + 1) < tPreOldLight) {
                                CopyOnWrite.sLightUpdateBlockList.get()[tCenterIdx++] = tX - aX + 32 + (tY - aY + 32 << 6) + (tZ + 1 - aZ + 32 << 12);
                            }
                        }
                    }
                }
            }
            tLightArrayBuffer.putLightValue();
            return true;
        }
    }
}
