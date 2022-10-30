package gregtechCH.tileentity.cores.basicmachines;

import gregapi.data.LH;
import gregapi.tileentity.multiblocks.TileEntityBase10MultiBlockMachine;

import java.util.List;

public final class MTEC_MultiBlockDistillationTower extends MTEC_MultiBlockTowerBase {
    public MTEC_MultiBlockDistillationTower(TileEntityBase10MultiBlockMachine aTE) {super(aTE);}
    
    @Override public int towerHeight() {return 8;}
    @Override public int mainPartMeta() {return 18102;}
    
    /* main code */
    static {
        LH.add("gt.tooltip.multiblock.distillationtower.1", "3x3 Base of Heat Transmitters");
        LH.add("gt.tooltip.multiblock.distillationtower.2", "3x3x8 of Distillation Tower Parts");
        LH.add("gt.tooltip.multiblock.distillationtower.3", "Main centered on Side-Bottom of Tower facing outwards");
        LH.add("gt.tooltip.multiblock.distillationtower.4", "Outputs automatically to the Holes on the Backside");
        LH.add("gt.tooltip.multiblock.distillationtower.5", "Bottom Hole is for outputting all Items");
        LH.add("gt.tooltip.multiblock.distillationtower.6", "Input only possible at Bottom Layer of Tower");
    }
    
    @Override
    public void toolTipsMultiblock(List<String> aList) {
        aList.add(LH.Chat.CYAN     + LH.get(LH.STRUCTURE) + ":");
        aList.add(LH.Chat.WHITE    + LH.get("gt.tooltip.multiblock.distillationtower.1"));
        aList.add(LH.Chat.WHITE    + LH.get("gt.tooltip.multiblock.distillationtower.2"));
        aList.add(LH.Chat.WHITE    + LH.get("gt.tooltip.multiblock.distillationtower.3"));
        aList.add(LH.Chat.WHITE    + LH.get("gt.tooltip.multiblock.distillationtower.4"));
        aList.add(LH.Chat.WHITE    + LH.get("gt.tooltip.multiblock.distillationtower.5"));
        aList.add(LH.Chat.WHITE    + LH.get("gt.tooltip.multiblock.distillationtower.6"));
    }
    
    @Override public String getTileEntityNameCompat() {return "gtch.multitileentity.multiblock.distillationtower";}
}
