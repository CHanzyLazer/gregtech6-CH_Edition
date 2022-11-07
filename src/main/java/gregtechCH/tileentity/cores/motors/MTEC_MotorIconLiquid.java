package gregtechCH.tileentity.cores.motors;

import gregapi.old.Textures;
import gregapi.render.IIconContainer;

import static gregapi.data.CS.OPOS;
import static gregapi.data.CS.SIDES_EQUAL;
import static gregtechCH.data.CS_CH.FACE_TO_ORDER;

public class MTEC_MotorIconLiquid extends MTEC_MotorIconBase {
    public MTEC_MotorIconLiquid(MTEC_MotorLiquid aCore) {super(aCore);}
    public MTEC_MotorLiquid core() {return (MTEC_MotorLiquid)mCore;}
    
    static final IIconContainer
    C_FRONT     = new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/colored/front"),
    C_BACK      = new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/colored/back"),
    C_SIDE      = new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/colored/sides"),
    
    FRONT       = new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/overlay/front"),
    FRONT_C     = new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/overlay/front_active"),
    FRONT_CC    = new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/overlay/front_active_cc"),
    FRONT_P_C   = new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/overlay/front_preheat"),
    FRONT_P_CC  = new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/overlay/front_preheat_cc"),
    BACK        = new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/overlay/back"),
    SIDE        = new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/overlay/sides");
    
    static final IIconContainer[]
    SIDES_A = {
        new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/overlay/sides_active"),
        new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/overlay/sides_active1"),
        new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/overlay/sides_active2"),
        new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/overlay/sides_active3")
    },
    SIDES_P = {
        new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/overlay/sides_preheat"),
        new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/overlay/sides_preheat1"),
        new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/overlay/sides_preheat2"),
        new Textures.BlockIcons.CustomIcon("machines/generators/motor_liquid/overlay/sides_preheat3")
    };
    
    @Override protected IIconContainer getColoredIcon       (byte aSide) {return SIDES_EQUAL[aSide][mCore.mTE.mFacing] ? C_FRONT : (SIDES_EQUAL[aSide][OPOS[mCore.mTE.mFacing]] ? C_BACK : C_SIDE);}
    @Override protected IIconContainer getOverlayFrontIcon  () {return mCore.mD.mActive ? (mCore.mD.mCounterClockwise ? FRONT_CC : FRONT_C) : (mCore.mD.mPreheat||mCore.mD.mCooldown ? (mCore.mD.mCounterClockwise ? FRONT_P_CC : FRONT_P_C) : FRONT);}
    @Override protected IIconContainer getOverlayBackIcon   () {return BACK;}
    @Override protected IIconContainer getOverlaySideIcon   (byte aSide) {
        byte tOrder = FACE_TO_ORDER[mCore.mTE.mFacing][aSide];
        if (mCore.mD.mCounterClockwise) tOrder = (byte)(3-tOrder);
        return mCore.mD.mActive ? SIDES_A[tOrder] : (mCore.mD.mPreheat||mCore.mD.mCooldown ? SIDES_P[tOrder] : SIDE);
    }
}
