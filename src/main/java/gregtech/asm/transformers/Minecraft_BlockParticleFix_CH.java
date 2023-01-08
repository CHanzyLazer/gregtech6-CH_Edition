package gregtech.asm.transformers;

import gregtech.asm.GT_ASM;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import static gregtech.asm.GT_ASM_UT.Name.*;
import static gregtech.asm.GT_ASM_UT.isObfuscated;
import static gregtech.asm.GT_ASM_UT.removeLine;

/**
 * @author CHanzy
 * 修复一些粒子效果错误，使其和破坏方块一样调用 applyColourMultiplier 方法
 */
public class Minecraft_BlockParticleFix_CH implements IClassTransformer  {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        // 修复奔跑时的粒子效果
        if (transformedName.equals(C_Entity.clazzPath)) {
            ClassNode classNode = GT_ASM.makeNodes(basicClass);
//			GT_ASM.writePrettyPrintedOpCodesToFile(classNode, "C_Entity");
            for (MethodNode m: classNode.methods) if (M_onEntityUpdate.matches(m)) {
                GT_ASM.logger.info("Transforming " + C_Entity + "." + M_onEntityUpdate);
                AbstractInsnNode at = m.instructions.getFirst();
                // 先获取 j i k block 的序号
                while (at != null) {
                    // 匹配调用 getBlock 的操作
                    if (at.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                        assert at instanceof MethodInsnNode;
                        MethodInsnNode tNode = (MethodInsnNode)at;
                        if (M_getBlock.matches(tNode)) break;
                    }
                    at = at.getNext();
                }
                if (at == null) {
                    GT_ASM.logger.warn("Reached `null` in `at (0)` too soon!  No changes made, bailing!");
                    return GT_ASM.writeByteArraySelfReferenceFixup(classNode);
                }
                int tJIdx = ((VarInsnNode)at.getPrevious().getPrevious().getPrevious()).var;    // get var of j
                int tIIdx = ((VarInsnNode)at.getPrevious().getPrevious()).var;                  // get var of i
                int tKIdx = ((VarInsnNode)at.getPrevious()).var;                                // get var of k
                int tBlockIdx = ((VarInsnNode)at.getNext()).var;                                // get var of block
                // 再查找需要替换的行
                while (at != null) {
                    if (at instanceof LdcInsnNode && ((LdcInsnNode)at).cst.equals("blockcrack_")) break; // 跳转到 "blockcrack_" 段
                    at = at.getNext();
                }
                if (at == null) {
                    GT_ASM.logger.warn("Reached `null` in `at (1)` too soon!  No changes made, bailing!");
                    return GT_ASM.writeByteArraySelfReferenceFixup(classNode);
                }
                // 直接移除整行的代码
                at = removeLine(m.instructions, at);
                // 在 at 后插入我需要的代码
                InsnList insert = new InsnList();
                insert.add(new VarInsnNode(Opcodes.ALOAD, 0));          // Load entity
                insert.add(new VarInsnNode(Opcodes.ALOAD, 0));          // Load this
                insert.add(F_worldObj.virtualGet(isObfuscated()));                  // Load worldObj
                insert.add(new VarInsnNode(Opcodes.ALOAD, tBlockIdx));  // Load block
                insert.add(new VarInsnNode(Opcodes.ILOAD, tJIdx));      // Load j
                insert.add(new VarInsnNode(Opcodes.ILOAD, tIIdx));      // Load i
                insert.add(new VarInsnNode(Opcodes.ILOAD, tKIdx));      // Load k
                insert.add(M_spawnSprintingParticle.staticInvocation(isObfuscated()));
                m.instructions.insert(at, insert);
            }
//			GT_ASM.writePrettyPrintedOpCodesToFile(classNode, "C_Entity_Modified"); // DEBUG
            
            return GT_ASM.writeByteArraySelfReferenceFixup(classNode);
        }
        
        // 修复摔落时的粒子效果
        if (transformedName.equals(C_RenderGlobal.clazzPath)) {
            ClassNode classNode = GT_ASM.makeNodes(basicClass);
//			GT_ASM.writePrettyPrintedOpCodesToFile(classNode, "C_RenderGlobal");
            for (MethodNode m: classNode.methods) if (M_playAuxSFX.matches(m)) {
                GT_ASM.logger.info("Transforming " + C_RenderGlobal + "." + M_playAuxSFX);
                AbstractInsnNode at = m.instructions.getFirst();
                // 先获取 p_72706_3_ p_72706_4_ p_72706_5_ block 的序号
                while (at != null) {
                    // 匹配调用 getBlock 的操作
                    if (at.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                        assert at instanceof MethodInsnNode;
                        MethodInsnNode tNode = (MethodInsnNode)at;
                        if (M_WC_getBlock.matches(tNode)) break; // 注意是通过 WorldClient 调用的，owner 不同
                    }
                    at = at.getNext();
                }
                if (at == null) {
                    GT_ASM.logger.warn("Reached `null` in `at (0)` too soon!  No changes made, bailing!");
                    return GT_ASM.writeByteArraySelfReferenceFixup(classNode);
                }
                int tXIdx = ((VarInsnNode)at.getPrevious().getPrevious().getPrevious()).var;    // get var of p_72706_3_
                int tYIdx = ((VarInsnNode)at.getPrevious().getPrevious()).var;                  // get var of p_72706_4_
                int tZIdx = ((VarInsnNode)at.getPrevious()).var;                                // get var of p_72706_5_
                int tBlockIdx = ((VarInsnNode)at.getNext()).var;                                // get var of block
                // 再查找需要替换的行
                while (at != null) {
                    if (at instanceof LdcInsnNode && ((LdcInsnNode)at).cst.equals("blockdust_")) break; // 跳转到 "blockdust_" 段
                    at = at.getNext();
                }
                if (at == null) {
                    GT_ASM.logger.warn("Reached `null` in `at (1)` too soon!  No changes made, bailing!");
                    return GT_ASM.writeByteArraySelfReferenceFixup(classNode);
                }
                // 向前获取 d8 d7 d6
                AbstractInsnNode at2 = at.getPrevious();
                while (at2 != null) {
                    // 匹配 DSTORE 操作
                    if (at2.getOpcode() == Opcodes.DSTORE) break;
                    at2 = at2.getPrevious();
                }
                if (at2 == null) {
                    GT_ASM.logger.warn("Reached `null` in `at2 (0)` too soon!  No changes made, bailing!");
                    return GT_ASM.writeByteArraySelfReferenceFixup(classNode);
                }
                int tD8Idx = ((VarInsnNode)at2).var;
                at2 = at2.getPrevious();
                while (at2 != null) {
                    // 匹配 DSTORE 操作
                    if (at2.getOpcode() == Opcodes.DSTORE) break;
                    at2 = at2.getPrevious();
                }
                if (at2 == null) {
                    GT_ASM.logger.warn("Reached `null` in `at2 (1)` too soon!  No changes made, bailing!");
                    return GT_ASM.writeByteArraySelfReferenceFixup(classNode);
                }
                int tD7Idx = ((VarInsnNode)at2).var;
                at2 = at2.getPrevious();
                while (at2 != null) {
                    // 匹配 DSTORE 操作
                    if (at2.getOpcode() == Opcodes.DSTORE) break;
                    at2 = at2.getPrevious();
                }
                if (at2 == null) {
                    GT_ASM.logger.warn("Reached `null` in `at2 (2)` too soon!  No changes made, bailing!");
                    return GT_ASM.writeByteArraySelfReferenceFixup(classNode);
                }
                int tD6Idx = ((VarInsnNode)at2).var;
                // 直接移除整行的代码
                at = removeLine(m.instructions, at);
                // 在 at 后插入我需要的代码
                InsnList insert = new InsnList();
                insert.add(new VarInsnNode(Opcodes.ALOAD, 0));          // Load this
                insert.add(F_theWorld.virtualGet(isObfuscated()));                  // Load theWorld
                insert.add(new VarInsnNode(Opcodes.ALOAD, tBlockIdx));  // Load block
                insert.add(new VarInsnNode(Opcodes.ILOAD, tXIdx));      // Load p_72706_3_
                insert.add(new VarInsnNode(Opcodes.ILOAD, tYIdx));      // Load p_72706_4_
                insert.add(new VarInsnNode(Opcodes.ILOAD, tZIdx));      // Load p_72706_5_
                insert.add(new VarInsnNode(Opcodes.DLOAD, tD7Idx));     // Load d7
                insert.add(new VarInsnNode(Opcodes.DLOAD, tD6Idx));     // Load d6
                insert.add(new VarInsnNode(Opcodes.DLOAD, tD8Idx));     // Load d8
                insert.add(M_spawnFallParticle.staticInvocation(isObfuscated()));
                m.instructions.insert(at, insert);
            }
//			GT_ASM.writePrettyPrintedOpCodesToFile(classNode, "C_RenderGlobal_Modified"); // DEBUG
            
            return GT_ASM.writeByteArraySelfReferenceFixup(classNode);
        }
        
        return basicClass;
    }
}
