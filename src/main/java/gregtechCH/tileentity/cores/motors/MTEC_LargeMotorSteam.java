package gregtechCH.tileentity.cores.motors;

import gregapi.data.FL;
import gregapi.data.LH;
import gregapi.fluid.FluidTankGT;
import gregapi.tileentity.multiblocks.TileEntityBase10MultiBlockBase;
import gregapi.util.UT;
import gregtechCH.config.ConfigForge;
import gregtechCH.data.LH_CH;
import gregtechCH.tileentity.cores.IMTEC_Texture;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

import static gregapi.data.CS.*;

public class MTEC_LargeMotorSteam extends MTEC_LargeMotor {
    public MTEC_LargeMotorSteam(TileEntityBase10MultiBlockBase aTE) {super(aTE);}
    public MTEC_MotorMainSteam data() {return (MTEC_MotorMainSteam)mD;}
    
    /* stuff to override */
    @Override protected MTEC_MotorMainBase getNewCoreMain() {return new MTEC_MotorMainSteam(this, mTankWater.AS_ARRAY);}
    @Override protected IMTEC_Texture getNewCoreIcon() {return new MTEC_LargeMotorIconSteam(this);}
    
    /* main code */
    protected FluidTankGT mTankWater = new FluidTankGT(); // 大的涡轮会多一个蒸馏水的储罐
    
    // init of core
    @Override protected void postInitTank() {super.postInitTank(); mTankWater.setCapacity(mD.mInRate*16).fixFluid(FL.DistW.fluid()).setVoidExcess();} // core 不进行容量大小设定
    
    @Override public void writeToNBT(NBTTagCompound aNBT) {
        super.writeToNBT(aNBT);
        mTankWater.unfixFluid().writeToNBT(aNBT, NBT_TANK+"."+1);
        UT.NBT.setNumber(aNBT, NBT_TANK_CAPACITY+"."+1, mTankWater.capacity());
    }
    
    // tooltips
    @Override
    public void toolTipsEnergy2(List<String> aList) {
        long tInput = UT.Code.units(mDL.mRateArray[mDL.mMidLength-mDL.mMinLength], mDL.mEfficiencyArray[mDL.mMidLength-mDL.mMinLength], 10000, T) * STEAM_PER_EU, tOutput = mDL.mRateArray[mDL.mMidLength-mDL.mMinLength];
        aList.add(LH.Chat.GREEN    + LH.get(LH.ENERGY_INPUT ) + ": " + LH.Chat.WHITE 	+ tInput  + " " + data().mEnergyTypeAccepted.getLocalisedChatNameShort() + LH.Chat.WHITE + "/t (" + LH_CH.getNumber(LH_CH.ENERGY_TO, tInput/2,  tInput*2)  + ")");
        aList.add(LH.Chat.RED      + LH.get(LH.ENERGY_OUTPUT) + ": " + LH.Chat.WHITE 	+ tOutput + " " + data().mEnergyTypeEmitted.getLocalisedChatNameShort()  + LH.Chat.WHITE + "/t (" + LH_CH.getNumber(LH_CH.ENERGY_TO, tOutput/2, tOutput + UT.Code.units(tOutput, 10000, data().mEfficiencyOverclock, F)) + ")");
    }
    @Override
    public void toolTipsUseful(List<String> aList) {
        data().toolTipsUseful_overclock(aList);
        super.toolTipsUseful(aList);
    }
    @Override
    public void toolTipsImportant(List<String> aList) {
        aList.add(LH.Chat.ORANGE + LH.get(LH.EMITS_USED_STEAM) + " ("+LH_CH.get(LH_CH.FACE_PIPE_HOLE)+", " + LH_CH.getToolTipEfficiencySimple(data().mEfficiencyWater) + ")");
        super.toolTipsImportant(aList);
    }
    @Override
    public void toolTipsOther(List<String> aList, ItemStack aStack, boolean aF3_H) {
        aList.add(LH.Chat.DGRAY    + LH_CH.get("gtch.tooltip.multiblock.steamturbine.4"));
        super.toolTipsOther(aList, aStack, aF3_H);
    }
    @Override
    protected void toolTipsMultiblock2(List<String> aList) {
        aList.add(LH.Chat.WHITE    + LH_CH.get("gtch.tooltip.multiblock.steamturbine.1"));
        aList.add(LH.Chat.WHITE    + LH_CH.getNumber("gtch.tooltip.multiblock.steamturbine.5", mDL.mMinLength, mDL.mMaxLength));
        aList.add(LH.Chat.WHITE    + LH_CH.get("gtch.tooltip.multiblock.steamturbine.2"));
        aList.add(LH.Chat.WHITE    + LH_CH.get("gtch.tooltip.multiblock.steamturbine.3"));
    }
    static {
        LH_CH.add("gtch.tooltip.multiblock.steamturbine.1", "3x3xN of the Walls you crafted this with");
        LH_CH.add("gtch.tooltip.multiblock.steamturbine.2", "Main centered on the 3x3 facing outwards");
        LH_CH.add("gtch.tooltip.multiblock.steamturbine.3", "Input only possible at frontal 3x3");
        LH_CH.add("gtch.tooltip.multiblock.steamturbine.4", "Distilled Water can still be pumped out at Bottom Layer");
        LH_CH.add("gtch.tooltip.multiblock.steamturbine.5", "N can be from %d to %d");
    }
    
    
    // 工具右键
    @Override
    public long onToolClick(String aTool, long aRemainingDurability, long aQuality, Entity aPlayer, List<String> aChatReturn, IInventory aPlayerInventory, boolean aSneaking, ItemStack aStack, byte aSide, float aHitX, float aHitY, float aHitZ) {
        long rReturn = super.onToolClick(aTool, aRemainingDurability, aQuality, aPlayer, aChatReturn, aPlayerInventory, aSneaking, aStack, aSide, aHitX, aHitY, aHitZ);
        if (rReturn > 0) return rReturn;
        
        // 特例，搋子优先清除蒸馏水，同样运行时用搋子清除蒸馏水会爆炸
        if (aTool.equals(TOOL_plunger)) {
            if (mTankWater.isEmpty()) return 0;
            long tOut = GarbageGT.trash(mTankWater);
            if (tOut > 0) data().mCTanks.plungerExplode(aPlayer);
            return tOut;
        }
        return 0;
    }
    @Override
    protected void onMagnifyingGlass3(List<String> aChatReturn) {
        aChatReturn.add(LH.get(LH.ENERGY_INPUT)   + ": " + getEnergySizeInputMin( data().mEnergyTypeAccepted, SIDE_ANY) + " - " + getEnergySizeInputMax( data().mEnergyTypeAccepted, SIDE_ANY) + " " + data().mEnergyTypeAccepted.getLocalisedChatNameShort() + LH.Chat.WHITE + "/t");
        aChatReturn.add(LH.get(LH.ENERGY_OUTPUT)  + ": " + getEnergySizeOutputMin(data().mEnergyTypeEmitted,  SIDE_ANY) + " - " + getEnergySizeOutputMax(data().mEnergyTypeEmitted,  SIDE_ANY) + " " + data().mEnergyTypeEmitted.getLocalisedChatNameShort()  + LH.Chat.WHITE + "/t");
    }
    
    // ticking
    @Override
    protected void convertToTanks(FluidStack... aFluids) {
        mTankWater.fillAll(aFluids[0]); // 一定会成功
    }
    @Override
    protected void convertAutoOutput() {
        if (mTankWater.has()) {
            FL.move(mTankWater, getFluidEmitter().getAdjacentTank(getFluidEmittingSide()));
            long tAmount = mTankWater.amount() - mTankWater.capacity() / 2;
            if (tAmount > 0) GarbageGT.trash(mTankWater, tAmount);
        }
    }
    
    // 一些接口
    @Override
    public void onBreakBlock() {
        if (mTE.isServerSide()) {
            GarbageGT.trash(mTankWater);
        }
        super.onBreakBlock();
    }
}
