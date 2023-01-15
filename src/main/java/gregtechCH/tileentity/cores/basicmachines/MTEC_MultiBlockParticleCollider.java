package gregtechCH.tileentity.cores.basicmachines;

import gregapi.data.LH;
import gregapi.tileentity.multiblocks.MultiTileEntityMultiBlockPart;
import gregapi.tileentity.multiblocks.TileEntityBase10MultiBlockMachine;
import gregtechCH.data.LH_CH;

import java.util.List;

import static gregtechCH.data.CS_CH.RegType;

/**
 * @author Gregorius Techneticies, YueSha, CHanzy
 * stuff from GT6U
 */
public final class MTEC_MultiBlockParticleCollider extends MTEC_MultiBlockLargeRing {
    public MTEC_MultiBlockParticleCollider(TileEntityBase10MultiBlockMachine aTE) {super(aTE);}
    
    /* main code */
    @Override public int ringOuterCoverMeta() {return 18014;} // Osmiridium Wall
    @Override public int ringInnerCoverMeta() {return 18046;} // Superconducting Coil
    @Override public int ringCenterMeta() {return 18002;} // Stainless Steel Walls
    @Override public int backPartMode() {return MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN;}
    
    static {
        LH_CH.add(RegType.GT6U, "gt6u.tooltip.multiblock.particlecollider.1", "For Construction Instructions read the Manual or the GUI.");
        LH_CH.add(RegType.GT6U, "gt6u.tooltip.multiblock.particlecollider.2", "144 Superconducting Coils, 576 Regular Osmiridium Walls, 50 Ventilation Units.");
        LH_CH.add(RegType.GT6U, "gt6u.tooltip.multiblock.particlecollider.3", "36 Regular Stainless Steel Walls, 53 Galvanized Steel Walls.");
        LH_CH.add(RegType.GT6U, "gt6u.tooltip.multiblock.particlecollider.4", "3 Versatile, 12 Logic and 12 Control Quadcore Processing Units.");
        LH_CH.add(RegType.GT6U, "gt6u.tooltip.multiblock.particlecollider.5", "Input energy for start.Then for process");
        LH_CH.add(RegType.GT6U, "gt6u.tooltip.multiblock.particlecollider.6", "Electric power Input at the 'Glass' Ring");
        LH_CH.add(RegType.GT6U, "gt6u.tooltip.multiblock.particlecollider.7", "Items and Fluids are handeled at the normal Walls");
    }
    
    @Override
    public void toolTipsMultiblock(List<String> aList) {
        aList.add(LH.Chat.CYAN  + LH.get(LH.STRUCTURE) + ":");
        aList.add(LH.Chat.WHITE + LH_CH.get("gt6u.tooltip.multiblock.particlecollider.1"));
        aList.add(LH.Chat.WHITE + LH_CH.get("gt6u.tooltip.multiblock.particlecollider.2"));
        aList.add(LH.Chat.WHITE + LH_CH.get("gt6u.tooltip.multiblock.particlecollider.3"));
        aList.add(LH.Chat.WHITE + LH_CH.get("gt6u.tooltip.multiblock.particlecollider.4"));
        aList.add(LH.Chat.WHITE + LH_CH.get("gt6u.tooltip.multiblock.particlecollider.5"));
        aList.add(LH.Chat.WHITE + LH_CH.get("gt6u.tooltip.multiblock.particlecollider.6"));
        aList.add(LH.Chat.WHITE + LH_CH.get("gt6u.tooltip.multiblock.particlecollider.7"));
    }
}
