package gregtechCH;

import gregapi.tileentity.ITileEntityErrorable;
import gregtechCH.config.ConfigJson_CH;
import gregtechCH.threads.ThreadPools.ITaskNumberExecutor;
import gregtechCH.tileentity.ITEScheduledUpdate_CH;
import gregtechCH.tileentity.compat.PipeCompat_CH;
import gregtechCH.util.WD_CH;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

import static gregapi.data.CS.*;

/**
 * @author CHanzy
 *
 * Main class of GTCH
**/
public class GTCH_Main {
    public static String getModNameForLog() {return "GTCH";}

    // 几个初始化阶段的 hook
    public static void preInit() {
        OUT.println(getModNameForLog() + ": ======================");
        OUT.println(getModNameForLog() + ": PreInit-Phase started!");

        ConfigJson_CH.initJsonFile();

        OUT.println(getModNameForLog() + ": PreInit-Phase finished!");
        OUT.println(getModNameForLog() + ": ======================");
    }

    public static void init() {
        OUT.println(getModNameForLog() + ": ======================");
        OUT.println(getModNameForLog() + ": Init-Phase started!");

        ConfigJson_CH.readJsonFile();

        OUT.println(getModNameForLog() + ": Init-Phase finished!");
        OUT.println(getModNameForLog() + ": ======================");
    }

    public static void postInit() {
        OUT.println(getModNameForLog() + ": ======================");
        OUT.println(getModNameForLog() + ": PostInit-Phase started!");

        ConfigJson_CH.readJsonFilePost();
        PipeCompat_CH.checkAvailabilities();

        OUT.println(getModNameForLog() + ": PostInit-Phase finished!");
        OUT.println(getModNameForLog() + ": ======================");
    }

    // 重写了原本的更新 tick，用于实现在每 tick 更新时进行调用内部函数。全大写代表不能随便调用
    public static void RESET_SERVER_TIME() {
        sCurrentHandlesServer = 0;
        SERVER_TIME = 0;
    }
    public static void UPDATE_SERVER_TIME() {
        if (SERVER_TIME > 10) doScheduled(T);
        ++SERVER_TIME;
        sCurrentHandlesServer = 0;
    }
    public static void UPDATE_CLIENT_TIME() {
        if (CLIENT_TIME > 10) {
            doScheduled(F);
            // 在这里调用 WD_CH 的 ticking，主要是区块渲染的更新
            WD_CH.onTicking(CLIENT_TIME);
        }
        ++CLIENT_TIME;
        sCurrentHandlesClient = 0;
    }

    // 实现自用的实体计划任务 api，服务端和客户端通用
    // 不使用 greg 自带的用来防止相互干扰
    // 使用 Set 来支持自动排除已有的计划
    // 客户端和服务端应该必须手动使用的不同的数据（java 并行不是 mpi）
    private final static LinkedList<Set<ITEScheduledUpdate_CH>> TE_SCHEDULED_UPDATERS_LIST_SERVER = new LinkedList<>();
    private final static LinkedList<Set<ITEScheduledUpdate_CH>> TE_SCHEDULED_UPDATERS_LIST_CLIENT = new LinkedList<>();
    public static void pushScheduled(boolean aIsServerSide, ITEScheduledUpdate_CH aScheduleUpdater) {
        LinkedList<Set<ITEScheduledUpdate_CH>> tUpdatersList = aIsServerSide?TE_SCHEDULED_UPDATERS_LIST_SERVER:TE_SCHEDULED_UPDATERS_LIST_CLIENT;
        synchronized(aIsServerSide?TE_SCHEDULED_UPDATERS_LIST_SERVER:TE_SCHEDULED_UPDATERS_LIST_CLIENT) {
            if (tUpdatersList.isEmpty()) tUpdatersList.addLast(new LinkedHashSet<ITEScheduledUpdate_CH>()); // 不再限制每 tick 的任务数
            tUpdatersList.getLast().add(aScheduleUpdater);
        }
    }
    // 注意每 tick 只能调用一次
    // 加锁防止并行修改队列
    private static void doScheduled(boolean aIsServerSide) {
        // 先提取本 tick 需要执行的计划，然后将其移除出总 LIST
        Set<ITEScheduledUpdate_CH> tScheduledUpdatersDo;
        synchronized(aIsServerSide?TE_SCHEDULED_UPDATERS_LIST_SERVER:TE_SCHEDULED_UPDATERS_LIST_CLIENT) {
            tScheduledUpdatersDo = (aIsServerSide?TE_SCHEDULED_UPDATERS_LIST_SERVER:TE_SCHEDULED_UPDATERS_LIST_CLIENT).pollFirst();
        }
        if (tScheduledUpdatersDo == null) return;
        // 遍历执行
        for (ITEScheduledUpdate_CH tTileEntity : tScheduledUpdatersDo) {
            try {
                tTileEntity.onScheduledUpdate_CH(aIsServerSide);
            } catch(Throwable e) {
                if (tTileEntity instanceof ITileEntityErrorable) ((ITileEntityErrorable)tTileEntity).setError("GTCH: Scheduled TileEntity Update - " + e);
                e.printStackTrace(ERR);
            }
        }
    }

    // 用于实现限制每 tick 的函数运行次数
    private static final int MIN_HANDLES_SERVER = 1;
    private static final int MIN_HANDLES_CLIENT = 1;
    private static final int MAX_HANDLES_SERVER = 32;
    private static final int MAX_HANDLES_CLIENT = 32;
    private static int sCurrentHandlesServer = 0;
    private static int sCurrentHandlesClient = 0;
    private static final int MAX_TASK_SERVER = 16; // 并行 Executor 最多拥有的队列任务数目，优先级最高，避免队列过长
    private static final int MAX_TASK_CLIENT = 16;
    public static void pushHandle(boolean aIsServerSide, @NotNull ITaskNumberExecutor aExecutor, @NotNull Runnable aRunnable) {
        if (aIsServerSide) ++sCurrentHandlesServer; else ++sCurrentHandlesClient;
        aExecutor.execute(aRunnable);
    }
    // 如果本 tick 花费过多时间并且已经达到了最低需要的任务数则不能继续提交任务
    public static boolean canPushHandle(boolean aIsServerSide, @NotNull ITaskNumberExecutor aExecutor) {
        if (aIsServerSide) {
            if (aExecutor.getTaskNumber() >= MAX_TASK_SERVER) return F;
            if (sCurrentHandlesServer < MIN_HANDLES_SERVER) return T;
            if (sCurrentHandlesServer >= MAX_HANDLES_SERVER) return F;
        } else {
            if (aExecutor.getTaskNumber() >= MAX_TASK_CLIENT) return F;
            if (sCurrentHandlesClient < MIN_HANDLES_CLIENT) return T;
            if (sCurrentHandlesServer >= MAX_HANDLES_CLIENT) return F;
        }
        return F;
    }

}
