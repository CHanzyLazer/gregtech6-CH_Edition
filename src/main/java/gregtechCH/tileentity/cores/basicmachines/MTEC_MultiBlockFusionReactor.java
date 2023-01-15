package gregtechCH.tileentity.cores.basicmachines;

import gregapi.data.LH;
import gregapi.tileentity.multiblocks.TileEntityBase10MultiBlockMachine;

import java.util.List;

/**
 * @author Gregorius Techneticies, CHanzy
 */
public final class MTEC_MultiBlockFusionReactor extends MTEC_MultiBlockLargeRing {
    public MTEC_MultiBlockFusionReactor(TileEntityBase10MultiBlockMachine aTE) {super(aTE);}

    /* main code */
    @Override public int ringOuterCoverMeta() {return 18003;} // Tungstensteel Walls
    @Override public int ringInnerCoverMeta() {return 18045;} // Iridium Coils
    @Override public int ringCenterMeta() {return 18002;} // Stainless Steel Walls

    static {
        LH.add("gt.tooltip.multiblock.fusionreactor.1", "For Assembly Instructions read the Manual in the GUI.");
        LH.add("gt.tooltip.multiblock.fusionreactor.2", "144 Iridium Coils, 576 Regular Tungstensteel Walls, 50 Ventilation Units.");
        LH.add("gt.tooltip.multiblock.fusionreactor.3", "36 Regular Stainless Steel Walls, 53 Galvanized Steel Walls.");
        LH.add("gt.tooltip.multiblock.fusionreactor.4", "3 Versatile, 12 Logic and 12 Control Quadcore Processing Units.");
        LH.add("gt.tooltip.multiblock.fusionreactor.5", "Energy Output at the Electric Interfaces");
        LH.add("gt.tooltip.multiblock.fusionreactor.6", "Laser Input at the 'Glass' Ring");
        LH.add("gt.tooltip.multiblock.fusionreactor.7", "Items and Fluids are handeled at the normal Walls");
    }

    @Override
    public void toolTipsMultiblock(List<String> aList) {
        aList.add(LH.Chat.CYAN  + LH.get(LH.STRUCTURE) + ":");
        aList.add(LH.Chat.WHITE + LH.get("gt.tooltip.multiblock.fusionreactor.1"));
        aList.add(LH.Chat.WHITE + LH.get("gt.tooltip.multiblock.fusionreactor.2"));
        aList.add(LH.Chat.WHITE + LH.get("gt.tooltip.multiblock.fusionreactor.3"));
        aList.add(LH.Chat.WHITE + LH.get("gt.tooltip.multiblock.fusionreactor.4"));
        aList.add(LH.Chat.WHITE + LH.get("gt.tooltip.multiblock.fusionreactor.5"));
        aList.add(LH.Chat.WHITE + LH.get("gt.tooltip.multiblock.fusionreactor.6"));
        aList.add(LH.Chat.WHITE + LH.get("gt.tooltip.multiblock.fusionreactor.7"));
    }
    
    @Override public String getTileEntityNameCompat() {return "gtch.multitileentity.multiblock.fusionreactor";}
}
