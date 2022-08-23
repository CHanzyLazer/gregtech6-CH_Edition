package gregtechCH.tileentity.cores.motors;

import gregapi.data.FM;
import gregapi.data.LH;
import gregapi.fluid.FluidTankGT;
import gregapi.tileentity.base.TileEntityBase09FacingSingle;
import gregtechCH.data.LH_CH;
import gregtechCH.tileentity.cores.IMTEC_Texture;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

import static gregapi.data.CS.*;

public class MTEC_MotorLiquid extends MTEC_Motor {
    public MTEC_MotorLiquid(TileEntityBase09FacingSingle aTE) {super(aTE);}
    public MTEC_MotorMainFluidBurner data() {return (MTEC_MotorMainFluidBurner)mD;}

    @Override protected MTEC_MotorMainBase getNewCoreMain() {return new MTEC_MotorMainFluidBurner(this, mOutputTank.AS_ARRAY);}
    @Override protected IMTEC_Texture getNewCoreIcon() {return new MTEC_MotorIconLiquid(this);}

    /* main code */
    protected FluidTankGT mOutputTank = new FluidTankGT(1000);

    // init of core
    @Override public void preInit() {super.preInit(); data().mRecipes = FM.Engine;} // 先指定默认的合成表
    @Override protected void postInitTank() {super.postInitTank(); mOutputTank.setCapacity(data().mRate * 10);} // core 不进行容量设定

    // tooltips
    @Override public void toolTipsRecipe(List<String> aList) {data().toolTipsRecipe(aList);}
    @Override public void toolTipsImportant(List<String> aList) {
        data().toolTipsImportant(aList);
        aList.add(LH.Chat.ORANGE   + LH.get(LH.NO_GUI_FUNNEL_TAP_TO_TANK));
        super.toolTipsImportant(aList);
    }
    @Override public void toolTipsOther(List<String> aList, ItemStack aStack, boolean aF3_H) {
        super.toolTipsOther(aList, aStack, aF3_H);
        data().toolTipsOther(aList);
    }

    // 工具右键
    @Override
    public long onToolClick(String aTool, long aRemainingDurability, long aQuality, Entity aPlayer, List<String> aChatReturn, IInventory aPlayerInventory, boolean aSneaking, ItemStack aStack, byte aSide, float aHitX, float aHitY, float aHitZ) {
        long tReturn = data().onToolClickFirst(aTool, aChatReturn, aSneaking);
        if (tReturn > 0) return tReturn;
        tReturn = super.onToolClick(aTool, aRemainingDurability, aQuality, aPlayer, aChatReturn, aPlayerInventory, aSneaking, aStack, aSide, aHitX, aHitY, aHitZ);
        if (tReturn > 0) return tReturn;
        tReturn = data().onToolClickLast(aTool, aChatReturn, aSneaking);
        if (tReturn > 0) return tReturn;
        return 0;
    }

    // 一些接口
    @Override
    public void onBreakBlock() {
        if (mTE.isServerSide()) {
            GarbageGT.trash(data().mTankFluid);
        }
    }

    @Override public byte getDefaultSide() {return SIDE_FRONT;}
    @Override public boolean[] getValidSides() {return SIDES_VALID;}
}
