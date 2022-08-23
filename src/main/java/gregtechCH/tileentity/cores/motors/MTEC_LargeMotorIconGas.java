package gregtechCH.tileentity.cores.motors;

import gregapi.old.Textures;
import gregapi.render.IIconContainer;
import gregtechCH.data.CS_CH;

public class MTEC_LargeMotorIconGas extends MTEC_LargeMotorIconBase {
    protected MTEC_LargeMotorIconGas(MTEC_LargeMotorGas aCore) {super(aCore);}

    public static final IIconContainer mTextureInactive = new Textures.BlockIcons.CustomIcon("machines/multiblockmains/turbine");
    public static final IIconContainer mTextureActive   = new Textures.BlockIcons.CustomIcon("machines/multiblockmains/turbine_active");
    public static final IIconContainer mTexturePreheat  = new Textures.BlockIcons.CustomIcon("machines/multiblockmains/turbine_preheat");
    public static final IIconContainer mTextureActiveL   = new Textures.BlockIcons.CustomIcon("machines/multiblockmains/turbine_active_l");
    public static final IIconContainer mTexturePreheatL  = new Textures.BlockIcons.CustomIcon("machines/multiblockmains/turbine_preheat_l");
    @Override
    public IIconContainer getIIconContainer(CS_CH.IconType aIconType) {
        switch (aIconType) {
            case OVERLAY_ACTIVE_L: return mTextureActiveL;
            case OVERLAY_ACTIVE_R: return mTextureActive;
            case OVERLAY_PREHEAT_L: return mTexturePreheatL;
            case OVERLAY_PREHEAT_R: return mTexturePreheat;
            case OVERLAY:
            default: return mTextureInactive;
        }
    }
}
