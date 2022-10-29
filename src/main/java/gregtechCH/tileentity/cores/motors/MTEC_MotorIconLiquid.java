package gregtechCH.tileentity.cores.motors;

import gregapi.old.Textures;
import gregapi.render.IIconContainer;

import static gregapi.data.CS.OPOS;
import static gregapi.data.CS.SIDES_EQUAL;

public class MTEC_MotorIconLiquid extends MTEC_MotorIconBase {
    public MTEC_MotorIconLiquid(MTEC_MotorLiquid aCore) {super(aCore);}
    public MTEC_MotorLiquid core() {return (MTEC_MotorLiquid)mCore;}
    
    final static IIconContainer
    C_FRONT                 = new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/colored/front"),
    C_BACK                  = new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/colored/back"),
    C_SIDE                  = new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/colored/sides"),
    
    FRONT                   = new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/overlay/front"),
    FRONT_C                 = new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/overlay/front_active"),
    FRONT_CC                = new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/overlay/front_active_cc"),
    FRONT_P_C               = new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/overlay/front_preheat"),
    FRONT_P_CC              = new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/overlay/front_preheat_cc"),
    BACK                    = new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/overlay/back"),
    SIDE                    = new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/overlay/sides"),
    SIDE_A                  = new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/overlay/sides_active"),
    SIDE_P                  = new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/overlay/sides_preheat");
    
    @Override protected IIconContainer getColoredIcon            (byte aSide) {return SIDES_EQUAL[aSide][mCore.mTE.mFacing] ? C_FRONT : (SIDES_EQUAL[aSide][OPOS[mCore.mTE.mFacing]] ? C_BACK : C_SIDE);}
    @Override protected IIconContainer getOverlayFrontIcon       () {return mCore.mD.mActive ? (mCore.mD.mCounterClockwise ? FRONT_CC : FRONT_C) : (mCore.mD.mPreheat||mCore.mD.mCooldown ? (mCore.mD.mCounterClockwise ? FRONT_P_CC : FRONT_P_C) : FRONT);}
    @Override protected IIconContainer getOverlayBackIcon        () {return BACK;}
    @Override protected IIconContainer getOverlaySideUpIcon      () {return mCore.mD.mActive ? SIDE_A : (mCore.mD.mPreheat||mCore.mD.mCooldown ? SIDE_P : SIDE);}
    @Override protected IIconContainer getOverlaySideDownIcon    () {return mCore.mD.mActive ? SIDE_A : (mCore.mD.mPreheat||mCore.mD.mCooldown ? SIDE_P : SIDE);}
    @Override protected IIconContainer getOverlaySideLeftIcon    () {return mCore.mD.mActive ? SIDE_A : (mCore.mD.mPreheat||mCore.mD.mCooldown ? SIDE_P : SIDE);}
    @Override protected IIconContainer getOverlaySideRightIcon   () {return mCore.mD.mActive ? SIDE_A : (mCore.mD.mPreheat||mCore.mD.mCooldown ? SIDE_P : SIDE);}
}
