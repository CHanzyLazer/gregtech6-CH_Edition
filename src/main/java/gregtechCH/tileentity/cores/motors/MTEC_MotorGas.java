package gregtechCH.tileentity.cores.motors;

import gregapi.data.FM;
import gregapi.data.LH;
import gregapi.fluid.FluidTankGT;
import gregapi.tileentity.base.TileEntityBase09FacingSingle;
import gregtechCH.data.LH_CH;
import gregtechCH.tileentity.cores.IMTEC_Texture;

import java.util.List;

/**
 * @author CHanzy
 * 目前仅默认的合成表不同
 */
public final class MTEC_MotorGas extends MTEC_MotorFluidBase {
    public MTEC_MotorGas(TileEntityBase09FacingSingle aTE) {super(aTE);}
    
    @Override protected MTEC_MotorMainBase getNewCoreMain() {return new MTEC_MotorMainFluidBurner(this, mTankOutput.AS_ARRAY);}
    @Override protected IMTEC_Texture getNewCoreIcon() {return new MTEC_MotorIconGas(this);}
    
    /* main code */
    private final FluidTankGT mTankOutput = new FluidTankGT(1000); // 小燃气涡轮还是只有一种输出槽好了，与 GT6U 保持一致
    
    // init of core
    @Override protected void preInit2() {data().mRecipes = FM.Gas;} // 先指定默认的合成表
    @Override protected void postInitTank2() {mTankOutput.setCapacity(data().mRate * 10);} // core 不进行容量设定
    
    // tooltips
    @Override public void toolTipsUseful(List<String> aList) {
        super.toolTipsUseful(aList);
        aList.add(LH.Chat.GREEN + LH_CH.get("gtch.tooltip.motor_gas.useful.1"));
    }
    @Override protected void toolTipsImportant2(List<String> aList) {/* 燃气涡轮不能手动加燃料 */}
    
    static {
        LH_CH.add("gtch.tooltip.motor_gas.useful.1", "Turbines have a higher output but requires longer preheating");
    }
}
