package gregtechCH.tileentity.cores.basicmachines;

import gregapi.data.LH;
import gregapi.data.TD;
import gregapi.recipes.Recipe;
import gregapi.tileentity.machines.MultiTileEntityBasicMachine;
import gregapi.util.UT;
import gregtechCH.data.LH_CH;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

import static gregapi.data.CS.F;
import static gregapi.data.CS.T;
import static gregtechCH.data.CS_CH.*;

/**
 * @author CHanzy
 */
public class MTEC_BasicMachine extends MTEC_BasicMachine_Greg {
    public MTEC_BasicMachine(MultiTileEntityBasicMachine aTE) {super(aTE);}
    
    /* main code */
    protected long mProgressRate = 0;
    protected long mInputNow = 0;
    // NBT 读写
    @Override
    public void writeToNBT(NBTTagCompound aNBT) {
        super.writeToNBT(aNBT);
        UT.NBT.setNumber(aNBT, NBT_PROGRESS_RATE, mProgressRate);   // for OmniOcular usage
        UT.NBT.setNumber(aNBT, NBT_INPUT_NOW, mInputNow);           // for OmniOcular usage
        UT.NBT.setNumber(aNBT, NBT_EFFICIENCY_CH, mTE.mEfficiency); // for OmniOcular usage
    }
    
    // tooltips
    @Override
    public void toolTipsEnergy(List<String> aList) {
        aList.add(LH.Chat.CYAN + LH.get(LH.RECIPES) + ": " + LH.Chat.WHITE + LH.get(mTE.mRecipes.mNameInternal) + (mParallel > 1 ? " (" + LH_CH.getNumber(LH_CH.ENERGY_PARALLEL, mParallel) + ")" : ""));
        if (mTE.mEfficiency != 10000)
            aList.add(LH.getToolTipEfficiency(mTE.mEfficiency));
    }
    @Override
    public void toolTipsUseful(List<String> aList) {
        if (mTE.mEnergyTypeAccepted == TD.Energy.TU) {
            if (!mParallelDuration && mParallel>1) {
                aList.add(LH.Chat.YELLOW + LH_CH.get(LH_CH.OVERCLOCK_PARALLEL_TU));
            }
        } else {
            if (mTE.mCheapOverclocking) {
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
        if (mTE.mActive) aChatReturn.add("Processing, " + LH.get(LH.EFFICIENCY) + ": " + LH.percent(UT.Code.units(mTE.mEfficiency, mInputNow, mProgressRate, F)) + "%");
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
            mMaxProgress = Math.max(1, UT.Code.units(mMinEnergy * Math.max(1, aRecipe.mDuration) * aProcessCount, mTE.mEfficiency, 10000, T));
        } else if (mTE.mEnergyTypeAccepted == TD.Energy.TU) {
            mMinEnergy = Math.max(1, aRecipe.mEUt);
            mMaxProgress = Math.max(1, UT.Code.units(mMinEnergy * Math.max(1, aRecipe.mDuration), mTE.mEfficiency, 10000, T));
        } else {
            mMinEnergy = Math.max(1, aRecipe.mEUt * aProcessCount);
            mMaxProgress = Math.max(1, UT.Code.units(mMinEnergy * Math.max(1, aRecipe.mDuration), mTE.mEfficiency, 10000, T));
        }
    }
    
    @Override public void doWorkFirst(long aTimer) {mInputNow = mEnergy;}
    @Override public void doWorkActive(long aTimer) {
        if (!mTE.mCheapOverclocking) {
            // 有损超频，采用连续公式 rate = sqrt(input * minEnergy)，和原本一样不过连续化了
            mProgressRate = (long) Math.sqrt(Math.min(mInputMax, mEnergy) * mMinEnergy);
        } else {
            // 无损超频，处理速度直接是输入能量
            mProgressRate = Math.min(mInputMax, mEnergy);
        }
        mTE.mActive = doActive(aTimer, mProgressRate);
        mTE.mRunning = T;
    }
    @Override public void doWorkInactive(long aTimer) {
        super.doWorkInactive(aTimer);
        mProgressRate = 0;
    }
}
