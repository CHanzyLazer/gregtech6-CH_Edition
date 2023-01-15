package gregtechCH.util;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregapi.GT_API;
import gregapi.util.UT;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.event.world.WorldEvent;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static gregapi.data.CS.*;
import static gregtech.interfaces.asm.LO_CH.*;
import static gregtechCH.config.ConfigForge.DATA_GTCH;
import static gregtechCH.threads.ThreadPools.RENDER_THREAD;

/**
 * @author CHanzy
 * Extension of WD
 */
public class WD_CH {
    @SideOnly(Side.CLIENT)
    public static void onTicking(long aTimer) {
        if (!DATA_GTCH.disableGTRerender) doChunkRerender(aTimer);
    }

    public static <WorldType> boolean isServerSide(WorldType aWorld) {
        return (aWorld instanceof World) ? (!((World)aWorld).isRemote) : cpw.mods.fml.common.FMLCommonHandler.instance().getEffectiveSide().isServer();
    }
    public static <WorldType> boolean isClientSide(WorldType aWorld) {
        return (aWorld instanceof World) ? ((World)aWorld).isRemote : cpw.mods.fml.common.FMLCommonHandler.instance().getEffectiveSide().isClient();
    }

    /* 区块渲染更新部分 */
    // 客户端世界加载或者卸载时清空渲染队列
    @SubscribeEvent public void onWorldLoad(WorldEvent.Load   aEvent) {if (aEvent.world.isRemote) clearStatic();}
    @SubscribeEvent public void onWorldUnload(WorldEvent.Unload aEvent) {if (aEvent.world.isRemote) clearStatic();}

    @SideOnly(Side.CLIENT)
    public static void doChunkRerender(long aTimer) {
        switch (DATA_GTCH.rerenderTickList[(int)(aTimer%DATA_GTCH.rerenderTickList.length)]) {
        case INIT:
            sPlayer = GT_API.api_proxy.getThePlayer();
            if (sPlayer!=null && sUpdated) {
                RENDER_THREAD.execute(new FormRenderList());
                sUpdated = F;
            }
            break;
        case MAIN:
            if (sPlayer!=null && RENDER_THREAD.getTaskNumber()==0) {
                int tRendCount = 0;
                while (!sMainChunkRenderList.isEmpty() && tRendCount<DATA_GTCH.rerenderChunkPerTick) {
                    sMainChunkRenderList.poll().run(); ++tRendCount;
                }
                sUpdated = T; // 周围更新不影响这个判断
            }
            break;
        case AROUND:
            if (sPlayer!=null && RENDER_THREAD.getTaskNumber()==0) {
                int tRendCount = 0;
                while (!sAroundChunkRenderList.isEmpty() && tRendCount<DATA_GTCH.rerenderChunkPerTick) {
                    sAroundChunkRenderList.poll().run(); ++tRendCount;
                }
            }
            break;
        case SLEEP:
            break;
        }
    }

    // 用于另一个线程调用，根据设置得到下次需要重新渲染的区块队列
    @SideOnly(Side.CLIENT)
    public static class FormRenderList implements Runnable {
        @Override public void run() {
            try {
                formMainChunkList();
                formAroundChunkList();
            } catch (Exception e) {
                e.printStackTrace(ERR);
            }
        }

        /* 玩家视觉的区块 */
        public void formMainChunkList() {
            synchronized(sMainChunkRenderList) {sMainChunkRenderList.clear();}
            // 用来构造的坐标 list
            LinkedList<ChunkCoordinates> tCoordList = new LinkedList<>();
            Vec3 tPlayerView = UT_CH.Code.getPlayerViewVec3(sPlayer);
            ChunkCoordinates tPlayerChunkCoord = UT_CH.Code.getPlayerChunkCoord(sPlayer);
            ChunkCoordinates tChunkCoord = new ChunkCoordinates(tPlayerChunkCoord);
            Vec3 tNextChunkCoord = tPlayerView.addVector(tChunkCoord.posX, tChunkCoord.posY, tChunkCoord.posZ);
            tCoordList.add(new ChunkCoordinates(tChunkCoord));
            int tAddIdx = 0;
            // 直接使用循环的方式添加，使用随机位置插入的方法来得到随机排序的 list
            while (tCoordList.size()<DATA_GTCH.rerenderMainLength) {
                tChunkCoord.posX = (int)Math.round(tNextChunkCoord.xCoord);
                tChunkCoord.posY = (int)Math.round(tNextChunkCoord.yCoord);
                tChunkCoord.posZ = (int)Math.round(tNextChunkCoord.zCoord);
                if (!tChunkCoord.equals(tCoordList.get(tAddIdx))) {
                    tAddIdx = RNGSUS.nextInt(tCoordList.size()+1);
                    tCoordList.add(tAddIdx, new ChunkCoordinates(tChunkCoord));
                }
                tNextChunkCoord.xCoord += tPlayerView.xCoord;
                tNextChunkCoord.yCoord += tPlayerView.yCoord;
                tNextChunkCoord.zCoord += tPlayerView.zCoord;
            }
            // 从中随机选取放入队列，需要满足要求并且记得移除主序列的元素
            while (sMainChunkRenderList.size()<DATA_GTCH.rerenderMainMaxChunk && !tCoordList.isEmpty()) {
                tChunkCoord = tCoordList.poll();
                Runnable tRender = checkAndGetRerender(tChunkCoord);
                if (tRender != null) synchronized(sMainChunkRenderList) {sMainChunkRenderList.add(tRender);}
            }
        }

        /* 玩家周边的区块 */
        public void formAroundChunkList() {
            synchronized(sAroundChunkRenderList) {sAroundChunkRenderList.clear();}
            // 用来构造的坐标 list
            LinkedList<ChunkCoordinates> tCoordList = new LinkedList<>();
            ChunkCoordinates tPlayerChunkCoord = UT_CH.Code.getPlayerChunkCoord(sPlayer);
            ChunkCoordinates tChunkCoord = new ChunkCoordinates(tPlayerChunkCoord);
            // 使用循环的方式添加，也使用随机位置插入的方法来得到随机排序的 list
            for (int tX = -DATA_GTCH.rerenderAroundLength; tX <= DATA_GTCH.rerenderAroundLength; ++tX)
            for (int tY = -DATA_GTCH.rerenderAroundLength; tY <= DATA_GTCH.rerenderAroundLength; ++tY)
            for (int tZ = -DATA_GTCH.rerenderAroundLength; tZ <= DATA_GTCH.rerenderAroundLength; ++tZ) {
                tChunkCoord.posX = tPlayerChunkCoord.posX + tX;
                tChunkCoord.posY = tPlayerChunkCoord.posY + tY;
                tChunkCoord.posZ = tPlayerChunkCoord.posZ + tZ;
                tCoordList.add(RNGSUS.nextInt(tCoordList.size()+1), new ChunkCoordinates(tChunkCoord));
            }
            // 从中随机选取放入队列，需要满足要求并且记得移除主序列的元素
            while (sAroundChunkRenderList.size()<DATA_GTCH.rerenderAroundMaxChunk && !tCoordList.isEmpty()) {
                tChunkCoord = tCoordList.poll();
                Runnable tRender = checkAndGetRerender(tChunkCoord);
                if (tRender != null) synchronized(sAroundChunkRenderList) {sAroundChunkRenderList.add(tRender);}
            }
        }
    }

    // 检测坐标是否需要重新渲染，不合理的返回 null，现在不再检测是否超时
    @SideOnly(Side.CLIENT)
    private static Runnable checkAndGetRerender(ChunkCoordinates aChunkCoordToRender) {
        if (!sChunkRenderList.containsKey(aChunkCoordToRender)) return null;
        Runnable tRender = sChunkRenderList.get(aChunkCoordToRender);
        synchronized(sChunkRenderList) {sChunkRenderList.remove(aChunkCoordToRender);}
        return tRender;
    }

    @SideOnly(Side.CLIENT)
    public static class ChunkRender implements Runnable {
        private final World mWorld;
        private final int mX, mY, mZ;
        public ChunkRender(World aWorld, int aX, int aY, int aZ) {
            mWorld = aWorld;
            mX = aX; mY = aY; mZ = aZ;
        }
        @Override public void run() {
            mWorld.markBlockRangeForRenderUpdate(mX, mY, mZ, mX, mY, mZ);
        }
    }

    // 使用 hashMap 来存储计划重新渲染的队列
    @SideOnly(Side.CLIENT) private static final Map<ChunkCoordinates, Runnable> sChunkRenderList = new HashMap<>();
    // 下次重新渲染的区块队列，分为主要影响视觉的区块和玩家周围的区块
    @SideOnly(Side.CLIENT) private static final LinkedList<Runnable> sMainChunkRenderList = new LinkedList<>();
    @SideOnly(Side.CLIENT) private static final LinkedList<Runnable> sAroundChunkRenderList = new LinkedList<>();
    // 记录玩家，保证玩家一致
    @SideOnly(Side.CLIENT) private static EntityPlayer sPlayer = null;
    // 记录是否成功更新，没有则不能刷新下次渲染队列
    @SideOnly(Side.CLIENT) private static boolean sUpdated = T;
    // 清空的接口
    @SideOnly(Side.CLIENT)
    private static void clearStatic() {
        sChunkRenderList.clear();
        sMainChunkRenderList.clear();
        sAroundChunkRenderList.clear();
        sPlayer = null;
        sUpdated = T;
    }

    // 标记方块（所在区块）用于计划重新渲染
    @SideOnly(Side.CLIENT)
    public static <WorldType> void markBlockForRerender(WorldType aWorld, int aX, int aY, int aZ, boolean aImmediate) {
        if (aWorld instanceof World) {
            ChunkCoordinates tCoord = new ChunkCoordinates(aX>>4, aY>>4, aZ>>4);
            synchronized(sChunkRenderList) {sChunkRenderList.put(tCoord, new ChunkRender((World)aWorld, aX, aY, aZ));}
            if (aImmediate) {
                Runnable tRender = checkAndGetRerender(tCoord);
                if (tRender != null) tRender.run();
            }
        }
    }

    // 用于客户端调用的更加优化的渲染更新
    public static void updateRender(IBlockAccess aWorld, int aX, int aY, int aZ) {updateRender(aWorld, aX, aY, aZ, DATA_GTCH.rerenderAll, T);}
    public static void updateRender(IBlockAccess aWorld, int aX, int aY, int aZ, boolean aImmediate, boolean aSound) {
        if (!DATA_GTCH.disableGTRerender && CODE_CLIENT && CLIENT_TIME > 100) markBlockForRerender(aWorld, aX, aY, aZ, aImmediate);
        if (aSound && CLIENT_BLOCKUPDATE_SOUNDS && CODE_CLIENT && CLIENT_TIME > 100) {
            EntityPlayer tPlayer = GT_API.api_proxy.getThePlayer();
            if (tPlayer != null && Math.abs(tPlayer.posX - aX) < 16 && Math.abs(tPlayer.posY - aY) < 16 && Math.abs(tPlayer.posZ - aZ) < 16) {
                UT.Sounds.play(SFX.MC_FIREWORK_LAUNCH, 1, 1.0F, 1.0F, aX, aY, aZ);
            }
        }
    }

    // 存储的 0-15 对应的不透光度值
    public static final Short[] LIGHT_OPACITY_ARRAY = {null, LIGHT_OPACITY_NONE, LIGHT_OPACITY_LEAVES, 2, LIGHT_OPACITY_WATER, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, LIGHT_OPACITY_MAX};
    // 不透光度对应的存储值
    public static byte getStoredValueLightOpacity(short aLightOpacity) {
        if (aLightOpacity >= 0 && aLightOpacity <= 13) return (byte)(aLightOpacity+1);
        if (aLightOpacity > 13) return 15;
        return 0;
    }
    // 方便的获取 GT 方块存储的不透光度的接口，没有存储则返回 null
    public static Short getBlockGTLightOpacity(IBlockAccess aWorld, int aX, int aY, int aZ) {
        // 没有开启 ASM 时一定获取不到不透光度
        if (!isEnableAsmBlockGtLightOpacity()) return null;
        // 非法输入检测
        if (aX < -30000000 || aZ < -30000000 || aX >= 30000000 || aZ >= 30000000 || aY < 0 || aY >= 256) return null;
        ExtendedBlockStorage tEBS = null;
        // 可能需要分情况讨论，一般都是这个最常见的 World 的情况
        if (aWorld instanceof World) {
            tEBS = ((World)aWorld).getChunkFromBlockCoords(aX, aZ).getBlockStorageArray()[aY>>4];
        } else
        // ChunkCache 的情况
        if (aWorld instanceof ChunkCache) {
            Chunk tChunk = UT_CH.Hack.getChunk((ChunkCache)aWorld, aX, aZ);
            if (tChunk != null) tEBS = tChunk.getBlockStorageArray()[aY>>4];
        }

        // 最后获取结果
        if (tEBS != null) {
            NibbleArray tNA = getLightOpacityNA(tEBS);
            if (tNA != null) return LIGHT_OPACITY_ARRAY[tNA.get(aX&15, aY&15, aZ&15)];
        }
        return null;
    }
    // 方便的设置 GT 方块不透光的接口，直接在 TE 中调用因此直接输入 World 即可，建议只在服务端调用
    public static void setBlockGTLightOpacity(World aWorld, int aX, int aY, int aZ, short aValue) {
        // 没有开启 ASM 时一定不能存储
        if (!isEnableAsmBlockGtLightOpacity()) return;
        // 非法输入检测
        if (aX < -30000000 || aZ < -30000000 || aX >= 30000000 || aZ >= 30000000 || aY < 0 || aY >= 256) return;
        // 执行存储
//        OUT.println("set LO at ("+aX+", "+aY+", "+aZ+") to "+aValue+" start"); // DEBUG

        byte tStoredValue = getStoredValueLightOpacity(aValue);
        ExtendedBlockStorage tEBS = aWorld.getChunkFromBlockCoords(aX, aZ).getBlockStorageArray()[aY>>4];
        if (tEBS == null) return; // 不知为何没有 EBS（没有方块或者其他情况，不处理）
        NibbleArray tNA = getLightOpacityNA(tEBS);
        if (tNA == null && tStoredValue == 0) return; // 没有数据并且设置值为 0 的情况，不需要进行存储
        if (tNA == null) {tNA = createLightOpacityNA(); initLightOpacityNA(tEBS, tNA);} // 如果不为零且没有数据，则需要初始化数据
        tNA.set(aX&15, aY&15, aZ&15, tStoredValue); // 存储数据

//        OUT.println("set LO at ("+aX+", "+aY+", "+aZ+") to "+aValue+" end"); // DEBUG
    }
    // 方便的重置 GT 方块的不透光度，用于在方块破坏时调用
    public static void resetBlockGTLightOpacity(World aWorld, int aX, int aY, int aZ) {
        // 没有开启 ASM 时一定不能存储
        if (!isEnableAsmBlockGtLightOpacity()) return;
        // 非法输入检测
        if (aX < -30000000 || aZ < -30000000 || aX >= 30000000 || aZ >= 30000000 || aY < 0 || aY >= 256) return;
        // 执行存储
//        OUT.println("reset LO at ("+aX+", "+aY+", "+aZ+") start"); // DEBUG

        ExtendedBlockStorage tEBS = aWorld.getChunkFromBlockCoords(aX, aZ).getBlockStorageArray()[aY>>4];
        if (tEBS == null) return; // 不知为何没有 EBS（没有方块或者其他情况，不处理）
        NibbleArray tNA = getLightOpacityNA(tEBS);
        if (tNA == null) return; // 没有数据的情况不需要进行重置
        tNA.set(aX&15, aY&15, aZ&15, 0); // 设为 0 来清除数据

//        OUT.println("reset LO at ("+aX+", "+aY+", "+aZ+") end"); // DEBUG
    }
}
