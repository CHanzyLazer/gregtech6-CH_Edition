package gregtech.asm;

import net.minecraft.client.Minecraft;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;


// 这里提供一些通用接口，部分借鉴 NotEnoughIDs 的写法
public class GT_ASM_UT {

    // 检测当前的的 mc 是否是混淆的，仅在直接获取失效时使用
    public static Boolean OBFUSCATED = null;
    public static boolean isObfuscated() {
        if (OBFUSCATED == null) {
            try {
                Minecraft.class.getField("theWorld");
                OBFUSCATED = false;
            } catch (NoSuchFieldException e) {
                OBFUSCATED = true;
            }
        }
        return OBFUSCATED;
    }

    // 修改 method 行内硬编码数值的方法
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean transformInlinedNumberMethod(MethodNode aMethod, int aOldValue, int aNewValue) {
        AbstractInsnNode at = aMethod.instructions.getFirst();
        boolean aFound = false;
        // 遍历寻找旧数值
        while (at!=null) {
            if (at.getOpcode() == Opcodes.ICONST_0 && aOldValue == 0) {aFound = true; break;}
            if (at.getOpcode() == Opcodes.ICONST_1 && aOldValue == 1) {aFound = true; break;}
            if (at.getOpcode() == Opcodes.ICONST_2 && aOldValue == 2) {aFound = true; break;}
            if (at.getOpcode() == Opcodes.ICONST_3 && aOldValue == 3) {aFound = true; break;}
            if (at.getOpcode() == Opcodes.ICONST_4 && aOldValue == 4) {aFound = true; break;}
            if (at.getOpcode() == Opcodes.ICONST_5 && aOldValue == 5) {aFound = true; break;}
            if (at.getOpcode() == Opcodes.LDC) {
                LdcInsnNode tNode = (LdcInsnNode)at; if (tNode.cst instanceof Integer && (Integer)tNode.cst == aOldValue) {aFound = true; break;}}
            if (at.getOpcode() == Opcodes.BIPUSH || at.getOpcode() == Opcodes.SIPUSH) {
                IntInsnNode tNode = (IntInsnNode)at; if (tNode.operand == aOldValue) {aFound = true; break;}}
            at = at.getNext();
        }
        // 找到数值后替换
        if (aFound) {
            if      (aNewValue == 0) aMethod.instructions.set(at, new InsnNode(Opcodes.ICONST_0));
            else if (aNewValue == 1) aMethod.instructions.set(at, new InsnNode(Opcodes.ICONST_1));
            else if (aNewValue == 2) aMethod.instructions.set(at, new InsnNode(Opcodes.ICONST_2));
            else if (aNewValue == 3) aMethod.instructions.set(at, new InsnNode(Opcodes.ICONST_3));
            else if (aNewValue == 4) aMethod.instructions.set(at, new InsnNode(Opcodes.ICONST_4));
            else if (aNewValue == 5) aMethod.instructions.set(at, new InsnNode(Opcodes.ICONST_5));
            else if (aNewValue >= Byte.MIN_VALUE && aNewValue <= Byte.MAX_VALUE) aMethod.instructions.set(at, new IntInsnNode(Opcodes.BIPUSH, aNewValue));
            else if (aNewValue >= Short.MIN_VALUE && aNewValue <= Short.MAX_VALUE) aMethod.instructions.set(at, new IntInsnNode(Opcodes.SIPUSH, aNewValue));
            else aMethod.instructions.set(at, new LdcInsnNode(aNewValue));
        }
        return aFound;
    }

    // 修改 method 行内硬编码 bool 值的方法
    public static boolean transformInlinedBoolMethod(MethodNode aMethod, boolean aOldValue, boolean aNewValue) {
        AbstractInsnNode at = aMethod.instructions.getFirst();
        boolean aFound = false;
        // 遍历寻找旧数值
        while (at!=null) {
            if (at.getOpcode() == Opcodes.ICONST_0 && !aOldValue) {aFound = true; break;}
            if (at.getOpcode() == Opcodes.ICONST_1 && aOldValue)  {aFound = true; break;}
            at = at.getNext();
        }
        // 找到数值后替换
        if (aFound) {
            aMethod.instructions.set(at, new InsnNode(aNewValue?Opcodes.ICONST_1:Opcodes.ICONST_0));
        }
        return aFound;
    }

    // copy from NotEnoughIDs
    @SuppressWarnings("ConstantConditions")
    public enum Name {
        // C_: Class, M_: Method, F_: Field

        C_ACL("net/minecraft/world/chunk/storage/AnvilChunkLoader", "aqk"),
        C_Block("net/minecraft/block/Block", "aji"),
        C_Chunk("net/minecraft/world/chunk/Chunk", "apx"),
        C_EBS("net/minecraft/world/chunk/storage/ExtendedBlockStorage", "apz"),
        C_IChunkProvider("net/minecraft/world/chunk/IChunkProvider", "apu"),
        C_NBT("net/minecraft/nbt/NBTTagCompound", "dh"),
        C_NetClient("net/minecraft/client/network/NetHandlerPlayClient", "bjb"),
        C_NA("net/minecraft/world/chunk/NibbleArray", "apv"),
        C_Packet("net/minecraft/network/Packet", "ft"),
        C_PacketBuffer("net/minecraft/network/PacketBuffer", "et"),
        C_PacketS26("net/minecraft/network/play/server/S26PacketMapChunkBulk", "gz"),
        C_PacketS21("net/minecraft/network/play/server/S21PacketChunkData", "gx"),
        C_PackedS21Ex("net/minecraft/network/play/server/S21PacketChunkData$Extracted", "gy"),
        C_World("net/minecraft/world/World", "ahb"),
        C_DataWatcher("net/minecraft/entity/DataWatcher", "te"),
        C_DataWatcherWO("net/minecraft/entity/DataWatcher$WatchableObject", "tf"),
        C_RenderGlobal("net/minecraft/client/renderer/RenderGlobal", "bma"),
        C_PlayerControllerMP("net/minecraft/client/multiplayer/PlayerControllerMP", "bje"),
        C_ItemInWorldManager("net/minecraft/server/management/ItemInWorldManager", "mx"),
        C_Player("net/minecraft/entity/player/EntityPlayer", "yz"),
        C_Entity("net/minecraft/entity/Entity", "sa"),

        M_onEntityUpdate(C_Entity, "onEntityUpdate", "C", "func_70030_z", "()V"),
        F_worldObj(C_Entity, "worldObj", "o", "field_70170_p", toDesc(C_World)),

        M_getBlock(C_World, "getBlock", "a", "func_147439_a", "(III)"+toDesc(C_Block)),

        M_EBS_getBlock(C_EBS, "getBlockByExtId", "a", "func_150819_a", "(III)"+toDesc(C_Block)),
        M_EBS_setBlock(C_EBS, "func_150818_a", "a", null, "(III"+toDesc(C_Block)+")V"),
        M_getBlockLSBArray(C_EBS, "getBlockLSBArray", "g", "func_76658_g", "()[B"),
        M_getBlockMSBArray(C_EBS, "getBlockMSBArray", "i", "func_76660_i", "()"+toDesc(C_NA)),
        M_setBlockMSBArray(C_EBS, "setBlockMSBArray", "a", "func_76673_a", "("+toDesc(C_NA)+")V"),
        M_isEmpty(C_EBS, "isEmpty", "a", "func_76663_a", "()Z"),
        M_removeInvalidBlocks(C_EBS, "removeInvalidBlocks", "e", "func_76672_e", "()V"),
        F_blockRefCount(C_EBS, "blockRefCount", "b", "field_76682_b", "I"),
        F_tickRefCount(C_EBS, "tickRefCount", "c", "field_76683_c", "I"),

        M_writeChunkToNBT(C_ACL, "writeChunkToNBT", "a", "func_75820_a", "("+toDesc(C_Chunk, C_World, C_NBT)+")V"),
        M_readChunkFromNBT(C_ACL, "readChunkFromNBT", "a", "func_75823_a", "("+toDesc(C_World, C_NBT)+")"+toDesc(C_Chunk)),

        M_fillChunk(C_Chunk, "fillChunk", "a", "func_76607_a", "([BIIZ)V"),
        M_getBlockStorageArray(C_Chunk, "getBlockStorageArray", "i", "func_76587_i", "()["+toDesc(C_EBS)),
        F_storageArrays(C_Chunk, "storageArrays", "u", "field_76652_q", "["+toDesc(C_EBS)),
        F_blockBiomeArray(C_Chunk, "blockBiomeArray", "v", "field_76651_r", "[B"),

        M_readPacketData(C_Packet, "readPacketData", "a", "func_148837_a", "("+toDesc(C_PacketBuffer)+")V"),
        M_writePacketData(C_Packet, "writePacketData", "b", "func_148840_b", "("+toDesc(C_PacketBuffer)+")V"),
        M_func_149275_c(C_PacketS21, "func_149275_c", "c", null, "()I"),
        M_func_149269_a(C_PacketS21, "func_149269_a", "a", null, "("+toDesc(C_Chunk)+"ZI)"+toDesc(C_PackedS21Ex)),
        F_field_150282_a(C_PackedS21Ex, "field_150282_a", "a", null, "[B"),
        // my hooks
        C_GTASM_LO("gregtech/interfaces/asm/LO_CH"),
        C_GTASM_R("gregtech/asm/transformers/minecraft/Replacements_CH"),
        M_enabledLO(C_GTASM_LO, "isEnableAsmBlockGtLightOpacity", null, null, "()Z"),
        M_getLO(C_GTASM_LO, "getLightOpacityNA", null, null, "("+toDesc(C_EBS)+")"+toDesc(C_NA)),
        M_initLO(C_GTASM_LO, "initLightOpacityNA", null, null, "("+toDesc(C_EBS, C_NA)+")V"),
        M_clearLO(C_GTASM_LO, "clearLightOpacityNA", null, null, "("+toDesc(C_EBS)+")V"),
        M_GTWriteNBT(C_GTASM_R, "writeChunkToNbt", null, null, "("+toDesc(C_EBS, C_NBT)+")V"),
        M_GTReadNBT(C_GTASM_R, "readChunkFromNbt", null, null, "("+toDesc(C_EBS, C_NBT)+")V"),
        M_setLOData(C_GTASM_R, "setBlockGTLightOpacityData", null, null, "(["+toDesc(C_EBS)+"ZI[BI)I"),
        M_getLOData(C_GTASM_R, "getBlockGTLightOpacityData", null, null, "(["+toDesc(C_EBS)+"I[BI)I"),
        M_spawnSprintingParticle(C_GTASM_R, "spawnSprintingParticle", null, null, "("+toDesc(C_Entity, C_World, C_Block)+"III)V"),
        F_GTLO(C_EBS, "blockGTLightOpacityArray", null, null, toDesc(C_NA));

        public final Name clazz; // 所属类
        public final String clazzPath; // 类的路径（以"."分割）
        public final String deobf; // 反混淆名称
        public final String obf; // 混淆名称
        public final String srg; // srg 混淆名称
        public final String desc; // 类型
        public String obfDesc; // 混淆类型

        Name(String deobf) {
            this(deobf, deobf);
        }

        Name(String deobf, String obf) {
            this.clazz = null;
            this.deobf = deobf;
            this.clazzPath = deobf.replace('/', '.');
            this.obf = obf;
            this.srg = deobf;
            this.desc = null;
        }

        Name(Name clazz, String deobf, String obf, String srg, String desc) {
            this.clazz = clazz;
            this.deobf = deobf;
            this.clazzPath = deobf.replace('/', '.');
            this.obf = obf != null ? obf : deobf;
            this.srg = srg != null ? srg : deobf;
            this.desc = desc;
        }
        // 类型转 desc
        public static String toDesc(Name... clazzs) {
            StringBuilder rOut = new StringBuilder();
            for (Name clazz : clazzs) rOut.append("L").append(clazz.deobf).append(";");
            return rOut.toString();
        }
        public String toDesc() {return "L"+this.deobf+";";}
        // 用于输出
        @Override public String toString() {return this.clazzPath;}

        public boolean matches(MethodNode m) {assert  this.desc.startsWith("("); return (this.obf.equals(m.name) && this.obfDesc.equals(m.desc)) || (this.srg.equals(m.name) && this.desc.equals(m.desc)) || (this.deobf.equals(m.name) && this.desc.equals(m.desc));}
        public boolean matches(FieldNode  f) {assert !this.desc.startsWith("("); return (this.obf.equals(f.name) && this.obfDesc.equals(f.desc)) || (this.srg.equals(f.name) && this.desc.equals(f.desc)) || (this.deobf.equals(f.name) && this.desc.equals(f.desc));}

        public boolean matches(MethodInsnNode m) {assert  this.desc.startsWith("("); return (this.clazz.obf.equals(m.owner) && this.obf.equals(m.name) && this.obfDesc.equals(m.desc)) || (this.clazz.srg.equals(m.owner) && this.srg.equals(m.name) && this.desc.equals(m.desc)) || (this.clazz.deobf.equals(m.owner) && this.deobf.equals(m.name) && this.desc.equals(m.desc));}
        public boolean matches(FieldInsnNode  f) {assert !this.desc.startsWith("("); return (this.clazz.obf.equals(f.owner) && this.obf.equals(f.name) && this.obfDesc.equals(f.desc)) || (this.clazz.srg.equals(f.owner) && this.srg.equals(f.name) && this.desc.equals(f.desc)) || (this.clazz.deobf.equals(f.owner) && this.deobf.equals(f.name) && this.desc.equals(f.desc));}

        // 由于 forge 的存在，注入只需要 srg 混淆即可（即注入的代码可以不进行混淆）
        public MethodInsnNode staticInvocation(boolean obfuscated) {assert  this.desc.startsWith("("); return obfuscated ? new MethodInsnNode(Opcodes.INVOKESTATIC, this.clazz.srg, this.srg, this.desc, false) : new MethodInsnNode(Opcodes.INVOKESTATIC, this.clazz.deobf, this.deobf, this.desc, false);}
        public FieldInsnNode  staticGet       (boolean obfuscated) {assert !this.desc.startsWith("("); return obfuscated ? new FieldInsnNode (Opcodes.GETSTATIC,    this.clazz.srg, this.srg, this.desc)           : new FieldInsnNode (Opcodes.GETSTATIC,    this.clazz.deobf, this.deobf, this.desc);}
        public FieldInsnNode  virtualGet      (boolean obfuscated) {assert !this.desc.startsWith("("); return obfuscated ? new FieldInsnNode (Opcodes.GETFIELD,     this.clazz.srg, this.srg, this.desc)           : new FieldInsnNode (Opcodes.GETFIELD,     this.clazz.deobf, this.deobf, this.desc);}
        public FieldInsnNode  staticSet       (boolean obfuscated) {assert !this.desc.startsWith("("); return obfuscated ? new FieldInsnNode (Opcodes.PUTSTATIC,    this.clazz.srg, this.srg, this.desc)           : new FieldInsnNode (Opcodes.PUTSTATIC,    this.clazz.deobf, this.deobf, this.desc);}
        public FieldInsnNode  virtualSet      (boolean obfuscated) {assert !this.desc.startsWith("("); return obfuscated ? new FieldInsnNode (Opcodes.PUTFIELD,     this.clazz.srg, this.srg, this.desc)           : new FieldInsnNode (Opcodes.PUTFIELD,     this.clazz.deobf, this.deobf, this.desc);}

        // 将反混淆的 desc 转换为混淆的
        private static void translateDescs() {
            StringBuilder sb = new StringBuilder();
            for (Name name : values()) {
                if (name.desc != null) {
                    int pos = 0;

                    int endPos;
                    for (endPos = -1; (pos = name.desc.indexOf(76, pos)) != -1; pos = endPos + 1) {
                        sb.append(name.desc, endPos + 1, pos);
                        endPos = name.desc.indexOf(59, pos + 1);
                        String cName = name.desc.substring(pos + 1, endPos);
                        for (Name name2 : values()) {
                            if (name2.deobf.equals(cName)) {
                                cName = name2.obf;
                                break;
                            }
                        }
                        sb.append('L').append(cName).append(';');
                    }

                    sb.append(name.desc, endPos + 1, name.desc.length());
                    name.obfDesc = sb.toString();
                    sb.setLength(0);
                }
            }
        }
        private static void printObfDesc() {
            for (Name name : values()) GT_ASM.logger.info(name.deobf + ": " + name.obfDesc);
        }
        static {translateDescs();}
    }
}
