package gregtechCH.tileentity.cores.motors;

import gregapi.render.BlockTextureDefault;
import gregapi.render.BlockTextureMulti;
import gregapi.render.IIconContainer;
import gregapi.render.ITexture;
import gregtechCH.tileentity.cores.IMTEC_Texture;
import gregtechCH.util.UT_CH;
import net.minecraft.block.Block;

import static gregapi.data.CS.*;

/**
 * @author CHanzy
 * 管理涡轮的图像
 */
public abstract class MTEC_MotorIconBase implements IMTEC_Texture {
    // the reference of MTEC_Motor
    protected final MTEC_Motor mCore;
    protected MTEC_MotorIconBase(MTEC_Motor aCore) {mCore = aCore;}
    
    protected abstract IIconContainer getColoredIcon        (byte aSide);
    protected abstract IIconContainer getOverlayFrontIcon   ();
    protected abstract IIconContainer getOverlayBackIcon    ();
    protected abstract IIconContainer getOverlaySideIcon    (byte aSide);
    
    @Override public ITexture getTexture(Block aBlock, int aRenderPass, byte aSide, boolean[] aShouldSideBeRendered) {
        if (!aShouldSideBeRendered[aSide]) return null;
        return BlockTextureMulti.get(BlockTextureDefault.get(getColoredIcon(aSide), mCore.mTE.getRGBa()), BlockTextureDefault.get(SIDES_EQUAL[aSide][mCore.mTE.mFacing]?getOverlayFrontIcon():(SIDES_EQUAL[aSide][OPOS[mCore.mTE.mFacing]]?getOverlayBackIcon():getOverlaySideIcon(aSide))));
    }
}
