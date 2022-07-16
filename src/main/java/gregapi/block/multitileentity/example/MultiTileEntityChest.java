/**
 * Copyright (c) 2021 GregTech-6 Team
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

package gregapi.block.multitileentity.example;

import static gregapi.data.CS.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregapi.block.multitileentity.IMultiTileEntity.*;
import gregapi.block.multitileentity.MultiTileEntityBlockInternal;
import gregapi.block.multitileentity.MultiTileEntityContainer;
import gregapi.block.multitileentity.MultiTileEntityRegistry;
import gregapi.data.LH;
import gregapi.data.MD;
import gregapi.data.MT;
import gregapi.gui.ContainerClientChest;
import gregapi.gui.ContainerCommonChest;
import gregapi.item.IItemColorableRGB;
import gregapi.network.INetworkHandler;
import gregapi.network.IPacket;
import gregapi.oredict.OreDictMaterial;
import gregapi.render.ITexture;
import gregapi.tileentity.ITileEntityAdjacentInventoryUpdatable;
import gregapi.tileentity.ITileEntityDecolorable;
import gregapi.tileentity.base.TileEntityBase05Inventories;
import gregapi.tileentity.data.ITileEntitySurface;
import gregapi.tileentity.delegate.DelegatorTileEntity;
import gregapi.util.UT;
import gregtechCH.tileentity.ITEPaintable_CH;
import gregtechCH.util.UT_CH;
import net.minecraft.block.Block;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ChestGenHooks;

/**
 * @author Gregorius Techneticies
 * 
 * An example implementation of a Chest with my MultiTileEntity System.
 */
public class MultiTileEntityChest extends TileEntityBase05Inventories implements ITEPaintable_CH, IMTE_GetLightOpacity, IItemColorableRGB, ITileEntityDecolorable, ITileEntitySurface, IMTE_OnRegistrationClient, IMTE_OnRegistrationFirstClient, IMTE_SyncDataByte, IMTE_AddToolTips, IMTE_SetBlockBoundsBasedOnState, IMTE_GetSubItems, IMTE_SyncDataByteArray, IMTE_GetExplosionResistance, IMTE_GetBlockHardness, IMTE_GetComparatorInputOverride, IMTE_GetSelectedBoundingBoxFromPool, IMTE_GetCollisionBoundingBoxFromPool, IMTE_OnPlaced, IMTE_OnToolClick {
	protected boolean mIsPainted = F;
	// GTCH, 用于在染色后保留一定原本颜色
	protected int mRGBaPaint = UNCOLORED;
	// 仅客户端有效
	protected int mRGBa = UNCOLORED;

	protected byte mFacing = 3, mUsingPlayers = 0, oUsingPlayers = 0;
	protected float mLidAngle = 0, oLidAngle = 0, mHardness = 6, mResistance = 3;
	protected OreDictMaterial mMaterial = MT.NULL;
	
	/** Gets supplied via Default NBT. */
	public String mTextureName = "", mDungeonLootName = "";
	
	public MultiTileEntityChest() {/**/}
	
	@Override
	public void readFromNBT2(NBTTagCompound aNBT) {
		super.readFromNBT2(aNBT);
//		if (aNBT.hasKey(NBT_COLOR)) mRGBa = aNBT.getInteger(NBT_COLOR); // 变成了临时变量，不需要存储
		if (aNBT.hasKey(NBT_FACING)) mFacing = aNBT.getByte(NBT_FACING);
		if (aNBT.hasKey(NBT_PAINTED)) mIsPainted = aNBT.getBoolean(NBT_PAINTED);
		if (aNBT.hasKey(NBT_TEXTURE)) mTextureName = aNBT.getString(NBT_TEXTURE);
		if (aNBT.hasKey("gt.dungeonloot")) mDungeonLootName = aNBT.getString("gt.dungeonloot");
		if (aNBT.hasKey(NBT_HARDNESS)) mHardness = aNBT.getFloat(NBT_HARDNESS);
		if (aNBT.hasKey(NBT_RESISTANCE)) mResistance = aNBT.getFloat(NBT_RESISTANCE);
		if (aNBT.hasKey(NBT_MATERIAL)) mMaterial = OreDictMaterial.get(aNBT.getString(NBT_MATERIAL));

		// 需要分情况讨论，考虑有不允许染色的，带有默认颜色的，并且不是材料颜色的方块
		if (isPainted()) {
			if (aNBT.hasKey(NBT_COLOR)) mRGBaPaint = (int) UT_CH.NBT.getItemNumber(aNBT.getInteger(NBT_COLOR)); // mRGBaPaint 替代原本的 NBT_COLOR
			mRGBa = UT_CH.Code.getPaintRGB(getBottomRGB(), mRGBaPaint);
		} else {
			mRGBaPaint = getBottomRGB();
			if (aNBT.hasKey(NBT_COLOR)) mRGBa = aNBT.getInteger(NBT_COLOR);
			else mRGBa = getOriginalRGB(); // 可以防止一些问题
		}
	}
	
	@Override
	public void writeToNBT2(NBTTagCompound aNBT) {
		super.writeToNBT2(aNBT);
		aNBT.setByte(NBT_FACING, mFacing);
		if (UT.Code.stringValid(mDungeonLootName)) aNBT.setString("gt.dungeonloot", mDungeonLootName);
	}
	
	@Override
	public NBTTagCompound writeItemNBT(NBTTagCompound aNBT) {
		aNBT = super.writeItemNBT(aNBT);
		if (UT.Code.stringValid(mDungeonLootName)) aNBT.setString("gt.dungeonloot", mDungeonLootName);
		return aNBT;
	}
	
	@Override
	public IPacket getClientDataPacket(boolean aSendAll) {
		return getClientDataPacketByteArray(aSendAll, mFacing, mUsingPlayers, (byte)getSizeInventory(), (byte)UT.Code.getR(mRGBaPaint), (byte)UT.Code.getG(mRGBaPaint), (byte)UT.Code.getB(mRGBaPaint), getPaintData());
	}
	
	@Override
	public boolean onPlaced(ItemStack aStack, EntityPlayer aPlayer, MultiTileEntityContainer aMTEContainer, World aWorld, int aX, int aY, int aZ, byte aSide, float aHitX, float aHitY, float aHitZ) {
		mFacing = UT.Code.getSideForPlayerPlacing(aPlayer, mFacing, SIDES_HORIZONTAL);
		return T;
	}
	
	@Override
	public void onTick(long aTimer, boolean aIsServerSide) {
		super.onTick(aTimer, aIsServerSide);
		if (aIsServerSide) {
			if (mInventoryChanged) {
				for (byte tSide : ALL_SIDES_VALID) {
					DelegatorTileEntity<TileEntity> tDelegator = getAdjacentTileEntity(tSide);
					if (tDelegator.mTileEntity instanceof ITileEntityAdjacentInventoryUpdatable) {
						((ITileEntityAdjacentInventoryUpdatable)tDelegator.mTileEntity).adjacentInventoryUpdated(tDelegator.mSideOfTileEntity, this);
					}
				}
			}
			if (mUsingPlayers > 0 && aTimer % 1200 == 0) {
				mUsingPlayers = UT.Code.bind7(getOpenGUIs());
			}
		} else {
			oLidAngle = mLidAngle;
			if (mUsingPlayers > 0) {
				mLidAngle = Math.min(1, mLidAngle+0.1F);
				if (mLidAngle > 0.1F && oLidAngle <= 0.1F) UT.Sounds.play("random.chestopen"  , 10, 0.5F, RNGSUS.nextFloat() * 0.1F + 0.9F, getCoords());
			} else {
				mLidAngle = Math.max(0, mLidAngle-0.1F);
				if (mLidAngle < 0.5F && oLidAngle >= 0.5F) UT.Sounds.play("random.chestclosed", 10, 0.5F, RNGSUS.nextFloat() * 0.1F + 0.9F, getCoords());
			}
		}
	}
	
	@Override
	public boolean onTickCheck(long aTimer) {
		return mUsingPlayers != oUsingPlayers || super.onTickCheck(aTimer);
	}
	@Override
	public void onTickResetChecks(long aTimer, boolean aIsServerSide) {
		super.onTickResetChecks(aTimer, aIsServerSide);
		oUsingPlayers = mUsingPlayers;
	}
	
	@Override
	public long onToolClick(String aTool, long aRemainingDurability, long aQuality, Entity aPlayer, List<String> aChatReturn, IInventory aPlayerInventory, boolean aSneaking, ItemStack aStack, byte aSide, float aHitX, float aHitY, float aHitZ) {
		if (isClientSide()) return 0;
		if (aTool.equals(TOOL_wrench)) {byte aTargetSide = UT.Code.getSideWrenching(aSide, aHitX, aHitY, aHitZ); if (aTargetSide > 1) {mFacing = aTargetSide; updateClientData(); causeBlockUpdate(); return 10000;}}
		return 0;
	}
	
	@Override
	public boolean onBlockActivated2(EntityPlayer aPlayer, byte aSide, float aHitX, float aHitY, float aHitZ) {
		if (isServerSide() && !worldObj.isSideSolid(xCoord, yCoord + 1, zCoord, FORGE_DIR[SIDE_BOTTOM]) && isUseableByPlayerGUI(aPlayer)) {
			generateDungeonLoot();
			openGUI(aPlayer);
		}
		return T;
	}
	// No longer generate Loot when harvested, instead pick up the Chest including the Loot it contains!
	//@Override
	//public boolean breakBlock() {
	//  // Only auto-generate Loot if a second has passed since its original placement. Prevents Item spillage during Worldgen in most cases.
	//  if (mTimer > 20) generateDungeonLoot();
	//  return super.breakBlock();
	//}
	
	@Override public boolean canDrop(int aInventorySlot) {return T;}
	@Override public String getTileEntityName() {return "gt.multitileentity.chest";}
	@Override public void openInventoryGUI () {mUsingPlayers++;}
	@Override public void closeInventoryGUI() {mUsingPlayers--;}
	@Override public float getExplosionResistance2() {return mResistance;}
	@Override public float getBlockHardness() {return mHardness;}
	@Override public int getComparatorInputOverride(byte aSide) {return Container.calcRedstoneFromInventory(this);}
	@Override public ITexture getTexture(Block aBlock, int aRenderPass, byte aSide, boolean[] aShouldSideBeRendered) {return null;}
	@Override public int getRenderPasses(Block aBlock, boolean[] aShouldSideBeRendered) {return 0;}
	@Override public boolean renderBlock(Block aBlock, RenderBlocks aRenderer, IBlockAccess aWorld, int aX, int aY, int aZ) {return T;}
	
	protected void generateDungeonLoot() {
		if (isServerSide() && UT.Code.stringValid(mDungeonLootName)) try {
			WeightedRandomChestContent.generateChestContents(RNGSUS, ChestGenHooks.getItems(mDungeonLootName, RNGSUS), this, ChestGenHooks.getCount(mDungeonLootName, RNGSUS));
			mDungeonLootName = "";
		} catch(Throwable e) {e.printStackTrace(ERR);}
	}
	
	@Override
	public boolean getSubItems(MultiTileEntityBlockInternal aBlock, Item aItem, CreativeTabs aTab, List<ItemStack> aList, short aID) {
		if (!SHOW_HIDDEN_MATERIALS && mMaterial.mHidden) return F;
		if (D1 || "lootchest".equalsIgnoreCase(mTextureName)) for (String tLoot : new String[] {"mineshaftCorridor", "pyramidDesertyChest", "pyramidJungleChest", "pyramidJungleDispenser", "strongholdCorridor", "strongholdLibrary", "strongholdCrossing", "villageBlacksmith", "bonusChest", "dungeonChest"}) aList.add(aBlock.mMultiTileEntityRegistry.getItem(aID, UT.NBT.makeString("gt.dungeonloot", tLoot)));
		return T;
	}
	
	@Override
	public void addToolTips(List<String> aList, ItemStack aStack, boolean aF3_H) {
		if (UT.Code.stringValid(mDungeonLootName)) aList.add(LH.Chat.BLINKING_CYAN + "Contains Loot of " + LH.Chat.WHITE + LH.get("loot." + mDungeonLootName));
	}
	
	@Override public boolean receiveDataByte(byte aData, INetworkHandler aNetworkHandler) {mUsingPlayers = aData; return T;}
	
	@Override
	public boolean receiveDataByteArray(byte[] aData, INetworkHandler aNetworkHandler) {
		mFacing = (byte)(aData[0] & 7);
		mUsingPlayers = aData[1];
		if (UT.Code.unsignB(aData[2]) != getSizeInventory()) setInventory(new ItemStack[UT.Code.unsignB(aData[2])]);
		setRGBData(aData[3], aData[4], aData[5], aData[6]);
		return T;
	}

	// 用于在重写接受数据代码时调用简单的设置颜色
	protected final void setRGBData(byte aR, byte aG, byte aB, byte aPaintData) {
		setPaintData(aPaintData);
		int oRGB = UT.Code.getRGBInt(new short[] {UT.Code.unsignB(aR), UT.Code.unsignB(aG), UT.Code.unsignB(aB)});
		if (oRGB != mRGBaPaint) {
			mRGBaPaint = oRGB;
			onPaintChangeClient(oRGB); // 仅客户端，用于在染色改变时客户端更改对应的显示颜色
		}
	}
	/* 仅客户端，用于在染色改变时客户端更改对应的显示颜色 */
	public void onPaintChangeClient(int aPreviousRGBaPaint) {
		mRGBa = isPainted() ? UT_CH.Code.getPaintRGB(getBottomRGB(), mRGBaPaint) : getOriginalRGB();
	}
	// GTCH, 原本逻辑过于麻烦，直接把是否已经染色也同步到客户端好了，这样还可以多出来一些数据用于专门处理颜色动画，可能可以方便后续的温度变色之类的开发（因为温度并没有传到客户端）
	public byte getPaintData() {return (byte) (mIsPainted?1:0);}
	public void setPaintData(byte aData) {mIsPainted = ((aData & 1) != 0);}
	// GTCH, 返回染色中用于叠底的颜色，用于给有外套层的机器重写，也用于客户端判断是否有染色
	@Override public int getBottomRGB() {return UT.Code.getRGBInt(mMaterial.fRGBaSolid);}
	// GTCH, 返回机器原本的颜色，用于客户端判断是否有染色，由于原本的默认 RGB 都是材料颜色，所以不允许重写
	@Override public final int getOriginalRGB() {return UT.Code.getRGBInt(mMaterial.fRGBaSolid);}

	@Override public boolean unpaint() {if (mIsPainted) {mIsPainted=F; mRGBaPaint=getBottomRGB(); updateClientData(); return T;} return F;}
	// GTCH, 原本逻辑过于麻烦，直接把是否已经染色也同步到客户端好了，这样还可以多出来一些数据用于专门处理颜色动画，可能可以方便后续的温度变色之类的开发（因为温度并没有传到客户端，不过实际用时需要优化把这个放一份到 NoSendAll里）
	@Override public boolean isPainted() {return mIsPainted;}
	@Override public boolean paint(int aRGB) {if (aRGB!=mRGBaPaint) {mRGBaPaint=aRGB; mIsPainted=T; return T;} return F;}
	@Override public int getPaint() {return mRGBaPaint;}
	@Override public boolean canRecolorItem(ItemStack aStack) {return T;}
	@Override public boolean canDecolorItem(ItemStack aStack) {return mIsPainted;}
	@Override public boolean recolorItem(ItemStack aStack, int aRGB) {if (paint((isPainted() ? UT_CH.Code.mixRGBInt(getPaint(), aRGB) : aRGB) & ALL_NON_ALPHA_COLOR)) {UT.NBT.set(aStack, writeItemNBT(aStack.hasTagCompound() ? aStack.getTagCompound() : UT.NBT.make())); return T;} return F;}
	@Override public boolean decolorItem(ItemStack aStack) {if (unpaint()) {UT.NBT.set(aStack, writeItemNBT(aStack.hasTagCompound() ? aStack.getTagCompound() : UT.NBT.make())); return T;} return F;}


	private static final float minX = 0.0625F, minY = 0F, minZ = 0.0625F, maxX = 0.9375F, maxY = 0.875F, maxZ = 0.9375F;
	@Override public AxisAlignedBB getCollisionBoundingBoxFromPool() {return box(minX, minY, minZ, maxX, maxY, maxZ);}
	@Override public AxisAlignedBB getSelectedBoundingBoxFromPool () {return box(minX, minY, minZ, maxX, maxY, maxZ);}
	@Override public void setBlockBoundsBasedOnState(Block aBlock) {box(aBlock, minX, minY, minZ, maxX, maxY, maxZ);}
	@Override public boolean setBlockBounds(Block aBlock, int aRenderPass, boolean[] aShouldSideBeRendered) {box(aBlock, minX, minY, minZ, maxX, maxY, maxZ); return true;}
	@Override public float getSurfaceSize           (byte aSide) {return 0.875F;}
	@Override public float getSurfaceSizeAttachable (byte aSide) {return 0.875F;}
	@Override public float getSurfaceDistance       (byte aSide) {return aSide > 1 ? 0.0625F : aSide == 1 ? 0.125F : 0;}
	@Override public boolean isSurfaceSolid         (byte aSide) {return F;}
	@Override public boolean isSurfaceOpaque        (byte aSide) {return F;}
	
	@Override public Object getGUIClient(int aGUIID, EntityPlayer aPlayer) {return new ContainerClientChest(aPlayer.inventory, this, aGUIID);}
	@Override public Object getGUIServer(int aGUIID, EntityPlayer aPlayer) {return new ContainerCommonChest(aPlayer.inventory, this, aGUIID);}
	
	@Override
	public boolean renderItem(Block aBlock, RenderBlocks aRenderer) {
		TileEntityRendererDispatcher.instance.renderTileEntityAt(this, 0, 0, 0, 0);
		return T;
	}
	
	@SideOnly(Side.CLIENT)
	private static MultiTileEntityRendererChest RENDERER;
	
	@Override
	@SideOnly(Side.CLIENT)
	public void onRegistrationFirstClient(MultiTileEntityRegistry aRegistry, short aID) {
		ClientRegistry.bindTileEntitySpecialRenderer(getClass(), RENDERER = new MultiTileEntityRendererChest());
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void onRegistrationClient(MultiTileEntityRegistry aRegistry, short aID) {
		RENDERER.mResources.put(mTextureName, new ResourceLocation[] {new ResourceLocation(MD.GT.mID, TEX_DIR_MODEL + aRegistry.mNameInternal + "/" + mTextureName + ".colored.png"), new ResourceLocation(MD.GT.mID, TEX_DIR_MODEL + aRegistry.mNameInternal + "/" + mTextureName + ".plain.png")});
	}

	@Override public int getLightOpacity() {return LIGHT_OPACITY_NONE;}

	@SideOnly(Side.CLIENT)
	public static class MultiTileEntityRendererChest extends TileEntitySpecialRenderer {
		private static final MultiTileEntityModelChest sModel = new MultiTileEntityModelChest();
		public final Map<String, ResourceLocation[]> mResources = new HashMap<>();
		
		@Override
		public void renderTileEntityAt(TileEntity aTileEntity, double aX, double aY, double aZ, float aPartialTick) {
			if (aTileEntity instanceof MultiTileEntityChest) {
				double tLidAngle = 1 - (((MultiTileEntityChest)aTileEntity).oLidAngle + (((MultiTileEntityChest)aTileEntity).mLidAngle - ((MultiTileEntityChest)aTileEntity).oLidAngle) * aPartialTick); tLidAngle = -(((1 - tLidAngle*tLidAngle*tLidAngle) * Math.PI) / 2);
				ResourceLocation[] tLocation = mResources.get(((MultiTileEntityChest)aTileEntity).mTextureName);
				bindTexture(tLocation[0]);
				glPushMatrix();
				glEnable(GL_BLEND);
				glEnable(GL_LIGHTING);
				glEnable(GL_ALPHA_TEST);
				glEnable(GL_RESCALE_NORMAL);
				glAlphaFunc(GL_GREATER, 0.1F);
				OpenGlHelper.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, 1, 0);
				short[] tRGBa = UT.Code.getRGBaArray(((MultiTileEntityChest)aTileEntity).mRGBa);
				glColor4f(tRGBa[0] / 255.0F, tRGBa[1] / 255.0F, tRGBa[2] / 255.0F, 1);
				glTranslated(aX, aY + 1, aZ + 1);
				glScalef(1, -1, -1);
				glTranslated(0.5, 0.5, 0.5);
				glRotatef(COMPASS_FROM_SIDE[((MultiTileEntityChest)aTileEntity).mFacing] * 90 - 180, 0, 1, 0);
				glTranslated(-0.5, -0.5, -0.5);
				sModel.render(tLidAngle);
				glDisable(GL_RESCALE_NORMAL);
				glPopMatrix();
				glEnable(GL_RESCALE_NORMAL);
				glColor4f(1, 1, 1, 1);
				
				bindTexture(tLocation[1]);
				glPushMatrix();
				if (((MultiTileEntityChest)aTileEntity).isPainted()) {
					tRGBa = UT.Code.getRGBaArray(UT_CH.Code.getOverlayRGB(((MultiTileEntityChest)aTileEntity).getPaint()));
					glColor4f(tRGBa[0] / 255.0F, tRGBa[1] / 255.0F, tRGBa[2] / 255.0F, 1);
				}
				glTranslated(aX, aY + 1, aZ + 1);
				glScalef(1, -1, -1);
				glTranslated(0.5, 0.5, 0.5);
				glRotatef(COMPASS_FROM_SIDE[((MultiTileEntityChest)aTileEntity).mFacing] * 90 - 180, 0, 1, 0);
				glTranslated(-0.5, -0.5, -0.5);
				sModel.render(tLidAngle);
				glDisable(GL_RESCALE_NORMAL);
				glPopMatrix();
				glEnable(GL_RESCALE_NORMAL);
				if (((MultiTileEntityChest)aTileEntity).isPainted()) glColor4f(1, 1, 1, 1);
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static class MultiTileEntityModelChest extends ModelBase {
		private final ModelRenderer mLid, mBottom, mKnob;
		
		public MultiTileEntityModelChest() {
			mLid = (new ModelRenderer(this, 0, 0)).setTextureSize(64, 64);
			mLid.addBox(0, -5, -14, 14, 5, 14, 0);
			mLid.rotationPointX =  1;
			mLid.rotationPointY =  7;
			mLid.rotationPointZ = 15;
			mKnob = (new ModelRenderer(this, 0, 0)).setTextureSize(64, 64);
			mKnob.addBox(-1, -2, -15, 2, 4, 1, 0);
			mKnob.rotationPointX =  8;
			mKnob.rotationPointY =  7;
			mKnob.rotationPointZ = 15;
			mBottom = (new ModelRenderer(this, 0, 19)).setTextureSize(64, 64);
			mBottom.addBox(0, 0, 0, 14, 10, 14, 0);
			mBottom.rotationPointX = 1;
			mBottom.rotationPointY = 6;
			mBottom.rotationPointZ = 1;
		}
		
		public void render(double aLidAngle) {
			mKnob.rotateAngleX = mLid.rotateAngleX = (float)aLidAngle;
			mLid.render(0.0625F);
			mKnob.render(0.0625F);
			mBottom.render(0.0625F);
		}
	}
}
