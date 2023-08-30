package gregtechCH.loaders.b;

import gregapi.block.multitileentity.MultiTileEntityBlock;
import gregapi.block.multitileentity.MultiTileEntityRegistry;
import gregapi.data.*;
import gregapi.oredict.OreDictMaterial;
import gregapi.tileentity.multiblocks.MultiTileEntityMultiBlockPart;
import gregapi.util.CR;
import gregapi.util.OM;
import gregapi.util.ST;
import gregapi.util.UT;
import gregtech.loaders.b.Loader_MultiTileEntities;
import gregtech.tileentity.energy.converters.MultiTileEntityDynamoElectric;
import gregtech.tileentity.energy.generators.*;
import gregtech.tileentity.multiblocks.*;
import gregtech.tileentity.tools.*;
import gregtechCH.config.data.DataMultiTileEntity;
import gregtechCH.data.RM_CH;
import gregtechCH.tileentity.batteries.eu.MultiTileEntityBatteryAdvEU8192;
import gregtechCH.tileentity.batteries.eu.MultiTileEntityBatteryEU8192;
import gregtechCH.tileentity.energy.MultiTileEntityMotorGas;
import gregtechCH.tileentity.miner.MultiTileEntityDepositDrill;
import gregtechCH.tileentity.misc.MultiTileEntityBedrockDeposit;
import gregtechCH.tileentity.misc.MultiTileEntityDeposit;
import gregtechCH.tileentity.multiblocks.*;
import gregtechCH.tileentity.sensors.*;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.tileentity.TileEntity;

import static gregapi.data.CS.*;
import static gregtechCH.config.ConfigForge.DATA_GTCH;
import static gregtechCH.config.ConfigJson.DATA_MULTITILEENTITY;
import static gregtechCH.data.CS_CH.*;


/**
 * @author CHanzy
 * Extension of Loader_MultiTileEntities
 * EMPTY IDs: 23000 - 24999; 15000 - 16999; 9500 - 9999
 * ID for GTCH: 23300 - 23499
 * 由于后续 greg 可能还有比较激进的添加，因此不按照 mod 来分划 id 使用区域
 * TODO 想方法完善 id 改变时能够找到正确的新 id 的方法
 */
@SuppressWarnings({"PointlessArithmeticExpression", "ConstantConditions"})
public class Loader_MultiTileEntities_CH extends Loader_MultiTileEntities  {
    
    private static MultiTileEntityBlock getBlock(String aBlockName, MultiTileEntityBlock aMetal, MultiTileEntityBlock aMetalChips, MultiTileEntityBlock aMetalWires, MultiTileEntityBlock aMachine, MultiTileEntityBlock aWooden, MultiTileEntityBlock aBush, MultiTileEntityBlock aStone, MultiTileEntityBlock aWool, MultiTileEntityBlock aTNT, MultiTileEntityBlock aHive, MultiTileEntityBlock aUtilMetal, MultiTileEntityBlock aUtilStone, MultiTileEntityBlock aUtilWood, MultiTileEntityBlock aUtilWool) {
        if (aBlockName == null) return null;
        switch (aBlockName) {
        case "metal":           return aMetal;
        case "metalChips":      return aMetalChips;
        case "metalWires":      return aMetalWires;
        case "machine":         return aMachine;
        case "wooden":          return aWooden;
        case "bush":            return aBush;
        case "stone":           return aStone;
        case "wool":            return aWool;
        case "TNT": case "tnt": return aTNT;
        case "utilMetal":       return aUtilMetal;
        case "utilStone":       return aUtilStone;
        case "utilWood":        return aUtilWood;
        case "utilWool":        return aUtilWool;
        case "hive":            return aHive;
        }
        return null;
    }
    
    // 用户自定义修改部分
    @Override protected void userConfigLoad(MultiTileEntityRegistry aRegistry, MultiTileEntityBlock aMetal, MultiTileEntityBlock aMetalChips, MultiTileEntityBlock aMetalWires, MultiTileEntityBlock aMachine, MultiTileEntityBlock aWooden, MultiTileEntityBlock aBush, MultiTileEntityBlock aStone, MultiTileEntityBlock aWool, MultiTileEntityBlock aTNT, MultiTileEntityBlock aHive, MultiTileEntityBlock aUtilMetal, MultiTileEntityBlock aUtilStone, MultiTileEntityBlock aUtilWood, MultiTileEntityBlock aUtilWool, OreDictMaterial aMat, Class<? extends TileEntity> aClass) {
        /// 修改项
        for (DataMultiTileEntity.ReplaceObject tReplace : DATA_MULTITILEENTITY.replace) {
            aRegistry.addReplacer(tReplace.ID).localised(tReplace.localised).categoricalName(tReplace.categoricalName).creativeTabID(tReplace.creativeTabID).te(tReplace.clazz()).toolQuality(tReplace.blockMetaData).stackSize(tReplace.stackSize)
                    .block(getBlock(tReplace.block, aMetal, aMetalChips, aMetalWires, aMachine, aWooden, aBush, aStone, aWool, aTNT, aHive, aUtilMetal, aUtilStone, aUtilWood, aUtilWool))
                    .setParametersMergeLast(tReplace.parametersMerge).removeParametersRemoveLast(tReplace.parametersRemove).recipe(tReplace.recipe());
        }
        /// 删除项
        for (int tID : DATA_MULTITILEENTITY.remove) {
            aRegistry.removeAdds(tID);
        }
        /// 添加项
        for (DataMultiTileEntity.AppendBeforeObject tAppend : DATA_MULTITILEENTITY.appendBefore) {
            MultiTileEntityBlock tBlock = getBlock(tAppend.block, aMetal, aMetalChips, aMetalWires, aMachine, aWooden, aBush, aStone, aWool, aTNT, aHive, aUtilMetal, aUtilStone, aUtilWood, aUtilWool);
            aRegistry.appendAddBefore(tAppend.beforeID, tAppend.localised, tAppend.categoricalName, tAppend.ID, tAppend.creativeTabID, tAppend.clazz(), tAppend.blockMetaData, tAppend.stackSize, tBlock, tAppend.parameters, tAppend.recipe());
        }
        for (DataMultiTileEntity.AppendAfterObject tAppend : DATA_MULTITILEENTITY.appendAfter) {
            MultiTileEntityBlock tBlock = getBlock(tAppend.block, aMetal, aMetalChips, aMetalWires, aMachine, aWooden, aBush, aStone, aWool, aTNT, aHive, aUtilMetal, aUtilStone, aUtilWood, aUtilWool);
            aRegistry.appendAddAfter(tAppend.afterID, tAppend.localised, tAppend.categoricalName, tAppend.ID, tAppend.creativeTabID, tAppend.clazz(), tAppend.blockMetaData, tAppend.stackSize, tBlock, tAppend.parameters, tAppend.recipe());
        }
    }
    
    
    @Override protected void unsorted1BeforeLoad(MultiTileEntityRegistry aRegistry, MultiTileEntityBlock aMetal, MultiTileEntityBlock aMetalChips, MultiTileEntityBlock aMetalWires, MultiTileEntityBlock aMachine, MultiTileEntityBlock aWooden, MultiTileEntityBlock aBush, MultiTileEntityBlock aStone, MultiTileEntityBlock aWool, MultiTileEntityBlock aTNT, MultiTileEntityBlock aHive, MultiTileEntityBlock aUtilMetal, MultiTileEntityBlock aUtilStone, MultiTileEntityBlock aUtilWood, MultiTileEntityBlock aUtilWool, OreDictMaterial aMat, Class<? extends TileEntity> aClass) {
        /// 修改项
        // Burning Boxes 输出翻倍，部分效率调整
        aRegistry.addReplacer(1199).setParameters(NBT_OUTPUT,    16, NBT_EFFICIENCY,  2500); // Solid brick 不做修改
        aRegistry.addReplacer(1100).setParameters(NBT_OUTPUT,  16*2, NBT_EFFICIENCY,  5000); // Solid Lead
        aRegistry.addReplacer(1101).setParameters(NBT_OUTPUT,  20*2, NBT_EFFICIENCY,  4500); // Solid Bismuth
        aRegistry.addReplacer(1102).setParameters(NBT_OUTPUT,  24*2, NBT_EFFICIENCY,  7500); // Solid Bronze
        aRegistry.addReplacer(1111).setParameters(NBT_OUTPUT,  24*2, NBT_EFFICIENCY,  8000); // Solid ArsenicCopper
        aRegistry.addReplacer(1112).setParameters(NBT_OUTPUT,  28*2, NBT_EFFICIENCY,  8500); // Solid ArsenicBronze
        aRegistry.addReplacer(1103).setParameters(NBT_OUTPUT,  16*2, NBT_EFFICIENCY, 10000); // Solid Invar
        aRegistry.addReplacer(1104).setParameters(NBT_OUTPUT,  32*2, NBT_EFFICIENCY,  6500); // Solid AnyIronSteel
        aRegistry.addReplacer(1105).setParameters(NBT_OUTPUT, 112*2, NBT_EFFICIENCY,  8000); // Solid Chromium
        aRegistry.addReplacer(1106).setParameters(NBT_OUTPUT,  96*2, NBT_EFFICIENCY,  8000); // Solid Titanium
        aRegistry.addReplacer(1110).setParameters(NBT_OUTPUT,  96*2, NBT_EFFICIENCY,  9000); // Solid Netherite
        aRegistry.addReplacer(1107).setParameters(NBT_OUTPUT, 128*2, NBT_EFFICIENCY, 10000); // Solid AnyTungsten
        aRegistry.addReplacer(1108).setParameters(NBT_OUTPUT, 128*2, NBT_EFFICIENCY,  9000); // Solid Tungstensteel
        aRegistry.addReplacer(1109).setParameters(NBT_OUTPUT, 256*2, NBT_EFFICIENCY,  9500); // Solid TantalumHafniumCarbide
        
        aRegistry.addReplacer(1150).setParameters(NBT_OUTPUT,  16*8, NBT_EFFICIENCY,  5000); // Solid Dense Lead
        aRegistry.addReplacer(1151).setParameters(NBT_OUTPUT,  20*8, NBT_EFFICIENCY,  4500); // Solid Dense Bismuth
        aRegistry.addReplacer(1152).setParameters(NBT_OUTPUT,  24*8, NBT_EFFICIENCY,  7500); // Solid Dense Bronze
        aRegistry.addReplacer(1161).setParameters(NBT_OUTPUT,  24*8, NBT_EFFICIENCY,  8000); // Solid Dense ArsenicCopper
        aRegistry.addReplacer(1162).setParameters(NBT_OUTPUT,  28*8, NBT_EFFICIENCY,  8500); // Solid Dense ArsenicBronze
        aRegistry.addReplacer(1153).setParameters(NBT_OUTPUT,  16*8, NBT_EFFICIENCY, 10000); // Solid Dense Invar
        aRegistry.addReplacer(1154).setParameters(NBT_OUTPUT,  32*8, NBT_EFFICIENCY,  6500); // Solid Dense AnyIronSteel
        aRegistry.addReplacer(1155).setParameters(NBT_OUTPUT, 112*8, NBT_EFFICIENCY,  8000); // Solid Dense Chromium
        aRegistry.addReplacer(1156).setParameters(NBT_OUTPUT,  96*8, NBT_EFFICIENCY,  8000); // Solid Dense Titanium
        aRegistry.addReplacer(1160).setParameters(NBT_OUTPUT,  96*8, NBT_EFFICIENCY,  9000); // Solid Dense Netherite
        aRegistry.addReplacer(1157).setParameters(NBT_OUTPUT, 128*8, NBT_EFFICIENCY, 10000); // Solid Dense AnyTungsten
        aRegistry.addReplacer(1158).setParameters(NBT_OUTPUT, 128*8, NBT_EFFICIENCY,  9000); // Solid Dense Tungstensteel
        aRegistry.addReplacer(1159).setParameters(NBT_OUTPUT, 256*8, NBT_EFFICIENCY,  9500); // Solid Dense TantalumHafniumCarbide
        
        aRegistry.addReplacer(1402).setParameters(NBT_OUTPUT,  24*2, NBT_EFFICIENCY,  7500); // Liquid Bronze
        aRegistry.addReplacer(1411).setParameters(NBT_OUTPUT,  24*2, NBT_EFFICIENCY,  8000).recipe("PCP", "IwI", "BBB", 'B', Blocks.brick_block, 'P', OP.plate.dat(MT.ArsenicCopper), 'I', OP.pipeSmall.dat(MT.Bronze), 'C', OP.plateDouble.dat(ANY.Cu)); // Liquid ArsenicCopper
        aRegistry.addReplacer(1412).setParameters(NBT_OUTPUT,  28*2, NBT_EFFICIENCY,  8500).recipe("PCP", "IwI", "BBB", 'B', Blocks.brick_block, 'P', OP.plate.dat(MT.ArsenicBronze), 'I', OP.pipeSmall.dat(MT.Bronze), 'C', OP.plateDouble.dat(ANY.Cu)); // Liquid ArsenicBronze
        aRegistry.addReplacer(1403).setParameters(NBT_OUTPUT,  16*2, NBT_EFFICIENCY, 10000); // Liquid Invar
        aRegistry.addReplacer(1404).setParameters(NBT_OUTPUT,  32*2, NBT_EFFICIENCY,  6500); // Liquid AnyIronSteel
        aRegistry.addReplacer(1405).setParameters(NBT_OUTPUT, 112*2, NBT_EFFICIENCY,  8000); // Liquid Chromium
        aRegistry.addReplacer(1406).setParameters(NBT_OUTPUT,  96*2, NBT_EFFICIENCY,  8000); // Liquid Titanium
        aRegistry.addReplacer(1410).setParameters(NBT_OUTPUT,  96*2, NBT_EFFICIENCY,  9000); // Liquid Netherite
        aRegistry.addReplacer(1407).setParameters(NBT_OUTPUT, 128*2, NBT_EFFICIENCY, 10000); // Liquid AnyTungsten
        aRegistry.addReplacer(1408).setParameters(NBT_OUTPUT, 128*2, NBT_EFFICIENCY,  9000); // Liquid Tungstensteel
        aRegistry.addReplacer(1409).setParameters(NBT_OUTPUT, 256*2, NBT_EFFICIENCY,  9500); // Liquid TantalumHafniumCarbide
        
        aRegistry.addReplacer(1452).setParameters(NBT_OUTPUT,  24*8, NBT_EFFICIENCY,  7500); // Liquid Dense Bronze
        aRegistry.addReplacer(1461).setParameters(NBT_OUTPUT,  24*8, NBT_EFFICIENCY,  8000).recipe("PCP", "IwI", "BBB", 'B', Blocks.brick_block, 'P', OP.plateQuintuple.dat(MT.ArsenicCopper), 'I', OP.pipeLarge.dat(MT.Bronze), 'C', OP.plateDense.dat(ANY.Cu)); // Liquid Dense ArsenicCopper
        aRegistry.addReplacer(1462).setParameters(NBT_OUTPUT,  28*8, NBT_EFFICIENCY,  8500).recipe("PCP", "IwI", "BBB", 'B', Blocks.brick_block, 'P', OP.plateQuintuple.dat(MT.ArsenicBronze), 'I', OP.pipeLarge.dat(MT.Bronze), 'C', OP.plateDense.dat(ANY.Cu)); // Liquid Dense ArsenicBronze
        aRegistry.addReplacer(1453).setParameters(NBT_OUTPUT,  16*8, NBT_EFFICIENCY, 10000); // Liquid Dense Invar
        aRegistry.addReplacer(1454).setParameters(NBT_OUTPUT,  32*8, NBT_EFFICIENCY,  6500); // Liquid Dense AnyIronSteel
        aRegistry.addReplacer(1455).setParameters(NBT_OUTPUT, 112*8, NBT_EFFICIENCY,  8000); // Liquid Dense Chromium
        aRegistry.addReplacer(1456).setParameters(NBT_OUTPUT,  96*8, NBT_EFFICIENCY,  8000); // Liquid Dense Titanium
        aRegistry.addReplacer(1460).setParameters(NBT_OUTPUT,  96*8, NBT_EFFICIENCY,  9000); // Liquid Dense Netherite
        aRegistry.addReplacer(1457).setParameters(NBT_OUTPUT, 128*8, NBT_EFFICIENCY, 10000); // Liquid Dense AnyTungsten
        aRegistry.addReplacer(1458).setParameters(NBT_OUTPUT, 128*8, NBT_EFFICIENCY,  9000); // Liquid Dense Tungstensteel
        aRegistry.addReplacer(1459).setParameters(NBT_OUTPUT, 256*8, NBT_EFFICIENCY,  9500); // Liquid Dense TantalumHafniumCarbide
        
        aRegistry.addReplacer(1602).setParameters(NBT_OUTPUT,  24*2, NBT_EFFICIENCY,  7500); // Gas Bronze
        aRegistry.addReplacer(1611).setParameters(NBT_OUTPUT,  24*2, NBT_EFFICIENCY,  8000).recipe("PCP", "BwB", "BIB", 'B', Blocks.brick_block, 'P', OP.plate.dat(MT.ArsenicCopper), 'I', OP.pipeSmall.dat(MT.Bronze), 'C', OP.plateDouble.dat(ANY.Cu)); // Gas ArsenicCopper
        aRegistry.addReplacer(1612).setParameters(NBT_OUTPUT,  28*2, NBT_EFFICIENCY,  8500).recipe("PCP", "BwB", "BIB", 'B', Blocks.brick_block, 'P', OP.plate.dat(MT.ArsenicBronze), 'I', OP.pipeSmall.dat(MT.Bronze), 'C', OP.plateDouble.dat(ANY.Cu)); // Gas ArsenicBronze
        aRegistry.addReplacer(1603).setParameters(NBT_OUTPUT,  16*2, NBT_EFFICIENCY, 10000); // Gas Invar
        aRegistry.addReplacer(1604).setParameters(NBT_OUTPUT,  32*2, NBT_EFFICIENCY,  6500); // Gas AnyIronSteel
        aRegistry.addReplacer(1605).setParameters(NBT_OUTPUT, 112*2, NBT_EFFICIENCY,  8000); // Gas Chromium
        aRegistry.addReplacer(1606).setParameters(NBT_OUTPUT,  96*2, NBT_EFFICIENCY,  8000); // Gas Titanium
        aRegistry.addReplacer(1610).setParameters(NBT_OUTPUT,  96*2, NBT_EFFICIENCY,  9000); // Gas Netherite
        aRegistry.addReplacer(1607).setParameters(NBT_OUTPUT, 128*2, NBT_EFFICIENCY, 10000); // Gas AnyTungsten
        aRegistry.addReplacer(1608).setParameters(NBT_OUTPUT, 128*2, NBT_EFFICIENCY,  9000); // Gas Tungstensteel
        aRegistry.addReplacer(1609).setParameters(NBT_OUTPUT, 256*2, NBT_EFFICIENCY,  9500); // Gas TantalumHafniumCarbide
        
        aRegistry.addReplacer(1652).setParameters(NBT_OUTPUT,  24*8, NBT_EFFICIENCY,  7500); // Gas Dense Bronze
        aRegistry.addReplacer(1661).setParameters(NBT_OUTPUT,  24*8, NBT_EFFICIENCY,  8000).recipe("PCP", "BwB", "BIB", 'B', Blocks.brick_block, 'P', OP.plateQuintuple.dat(MT.ArsenicCopper), 'I', OP.pipeLarge.dat(MT.Bronze), 'C', OP.plateDense.dat(ANY.Cu)); // Gas Dense ArsenicCopper
        aRegistry.addReplacer(1662).setParameters(NBT_OUTPUT,  28*8, NBT_EFFICIENCY,  8500).recipe("PCP", "BwB", "BIB", 'B', Blocks.brick_block, 'P', OP.plateQuintuple.dat(MT.ArsenicBronze), 'I', OP.pipeLarge.dat(MT.Bronze), 'C', OP.plateDense.dat(ANY.Cu)); // Gas Dense ArsenicBronze
        aRegistry.addReplacer(1653).setParameters(NBT_OUTPUT,  16*8, NBT_EFFICIENCY, 10000); // Gas Dense Invar
        aRegistry.addReplacer(1654).setParameters(NBT_OUTPUT,  32*8, NBT_EFFICIENCY,  6500); // Gas Dense AnyIronSteel
        aRegistry.addReplacer(1655).setParameters(NBT_OUTPUT, 112*8, NBT_EFFICIENCY,  8000); // Gas Dense Chromium
        aRegistry.addReplacer(1656).setParameters(NBT_OUTPUT,  96*8, NBT_EFFICIENCY,  8000); // Gas Dense Titanium
        aRegistry.addReplacer(1660).setParameters(NBT_OUTPUT,  96*8, NBT_EFFICIENCY,  9000); // Gas Dense Netherite
        aRegistry.addReplacer(1657).setParameters(NBT_OUTPUT, 128*8, NBT_EFFICIENCY, 10000); // Gas Dense AnyTungsten
        aRegistry.addReplacer(1658).setParameters(NBT_OUTPUT, 128*8, NBT_EFFICIENCY,  9000); // Gas Dense Tungstensteel
        aRegistry.addReplacer(1659).setParameters(NBT_OUTPUT, 256*8, NBT_EFFICIENCY,  9500); // Gas Dense TantalumHafniumCarbide
        
        aRegistry.addReplacer(9000).setParameters(NBT_OUTPUT,  16*4, NBT_EFFICIENCY,  5000); // FluidizedBed Lead
        aRegistry.addReplacer(9001).setParameters(NBT_OUTPUT,  20*4, NBT_EFFICIENCY,  4500); // FluidizedBed Bismuth
        aRegistry.addReplacer(9002).setParameters(NBT_OUTPUT,  24*4, NBT_EFFICIENCY,  7500); // FluidizedBed Bronze
        aRegistry.addReplacer(9011).setParameters(NBT_OUTPUT,  24*4, NBT_EFFICIENCY,  8000); // FluidizedBed ArsenicCopper
        aRegistry.addReplacer(9012).setParameters(NBT_OUTPUT,  28*4, NBT_EFFICIENCY,  8500); // FluidizedBed ArsenicBronze
        aRegistry.addReplacer(9003).setParameters(NBT_OUTPUT,  16*4, NBT_EFFICIENCY, 10000); // FluidizedBed Invar
        aRegistry.addReplacer(9004).setParameters(NBT_OUTPUT,  32*4, NBT_EFFICIENCY,  6500); // FluidizedBed AnyIronSteel
        aRegistry.addReplacer(9005).setParameters(NBT_OUTPUT, 112*4, NBT_EFFICIENCY,  8000); // FluidizedBed Chromium
        aRegistry.addReplacer(9006).setParameters(NBT_OUTPUT,  96*4, NBT_EFFICIENCY,  8000); // FluidizedBed Titanium
        aRegistry.addReplacer(9010).setParameters(NBT_OUTPUT,  96*4, NBT_EFFICIENCY,  9000); // FluidizedBed Netherite
        aRegistry.addReplacer(9007).setParameters(NBT_OUTPUT, 128*4, NBT_EFFICIENCY, 10000); // FluidizedBed AnyTungsten
        aRegistry.addReplacer(9008).setParameters(NBT_OUTPUT, 128*4, NBT_EFFICIENCY,  9000); // FluidizedBed Tungstensteel
        aRegistry.addReplacer(9009).setParameters(NBT_OUTPUT, 256*4, NBT_EFFICIENCY,  9500); // FluidizedBed TantalumHafniumCarbide
        
        aRegistry.addReplacer(9050).setParameters(NBT_OUTPUT, 16*16, NBT_EFFICIENCY,  5000); // FluidizedBed Dense Lead
        aRegistry.addReplacer(9051).setParameters(NBT_OUTPUT, 20*16, NBT_EFFICIENCY,  4500); // FluidizedBed Dense Bismuth
        aRegistry.addReplacer(9052).setParameters(NBT_OUTPUT, 24*16, NBT_EFFICIENCY,  7500); // FluidizedBed Dense Bronze
        aRegistry.addReplacer(9061).setParameters(NBT_OUTPUT, 24*16, NBT_EFFICIENCY,  8000); // FluidizedBed Dense ArsenicCopper
        aRegistry.addReplacer(9062).setParameters(NBT_OUTPUT, 28*16, NBT_EFFICIENCY,  8500); // FluidizedBed Dense ArsenicBronze
        aRegistry.addReplacer(9053).setParameters(NBT_OUTPUT, 16*16, NBT_EFFICIENCY, 10000); // FluidizedBed Dense Invar
        aRegistry.addReplacer(9054).setParameters(NBT_OUTPUT, 32*16, NBT_EFFICIENCY,  6500); // FluidizedBed Dense AnyIronSteel
        aRegistry.addReplacer(9055).setParameters(NBT_OUTPUT,112*16, NBT_EFFICIENCY,  8000); // FluidizedBed Dense Chromium
        aRegistry.addReplacer(9056).setParameters(NBT_OUTPUT, 96*16, NBT_EFFICIENCY,  8000); // FluidizedBed Dense Titanium
        aRegistry.addReplacer(9060).setParameters(NBT_OUTPUT, 96*16, NBT_EFFICIENCY,  9000); // FluidizedBed Dense Netherite
        aRegistry.addReplacer(9057).setParameters(NBT_OUTPUT,128*16, NBT_EFFICIENCY, 10000); // FluidizedBed Dense AnyTungsten
        aRegistry.addReplacer(9058).setParameters(NBT_OUTPUT,128*16, NBT_EFFICIENCY,  9000); // FluidizedBed Dense Tungstensteel
        aRegistry.addReplacer(9059).setParameters(NBT_OUTPUT,256*16, NBT_EFFICIENCY,  9500); // FluidizedBed Dense TantalumHafniumCarbide
        
        // Heat Exchangers 输出翻倍
        aRegistry.addReplacer(9103).setParameters(NBT_OUTPUT,  16*2); // Invar
        aRegistry.addReplacer(9107).setParameters(NBT_OUTPUT, 128*2); // AnyTungsten
        aRegistry.addReplacer(9108).setParameters(NBT_OUTPUT, 128*2); // Tungstensteel
        aRegistry.addReplacer(9109).setParameters(NBT_OUTPUT, 256*2); // TantalumHafniumCarbide
        
        aRegistry.addReplacer(9153).setParameters(NBT_OUTPUT,  16*8); // Dense Invar
        aRegistry.addReplacer(9157).setParameters(NBT_OUTPUT, 128*8); // Dense AnyTungsten
        aRegistry.addReplacer(9158).setParameters(NBT_OUTPUT, 128*8); // Dense Tungstensteel
        aRegistry.addReplacer(9159).setParameters(NBT_OUTPUT, 256*8); // Dense TantalumHafniumCarbide
        
        // Diesel Engines 效率降低，合成变贵，增加预热相关
        aMat = MT.Bronze;           aRegistry.addReplacer(9147).setParameters(NBT_OUTPUT,  16, NBT_EFFICIENCY, 2750, NBT_PREHEAT_ENERGY,  16*1000, NBT_PREHEAT_RATE ,  16*4, NBT_PREHEAT_COST,  16/16, NBT_COOLDOWN_RATE,  16).recipe("PLP", "SMS", "GOC", 'M', OP.casingMachineDense.dat(aMat), 'O', OP.pipeQuadruple.dat(aMat)     , 'P', OP.rotor.dat(aMat), 'S', OP.stickLong.dat(aMat), 'G', OP.gearGt.dat(aMat), 'C', OP.gearGtSmall.dat(aMat), 'L', OD.itemLubricant);
        aMat = MT.ArsenicCopper;    aRegistry.addReplacer(9146).setParameters(NBT_OUTPUT,  16, NBT_EFFICIENCY, 2875, NBT_PREHEAT_ENERGY,  16*1000, NBT_PREHEAT_RATE ,  16*4, NBT_PREHEAT_COST,  16/16, NBT_COOLDOWN_RATE,  16).recipe("PLP", "SMS", "GOC", 'M', OP.casingMachineDense.dat(aMat), 'O', OP.pipeQuadruple.dat(MT.Bronze), 'P', OP.rotor.dat(aMat), 'S', OP.stickLong.dat(aMat), 'G', OP.gearGt.dat(aMat), 'C', OP.gearGtSmall.dat(aMat), 'L', OD.itemLubricant);
        aMat = MT.ArsenicBronze;    aRegistry.addReplacer(9145).setParameters(NBT_OUTPUT,  24, NBT_EFFICIENCY, 3000, NBT_PREHEAT_ENERGY,  24*1000, NBT_PREHEAT_RATE ,  24*4, NBT_PREHEAT_COST,  24/16, NBT_COOLDOWN_RATE,  16).recipe("PLP", "SMS", "GOC", 'M', OP.casingMachineDense.dat(aMat), 'O', OP.pipeQuadruple.dat(MT.Bronze), 'P', OP.rotor.dat(aMat), 'S', OP.stickLong.dat(aMat), 'G', OP.gearGt.dat(aMat), 'C', OP.gearGtSmall.dat(aMat), 'L', OD.itemLubricant);
        aMat = ANY.Steel;           aRegistry.addReplacer(9148).setParameters(NBT_OUTPUT,  32, NBT_EFFICIENCY, 2500, NBT_PREHEAT_ENERGY,  32*1000, NBT_PREHEAT_RATE ,  32*4, NBT_PREHEAT_COST,  32/16, NBT_COOLDOWN_RATE,  32).recipe("PLP", "SMS", "GOC", 'M', OP.casingMachineDense.dat(aMat), 'O', OP.pipeQuadruple.dat(aMat)     , 'P', OP.rotor.dat(aMat), 'S', OP.stickLong.dat(aMat), 'G', OP.gearGt.dat(aMat), 'C', OP.gearGtSmall.dat(aMat), 'L', OD.itemLubricant);
        aMat = MT.Invar;            aRegistry.addReplacer(9149).setParameters(NBT_OUTPUT,  64, NBT_EFFICIENCY, 3500, NBT_PREHEAT_ENERGY,  64*1000, NBT_PREHEAT_RATE ,  64*4, NBT_PREHEAT_COST,  64/16, NBT_COOLDOWN_RATE,  64).recipe("PLP", "SMS", "GOC", 'M', OP.casingMachineDense.dat(aMat), 'O', OP.pipeQuadruple.dat(aMat)     , 'P', OP.rotor.dat(aMat), 'S', OP.stickLong.dat(aMat), 'G', OP.gearGt.dat(aMat), 'C', OP.gearGtSmall.dat(aMat), 'L', OD.itemLubricant);
        aMat = MT.Ti;               aRegistry.addReplacer(9197).setParameters(NBT_OUTPUT, 128, NBT_EFFICIENCY, 2750, NBT_PREHEAT_ENERGY, 128*1000, NBT_PREHEAT_RATE , 128*4, NBT_PREHEAT_COST, 128/16, NBT_COOLDOWN_RATE, 128).recipe("PLP", "SMS", "GOC", 'M', OP.casingMachineDense.dat(aMat), 'O', OP.pipeQuadruple.dat(aMat)     , 'P', OP.rotor.dat(aMat), 'S', OP.stickLong.dat(aMat), 'G', OP.gearGt.dat(aMat), 'C', OP.gearGtSmall.dat(aMat), 'L', OD.itemLubricant);
        aMat = MT.TungstenSteel;    aRegistry.addReplacer(9198).setParameters(NBT_OUTPUT, 256, NBT_EFFICIENCY, 3000, NBT_PREHEAT_ENERGY, 256*1000, NBT_PREHEAT_RATE , 256*4, NBT_PREHEAT_COST, 256/16, NBT_COOLDOWN_RATE, 256).recipe("PLP", "SMS", "GOC", 'M', OP.casingMachineDense.dat(aMat), 'O', OP.pipeQuadruple.dat(aMat)     , 'P', OP.rotor.dat(aMat), 'S', OP.stickLong.dat(aMat), 'G', OP.gearGt.dat(aMat), 'C', OP.gearGtSmall.dat(aMat), 'L', OD.itemLubricant);
        aMat = MT.Ir;               aRegistry.addReplacer(9199).setParameters(NBT_OUTPUT, 512, NBT_EFFICIENCY, 3333, NBT_PREHEAT_ENERGY, 512*1000, NBT_PREHEAT_RATE , 512*4, NBT_PREHEAT_COST, 512/16, NBT_COOLDOWN_RATE, 512).recipe("PLP", "SMS", "GOC", 'M', OP.casingMachineDense.dat(aMat), 'O', OP.pipeQuadruple.dat(aMat)     , 'P', OP.rotor.dat(aMat), 'S', OP.stickLong.dat(aMat), 'G', OP.gearGt.dat(aMat), 'C', OP.gearGtSmall.dat(aMat), 'L', OD.itemLubricant);
        
        // Steam Boilers 增加效率参数，输入调整
        aRegistry.addReplacer(1200).setParameters(NBT_INPUT,  16*2, NBT_EFFICIENCY_CH, 4500, NBT_CAPACITY,  16*1000, NBT_CAPACITY_SU,  16*10000).removeParameters(NBT_OUTPUT_SU); // Lead
        aRegistry.addReplacer(1201).setParameters(NBT_INPUT,  20*2, NBT_EFFICIENCY_CH, 4333, NBT_CAPACITY,  20*1000, NBT_CAPACITY_SU,  20*10000).removeParameters(NBT_OUTPUT_SU); // Bismuth
        aRegistry.addReplacer(1202).setParameters(NBT_INPUT,  24*2, NBT_EFFICIENCY_CH, 5000, NBT_CAPACITY,  24*1000, NBT_CAPACITY_SU,  24*10000).removeParameters(NBT_OUTPUT_SU); // Bronze
        aRegistry.addReplacer(1210).setParameters(NBT_INPUT,  24*2, NBT_EFFICIENCY_CH, 5125, NBT_CAPACITY,  24*1000, NBT_CAPACITY_SU,  24*10000).removeParameters(NBT_OUTPUT_SU); // ArsenicCopper
        aRegistry.addReplacer(1211).setParameters(NBT_INPUT,  28*2, NBT_EFFICIENCY_CH, 5250, NBT_CAPACITY,  28*1000, NBT_CAPACITY_SU,  28*10000).removeParameters(NBT_OUTPUT_SU); // ArsenicBronze
        aRegistry.addReplacer(1203).setParameters(NBT_INPUT,  16*2, NBT_EFFICIENCY_CH, 5666, NBT_CAPACITY,  16*1000, NBT_CAPACITY_SU,  16*10000).removeParameters(NBT_OUTPUT_SU); // Invar
        aRegistry.addReplacer(1204).setParameters(NBT_INPUT,  32*2, NBT_EFFICIENCY_CH, 4750, NBT_CAPACITY,  32*1000, NBT_CAPACITY_SU,  32*10000).removeParameters(NBT_OUTPUT_SU); // AnyIronSteel
        aRegistry.addReplacer(1205).setParameters(NBT_INPUT,  96*2, NBT_EFFICIENCY_CH, 5000, NBT_CAPACITY,  96*1000, NBT_CAPACITY_SU,  96*10000).removeParameters(NBT_OUTPUT_SU); // Chromium
        aRegistry.addReplacer(1206).setParameters(NBT_INPUT, 112*2, NBT_EFFICIENCY_CH, 5000, NBT_CAPACITY, 112*1000, NBT_CAPACITY_SU, 112*10000).removeParameters(NBT_OUTPUT_SU); // Titanium
        aRegistry.addReplacer(1209).setParameters(NBT_INPUT, 112*2, NBT_EFFICIENCY_CH, 5250, NBT_CAPACITY, 112*1000, NBT_CAPACITY_SU, 112*10000).removeParameters(NBT_OUTPUT_SU); // Netherite
        aRegistry.addReplacer(1207).setParameters(NBT_INPUT, 128*2, NBT_EFFICIENCY_CH, 5500, NBT_CAPACITY, 128*1000, NBT_CAPACITY_SU, 128*10000).removeParameters(NBT_OUTPUT_SU); // AnyTungsten
        aRegistry.addReplacer(1208).setParameters(NBT_INPUT, 128*2, NBT_EFFICIENCY_CH, 5250, NBT_CAPACITY, 128*1000, NBT_CAPACITY_SU, 128*10000).removeParameters(NBT_OUTPUT_SU); // Tungstensteel
        
        aRegistry.addReplacer(1250).setParameters(NBT_INPUT,  16*8, NBT_EFFICIENCY_CH, 5500, NBT_CAPACITY,  16*4000, NBT_CAPACITY_SU,  16*40000).removeParameters(NBT_OUTPUT_SU); // Strong Lead
        aRegistry.addReplacer(1251).setParameters(NBT_INPUT,  20*8, NBT_EFFICIENCY_CH, 5333, NBT_CAPACITY,  20*4000, NBT_CAPACITY_SU,  20*40000).removeParameters(NBT_OUTPUT_SU); // Strong Bismuth
        aRegistry.addReplacer(1252).setParameters(NBT_INPUT,  24*8, NBT_EFFICIENCY_CH, 6000, NBT_CAPACITY,  24*4000, NBT_CAPACITY_SU,  24*40000).removeParameters(NBT_OUTPUT_SU); // Strong Bronze
        aRegistry.addReplacer(1260).setParameters(NBT_INPUT,  24*8, NBT_EFFICIENCY_CH, 6125, NBT_CAPACITY,  24*4000, NBT_CAPACITY_SU,  24*40000).removeParameters(NBT_OUTPUT_SU); // Strong ArsenicCopper
        aRegistry.addReplacer(1261).setParameters(NBT_INPUT,  28*8, NBT_EFFICIENCY_CH, 6250, NBT_CAPACITY,  28*4000, NBT_CAPACITY_SU,  28*40000).removeParameters(NBT_OUTPUT_SU); // Strong ArsenicBronze
        aRegistry.addReplacer(1253).setParameters(NBT_INPUT,  16*8, NBT_EFFICIENCY_CH, 6666, NBT_CAPACITY,  16*4000, NBT_CAPACITY_SU,  16*40000).removeParameters(NBT_OUTPUT_SU); // Strong Invar
        aRegistry.addReplacer(1254).setParameters(NBT_INPUT,  32*8, NBT_EFFICIENCY_CH, 5750, NBT_CAPACITY,  32*4000, NBT_CAPACITY_SU,  32*40000).removeParameters(NBT_OUTPUT_SU); // Strong AnyIronSteel
        aRegistry.addReplacer(1255).setParameters(NBT_INPUT,  96*8, NBT_EFFICIENCY_CH, 6000, NBT_CAPACITY,  96*4000, NBT_CAPACITY_SU,  96*40000).removeParameters(NBT_OUTPUT_SU); // Strong Chromium
        aRegistry.addReplacer(1256).setParameters(NBT_INPUT, 112*8, NBT_EFFICIENCY_CH, 6000, NBT_CAPACITY, 112*4000, NBT_CAPACITY_SU, 112*40000).removeParameters(NBT_OUTPUT_SU); // Strong Titanium
        aRegistry.addReplacer(1259).setParameters(NBT_INPUT, 112*8, NBT_EFFICIENCY_CH, 6250, NBT_CAPACITY, 112*4000, NBT_CAPACITY_SU, 112*40000).removeParameters(NBT_OUTPUT_SU); // Strong Netherite
        aRegistry.addReplacer(1257).setParameters(NBT_INPUT, 128*8, NBT_EFFICIENCY_CH, 6500, NBT_CAPACITY, 128*4000, NBT_CAPACITY_SU, 128*40000).removeParameters(NBT_OUTPUT_SU); // Strong AnyTungsten
        aRegistry.addReplacer(1258).setParameters(NBT_INPUT, 128*8, NBT_EFFICIENCY_CH, 6250, NBT_CAPACITY, 128*4000, NBT_CAPACITY_SU, 128*40000).removeParameters(NBT_OUTPUT_SU); // Strong Tungstensteel
        
        // Steam Engines 效率调整，其他保留
        aRegistry.addReplacer(1300).setParameters(NBT_OUTPUT,    16/STEAM_PER_EU, NBT_EFFICIENCY, 2500, NBT_CAPACITY,  16*1000, NBT_EFFICIENCY_WATER, 8000); // Lead
        aRegistry.addReplacer(1301).setParameters(NBT_OUTPUT,    20/STEAM_PER_EU, NBT_EFFICIENCY, 3000, NBT_CAPACITY,  20*1000, NBT_EFFICIENCY_WATER, 8000); // TinAlloy
        aRegistry.addReplacer(1302).setParameters(NBT_OUTPUT,    24/STEAM_PER_EU, NBT_EFFICIENCY, 3333, NBT_CAPACITY,  24*1000, NBT_EFFICIENCY_WATER, 8000); // Bronze
        aRegistry.addReplacer(1312).setParameters(NBT_OUTPUT,    24/STEAM_PER_EU, NBT_EFFICIENCY, 3500, NBT_CAPACITY,  24*1000, NBT_EFFICIENCY_WATER, 8000); // ArsenicCopper
        aRegistry.addReplacer(1313).setParameters(NBT_OUTPUT,    28/STEAM_PER_EU, NBT_EFFICIENCY, 3666, NBT_CAPACITY,  28*1000, NBT_EFFICIENCY_WATER, 8000); // ArsenicBronze
        aRegistry.addReplacer(1309).setParameters(NBT_OUTPUT,    24/STEAM_PER_EU, NBT_EFFICIENCY, 3333, NBT_CAPACITY,  24*1000, NBT_EFFICIENCY_WATER, 8000); // Brass
        aRegistry.addReplacer(1303).setParameters(NBT_OUTPUT,    16/STEAM_PER_EU, NBT_EFFICIENCY, 4500, NBT_CAPACITY,  16*1000, NBT_EFFICIENCY_WATER, 8000); // Invar
        aRegistry.addReplacer(1310).setParameters(NBT_OUTPUT,    16/STEAM_PER_EU, NBT_EFFICIENCY, 4666, NBT_CAPACITY,  16*1000, NBT_EFFICIENCY_WATER, 8000); // Ironwood
        aRegistry.addReplacer(1304).setParameters(NBT_OUTPUT,    32/STEAM_PER_EU, NBT_EFFICIENCY, 3000, NBT_CAPACITY,  32*1000, NBT_EFFICIENCY_WATER, 8000); // AnyIronSteel
        aRegistry.addReplacer(1311).setParameters(NBT_OUTPUT,    64/STEAM_PER_EU, NBT_EFFICIENCY, 4333, NBT_CAPACITY,  64*1000, NBT_EFFICIENCY_WATER, 8000); // FierySteel
        aRegistry.addReplacer(1305).setParameters(NBT_OUTPUT,    96/STEAM_PER_EU, NBT_EFFICIENCY, 3500, NBT_CAPACITY,  96*1000, NBT_EFFICIENCY_WATER, 8000); // Chromium
        aRegistry.addReplacer(1306).setParameters(NBT_OUTPUT,   112/STEAM_PER_EU, NBT_EFFICIENCY, 3500, NBT_CAPACITY, 112*1000, NBT_EFFICIENCY_WATER, 8000); // Titanium
        aRegistry.addReplacer(1307).setParameters(NBT_OUTPUT,   128/STEAM_PER_EU, NBT_EFFICIENCY, 4333, NBT_CAPACITY, 128*1000, NBT_EFFICIENCY_WATER, 8000); // AnyTungsten
        aRegistry.addReplacer(1308).setParameters(NBT_OUTPUT,   128/STEAM_PER_EU, NBT_EFFICIENCY, 3750, NBT_CAPACITY, 128*1000, NBT_EFFICIENCY_WATER, 8000); // Tungstensteel
        
        aRegistry.addReplacer(1350).setParameters(NBT_OUTPUT,  16*4/STEAM_PER_EU, NBT_EFFICIENCY, 2500, NBT_CAPACITY,  16*4000, NBT_EFFICIENCY_WATER, 8000); // Strong Lead
        aRegistry.addReplacer(1351).setParameters(NBT_OUTPUT,  20*4/STEAM_PER_EU, NBT_EFFICIENCY, 3000, NBT_CAPACITY,  20*4000, NBT_EFFICIENCY_WATER, 8000); // Strong TinAlloy
        aRegistry.addReplacer(1352).setParameters(NBT_OUTPUT,  24*4/STEAM_PER_EU, NBT_EFFICIENCY, 3333, NBT_CAPACITY,  24*4000, NBT_EFFICIENCY_WATER, 8000); // Strong Bronze
        aRegistry.addReplacer(1362).setParameters(NBT_OUTPUT,  24*4/STEAM_PER_EU, NBT_EFFICIENCY, 3500, NBT_CAPACITY,  24*4000, NBT_EFFICIENCY_WATER, 8000); // Strong ArsenicCopper
        aRegistry.addReplacer(1363).setParameters(NBT_OUTPUT,  28*4/STEAM_PER_EU, NBT_EFFICIENCY, 3666, NBT_CAPACITY,  28*4000, NBT_EFFICIENCY_WATER, 8000); // Strong ArsenicBronze
        aRegistry.addReplacer(1359).setParameters(NBT_OUTPUT,  24*4/STEAM_PER_EU, NBT_EFFICIENCY, 3333, NBT_CAPACITY,  24*4000, NBT_EFFICIENCY_WATER, 8000); // Strong Brass
        aRegistry.addReplacer(1353).setParameters(NBT_OUTPUT,  16*4/STEAM_PER_EU, NBT_EFFICIENCY, 4500, NBT_CAPACITY,  16*4000, NBT_EFFICIENCY_WATER, 8000); // Strong Invar
        aRegistry.addReplacer(1360).setParameters(NBT_OUTPUT,  16*4/STEAM_PER_EU, NBT_EFFICIENCY, 4666, NBT_CAPACITY,  16*4000, NBT_EFFICIENCY_WATER, 8000); // Strong Ironwood
        aRegistry.addReplacer(1354).setParameters(NBT_OUTPUT,  32*4/STEAM_PER_EU, NBT_EFFICIENCY, 3000, NBT_CAPACITY,  32*4000, NBT_EFFICIENCY_WATER, 8000); // Strong AnyIronSteel
        aRegistry.addReplacer(1361).setParameters(NBT_OUTPUT,  64*4/STEAM_PER_EU, NBT_EFFICIENCY, 4333, NBT_CAPACITY,  64*4000, NBT_EFFICIENCY_WATER, 8000); // Strong FierySteel
        aRegistry.addReplacer(1355).setParameters(NBT_OUTPUT,  96*4/STEAM_PER_EU, NBT_EFFICIENCY, 3500, NBT_CAPACITY,  96*4000, NBT_EFFICIENCY_WATER, 8000); // Strong Chromium
        aRegistry.addReplacer(1356).setParameters(NBT_OUTPUT, 112*4/STEAM_PER_EU, NBT_EFFICIENCY, 3500, NBT_CAPACITY, 112*4000, NBT_EFFICIENCY_WATER, 8000); // Strong Titanium
        aRegistry.addReplacer(1357).setParameters(NBT_OUTPUT, 128*4/STEAM_PER_EU, NBT_EFFICIENCY, 4333, NBT_CAPACITY, 128*4000, NBT_EFFICIENCY_WATER, 8000); // Strong AnyTungsten
        aRegistry.addReplacer(1358).setParameters(NBT_OUTPUT, 128*4/STEAM_PER_EU, NBT_EFFICIENCY, 3750, NBT_CAPACITY, 128*4000, NBT_EFFICIENCY_WATER, 8000); // Strong Tungstensteel
    
        // Steam Turbines 效率调整，预热相关
        aRegistry.addReplacer(1512).setParameters(NBT_OUTPUT,   16, NBT_EFFICIENCY, 3500, NBT_PREHEAT_ENERGY,   16*4000, NBT_PREHEAT_COST,   16/16, NBT_COOLDOWN_RATE,   16, NBT_EFFICIENCY_WATER, 8000, NBT_EFFICIENCY_OC, 5000).removeParameters(NBT_INPUT, NBT_WASTE_ENERGY); // Bronze BronzeRotor
        aRegistry.addReplacer(1515).setParameters(NBT_OUTPUT,   24, NBT_EFFICIENCY, 3500, NBT_PREHEAT_ENERGY,   24*4000, NBT_PREHEAT_COST,   24/16, NBT_COOLDOWN_RATE,   24, NBT_EFFICIENCY_WATER, 8000, NBT_EFFICIENCY_OC, 5000).removeParameters(NBT_INPUT, NBT_WASTE_ENERGY); // Bronze BrassRotor
        aRegistry.addReplacer(1518).setParameters(NBT_OUTPUT,   32, NBT_EFFICIENCY, 4000, NBT_PREHEAT_ENERGY,   32*4000, NBT_PREHEAT_COST,   32/16, NBT_COOLDOWN_RATE,   32, NBT_EFFICIENCY_WATER, 8000, NBT_EFFICIENCY_OC, 5000).removeParameters(NBT_INPUT, NBT_WASTE_ENERGY); // Bronze InvarRotor
        
        aRegistry.addReplacer(1522).setParameters(NBT_OUTPUT,   64, NBT_EFFICIENCY, 3000, NBT_PREHEAT_ENERGY,   64*4000, NBT_PREHEAT_COST,   64/16, NBT_COOLDOWN_RATE,   64, NBT_EFFICIENCY_WATER, 8000, NBT_EFFICIENCY_OC, 5000).removeParameters(NBT_INPUT, NBT_WASTE_ENERGY); // AnyIronSteel AnyIronSteelRotor
        aRegistry.addReplacer(1525).setParameters(NBT_OUTPUT,   96, NBT_EFFICIENCY, 4000, NBT_PREHEAT_ENERGY,   96*4000, NBT_PREHEAT_COST,   96/16, NBT_COOLDOWN_RATE,   96, NBT_EFFICIENCY_WATER, 8000, NBT_EFFICIENCY_OC, 5000).removeParameters(NBT_INPUT, NBT_WASTE_ENERGY); // AnyIronSteel ChromiumRotor
        aRegistry.addReplacer(1527).setParameters(NBT_OUTPUT,  128, NBT_EFFICIENCY, 4750, NBT_PREHEAT_ENERGY,  128*4000, NBT_PREHEAT_COST,  128/16, NBT_COOLDOWN_RATE,  128, NBT_EFFICIENCY_WATER, 8000, NBT_EFFICIENCY_OC, 5000).removeParameters(NBT_INPUT, NBT_WASTE_ENERGY); // AnyIronSteel IronwoodRotor
        aRegistry.addReplacer(1528).setParameters(NBT_OUTPUT,  128, NBT_EFFICIENCY, 4500, NBT_PREHEAT_ENERGY,  128*4000, NBT_PREHEAT_COST,  128/16, NBT_COOLDOWN_RATE,  128, NBT_EFFICIENCY_WATER, 8000, NBT_EFFICIENCY_OC, 5000).removeParameters(NBT_INPUT, NBT_WASTE_ENERGY); // AnyIronSteel SteeleafRotor
        aRegistry.addReplacer(1529).setParameters(NBT_OUTPUT,  128, NBT_EFFICIENCY, 4000, NBT_PREHEAT_ENERGY,  128*4000, NBT_PREHEAT_COST,  128/16, NBT_COOLDOWN_RATE,  128, NBT_EFFICIENCY_WATER, 8000, NBT_EFFICIENCY_OC, 5000).removeParameters(NBT_INPUT, NBT_WASTE_ENERGY); // AnyIronSteel ThaumiumRotor
        
        aRegistry.addReplacer(1530).setParameters(NBT_OUTPUT,  256, NBT_EFFICIENCY, 4500, NBT_PREHEAT_ENERGY,  256*4000, NBT_PREHEAT_COST,  256/16, NBT_COOLDOWN_RATE,  256, NBT_EFFICIENCY_WATER, 8000, NBT_EFFICIENCY_OC, 5000).removeParameters(NBT_INPUT, NBT_WASTE_ENERGY); // Titanium TitaniumRotor
        aRegistry.addReplacer(1531).setParameters(NBT_OUTPUT,  256, NBT_EFFICIENCY, 3500, NBT_PREHEAT_ENERGY,  256*4000, NBT_PREHEAT_COST,  256/16, NBT_COOLDOWN_RATE,  256, NBT_EFFICIENCY_WATER, 8000, NBT_EFFICIENCY_OC, 5000).removeParameters(NBT_INPUT, NBT_WASTE_ENERGY); // Titanium FierySteelRotor
        aRegistry.addReplacer(1535).setParameters(NBT_OUTPUT,  384, NBT_EFFICIENCY, 5000, NBT_PREHEAT_ENERGY,  384*4000, NBT_PREHEAT_COST,  384/16, NBT_COOLDOWN_RATE,  384, NBT_EFFICIENCY_WATER, 8000, NBT_EFFICIENCY_OC, 5000).removeParameters(NBT_INPUT, NBT_WASTE_ENERGY); // Titanium AluminiumRotor
        aRegistry.addReplacer(1538).setParameters(NBT_OUTPUT,  512, NBT_EFFICIENCY, 5250, NBT_PREHEAT_ENERGY,  512*4000, NBT_PREHEAT_COST,  512/16, NBT_COOLDOWN_RATE,  512, NBT_EFFICIENCY_WATER, 8000, NBT_EFFICIENCY_OC, 5000).removeParameters(NBT_INPUT, NBT_WASTE_ENERGY); // Titanium MagnaliumRotor
        
        aRegistry.addReplacer(1540).setParameters(NBT_OUTPUT,  768, NBT_EFFICIENCY, 5750, NBT_PREHEAT_ENERGY,  768*4000, NBT_PREHEAT_COST,  768/16, NBT_COOLDOWN_RATE,  768, NBT_EFFICIENCY_WATER, 8000, NBT_EFFICIENCY_OC, 5000).removeParameters(NBT_INPUT, NBT_WASTE_ENERGY); // Tungstensteel VoidMetalRotor
        aRegistry.addReplacer(1545).setParameters(NBT_OUTPUT, 1024, NBT_EFFICIENCY, 5250, NBT_PREHEAT_ENERGY, 1024*4000, NBT_PREHEAT_COST, 1024/16, NBT_COOLDOWN_RATE, 1024, NBT_EFFICIENCY_WATER, 8000, NBT_EFFICIENCY_OC, 5000).removeParameters(NBT_INPUT, NBT_WASTE_ENERGY); // Tungstensteel TrinitaniumRotor
        aRegistry.addReplacer(1548).setParameters(NBT_OUTPUT, 2048, NBT_EFFICIENCY, 5250, NBT_PREHEAT_ENERGY, 2048*4000, NBT_PREHEAT_COST, 2048/16, NBT_COOLDOWN_RATE, 2048, NBT_EFFICIENCY_WATER, 8000, NBT_EFFICIENCY_OC, 5000).removeParameters(NBT_INPUT, NBT_WASTE_ENERGY); // Tungstensteel GrapheneRotor
        
        
        /// 删除项
//        aRegistry.removeHolding(9220); // TEST REMOVE
//        aRegistry.removeHolding(9320); // TEST REMOVE
    }
    
    
    @Override protected void unsorted2BeforeLoad(MultiTileEntityRegistry aRegistry, MultiTileEntityBlock aMetal, MultiTileEntityBlock aMetalChips, MultiTileEntityBlock aMetalWires, MultiTileEntityBlock aMachine, MultiTileEntityBlock aWooden, MultiTileEntityBlock aBush, MultiTileEntityBlock aStone, MultiTileEntityBlock aWool, MultiTileEntityBlock aTNT, MultiTileEntityBlock aHive, MultiTileEntityBlock aUtilMetal, MultiTileEntityBlock aUtilStone, MultiTileEntityBlock aUtilWood, MultiTileEntityBlock aUtilWool, OreDictMaterial aMat, Class<? extends TileEntity> aClass) {
        /// 修改项
        // 修改 Power Cell (Hydrogen) 的 NBT_COLOR 改成 NBT_COLOR_BOTTOM
        aRegistry.addReplacer(14701).setParameters(NBT_COLOR_BOTTOM, UT.Code.getRGBInt(MT.H.fRGBaGas)).removeParameters(NBT_COLOR);
    
        // Dynamos 小发电机效率改为 60%
        aRegistry.addReplacer(10110).setParameters(NBT_OUTPUT,    8, NBT_EFFICIENCY_NUM, 6000).removeParameters(NBT_INPUT);
        aRegistry.addReplacer(10111).setParameters(NBT_OUTPUT,   32, NBT_EFFICIENCY_NUM, 6000).removeParameters(NBT_INPUT);
        aRegistry.addReplacer(10112).setParameters(NBT_OUTPUT,  128, NBT_EFFICIENCY_NUM, 6000).removeParameters(NBT_INPUT);
        aRegistry.addReplacer(10113).setParameters(NBT_OUTPUT,  512, NBT_EFFICIENCY_NUM, 6000).removeParameters(NBT_INPUT);
        aRegistry.addReplacer(10114).setParameters(NBT_OUTPUT, 2048, NBT_EFFICIENCY_NUM, 6000).removeParameters(NBT_INPUT);
        aRegistry.addReplacer(10115).setParameters(NBT_OUTPUT, 8192, NBT_EFFICIENCY_NUM, 6000).removeParameters(NBT_INPUT);
        
        /// 添加项
        // Dynamos
        aClass = MultiTileEntityDynamoElectric.class;
        aMat = MT.DATA.Electric_T[0];   aRegistry.appendAddBefore(10111, "Electric Dynamo ("              +VN[0]+")", "Dynamos"                             , 10110, 10111, aClass, aMat.mToolQuality, 16, aMachine     , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   4.0F, NBT_RESISTANCE,   4.0F, NBT_INPUT,   14, NBT_OUTPUT,    8, NBT_WASTE_ENERGY, T, NBT_ENERGY_ACCEPTED, TD.Energy.RU, NBT_ENERGY_EMITTED, TD.Energy.EU), "TGT", "CMC", "TId", 'M', OP.casingMachineDouble.dat(aMat), 'T', OP.screw.dat(aMat), 'G', OP.gearGt.dat(aMat), 'I', OP.stickLong.dat(MT.IronMagnetic     ), 'C', OP.wireGt01.dat(ANY.Cu));
    }
    
    
    @Override protected void multiblocksBeforeLoad(MultiTileEntityRegistry aRegistry, MultiTileEntityBlock aMetal, MultiTileEntityBlock aMetalChips, MultiTileEntityBlock aMetalWires, MultiTileEntityBlock aMachine, MultiTileEntityBlock aWooden, MultiTileEntityBlock aBush, MultiTileEntityBlock aStone, MultiTileEntityBlock aWool, MultiTileEntityBlock aTNT, MultiTileEntityBlock aHive, MultiTileEntityBlock aUtilMetal, MultiTileEntityBlock aUtilStone, MultiTileEntityBlock aUtilWood, MultiTileEntityBlock aUtilWool, OreDictMaterial aMat, Class<? extends TileEntity> aClass) {
        /// 修改项
        // 一般机器改为通过并行提高效率
        aRegistry.addReplacer(17100).removeParameters(NBT_CHEAP_OVERCLOCKING, NBT_PARALLEL_DURATION).setParameters(NBT_PARALLEL, 64);
        aRegistry.addReplacer(17103).removeParameters(NBT_CHEAP_OVERCLOCKING, NBT_PARALLEL_DURATION).setParameters(NBT_PARALLEL, 64);
        aRegistry.addReplacer(17102).removeParameters(NBT_CHEAP_OVERCLOCKING, NBT_PARALLEL_DURATION);
        aRegistry.addReplacer(17113).removeParameters(NBT_CHEAP_OVERCLOCKING, NBT_PARALLEL_DURATION);
        aRegistry.addReplacer(17106).removeParameters(NBT_CHEAP_OVERCLOCKING, NBT_PARALLEL_DURATION);
        aRegistry.addReplacer(17107).removeParameters(NBT_CHEAP_OVERCLOCKING, NBT_PARALLEL_DURATION);
        aRegistry.addReplacer(17108).removeParameters(NBT_CHEAP_OVERCLOCKING, NBT_PARALLEL_DURATION);
        aRegistry.addReplacer(17109).removeParameters(NBT_CHEAP_OVERCLOCKING, NBT_PARALLEL_DURATION);
        aRegistry.addReplacer(17114).removeParameters(NBT_CHEAP_OVERCLOCKING, NBT_PARALLEL_DURATION);
        
        // Boilers 增加效率参数，输入调整
        aRegistry.addReplacer(17201).setParameters(NBT_INPUT,   4096, NBT_EFFICIENCY_CH, 8000, NBT_CAPACITY,   4096*1000, NBT_CAPACITY_SU,    4096*40000).removeParameters(NBT_OUTPUT_SU); // StainlessSteel
        aRegistry.addReplacer(17205).setParameters(NBT_INPUT,   4096, NBT_EFFICIENCY_CH, 8000, NBT_CAPACITY,   4096*1000, NBT_CAPACITY_SU,    4096*40000).removeParameters(NBT_OUTPUT_SU); // Invar
        aRegistry.addReplacer(17202).setParameters(NBT_INPUT,   8192, NBT_EFFICIENCY_CH, 8000, NBT_CAPACITY,   8192*1000, NBT_CAPACITY_SU,    8192*40000).removeParameters(NBT_OUTPUT_SU); // Titanium
        aRegistry.addReplacer(17203).setParameters(NBT_INPUT,  16384, NBT_EFFICIENCY_CH, 8000, NBT_CAPACITY,  16384*1000, NBT_CAPACITY_SU,   16384*40000).removeParameters(NBT_OUTPUT_SU); // Tungstensteel
        aRegistry.addReplacer(17204).setParameters(NBT_INPUT, 131072, NBT_EFFICIENCY_CH, 8000, NBT_CAPACITY, 131072*1000, NBT_CAPACITY_SU,131072L*40000L).removeParameters(NBT_OUTPUT_SU); // Adamantium
        
        // Steam Turbines 各长度的参数，增加预热相关
        aRegistry.addReplacer(17211).setParameters(NBT_EFFICIENCY_WATER, 9500, NBT_EFFICIENCY_OC, 5000, NBT_LENGTH_MIN, 1, NBT_LENGTH_MAX, 10, NBT_LENGTH_MID, 4).removeParameters(NBT_OUTPUT, NBT_EFFICIENCY, NBT_INPUT, NBT_WASTE_ENERGY).setParameterArray(NBT_OUTPUT,  1365, 2560,  3431,  4096,  4618,  5041,  5389,  5681,  5930,  6144).setParameterArray(NBT_EFFICIENCY, 3250,5000,6000,6500,7000,7250,7333,7500,7666,7750).setParameterArray(NBT_PREHEAT_ENERGY,  24576000,  49152000,   73728000,   98304000,  122880000,  147456000,  172032000,  196608000,  221184000,  245760000).setParameterArray(NBT_PREHEAT_COST,  11, 22,  32,  43,  54,  64,  75,  86,  96, 107).setParameterArray(NBT_COOLDOWN_RATE,  1024, 2048, 3072,  4096,  5120,  6144,  7168,  8192,  9216, 10240); // StainlessSteel
        aRegistry.addReplacer(17212).setParameters(NBT_EFFICIENCY_WATER, 9500, NBT_EFFICIENCY_OC, 5000, NBT_LENGTH_MIN, 1, NBT_LENGTH_MAX, 10, NBT_LENGTH_MID, 4).removeParameters(NBT_OUTPUT, NBT_EFFICIENCY, NBT_INPUT, NBT_WASTE_ENERGY).setParameterArray(NBT_OUTPUT,  2730, 5120,  6863,  8192,  9237, 10082, 10778, 11363, 11860, 12288).setParameterArray(NBT_EFFICIENCY, 3250,5000,6000,6500,7000,7250,7333,7500,7666,7750).setParameterArray(NBT_PREHEAT_ENERGY,  49152000,  98304000,  147456000,  196608000,  245760000,  294912000,  344064000,  393216000,  442368000,  491520000).setParameterArray(NBT_PREHEAT_COST,  22, 43,  64,  86, 107, 128, 150, 171, 192, 214).setParameterArray(NBT_COOLDOWN_RATE,  2048, 4096, 6144,  8192, 10240, 12288, 14336, 16384, 18432, 20480); // Titanium
        aRegistry.addReplacer(17213).setParameters(NBT_EFFICIENCY_WATER, 9500, NBT_EFFICIENCY_OC, 5000, NBT_LENGTH_MIN, 1, NBT_LENGTH_MAX, 10, NBT_LENGTH_MID, 4).removeParameters(NBT_OUTPUT, NBT_EFFICIENCY, NBT_INPUT, NBT_WASTE_ENERGY).setParameterArray(NBT_OUTPUT,  5461,10240, 13727, 16384, 18475, 20164, 21557, 22726, 23720, 24576).setParameterArray(NBT_EFFICIENCY, 3250,5000,6000,6500,7000,7250,7333,7500,7666,7750).setParameterArray(NBT_PREHEAT_ENERGY,  98304000, 196608000,  294912000,  393216000,  491520000,  589824000,  688128000,  786432000,  884736000,  983040000).setParameterArray(NBT_PREHEAT_COST,  43, 86, 128, 171, 214, 256, 299, 342, 384, 427).setParameterArray(NBT_COOLDOWN_RATE,  4096, 8192,12288, 16384, 20480, 24576, 28672, 32768, 36864, 40960); // Tungstensteel
        aRegistry.addReplacer(17214).setParameters(NBT_EFFICIENCY_WATER, 9500, NBT_EFFICIENCY_OC, 5000, NBT_LENGTH_MIN, 1, NBT_LENGTH_MAX, 10, NBT_LENGTH_MID, 4).removeParameters(NBT_OUTPUT, NBT_EFFICIENCY, NBT_INPUT, NBT_WASTE_ENERGY).setParameterArray(NBT_OUTPUT, 43690,81920,109817,131072,147804,161319,172463,181809,189760,196608).setParameterArray(NBT_EFFICIENCY, 3250,5000,6000,6500,7000,7250,7333,7500,7666,7750).setParameterArray(NBT_PREHEAT_ENERGY, 786432000,1572864000,2359296000L,3145728000L,3932160000L,4718592000L,5505024000L,6291456000L,7077888000L,7864320000L).setParameterArray(NBT_PREHEAT_COST, 342,683,1024,1366,1707,2048,2390,2731,3072,3414).setParameterArray(NBT_COOLDOWN_RATE, 32768,65536,98304,131072,163840,196608,229376,262144,294912,327680); // Adamantium
        
        // Gas Turbines 合成变贵，各长度的参数，增加预热相关
        aRegistry.addReplacer(17231).setParameters(NBT_LENGTH_MIN, 3, NBT_LENGTH_MAX, 12, NBT_LENGTH_MID, 6).removeParameters(NBT_OUTPUT, NBT_EFFICIENCY, NBT_INPUT, NBT_WASTE_ENERGY, NBT_LIMIT_CONSUMPTION, NBT_ENERGY_ACCEPTED).setParameterArray(NBT_OUTPUT,  1365, 2560,  3431,  4096,  4618,  5041,  5389,  5681,  5930,  6144).setParameterArray(NBT_EFFICIENCY, 3250,5000,6000,6500,7000,7250,7333,7500,7666,7750).setParameterArray(NBT_PREHEAT_ENERGY,  24576000,  49152000,   73728000,   98304000,  122880000,  147456000,  172032000,  196608000,  221184000,  245760000).setParameterArray(NBT_PREHEAT_RATE,  1365, 2560,  3431,  4096,  4618,  5041,  5389,  5681,  5930,  6144).setParameterArray(NBT_PREHEAT_COST,  11, 22,  32,  43,  54,  64,  75,  86,  96, 107).setParameterArray(NBT_COOLDOWN_RATE,  1024, 2048, 3072,  4096,  5120,  6144,  7168,  8192,  9216, 10240).recipe("PwP", "BMC", "PEP", 'M', aRegistry.getItem(17211), 'B', "gt:re-battery1", 'C', IL.Processor_Crystal_Diamond, 'E', IL.MOTORS[1], 'P', OP.plateDense.dat(MT.Invar));         // StainlessSteel
        aRegistry.addReplacer(17232).setParameters(NBT_LENGTH_MIN, 3, NBT_LENGTH_MAX, 12, NBT_LENGTH_MID, 6).removeParameters(NBT_OUTPUT, NBT_EFFICIENCY, NBT_INPUT, NBT_WASTE_ENERGY, NBT_LIMIT_CONSUMPTION, NBT_ENERGY_ACCEPTED).setParameterArray(NBT_OUTPUT,  2730, 5120,  6863,  8192,  9237, 10082, 10778, 11363, 11860, 12288).setParameterArray(NBT_EFFICIENCY, 3250,5000,6000,6500,7000,7250,7333,7500,7666,7750).setParameterArray(NBT_PREHEAT_ENERGY,  49152000,  98304000,  147456000,  196608000,  245760000,  294912000,  344064000,  393216000,  442368000,  491520000).setParameterArray(NBT_PREHEAT_RATE,  2730, 5120,  6863,  8192,  9237, 10082, 10778, 11363, 11860, 12288).setParameterArray(NBT_PREHEAT_COST,  22, 43,  64,  86, 107, 128, 150, 171, 192, 214).setParameterArray(NBT_COOLDOWN_RATE,  2048, 4096, 6144,  8192, 10240, 12288, 14336, 16384, 18432, 20480).recipe("PwP", "BMC", "PEP", 'M', aRegistry.getItem(17212), 'B', "gt:re-battery2", 'C', IL.Processor_Crystal_Diamond, 'E', IL.MOTORS[2], 'P', OP.plateDense.dat(MT.TungstenSteel)); // Titanium
        aRegistry.addReplacer(17233).setParameters(NBT_LENGTH_MIN, 3, NBT_LENGTH_MAX, 12, NBT_LENGTH_MID, 6).removeParameters(NBT_OUTPUT, NBT_EFFICIENCY, NBT_INPUT, NBT_WASTE_ENERGY, NBT_LIMIT_CONSUMPTION, NBT_ENERGY_ACCEPTED).setParameterArray(NBT_OUTPUT,  5461,10240, 13727, 16384, 18475, 20164, 21557, 22726, 23720, 24576).setParameterArray(NBT_EFFICIENCY, 3250,5000,6000,6500,7000,7250,7333,7500,7666,7750).setParameterArray(NBT_PREHEAT_ENERGY,  98304000, 196608000,  294912000,  393216000,  491520000,  589824000,  688128000,  786432000,  884736000,  983040000).setParameterArray(NBT_PREHEAT_RATE,  5461,10240, 13727, 16384, 18475, 20164, 21557, 22726, 23720, 24576).setParameterArray(NBT_PREHEAT_COST,  43, 86, 128, 171, 214, 256, 299, 342, 384, 427).setParameterArray(NBT_COOLDOWN_RATE,  4096, 8192,12288, 16384, 20480, 24576, 28672, 32768, 36864, 40960).recipe("PwP", "BMC", "PEP", 'M', aRegistry.getItem(17213), 'B', "gt:re-battery3", 'C', IL.Processor_Crystal_Diamond, 'E', IL.MOTORS[3], 'P', OP.plateDense.dat(MT.W));             // Tungstensteel
        aRegistry.addReplacer(17234).setParameters(NBT_LENGTH_MIN, 3, NBT_LENGTH_MAX, 12, NBT_LENGTH_MID, 6).removeParameters(NBT_OUTPUT, NBT_EFFICIENCY, NBT_INPUT, NBT_WASTE_ENERGY, NBT_LIMIT_CONSUMPTION, NBT_ENERGY_ACCEPTED).setParameterArray(NBT_OUTPUT, 43690,81920,109817,131072,147804,161319,172463,181809,189760,196608).setParameterArray(NBT_EFFICIENCY, 3250,5000,6000,6500,7000,7250,7333,7500,7666,7750).setParameterArray(NBT_PREHEAT_ENERGY, 786432000,1572864000,2359296000L,3145728000L,3932160000L,4718592000L,5505024000L,6291456000L,7077888000L,7864320000L).setParameterArray(NBT_PREHEAT_RATE, 43690,81920,109817,131072,147804,161319,172463,181809,189760,196608).setParameterArray(NBT_PREHEAT_COST, 342,683,1024,1366,1707,2048,2390,2731,3072,3414).setParameterArray(NBT_COOLDOWN_RATE, 32768,65536,98304,131072,163840,196608,229376,262144,294912,327680).recipe("PwP", "BMC", "PEP", 'M', aRegistry.getItem(17214), 'B', "gt:re-battery5", 'C', IL.Processor_Crystal_Diamond, 'E', IL.MOTORS[5], 'P', OP.plateDense.dat(MT.Ad));            // Adamantium
        
        // Dynamo 大型发电机效率调整为 95%
        aRegistry.addReplacer(17221).setParameters(NBT_OUTPUT,   4096, NBT_EFFICIENCY_NUM, 9500).removeParameters(NBT_INPUT);
        aRegistry.addReplacer(17222).setParameters(NBT_OUTPUT,   8192, NBT_EFFICIENCY_NUM, 9500).removeParameters(NBT_INPUT);
        aRegistry.addReplacer(17223).setParameters(NBT_OUTPUT,  16384, NBT_EFFICIENCY_NUM, 9500).removeParameters(NBT_INPUT);
        aRegistry.addReplacer(17224).setParameters(NBT_OUTPUT, 131072, NBT_EFFICIENCY_NUM, 9500).removeParameters(NBT_INPUT);
    }
    
    
    @Override protected void machines1BeforeLoad(MultiTileEntityRegistry aRegistry, MultiTileEntityBlock aMetal, MultiTileEntityBlock aMetalChips, MultiTileEntityBlock aMetalWires, MultiTileEntityBlock aMachine, MultiTileEntityBlock aWooden, MultiTileEntityBlock aBush, MultiTileEntityBlock aStone, MultiTileEntityBlock aWool, MultiTileEntityBlock aTNT, MultiTileEntityBlock aHive, MultiTileEntityBlock aUtilMetal, MultiTileEntityBlock aUtilStone, MultiTileEntityBlock aUtilWood, MultiTileEntityBlock aUtilWool, OreDictMaterial aMat, Class<? extends TileEntity> aClass) {
        /// 修改项
        // Oven 增加并行避免卡 1 tick
        aRegistry.addReplacer(20001).setParameters(NBT_PARALLEL,  4, NBT_PARALLEL_DURATION, T);
        aRegistry.addReplacer(20002).setParameters(NBT_PARALLEL,  8, NBT_PARALLEL_DURATION, T);
        aRegistry.addReplacer(20003).setParameters(NBT_PARALLEL, 16, NBT_PARALLEL_DURATION, T);
        aRegistry.addReplacer(20004).setParameters(NBT_PARALLEL, 32, NBT_PARALLEL_DURATION, T);
    }
    @Override protected void machines1FinishLoad(MultiTileEntityRegistry aRegistry, MultiTileEntityBlock aMetal, MultiTileEntityBlock aMetalChips, MultiTileEntityBlock aMetalWires, MultiTileEntityBlock aMachine, MultiTileEntityBlock aWooden, MultiTileEntityBlock aBush, MultiTileEntityBlock aStone, MultiTileEntityBlock aWool, MultiTileEntityBlock aTNT, MultiTileEntityBlock aHive, MultiTileEntityBlock aUtilMetal, MultiTileEntityBlock aUtilStone, MultiTileEntityBlock aUtilWood, MultiTileEntityBlock aUtilWool, OreDictMaterial aMat, Class<? extends TileEntity> aClass) {
        /// 添加项
        // 矿藏钻头 ID: 23400-23450
        aClass = MultiTileEntityDepositDrill.class;
        aMat = MT.DATA.Kinetic_T[1];    aRegistry.add("Deposit Mining Drill ("+aMat.getLocal()+")", "Basic Machines"                      , 23400, 20001, aClass, aMat.mToolQuality, 16, aMachine     , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   7.0F, NBT_RESISTANCE,   7.0F, NBT_INPUT,   32, NBT_LEVEL, 0, NBT_EFFICIENCY, 10000));
        aMat = MT.DATA.Kinetic_T[2];    aRegistry.add("Deposit Mining Drill ("+aMat.getLocal()+")", "Basic Machines"                      , 23401, 20001, aClass, aMat.mToolQuality, 16, aMachine     , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_INPUT,  128, NBT_LEVEL, 2, NBT_EFFICIENCY, 10000));
        aMat = MT.DATA.Kinetic_T[3];    aRegistry.add("Deposit Mining Drill ("+aMat.getLocal()+")", "Basic Machines"                      , 23402, 20001, aClass, aMat.mToolQuality, 16, aMachine     , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   9.0F, NBT_RESISTANCE,   9.0F, NBT_INPUT,  512, NBT_LEVEL, 4, NBT_EFFICIENCY, 10000));
        aMat = MT.DATA.Kinetic_T[4];    aRegistry.add("Deposit Mining Drill ("+aMat.getLocal()+")", "Basic Machines"                      , 23403, 20001, aClass, aMat.mToolQuality, 16, aMachine     , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,  12.5F, NBT_RESISTANCE,  12.5F, NBT_INPUT, 2048, NBT_LEVEL, 6, NBT_EFFICIENCY, 10000));
    }
    
    
    @Override protected void machines3BeforeLoad(MultiTileEntityRegistry aRegistry, MultiTileEntityBlock aMetal, MultiTileEntityBlock aMetalChips, MultiTileEntityBlock aMetalWires, MultiTileEntityBlock aMachine, MultiTileEntityBlock aWooden, MultiTileEntityBlock aBush, MultiTileEntityBlock aStone, MultiTileEntityBlock aWool, MultiTileEntityBlock aTNT, MultiTileEntityBlock aHive, MultiTileEntityBlock aUtilMetal, MultiTileEntityBlock aUtilStone, MultiTileEntityBlock aUtilWood, MultiTileEntityBlock aUtilWool, OreDictMaterial aMat, Class<? extends TileEntity> aClass) {
        /// 修改项
        // Steam Cracker 增加可以填充蒸汽
        aRegistry.addReplacer(20491).setParameters(NBT_CANFILL_STEAM, T);
        aRegistry.addReplacer(20492).setParameters(NBT_CANFILL_STEAM, T);
        aRegistry.addReplacer(20493).setParameters(NBT_CANFILL_STEAM, T);
        aRegistry.addReplacer(20494).setParameters(NBT_CANFILL_STEAM, T);
    }
    
    
    @Override protected void machines4BeforeLoad(MultiTileEntityRegistry aRegistry, MultiTileEntityBlock aMetal, MultiTileEntityBlock aMetalChips, MultiTileEntityBlock aMetalWires, MultiTileEntityBlock aMachine, MultiTileEntityBlock aWooden, MultiTileEntityBlock aBush, MultiTileEntityBlock aStone, MultiTileEntityBlock aWool, MultiTileEntityBlock aTNT, MultiTileEntityBlock aHive, MultiTileEntityBlock aUtilMetal, MultiTileEntityBlock aUtilStone, MultiTileEntityBlock aUtilWood, MultiTileEntityBlock aUtilWool, OreDictMaterial aMat, Class<? extends TileEntity> aClass) {
        /// 修改项
        // Bath 和 Autoclave 增加可以填充蒸汽
        aRegistry.addReplacer(22002).setParameters(NBT_CANFILL_STEAM, T);
        aRegistry.addReplacer(22004).setParameters(NBT_CANFILL_STEAM, T);
    }
    
    
    @Override protected void kineticBeforeLoad(MultiTileEntityRegistry aRegistry, MultiTileEntityBlock aMetal, MultiTileEntityBlock aMetalChips, MultiTileEntityBlock aMetalWires, MultiTileEntityBlock aMachine, MultiTileEntityBlock aWooden, MultiTileEntityBlock aBush, MultiTileEntityBlock aStone, MultiTileEntityBlock aWool, MultiTileEntityBlock aTNT, MultiTileEntityBlock aHive, MultiTileEntityBlock aUtilMetal, MultiTileEntityBlock aUtilStone, MultiTileEntityBlock aUtilWood, MultiTileEntityBlock aUtilWool, OreDictMaterial aMat, Class<? extends TileEntity> aClass) {
        /// 修改项
        // Axles 调整功率限制
        // Wood
        aRegistry.addReplacer(24800).setParameters(NBT_PIPEBANDWIDTH,  1);
        aRegistry.addReplacer(24801).setParameters(NBT_PIPEBANDWIDTH,  4);
        aRegistry.addReplacer(24802).setParameters(NBT_PIPEBANDWIDTH, 16);
        aRegistry.addReplacer(24803).setParameters(NBT_PIPEBANDWIDTH, 64);
        // Bronze
        aRegistry.addReplacer(24810).setParameters(NBT_PIPEBANDWIDTH,  1);
        aRegistry.addReplacer(24811).setParameters(NBT_PIPEBANDWIDTH,  4);
        aRegistry.addReplacer(24812).setParameters(NBT_PIPEBANDWIDTH, 16);
        aRegistry.addReplacer(24813).setParameters(NBT_PIPEBANDWIDTH, 64);
        // ArsenicCopper
        aRegistry.addReplacer(24780).setParameters(NBT_PIPEBANDWIDTH,  1);
        aRegistry.addReplacer(24781).setParameters(NBT_PIPEBANDWIDTH,  4);
        aRegistry.addReplacer(24782).setParameters(NBT_PIPEBANDWIDTH, 16);
        aRegistry.addReplacer(24783).setParameters(NBT_PIPEBANDWIDTH, 64);
        // ArsenicBronze
        aRegistry.addReplacer(24790).setParameters(NBT_PIPEBANDWIDTH,  1);
        aRegistry.addReplacer(24791).setParameters(NBT_PIPEBANDWIDTH,  4);
        aRegistry.addReplacer(24792).setParameters(NBT_PIPEBANDWIDTH, 16);
        aRegistry.addReplacer(24793).setParameters(NBT_PIPEBANDWIDTH, 64);
        // AnyIronSteel
        aRegistry.addReplacer(24820).setParameters(NBT_PIPEBANDWIDTH,  1);
        aRegistry.addReplacer(24821).setParameters(NBT_PIPEBANDWIDTH,  4);
        aRegistry.addReplacer(24822).setParameters(NBT_PIPEBANDWIDTH, 16);
        aRegistry.addReplacer(24823).setParameters(NBT_PIPEBANDWIDTH, 64);
        // Titanium
        aRegistry.addReplacer(24830).setParameters(NBT_PIPEBANDWIDTH,  1);
        aRegistry.addReplacer(24831).setParameters(NBT_PIPEBANDWIDTH,  4);
        aRegistry.addReplacer(24832).setParameters(NBT_PIPEBANDWIDTH, 16);
        aRegistry.addReplacer(24833).setParameters(NBT_PIPEBANDWIDTH, 64);
        // Tungstensteel
        aRegistry.addReplacer(24840).setParameters(NBT_PIPEBANDWIDTH,  1);
        aRegistry.addReplacer(24841).setParameters(NBT_PIPEBANDWIDTH,  4);
        aRegistry.addReplacer(24842).setParameters(NBT_PIPEBANDWIDTH, 16);
        aRegistry.addReplacer(24843).setParameters(NBT_PIPEBANDWIDTH, 64);
        // Iridium
        aRegistry.addReplacer(24850).setParameters(NBT_PIPEBANDWIDTH,  1);
        aRegistry.addReplacer(24851).setParameters(NBT_PIPEBANDWIDTH,  4);
        aRegistry.addReplacer(24852).setParameters(NBT_PIPEBANDWIDTH, 16);
        aRegistry.addReplacer(24853).setParameters(NBT_PIPEBANDWIDTH, 64);
        // TitaniumIridium
        aRegistry.addReplacer(24860).setParameters(NBT_PIPEBANDWIDTH,  1);
        aRegistry.addReplacer(24861).setParameters(NBT_PIPEBANDWIDTH,  4);
        aRegistry.addReplacer(24862).setParameters(NBT_PIPEBANDWIDTH, 16);
        aRegistry.addReplacer(24863).setParameters(NBT_PIPEBANDWIDTH, 64);
        // Trinitanium
        aRegistry.addReplacer(24870).setParameters(NBT_PIPEBANDWIDTH,  1);
        aRegistry.addReplacer(24871).setParameters(NBT_PIPEBANDWIDTH,  4);
        aRegistry.addReplacer(24872).setParameters(NBT_PIPEBANDWIDTH, 16);
        aRegistry.addReplacer(24873).setParameters(NBT_PIPEBANDWIDTH, 64);
        // Trinaquadalloy
        aRegistry.addReplacer(24880).setParameters(NBT_PIPEBANDWIDTH,  1);
        aRegistry.addReplacer(24881).setParameters(NBT_PIPEBANDWIDTH,  4);
        aRegistry.addReplacer(24882).setParameters(NBT_PIPEBANDWIDTH, 16);
        aRegistry.addReplacer(24883).setParameters(NBT_PIPEBANDWIDTH, 64);
        // Adamantium
        aRegistry.addReplacer(24890).setParameters(NBT_PIPEBANDWIDTH,  1);
        aRegistry.addReplacer(24891).setParameters(NBT_PIPEBANDWIDTH,  4);
        aRegistry.addReplacer(24892).setParameters(NBT_PIPEBANDWIDTH, 16);
        aRegistry.addReplacer(24893).setParameters(NBT_PIPEBANDWIDTH, 64);
        
        // Engine rotations 降低造价
        aMat = MT.WoodTreated;      aRegistry.addReplacer(24807).recipe("PSP", "wAL", "GAG", 'S', OP.gearGtSmall.dat(aMat), 'G', OP.gearGt.dat(aMat), 'P', OP.plate        .dat(aMat), 'L', OD.itemLubricantEarly, 'A', aRegistry.getItem(24800));
        aMat = MT.Bronze;           aRegistry.addReplacer(24817).recipe("SAS", "wML", "GAG", 'S', OP.gearGtSmall.dat(aMat), 'G', OP.gearGt.dat(aMat), 'M', OP.casingMachine.dat(aMat), 'L', OD.itemLubricantEarly, 'A', aRegistry.getItem(24810));
        aMat = MT.ArsenicCopper;    aRegistry.addReplacer(24787).recipe("SAS", "wML", "GAG", 'S', OP.gearGtSmall.dat(aMat), 'G', OP.gearGt.dat(aMat), 'M', OP.casingMachine.dat(aMat), 'L', OD.itemLubricantEarly, 'A', aRegistry.getItem(24780));
        aMat = MT.ArsenicBronze;    aRegistry.addReplacer(24797).recipe("SAS", "wML", "GAG", 'S', OP.gearGtSmall.dat(aMat), 'G', OP.gearGt.dat(aMat), 'M', OP.casingMachine.dat(aMat), 'L', OD.itemLubricantEarly, 'A', aRegistry.getItem(24790));
        aMat = ANY.Steel;           aRegistry.addReplacer(24827).recipe("SAS", "wML", "GAG", 'S', OP.gearGtSmall.dat(aMat), 'G', OP.gearGt.dat(aMat), 'M', OP.casingMachine.dat(aMat), 'L', OD.itemLubricantEarly, 'A', aRegistry.getItem(24820));
        aMat = MT.Ti;               aRegistry.addReplacer(24837).recipe("SAS", "wML", "GAG", 'S', OP.gearGtSmall.dat(aMat), 'G', OP.gearGt.dat(aMat), 'M', OP.casingMachine.dat(aMat), 'L', OD.itemLubricantEarly, 'A', aRegistry.getItem(24830));
        aMat = MT.TungstenSteel;    aRegistry.addReplacer(24847).recipe("SAS", "wML", "GAG", 'S', OP.gearGtSmall.dat(aMat), 'G', OP.gearGt.dat(aMat), 'M', OP.casingMachine.dat(aMat), 'L', OD.itemLubricantEarly, 'A', aRegistry.getItem(24840));
        aMat = MT.Ir;               aRegistry.addReplacer(24857).recipe("SAS", "wML", "GAG", 'S', OP.gearGtSmall.dat(aMat), 'G', OP.gearGt.dat(aMat), 'M', OP.casingMachine.dat(aMat), 'L', OD.itemLubricantEarly, 'A', aRegistry.getItem(24850));
        aMat = MT.Iritanium;        aRegistry.addReplacer(24867).recipe("SAS", "wML", "GAG", 'S', OP.gearGtSmall.dat(aMat), 'G', OP.gearGt.dat(aMat), 'M', OP.casingMachine.dat(aMat), 'L', OD.itemLubricantEarly, 'A', aRegistry.getItem(24860));
        aMat = MT.Trinitanium;      aRegistry.addReplacer(24877).recipe("SAS", "wML", "GAG", 'S', OP.gearGtSmall.dat(aMat), 'G', OP.gearGt.dat(aMat), 'M', OP.casingMachine.dat(aMat), 'L', OD.itemLubricantEarly, 'A', aRegistry.getItem(24870));
        aMat = MT.Trinaquadalloy;   aRegistry.addReplacer(24887).recipe("SAS", "wML", "GAG", 'S', OP.gearGtSmall.dat(aMat), 'G', OP.gearGt.dat(aMat), 'M', OP.casingMachine.dat(aMat), 'L', OD.itemLubricantEarly, 'A', aRegistry.getItem(24880));
        aMat = MT.Ad;               aRegistry.addReplacer(24897).recipe("SAS", "wML", "GAG", 'S', OP.gearGtSmall.dat(aMat), 'G', OP.gearGt.dat(aMat), 'M', OP.casingMachine.dat(aMat), 'L', OD.itemLubricantEarly, 'A', aRegistry.getItem(24890));
        
        // Transformer rotations 降低造价
        aMat = MT.WoodTreated;      aRegistry.addReplacer(24808).recipe("ASL", "SGS", "PSA", 'S', OP.gearGtSmall.dat(aMat), 'G', OP.gearGt.dat(aMat), 'P', OP.plate        .dat(aMat), 'L', OD.itemLubricantEarly, 'A', aRegistry.getItem(24800));
        aMat = MT.Bronze;           aRegistry.addReplacer(24818).recipe("ASL", "SGS", "MSA", 'S', OP.gearGtSmall.dat(aMat), 'G', OP.gearGt.dat(aMat), 'M', OP.casingMachine.dat(aMat), 'L', OD.itemLubricantEarly, 'A', aRegistry.getItem(24810));
        aMat = MT.ArsenicCopper;    aRegistry.addReplacer(24788).recipe("ASL", "SGS", "MSA", 'S', OP.gearGtSmall.dat(aMat), 'G', OP.gearGt.dat(aMat), 'M', OP.casingMachine.dat(aMat), 'L', OD.itemLubricantEarly, 'A', aRegistry.getItem(24780));
        aMat = MT.ArsenicBronze;    aRegistry.addReplacer(24798).recipe("ASL", "SGS", "MSA", 'S', OP.gearGtSmall.dat(aMat), 'G', OP.gearGt.dat(aMat), 'M', OP.casingMachine.dat(aMat), 'L', OD.itemLubricantEarly, 'A', aRegistry.getItem(24790));
        aMat = ANY.Steel;           aRegistry.addReplacer(24828).recipe("ASL", "SGS", "MSA", 'S', OP.gearGtSmall.dat(aMat), 'G', OP.gearGt.dat(aMat), 'M', OP.casingMachine.dat(aMat), 'L', OD.itemLubricantEarly, 'A', aRegistry.getItem(24820));
        aMat = MT.Ti;               aRegistry.addReplacer(24838).recipe("ASL", "SGS", "MSA", 'S', OP.gearGtSmall.dat(aMat), 'G', OP.gearGt.dat(aMat), 'M', OP.casingMachine.dat(aMat), 'L', OD.itemLubricantEarly, 'A', aRegistry.getItem(24830));
        aMat = MT.TungstenSteel;    aRegistry.addReplacer(24848).recipe("ASL", "SGS", "MSA", 'S', OP.gearGtSmall.dat(aMat), 'G', OP.gearGt.dat(aMat), 'M', OP.casingMachine.dat(aMat), 'L', OD.itemLubricantEarly, 'A', aRegistry.getItem(24840));
        aMat = MT.Ir;               aRegistry.addReplacer(24858).recipe("ASL", "SGS", "MSA", 'S', OP.gearGtSmall.dat(aMat), 'G', OP.gearGt.dat(aMat), 'M', OP.casingMachine.dat(aMat), 'L', OD.itemLubricantEarly, 'A', aRegistry.getItem(24850));
        aMat = MT.Iritanium;        aRegistry.addReplacer(24868).recipe("ASL", "SGS", "MSA", 'S', OP.gearGtSmall.dat(aMat), 'G', OP.gearGt.dat(aMat), 'M', OP.casingMachine.dat(aMat), 'L', OD.itemLubricantEarly, 'A', aRegistry.getItem(24860));
        aMat = MT.Trinitanium;      aRegistry.addReplacer(24878).recipe("ASL", "SGS", "MSA", 'S', OP.gearGtSmall.dat(aMat), 'G', OP.gearGt.dat(aMat), 'M', OP.casingMachine.dat(aMat), 'L', OD.itemLubricantEarly, 'A', aRegistry.getItem(24870));
        aMat = MT.Trinaquadalloy;   aRegistry.addReplacer(24888).recipe("ASL", "SGS", "MSA", 'S', OP.gearGtSmall.dat(aMat), 'G', OP.gearGt.dat(aMat), 'M', OP.casingMachine.dat(aMat), 'L', OD.itemLubricantEarly, 'A', aRegistry.getItem(24880));
        aMat = MT.Ad;               aRegistry.addReplacer(24898).recipe("ASL", "SGS", "MSA", 'S', OP.gearGtSmall.dat(aMat), 'G', OP.gearGt.dat(aMat), 'M', OP.casingMachine.dat(aMat), 'L', OD.itemLubricantEarly, 'A', aRegistry.getItem(24890));
        
        // Gear boxes 降低造价
        aMat = MT.WoodTreated;      aRegistry.addReplacer(24809).recipe("PsP", "ALA", "PAP"                                                         , 'P', OP.plate        .dat(aMat), 'L', OD.itemLubricantEarly, 'A', aRegistry.getItem(24800));
        aMat = MT.Bronze;           aRegistry.addReplacer(24819).recipe("wAL", "AMA"                                                                , 'M', OP.casingMachine.dat(aMat), 'L', OD.itemLubricantEarly, 'A', aRegistry.getItem(24810));
        aMat = MT.ArsenicCopper;    aRegistry.addReplacer(24789).recipe("wAL", "AMA"                                                                , 'M', OP.casingMachine.dat(aMat), 'L', OD.itemLubricantEarly, 'A', aRegistry.getItem(24780));
        aMat = MT.ArsenicBronze;    aRegistry.addReplacer(24799).recipe("wAL", "AMA"                                                                , 'M', OP.casingMachine.dat(aMat), 'L', OD.itemLubricantEarly, 'A', aRegistry.getItem(24790));
        aMat = ANY.Steel;           aRegistry.addReplacer(24829).recipe("wAL", "AMA"                                                                , 'M', OP.casingMachine.dat(aMat), 'L', OD.itemLubricantEarly, 'A', aRegistry.getItem(24820));
        aMat = MT.Ti;               aRegistry.addReplacer(24839).recipe("wAL", "AMA"                                                                , 'M', OP.casingMachine.dat(aMat), 'L', OD.itemLubricantEarly, 'A', aRegistry.getItem(24830));
        aMat = MT.TungstenSteel;    aRegistry.addReplacer(24849).recipe("wAL", "AMA"                                                                , 'M', OP.casingMachine.dat(aMat), 'L', OD.itemLubricantEarly, 'A', aRegistry.getItem(24840));
        aMat = MT.Ir;               aRegistry.addReplacer(24859).recipe("wAL", "AMA"                                                                , 'M', OP.casingMachine.dat(aMat), 'L', OD.itemLubricantEarly, 'A', aRegistry.getItem(24850));
        aMat = MT.Iritanium;        aRegistry.addReplacer(24869).recipe("wAL", "AMA"                                                                , 'M', OP.casingMachine.dat(aMat), 'L', OD.itemLubricantEarly, 'A', aRegistry.getItem(24860));
        aMat = MT.Trinitanium;      aRegistry.addReplacer(24879).recipe("wAL", "AMA"                                                                , 'M', OP.casingMachine.dat(aMat), 'L', OD.itemLubricantEarly, 'A', aRegistry.getItem(24870));
        aMat = MT.Trinaquadalloy;   aRegistry.addReplacer(24889).recipe("wAL", "AMA"                                                                , 'M', OP.casingMachine.dat(aMat), 'L', OD.itemLubricantEarly, 'A', aRegistry.getItem(24880));
        aMat = MT.Ad;               aRegistry.addReplacer(24899).recipe("wAL", "AMA"                                                                , 'M', OP.casingMachine.dat(aMat), 'L', OD.itemLubricantEarly, 'A', aRegistry.getItem(24890));
    }
    
    
    // 提供通用的代码来直接修改这种材料的所有种类电线的参数，减少重复代码
    private static void setElectricWiresVoltage(MultiTileEntityRegistry aRegistry, int aID, long aVoltage) {
        aRegistry.addReplacer(aID+ 0).setParameters(NBT_PIPESIZE, aVoltage);
        aRegistry.addReplacer(aID+ 1).setParameters(NBT_PIPESIZE, aVoltage);
        aRegistry.addReplacer(aID+ 2).setParameters(NBT_PIPESIZE, aVoltage);
        aRegistry.addReplacer(aID+ 3).setParameters(NBT_PIPESIZE, aVoltage);
        aRegistry.addReplacer(aID+ 4).setParameters(NBT_PIPESIZE, aVoltage);
        aRegistry.addReplacer(aID+ 5).setParameters(NBT_PIPESIZE, aVoltage);
        aRegistry.addReplacer(aID+ 6).setParameters(NBT_PIPESIZE, aVoltage);
        aRegistry.addReplacer(aID+ 7).setParameters(NBT_PIPESIZE, aVoltage);
        aRegistry.addReplacer(aID+ 8).setParameters(NBT_PIPESIZE, aVoltage);
        aRegistry.addReplacer(aID+ 9).setParameters(NBT_PIPESIZE, aVoltage);
        aRegistry.addReplacer(aID+10).setParameters(NBT_PIPESIZE, aVoltage);
        aRegistry.addReplacer(aID+11).setParameters(NBT_PIPESIZE, aVoltage);
        aRegistry.addReplacer(aID+12).setParameters(NBT_PIPESIZE, aVoltage);
        aRegistry.addReplacer(aID+13).setParameters(NBT_PIPESIZE, aVoltage);
        aRegistry.addReplacer(aID+14).setParameters(NBT_PIPESIZE, aVoltage);
        aRegistry.addReplacer(aID+15).setParameters(NBT_PIPESIZE, aVoltage);
    }
    private static void setElectricCablesVoltage(MultiTileEntityRegistry aRegistry, int aID, long aVoltage) {
        aRegistry.addReplacer(aID+16).setParameters(NBT_PIPESIZE, aVoltage);
        aRegistry.addReplacer(aID+17).setParameters(NBT_PIPESIZE, aVoltage);
        aRegistry.addReplacer(aID+19).setParameters(NBT_PIPESIZE, aVoltage);
        aRegistry.addReplacer(aID+23).setParameters(NBT_PIPESIZE, aVoltage);
        aRegistry.addReplacer(aID+27).setParameters(NBT_PIPESIZE, aVoltage);
    }
    private static void setElectricWiresAmperage(MultiTileEntityRegistry aRegistry, int aID, long aAmperage) {
        aRegistry.addReplacer(aID+ 0).setParameters(NBT_PIPEBANDWIDTH, aAmperage* 1);
        aRegistry.addReplacer(aID+ 1).setParameters(NBT_PIPEBANDWIDTH, aAmperage* 2);
        aRegistry.addReplacer(aID+ 2).setParameters(NBT_PIPEBANDWIDTH, aAmperage* 3);
        aRegistry.addReplacer(aID+ 3).setParameters(NBT_PIPEBANDWIDTH, aAmperage* 4);
        aRegistry.addReplacer(aID+ 4).setParameters(NBT_PIPEBANDWIDTH, aAmperage* 5);
        aRegistry.addReplacer(aID+ 5).setParameters(NBT_PIPEBANDWIDTH, aAmperage* 6);
        aRegistry.addReplacer(aID+ 6).setParameters(NBT_PIPEBANDWIDTH, aAmperage* 7);
        aRegistry.addReplacer(aID+ 7).setParameters(NBT_PIPEBANDWIDTH, aAmperage* 8);
        aRegistry.addReplacer(aID+ 8).setParameters(NBT_PIPEBANDWIDTH, aAmperage* 9);
        aRegistry.addReplacer(aID+ 9).setParameters(NBT_PIPEBANDWIDTH, aAmperage*10);
        aRegistry.addReplacer(aID+10).setParameters(NBT_PIPEBANDWIDTH, aAmperage*11);
        aRegistry.addReplacer(aID+11).setParameters(NBT_PIPEBANDWIDTH, aAmperage*12);
        aRegistry.addReplacer(aID+12).setParameters(NBT_PIPEBANDWIDTH, aAmperage*13);
        aRegistry.addReplacer(aID+13).setParameters(NBT_PIPEBANDWIDTH, aAmperage*14);
        aRegistry.addReplacer(aID+14).setParameters(NBT_PIPEBANDWIDTH, aAmperage*15);
        aRegistry.addReplacer(aID+15).setParameters(NBT_PIPEBANDWIDTH, aAmperage*16);
    }
    private static void setElectricCablesAmperage(MultiTileEntityRegistry aRegistry, int aID, long aAmperage) {
        aRegistry.addReplacer(aID+16).setParameters(NBT_PIPEBANDWIDTH, aAmperage* 1);
        aRegistry.addReplacer(aID+17).setParameters(NBT_PIPEBANDWIDTH, aAmperage* 2);
        aRegistry.addReplacer(aID+19).setParameters(NBT_PIPEBANDWIDTH, aAmperage* 4);
        aRegistry.addReplacer(aID+23).setParameters(NBT_PIPEBANDWIDTH, aAmperage* 8);
        aRegistry.addReplacer(aID+27).setParameters(NBT_PIPEBANDWIDTH, aAmperage*12);
    }
    private static void setElectricWiresResistance(MultiTileEntityRegistry aRegistry, int aID, long aResistance) {
        aRegistry.addReplacer(aID+ 0).setParameters(NBT_RESISTANCE+".electric", aResistance   );
        aRegistry.addReplacer(aID+ 1).setParameters(NBT_RESISTANCE+".electric", aResistance/2 );
        aRegistry.addReplacer(aID+ 2).setParameters(NBT_RESISTANCE+".electric", aResistance/3 );
        aRegistry.addReplacer(aID+ 3).setParameters(NBT_RESISTANCE+".electric", aResistance/4 );
        aRegistry.addReplacer(aID+ 4).setParameters(NBT_RESISTANCE+".electric", aResistance/5 );
        aRegistry.addReplacer(aID+ 5).setParameters(NBT_RESISTANCE+".electric", aResistance/6 );
        aRegistry.addReplacer(aID+ 6).setParameters(NBT_RESISTANCE+".electric", aResistance/7 );
        aRegistry.addReplacer(aID+ 7).setParameters(NBT_RESISTANCE+".electric", aResistance/8 );
        aRegistry.addReplacer(aID+ 8).setParameters(NBT_RESISTANCE+".electric", aResistance/9 );
        aRegistry.addReplacer(aID+ 9).setParameters(NBT_RESISTANCE+".electric", aResistance/10);
        aRegistry.addReplacer(aID+10).setParameters(NBT_RESISTANCE+".electric", aResistance/11);
        aRegistry.addReplacer(aID+11).setParameters(NBT_RESISTANCE+".electric", aResistance/12);
        aRegistry.addReplacer(aID+12).setParameters(NBT_RESISTANCE+".electric", aResistance/13);
        aRegistry.addReplacer(aID+13).setParameters(NBT_RESISTANCE+".electric", aResistance/14);
        aRegistry.addReplacer(aID+14).setParameters(NBT_RESISTANCE+".electric", aResistance/15);
        aRegistry.addReplacer(aID+15).setParameters(NBT_RESISTANCE+".electric", aResistance/16);
    }
    private static void setElectricCablesResistance(MultiTileEntityRegistry aRegistry, int aID, long aResistance) {
        aRegistry.addReplacer(aID+16).setParameters(NBT_RESISTANCE+".electric", aResistance   );
        aRegistry.addReplacer(aID+17).setParameters(NBT_RESISTANCE+".electric", aResistance/2 );
        aRegistry.addReplacer(aID+19).setParameters(NBT_RESISTANCE+".electric", aResistance/4 );
        aRegistry.addReplacer(aID+23).setParameters(NBT_RESISTANCE+".electric", aResistance/8 );
        aRegistry.addReplacer(aID+27).setParameters(NBT_RESISTANCE+".electric", aResistance/12);
    }
    @Override protected void connectorsBeforeLoad(MultiTileEntityRegistry aRegistry, MultiTileEntityBlock aMetal, MultiTileEntityBlock aMetalChips, MultiTileEntityBlock aMetalWires, MultiTileEntityBlock aMachine, MultiTileEntityBlock aWooden, MultiTileEntityBlock aBush, MultiTileEntityBlock aStone, MultiTileEntityBlock aWool, MultiTileEntityBlock aTNT, MultiTileEntityBlock aHive, MultiTileEntityBlock aUtilMetal, MultiTileEntityBlock aUtilStone, MultiTileEntityBlock aUtilWood, MultiTileEntityBlock aUtilWool, OreDictMaterial aMat, Class<? extends TileEntity> aClass) {
        /// 修改项
        // 调整部分电线的电压电流电阻的参数，直接选用现实的真实值，取 2.4 时为 1，加上绝缘后电阻减少为原来的 0.75
        // Sn 11.5
        setElectricWiresAmperage   (aRegistry, 28050, 1);
        setElectricCablesAmperage  (aRegistry, 28050, 1);
        setElectricWiresResistance (aRegistry, 28050, 115 * U24);
        setElectricCablesResistance(aRegistry, 28050, 115 * U32);
        // Pb 20.8
        setElectricWiresAmperage   (aRegistry, 28100, 1);
        setElectricCablesAmperage  (aRegistry, 28100, 1);
        setElectricWiresResistance (aRegistry, 28100, 208 * U24);
        setElectricCablesResistance(aRegistry, 28100, 208 * U32);
        
        // Constantan 49
        setElectricWiresAmperage   (aRegistry, 28300, 4);
        setElectricCablesAmperage  (aRegistry, 28300, 4);
        setElectricWiresResistance (aRegistry, 28300, 490 * U24);
        setElectricCablesResistance(aRegistry, 28300, 490 * U32);
        // Cu 1.68
        setElectricWiresAmperage   (aRegistry, 28350, 3);
        setElectricCablesAmperage  (aRegistry, 28350, 3);
        setElectricWiresResistance (aRegistry, 28350, 168 * U240);
        setElectricCablesResistance(aRegistry, 28350, 168 * U320);
        // AnnealedCopper 1.72
        setElectricWiresAmperage   (aRegistry, 28400, 3);
        setElectricCablesAmperage  (aRegistry, 28400, 3);
        setElectricWiresResistance (aRegistry, 28400, 172 * U240);
        setElectricCablesResistance(aRegistry, 28400, 172 * U320);
        // Efrine
        setElectricWiresAmperage   (aRegistry, 28450, 1);
        setElectricCablesAmperage  (aRegistry, 28450, 1);
        setElectricWiresResistance (aRegistry, 28450, 100 * U24);
        setElectricCablesResistance(aRegistry, 28450, 100 * U32);
        
        // Kanthal 140
        setElectricWiresAmperage   (aRegistry, 28600, 4);
        setElectricCablesAmperage  (aRegistry, 28600, 4);
        setElectricWiresResistance (aRegistry, 28600, 1400 * U24);
        setElectricCablesResistance(aRegistry, 28600, 1400 * U32);
        // Ag 1.59
        setElectricWiresAmperage   (aRegistry, 28650, 3);
        setElectricCablesAmperage  (aRegistry, 28650, 3);
        setElectricWiresResistance (aRegistry, 28650, 159 * U240);
        setElectricCablesResistance(aRegistry, 28650, 159 * U320);
        // Au 2.21
        setElectricWiresAmperage   (aRegistry, 28700, 4);
        setElectricCablesAmperage  (aRegistry, 28700, 4);
        setElectricWiresResistance (aRegistry, 28700, 22 * U24);
        setElectricCablesResistance(aRegistry, 28700, 22 * U32);
        // Electrum
        setElectricWiresAmperage   (aRegistry, 28750, 2);
        setElectricCablesAmperage  (aRegistry, 28750, 2);
        setElectricWiresResistance (aRegistry, 28750, 80 * U24);
        setElectricCablesResistance(aRegistry, 28750, 80 * U32);
        // BlueAlloy
        setElectricWiresAmperage   (aRegistry, 28800, 2);
        setElectricCablesAmperage  (aRegistry, 28800, 2);
        setElectricWiresResistance (aRegistry, 28800, 120 * U24);
        setElectricCablesResistance(aRegistry, 28800, 120 * U32);
        // ElectrotineAlloy
        setElectricWiresAmperage   (aRegistry, 28850, 3);
        setElectricCablesAmperage  (aRegistry, 28850, 3);
        setElectricWiresResistance (aRegistry, 28850, 75 * U24);
        setElectricCablesResistance(aRegistry, 28850, 75 * U32);
        
        // Nichrome 100
        setElectricWiresAmperage   (aRegistry, 28900, 4);
        setElectricCablesAmperage  (aRegistry, 28900, 4);
        setElectricWiresResistance (aRegistry, 28900, 1000 * U24);
        setElectricCablesResistance(aRegistry, 28900, 1000 * U32);
        // Steel 9.8
        setElectricWiresAmperage   (aRegistry, 28950, 2);
        setElectricCablesAmperage  (aRegistry, 28950, 2);
        setElectricWiresResistance (aRegistry, 28950, 98 * U24);
        setElectricCablesResistance(aRegistry, 28950, 98 * U32);
        // HSLA
        setElectricWiresAmperage   (aRegistry, 28250, 3);
        setElectricCablesAmperage  (aRegistry, 28250, 3);
        setElectricWiresResistance (aRegistry, 28250, 98 * U24);
        setElectricCablesResistance(aRegistry, 28250, 98 * U32);
        // Al 2.65
        setElectricWiresAmperage   (aRegistry, 29000, 1);
        setElectricCablesAmperage  (aRegistry, 29000, 1);
        setElectricWiresResistance (aRegistry, 29000, 265 * U240);
        setElectricCablesResistance(aRegistry, 29000, 265 * U320);
        // TungstenSteel
        setElectricWiresAmperage   (aRegistry, 29050, 4);
        setElectricCablesAmperage  (aRegistry, 29050, 4);
        setElectricWiresResistance (aRegistry, 29050, 150 * U24);
        setElectricCablesResistance(aRegistry, 29050, 150 * U32);
        // W 5.28
        setElectricWiresAmperage   (aRegistry, 29100, 8);
        setElectricCablesAmperage  (aRegistry, 29100, 8);
        setElectricWiresResistance (aRegistry, 29100, 528 * U240);
        setElectricCablesResistance(aRegistry, 29100, 528 * U320);
        // Netherite
        setElectricWiresAmperage   (aRegistry, 29150, 1);
        setElectricCablesAmperage  (aRegistry, 29150, 1);
        setElectricWiresResistance (aRegistry, 29150, 200 * U24);
        setElectricCablesResistance(aRegistry, 29150, 200 * U32);
        
        // Os 8.12
        setElectricWiresAmperage   (aRegistry, 29200, 4);
        setElectricCablesAmperage  (aRegistry, 29200, 4);
        setElectricWiresResistance (aRegistry, 29200, 81 * U24);
        setElectricCablesResistance(aRegistry, 29200, 81 * U32);
        // Pt 10.5
        setElectricWiresAmperage   (aRegistry, 29250, 2);
        setElectricCablesAmperage  (aRegistry, 29250, 2);
        setElectricWiresResistance (aRegistry, 29250, 105 * U24);
        setElectricCablesResistance(aRegistry, 29250, 105 * U32);
        // Osmiridium
        setElectricWiresAmperage   (aRegistry, 29300, 4);
        setElectricCablesAmperage  (aRegistry, 29300, 4);
        setElectricWiresResistance (aRegistry, 29300, 120 * U24);
        setElectricCablesResistance(aRegistry, 29300, 120 * U32);
        // SiC
        setElectricWiresAmperage   (aRegistry, 29350, 4);
        setElectricCablesAmperage  (aRegistry, 29350, 4);
        setElectricWiresResistance (aRegistry, 29350, 10000 * U24);
        setElectricCablesResistance(aRegistry, 29350, 10000 * U32);
        // Ir 4.7
        setElectricWiresAmperage   (aRegistry, 29400, 4);
        setElectricCablesAmperage  (aRegistry, 29400, 4);
        setElectricWiresResistance (aRegistry, 29400, 47 * U24);
        setElectricCablesResistance(aRegistry, 29400, 47 * U32);
        
        // Nq
        setElectricWiresAmperage   (aRegistry, 29500, 4);
        setElectricCablesAmperage  (aRegistry, 29500, 4);
        setElectricWiresResistance (aRegistry, 29500, 50 * U24);
        setElectricCablesResistance(aRegistry, 29500, 50 * U32);
        // NiobiumTitanium
        setElectricWiresAmperage   (aRegistry, 29550, 4);
        setElectricCablesAmperage  (aRegistry, 29550, 4);
        setElectricWiresResistance (aRegistry, 29550, 300 * U24);
        setElectricCablesResistance(aRegistry, 29550, 300 * U32);
        // VanadiumGallium
        setElectricWiresAmperage   (aRegistry, 29600, 4);
        setElectricCablesAmperage  (aRegistry, 29600, 4);
        setElectricWiresResistance (aRegistry, 29600, 350 * U24);
        setElectricCablesResistance(aRegistry, 29600, 350 * U32);
        // YttriumBariumCuprate
        setElectricWiresAmperage   (aRegistry, 29650, 4);
        setElectricCablesAmperage  (aRegistry, 29650, 4);
        setElectricWiresResistance (aRegistry, 29650, 400 * U24);
        setElectricCablesResistance(aRegistry, 29650, 400 * U32);
        
        // Graphene (high resistance in reality)
        setElectricWiresAmperage   (aRegistry, 29800, 1);
        setElectricWiresResistance (aRegistry, 29800, 100 * U24);
        
        // Superconductor
        setElectricWiresVoltage    (aRegistry, 29950, V[7]);
        setElectricWiresAmperage   (aRegistry, 29950, 64);
        setElectricWiresResistance (aRegistry, 29950, U1000);
    }
    
    
    @Override protected void sensorsBeforeLoad(MultiTileEntityRegistry aRegistry, MultiTileEntityBlock aMetal, MultiTileEntityBlock aMetalChips, MultiTileEntityBlock aMetalWires, MultiTileEntityBlock aMachine, MultiTileEntityBlock aWooden, MultiTileEntityBlock aBush, MultiTileEntityBlock aStone, MultiTileEntityBlock aWool, MultiTileEntityBlock aTNT, MultiTileEntityBlock aHive, MultiTileEntityBlock aUtilMetal, MultiTileEntityBlock aUtilStone, MultiTileEntityBlock aUtilWood, MultiTileEntityBlock aUtilWool, OreDictMaterial aMat, Class<? extends TileEntity> aClass) {
        /// 添加项
        aRegistry.appendAddAfter(31001, "Large Range Gibbl-O-Meter Sensor"         , "Sensors"                             , 31040, 31015, MultiTileEntityGibblometerKilo.class                    ,                 1, 16, aUtilMetal     , null, "WPW", "BXB", "WPW", 'P', OP.plateDouble.dat(MT.TinAlloy), 'W', OP.wireFine.dat(MT.RedAlloy), 'R', OD.itemRedstone, 'G', OD.blockGlassColorless, 'B', OP.bolt.dat(MT.TinAlloy), 'C', Items.comparator, 'X', OP.plateGem.dat(ANY.SiO2)                            ); CR.shapeless(aRegistry.getItem(), CR.DEF_NCC, new Object[] {aRegistry.getItem()});
        aRegistry.appendAddAfter(31020, "Large Range Geiger Counter Sensor"        , "Sensors"                             , 31041, 31015, MultiTileEntityGeigerCounterKilo.class                  ,                 1, 16, aUtilMetal     , null, "WGW", "YXY", "WPW", 'P', OP.plateDouble.dat(MT.TinAlloy), 'W', OP.wireFine.dat(MT.RedAlloy), 'R', OD.itemRedstone, 'G', OD.blockGlassColorless, 'B', OP.bolt.dat(MT.TinAlloy), 'C', Items.comparator, 'X', IL.Geiger_Counter, 'Y', OP.plateDense.dat(MT.Pb)     ); CR.shapeless(aRegistry.getItem(), CR.DEF_NCC, new Object[] {aRegistry.getItem()});
        aRegistry.appendAddAfter(31015, "Large Range Electrometer Sensor"          , "Sensors"                             , 31042, 31015, MultiTileEntityElectrometerKilo.class                   ,                 1, 16, aUtilMetal     , null, "WGW", "YXY", "WPW", 'P', OP.plateDouble.dat(MT.TinAlloy), 'W', OP.wireFine.dat(MT.RedAlloy), 'R', OD.itemRedstone, 'G', OD.blockGlassColorless, 'B', OP.bolt.dat(MT.TinAlloy), 'C', Items.comparator, 'X', IL.Electro_Meter , 'Y', OP.wireGt01.dat(MT.Au)       ); CR.shapeless(aRegistry.getItem(), CR.DEF_NCC, new Object[] {aRegistry.getItem()});
        aRegistry.appendAddAfter(31015, "Voltage-O-Meter Sensor"                   , "Sensors"                             , 31032, 31015, MultiTileEntityVoltageometer.class                      ,                 1, 16, aUtilMetal     , null, "WGW", "YXY", "WPW", 'P', OP.plateDouble.dat(MT.TinAlloy), 'W', OP.wireFine.dat(MT.RedAlloy), 'R', OD.itemRedstone, 'G', OD.blockGlassColorless, 'B', OP.bolt.dat(MT.TinAlloy), 'C', Items.comparator, 'X', IL.Electro_Meter , 'Y', OP.wireFine.dat(ANY.Cu)      ); CR.shapeless(aRegistry.getItem(), CR.DEF_NCC, new Object[] {aRegistry.getItem()});
        aRegistry.appendAddAfter(31015, "Large Range Voltage-O-Meter Sensor"       , "Sensors"                             , 31033, 31015, MultiTileEntityVoltageometerKilo.class                  ,                 1, 16, aUtilMetal     , null, "WGW", "YXY", "WPW", 'P', OP.plateDouble.dat(MT.TinAlloy), 'W', OP.wireFine.dat(MT.RedAlloy), 'R', OD.itemRedstone, 'G', OD.blockGlassColorless, 'B', OP.bolt.dat(MT.TinAlloy), 'C', Items.comparator, 'X', IL.Electro_Meter , 'Y', OP.wireFine.dat(MT.Au)       ); CR.shapeless(aRegistry.getItem(), CR.DEF_NCC, new Object[] {aRegistry.getItem()});
        aRegistry.appendAddAfter(31015, "Amperage-O-Meter Sensor"                  , "Sensors"                             , 31034, 31015, MultiTileEntityAmperageometer.class                     ,                 1, 16, aUtilMetal     , null, "WGW", "YXY", "WPW", 'P', OP.plateDouble.dat(MT.TinAlloy), 'W', OP.wireFine.dat(MT.RedAlloy), 'R', OD.itemRedstone, 'G', OD.blockGlassColorless, 'B', OP.bolt.dat(MT.TinAlloy), 'C', Items.comparator, 'X', IL.Electro_Meter , 'Y', OP.wireFine.dat(MT.Lumium)   ); CR.shapeless(aRegistry.getItem(), CR.DEF_NCC, new Object[] {aRegistry.getItem()});
        aRegistry.appendAddAfter(31019, "Large Range Tachometer Sensor"            , "Sensors"                             , 31043, 31015, MultiTileEntityTachometerKilo.class                     ,                 1, 16, aUtilMetal     , null, "WGW", "YXY", "WPW", 'P', OP.plateDouble.dat(MT.TinAlloy), 'W', OP.wireFine.dat(MT.RedAlloy), 'R', OD.itemRedstone, 'G', OD.blockGlassColorless, 'B', OP.bolt.dat(MT.TinAlloy), 'C', Items.comparator, 'X', IL.Tacho_Meter   , 'Y', OP.gearGt.dat(MT.RoseGold)   ); CR.shapeless(aRegistry.getItem(), CR.DEF_NCC, new Object[] {aRegistry.getItem()});
        aRegistry.appendAddAfter(31021, "Large Range Laser-O-Meter Sensor"         , "Sensors"                             , 31044, 31015, MultiTileEntityLaserometerKilo.class                    ,                 1, 16, aUtilMetal     , null, "WGW", "YXY", "WPW", 'P', OP.plateDouble.dat(MT.TinAlloy), 'W', OP.wireFine.dat(MT.RedAlloy), 'R', OD.itemRedstone, 'G', OD.blockGlassColorless, 'B', OP.bolt.dat(MT.TinAlloy), 'C', Items.comparator, 'X', IL.SENSORS[3]    , 'Y', OP.wireFine.dat(MT.Au)       ); CR.shapeless(aRegistry.getItem(), CR.DEF_NCC, new Object[] {aRegistry.getItem()});
    }
    @Override protected void sensorsFinishLoad(MultiTileEntityRegistry aRegistry, MultiTileEntityBlock aMetal, MultiTileEntityBlock aMetalChips, MultiTileEntityBlock aMetalWires, MultiTileEntityBlock aMachine, MultiTileEntityBlock aWooden, MultiTileEntityBlock aBush, MultiTileEntityBlock aStone, MultiTileEntityBlock aWool, MultiTileEntityBlock aTNT, MultiTileEntityBlock aHive, MultiTileEntityBlock aUtilMetal, MultiTileEntityBlock aUtilStone, MultiTileEntityBlock aUtilWood, MultiTileEntityBlock aUtilWool, OreDictMaterial aMat, Class<? extends TileEntity> aClass) {
        /// 添加项
        aRegistry.add(RegType.GTCH, "Flow-O-Meter Sensor"                      , "Sensors"                              , 31030, 31015, MultiTileEntityFlowometer.class                      ,                 1, 16, aUtilMetal     , null, "WYW", "ZXZ", "WPW", 'P', OP.plateDouble.dat(MT.TinAlloy), 'W', OP.wireFine.dat(MT.RedAlloy), 'R', OD.itemRedstone, 'G', OD.blockGlassColorless, 'B', OP.bolt.dat(MT.TinAlloy), 'C', Items.comparator, 'X', OD.pressurePlateGold, 'Y', OP.rotor.dat(MT.TinAlloy), 'Z', OP.ring.dat(ANY.Rubber)); CR.shapeless(aRegistry.getItem(), CR.DEF_NCC, new Object[] {aRegistry.getItem()});
        aRegistry.add(RegType.GTCH, "Bucket Flow-O-Meter Sensor"               , "Sensors"                              , 31031, 31015, MultiTileEntityFlowometerBucket.class                ,                 1, 16, aUtilMetal     , null, "WYW", "ZXZ", "WPW", 'P', OP.plateDouble.dat(MT.TinAlloy), 'W', OP.wireFine.dat(MT.RedAlloy), 'R', OD.itemRedstone, 'G', OD.blockGlassColorless, 'B', OP.bolt.dat(MT.TinAlloy), 'C', Items.comparator, 'X', OD.pressurePlateIron, 'Y', OP.rotor.dat(MT.TinAlloy), 'Z', OP.ring.dat(ANY.Rubber)); CR.shapeless(aRegistry.getItem(), CR.DEF_NCC, new Object[] {aRegistry.getItem()});
    }
    
    
    
    @Override protected void miscFinishLoad(MultiTileEntityRegistry aRegistry, MultiTileEntityBlock aMetal, MultiTileEntityBlock aMetalChips, MultiTileEntityBlock aMetalWires, MultiTileEntityBlock aMachine, MultiTileEntityBlock aWooden, MultiTileEntityBlock aBush, MultiTileEntityBlock aStone, MultiTileEntityBlock aWool, MultiTileEntityBlock aTNT, MultiTileEntityBlock aHive, MultiTileEntityBlock aUtilMetal, MultiTileEntityBlock aUtilStone, MultiTileEntityBlock aUtilWood, MultiTileEntityBlock aUtilWool, OreDictMaterial aMat, Class<? extends TileEntity> aClass) {
        /// 添加项
        // 新的基岩矿 ID: 23300-23399
        aMat = MT.NULL                ; MultiTileEntityDeposit.addDeposit(23399, 32764, F,      1000, 0  ,   8,    128, aRegistry, aStone, MultiTileEntityDeposit.class       , aMat);
        aMat = MT.Diamond             ; MultiTileEntityDeposit.addDeposit(23300, 32764, F,   4000000, 3  ,  64,  65536, aRegistry, aStone, MultiTileEntityDeposit.class       , aMat);
        aMat = MT.OREMATS.Tungstate   ; MultiTileEntityDeposit.addDeposit(23301, 32764, F, 256000000, 5  , 256, 262144, aRegistry, aStone, MultiTileEntityDeposit.class       , aMat);
        aMat = MT.OREMATS.Ferberite   ; MultiTileEntityDeposit.addDeposit(23302, 32764, F, 256000000, 5  , 256, 262144, aRegistry, aStone, MultiTileEntityDeposit.class       , aMat);
        aMat = MT.OREMATS.Wolframite  ; MultiTileEntityDeposit.addDeposit(23303, 32764, F, 256000000, 5  , 256, 262144, aRegistry, aStone, MultiTileEntityDeposit.class       , aMat);
        aMat = MT.OREMATS.Stolzite    ; MultiTileEntityDeposit.addDeposit(23304, 32764, F, 256000000, 5  , 256, 262144, aRegistry, aStone, MultiTileEntityDeposit.class       , aMat);
        aMat = MT.OREMATS.Scheelite   ; MultiTileEntityDeposit.addDeposit(23305, 32764, F, 256000000, 5  , 256, 262144, aRegistry, aStone, MultiTileEntityDeposit.class       , aMat);
        aMat = MT.OREMATS.Huebnerite  ; MultiTileEntityDeposit.addDeposit(23306, 32764, F, 256000000, 5  , 256, 262144, aRegistry, aStone, MultiTileEntityDeposit.class       , aMat);
        aMat = MT.OREMATS.Russellite  ; MultiTileEntityDeposit.addDeposit(23307, 32764, F, 256000000, 5  , 256, 262144, aRegistry, aStone, MultiTileEntityDeposit.class       , aMat);
        aMat = MT.OREMATS.Pinalite    ; MultiTileEntityDeposit.addDeposit(23308, 32764, F, 256000000, 5  , 256, 262144, aRegistry, aStone, MultiTileEntityDeposit.class       , aMat);
        aMat = MT.OREMATS.Uraninite   ; MultiTileEntityDeposit.addDeposit(23309, 32764, F, 128000000, 4  , 128, 262144, aRegistry, aStone, MultiTileEntityDeposit.class       , aMat);
        aMat = MT.OREMATS.Pitchblende ; MultiTileEntityDeposit.addDeposit(23310, 32764, F, 256000000, 4  , 128, 262144, aRegistry, aStone, MultiTileEntityDeposit.class       , aMat);
        aMat = MT.Au                  ; MultiTileEntityDeposit.addDeposit(23311, 32764, F,   4000000, 2  ,  32,  16384, aRegistry, aStone, MultiTileEntityDeposit.class       , aMat);
        aMat = MT.OREMATS.Cooperite   ; MultiTileEntityDeposit.addDeposit(23313, 32764, F, 512000000, 5  , 256, 524288, aRegistry, aStone, MultiTileEntityDeposit.class       , aMat);
        aMat = MT.Cu                  ; MultiTileEntityDeposit.addDeposit(23314, 32764, F,   8000000, 0  ,   8,   8192, aRegistry, aStone, MultiTileEntityDeposit.class       , aMat);
        aMat = MT.Monazite            ; MultiTileEntityDeposit.addDeposit(23315, 32764, F,  32000000, 2  ,  32,  32768, aRegistry, aStone, MultiTileEntityDeposit.class       , aMat); // Not Sure
        aMat = MT.OREMATS.Powellite   ; MultiTileEntityDeposit.addDeposit(23316, 32764, F,  32000000, 2  ,  32,  32768, aRegistry, aStone, MultiTileEntityDeposit.class       , aMat); // Not Sure
        aMat = MT.OREMATS.Bastnasite  ; MultiTileEntityDeposit.addDeposit(23317, 32764, F,  32000000, 2  ,  32,  32768, aRegistry, aStone, MultiTileEntityDeposit.class       , aMat); // Not Sure
        aMat = MT.OREMATS.Arsenopyrite; MultiTileEntityDeposit.addDeposit(23318, 32764, F,  32000000, 2  ,  32,  32768, aRegistry, aStone, MultiTileEntityDeposit.class       , aMat); // Not Sure
        aMat = MT.Redstone            ; MultiTileEntityDeposit.addDeposit(23319, 32764, F,  32000000, 2  ,  16,   8192, aRegistry, aStone, MultiTileEntityDeposit.class       , aMat);
        aMat = MT.V2O5                ; MultiTileEntityDeposit.addDeposit(23320, 32764, F,  32000000, 2  ,  32,  32768, aRegistry, aStone, MultiTileEntityDeposit.class       , aMat);
        aMat = MT.OREMATS.Galena      ; MultiTileEntityDeposit.addDeposit(23321, 32764, F,   8000000, 0  ,   8,   8192, aRegistry, aStone, MultiTileEntityDeposit.class       , aMat);
        aMat = MT.Coal                ; MultiTileEntityDeposit.addDeposit(23322, 32764, F,   4000000, 0  ,   8,   4096, aRegistry, aStone, MultiTileEntityDeposit.class       , aMat);
        aMat = MT.Graphite            ; MultiTileEntityDeposit.addDeposit(23323, 32764, F,   8000000, 1  ,  16,  16384, aRegistry, aStone, MultiTileEntityDeposit.class       , aMat);
        aMat = MT.OREMATS.Stibnite    ; MultiTileEntityDeposit.addDeposit(23324, 32764, F,  32000000, 2  ,  32,  32768, aRegistry, aStone, MultiTileEntityDeposit.class       , aMat); // Not Sure
        aMat = MT.Fe2O3               ; MultiTileEntityDeposit.addDeposit(23325, 32764, F,  16000000, 1  ,  16,  16384, aRegistry, aStone, MultiTileEntityDeposit.class       , aMat);
        aMat = MT.OREMATS.Sphalerite  ; MultiTileEntityDeposit.addDeposit(23326, 32764, F,  16000000, 1  ,  16,  16384, aRegistry, aStone, MultiTileEntityDeposit.class       , aMat);
        aMat = MT.OREMATS.Smithsonite ; MultiTileEntityDeposit.addDeposit(23327, 32764, F,  32000000, 2  ,  32,  32768, aRegistry, aStone, MultiTileEntityDeposit.class       , aMat); // Not Sure
        aMat = MT.OREMATS.Pentlandite ; MultiTileEntityDeposit.addDeposit(23328, 32764, F,  32000000, 2  ,  32,  32768, aRegistry, aStone, MultiTileEntityDeposit.class       , aMat); // Not Sure
        aMat = MT.Niter               ; MultiTileEntityDeposit.addDeposit(23329, 32764, F,  16000000, 1  ,  16,  16384, aRegistry, aStone, MultiTileEntityDeposit.class       , aMat);
        aMat = MT.OREMATS.Bauxite     ; MultiTileEntityDeposit.addDeposit(23330, 32764, F, 128000000, 2  ,  32,  65536, aRegistry, aStone, MultiTileEntityDeposit.class       , aMat);
        aMat = MT.OREMATS.Cassiterite ; MultiTileEntityDeposit.addDeposit(23331, 32764, F,   8000000, 0  ,   8,   8192, aRegistry, aStone, MultiTileEntityDeposit.class       , aMat);
        aMat = MT.OREMATS.Chalcopyrite; MultiTileEntityDeposit.addDeposit(23332, 32764, F,   8000000, 0  ,   8,   8192, aRegistry, aStone, MultiTileEntityDeposit.class       , aMat);
        
        aMat = MT.Diamond             ; MultiTileEntityDeposit.addDeposit(23350, 32764, T,  12000000, 3+1,  64,  65536, aRegistry, aStone, MultiTileEntityBedrockDeposit.class, aMat);
        aMat = MT.OREMATS.Tungstate   ; MultiTileEntityDeposit.addDeposit(23351, 32764, T, 384000000, 5+1, 256, 262144, aRegistry, aStone, MultiTileEntityBedrockDeposit.class, aMat);
        aMat = MT.OREMATS.Ferberite   ; MultiTileEntityDeposit.addDeposit(23352, 32764, T, 384000000, 5+1, 256, 262144, aRegistry, aStone, MultiTileEntityBedrockDeposit.class, aMat);
        aMat = MT.OREMATS.Wolframite  ; MultiTileEntityDeposit.addDeposit(23353, 32764, T, 384000000, 5+1, 256, 262144, aRegistry, aStone, MultiTileEntityBedrockDeposit.class, aMat);
        aMat = MT.OREMATS.Stolzite    ; MultiTileEntityDeposit.addDeposit(23354, 32764, T, 384000000, 5+1, 256, 262144, aRegistry, aStone, MultiTileEntityBedrockDeposit.class, aMat);
        aMat = MT.OREMATS.Scheelite   ; MultiTileEntityDeposit.addDeposit(23355, 32764, T, 384000000, 5+1, 256, 262144, aRegistry, aStone, MultiTileEntityBedrockDeposit.class, aMat);
        aMat = MT.OREMATS.Huebnerite  ; MultiTileEntityDeposit.addDeposit(23356, 32764, T, 384000000, 5+1, 256, 262144, aRegistry, aStone, MultiTileEntityBedrockDeposit.class, aMat);
        aMat = MT.OREMATS.Russellite  ; MultiTileEntityDeposit.addDeposit(23357, 32764, T, 384000000, 5+1, 256, 262144, aRegistry, aStone, MultiTileEntityBedrockDeposit.class, aMat);
        aMat = MT.OREMATS.Pinalite    ; MultiTileEntityDeposit.addDeposit(23358, 32764, T, 384000000, 5+1, 256, 262144, aRegistry, aStone, MultiTileEntityBedrockDeposit.class, aMat);
        aMat = MT.OREMATS.Uraninite   ; MultiTileEntityDeposit.addDeposit(23359, 32764, T, 192000000, 4+1, 128, 262144, aRegistry, aStone, MultiTileEntityBedrockDeposit.class, aMat);
        aMat = MT.OREMATS.Pitchblende ; MultiTileEntityDeposit.addDeposit(23360, 32764, T, 384000000, 4+1, 128, 262144, aRegistry, aStone, MultiTileEntityBedrockDeposit.class, aMat);
        aMat = MT.Au                  ; MultiTileEntityDeposit.addDeposit(23361, 32764, T,   8000000, 2+1,  32,  16384, aRegistry, aStone, MultiTileEntityBedrockDeposit.class, aMat);
        aMat = MT.OREMATS.Cooperite   ; MultiTileEntityDeposit.addDeposit(23363, 32764, T, 768000000, 5+1, 256, 524288, aRegistry, aStone, MultiTileEntityBedrockDeposit.class, aMat);
        aMat = MT.Cu                  ; MultiTileEntityDeposit.addDeposit(23364, 32764, T,  12000000, 0+1,   8,   8192, aRegistry, aStone, MultiTileEntityBedrockDeposit.class, aMat);
        aMat = MT.Monazite            ; MultiTileEntityDeposit.addDeposit(23365, 32764, T,  48000000, 2+1,  32,  32768, aRegistry, aStone, MultiTileEntityBedrockDeposit.class, aMat); // Not Sure
        aMat = MT.OREMATS.Powellite   ; MultiTileEntityDeposit.addDeposit(23366, 32764, T,  48000000, 2+1,  32,  32768, aRegistry, aStone, MultiTileEntityBedrockDeposit.class, aMat); // Not Sure
        aMat = MT.OREMATS.Bastnasite  ; MultiTileEntityDeposit.addDeposit(23367, 32764, T,  48000000, 2+1,  32,  32768, aRegistry, aStone, MultiTileEntityBedrockDeposit.class, aMat); // Not Sure
        aMat = MT.OREMATS.Arsenopyrite; MultiTileEntityDeposit.addDeposit(23368, 32764, T,  48000000, 2+1,  32,  32768, aRegistry, aStone, MultiTileEntityBedrockDeposit.class, aMat); // Not Sure
        aMat = MT.Redstone            ; MultiTileEntityDeposit.addDeposit(23369, 32764, T,  48000000, 2+1,  16,   8192, aRegistry, aStone, MultiTileEntityBedrockDeposit.class, aMat);
        aMat = MT.V2O5                ; MultiTileEntityDeposit.addDeposit(23370, 32764, T,  48000000, 2+1,  32,  32768, aRegistry, aStone, MultiTileEntityBedrockDeposit.class, aMat);
        aMat = MT.OREMATS.Galena      ; MultiTileEntityDeposit.addDeposit(23371, 32764, T,  12000000, 0+1,   8,   8192, aRegistry, aStone, MultiTileEntityBedrockDeposit.class, aMat);
        aMat = MT.Coal                ; MultiTileEntityDeposit.addDeposit(23372, 32764, T,   6000000, 0+1,   8,   4096, aRegistry, aStone, MultiTileEntityBedrockDeposit.class, aMat);
        aMat = MT.Graphite            ; MultiTileEntityDeposit.addDeposit(23373, 32764, T,  12000000, 1+1,  16,  16384, aRegistry, aStone, MultiTileEntityBedrockDeposit.class, aMat);
        aMat = MT.OREMATS.Stibnite    ; MultiTileEntityDeposit.addDeposit(23374, 32764, T,  48000000, 2+1,  32,  32768, aRegistry, aStone, MultiTileEntityBedrockDeposit.class, aMat); // Not Sure
        aMat = MT.Fe2O3               ; MultiTileEntityDeposit.addDeposit(23375, 32764, T,  24000000, 1+1,  16,  16384, aRegistry, aStone, MultiTileEntityBedrockDeposit.class, aMat);
        aMat = MT.OREMATS.Sphalerite  ; MultiTileEntityDeposit.addDeposit(23376, 32764, T,  24000000, 1+1,  16,  16384, aRegistry, aStone, MultiTileEntityBedrockDeposit.class, aMat);
        aMat = MT.OREMATS.Smithsonite ; MultiTileEntityDeposit.addDeposit(23377, 32764, T,  48000000, 2+1,  32,  32768, aRegistry, aStone, MultiTileEntityBedrockDeposit.class, aMat); // Not Sure
        aMat = MT.OREMATS.Pentlandite ; MultiTileEntityDeposit.addDeposit(23378, 32764, T,  48000000, 2+1,  32,  32768, aRegistry, aStone, MultiTileEntityBedrockDeposit.class, aMat); // Not Sure
        aMat = MT.Niter               ; MultiTileEntityDeposit.addDeposit(23379, 32764, T,  24000000, 1+1,  16,  16384, aRegistry, aStone, MultiTileEntityBedrockDeposit.class, aMat);
        aMat = MT.OREMATS.Bauxite     ; MultiTileEntityDeposit.addDeposit(23380, 32764, T, 192000000, 2+1,  32,  65536, aRegistry, aStone, MultiTileEntityBedrockDeposit.class, aMat);
        aMat = MT.OREMATS.Cassiterite ; MultiTileEntityDeposit.addDeposit(23381, 32764, T,  24000000, 0+1,   8,   8192, aRegistry, aStone, MultiTileEntityBedrockDeposit.class, aMat);
        aMat = MT.OREMATS.Chalcopyrite; MultiTileEntityDeposit.addDeposit(23382, 32764, T,  24000000, 0+1,   8,   8192, aRegistry, aStone, MultiTileEntityBedrockDeposit.class, aMat);
    }
    
    
    
    /* GT6U stuff */
    @Override protected void crucibleBeforeLoadGT6U(MultiTileEntityRegistry aRegistry, MultiTileEntityBlock aMetal, MultiTileEntityBlock aMetalChips, MultiTileEntityBlock aMetalWires, MultiTileEntityBlock aMachine, MultiTileEntityBlock aWooden, MultiTileEntityBlock aBush, MultiTileEntityBlock aStone, MultiTileEntityBlock aWool, MultiTileEntityBlock aTNT, MultiTileEntityBlock aHive, MultiTileEntityBlock aUtilMetal, MultiTileEntityBlock aUtilStone, MultiTileEntityBlock aUtilWood, MultiTileEntityBlock aUtilWool, OreDictMaterial aMat, Class<? extends TileEntity> aClass) {
        if (!DATA_GTCH.enableGT6U) return;
        
        /// 添加项
        // 碳化铌钛坩埚 // MARK ID 1044 -> 1045
        aClass = MultiTileEntitySmeltery.class;
        aMat = MT_CH.Nb2Ti3C5;          aRegistry.appendAddBefore(1043, RegType.GT6U, "Smelting Crucible ("             +aMat.getLocal()+")", "Smelting Crucibles"                  ,  1045,  1022, aClass, aMat.mToolQuality, 16, aMetal           , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   9.0F, NBT_RESISTANCE,   9.0F, NBT_RECIPEMAP, RM.CrucibleAlloying , NBT_ENERGY_ACCEPTED, TD.Energy.HU, NBT_ACIDPROOF, F), "PhP", "PwP", "PPP", 'P', OP.plate.dat(aMat));
        // 碳化铌钛浇筑口 // MARK ID 1795 -> 1745
        aClass = MultiTileEntityFaucet.class;
        aMat = MT_CH.Nb2Ti3C5;          aRegistry.appendAddBefore(1743, RegType.GT6U, "Crucible Faucet ("               +aMat.getLocal()+")", "Crucibles Faucets"                   ,  1745,  1722, aClass, aMat.mToolQuality, 16, aUtilMetal       , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   1.0F, NBT_RESISTANCE,   6.0F, NBT_ACIDPROOF, F), "h y", "P P", " P ", 'P', OP.plate.dat(aMat));
        // 碳化铌钛模具 // MARK ID 1094 -> 1095
        aClass = MultiTileEntityMold.class;
        aMat = MT_CH.Nb2Ti3C5;          aRegistry.appendAddBefore(1093, RegType.GT6U, "Mold ("                          +aMat.getLocal()+")", "Molds"                               ,  1095,  1072, aClass, aMat.mToolQuality, 16, aUtilMetal       , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   1.0F, NBT_RESISTANCE,   6.0F, NBT_ACIDPROOF, F), "h y", "P P", "PPP", 'P', OP.plate.dat(aMat));
        // 碳化铌钛浇筑盆 // MARK ID 1794 -> 1795
        aClass = MultiTileEntityBasin.class;
        aMat = MT_CH.Nb2Ti3C5;          aRegistry.appendAddBefore(1793, RegType.GT6U, "Basin ("                         +aMat.getLocal()+")", "Molds"                               ,  1795,  1072, aClass, aMat.mToolQuality, 16, aMetal           , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   9.0F, NBT_RESISTANCE,   9.0F, NBT_ACIDPROOF, F), "PhP", "PyP", " P ", 'P', OP.plate.dat(aMat));
        // 碳化铌钛十字 // MARK ID  1894 -> 1895
        aClass = MultiTileEntityCrossing.class;
        aMat = MT_CH.Nb2Ti3C5;          aRegistry.appendAddBefore(1893, RegType.GT6U, "Crucible Crossing ("             +aMat.getLocal()+")", "Molds"                               ,  1895,  1072, aClass, aMat.mToolQuality, 16, aMetal           , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   9.0F, NBT_RESISTANCE,   9.0F, NBT_ACIDPROOF, F), "hPy", "PPP", " P ", 'P', OP.plate.dat(aMat));
    }
    
    
    @Override protected void unsorted1BeforeLoadGT6U(MultiTileEntityRegistry aRegistry, MultiTileEntityBlock aMetal, MultiTileEntityBlock aMetalChips, MultiTileEntityBlock aMetalWires, MultiTileEntityBlock aMachine, MultiTileEntityBlock aWooden, MultiTileEntityBlock aBush, MultiTileEntityBlock aStone, MultiTileEntityBlock aWool, MultiTileEntityBlock aTNT, MultiTileEntityBlock aHive, MultiTileEntityBlock aUtilMetal, MultiTileEntityBlock aUtilStone, MultiTileEntityBlock aUtilWood, MultiTileEntityBlock aUtilWool, OreDictMaterial aMat, Class<? extends TileEntity> aClass) {
        if (!DATA_GTCH.enableGT6U) return;
        
        /// 添加项
        // 碳化铌钛燃烧室 // MARK ID + 1 -> +10; DOUBLE OUTPUT
        aClass = MultiTileEntityGeneratorMetal.class;
        aMat = MT_CH.Nb2Ti3C5;          aRegistry.appendAddBefore(1109, RegType.GT6U, "Burning Box (Solid, "            +aMat.getLocal()+")", "Burning Boxes"                       ,  1120,  1104, aClass, aMat.mToolQuality, 16, aMachine     , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,  12.5F, NBT_RESISTANCE,  12.5F, NBT_FUELMAP, FM.Furnace,    NBT_EFFICIENCY, 10000, NBT_OUTPUT, 392, NBT_ENERGY_EMITTED, TD.Energy.HU), "PCP", "PwP", "BBB", 'B', Blocks.brick_block, 'P', OP.plate.dat(aMat), 'C', OP.plateDouble.dat(ANY.Cu));
        aMat = MT_CH.Nb2Ti3C5;          aRegistry.appendAddBefore(1159, RegType.GT6U, "Dense Burning Box (Solid, "      +aMat.getLocal()+")", "Burning Boxes"                       ,  1170,  1104, aClass, aMat.mToolQuality, 16, aMachine     , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,  12.5F, NBT_RESISTANCE,  12.5F, NBT_FUELMAP, FM.Furnace,    NBT_EFFICIENCY, 10000, NBT_OUTPUT,1536, NBT_ENERGY_EMITTED, TD.Energy.HU), "PCP", "PwP", "BBB", 'B', Blocks.brick_block, 'P', OP.plateQuintuple.dat(aMat), 'C', OP.plateDense.dat(ANY.Cu));
        aClass = MultiTileEntityGeneratorLiquid.class;
        aMat = MT_CH.Nb2Ti3C5;          aRegistry.appendAddBefore(1409, RegType.GT6U, "Burning Box (Liquid, "           +aMat.getLocal()+")", "Burning Boxes"                       ,  1420,  1104, aClass, aMat.mToolQuality, 16, aMachine     , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,  12.5F, NBT_RESISTANCE,  12.5F, NBT_FUELMAP, FM.Burn,       NBT_EFFICIENCY, 10000, NBT_OUTPUT, 392, NBT_ENERGY_EMITTED, TD.Energy.HU), "PCP", "IwI", "BBB", 'B', Blocks.brick_block, 'P', OP.plate.dat(aMat), 'I', OP.pipeSmall.dat(aMat), 'C', OP.plateDouble.dat(ANY.Cu));
        aMat = MT_CH.Nb2Ti3C5;          aRegistry.appendAddBefore(1459, RegType.GT6U, "Dense Burning Box (Liquid, "     +aMat.getLocal()+")", "Burning Boxes"                       ,  1470,  1104, aClass, aMat.mToolQuality, 16, aMachine     , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,  12.5F, NBT_RESISTANCE,  12.5F, NBT_FUELMAP, FM.Burn,       NBT_EFFICIENCY, 10000, NBT_OUTPUT,1536, NBT_ENERGY_EMITTED, TD.Energy.HU), "PCP", "IwI", "BBB", 'B', Blocks.brick_block, 'P', OP.plateQuintuple.dat(aMat), 'I', OP.pipeLarge.dat(aMat), 'C', OP.plateDense.dat(ANY.Cu));
        aClass = MultiTileEntityGeneratorGas.class;
        aMat = MT_CH.Nb2Ti3C5;          aRegistry.appendAddBefore(1609, RegType.GT6U, "Burning Box (Gas, "              +aMat.getLocal()+")", "Burning Boxes"                       ,  1620,  1104, aClass, aMat.mToolQuality, 16, aMachine     , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,  12.5F, NBT_RESISTANCE,  12.5F, NBT_FUELMAP, FM.Burn,       NBT_EFFICIENCY, 10000, NBT_OUTPUT, 392, NBT_ENERGY_EMITTED, TD.Energy.HU), "PCP", "BwB", "BIB", 'B', Blocks.brick_block, 'P', OP.plate.dat(aMat), 'I', OP.pipeSmall.dat(aMat), 'C', OP.plateDouble.dat(ANY.Cu));
        aMat = MT_CH.Nb2Ti3C5;          aRegistry.appendAddBefore(1659, RegType.GT6U, "Dense Burning Box (Gas, "        +aMat.getLocal()+")", "Burning Boxes"                       ,  1670,  1104, aClass, aMat.mToolQuality, 16, aMachine     , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,  12.5F, NBT_RESISTANCE,  12.5F, NBT_FUELMAP, FM.Burn,       NBT_EFFICIENCY, 10000, NBT_OUTPUT,1536, NBT_ENERGY_EMITTED, TD.Energy.HU), "PCP", "BwB", "BIB", 'B', Blocks.brick_block, 'P', OP.plateQuintuple.dat(aMat), 'I', OP.pipeLarge.dat(aMat), 'C', OP.plateDense.dat(ANY.Cu));
        aClass = MultiTileEntityGeneratorFluidBed.class;
        aMat = MT_CH.Nb2Ti3C5;          aRegistry.appendAddBefore(9009, RegType.GT6U, "Fluidized Bed Burning Box ("     +aMat.getLocal()+")", "Burning Boxes"                       ,  9020,  1104, aClass, aMat.mToolQuality, 16, aMachine     , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,  12.5F, NBT_RESISTANCE,  12.5F, NBT_FUELMAP, FM.FluidBed,   NBT_EFFICIENCY, 10000, NBT_OUTPUT, 768, NBT_ENERGY_EMITTED, TD.Energy.HU), "PCP", "UwU", "BXB", 'B', Blocks.brick_block, 'U', OP.plateCurved.dat(aMat), 'X', OP.rotor.dat(aMat), 'P', OP.plate.dat(aMat), 'C', OP.plateDouble.dat(ANY.Cu));
        aMat = MT_CH.Nb2Ti3C5;          aRegistry.appendAddBefore(9059, RegType.GT6U, "Dense Fluidized Bed Burning Box ("+aMat.getLocal()+")","Burning Boxes"                       ,  9070,  1104, aClass, aMat.mToolQuality, 16, aMachine     , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,  12.5F, NBT_RESISTANCE,  12.5F, NBT_FUELMAP, FM.FluidBed,   NBT_EFFICIENCY, 10000, NBT_OUTPUT,3072, NBT_ENERGY_EMITTED, TD.Energy.HU), "PCP", "UwU", "BXB", 'B', Blocks.brick_block, 'U', OP.plateCurved.dat(aMat), 'X', OP.rotor.dat(aMat), 'P', OP.plateQuintuple.dat(aMat), 'C', OP.plateDense.dat(ANY.Cu));
        
        /// ID: 9140-9149 and 9190-9199 for Diesel Engine; 9160-9169 and 9180-9189 for Small Gas Turbine; (9150-9159 for Dense Heat Exchanger)
        // 钨燃油引擎 // MARK ID 9199 -> 9190; 效率输出合成和 GTCH 统一
        aClass = MultiTileEntityMotorLiquid.class;
        aMat = ANY.W;                   aRegistry.appendAddAfter(9198, RegType.GT6U, "Diesel Engine ("                  +aMat.getLocal()+")", "Engines"                             ,  9190,  1304, aClass, aMat.mToolQuality, 16, aMachine     , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_FUELMAP, FM.Engine, NBT_EFFICIENCY,  3500, NBT_OUTPUT,  256, NBT_PREHEAT_ENERGY,  256*1000, NBT_PREHEAT_RATE,  256*4, NBT_PREHEAT_COST,  256/16, NBT_COOLDOWN_RATE,  256, NBT_ENERGY_EMITTED, TD.Energy.RU), "PLP", "SMS", "GOC", 'M', OP.casingMachineDense.dat(aMat), 'O', OP.pipeQuadruple.dat(aMat), 'P', OP.rotor.dat(aMat), 'S', OP.stickLong.dat(aMat), 'G', OP.gearGt.dat(aMat), 'C', OP.gearGtSmall.dat(aMat), 'L', OD.itemLubricant);
        // 小燃气涡轮 // MARK ID CHANGED
        aClass = MultiTileEntityMotorGas.class;
        aMat = MT.Bronze;               aRegistry.appendAddAfter(9199, RegType.GT6U, "Small Gas Turbine ("              +aMat.getLocal()+")", "Engines"                             ,  9167,  1304, aClass, aMat.mToolQuality, 16, aMachine     , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_FUELMAP, FM.Gas,    NBT_EFFICIENCY,  2750, NBT_OUTPUT,   32, NBT_PREHEAT_ENERGY,   32*4000, NBT_PREHEAT_RATE,   32*1, NBT_PREHEAT_COST,   32/16, NBT_COOLDOWN_RATE,   32, NBT_ENERGY_EMITTED, TD.Energy.RU), "PLP", "SMS", "GOC", 'M', OP.casingMachineDense.dat(aMat), 'O', OP.pipeHuge.dat(aMat)     , 'P', OP.rotor.dat(aMat), 'S', OP.stickLong.dat(aMat), 'G', OP.gearGt.dat(aMat), 'C', OP.gearGtSmall.dat(aMat), 'L', OD.itemLubricant);
        aMat = MT.ArsenicCopper;        aRegistry.appendAddAfter(9199, RegType.GT6U, "Small Gas Turbine ("              +aMat.getLocal()+")", "Engines"                             ,  9166,  1304, aClass, aMat.mToolQuality, 16, aMachine     , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_FUELMAP, FM.Gas,    NBT_EFFICIENCY,  2875, NBT_OUTPUT,   32, NBT_PREHEAT_ENERGY,   32*4000, NBT_PREHEAT_RATE,   32*1, NBT_PREHEAT_COST,   32/16, NBT_COOLDOWN_RATE,   32, NBT_ENERGY_EMITTED, TD.Energy.RU), "PLP", "SMS", "GOC", 'M', OP.casingMachineDense.dat(aMat), 'O', OP.pipeHuge.dat(MT.Bronze), 'P', OP.rotor.dat(aMat), 'S', OP.stickLong.dat(aMat), 'G', OP.gearGt.dat(aMat), 'C', OP.gearGtSmall.dat(aMat), 'L', OD.itemLubricant);
        aMat = MT.ArsenicBronze;        aRegistry.appendAddAfter(9199, RegType.GT6U, "Small Gas Turbine ("              +aMat.getLocal()+")", "Engines"                             ,  9165,  1304, aClass, aMat.mToolQuality, 16, aMachine     , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_FUELMAP, FM.Gas,    NBT_EFFICIENCY,  3000, NBT_OUTPUT,   48, NBT_PREHEAT_ENERGY,   48*4000, NBT_PREHEAT_RATE,   48*1, NBT_PREHEAT_COST,   48/16, NBT_COOLDOWN_RATE,   48, NBT_ENERGY_EMITTED, TD.Energy.RU), "PLP", "SMS", "GOC", 'M', OP.casingMachineDense.dat(aMat), 'O', OP.pipeHuge.dat(MT.Bronze), 'P', OP.rotor.dat(aMat), 'S', OP.stickLong.dat(aMat), 'G', OP.gearGt.dat(aMat), 'C', OP.gearGtSmall.dat(aMat), 'L', OD.itemLubricant);
        aMat = ANY.Steel;               aRegistry.appendAddAfter(9199, RegType.GT6U, "Small Gas Turbine ("              +aMat.getLocal()+")", "Engines"                             ,  9168,  1304, aClass, aMat.mToolQuality, 16, aMachine     , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_FUELMAP, FM.Gas,    NBT_EFFICIENCY,  2500, NBT_OUTPUT,   64, NBT_PREHEAT_ENERGY,   64*4000, NBT_PREHEAT_RATE,   64*1, NBT_PREHEAT_COST,   64/16, NBT_COOLDOWN_RATE,   64, NBT_ENERGY_EMITTED, TD.Energy.RU), "PLP", "SMS", "GOC", 'M', OP.casingMachineDense.dat(aMat), 'O', OP.pipeHuge.dat(aMat)     , 'P', OP.rotor.dat(aMat), 'S', OP.stickLong.dat(aMat), 'G', OP.gearGt.dat(aMat), 'C', OP.gearGtSmall.dat(aMat), 'L', OD.itemLubricant);
        aMat = MT.Invar;                aRegistry.appendAddAfter(9199, RegType.GT6U, "Small Gas Turbine ("              +aMat.getLocal()+")", "Engines"                             ,  9169,  1304, aClass, aMat.mToolQuality, 16, aMachine     , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_FUELMAP, FM.Gas,    NBT_EFFICIENCY,  3500, NBT_OUTPUT,  128, NBT_PREHEAT_ENERGY,  128*4000, NBT_PREHEAT_RATE,  128*1, NBT_PREHEAT_COST,  128/16, NBT_COOLDOWN_RATE,  128, NBT_ENERGY_EMITTED, TD.Energy.RU), "PLP", "SMS", "GOC", 'M', OP.casingMachineDense.dat(aMat), 'O', OP.pipeHuge.dat(aMat)     , 'P', OP.rotor.dat(aMat), 'S', OP.stickLong.dat(aMat), 'G', OP.gearGt.dat(aMat), 'C', OP.gearGtSmall.dat(aMat), 'L', OD.itemLubricant);
        aMat = MT.Ti;                   aRegistry.appendAddAfter(9199, RegType.GT6U, "Small Gas Turbine ("              +aMat.getLocal()+")", "Engines"                             ,  9187,  1304, aClass, aMat.mToolQuality, 16, aMachine     , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_FUELMAP, FM.Gas,    NBT_EFFICIENCY,  2750, NBT_OUTPUT,  256, NBT_PREHEAT_ENERGY,  256*4000, NBT_PREHEAT_RATE,  256*1, NBT_PREHEAT_COST,  256/16, NBT_COOLDOWN_RATE,  256, NBT_ENERGY_EMITTED, TD.Energy.RU), "PLP", "SMS", "GOC", 'M', OP.casingMachineDense.dat(aMat), 'O', OP.pipeHuge.dat(aMat)     , 'P', OP.rotor.dat(aMat), 'S', OP.stickLong.dat(aMat), 'G', OP.gearGt.dat(aMat), 'C', OP.gearGtSmall.dat(aMat), 'L', OD.itemLubricant);
        aMat = MT.TungstenSteel;        aRegistry.appendAddAfter(9199, RegType.GT6U, "Small Gas Turbine ("              +aMat.getLocal()+")", "Engines"                             ,  9188,  1304, aClass, aMat.mToolQuality, 16, aMachine     , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_FUELMAP, FM.Gas,    NBT_EFFICIENCY,  3000, NBT_OUTPUT,  512, NBT_PREHEAT_ENERGY,  512*4000, NBT_PREHEAT_RATE,  512*1, NBT_PREHEAT_COST,  512/16, NBT_COOLDOWN_RATE,  512, NBT_ENERGY_EMITTED, TD.Energy.RU), "PLP", "SMS", "GOC", 'M', OP.casingMachineDense.dat(aMat), 'O', OP.pipeHuge.dat(aMat)     , 'P', OP.rotor.dat(aMat), 'S', OP.stickLong.dat(aMat), 'G', OP.gearGt.dat(aMat), 'C', OP.gearGtSmall.dat(aMat), 'L', OD.itemLubricant);
        aMat = ANY.W;                   aRegistry.appendAddAfter(9199, RegType.GT6U, "Small Gas Turbine ("              +aMat.getLocal()+")", "Engines"                             ,  9180,  1304, aClass, aMat.mToolQuality, 16, aMachine     , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_FUELMAP, FM.Gas,    NBT_EFFICIENCY,  3500, NBT_OUTPUT,  512, NBT_PREHEAT_ENERGY,  512*4000, NBT_PREHEAT_RATE,  512*1, NBT_PREHEAT_COST,  512/16, NBT_COOLDOWN_RATE,  512, NBT_ENERGY_EMITTED, TD.Energy.RU), "PLP", "SMS", "GOC", 'M', OP.casingMachineDense.dat(aMat), 'O', OP.pipeHuge.dat(aMat)     , 'P', OP.rotor.dat(aMat), 'S', OP.stickLong.dat(aMat), 'G', OP.gearGt.dat(aMat), 'C', OP.gearGtSmall.dat(aMat), 'L', OD.itemLubricant);
        aMat = MT.Ir;                   aRegistry.appendAddAfter(9199, RegType.GT6U, "Small Gas Turbine ("              +aMat.getLocal()+")", "Engines"                             ,  9189,  1304, aClass, aMat.mToolQuality, 16, aMachine     , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_FUELMAP, FM.Gas,    NBT_EFFICIENCY,  3333, NBT_OUTPUT, 1024, NBT_PREHEAT_ENERGY, 1024*4000, NBT_PREHEAT_RATE, 1024*1, NBT_PREHEAT_COST, 1024/16, NBT_COOLDOWN_RATE, 1024, NBT_ENERGY_EMITTED, TD.Energy.RU), "PLP", "SMS", "GOC", 'M', OP.casingMachineDense.dat(aMat), 'O', OP.pipeHuge.dat(aMat)     , 'P', OP.rotor.dat(aMat), 'S', OP.stickLong.dat(aMat), 'G', OP.gearGt.dat(aMat), 'C', OP.gearGtSmall.dat(aMat), 'L', OD.itemLubricant);
    }
    
    
    @Override protected void unsorted2BeforeLoadGT6U(MultiTileEntityRegistry aRegistry, MultiTileEntityBlock aMetal, MultiTileEntityBlock aMetalChips, MultiTileEntityBlock aMetalWires, MultiTileEntityBlock aMachine, MultiTileEntityBlock aWooden, MultiTileEntityBlock aBush, MultiTileEntityBlock aStone, MultiTileEntityBlock aWool, MultiTileEntityBlock aTNT, MultiTileEntityBlock aHive, MultiTileEntityBlock aUtilMetal, MultiTileEntityBlock aUtilStone, MultiTileEntityBlock aUtilWood, MultiTileEntityBlock aUtilWool, OreDictMaterial aMat, Class<? extends TileEntity> aClass) {
        if (!DATA_GTCH.enableGT6U) return;
        
        /// 添加项
        // IV 电池
        aRegistry.appendAddAfter(14004, RegType.GT6U, "Lead-Acid Battery ("                 +VN[5]+")", "Batteries"                         , 14005, 14013, MultiTileEntityBatteryEU8192.class,     0, 16, aUtilMetal , UT.NBT.make(NBT_HARDNESS,   0.5F, NBT_RESISTANCE,   3.0F, NBT_COLOR, DYES_INT[DYE_INDEX_Orange] , NBT_INPUT, V[5], NBT_CAPACITY, V[4] *   2000, NBT_ENERGY_ACCEPTED, TD.Energy.EU), "WPW", "BCB", "BBB", 'P', OP.plate.dat(MT.BatteryAlloy), 'B', IL.Battery_Lead_Acid_Cell_Filled, 'W', MT.DATA.CABLES_01[5], 'C', OD_CIRCUITS[5]);
        aRegistry.appendAddAfter(14014, RegType.GT6U, "Alkaline Battery ("                  +VN[5]+")", "Batteries"                         , 14015, 14013, MultiTileEntityBatteryEU8192.class,     0, 16, aUtilMetal , UT.NBT.make(NBT_HARDNESS,   0.5F, NBT_RESISTANCE,   3.0F, NBT_COLOR, DYES_INT[DYE_INDEX_Blue]   , NBT_INPUT, V[5], NBT_CAPACITY, V[4] *   4000, NBT_ENERGY_ACCEPTED, TD.Energy.EU), "WPW", "BCB", "BBB", 'P', OP.plate.dat(MT.BatteryAlloy), 'B', IL.Battery_Alkaline_Cell_Filled, 'W', MT.DATA.CABLES_01[5], 'C', OD_CIRCUITS[5]);
        aRegistry.appendAddAfter(14024, RegType.GT6U, "Nickel-Cadmium Battery ("            +VN[5]+")", "Batteries"                         , 14025, 14013, MultiTileEntityBatteryEU8192.class,     0, 16, aUtilMetal , UT.NBT.make(NBT_HARDNESS,   0.5F, NBT_RESISTANCE,   3.0F, NBT_COLOR, DYES_INT[DYE_INDEX_Lime]   , NBT_INPUT, V[5], NBT_CAPACITY, V[4] *   4000, NBT_ENERGY_ACCEPTED, TD.Energy.EU), "WPW", "BCB", "BBB", 'P', OP.plate.dat(MT.BatteryAlloy), 'B', IL.Battery_NiCd_Cell_Filled, 'W', MT.DATA.CABLES_01[5], 'C', OD_CIRCUITS[5]);
        aRegistry.appendAddAfter(14034, RegType.GT6U, "Lithium-Cobalt Battery ("            +VN[5]+")", "Batteries"                         , 14035, 14013, MultiTileEntityBatteryAdvEU8192.class , 0, 16, aUtilMetal , UT.NBT.make(NBT_HARDNESS,   0.5F, NBT_RESISTANCE,   3.0F, NBT_COLOR, DYES_INT[DYE_INDEX_Blue]   , NBT_INPUT, V[5], NBT_CAPACITY, V[4] *  64000, NBT_ENERGY_ACCEPTED, TD.Energy.EU), "WPW", "BCB", "BBB", 'P', OP.plate.dat(MT.BatteryAlloy), 'B', IL.Battery_LiCoO2_Cell_Filled, 'W', MT.DATA.CABLES_01[5], 'C', OD_CIRCUITS[6]);
        aRegistry.appendAddAfter(14044, RegType.GT6U, "Lithium-Manganese Battery ("         +VN[5]+")", "Batteries"                         , 14045, 14013, MultiTileEntityBatteryAdvEU8192.class , 0, 16, aUtilMetal , UT.NBT.make(NBT_HARDNESS,   0.5F, NBT_RESISTANCE,   3.0F, NBT_COLOR, DYES_INT[DYE_INDEX_Green]  , NBT_INPUT, V[5], NBT_CAPACITY, V[4] * 128000, NBT_ENERGY_ACCEPTED, TD.Energy.EU), "WPW", "BCB", "BBB", 'P', OP.plate.dat(MT.BatteryAlloy), 'B', IL.Battery_LiMn_Cell_Filled, 'W', MT.DATA.CABLES_01[5], 'C', OD_CIRCUITS[6]);
    }
    @Override protected void unsorted2FinishLoadGT6U(MultiTileEntityRegistry aRegistry, MultiTileEntityBlock aMetal, MultiTileEntityBlock aMetalChips, MultiTileEntityBlock aMetalWires, MultiTileEntityBlock aMachine, MultiTileEntityBlock aWooden, MultiTileEntityBlock aBush, MultiTileEntityBlock aStone, MultiTileEntityBlock aWool, MultiTileEntityBlock aTNT, MultiTileEntityBlock aHive, MultiTileEntityBlock aUtilMetal, MultiTileEntityBlock aUtilStone, MultiTileEntityBlock aUtilWood, MultiTileEntityBlock aUtilWool, OreDictMaterial aMat, Class<? extends TileEntity> aClass) {
        if (!DATA_GTCH.enableGT6U) return;
        
        /// 附加修改部分
        // IV 电池的 IL 设定
        IL.Battery_Lead_Acid_IV .set(aRegistry.getItem(14005), null, "gt:re-battery5");
        IL.Battery_Alkaline_IV  .set(aRegistry.getItem(14015), null, "gt:re-battery5");
        IL.Battery_NiCd_IV      .set(aRegistry.getItem(14025), null, "gt:re-battery5");
        IL.Battery_LiCoO2_IV    .set(aRegistry.getItem(14035), null, "gt:re-battery5");
        IL.Battery_LiMn_IV      .set(aRegistry.getItem(14045), null, "gt:re-battery5");
    }
    
    
    @Override protected void multiblocksBeforeLoadGT6U(MultiTileEntityRegistry aRegistry, MultiTileEntityBlock aMetal, MultiTileEntityBlock aMetalChips, MultiTileEntityBlock aMetalWires, MultiTileEntityBlock aMachine, MultiTileEntityBlock aWooden, MultiTileEntityBlock aBush, MultiTileEntityBlock aStone, MultiTileEntityBlock aWool, MultiTileEntityBlock aTNT, MultiTileEntityBlock aHive, MultiTileEntityBlock aUtilMetal, MultiTileEntityBlock aUtilStone, MultiTileEntityBlock aUtilWood, MultiTileEntityBlock aUtilWool, OreDictMaterial aMat, Class<? extends TileEntity> aClass) {
        if (!DATA_GTCH.enableGT6U) return;
        
        /// 修改项
        // 蒸馏塔设置最低输入
        aRegistry.addReplacer(17101).setParameters(NBT_INPUT_MIN, 16);
        // 低温蒸馏塔设置最低输入，并且替换核心方块，材料替换，需要部件修改为 Frost Proof Machine Casing
        aRegistry.addReplacer(17111).setParameters(NBT_INPUT_MIN, 16, NBT_MATERIAL, MT.Al, NBT_DESIGN, 18112).recipe("PPP", "PMP", "PPP", 'M', aRegistry.getItem(18112), 'P', OP.pipeNonuple.dat(ANY.Cu));
        // MARK 内爆压缩机不需要输入电力，莫名其妙，并且也没有对聚爆压缩机的类进行任何修改 TODO 材质的修改检查
        // MARK 大型高压釜不再需要输入电力，莫名其妙，TODO 类的修改检查，材质的修改检查
        // MARK 大电炉的合成不再修改
        // 大型物质制造机，合成需要的 FIELD_GENERATORS 从 5 调整到 6
        aRegistry.addReplacer(17199).recipe("FFF", "FMF", "FFF", 'M', aRegistry.getItem(18031), 'F', IL.FIELD_GENERATORS[6]);
        // 聚变反应堆改名为 MkI，且语言文件放入 GT6U 部分；目前不对输入输出做任何调整
        aRegistry.addReplacer(17198).regType(RegType.GT6U).localised("Fusion Reactor MkI");
        // 钨热交换器部件从致密壁板改为 gt6u 的热部件 MARK output 32768 -> 16384 （不改变原版的输出效率） TODO 大型热交换器增加效率
        aRegistry.addReplacer(17197).regType(RegType.GT6U).localised("Large Heat Exchanger (Tungsten)").setParameters(NBT_OUTPUT, 16384, NBT_DESIGN, 18110, NBT_EFFICIENCY, 10000);
        
        // GTCH 的通用改动
        // Boilers 增加效率参数，输入调整
        aRegistry.addReplacer(17206).setParameters(NBT_INPUT,  32768, NBT_EFFICIENCY_CH, 8000, NBT_CAPACITY,  32768*1000, NBT_CAPACITY_SU,   32768*40000).removeParameters(NBT_OUTPUT_SU); // Titanium Niobium Carbide
        // Turbines 各长度的参数，增加预热相关
        aRegistry.addReplacer(17215).setParameters(NBT_EFFICIENCY_WATER, 9500, NBT_EFFICIENCY_OC, 5000, NBT_LENGTH_MIN, 1, NBT_LENGTH_MAX, 10, NBT_LENGTH_MID, 4).removeParameters(NBT_OUTPUT, NBT_EFFICIENCY, NBT_INPUT, NBT_WASTE_ENERGY).setParameterArray(NBT_OUTPUT, 10922,20480, 27454, 32768, 36951, 40329, 43115, 45452, 47440, 49152).setParameterArray(NBT_EFFICIENCY, 3250,5000,6000,6500,7000,7250,7333,7500,7666,7750).setParameterArray(NBT_PREHEAT_ENERGY, 196608000, 393216000,  589824000,  786432000,  983040000, 1179648000, 1376256000, 1572864000, 1769472000, 1966080000).setParameterArray(NBT_PREHEAT_COST,  86,171, 256, 342, 427, 512, 598, 683, 768, 854).setParameterArray(NBT_COOLDOWN_RATE,  8192,16384,24576, 32768, 40960, 49152, 57344, 65536, 73728, 81920); // Titanium Niobium Carbide
        aRegistry.addReplacer(17235).setParameters(NBT_LENGTH_MIN, 3, NBT_LENGTH_MAX, 12, NBT_LENGTH_MID, 6).removeParameters(NBT_OUTPUT, NBT_EFFICIENCY, NBT_INPUT, NBT_WASTE_ENERGY, NBT_LIMIT_CONSUMPTION, NBT_ENERGY_ACCEPTED).setParameterArray(NBT_OUTPUT, 10922,20480, 27454, 32768, 36951, 40329, 43115, 45452, 47440, 49152).setParameterArray(NBT_EFFICIENCY, 3250,5000,6000,6500,7000,7250,7333,7500,7666,7750).setParameterArray(NBT_PREHEAT_ENERGY, 196608000, 393216000,  589824000,  786432000,  983040000, 1179648000, 1376256000, 1572864000, 1769472000, 1966080000).setParameterArray(NBT_PREHEAT_RATE, 10922,20480, 27454, 32768, 36951, 40329, 43115, 45452, 47440, 49152).setParameterArray(NBT_PREHEAT_COST,  86,171, 256, 342, 427, 512, 598, 683, 768, 854).setParameterArray(NBT_COOLDOWN_RATE,  8192,16384,24576, 32768, 40960, 49152, 57344, 65536, 73728, 81920).recipe("PwP", "BMC", "PEP", 'M', aRegistry.getItem(17215), 'B', "gt:re-battery4", 'C', IL.Processor_Crystal_Diamond, 'E', IL.MOTORS[4], 'P', OP.plateDense.dat(MT.Ta4HfC5));       // Titanium Niobium Carbide
        // Dynamo 大型发电机效率调整为 95%
        aRegistry.addReplacer(17225).setParameters(NBT_OUTPUT,  32768, NBT_EFFICIENCY_NUM, 9500).removeParameters(NBT_INPUT);
        
        
        /// 添加项
        // 添加一些多方快部件
        // Wall
        aClass = MultiTileEntityMultiBlockPart.class;
        aMat = MT.Ir;                   aRegistry.appendAddAfter (18004, RegType.GT6U, "Iridium Wall"                                       , "Multiblock Machines", 18016, 17101, aClass                                       , aMat.mToolQuality, 64, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,  10.0F, NBT_RESISTANCE,  10.0F, NBT_TEXTURE, "metalwall"               , NBT_DESIGNS, 7), "wPP", "hPP"       , 'P', OP.plate.dat(aMat));
        aMat = MT_CH.Nb2Ti3C5;          aRegistry.appendAddBefore(18012, RegType.GT6U, "Titanium Niobium Carbide Wall"                      , "Multiblock Machines", 18013, 17101, aClass                                       , aMat.mToolQuality, 64, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,  12.5F, NBT_RESISTANCE,  12.5F, NBT_TEXTURE, "metalwall"               , NBT_DESIGNS, 7), "wPP", "hPP"       , 'P', OP.plate.dat(aMat));
        aMat = MT.Osmiridium;           aRegistry.appendAddAfter (18012, RegType.GT6U, "Osmiridium Wall"                                    , "Multiblock Machines", 18014, 17101, aClass                                       , aMat.mToolQuality, 64, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,  12.5F, NBT_RESISTANCE,  12.5F, NBT_TEXTURE, "metalwall"               , NBT_DESIGNS, 8), "wPP", "hPP"       , 'P', OP.plate.dat(aMat));
        aMat = MT_CH.PTFE;              aRegistry.appendAddAfter (18005, RegType.GT6U, "Polytetrafluoroethylene Wall"                       , "Multiblock Machines", 18015, 17101, aClass                                       , aMat.mToolQuality, 64, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,  10.0F, NBT_RESISTANCE,  10.0F, NBT_TEXTURE, "metalwall"               , NBT_DESIGNS, 7), "wPP", "hPP"       , 'P', OP.plate.dat(aMat));
        // Dense Wall
        aMat = MT.Ir;                   aRegistry.appendAddAfter (18024, RegType.GT6U, "Dense Iridium Wall"                                 , "Multiblock Machines", 18036, 17101, aClass                                       , aMat.mToolQuality, 64, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,  12.5F, NBT_RESISTANCE,  12.5F, NBT_TEXTURE, "metalwalldense"          , NBT_DESIGNS, 7));
        aMat = MT_CH.Nb2Ti3C5;          aRegistry.appendAddBefore(18032, RegType.GT6U, "Dense Titanium Niobium Carbide Wall"                , "Multiblock Machines", 18033, 17101, aClass                                       , aMat.mToolQuality, 64, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,  12.5F, NBT_RESISTANCE,  12.5F, NBT_TEXTURE, "metalwalldense"          , NBT_DESIGNS, 7));
        aMat = MT.Osmiridium;           aRegistry.appendAddAfter (18032, RegType.GT6U, "Dense Osmiridium Wall"                              , "Multiblock Machines", 18034, 17101, aClass                                       , aMat.mToolQuality, 64, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,  12.5F, NBT_RESISTANCE,  12.5F, NBT_TEXTURE, "metalwalldense"          , NBT_DESIGNS, 7));
        aMat = MT.VanadiumSteel;        aRegistry.appendAddAfter (18025, RegType.GT6U, "Dense Vanadiumsteel Wall"                           , "Multiblock Machines", 18039, 17101, aClass                                       , aMat.mToolQuality, 64, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,  10.0F, NBT_RESISTANCE,  10.0F, NBT_TEXTURE, "metalwalldense"          , NBT_DESIGNS, 7));
        aMat = MT.TungstenCarbide;      aRegistry.appendAddAfter (18025, RegType.GT6U, "Dense Tungsten Carbide Wall"                        , "Multiblock Machines", 18035, 17101, aClass                                       , aMat.mToolQuality, 64, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,  12.0F, NBT_RESISTANCE,  12.0F, NBT_TEXTURE, "metalwalldense"          , NBT_DESIGNS, 7));
        // Coil
        aMat = MT.Superconductor;       aRegistry.appendAddAfter (18045, RegType.GT6U, "Large Superconducting Coil"                         , "Multiblock Machines", 18046, 17101, aClass                                       , aMat.mToolQuality, 64, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_TEXTURE, "coil"                    , NBT_DESIGNS, 1), "WWW", "WxW", "WWW", 'W', OP.wireGt04.dat(aMat));
        
        // Thermomechanical Block (暂不知用途)
        aMat = MT.TungstenSteel;        aRegistry.appendAddBefore(18100, RegType.GT6U, "TungstenSteel Thermomechanical Block"               , "Multiblock Machines", 18109, 17101, aClass                                       , aMat.mToolQuality, 64, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,  10.0F, NBT_RESISTANCE,  10.0F, NBT_TEXTURE, "metalwall"               , NBT_DESIGNS, 7), "DDD", "PMP", "DDD", 'M', aRegistry.getItem(18023), 'D', OP.plateDense.dat(MT.AnnealedCopper), 'P', OP.pipeSmall.dat(ANY.Cu));
        aMat = ANY.W;                   aRegistry.appendAddBefore(18100, RegType.GT6U, "Tungsten Thermomechanical Block"                    , "Multiblock Machines", 18110, 17101, aClass                                       , aMat.mToolQuality, 64, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,  10.0F, NBT_RESISTANCE,  10.0F, NBT_TEXTURE, "metalwall"               , NBT_DESIGNS, 7), "DDD", "PMP", "DDD", 'M', aRegistry.getItem(18024), 'D', OP.plateDense.dat(MT.AnnealedCopper), 'P', OP.pipeSmall.dat(ANY.Cu));
        aMat = MT.Ta4HfC5;              aRegistry.appendAddBefore(18100, RegType.GT6U, "Tantalum Hafnium Carbide Thermomechanical Block"    , "Multiblock Machines", 18111, 17101, aClass                                       , aMat.mToolQuality, 64, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,  10.0F, NBT_RESISTANCE,  10.0F, NBT_TEXTURE, "metalwall"               , NBT_DESIGNS, 7), "DDD", "PMP", "DDD", 'M', aRegistry.getItem(18032), 'D', OP.plateDense.dat(MT.AnnealedCopper), 'P', OP.pipeSmall.dat(ANY.Cu));
        
        // GT6U 的多方快机器部件
        aMat = MT.Al;                   aRegistry.appendAddBefore(18100, RegType.GT6U, "Frost Proof Machine Casing"                         , "Multiblock Machines", 18112, 17101, aClass                                       , aMat.mToolQuality, 64, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,  10.0F, NBT_RESISTANCE,  10.0F, NBT_TEXTURE, "distillationtowerparts"  , NBT_DESIGNS, 7), "DhD", "DMD", "DwD", 'M', aRegistry.getItem(18002), 'D', OP.plate.dat(MT.Al));
        aMat = MT.StainlessSteel;       aRegistry.appendAddBefore(18100, RegType.GT6U, "Cracking Tower Part"                                , "Multiblock Machines", 18113, 17101, aClass                                       , aMat.mToolQuality, 64, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_TEXTURE, "distillationtowerparts"  , NBT_DESIGNS, 1), "hTd", "PMP", 'M', OP.plate.dat(aMat), 'P', OP.pipeSmall.dat(aMat), 'T', aRegistry.getItem(18002));
        aMat = MT.StainlessSteel;       aRegistry.appendAddBefore(18100, RegType.GT6U, "Clean StainlessSteel Machine Casing"                , "Multiblock Machines", 18114, 17101, aClass                                       , aMat.mToolQuality, 64, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_TEXTURE, "metalwalldense"          , NBT_DESIGNS, 7), "WWW", "DwD", "WWW", 'W', OP.plateDouble.dat(aMat), 'D', OP.stick.dat(aMat));
        aMat = MT.Rubber;               aRegistry.appendAddBefore(18100, RegType.GT6U, "Electrical Proof Machine Casing"                    , "Multiblock Machines", 18115, 17101, aClass                                       , aMat.mToolQuality, 64, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,  10.0F, NBT_RESISTANCE,  10.0F, NBT_TEXTURE, "metalwalldense"          , NBT_DESIGNS, 7), "DhD", "DMD", "DwD", 'M', aRegistry.getItem(18039), 'D', OP.plateDense.dat(ANY.Rubber));
        aMat = MT.StainlessSteel;       aRegistry.appendAddBefore(18100, RegType.GT6U, "Aligner Unit"                                       , "Multiblock Machines", 18116, 17101, aClass                                       , aMat.mToolQuality, 64, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_TEXTURE, "alignerunit"             , NBT_DESIGNS, 7), "WwW", "WMW", "CCC", 'M', OP.casingMachine.dat(aMat), 'W', OP.wireGt01.dat(MT.Cu), 'C', OD_CIRCUITS[4]);
        aMat = MT.Osmiridium;           aRegistry.appendAddBefore(18100, RegType.GT6U, "Mass Spectrometer Module"                           , "Multiblock Machines", 18117, 17101, aClass                                       , aMat.mToolQuality, 64, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,  10.0F, NBT_RESISTANCE,  10.0F, NBT_TEXTURE, "massspectrometermodule"  , NBT_DESIGNS, 0), "CPC", "WMW", "CPC", 'M', OP.casingMachine.dat(aMat), 'W', OP.wireGt08.dat(MT.Os), 'C', IL.Processor_Crystal_Diamond, 'P', IL.FIELD_GENERATORS[5]);
        aMat = MT.Steel;                aRegistry.appendAddBefore(18100, RegType.GT6U, "Well Pipe"                                          , "Multiblock Machines", 18118, 17101, aClass                                       , aMat.mToolQuality, 64, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_TEXTURE, "wellpipe"                , NBT_DESIGNS, 0), "WCW", "WwW", "WCW", 'W', OP.plate.dat(MT.WoodTreated), 'C', OP.ring.dat(MT.Steel));
        aMat = MT_CH.PTFE;              aRegistry.appendAddBefore(18100, RegType.GT6U, "Sterile Machine Casing"                             , "Multiblock Machines", 18119, 17101, aClass                                       , aMat.mToolQuality, 64, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,  10.0F, NBT_RESISTANCE,  10.0F, NBT_TEXTURE, "metalwall"               , NBT_DESIGNS, 7), "DhD", "DMD", "DwD", 'M', aRegistry.getItem(18002), 'D', OP.plate.dat(MT_CH.PTFE));
        
        // 大型电池部件
        aMat = MT.Graphite;             aRegistry.appendAddBefore(18100, RegType.GT6U, "Graphite Electrode Part"                            , "Multiblock Machines", 18120, 17101, aClass                                       , aMat.mToolQuality, 64, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,  10.0F, NBT_RESISTANCE,  10.0F, NBT_TEXTURE, "metalwalldense"          , NBT_DESIGNS, 7), "WWW", "WDW", "WWW", 'W', OP.plate.dat(aMat), 'D', OP.blockPlate.dat(aMat)); OM.data(aRegistry.getItem(), aMat, U*17);
        aMat = MT.Graphene;             aRegistry.appendAddBefore(18100, RegType.GT6U, "Graphene Electrode Part"                            , "Multiblock Machines", 18121, 17101, aClass                                       , aMat.mToolQuality, 64, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,  10.0F, NBT_RESISTANCE,  10.0F, NBT_TEXTURE, "metalwalldense"          , NBT_DESIGNS, 7), "WWW", "WDW", "WWW", 'W', OP.plate.dat(aMat), 'D', OP.blockPlate.dat(aMat)); OM.data(aRegistry.getItem(), aMat, U*17);
        aMat = MT.BatteryAlloy;         aRegistry.appendAddBefore(18100, RegType.GT6U, "MESU Casing"                                        , "Multiblock Machines", 18122, 17101, aClass                                       , aMat.mToolQuality, 64, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,  10.0F, NBT_RESISTANCE,  10.0F, NBT_TEXTURE, "metalwall"               , NBT_DESIGNS, 7), "DhD", "DMD", "DwD", 'M', OP.plate.dat(ANY.Plastic), 'D', OP.plate.dat(aMat)); // OM.data(aRegistry.getItem(), MT.BatteryAlloy, U*6, MT.Plastic, U*1);
        aMat = MT.BatteryAlloy;         aRegistry.appendAddBefore(18100, RegType.GT6U, "Lead-Acid Battery Core (Filled)"                    , "Multiblock Machines", 18124, 17101, aClass                                       , aMat.mToolQuality, 64, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_TEXTURE, "batterycore1"            , NBT_DESIGNS, 0));
        aMat = MT.BatteryAlloy;         aRegistry.appendAddBefore(18100, RegType.GT6U, "Alkaline Battery Core (Filled)"                     , "Multiblock Machines", 18125, 17101, aClass                                       , aMat.mToolQuality, 64, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_TEXTURE, "batterycore2"            , NBT_DESIGNS, 0));
        aMat = MT.BatteryAlloy;         aRegistry.appendAddBefore(18100, RegType.GT6U, "Nickel-Cadmium Battery Core (Filled)"               , "Multiblock Machines", 18126, 17101, aClass                                       , aMat.mToolQuality, 64, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_TEXTURE, "batterycore3"            , NBT_DESIGNS, 0));
        aMat = MT.BatteryAlloy;         aRegistry.appendAddBefore(18100, RegType.GT6U, "Lithium-Cobalt Battery Core (Filled)"               , "Multiblock Machines", 18127, 17101, aClass                                       , aMat.mToolQuality, 64, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_TEXTURE, "batterycore4"            , NBT_DESIGNS, 0));
        aMat = MT.BatteryAlloy;         aRegistry.appendAddBefore(18100, RegType.GT6U, "Lithium-Manganese Battery Core (Filled)"            , "Multiblock Machines", 18128, 17101, aClass                                       , aMat.mToolQuality, 64, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_TEXTURE, "batterycore5"            , NBT_DESIGNS, 0));
        aMat = MT.BatteryAlloy;         aRegistry.appendAddBefore(18100, RegType.GT6U, "Lithium-F-Phosphate Battery Core (Filled)"          , "Multiblock Machines", 18129, 17101, aClass                                       , aMat.mToolQuality, 64, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_TEXTURE, "batterycore6"            , NBT_DESIGNS, 0));
        aMat = MT.BatteryAlloy;         aRegistry.appendAddBefore(18100, RegType.GT6U, "Lead-Acid Battery Core (Empty)"                     , "Multiblock Machines", 18130, 17101, aClass                                       , aMat.mToolQuality, 64, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_TEXTURE, "batterycore1empty"       , NBT_DESIGNS, 0), "DDD", "WAW", "PPP", 'W', OP.stickLong.dat(MT.Pb), 'D', OP.plate.dat(MT.Graphite), 'P', OP.plateDouble.dat(MT.Pb), 'A', OP.plateDense.dat(MT.BatteryAlloy));
        aMat = MT.BatteryAlloy;         aRegistry.appendAddBefore(18100, RegType.GT6U, "Alkaline Battery Core (Empty)"                      , "Multiblock Machines", 18131, 17101, aClass                                       , aMat.mToolQuality, 64, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_TEXTURE, "batterycore2empty"       , NBT_DESIGNS, 0), "DZD", "WAY", "PPP", 'W', OP.blockDust.dat(MT.KOH), 'D', OP.plate.dat(MT.Graphite), 'P', OP.plateDouble.dat(MT.Al), 'A', OP.plateDense.dat(MT.BatteryAlloy), 'Y', OP.blockDust.dat(MT.MnO2), 'Z', OP.blockDust.dat(MT.Zn));
        aMat = MT.BatteryAlloy;         aRegistry.appendAddBefore(18100, RegType.GT6U, "Nickel-Cadmium Battery Core (Empty)"                , "Multiblock Machines", 18132, 17101, aClass                                       , aMat.mToolQuality, 64, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_TEXTURE, "batterycore3empty"       , NBT_DESIGNS, 0), "DDD", "WAY", "PPP", 'W', OP.blockDust.dat(MT.KOH), 'D', OP.plate.dat(MT.Graphite), 'P', OP.plateDouble.dat(MT.Cd), 'A', OP.plateDense.dat(MT.BatteryAlloy), 'Y', OP.stickLong.dat(MT.Ni));
        aMat = MT.BatteryAlloy;         aRegistry.appendAddBefore(18100, RegType.GT6U, "Lithium-Cobalt Battery Core (Empty)"                , "Multiblock Machines", 18133, 17101, aClass                                       , aMat.mToolQuality, 64, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_TEXTURE, "batterycoreliempty"      , NBT_DESIGNS, 0), "DDD", "WAW", "PPP", 'W', OP.blockDust.dat(MT.LiClO4), 'D', OP.plate.dat(MT.Graphene), 'P', OP.plateDouble.dat(MT.Co), 'A', OP.plateDense.dat(MT.BatteryAlloy));
        aMat = MT.BatteryAlloy;         aRegistry.appendAddBefore(18100, RegType.GT6U, "Lithium-Manganese Battery Core (Empty)"             , "Multiblock Machines", 18134, 17101, aClass                                       , aMat.mToolQuality, 64, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_TEXTURE, "batterycoreliempty"      , NBT_DESIGNS, 0), "DDD", "WAW", "PPP", 'W', OP.blockDust.dat(MT.LiClO4), 'D', OP.plate.dat(MT.Graphene), 'P', OP.plateDouble.dat(MT.Mn), 'A', OP.plateDense.dat(MT.BatteryAlloy));
        aMat = MT.BatteryAlloy;         aRegistry.appendAddBefore(18100, RegType.GT6U, "Lithium-F-Phosphate Battery Core (Empty)"           , "Multiblock Machines", 18135, 17101, aClass                                       , aMat.mToolQuality, 64, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_TEXTURE, "batterycoreliempty"      , NBT_DESIGNS, 0), "DDD", "WAW", "PPP", 'W', OP.blockDust.dat(MT_CH.LiPF6), 'D', OP.plate.dat(MT.Graphene), 'P', OP.plateDouble.dat(MT.Pt), 'A', OP.plateDense.dat(MT.BatteryAlloy));
        
        // MARK 不再修改洗矿场部件耗材
        // 水农场部件
        aMat = MT.StainlessSteel;       aRegistry.appendAddBefore(18299, RegType.GT6U, "Aquatic Farm Block"                                 , "Multiblock Machines", 18298, 17101, aClass                                       , aMat.mToolQuality, 64, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_TEXTURE, "aquaticfarmblock"        , NBT_DESIGNS, 3), "EME", "FFF", "EME", 'M', IL.Electric_Pump_LV, 'F', Blocks.glass_pane, 'E', OP.pipeTiny.dat(aMat));
        
        // MARK 不再修改木储罐的合成
        // 各种储罐核心
        aMat = MT.SteelGalvanized;      aRegistry.appendAddAfter (17007, RegType.GT6U, "Small SteelGalvanized Tank Main Valve"              , "Multiblock Machines", 17008, 17101, MultiTileEntityTank3x3x3Metal.class     , aMat.mToolQuality, 16, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_TEXTURE, "tankmetal"               , NBT_TANK_CAPACITY,    2474000, NBT_DESIGN, 18008, NBT_GASPROOF, T, NBT_ACIDPROOF, F, NBT_PLASMAPROOF, F, NBT_MAGICPROOF, F                        ), " R ", "hMs", " R ", 'M', aRegistry.getItem(18008), 'R', OP.ring.dat(aMat)); // nbt 获取会失败，但是合成表添加只需要 id 即可
        aMat = MT_CH.Nb2Ti3C5;          aRegistry.appendAddAfter (17004, RegType.GT6U, "Small Titanium Niobium Carbide Tank Main Valve"     , "Multiblock Machines", 17009, 17101, MultiTileEntityTank3x3x3Metal.class     , aMat.mToolQuality, 16, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,  12.5F, NBT_RESISTANCE,  12.5F, NBT_TEXTURE, "tankmetal"               , NBT_TANK_CAPACITY,    8192000, NBT_DESIGN, 18013, NBT_GASPROOF, T, NBT_ACIDPROOF, F, NBT_PLASMAPROOF, F, NBT_MAGICPROOF, F                        ), " R ", "hMs", " R ", 'M', aRegistry.getItem(18013), 'R', OP.ring.dat(aMat));
        aMat = MT.SteelGalvanized;      aRegistry.appendAddAfter (17027, RegType.GT6U, "Small Dense SteelGalvanized Tank Main Valve"        , "Multiblock Machines", 17028, 17101, MultiTileEntityTank3x3x3Metal.class     , aMat.mToolQuality, 16, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_TEXTURE, "tankmetal"               , NBT_TANK_CAPACITY,    9896000, NBT_DESIGN, 18028, NBT_GASPROOF, T, NBT_ACIDPROOF, F, NBT_PLASMAPROOF, F, NBT_MAGICPROOF, F                        ), " R ", "hMs", " R ", 'M', aRegistry.getItem(18028), 'R', OP.ring.dat(aMat));
        aMat = MT_CH.Nb2Ti3C5;          aRegistry.appendAddAfter (17024, RegType.GT6U, "Small Dense Titanium Niobium Carbide Tank Main Valve","Multiblock Machines", 17029, 17101, MultiTileEntityTank3x3x3Metal.class     , aMat.mToolQuality, 16, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,  10.0F, NBT_RESISTANCE,  10.0F, NBT_TEXTURE, "tankmetal"               , NBT_TANK_CAPACITY,   32768000, NBT_DESIGN, 18033, NBT_GASPROOF, T, NBT_ACIDPROOF, T, NBT_PLASMAPROOF, F, NBT_MAGICPROOF, F                        ), " R ", "hMs", " R ", 'M', aRegistry.getItem(18033), 'R', OP.ring.dat(aMat));
        aMat = MT.SteelGalvanized;      aRegistry.appendAddAfter (17047, RegType.GT6U, "Large SteelGalvanized Tank Main Valve"              , "Multiblock Machines", 17048, 17101, MultiTileEntityTank5x5x5Metal.class     , aMat.mToolQuality, 16, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_TEXTURE, "tankmetal"               , NBT_TANK_CAPACITY,   12000000, NBT_DESIGN, 18008, NBT_GASPROOF, T, NBT_ACIDPROOF, F, NBT_PLASMAPROOF, F, NBT_MAGICPROOF, F                        ), "PPP", "hMs", "PPP", 'M', aRegistry.getItem(17008), 'P', OP.plate.dat(aMat));
        aMat = MT_CH.Nb2Ti3C5;          aRegistry.appendAddAfter (17044, RegType.GT6U, "Large Titanium Niobium Carbide Tank Main Valve"     , "Multiblock Machines", 17049, 17101, MultiTileEntityTank5x5x5Metal.class     , aMat.mToolQuality, 16, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,  10.0F, NBT_RESISTANCE,  10.0F, NBT_TEXTURE, "tankmetal"               , NBT_TANK_CAPACITY,   64000000, NBT_DESIGN, 18013, NBT_GASPROOF, T, NBT_ACIDPROOF, T, NBT_PLASMAPROOF, F, NBT_MAGICPROOF, F                        ), "PPP", "hMs", "PPP", 'M', aRegistry.getItem(17009), 'P', OP.plate.dat(aMat));
        aMat = MT.SteelGalvanized;      aRegistry.appendAddAfter (17067, RegType.GT6U, "Large Dense SteelGalvanized Tank Main Valve"        , "Multiblock Machines", 17068, 17101, MultiTileEntityTank5x5x5Metal.class     , aMat.mToolQuality, 16, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_TEXTURE, "tankmetal"               , NBT_TANK_CAPACITY,   48000000, NBT_DESIGN, 18028, NBT_GASPROOF, T, NBT_ACIDPROOF, F, NBT_PLASMAPROOF, F, NBT_MAGICPROOF, F                        ), "PPP", "hMs", "PPP", 'M', aRegistry.getItem(17028), 'P', OP.plateDense.dat(aMat));
        aMat = MT_CH.Nb2Ti3C5;          aRegistry.appendAddAfter (17064, RegType.GT6U, "Large Dense Titanium Niobium Carbide Tank Main Valve","Multiblock Machines", 17069, 17101, MultiTileEntityTank5x5x5Metal.class     , aMat.mToolQuality, 16, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,  10.0F, NBT_RESISTANCE,  10.0F, NBT_TEXTURE, "tankmetal"               , NBT_TANK_CAPACITY,  256000000, NBT_DESIGN, 18033, NBT_GASPROOF, T, NBT_ACIDPROOF, T, NBT_PLASMAPROOF, F, NBT_MAGICPROOF, F                        ), "PPP", "hMs", "PPP", 'M', aRegistry.getItem(17029), 'P', OP.plateDense.dat(aMat));
        
        // 裂解塔
        aMat = MT.StainlessSteel;       aRegistry.appendAddAfter (17111, RegType.GT6U, "Cracking Tower"                                     , "Multiblock Machines", 17115, 17101, MultiTileEntityCrackingTower.class      , aMat.mToolQuality, 16, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_TEXTURE, "distillationtower"       , NBT_INPUT, 2048, NBT_INPUT_MIN,  128, NBT_INPUT_MAX,     4096                       , NBT_ENERGY_ACCEPTED, TD.Energy.HU, NBT_RECIPEMAP, RM_CH.CrackingTower    , NBT_INV_SIDE_AUTO_OUT, SIDE_BACK  , NBT_TANK_SIDE_AUTO_OUT, SIDE_BACK  , NBT_CHEAP_OVERCLOCKING, T                                             ), "PPP", "PMP", "PPP", 'M', aRegistry.getItem(18113), 'P', OP.pipeNonuple.dat(aMat));
        // 燃油清洁机
        aMat = MT.StainlessSteel;       aRegistry.appendAddAfter (17111, RegType.GT6U, "Fuel Cleaner"                                       , "Multiblock Machines", 17116, 17101, MultiTileEntityOilCleaner.class         , aMat.mToolQuality, 16, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,  12.5F, NBT_RESISTANCE,  12.5F, NBT_TEXTURE, "distillationtower"       , NBT_INPUT,   64, NBT_INPUT_MIN,   64, NBT_INPUT_MAX,     4096                       , NBT_ENERGY_ACCEPTED, TD.Energy.EU, NBT_RECIPEMAP, RM_CH.OilCleaner       , NBT_INV_SIDE_AUTO_OUT, SIDE_BOTTOM, NBT_TANK_SIDE_AUTO_OUT, SIDE_BOTTOM, NBT_CHEAP_OVERCLOCKING, T, NBT_PARALLEL,  32, NBT_PARALLEL_DURATION, T), "PPP", "PMP", "CPC", 'M', aRegistry.getItem(18022), 'P', OP.pipeLarge.dat(aMat),  'C', OD_CIRCUITS[2]);
        
        // 工业焦炉
        aMat = MT.StainlessSteel;       aRegistry.appendAddBefore(17199, RegType.GT6U, "Industry Coke Oven"                                 , "Multiblock Machines", 17119, 17101, MultiTileEntityPyrolyseOven.class       , aMat.mToolQuality, 16, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   5.0F, NBT_RESISTANCE,   5.0F, NBT_TEXTURE, "largeoven"               , NBT_INPUT,   64, NBT_INPUT_MIN,   64, NBT_INPUT_MAX,     4096                       , NBT_ENERGY_ACCEPTED, TD.Energy.EU, NBT_RECIPEMAP, RM.CokeOven            , NBT_INV_SIDE_AUTO_OUT, SIDE_BOTTOM, NBT_TANK_SIDE_AUTO_OUT, SIDE_BOTTOM, NBT_PARALLEL,  64                                                     ), "PPP", "PwP", "CMC", 'M', aRegistry.getItem(18002), 'C', OD_CIRCUITS[4], 'P', OP.plateDense.dat(aMat));
        // 大型焙烧
        aMat = MT.StainlessSteel;       aRegistry.appendAddBefore(17199, RegType.GT6U, "Large Roasting Oven"                                , "Multiblock Machines", 17121, 17101, MultiTileEntityRoasting.class           , aMat.mToolQuality, 16, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_TEXTURE, "largeoven"               , NBT_INPUT,  512, NBT_INPUT_MIN,  512, NBT_INPUT_MAX,     4096, NBT_EFFICIENCY,  2500, NBT_ENERGY_ACCEPTED, TD.Energy.EU, NBT_RECIPEMAP, RM.Roasting            , NBT_INV_SIDE_AUTO_OUT, SIDE_BOTTOM, NBT_TANK_SIDE_AUTO_OUT, SIDE_BOTTOM, NBT_PARALLEL,  64                                                     ), "PPP", "PwP", "CMC", 'M', aRegistry.getItem(18002), 'C', OD_CIRCUITS[4], 'P', OP.cableGt04.dat(MT.Nichrome));
        // 光刻
        aMat = MT.StainlessSteel;       aRegistry.appendAddBefore(17199, RegType.GT6U, "Mask Aligner"                                       , "Multiblock Machines", 17122, 17101, MultiTileEntityMaskAligner.class        , aMat.mToolQuality, 16, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_TEXTURE, "maskaligner"             , NBT_INPUT,  512, NBT_INPUT_MIN,  256, NBT_INPUT_MAX,    16384                       , NBT_ENERGY_ACCEPTED, TD.Energy.LU, NBT_RECIPEMAP, RM_CH.MaskAligner      , NBT_INV_SIDE_AUTO_OUT, SIDE_BOTTOM, NBT_TANK_SIDE_AUTO_OUT, SIDE_BOTTOM, NBT_CHEAP_OVERCLOCKING, T, NBT_PARALLEL,  4,  NBT_PARALLEL_DURATION, T), "CMC", "CCC"       , 'M', aRegistry.getItem(18116), 'C', OD_CIRCUITS[4]);
        // 大型热搅拌
        aMat = ANY.Steel;               aRegistry.appendAddBefore(17199, RegType.GT6U, "Large Heat Mixer"                                   , "Multiblock Machines", 17123, 17101, MultiTileEntityHeatMixer.class          , aMat.mToolQuality, 16, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_TEXTURE, "largeheatmixer"          , NBT_INPUT,  512, NBT_INPUT_MIN,  512, NBT_INPUT_MAX,     4096, NBT_EFFICIENCY,  5000, NBT_ENERGY_ACCEPTED, TD.Energy.EU, NBT_RECIPEMAP, RM.HeatMixer           , NBT_INV_SIDE_AUTO_OUT, SIDE_BOTTOM, NBT_TANK_SIDE_AUTO_OUT, SIDE_BOTTOM, NBT_PARALLEL, 256                                                     ), "PSP", "PSP", "RMC", 'M', aRegistry.getItem(18009), 'R', IL.Processor_Crystal_Ruby, 'C', OD_CIRCUITS[6], 'P', OP.plateDense.dat(MT_CH.PTFE), 'S', OP.stickLong.dat(MT_CH.PTFE));
        // 排水
        aMat = ANY.Steel;               aRegistry.appendAddBefore(17199, RegType.GT6U, "Draining Well"                                      , "Multiblock Machines", 17124, 17101, MultiTileEntityDrainingWell.class       , aMat.mToolQuality, 16, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_TEXTURE, "drainingwell"            , NBT_INPUT,   16, NBT_INPUT_MIN,   16, NBT_INPUT_MAX,     4096                       , NBT_ENERGY_ACCEPTED, TD.Energy.RU, NBT_RECIPEMAP, RM_CH.Well             , NBT_INV_SIDE_AUTO_OUT, SIDE_BOTTOM, NBT_TANK_SIDE_AUTO_OUT, SIDE_BOTTOM, NBT_CHEAP_OVERCLOCKING, T, NBT_NO_CONSTANT_POWER, T                   ), "CCC", "CMC", "CCC", 'M', aRegistry.getItem(18009), 'C', OP.pipeHuge.dat(MT.Wood));
        // 生化研究实验 // TODO IL.Precision_Manipulator 还未添加，因此不能合成
//        aMat = MT_CH.PTFE;              aRegistry.appendAddBefore(17199, RegType.GT6U, "Biochemical Research Lab"                           , "Multiblock Machines", 17127, 17101, MultiTileEntityBiolab.class             , aMat.mToolQuality, 16, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,  10.0F, NBT_RESISTANCE,  10.0F, NBT_TEXTURE, "biolab"                  , NBT_INPUT,  128, NBT_INPUT_MIN,  128, NBT_INPUT_MAX,     8192                       , NBT_ENERGY_ACCEPTED, TD.Energy.EU, NBT_RECIPEMAP, RM_CH.BioLab           , NBT_ENERGY_ACCEPTED_2, TD.Energy.LU, NBT_SPECIAL_IS_START_ENERGY, T, NBT_INV_SIDE_AUTO_OUT, SIDE_BOTTOM, NBT_TANK_SIDE_AUTO_OUT, SIDE_BOTTOM, NBT_CHEAP_OVERCLOCKING, T, NBT_PARALLEL,  16, NBT_PARALLEL_DURATION, T),  "GGG", "SwS", "RMC", 'M', aRegistry.getItem(18119), 'R', IL.Processor_Crystal_Ruby, 'C', IL.Precision_Manipulator, 'G', OP.wireGt02.dat(MT.Ir), 'S', OP.stickLong.dat(MT.Ir));
        // 大型结晶坩
        aMat = MT.TungstenCarbide;      aRegistry.appendAddBefore(17199, RegType.GT6U, "Large Crystallisation Crucible"                     , "Multiblock Machines", 17128, 17101, MultiTileEntityCrystallisationCrucible.class,aMat.mToolQuality,16,aMachine  , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,  12.0F, NBT_RESISTANCE,  12.0F, NBT_TEXTURE, "largecrystallisationcrucible",NBT_INPUT,2048,NBT_INPUT_MIN, 512, NBT_INPUT_MAX,    16384                       , NBT_ENERGY_ACCEPTED, TD.Energy.HU, NBT_RECIPEMAP, RM.CrystallisationCrucible,NBT_INV_SIDE_AUTO_OUT,SIDE_BOTTOM,NBT_TANK_SIDE_AUTO_OUT, SIDE_BOTTOM, NBT_PARALLEL,  64                                                     ), "PPP", "PwP", "CMC", 'M', aRegistry.getItem(18035), 'C', OD_CIRCUITS[4], 'P', OP.plateDense.dat(aMat));
        // 大型压模
        aMat = MT.TungstenCarbide;      aRegistry.appendAddBefore(17199, RegType.GT6U, "Large Extruder"                                     , "Multiblock Machines", 17129, 17101, MultiTileEntityExtruder.class           , aMat.mToolQuality, 16, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,  12.0F, NBT_RESISTANCE,  12.0F, NBT_TEXTURE, "largeextruder"           , NBT_INPUT, 2048, NBT_INPUT_MIN,  512, NBT_INPUT_MAX,    32768, NBT_EFFICIENCY,  5000, NBT_ENERGY_ACCEPTED, TD.Energy.EU, NBT_RECIPEMAP, RM.Extruder            , NBT_INV_SIDE_AUTO_OUT, SIDE_BOTTOM, NBT_TANK_SIDE_AUTO_OUT, SIDE_BOTTOM, NBT_PARALLEL,  64                                                     ), "CMC", "PwP", "PPP", 'M', aRegistry.getItem(18035), 'C', OD_CIRCUITS[4], 'P', OP.plateDense.dat(aMat));
        // 水生农场控制
        aMat = MT.StainlessSteel;       aRegistry.appendAddBefore(17199, RegType.GT6U, "Aquatic Farm Controller"                            , "Multiblock Machines", 17131, 17101, MultiTileEntityAquaticFarm.class        , aMat.mToolQuality, 16, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,  12.5F, NBT_RESISTANCE,  12.5F, NBT_TEXTURE, "aquaticfarm"             , NBT_INPUT, 1024, NBT_INPUT_MIN,    256, NBT_INPUT_MAX,   4096                       , NBT_ENERGY_ACCEPTED, TD.Energy.EU, NBT_RECIPEMAP, RM_CH.AquaticFarm      , NBT_INV_SIDE_AUTO_OUT, SIDE_BACK  , NBT_TANK_SIDE_AUTO_OUT, SIDE_BACK  , NBT_CHEAP_OVERCLOCKING, T                                             ), "GGG", "KSK", "GRG", 'S', aRegistry.getItem(18002), 'R', IL.Processor_Crystal_Ruby, 'G', OP.wireFine.dat(MT.RedAlloy), 'K', IL.Thermometer_Quicksilver);
        // 多方块储能单元 // TODO 注意有很多 bug 需要修
        aMat = MT.BatteryAlloy;         aRegistry.appendAddBefore(17199, RegType.GT6U, "Multiblock Energy Storage Unit"                     , "Multiblock Machines", 17130, 17101, MultiTileEntityMultiblockEnergyStorageUnit.class,aMat.mToolQuality,16,aMachine,UT.NBT.make(NBT_MATERIAL,aMat, NBT_HARDNESS,  12.0F, NBT_RESISTANCE,  12.0F, NBT_TEXTURE, "lightningrod"            , NBT_WASTE_ENERGY, T, NBT_ENERGY_ACCEPTED, TD.Energy.EU, NBT_ENERGY_EMITTED, TD.Energy.EU), "PMP", "MCM", "PMP", 'M', OP.wireGt08.dat(MT.W), 'C' , IL.Processor_Crystal_Ruby, 'P', OP.plateQuadruple.dat(aMat));
        
        // 分子扫描器（和复制机的区别？？）
        aMat = MT.Osmiridium;           aRegistry.appendAddAfter (17199, RegType.GT6U, "Molecular Scanner"                                  , "Multiblock Machines", 17117, 17101, MultiTileEntityMolecularScanner.class   , aMat.mToolQuality, 16, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_TEXTURE, "molecular"               , NBT_INPUT,    1, NBT_INPUT_MIN,    1, NBT_INPUT_MAX,  2097152                        , NBT_ENERGY_ACCEPTED, TD.Energy.QU, NBT_RECIPEMAP, RM.ScannerMolecular   , NBT_INV_SIDE_AUTO_OUT, SIDE_BOTTOM, NBT_TANK_SIDE_AUTO_OUT, SIDE_BOTTOM, NBT_CHEAP_OVERCLOCKING, T, NBT_PARALLEL,  64, NBT_PARALLEL_DURATION, T, NBT_NO_CONSTANT_POWER, T), "FFF", "FMF", "FFF", 'M', aRegistry.getItem(18014), 'F', IL.FIELD_GENERATORS[4]);
        // 大物质复制机 // TODO 检测原版的部件是否改成了锇？是否需要统一都用锇？
        aMat = MT.Pb;                   aRegistry.appendAddAfter (17199, RegType.GT6U, "Large Matter Replicator"                            , "Multiblock Machines", 17118, 17101, MultiTileEntityMatterReplicator.class   , aMat.mToolQuality, 16, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_TEXTURE, "largerepfab"             , NBT_INPUT,    1, NBT_INPUT_MIN,    1, NBT_INPUT_MAX,  2097152                        , NBT_ENERGY_ACCEPTED, TD.Energy.QU, NBT_RECIPEMAP, RM.Replicator         , NBT_INV_SIDE_AUTO_OUT, SIDE_BOTTOM, NBT_TANK_SIDE_AUTO_OUT, SIDE_BOTTOM, NBT_CHEAP_OVERCLOCKING, T, NBT_PARALLEL,  64, NBT_PARALLEL_DURATION, T, NBT_NO_CONSTANT_POWER, T), "F F", "FMF", "FFF", 'M', aRegistry.getItem(18031), 'F', IL.FIELD_GENERATORS[6]);
        
        // 分子对撞机
        aMat = MT.Osmiridium;           aRegistry.appendAddAfter (17198, RegType.GT6U, "Particle Collider"                                  , "Multiblock Machines", 17200, 17101, MultiTileEntityParticleCollider.class   , aMat.mToolQuality, 16, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,  12.5F, NBT_RESISTANCE,  12.5F, NBT_TEXTURE, "particlecollider"        , NBT_INPUT, 8192, NBT_INPUT_MIN,    1, NBT_INPUT_MAX,   524288                        , NBT_ENERGY_ACCEPTED, TD.Energy.EU, NBT_RECIPEMAP, RM_CH.ParticleCollider, NBT_ENERGY_ACCEPTED_2, TD.Energy.EU, NBT_SPECIAL_IS_START_ENERGY, T), "FFF", "FMF", "FFF", 'M', aRegistry.getItem(18014), 'F', IL.FIELD_GENERATORS[5]);
        // 离子发生器
        aMat = MT.Rubber;               aRegistry.appendAddAfter (17198, RegType.GT6U, "Ionizer"                                            , "Multiblock Machines", 17315, 17101, MultiTileEntityIonizer.class            , aMat.mToolQuality, 16, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,  10.0F, NBT_RESISTANCE,  10.0F, NBT_TEXTURE, "ionizer"                 , NBT_INPUT, 2048, NBT_INPUT_MIN, 1024, NBT_INPUT_MAX,   524288                        , NBT_ENERGY_ACCEPTED, TD.Energy.EU, NBT_RECIPEMAP, RM_CH.Ionizer         , NBT_INV_SIDE_AUTO_OUT, SIDE_BOTTOM, NBT_TANK_SIDE_AUTO_OUT, SIDE_BOTTOM, NBT_CHEAP_OVERCLOCKING, T, NBT_PARALLEL,  64, NBT_PARALLEL_DURATION, T                          ),  "GGG", "SwS", "RMC", 'M', aRegistry.getItem(18115), 'R', IL.Processor_Crystal_Ruby, 'C', OD_CIRCUITS[6], 'G', OP.wireGt16.dat(MT.VanadiumGallium), 'S', OP.stickLong.dat(MT.VanadiumGallium));
        // 圆形正负电子对撞机 MARK GT6U 已注释，这里直接移除
        
        // 更多大型热交换器 MARK 输出功率调低 TODO 增加殷钢的
        aMat = MT.TungstenSteel;        aRegistry.appendAddBefore(17197, RegType.GT6U, "Large Heat Exchanger (TungstenSteel)"               , "Multiblock Machines", 18301, 17101, MultiTileEntityLargeHeatExchanger.class  , aMat.mToolQuality, 16, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_TEXTURE, "largeheatexchanger"     , NBT_DESIGN, 18109, NBT_EFFICIENCY,  9000, NBT_OUTPUT, 16384, NBT_FUELMAP, FM.Hot, NBT_ENERGY_EMITTED, TD.Energy.HU), "DDD", "PMP", "DDD", 'M', aRegistry.getItem(18023), 'D', OP.plateDense.dat(MT.AnnealedCopper), 'P', OP.pipeHuge.dat(ANY.Cu));
        aMat = MT.Ta4HfC5;              aRegistry.appendAddAfter (17197, RegType.GT6U, "Large Heat Exchanger (Tantalum Hafnium Carbide)"    , "Multiblock Machines", 18304, 17101, MultiTileEntityLargeHeatExchanger.class  , aMat.mToolQuality, 16, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_TEXTURE, "largeheatexchanger"     , NBT_DESIGN, 18111, NBT_EFFICIENCY,  9500, NBT_OUTPUT, 32768, NBT_FUELMAP, FM.Hot, NBT_ENERGY_EMITTED, TD.Energy.HU), "DDD", "PMP", "DDD", 'M', aRegistry.getItem(18032), 'D', OP.plateDense.dat(MT.AnnealedCopper), 'P', OP.pipeHuge.dat(ANY.Cu));
        
        // 大型干燥器
        aMat = MT.TungstenSteel;        aRegistry.appendAddAfter (17197, RegType.GT6U, "Large TungstenSteel Dryer"                          , "Multiblock Machines", 17316, 17101, MultiTileEntityDryer.class               , aMat.mToolQuality, 16, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_TEXTURE, "largedryer"             , NBT_DESIGN, 18023, NBT_INPUT, 1024,   NBT_INPUT_MIN,     512, NBT_INPUT_MAX,    4096, NBT_ENERGY_ACCEPTED, TD.Energy.HU, NBT_RECIPEMAP, RM.Drying              , NBT_INV_SIDE_AUTO_OUT, SIDE_BOTTOM, NBT_TANK_SIDE_AUTO_OUT, SIDE_BOTTOM, NBT_CHEAP_OVERCLOCKING, T, NBT_PARALLEL,  64, NBT_PARALLEL_DURATION, T                          ),  "GGG", "SwS", "RMC", 'M', aRegistry.getItem(18023), 'R', IL.Processor_Crystal_Ruby, 'C', OD_CIRCUITS[6], 'G', OP.gearGt.dat(aMat), 'S', OP.stick.dat(aMat));
        aMat = ANY.W;                   aRegistry.appendAddAfter (17197, RegType.GT6U, "Large Tungsten Dryer"                               , "Multiblock Machines", 17317, 17101, MultiTileEntityDryer.class               , aMat.mToolQuality, 16, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   9.0F, NBT_RESISTANCE,   9.0F, NBT_TEXTURE, "largedryer"             , NBT_DESIGN, 18024, NBT_INPUT, 4096,   NBT_INPUT_MIN,    2048, NBT_INPUT_MAX,    8192, NBT_ENERGY_ACCEPTED, TD.Energy.HU, NBT_RECIPEMAP, RM.Drying              , NBT_INV_SIDE_AUTO_OUT, SIDE_BOTTOM, NBT_TANK_SIDE_AUTO_OUT, SIDE_BOTTOM, NBT_CHEAP_OVERCLOCKING, T, NBT_PARALLEL, 128, NBT_PARALLEL_DURATION, T                          ),  "GGG", "SwS", "RMC", 'M', aRegistry.getItem(18024), 'R', IL.Processor_Crystal_Ruby, 'C', OD_CIRCUITS[6], 'G', OP.gearGt.dat(aMat), 'S', OP.stick.dat(aMat));
        aMat = MT.Ta4HfC5;              aRegistry.appendAddAfter (17197, RegType.GT6U, "Large Tantalum Hafnium Carbide Dryer"               , "Multiblock Machines", 17318, 17101, MultiTileEntityDryer.class               , aMat.mToolQuality, 16, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,  12.5F, NBT_RESISTANCE,  12.5F, NBT_TEXTURE, "largedryer"             , NBT_DESIGN, 18032, NBT_INPUT,16384,   NBT_INPUT_MIN,    8192, NBT_INPUT_MAX,   32768, NBT_ENERGY_ACCEPTED, TD.Energy.HU, NBT_RECIPEMAP, RM.Drying              , NBT_INV_SIDE_AUTO_OUT, SIDE_BOTTOM, NBT_TANK_SIDE_AUTO_OUT, SIDE_BOTTOM, NBT_CHEAP_OVERCLOCKING, T, NBT_PARALLEL, 256, NBT_PARALLEL_DURATION, T                          ),  "GGG", "SwS", "RMC", 'M', aRegistry.getItem(18032), 'R', IL.Processor_Crystal_Ruby, 'C', OD_CIRCUITS[6], 'G', OP.gearGt.dat(aMat), 'S', OP.stick.dat(aMat));
        
        // 碳化铌钛大型坩埚
        aMat = MT_CH.Nb2Ti3C5;          aRegistry.appendAddBefore(17312, RegType.GT6U, "Large Titanium Niobium Carbide Crucible"            , "Multiblock Machines", 17313, 17101, MultiTileEntityCrucible.class            , aMat.mToolQuality, 16, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,  12.5F, NBT_RESISTANCE,  12.5F, NBT_TEXTURE, "crucible"               , NBT_DESIGN, 18013, NBT_ACIDPROOF, F), "hMy", 'M', aRegistry.getItem(18013));
        
        // 碳化铌钛的大锅炉涡轮机, 注意这些不是所见即所得，为了格式统一有些通用的对于这些机器的修改会在上方对其进行修改
        aMat = MT_CH.Nb2Ti3C5;          aRegistry.appendAddAfter (17203, RegType.GT6U, "Titanium Niobium Carbide Boiler Main Barometer"     , "Multiblock Machines", 17206, 17101, MultiTileEntityLargeBoiler.class         , aMat.mToolQuality, 16, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,  12.5F, NBT_RESISTANCE,  12.5F, NBT_TEXTURE, "largeboiler"            , NBT_DESIGN, 18033, NBT_INPUT,  32768, NBT_EFFICIENCY_CH, 8000, NBT_CAPACITY, 32768*1000, NBT_CAPACITY_SU, 32768*40000), "PPh", "PMP", "wPP", 'M', aRegistry.getItem(18033), 'P', OP.plateDense.dat(aMat));
        aMat = MT_CH.Nb2Ti3C5;          aRegistry.appendAddAfter (17213, RegType.GT6U, "Titanium Niobium Carbide Steam Turbine Main Housing", "Multiblock Machines", 17215, 17101, MultiTileEntityLargeTurbineSteam.class   , aMat.mToolQuality, 16, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,  12.5F, NBT_RESISTANCE,  12.5F, NBT_TEXTURE, "largeturbine"           , NBT_DESIGN, 18033, NBT_INPUT,  49512*STEAM_PER_EU, NBT_OUTPUT,  32768, NBT_WASTE_ENERGY, T, NBT_ENERGY_ACCEPTED, TD.Energy.STEAM, NBT_ENERGY_EMITTED, TD.Energy.RU), "PPP", "PMP", "PPP", 'M', aRegistry.getItem(18013), 'P', OP.blockPlate.dat(MT.Graphene));
        aMat = MT_CH.Nb2Ti3C5;          aRegistry.appendAddAfter (17223, RegType.GT6U, "Titanium Niobium Carbide Dynamo Main Housing"       , "Multiblock Machines", 17225, 17101, MultiTileEntityLargeDynamo.class         , aMat.mToolQuality, 16, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,  12.5F, NBT_RESISTANCE,  12.5F, NBT_TEXTURE, "largedynamo"            , NBT_DESIGN, 18033, NBT_INPUT,  32768, NBT_OUTPUT,  24576, NBT_WASTE_ENERGY, T, NBT_ENERGY_ACCEPTED, TD.Energy.RU, NBT_ENERGY_EMITTED, TD.Energy.EU), "SwS", "CMC", "SBS", 'M', aRegistry.getItem(18033), 'S', OP.stickLong.dat(aMat), 'C', OD_CIRCUITS[6], 'B', "gt:re-battery1");
        aMat = MT_CH.Nb2Ti3C5;          aRegistry.appendAddAfter (17233, RegType.GT6U, "Titanium Niobium Carbide Gas Turbine Main Housing"  , "Multiblock Machines", 17235, 17101, MultiTileEntityLargeTurbineGas.class     , aMat.mToolQuality, 16, aMachine   , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,  12.5F, NBT_RESISTANCE,  12.5F, NBT_TEXTURE, "gasturbine"             , NBT_DESIGN, 18033, NBT_INPUT,  49512, NBT_OUTPUT,  32768, NBT_WASTE_ENERGY, F, NBT_LIMIT_CONSUMPTION, T, NBT_ENERGY_ACCEPTED, TD.Energy.HU, NBT_ENERGY_EMITTED, TD.Energy.RU, NBT_FUELMAP, FM.Gas), "PwP", "BMC", "PEP", 'M', aRegistry.getItem(17215), 'B', "gt:re-battery4", 'C', IL.Processor_Crystal_Diamond, 'E', IL.MOTORS[4], 'P', OP.plateDense.dat(MT.Ta4HfC5));
        
        // MARK 不修改燃气涡轮的合成
        // MARK 移除大型燃油引擎
        // MARK 暂不添加大型等离子涡轮
    }
    @Override protected void multiblocksFinishLoadGT6U(MultiTileEntityRegistry aRegistry, MultiTileEntityBlock aMetal, MultiTileEntityBlock aMetalChips, MultiTileEntityBlock aMetalWires, MultiTileEntityBlock aMachine, MultiTileEntityBlock aWooden, MultiTileEntityBlock aBush, MultiTileEntityBlock aStone, MultiTileEntityBlock aWool, MultiTileEntityBlock aTNT, MultiTileEntityBlock aHive, MultiTileEntityBlock aUtilMetal, MultiTileEntityBlock aUtilStone, MultiTileEntityBlock aUtilWood, MultiTileEntityBlock aUtilWool, OreDictMaterial aMat, Class<? extends TileEntity> aClass) {
        if (!DATA_GTCH.enableGT6U) return;
        
        /// TODO 部分非工作台的合成目前似乎需要在全部初始化完成后再添加才能保证正常工作
        // Wall
        aMat = MT.Ir;                   RM.Welder.addRecipe2(F, 16, 256, OP.plate.mat(aMat, 4), ST.tag(10), aRegistry.getItem(18016));
        aMat = MT_CH.Nb2Ti3C5;          RM.Welder.addRecipe2(F, 16, 256, OP.plate.mat(aMat, 4), ST.tag(10), aRegistry.getItem(18013));
        aMat = MT.Osmiridium;           RM.Welder.addRecipe2(F, 16, 256, OP.plate.mat(aMat, 4), ST.tag(10), aRegistry.getItem(18014));
        aMat = MT_CH.PTFE;              RM.Welder.addRecipe2(F, 16, 256, OP.plate.mat(aMat, 4), ST.tag(10), aRegistry.getItem(18015));
        // Dense Wall
        aMat = MT.Ir;                   OM.data(aRegistry.getItem(18036), aMat, U*36); RM.Welder.addRecipe2(F, 64, 512, OP.plateDense.mat(aMat, 4), ST.tag(10), aRegistry.getItem(18036));
        aMat = MT_CH.Nb2Ti3C5;          OM.data(aRegistry.getItem(18033), aMat, U*36); RM.Welder.addRecipe2(F, 64, 512, OP.plateDense.mat(aMat, 4), ST.tag(10), aRegistry.getItem(18033));
        aMat = MT.Osmiridium;           OM.data(aRegistry.getItem(18034), aMat, U*36); RM.Welder.addRecipe2(F, 64, 512, OP.plateDense.mat(aMat, 4), ST.tag(10), aRegistry.getItem(18034));
        aMat = MT.VanadiumSteel;        OM.data(aRegistry.getItem(18039), aMat, U*36); RM.Welder.addRecipe2(F, 64, 512, OP.plateDense.mat(aMat, 4), ST.tag(10), aRegistry.getItem(18039));
        aMat = MT.TungstenCarbide;      OM.data(aRegistry.getItem(18035), aMat, U*36); RM.Welder.addRecipe2(F, 64, 512, OP.plateDense.mat(aMat, 4), ST.tag(10), aRegistry.getItem(18035));
        
        RM.Canner.addRecipe1(F, 16, 512, aRegistry.getItem(18130), MT.H2SO4.        fluid(U*18, F), NF, aRegistry.getItem(18124));
        RM.Canner.addRecipe1(F, 16, 512, aRegistry.getItem(18131), MT.DistWater.    fluid(U*9,  F), NF, aRegistry.getItem(18125));
        RM.Canner.addRecipe1(F, 16, 512, aRegistry.getItem(18132), MT.DistWater.    fluid(U*9,  F), NF, aRegistry.getItem(18126));
        RM.Canner.addRecipe1(F, 16, 512, aRegistry.getItem(18133), MT.HCl.          fluid(U*18, F), NF, aRegistry.getItem(18127));
        RM.Canner.addRecipe1(F, 16, 512, aRegistry.getItem(18134), MT.HF.           fluid(U*18, F), NF, aRegistry.getItem(18128));
        RM.Canner.addRecipe1(F, 16, 512, aRegistry.getItem(18135), MT_CH.C4H8O3.    fluid(U*18, F), NF, aRegistry.getItem(18129));
    }
}
