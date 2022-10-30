package gregtechCH.tileentity.cores.basicmachines;

import gregapi.data.LH;
import gregapi.tileentity.multiblocks.TileEntityBase10MultiBlockMachine;
import gregtechCH.data.LH_CH;

import java.util.List;

import static gregtechCH.data.CS_CH.RegType;

/**
 * stuff from GT6U
 **/
public final class MTEC_MultiBlockCrackingTower extends MTEC_MultiBlockTowerBase {
    public MTEC_MultiBlockCrackingTower(TileEntityBase10MultiBlockMachine aTE) {super(aTE);}
    
    @Override public int towerHeight() {return 8;}
    @Override public int mainPartMeta() {return 18113;} // Cracking Tower Part
    
    /* main code */
    static {
        LH_CH.add(RegType.GT6U, "gt6u.tooltip.multiblock.crackingtower.1", "3x3 Base of Heat Transmitters");
        LH_CH.add(RegType.GT6U, "gt6u.tooltip.multiblock.crackingtower.2", "3x3x8 of Cracking Tower Parts");
        LH_CH.add(RegType.GT6U, "gt6u.tooltip.multiblock.crackingtower.3", "Main centered on Side-Bottom of Tower facing outwards");
        LH_CH.add(RegType.GT6U, "gt6u.tooltip.multiblock.crackingtower.4", "Outputs automatically to the Holes on the Backside");
        LH_CH.add(RegType.GT6U, "gt6u.tooltip.multiblock.crackingtower.5", "Bottom Hole is for outputting all Items");
        LH_CH.add(RegType.GT6U, "gt6u.tooltip.multiblock.crackingtower.6", "Input only possible at Bottom Layer of Tower");
    }
    
    @Override
    public void toolTipsMultiblock(List<String> aList) {
        aList.add(LH.Chat.CYAN     + LH.get(LH.STRUCTURE) + ":");
        aList.add(LH.Chat.WHITE    + LH_CH.get("gt6u.tooltip.multiblock.crackingtower.1"));
        aList.add(LH.Chat.WHITE    + LH_CH.get("gt6u.tooltip.multiblock.crackingtower.2"));
        aList.add(LH.Chat.WHITE    + LH_CH.get("gt6u.tooltip.multiblock.crackingtower.3"));
        aList.add(LH.Chat.WHITE    + LH_CH.get("gt6u.tooltip.multiblock.crackingtower.4"));
        aList.add(LH.Chat.WHITE    + LH_CH.get("gt6u.tooltip.multiblock.crackingtower.5"));
        aList.add(LH.Chat.WHITE    + LH_CH.get("gt6u.tooltip.multiblock.crackingtower.6"));
    }
}
