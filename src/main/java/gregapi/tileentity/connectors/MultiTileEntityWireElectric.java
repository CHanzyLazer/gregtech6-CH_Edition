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

package gregapi.tileentity.connectors;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregapi.block.multitileentity.IMultiTileEntity;
import gregapi.block.multitileentity.MultiTileEntityBlock;
import gregapi.block.multitileentity.MultiTileEntityRegistry;
import gregapi.code.ArrayListNoNulls;
import gregapi.code.TagData;
import gregapi.data.*;
import gregapi.old.Textures;
import gregapi.oredict.OreDictManager;
import gregapi.oredict.OreDictMaterial;
import gregapi.render.BlockTextureDefault;
import gregapi.render.BlockTextureMulti;
import gregapi.render.ITexture;
import gregapi.tileentity.ITileEntityQuickObstructionCheck;
import gregapi.tileentity.data.ITileEntityProgress;
import gregapi.tileentity.delegate.DelegatorTileEntity;
import gregapi.tileentity.energy.EnergyCompat;
import gregapi.tileentity.energy.ITileEntityEnergy;
import gregapi.tileentity.energy.ITileEntityEnergyDataConductor;
import gregapi.util.UT;
import gregtechCH.tileentity.cores.electric.IMTEC_HasElectricWire;
import gregtechCH.tileentity.cores.electric.MTEC_ElectricWireBase;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static gregapi.data.CS.*;

/**
 * @author Gregorius Techneticies, CHanzy
 * 符合欧姆定律的新电线
 */
public class MultiTileEntityWireElectric extends TileEntityBase10ConnectorRendered implements IMTEC_HasElectricWire, ITileEntityQuickObstructionCheck, ITileEntityEnergy, ITileEntityEnergyDataConductor, ITileEntityProgress, IMultiTileEntity.IMTE_GetDebugInfo, IMultiTileEntity.IMTE_GetCollisionBoundingBoxFromPool, IMultiTileEntity.IMTE_OnEntityCollidedWithBlock {
	public byte mRenderType = 0;
	private final MTEC_ElectricWireBase mCore = new MTEC_ElectricWireBase(this);
	@Override public MTEC_ElectricWireBase core() {return mCore;}
	
	/**
	 * Utility to quickly add a whole set of Electric Wires.
	 * May use up to 50 IDs, even if it is just 21 right now!
	 * 对不同尺寸的电线进行电阻的调整
	 */
	public static void addElectricWires(int aID, int aCreativeTabID, long aVoltage, long aAmperage, long aLossWire, long aLossCable, boolean aContactDamageWire, boolean aContactDamageCable, boolean aCable, MultiTileEntityRegistry aRegistry, MultiTileEntityBlock aBlock, Class<? extends TileEntity> aClass, OreDictMaterial aMat) {
		OreDictManager.INSTANCE.setTarget_(OP.wireGt01 , aMat, aRegistry.add( "1x " + aMat.getLocal() + " Wire" , "Electric Wires", aID+ 0, aCreativeTabID, aClass, aMat.mToolQuality, 64/ 1, aBlock, UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS, 1.0F, NBT_RESISTANCE, 2.0F, NBT_COLOR, UT.Code.getRGBInt(aMat.fRGBaSolid), NBT_PIPERENDER, 0, NBT_DIAMETER, PX_P[ 2], NBT_PIPESIZE, aVoltage, NBT_PIPEBANDWIDTH, aAmperage* 1, NBT_CONTACTDAMAGE, aContactDamageWire, NBT_RESISTANCE+".electric", aLossWire*U  )), T, F, T);
		OreDictManager.INSTANCE.setTarget_(OP.wireGt02 , aMat, aRegistry.add( "2x " + aMat.getLocal() + " Wire" , "Electric Wires", aID+ 1, aCreativeTabID, aClass, aMat.mToolQuality, 64/ 2, aBlock, UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS, 1.0F, NBT_RESISTANCE, 2.0F, NBT_COLOR, UT.Code.getRGBInt(aMat.fRGBaSolid), NBT_PIPERENDER, 0, NBT_DIAMETER, PX_P[ 3], NBT_PIPESIZE, aVoltage, NBT_PIPEBANDWIDTH, aAmperage* 2, NBT_CONTACTDAMAGE, aContactDamageWire, NBT_RESISTANCE+".electric", aLossWire*U2 )), T, F, T);
		OreDictManager.INSTANCE.setTarget_(OP.wireGt03 , aMat, aRegistry.add( "3x " + aMat.getLocal() + " Wire" , "Electric Wires", aID+ 2, aCreativeTabID, aClass, aMat.mToolQuality, 64/ 3, aBlock, UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS, 1.0F, NBT_RESISTANCE, 2.0F, NBT_COLOR, UT.Code.getRGBInt(aMat.fRGBaSolid), NBT_PIPERENDER, 0, NBT_DIAMETER, PX_P[ 4], NBT_PIPESIZE, aVoltage, NBT_PIPEBANDWIDTH, aAmperage* 3, NBT_CONTACTDAMAGE, aContactDamageWire, NBT_RESISTANCE+".electric", aLossWire*U3 )), T, F, T);
		OreDictManager.INSTANCE.setTarget_(OP.wireGt04 , aMat, aRegistry.add( "4x " + aMat.getLocal() + " Wire" , "Electric Wires", aID+ 3, aCreativeTabID, aClass, aMat.mToolQuality, 64/ 4, aBlock, UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS, 1.0F, NBT_RESISTANCE, 2.0F, NBT_COLOR, UT.Code.getRGBInt(aMat.fRGBaSolid), NBT_PIPERENDER, 0, NBT_DIAMETER, PX_P[ 6], NBT_PIPESIZE, aVoltage, NBT_PIPEBANDWIDTH, aAmperage* 4, NBT_CONTACTDAMAGE, aContactDamageWire, NBT_RESISTANCE+".electric", aLossWire*U4 )), T, F, T);
		OreDictManager.INSTANCE.setTarget_(OP.wireGt05 , aMat, aRegistry.add( "5x " + aMat.getLocal() + " Wire" , "Electric Wires", aID+ 4, aCreativeTabID, aClass, aMat.mToolQuality, 64/ 5, aBlock, UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS, 1.0F, NBT_RESISTANCE, 2.0F, NBT_COLOR, UT.Code.getRGBInt(aMat.fRGBaSolid), NBT_PIPERENDER, 0, NBT_DIAMETER, PX_P[ 7], NBT_PIPESIZE, aVoltage, NBT_PIPEBANDWIDTH, aAmperage* 5, NBT_CONTACTDAMAGE, aContactDamageWire, NBT_RESISTANCE+".electric", aLossWire*U5 )), T, F, T);
		OreDictManager.INSTANCE.setTarget_(OP.wireGt06 , aMat, aRegistry.add( "6x " + aMat.getLocal() + " Wire" , "Electric Wires", aID+ 5, aCreativeTabID, aClass, aMat.mToolQuality, 64/ 6, aBlock, UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS, 1.0F, NBT_RESISTANCE, 2.0F, NBT_COLOR, UT.Code.getRGBInt(aMat.fRGBaSolid), NBT_PIPERENDER, 0, NBT_DIAMETER, PX_P[ 7], NBT_PIPESIZE, aVoltage, NBT_PIPEBANDWIDTH, aAmperage* 6, NBT_CONTACTDAMAGE, aContactDamageWire, NBT_RESISTANCE+".electric", aLossWire*U6 )), T, F, T);
		OreDictManager.INSTANCE.setTarget_(OP.wireGt07 , aMat, aRegistry.add( "7x " + aMat.getLocal() + " Wire" , "Electric Wires", aID+ 6, aCreativeTabID, aClass, aMat.mToolQuality, 64/ 7, aBlock, UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS, 1.0F, NBT_RESISTANCE, 2.0F, NBT_COLOR, UT.Code.getRGBInt(aMat.fRGBaSolid), NBT_PIPERENDER, 0, NBT_DIAMETER, PX_P[ 8], NBT_PIPESIZE, aVoltage, NBT_PIPEBANDWIDTH, aAmperage* 7, NBT_CONTACTDAMAGE, aContactDamageWire, NBT_RESISTANCE+".electric", aLossWire*U7 )), T, F, T);
		OreDictManager.INSTANCE.setTarget_(OP.wireGt08 , aMat, aRegistry.add( "8x " + aMat.getLocal() + " Wire" , "Electric Wires", aID+ 7, aCreativeTabID, aClass, aMat.mToolQuality, 64/ 8, aBlock, UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS, 1.0F, NBT_RESISTANCE, 2.0F, NBT_COLOR, UT.Code.getRGBInt(aMat.fRGBaSolid), NBT_PIPERENDER, 0, NBT_DIAMETER, PX_P[ 8], NBT_PIPESIZE, aVoltage, NBT_PIPEBANDWIDTH, aAmperage* 8, NBT_CONTACTDAMAGE, aContactDamageWire, NBT_RESISTANCE+".electric", aLossWire*U8 )), T, F, T);
		OreDictManager.INSTANCE.setTarget_(OP.wireGt09 , aMat, aRegistry.add( "9x " + aMat.getLocal() + " Wire" , "Electric Wires", aID+ 8, aCreativeTabID, aClass, aMat.mToolQuality, 64/ 9, aBlock, UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS, 1.0F, NBT_RESISTANCE, 2.0F, NBT_COLOR, UT.Code.getRGBInt(aMat.fRGBaSolid), NBT_PIPERENDER, 0, NBT_DIAMETER, PX_P[ 9], NBT_PIPESIZE, aVoltage, NBT_PIPEBANDWIDTH, aAmperage* 9, NBT_CONTACTDAMAGE, aContactDamageWire, NBT_RESISTANCE+".electric", aLossWire*U9 )), T, F, T);
		OreDictManager.INSTANCE.setTarget_(OP.wireGt10 , aMat, aRegistry.add("10x " + aMat.getLocal() + " Wire" , "Electric Wires", aID+ 9, aCreativeTabID, aClass, aMat.mToolQuality, 64/10, aBlock, UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS, 1.0F, NBT_RESISTANCE, 2.0F, NBT_COLOR, UT.Code.getRGBInt(aMat.fRGBaSolid), NBT_PIPERENDER, 0, NBT_DIAMETER, PX_P[10], NBT_PIPESIZE, aVoltage, NBT_PIPEBANDWIDTH, aAmperage*10, NBT_CONTACTDAMAGE, aContactDamageWire, NBT_RESISTANCE+".electric", aLossWire*U10)), T, F, T);
		OreDictManager.INSTANCE.setTarget_(OP.wireGt11 , aMat, aRegistry.add("11x " + aMat.getLocal() + " Wire" , "Electric Wires", aID+10, aCreativeTabID, aClass, aMat.mToolQuality, 64/11, aBlock, UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS, 1.0F, NBT_RESISTANCE, 2.0F, NBT_COLOR, UT.Code.getRGBInt(aMat.fRGBaSolid), NBT_PIPERENDER, 0, NBT_DIAMETER, PX_P[11], NBT_PIPESIZE, aVoltage, NBT_PIPEBANDWIDTH, aAmperage*11, NBT_CONTACTDAMAGE, aContactDamageWire, NBT_RESISTANCE+".electric", aLossWire*U11)), T, F, T);
		OreDictManager.INSTANCE.setTarget_(OP.wireGt12 , aMat, aRegistry.add("12x " + aMat.getLocal() + " Wire" , "Electric Wires", aID+11, aCreativeTabID, aClass, aMat.mToolQuality, 64/12, aBlock, UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS, 1.0F, NBT_RESISTANCE, 2.0F, NBT_COLOR, UT.Code.getRGBInt(aMat.fRGBaSolid), NBT_PIPERENDER, 0, NBT_DIAMETER, PX_P[12], NBT_PIPESIZE, aVoltage, NBT_PIPEBANDWIDTH, aAmperage*12, NBT_CONTACTDAMAGE, aContactDamageWire, NBT_RESISTANCE+".electric", aLossWire*U12)), T, F, T);
		OreDictManager.INSTANCE.setTarget_(OP.wireGt13 , aMat, aRegistry.add("13x " + aMat.getLocal() + " Wire" , "Electric Wires", aID+12, aCreativeTabID, aClass, aMat.mToolQuality, 64/13, aBlock, UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS, 1.0F, NBT_RESISTANCE, 2.0F, NBT_COLOR, UT.Code.getRGBInt(aMat.fRGBaSolid), NBT_PIPERENDER, 0, NBT_DIAMETER, PX_P[13], NBT_PIPESIZE, aVoltage, NBT_PIPEBANDWIDTH, aAmperage*13, NBT_CONTACTDAMAGE, aContactDamageWire, NBT_RESISTANCE+".electric", aLossWire*U13)), T, F, T);
		OreDictManager.INSTANCE.setTarget_(OP.wireGt14 , aMat, aRegistry.add("14x " + aMat.getLocal() + " Wire" , "Electric Wires", aID+13, aCreativeTabID, aClass, aMat.mToolQuality, 64/14, aBlock, UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS, 1.0F, NBT_RESISTANCE, 2.0F, NBT_COLOR, UT.Code.getRGBInt(aMat.fRGBaSolid), NBT_PIPERENDER, 0, NBT_DIAMETER, PX_P[14], NBT_PIPESIZE, aVoltage, NBT_PIPEBANDWIDTH, aAmperage*14, NBT_CONTACTDAMAGE, aContactDamageWire, NBT_RESISTANCE+".electric", aLossWire*U14)), T, F, T);
		OreDictManager.INSTANCE.setTarget_(OP.wireGt15 , aMat, aRegistry.add("15x " + aMat.getLocal() + " Wire" , "Electric Wires", aID+14, aCreativeTabID, aClass, aMat.mToolQuality, 64/15, aBlock, UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS, 1.0F, NBT_RESISTANCE, 2.0F, NBT_COLOR, UT.Code.getRGBInt(aMat.fRGBaSolid), NBT_PIPERENDER, 0, NBT_DIAMETER, PX_P[15], NBT_PIPESIZE, aVoltage, NBT_PIPEBANDWIDTH, aAmperage*15, NBT_CONTACTDAMAGE, aContactDamageWire, NBT_RESISTANCE+".electric", aLossWire*U15)), T, F, T);
		OreDictManager.INSTANCE.setTarget_(OP.wireGt16 , aMat, aRegistry.add("16x " + aMat.getLocal() + " Wire" , "Electric Wires", aID+15, aCreativeTabID, aClass, aMat.mToolQuality, 64/16, aBlock, UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS, 1.0F, NBT_RESISTANCE, 2.0F, NBT_COLOR, UT.Code.getRGBInt(aMat.fRGBaSolid), NBT_PIPERENDER, 0, NBT_DIAMETER, PX_P[16], NBT_PIPESIZE, aVoltage, NBT_PIPEBANDWIDTH, aAmperage*16, NBT_CONTACTDAMAGE, aContactDamageWire, NBT_RESISTANCE+".electric", aLossWire*U16)), T, F, T);
		if (aCable) {
		OreDictManager.INSTANCE.setTarget_(OP.cableGt01, aMat, aRegistry.add( "1x " + aMat.getLocal() + " Cable", "Electric Wires", aID+16, aCreativeTabID, aClass, aMat.mToolQuality, 64, aBlock     , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS, 1.0F, NBT_RESISTANCE, 2.0F, NBT_COLOR, UT.Code.getRGBInt(aMat.fRGBaSolid), NBT_PIPERENDER, 1, NBT_DIAMETER, PX_P[ 4], NBT_PIPESIZE, aVoltage, NBT_PIPEBANDWIDTH, aAmperage   , NBT_CONTACTDAMAGE, aContactDamageCable, NBT_RESISTANCE+".electric", aLossCable*U  )), T, F, T);
		OreDictManager.INSTANCE.setTarget_(OP.cableGt02, aMat, aRegistry.add( "2x " + aMat.getLocal() + " Cable", "Electric Wires", aID+17, aCreativeTabID, aClass, aMat.mToolQuality, 32, aBlock     , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS, 1.0F, NBT_RESISTANCE, 2.0F, NBT_COLOR, UT.Code.getRGBInt(aMat.fRGBaSolid), NBT_PIPERENDER, 1, NBT_DIAMETER, PX_P[ 6], NBT_PIPESIZE, aVoltage, NBT_PIPEBANDWIDTH, aAmperage* 2, NBT_CONTACTDAMAGE, aContactDamageCable, NBT_RESISTANCE+".electric", aLossCable*U2 )), T, F, T);
		OreDictManager.INSTANCE.setTarget_(OP.cableGt04, aMat, aRegistry.add( "4x " + aMat.getLocal() + " Cable", "Electric Wires", aID+19, aCreativeTabID, aClass, aMat.mToolQuality, 16, aBlock     , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS, 1.0F, NBT_RESISTANCE, 2.0F, NBT_COLOR, UT.Code.getRGBInt(aMat.fRGBaSolid), NBT_PIPERENDER, 1, NBT_DIAMETER, PX_P[ 8], NBT_PIPESIZE, aVoltage, NBT_PIPEBANDWIDTH, aAmperage* 4, NBT_CONTACTDAMAGE, aContactDamageCable, NBT_RESISTANCE+".electric", aLossCable*U4 )), T, F, T);
		OreDictManager.INSTANCE.setTarget_(OP.cableGt08, aMat, aRegistry.add( "8x " + aMat.getLocal() + " Cable", "Electric Wires", aID+23, aCreativeTabID, aClass, aMat.mToolQuality,  8, aBlock     , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS, 1.0F, NBT_RESISTANCE, 2.0F, NBT_COLOR, UT.Code.getRGBInt(aMat.fRGBaSolid), NBT_PIPERENDER, 1, NBT_DIAMETER, PX_P[12], NBT_PIPESIZE, aVoltage, NBT_PIPEBANDWIDTH, aAmperage* 8, NBT_CONTACTDAMAGE, aContactDamageCable, NBT_RESISTANCE+".electric", aLossCable*U8 )), T, F, T);
		OreDictManager.INSTANCE.setTarget_(OP.cableGt12, aMat, aRegistry.add("12x " + aMat.getLocal() + " Cable", "Electric Wires", aID+27, aCreativeTabID, aClass, aMat.mToolQuality,  4, aBlock     , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS, 1.0F, NBT_RESISTANCE, 2.0F, NBT_COLOR, UT.Code.getRGBInt(aMat.fRGBaSolid), NBT_PIPERENDER, 1, NBT_DIAMETER, PX_P[16], NBT_PIPESIZE, aVoltage, NBT_PIPEBANDWIDTH, aAmperage*12, NBT_CONTACTDAMAGE, aContactDamageCable, NBT_RESISTANCE+".electric", aLossCable*U12)), T, F, T);
		
		for (OreDictMaterial tMat : ANY.Rubber.mToThis) {
		RM.Laminator.addRecipe2(T, 16, 16, OP.plate.mat(tMat, 1), aRegistry.getItem(aID+ 0), aRegistry.getItem(aID+16   ));
		RM.Laminator.addRecipe2(T, 16, 16, OP.plate.mat(tMat, 1), aRegistry.getItem(aID+ 1), aRegistry.getItem(aID+16+ 1));
		RM.Laminator.addRecipe2(T, 16, 32, OP.plate.mat(tMat, 2), aRegistry.getItem(aID+ 3), aRegistry.getItem(aID+16+ 3));
		RM.Laminator.addRecipe2(T, 16, 48, OP.plate.mat(tMat, 3), aRegistry.getItem(aID+ 7), aRegistry.getItem(aID+16+ 7));
		RM.Laminator.addRecipe2(T, 16, 64, OP.plate.mat(tMat, 4), aRegistry.getItem(aID+11), aRegistry.getItem(aID+16+11));
		
		RM.Laminator.addRecipe2(T, 16, 16, OP.foil .mat(tMat, 4), aRegistry.getItem(aID+ 0), aRegistry.getItem(aID+16   ));
		RM.Laminator.addRecipe2(T, 16, 16, OP.foil .mat(tMat, 4), aRegistry.getItem(aID+ 1), aRegistry.getItem(aID+16+ 1));
		RM.Laminator.addRecipe2(T, 16, 32, OP.foil .mat(tMat, 8), aRegistry.getItem(aID+ 3), aRegistry.getItem(aID+16+ 3));
		RM.Laminator.addRecipe2(T, 16, 48, OP.foil .mat(tMat,12), aRegistry.getItem(aID+ 7), aRegistry.getItem(aID+16+ 7));
		RM.Laminator.addRecipe2(T, 16, 64, OP.foil .mat(tMat,16), aRegistry.getItem(aID+11), aRegistry.getItem(aID+16+11));
		}
		}
	}
	
	@Override
	public void readFromNBT2(NBTTagCompound aNBT) {
		if (aNBT.hasKey(NBT_PIPERENDER)) mRenderType = aNBT.getByte(NBT_PIPERENDER);
		super.readFromNBT2(aNBT);
		mCore.readFromNBT(aNBT);
	}
	
	@Override
	public void writeToNBT2(NBTTagCompound aNBT) {
		super.writeToNBT2(aNBT);
	}
	
	@Override protected void toolTipsDescribe(List<String> aList) {mCore.toolTipsDescribe(aList);}
	@Override protected void toolTipsUseful(List<String> aList)  {mCore.toolTipsUseful(aList);}
	@Override protected void toolTipsHazard(List<String> aList) {
		if (mContactDamage) aList.add(LH.Chat.DRED     + LH.get(LH.HAZARD_CONTACT));
	}
	@Override protected void toolTipsOther(List<String> aList, ItemStack aStack, boolean aF3_H) {
		super.toolTipsOther(aList, aStack, aF3_H);
		mCore.toolTipsOther(aList);
	}
	
	@Override public boolean breakBlock() {mCore.markUpdateManager(); return super.breakBlock();}
	@Override public void onConnectionChange(byte aPreviousConnections) {super.onConnectionChange(aPreviousConnections); mCore.markUpdateManager();}
	@Override public void onAdjacentBlockChangeBefore() {
		List<Byte> tChangedSides = clearNullMarkersAndGetChangedSide();
		// 只有连接的方向发生了改变才更新 manager
		for (byte tSide : tChangedSides) if (connected(tSide)) {
			mCore.markUpdateManager(); return;
		}
	}
	
	@Override
	public void onTick2(long aTimer, boolean aIsServerSide) {
		super.onTick2(aTimer, aIsServerSide);
		mCore.onTick(aTimer, aIsServerSide);
	}
	
	@Override
	public long onToolClick2(String aTool, long aRemainingDurability, long aQuality, Entity aPlayer, List<String> aChatReturn, IInventory aPlayerInventory, boolean aSneaking, ItemStack aStack, byte aSide, float aHitX, float aHitY, float aHitZ) {
		long rReturn = super.onToolClick2(aTool, aRemainingDurability, aQuality, aPlayer, aChatReturn, aPlayerInventory, aSneaking, aStack, aSide, aHitX, aHitY, aHitZ);
		if (rReturn > 0) return rReturn;
		if (isClientSide()) return 0;
		return mCore.onToolClick(aTool, aRemainingDurability, aQuality, aPlayer, aChatReturn, aPlayerInventory, aSneaking, aStack, aSide, aHitX, aHitY, aHitZ);
	}
	
	@Override public boolean canConnect(byte aSide, DelegatorTileEntity<TileEntity> aDelegator) {return EnergyCompat.canConnectElectricity(this, aDelegator.mTileEntity, aDelegator.mSideOfTileEntity);}
	
	@Override public void onEntityCollidedWithBlock(Entity aEntity) {if (mContactDamage && !isFoamDried()) UT.Entities.applyElectricityDamage(aEntity, mCore.getVoltage()*mCore.getAmperage());}
	
	@Override public boolean isEnergyType(TagData aEnergyType, byte aSide, boolean aEmitting) {return aEnergyType == TD.Energy.EU;}
	@Override public Collection<TagData> getEnergyTypes(byte aSide) {return TD.Energy.EU.AS_LIST;}
	
	@Override public boolean isEnergyEmittingTo   (TagData aEnergyType, byte aSide, boolean aTheoretical) {return isEnergyType(aEnergyType, aSide, T) && canEmitEnergyTo    (aSide);}
	@Override public boolean isEnergyAcceptingFrom(TagData aEnergyType, byte aSide, boolean aTheoretical) {return isEnergyType(aEnergyType, aSide, F) && canAcceptEnergyFrom(aSide);}
	@Override public synchronized long doEnergyExtraction(TagData aEnergyType, byte aSide, long aSize, long aAmount, boolean aDoExtract) {return 0;}
	@Override public synchronized long doEnergyInjection (TagData aEnergyType, byte aSide, long aSize, long aAmount, boolean aDoInject ) {return mCore.doEnergyInjection(aEnergyType, aSide, aSize, aAmount, aDoInject);}
	@Override public long getEnergySizeOutputRecommended(TagData aEnergyType, byte aSide) {return mCore.getMaxVoltage();}
	@Override public long getEnergySizeOutputMin(TagData aEnergyType, byte aSide) {return 0;}
	@Override public long getEnergySizeOutputMax(TagData aEnergyType, byte aSide) {return mCore.getMaxVoltage();}
	@Override public long getEnergySizeInputRecommended(TagData aEnergyType, byte aSide) {return mCore.getMaxVoltage();}
	@Override public long getEnergySizeInputMin(TagData aEnergyType, byte aSide) {return 0;}
	@Override public long getEnergySizeInputMax(TagData aEnergyType, byte aSide) {return mCore.getMaxVoltage();}
	
	@Override public boolean canDrop(int aInventorySlot) {return F;}
	@Override public boolean isObstructingBlockAt2(byte aSide) {return F;} // Btw, Wires have this but Pipes don't. This is because Wires are flexible, while Pipes aren't.
	
	@Override public boolean isEnergyConducting(TagData aEnergyType) {return aEnergyType == TD.Energy.EU;}
	@Override public long getEnergyMaxSize(TagData aEnergyType) {return aEnergyType == TD.Energy.EU ? mCore.getMaxVoltage() : 0;}
	@Override public long getEnergyMaxPackets(TagData aEnergyType) {return aEnergyType == TD.Energy.EU ? mCore.getMaxAmperage() : 0;}
	@Override public long getEnergyLossPerMeter(TagData aEnergyType) {return aEnergyType == TD.Energy.EU ? mCore.getLoss() : 0;}
	@Override public OreDictMaterial getEnergyConductorMaterial() {return mMaterial;}
	@Override public OreDictMaterial getEnergyConductorInsulation() {return isInsulated() ? ANY.Rubber : MT.NULL;}
	
	public boolean canEmitEnergyTo                          (byte aSide) {return connected(aSide);}
	public boolean canAcceptEnergyFrom                      (byte aSide) {return connected(aSide);}
	
	@Override public long getProgressValue                  (byte aSide) {return mCore.getAmperage();}
	@Override public long getProgressMax                    (byte aSide) {return mCore.getMaxAmperage();}
	
	public long getWattageValue                             (byte aSide) {return mCore.getAmperage()*mCore.getVoltage();}
	public long getWattageMax                               (byte aSide) {return mCore.getMaxAmperage()*mCore.getMaxVoltage();}
	public long getVoltageValue                             (byte aSide) {return mCore.getVoltage();}
	public long getVoltageMax                               (byte aSide) {return mCore.getMaxVoltage();}
	public long getAmperageValue                            (byte aSide) {return mCore.getAmperage();}
	public long getAmperageMax                              (byte aSide) {return mCore.getMaxAmperage();}
	
	@Override public ArrayList<String> getDebugInfo(int aScanLevel) {return aScanLevel > 0 ? new ArrayListNoNulls<>(F, "Transferred Power: " + mCore.getVoltage()*mCore.getAmperage()) : null;}
	
	// 绝缘线缆不会改变半径
	@Override public float getConnectorDiameter(byte aConnectorSide, DelegatorTileEntity<TileEntity> aDelegator) {
		// 绝缘线缆连接非绝缘线缆时不会收缩
		if (isInsulated() && aDelegator.mTileEntity instanceof MultiTileEntityWireElectric && !((MultiTileEntityWireElectric)aDelegator.mTileEntity).isInsulated()) return mDiameter;
		return super.getConnectorDiameter(aConnectorSide, aDelegator);
	}
	// GTCH, 用于减少重复代码
	private boolean isInsulated() { return mRenderType == 1 || mRenderType == 2; }
	
	// GTCH, 返回绝缘层的颜色为原本颜色
	@Override public int getBottomRGB() {return isInsulated() ? UT.Code.getRGBInt(64, 64, 64) : super.getBottomRGB();}
	// GTCH, 绝缘时返回绝缘层颜色
	@SideOnly(Side.CLIENT) @Override protected int colorMultiplier2() {
		if (isInsulated()) return isPainted() ? mRGBa : getBottomRGB();
		return super.colorMultiplier2();
	}
	
	@Override public ITexture getTextureSide                (byte aSide, byte aConnections, float aDiameter, int aRenderPass) {return isInsulated() ? BlockTextureDefault.get(Textures.BlockIcons.INSULATION_FULL, isPainted()?mRGBa:getBottomRGB()) : BlockTextureDefault.get(mMaterial, getIconIndexSide(aSide, aConnections, aDiameter, aRenderPass), F, mRGBa);}
	@Override public ITexture getTextureConnected           (byte aSide, byte aConnections, float aDiameter, int aRenderPass) {return isInsulated() ? BlockTextureMulti.get(BlockTextureDefault.get(mMaterial, getIconIndexConnected(aSide, aConnections, aDiameter, aRenderPass), mIsGlowing), BlockTextureDefault.get(mRenderType==2?Textures.BlockIcons.INSULATION_BUNDLED:aDiameter<0.37F?Textures.BlockIcons.INSULATION_TINY:aDiameter<0.49F?Textures.BlockIcons.INSULATION_SMALL:aDiameter<0.74F?Textures.BlockIcons.INSULATION_MEDIUM:aDiameter<0.99F?Textures.BlockIcons.INSULATION_LARGE:Textures.BlockIcons.INSULATION_HUGE, isPainted()?mRGBa: getBottomRGB())) : BlockTextureDefault.get(mMaterial, getIconIndexConnected(aSide, aConnections, aDiameter, aRenderPass), mIsGlowing, mRGBa);}
	
	@Override public int getIconIndexSide                   (byte aSide, byte aConnections, float aDiameter, int aRenderPass) {return OP.wire.mIconIndexBlock;}
	@Override public int getIconIndexConnected              (byte aSide, byte aConnections, float aDiameter, int aRenderPass) {return OP.wire.mIconIndexBlock;}
	
	@Override public Collection<TagData> getConnectorTypes  (byte aSide) {return TD.Connectors.WIRE_ELECTRIC.AS_LIST;}
	
	@Override public String getFacingTool                   () {return TOOL_cutter;}
	
	@Override public String getTileEntityName               () {return "gt.multitileentity.connector.wire.electric";}
}
