package gregtechCH.tileentity.cores.motors;

import gregapi.render.IIconContainer;

import static gregtechCH.tileentity.cores.motors.MTEC_LargeMotorIconSteam.*;

/**
 * @author CHanzy
 */
public class MTEC_LargeMotorIconGas extends MTEC_LargeMotorIconBase {
    protected MTEC_LargeMotorIconGas(MTEC_LargeMotorGas aCore) {super(aCore);}
    
    /// 暂时共用 steam 的材质
    
    @Override protected IIconContainer getOverlayInputIcon       () {return mCore.mD.mActive ? (mCore.mD.mCounterClockwise ? mTextureActiveL : mTextureActive) : (mCore.mD.mPreheat||mCore.mD.mCooldown ? (mCore.mD.mCounterClockwise ? mTexturePreheatL : mTexturePreheat) : mTextureInactive);}
    @Override protected IIconContainer getOverlayOutputIcon      () {return mTextureOutput;}
}
