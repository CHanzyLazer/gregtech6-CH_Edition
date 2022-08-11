package gregtech.asm.transformers;

import gregtech.asm.GT_ASM;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import static gregtech.asm.GT_ASM_UT.Name;
import static gregtech.asm.GT_ASM_UT.Name.*;
import static gregtech.asm.GT_ASM_UT.isObfuscated;

// 修复莫名其妙 GT 方块颜色可能正常显示又可能不能正常显示的问题
public class Journeymap_BlockGTColorFix_CH implements IClassTransformer  {
	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		if (!transformedName.equals(C_JM_VanillaBlockHandler.clazzPath)) return basicClass;
		ClassNode classNode = GT_ASM.makeNodes(basicClass);
//		GT_ASM.writePrettyPrintedOpCodesToFile(classNode, "C_JM_VanillaBlockHandler");
		for (MethodNode m: classNode.methods) if (M_JM_preInitialize.matches(m)) {
			GT_ASM.logger.info("Transforming " + C_JM_VanillaBlockHandler + "." + M_JM_preInitialize);
			AbstractInsnNode at = m.instructions.getLast();
			// 跳转到 return 行
			while (at != null) {
				if (at.getOpcode() == Opcodes.RETURN) break;
				at = at.getPrevious();
			}
			if (at == null) {
				GT_ASM.logger.warn("Reached `null` in `at` too soon!  No changes made, bailing!");
				return GT_ASM.writeByteArraySelfReferenceFixup(classNode);
			}
			// 在 at 前插入
			m.instructions.insertBefore(at, getCustomBiomeColorInsert(M_MultiTileEntityBlock));
			m.instructions.insertBefore(at, getCustomBiomeColorInsert(M_PrefixBlock));
			m.instructions.insertBefore(at, getCustomBiomeColorInsert(M_BlockBase));
		}

		return GT_ASM.writeByteArraySelfReferenceFixup(classNode);
	}

	private InsnList getCustomBiomeColorInsert(Name aMethod) {
		InsnList insert = new InsnList();
		insert.add(new VarInsnNode(Opcodes.ALOAD, 0));
		insert.add(aMethod.staticInvocation(isObfuscated())); // 不能简单的通过 LDC 来直接调用，使用调用方法来间接获取 class
		insert.add(new InsnNode(Opcodes.ICONST_1));
		insert.add(new TypeInsnNode(Opcodes.ANEWARRAY, C_JM_Flag.deobf));
		insert.add(new InsnNode(Opcodes.DUP));
		insert.add(new InsnNode(Opcodes.ICONST_0));
		insert.add(F_JM_CustomBiomeColor.staticGet(isObfuscated()));
		insert.add(new InsnNode(Opcodes.AASTORE));
		insert.add(M_JM_setFlags.specialInvocation(isObfuscated()));
		return insert;
	}
}
