package gregtechCH.tileentity.cores.motors;

import gregapi.data.FM;
import gregapi.fluid.FluidTankGT;
import gregapi.tileentity.base.TileEntityBase09FacingSingle;
import gregtechCH.tileentity.cores.IMTEC_Texture;

import java.util.List;

// 目前仅默认的合成表不同
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
    @Override protected void toolTipsImportant2(List<String> aList) {/* 燃气涡轮不能手动加燃料 */}
}
