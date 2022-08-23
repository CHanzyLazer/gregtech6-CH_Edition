package gregtechCH.tileentity.cores;

import gregapi.render.ITexture;
import net.minecraft.block.Block;

/**
 * @author CHanzy
 * 包含管理 Texture 的 core 可以继承的接口
 **/
public interface IMTEC_Texture {
    public ITexture getTexture(Block aBlock, int aRenderPass, byte aSide, boolean[] aShouldSideBeRendered);
}
