package gregtechCH.tileentity.cores.motors;

import gregapi.old.Textures;
import gregapi.render.IIconContainer;
import gregtechCH.data.CS_CH;

public class MTEC_MotorIconSteam extends MTEC_MotorIconBase {
    protected MTEC_MotorIconSteam(MTEC_MotorSteam aCore) {super(aCore);}
    public MTEC_MotorSteam core() {return (MTEC_MotorSteam)mCore;}

    public final static IIconContainer[] sColoreds = new IIconContainer[] {
            new Textures.BlockIcons.CustomIcon("machines/turbines/rotation_steam/colored/front"),
            new Textures.BlockIcons.CustomIcon("machines/turbines/rotation_steam/colored/back"),
            new Textures.BlockIcons.CustomIcon("machines/turbines/rotation_steam/colored/side"),
    }, sOverlays = new IIconContainer[] {
            new Textures.BlockIcons.CustomIcon("machines/turbines/rotation_steam/overlay/front"),
            new Textures.BlockIcons.CustomIcon("machines/turbines/rotation_steam/overlay/back"),
            new Textures.BlockIcons.CustomIcon("machines/turbines/rotation_steam/overlay/side"),
    }, sOverlaysActiveLS = new IIconContainer[] {
            new Textures.BlockIcons.CustomIcon("machines/turbines/rotation_steam/overlay_active_ls/front"),
            new Textures.BlockIcons.CustomIcon("machines/turbines/rotation_steam/overlay_active_ls/back"),
            new Textures.BlockIcons.CustomIcon("machines/turbines/rotation_steam/overlay_active_ls/side"),
    }, sOverlaysActiveLF = new IIconContainer[] {
            new Textures.BlockIcons.CustomIcon("machines/turbines/rotation_steam/overlay_active_lf/front"),
            new Textures.BlockIcons.CustomIcon("machines/turbines/rotation_steam/overlay_active_lf/back"),
            new Textures.BlockIcons.CustomIcon("machines/turbines/rotation_steam/overlay_active_lf/side"),
    }, sOverlaysActiveRS = new IIconContainer[] {
            new Textures.BlockIcons.CustomIcon("machines/turbines/rotation_steam/overlay_active_rs/front"),
            new Textures.BlockIcons.CustomIcon("machines/turbines/rotation_steam/overlay_active_rs/back"),
            new Textures.BlockIcons.CustomIcon("machines/turbines/rotation_steam/overlay_active_rs/side"),
    }, sOverlaysActiveRF = new IIconContainer[] {
            new Textures.BlockIcons.CustomIcon("machines/turbines/rotation_steam/overlay_active_rf/front"),
            new Textures.BlockIcons.CustomIcon("machines/turbines/rotation_steam/overlay_active_rf/back"),
            new Textures.BlockIcons.CustomIcon("machines/turbines/rotation_steam/overlay_active_rf/side"),
    }, sOverlaysPreheatL = new IIconContainer[] {
            new Textures.BlockIcons.CustomIcon("machines/turbines/rotation_steam/overlay_preheat_l/front"),
            new Textures.BlockIcons.CustomIcon("machines/turbines/rotation_steam/overlay_preheat_l/back"),
            new Textures.BlockIcons.CustomIcon("machines/turbines/rotation_steam/overlay_preheat_l/side"),
    }, sOverlaysPreheatR = new IIconContainer[] {
            new Textures.BlockIcons.CustomIcon("machines/turbines/rotation_steam/overlay_preheat_r/front"),
            new Textures.BlockIcons.CustomIcon("machines/turbines/rotation_steam/overlay_preheat_r/back"),
            new Textures.BlockIcons.CustomIcon("machines/turbines/rotation_steam/overlay_preheat_r/side"),
    };

    @Override
    public IIconContainer[] getIIconContainers(CS_CH.IconType aIconType) {
        switch (aIconType) {
            case COLORED: return sColoreds;
            case OVERLAY: return sOverlays;
            case OVERLAY_ACTIVE_L: return core().isFast()?sOverlaysActiveLF:sOverlaysActiveLS;
            case OVERLAY_ACTIVE_LS: return sOverlaysActiveLS;
            case OVERLAY_ACTIVE_LF: return sOverlaysActiveLF;
            case OVERLAY_ACTIVE_R: return core().isFast()?sOverlaysActiveRF:sOverlaysActiveRS;
            case OVERLAY_ACTIVE_RS: return sOverlaysActiveRS;
            case OVERLAY_ACTIVE_RF: return sOverlaysActiveRF;
            case OVERLAY_PREHEAT_L: return sOverlaysPreheatL;
            case OVERLAY_PREHEAT_R: return sOverlaysPreheatR;
            default: return sOverlays;
        }
    }
}
