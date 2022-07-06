package gregtechCH.util;

import net.minecraft.world.World;

public class WD_CH {
    public static <WorldType> boolean isServerSide(WorldType aWorld) {
        return (aWorld instanceof World) ? (!((World)aWorld).isRemote) : cpw.mods.fml.common.FMLCommonHandler.instance().getEffectiveSide().isServer();
    }
    public static <WorldType> boolean isClientSide(WorldType aWorld) {
        return (aWorld instanceof World) ? ((World)aWorld).isRemote : cpw.mods.fml.common.FMLCommonHandler.instance().getEffectiveSide().isClient();
    }
}
