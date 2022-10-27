package gregtechCH.tileentity.cores.motors;

import gregapi.data.CS;
import gregapi.data.LH;
import gregapi.tileentity.base.TileEntityBase09FacingSingle;
import gregtechCH.tileentity.cores.IMTEC_Texture;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import java.util.List;

import static gregapi.data.CS.SIDES_VALID;
import static gregapi.data.CS.SIDE_FRONT;

// 提取公共的部分
public abstract class MTEC_MotorFluidBase extends MTEC_Motor {
    protected MTEC_MotorFluidBase(TileEntityBase09FacingSingle aTE) {super(aTE);}
    public MTEC_MotorMainFluidBurner data() {return (MTEC_MotorMainFluidBurner)mD;}
    
    @Override abstract protected MTEC_MotorMainBase getNewCoreMain();
    @Override abstract protected IMTEC_Texture getNewCoreIcon();
    
    /* main code */
    // init of core
    @Override public final void preInit() {super.preInit(); preInit2();}
    abstract protected void preInit2();
    @Override protected final void postInitTank() {super.postInitTank(); postInitTank2();}
    abstract protected void postInitTank2();
    
    // tooltips
    @Override public void toolTipsRecipe(List<String> aList) {data().toolTipsRecipe(aList);}
    @Override public final void toolTipsImportant(List<String> aList) {
        data().toolTipsImportant(aList);
        toolTipsImportant2(aList);
        super.toolTipsImportant(aList);
    }
    protected void toolTipsImportant2(List<String> aList) {
        aList.add(LH.Chat.ORANGE   + LH.get(LH.NO_GUI_FUNNEL_TAP_TO_TANK));
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
            CS.GarbageGT.trash(data().mTankFluid);
        }
    }
    
    @Override public byte getDefaultSide() {return SIDE_FRONT;}
    @Override public boolean[] getValidSides() {return SIDES_VALID;}
}
