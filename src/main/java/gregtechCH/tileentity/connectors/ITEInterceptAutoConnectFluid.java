package gregtechCH.tileentity.connectors;

/**
 * @author CHanzy
 * 实体继承此类用于阻止部分面的染色管道自动连接
 */
public interface ITEInterceptAutoConnectFluid {
    boolean interceptAutoConnectFluid(byte aSide);
}
