package gregtechCH;

import gregapi.tileentity.ITileEntityErrorable;
import gregtechCH.config.ConfigJson_CH;
import gregtechCH.data.CS_CH;
import gregtechCH.tileentity.ITEScheduledUpdate_CH;
import gregtechCH.util.UT_CH;

import java.util.*;

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
        CS_CH.initCS_CH();

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

        OUT.println(getModNameForLog() + ": PostInit-Phase finished!");
        OUT.println(getModNameForLog() + ": ======================");
    }

    // 重写了原本的更新 tick，用于实现在每 tick 更新时进行调用内部函数。全大写代表不能随便调用
    public static void RESET_SERVER_TIME() {
        LAST_TIME_SERVER = System.currentTimeMillis();
        sCurrentHandlesServer = 0;
        SERVER_TIME = 0;
    }
    public static void UPDATE_SERVER_TIME() {
        if (SERVER_TIME > 10) doScheduled(T);

        ++SERVER_TIME;
        LAST_TIME_SERVER = System.currentTimeMillis();
        sCurrentHandlesServer = 0;
    }
    public static void UPDATE_CLIENT_TIME() {
        if (CLIENT_TIME > 10) doScheduled(F);

        ++CLIENT_TIME;
        LAST_TIME_CLIENT = System.currentTimeMillis();
        sCurrentHandlesClient = 0;
    }

    // 实现自用的实体计划任务 api，服务端和客户端通用
    // 不使用 greg 自带的用来防止相互干扰
    // 支持更长 tick 的延长计划，使用 LinkedList 来记录每 tick 需要更新的计划任务，每次更新需要移除开头的更新实体组
    // 客户端和服务端应该必须手动使用的不同的数据（java 并行不是 mpi）
    private final static LinkedList<Set<ITEScheduledUpdate_CH>> TE_SCHEDULED_UPDATERS_LIST_SERVER = new LinkedList<>();
    private final static LinkedList<Set<ITEScheduledUpdate_CH>> TE_SCHEDULED_UPDATERS_LIST_CLIENT = new LinkedList<>();
    @SuppressWarnings({"unchecked", "InstantiatingObjectToGetClassObject"})
    private final static Class<HashSet<ITEScheduledUpdate_CH>> SET_CLASS = (Class<HashSet<ITEScheduledUpdate_CH>>)(new HashSet<ITEScheduledUpdate_CH>()).getClass();
    public static synchronized void pushScheduled(boolean aIsServerSide, ITEScheduledUpdate_CH aScheduleUpdater) {pushScheduled(aIsServerSide, aScheduleUpdater, 0);}
    public static synchronized void pushScheduled(boolean aIsServerSide, ITEScheduledUpdate_CH aScheduleUpdater, int aDelayTick) {
        UT_CH.STL.adaptive_get(aIsServerSide?TE_SCHEDULED_UPDATERS_LIST_SERVER:TE_SCHEDULED_UPDATERS_LIST_CLIENT, aDelayTick, SET_CLASS).add(aScheduleUpdater);
    }
    // 注意每 tick 只能调用一次
    // 加锁防止并行修改队列
    private static synchronized void doScheduled(boolean aIsServerSide) {
        // 先提取本 tick 需要执行的计划，然后将其移除出总 LIST
        Set<ITEScheduledUpdate_CH> tScheduledUpdatersDo = (aIsServerSide?TE_SCHEDULED_UPDATERS_LIST_SERVER:TE_SCHEDULED_UPDATERS_LIST_CLIENT).pollFirst();
        if (tScheduledUpdatersDo == null) return;
        // 遍历执行
        for (ITEScheduledUpdate_CH tTileEntity : tScheduledUpdatersDo) {
            try {
                tTileEntity.onScheduledUpdate(aIsServerSide);
            } catch(Throwable e) {
                if (tTileEntity instanceof ITileEntityErrorable) ((ITileEntityErrorable)tTileEntity).setError("GTCH: Scheduled TileEntity Update - " + e);
                e.printStackTrace(ERR);
            }
        }
    }

    // 用于实现限制每 tick 的函数运行次数
    private static final int MIN_HANDLES_SERVER = 1;
    private static final int MIN_HANDLES_CLIENT = 1;
    private static final long MAX_HANDLE_TIME_SERVER = 50; // ms 最多处理的时间
    private static final long MAX_HANDLE_TIME_CLIENT = 5;  // ms 最多处理的时间
    private static long LAST_TIME_SERVER = 0; // ms 记录上一 tick 的时间，用于统计本 tick 已经花费了多少时间
    private static long LAST_TIME_CLIENT = 0;
    private static int sCurrentHandlesServer = 0;
    private static int sCurrentHandlesClient = 0;
    public static void pushHandle(boolean aIsServerSide) {if (aIsServerSide) ++sCurrentHandlesServer; else ++sCurrentHandlesClient;}
    // 如果本 tick 花费过多时间并且已经达到了最低需要的任务数则不能继续提交任务
    public static boolean canPushHandle(boolean aIsServerSide) {
        if (aIsServerSide) {
            if (sCurrentHandlesServer < MIN_HANDLES_SERVER) return T;
            long tTickTime = System.currentTimeMillis() - LAST_TIME_SERVER;
            if (tTickTime < MAX_HANDLE_TIME_SERVER) return T;
        } else {
            if (sCurrentHandlesClient < MIN_HANDLES_CLIENT) return T;
            long tTickTime = System.currentTimeMillis() - LAST_TIME_CLIENT;
            if (tTickTime < MAX_HANDLE_TIME_CLIENT) return T;
        }
        return F;
    }

}
