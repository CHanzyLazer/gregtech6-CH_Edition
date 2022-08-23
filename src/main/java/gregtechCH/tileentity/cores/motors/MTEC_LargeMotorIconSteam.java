package gregtechCH.tileentity.cores.motors;

import gregapi.old.Textures;
import gregapi.render.IIconContainer;
import gregtechCH.data.CS_CH;

public class MTEC_LargeMotorIconSteam extends MTEC_LargeMotorIconBase {
    protected MTEC_LargeMotorIconSteam(MTEC_LargeMotorSteam aCore) {super(aCore);}

    public static final IIconContainer mTextureInactive	= new Textures.BlockIcons.CustomIcon("machines/multiblockmains/turbine");
    public static final IIconContainer mTextureActive   = new Textures.BlockIcons.CustomIcon("machines/multiblockmains/turbine_active");
    public static final IIconContainer mTexturePreheat  = new Textures.BlockIcons.CustomIcon("machines/multiblockmains/turbine_preheat");
    public static final IIconContainer mTextureActiveL	= new Textures.BlockIcons.CustomIcon("machines/multiblockmains/turbine_active_l");
    public static final IIconContainer mTexturePreheatL	= new Textures.BlockIcons.CustomIcon("machines/multiblockmains/turbine_preheat_l");
    public static final IIconContainer mTextureOutput  	= new Textures.BlockIcons.CustomIcon("machines/multiblockparts/metalwalldense/3/overlay/top");
    @Override
    public IIconContainer getIIconContainer(CS_CH.IconType aIconType) {
        switch (aIconType) {
            case OVERLAY_ACTIVE_L: return mTextureActiveL;
            case OVERLAY_ACTIVE_R: return mTextureActive;
            case OVERLAY_PREHEAT_L: return mTexturePreheatL;
            case OVERLAY_PREHEAT_R: return mTexturePreheat;
            case OVERLAY_ENERGY_RU: return mTextureOutput;
            case OVERLAY:
            default: return mTextureInactive;
        }
    }
}
