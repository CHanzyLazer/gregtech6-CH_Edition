package gregtechCH.tileentity.cores.motors;

import gregapi.old.Textures;
import gregapi.render.IIconContainer;

public class MTEC_LargeMotorIconSteam extends MTEC_LargeMotorIconBase {
    protected MTEC_LargeMotorIconSteam(MTEC_LargeMotorSteam aCore) {super(aCore);}

    static final IIconContainer mTextureInactive	= new Textures.BlockIcons.CustomIcon("machines/multiblockmains/turbine");
    static final IIconContainer mTextureActive      = new Textures.BlockIcons.CustomIcon("machines/multiblockmains/turbine_active");
    static final IIconContainer mTexturePreheat     = new Textures.BlockIcons.CustomIcon("machines/multiblockmains/turbine_preheat");
    static final IIconContainer mTextureActiveL	    = new Textures.BlockIcons.CustomIcon("machines/multiblockmains/turbine_active_l");
    static final IIconContainer mTexturePreheatL	= new Textures.BlockIcons.CustomIcon("machines/multiblockmains/turbine_preheat_l");
    static final IIconContainer mTextureOutput  	= new Textures.BlockIcons.CustomIcon("machines/multiblockparts/metalwalldense/3/overlay/top");
    
    @Override protected IIconContainer getOverlayInputIcon       () {return mCore.mD.mActive ? (mCore.mD.mCounterClockwise ? mTextureActiveL : mTextureActive) : (mCore.mD.mPreheat||mCore.mD.mCooldown ? (mCore.mD.mCounterClockwise ? mTexturePreheatL : mTexturePreheat) : mTextureInactive);}
    @Override protected IIconContainer getOverlayOutputIcon      () {return mTextureOutput;}
}
