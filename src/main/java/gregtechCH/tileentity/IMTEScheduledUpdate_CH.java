package gregtechCH.tileentity;

/**
 * @author CHanzy
 * GTCH 专用纯粹的计划任务更新，服务端客户端均有，不考虑实体是否已经 dead 的情况
 */
public interface IMTEScheduledUpdate_CH {
    void onScheduledUpdate_CH(boolean aIsServerSide);
}
