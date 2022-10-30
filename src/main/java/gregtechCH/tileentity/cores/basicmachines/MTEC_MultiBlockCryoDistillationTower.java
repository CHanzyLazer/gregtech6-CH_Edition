package gregtechCH.tileentity.cores.basicmachines;

import gregapi.data.LH;
import gregapi.tileentity.multiblocks.TileEntityBase10MultiBlockMachine;
import gregtechCH.data.LH_CH;

import java.util.List;

import static gregtechCH.data.CS_CH.RegType;

public final class MTEC_MultiBlockCryoDistillationTower extends MTEC_MultiBlockTowerBase {
    public MTEC_MultiBlockCryoDistillationTower(TileEntityBase10MultiBlockMachine aTE) {super(aTE);}
    
    @Override public int towerHeight() {return 8;}
    @Override public int mainPartMeta() {return 18112;} // 修改成 Frost Proof Machine Casing
    
    /* main code */
    static {
        LH.add("gt.tooltip.multiblock.cryodistillationtower.1", "3x3 Base of Heat Transmitters. They can also transfer Cold. ;)");
        LH_CH.add(RegType.GT6U, "gt6U.tooltip.multiblock.cryodistillationtower.2", "3x3x8 of Frost Proof Machine Casing"); // 修改冷凝塔的主要部件为 Frost Proof Machine Casing
        LH.add("gt.tooltip.multiblock.cryodistillationtower.3", "Main centered on Side-Bottom of Tower facing outwards");
        LH.add("gt.tooltip.multiblock.cryodistillationtower.4", "Outputs automatically to the Holes on the Backside");
        LH.add("gt.tooltip.multiblock.cryodistillationtower.5", "Bottom Hole is for outputting all Items");
        LH.add("gt.tooltip.multiblock.cryodistillationtower.6", "Input only possible at Bottom Layer of Tower");
    }
    
    @Override
    public void toolTipsMultiblock(List<String> aList) {
        aList.add(LH.Chat.CYAN     + LH.get(LH.STRUCTURE) + ":");
        aList.add(LH.Chat.WHITE    + LH.get("gt.tooltip.multiblock.cryodistillationtower.1"));
        aList.add(LH.Chat.WHITE    + LH_CH.get("gt6U.tooltip.multiblock.cryodistillationtower.2"));
        aList.add(LH.Chat.WHITE    + LH.get("gt.tooltip.multiblock.cryodistillationtower.3"));
        aList.add(LH.Chat.WHITE    + LH.get("gt.tooltip.multiblock.cryodistillationtower.4"));
        aList.add(LH.Chat.WHITE    + LH.get("gt.tooltip.multiblock.cryodistillationtower.5"));
        aList.add(LH.Chat.WHITE    + LH.get("gt.tooltip.multiblock.cryodistillationtower.6"));
    }
    
    @Override public String getTileEntityNameCompat() {return "gtch.multitileentity.multiblock.cryodistillationtower";}
}
