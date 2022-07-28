package gregtechCH.util;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregapi.GT_API;
import gregapi.util.UT;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static gregapi.data.CS.*;
import static gregtechCH.threads.ThreadPools.RENDER_THREAD;
import static gregtechCH.config.ConfigForge_CH.*;

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
            sPlayer = Minecraft.getMinecraft().thePlayer;
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
        private final RenderGlobal mRenderGlobal;
        private final int mX, mY, mZ;
        public ChunkRender(RenderGlobal aRenderGlobal, int aX, int aY, int aZ) {
            mRenderGlobal = aRenderGlobal;
            mX = aX; mY = aY; mZ = aZ;
        }
        @Override public void run() {
            mRenderGlobal.markBlocksForUpdate(mX, mY, mZ, mX, mY, mZ);
        }
    }

    // 使用 hashMap 来存储计划重新渲染的队列
    @SideOnly(Side.CLIENT) private static final Map<ChunkCoordinates, Runnable> sChunkRenderList = new HashMap<>();
    // 下次重新渲染的区块队列，分为主要影响视觉的区块和玩家周围的区块
    @SideOnly(Side.CLIENT) private static final LinkedList<Runnable> sMainChunkRenderList = new LinkedList<>();
    @SideOnly(Side.CLIENT) private static final LinkedList<Runnable> sAroundChunkRenderList = new LinkedList<>();
    // 记录玩家，保证玩家一致
    @SideOnly(Side.CLIENT) private static EntityClientPlayerMP sPlayer = null;
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
        if (aWorld instanceof World) for (Object tWorldObj : UT_CH.Hack.getWorldAccesses((World)aWorld)) if (tWorldObj instanceof RenderGlobal) {
            ChunkCoordinates tCoord = new ChunkCoordinates(aX>>4, aY>>4, aZ>>4);
            synchronized(sChunkRenderList) {sChunkRenderList.put(tCoord, new ChunkRender((RenderGlobal)tWorldObj, aX, aY, aZ));}
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

    // （弃用，过于麻烦，改用只标记 dirty 但不添加更新来优化）暂定的客户端调用的仅单方块渲染更新
//    @SideOnly(Side.CLIENT)
//    public static void updateBlockRenderer(IBlockAccess aWorld, int aX, int aY, int aZ) {
//        ChunkCache tChunkCache = new ChunkCache((World)aWorld, aX, aY, aZ, aX, aY, aZ, 1);
//        RenderBlocks tRenderBlocks = new RenderBlocks(tChunkCache);
//        Block block = tChunkCache.getBlock(aX, aY, aZ);

//        for (Object tWorldObj : UT_CH.Hack.getWorldAccesses((World)aWorld)) if (tWorldObj instanceof RenderGlobal) {
//            WorldRenderer tWorldRenderer = UT_CH.Hack.getWorldRenderer(((RenderGlobal)tWorldObj), aX, aY, aZ);
//            if (tWorldRenderer != null) {
//                if (tWorldRenderer.needsUpdate) continue; // 如果需要更新则走原版的更新
//                tWorldRenderer.updateRenderer(Minecraft.getMinecraft().thePlayer);
//                net.minecraftforge.client.ForgeHooksClient.setWorldRendererRB(tRenderBlocks);
//
//                for (int tPass = 0; tPass < 2; ++tPass) {
//                    GL11.glPushMatrix();
//                    GL11.glTranslatef(tWorldRenderer.posXClip, tWorldRenderer.posYClip, tWorldRenderer.posZClip);
//                    float tScale = 1.000001F;
//                    GL11.glTranslatef(-8.0F, -8.0F, -8.0F);
//                    GL11.glScalef(tScale, tScale, tScale);
//                    GL11.glTranslatef(8.0F, 8.0F, 8.0F);
//                    net.minecraftforge.client.ForgeHooksClient.onPreRenderWorld(tWorldRenderer, tPass);
//                    Tessellator.instance.startDrawingQuads();
//                    Tessellator.instance.setTranslation((double)(-tWorldRenderer.posX), (double)(-tWorldRenderer.posY), (double)(-tWorldRenderer.posZ));
//
//                    FMLRenderAccessLibrary.renderWorldBlock(tRenderBlocks, aWorld, aX, aY, aZ, block, block.getRenderType());
//
//                    EntityLivingBase tPlayer = Minecraft.getMinecraft().thePlayer;
//                    if (tPass == 1 && tPlayer != null) Tessellator.instance.addVertex((float)tPlayer.posX, (float)tPlayer.posY, (float)tPlayer.posZ);
//                    Tessellator.instance.draw();
//                    net.minecraftforge.client.ForgeHooksClient.onPostRenderWorld(tWorldRenderer, tPass);
//                    GL11.glPopMatrix();
//                    Tessellator.instance.setTranslation(0.0D, 0.0D, 0.0D);
//                }
//                net.minecraftforge.client.ForgeHooksClient.setWorldRendererRB(null);
//            }
//        }
//    }
}
