package gregtechCH.tileentity;

/**
 * @author CHanzy
 * GTCH 专用纯粹的计划任务更新，服务端客户端均有，不考虑实体是否已经 dead 的情况
 * 目前仅用于光照更新，如果有更多的需求还需要改成 runnable 的形式
 */
public interface IMTEScheduledUpdate_CH {
    void onScheduledUpdate_CH(boolean aIsServerSide);
}
