package gregtechCH.tileentity.connectors;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregapi.block.multitileentity.IMultiTileEntity;
import gregapi.code.ArrayListNoNulls;
import gregapi.code.TagData;
import gregapi.data.LH;
import gregapi.data.MT;
import gregapi.data.OP;
import gregapi.data.TD;
import gregapi.old.Textures;
import gregapi.oredict.OreDictMaterial;
import gregapi.render.BlockTextureDefault;
import gregapi.render.BlockTextureMulti;
import gregapi.render.ITexture;
import gregapi.tileentity.ITileEntityQuickObstructionCheck;
import gregapi.tileentity.connectors.TileEntityBase10ConnectorRendered;
import gregapi.tileentity.data.ITileEntityProgress;
import gregapi.tileentity.delegate.DelegatorTileEntity;
import gregapi.tileentity.energy.EnergyCompat;
import gregapi.tileentity.energy.ITileEntityEnergy;
import gregapi.tileentity.energy.ITileEntityEnergyDataConductor;
import gregapi.util.UT;
import gregtechCH.tileentity.cores.electric.IMTEC_HasElectricCore;
import gregtechCH.tileentity.cores.electric.MTEC_ElectricWireBase;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static gregapi.data.CS.*;

/**
 * WIP
 * 符合欧姆定律的新电线
 */
public class MultiTileEntityWireElectric_CH extends TileEntityBase10ConnectorRendered implements IMTEC_HasElectricCore, ITileEntityQuickObstructionCheck, ITileEntityEnergy, ITileEntityEnergyDataConductor, ITileEntityProgress, IMultiTileEntity.IMTE_GetDebugInfo, IMultiTileEntity.IMTE_GetCollisionBoundingBoxFromPool, IMultiTileEntity.IMTE_OnEntityCollidedWithBlock {
    public long mLoss = 1, mVoltage = 0, mAmperage = 0;
    public byte mRenderType = 0;
    private final MTEC_ElectricWireBase mCore = new MTEC_ElectricWireBase(this);
    @Override public MTEC_ElectricWireBase core() {return mCore;}
    
    @Override
    public void readFromNBT2(NBTTagCompound aNBT) {
        if (aNBT.hasKey(NBT_PIPERENDER)) mRenderType = aNBT.getByte(NBT_PIPERENDER);
        super.readFromNBT2(aNBT);
        if (aNBT.hasKey(NBT_PIPELOSS)) mLoss = Math.max(1, aNBT.getLong(NBT_PIPELOSS));
        if (aNBT.hasKey(NBT_PIPESIZE)) mVoltage = Math.max(1, aNBT.getLong(NBT_PIPESIZE));
        if (aNBT.hasKey(NBT_PIPEBANDWIDTH)) mAmperage = Math.max(1, aNBT.getLong(NBT_PIPEBANDWIDTH));
    }
    
    @Override
    public void writeToNBT2(NBTTagCompound aNBT) {
        super.writeToNBT2(aNBT);
    }
    
    @Override
    public void addToolTips(List<String> aList, ItemStack aStack, boolean aF3_H) {
        aList.add(LH.Chat.CYAN     + LH.get(LH.WIRE_STATS_VOLTAGE) + mVoltage + " " + TD.Energy.EU.getLocalisedNameShort() + " (" + VN[UT.Code.tierMin(mVoltage)] + ")");
        aList.add(LH.Chat.CYAN     + LH.get(LH.WIRE_STATS_AMPERAGE) + mAmperage);
        aList.add(LH.Chat.CYAN     + LH.get(LH.WIRE_STATS_LOSS) + mLoss + " " + TD.Energy.EU.getLocalisedNameShort() + "/m");
        if (mContactDamage) aList.add(LH.Chat.DRED     + LH.get(LH.HAZARD_CONTACT));
        super.addToolTips(aList, aStack, aF3_H);
    }
    
    @Override public void onConnectionChange(byte aPreviousConnections) {super.onConnectionChange(aPreviousConnections); mCore.updateManager();}
    @Override public void onAdjacentBlockChange2(int aTileX, int aTileY, int aTileZ) {super.onAdjacentBlockChange2(aTileX, aTileY, aTileZ); mCore.updateManager();}
    
    @Override
    public void onTick2(long aTimer, boolean aIsServerSide) {
        super.onTick2(aTimer, aIsServerSide);
        mCore.onTick(aTimer, aIsServerSide);
        // TODO 兼容输入
    }
    
    
    @Override public boolean canConnect(byte aSide, DelegatorTileEntity<TileEntity> aDelegator) {return EnergyCompat.canConnectElectricity(this, aDelegator.mTileEntity, aDelegator.mSideOfTileEntity);}
    
    @Override public void onEntityCollidedWithBlock(Entity aEntity) {if (mContactDamage && !isFoamDried()) UT.Entities.applyElectricityDamage(aEntity, mCore.mVoltage*mCore.mAmperage);}
    
    @Override public boolean isEnergyType(TagData aEnergyType, byte aSide, boolean aEmitting) {return aEnergyType == TD.Energy.EU;}
    @Override public Collection<TagData> getEnergyTypes(byte aSide) {return TD.Energy.EU.AS_LIST;}
    
    @Override public boolean isEnergyEmittingTo   (TagData aEnergyType, byte aSide, boolean aTheoretical) {return isEnergyType(aEnergyType, aSide, T) && canEmitEnergyTo    (aSide);}
    @Override public boolean isEnergyAcceptingFrom(TagData aEnergyType, byte aSide, boolean aTheoretical) {return isEnergyType(aEnergyType, aSide, F) && canAcceptEnergyFrom(aSide);}
    @Override public synchronized long doEnergyExtraction(TagData aEnergyType, byte aSide, long aSize, long aAmount, boolean aDoExtract) {return 0;}
    @Override public synchronized long doEnergyInjection (TagData aEnergyType, byte aSide, long aSize, long aAmount, boolean aDoInject ) {return mCore.doEnergyInjection(aEnergyType, aSide, aSize, aAmount, aDoInject);}
    @Override public long getEnergySizeOutputRecommended(TagData aEnergyType, byte aSide) {return mVoltage;}
    @Override public long getEnergySizeOutputMin(TagData aEnergyType, byte aSide) {return 0;}
    @Override public long getEnergySizeOutputMax(TagData aEnergyType, byte aSide) {return mVoltage;}
    @Override public long getEnergySizeInputRecommended(TagData aEnergyType, byte aSide) {return mVoltage;}
    @Override public long getEnergySizeInputMin(TagData aEnergyType, byte aSide) {return 0;}
    @Override public long getEnergySizeInputMax(TagData aEnergyType, byte aSide) {return mVoltage;}
    
    @Override public boolean canDrop(int aInventorySlot) {return F;}
    @Override public boolean isObstructingBlockAt2(byte aSide) {return F;} // Btw, Wires have this but Pipes don't. This is because Wires are flexible, while Pipes aren't.
    
    @Override public boolean isEnergyConducting(TagData aEnergyType) {return aEnergyType == TD.Energy.EU;}
    @Override public long getEnergyMaxSize(TagData aEnergyType) {return aEnergyType == TD.Energy.EU ? mVoltage : 0;}
    @Override public long getEnergyMaxPackets(TagData aEnergyType) {return aEnergyType == TD.Energy.EU ? mAmperage : 0;}
    @Override public long getEnergyLossPerMeter(TagData aEnergyType) {return aEnergyType == TD.Energy.EU ? mLoss : 0;}
    @Override public OreDictMaterial getEnergyConductorMaterial() {return mMaterial;}
    @Override public OreDictMaterial getEnergyConductorInsulation() {return isInsulated() ? MT.Rubber : MT.NULL;}
    
    public boolean canEmitEnergyTo                          (byte aSide) {return connected(aSide);}
    public boolean canAcceptEnergyFrom                      (byte aSide) {return connected(aSide);}
    
    @Override public long getProgressValue                  (byte aSide) {return mCore.mAmperage;}
    @Override public long getProgressMax                    (byte aSide) {return mAmperage;}
    
    @Override public ArrayList<String> getDebugInfo(int aScanLevel) {return aScanLevel > 0 ? new ArrayListNoNulls<>(F, "Transferred Power: " + mCore.mVoltage*mCore.mAmperage) : null;}
    
    // 绝缘线缆不会改变半径
    @Override public float getConnectorDiameter(byte aConnectorSide, DelegatorTileEntity<TileEntity> aDelegator) {
        // 绝缘线缆连接非绝缘线缆时不会收缩
        if (isInsulated() && aDelegator.mTileEntity instanceof MultiTileEntityWireElectric_CH && !((MultiTileEntityWireElectric_CH)aDelegator.mTileEntity).isInsulated()) return mDiameter;
        return super.getConnectorDiameter(aConnectorSide, aDelegator);
    }
    // GTCH, 用于减少重复代码
    private boolean isInsulated() { return mRenderType == 1 || mRenderType == 2; }
    
    // GTCH, 返回绝缘层的颜色为原本颜色
    @Override public int getBottomRGB() {return isInsulated() ? UT.Code.getRGBInt(64, 64, 64) : super.getBottomRGB();}
    // GTCH, 绝缘时返回绝缘层颜色
    @SideOnly(Side.CLIENT) @Override protected int colorMultiplier2() {
        if (isInsulated()) return isPainted()?mRGBa: getBottomRGB();
        return super.colorMultiplier2();
    }
    
    @Override public ITexture getTextureSide                (byte aSide, byte aConnections, float aDiameter, int aRenderPass) {return isInsulated() ? BlockTextureDefault.get(Textures.BlockIcons.INSULATION_FULL, isPainted()?mRGBa:getBottomRGB()) : BlockTextureDefault.get(mMaterial, getIconIndexSide(aSide, aConnections, aDiameter, aRenderPass), F, mRGBa);}
    @Override public ITexture getTextureConnected           (byte aSide, byte aConnections, float aDiameter, int aRenderPass) {return isInsulated() ? BlockTextureMulti.get(BlockTextureDefault.get(mMaterial, getIconIndexConnected(aSide, aConnections, aDiameter, aRenderPass), mIsGlowing), BlockTextureDefault.get(mRenderType==2?Textures.BlockIcons.INSULATION_BUNDLED:aDiameter<0.37F?Textures.BlockIcons.INSULATION_TINY:aDiameter<0.49F?Textures.BlockIcons.INSULATION_SMALL:aDiameter<0.74F?Textures.BlockIcons.INSULATION_MEDIUM:aDiameter<0.99F?Textures.BlockIcons.INSULATION_LARGE:Textures.BlockIcons.INSULATION_HUGE, isPainted()?mRGBa: getBottomRGB())) : BlockTextureDefault.get(mMaterial, getIconIndexConnected(aSide, aConnections, aDiameter, aRenderPass), mIsGlowing, mRGBa);}
    
    @Override public int getIconIndexSide                   (byte aSide, byte aConnections, float aDiameter, int aRenderPass) {return OP.wire.mIconIndexBlock;}
    @Override public int getIconIndexConnected              (byte aSide, byte aConnections, float aDiameter, int aRenderPass) {return OP.wire.mIconIndexBlock;}
    
    @Override public Collection<TagData> getConnectorTypes  (byte aSide) {return TD.Connectors.WIRE_ELECTRIC.AS_LIST;}
    
    @Override public String getFacingTool                   () {return TOOL_cutter;}
    
    @Override public String getTileEntityName               () {return "gt.multitileentity.connector.wire.electric.wip";}
}
