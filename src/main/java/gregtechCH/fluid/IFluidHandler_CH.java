package gregtechCH.fluid;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

public interface IFluidHandler_CH extends IFluidHandler {
    // 提供额外接口来实现容器液体白名单的效果
    boolean canFillExtra(FluidStack aFluid);
}
