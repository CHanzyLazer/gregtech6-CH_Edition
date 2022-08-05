package gregtech.asm.transformers;

import gregapi.data.MD;
import gregtech.asm.GT_ASM;
import gregtech.asm.GT_ASM_UT;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import static gregtech.asm.GT_ASM_UT.Name.*;
import static gregtech.asm.GT_ASM_UT.isObfuscated;

/**
 * @author CHanzy
 * 修复在方块上奔跑的粒子效果错误，使其和破坏方块一样调用 applyColourMultiplier 方法
 */
public class Minecraft_BlockParticleFix_CH implements IClassTransformer  {
	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		if (!transformedName.equals(C_Entity.clazzPath)) return basicClass;
		ClassNode classNode = GT_ASM.makeNodes(basicClass);
//		GT_ASM.writePrettyPrintedOpCodesToFile(classNode, "C_Entity");
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
				GT_ASM.logger.warn("Reached `null` in `at` too soon!  No changes made, bailing!");
				return GT_ASM.writeByteArraySelfReferenceFixup(classNode);
			}
			int tJIdx = ((VarInsnNode)at.getPrevious().getPrevious().getPrevious()).var;	// get var of j
			int tIIdx = ((VarInsnNode)at.getPrevious().getPrevious()).var;   				// get var of i
			int tKIdx = ((VarInsnNode)at.getPrevious()).var;   							   	// get var of k
			int tBlockIdx = ((VarInsnNode)at.getNext()).var;   							   	// get var of block
			// 再查找需要替换的行
			while (at != null) {
				if (at instanceof LdcInsnNode && ((LdcInsnNode)at).cst.equals("blockcrack_")) break; // 跳转到 "blockcrack_" 段
				at = at.getNext();
			}
			if (at == null) {
				GT_ASM.logger.warn("Reached `null` in `at` too soon!  No changes made, bailing!");
				return GT_ASM.writeByteArraySelfReferenceFixup(classNode);
			}
			// 直接移除整行的代码
			// 删除前的代码
			AbstractInsnNode at1 = at.getPrevious();
			while (at1 != null && !(at1 instanceof LineNumberNode)) {
				AbstractInsnNode tAt1 = at1.getPrevious();
				m.instructions.remove(at1); // 注意 remove 会使这个节点失效
				at1 = tAt1;
			}
			// 删除后的代码
			at1 = at.getNext();
			while (at1 != null && !(at1 instanceof LabelNode)) {
				AbstractInsnNode tAt1 = at1.getNext();
				m.instructions.remove(at1); // 注意 remove 会使这个节点失效
				at1 = tAt1;
			}
			// 在 at 前插入我需要的代码
			InsnList insert = new InsnList();
			insert.add(new VarInsnNode(Opcodes.ALOAD, 0)); 			// Load entity
			insert.add(new VarInsnNode(Opcodes.ALOAD, 0)); 			// Load this
			insert.add(F_worldObj.virtualGet(isObfuscated())); 					// Load worldObj
			insert.add(new VarInsnNode(Opcodes.ALOAD, tBlockIdx)); 	// Load block
			insert.add(new VarInsnNode(Opcodes.ILOAD, tJIdx)); 		// Load j
			insert.add(new VarInsnNode(Opcodes.ILOAD, tIIdx)); 		// Load i
			insert.add(new VarInsnNode(Opcodes.ILOAD, tKIdx)); 		// Load k
			insert.add(M_spawnSprintingParticle.staticInvocation(isObfuscated()));
			m.instructions.insertBefore(at, insert);
			// 最后删除不必要的 at
			m.instructions.remove(at);
		}
//		GT_ASM.writePrettyPrintedOpCodesToFile(classNode, "C_Entity_Modified"); // DEBUG

		return GT_ASM.writeByteArraySelfReferenceFixup(classNode);
	}
}
