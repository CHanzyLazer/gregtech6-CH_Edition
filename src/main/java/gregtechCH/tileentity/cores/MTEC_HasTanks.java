package gregtechCH.tileentity.cores;

import gregapi.fluid.FluidTankGT;
import gregtechCH.config.ConfigForge;
import gregtechCH.tileentity.cores.motors.IMTEC_CanExplode;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

import java.util.List;

import static gregapi.data.CS.*;

/**
 * @author CHanzy
 * 将拥有储罐的机器的接口合并到这个 core 中
 */
public class MTEC_HasTanks {
    final IMTEC_CanExplode mCore;
    public MTEC_HasTanks(IMTEC_CanExplode aCore, FluidTankGT[] aInputTanks, FluidTankGT[] aOutputTanks) {mCore = aCore; mTanksInput = aInputTanks; mTanksOutput = aOutputTanks;}
    
    /* stuff to override */
    public final FluidTankGT[] mTanksInput;
    public final FluidTankGT[] mTanksOutput;
    
    /* main code */
    protected FluidTankGT[] mTanks = ZL_FT;
    
    // NBT读写
    public void readFromNBT(NBTTagCompound aNBT) {
        int tIdx = 0;
        int tInTankLen = mTanksInput.length;
        int tTankLen = tInTankLen + mTanksOutput.length;
        mTanks = new FluidTankGT[tTankLen];

        for (; tIdx < tInTankLen; ++tIdx) {
            FluidTankGT tTank = mTanksInput[tIdx];
            tTank.readFromNBT(aNBT, NBT_TANK+"."+tIdx);
            mTanks[tIdx] = tTank;
        }
        for (; tIdx < tTankLen; ++tIdx) {
            FluidTankGT tTank = mTanksOutput[tIdx-tInTankLen];
            tTank.readFromNBT(aNBT, NBT_TANK+"."+tIdx);
            mTanks[tIdx] = tTank;
        }
    }
    public void writeToNBT(NBTTagCompound aNBT) {
        for (int tIdx = 0; tIdx < mTanks.length; ++tIdx)
            mTanks[tIdx].writeToNBT(aNBT, NBT_TANK+"."+tIdx);
    }
    
    public IFluidTank getFluidTankFillable(byte aSide, FluidStack aFluidToFill) {
        for (FluidTankGT tTank : mTanksInput) if (tTank.contains(aFluidToFill) || tTank.isEmpty()) return tTank;
        return null;
    }
    public IFluidTank getFluidTankDrainable(byte aSide, FluidStack aFluidToDrain) {
        if (aFluidToDrain == null) {
            for (FluidTankGT tTank : mTanksOutput) if (tTank.has()) return tTank;
        } else {
            for (FluidTankGT tTank : mTanksOutput) if (tTank.contains(aFluidToDrain)) return tTank;
        }
        return null;
    }
    public IFluidTank[] getFluidTanks(byte aSide) {return mTanks;}
    public int funnelFill(byte aSide, FluidStack aFluid, boolean aDoFill) {
        for (FluidTankGT tTank : mTanksInput) if (tTank.contains(aFluid) || tTank.isEmpty()) return tTank.fill(aFluid, aDoFill);
        return 0;
    }
    public FluidStack tapDrain(byte aSide, int aMaxDrain, boolean aDoDrain) {
        for (FluidTankGT tTank : mTanksOutput) if (tTank.has()) return tTank.drain(aMaxDrain, aDoDrain);
        for (FluidTankGT tTank : mTanksInput)  if (tTank.has()) return tTank.drain(aMaxDrain, aDoDrain);
        return null;
    }
    
    // 放大镜右键监视内部储罐
    public void onMagnifyingGlass(List<String> aChatReturn) {
        if (mTanksInput.length == 1) {if (mTanksInput[0].has()) aChatReturn.add("Tank input: " + mTanksInput[0].content());}
        else {for (int i = 0; i < mTanksInput.length; ++i) if (mTanksInput[i].has()) aChatReturn.add("Tank input " + (i+1) + ": " + mTanksInput[i].content());}
        if (mTanksOutput.length == 1) {if (mTanksOutput[0].has()) aChatReturn.add("Tank output: " + mTanksOutput[0].content());}
        else {for (int i = 0; i < mTanksOutput.length; ++i) if (mTanksOutput[i].has()) aChatReturn.add("Tank output " + (i+1) + ": " + mTanksOutput[i].content());}
    }
    // 搋子右键清空内部流体
    public long onPlunger(Entity aPlayer, List<String> aChatReturn) {
        for (FluidTankGT tTank : mTanksOutput) if (tTank.has()) {long tOut = GarbageGT.trash(tTank); if (tOut > 0) plungerExplode(aPlayer); return tOut;}
        for (FluidTankGT tTank : mTanksInput)  if (tTank.has()) {long tOut = GarbageGT.trash(tTank); if (tOut > 0) plungerExplode(aPlayer); return tOut;}
        return 0;
    }
    // 运行时清除流体会爆炸
    public void plungerExplode(Entity aPlayer) {
        if (ConfigForge.DATA_MACHINES.motorExplodeByPlunger && mCore.canExplode()) {
            mCore.explode(F);
        }
    }
}
