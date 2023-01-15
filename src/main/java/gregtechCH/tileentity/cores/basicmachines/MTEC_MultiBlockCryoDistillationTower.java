package gregtechCH.tileentity.cores.basicmachines;

import gregapi.data.LH;
import gregapi.tileentity.multiblocks.TileEntityBase10MultiBlockMachine;
import gregtechCH.data.LH_CH;

import java.util.List;

/**
 * @author Gregorius Techneticies, CHanzy
 */
public final class MTEC_MultiBlockCryoDistillationTower extends MTEC_MultiBlockTowerBase {
    public MTEC_MultiBlockCryoDistillationTower(TileEntityBase10MultiBlockMachine aTE) {super(aTE);}
    public short mDesign = 18102; // 为了兼容 GT6U，冷凝塔的主要部件是可变的
    
    @Override public int towerHeight() {return 8;}
    @Override public int mainPartMeta() {return mDesign;}
    
    /* main code */
    static {
        LH.add("gt.tooltip.multiblock.cryodistillationtower.1", "3x3 Base of Heat Transmitters. They can also transfer Cold. ;)");
        LH_CH.add("gtch.tooltip.multiblock.cryodistillationtower.2", "3x3x8 of %s");
        LH.add("gt.tooltip.multiblock.cryodistillationtower.3", "Main centered on Side-Bottom of Tower facing outwards");
        LH.add("gt.tooltip.multiblock.cryodistillationtower.4", "Outputs automatically to the Holes on the Backside");
        LH.add("gt.tooltip.multiblock.cryodistillationtower.5", "Bottom Hole is for outputting all Items");
        LH.add("gt.tooltip.multiblock.cryodistillationtower.6", "Input only possible at Bottom Layer of Tower");
    }
    
    @Override
    public void toolTipsMultiblock(List<String> aList) {
        aList.add(LH.Chat.CYAN     + LH.get(LH.STRUCTURE) + ":");
        aList.add(LH.Chat.WHITE    + LH.get("gt.tooltip.multiblock.cryodistillationtower.1"));
        aList.add(LH.Chat.WHITE    + LH_CH.getItemName("gtch.tooltip.multiblock.cryodistillationtower.2", mDesign, mTE.getMultiTileEntityRegistryID()));
        aList.add(LH.Chat.WHITE    + LH.get("gt.tooltip.multiblock.cryodistillationtower.3"));
        aList.add(LH.Chat.WHITE    + LH.get("gt.tooltip.multiblock.cryodistillationtower.4"));
        aList.add(LH.Chat.WHITE    + LH.get("gt.tooltip.multiblock.cryodistillationtower.5"));
        aList.add(LH.Chat.WHITE    + LH.get("gt.tooltip.multiblock.cryodistillationtower.6"));
    }
    
    @Override public String getTileEntityNameCompat() {return "gtch.multitileentity.multiblock.cryodistillationtower";}
}
