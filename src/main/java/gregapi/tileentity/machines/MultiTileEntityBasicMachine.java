/**
 * Copyright (c) 2023 GregTech-6 Team
 *
 * This file is part of GregTech.
 *
 * GregTech is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GregTech is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GregTech. If not, see <http://www.gnu.org/licenses/>.
 */

package gregapi.tileentity.machines;

import buildcraft.api.tiles.IHasWork;
import cpw.mods.fml.common.Optional;
import gregapi.GT_API;
import gregapi.block.multitileentity.MultiTileEntityRegistry;
import gregapi.code.TagData;
import gregapi.data.*;
import gregapi.fluid.FluidTankGT;
import gregapi.gui.ContainerClientBasicMachine;
import gregapi.gui.ContainerCommonBasicMachine;
import gregapi.old.Textures;
import gregapi.recipes.Recipe;
import gregapi.recipes.Recipe.RecipeMap;
import gregapi.render.BlockTextureDefault;
import gregapi.render.BlockTextureMulti;
import gregapi.render.IIconContainer;
import gregapi.render.ITexture;
import gregapi.tileentity.ITileEntityAdjacentInventoryUpdatable;
import gregapi.tileentity.ITileEntityFunnelAccessible;
import gregapi.tileentity.ITileEntityTapAccessible;
import gregapi.tileentity.base.TileEntityBase09FacingSingle;
import gregapi.tileentity.data.ITileEntityGibbl;
import gregapi.tileentity.data.ITileEntityProgress;
import gregapi.tileentity.delegate.DelegatorTileEntity;
import gregapi.tileentity.energy.ITileEntityEnergy;
import gregapi.util.ST;
import gregapi.util.UT;
import gregtechCH.fluid.IFluidHandler_CH;
import gregtechCH.tileentity.ITileEntityNameCompat;
import gregtechCH.tileentity.connectors.ITEInterceptAutoConnectFluid;
import gregtechCH.tileentity.connectors.ITEInterceptAutoConnectItem;
import gregtechCH.tileentity.connectors.ITEInterceptModConnectFluid;
import gregtechCH.tileentity.connectors.ITEInterceptModConnectItem;
import gregtechCH.tileentity.cores.basicmachines.IMTEC_BasicMachine;
import gregtechCH.tileentity.cores.IMTEC_ToolTips;
import gregtechCH.tileentity.cores.basicmachines.IMTEC_HasBasicMachine;
import gregtechCH.tileentity.cores.basicmachines.MTEC_BasicMachine_Greg;
import gregtechCH.tileentity.cores.basicmachines.MTEC_BasicMachine;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.fluids.*;

import java.util.Collection;
import java.util.List;

import static gregapi.data.CS.*;

/**
 * @author Gregorius Techneticies
 * 
 * This is the Base Class for almost all my Basic Machines. It is almost too simple to use.
 * 
 * In order to create a Basic Machine (Steel Shredder in this example), I use the following NBT Parameters in my MultiTileEntity System:
 * 
 * NBT_MATERIAL                     = MT.Steel
 * NBT_HARDNESS                     = 6.0F
 * NBT_RESISTANCE                   = 6.0F
 * NBT_COLOR                        = UT.Code.getRGBInt(MT.Steel.mRGBaSolid)
 * NBT_INPUT                        = 128
 * NBT_GUI                          = RES_PATH_GUI+"machines/Shredder"
 * NBT_TEXTURE                      = "shredder"
 * NBT_ENERGY_ACCEPTED              = TD.Energy.RU
 * NBT_RECIPEMAP                    = RecipeMap.sShredderRecipes
 * NBT_EFFICIENCY                   = 10000
 * NBT_INV_SIDE_INPUT               = SIDE_BITS[SIDE_TOP]
 * NBT_INV_SIDE_AUTO_INPUT          = SIDE_TOP
 * NBT_INV_SIDE_OUTPUT              = SIDE_BITS[SIDE_BOTTOM]
 * NBT_INV_SIDE_AUTO_OUTPUT         = SIDE_BOTTOM
 * NBT_ENERGY_ACCEPTED_SIDES        = SIDE_BITS[SIDE_LEFT]|SIDE_BITS[SIDE_RIGHT]
 */
@Optional.InterfaceList(value = {
	@Optional.Interface(iface = "buildcraft.api.tiles.IHasWork", modid = ModIDs.BC)
})
public class MultiTileEntityBasicMachine extends TileEntityBase09FacingSingle implements IMTEC_HasBasicMachine, IMTEC_ToolTips, ITileEntityNameCompat, ITEInterceptModConnectItem, ITEInterceptModConnectFluid, ITEInterceptAutoConnectItem, ITEInterceptAutoConnectFluid, IHasWork, ITileEntityFunnelAccessible, ITileEntityTapAccessible, ITileEntitySwitchableOnOff, ITileEntityRunningSuccessfully, ITileEntityAdjacentInventoryUpdatable, ITileEntityEnergy, ITileEntityProgress, ITileEntityGibbl, IFluidHandler_CH {
	protected MTEC_BasicMachine mCore; // 暂时使用类而不是接口
	@Override public MTEC_BasicMachine core() {return mCore;}
	
	public boolean mSpecialIsStartEnergy = F, mNoConstantEnergy = F, mCheapOverclocking = F, mCouldUseRecipe = F, mStopped = F, oActive = F, oRunning = F, mStateNew = F, mStateOld = F, mDisabledItemInput = F, mDisabledItemOutput = F, mDisabledFluidInput = F, mDisabledFluidOutput = F, mRequiresIgnition = F, mCanUseOutputTanks = F;
	public byte mEnergyInputs = 127, mEnergyOutput = SIDE_UNDEFINED, mOutputBlocked = 0, mMode = 0, mIgnited = 0;
	public byte mItemInputs   = 127, mItemOutputs  = 127, mItemAutoInput  = SIDE_UNDEFINED, mItemAutoOutput  = SIDE_UNDEFINED;
	public byte mFluidInputs  = 127, mFluidOutputs = 127, mFluidAutoInput = SIDE_UNDEFINED, mFluidAutoOutput = SIDE_UNDEFINED;
	public short mEfficiency = 10000;
	public TagData mEnergyTypeAccepted = TD.Energy.TU, mEnergyTypeEmitted = TD.Energy.QU, mEnergyTypeCharged = TD.Energy.TU;
	public Recipe mLastRecipe = null, mCurrentRecipe = null;
	public FluidTankGT[] mTanksInput = ZL_FT, mTanksOutput = ZL_FT;
	public ItemStack[] mOutputItems = ZL_IS;
	public FluidStack[] mOutputFluids = ZL_FS;
	public IIconContainer[] mTexturesMaterial = L6_IICONCONTAINER, mTexturesInactive = L6_IICONCONTAINER, mTexturesActive = L6_IICONCONTAINER, mTexturesRunning = L6_IICONCONTAINER;
	
	public String mGUITexture = "";
	public RecipeMap mRecipes = RM.Furnace;
	public boolean mSuccessful = F, mActive = F, mRunning = F;
	
	@Override
	public void readFromNBT2(NBTTagCompound aNBT) {
		// GTCH, core init
		if (mCore == null) mCore = new MTEC_BasicMachine(this);
		super.readFromNBT2(aNBT);
		
		mGUITexture = mRecipes.mGUIPath;
		if (aNBT.hasKey(NBT_ACTIVE)) mCouldUseRecipe = mActive = aNBT.getBoolean(NBT_ACTIVE);
		if (aNBT.hasKey(NBT_STOPPED)) mStopped = aNBT.getBoolean(NBT_STOPPED);
		if (aNBT.hasKey(NBT_RUNNING)) mRunning = aNBT.getBoolean(NBT_RUNNING);
		if (aNBT.hasKey(NBT_STATE+".new")) mStateNew = aNBT.getBoolean(NBT_STATE+".new");
		if (aNBT.hasKey(NBT_STATE+".old")) mStateOld = aNBT.getBoolean(NBT_STATE+".old");
		if (aNBT.hasKey(NBT_NEEDS_IGNITION)) mRequiresIgnition = aNBT.getBoolean(NBT_NEEDS_IGNITION);
		if (aNBT.hasKey(NBT_CHEAP_OVERCLOCKING)) mCheapOverclocking = aNBT.getBoolean(NBT_CHEAP_OVERCLOCKING);
		if (aNBT.hasKey(NBT_NO_CONSTANT_POWER)) mNoConstantEnergy = aNBT.getBoolean(NBT_NO_CONSTANT_POWER);
		if (aNBT.hasKey(NBT_SPECIAL_IS_START_ENERGY)) mSpecialIsStartEnergy = aNBT.getBoolean(NBT_SPECIAL_IS_START_ENERGY);
		if (aNBT.hasKey(NBT_EFFICIENCY)) mEfficiency = (short)UT.Code.bind_(0, 10000, aNBT.getShort(NBT_EFFICIENCY));
		if (aNBT.hasKey(NBT_USE_OUTPUT_TANK)) mCanUseOutputTanks = aNBT.getBoolean(NBT_USE_OUTPUT_TANK);
		if (aNBT.hasKey(NBT_MODE)) mMode = aNBT.getByte(NBT_MODE);
		if (aNBT.hasKey(NBT_IGNITION)) mIgnited = aNBT.getByte(NBT_IGNITION);
		if (aNBT.hasKey(NBT_INV_SIDE_IN)) mItemInputs = (byte)(aNBT.getByte(NBT_INV_SIDE_IN) | SBIT_A);
		if (aNBT.hasKey(NBT_INV_SIDE_OUT)) mItemOutputs = (byte)(aNBT.getByte(NBT_INV_SIDE_OUT) | SBIT_A);
		if (aNBT.hasKey(NBT_INV_SIDE_AUTO_IN)) mItemAutoInput = aNBT.getByte(NBT_INV_SIDE_AUTO_IN);
		if (aNBT.hasKey(NBT_INV_SIDE_AUTO_OUT)) mItemAutoOutput = aNBT.getByte(NBT_INV_SIDE_AUTO_OUT);
		if (aNBT.hasKey(NBT_INV_DISABLED_IN)) mDisabledItemInput = aNBT.getBoolean(NBT_INV_DISABLED_IN);
		if (aNBT.hasKey(NBT_INV_DISABLED_OUT)) mDisabledItemOutput = aNBT.getBoolean(NBT_INV_DISABLED_OUT);
		if (aNBT.hasKey(NBT_TANK_SIDE_IN)) mFluidInputs = (byte)(aNBT.getByte(NBT_TANK_SIDE_IN) | SBIT_A);
		if (aNBT.hasKey(NBT_TANK_SIDE_OUT)) mFluidOutputs = (byte)(aNBT.getByte(NBT_TANK_SIDE_OUT) | SBIT_A);
		if (aNBT.hasKey(NBT_TANK_SIDE_AUTO_IN)) mFluidAutoInput = aNBT.getByte(NBT_TANK_SIDE_AUTO_IN);
		if (aNBT.hasKey(NBT_TANK_SIDE_AUTO_OUT)) mFluidAutoOutput = aNBT.getByte(NBT_TANK_SIDE_AUTO_OUT);
		if (aNBT.hasKey(NBT_TANK_DISABLED_IN)) mDisabledFluidInput = aNBT.getBoolean(NBT_TANK_DISABLED_IN);
		if (aNBT.hasKey(NBT_TANK_DISABLED_OUT)) mDisabledFluidOutput = aNBT.getBoolean(NBT_TANK_DISABLED_OUT);
		if (aNBT.hasKey(NBT_ENERGY_ACCEPTED)) mEnergyTypeAccepted = TagData.createTagData(aNBT.getString(NBT_ENERGY_ACCEPTED));
		if (aNBT.hasKey(NBT_ENERGY_ACCEPTED_2)) mEnergyTypeCharged = TagData.createTagData(aNBT.getString(NBT_ENERGY_ACCEPTED_2));
		if (aNBT.hasKey(NBT_ENERGY_ACCEPTED_SIDES)) mEnergyInputs = (byte)(aNBT.getByte(NBT_ENERGY_ACCEPTED_SIDES) | SBIT_A);
		if (aNBT.hasKey(NBT_ENERGY_EMITTED)) mEnergyTypeEmitted = TagData.createTagData(aNBT.getString(NBT_ENERGY_EMITTED));
		if (aNBT.hasKey(NBT_ENERGY_EMITTED_SIDES)) mEnergyOutput = aNBT.getByte(NBT_ENERGY_EMITTED_SIDES);
		
		long tCapacity = 1000;
		if (aNBT.hasKey(NBT_TANK_CAPACITY)) tCapacity = UT.Code.bindInt(aNBT.getLong(NBT_TANK_CAPACITY));
		mTanksInput = new FluidTankGT[mRecipes.mInputFluidCount];
		for (int i = 0; i < mTanksInput.length; i++) mTanksInput[i] = new FluidTankGT(tCapacity).setCapacity(mRecipes, mCore.mParallel * 2L).readFromNBT(aNBT, NBT_TANK+".in."+i);
		mTanksOutput = new FluidTankGT[mRecipes.mOutputFluidCount];
		for (int i = 0; i < mTanksOutput.length; i++) mTanksOutput[i] = new FluidTankGT().readFromNBT(aNBT, NBT_TANK+".out."+i);
		
		mOutputFluids = new FluidStack[mRecipes.mOutputFluidCount];
		for (int i = 0; i < mOutputFluids.length; i++) mOutputFluids[i] = FL.load(aNBT, NBT_TANK_OUT+"."+i);
		mOutputItems = new ItemStack[mRecipes.mOutputItemsCount];
		for (int i = 0; i < mOutputItems.length; i++) mOutputItems[i] = ST.load(aNBT, NBT_INV_OUT+"."+i);
		
		if (CODE_CLIENT) {
			if (aNBT.hasKey(NBT_GUI)) {
				mGUITexture = aNBT.getString(NBT_GUI);
				if (!mGUITexture.endsWith(".png")) mGUITexture += ".png";
			}
			if (GT_API.sBlockIcons == null && aNBT.hasKey(NBT_TEXTURE)) {
				String tTextureName = aNBT.getString(NBT_TEXTURE);
				mTexturesMaterial = new IIconContainer[] {
				new Textures.BlockIcons.CustomIcon("machines/basicmachines/"+tTextureName+"/colored/bottom"),
				new Textures.BlockIcons.CustomIcon("machines/basicmachines/"+tTextureName+"/colored/top"),
				new Textures.BlockIcons.CustomIcon("machines/basicmachines/"+tTextureName+"/colored/left"),
				new Textures.BlockIcons.CustomIcon("machines/basicmachines/"+tTextureName+"/colored/front"),
				new Textures.BlockIcons.CustomIcon("machines/basicmachines/"+tTextureName+"/colored/right"),
				new Textures.BlockIcons.CustomIcon("machines/basicmachines/"+tTextureName+"/colored/back")};
				mTexturesInactive = new IIconContainer[] {
				new Textures.BlockIcons.CustomIcon("machines/basicmachines/"+tTextureName+"/overlay/bottom"),
				new Textures.BlockIcons.CustomIcon("machines/basicmachines/"+tTextureName+"/overlay/top"),
				new Textures.BlockIcons.CustomIcon("machines/basicmachines/"+tTextureName+"/overlay/left"),
				new Textures.BlockIcons.CustomIcon("machines/basicmachines/"+tTextureName+"/overlay/front"),
				new Textures.BlockIcons.CustomIcon("machines/basicmachines/"+tTextureName+"/overlay/right"),
				new Textures.BlockIcons.CustomIcon("machines/basicmachines/"+tTextureName+"/overlay/back")};
				mTexturesActive = new IIconContainer[] {
				new Textures.BlockIcons.CustomIcon("machines/basicmachines/"+tTextureName+"/overlay_active/bottom"),
				new Textures.BlockIcons.CustomIcon("machines/basicmachines/"+tTextureName+"/overlay_active/top"),
				new Textures.BlockIcons.CustomIcon("machines/basicmachines/"+tTextureName+"/overlay_active/left"),
				new Textures.BlockIcons.CustomIcon("machines/basicmachines/"+tTextureName+"/overlay_active/front"),
				new Textures.BlockIcons.CustomIcon("machines/basicmachines/"+tTextureName+"/overlay_active/right"),
				new Textures.BlockIcons.CustomIcon("machines/basicmachines/"+tTextureName+"/overlay_active/back")};
				mTexturesRunning = new IIconContainer[] {
				new Textures.BlockIcons.CustomIcon("machines/basicmachines/"+tTextureName+"/overlay_running/bottom"),
				new Textures.BlockIcons.CustomIcon("machines/basicmachines/"+tTextureName+"/overlay_running/top"),
				new Textures.BlockIcons.CustomIcon("machines/basicmachines/"+tTextureName+"/overlay_running/left"),
				new Textures.BlockIcons.CustomIcon("machines/basicmachines/"+tTextureName+"/overlay_running/front"),
				new Textures.BlockIcons.CustomIcon("machines/basicmachines/"+tTextureName+"/overlay_running/right"),
				new Textures.BlockIcons.CustomIcon("machines/basicmachines/"+tTextureName+"/overlay_running/back")};
			} else {
				TileEntity tCanonicalTileEntity = MultiTileEntityRegistry.getCanonicalTileEntity(getMultiTileEntityRegistryID(), getMultiTileEntityID());
				if (tCanonicalTileEntity instanceof MultiTileEntityBasicMachine) {
					mTexturesMaterial = ((MultiTileEntityBasicMachine)tCanonicalTileEntity).mTexturesMaterial;
					mTexturesInactive = ((MultiTileEntityBasicMachine)tCanonicalTileEntity).mTexturesInactive;
					mTexturesRunning  = ((MultiTileEntityBasicMachine)tCanonicalTileEntity).mTexturesRunning;
					mTexturesActive   = ((MultiTileEntityBasicMachine)tCanonicalTileEntity).mTexturesActive;
				} else {
					mTexturesMaterial = mTexturesInactive = mTexturesRunning = mTexturesActive = L6_IICONCONTAINER;
				}
			}
		}
		
		updateAccessibleSlots();
		
		// GTCH, core init
		mCore.readFromNBT(aNBT);
	}
	
	@Override
	public void writeToNBT2(NBTTagCompound aNBT) {
		super.writeToNBT2(aNBT);
		
		UT.NBT.setBoolean(aNBT, NBT_ACTIVE, mActive);
		UT.NBT.setBoolean(aNBT, NBT_RUNNING, mRunning);
		UT.NBT.setBoolean(aNBT, NBT_STOPPED, mStopped);
		UT.NBT.setBoolean(aNBT, NBT_STATE+".new", mStateNew);
		UT.NBT.setBoolean(aNBT, NBT_STATE+".old", mStateOld);
		
		UT.NBT.setNumber(aNBT, NBT_MODE, mMode);
		UT.NBT.setNumber(aNBT, NBT_IGNITION, mIgnited);
		UT.NBT.setBoolean(aNBT, NBT_INV_DISABLED_IN, mDisabledItemInput);
		UT.NBT.setBoolean(aNBT, NBT_INV_DISABLED_OUT, mDisabledItemOutput);
		UT.NBT.setBoolean(aNBT, NBT_TANK_DISABLED_IN, mDisabledFluidInput);
		UT.NBT.setBoolean(aNBT, NBT_TANK_DISABLED_OUT, mDisabledFluidOutput);
		
		for (int i = 0; i < mTanksInput  .length; i++) mTanksInput [i].writeToNBT(aNBT, NBT_TANK+".in." +i);
		for (int i = 0; i < mTanksOutput .length; i++) mTanksOutput[i].writeToNBT(aNBT, NBT_TANK+".out."+i);
		for (int i = 0; i < mOutputFluids.length; i++) FL.save(aNBT, NBT_TANK_OUT+"."+i, mOutputFluids[i]);
		for (int i = 0; i < mOutputItems .length; i++) ST.save(aNBT, NBT_INV_OUT +"."+i, mOutputItems [i]);
		
		// GTCH, core save
		mCore.writeToNBT(aNBT);
	}
	
	@Override
	public NBTTagCompound writeItemNBT2(NBTTagCompound aNBT) {
		// GTCH, core save
		mCore.writeItemNBT(aNBT);
		
		UT.NBT.setNumber(aNBT, NBT_MODE, mMode);
		UT.NBT.setBoolean(aNBT, NBT_INV_DISABLED_IN, mDisabledItemInput);
		UT.NBT.setBoolean(aNBT, NBT_INV_DISABLED_OUT, mDisabledItemOutput);
		UT.NBT.setBoolean(aNBT, NBT_TANK_DISABLED_IN, mDisabledFluidInput);
		UT.NBT.setBoolean(aNBT, NBT_TANK_DISABLED_OUT, mDisabledFluidOutput);
		return super.writeItemNBT2(aNBT);
	}
	
	// tooltips
	@Override public final void addToolTips(List<String> aList, ItemStack aStack, boolean aF3_H) {IMTEC_ToolTips.Util.addToolTips(this, aList, aStack, aF3_H); super.addToolTips(aList, aStack, aF3_H);}
	// 这种写法可以既让子类实体可以用额外的 core 来重写 tooltips，又可以让 core 的重写来补充 tooltips
	@Override public void toolTipsMultiblock(List<String> aList) {mCore.toolTipsMultiblock(aList);}
	@Override public void toolTipsRecipe(List<String> aList) {mCore.toolTipsRecipe(aList);}
	@Override public void toolTipsEnergy(List<String> aList) {mCore.toolTipsEnergy(aList);}
	@Override public void toolTipsUseful(List<String> aList) {mCore.toolTipsUseful(aList);}
	@Override public void toolTipsImportant(List<String> aList) {mCore.toolTipsImportant(aList);}
	@Override public void toolTipsHazard(List<String> aList) {mCore.toolTipsHazard(aList);}
	@Override public void toolTipsOther(List<String> aList, ItemStack aStack, boolean aF3_H) {mCore.toolTipsOther(aList, aStack, aF3_H);}
	@Override public void addToolTipsSided(List<String> aList, ItemStack aStack, boolean aF3_H) {mCore.addToolTipsSided(aList, aStack, aF3_H);}
	
	
	public long onToolClick3(String aTool, long aRemainingDurability, long aQuality, Entity aPlayer, List<String> aChatReturn, IInventory aPlayerInventory, boolean aSneaking, ItemStack aStack, byte aSide, float aHitX, float aHitY, float aHitZ, ChunkCoordinates aFrom) {
		return onToolClick2(aTool, aRemainingDurability, aQuality, aPlayer, aChatReturn, aPlayerInventory, aSneaking, aStack, aSide, aHitX, aHitY, aHitZ);
	}
	
	@Override
	public long onToolClick2(String aTool, long aRemainingDurability, long aQuality, Entity aPlayer, List<String> aChatReturn, IInventory aPlayerInventory, boolean aSneaking, ItemStack aStack, byte aSide, float aHitX, float aHitY, float aHitZ) {
		long rReturn = super.onToolClick2(aTool, aRemainingDurability, aQuality, aPlayer, aChatReturn, aPlayerInventory, aSneaking, aStack, aSide, aHitX, aHitY, aHitZ);
		if (rReturn > 0) return rReturn;
		
		if (isClientSide()) return 0;
		
		if (aTool.equals(TOOL_screwdriver)) {
			mMode = (byte)((mMode + 1) % 4);
			aChatReturn.add("========================================");
			aChatReturn.add((mMode & 1) != 0 ?"Only produce when Output is completely empty":"Produce whenever there is space");
			aChatReturn.add((mMode & 2) != 0 ?"Only accept Input on empty Input Slots":"Accept Input on all Input Slots");
			updateInventory();
			return 10000;
		}
		if (aTool.equals(TOOL_softhammer)) {
			mCore.mProgress = mCore.mMinEnergy = mCore.mMaxProgress = mCore.mOutputEnergy = mCore.mChargeRequirement = 0;
			mOutputFluids = ZL_FS;
			mOutputItems = ZL_IS;
			updateInventory();
			return 10000;
		}
		if (aTool.equals(TOOL_igniter)) {
			if (mRequiresIgnition) {
				mIgnited = 40;
				return 10000;
			}
			return 0;
		}
		if (aTool.equals(TOOL_plunger)) {
			updateInventory();
			for (FluidTankGT tTank : mTanksOutput) {long rAmount = GarbageGT.trash(tTank, 1000); if (rAmount > 0) return rAmount;}
			for (FluidTankGT tTank : mTanksInput ) {long rAmount = GarbageGT.trash(tTank, 1000); if (rAmount > 0) return rAmount;}
		}
		if (aTool.equals(TOOL_monkeywrench)) {
			long rOutput = 0;
			if (FACING_TO_SIDE[mFacing][mItemAutoInput] == aSide) {
				mDisabledItemInput = !mDisabledItemInput;
				aChatReturn.add(mDisabledItemInput?"Auto Item Input Disabled":"Auto Item Input Enabled");
				rOutput += 10000;
			}
			if (FACING_TO_SIDE[mFacing][mItemAutoOutput] == aSide) {
				mDisabledItemOutput = !mDisabledItemOutput;
				aChatReturn.add(mDisabledItemOutput?"Auto Item Output Disabled":"Auto Item Output Enabled");
				rOutput += 10000;
			}
			if (FACING_TO_SIDE[mFacing][mFluidAutoInput] == aSide) {
				mDisabledFluidInput = !mDisabledFluidInput;
				aChatReturn.add(mDisabledFluidInput?"Auto Fluid Input Disabled":"Auto Fluid Input Enabled");
				rOutput += 10000;
			}
			if (FACING_TO_SIDE[mFacing][mFluidAutoOutput] == aSide) {
				mDisabledFluidOutput = !mDisabledFluidOutput;
				aChatReturn.add(mDisabledFluidOutput?"Auto Fluid Output Disabled":"Auto Fluid Output Enabled");
				rOutput += 10000;
			}
			if (rOutput > 0) {
				updateInventory();
				return rOutput;
			}
		}
		if (aTool.equals(TOOL_magnifyingglass)) {
			if (aChatReturn != null) onMagnifyingGlass(aChatReturn);
			return 1;
		}
		return 0;
	}
	
	public void onMagnifyingGlass(List<String> aChatReturn) {mCore.onMagnifyingGlass(aChatReturn);}
	
	@Override
	public void onCoordinateChange() {
		updateAdjacentToggleableEnergySources();
		checkStructure(T);
	}
	
	@Override
	public void onTickFailed(long aTimer, boolean aIsServerSide) {
		super.onTickFailed(aTimer, aIsServerSide);
		// Just to prevent Infinite Item dupes in case this happens during the Processing Functions.
		mCore.mProgress = mCore.mMinEnergy = mCore.mMaxProgress = mCore.mOutputEnergy = mCore.mChargeRequirement = 0;
		mOutputFluids = ZL_FS;
		mOutputItems = ZL_IS;
	}
	
	@Override
	public void onTickFirst2(boolean aIsServerSide) {
		super.onTickFirst2(aIsServerSide);
		if (aIsServerSide) {
			if (checkStructure(T) && !mActive) checkRecipe(F, mRunning || mStopped);
		}
	}
	
	@Override
	public void onTick2(long aTimer, boolean aIsServerSide) {
		if (aIsServerSide) {
			if (mBlockUpdated) updateAdjacentToggleableEnergySources();
			
			if (mTimer % 600 == 5) {
				if (!checkStructure(F)) checkStructure(T);
				if (isStructureOkay() && mRunning) doDefaultStructuralChecks();
			} else {
				checkStructure(F); // 为了保证结构能够及时失效，这里每 tick 都要检测一次结构是否改变并且在改变后进行检测。也能保证 only 的检测不会有过多的无用检测
			}
			
			if (!mStopped) {
				if (mEnergyTypeAccepted == TD.Energy.TU) ++mCore.mEnergy;
				if (mCore.mChargeRequirement > 0 && mEnergyTypeCharged == TD.Energy.TU) --mCore.mChargeRequirement;
			}
			
			if (!mDisabledFluidOutput && SIDES_VALID[mFluidAutoOutput]) doOutputFluids();
			
			doWork(aTimer);
			
			for (int i = 0; i < mTanksInput .length; i++) slot(mRecipes.mInputItemsCount + mRecipes.mOutputItemsCount + 1 + i                       , FL.display(mTanksInput [i], T, T));
			for (int i = 0; i < mTanksOutput.length; i++) slot(mRecipes.mInputItemsCount + mRecipes.mOutputItemsCount + 1 + i + mTanksInput.length  , FL.display(mTanksOutput[i], T, T));
		}
	}
	
	@Override
	public boolean onTickCheck(long aTimer) {
		return mActive != oActive || mRunning != oRunning || super.onTickCheck(aTimer);
	}
	
	@Override
	public void onTickResetChecks(long aTimer, boolean aIsServerSide) {
		super.onTickResetChecks(aTimer, aIsServerSide);
		oRunning = mRunning;
		oActive  = mActive;
	}
	
	@Override
	public boolean onBlockActivated3(EntityPlayer aPlayer, byte aSide, float aHitX, float aHitY, float aHitZ) {
		if (isServerSide()) openGUI(aPlayer, aSide);
		return T;
	}
	
	@Override
	public long doInject(TagData aEnergyType, byte aSide, long aSize, long aAmount, boolean aDoInject) {
		if (mStopped) return 0;
		boolean tPositive = (aSize > 0);
		aSize = Math.abs(aSize);
		if (aSize > getEnergySizeInputMax(aEnergyType, aSide)) {
			if (aDoInject) overcharge(aSize, aEnergyType);
			return aAmount;
		}
		if (aEnergyType == mEnergyTypeCharged && mCore.mChargeRequirement > 0) {
			if (aDoInject) mCore.mChargeRequirement -= aSize * aAmount;
			return aAmount;
		}
		if (aEnergyType == mEnergyTypeAccepted) {
			if (aDoInject) mStateNew = tPositive;
			long tInput = Math.min(mCore.mInputMax - mCore.mEnergy, aSize * aAmount), tConsumed = Math.min(aAmount, (tInput/aSize) + (tInput%aSize!=0?1:0));
			if (aDoInject) mCore.mEnergy += tConsumed * aSize;
			return tConsumed;
		}
		return 0;
	}
	
	@Override public boolean isEnergyType                   (TagData aEnergyType, byte aSide, boolean aEmitting) {return aEmitting ? aEnergyType == mEnergyTypeEmitted : aEnergyType == mEnergyTypeAccepted || aEnergyType == mEnergyTypeCharged;}
	@Override public boolean isEnergyAcceptingFrom          (TagData aEnergyType, byte aSide, boolean aTheoretical) {return (aTheoretical || !mStopped) &&                   FACE_CONNECTED[FACING_ROTATIONS[mFacing][aSide]][mEnergyInputs] && super.isEnergyAcceptingFrom(aEnergyType, aSide, aTheoretical);}
	@Override public boolean isEnergyEmittingTo             (TagData aEnergyType, byte aSide, boolean aTheoretical) {return (aTheoretical || !mStopped) && (SIDES_INVALID[mEnergyOutput] || FACING_ROTATIONS[mFacing][aSide]==mEnergyOutput) && super.isEnergyEmittingTo   (aEnergyType, aSide, aTheoretical);}
	@Override public long getEnergySizeInputMin             (TagData aEnergyType, byte aSide) {return mCore.mInputMin;}
	@Override public long getEnergySizeInputRecommended     (TagData aEnergyType, byte aSide) {return mCore.mInput;}
	@Override public long getEnergySizeInputMax             (TagData aEnergyType, byte aSide) {return mCore.mInputMax;}
	@Override public long getEnergySizeOutputMin            (TagData aEnergyType, byte aSide) {return 1;}
	@Override public long getEnergySizeOutputRecommended    (TagData aEnergyType, byte aSide) {return mCore.mInputMax;}
	@Override public long getEnergySizeOutputMax            (TagData aEnergyType, byte aSide) {return Integer.MAX_VALUE;}
	@Override public Collection<TagData> getEnergyTypes(byte aSide) {return mEnergyTypeAccepted.AS_LIST;}
	
	// Inventory Stuff
	
	@Override
	public ItemStack[] getDefaultInventory(NBTTagCompound aNBT) {
		if (aNBT.hasKey(NBT_RECIPEMAP)) mRecipes = RecipeMap.RECIPE_MAPS.get(aNBT.getString(NBT_RECIPEMAP));
		ACCESSIBLE_SLOTS = UT.Code.getAscendingArray(mRecipes.mInputItemsCount + mRecipes.mOutputItemsCount);
		ACCESSIBLE_INPUTS = UT.Code.getAscendingArray(mRecipes.mInputItemsCount);
		ACCESSIBLE_OUTPUTS = new int[mRecipes.mOutputItemsCount];
		for (int i = 0; i < ACCESSIBLE_OUTPUTS.length; i++) ACCESSIBLE_OUTPUTS[i] = i + mRecipes.mInputItemsCount;
		return new ItemStack[mRecipes.mInputItemsCount + mRecipes.mOutputItemsCount + 1 + mRecipes.mInputFluidCount + mRecipes.mOutputFluidCount];
	}
	
	public void updateAccessibleSlots() {
		for (byte i = 0; i < ACCESSIBLE.length; i++) {
			if (FACE_CONNECTED[FACING_ROTATIONS[mFacing][i]][mItemInputs]) {
				if (FACE_CONNECTED[FACING_ROTATIONS[mFacing][i]][mItemOutputs]) ACCESSIBLE[i] = ACCESSIBLE_SLOTS; else ACCESSIBLE[i] = ACCESSIBLE_INPUTS;
			} else {
				if (FACE_CONNECTED[FACING_ROTATIONS[mFacing][i]][mItemOutputs]) ACCESSIBLE[i] = ACCESSIBLE_OUTPUTS; else ACCESSIBLE[i] = ZL_INTEGER;
			}
		}
	}
	
	public int[][] ACCESSIBLE = new int[7][];
	public int[] ACCESSIBLE_SLOTS, ACCESSIBLE_INPUTS, ACCESSIBLE_OUTPUTS;
	@Override public int[] getAccessibleSlotsFromSide2(byte aSide) {return ACCESSIBLE[aSide];}
	@Override public boolean canDrop(int aInventorySlot) {return aInventorySlot < mRecipes.mInputItemsCount + mRecipes.mOutputItemsCount + 1;}
	
	@Override
	public boolean canInsertItem2(int aSlot, ItemStack aStack, byte aSide) {
		if (aSlot >= mRecipes.mInputItemsCount) return F;
		if ((mMode & 2) != 0 && slotHas(aSlot)) return F;
		for (int i = 0; i < mRecipes.mInputItemsCount; i++) if (ST.equal(aStack, slot(i), T)) return i == aSlot;
		return mRecipes.containsInput(aStack, this, slot(mRecipes.mInputItemsCount + mRecipes.mOutputItemsCount));
	}
	
	@Override
	public boolean canExtractItem2(int aSlot, ItemStack aStack, byte aSide) {
		return aSlot >= mRecipes.mInputItemsCount && aSlot < mRecipes.mInputItemsCount + mRecipes.mOutputItemsCount;
	}
	
	// Tank things
	
	@Override
	public IFluidTank getFluidTankFillable2(byte aSide, FluidStack aFluidToFill) {
		if (!mDisabledFluidOutput && SIDES_VALID[mFluidAutoOutput] && FACING_TO_SIDE[mFacing][mFluidAutoOutput] == aSide) return null;
		if (!FACE_CONNECTED[FACING_ROTATIONS[mFacing][aSide]][mFluidInputs]) return null;
		for (FluidTankGT fluidTankGT : mTanksInput) if (fluidTankGT.contains(aFluidToFill)) return fluidTankGT;
		if (!mRecipes.containsInput(aFluidToFill, this, slot(mRecipes.mInputItemsCount + mRecipes.mOutputItemsCount))) return null;
		for (FluidTankGT fluidTankGT : mTanksInput) if (fluidTankGT.isEmpty()) return fluidTankGT;
		return null;
	}
	
	@Override
	public IFluidTank getFluidTankDrainable2(byte aSide, FluidStack aFluidToDrain) {
		if (!FACE_CONNECTED[FACING_ROTATIONS[mFacing][aSide]][mFluidOutputs]) return null;
		if (aFluidToDrain == null) {
			for (FluidTankGT fluidTankGT : mTanksOutput) if (fluidTankGT.has()) return fluidTankGT;
		} else {
			for (FluidTankGT fluidTankGT : mTanksOutput) if (fluidTankGT.contains(aFluidToDrain)) return fluidTankGT;
		}
		return null;
	}
	
	@Override
	public IFluidTank[] getFluidTanks2(byte aSide) {
		if (FACE_CONNECTED[FACING_ROTATIONS[mFacing][aSide]][mFluidInputs]) {
			if (FACE_CONNECTED[FACING_ROTATIONS[mFacing][aSide]][mFluidOutputs]) {
				IFluidTank[] rTanks = new IFluidTank[mTanksInput.length + mTanksOutput.length];
				System.arraycopy(mTanksInput, 0, rTanks, 0, mTanksInput.length);
				System.arraycopy(mTanksOutput, 0, rTanks, mTanksInput.length, mTanksOutput.length);
				return rTanks;
			}
			return mTanksInput;
		}
		if (FACE_CONNECTED[FACING_ROTATIONS[mFacing][aSide]][mFluidOutputs]) return mTanksOutput;
		return ZL_FT;
	}
	// GTCH, 阻止非自动输入输出面的自动连接
	@Override public boolean interceptAutoConnectItem(byte aSide)  {return mCore.interceptAutoConnectItem(aSide);}
	@Override public boolean interceptAutoConnectFluid(byte aSide) {return mCore.interceptAutoConnectFluid(aSide);}
	// GTCH, 不能输入和输出的面阻止 MOD 管道连接
	@Override public boolean interceptModConnectItem(byte aSide)   {return mCore.interceptModConnectItem(aSide);}
	@Override public boolean interceptModConnectFluid(byte aSide)  {return mCore.interceptModConnectFluid(aSide);}
	
	@Override
	public boolean breakBlock() {
		setStateOnOff(T);
		GarbageGT.trash(mTanksInput);
		GarbageGT.trash(mTanksOutput);
		GarbageGT.trash(mOutputItems);
		GarbageGT.trash(mOutputFluids);
		return super.breakBlock();
	}
	
	public void updateAdjacentToggleableEnergySources() {
		for (byte tSide : ALL_SIDES_VALID) if (isEnergyAcceptingFrom(mEnergyTypeAccepted, tSide, T)) {
			DelegatorTileEntity<TileEntity> tDelegator = getAdjacentTileEntity(tSide);
			if (tDelegator.mTileEntity instanceof ITileEntityAdjacentOnOff && tDelegator.mTileEntity instanceof ITileEntityEnergy && ((ITileEntityEnergy)tDelegator.mTileEntity).isEnergyEmittingTo(mEnergyTypeAccepted, tDelegator.mSideOfTileEntity, T)) {
				((ITileEntityAdjacentOnOff)tDelegator.mTileEntity).setAdjacentOnOff(getStateOnOff());
			}
		}
	}
	
	// Override the code in MTEC_BasicMachine instead
//	public final int canOutput(Recipe aRecipe) {return mCore.canOutput(aRecipe);}
	public final int checkRecipe(boolean aApplyRecipe, boolean aUseAutoIO) {return mCore.checkRecipe(aApplyRecipe, aUseAutoIO);}
	public final void doWork(long aTimer) {IMTEC_BasicMachine.Util.doWork(mCore, aTimer);}
//	public final boolean doActive(long aTimer, long aEnergy) {return mCore.doActive(aTimer, aEnergy);}
//	public final boolean doInactive(long aTimer) {return mCore.doInactive(aTimer);}
	
	@Override
	public int funnelFill(byte aSide, FluidStack aFluid, boolean aDoFill) {
		for (FluidTankGT tTank : mTanksInput) if (tTank.contains(aFluid)) {
			updateInventory();
			return tTank.fill(aFluid, aDoFill);
		}
		if (!mRecipes.containsInput(aFluid, this, slot(mRecipes.mInputItemsCount + mRecipes.mOutputItemsCount))) return 0;
		for (FluidTankGT tTank : mTanksInput) if (tTank.isEmpty()) {
			updateInventory();
			return tTank.fill(aFluid, aDoFill);
		}
		return 0;
	}
	
	@Override
	public FluidStack tapDrain(byte aSide, int aMaxDrain, boolean aDoDrain) {
		for (FluidTankGT tTank : mTanksOutput) if (tTank.has() && !FL.gas(tTank)) {
			updateInventory();
			return tTank.drain(aMaxDrain, aDoDrain);
		}
		for (FluidTankGT tTank : mTanksInput) if (tTank.has() && !FL.gas(tTank)) {
			updateInventory();
			return tTank.drain(aMaxDrain, aDoDrain);
		}
		for (FluidTankGT tTank : mTanksOutput) if (tTank.has()) {
			updateInventory();
			return tTank.drain(aMaxDrain, aDoDrain);
		}
		for (FluidTankGT tTank : mTanksInput) if (tTank.has()) {
			updateInventory();
			return tTank.drain(aMaxDrain, aDoDrain);
		}
		return null;
	}
	
	@Override
	public FluidStack nozzleDrain(byte aSide, int aMaxDrain, boolean aDoDrain) {
		for (FluidTankGT tTank : mTanksOutput) if (tTank.has() && FL.gas(tTank)) {
			updateInventory();
			return tTank.drain(aMaxDrain, aDoDrain);
		}
		for (FluidTankGT tTank : mTanksInput) if (tTank.has() && FL.gas(tTank)) {
			updateInventory();
			return tTank.drain(aMaxDrain, aDoDrain);
		}
		for (FluidTankGT tTank : mTanksOutput) if (tTank.has()) {
			updateInventory();
			return tTank.drain(aMaxDrain, aDoDrain);
		}
		for (FluidTankGT tTank : mTanksInput) if (tTank.has()) {
			updateInventory();
			return tTank.drain(aMaxDrain, aDoDrain);
		}
		return null;
	}
	
	public boolean doSoundInterrupt() {
		return UT.Sounds.send(mRequiresIgnition?SFX.MC_FIZZ:mNoConstantEnergy?SFX.IC_MACHINE_INTERRUPT:SFX.MC_CLICK, this);
	}
	
	public boolean checkStructure(boolean aForceReset) {return T;}
	public boolean isStructureOkay() {return T;}
	
	public DelegatorTileEntity<IInventory> getItemInputTarget(byte aSide) {
		return getAdjacentInventory(aSide);
	}
	
	public DelegatorTileEntity<TileEntity> getItemOutputTarget(byte aSide) {
		return getAdjacentTileEntity(aSide);
	}
	
	public DelegatorTileEntity<IFluidHandler> getFluidInputTarget(byte aSide) {
		return getAdjacentTank(aSide);
	}
	
	public DelegatorTileEntity<IFluidHandler> getFluidOutputTarget(byte aSide, Fluid aOutput) {
		return getAdjacentTank(aSide);
	}
	
	public void doInputItems() {
		if (mDisabledItemInput) return;
		byte tAutoInput = FACING_TO_SIDE[mFacing][mItemAutoInput];
		if (SIDES_VALID[tAutoInput]) ST.moveAll(getItemInputTarget(tAutoInput), delegator(tAutoInput));
	}
	
	public void doOutputItems() {
		if (mDisabledItemOutput) return;
		byte tAutoOutput = FACING_TO_SIDE[mFacing][mItemAutoOutput];
		if (SIDES_VALID[tAutoOutput]) ST.moveAll(delegator(tAutoOutput), getItemOutputTarget(tAutoOutput));
	}
	
	public void doOutputFluids() {
		for (FluidTankGT tCheck : mTanksOutput) if (tCheck.has()) {if (FL.move(tCheck, getFluidOutputTarget(FACING_TO_SIDE[mFacing][mFluidAutoOutput], tCheck.fluid())) > 0) updateInventory();}
	}
	
	public void doOutputEnergy() {
		ITileEntityEnergy.Util.emitEnergyToSide(mEnergyTypeEmitted, FACING_TO_SIDE[mFacing][mEnergyOutput], mCore.mOutputEnergy, 1, this);
	}
	
	public void onProcessStarted () {/**/}
	public void onProcessFinished() {/**/}
	
	@Override public void onFacingChange(byte aPreviousFacing) {updateAccessibleSlots();}
	
	@Override public Object getGUIClient2(int aGUIID, EntityPlayer aPlayer) {return new ContainerClientBasicMachine(aPlayer.inventory, this, mRecipes, aGUIID, mGUITexture);}
	@Override public Object getGUIServer2(int aGUIID, EntityPlayer aPlayer) {return new ContainerCommonBasicMachine(aPlayer.inventory, this, mRecipes, aGUIID);}
	
	@Override public byte getVisualData() {return (byte)((mActive?1:0)|(mRunning?2:0));}
	@Override public void setVisualData(byte aData) {mRunning=((aData&2)!=0); mActive=((aData&1)!=0);}
	@Override public byte getDefaultSide() {return SIDE_FRONT;}
	@Override public boolean[] getValidSides() {return mActive ? SIDES_THIS[mFacing] : SIDES_HORIZONTAL;}
	@Override public ITexture getTexture2(Block aBlock, int aRenderPass, byte aSide, boolean[] aShouldSideBeRendered) {return aShouldSideBeRendered[aSide] ? BlockTextureMulti.get(BlockTextureDefault.get(mTexturesMaterial[FACING_ROTATIONS[mFacing][aSide]], mRGBa), BlockTextureDefault.get((mActive||worldObj==null?mTexturesActive:mRunning?mTexturesRunning:mTexturesInactive)[FACING_ROTATIONS[mFacing][aSide]])) : null;}
	
	@Override public boolean canSave(int aSlot) {return !IL.Display_Fluid.equal(slot(aSlot), T, T);}
	@Override public boolean hasWork() {return mCore.hasWork();}
	@Override public long getProgressValue(byte aSide) {return mCore.getProgressValue(aSide);}
	@Override public long getProgressMax  (byte aSide) {return mCore.getProgressMax(aSide);}
	@Override public long getGibblValue   (byte aSide) {long rGibbl = 0; for (FluidTankGT fluidTankGT : mTanksInput) rGibbl += fluidTankGT.amount  (); return rGibbl;}
	@Override public long getGibblMax     (byte aSide) {long rGibbl = 0; for (FluidTankGT fluidTankGT : mTanksInput) rGibbl += fluidTankGT.capacity(); return rGibbl;}
	
	@Override public boolean getStateRunningPossible    () {return mCouldUseRecipe || mActive || mCore.mMaxProgress > 0 || mCore.mChargeRequirement > 0 || (mIgnited > 0 && !mDisabledItemOutput && mOutputBlocked != 0);}
	@Override public boolean getStateRunningPassively   () {return mRunning;}
	@Override public boolean getStateRunningActively    () {return mActive;}
	@Override public boolean getStateRunningSuccessfully() {return mSuccessful;}
	@Override public boolean setStateOnOff(boolean aOnOff) {if (mStopped == aOnOff) {mStopped = !aOnOff; updateAdjacentToggleableEnergySources();} return !mStopped;}
	@Override public boolean getStateOnOff() {return !mStopped;}
	
	@Override public String getTileEntityNameCompat() {return "gtch.multitileentity.machine.basic";}
	@Override public String getTileEntityName() {return "gt.multitileentity.machine.basic";}
	
	@Override
	public void adjacentInventoryUpdated(byte aSide, IInventory aTileEntity) {
		if (FACE_CONNECTED[FACING_ROTATIONS[mFacing][aSide]][mItemInputs|mItemOutputs]) updateInventory();
	}
	
	// 虽然机器都只接受能合成的流体，但是还是以防万一进行白名单写法
	@Override
	public boolean canFillExtra(FluidStack aFluid) {return mCore.canFillExtra(aFluid);}
}
