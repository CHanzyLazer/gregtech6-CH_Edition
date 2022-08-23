package gregtechCH.tileentity.cores.motors;

import gregapi.render.BlockTextureDefault;
import gregapi.render.BlockTextureMulti;
import gregapi.render.IIconContainer;
import gregapi.render.ITexture;
import gregtechCH.data.CS_CH;
import gregtechCH.tileentity.cores.IMTEC_Texture;
import net.minecraft.block.Block;

import static gregapi.data.CS.OPOS;
import static gregtechCH.data.CS_CH.IconType.*;
import static gregtechCH.data.CS_CH.IconType.OVERLAY;

// 管理涡轮的图像
public abstract class MTEC_MotorIconBase implements IMTEC_Texture {
    // the reference of MTEC_Motor
    protected final MTEC_Motor mCore;
    protected MTEC_MotorIconBase(MTEC_Motor aCore) {mCore = aCore; assert mCore.mDI == this;}

    protected abstract IIconContainer[] getIIconContainers(CS_CH.IconType aIconType);
    @Override
    public ITexture getTexture(Block aBlock, int aRenderPass, byte aSide, boolean[] aShouldSideBeRendered) {
        if (!aShouldSideBeRendered[aSide]) return null;
        int aIndex = aSide==mCore.mTE.mFacing?0:aSide==OPOS[mCore.mTE.mFacing]?1:2;
        return BlockTextureMulti.get(BlockTextureDefault.get(getIIconContainers(COLORED)[aIndex], mCore.mTE.getRGBa()), BlockTextureDefault.get(((mCore.mD.mPreheat||mCore.mD.mCooldown)?(mCore.mD.mCounterClockwise?getIIconContainers(OVERLAY_PREHEAT_L):getIIconContainers(OVERLAY_PREHEAT_R)):(mCore.mD.mActive?(mCore.mD.mCounterClockwise?getIIconContainers(OVERLAY_ACTIVE_L):getIIconContainers(OVERLAY_ACTIVE_R)):getIIconContainers(OVERLAY)))[aIndex]));
    }
}
