package gregtech.asm.transformers;

import gregtech.asm.GT_ASM;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import static gregtech.asm.GT_ASM_UT.Name;
import static gregtech.asm.GT_ASM_UT.Name.*;
import static gregtech.asm.GT_ASM_UT.isObfuscated;

// 修复 BC 管道会自动连接太多 GT 方块的问题
public class BuildCraft_PipeAutoConnectFix_CH implements IClassTransformer  {
	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		// 物品管道
		if (transformedName.equals(C_BC_PipeItem.clazzPath)) {
			ClassNode classNode = GT_ASM.makeNodes(basicClass);
//			GT_ASM.writePrettyPrintedOpCodesToFile(classNode, "C_BC_PipeItem");
			for (MethodNode m: classNode.methods) if (M_BC_canPipeConnect.matches(m)) {
				GT_ASM.logger.info("Transforming " + C_BC_PipeItem + "." + M_BC_canPipeConnect);
				AbstractInsnNode at = m.instructions.getFirst();
				// 跳转到第一个 INSTANCEOF buildcraft/api/transport/IPipeTile 行
				while (at != null) {
					if (at.getOpcode() == Opcodes.INSTANCEOF) {
						assert at instanceof TypeInsnNode;
						TypeInsnNode tNode = (TypeInsnNode)at;
						if (tNode.desc.equals(C_BC_IPipe.deobf)) break;
					}
					at = at.getNext();
				}
				if (at == null) {
					GT_ASM.logger.warn("Reached `null` in `at` too soon!  No changes made, bailing!");
					return GT_ASM.writeByteArraySelfReferenceFixup(classNode);
				}
				at = at.getPrevious(); // 到达 ALOAD 1
				// 在 at 前插入额外的判断
				InsnList insert = new InsnList();
				insert.add(new VarInsnNode(Opcodes.ALOAD, 1)); 			// Load tile
				insert.add(new VarInsnNode(Opcodes.ALOAD, 2)); 			// Load side
				insert.add(M_interceptModConnectItem.staticInvocation(isObfuscated()));
				LabelNode after = new LabelNode();
				insert.add(new JumpInsnNode(Opcodes.IFEQ, after));     // 如果满足（为一）阻止连接的条件则继续后续，直接 return false 阻止连接，因此不满足（为零）则需要跳过这段
				insert.add(new InsnNode(Opcodes.ICONST_0));
				insert.add(new InsnNode(Opcodes.IRETURN));
				insert.add(after);													// 不满足则跳转到这里
				m.instructions.insertBefore(at, insert);
			}
			return GT_ASM.writeByteArraySelfReferenceFixup(classNode);
		}
		// 流体管道
		if (transformedName.equals(C_BC_PipeFluid.clazzPath)) {
			ClassNode classNode = GT_ASM.makeNodes(basicClass);
//			GT_ASM.writePrettyPrintedOpCodesToFile(classNode, "C_BC_PipeFluid");
			for (MethodNode m: classNode.methods) if (M_BC_canPipeConnect.matches(m)) {
				GT_ASM.logger.info("Transforming " + C_BC_PipeFluid + "." + M_BC_canPipeConnect);
				AbstractInsnNode at = m.instructions.getFirst();
				// 跳转到第一个 INSTANCEOF buildcraft/api/transport/IPipeTile 行
				while (at != null) {
					if (at.getOpcode() == Opcodes.INSTANCEOF) {
						assert at instanceof TypeInsnNode;
						TypeInsnNode tNode = (TypeInsnNode)at;
						if (tNode.desc.equals(C_BC_IPipe.deobf)) break;
					}
					at = at.getNext();
				}
				if (at == null) {
					GT_ASM.logger.warn("Reached `null` in `at` too soon!  No changes made, bailing!");
					return GT_ASM.writeByteArraySelfReferenceFixup(classNode);
				}
				at = at.getPrevious(); // 到达 ALOAD 1
				// 在 at 前插入额外的判断
				InsnList insert = new InsnList();
				insert.add(new VarInsnNode(Opcodes.ALOAD, 1)); 			// Load tile
				insert.add(new VarInsnNode(Opcodes.ALOAD, 2)); 			// Load side
				insert.add(M_interceptModConnectFluid.staticInvocation(isObfuscated()));
				LabelNode after = new LabelNode();
				insert.add(new JumpInsnNode(Opcodes.IFEQ, after));     // 如果满足（为一）阻止连接的条件则继续后续，直接 return false 阻止连接，因此不满足（为零）则需要跳过这段
				insert.add(new InsnNode(Opcodes.ICONST_0));
				insert.add(new InsnNode(Opcodes.IRETURN));
				insert.add(after);													// 不满足则跳转到这里
				m.instructions.insertBefore(at, insert);
			}
			return GT_ASM.writeByteArraySelfReferenceFixup(classNode);
		}

		return basicClass;
	}
}
