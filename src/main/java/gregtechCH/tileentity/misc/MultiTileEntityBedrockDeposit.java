package gregtechCH.tileentity.misc;


import gregapi.render.BlockTextureCopied;
import gregapi.render.BlockTextureMulti;
import gregapi.render.ITexture;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

/**
 * @author CHanzy
 * 基岩矿藏，材质不同且挖掘完成后变回基岩
 */
public class MultiTileEntityBedrockDeposit extends MultiTileEntityDeposit {
    // 达到最后一个状态后的行为，对于基岩矿则变成基岩
    @Override protected void onLastState() {
        if (mState < maxSate()) return;
        worldObj.setBlock(xCoord, yCoord, zCoord, Blocks.bedrock , 0, 3);
    }
    @Override public ITexture getTexture(Block aBlock, int aRenderPass, byte aSide, boolean[] aShouldSideBeRendered) {return aShouldSideBeRendered[aSide] ? BlockTextureMulti.get(BlockTextureCopied.get(Blocks.bedrock, 0), getTexturePrefix()) : null;}
    
    @Override public String getTileEntityName() {return "gt.multitileentity.deposit.bedrock";}
}
