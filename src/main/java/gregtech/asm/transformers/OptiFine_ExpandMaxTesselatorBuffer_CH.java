package gregtech.asm.transformers;

import gregtech.asm.GT_ASM;
import gregtech.asm.GT_ASM_UT;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import static gregtech.asm.GT_ASM_UT.Name.C_OptiFine_ShadersTess;
import static gregtech.asm.GT_ASM_UT.Name.M_OptiFine_addVertex;

/**
 * @author CHanzy
 * 扩展 OptiFine 的最大 TesselatorBuffer，避免因为多边形过多而崩溃
 */
public class OptiFine_ExpandMaxTesselatorBuffer_CH implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (transformedName.equals(C_OptiFine_ShadersTess.clazzPath)) {
            ClassNode classNode = GT_ASM.makeNodes(basicClass);
//            GT_ASM.writePrettyPrintedOpCodesToFile(classNode, "C_OptiFine_ShadersTess");
            // 替换 addVertex 内的常量
            for (MethodNode m: classNode.methods) if (M_OptiFine_addVertex.matches(m)) {
                int tOldValue = 16777216, tNewValue = tOldValue<<4; // 新值会将限制增加到 1G
                GT_ASM.logger.info("Transforming first value "+tOldValue+" to "+tNewValue+" in " + C_OptiFine_ShadersTess + "." + M_OptiFine_addVertex);
                if (!GT_ASM_UT.transformInlinedNumberMethod(m, tOldValue, tNewValue))
                    GT_ASM.logger.warn("Cant find value "+tOldValue+", No changes made, bailing!");
            }
            return GT_ASM.writeByteArraySelfReferenceFixup(classNode, 0);
        }
        
        return basicClass;
    }
}
