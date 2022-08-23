package gregtechCH.tileentity.cores.motors;

import gregapi.render.BlockTextureDefault;
import gregapi.render.BlockTextureMulti;
import gregapi.render.IIconContainer;
import gregapi.render.ITexture;
import gregtechCH.data.CS_CH;
import gregtechCH.tileentity.cores.IMTEC_Texture;
import gregtechCH.util.UT_CH;
import net.minecraft.block.Block;

import static gregtechCH.data.CS_CH.IconType.*;
import static gregtechCH.data.CS_CH.IconType.OVERLAY;

public abstract class MTEC_LargeMotorIconBase implements IMTEC_Texture {
    // the reference of MTEC_LargeMotor
    protected final MTEC_LargeMotor mCore;
    protected MTEC_LargeMotorIconBase(MTEC_LargeMotor aCore) {mCore = aCore; assert mCore.mDI == this;}

    protected abstract IIconContainer getIIconContainer(CS_CH.IconType aIconType);
    @Override
    public ITexture getTexture(Block aBlock, int aRenderPass, byte aSide, boolean[] aShouldSideBeRendered) {
        if (aRenderPass == 0) {
            if (!aShouldSideBeRendered[aSide]) return null;
            if (mCore.isOutput(aSide) && mCore.mDL.mSelfOut) return BlockTextureMulti.get(mCore.te().getBaseTexture(aSide, aShouldSideBeRendered), BlockTextureDefault.get(getIIconContainer(OVERLAY_ENERGY_RU)));
            return mCore.te().getBaseTexture(aSide, aShouldSideBeRendered);
        }
        // 超过了原本的方块，所以无论何时都要渲染，并且需要关闭环境光遮蔽来防止一些显示错误
        if (mCore.isInput(aSide)) {
            if (mCore.mD.mPreheat || mCore.mD.mCooldown) {
                if (mCore.mD.mCounterClockwise) return UT_CH.Texture.BlockTextureDefaultNoAO(getIIconContainer(OVERLAY_PREHEAT_L));
                return UT_CH.Texture.BlockTextureDefaultNoAO(getIIconContainer(OVERLAY_PREHEAT_R));
            }
            if (mCore.mD.mActive) {
                if (mCore.mD.mCounterClockwise) return UT_CH.Texture.BlockTextureDefaultNoAO(getIIconContainer(OVERLAY_ACTIVE_L));
                return UT_CH.Texture.BlockTextureDefaultNoAO(getIIconContainer(OVERLAY_ACTIVE_R));
            }
            return UT_CH.Texture.BlockTextureDefaultNoAO(getIIconContainer(OVERLAY));
        }
        return null;
    }
}
