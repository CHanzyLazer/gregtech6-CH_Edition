package gregtechCH.tileentity.connectors;

// 实体继承此类用于阻止部分面的染色管道自动连接
public interface ITEInterceptAutoConnectItem {
    boolean interceptAutoConnectItem(byte aSide);
}
