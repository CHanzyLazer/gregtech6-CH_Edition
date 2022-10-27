package gregtechCH.tileentity.cores.motors;

import gregapi.data.FM;
import gregapi.fluid.FluidTankGT;
import gregapi.tileentity.base.TileEntityBase09FacingSingle;
import gregtechCH.tileentity.cores.IMTEC_Texture;

import java.util.List;

// 目前仅默认的合成表不同
public final class MTEC_MotorGas extends MTEC_MotorFluidBase {
    public MTEC_MotorGas(TileEntityBase09FacingSingle aTE) {super(aTE);}
    
    @Override protected MTEC_MotorMainBase getNewCoreMain() {return new MTEC_MotorMainFluidBurner(this, mTanksOutput);}
    @Override protected IMTEC_Texture getNewCoreIcon() {return new MTEC_MotorIconGas(this);}
    
    /* main code */
    private final FluidTankGT[] mTanksOutput = new FluidTankGT[] {new FluidTankGT(0), new FluidTankGT(0), new FluidTankGT(0)};
    
    // init of core
    @Override protected void preInit2() {data().mRecipes = FM.Gas;} // 先指定默认的合成表
    @Override protected void postInitTank2() {for (FluidTankGT tTank : mTanksOutput) tTank.setCapacity(data().mRate * 10);} // core 不进行容量设定
    
    // tooltips
    @Override protected void toolTipsImportant2(List<String> aList) {/* 燃气涡轮不能手动加燃料 */}
}
