package gregtechCH.loaders.b;

import gregapi.block.multitileentity.MultiTileEntityBlock;
import gregapi.block.multitileentity.MultiTileEntityRegistry;
import gregapi.data.*;
import gregapi.oredict.OreDictMaterial;
import gregapi.tileentity.connectors.MultiTileEntityAxle;
import gregapi.tileentity.machines.MultiTileEntityBasicMachine;
import gregapi.util.UT;
import gregtech.loaders.b.Loader_MultiTileEntities;
import gregtech.tileentity.batteries.eu.MultiTileEntityBatteryEU2048;
import gregtech.tileentity.batteries.eu.MultiTileEntityPowerCell;
import gregtech.tileentity.energy.converters.*;
import gregtech.tileentity.energy.generators.*;
import gregtech.tileentity.energy.transformers.MultiTileEntityGearBox;
import gregtech.tileentity.energy.transformers.MultiTileEntityTransformerRotation;
import gregtech.tileentity.multiblocks.MultiTileEntityLargeBoiler;
import gregtech.tileentity.multiblocks.MultiTileEntityLargeTurbineGas;
import gregtech.tileentity.multiblocks.MultiTileEntityLargeTurbineSteam;
import gregtech.tileentity.tools.*;
import gregtechCH.config.machine.generator.*;
import gregtechCH.config.machine.kinetic.*;
import gregtechCH.config.machine.multiblock.AttributesLargeBoilerTank_CH;
import gregtechCH.config.machine.multiblock.AttributesLargeGasTurbine_CH;
import gregtechCH.config.machine.multiblock.AttributesLargeSteamTurbine_CH;
import gregtechCH.config.machine.steam.*;
import gregtechCH.tileentity.batteries.eu.MultiTileEntityBatteryAdvEU8192;
import gregtechCH.tileentity.batteries.eu.MultiTileEntityBatteryEU8192;
import gregtechCH.tileentity.energy.MultiTileEntityMotorGas;
import gregtechCH.tileentity.sensors.*;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import java.util.Objects;

import static gregapi.data.CS.*;
import static gregtechCH.config.ConfigJson_CH.*;
import static gregtechCH.data.CS_CH.*;


/**
 * EMPTY IDS: 23000 - 24999; 15000 - 16999; 9500 - 9999
 * 由于后续 greg 还有比较激进的添加，因此不按照 mod 来分划 id 使用区域
 * TODO 想方法完善 id 改变时能够找到正确的新 id 的方法
 **/
public class Loader_MultiTileEntities_CH extends Loader_MultiTileEntities  {
    /* FORMAT:
    @Override protected void xxxBeforeLoad(MultiTileEntityRegistry aRegistry, MultiTileEntityBlock aMetal, MultiTileEntityBlock aMetalChips, MultiTileEntityBlock aMetalWires, MultiTileEntityBlock aMachine, MultiTileEntityBlock aWooden, MultiTileEntityBlock aBush, MultiTileEntityBlock aStone, MultiTileEntityBlock aWool, MultiTileEntityBlock aTNT, MultiTileEntityBlock aHive, MultiTileEntityBlock aUtilMetal, MultiTileEntityBlock aUtilStone, MultiTileEntityBlock aUtilWood, MultiTileEntityBlock aUtilWool, OreDictMaterial aMat, Class<? extends TileEntity> aClass) {
        /// 修改前标记修改开始
        aRegistry.MODIFYING_ADD_START();
        
        /// 修改项
        
        /// 删除项
        
        /// 添加项（插入到指定位置后或者直接添加到最前）
        
    }
   @Override protected void xxxFinishLoad(MultiTileEntityRegistry aRegistry, MultiTileEntityBlock aMetal, MultiTileEntityBlock aMetalChips, MultiTileEntityBlock aMetalWires, MultiTileEntityBlock aMachine, MultiTileEntityBlock aWooden, MultiTileEntityBlock aBush, MultiTileEntityBlock aStone, MultiTileEntityBlock aWool, MultiTileEntityBlock aTNT, MultiTileEntityBlock aHive, MultiTileEntityBlock aUtilMetal, MultiTileEntityBlock aUtilStone, MultiTileEntityBlock aUtilWood, MultiTileEntityBlock aUtilWool, OreDictMaterial aMat, Class<? extends TileEntity> aClass) {
        /// 添加项（直接添加到最后）
        
        /// 最后标记修改结束，并进行错误检测
        aRegistry.MODIFYING_ADD_END();
    }
    **/
    @Override protected void crucibleBeforeLoad(MultiTileEntityRegistry aRegistry, MultiTileEntityBlock aMetal, MultiTileEntityBlock aMetalChips, MultiTileEntityBlock aMetalWires, MultiTileEntityBlock aMachine, MultiTileEntityBlock aWooden, MultiTileEntityBlock aBush, MultiTileEntityBlock aStone, MultiTileEntityBlock aWool, MultiTileEntityBlock aTNT, MultiTileEntityBlock aHive, MultiTileEntityBlock aUtilMetal, MultiTileEntityBlock aUtilStone, MultiTileEntityBlock aUtilWood, MultiTileEntityBlock aUtilWool, OreDictMaterial aMat, Class<? extends TileEntity> aClass) {
        /// 修改前标记修改开始
        aRegistry.MODIFYING_ADD_START();
        
        /// 添加项
        /* GT6U stuff */
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
    @Override protected void crucibleFinishLoad(MultiTileEntityRegistry aRegistry, MultiTileEntityBlock aMetal, MultiTileEntityBlock aMetalChips, MultiTileEntityBlock aMetalWires, MultiTileEntityBlock aMachine, MultiTileEntityBlock aWooden, MultiTileEntityBlock aBush, MultiTileEntityBlock aStone, MultiTileEntityBlock aWool, MultiTileEntityBlock aTNT, MultiTileEntityBlock aHive, MultiTileEntityBlock aUtilMetal, MultiTileEntityBlock aUtilStone, MultiTileEntityBlock aUtilWood, MultiTileEntityBlock aUtilWool, OreDictMaterial aMat, Class<? extends TileEntity> aClass) {
        /// 最后标记修改结束，并进行错误检测
        aRegistry.MODIFYING_ADD_END();
    }
    
    
    // TODO 可能考虑成直读一个 json 文件后直接循环替换，而原本的结构用于在默认情况自动生成 json
    // TODO 默认的修改改为硬编码的方式实现（提高可读性，添加和替换格式统一），将玩家自己修改在最上面优先度最高，例子放到说明文档（要成为 greg 了），为了方便修改，需要对小的修改有专门的 api
    @Override protected void unsorted1BeforeLoad(MultiTileEntityRegistry aRegistry, MultiTileEntityBlock aMetal, MultiTileEntityBlock aMetalChips, MultiTileEntityBlock aMetalWires, MultiTileEntityBlock aMachine, MultiTileEntityBlock aWooden, MultiTileEntityBlock aBush, MultiTileEntityBlock aStone, MultiTileEntityBlock aWool, MultiTileEntityBlock aTNT, MultiTileEntityBlock aHive, MultiTileEntityBlock aUtilMetal, MultiTileEntityBlock aUtilStone, MultiTileEntityBlock aUtilWood, MultiTileEntityBlock aUtilWool, OreDictMaterial aMat, Class<? extends TileEntity> aClass) {
        /// 添加前将后续添加全部 hold
        aRegistry.MODIFYING_ADD_START();
        
        /// 添加项
        /* GT6U stuff */
        // 碳化铌钛燃烧室 // MARK ID + 1; DOUBLE OUTPUT
        aClass = MultiTileEntityGeneratorMetal.class;
        aMat = MT_CH.Nb2Ti3C5;          aRegistry.appendAddBefore(1109, RegType.GT6U, "Burning Box (Solid, "            +aMat.getLocal()+")", "Burning Boxes"                       ,  1111,  1104, aClass, aMat.mToolQuality, 16, aMachine     , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,  12.5F, NBT_RESISTANCE,  12.5F, NBT_FUELMAP, FM.Furnace,    NBT_EFFICIENCY, 10000, NBT_OUTPUT, 392, NBT_ENERGY_EMITTED, TD.Energy.HU), "PCP", "PwP", "BBB", 'B', Blocks.brick_block, 'P', OP.plate.dat(aMat), 'C', OP.plateDouble.dat(ANY.Cu));
        aMat = MT_CH.Nb2Ti3C5;          aRegistry.appendAddBefore(1159, RegType.GT6U, "Dense Burning Box (Solid, "      +aMat.getLocal()+")", "Burning Boxes"                       ,  1161,  1104, aClass, aMat.mToolQuality, 16, aMachine     , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,  12.5F, NBT_RESISTANCE,  12.5F, NBT_FUELMAP, FM.Furnace,    NBT_EFFICIENCY, 10000, NBT_OUTPUT,1536, NBT_ENERGY_EMITTED, TD.Energy.HU), "PCP", "PwP", "BBB", 'B', Blocks.brick_block, 'P', OP.plateQuintuple.dat(aMat), 'C', OP.plateDense.dat(ANY.Cu));
        aClass = MultiTileEntityGeneratorLiquid.class;
        aMat = MT_CH.Nb2Ti3C5;          aRegistry.appendAddBefore(1409, RegType.GT6U, "Burning Box (Liquid, "           +aMat.getLocal()+")", "Burning Boxes"                       ,  1411,  1104, aClass, aMat.mToolQuality, 16, aMachine     , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,  12.5F, NBT_RESISTANCE,  12.5F, NBT_FUELMAP, FM.Burn,       NBT_EFFICIENCY, 10000, NBT_OUTPUT, 392, NBT_ENERGY_EMITTED, TD.Energy.HU), "PCP", "IwI", "BBB", 'B', Blocks.brick_block, 'P', OP.plate.dat(aMat), 'I', OP.pipeSmall.dat(aMat), 'C', OP.plateDouble.dat(ANY.Cu));
        aMat = MT_CH.Nb2Ti3C5;          aRegistry.appendAddBefore(1459, RegType.GT6U, "Dense Burning Box (Liquid, "     +aMat.getLocal()+")", "Burning Boxes"                       ,  1461,  1104, aClass, aMat.mToolQuality, 16, aMachine     , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,  12.5F, NBT_RESISTANCE,  12.5F, NBT_FUELMAP, FM.Burn,       NBT_EFFICIENCY, 10000, NBT_OUTPUT,1536, NBT_ENERGY_EMITTED, TD.Energy.HU), "PCP", "IwI", "BBB", 'B', Blocks.brick_block, 'P', OP.plateQuintuple.dat(aMat), 'I', OP.pipeLarge.dat(aMat), 'C', OP.plateDense.dat(ANY.Cu));
        aClass = MultiTileEntityGeneratorGas.class;
        aMat = MT_CH.Nb2Ti3C5;          aRegistry.appendAddBefore(1609, RegType.GT6U, "Burning Box (Gas, "              +aMat.getLocal()+")", "Burning Boxes"                       ,  1611,  1104, aClass, aMat.mToolQuality, 16, aMachine     , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,  12.5F, NBT_RESISTANCE,  12.5F, NBT_FUELMAP, FM.Burn,       NBT_EFFICIENCY, 10000, NBT_OUTPUT, 392, NBT_ENERGY_EMITTED, TD.Energy.HU), "PCP", "BwB", "BIB", 'B', Blocks.brick_block, 'P', OP.plate.dat(aMat), 'I', OP.pipeSmall.dat(aMat), 'C', OP.plateDouble.dat(ANY.Cu));
        aMat = MT_CH.Nb2Ti3C5;          aRegistry.appendAddBefore(1659, RegType.GT6U, "Dense Burning Box (Gas, "        +aMat.getLocal()+")", "Burning Boxes"                       ,  1661,  1104, aClass, aMat.mToolQuality, 16, aMachine     , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,  12.5F, NBT_RESISTANCE,  12.5F, NBT_FUELMAP, FM.Burn,       NBT_EFFICIENCY, 10000, NBT_OUTPUT,1536, NBT_ENERGY_EMITTED, TD.Energy.HU), "PCP", "BwB", "BIB", 'B', Blocks.brick_block, 'P', OP.plateQuintuple.dat(aMat), 'I', OP.pipeLarge.dat(aMat), 'C', OP.plateDense.dat(ANY.Cu));
        aClass = MultiTileEntityGeneratorFluidBed.class;
        aMat = MT_CH.Nb2Ti3C5;          aRegistry.appendAddBefore(9009, RegType.GT6U, "Fluidized Bed Burning Box ("     +aMat.getLocal()+")", "Burning Boxes"                       ,  9011,  1104, aClass, aMat.mToolQuality, 16, aMachine     , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,  12.5F, NBT_RESISTANCE,  12.5F, NBT_FUELMAP, FM.FluidBed,   NBT_EFFICIENCY, 10000, NBT_OUTPUT, 768, NBT_ENERGY_EMITTED, TD.Energy.HU), "PCP", "UwU", "BXB", 'B', Blocks.brick_block, 'U', OP.plateCurved.dat(aMat), 'X', OP.rotor.dat(aMat), 'P', OP.plate.dat(aMat), 'C', OP.plateDouble.dat(ANY.Cu));
        aMat = MT_CH.Nb2Ti3C5;          aRegistry.appendAddBefore(9059, RegType.GT6U, "Dense Fluidized Bed Burning Box ("+aMat.getLocal()+")","Burning Boxes"                       ,  9061,  1104, aClass, aMat.mToolQuality, 16, aMachine     , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,  12.5F, NBT_RESISTANCE,  12.5F, NBT_FUELMAP, FM.FluidBed,   NBT_EFFICIENCY, 10000, NBT_OUTPUT,3072, NBT_ENERGY_EMITTED, TD.Energy.HU), "PCP", "UwU", "BXB", 'B', Blocks.brick_block, 'U', OP.plateCurved.dat(aMat), 'X', OP.rotor.dat(aMat), 'P', OP.plateQuintuple.dat(aMat), 'C', OP.plateDense.dat(ANY.Cu));
        
        /// ID: 9140-9149 and 9190-9199 for Diesel Engine; 9160-9169 and 9180-9189 for Small Gas Turbine; (9150-9159 for Dense Heat Exchanger)
        // 钨燃油引擎 // MARK ID 9199 -> 9190; 效率输出合成和 GTCH 统一
        aClass = MultiTileEntityMotorLiquid.class;
        aMat = ANY.W;                   aRegistry.appendAddAfter(9198, RegType.GT6U, "Diesel Engine ("                  +aMat.getLocal()+")", "Engines"                             ,  9190,  1304, aClass, aMat.mToolQuality, 16, aMachine     , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_FUELMAP, FM.Engine, NBT_EFFICIENCY,  3500, NBT_OUTPUT,  256, NBT_PREHEAT_ENERGY,  256*1000, NBT_PREHEAT_RATE,  256*4, NBT_PREHEAT_COST,  256/16, NBT_COOLDOWN_RATE,  256, NBT_ENERGY_EMITTED, TD.Energy.RU), "PLP", "SMS", "GOC", 'M', OP.casingMachineDense.dat(aMat), 'O', OP.pipeQuadruple.dat(aMat), 'P', OP.rotor.dat(aMat), 'S', OP.stickLong.dat(aMat), 'G', OP.gearGt.dat(aMat), 'C', OP.gearGtSmall.dat(aMat), 'L', OD.itemLubricant);
        // 小燃气涡轮 // MARK ID CHANGED
        aClass = MultiTileEntityMotorGas.class;
        aMat = MT.Bronze;               aRegistry.appendAddAfter(9199, RegType.GT6U, "Small Gas Turbine ("              +aMat.getLocal()+")", "Engines"                             ,  9167,  1304, aClass, aMat.mToolQuality, 16, aMachine     , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_FUELMAP, FM.Gas,    NBT_EFFICIENCY,  2750, NBT_OUTPUT,   32, NBT_PREHEAT_ENERGY,   32*4000, NBT_PREHEAT_RATE,   32*1, NBT_PREHEAT_COST,   32/16, NBT_COOLDOWN_RATE,   32, NBT_ENERGY_EMITTED, TD.Energy.RU), "PLP", "SMS", "GOC", 'M', OP.casingMachineDense.dat(aMat), 'O', OP.pipeHuge.dat(aMat), 'P', OP.rotor.dat(aMat), 'S', OP.stickLong.dat(aMat), 'G', OP.gearGt.dat(aMat), 'C', OP.gearGtSmall.dat(aMat), 'L', OD.itemLubricant);
        aMat = ANY.Steel;               aRegistry.appendAddAfter(9199, RegType.GT6U, "Small Gas Turbine ("              +aMat.getLocal()+")", "Engines"                             ,  9168,  1304, aClass, aMat.mToolQuality, 16, aMachine     , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_FUELMAP, FM.Gas,    NBT_EFFICIENCY,  2500, NBT_OUTPUT,   64, NBT_PREHEAT_ENERGY,   64*4000, NBT_PREHEAT_RATE,   64*1, NBT_PREHEAT_COST,   64/16, NBT_COOLDOWN_RATE,   64, NBT_ENERGY_EMITTED, TD.Energy.RU), "PLP", "SMS", "GOC", 'M', OP.casingMachineDense.dat(aMat), 'O', OP.pipeHuge.dat(aMat), 'P', OP.rotor.dat(aMat), 'S', OP.stickLong.dat(aMat), 'G', OP.gearGt.dat(aMat), 'C', OP.gearGtSmall.dat(aMat), 'L', OD.itemLubricant);
        aMat = MT.Invar;                aRegistry.appendAddAfter(9199, RegType.GT6U, "Small Gas Turbine ("              +aMat.getLocal()+")", "Engines"                             ,  9169,  1304, aClass, aMat.mToolQuality, 16, aMachine     , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_FUELMAP, FM.Gas,    NBT_EFFICIENCY,  3500, NBT_OUTPUT,  128, NBT_PREHEAT_ENERGY,  128*4000, NBT_PREHEAT_RATE,  128*1, NBT_PREHEAT_COST,  128/16, NBT_COOLDOWN_RATE,  128, NBT_ENERGY_EMITTED, TD.Energy.RU), "PLP", "SMS", "GOC", 'M', OP.casingMachineDense.dat(aMat), 'O', OP.pipeHuge.dat(aMat), 'P', OP.rotor.dat(aMat), 'S', OP.stickLong.dat(aMat), 'G', OP.gearGt.dat(aMat), 'C', OP.gearGtSmall.dat(aMat), 'L', OD.itemLubricant);
        aMat = MT.Ti;                   aRegistry.appendAddAfter(9199, RegType.GT6U, "Small Gas Turbine ("              +aMat.getLocal()+")", "Engines"                             ,  9187,  1304, aClass, aMat.mToolQuality, 16, aMachine     , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_FUELMAP, FM.Gas,    NBT_EFFICIENCY,  2750, NBT_OUTPUT,  256, NBT_PREHEAT_ENERGY,  256*4000, NBT_PREHEAT_RATE,  256*1, NBT_PREHEAT_COST,  256/16, NBT_COOLDOWN_RATE,  256, NBT_ENERGY_EMITTED, TD.Energy.RU), "PLP", "SMS", "GOC", 'M', OP.casingMachineDense.dat(aMat), 'O', OP.pipeHuge.dat(aMat), 'P', OP.rotor.dat(aMat), 'S', OP.stickLong.dat(aMat), 'G', OP.gearGt.dat(aMat), 'C', OP.gearGtSmall.dat(aMat), 'L', OD.itemLubricant);
        aMat = MT.TungstenSteel;        aRegistry.appendAddAfter(9199, RegType.GT6U, "Small Gas Turbine ("              +aMat.getLocal()+")", "Engines"                             ,  9188,  1304, aClass, aMat.mToolQuality, 16, aMachine     , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_FUELMAP, FM.Gas,    NBT_EFFICIENCY,  3000, NBT_OUTPUT,  512, NBT_PREHEAT_ENERGY,  512*4000, NBT_PREHEAT_RATE,  512*1, NBT_PREHEAT_COST,  512/16, NBT_COOLDOWN_RATE,  512, NBT_ENERGY_EMITTED, TD.Energy.RU), "PLP", "SMS", "GOC", 'M', OP.casingMachineDense.dat(aMat), 'O', OP.pipeHuge.dat(aMat), 'P', OP.rotor.dat(aMat), 'S', OP.stickLong.dat(aMat), 'G', OP.gearGt.dat(aMat), 'C', OP.gearGtSmall.dat(aMat), 'L', OD.itemLubricant);
        aMat = ANY.W;                   aRegistry.appendAddAfter(9199, RegType.GT6U, "Small Gas Turbine ("              +aMat.getLocal()+")", "Engines"                             ,  9180,  1304, aClass, aMat.mToolQuality, 16, aMachine     , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_FUELMAP, FM.Gas,    NBT_EFFICIENCY,  3500, NBT_OUTPUT,  512, NBT_PREHEAT_ENERGY,  512*4000, NBT_PREHEAT_RATE,  512*1, NBT_PREHEAT_COST,  512/16, NBT_COOLDOWN_RATE,  512, NBT_ENERGY_EMITTED, TD.Energy.RU), "PLP", "SMS", "GOC", 'M', OP.casingMachineDense.dat(aMat), 'O', OP.pipeHuge.dat(aMat), 'P', OP.rotor.dat(aMat), 'S', OP.stickLong.dat(aMat), 'G', OP.gearGt.dat(aMat), 'C', OP.gearGtSmall.dat(aMat), 'L', OD.itemLubricant);
        aMat = MT.Ir;                   aRegistry.appendAddAfter(9199, RegType.GT6U, "Small Gas Turbine ("              +aMat.getLocal()+")", "Engines"                             ,  9189,  1304, aClass, aMat.mToolQuality, 16, aMachine     , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_FUELMAP, FM.Gas,    NBT_EFFICIENCY,  3333, NBT_OUTPUT, 1024, NBT_PREHEAT_ENERGY, 1024*4000, NBT_PREHEAT_RATE, 1024*1, NBT_PREHEAT_COST, 1024/16, NBT_COOLDOWN_RATE, 1024, NBT_ENERGY_EMITTED, TD.Energy.RU), "PLP", "SMS", "GOC", 'M', OP.casingMachineDense.dat(aMat), 'O', OP.pipeHuge.dat(aMat), 'P', OP.rotor.dat(aMat), 'S', OP.stickLong.dat(aMat), 'G', OP.gearGt.dat(aMat), 'C', OP.gearGtSmall.dat(aMat), 'L', OD.itemLubricant);
        
        
        /// 修改项
        /* GTCH stuff */
        // Burning Boxes
        aClass = MultiTileEntityGeneratorBrick.class;
        for (AttributesBurningBoxBrick_CH BURNING_BOX_BRICK : DATA_MACHINES_GENERATOR.BurningBoxBrick) {
            aMat = BURNING_BOX_BRICK.material;
            aRegistry.replaceAdd("Brick Burning Box (Solid)",                      "Burning Boxes",  BURNING_BOX_BRICK.ID,         1104, aClass, aMat.mToolQuality, BURNING_BOX_BRICK.stackSize,           aStone,
                UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   BURNING_BOX_BRICK.nbtHardness,          NBT_RESISTANCE,   BURNING_BOX_BRICK.nbtResistance,          NBT_FUELMAP, FM.Furnace, NBT_EFFICIENCY,  BURNING_BOX_BRICK.nbtEfficiency,          NBT_OUTPUT,  BURNING_BOX_BRICK.nbtOutput,           NBT_ENERGY_EMITTED, TD.Energy.HU),
                BURNING_BOX_BRICK.recipeObject);
        }
        aClass = MultiTileEntityGeneratorMetal.class;
        for (AttributesBurningBoxSolid_CH BURNING_BOX_SOLID : DATA_MACHINES_GENERATOR.BurningBoxSolid) {
            aMat = BURNING_BOX_SOLID.material;
            aRegistry.replaceAdd("Burning Box (Solid, "+aMat.getLocal()+")",       "Burning Boxes",  BURNING_BOX_SOLID.ID,         1104, aClass, aMat.mToolQuality, BURNING_BOX_SOLID.stackSize,           aMachine,
                UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   BURNING_BOX_SOLID.nbtHardness,          NBT_RESISTANCE,   BURNING_BOX_SOLID.nbtResistance,          NBT_FUELMAP, FM.Furnace, NBT_EFFICIENCY,  BURNING_BOX_SOLID.nbtEfficiency,          NBT_OUTPUT,  BURNING_BOX_SOLID.nbtOutput,           NBT_ENERGY_EMITTED, TD.Energy.HU),
                BURNING_BOX_SOLID.recipeObject);
        }
        for (AttributesDenseBurningBoxSolid_CH DENSE_BURNING_BOX_SOLID : DATA_MACHINES_GENERATOR.DenseBurningBoxSolid) {
            aMat = DENSE_BURNING_BOX_SOLID.material;
            aRegistry.replaceAdd("Dense Burning Box (Solid, "+aMat.getLocal()+")", "Burning Boxes",  DENSE_BURNING_BOX_SOLID.ID,   1104, aClass, aMat.mToolQuality, DENSE_BURNING_BOX_SOLID.stackSize,     aMachine,
                UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   DENSE_BURNING_BOX_SOLID.nbtHardness,    NBT_RESISTANCE,   DENSE_BURNING_BOX_SOLID.nbtResistance,    NBT_FUELMAP, FM.Furnace, NBT_EFFICIENCY,  DENSE_BURNING_BOX_SOLID.nbtEfficiency,    NBT_OUTPUT,  DENSE_BURNING_BOX_SOLID.nbtOutput,     NBT_ENERGY_EMITTED, TD.Energy.HU),
                DENSE_BURNING_BOX_SOLID.recipeObject);
        }
        
        // Steam Boilers
        aClass = MultiTileEntityBoilerTank.class;
        for (AttributesSteamBoilerTank_CH STEAM_BOILER_TANK : DATA_MACHINES_STEAM.SteamBoilerTank) {
            aMat = STEAM_BOILER_TANK.material;
            aRegistry.replaceAdd("Steam Boiler Tank ("+aMat.getLocal()+")",        "Steam Boilers", STEAM_BOILER_TANK.ID,         1204, aClass, aMat.mToolQuality,STEAM_BOILER_TANK.stackSize,           aMachine,
                UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   STEAM_BOILER_TANK.nbtHardness,          NBT_RESISTANCE,   STEAM_BOILER_TANK.nbtResistance,          NBT_CAPACITY, STEAM_BOILER_TANK.nbtCapacity,        NBT_CAPACITY_SU, STEAM_BOILER_TANK.nbtCapacity_SU,          NBT_INPUT, STEAM_BOILER_TANK.nbtInput,            NBT_EFFICIENCY_CH,STEAM_BOILER_TANK.nbtEfficiency_CH,           NBT_ENERGY_ACCEPTED, TD.Energy.HU,    NBT_OUTPUT_SU,  STEAM_BOILER_TANK.nbtInput*STEAM_PER_EU),
                STEAM_BOILER_TANK.recipeObject);
        }
        for (AttributesStrongSteamBoilerTank_CH STRONG_STEAM_BOILER_TANK : DATA_MACHINES_STEAM.StrongSteamBoilerTank) {
            aMat = STRONG_STEAM_BOILER_TANK.material;
            aRegistry.replaceAdd("Steam Boiler Tank ("+aMat.getLocal()+")",        "Steam Boilers",  STRONG_STEAM_BOILER_TANK.ID,   1204, aClass, aMat.mToolQuality, STRONG_STEAM_BOILER_TANK.stackSize,     aMachine,
                UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   STRONG_STEAM_BOILER_TANK.nbtHardness,   NBT_RESISTANCE,   STRONG_STEAM_BOILER_TANK.nbtResistance,   NBT_CAPACITY, STRONG_STEAM_BOILER_TANK.nbtCapacity, NBT_CAPACITY_SU, STRONG_STEAM_BOILER_TANK.nbtCapacity_SU,   NBT_INPUT,  STRONG_STEAM_BOILER_TANK.nbtInput,    NBT_EFFICIENCY_CH, STRONG_STEAM_BOILER_TANK.nbtEfficiency_CH,   NBT_ENERGY_ACCEPTED, TD.Energy.HU,    NBT_OUTPUT_SU,  STRONG_STEAM_BOILER_TANK.nbtInput*STEAM_PER_EU),
                STRONG_STEAM_BOILER_TANK.recipeObject);
        }
        
        // Steam Engines
        aClass = MultiTileEntityEngineSteam.class;
        for (AttributesSteamEngine_CH STEAM_ENGINE : DATA_MACHINES_STEAM.SteamEngine) {
            aMat = STEAM_ENGINE.material;
            aRegistry.replaceAdd("Steam Engine ("+aMat.getLocal()+")",             "Engines",        STEAM_ENGINE.ID,             1304, aClass, aMat.mToolQuality, STEAM_ENGINE.stackSize,               Objects.equals(aMat, MT.IronWood) ? aWooden : aMachine,
                UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   STEAM_ENGINE.nbtHardness,              NBT_RESISTANCE,   STEAM_ENGINE.nbtResistance,              NBT_EFFICIENCY, STEAM_ENGINE.nbtEfficiency,        NBT_CAPACITY,    STEAM_ENGINE.nbtCapacity,                 NBT_OUTPUT,    STEAM_ENGINE.nbtOutput,              NBT_EFFICIENCY_WATER, STEAM_ENGINE.nbtEfficiencyWater,             NBT_ENERGY_EMITTED, TD.Energy.KU),
                STEAM_ENGINE.recipeObject);
        }
        for (AttributesStrongSteamEngine_CH STRONG_STEAM_ENGINE : DATA_MACHINES_STEAM.StrongSteamEngine) {
            aMat = STRONG_STEAM_ENGINE.material;
            aRegistry.replaceAdd("Strong Steam Engine ("+aMat.getLocal()+")",      "Engines",        STRONG_STEAM_ENGINE.ID,       1304, aClass, aMat.mToolQuality, STRONG_STEAM_ENGINE.stackSize,         Objects.equals(aMat, MT.IronWood) ? aWooden : aMachine,
                UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   STRONG_STEAM_ENGINE.nbtHardness,        NBT_RESISTANCE,   STRONG_STEAM_ENGINE.nbtResistance,        NBT_EFFICIENCY, STRONG_STEAM_ENGINE.nbtEfficiency,  NBT_CAPACITY,    STRONG_STEAM_ENGINE.nbtCapacity,           NBT_OUTPUT,    STRONG_STEAM_ENGINE.nbtOutput,   NBT_EFFICIENCY_WATER, STRONG_STEAM_ENGINE.nbtEfficiencyWater,       NBT_ENERGY_EMITTED, TD.Energy.KU),
                STRONG_STEAM_ENGINE.recipeObject);
        }
        
        // Burning Boxes Liquid
        aClass = MultiTileEntityGeneratorLiquid.class;
        for (AttributesBurningBoxLiquid_CH BURNING_BOX_LIQUID : DATA_MACHINES_GENERATOR.BurningBoxLiquid) {
            aMat = BURNING_BOX_LIQUID.material;
            aRegistry.replaceAdd("Burning Box (Liquid, "+aMat.getLocal()+")",       "Burning Boxes",  BURNING_BOX_LIQUID.ID,       1104, aClass, aMat.mToolQuality, BURNING_BOX_LIQUID.stackSize,         aMachine,
                UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   BURNING_BOX_LIQUID.nbtHardness,        NBT_RESISTANCE,   BURNING_BOX_LIQUID.nbtResistance,        NBT_FUELMAP, FM.Burn, NBT_EFFICIENCY,  BURNING_BOX_LIQUID.nbtEfficiency,        NBT_OUTPUT,  BURNING_BOX_LIQUID.nbtOutput,         NBT_ENERGY_EMITTED, TD.Energy.HU),
                BURNING_BOX_LIQUID.recipeObject);
        }
        for (AttributesDenseBurningBoxLiquid_CH DENSE_BURNING_BOX_LIQUID : DATA_MACHINES_GENERATOR.DenseBurningBoxLiquid) {
            aMat = DENSE_BURNING_BOX_LIQUID.material;
            aRegistry.replaceAdd("Dense Burning Box (Liquid, "+aMat.getLocal()+")",       "Burning Boxes",  DENSE_BURNING_BOX_LIQUID.ID,       1104, aClass, aMat.mToolQuality, DENSE_BURNING_BOX_LIQUID.stackSize,         aMachine,
                UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   DENSE_BURNING_BOX_LIQUID.nbtHardness,        NBT_RESISTANCE,   DENSE_BURNING_BOX_LIQUID.nbtResistance,        NBT_FUELMAP, FM.Burn, NBT_EFFICIENCY,  DENSE_BURNING_BOX_LIQUID.nbtEfficiency,        NBT_OUTPUT,  DENSE_BURNING_BOX_LIQUID.nbtOutput,         NBT_ENERGY_EMITTED, TD.Energy.HU),
                DENSE_BURNING_BOX_LIQUID.recipeObject);
        }
        // Burning Boxes Gas
        aClass = MultiTileEntityGeneratorGas.class;
        for (AttributesBurningBoxGas_CH BURNING_BOX_GAS : DATA_MACHINES_GENERATOR.BurningBoxGas) {
            aMat = BURNING_BOX_GAS.material;
            aRegistry.replaceAdd("Burning Box (Gas, "+aMat.getLocal()+")",       "Burning Boxes",  BURNING_BOX_GAS.ID,       1104, aClass, aMat.mToolQuality, BURNING_BOX_GAS.stackSize,         aMachine,
                UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   BURNING_BOX_GAS.nbtHardness,        NBT_RESISTANCE,   BURNING_BOX_GAS.nbtResistance,        NBT_FUELMAP, FM.Burn, NBT_EFFICIENCY,  BURNING_BOX_GAS.nbtEfficiency,        NBT_OUTPUT,  BURNING_BOX_GAS.nbtOutput,         NBT_ENERGY_EMITTED, TD.Energy.HU),
                BURNING_BOX_GAS.recipeObject);
        }
        for (AttributesDenseBurningBoxGas_CH DENSE_BURNING_BOX_GAS : DATA_MACHINES_GENERATOR.DenseBurningBoxGas) {
            aMat = DENSE_BURNING_BOX_GAS.material;
            aRegistry.replaceAdd("Dense Burning Box (Gas, "+aMat.getLocal()+")",       "Burning Boxes",  DENSE_BURNING_BOX_GAS.ID,       1104, aClass, aMat.mToolQuality, DENSE_BURNING_BOX_GAS.stackSize,         aMachine,
                UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   DENSE_BURNING_BOX_GAS.nbtHardness,        NBT_RESISTANCE,   DENSE_BURNING_BOX_GAS.nbtResistance,        NBT_FUELMAP, FM.Burn, NBT_EFFICIENCY,  DENSE_BURNING_BOX_GAS.nbtEfficiency,        NBT_OUTPUT,  DENSE_BURNING_BOX_GAS.nbtOutput,         NBT_ENERGY_EMITTED, TD.Energy.HU),
                DENSE_BURNING_BOX_GAS.recipeObject);
        }
        // Burning Boxes Fluidized Bed
        aClass = MultiTileEntityGeneratorFluidBed.class;
        for (AttributesBurningBoxFluidizedBed_CH BURNING_BOX_FLUIDIZED_BED : DATA_MACHINES_GENERATOR.BurningBoxFluidizedBed) {
            aMat = BURNING_BOX_FLUIDIZED_BED.material;
            aRegistry.replaceAdd("Fluidized Bed Burning Box ("+aMat.getLocal()+")",       "Burning Boxes",  BURNING_BOX_FLUIDIZED_BED.ID,       1104, aClass, aMat.mToolQuality, BURNING_BOX_FLUIDIZED_BED.stackSize,         aMachine,
                UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   BURNING_BOX_FLUIDIZED_BED.nbtHardness,        NBT_RESISTANCE,   BURNING_BOX_FLUIDIZED_BED.nbtResistance,        NBT_FUELMAP, FM.FluidBed, NBT_EFFICIENCY,  BURNING_BOX_FLUIDIZED_BED.nbtEfficiency,        NBT_OUTPUT,  BURNING_BOX_FLUIDIZED_BED.nbtOutput,         NBT_ENERGY_EMITTED, TD.Energy.HU),
                BURNING_BOX_FLUIDIZED_BED.recipeObject);
        }
        for (AttributesDenseBurningBoxFluidizedBed_CH DENSE_BURNING_BOX_FLUIDIZED_BED : DATA_MACHINES_GENERATOR.DenseBurningBoxFluidizedBed) {
            aMat = DENSE_BURNING_BOX_FLUIDIZED_BED.material;
            aRegistry.replaceAdd("Dense Fluidized Bed Burning Box ("+aMat.getLocal()+")",       "Burning Boxes",  DENSE_BURNING_BOX_FLUIDIZED_BED.ID,       1104, aClass, aMat.mToolQuality, DENSE_BURNING_BOX_FLUIDIZED_BED.stackSize,         aMachine,
                UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   DENSE_BURNING_BOX_FLUIDIZED_BED.nbtHardness,        NBT_RESISTANCE,   DENSE_BURNING_BOX_FLUIDIZED_BED.nbtResistance,        NBT_FUELMAP, FM.FluidBed, NBT_EFFICIENCY,  DENSE_BURNING_BOX_FLUIDIZED_BED.nbtEfficiency,        NBT_OUTPUT,  DENSE_BURNING_BOX_FLUIDIZED_BED.nbtOutput,         NBT_ENERGY_EMITTED, TD.Energy.HU),
                DENSE_BURNING_BOX_FLUIDIZED_BED.recipeObject);
        }
        
        // Heat Exchangers
        aClass = MultiTileEntityGeneratorHotFluid.class;
        for (AttributesHeatExchanger_CH HEAT_EXCHANGER : DATA_MACHINES_GENERATOR.HeatExchanger) {
            aMat = HEAT_EXCHANGER.material;
            aRegistry.replaceAdd("Heat Exchanger ("+aMat.getLocal()+")",       "Burning Boxes",  HEAT_EXCHANGER.ID,       9103, aClass, aMat.mToolQuality, HEAT_EXCHANGER.stackSize,         aMachine,
                UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   HEAT_EXCHANGER.nbtHardness,        NBT_RESISTANCE,   HEAT_EXCHANGER.nbtResistance,        NBT_FUELMAP, FM.Hot, NBT_EFFICIENCY,  HEAT_EXCHANGER.nbtEfficiency,        NBT_OUTPUT,  HEAT_EXCHANGER.nbtOutput,         NBT_ENERGY_EMITTED, TD.Energy.HU),
                HEAT_EXCHANGER.recipeObject);
        }
        for (AttributesDenseHeatExchanger_CH DENSE_HEAT_EXCHANGER : DATA_MACHINES_GENERATOR.DenseHeatExchanger) {
            aMat = DENSE_HEAT_EXCHANGER.material;
            aRegistry.replaceAdd("Dense Heat Exchanger ("+aMat.getLocal()+")",       "Burning Boxes",  DENSE_HEAT_EXCHANGER.ID,       9103, aClass, aMat.mToolQuality, DENSE_HEAT_EXCHANGER.stackSize,         aMachine,
                UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   DENSE_HEAT_EXCHANGER.nbtHardness,        NBT_RESISTANCE,   DENSE_HEAT_EXCHANGER.nbtResistance,        NBT_FUELMAP, FM.Hot, NBT_EFFICIENCY,  DENSE_HEAT_EXCHANGER.nbtEfficiency,        NBT_OUTPUT,  DENSE_HEAT_EXCHANGER.nbtOutput,         NBT_ENERGY_EMITTED, TD.Energy.HU),
                DENSE_HEAT_EXCHANGER.recipeObject);
        }
        // Diesel Engines
        aClass = MultiTileEntityMotorLiquid.class;
        for (AttributesDieselEngine_CH DIESEL_ENGINE : DATA_MACHINES_GENERATOR.DieselEngine) {
            aMat = DIESEL_ENGINE.material;
            aRegistry.replaceAdd("Diesel Engine ("+aMat.getLocal()+")", "Engines", DIESEL_ENGINE.ID, 1304, aClass, aMat.mToolQuality, DIESEL_ENGINE.stackSize, aMachine,
                UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS, DIESEL_ENGINE.nbtHardness, NBT_RESISTANCE, DIESEL_ENGINE.nbtResistance, NBT_FUELMAP, FM.Engine, NBT_EFFICIENCY,  DIESEL_ENGINE.nbtEfficiency, NBT_OUTPUT, DIESEL_ENGINE.nbtOutput, NBT_PREHEAT_ENERGY,  DIESEL_ENGINE.nbtPreheatEnergy, NBT_PREHEAT_RATE ,  DIESEL_ENGINE.nbtPreheatRate, NBT_PREHEAT_COST,   DIESEL_ENGINE.nbtPreheatCost, NBT_COOLDOWN_RATE,   DIESEL_ENGINE.nbtCooldownRate, NBT_ENERGY_EMITTED, TD.Energy.RU),
                DIESEL_ENGINE.recipeObject);
        }
        
        // Steam Turbines
        aClass = MultiTileEntityTurbineSteam.class;
        for (AttributesSteamTurbine_CH STEAM_TURBINE : DATA_MACHINES_STEAM.SteamTurbine) {
            aMat = STEAM_TURBINE.material;
            aRegistry.replaceAdd("Steam Turbine ("+STEAM_TURBINE.rotorMaterial.getLocal()+")" , "Turbines",  STEAM_TURBINE.ID,  1538, aClass, aMat.mToolQuality, STEAM_TURBINE.stackSize, aMachine,
                UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   STEAM_TURBINE.nbtHardness, NBT_RESISTANCE,   STEAM_TURBINE.nbtResistance, NBT_OUTPUT,   STEAM_TURBINE.nbtOutput, NBT_EFFICIENCY, STEAM_TURBINE.nbtEfficiency, NBT_PREHEAT_ENERGY,  STEAM_TURBINE.nbtPreheatEnergy, NBT_PREHEAT_COST,   STEAM_TURBINE.nbtPreheatCost, NBT_COOLDOWN_RATE,   STEAM_TURBINE.nbtCooldownRate, NBT_EFFICIENCY_WATER, STEAM_TURBINE.nbtEfficiencyWater, NBT_EFFICIENCY_OC, STEAM_TURBINE.nbtEfficiencyOC, NBT_ENERGY_ACCEPTED, TD.Energy.STEAM, NBT_ENERGY_EMITTED, TD.Energy.RU
                    , NBT_INPUT,   UT.Code.units(STEAM_TURBINE.nbtOutput, STEAM_TURBINE.nbtEfficiency, 10000, T) * STEAM_PER_EU, NBT_WASTE_ENERGY, T),
                STEAM_TURBINE.recipeObject);
        }
        
        /// 删除项
//        aRegistry.removeHolding(9220); // TEST REMOVE
//        aRegistry.removeHolding(9320); // TEST REMOVE
    }
    @Override protected void unsorted1FinishLoad(MultiTileEntityRegistry aRegistry, MultiTileEntityBlock aMetal, MultiTileEntityBlock aMetalChips, MultiTileEntityBlock aMetalWires, MultiTileEntityBlock aMachine, MultiTileEntityBlock aWooden, MultiTileEntityBlock aBush, MultiTileEntityBlock aStone, MultiTileEntityBlock aWool, MultiTileEntityBlock aTNT, MultiTileEntityBlock aHive, MultiTileEntityBlock aUtilMetal, MultiTileEntityBlock aUtilStone, MultiTileEntityBlock aUtilWood, MultiTileEntityBlock aUtilWool, OreDictMaterial aMat, Class<? extends TileEntity> aClass) {
        /// 最后释放这些修改后的添加
        aRegistry.MODIFYING_ADD_END();
    }
    
    
    @Override protected void unsorted2BeforeLoad(MultiTileEntityRegistry aRegistry, MultiTileEntityBlock aMetal, MultiTileEntityBlock aMetalChips, MultiTileEntityBlock aMetalWires, MultiTileEntityBlock aMachine, MultiTileEntityBlock aWooden, MultiTileEntityBlock aBush, MultiTileEntityBlock aStone, MultiTileEntityBlock aWool, MultiTileEntityBlock aTNT, MultiTileEntityBlock aHive, MultiTileEntityBlock aUtilMetal, MultiTileEntityBlock aUtilStone, MultiTileEntityBlock aUtilWood, MultiTileEntityBlock aUtilWool, OreDictMaterial aMat, Class<? extends TileEntity> aClass) {
        /// 添加前将后续添加全部 hold
        aRegistry.MODIFYING_ADD_START();
        
        /// 修改项
        aRegistry.replaceAdd("Power Cell (Hydrogen)"                          , "Portable Power Cells"                , 14701, 14700, MultiTileEntityPowerCell.class, 0, 16, aUtilMetal, UT.NBT.make(NBT_HARDNESS, 0.5F, NBT_RESISTANCE, 1.0F, NBT_ENERGY_ACCEPTED, TD.Energy.EU, NBT_COLOR_BOTTOM, UT.Code.getRGBInt(MT.H  .fRGBaGas), NBT_INPUT_MIN, 1, NBT_INPUT, V[3], NBT_INPUT_MAX, V   [3], NBT_CAPACITY,         3_200_000L));
        
        /// 添加项
        /* GT6U stuff */
        // IV 电池
        aRegistry.appendAddAfter(14004, RegType.GT6U, "Lead-Acid Battery ("                 +VN[5]+")", "Batteries"                         , 14005, 14013, MultiTileEntityBatteryEU8192.class,     0, 16, aUtilMetal , UT.NBT.make(NBT_HARDNESS,   0.5F, NBT_RESISTANCE,   3.0F, NBT_COLOR, DYES_INT[DYE_INDEX_Orange] , NBT_INPUT, V[5], NBT_CAPACITY, V[4] *   2000, NBT_ENERGY_ACCEPTED, TD.Energy.EU), "WPW", "BCB", "BBB", 'P', OP.plate.dat(MT.BatteryAlloy), 'B', IL.Battery_Lead_Acid_Cell_Filled, 'W', MT.DATA.CABLES_01[5], 'C', OD_CIRCUITS[5]);
        aRegistry.appendAddAfter(14014, RegType.GT6U, "Alkaline Battery ("                  +VN[5]+")", "Batteries"                         , 14015, 14013, MultiTileEntityBatteryEU8192.class,     0, 16, aUtilMetal , UT.NBT.make(NBT_HARDNESS,   0.5F, NBT_RESISTANCE,   3.0F, NBT_COLOR, DYES_INT[DYE_INDEX_Blue]   , NBT_INPUT, V[5], NBT_CAPACITY, V[4] *   4000, NBT_ENERGY_ACCEPTED, TD.Energy.EU), "WPW", "BCB", "BBB", 'P', OP.plate.dat(MT.BatteryAlloy), 'B', IL.Battery_Alkaline_Cell_Filled, 'W', MT.DATA.CABLES_01[5], 'C', OD_CIRCUITS[5]);
        aRegistry.appendAddAfter(14024, RegType.GT6U, "Nickel-Cadmium Battery ("            +VN[5]+")", "Batteries"                         , 14025, 14013, MultiTileEntityBatteryEU8192.class,     0, 16, aUtilMetal , UT.NBT.make(NBT_HARDNESS,   0.5F, NBT_RESISTANCE,   3.0F, NBT_COLOR, DYES_INT[DYE_INDEX_Lime]   , NBT_INPUT, V[5], NBT_CAPACITY, V[4] *   4000, NBT_ENERGY_ACCEPTED, TD.Energy.EU), "WPW", "BCB", "BBB", 'P', OP.plate.dat(MT.BatteryAlloy), 'B', IL.Battery_NiCd_Cell_Filled, 'W', MT.DATA.CABLES_01[5], 'C', OD_CIRCUITS[5]);
        aRegistry.appendAddAfter(14034, RegType.GT6U, "Lithium-Cobalt Battery ("            +VN[5]+")", "Batteries"                         , 14035, 14013, MultiTileEntityBatteryAdvEU8192.class , 0, 16, aUtilMetal , UT.NBT.make(NBT_HARDNESS,   0.5F, NBT_RESISTANCE,   3.0F, NBT_COLOR, DYES_INT[DYE_INDEX_Blue]   , NBT_INPUT, V[5], NBT_CAPACITY, V[4] *  64000, NBT_ENERGY_ACCEPTED, TD.Energy.EU), "WPW", "BCB", "BBB", 'P', OP.plate.dat(MT.BatteryAlloy), 'B', IL.Battery_LiCoO2_Cell_Filled, 'W', MT.DATA.CABLES_01[5], 'C', OD_CIRCUITS[6]);
        aRegistry.appendAddAfter(14044, RegType.GT6U, "Lithium-Manganese Battery ("         +VN[5]+")", "Batteries"                         , 14045, 14013, MultiTileEntityBatteryAdvEU8192.class , 0, 16, aUtilMetal , UT.NBT.make(NBT_HARDNESS,   0.5F, NBT_RESISTANCE,   3.0F, NBT_COLOR, DYES_INT[DYE_INDEX_Green]  , NBT_INPUT, V[5], NBT_CAPACITY, V[4] * 128000, NBT_ENERGY_ACCEPTED, TD.Energy.EU), "WPW", "BCB", "BBB", 'P', OP.plate.dat(MT.BatteryAlloy), 'B', IL.Battery_LiMn_Cell_Filled, 'W', MT.DATA.CABLES_01[5], 'C', OD_CIRCUITS[6]);
        
        
        /* GTCH stuff */
        // Dynamos
        aClass = MultiTileEntityDynamoElectric.class;
        aMat = MT.DATA.Electric_T[0];   aRegistry.appendAddBefore(10111, "Electric Dynamo ("              +VN[0]+")", "Dynamos"                             , 10110, 10111, aClass, aMat.mToolQuality, 16, aMachine     , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   4.0F, NBT_RESISTANCE,   4.0F, NBT_INPUT,   14, NBT_OUTPUT,    8, NBT_WASTE_ENERGY, T, NBT_ENERGY_ACCEPTED, TD.Energy.RU, NBT_ENERGY_EMITTED, TD.Energy.EU), "TGT", "CMC", "TId", 'M', OP.casingMachineDouble.dat(aMat), 'T', OP.screw.dat(aMat), 'G', OP.gearGt.dat(aMat), 'I', OP.stickLong.dat(MT.IronMagnetic     ), 'C', OP.wireGt01.dat(ANY.Cu));
    }
    @Override protected void unsorted2FinishLoad(MultiTileEntityRegistry aRegistry, MultiTileEntityBlock aMetal, MultiTileEntityBlock aMetalChips, MultiTileEntityBlock aMetalWires, MultiTileEntityBlock aMachine, MultiTileEntityBlock aWooden, MultiTileEntityBlock aBush, MultiTileEntityBlock aStone, MultiTileEntityBlock aWool, MultiTileEntityBlock aTNT, MultiTileEntityBlock aHive, MultiTileEntityBlock aUtilMetal, MultiTileEntityBlock aUtilStone, MultiTileEntityBlock aUtilWood, MultiTileEntityBlock aUtilWool, OreDictMaterial aMat, Class<? extends TileEntity> aClass) {
        /// 最后释放这些修改后的添加
        aRegistry.MODIFYING_ADD_END();
        
        /// 附加修改部分
        /* GT6U stuff */
        // IV 电池的 IL 设定
        IL.Battery_Lead_Acid_IV .set(aRegistry.getItem(14005), null, "gt:re-battery5");
        IL.Battery_Alkaline_IV  .set(aRegistry.getItem(14015), null, "gt:re-battery5");
        IL.Battery_NiCd_IV      .set(aRegistry.getItem(14025), null, "gt:re-battery5");
        IL.Battery_LiCoO2_IV    .set(aRegistry.getItem(14035), null, "gt:re-battery5");
        IL.Battery_LiMn_IV      .set(aRegistry.getItem(14045), null, "gt:re-battery5");
    }
    
    
    @Override protected void multiblocksBeforeLoad(MultiTileEntityRegistry aRegistry, MultiTileEntityBlock aMetal, MultiTileEntityBlock aMetalChips, MultiTileEntityBlock aMetalWires, MultiTileEntityBlock aMachine, MultiTileEntityBlock aWooden, MultiTileEntityBlock aBush, MultiTileEntityBlock aStone, MultiTileEntityBlock aWool, MultiTileEntityBlock aTNT, MultiTileEntityBlock aHive, MultiTileEntityBlock aUtilMetal, MultiTileEntityBlock aUtilStone, MultiTileEntityBlock aUtilWood, MultiTileEntityBlock aUtilWool, OreDictMaterial aMat, Class<? extends TileEntity> aClass) {
        /// 添加前将后续添加全部 hold
        aRegistry.MODIFYING_ADD_START();
        
        /// 修改项
        // Boilers
        aClass = MultiTileEntityLargeBoiler.class;
        for (AttributesLargeBoilerTank_CH BOILER_TANK : DATA_MACHINES_MULTIBLOCK.LargeBoilerTank) {
            aMat = BOILER_TANK.material;
            aRegistry.replaceAdd(aMat.getLocal() + "Stainless Steel" + " Boiler Main Barometer" , "Multiblock Machines", BOILER_TANK.ID, 17101, aClass, aMat.mToolQuality, BOILER_TANK.stackSize, aMachine,
                UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   BOILER_TANK.nbtHardness, NBT_RESISTANCE,   BOILER_TANK.nbtResistance, NBT_TEXTURE, "largeboiler", NBT_DESIGN, BOILER_TANK.nbtDesign, NBT_CAPACITY,  BOILER_TANK.nbtCapacity, NBT_CAPACITY_SU,  BOILER_TANK.nbtCapacity_SU, NBT_INPUT,  BOILER_TANK.nbtInput,   NBT_EFFICIENCY_CH, BOILER_TANK.nbtEfficiency_CH, NBT_OUTPUT_SU,  BOILER_TANK.nbtInput*STEAM_PER_EU),
                BOILER_TANK.recipeObject);
        }
        
        // Steam Turbines
        NBTTagCompound tNBT;
        aClass = MultiTileEntityLargeTurbineSteam.class;
        for (AttributesLargeSteamTurbine_CH STEAM_TURBINE : DATA_MACHINES_MULTIBLOCK.LargeSteamTurbine) {
            aMat = STEAM_TURBINE.material;
            tNBT = UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS, STEAM_TURBINE.nbtHardness, NBT_RESISTANCE, STEAM_TURBINE.nbtResistance, NBT_TEXTURE, "largeturbine", NBT_DESIGN, STEAM_TURBINE.nbtDesign, NBT_EFFICIENCY_WATER, STEAM_TURBINE.nbtEfficiencyWater, NBT_LENGTH_MIN, STEAM_TURBINE.nbtLengthMin, NBT_LENGTH_MAX, STEAM_TURBINE.nbtLengthMax, NBT_LENGTH_MID, STEAM_TURBINE.nbtLengthMid, NBT_EFFICIENCY_OC, STEAM_TURBINE.nbtEfficiencyOC, NBT_ENERGY_ACCEPTED, TD.Energy.STEAM, NBT_ENERGY_EMITTED, TD.Energy.RU
                , NBT_OUTPUT, STEAM_TURBINE.nbtOutput[STEAM_TURBINE.nbtLengthMid-STEAM_TURBINE.nbtLengthMin], NBT_EFFICIENCY, STEAM_TURBINE.nbtEfficiency[STEAM_TURBINE.nbtLengthMid-STEAM_TURBINE.nbtLengthMin], NBT_INPUT, UT.Code.units(STEAM_TURBINE.nbtOutput[STEAM_TURBINE.nbtLengthMid-STEAM_TURBINE.nbtLengthMin], STEAM_TURBINE.nbtEfficiency[STEAM_TURBINE.nbtLengthMid-STEAM_TURBINE.nbtLengthMin], 10000, T) * STEAM_PER_EU, NBT_WASTE_ENERGY, T);
            for (int i = 0; i < STEAM_TURBINE.nbtLengthMax-STEAM_TURBINE.nbtLengthMin + 1; ++i) {
                UT.NBT.setNumber(tNBT, NBT_EFFICIENCY+"."+i, STEAM_TURBINE.nbtEfficiency[i]);
                UT.NBT.setNumber(tNBT, NBT_OUTPUT+"."+i, STEAM_TURBINE.nbtOutput[i]);
                UT.NBT.setNumber(tNBT, NBT_PREHEAT_ENERGY+"."+i, STEAM_TURBINE.nbtPreheatEnergy[i]);
                UT.NBT.setNumber(tNBT, NBT_PREHEAT_COST+"."+i, STEAM_TURBINE.nbtPreheatCost[i]);
                UT.NBT.setNumber(tNBT, NBT_COOLDOWN_RATE+"."+i, STEAM_TURBINE.nbtCooldownRate[i]);
            }
            aRegistry.replaceAdd(STEAM_TURBINE.rotorMaterial.getLocal() + " Steam Turbine Main Housing", "Multiblock Machines", STEAM_TURBINE.ID, 17101, aClass  , aMat.mToolQuality, STEAM_TURBINE.stackSize, aMachine,
                tNBT, STEAM_TURBINE.recipeObject);
        }
        
        // Gas Turbines
        aClass = MultiTileEntityLargeTurbineGas.class;
        for (AttributesLargeGasTurbine_CH GAS_TURBINE : DATA_MACHINES_MULTIBLOCK.LargeGasTurbine) {
            aMat = GAS_TURBINE.material;
            tNBT = UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   GAS_TURBINE.nbtHardness, NBT_RESISTANCE,   GAS_TURBINE.nbtResistance, NBT_TEXTURE, "gasturbine", NBT_DESIGN, GAS_TURBINE.nbtDesign, NBT_LENGTH_MIN, GAS_TURBINE.nbtLengthMin, NBT_LENGTH_MAX, GAS_TURBINE.nbtLengthMax, NBT_LENGTH_MID, GAS_TURBINE.nbtLengthMid, NBT_ENERGY_EMITTED, TD.Energy.RU, NBT_FUELMAP, FM.Gas
                , NBT_OUTPUT,   GAS_TURBINE.nbtOutput[GAS_TURBINE.nbtLengthMid-GAS_TURBINE.nbtLengthMin], NBT_EFFICIENCY, GAS_TURBINE.nbtEfficiency[GAS_TURBINE.nbtLengthMid-GAS_TURBINE.nbtLengthMin], NBT_INPUT, GAS_TURBINE.nbtOutput[GAS_TURBINE.nbtLengthMid-GAS_TURBINE.nbtLengthMin], NBT_WASTE_ENERGY, F, NBT_LIMIT_CONSUMPTION, T, NBT_ENERGY_ACCEPTED, TD.Energy.HU);
            for (int i = 0; i < GAS_TURBINE.nbtLengthMax-GAS_TURBINE.nbtLengthMin + 1; ++i) {
                UT.NBT.setNumber(tNBT, NBT_EFFICIENCY+"."+i, GAS_TURBINE.nbtEfficiency[i]);
                UT.NBT.setNumber(tNBT, NBT_OUTPUT+"."+i, GAS_TURBINE.nbtOutput[i]);
                UT.NBT.setNumber(tNBT, NBT_PREHEAT_ENERGY+"."+i, GAS_TURBINE.nbtPreheatEnergy[i]);
                UT.NBT.setNumber(tNBT, NBT_PREHEAT_COST+"."+i, GAS_TURBINE.nbtPreheatCost[i]);
                UT.NBT.setNumber(tNBT, NBT_COOLDOWN_RATE+"."+i, GAS_TURBINE.nbtCooldownRate[i]);
                UT.NBT.setNumber(tNBT, NBT_PREHEAT_RATE+"."+i, GAS_TURBINE.nbtPreheatRate[i]);
            }
            aRegistry.replaceAdd(GAS_TURBINE.rotorMaterial.getLocal() + " Gas Turbine Main Housing", "Multiblock Machines", GAS_TURBINE.ID, 17101, aClass  , aMat.mToolQuality, GAS_TURBINE.stackSize, aMachine,
                tNBT, GAS_TURBINE.recipeObject);
        }
    }
    @Override protected void multiblocksFinishLoad(MultiTileEntityRegistry aRegistry, MultiTileEntityBlock aMetal, MultiTileEntityBlock aMetalChips, MultiTileEntityBlock aMetalWires, MultiTileEntityBlock aMachine, MultiTileEntityBlock aWooden, MultiTileEntityBlock aBush, MultiTileEntityBlock aStone, MultiTileEntityBlock aWool, MultiTileEntityBlock aTNT, MultiTileEntityBlock aHive, MultiTileEntityBlock aUtilMetal, MultiTileEntityBlock aUtilStone, MultiTileEntityBlock aUtilWood, MultiTileEntityBlock aUtilWool, OreDictMaterial aMat, Class<? extends TileEntity> aClass) {
        /// 最后释放这些修改后的添加
        aRegistry.MODIFYING_ADD_END();
    }
    
    
    // TODO 这些微小修改提供专门的 api 来进一步减少重复代码
    @Override protected void machines1BeforeLoad(MultiTileEntityRegistry aRegistry, MultiTileEntityBlock aMetal, MultiTileEntityBlock aMetalChips, MultiTileEntityBlock aMetalWires, MultiTileEntityBlock aMachine, MultiTileEntityBlock aWooden, MultiTileEntityBlock aBush, MultiTileEntityBlock aStone, MultiTileEntityBlock aWool, MultiTileEntityBlock aTNT, MultiTileEntityBlock aHive, MultiTileEntityBlock aUtilMetal, MultiTileEntityBlock aUtilStone, MultiTileEntityBlock aUtilWood, MultiTileEntityBlock aUtilWool, OreDictMaterial aMat, Class<? extends TileEntity> aClass) {
        /// 添加前将后续添加全部 hold
        aRegistry.MODIFYING_ADD_START();
        
        /// 修改项
        aClass = MultiTileEntityBasicMachine.class;
        aMat = MT.DATA.Heat_T[1];       aRegistry.replaceAdd("Oven ("                          +aMat.getLocal()+")", "Basic Machines"                      , 20001, 20001, aClass, aMat.mToolQuality, 16, aMachine     , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_INPUT,   32, NBT_TEXTURE, "oven", NBT_ENERGY_ACCEPTED, TD.Energy.HU, NBT_RECIPEMAP, RM.Furnace, NBT_INV_SIDE_IN, SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_TANK_SIDE_OUT, 63, NBT_ENERGY_ACCEPTED_SIDES, SBIT_D, NBT_PARALLEL, 4, NBT_PARALLEL_DURATION, T), "wMh", "BCB", 'M', OP.casingMachine.dat(aMat), 'C', OP.plateDouble.dat(ANY.Cu), 'B', Blocks.brick_block);
        aMat = MT.DATA.Heat_T[2];       aRegistry.replaceAdd("Oven ("                          +aMat.getLocal()+")", "Basic Machines"                      , 20002, 20001, aClass, aMat.mToolQuality, 16, aMachine     , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   4.0F, NBT_RESISTANCE,   4.0F, NBT_INPUT,  128, NBT_TEXTURE, "oven", NBT_ENERGY_ACCEPTED, TD.Energy.HU, NBT_RECIPEMAP, RM.Furnace, NBT_INV_SIDE_IN, SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_TANK_SIDE_OUT, 63, NBT_ENERGY_ACCEPTED_SIDES, SBIT_D, NBT_PARALLEL, 8, NBT_PARALLEL_DURATION, T), "wMh", "BCB", 'M', OP.casingMachine.dat(aMat), 'C', OP.plateDouble.dat(ANY.Cu), 'B', Blocks.brick_block);
        aMat = MT.DATA.Heat_T[3];       aRegistry.replaceAdd("Oven ("                          +aMat.getLocal()+")", "Basic Machines"                      , 20003, 20001, aClass, aMat.mToolQuality, 16, aMachine     , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   9.0F, NBT_RESISTANCE,   9.0F, NBT_INPUT,  512, NBT_TEXTURE, "oven", NBT_ENERGY_ACCEPTED, TD.Energy.HU, NBT_RECIPEMAP, RM.Furnace, NBT_INV_SIDE_IN, SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_TANK_SIDE_OUT, 63, NBT_ENERGY_ACCEPTED_SIDES, SBIT_D, NBT_PARALLEL,16, NBT_PARALLEL_DURATION, T), "wMh", "BCB", 'M', OP.casingMachine.dat(aMat), 'C', OP.plateDouble.dat(ANY.Cu), 'B', Blocks.brick_block);
        aMat = MT.DATA.Heat_T[4];       aRegistry.replaceAdd("Oven ("                          +aMat.getLocal()+")", "Basic Machines"                      , 20004, 20001, aClass, aMat.mToolQuality, 16, aMachine     , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,  12.5F, NBT_RESISTANCE,  12.5F, NBT_INPUT, 2048, NBT_TEXTURE, "oven", NBT_ENERGY_ACCEPTED, TD.Energy.HU, NBT_RECIPEMAP, RM.Furnace, NBT_INV_SIDE_IN, SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_TANK_SIDE_OUT, 63, NBT_ENERGY_ACCEPTED_SIDES, SBIT_D, NBT_PARALLEL,32, NBT_PARALLEL_DURATION, T), "wMh", "BCB", 'M', OP.casingMachine.dat(aMat), 'C', OP.plateDouble.dat(ANY.Cu), 'B', Blocks.brick_block);
    }
    @Override protected void machines1FinishLoad(MultiTileEntityRegistry aRegistry, MultiTileEntityBlock aMetal, MultiTileEntityBlock aMetalChips, MultiTileEntityBlock aMetalWires, MultiTileEntityBlock aMachine, MultiTileEntityBlock aWooden, MultiTileEntityBlock aBush, MultiTileEntityBlock aStone, MultiTileEntityBlock aWool, MultiTileEntityBlock aTNT, MultiTileEntityBlock aHive, MultiTileEntityBlock aUtilMetal, MultiTileEntityBlock aUtilStone, MultiTileEntityBlock aUtilWood, MultiTileEntityBlock aUtilWool, OreDictMaterial aMat, Class<? extends TileEntity> aClass) {
        /// 最后释放这些修改后的添加
        aRegistry.MODIFYING_ADD_END();
    }
    
    
    @Override protected void machines3BeforeLoad(MultiTileEntityRegistry aRegistry, MultiTileEntityBlock aMetal, MultiTileEntityBlock aMetalChips, MultiTileEntityBlock aMetalWires, MultiTileEntityBlock aMachine, MultiTileEntityBlock aWooden, MultiTileEntityBlock aBush, MultiTileEntityBlock aStone, MultiTileEntityBlock aWool, MultiTileEntityBlock aTNT, MultiTileEntityBlock aHive, MultiTileEntityBlock aUtilMetal, MultiTileEntityBlock aUtilStone, MultiTileEntityBlock aUtilWood, MultiTileEntityBlock aUtilWool, OreDictMaterial aMat, Class<? extends TileEntity> aClass) {
        /// 添加前将后续添加全部 hold
        aRegistry.MODIFYING_ADD_START();
        
        /// 修改项
        aClass = MultiTileEntityBasicMachine.class;
        aMat = MT.DATA.Heat_T[1];       aRegistry.replaceAdd("Steam Cracker ("                 +aMat.getLocal()+")", "Basic Machines"                      , 20491, 20001, aClass, aMat.mToolQuality, 16, aMachine     , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_INPUT,   32, NBT_TEXTURE, "steamcracker", NBT_ENERGY_ACCEPTED, TD.Energy.HU, NBT_RECIPEMAP, RM.SteamCracking, NBT_INV_SIDE_IN, SBIT_U|SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_TOP, NBT_INV_SIDE_OUT, SBIT_R|SBIT_B, NBT_INV_SIDE_AUTO_OUT, SIDE_BACK, NBT_TANK_SIDE_IN, SBIT_U|SBIT_L, NBT_TANK_SIDE_AUTO_IN, SIDE_LEFT, NBT_TANK_SIDE_OUT, SBIT_R|SBIT_B, NBT_TANK_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_ENERGY_ACCEPTED_SIDES, SBIT_D, NBT_CANFILL_STEAM, T), "IwI", "PMP", "ICI", 'M', OP.casingMachineDouble.dat(aMat), 'C', OP.plateDouble   .dat(ANY.Cu), 'I', OP.plateDouble   .dat(MT.Invar), 'P', OP.pipeMedium.dat(aMat));
        aMat = MT.DATA.Heat_T[2];       aRegistry.replaceAdd("Steam Cracker ("                 +aMat.getLocal()+")", "Basic Machines"                      , 20492, 20001, aClass, aMat.mToolQuality, 16, aMachine     , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   4.0F, NBT_RESISTANCE,   4.0F, NBT_INPUT,  128, NBT_TEXTURE, "steamcracker", NBT_ENERGY_ACCEPTED, TD.Energy.HU, NBT_RECIPEMAP, RM.SteamCracking, NBT_INV_SIDE_IN, SBIT_U|SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_TOP, NBT_INV_SIDE_OUT, SBIT_R|SBIT_B, NBT_INV_SIDE_AUTO_OUT, SIDE_BACK, NBT_TANK_SIDE_IN, SBIT_U|SBIT_L, NBT_TANK_SIDE_AUTO_IN, SIDE_LEFT, NBT_TANK_SIDE_OUT, SBIT_R|SBIT_B, NBT_TANK_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_ENERGY_ACCEPTED_SIDES, SBIT_D, NBT_CANFILL_STEAM, T), "IwI", "PMP", "ICI", 'M', OP.casingMachineDouble.dat(aMat), 'C', OP.plateTriple   .dat(ANY.Cu), 'I', OP.plateTriple   .dat(MT.Invar), 'P', OP.pipeMedium.dat(aMat));
        aMat = MT.DATA.Heat_T[3];       aRegistry.replaceAdd("Steam Cracker ("                 +aMat.getLocal()+")", "Basic Machines"                      , 20493, 20001, aClass, aMat.mToolQuality, 16, aMachine     , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   9.0F, NBT_RESISTANCE,   9.0F, NBT_INPUT,  512, NBT_TEXTURE, "steamcracker", NBT_ENERGY_ACCEPTED, TD.Energy.HU, NBT_RECIPEMAP, RM.SteamCracking, NBT_INV_SIDE_IN, SBIT_U|SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_TOP, NBT_INV_SIDE_OUT, SBIT_R|SBIT_B, NBT_INV_SIDE_AUTO_OUT, SIDE_BACK, NBT_TANK_SIDE_IN, SBIT_U|SBIT_L, NBT_TANK_SIDE_AUTO_IN, SIDE_LEFT, NBT_TANK_SIDE_OUT, SBIT_R|SBIT_B, NBT_TANK_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_ENERGY_ACCEPTED_SIDES, SBIT_D, NBT_CANFILL_STEAM, T), "IwI", "PMP", "ICI", 'M', OP.casingMachineDouble.dat(aMat), 'C', OP.plateQuadruple.dat(ANY.Cu), 'I', OP.plateQuadruple.dat(MT.Invar), 'P', OP.pipeMedium.dat(aMat));
        aMat = MT.DATA.Heat_T[4];       aRegistry.replaceAdd("Steam Cracker ("                 +aMat.getLocal()+")", "Basic Machines"                      , 20494, 20001, aClass, aMat.mToolQuality, 16, aMachine     , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,  12.5F, NBT_RESISTANCE,  12.5F, NBT_INPUT, 2048, NBT_TEXTURE, "steamcracker", NBT_ENERGY_ACCEPTED, TD.Energy.HU, NBT_RECIPEMAP, RM.SteamCracking, NBT_INV_SIDE_IN, SBIT_U|SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_TOP, NBT_INV_SIDE_OUT, SBIT_R|SBIT_B, NBT_INV_SIDE_AUTO_OUT, SIDE_BACK, NBT_TANK_SIDE_IN, SBIT_U|SBIT_L, NBT_TANK_SIDE_AUTO_IN, SIDE_LEFT, NBT_TANK_SIDE_OUT, SBIT_R|SBIT_B, NBT_TANK_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_ENERGY_ACCEPTED_SIDES, SBIT_D, NBT_CANFILL_STEAM, T), "IwI", "PMP", "ICI", 'M', OP.casingMachineDouble.dat(aMat), 'C', OP.plateQuintuple.dat(ANY.Cu), 'I', OP.plateQuintuple.dat(MT.Invar), 'P', OP.pipeMedium.dat(aMat));
    }
    @Override protected void machines3FinishLoad(MultiTileEntityRegistry aRegistry, MultiTileEntityBlock aMetal, MultiTileEntityBlock aMetalChips, MultiTileEntityBlock aMetalWires, MultiTileEntityBlock aMachine, MultiTileEntityBlock aWooden, MultiTileEntityBlock aBush, MultiTileEntityBlock aStone, MultiTileEntityBlock aWool, MultiTileEntityBlock aTNT, MultiTileEntityBlock aHive, MultiTileEntityBlock aUtilMetal, MultiTileEntityBlock aUtilStone, MultiTileEntityBlock aUtilWood, MultiTileEntityBlock aUtilWool, OreDictMaterial aMat, Class<? extends TileEntity> aClass) {
        /// 最后释放这些修改后的添加
        aRegistry.MODIFYING_ADD_END();
    }
    
    
    @Override protected void machines4BeforeLoad(MultiTileEntityRegistry aRegistry, MultiTileEntityBlock aMetal, MultiTileEntityBlock aMetalChips, MultiTileEntityBlock aMetalWires, MultiTileEntityBlock aMachine, MultiTileEntityBlock aWooden, MultiTileEntityBlock aBush, MultiTileEntityBlock aStone, MultiTileEntityBlock aWool, MultiTileEntityBlock aTNT, MultiTileEntityBlock aHive, MultiTileEntityBlock aUtilMetal, MultiTileEntityBlock aUtilStone, MultiTileEntityBlock aUtilWood, MultiTileEntityBlock aUtilWool, OreDictMaterial aMat, Class<? extends TileEntity> aClass) {
        /// 添加前将后续添加全部 hold
        aRegistry.MODIFYING_ADD_START();
        
        /// 修改项
        aClass = MultiTileEntityBasicMachine.class;
        aMat = MT.StainlessSteel;       aRegistry.replaceAdd("Bath"                                                , "Basic Machines"                      , 22002, 20001, aClass, aMat.mToolQuality, 16, aMachine     , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_INPUT, 1, NBT_TEXTURE, "bath"          , NBT_ENERGY_ACCEPTED, TD.Energy.TU, NBT_NO_CONSTANT_POWER, T, NBT_RECIPEMAP, RM.Bath         , NBT_INV_SIDE_IN, SBIT_U|SBIT_L    , NBT_INV_SIDE_AUTO_IN, SIDE_LEFT   , NBT_INV_SIDE_OUT, SBIT_D|SBIT_R   , NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT     , NBT_TANK_SIDE_IN, SBIT_U|SBIT_L   , NBT_TANK_SIDE_AUTO_IN, SIDE_TOP   , NBT_TANK_SIDE_OUT, SBIT_D|SBIT_R  , NBT_TANK_SIDE_AUTO_OUT, SIDE_BOTTOM   , NBT_ENERGY_ACCEPTED_SIDES, SBIT_B                                     , NBT_CANFILL_STEAM, T), "CwC", "PMP", "PPP", 'M', OP.casingMachine.dat(aMat), 'C', OP.casingSmall.dat(aMat), 'P', OP.plate.dat(aMat));
        aMat = MT.StainlessSteel;       aRegistry.replaceAdd("Autoclave"                                           , "Basic Machines"                      , 22004, 20001, aClass, aMat.mToolQuality, 16, aMachine     , UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_INPUT, 1, NBT_TEXTURE, "autoclave"     , NBT_ENERGY_ACCEPTED, TD.Energy.TU, NBT_NO_CONSTANT_POWER, T, NBT_RECIPEMAP, RM.Autoclave    , NBT_INV_SIDE_IN, SBIT_U|SBIT_L    , NBT_INV_SIDE_AUTO_IN, SIDE_LEFT   , NBT_INV_SIDE_OUT, SBIT_B|SBIT_R   , NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT     , NBT_TANK_SIDE_IN, SBIT_D|SBIT_L   , NBT_TANK_SIDE_AUTO_IN, SIDE_BOTTOM, NBT_TANK_SIDE_OUT, SBIT_B|SBIT_R  , NBT_TANK_SIDE_AUTO_OUT, SIDE_BACK     , NBT_ENERGY_ACCEPTED_SIDES, SBIT_B                                     , NBT_CANFILL_STEAM, T), "CwC", "PMP", "GPG", 'M', OP.casingMachineQuadruple.dat(aMat), 'C', OP.casingSmall.dat(aMat), 'G', OP.gearGtSmall.dat(aMat), 'P', OP.pipeSmall.dat(aMat));
    }
    @Override protected void machines4FinishLoad(MultiTileEntityRegistry aRegistry, MultiTileEntityBlock aMetal, MultiTileEntityBlock aMetalChips, MultiTileEntityBlock aMetalWires, MultiTileEntityBlock aMachine, MultiTileEntityBlock aWooden, MultiTileEntityBlock aBush, MultiTileEntityBlock aStone, MultiTileEntityBlock aWool, MultiTileEntityBlock aTNT, MultiTileEntityBlock aHive, MultiTileEntityBlock aUtilMetal, MultiTileEntityBlock aUtilStone, MultiTileEntityBlock aUtilWood, MultiTileEntityBlock aUtilWool, OreDictMaterial aMat, Class<? extends TileEntity> aClass) {
        /// 最后释放这些修改后的添加
        aRegistry.MODIFYING_ADD_END();
    }
    
    
    @Override protected void kineticBeforeLoad(MultiTileEntityRegistry aRegistry, MultiTileEntityBlock aMetal, MultiTileEntityBlock aMetalChips, MultiTileEntityBlock aMetalWires, MultiTileEntityBlock aMachine, MultiTileEntityBlock aWooden, MultiTileEntityBlock aBush, MultiTileEntityBlock aStone, MultiTileEntityBlock aWool, MultiTileEntityBlock aTNT, MultiTileEntityBlock aHive, MultiTileEntityBlock aUtilMetal, MultiTileEntityBlock aUtilStone, MultiTileEntityBlock aUtilWood, MultiTileEntityBlock aUtilWool, OreDictMaterial aMat, Class<? extends TileEntity> aClass) {
        /// 添加前将后续添加全部 hold
        aRegistry.MODIFYING_ADD_START();
        
        /// 修改项
        // Axles
        aClass = MultiTileEntityAxle.class;
        for (AttributesAxleWood_CH AXLE : DATA_MACHINES_KINETIC.AxleWood) {
            aMat = AXLE.material;
            aRegistry.replaceAdd(AXLE.sizeName + "Wooden Axle", "Axles and Gearboxes", AXLE.ID, 24819, aClass, aMat.mToolQuality, AXLE.stackSize, aWooden ,
                UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS, AXLE.nbtHardness, NBT_RESISTANCE, AXLE.nbtResistance, NBT_FLAMMABILITY, AXLE.nbtFlammability, NBT_PIPESIZE, AXLE.nbtSpeedLimit, NBT_PIPEBANDWIDTH, AXLE.nbtPowerLimit, NBT_DIAMETER, AXLE.nbtDiameter),
                AXLE.recipeObject);
        }
        for (AttributesAxle_CH AXLE : DATA_MACHINES_KINETIC.Axle) {
            aMat = AXLE.material;
            aRegistry.replaceAdd(AXLE.sizeName + aMat.mNameLocal + " Axle", "Axles and Gearboxes", AXLE.ID, 24819, aClass, aMat.mToolQuality, AXLE.stackSize, aMachine,
                UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS, AXLE.nbtHardness, NBT_RESISTANCE, AXLE.nbtResistance, NBT_PIPESIZE, AXLE.nbtSpeedLimit, NBT_PIPEBANDWIDTH, AXLE.nbtPowerLimit, NBT_DIAMETER, AXLE.nbtDiameter),
                AXLE.recipeObject);
        }
        // Engine rotations
        for (AttributesEngineRotationWood_CH ENGINE_ROTATION : DATA_MACHINES_KINETIC.EngineRotationWood) {
            aMat = ENGINE_ROTATION.material;
            aRegistry.replaceAdd("Wooden Rotation Engine", "Axles and Gearboxes", ENGINE_ROTATION.ID, 24819, MultiTileEntityEngineRotation.class, aMat.mToolQuality, ENGINE_ROTATION.stackSize, aWooden ,
                UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS, ENGINE_ROTATION.nbtHardness, NBT_RESISTANCE, ENGINE_ROTATION.nbtResistance, NBT_FLAMMABILITY, ENGINE_ROTATION.nbtFlammability, NBT_INPUT, ENGINE_ROTATION.nbtInput, NBT_OUTPUT, ENGINE_ROTATION.nbtOutput, NBT_WASTE_ENERGY, ENGINE_ROTATION.nbtWasteEnergy, NBT_ENERGY_ACCEPTED, TD.Energy.RU, NBT_ENERGY_EMITTED, TD.Energy.KU),
                ENGINE_ROTATION.recipeObject);
        }
        for (AttributesEngineRotation_CH ENGINE_ROTATION : DATA_MACHINES_KINETIC.EngineRotation) {
            aMat = ENGINE_ROTATION.material;
            aRegistry.replaceAdd(aMat.mNameLocal + " Rotation Engine", "Axles and Gearboxes", ENGINE_ROTATION.ID, 24819, MultiTileEntityEngineRotation.class     , aMat.mToolQuality, ENGINE_ROTATION.stackSize, aMachine,
                UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS, ENGINE_ROTATION.nbtHardness, NBT_RESISTANCE, ENGINE_ROTATION.nbtResistance, NBT_INPUT, ENGINE_ROTATION.nbtInput, NBT_OUTPUT, ENGINE_ROTATION.nbtOutput, NBT_WASTE_ENERGY, ENGINE_ROTATION.nbtWasteEnergy, NBT_ENERGY_ACCEPTED, TD.Energy.RU, NBT_ENERGY_EMITTED, TD.Energy.KU),
                ENGINE_ROTATION.recipeObject);
        }
        // Transformer rotations
        aClass = MultiTileEntityTransformerRotation.class;
        for (AttributesTransformerRotationWood_CH TRANSFORMER : DATA_MACHINES_KINETIC.TransformerRotationWood) {
            aMat = TRANSFORMER.material;
            aRegistry.replaceAdd("Wooden Transformer Gearbox", "Axles and Gearboxes", TRANSFORMER.ID, 24819, aClass, aMat.mToolQuality, TRANSFORMER.stackSize, aWooden ,
                UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS, TRANSFORMER.nbtHardness, NBT_RESISTANCE, TRANSFORMER.nbtResistance, NBT_FLAMMABILITY, TRANSFORMER.nbtFlammability, NBT_OUTPUT, TRANSFORMER.nbtOutput, NBT_MULTIPLIER, TRANSFORMER.nbtMultiplier, NBT_ENERGY_ACCEPTED, TD.Energy.RU, NBT_ENERGY_EMITTED, TD.Energy.RU),
                TRANSFORMER.recipeObject);
        }
        for (AttributesTransformerRotation_CH TRANSFORMER : DATA_MACHINES_KINETIC.TransformerRotation) {
            aMat = TRANSFORMER.material;
            aRegistry.replaceAdd(aMat.mNameLocal + " Transformer Gearbox", "Axles and Gearboxes", TRANSFORMER.ID, 24819, aClass, aMat.mToolQuality, TRANSFORMER.stackSize, aMachine,
                UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS, TRANSFORMER.nbtHardness, NBT_RESISTANCE, TRANSFORMER.nbtResistance, NBT_OUTPUT, TRANSFORMER.nbtOutput, NBT_MULTIPLIER, TRANSFORMER.nbtMultiplier, NBT_ENERGY_ACCEPTED, TD.Energy.RU, NBT_ENERGY_EMITTED, TD.Energy.RU),
                TRANSFORMER.recipeObject);
        }
        // Gear boxes
        for (AttributesGearBoxWood_CH GEAR_BOX : DATA_MACHINES_KINETIC.GearBoxWood) {
            aMat = GEAR_BOX.material;
            aRegistry.replaceAdd("Custom Wooden Gearbox", "Axles and Gearboxes", GEAR_BOX.ID, 24819, MultiTileEntityGearBox.class, aMat.mToolQuality, GEAR_BOX.stackSize, aWooden ,
                UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS, GEAR_BOX.nbtHardness, NBT_RESISTANCE, GEAR_BOX.nbtResistance, NBT_FLAMMABILITY, GEAR_BOX.nbtFlammability, NBT_INPUT   , GEAR_BOX.nbtInput),
                GEAR_BOX.recipeObject);
        }
        for (AttributesGearBox_CH GEAR_BOX : DATA_MACHINES_KINETIC.GearBox) {
            aMat = GEAR_BOX.material;
            aRegistry.replaceAdd("Custom " + aMat.mNameLocal + " Gearbox", "Axles and Gearboxes", GEAR_BOX.ID, 24819, MultiTileEntityGearBox.class, aMat.mToolQuality, GEAR_BOX.stackSize, aMachine,
                UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS, GEAR_BOX.nbtHardness, NBT_RESISTANCE, GEAR_BOX.nbtResistance, NBT_INPUT   , GEAR_BOX.nbtInput),
                GEAR_BOX.recipeObject);
        }
    }
    @Override protected void kineticFinishLoad(MultiTileEntityRegistry aRegistry, MultiTileEntityBlock aMetal, MultiTileEntityBlock aMetalChips, MultiTileEntityBlock aMetalWires, MultiTileEntityBlock aMachine, MultiTileEntityBlock aWooden, MultiTileEntityBlock aBush, MultiTileEntityBlock aStone, MultiTileEntityBlock aWool, MultiTileEntityBlock aTNT, MultiTileEntityBlock aHive, MultiTileEntityBlock aUtilMetal, MultiTileEntityBlock aUtilStone, MultiTileEntityBlock aUtilWood, MultiTileEntityBlock aUtilWool, OreDictMaterial aMat, Class<? extends TileEntity> aClass) {
        /// 最后释放这些修改后的添加
        aRegistry.MODIFYING_ADD_END();
    }
    
    
    @Override protected void sensorsBeforeLoad(MultiTileEntityRegistry aRegistry, MultiTileEntityBlock aMetal, MultiTileEntityBlock aMetalChips, MultiTileEntityBlock aMetalWires, MultiTileEntityBlock aMachine, MultiTileEntityBlock aWooden, MultiTileEntityBlock aBush, MultiTileEntityBlock aStone, MultiTileEntityBlock aWool, MultiTileEntityBlock aTNT, MultiTileEntityBlock aHive, MultiTileEntityBlock aUtilMetal, MultiTileEntityBlock aUtilStone, MultiTileEntityBlock aUtilWood, MultiTileEntityBlock aUtilWool, OreDictMaterial aMat, Class<? extends TileEntity> aClass) {
        /// 添加前将后续添加全部 hold
        aRegistry.MODIFYING_ADD_START();
        
        /// 添加项
        aRegistry.appendAddAfter(31001, "Large Range Gibbl-O-Meter Sensor"         , "Sensors"                             , 31040, 31015, MultiTileEntityGibblometerKilo.class                    ,                 1, 16, aUtilMetal     , null, "WPW", "BXB", "WPW", 'P', OP.plateDouble.dat(MT.TinAlloy), 'W', OP.wireFine.dat(MT.RedAlloy), 'R', OD.itemRedstone, 'G', OD.blockGlassColorless, 'B', OP.bolt.dat(MT.TinAlloy), 'C', Items.comparator, 'X', OP.plateGem.dat(ANY.SiO2));
        aRegistry.appendAddAfter(31020, "Large Range Geiger Counter Sensor"        , "Sensors"                             , 31041, 31015, MultiTileEntityGeigerCounterKilo.class                  ,                 1, 16, aUtilMetal     , null, "WGW", "YXY", "WPW", 'P', OP.plateDouble.dat(MT.TinAlloy), 'W', OP.wireFine.dat(MT.RedAlloy), 'R', OD.itemRedstone, 'G', OD.blockGlassColorless, 'B', OP.bolt.dat(MT.TinAlloy), 'C', Items.comparator, 'X', IL.Geiger_Counter, 'Y', OP.plateDense.dat(MT.Pb));
        aRegistry.appendAddAfter(31015, "Large Range Electrometer Sensor"          , "Sensors"                             , 31042, 31015, MultiTileEntityElectrometerKilo.class                   ,                 1, 16, aUtilMetal     , null, "WGW", "YXY", "WPW", 'P', OP.plateDouble.dat(MT.TinAlloy), 'W', OP.wireFine.dat(MT.RedAlloy), 'R', OD.itemRedstone, 'G', OD.blockGlassColorless, 'B', OP.bolt.dat(MT.TinAlloy), 'C', Items.comparator, 'X', OD.lever, 'Y', OP.wireFine.dat(MT.Au));
        aRegistry.appendAddAfter(31019, "Large Range Tachometer Sensor"            , "Sensors"                             , 31043, 31015, MultiTileEntityTachometerKilo.class                     ,                 1, 16, aUtilMetal     , null, "WGW", "YXY", "WPW", 'P', OP.plateDouble.dat(MT.TinAlloy), 'W', OP.wireFine.dat(MT.RedAlloy), 'R', OD.itemRedstone, 'G', OD.blockGlassColorless, 'B', OP.bolt.dat(MT.TinAlloy), 'C', Items.comparator, 'X', OP.gearGt.dat(MT.RoseGold), 'Y', OP.gearGtSmall.dat(MT.RoseGold));
        aRegistry.appendAddAfter(31021, "Large Range Laser-O-Meter Sensor"         , "Sensors"                             , 31044, 31015, MultiTileEntityLaserometerKilo.class                    ,                 1, 16, aUtilMetal     , null, "WGW", "YXY", "WPW", 'P', OP.plateDouble.dat(MT.TinAlloy), 'W', OP.wireFine.dat(MT.RedAlloy), 'R', OD.itemRedstone, 'G', OD.blockGlassColorless, 'B', OP.bolt.dat(MT.TinAlloy), 'C', Items.comparator, 'X', IL.SENSORS[3], 'Y', OP.wireFine.dat(MT.Au));
    }
    @Override protected void sensorsFinishLoad(MultiTileEntityRegistry aRegistry, MultiTileEntityBlock aMetal, MultiTileEntityBlock aMetalChips, MultiTileEntityBlock aMetalWires, MultiTileEntityBlock aMachine, MultiTileEntityBlock aWooden, MultiTileEntityBlock aBush, MultiTileEntityBlock aStone, MultiTileEntityBlock aWool, MultiTileEntityBlock aTNT, MultiTileEntityBlock aHive, MultiTileEntityBlock aUtilMetal, MultiTileEntityBlock aUtilStone, MultiTileEntityBlock aUtilWood, MultiTileEntityBlock aUtilWool, OreDictMaterial aMat, Class<? extends TileEntity> aClass) {
        /// 添加项
        aRegistry.add(RegType.GTCH, "Flow-O-Meter Sensor"                      , "Sensors"                             , 31030, 31015, MultiTileEntityFlowometer.class                      ,                 1, 16, aUtilMetal     , null, "WYW", "ZXZ", "WPW", 'P', OP.plateDouble.dat(MT.TinAlloy), 'W', OP.wireFine.dat(MT.RedAlloy), 'R', OD.itemRedstone, 'G', OD.blockGlassColorless, 'B', OP.bolt.dat(MT.TinAlloy), 'C', Items.comparator, 'X', OD.pressurePlateGold, 'Y', OP.rotor.dat(MT.TinAlloy), 'Z', OP.ring.dat(MT.Rubber));
        aRegistry.add(RegType.GTCH, "Bucket Flow-O-Meter Sensor"               , "Sensors"                             , 31031, 31015, MultiTileEntityFlowometerBucket.class                ,                 1, 16, aUtilMetal     , null, "WYW", "ZXZ", "WPW", 'P', OP.plateDouble.dat(MT.TinAlloy), 'W', OP.wireFine.dat(MT.RedAlloy), 'R', OD.itemRedstone, 'G', OD.blockGlassColorless, 'B', OP.bolt.dat(MT.TinAlloy), 'C', Items.comparator, 'X', OD.pressurePlateIron, 'Y', OP.rotor.dat(MT.TinAlloy), 'Z', OP.ring.dat(MT.Rubber));
        
        /// 最后释放这些修改后的添加
        aRegistry.MODIFYING_ADD_END();
    }
}
