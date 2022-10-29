package gregtechCH.tileentity.cores.motors;

import gregapi.old.Textures;
import gregapi.render.IIconContainer;

import static gregapi.data.CS.OPOS;
import static gregapi.data.CS.SIDES_EQUAL;

public class MTEC_MotorIconSteam extends MTEC_MotorIconBase {
    protected MTEC_MotorIconSteam(MTEC_MotorSteam aCore) {super(aCore);}
    public MTEC_MotorSteam core() {return (MTEC_MotorSteam)mCore;}
    
    final static IIconContainer
    C_FRONT                 = new Textures.BlockIcons.CustomIcon("machines/turbines/rotation_steam/colored/front"),
    C_BACK                  = new Textures.BlockIcons.CustomIcon("machines/turbines/rotation_steam/colored/back"),
    C_SIDE                  = new Textures.BlockIcons.CustomIcon("machines/turbines/rotation_steam/colored/side"),
    
    FRONT                   = new Textures.BlockIcons.CustomIcon("machines/turbines/rotation_steam/overlay/front"),
    FRONT_C                 = new Textures.BlockIcons.CustomIcon("machines/turbines/rotation_steam/overlay/front_active"),
    FRONT_CC                = new Textures.BlockIcons.CustomIcon("machines/turbines/rotation_steam/overlay/front_active_cc"),
    FRONT_F_C               = new Textures.BlockIcons.CustomIcon("machines/turbines/rotation_steam/overlay/front_fast"),
    FRONT_F_CC              = new Textures.BlockIcons.CustomIcon("machines/turbines/rotation_steam/overlay/front_fast_cc"),
    FRONT_P_C               = new Textures.BlockIcons.CustomIcon("machines/turbines/rotation_steam/overlay/front_preheat"),
    FRONT_P_CC              = new Textures.BlockIcons.CustomIcon("machines/turbines/rotation_steam/overlay/front_preheat_cc"),
    BACK                    = new Textures.BlockIcons.CustomIcon("machines/turbines/rotation_steam/overlay/back"),
    SIDE                    = new Textures.BlockIcons.CustomIcon("machines/turbines/rotation_steam/overlay/side");
    
    @Override protected IIconContainer getColoredIcon            (byte aSide) {return SIDES_EQUAL[aSide][mCore.mTE.mFacing] ? C_FRONT : (SIDES_EQUAL[aSide][OPOS[mCore.mTE.mFacing]] ? C_BACK : C_SIDE);}
    @Override protected IIconContainer getOverlayFrontIcon       () {return mCore.mD.mActive ? (core().isFast() ? (mCore.mD.mCounterClockwise ? FRONT_F_CC : FRONT_F_C) : mCore.mD.mCounterClockwise ? FRONT_CC : FRONT_C) : (mCore.mD.mPreheat||mCore.mD.mCooldown ? (mCore.mD.mCounterClockwise ? FRONT_P_CC : FRONT_P_C) : FRONT);}
    @Override protected IIconContainer getOverlayBackIcon        () {return BACK;}
    @Override protected IIconContainer getOverlaySideUpIcon      () {return SIDE;}
    @Override protected IIconContainer getOverlaySideDownIcon    () {return SIDE;}
    @Override protected IIconContainer getOverlaySideLeftIcon    () {return SIDE;}
    @Override protected IIconContainer getOverlaySideRightIcon   () {return SIDE;}
}
