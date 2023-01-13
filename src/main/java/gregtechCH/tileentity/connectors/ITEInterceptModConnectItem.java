package gregtechCH.tileentity.connectors;

/**
 * @author CHanzy
 * 实体继承此类用于阻止部分面的其他模组的连接
 */
public interface ITEInterceptModConnectItem {
    boolean interceptModConnectItem(byte aSide);
}
