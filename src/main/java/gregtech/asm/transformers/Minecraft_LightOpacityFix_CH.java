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
 * GTCH, 用来向区块中添加数据专门记录方块的不透光度（仅限GT的方块），来避免实体卸载造成的光照计算错误
 */
public class Minecraft_LightOpacityFix_CH implements IClassTransformer  {
	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		// 向 ExtendedBlockStorage 中添加成员变量
		if (transformedName.equals(C_EBS.clazzPath)) {
			ClassNode classNode = GT_ASM.makeNodes(basicClass);
			GT_ASM.logger.info("Adding field " + C_NA + "." + F_GTLO);
			classNode.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, F_GTLO.deobf, F_GTLO.desc, null, null));
			return GT_ASM.writeByteArray(classNode);
		}
		// 修改接口函数使其名副其实
		if (transformedName.equals(C_GTASM_LO.clazzPath)) {
			ClassNode classNode = GT_ASM.makeNodes(basicClass);
			// 替换 bool 值让程序知道已经执行了 ASM
			for (MethodNode m: classNode.methods) if (M_enabledLO.matches(m)) {
				GT_ASM.logger.info("Transforming first value F to T in " + C_GTASM_LO + "." + M_enabledLO);
				if (!GT_ASM_UT.transformInlinedBoolMethod(m, false, true))
					GT_ASM.logger.warn("Cant find value F, No changes made, bailing!");
			}
			// 提供读取成员变量的接口
			for (MethodNode m: classNode.methods) if (M_getLO.matches(m)) {
				GT_ASM.logger.info("Transforming " + C_GTASM_LO + "." + M_getLO);
				m.instructions.clear();
				m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0)); // Load ExtendedBlockStorage
				m.instructions.add(F_GTLO.virtualGet(isObfuscated()));
				m.instructions.add(new InsnNode(Opcodes.ARETURN));
			}
			// 提供创造成员变量的接口
			for (MethodNode m: classNode.methods) if (M_initLO.matches(m)) {
				GT_ASM.logger.info("Transforming " + C_GTASM_LO + "." + M_initLO);
				m.instructions.clear();
				m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0)); // Load ExtendedBlockStorage
				m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1)); // Load NibbleArray
				m.instructions.add(F_GTLO.virtualSet(isObfuscated()));
				m.instructions.add(new InsnNode(Opcodes.RETURN));
			}
			// 提供清空成员变量的接口
			for (MethodNode m: classNode.methods) if (M_clearLO.matches(m)) {
				GT_ASM.logger.info("Transforming " + C_GTASM_LO + "." + M_clearLO);
				m.instructions.clear();
				m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0)); // Load ExtendedBlockStorage
				m.instructions.add(new InsnNode(Opcodes.ACONST_NULL));
				m.instructions.add(F_GTLO.virtualSet(isObfuscated()));
				m.instructions.add(new InsnNode(Opcodes.RETURN));
			}
			return GT_ASM.writeByteArray(classNode);
		}
		// 存入读取 NBT
		if (transformedName.equals(C_ACL.clazzPath)) {
			ClassNode classNode = GT_ASM.makeNodes(basicClass);
//			GT_ASM.writePrettyPrintedOpCodesToFile(classNode, "C_ACL");
			// 写入 NBT
			for (MethodNode m: classNode.methods) if (M_writeChunkToNBT.matches(m)) {
				GT_ASM.logger.info("Transforming " + C_ACL + "." + M_writeChunkToNBT);
				AbstractInsnNode at = m.instructions.getFirst();
				while (at != null) {
					if (at instanceof LdcInsnNode && ((LdcInsnNode)at).cst.equals("BlockLight")) break; // 跳转到 "BlockLight" 段
					at = at.getNext();
				}
				if (at == null) {
					GT_ASM.logger.warn("Reached `null` in `at (0)` too soon!  No changes made, bailing!");
					return GT_ASM.writeByteArraySelfReferenceFixup(classNode);
				}
				// 由于序号可能会发生变化，采用直接从源码中获取序号的方式
				int tEBSIdx = ((VarInsnNode)at.getNext()).var;     // get var of extendedblockstorage
				int tNBTIdx = ((VarInsnNode)at.getPrevious()).var; // get var of nbttagcompound1
				for (int i=0; i<4 && at!=null; ++i) at = at.getNext(); // 再向后移动四段（移动到此代码行行末尾）
				if (at == null) {
					GT_ASM.logger.warn("Reached `null` in `at (1)` too soon!  No changes made, bailing!");
					return GT_ASM.writeByteArraySelfReferenceFixup(classNode);
				}
				// 编辑插入的代码
				InsnList insert = new InsnList();
				insert.add(new VarInsnNode(Opcodes.ALOAD, tEBSIdx)); // Load extendedblockstorage
				insert.add(new VarInsnNode(Opcodes.ALOAD, tNBTIdx)); // Load nbttagcompound1
				insert.add(M_GTWriteNBT.staticInvocation(isObfuscated()));
				m.instructions.insert(at, insert);
			}
			// 读取 NBT
			for (MethodNode m: classNode.methods) if (M_readChunkFromNBT.matches(m)) {
				GT_ASM.logger.info("Transforming " + C_ACL + "." + M_readChunkFromNBT);
				AbstractInsnNode at = m.instructions.getFirst();
				while (at != null) {
					if (at instanceof LdcInsnNode && ((LdcInsnNode)at).cst.equals("BlockLight")) break; // 跳转到 "BlockLight" 段
					at = at.getNext();
				}
				if (at == null) {
					GT_ASM.logger.warn("Reached `null` in `at (0)` too soon!  No changes made, bailing!");
					return GT_ASM.writeByteArraySelfReferenceFixup(classNode);
				}
				// 由于序号可能会发生变化，采用直接从源码中获取序号的方式
				int tEBSIdx = ((VarInsnNode)at.getPrevious().getPrevious().getPrevious().getPrevious()).var; // get var of extendedblockstorage
				int tNBTIdx = ((VarInsnNode)at.getPrevious()).var; 											 // get var of nbttagcompound1
				for (int i=0; i<4 && at!=null; ++i) at = at.getNext(); // 再向后移动四段（移动到此代码行行末尾）
				if (at == null) {
					GT_ASM.logger.warn("Reached `null` in `at (1)` too soon!  No changes made, bailing!");
					return GT_ASM.writeByteArraySelfReferenceFixup(classNode);
				}
				// 编辑插入的代码
				InsnList insert = new InsnList();
				insert.add(new VarInsnNode(Opcodes.ALOAD, tEBSIdx)); // Load extendedblockstorage
				insert.add(new VarInsnNode(Opcodes.ALOAD, tNBTIdx)); // Load nbttagcompound1
				insert.add(M_GTReadNBT.staticInvocation(isObfuscated()));
				m.instructions.insert(at, insert);
			}
			return GT_ASM.writeByteArraySelfReferenceFixup(classNode); // 对于有继承关系的类需要使用这个接口
		}
		// 数据同步
		// 修改重新计算光照时的数据包 S21PacketChunkData
		if (transformedName.equals(C_PacketS21.clazzPath)) {
			ClassNode classNode = GT_ASM.makeNodes(basicClass);
//			GT_ASM.writePrettyPrintedOpCodesToFile(classNode, "C_PacketS21");
			// 替换行内的常量初始化，private static byte[] field_149286_i = new byte[196864];
			for (MethodNode m: classNode.methods) if (m.name.equals("<clinit>")) {
				int tOldValue = 196864, tNewValue = tOldValue+(2048*16);
				// 对于 NotEnoughIDs 加载的情况特殊讨论
				if (MD.NEID.mLoaded) {
					int tOldValue2 = 229632; tNewValue = tOldValue2+(2048*16);
					GT_ASM.logger.info("Transforming first value "+tOldValue+" to "+tNewValue+" in " + C_PacketS21 + ".<clinit>");
					if (!GT_ASM_UT.transformInlinedNumberMethod(m, tOldValue2, tNewValue) && !GT_ASM_UT.transformInlinedNumberMethod(m, tOldValue, tNewValue))
						GT_ASM.logger.warn("Cant find value "+tOldValue+", No changes made, bailing!");
				} else {
					GT_ASM.logger.info("Transforming first value "+tOldValue+" to "+tNewValue+" in " + C_PacketS21 + ".<clinit>");
					if (!GT_ASM_UT.transformInlinedNumberMethod(m, tOldValue, tNewValue))
						GT_ASM.logger.warn("Cant find value "+tOldValue+", No changes made, bailing!");
				}
			}
			// 替换 func_149275_c 内的常量
			for (MethodNode m: classNode.methods) if (M_func_149275_c.matches(m)) {
				int tOldValue = 196864, tNewValue = tOldValue+(2048*16);
				// 对于 NotEnoughIDs 加载的情况特殊讨论
				if (MD.NEID.mLoaded) {
					int tOldValue2 = 229632; tNewValue = tOldValue2+(2048*16);
					GT_ASM.logger.info("Transforming first value "+tOldValue+" to "+tNewValue+" in " + C_PacketS21 + "." + M_func_149275_c);
					if (!GT_ASM_UT.transformInlinedNumberMethod(m, tOldValue2, tNewValue) && !GT_ASM_UT.transformInlinedNumberMethod(m, tOldValue, tNewValue))
						GT_ASM.logger.warn("Cant find value "+tOldValue+", No changes made, bailing!");
				} else {
					GT_ASM.logger.info("Transforming first value "+tOldValue+" to "+tNewValue+" in " + C_PacketS21 + "." + M_func_149275_c);
					if (!GT_ASM_UT.transformInlinedNumberMethod(m, tOldValue, tNewValue))
						GT_ASM.logger.warn("Cant find value "+tOldValue+", No changes made, bailing!");
				}
			}
			// 替换 readPacketData 内的常量
			for (MethodNode m: classNode.methods) if (M_readPacketData.matches(m)) {
				int tOldValue = 12288, tNewValue = tOldValue+2048; // 由于 mc 并没有提供更多的数据来确定数据是否存在，方便起见，无论如何都发送 GT 的不透光度数据
				// 对于 NotEnoughIDs 加载的情况特殊讨论
				if (MD.NEID.mLoaded) {
					int tOldValue2 = 14336; tNewValue = tOldValue2+2048;
					GT_ASM.logger.info("Transforming first value "+tOldValue+" to "+tNewValue+" in " + C_PacketS21 + "." + M_readPacketData);
					if (!GT_ASM_UT.transformInlinedNumberMethod(m, tOldValue2, tNewValue) && !GT_ASM_UT.transformInlinedNumberMethod(m, tOldValue, tNewValue))
						GT_ASM.logger.warn("Cant find value "+tOldValue+", No changes made, bailing!");
				} else {
					GT_ASM.logger.info("Transforming first value "+tOldValue+" to "+tNewValue+" in " + C_PacketS21 + "." + M_readPacketData);
					if (!GT_ASM_UT.transformInlinedNumberMethod(m, tOldValue, tNewValue))
						GT_ASM.logger.warn("Cant find value "+tOldValue+", No changes made, bailing!");
				}
			}
			// 修改函数 func_149269_a 来将数据写入 byte
			for (MethodNode m: classNode.methods) if (M_func_149269_a.matches(m)) {
				GT_ASM.logger.info("Transforming " + C_PacketS21 + "." + M_func_149269_a);
				AbstractInsnNode at = m.instructions.getFirst();
				// 获取 aextendedblockstorage 的序号，需要匹配到 getBlockStorageArray 的调用
				while (at != null) {
					if (at.getOpcode() == Opcodes.INVOKEVIRTUAL) {
						assert at instanceof MethodInsnNode;
						MethodInsnNode tNode = (MethodInsnNode)at;
						if (M_getBlockStorageArray.matches(tNode)) break;
					}
					at = at.getNext();
				}
				if (at == null) {
					GT_ASM.logger.warn("Reached `null` in `at (0)` too soon!  No changes made, bailing!");
					return GT_ASM.writeByteArraySelfReferenceFixup(classNode);
				}
				int tEBSIdx = ((VarInsnNode)at.getNext()).var; // get var of aextendedblockstorage
				at = m.instructions.getLast(); // 改为 last 用于匹配最后一个
				while (at != null) {
					// 匹配最后的 PUTFIELD 操作，在之前插入 GTLO 的数据
					if (at.getOpcode() == Opcodes.PUTFIELD) {
						assert at instanceof FieldInsnNode;
						FieldInsnNode tNode = (FieldInsnNode)at;
						if (F_field_150282_a.matches(tNode)) break;
					}
					at = at.getPrevious();
				}
				if (at == null) {
					GT_ASM.logger.warn("Reached `null` in `at (1)` too soon!  No changes made, bailing!");
					return GT_ASM.writeByteArraySelfReferenceFixup(classNode);
				}
				int tByteIdx = ((VarInsnNode)at.getNext().getNext().getNext()).var; // get var of abyte
				int tJIdx    = ((VarInsnNode)at.getPrevious().getPrevious()).var;   // get var of j
				for (int i=0; i<3 && at!=null; ++i) at = at.getPrevious(); // 再向前移动三段（移动到此代码行行首）
				if (at == null) {
					GT_ASM.logger.warn("Reached `null` in `at (2)` too soon!  No changes made, bailing!");
					return GT_ASM.writeByteArraySelfReferenceFixup(classNode);
				}
				// 编辑插入的代码
				InsnList insert = new InsnList();
				insert.add(new VarInsnNode(Opcodes.ALOAD,  tEBSIdx));  // Load aextendedblockstorage
				insert.add(new VarInsnNode(Opcodes.ILOAD,  1)); 	   // Load p_149269_1_
				insert.add(new VarInsnNode(Opcodes.ILOAD,  2)); 	   // Load p_149269_2_
				insert.add(new VarInsnNode(Opcodes.ALOAD,  tByteIdx)); // Load abyte
				insert.add(new VarInsnNode(Opcodes.ILOAD,  tJIdx));    // Load j
				insert.add(M_setLOData.staticInvocation(isObfuscated()));
				insert.add(new VarInsnNode(Opcodes.ISTORE, tJIdx));    // Store j
				m.instructions.insertBefore(at, insert);
			}
			return GT_ASM.writeByteArraySelfReferenceFixup(classNode);
		}
		// 修改重新计算光照时的数据包 S26PacketMapChunkBulk
		if (transformedName.equals(C_PacketS26.clazzPath)) {
			ClassNode classNode = GT_ASM.makeNodes(basicClass);
//			GT_ASM.writePrettyPrintedOpCodesToFile(classNode, "C_PacketS26");
			// 替换 readPacketData 内的常量
			for (MethodNode m: classNode.methods) if (M_readPacketData.matches(m)) {
				int tOldValue = 8192, tNewValue = tOldValue+2048; // 由于 mc 并没有提供更多的数据来确定数据是否存在，方便起见，无论如何都发送 GT 的不透光度数据
				// 对于 NotEnoughIDs 加载的情况特殊讨论
				if (MD.NEID.mLoaded) {
					int tOldValue2 = 12288; tNewValue = tOldValue2+2048;
					GT_ASM.logger.info("Transforming first value "+tOldValue+" to "+tNewValue+" in " + C_PacketS26 + "." + M_readPacketData);
					if (!GT_ASM_UT.transformInlinedNumberMethod(m, tOldValue2, tNewValue) && !GT_ASM_UT.transformInlinedNumberMethod(m, tOldValue, tNewValue))
						GT_ASM.logger.warn("Cant find value "+tOldValue+", No changes made, bailing!");
				} else {
					GT_ASM.logger.info("Transforming first value "+tOldValue+" to "+tNewValue+" in " + C_PacketS26 + "." + M_readPacketData);
					if (!GT_ASM_UT.transformInlinedNumberMethod(m, tOldValue, tNewValue))
						GT_ASM.logger.warn("Cant find value "+tOldValue+", No changes made, bailing!");
				}
			}
			return GT_ASM.writeByteArraySelfReferenceFixup(classNode);
		}
		// 修改对应的接收数据端
		if (transformedName.equals(C_Chunk.clazzPath)) {
			ClassNode classNode = GT_ASM.makeNodes(basicClass);
//			GT_ASM.writePrettyPrintedOpCodesToFile(classNode, "C_Chunk");
			// 修改 fillChunk 方法来接收额外的不透光度数据
			for (MethodNode m: classNode.methods) if (M_fillChunk.matches(m)) {
				GT_ASM.logger.info("Transforming " + C_Chunk + "." + M_fillChunk);
				AbstractInsnNode at = m.instructions.getLast(); // 改为 last 用于匹配最后一个
				GT_ASM.logger.info("Transforming " + C_Chunk + "." + M_fillChunk + ", " + "add to k");
				while (at != null) {
					// 匹配最后的调用 blockBiomeArray 的操作
					if (at.getOpcode() == Opcodes.GETFIELD) {
						assert at instanceof FieldInsnNode;
						FieldInsnNode tNode = (FieldInsnNode)at;
						if (F_blockBiomeArray.matches(tNode)) break;
					}
					at = at.getPrevious();
				}
				if (at == null) {
					GT_ASM.logger.warn("Reached `null` in `at (0)` too soon!  No changes made, bailing!");
					return GT_ASM.writeByteArray(classNode);
				}
				int tKIdx = ((VarInsnNode)at.getPrevious().getPrevious()).var;   // get var of k
				// 将最后的 ISTORE 10 操作，修改为 ISTORE 6
				m.instructions.set(at.getNext().getNext().getNext(), new VarInsnNode(Opcodes.ISTORE, tKIdx)); // 修改为向 k 赋值

				GT_ASM.logger.info("Transforming " + C_Chunk + "." + M_fillChunk + ", " + "get data");
				while (at != null) {
					// 继续直到匹配到 ICONST_0，在循环前添加下代码
					if (at.getOpcode() == Opcodes.ICONST_0) break;
					at = at.getNext();
				}
				if (at == null) {
					GT_ASM.logger.warn("Reached `null` in `at (1)` too soon!  No changes made, bailing!");
					return GT_ASM.writeByteArray(classNode);
				}
				// 编辑插入的代码
				InsnList insert = new InsnList();
				insert.add(new VarInsnNode(Opcodes.ALOAD,  0));     // load this
				insert.add(F_storageArrays.virtualGet(isObfuscated()));         // load storageArrays
				insert.add(new VarInsnNode(Opcodes.ILOAD,  2));     // Load p_76607_2_
				insert.add(new VarInsnNode(Opcodes.ALOAD,  1));     // Load p_76607_1_
				insert.add(new VarInsnNode(Opcodes.ILOAD,  tKIdx)); // Load k
				insert.add(M_getLOData.staticInvocation(isObfuscated()));
				insert.add(new VarInsnNode(Opcodes.ISTORE, tKIdx)); // Store k
				m.instructions.insertBefore(at, insert);
			}
			return GT_ASM.writeByteArray(classNode);
		}

		return basicClass;
	}
}
