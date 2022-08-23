package gregtechCH.tileentity.cores.motors;

import gregapi.old.Textures;
import gregapi.render.IIconContainer;
import gregtechCH.data.CS_CH;

public class MTEC_MotorIconLiquid extends MTEC_MotorIconBase {
    protected MTEC_MotorIconLiquid(MTEC_MotorLiquid aCore) {super(aCore);}
    public MTEC_MotorLiquid core() {return (MTEC_MotorLiquid)mCore;}

    public final static IIconContainer[] sColoreds = new IIconContainer[] {
            new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/colored/front"),
            new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/colored/back"),
            new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/colored/sides"),
    }, sOverlays = new IIconContainer[] {
            new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/overlay/front"),
            new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/overlay/back"),
            new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/overlay/sides"),
    }, sOverlaysActiveL = new IIconContainer[] {
            new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/overlay_active_l/front"),
            new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/overlay_active_l/back"),
            new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/overlay_active_l/sides"),
    }, sOverlaysActiveR = new IIconContainer[] {
            new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/overlay_active_r/front"),
            new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/overlay_active_r/back"),
            new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/overlay_active_r/sides"),
    }, sOverlaysPreheatL = new IIconContainer[] {
            new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/overlay_preheat_l/front"),
            new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/overlay_preheat_l/back"),
            new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/overlay_preheat_l/sides"),
    }, sOverlaysPreheatR = new IIconContainer[] {
            new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/overlay_preheat_r/front"),
            new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/overlay_preheat_r/back"),
            new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/overlay_preheat_r/sides"),
    };
    @Override
    public IIconContainer[] getIIconContainers(CS_CH.IconType aIconType) {
        switch (aIconType) {
            case COLORED: return sColoreds;
            case OVERLAY: return sOverlays;
            case OVERLAY_ACTIVE_L: return sOverlaysActiveL;
            case OVERLAY_ACTIVE_R: return sOverlaysActiveR;
            case OVERLAY_PREHEAT_L: return sOverlaysPreheatL;
            case OVERLAY_PREHEAT_R: return sOverlaysPreheatR;
            default: return sOverlays;
        }
    }
}
