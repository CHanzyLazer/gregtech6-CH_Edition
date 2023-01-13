package gregtechCH.tileentity.cores.motors;

import gregapi.old.Textures;
import gregapi.render.IIconContainer;

import static gregapi.data.CS.OPOS;
import static gregapi.data.CS.SIDES_EQUAL;
import static gregtechCH.data.CS_CH.DIR_ICON;
import static gregtechCH.data.CS_CH.IconType;

/**
 * @author CHanzy
 */
public class MTEC_MotorIconGas extends MTEC_MotorIconBase {
    public MTEC_MotorIconGas(MTEC_MotorGas aCore) {super(aCore);}
    public MTEC_MotorGas core() {return (MTEC_MotorGas)mCore;}
    
    static final IIconContainer
    C_FRONT         = new Textures.BlockIcons.CustomIcon("machines/generators/motor_gas/colored/front"),
    C_BACK          = new Textures.BlockIcons.CustomIcon("machines/generators/motor_gas/colored/back"),
    C_SIDE          = new Textures.BlockIcons.CustomIcon("machines/generators/motor_gas/colored/sides"),
    
    FRONT           = new Textures.BlockIcons.CustomIcon("machines/generators/motor_gas/overlay/front"),
    FRONT_C         = new Textures.BlockIcons.CustomIcon("machines/generators/motor_gas/overlay/front_active"),
    FRONT_CC        = new Textures.BlockIcons.CustomIcon("machines/generators/motor_gas/overlay/front_active_cc"),
    FRONT_P_C       = new Textures.BlockIcons.CustomIcon("machines/generators/motor_gas/overlay/front_preheat"),
    FRONT_P_CC      = new Textures.BlockIcons.CustomIcon("machines/generators/motor_gas/overlay/front_preheat_cc"),
    BACK            = new Textures.BlockIcons.CustomIcon("machines/generators/motor_gas/overlay/back"),
    SIDE_HORIZONTAL = new Textures.BlockIcons.CustomIcon("machines/generators/motor_gas/overlay/sides_HORIZONTAL"),
    SIDE_VERTICAL   = new Textures.BlockIcons.CustomIcon("machines/generators/motor_gas/overlay/sides_VERTICAL"),
    SIDE_UP         = new Textures.BlockIcons.CustomIcon("machines/generators/motor_gas/overlay/sides_UP"),
    SIDE_DOWN       = new Textures.BlockIcons.CustomIcon("machines/generators/motor_gas/overlay/sides_DOWN"),
    SIDE_LEFT       = new Textures.BlockIcons.CustomIcon("machines/generators/motor_gas/overlay/sides_LEFT"),
    SIDE_RIGHT      = new Textures.BlockIcons.CustomIcon("machines/generators/motor_gas/overlay/sides_RIGHT"),
    SIDE_P_UP       = new Textures.BlockIcons.CustomIcon("machines/generators/motor_gas/overlay/sides_UP_preheat"),
    SIDE_P_DOWN     = new Textures.BlockIcons.CustomIcon("machines/generators/motor_gas/overlay/sides_DOWN_preheat"),
    SIDE_P_LEFT     = new Textures.BlockIcons.CustomIcon("machines/generators/motor_gas/overlay/sides_LEFT_preheat"),
    SIDE_P_RIGHT    = new Textures.BlockIcons.CustomIcon("machines/generators/motor_gas/overlay/sides_RIGHT_preheat");
    
    @Override protected IIconContainer getOverlaySideIcon   (byte aSide) {
        IconType tIconType = DIR_ICON[aSide][mCore.mTE.mFacing];
        switch (tIconType) {
            case SIDE_UP:       return mCore.mD.mActive ? (mCore.mD.mCounterClockwise ? SIDE_RIGHT : SIDE_LEFT) : (mCore.mD.mPreheat||mCore.mD.mCooldown ? (mCore.mD.mCounterClockwise ? SIDE_P_RIGHT : SIDE_P_LEFT) : SIDE_HORIZONTAL);
            case SIDE_DOWN:     return mCore.mD.mActive ? (mCore.mD.mCounterClockwise ? SIDE_LEFT : SIDE_RIGHT) : (mCore.mD.mPreheat||mCore.mD.mCooldown ? (mCore.mD.mCounterClockwise ? SIDE_P_LEFT : SIDE_P_RIGHT) : SIDE_HORIZONTAL);
            case SIDE_LEFT:     return mCore.mD.mActive ? (mCore.mD.mCounterClockwise ? SIDE_UP : SIDE_DOWN) : (mCore.mD.mPreheat||mCore.mD.mCooldown ? (mCore.mD.mCounterClockwise ? SIDE_P_UP : SIDE_P_DOWN) : SIDE_VERTICAL);
            case SIDE_RIGHT:    return mCore.mD.mActive ? (mCore.mD.mCounterClockwise ? SIDE_DOWN : SIDE_UP) : (mCore.mD.mPreheat||mCore.mD.mCooldown ? (mCore.mD.mCounterClockwise ? SIDE_P_DOWN : SIDE_P_UP) : SIDE_VERTICAL);
            case VOID: default: return null;
        }
    }
    @Override protected IIconContainer getColoredIcon       (byte aSide) {return SIDES_EQUAL[aSide][mCore.mTE.mFacing] ? C_FRONT : (SIDES_EQUAL[aSide][OPOS[mCore.mTE.mFacing]] ? C_BACK : C_SIDE);}
    @Override protected IIconContainer getOverlayFrontIcon  () {return mCore.mD.mActive ? (mCore.mD.mCounterClockwise ? FRONT_CC : FRONT_C) : (mCore.mD.mPreheat||mCore.mD.mCooldown ? (mCore.mD.mCounterClockwise ? FRONT_P_CC : FRONT_P_C) : FRONT);}
    @Override protected IIconContainer getOverlayBackIcon   () {return BACK;}
}
