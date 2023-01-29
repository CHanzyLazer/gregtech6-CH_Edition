package gregtechCH.threads;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static gregtechCH.config.ConfigForge.DATA_GTCH;

/**
 * @author CHanzy
 */
public class ThreadPools {
    public static final Executor            NONE_THREAD     = Runnable::run;
    public static final ExecutorService     RENDER_THREAD   = Executors.newSingleThreadExecutor();
    public static final ExecutorService     SOUND_THREAD    = Executors.newSingleThreadExecutor();
    public static final ExecutorService     MACHINE_THREAD  = Executors.newSingleThreadExecutor();
    public static final ParRunPool          TICK_THREAD     = new ParRunPool(DATA_GTCH.overrideTickThread > 0 ? DATA_GTCH.overrideTickThread : Runtime.getRuntime().availableProcessors() / 2 + 1);
}
