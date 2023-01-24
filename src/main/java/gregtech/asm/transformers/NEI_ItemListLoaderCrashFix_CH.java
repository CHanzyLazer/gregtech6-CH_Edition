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
 * 修复进入世界时有概率 nei 物品列表加载失败导致游戏崩溃的问题
 */
public class NEI_ItemListLoaderCrashFix_CH implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        // 为了避免匿名类在不同打包下顺序不同的问题，这里匹配所有的匿名类
        for (int i = 0; i < 10; ++i) if (transformedName.equals(C_NEI_ItemList.clazzPath + "$"+i)) {
            ClassNode classNode = GT_ASM.makeNodes(basicClass);
//			GT_ASM.writePrettyPrintedOpCodesToFile(classNode, "C_NEI_ItemList$"+i);
            // 需要包含 damageSearch 方法才是目标的匿名类
            boolean tMatched = false;
            for (MethodNode m: classNode.methods) if (M_NEI_damageSearch.matches(m)) {
                tMatched = true;
                break;
            }
            if (!tMatched) break; // 不匹配直接跳过此类
            
            // 增加部分物品禁用 damageSearch，这可能会造成崩溃
            for (MethodNode m: classNode.methods) if (M_NEI_damageSearch.matches(m)) {
                GT_ASM.logger.info("Transforming " + C_NEI_ItemList+"$"+i + "." + M_NEI_damageSearch);
                AbstractInsnNode at = m.instructions.getFirst();
                // 跳转到第一个 NEW java/util/HashSet 行（第一行）
                while (at != null) {
                    if (at.getOpcode() == Opcodes.NEW) {
                        assert at instanceof TypeInsnNode;
                        TypeInsnNode tNode = (TypeInsnNode)at;
                        if (tNode.desc.equals(C_HashSet.deobf)) break;
                    }
                    at = at.getNext();
                }
                if (at == null) {
                    GT_ASM.logger.warn("Reached `null` in `at` too soon!  No changes made, bailing!");
                    return GT_ASM.writeByteArraySelfReferenceFixup(classNode);
                }
                // 在 at 前插入额外的判断
                InsnList insert = new InsnList();
                insert.add(new VarInsnNode(Opcodes.ALOAD, 1));          // Load item
                insert.add(M_disableNEIDamageSearch.staticInvocation(isObfuscated()));
                LabelNode after = new LabelNode();
                insert.add(new JumpInsnNode(Opcodes.IFEQ, after));     // 如果满足（为一）阻止连接的条件则继续后续，直接 return 跳过此 item，因此不满足（为零）则需要跳过这段
                insert.add(new InsnNode(Opcodes.RETURN));
                insert.add(after);                                                  // 不满足则跳转到这里
                m.instructions.insertBefore(at, insert);
            }
            
            // 将 execute 中的 timer 改为自用的方法，仅打印超时的结果
            for (MethodNode m: classNode.methods) if (M_NEI_execute.matches(m)) {
                GT_ASM.logger.info("Transforming " + C_NEI_ItemList+"$"+i + "." + M_NEI_execute);
                AbstractInsnNode at = m.instructions.getFirst();
                // 匹配创建 timer 的方法，将临时变量修改为自用的方法
                while (at != null) {
                    if (at.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                        assert at instanceof MethodInsnNode;
                        MethodInsnNode tNode = (MethodInsnNode)at;
                        if (tNode.name.equals(M_NEI_getTimer.deobf) && tNode.desc.equals(M_NEI_getTimer.desc)) break; // 由于是匿名类重写了方法，因此不能使用内置的方法去匹配 owner
                    }
                    at = at.getNext();
                }
                if (at == null) {
                    GT_ASM.logger.warn("Reached `null` in `at (0)` too soon!  No changes made, bailing!");
                    return GT_ASM.writeByteArraySelfReferenceFixup(classNode);
                }
                at = at.getNext(); // 到达 ASTORE 1
                assert at.getOpcode() == Opcodes.ASTORE && at instanceof VarInsnNode;
                int timerIdx = ((VarInsnNode)at).var;
                // 清空整行赋值代码
                at = removeLine(m.instructions, at);
                if (at == null) {
                    GT_ASM.logger.warn("Reached `null` in `at (1)` too soon!  No changes made, bailing!");
                    return GT_ASM.writeByteArraySelfReferenceFixup(classNode);
                }
                // 改变临时变量类型
                for (int j = 0; j < m.localVariables.size(); ++j) {
                    LocalVariableNode tLocalVariable = m.localVariables.get(j);
                    if (tLocalVariable.index != timerIdx) continue;
                    m.localVariables.set(j, new LocalVariableNode(tLocalVariable.name, C_TimerNEI.toDesc(), null, tLocalVariable.start, tLocalVariable.end, timerIdx));
                }
                
                // 修改为创建我的 timer
                InsnList insert = new InsnList();
                insert.add(new TypeInsnNode(Opcodes.NEW, C_TimerNEI.deobf));   // 创建 timer
                insert.add(new InsnNode(Opcodes.DUP));
                insert.add(M_TimerNEI_init.specialInvocation(isObfuscated()));             // 调用构造函数
                insert.add(new VarInsnNode(Opcodes.ASTORE, timerIdx));      // 存储到临时变量
                m.instructions.insert(at, insert);
                
                
                // 匹配后续的调用，并进行替换
                // 匹配 setLimit 行，并删除此调用
                while (at != null) {
                    if (at.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                        assert at instanceof MethodInsnNode;
                        MethodInsnNode tNode = (MethodInsnNode)at;
                        if (M_NEI_setLimit.matches(tNode)) break;
                    }
                    at = at.getNext();
                }
                if (at == null) {
                    GT_ASM.logger.warn("Reached `null` in `at (2)` too soon!  No changes made, bailing!");
                    return GT_ASM.writeByteArraySelfReferenceFixup(classNode);
                }
                at = removeLine(m.instructions, at);
                // 将这行完全删干净
                AbstractInsnNode
                tAt = at.getPrevious();
                m.instructions.remove(at);
                at = tAt;
                tAt = at.getPrevious();
                m.instructions.remove(at);
                at = tAt;
                
                // 匹配 FRAME FULL 中的 codechicken/nei/ThreadOperationTimer 替换为我的类
                while (at != null) {
                    if (at instanceof FrameNode) {
                        FrameNode tNode = (FrameNode)at;
                        if (tNode.type==Opcodes.F_FULL && tNode.local.size()==6 && tNode.local.get(1).equals(C_NEI_ThreadOperationTimer.deobf)) break;
                    }
                    at = at.getNext();
                }
                if (at == null) {
                    GT_ASM.logger.warn("Reached `null` in `at (7)` too soon!  No changes made, bailing!");
                    return GT_ASM.writeByteArraySelfReferenceFixup(classNode);
                }
                FrameNode tFrameNode = (FrameNode)at;
                Object[] tLocalList = tFrameNode.local.toArray();
                tLocalList[1] = C_TimerNEI.deobf;
                tAt = at.getNext();
                m.instructions.set(at, new FrameNode(Opcodes.F_FULL, tFrameNode.local.size(), tLocalList, tFrameNode.stack.size(), tFrameNode.stack.toArray()));
                at = tAt;
                
                // 匹配带参数的 reset 方法，替换为新的 reset 方法；注意此行带有其他 label，不能直接删除整行
                while (at != null) {
                    if (at.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                        assert at instanceof MethodInsnNode;
                        MethodInsnNode tNode = (MethodInsnNode)at;
                        if (M_NEI_reset_object.matches(tNode)) break;
                    }
                    at = at.getNext();
                }
                if (at == null) {
                    GT_ASM.logger.warn("Reached `null` in `at (3)` too soon!  No changes made, bailing!");
                    return GT_ASM.writeByteArraySelfReferenceFixup(classNode);
                }
                // 删除调用 node
                tAt = at.getPrevious();
                m.instructions.remove(at);
                at = tAt;
                if (at == null) {
                    GT_ASM.logger.warn("Reached `null` in `at (4)` too soon!  No changes made, bailing!");
                    return GT_ASM.writeByteArraySelfReferenceFixup(classNode);
                }
                // 添加自己的调用
                m.instructions.insert(at, M_TimerNEI_reset.virtualInvocation(isObfuscated())); // 调用 reset
                
                // 匹配不带参数的 reset 方法，替换为 check 方法
                while (at != null) {
                    if (at.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                        assert at instanceof MethodInsnNode;
                        MethodInsnNode tNode = (MethodInsnNode)at;
                        if (M_NEI_reset.matches(tNode)) break;
                    }
                    at = at.getNext();
                }
                if (at == null) {
                    GT_ASM.logger.warn("Reached `null` in `at (5)` too soon!  No changes made, bailing!");
                    return GT_ASM.writeByteArraySelfReferenceFixup(classNode);
                }
                // 删除调用 node
                tAt = at.getPrevious();
                m.instructions.remove(at);
                at = tAt;
                if (at == null) {
                    GT_ASM.logger.warn("Reached `null` in `at (6)` too soon!  No changes made, bailing!");
                    return GT_ASM.writeByteArraySelfReferenceFixup(classNode);
                }
                // 添加自己的调用
                m.instructions.insert(at, M_TimerNEI_check.virtualInvocation(isObfuscated()));
                
                // 匹配所有的 RETURN，在函数结束前手动关闭 timer
                at = m.instructions.getFirst();
                while (at != null) {
                    if (at.getOpcode() == Opcodes.RETURN) {
                        insert.clear();
                        insert.add(new VarInsnNode(Opcodes.ALOAD, timerIdx)); // load timer
                        insert.add(M_TimerNEI_close.virtualInvocation(isObfuscated()));      // close method
                        m.instructions.insertBefore(at, insert);
                    }
                    at = at.getNext();
                }
            }
//            GT_ASM.writePrettyPrintedOpCodesToFile(classNode, "C_NEI_ItemList$"+i+"_Modified"); // DEBUG
            
            return GT_ASM.writeByteArraySelfReferenceFixup(classNode);
        }
        return basicClass;
    }
}
