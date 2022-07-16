package gregtechCH.tileentity.energy.generators;

import gregapi.code.TagData;
import gregapi.data.FL;
import gregapi.data.FM;
import gregapi.data.LH;
import gregapi.data.TD;
import gregapi.fluid.FluidTankGT;
import gregapi.old.Textures;
import gregapi.recipes.Recipe;
import gregapi.render.BlockTextureDefault;
import gregapi.render.BlockTextureMulti;
import gregapi.render.IIconContainer;
import gregapi.render.ITexture;
import gregapi.tileentity.behavior.TE_Behavior_Active_Trinary;
import gregapi.tileentity.energy.ITileEntityEnergy;
import gregapi.util.UT;
import gregapi.util.WD;
import gregtech.tileentity.energy.generators.MultiTileEntityMotorLiquid;
import gregtechCH.data.LH_CH;
import gregtechCH.tileentity.ITileEntityName_CH;
import gregtechCH.tileentity.energy.MultiTileEntityMotor_CH;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;

import java.util.Collection;
import java.util.List;

import static gregapi.data.CS.*;
import static gregtechCH.data.CS_CH.*;
import static gregtechCH.data.CS_CH.NBT_PREHEAT_COST;

public class MultiTileEntityMotorLiquid_CH extends MultiTileEntityMotor_CH implements IFluidHandler, ITileEntityName_CH {

    protected boolean mBurning = F;
    protected long mEnergyHU = 0;
    protected long mPRate = 64, mInPRate = 64;

    protected static final byte COOLDOWN_NUM = 16;
    protected byte mBurningCounter = 0;  // 注意默认是停止工作的

    public Recipe.RecipeMap mRecipes = FM.Engine;
    public Recipe mLastRecipe = null;
    public FluidTankGT[] mTanks = {new FluidTankGT(1000), new FluidTankGT(1000)};

    // NBT读写
    @Override
    public void readFromNBT2(NBTTagCompound aNBT) {
        super.readFromNBT2(aNBT);
        if (aNBT.hasKey(NBT_COOLDOWN_COUNTER)) mBurningCounter = aNBT.getByte(NBT_COOLDOWN_COUNTER);
        if (aNBT.hasKey(NBT_BURNING)) mBurning = aNBT.getBoolean(NBT_BURNING);
        if (aNBT.hasKey(NBT_ENERGY_HU)) mEnergyHU = aNBT.getLong(NBT_ENERGY_HU);

        if (aNBT.hasKey(NBT_FUELMAP)) mRecipes = Recipe.RecipeMap.RECIPE_MAPS.get(aNBT.getString(NBT_FUELMAP));
        mTanks[0].readFromNBT(aNBT, NBT_TANK+".0").setCapacity(mRate * 10);
        mTanks[1].readFromNBT(aNBT, NBT_TANK+".1").setCapacity(mRate * 10);
    }
    @Override
    protected void setOutRate(NBTTagCompound aNBT) {
        super.setOutRate(aNBT);
        if (aNBT.hasKey(NBT_PREHEAT_RATE)) mPRate = aNBT.getLong(NBT_PREHEAT_RATE);
    }
    @Override
    protected void setInRate(NBTTagCompound aNBT) {
        super.setInRate(aNBT);
        mInPRate = UT.Code.units(mPRate, mEfficiency, 10000, T);
    }

    @Override
    public void writeToNBT2(NBTTagCompound aNBT) {
        super.writeToNBT2(aNBT);
        UT.NBT.setNumber(aNBT, NBT_COOLDOWN_COUNTER, mBurningCounter);
        UT.NBT.setBoolean(aNBT, NBT_BURNING, mBurning);
        UT.NBT.setNumber(aNBT, NBT_ENERGY_HU, mEnergyHU);

        mTanks[0].writeToNBT(aNBT, NBT_TANK+".0");
        mTanks[1].writeToNBT(aNBT, NBT_TANK+".1");
    }

    // tooltips
    @Override
    protected void toolTipsEnergy(List<String> aList) {
        aList.add(LH.Chat.CYAN     + LH.get(LH.RECIPES) + ": " + LH.Chat.WHITE + LH.get(mRecipes.mNameInternal));
        super.toolTipsEnergy(aList);
    }
    @Override
    protected void toolTipsImportant(List<String> aList) {
        aList.add(LH.Chat.ORANGE   + LH.get(LH.REQUIREMENT_IGNITE_FIRE) + " (" + LH.get(LH.FACE_ANY) + ")");
        aList.add(LH.Chat.ORANGE   + LH.get(LH.NO_GUI_FUNNEL_TAP_TO_TANK));
        super.toolTipsImportant(aList);
    }
    @Override
    protected void toolTipsOther(List<String> aList, ItemStack aStack, boolean aF3_H) {
        super.toolTipsOther(aList, aStack, aF3_H);
        aList.add(LH.Chat.DGRAY    + LH_CH.get(LH_CH.TOOL_TO_DETAIL_MAGNIFYINGGLASS_SNEAK));
    }

    // 工具右键
    @Override
    public long onToolClick2(String aTool, long aRemainingDurability, long aQuality, Entity aPlayer, List<String> aChatReturn, IInventory aPlayerInventory, boolean aSneaking, ItemStack aStack, byte aSide, float aHitX, float aHitY, float aHitZ) {
        if (aTool.equals(TOOL_magnifyingglass) && aSneaking) {
            if (aChatReturn != null) {
                if (mTanks[0].has()) aChatReturn.add("Tank input: "  + mTanks[0].content());
                if (mTanks[1].has()) aChatReturn.add("Tank output: " + mTanks[1].content());
            }
            return 1;
        }
        long rReturn = super.onToolClick2(aTool, aRemainingDurability, aQuality, aPlayer, aChatReturn, aPlayerInventory, aSneaking, aStack, aSide, aHitX, aHitY, aHitZ);
        if (rReturn > 0) return rReturn;

        if (isClientSide()) return 0;

        if (aTool.equals(TOOL_plunger)) {
            if (mTanks[1].has()) return GarbageGT.trash(mTanks[1]);
            return GarbageGT.trash(mTanks[0]);
        }

        if (aTool.equals(TOOL_igniter)) {mBurning = T; return 10000;}
        if (aTool.equals(TOOL_extinguisher  )) {mBurning = F; return 10000;}

        return 0;
    }

    // 每 tick 转换
    @Override
    protected void convert() {
        // 燃烧
        if (mBurning) {
            if (mEnergyHU < mInPRate * 4 + mInPCost) {
                Recipe tRecipe = mRecipes.findRecipe(this, mLastRecipe, T, Long.MAX_VALUE, NI, mTanks[0].AS_ARRAY, ZL_IS);
                if (tRecipe != null) {
                    // 这里就检测了输出是否填满的情况了（主要是种类问题）
                    if (tRecipe.mFluidOutputs.length <= 0 || mTanks[1].canFillAll(tRecipe.mFluidOutputs[0])) {
                        if (tRecipe.isRecipeInputEqual(T, F, mTanks[0].AS_ARRAY, ZL_IS)) {
                            mLastRecipe = tRecipe;
                            mEnergyHU += tRecipe.getAbsoluteTotalPower();
                            if (tRecipe.mFluidOutputs.length > 0) mTanks[1].fill(tRecipe.mFluidOutputs[0]);
                            while (mEnergyHU < mInPRate && (tRecipe.mFluidOutputs.length <= 0 || mTanks[1].canFillAll(tRecipe.mFluidOutputs[0])) && tRecipe.isRecipeInputEqual(T, F, mTanks[0].AS_ARRAY, ZL_IS)) {
                                mEnergyHU += tRecipe.getAbsoluteTotalPower();
                                if (tRecipe.mFluidOutputs.length > 0) mTanks[1].fill(tRecipe.mFluidOutputs[0]);
                                if (mTanks[0].isEmpty()) break;
                            }
                        } else if (mEnergyHU < mInPCost) {
                            // 有剩余燃料，有熄火风险，清空容器来切换燃料
                            mTanks[0].setEmpty();
                        }
                    }
                } else {
                    // set remaining Fluid to null, because it is not valid Fuel anymore for whatever reason. MineTweaker happens to live Modpacks too sometimes. ;)
                    mTanks[0].setEmpty();
                }
            }
            // 能量不够, 机器停止，则停止燃烧
            if (mEnergyHU >= mInPCost) mBurningCounter = COOLDOWN_NUM;
            else --mBurningCounter;
            if (mBurningCounter <= 0 || mStopped) {
                mBurning = F;
                mBurningCounter = 0;
            }
        }
        // 热量按照效率转换为旋转能
        if (mEnergy < mPEnergy){
            // 预热阶段
            if (mEnergy < mPEnergy - mPRate) convert(mInPRate, mPRate);
            else convert(mInRate, mRate);
        } else {
            // 运行阶段
            mEnergy = mPEnergy; // 只是为了好看
            convert(mInRate, mRate, T);
        }
        // 自动输出废气
        if (mTanks[1].has()) {
            FL.move(mTanks[1], getAdjacentTank(OPOS[mFacing]));
            if (FL.gas(mTanks[1]) && !WD.hasCollide(worldObj, getOffset(OPOS[mFacing], 1))) {
                GarbageGT.trash(mTanks[1]);
            }
        }
    }
    protected void convert(long aInRate, long aOutRate) {
        convert(aInRate, aOutRate, F);
    }
    protected void convert(long aInRate, long aOutRate, boolean aWasteEnergy) {
        if (mEnergyHU >= aInRate) {
            mEnergy += aOutRate;
            mEnergyHU -= aInRate;
            return;
        }
        if (aWasteEnergy && mEnergyHU > mInPCost) {
            mEnergy += mPCost;
            mEnergyHU = 0;
            return;
        }
        if (mEnergyHU > 0) {
            mEnergy += UT.Code.units(mEnergyHU, 10000, mEfficiency, F);
            mEnergyHU = 0;
            return;
        }
        mEnergyHU = 0;
    }

    @Override
    protected boolean checkOverload() {
        return F;
    }
    @Override
    protected long getOutput() {
        return mRate;
    }
    @Override
    protected boolean checkPreheat() {
        return super.checkPreheat() && mBurning;
    }
    @Override
    protected boolean checkCooldown() {
        return super.checkCooldown() && !mBurning;
    }
    @Override
    protected void doCooldown() {
        super.doCooldown();
        mBurningCounter = 0;
        mBurning = F;
        mEnergyHU = 0;
    }
    @Override
    protected void doElse() {
        // 能量耗尽，但是不熄火（因为熄火不在这里判断）
        super.stop();
        if (!mBurning) {
            mBurningCounter = 0;
            mBurning = F;
            mEnergyHU = 0;
        }
    }
    @Override
    protected void stop() {
        super.stop();
        mBurningCounter = 0;
        mBurning = F;
        mEnergyHU = 0;
    }

    // 一些接口
    @Override
    public boolean breakBlock() {
        if (isServerSide()) {
            for (FluidTankGT tank : mTanks) GarbageGT.trash(tank);
        }
        return super.breakBlock();
    }
    @Override
    protected IFluidTank getFluidTankFillable2(byte aSide, FluidStack aFluidToFill) {
        return mRecipes.containsInput(aFluidToFill, this, NI) ? mTanks[0] : null;
    }
    @Override
    protected IFluidTank getFluidTankDrainable2(byte aSide, FluidStack aFluidToDrain) {
        return mTanks[1];
    }
    @Override
    protected IFluidTank[] getFluidTanks2(byte aSide) {
        return mTanks;
    }
    @Override
    public int funnelFill(byte aSide, FluidStack aFluid, boolean aDoFill) {
        if (!mRecipes.containsInput(aFluid, this, NI)) return 0;
        updateInventory();
        return mTanks[0].fill(aFluid, aDoFill);
    }
    @Override
    public FluidStack tapDrain(byte aSide, int aMaxDrain, boolean aDoDrain) {
        updateInventory();
        return mTanks[mTanks[1].has() ? 1 : 0].drain(aMaxDrain, aDoDrain);
    }
    @Override public ItemStack[] getDefaultInventory(NBTTagCompound aNBT) {return ZL_IS;}
    @Override public boolean canDrop(int aInventorySlot) {return T;}

    @Override public boolean isEnergyAcceptingFrom(TagData aEnergyType, byte aSide, boolean aTheoretical) {return F;}
    @Override public long getEnergyOffered(TagData aEnergyType, byte aSide, long aSize) {return Math.min(mRate, mEnergy);}
    @Override public long getEnergySizeOutputRecommended(TagData aEnergyType, byte aSide) {return mRate;}
    @Override public long getEnergySizeOutputMin(TagData aEnergyType, byte aSide) {return mRate;}
    @Override public long getEnergySizeOutputMax(TagData aEnergyType, byte aSide) {return mRate;}

    @Override public boolean getStateRunningPossible() {return mActive || (mTanks[0].has() && !mTanks[1].isFull());}

    // Icons，图像动画
    public final static IIconContainer[] sColoreds = new IIconContainer[] {
            new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/colored/front"),
            new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/colored/back"),
            new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/colored/sides"),
    }, sOverlays = new IIconContainer[] {
            new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/overlay/front"),
            new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/overlay/back"),
            new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/overlay/sides"),
    }, sOverlaysActiveL = new IIconContainer[] {
            new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/overlay_active_l/front"),
            new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/overlay_active_l/back"),
            new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/overlay_active_l/sides"),
    }, sOverlaysActiveR = new IIconContainer[] {
            new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/overlay_active_r/front"),
            new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/overlay_active_r/back"),
            new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/overlay_active_r/sides"),
    }, sOverlaysPreheatL = new IIconContainer[] {
            new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/overlay_preheat_l/front"),
            new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/overlay_preheat_l/back"),
            new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/overlay_preheat_l/sides"),
    }, sOverlaysPreheatR = new IIconContainer[] {
            new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/overlay_preheat_r/front"),
            new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/overlay_preheat_r/back"),
            new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/overlay_preheat_r/sides"),
    };
    @Override
    public IIconContainer[] getIIconContainers(IconType aIconType) {
        switch (aIconType) {
            case COLORED: return sColoreds;
            case OVERLAY: return sOverlays;
            case OVERLAY_ACTIVE_L: return sOverlaysActiveL;
            case OVERLAY_ACTIVE_R: return sOverlaysActiveR;
            case OVERLAY_PREHEAT_L: return sOverlaysPreheatL;
            case OVERLAY_PREHEAT_R: return sOverlaysPreheatR;
            default: return sOverlays;
        }
    }
    @Override public byte getDefaultSide() {return SIDE_FRONT;}
    @Override public boolean[] getValidSides() {return SIDES_VALID;}


    @Override public String getTileEntityName() {return "gt.multitileentity.generator.motor_liquid";}
    @Override public String getTileEntityName_CH() {return "gtch.multitileentity.generator.motor_liquid";}
}
