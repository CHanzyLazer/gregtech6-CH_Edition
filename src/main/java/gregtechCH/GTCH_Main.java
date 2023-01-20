package gregtechCH;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregapi.code.ArrayListNoNulls;
import gregapi.tileentity.ITileEntityErrorable;
import gregapi.tileentity.ITileEntityServerTickPre;
import gregtechCH.config.ConfigJson;
import gregtechCH.data.CS_CH;
import gregtechCH.threads.GroupTaskPool;
import gregtechCH.tileentity.IMTEScheduledUpdate_CH;
import gregtechCH.tileentity.IMTEServerTickParallel;
import gregtechCH.tileentity.compat.PipeCompat;
import gregtechCH.util.WD_CH;

import java.util.*;

import static gregapi.data.CS.*;
import static gregtechCH.threads.ThreadPools.TICK_THREAD;

/**
 * @author CHanzy
 * Main class of GTCH
 */
public class GTCH_Main {
    public static String getModNameForLog() {return "GTCH";}
    
    // 几个初始化阶段的 hook
    public static void preInit() {
        OUT.println(getModNameForLog() + ": ======================");
        OUT.println(getModNameForLog() + ": PreInit-Phase started!");
        
        ConfigJson.initJsonFile();
        
        OUT.println(getModNameForLog() + ": PreInit-Phase finished!");
        OUT.println(getModNameForLog() + ": ======================");
    }
    
    public static void init() {
        OUT.println(getModNameForLog() + ": ======================");
        OUT.println(getModNameForLog() + ": Init-Phase started!");
    
        CS_CH.initCS_CH();
        ConfigJson.readJsonFile();
        
        OUT.println(getModNameForLog() + ": Init-Phase finished!");
        OUT.println(getModNameForLog() + ": ======================");
    }
    
    public static void postInit() {
        OUT.println(getModNameForLog() + ": ======================");
        OUT.println(getModNameForLog() + ": PostInit-Phase started!");
        
        ConfigJson.readJsonFilePost();
        PipeCompat.checkAvailabilities();
        
        OUT.println(getModNameForLog() + ": PostInit-Phase finished!");
        OUT.println(getModNameForLog() + ": ======================");
    }
    
    // 重写了原本的更新 tick，用于实现在每 tick 更新时进行调用内部函数。全大写代表不能随便调用
    public static void RESET_SERVER_TIME() {
        SERVER_TIME = 0;
    }
    public static void UPDATE_CLIENT_TIME() {
        if (CLIENT_TIME > 10) {
            doScheduled(F);
            // 在这里调用 WD_CH 的 ticking，主要是区块渲染的更新
            WD_CH.onTicking(CLIENT_TIME);
        }
        ++CLIENT_TIME;
    }
    public static void UPDATE_SERVER_TIME() {
        if (SERVER_TIME > 10) doScheduled(T);
        ++SERVER_TIME;
    }
    
    // 实现对于一些机器强制并行 tick
    private static class TETickRun extends GroupTaskPool.CountRunnable {
        private final IMTEServerTickParallel mTE;
        private final boolean mFirst;
        private boolean mError = F;
        public TETickRun(IMTEServerTickParallel aTE, boolean aFirst) {TICK_THREAD.super(); mTE = aTE; mFirst = aFirst;}
        public boolean isDead() {return mError || mTE.isDead();}
        public void onRemove() {if (!mError) mTE.onUnregisterPar();}
        @Override
        public void run2() {mTE.onServerTickPar(mFirst);}
        @Override
        public void doCatch(Throwable e) {
            mError = T;
            mTE.setError(mFirst?"Server Tick Pre 1 - ":"Server Tick Pre 2 - " + e);
            synchronized (ERR) {e.printStackTrace(ERR);}
        }
        // 重写 hashCode 以及 equals 来方便移除
        @Override public int hashCode() {return mTE.hashCode();}
        @Override
        public boolean equals(Object o) {
            if (this == o) return T;
            if (o == null) return F;
            if (o instanceof TETickRun) return mTE.equals(((TETickRun)o).mTE);
            return F;
        }
    }
    private static final Set<TETickRun> TE_SERVER_TICK_PAR  = new LinkedHashSet<>(), TE_SERVER_TICK_PA2  = new LinkedHashSet<>();
    // 添加实体到并行 tick 的队列中，使用此方法进行 tick 会高度并行，一定要注意线程安全
    public static void addToServerTickParallel(IMTEServerTickParallel aTE) {
        synchronized (TE_SERVER_TICK_PAR) {TE_SERVER_TICK_PAR.add(new TETickRun(aTE, T));}
    }
    public static void addToServerTickParallel2(IMTEServerTickParallel aTE) {
        synchronized (TE_SERVER_TICK_PA2) {TE_SERVER_TICK_PA2.add(new TETickRun(aTE, F));}
    }
    public static void removeFromServerTickParallel(IMTEServerTickParallel aTE) {
        synchronized (TE_SERVER_TICK_PAR) {TE_SERVER_TICK_PAR.remove(new TETickRun(aTE, T));}
    }
    public static void removeFromServerTickParallel2(IMTEServerTickParallel aTE) {
        synchronized (TE_SERVER_TICK_PA2) {TE_SERVER_TICK_PA2.remove(new TETickRun(aTE, F));}
    }
    
    // 专门的 server tick 方法，在 pre 之后，在 post 之前，用来将耗时部分专门并行处理
    public static void SERVER_TICK() {
        // 先遍历删除非法的实体
        Iterator<TETickRun>
        tIt = TE_SERVER_TICK_PAR.iterator();
        while (tIt.hasNext()) {
            TETickRun tTickRun = tIt.next();
            if (tTickRun.isDead()) {
                tIt.remove();
                tTickRun.onRemove();
            }
        }
        // 直接执行，会自动等待全部执行完成
        TICK_THREAD.runAll(TE_SERVER_TICK_PAR);
        
        // 先遍历删除非法的实体
        tIt = TE_SERVER_TICK_PA2.iterator();
        while (tIt.hasNext()) {
            TETickRun tTickRun = tIt.next();
            if (tTickRun.isDead()) {
                tIt.remove();
                tTickRun.onRemove();
            }
        }
        // 直接执行，会自动等待全部执行完成
        TICK_THREAD.runAll(TE_SERVER_TICK_PA2);
    }
    
    // 实现自用的实体计划任务 api，服务端和客户端通用
    // 不使用 greg 自带的用来防止相互干扰
    // 使用 Set 来支持自动排除已有的计划
    // 客户端和服务端应该必须手动使用的不同的数据（java 并行不是 mpi）
    private final static LinkedList<Set<IMTEScheduledUpdate_CH>> TE_SCHEDULED_UPDATERS_LIST_SERVER = new LinkedList<>();
    private final static LinkedList<Set<IMTEScheduledUpdate_CH>> TE_SCHEDULED_UPDATERS_LIST_CLIENT = new LinkedList<>();
    // 添加计划，为了让 LinkedList 有作用，这里再提供一个可以选择延迟 tick 数目的接口
    public static void pushScheduled(boolean aIsServerSide, IMTEScheduledUpdate_CH aScheduleUpdater) {pushScheduled(aIsServerSide, aScheduleUpdater, 0);}
    public static void pushScheduled(boolean aIsServerSide, IMTEScheduledUpdate_CH aScheduleUpdater, int aLate) {
        aLate = Math.max(aLate, 0);
        LinkedList<Set<IMTEScheduledUpdate_CH>> tUpdatersList = aIsServerSide?TE_SCHEDULED_UPDATERS_LIST_SERVER:TE_SCHEDULED_UPDATERS_LIST_CLIENT;
        synchronized(aIsServerSide?TE_SCHEDULED_UPDATERS_LIST_SERVER:TE_SCHEDULED_UPDATERS_LIST_CLIENT) {
            while (tUpdatersList.size() < aLate+1) tUpdatersList.addLast(new LinkedHashSet<>()); // 如果长度不够则遍历增加
            tUpdatersList.get(aLate).add(aScheduleUpdater);
        }
    }
    // 注意每 tick 只能调用一次
    private static void doScheduled(boolean aIsServerSide) {
        // 先提取本 tick 需要执行的计划，然后将其移除出总 LIST
        Set<IMTEScheduledUpdate_CH> tScheduledUpdatersDo;
        synchronized(aIsServerSide?TE_SCHEDULED_UPDATERS_LIST_SERVER:TE_SCHEDULED_UPDATERS_LIST_CLIENT) {
            tScheduledUpdatersDo = (aIsServerSide?TE_SCHEDULED_UPDATERS_LIST_SERVER:TE_SCHEDULED_UPDATERS_LIST_CLIENT).pollFirst();
        }
        if (tScheduledUpdatersDo == null) return;
        // 直接遍历执行，计划不需要并行
        for (IMTEScheduledUpdate_CH tTileEntity : tScheduledUpdatersDo) {
            try {
                tTileEntity.onScheduledUpdate_CH(aIsServerSide);
            } catch(Throwable e) {
                if (tTileEntity instanceof ITileEntityErrorable) ((ITileEntityErrorable)tTileEntity).setError("GTCH: Scheduled TileEntity Update - " + e);
                e.printStackTrace(ERR);
            }
        }
    }
}
