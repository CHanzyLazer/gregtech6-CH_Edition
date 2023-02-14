package gregtech.asm.transformers;

import gregtech.asm.GT_ASM;
import gregtech.asm.GT_ASM_UT;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.tree.*;

import static gregtech.asm.GT_ASM_UT.Name.*;

/**
 * @author CHanzy
 * 修复 NEIEasySearch 内部的 hashmap 线程不安全导致的概率卡死的问题
 */
public class NEIEasySearch_SynchronizeFix_CH implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        // 替换 C_NEIEasySearch 中 itemSearchNames 的类型为 SynchronizedHashMap
        if (transformedName.equals(C_NEIEasySearch.clazzPath)) {
            ClassNode classNode = GT_ASM.makeNodes(basicClass);
//            GT_ASM.writePrettyPrintedOpCodesToFile(classNode, "C_NEIEasySearch");
            
            GT_ASM.logger.info("Transforming " + C_NEIEasySearch);
            // 先替换成员变量的类型
            for (FieldNode f: classNode.fields) if (F_NEIES_itemSearchNames.matches(f)) f.desc = C_SyHashMap.toDesc();
            // 再替换所有方法中的调用 itemSearchNames（因为遍历了所有方法，有些方法当然可能不涉及这个成员变量，因此替换失败不添加 log）
            for (MethodNode m: classNode.methods) GT_ASM_UT.transformInlinedFieldClazzMethod(m, F_NEIES_itemSearchNames, C_SyHashMap);
            
//            GT_ASM.writePrettyPrintedOpCodesToFile(classNode, "C_NEIEasySearch_Modified"); // DEBUG
            return GT_ASM.writeByteArraySelfReferenceFixup(classNode, 0);
        }
        
        // 替换 C_NEIES_PinyinSearchProvider 中 itemSearchNames 的类型为 SynchronizedHashMap
        if (transformedName.equals(C_NEIES_PinyinSearchProvider.clazzPath)) {
            ClassNode classNode = GT_ASM.makeNodes(basicClass);
//            GT_ASM.writePrettyPrintedOpCodesToFile(classNode, "C_NEIES_PinyinSearchProvider");
            
            GT_ASM.logger.info("Transforming " + C_NEIES_PinyinSearchProvider);
            // 先替换成员变量的类型
            for (FieldNode f: classNode.fields) if (F_NEIES_itemSearchNames_1.matches(f)) f.desc = C_SyHashMap.toDesc();
            // 再替换所有方法中的调用 itemSearchNames（因为遍历了所有方法，有些方法当然可能不涉及这个成员变量，因此替换失败不添加 log）
            for (MethodNode m: classNode.methods) GT_ASM_UT.transformInlinedFieldClazzMethod(m, F_NEIES_itemSearchNames_1, C_SyHashMap);
            
//            GT_ASM.writePrettyPrintedOpCodesToFile(classNode, "C_NEIES_PinyinSearchProvider_Modified"); // DEBUG
            return GT_ASM.writeByteArraySelfReferenceFixup(classNode, 0);
        }
        
        // 替换 C_NEIES_PinyinItemFilter 中 itemSearchNames 的类型为 SynchronizedHashMap
        if (transformedName.equals(C_NEIES_PinyinItemFilter.clazzPath)) {
            ClassNode classNode = GT_ASM.makeNodes(basicClass);
//            GT_ASM.writePrettyPrintedOpCodesToFile(classNode, "C_NEIES_PinyinItemFilter");
            
            GT_ASM.logger.info("Transforming " + C_NEIES_PinyinItemFilter);
            // 先替换成员变量的类型
            for (FieldNode f: classNode.fields) if (F_NEIES_itemSearchNames_1.matches(f)) f.desc = C_SyHashMap.toDesc();
            // 再替换所有方法中的调用 itemSearchNames（因为遍历了所有方法，有些方法当然可能不涉及这个成员变量，因此替换失败不添加 log）
            for (MethodNode m: classNode.methods) GT_ASM_UT.transformInlinedFieldClazzMethod(m, F_NEIES_itemSearchNames_1, C_SyHashMap);
            
//            GT_ASM.writePrettyPrintedOpCodesToFile(classNode, "C_NEIES_PinyinItemFilter_Modified"); // DEBUG
            return GT_ASM.writeByteArraySelfReferenceFixup(classNode, 0);
        }
        
        // 替换 C_NEIES_EventItemSearchTooltip 中 itemSearchNames 的类型为 SynchronizedWeakHashMap
        if (transformedName.equals(C_NEIES_EventItemSearchTooltip.clazzPath)) {
            ClassNode classNode = GT_ASM.makeNodes(basicClass);
//            GT_ASM.writePrettyPrintedOpCodesToFile(classNode, "C_NEIES_EventItemSearchTooltip");
            
            GT_ASM.logger.info("Transforming " + C_NEIES_EventItemSearchTooltip);
            // 先替换成员变量的类型
            for (FieldNode f: classNode.fields) if (F_NEIES_itemSearchNames_2.matches(f)) f.desc = C_SyWeakHashMap.toDesc();
            // 再替换所有方法中的调用 itemSearchNames（因为遍历了所有方法，有些方法当然可能不涉及这个成员变量，因此替换失败不添加 log）
            for (MethodNode m: classNode.methods) GT_ASM_UT.transformInlinedFieldClazzMethod(m, F_NEIES_itemSearchNames_2, C_SyWeakHashMap);
            
//            GT_ASM.writePrettyPrintedOpCodesToFile(classNode, "C_NEIES_EventItemSearchTooltip_Modified"); // DEBUG
            return GT_ASM.writeByteArraySelfReferenceFixup(classNode, 0);
        }
        
        return basicClass;
    }
}
