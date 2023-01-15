package gregtech.asm.transformers;

import gregtech.asm.GT_ASM;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import static gregtech.asm.GT_ASM_UT.Name.*;
import static gregtech.asm.GT_ASM_UT.isObfuscated;

public class Forge_VersionDownloadFailFix_CH implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        // 为了避免匿名类在不同打包下顺序不同的问题，这里匹配所有的匿名类
        for (int i = 0; i < 10; ++i) if (transformedName.equals(C_ForgeVersion.clazzPath + "$"+i)) {
            ClassNode classNode = GT_ASM.makeNodes(basicClass);
//            GT_ASM.writePrettyPrintedOpCodesToFile(classNode, "C_ForgeVersion" + "$"+i);
            // 需要包含 run 方法并且匹配旧的 url 才是目标的匿名类
            for (MethodNode m: classNode.methods) if (M_run.matches(m)) {
                AbstractInsnNode at = m.instructions.getFirst();
                while (at != null) {
                    if (at.getOpcode() == Opcodes.LDC) {
                        assert at instanceof LdcInsnNode;
                        LdcInsnNode tNode = (LdcInsnNode)at;
                        if (tNode.cst.equals("http://files.minecraftforge.net/maven/net/minecraftforge/forge/promotions_slim.json")) break;
                    }
                    at = at.getNext();
                }
                if (at == null) return basicClass; // 不是对应的匿名类，直接跳过
                // 开始转换
                GT_ASM.logger.info("Transforming " + C_ForgeVersion+"$"+i + "." + M_run);
                // 删除过时的 url
                AbstractInsnNode tAt = at.getPrevious();
                m.instructions.remove(at);
                at = tAt;
                // 添加自定义的新的 url
                m.instructions.insert(at, M_getForgeVersionUrl.staticInvocation(isObfuscated()));
            }
            
            return GT_ASM.writeByteArraySelfReferenceFixup(classNode);
        }
        return basicClass;
    }
}
