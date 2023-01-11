package gregtech.asm.transformers;

import gregtech.asm.GT_ASM;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import static gregtech.asm.GT_ASM_UT.Name.*;
import static gregtech.asm.GT_ASM_UT.isObfuscated;

/**
 * @author CHanzy
 * 用于自定义何种原版的物品可以在潜行时使用功能，主要用于潜行安装红石火把和红石中继器
 */
public class Minecraft_CustomSneakBypassUse_CH implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (transformedName.equals(C_Item.clazzPath)) {
            ClassNode classNode = GT_ASM.makeNodes(basicClass);
//			GT_ASM.writePrettyPrintedOpCodesToFile(classNode, "C_Item");
            for (MethodNode m: classNode.methods) if (M_doesSneakBypassUse.matches(m)) {
                GT_ASM.logger.info("Transforming " + C_Item + "." + M_doesSneakBypassUse);
                m.instructions.clear();
                m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0)); // Load Item
                m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1)); // Load world
                m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 2)); // Load x
                m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 3)); // Load y
                m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 4)); // Load z
                m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 5)); // Load player
                m.instructions.add(M_MCItemDoesSneakBypassUse.staticInvocation(isObfuscated()));
                m.instructions.add(new InsnNode(Opcodes.IRETURN));
                m.maxStack = 6;
            }
            
            return GT_ASM.writeByteArraySelfReferenceFixup(classNode, 0); // default flags will just mess up everything here
        }
        return basicClass;
    }
}
