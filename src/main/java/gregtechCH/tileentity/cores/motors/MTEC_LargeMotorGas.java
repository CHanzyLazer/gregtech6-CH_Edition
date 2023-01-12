package gregtechCH.tileentity.cores.motors;

import gregapi.data.FM;
import gregapi.data.LH;
import gregapi.fluid.FluidTankGT;
import gregapi.tileentity.multiblocks.TileEntityBase10MultiBlockBase;
import gregtechCH.data.LH_CH;
import gregtechCH.tileentity.cores.IMTEC_Texture;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import java.util.List;

import static gregapi.data.CS.GarbageGT;
import static gregapi.data.CS.SIDE_ANY;

public class MTEC_LargeMotorGas extends MTEC_LargeMotor {
    public MTEC_LargeMotorGas(TileEntityBase10MultiBlockBase aTE) {super(aTE);}
    public MTEC_MotorMainFluidBurner data() {return (MTEC_MotorMainFluidBurner)mD;}

    /* stuff to override */
    @Override protected MTEC_MotorMainBase getNewCoreMain() {return new MTEC_MotorMainFluidBurner(this, mTanksOutput);}
    @Override protected IMTEC_Texture getNewCoreIcon() {return new MTEC_LargeMotorIconGas(this);}
    @Override protected MTEC_MultiBlockMotorBase getNewCoreMultiBlock() {return new MTEC_MultiBlockMotorFluidBurner(this);}

    /* main code */
    // 大燃气涡轮需要更多的输出储罐
    protected FluidTankGT[] mTanksOutput = new FluidTankGT[] {new FluidTankGT(0), new FluidTankGT(0), new FluidTankGT(0)};

    // init of core
    @Override public void preInit() {super.preInit(); data().mRecipes = FM.Gas;} // 先指定默认的合成表
    @Override protected void postInitTank() {super.postInitTank(); for (FluidTankGT tTank : mTanksOutput) tTank.setCapacity(mD.mInRate * 16);} // core 不进行容量大小设定

    // tooltips
    @Override public void toolTipsRecipe(List<String> aList) {data().toolTipsRecipe_burn(aList);}
    @Override protected void toolTipsEnergy2(List<String> aList) {
        aList.add(LH.Chat.RED      + LH.get(LH.ENERGY_OUTPUT) + ": " + LH.Chat.WHITE + mDL.mRateArray[mDL.mMidLength-mDL.mMinLength] + " " + mD.mEnergyTypeEmitted.getLocalisedChatNameShort() + LH.Chat.WHITE + "/t");
    }
    @Override public void toolTipsImportant(List<String> aList) {
        data().toolTipsImportant_igniteFire(aList);
        super.toolTipsImportant(aList);
    }
    @Override public void toolTipsOther(List<String> aList, ItemStack aStack, boolean aF3_H) {
        aList.add(LH.Chat.DGRAY   + LH_CH.get("gtch.tooltip.multiblock.gasturbine.4"));
        super.toolTipsOther(aList, aStack, aF3_H);
        data().toolTipsOther_sneakMagnify(aList);
    }
    @Override protected void toolTipsMultiblock2(List<String> aList) {
        aList.add(LH.Chat.WHITE    + LH_CH.get("gtch.tooltip.multiblock.gasturbine.1"));
        aList.add(LH.Chat.WHITE    + LH_CH.getNumber("gtch.tooltip.multiblock.gasturbine.5", mDL.mMinLength, mDL.mMaxLength));
        aList.add(LH.Chat.WHITE    + LH_CH.get("gtch.tooltip.multiblock.gasturbine.2"));
        aList.add(LH.Chat.WHITE    + LH_CH.get("gtch.tooltip.multiblock.gasturbine.3"));
    }
    static {
        LH_CH.add("gtch.tooltip.multiblock.gasturbine.1", "3x3xN of the Walls you crafted this with");
        LH_CH.add("gtch.tooltip.multiblock.gasturbine.2", "Main centered on the 3x3 facing outwards");
        LH_CH.add("gtch.tooltip.multiblock.gasturbine.3", "Input only possible at frontal 3x3");
        LH_CH.add("gtch.tooltip.multiblock.gasturbine.4", "Exhaust Gas can still be pumped out at Bottom Layer");
        LH_CH.add("gtch.tooltip.multiblock.gasturbine.5", "N can be from %d to %d");
    }


    // 工具右键
    @Override
    public long onToolClick(String aTool, long aRemainingDurability, long aQuality, Entity aPlayer, List<String> aChatReturn, IInventory aPlayerInventory, boolean aSneaking, ItemStack aStack, byte aSide, float aHitX, float aHitY, float aHitZ) {
        long tReturn = data().onToolClickFirst_sneakMagnify(aTool, aChatReturn, aSneaking);
        if (tReturn > 0) return tReturn;
        tReturn = super.onToolClick(aTool, aRemainingDurability, aQuality, aPlayer, aChatReturn, aPlayerInventory, aSneaking, aStack, aSide, aHitX, aHitY, aHitZ);
        if (tReturn > 0) return tReturn;
        tReturn = data().onToolClickLast_plungerIgniterExtinguisher(aTool, aPlayer, aChatReturn, aSneaking);
        if (tReturn > 0) return tReturn;
        return 0;
    }
    @Override protected void onMagnifyingGlass3(List<String> aChatReturn) {
        aChatReturn.add(LH.get(LH.ENERGY_OUTPUT)  + ": " + getEnergySizeOutputRecommended(mD.mEnergyTypeEmitted, SIDE_ANY) + " " + mD.mEnergyTypeEmitted.getLocalisedChatNameShort() + LH.Chat.WHITE + "/t");
    }

    // 一些接口
    @Override
    public void onBreakBlock() {
        if (mTE.isServerSide()) {
            GarbageGT.trash(data().mTankFluid);
            GarbageGT.trash(data().mCTanks.mTanksOutput);
        }
        super.onBreakBlock();
    }
}
