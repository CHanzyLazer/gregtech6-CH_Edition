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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static gregapi.data.CS.*;
import static gregtechCH.data.CS_CH.*;

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
		return add(aLocalised, aCategoricalName, new MultiTileEntityClassContainer(RegType.GREG, aID, aCreativeTabID, aClass, aBlockMetaData, aStackSize, aBlock, aParameters), aRecipe);
	}
	
	// GTCH, 为额外添加的机器添加特殊情况
	public ItemStack add(RegType aRegType, String aLocalised, String aCategoricalName, int aID, int aCreativeTabID, Class<? extends TileEntity> aClass, int aBlockMetaData, int aStackSize, MultiTileEntityBlock aBlock, NBTTagCompound aParameters, Object... aRecipe) {
		return add(aLocalised, aCategoricalName, new MultiTileEntityClassContainer(aRegType, aID, aCreativeTabID, aClass, aBlockMetaData, aStackSize, aBlock, aParameters), aRecipe);
	}
	
	// GTCH, 通过添加时检测 ID 是否处于替换 Map 中来进行“完美”的替换或删除
	// 不允许一个 ID 被添加，然后移除，然后又添加这种情况。（移除必须要在添加之前）
	public ItemStack add(String aLocalised, String aCategoricalName, MultiTileEntityClassContainer aClassContainer, Object... aRecipe) {
		AddObject tAddObject = new AddObject(aLocalised, aCategoricalName, aClassContainer, aRecipe);
		if (!mIsModifyingAdd) return tAddObject.addSelf();
		/// 先检测替换和移除
		if (mReplacingAddList.containsKey(aClassContainer.mID)) {
			tAddObject = mReplacingAddList.get(aClassContainer.mID); // 用来保证只有存在的项才会设置，而不会都变成 null
			mReplacingAddList.remove(aClassContainer.mID); // 获取后移除
		}
		/// 检测之前添加
		List<AddObject>
		tAppendList = mAppendingAddBeforeList.get(aClassContainer.mID);
		if (tAppendList != null) {
			for (AddObject tAppendObject : tAppendList) tAppendObject.addSelf(); // 遍历列表添加
			mAppendingAddBeforeList.remove(aClassContainer.mID); // 添加后移除此项
		}
		/// 进行添加
		ItemStack tOut = (tAddObject != null) ? tAddObject.addSelf() : null;
		/// 检测之后添加
		tAppendList = mAppendingAddAfterList.get(aClassContainer.mID);
		if (tAppendList != null) {
			for (AddObject tAppendObject : tAppendList) tAppendObject.addSelf(); // 遍历列表添加
			mAppendingAddAfterList.remove(aClassContainer.mID); // 添加后移除此项
		}
		return tOut;
	}
	
	/* 提供一些修改 adds 的一些接口 */
	// 修改原有的条目
	public void replaceAdd(String aLocalised, String aCategoricalName, int aID, int aCreativeTabID, Class<? extends TileEntity> aClass, int aBlockMetaData, int aStackSize, MultiTileEntityBlock aBlock, NBTTagCompound aParameters, Object... aRecipe) {
		replaceAdd(aLocalised, aCategoricalName, new MultiTileEntityClassContainer(RegType.GREG, aID, aCreativeTabID, aClass, aBlockMetaData, aStackSize, aBlock, aParameters), aRecipe);
	}
	public void replaceAdd(RegType aRegType, String aLocalised, String aCategoricalName, int aID, int aCreativeTabID, Class<? extends TileEntity> aClass, int aBlockMetaData, int aStackSize, MultiTileEntityBlock aBlock, NBTTagCompound aParameters, Object... aRecipe) {
		replaceAdd(aLocalised, aCategoricalName, new MultiTileEntityClassContainer(aRegType, aID, aCreativeTabID, aClass, aBlockMetaData, aStackSize, aBlock, aParameters), aRecipe);
	}
	public void replaceAdd(String aLocalised, String aCategoricalName, MultiTileEntityClassContainer aClassContainer, Object... aRecipe) {
		AddObject tAddObject = new AddObject(aLocalised, aCategoricalName, aClassContainer, aRecipe);
		if (mReplacingAddList.containsKey(aClassContainer.mID)) {
			ERR.println("MTE REGISTRY WARNING: The ID \"" + aClassContainer.mID + "\" is already on the replace list, replace fail!"); // TODO 后续为了配置文件修改，可以不进行此报错
			return;
		}
		mReplacingAddList.put(aClassContainer.mID, tAddObject);
	}
	// 删除指定条目
	public void removeAdds(int aID) {
		if (mReplacingAddList.containsKey((short)aID)) {
			ERR.println("MTE REGISTRY WARNING: The ID \"" + aID + "\" is already on the replace list, remove fail!"); // TODO 后续为了配置文件修改，可以不进行此报错
			return;
		}
		mReplacingAddList.put((short)aID, null);
	}
	// 在指定位置之前添加条目（添加项默认是 gtch）
	public void appendAddBefore(int aBeforeID, String aLocalised, String aCategoricalName, int aID, int aCreativeTabID, Class<? extends TileEntity> aClass, int aBlockMetaData, int aStackSize, MultiTileEntityBlock aBlock, NBTTagCompound aParameters, Object... aRecipe) {
		appendAddBefore(aBeforeID, aLocalised, aCategoricalName, new MultiTileEntityClassContainer(RegType.GTCH, aID, aCreativeTabID, aClass, aBlockMetaData, aStackSize, aBlock, aParameters), aRecipe);
	}
	public void appendAddBefore(int aBeforeID, RegType aRegType, String aLocalised, String aCategoricalName, int aID, int aCreativeTabID, Class<? extends TileEntity> aClass, int aBlockMetaData, int aStackSize, MultiTileEntityBlock aBlock, NBTTagCompound aParameters, Object... aRecipe) {
		appendAddBefore(aBeforeID, aLocalised, aCategoricalName, new MultiTileEntityClassContainer(aRegType, aID, aCreativeTabID, aClass, aBlockMetaData, aStackSize, aBlock, aParameters), aRecipe);
	}
	public void appendAddBefore(int aBeforeID, String aLocalised, String aCategoricalName, MultiTileEntityClassContainer aClassContainer, Object... aRecipe) {
		AddObject tAddObject = new AddObject(aLocalised, aCategoricalName, aClassContainer, aRecipe);
		if (!mAppendingAddBeforeList.containsKey((short)aBeforeID)) mAppendingAddBeforeList.put((short)aBeforeID, new LinkedList<AddObject>());
		// replace 也可以影响 append 操作
		if (mReplacingAddList.containsKey(aClassContainer.mID)) {
			tAddObject = mReplacingAddList.get(aClassContainer.mID); // 用来保证只有存在的项才会设置，而不会都变成 null
			mReplacingAddList.remove(aClassContainer.mID); // 获取后移除
		}
		if (tAddObject != null) mAppendingAddBeforeList.get((short)aBeforeID).add(tAddObject);
	}
	// 在指定位置之后添加条目
	public void appendAddAfter(int aAfterID, String aLocalised, String aCategoricalName, int aID, int aCreativeTabID, Class<? extends TileEntity> aClass, int aBlockMetaData, int aStackSize, MultiTileEntityBlock aBlock, NBTTagCompound aParameters, Object... aRecipe) {
		appendAddAfter(aAfterID, aLocalised, aCategoricalName, new MultiTileEntityClassContainer(RegType.GTCH, aID, aCreativeTabID, aClass, aBlockMetaData, aStackSize, aBlock, aParameters), aRecipe);
	}
	public void appendAddAfter(int aAfterID, RegType aRegType, String aLocalised, String aCategoricalName, int aID, int aCreativeTabID, Class<? extends TileEntity> aClass, int aBlockMetaData, int aStackSize, MultiTileEntityBlock aBlock, NBTTagCompound aParameters, Object... aRecipe) {
		appendAddAfter(aAfterID, aLocalised, aCategoricalName, new MultiTileEntityClassContainer(aRegType, aID, aCreativeTabID, aClass, aBlockMetaData, aStackSize, aBlock, aParameters), aRecipe);
	}
	public void appendAddAfter(int aAfterID, String aLocalised, String aCategoricalName, MultiTileEntityClassContainer aClassContainer, Object... aRecipe) {
		AddObject tAddObject = new AddObject(aLocalised, aCategoricalName, aClassContainer, aRecipe);
		if (!mAppendingAddAfterList.containsKey((short)aAfterID)) mAppendingAddAfterList.put((short)aAfterID, new LinkedList<AddObject>());
		// replace 也可以影响 append 操作
		if (mReplacingAddList.containsKey(aClassContainer.mID)) {
			tAddObject = mReplacingAddList.get(aClassContainer.mID); // 用来保证只有存在的项才会设置，而不会都变成 null
			mReplacingAddList.remove(aClassContainer.mID); // 获取后移除
		}
		if (tAddObject != null) mAppendingAddAfterList.get((short)aAfterID).add(tAddObject);
	}
	
	
	public void MODIFYING_ADD_START() {mIsModifyingAdd = T;}
	public void MODIFYING_ADD_END() {
		mIsModifyingAdd = F;
		// replace 条目应该已经为空，进行错误检测
		for (Map.Entry<Short, AddObject> tEntry : mReplacingAddList.entrySet()) {
			if (tEntry.getValue() != null) ERR.println("MTE REGISTRY ERROR: Has no ID \"" + tEntry.getKey() + "\" on replaceAdd, replace fail!");
			else ERR.println("Has no ID \"" + tEntry.getKey() + "\" on removeHolding, remove fail!");
		}
		mReplacingAddList.clear();
		// appendBefore 条目应该已经为空，进行错误检测
		for (Map.Entry<Short,  List<AddObject>> tEntry : mAppendingAddBeforeList.entrySet()) {
			ERR.println("MTE REGISTRY WARNING: Has no ID \"" + tEntry.getKey() + "\" on appendAdd, these MTE will be put at the end!");
			for (AddObject tAppendObject : tEntry.getValue()) tAppendObject.addSelf();
		}
		mAppendingAddBeforeList.clear();
		// appendAfterefore 条目应该已经为空，进行错误检测
		for (Map.Entry<Short, List<AddObject>> tEntry : mAppendingAddAfterList.entrySet()) {
			ERR.println("MTE REGISTRY WARNING: Has no ID \"" + tEntry.getKey() + "\" on appendAdd, these MTE will be put at the end!");
			for (AddObject tAppendObject : tEntry.getValue()) tAppendObject.addSelf();
		}
		mAppendingAddAfterList.clear();
	}
	
	private boolean mIsModifyingAdd = F;
	private final Map<Short, List<AddObject>> mAppendingAddBeforeList = new HashMap<>(); // 存储需要在指定位置之前添加的项
	private final Map<Short, List<AddObject>> mAppendingAddAfterList = new HashMap<>(); // 存储需要在指定位置之后添加的项
	private final Map<Short, AddObject> mReplacingAddList = new HashMap<>(); // 存储将要替换的项目，如果为 null 则为删除
	private class AddObject {
		private final String aLocalised;
		private final String aCategoricalName;
		private final MultiTileEntityClassContainer aClassContainer;
		private Object[] aRecipe;
		
		public AddObject(String aLocalised, String aCategoricalName, MultiTileEntityClassContainer aClassContainer, Object... aRecipe) {
			this.aLocalised = aLocalised; this.aCategoricalName = aCategoricalName; this.aClassContainer = aClassContainer; this.aRecipe = aRecipe;
		}
		
		private ItemStack addSelf() {
			boolean tFailed = F;
			if (UT.Code.stringInvalid(aLocalised)) {
				ERR.println("MTE REGISTRY ERROR: Localisation Missing!");
				tFailed = T;
			}
			if (aClassContainer == null) {
				ERR.println("MTE REGISTRY ERROR: Class Container is null!");
				tFailed = T;
			} else {
				if (aClassContainer.mClass == null) {
					ERR.println("MTE REGISTRY ERROR: Class inside Class Container is null!");
					tFailed = T;
				}
				if (aClassContainer.mID == W) {
					ERR.println("MTE REGISTRY ERROR: Class Container uses Wildcard MetaData!");
					tFailed = T;
				}
				if (aClassContainer.mID < 0) {
					ERR.println("MTE REGISTRY ERROR: Class Container uses negative MetaData!");
					tFailed = T;
				}
				if (mRegistry.containsKey(aClassContainer.mID)) {
					ERR.println("MTE REGISTRY ERROR: Class Container uses occupied MetaData!");
					tFailed = T;
				}
			}
			if (tFailed) {
				ERR.println("MTE REGISTRY ERROR: STACKTRACE START");
				int i = 0; for (StackTraceElement tElement : new Exception().getStackTrace()) if (i++<10 && !tElement.getClassName().startsWith("sun")) ERR.println("\tat " + tElement); else break;
				ERR.println("MTE REGISTRY ERROR: STACKTRACE END");
			}
			if (tFailed) return null;
			assert aClassContainer != null;
			// 目前所有的非 greg 的 MTE 都使用外置的语言文件
			switch (aClassContainer.mType) {
			case GTCH:
				LH_CH.add(mNameInternal+"."+ aClassContainer.mID+".name", aLocalised); break;
			case GT6U:
				LH_CH.add(T, mNameInternal+"."+ aClassContainer.mID+".name", aLocalised); break;
			case GREG: default:
				LH.add(mNameInternal+"."+ aClassContainer.mID+".name", aLocalised); break;
			}
			mRegistry.put(aClassContainer.mID, aClassContainer);
			mLastRegisteredID = aClassContainer.mID;
			mRegistrations.add(aClassContainer);
			if (!mCreativeTabs.containsKey(aClassContainer.mCreativeTabID)) mCreativeTabs.put(aClassContainer.mCreativeTabID, new CreativeTab(mNameInternal+"."+ aClassContainer.mCreativeTabID, aCategoricalName, Item.getItemFromBlock(mBlock), aClassContainer.mCreativeTabID));
			if (sRegisteredTileEntities.add(aClassContainer.mCanonicalTileEntity.getClass())) {
				if (aClassContainer.mCanonicalTileEntity instanceof IMTE_OnRegistrationFirst) ((IMTE_OnRegistrationFirst) aClassContainer.mCanonicalTileEntity).onRegistrationFirst(MultiTileEntityRegistry.this, aClassContainer.mID);
				if (CODE_CLIENT && aClassContainer.mCanonicalTileEntity instanceof IMTE_OnRegistrationFirstClient) ((IMTE_OnRegistrationFirstClient) aClassContainer.mCanonicalTileEntity).onRegistrationFirstClient(MultiTileEntityRegistry.this, aClassContainer.mID);
			}
			if (mRegisteredTileEntities.add(aClassContainer.mCanonicalTileEntity.getClass())) {
				if (aClassContainer.mCanonicalTileEntity instanceof IMTE_OnRegistrationFirstOfRegister) ((IMTE_OnRegistrationFirstOfRegister) aClassContainer.mCanonicalTileEntity).onRegistrationFirstOfRegister(MultiTileEntityRegistry.this, aClassContainer.mID);
				if (aClassContainer.mCanonicalTileEntity instanceof IMTE_OnRegistrationFirstOfRegisterClient) ((IMTE_OnRegistrationFirstOfRegisterClient) aClassContainer.mCanonicalTileEntity).onRegistrationFirstOfRegisterClient(MultiTileEntityRegistry.this, aClassContainer.mID);
			}
			if (aClassContainer.mCanonicalTileEntity instanceof IMTE_OnRegistration) {
				((IMTE_OnRegistration) aClassContainer.mCanonicalTileEntity).onRegistration(MultiTileEntityRegistry.this, aClassContainer.mID);
			}
			if (CODE_CLIENT && aClassContainer.mCanonicalTileEntity instanceof IMTE_OnRegistrationClient) {
				((IMTE_OnRegistrationClient) aClassContainer.mCanonicalTileEntity).onRegistrationClient(MultiTileEntityRegistry.this, aClassContainer.mID);
			}
			if (aRecipe != null && aRecipe.length > 1) {
				if (aRecipe[0] instanceof Object[]) aRecipe = (Object[]) aRecipe[0];
				if (aRecipe.length > 2) CR.shaped(getItem(aClassContainer.mID), CR.DEF_REV_NCC, aRecipe);
			}
			// A simple special case to make it easier to add a Machine to Recipe Lists without having to worry about anything.
			String
			tRecipeMapName = aClassContainer.mParameters.getString(NBT_RECIPEMAP);
			if (UT.Code.stringValid(tRecipeMapName)) {RecipeMap tMap = RecipeMap.RECIPE_MAPS.get(tRecipeMapName); if (tMap != null) tMap.mRecipeMachineList.add(getItem(aClassContainer.mID));}
			tRecipeMapName = aClassContainer.mParameters.getString(NBT_FUELMAP);
			if (UT.Code.stringValid(tRecipeMapName)) {RecipeMap tMap = RecipeMap.RECIPE_MAPS.get(tRecipeMapName); if (tMap != null) tMap.mRecipeMachineList.add(getItem(aClassContainer.mID));}
			return getItem(aClassContainer.mID);
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
	public MultiTileEntityClassContainer getClassContainer(ItemStack aStack) {return getClassContainer(ST.meta_(aStack));}
	
	public TileEntity getNewTileEntity(int aID)                                                 {MultiTileEntityContainer tContainer =  getNewTileEntityContainer(null  ,  0,  0,  0, aID, null); return tContainer == null ? null : tContainer.mTileEntity;}
	public TileEntity getNewTileEntity(World aWorld, int aX, int aY, int aZ, int aID)           {MultiTileEntityContainer tContainer =  getNewTileEntityContainer(aWorld, aX, aY, aZ, aID, null); return tContainer == null ? null : tContainer.mTileEntity;}
	
	public TileEntity getNewTileEntity(ItemStack aStack)                                        {MultiTileEntityContainer tContainer =  getNewTileEntityContainer(null  ,  0,  0,  0, ST.meta_(aStack), aStack.getTagCompound()); return tContainer == null ? null : tContainer.mTileEntity;}
	public TileEntity getNewTileEntity(World aWorld, int aX, int aY, int aZ, ItemStack aStack)  {MultiTileEntityContainer tContainer =  getNewTileEntityContainer(aWorld, aX, aY, aZ, ST.meta_(aStack), aStack.getTagCompound()); return tContainer == null ? null : tContainer.mTileEntity;}
	
	public MultiTileEntityContainer getNewTileEntityContainer(ItemStack aStack)                                                 {return getNewTileEntityContainer(null  ,  0,  0,  0, ST.meta_(aStack), aStack.getTagCompound());}
	public MultiTileEntityContainer getNewTileEntityContainer(World aWorld, int aX, int aY, int aZ, ItemStack aStack)           {return getNewTileEntityContainer(aWorld, aX, aY, aZ, ST.meta_(aStack), aStack.getTagCompound());}
	
	public MultiTileEntityContainer getNewTileEntityContainer(int aID, NBTTagCompound aNBT) {return getNewTileEntityContainer(null, 0, 0, 0, aID, aNBT);}
	public MultiTileEntityContainer getNewTileEntityContainer(World aWorld, int aX, int aY, int aZ, int aID, NBTTagCompound aNBT) {
		MultiTileEntityClassContainer tClass = getClassContainer(aID);
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
