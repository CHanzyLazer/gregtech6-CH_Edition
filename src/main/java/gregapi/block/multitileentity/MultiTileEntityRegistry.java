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
import java.util.*;

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
	private static final HashSetNoNulls<String> sRegisteredTileEntityClassNames = new HashSetNoNulls<>();
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
		REGISTRIES.put(mBlock, W, this);
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
	
	/** Returns the MultiTileEntityRegistry ID that is currently used by this World. */
	public int currentID() {return ST.id(mBlock);}
	
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
		mLastRegisteredID = aClassContainer.mID; // 所有 add 方法都会修改此值，并且要最先修改以免 return 掉
		AddObject tAddObject = new AddObject(aLocalised, aCategoricalName, aClassContainer, aRecipe);
		if (!mIsModifyingAdd) return tAddObject.addSelf();
		/// 先检测替换和移除，保证只有存在的项才会设置，而不会都变成 null
		if (mReplacingAddList.containsKey(aClassContainer.mID)) {
			tAddObject = mReplacingAddList.get(aClassContainer.mID).doReplace(tAddObject);
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
	public AddReplacer addReplacer(int aID) {
		AddReplacer tAddReplacer = mReplacingAddList.get((short)aID);
		if (tAddReplacer == null) {
			tAddReplacer = new AddReplacer();
			mReplacingAddList.put((short)aID, tAddReplacer);
		}
		return tAddReplacer;
	}
	
	// 删除指定条目
	public void removeAdds(int aID) {
		addReplacer(aID).remove();
	}
	// 在指定位置之前添加条目（添加项默认是 gtch）
	public void appendAddBefore(int aBeforeID, String aLocalised, String aCategoricalName, int aID, int aCreativeTabID, Class<? extends TileEntity> aClass, int aBlockMetaData, int aStackSize, MultiTileEntityBlock aBlock, NBTTagCompound aParameters, Object... aRecipe) {
		appendAddBefore(aBeforeID, aLocalised, aCategoricalName, new MultiTileEntityClassContainer(RegType.GTCH, aID, aCreativeTabID, aClass, aBlockMetaData, aStackSize, aBlock, aParameters), aRecipe);
	}
	public void appendAddBefore(int aBeforeID, RegType aRegType, String aLocalised, String aCategoricalName, int aID, int aCreativeTabID, Class<? extends TileEntity> aClass, int aBlockMetaData, int aStackSize, MultiTileEntityBlock aBlock, NBTTagCompound aParameters, Object... aRecipe) {
		appendAddBefore(aBeforeID, aLocalised, aCategoricalName, new MultiTileEntityClassContainer(aRegType, aID, aCreativeTabID, aClass, aBlockMetaData, aStackSize, aBlock, aParameters), aRecipe);
	}
	public void appendAddBefore(int aBeforeID, String aLocalised, String aCategoricalName, MultiTileEntityClassContainer aClassContainer, Object... aRecipe) {
		mLastRegisteredID = aClassContainer.mID; // 所有 add 方法都会修改此值，并且要最先修改以免 return 掉
		AddObject tAddObject = new AddObject(aLocalised, aCategoricalName, aClassContainer, aRecipe);
		if (!mAppendingAddBeforeList.containsKey((short)aBeforeID)) mAppendingAddBeforeList.put((short)aBeforeID, new LinkedList<AddObject>());
		// replace 也可以影响 append 操作
		if (mReplacingAddList.containsKey(aClassContainer.mID)) {
			tAddObject = mReplacingAddList.get(aClassContainer.mID).doReplace(tAddObject);
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
		mLastRegisteredID = aClassContainer.mID; // 所有 add 方法都会修改此值，并且要最先修改以免 return 掉
		AddObject tAddObject = new AddObject(aLocalised, aCategoricalName, aClassContainer, aRecipe);
		if (!mAppendingAddAfterList.containsKey((short)aAfterID)) mAppendingAddAfterList.put((short)aAfterID, new LinkedList<AddObject>());
		// replace 也可以影响 append 操作
		if (mReplacingAddList.containsKey(aClassContainer.mID)) {
			tAddObject = mReplacingAddList.get(aClassContainer.mID).doReplace(tAddObject);
			mReplacingAddList.remove(aClassContainer.mID); // 获取后移除
		}
		if (tAddObject != null) mAppendingAddAfterList.get((short)aAfterID).add(tAddObject);
	}
	
	
	public void MODIFYING_ADD_START() {mIsModifyingAdd = T;}
	public void MODIFYING_ADD_END() {
		mIsModifyingAdd = F;
		// replace 条目应该已经为空，进行错误检测
		for (Map.Entry<Short, AddReplacer> tEntry : mReplacingAddList.entrySet()) {
			if (tEntry.getValue().mRemoved) ERR.println("Has no ID \"" + tEntry.getKey() + "\" on removeHolding, remove fail!");
			else ERR.println("MTE REGISTRY ERROR: Has no ID \"" + tEntry.getKey() + "\" on replaceAdd, replace fail!");
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
	private final Map<Short, AddReplacer> mReplacingAddList = new HashMap<>(); // 存储将要替换的项目
	public class AddObject {
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
			if (aClassContainer.mRegType == RegType.GREG) LH.add(mNameInternal+"."+ aClassContainer.mID, aLocalised);
			else LH_CH.add(aClassContainer.mRegType, mNameInternal+"."+ aClassContainer.mID, aLocalised);
			
			mRegistry.put(aClassContainer.mID, aClassContainer);
			mRegistrations.add(aClassContainer);
			if (!mCreativeTabs.containsKey(aClassContainer.mCreativeTabID)) mCreativeTabs.put(aClassContainer.mCreativeTabID, new CreativeTab(aClassContainer.mRegType, mNameInternal+"."+ aClassContainer.mCreativeTabID, aCategoricalName, Item.getItemFromBlock(mBlock), aClassContainer.mCreativeTabID));
			if (sRegisteredTileEntityClassNames.add(aClassContainer.mCanonicalTileEntity.getClass().getName()) && sRegisteredTileEntities.add(aClassContainer.mCanonicalTileEntity.getClass())) {
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
	public class AddReplacer {
		private boolean mRemoved = F;
		private RegType mRegType = null;
		private String mLocalised = null;
		private String mCategoricalName = null;
		private Integer mCreativeTabID = null;
		private Class<? extends TileEntity> mClass = null;
		private Integer mBlockMetaData = null;
		private Integer mStackSize = null;
		private MultiTileEntityBlock mBlock = null;
		private final NBTTagCompound mParametersMerge = UT.NBT.make(); // 需要合并的参数，如果有相同的项目会直接替换
		private final List<String> mParametersRemove = new LinkedList<>(); // 需要删除的参数，由于 NBT 的机制没有不会进行警告
		private NBTTagCompound mParametersMergeLast = UT.NBT.make(); // User use only, 用来保证用户添加的参数能够添加回我删除的参数，而用户删除的能够删除我添加的
		private List<String> mParametersRemoveLast = new LinkedList<>(); // User use only, 用来保证用户添加的参数能够添加回我删除的参数，而用户删除的能够删除我添加的
		private Object[] mRecipe = null;
		private AddReplacer() {}
		
		// 返回替换后的 AddObject
		public AddObject doReplace(AddObject oAddObject) {
			if (mRemoved) return null;
			
			NBTTagCompound rParameters = (NBTTagCompound)oAddObject.aClassContainer.mParameters.copy();
			for (Object tKey : mParametersMerge.func_150296_c()) rParameters.setTag(tKey.toString(), mParametersMerge.getTag(tKey.toString()));
			for (String tKey : mParametersRemove) rParameters.removeTag(tKey);
			for (Object tKey : mParametersMergeLast.func_150296_c()) rParameters.setTag(tKey.toString(), mParametersMergeLast.getTag(tKey.toString()));
			for (String tKey : mParametersRemoveLast) rParameters.removeTag(tKey);
			
			return new AddObject(
			mLocalised ==null?oAddObject.aLocalised: mLocalised, mCategoricalName ==null?oAddObject.aCategoricalName: mCategoricalName, new MultiTileEntityClassContainer(
			mRegType ==null?oAddObject.aClassContainer.mRegType: mRegType, oAddObject.aClassContainer.mID, mCreativeTabID ==null?oAddObject.aClassContainer.mCreativeTabID: mCreativeTabID, mClass ==null?oAddObject.aClassContainer.mClass: mClass, mBlockMetaData ==null?oAddObject.aClassContainer.mBlockMetaData: mBlockMetaData, mStackSize ==null?oAddObject.aClassContainer.mStackSize: mStackSize, mBlock ==null?oAddObject.aClassContainer.mBlock: mBlock, rParameters), mRecipe ==null?oAddObject.aRecipe: mRecipe);
		}
		
		/* 用于链式调用修改的接口，保证重复修改后保留第一次的修改 */
		public AddReplacer remove() 								{mRemoved = T; return this;}
		public AddReplacer regType(RegType aRegType) 				{if (mRegType==null) mRegType = aRegType; return this;}
		public AddReplacer localised(String aLocalised) 			{if (mLocalised==null) mLocalised = aLocalised; return this;}
		public AddReplacer categoricalName(String aCategoricalName) {if (mCategoricalName==null) mCategoricalName = aCategoricalName; return this;}
		public AddReplacer creativeTabID(Integer aCreativeTabID) 	{if (mCreativeTabID==null) mCreativeTabID = aCreativeTabID; return this;}
		public AddReplacer te(Class<? extends TileEntity> aClass) 	{if (mClass==null) mClass = aClass; return this;}
		public AddReplacer toolQuality(Integer aBlockMetaData) 		{if (mBlockMetaData==null) mBlockMetaData = aBlockMetaData; return this;}
		public AddReplacer stackSize(Integer aStackSize) 			{if (mStackSize==null) mStackSize = aStackSize; return this;}
		public AddReplacer block(MultiTileEntityBlock aBlock) 		{if (mBlock==null) mBlock = aBlock; return this;}
		public AddReplacer recipe(Object... aRecipe) 				{if (mRecipe==null) mRecipe = aRecipe; return this;}
		
		// 如果有相同的项需要保留旧值
		public AddReplacer setParameters(String aFirstKey, Object aFirstValue, Object... aTags) {
			NBTTagCompound tParameters = UT.NBT.make(aFirstKey, aFirstValue, aTags);
			for (Object tKey : tParameters.func_150296_c()) if (!mParametersMerge.hasKey(tKey.toString())) mParametersMerge.setTag(tKey.toString(), tParameters.getTag(tKey.toString()));
			return this;
		}
		// 提供直接传入数组的接口
		public AddReplacer setParameterArray(String aKey, long... aValues) {
			for (int i = 0; i < aValues.length; ++i) {
				String tIdxKey = aKey+"."+i;
				if (!mParametersMerge.hasKey(tIdxKey)) UT.NBT.setNumber(mParametersMerge, tIdxKey, aValues[i]);
			}
			return this;
		}
		// 提供直接传入数组的接口
		public AddReplacer setParameterLastArray(String aKey, long... aValues) {
			for (int i = 0; i < aValues.length; ++i) {
				String tIdxKey = aKey+"."+i;
				if (!mParametersMergeLast.hasKey(tIdxKey)) UT.NBT.setNumber(mParametersMergeLast, tIdxKey, aValues[i]);
			}
			return this;
		}
		// 直接添加
		public AddReplacer removeParameters(String... aRemovedKeys) {mParametersRemove.addAll(Arrays.asList(aRemovedKeys)); return this;}
		
		// user use oly
		public AddReplacer setParametersMergeLast(NBTTagCompound aParametersMergeLast) {if (aParametersMergeLast != null) mParametersMergeLast = aParametersMergeLast; return this;}
		public AddReplacer removeParametersRemoveLast(List<String> aParametersRemoveLast) {if (aParametersRemoveLast != null) mParametersRemoveLast = aParametersRemoveLast; return this;}
	}
	
	public short mLastRegisteredID = W;
	
	/* 为了避免后续修改影响原本的逻辑，虽然所有 add 方都会修改 mLastRegisteredID，但在 addself 中不会修改，从而保证此值的局域性*/
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
	public String getLocal(int aID) {return LH.get(mNameInternal+"."+aID);}
	
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
		// 由于还会从 nbt 中读取坐标，所以原本直接设置是无效的，必须要直接设置到 nbt 中（需要注意即使这样也不能保证坐标是一定准确的）
		NBTTagCompound tNBT = (aNBT == null || aNBT.hasNoTags()) ? tClass.mParameters : UT.NBT.fuse(aNBT, tClass.mParameters);
		tNBT.setInteger("x", aX);
		tNBT.setInteger("y", aY);
		tNBT.setInteger("z", aZ);
		((IMultiTileEntity)rContainer.mTileEntity).initFromNBT(tNBT, (short)aID, (short)Block.getIdFromBlock(mBlock));
		return rContainer;
	}
	
	public static void onServerStart() {for (Class<?> tClass : sRegisteredTileEntities) if (IMTE_OnServerStart.class.isAssignableFrom(tClass)) try {((IMTE_OnServerStart)tClass.newInstance()).onServerStart();} catch (Throwable e) {e.printStackTrace(ERR);}}
	public static void onServerStop () {for (Class<?> tClass : sRegisteredTileEntities) if (IMTE_OnServerStop .class.isAssignableFrom(tClass)) try {((IMTE_OnServerStop )tClass.newInstance()).onServerStop ();} catch (Throwable e) {e.printStackTrace(ERR);}}
	
	public static void onServerLoad(File aSaveLocation) {for (Class<?> tClass : sRegisteredTileEntities) if (IMTE_OnServerLoad.class.isAssignableFrom(tClass)) try {((IMTE_OnServerLoad)tClass.newInstance()).onServerLoad(aSaveLocation);} catch (Throwable e) {e.printStackTrace(ERR);}}
	public static void onServerSave(File aSaveLocation) {for (Class<?> tClass : sRegisteredTileEntities) if (IMTE_OnServerSave.class.isAssignableFrom(tClass)) try {((IMTE_OnServerSave)tClass.newInstance()).onServerSave(aSaveLocation);} catch (Throwable e) {e.printStackTrace(ERR);}}
}
