package gregtechCH.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregapi.network.IPacket;
import net.minecraft.entity.player.EntityPlayerMP;

// 实现这个接口来实现方块获取服务器的数据
@Deprecated
public interface IBlockGetServerData_CH {
    // 告知服务器需要获取数据
    @SideOnly(Side.CLIENT)
    void callGetServerData(int aX, int aY, int aZ, EntityPlayerMP aPlayer);
    // 服务器接收后打包发送数据
    @SideOnly(Side.SERVER)
    void sendServerData(int aX, int aY, int aZ, EntityPlayerMP aPlayer);
    // 服务器数据打包
    @SideOnly(Side.SERVER)
    IPacket getServerDataPacket(int aX, int aY, int aZ);
    // 接收服务器数据
    @SideOnly(Side.CLIENT)
    void receiveServerData(byte[] aData);
}
