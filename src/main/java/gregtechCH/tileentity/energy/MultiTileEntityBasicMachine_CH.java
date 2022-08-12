package gregtechCH.tileentity.energy;

import gregapi.data.LH;
import gregapi.data.TD;
import gregapi.recipes.Recipe;
import gregapi.tileentity.machines.MultiTileEntityBasicMachine;
import gregapi.util.UT;
import gregtechCH.data.LH_CH;
import gregtechCH.tileentity.ITileEntityName_CH;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

import static gregapi.data.CS.*;
import static gregtechCH.data.CS_CH.*;

// 重写处理过程，将处理所需能量不变，单独增加一个变量表示处理速度，受到效率影响，超过合成表功率也会有损失，具体算法可以重写
// ParallelDuration 意义改成并行直接合并工作时间，不会进行加速
// 重写部分 tooltips
public class MultiTileEntityBasicMachine_CH extends MultiTileEntityBasicMachine implements ITileEntityName_CH {
    long mProgressRate = Math.min(mInputMax, mEnergy);
    long mInputNow = mEnergy;

    // NBT 读写
    @Override
    public void readFromNBT2(NBTTagCompound aNBT) {
        super.readFromNBT2(aNBT);
        if (aNBT.hasKey(NBT_PROGRESS_RATE)) mProgressRate = aNBT.getLong(NBT_PROGRESS_RATE);
    }

    @Override
    public void writeToNBT2(NBTTagCompound aNBT) {
        super.writeToNBT2(aNBT);
        UT.NBT.setNumber(aNBT, NBT_PROGRESS_RATE, mProgressRate);

        UT.NBT.setNumber(aNBT, NBT_INPUT_NOW, mInputNow);  // for OmniOcular usage
        UT.NBT.setNumber(aNBT, NBT_EFFICIENCY_CH, mEfficiency);  // for OmniOcular usage
    }

    // tooltips
    @Override
    protected void toolTipsEnergy(List<String> aList) {
        aList.add(LH.Chat.CYAN + LH.get(LH.RECIPES) + ": " + LH.Chat.WHITE + LH.get(mRecipes.mNameInternal) + (mParallel > 1 ? " (" + LH_CH.getNumber(LH_CH.ENERGY_PARALLEL, mParallel) + ")" : ""));
        if (mEfficiency != 10000)
            aList.add(LH.getToolTipEfficiency(mEfficiency));
    }
    @Override
    protected void toolTipsUseful(List<String> aList) {
        if (mEnergyTypeAccepted == TD.Energy.TU) {
            if (!mParallelDuration && mParallel>1) {
                aList.add(LH.Chat.YELLOW + LH_CH.get(LH_CH.OVERCLOCK_PARALLEL_TU));
            }
        } else {
            if (mCheapOverclocking) {
                aList.add(LH.Chat.YELLOW + LH_CH.get(LH_CH.OVERCLOCK_CHEAP));
            } else {
                aList.add(LH.Chat.YELLOW + LH_CH.get(LH_CH.OVERCLOCK_EXPENSIVE)+ " (" + LH_CH.get(LH_CH.OVERCLOCK_SQRT) + ")");
            }
            if (!mParallelDuration && mParallel>1) {
                aList.add(LH.Chat.YELLOW + LH_CH.get(LH_CH.OVERCLOCK_PARALLEL));
            }
        }
    }

    // 工具右键
    @Override
    public void onMagnifyingGlass(List<String> aChatReturn) {
        super.onMagnifyingGlass(aChatReturn);
        onMagnifyingGlassEnergy(aChatReturn);
    }
    public void onMagnifyingGlassEnergy(List<String> aChatReturn) {
        if (mActive) aChatReturn.add("Processing, " + LH.get(LH.EFFICIENCY) + ": " + LH.percent(UT.Code.units(mEfficiency, mInputNow, mProgressRate, F)) + "%");
    }

    // 处理计算
    @Override
    protected long getBoundInput() {
        return Math.max(Math.min(mInputMax, mInputNow), mInputMin);
    }

    @Override
    protected void calMaxProgress(int aProcessCount, Recipe aRecipe) {
        // 保持代码简洁这里不考虑 RF 输入
        if (mParallelDuration) {
            mMinEnergy = Math.max(1, aRecipe.mEUt);
            mMaxProgress = Math.max(1, UT.Code.units(aRecipe.mEUt * Math.max(1, aRecipe.mDuration) * aProcessCount, mEfficiency, 10000, T));
        } else if (mEnergyTypeAccepted == TD.Energy.TU) {
            mMinEnergy = Math.max(1, aRecipe.mEUt);
            mMaxProgress = Math.max(1, UT.Code.units(aRecipe.mEUt * Math.max(1, aRecipe.mDuration), mEfficiency, 10000, T));
        } else {
            mMinEnergy = Math.max(1, aRecipe.mEUt * aProcessCount);
            mMaxProgress = Math.max(1, UT.Code.units(aRecipe.mEUt * Math.max(1, aRecipe.mDuration) * aProcessCount, mEfficiency, 10000, T));
        }
    }

    @Override
    public void doWork(long aTimer) {
        mInputNow = mEnergy;
        if ((mEnergy >= mInputMin || mEnergyTypeAccepted == TD.Energy.TU) && mEnergy >= mMinEnergy && checkStructure(F)) {
            if (!mCheapOverclocking) {
                // 有损超频，采用连续公式 rate = sqrt(input * minEnergy)，和原本一样不过连续化了
                mProgressRate = (long) Math.sqrt(Math.min(mInputMax, mEnergy) * mMinEnergy);
            } else {
                // 无损超频，处理速度直接是输入能量
                mProgressRate = Math.min(mInputMax, mEnergy);
            }
            mActive = doActive(aTimer, mProgressRate);
            mRunning = T;
        } else {
            if (aTimer > 40) {
                mActive = doInactive(aTimer);
                mRunning = F;
            }
            mProgressRate = 0;
            mSuccessful = F;
        }
        mEnergy -= mInputMax; if (mEnergy < 0) mEnergy = 0;
        if (mIgnited > 0) mIgnited--;
    }

    @Override public String getTileEntityName_CH() {return "gtch.multitileentity.machine.basic";}
}
