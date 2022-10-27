package gregtechCH.tileentity.cores.motors;

import gregapi.data.FM;
import gregapi.data.LH;
import gregapi.fluid.FluidTankGT;
import gregapi.tileentity.base.TileEntityBase09FacingSingle;
import gregtechCH.tileentity.cores.IMTEC_Texture;

import java.util.List;

public final class MTEC_MotorLiquid extends MTEC_MotorFluidBase {
    public MTEC_MotorLiquid(TileEntityBase09FacingSingle aTE) {super(aTE);}
    
    @Override protected MTEC_MotorMainBase getNewCoreMain() {return new MTEC_MotorMainFluidBurner(this, mTankOutput.AS_ARRAY);}
    @Override protected IMTEC_Texture getNewCoreIcon() {return new MTEC_MotorIconLiquid(this);}
    
    /* main code */
    private final FluidTankGT mTankOutput = new FluidTankGT(1000);
    
    // init of core
    @Override protected void preInit2() {data().mRecipes = FM.Engine;} // 先指定默认的合成表
    @Override protected void postInitTank2() {mTankOutput.setCapacity(data().mRate * 10);} // core 不进行容量设定
    
    // tooltips
    protected void toolTipsImportant2(List<String> aList) {
        aList.add(LH.Chat.ORANGE   + LH.get(LH.NO_GUI_FUNNEL_TAP_TO_TANK));
    }
}
