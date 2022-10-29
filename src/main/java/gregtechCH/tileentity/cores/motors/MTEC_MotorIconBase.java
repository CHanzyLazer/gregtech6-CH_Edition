package gregtechCH.tileentity.cores.motors;

import gregapi.render.BlockTextureDefault;
import gregapi.render.BlockTextureMulti;
import gregapi.render.IIconContainer;
import gregapi.render.ITexture;
import gregtechCH.data.CS_CH;
import gregtechCH.tileentity.cores.IMTEC_Texture;
import net.minecraft.block.Block;

import static gregtechCH.data.CS_CH.DIR_ICON;

// 管理涡轮的图像
public abstract class MTEC_MotorIconBase implements IMTEC_Texture {
    // the reference of MTEC_Motor
    protected final MTEC_Motor mCore;
    protected MTEC_MotorIconBase(MTEC_Motor aCore) {mCore = aCore; assert mCore.mDI == this;}
    
    protected IIconContainer getIIconContainerOverlay(CS_CH.IconType aIconType) {
        switch (aIconType) {
            case FRONT:
                return getOverlayFrontIcon();
            case BACK:
                return getOverlayBackIcon();
            case SIDE_UP:
                return getOverlaySideUpIcon();
            case SIDE_DOWN:
                return getOverlaySideDownIcon();
            case SIDE_LEFT:
                return getOverlaySideLeftIcon();
            case SIDE_RIGHT:
                return getOverlaySideRightIcon();
            case VOID: default:
                return null;
        }
    }
    protected abstract IIconContainer getColoredIcon            (byte aSide);
    protected abstract IIconContainer getOverlayFrontIcon       ();
    protected abstract IIconContainer getOverlayBackIcon        ();
    protected abstract IIconContainer getOverlaySideUpIcon      ();
    protected abstract IIconContainer getOverlaySideDownIcon    ();
    protected abstract IIconContainer getOverlaySideLeftIcon    ();
    protected abstract IIconContainer getOverlaySideRightIcon   ();
    
    @Override public ITexture getTexture(Block aBlock, int aRenderPass, byte aSide, boolean[] aShouldSideBeRendered) {
        if (!aShouldSideBeRendered[aSide]) return null;
        return BlockTextureMulti.get(BlockTextureDefault.get(getColoredIcon(aSide), mCore.mTE.getRGBa()), BlockTextureDefault.get(getIIconContainerOverlay(DIR_ICON[aSide][mCore.mTE.mFacing])));
    }
}
