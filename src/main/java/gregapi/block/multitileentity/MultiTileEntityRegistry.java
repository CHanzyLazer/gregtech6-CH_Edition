/**
 * Copyright (c) 2022 GregTech-6 Team
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

package gregapi.block.multitileentity;

import gregapi.block.multitileentity.IMultiTileEntity.*;
import gregapi.code.ArrayListNoNulls;
import gregapi.code.HashSetNoNulls;
import gregapi.code.ItemStackContainer;
import gregapi.code.ItemStackMap;
import gregapi.data.LH;
import gregapi.item.CreativeTab;
import gregapi.recipes.Recipe.RecipeMap;
import gregapi.render.RendererBlockTextured;
import gregapi.util.CR;
import gregapi.util.ST;
import gregapi.util.UT;
import gregtechCH.data.LH_CH;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static gregapi.data.CS.*;

/**
 * @author Gregorius Techneticies
 * 
 * Create yourself a new MultiTileEntity Registry in order to have your very own 32767 Sub IDs.
 * You can do with those IDs whatever you want since this automatically creates your personal Item and DOESN'T use any Items or Blocks of GregTech itself.
 * 
 * Whatever you do, DO NOT GET THE UTTERLY RETARDED IDEA OF ADDING YOUR MULTITILEENTITIES TO MY REGISTRY!!! INSTANCIATE YOUR OWN REGISTRY!!!
 * 
 * ================================================================================================================================================
 * The way this whole System works is very simple. The setTileEntity call can set the TileEntity of your choice at every Location you want.
 * If now the BlockContainer doesn't return a TileEntity, but instead the ItemBlock manually sets the TileEntity, you can have every single
 * TileEntity being placed at every Block you want. If that Block then is compatible with your TileEntity (via Interfaces and such) it can
 * easily make use of the TileEntity no matter which one it is.
 * 
 * "But what is with the Loading of those TileEntities? Don't they get deleted on startup?" You think? No they don't get deleted. Minecraft
 * can load every TileEntity just by a Name->Class Map (you know about that when you have ever created a TileEntity yourself), and the remaining
 * Stats can be saved inside the NBT of the TileEntity.
 * 
 * In the end I have a dynamic collection of Blocks to get the vanilla Materials and Sound Effects right, a Registry of TileEntities to be
 * attached to those Blocks via additional custom ItemBlocks to enable everything, and an automatic Network Handler.
 * 
 * The only thing needed to be done manually is something that transmits the Data from the Server to the Client to set the proper TileEntity there.
 * 
 * In order to do that, just send one of the 5 Packets (PacketSyncDataByteAndIDs, PacketSyncDataShortAndIDs, PacketSyncDataIntegerAndIDs, 
 * PacketSyncDataLongAndIDs or PacketSyncDataByteArrayAndIDs) for transmitting the ID to the Client with aID1 = getMultiTileEntityRegistryID() and
 * aID2 = getMultiTileEntityID(). The Byte/Short/Integer/Long/ByteArray can be used for transmitting other Data, such as a Facing to the Client.
 * ================================================================================================================================================
 */
public class MultiTileEntityRegistry {
	private static final HashMap<String, MultiTileEntityRegistry> NAMED_REGISTRIES = new HashMap<>();
	private static final ItemStackMap<ItemStackContainer, MultiTileEntityRegistry> REGISTRIES = new ItemStackMap<>();
	private static final HashSetNoNulls<Class<?>> sRegisteredTileEntities = new HashSetNoNulls<>();
	private final HashSetNoNulls<Class<?>> mRegisteredTileEntities = new HashSetNoNulls<>();
	
	public HashMap<Short, CreativeTab> mCreativeTabs = new HashMap<>();
	public Map<Short, MultiTileEntityClassContainer> mRegistry = new HashMap<>();
	public List<MultiTileEntityClassContainer> mRegistrations = new ArrayListNoNulls<>();
	
	public final String mNameInternal;
	public final MultiTileEntityBlockInternal mBlock;
	
	private static final MultiTileEntityBlockInternal regblock(String aNameInternal, MultiTileEntityBlockInternal aBlock, Class<? extends ItemBlock> aItemClass) {
		ST.register(aBlock, aNameInternal, aItemClass);
		return aBlock;
	}
	
	/** @param aNameInternal the internal Name of the Item. DO NOT START YOUR UNLOCALISED NAME WITH "gt."!!! */
	public MultiTileEntityRegistry(String aNameInternal) {this(aNameInternal, new MultiTileEntityBlockInternal(), MultiTileEntityItemInternal.class);}
	/** @param aNameInternal the internal Name of the Item. DO NOT START YOUR UNLOCALISED NAME WITH "gt."!!! */
	public MultiTileEntityRegistry(String aNameInternal, MultiTileEntityBlockInternal aBlock, Class<? extends ItemBlock> aItemClass) {this(aNameInternal, aBlock, aItemClass, RendererBlockTextured.INSTANCE);}
	/** @param aNameInternal the internal Name of the Item. DO NOT START YOUR UNLOCALISED NAME WITH "gt."!!! */
	public MultiTileEntityRegistry(String aNameInternal, MultiTileEntityBlockInternal aBlock, Class<? extends ItemBlock> aItemClass, Object aItemRenderer) {
		this(aNameInternal, regblock(aNameInternal, aBlock, aItemClass));
		if (CODE_CLIENT) MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(mBlock), aItemRenderer == null ? RendererBlockTextured.INSTANCE : (IItemRenderer)aItemRenderer);
	}
	/** @param aNameInternal the internal Name of the Item. DO NOT START YOUR UNLOCALISED NAME WITH "gt."!!! */
	public MultiTileEntityRegistry(String aNameInternal, MultiTileEntityBlockInternal aBlock) {
		if (!GAPI.mStartedPreInit) throw new IllegalStateException("The MultiTileEntity Registry must be initialised at the Preload Phase and not before, because it relies on an ItemBlock being created!");
		if (GAPI.mStartedInit) throw new IllegalStateException("The MultiTileEntity Registry must be initialised at the Preload Phase and not later, because it relies on an ItemBlock being created!");
		mNameInternal = aNameInternal;
		mBlock = aBlock;
		mBlock.mMultiTileEntityRegistry = this;
		REGISTRIES.put(new ItemStackContainer(mBlock, 1, W), this);
		NAMED_REGISTRIES.put(mNameInternal, this);
	}
	
	/** Whatever you do, DO NOT GET THE UTTERLY RETARDED IDEA OF ADDING YOUR MULTITILEENTITIES TO MY OWN REGISTRY!!! Create your own instance! */
	public static MultiTileEntityRegistry getRegistry(int aRegistryID) {
		return REGISTRIES.get(new ItemStackContainer(Item.getItemById(aRegistryID), 1, W));
	}
	
	/** Whatever you do, DO NOT GET THE UTTERLY RETARDED IDEA OF ADDING YOUR MULTITILEENTITIES TO MY OWN REGISTRY!!! Create your own instance! */
	public static MultiTileEntityRegistry getRegistry(String aRegistryName) {
		return NAMED_REGISTRIES.get(aRegistryName);
	}
	
	public static TileEntity getCanonicalTileEntity(int aRegistryID, int aMultiTileEntityID) {
		MultiTileEntityRegistry tRegistry = getRegistry(aRegistryID);
		if (tRegistry == null) return null;
		MultiTileEntityClassContainer tClassContainer = tRegistry.getClassContainer(aMultiTileEntityID);
		if (tClassContainer == null) return null;
		return tClassContainer.mCanonicalTileEntity;
	}
	
	public static TileEntity getCanonicalTileEntity(String aRegistryName, int aMultiTileEntityID) {
		MultiTileEntityRegistry tRegistry = getRegistry(aRegistryName);
		if (tRegistry == null) return null;
		MultiTileEntityClassContainer tClassContainer = tRegistry.getClassContainer(aMultiTileEntityID);
		if (tClassContainer == null) return null;
		return tClassContainer.mCanonicalTileEntity;
	}
	
	/** Adds a new MultiTileEntity. It is highly recommended to do this in either the PreInit or the Init Phase. PostInit might not work well.*/
	public ItemStack add(String aLocalised, String aCategoricalName, int aID, int aCreativeTabID, Class<? extends TileEntity> aClass, int aBlockMetaData, int aStackSize, MultiTileEntityBlock aBlock, NBTTagCompound aParameters, Object... aRecipe) {
		return add(aLocalised, aCategoricalName, new MultiTileEntityClassContainer(aID, aCreativeTabID, aClass, aBlockMetaData, aStackSize, aBlock, aParameters), aRecipe);
	}

	// GTCH, 为额外添加的机器添加特殊情况
	public ItemStack add(Boolean aIsGTCH, String aLocalised, String aCategoricalName, int aID, int aCreativeTabID, Class<? extends TileEntity> aClass, int aBlockMetaData, int aStackSize, MultiTileEntityBlock aBlock, NBTTagCompound aParameters, Object... aRecipe) {
		return add(aIsGTCH, aLocalised, aCategoricalName, new MultiTileEntityClassContainer(aID, aCreativeTabID, aClass, aBlockMetaData, aStackSize, aBlock, aParameters), aRecipe);
	}

	// 保留兼容
	public ItemStack add(String aLocalised, String aCategoricalName, MultiTileEntityClassContainer aClassContainer, Object... aRecipe) {
		return add(F, aLocalised, aCategoricalName, aClassContainer, aRecipe);
	}
	
	// GTCH, 通过将 add 的项目存入 linkedHashMap 的方法来延迟 add 操作，方便魔改时对添加操作进行运行时修改并且保留对原版的更新的兼容
	public ItemStack add(Boolean aIsGTCH, String aLocalised, String aCategoricalName, MultiTileEntityClassContainer aClassContainer, Object... aRecipe) {
		AddObject tAddObject = new AddObject(aIsGTCH, aLocalised, aCategoricalName, aClassContainer, aRecipe);
		if (!mIsHoldingAdd) return tAddObject.addSelf();
		mHoldingAdds.put(aClassContainer.mID, tAddObject); // holding 时可以避免相同 id 的情况，直接选取后添加的
		return null; // holding 时直接返回空
	}
	
	public void holdAdd() {mIsHoldingAdd = T;}
	public void releaseAdd() {
		mIsHoldingAdd = F;
		for (AddObject tAddObject : mHoldingAdds.values()) tAddObject.addSelf();
		mHoldingAdds.clear();
	}
	
	private boolean mIsHoldingAdd = F;
	private final Map<Short, AddObject> mHoldingAdds = new LinkedHashMap<>();
	private class AddObject {
		private final Boolean mIsGTCH;
		private final String mLocalised;
		private final String mCategoricalName;
		private final MultiTileEntityClassContainer mClassContainer;
		private Object[] mRecipe;
		public AddObject(Boolean aIsGTCH, String aLocalised, String aCategoricalName, MultiTileEntityClassContainer aClassContainer, Object... aRecipe) {
			mIsGTCH = aIsGTCH; mLocalised = aLocalised; mCategoricalName = aCategoricalName; mClassContainer = aClassContainer; mRecipe = aRecipe;
		}
		/** Adds a new MultiTileEntity. It is highly recommended to do this in either the PreInit or the Init Phase. PostInit might not work well.*/
		public ItemStack addSelf() {
			boolean tFailed = F;
			if (UT.Code.stringInvalid(mLocalised)) {
				ERR.println("MTE REGISTRY ERROR: Localisation Missing!");
				tFailed = T;
			}
			if (mClassContainer == null) {
				ERR.println("MTE REGISTRY ERROR: Class Container is null!");
				tFailed = T;
			} else {
				if (mClassContainer.mClass == null) {
					ERR.println("MTE REGISTRY ERROR: Class inside Class Container is null!");
					tFailed = T;
				}
				if (mClassContainer.mID == W) {
					ERR.println("MTE REGISTRY ERROR: Class Container uses Wildcard MetaData!");
					tFailed = T;
				}
				if (mClassContainer.mID < 0) {
					ERR.println("MTE REGISTRY ERROR: Class Container uses negative MetaData!");
					tFailed = T;
				}
				if (mRegistry.containsKey(mClassContainer.mID)) {
					ERR.println("MTE REGISTRY ERROR: Class Container uses occupied MetaData!");
					tFailed = T;
				}
			}
			if (tFailed) {
				ERR.println("MTE REGISTRY ERROR: STACKTRACE START");
				int i = 0; for (StackTraceElement tElement : new Exception().getStackTrace()) if (i++<5 && !tElement.getClassName().startsWith("sun")) ERR.println("\tat " + tElement); else break;
				ERR.println("MTE REGISTRY ERROR: STACKTRACE END");
				return null;
			}
			assert mClassContainer != null;
			if (mIsGTCH) LH_CH.add(mNameInternal+"."+mClassContainer.mID+".name", mLocalised);
			else LH.add(mNameInternal+"."+mClassContainer.mID+".name", mLocalised);
			mRegistry.put(mClassContainer.mID, mClassContainer);
			mLastRegisteredID = mClassContainer.mID;
			mRegistrations.add(mClassContainer);
			if (!mCreativeTabs.containsKey(mClassContainer.mCreativeTabID)) mCreativeTabs.put(mClassContainer.mCreativeTabID, new CreativeTab(mNameInternal+"."+mClassContainer.mCreativeTabID, mCategoricalName, Item.getItemFromBlock(mBlock), mClassContainer.mCreativeTabID));
			if (sRegisteredTileEntities.add(mClassContainer.mCanonicalTileEntity.getClass())) {
				if (mClassContainer.mCanonicalTileEntity instanceof IMTE_OnRegistrationFirst) ((IMTE_OnRegistrationFirst)mClassContainer.mCanonicalTileEntity).onRegistrationFirst(MultiTileEntityRegistry.this, mClassContainer.mID);
				if (CODE_CLIENT && mClassContainer.mCanonicalTileEntity instanceof IMTE_OnRegistrationFirstClient) ((IMTE_OnRegistrationFirstClient)mClassContainer.mCanonicalTileEntity).onRegistrationFirstClient(MultiTileEntityRegistry.this, mClassContainer.mID);
			}
			if (mRegisteredTileEntities.add(mClassContainer.mCanonicalTileEntity.getClass())) {
				if (mClassContainer.mCanonicalTileEntity instanceof IMTE_OnRegistrationFirstOfRegister) ((IMTE_OnRegistrationFirstOfRegister)mClassContainer.mCanonicalTileEntity).onRegistrationFirstOfRegister(MultiTileEntityRegistry.this, mClassContainer.mID);
				if (mClassContainer.mCanonicalTileEntity instanceof IMTE_OnRegistrationFirstOfRegisterClient) ((IMTE_OnRegistrationFirstOfRegisterClient)mClassContainer.mCanonicalTileEntity).onRegistrationFirstOfRegisterClient(MultiTileEntityRegistry.this, mClassContainer.mID);
			}
			if (mClassContainer.mCanonicalTileEntity instanceof IMTE_OnRegistration) {
				((IMTE_OnRegistration)mClassContainer.mCanonicalTileEntity).onRegistration(MultiTileEntityRegistry.this, mClassContainer.mID);
			}
			if (CODE_CLIENT && mClassContainer.mCanonicalTileEntity instanceof IMTE_OnRegistrationClient) {
				((IMTE_OnRegistrationClient)mClassContainer.mCanonicalTileEntity).onRegistrationClient(MultiTileEntityRegistry.this, mClassContainer.mID);
			}
			if (mRecipe != null && mRecipe.length > 1) {
				if (mRecipe[0] instanceof Object[]) mRecipe = (Object[])mRecipe[0];
				if (mRecipe.length > 2) CR.shaped(getItem(mClassContainer.mID), CR.DEF_REV_NCC, mRecipe);
			}
			// A simple special case to make it easier to add a Machine to Recipe Lists without having to worry about anything.
			String
			tRecipeMapName = mClassContainer.mParameters.getString(NBT_RECIPEMAP);
			if (UT.Code.stringValid(tRecipeMapName)) {RecipeMap tMap = RecipeMap.RECIPE_MAPS.get(tRecipeMapName); if (tMap != null) tMap.mRecipeMachineList.add(getItem(mClassContainer.mID));}
			tRecipeMapName = mClassContainer.mParameters.getString(NBT_FUELMAP);
			if (UT.Code.stringValid(tRecipeMapName)) {RecipeMap tMap = RecipeMap.RECIPE_MAPS.get(tRecipeMapName); if (tMap != null) tMap.mRecipeMachineList.add(getItem(mClassContainer.mID));}
			return getItem(mClassContainer.mID);
		}
	}
	
	public short mLastRegisteredID = W;
	
	public ItemStack getItem() {return getItem(mLastRegisteredID, 1, null);}
	public ItemStack getItem(NBTTagCompound aNBT) {return getItem(mLastRegisteredID, 1, aNBT);}
	public ItemStack getItem(int aID) {return getItem(aID, 1, null);}
	public ItemStack getItem(int aID, NBTTagCompound aNBT) {return getItem(aID, 1, aNBT);}
	public ItemStack getItem(int aID, long aAmount) {return getItem(aID, aAmount, null);}
	
	public ItemStack getItem(int aID, long aAmount, NBTTagCompound aNBT) {
		ItemStack rStack = ST.make(mBlock, (int)aAmount, aID);
		if (aNBT == null) aNBT = UT.NBT.make();
		if (aNBT.hasNoTags()) {
			MultiTileEntityContainer tTileEntityContainer = getNewTileEntityContainer(aID, aNBT);
			if (tTileEntityContainer != null) ((IMultiTileEntity)tTileEntityContainer.mTileEntity).writeItemNBT(aNBT);
		}
		UT.NBT.set(rStack, aNBT);
		return rStack;
	}

	// get 由于是共用的一个语言 map 所以可以不用改
	public String getLocal(int aID) {return LH.get(mNameInternal+"."+aID+".name");}
	
	public MultiTileEntityClassContainer getClassContainer(int aID) {return mRegistry.get((short)aID);}
	public MultiTileEntityClassContainer getClassContainer(ItemStack aStack) {return mRegistry.get(ST.meta_(aStack));}
	
	public TileEntity getNewTileEntity(int aID)                                                 {MultiTileEntityContainer tContainer =  getNewTileEntityContainer(null  ,  0,  0,  0, aID, null); return tContainer == null ? null : tContainer.mTileEntity;}
	public TileEntity getNewTileEntity(World aWorld, int aX, int aY, int aZ, int aID)           {MultiTileEntityContainer tContainer =  getNewTileEntityContainer(aWorld, aX, aY, aZ, aID, null); return tContainer == null ? null : tContainer.mTileEntity;}
	
	public TileEntity getNewTileEntity(ItemStack aStack)                                        {MultiTileEntityContainer tContainer =  getNewTileEntityContainer(null  ,  0,  0,  0, ST.meta_(aStack), aStack.getTagCompound()); return tContainer == null ? null : tContainer.mTileEntity;}
	public TileEntity getNewTileEntity(World aWorld, int aX, int aY, int aZ, ItemStack aStack)  {MultiTileEntityContainer tContainer =  getNewTileEntityContainer(aWorld, aX, aY, aZ, ST.meta_(aStack), aStack.getTagCompound()); return tContainer == null ? null : tContainer.mTileEntity;}
	
	public MultiTileEntityContainer getNewTileEntityContainer(ItemStack aStack)                                                 {return getNewTileEntityContainer(null  ,  0,  0,  0, ST.meta_(aStack), aStack.getTagCompound());}
	public MultiTileEntityContainer getNewTileEntityContainer(World aWorld, int aX, int aY, int aZ, ItemStack aStack)           {return getNewTileEntityContainer(aWorld, aX, aY, aZ, ST.meta_(aStack), aStack.getTagCompound());}
	
	public MultiTileEntityContainer getNewTileEntityContainer(int aID, NBTTagCompound aNBT) {return getNewTileEntityContainer(null, 0, 0, 0, aID, aNBT);}
	public MultiTileEntityContainer getNewTileEntityContainer(World aWorld, int aX, int aY, int aZ, int aID, NBTTagCompound aNBT) {
		MultiTileEntityClassContainer tClass = mRegistry.get((short)aID);
		if (tClass == null || tClass.mBlock == null) return null;
		MultiTileEntityContainer rContainer = new MultiTileEntityContainer((TileEntity)UT.Reflection.callConstructor(tClass.mClass, -1, null, T), tClass.mBlock, tClass.mBlockMetaData);
		if (rContainer.mTileEntity == null) return null;
		rContainer.mTileEntity.setWorldObj(aWorld);
		rContainer.mTileEntity.xCoord = aX;
		rContainer.mTileEntity.yCoord = aY;
		rContainer.mTileEntity.zCoord = aZ;
		((IMultiTileEntity)rContainer.mTileEntity).initFromNBT(aNBT == null || aNBT.hasNoTags() ? tClass.mParameters : UT.NBT.fuse(aNBT, tClass.mParameters), (short)aID, (short)Block.getIdFromBlock(mBlock));
		return rContainer;
	}
	
	public static void onServerStart() {for (Class<?> tClass : sRegisteredTileEntities) if (IMTE_OnServerStart.class.isAssignableFrom(tClass)) try {((IMTE_OnServerStart)tClass.newInstance()).onServerStart();} catch (Throwable e) {e.printStackTrace(ERR);}}
	public static void onServerStop () {for (Class<?> tClass : sRegisteredTileEntities) if (IMTE_OnServerStop .class.isAssignableFrom(tClass)) try {((IMTE_OnServerStop )tClass.newInstance()).onServerStop ();} catch (Throwable e) {e.printStackTrace(ERR);}}
	
	public static void onServerLoad(File aSaveLocation) {for (Class<?> tClass : sRegisteredTileEntities) if (IMTE_OnServerLoad.class.isAssignableFrom(tClass)) try {((IMTE_OnServerLoad)tClass.newInstance()).onServerLoad(aSaveLocation);} catch (Throwable e) {e.printStackTrace(ERR);}}
	public static void onServerSave(File aSaveLocation) {for (Class<?> tClass : sRegisteredTileEntities) if (IMTE_OnServerSave.class.isAssignableFrom(tClass)) try {((IMTE_OnServerSave)tClass.newInstance()).onServerSave(aSaveLocation);} catch (Throwable e) {e.printStackTrace(ERR);}}
}
