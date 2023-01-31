package gregtechCH.tileentity.cores.electric;

import gregapi.data.TD;
import gregapi.tileentity.energy.ITileEntityEnergy;
import gregapi.util.UT;
import gregtechCH.code.Pair;
import gregtechCH.tileentity.IMTEServerTickParallel;
import gregtechCH.util.UT_CH;
import net.minecraft.tileentity.TileEntity;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static gregapi.data.CS.*;

/**
 * @author CHanzy
 * 电网的公用类，用来接受和输出能量
 * 根据输入来调整上限，并且存储输入用于最近搜索
 */
public class MTEC_ElectricWiresManager implements IMTEServerTickParallel {
    // par tick stuff
    @Override public void setError(String aError) {/**/}
    // 根据输入来判断是否被卸载
    @Override public synchronized boolean isDead() {
        if (needUpdate()) return T;
        if (mInputs.isEmpty()) return T;
        for (InputObject input : mInputs.values()) if (!input.mIOCore.isDead()) return F;
        return T;
    }
    @Override public void onUnregisterPar() {mHasToAddTimerPar = T;}
    boolean mHasToAddTimerPar = T;
    
    // 通用电力 buffer，标记电压电流和能量
    static class EnergyBuffer {
        public long mVoltage = 128, mAmperage = 1, mEnergy = 0; // 为了让代码简洁，保证实际 buffer 的电流都大于等于 1
        // 消耗电流量的能量
        public void emit(long aAmperage) {
            mEnergy -= mVoltage*aAmperage;
            UT_CH.Debug.assertWhenDebug(mEnergy >= 0);
        }
        public long stdEnergy() {return mVoltage*mAmperage;}
        public boolean full() {return mEnergy >= stdEnergy();}
        public long outputAmperage() {return Math.min(mEnergy/mVoltage, mAmperage);}
        public long neededAmperage() {long tEnergyNeed = stdEnergy()-mEnergy; return tEnergyNeed > 0 ? UT.Code.divup(tEnergyNeed, mVoltage) : 0;}
    }
    
    // 输入类和输出类以及通用部分（输入或输出的 core 和方向以及对应的 TE）
    static class IOObject {
        protected final MTEC_ElectricWireBase mIOCore;
        protected final byte mIOSide;
        protected final TileEntity mIOTE;
        
        private IOObject(MTEC_ElectricWireBase aIOCore, byte aIOSide, TileEntity aIOTE) {mIOCore = aIOCore; mIOSide = aIOSide; mIOTE = aIOTE;}
        public boolean valid(MTEC_ElectricWiresManager aManager) {return mIOCore.mManager == aManager && mIOCore.mTE.getAdjacentTileEntity(mIOSide).mTileEntity == mIOTE;}
        
        // 重写 equals 和 hashCode 来使其作为 key 时可以按照我希望的方式运行
        @Override public boolean equals(Object aRHS) {return aRHS instanceof IOObject ? equals((IOObject) aRHS) : F;}
        public boolean equals(IOObject aRHS) {return mIOCore==aRHS.mIOCore && mIOSide==aRHS.mIOSide;}
        @Override public int hashCode() {return Objects.hash(mIOCore, mIOSide);}
    }
    // 输入需要暂存电压电流用来统计此 tick 所有的输入，buffer 能量来比较简单实现能量守恒
    static class InputObject extends IOObject {
        protected final EnergyBuffer mEnergyBuffer = new EnergyBuffer();
        private InputObject(MTEC_ElectricWireBase aInputCore, byte aInputSide, TileEntity aInputTE) {super(aInputCore, aInputSide, aInputTE);}
        private InputObject(MTEC_ElectricWireBase aInputCore, byte aInputSide) {this(aInputCore, aInputSide, aInputCore.mTE.getAdjacentTileEntity(aInputSide).mTileEntity);}
        
        // 注入能量直到满, 返回成功输入的电流（零或输入电流），为了避免注入能量和调用输出的顺序问题，这里允许 buffer 达到两倍的容量
        public long doInput(long aVoltage, long aAmperage) {
            if (aVoltage == 0 || aAmperage == 0) return 0;
            mEnergyBuffer.mAmperage = aAmperage;
            mEnergyBuffer.mVoltage = aVoltage;
            long tInEnergy = aAmperage*aVoltage;
            if (mEnergyBuffer.mEnergy >= tInEnergy * 2) return 0;
            mEnergyBuffer.mEnergy += tInEnergy;
            return aAmperage;
        }
        // 是否可以输出
        public boolean active() {return mEnergyBuffer.full();}
        // 检测是否合理，并且处理不合理部分
        @Override public boolean valid(MTEC_ElectricWiresManager aManager) {
            if (!super.valid(aManager)) {mEnergyBuffer.mEnergy = 0; return F;}
            return T;
        }
    }
    // 输出需要 buffer 所有的能量，用来保证可以预知输出是否成功，由于有多输入以及电阻，输出会有多组电压电流和对应的 buffer
    static class OutputObject extends IOObject {
        // 不再限制电流，不容易实现并且会有更多的问题，导线熔断判断时注意添加持续时间考虑（或后期直接使用温度和散热）
        protected final Map<InputObject, Long> mInputAmperages = new LinkedHashMap<>(); // linked 用于加速遍历，用来暂存注入时需要的电流值
        
        protected final Map<InputObject, EnergyBuffer> mInputBuffers = new LinkedHashMap<>(); // linked 用于加速遍历
        protected final Map<InputObject, LinkedList<MTEC_ElectricWireBase>> mInputPaths = new LinkedHashMap<>(); // linked 用于加速遍历
        private OutputObject(MTEC_ElectricWireBase aOutputCore, byte aOutputSide, TileEntity aOutputTE) {super(aOutputCore, aOutputSide, aOutputTE);}
    
        // 仅用于网络非法后延迟网络更新时使用，检测对应输入的路径是否合法且带有缓存优化，因此结果也只是一次性的
        protected final Map<InputObject, Boolean> mPathValidBuffer = new LinkedHashMap<>(); // linked 用于加速遍历
        private boolean checkPathValid(InputObject aInputObject) {
            UT_CH.Debug.assertWhenDebug(!mIOCore.mManager.mValid);
            if (mPathValidBuffer.containsKey(aInputObject)) return mPathValidBuffer.get(aInputObject);
            LinkedList<MTEC_ElectricWireBase> tPath = mInputPaths.get(aInputObject);
            if (tPath == null || tPath.isEmpty()) {mPathValidBuffer.put(aInputObject, F); return F;}
            // 路径上的实体是否都存在的检测
            for (MTEC_ElectricWireBase tCore : tPath) if (tCore.isDead()) {mPathValidBuffer.put(aInputObject, F); return F;}
            // 路径连接是否正确的检测
            Iterator<MTEC_ElectricWireBase> it1 = tPath.iterator(), it2 = tPath.iterator();
            MTEC_ElectricWireBase tCore2 = it2.next();
            while (it2.hasNext()) {
                MTEC_ElectricWireBase
                tCore1 = it1.next();
                tCore2 = it2.next();
                if (!tCore1.isAdjacentCore(tCore2)) {mPathValidBuffer.put(aInputObject, F); return F;}
            }
            // 还需检测最后是否连接到了输出
            if (tCore2.getAdjacentTE(mIOSide) != mIOTE) {mPathValidBuffer.put(aInputObject, F); return F;}
            mPathValidBuffer.put(aInputObject, T); return T;
        }
        
        // 获取对应输入需要注入的电流量
        public long getNeededAmperage(InputObject aInputObject) {
            if (equals(aInputObject)) return 0; // 无论如何都不需要自己的输入
            EnergyBuffer tEnergyBuffer = mInputBuffers.get(aInputObject);
            if (tEnergyBuffer == null) {
                tEnergyBuffer = new EnergyBuffer();
                mInputBuffers.put(aInputObject, tEnergyBuffer);
                tEnergyBuffer.mAmperage = 1; // 仅保证和初值无关
            }
            return tEnergyBuffer.neededAmperage();
        }
        // 为路径注入电流，如果没有路径需要进行初始化，合法时一定会成功
        public void injectAmperageToPath(InputObject aInputObject, long aAmperage) {
            if (aAmperage == 0) return;
            LinkedList<MTEC_ElectricWireBase> tPath = mInputPaths.get(aInputObject);
            if (tPath == null) {
                tPath = new LinkedList<>();
                boolean tSuccess = getPath(aInputObject, this, tPath);
                if (!tSuccess) {mIOCore.mManager.setInvalid(); return;} // 总会有各种原因导致网络意外失效（例如过早的区块卸载，未触发近邻更新的近邻更新之类的），直接标记对应的 manager 非法然后退出
                mInputPaths.put(aInputObject, tPath);
            }
            // 检测路径是否完整合法，为了性能仅在设置网络非法时进行路径路径检测
            if (!mIOCore.mManager.mValid && !checkPathValid(aInputObject)) return;
            // 注入电流
            for (MTEC_ElectricWireBase tCore : tPath) tCore.appendAmperage(aInputObject, aAmperage);
            Long tAmperage = mInputAmperages.get(aInputObject);
            mInputAmperages.put(aInputObject, tAmperage == null ? aAmperage : tAmperage + aAmperage);
        }
        // 注入能量，一定成功（需要外部注入之前进行判断是否可以注入，而注入一定成功）
        public void injectEnergy(InputObject aInputObject, long aVoltage, long aAmperage) {
            UT_CH.Debug.assertWhenDebug(mInputAmperages.get(aInputObject) == aAmperage);
            if (equals(aInputObject)) return; // 无论如何都不需要自己的输入
            aInputObject.mEnergyBuffer.emit(aAmperage); // 无论电压是否为零，输入都需要损失电流
            if (aVoltage == 0 || aAmperage == 0) return;
            EnergyBuffer tEnergyBuffer = mInputBuffers.get(aInputObject);
            // 直接注入 buffer 即可
            tEnergyBuffer.mVoltage = aVoltage;
            tEnergyBuffer.mEnergy += aAmperage*aVoltage;
        }
        // 执行输出，需要遍历 mInputList 来输出，保证过高电压一定会导致爆炸之类的
        public void doOutput() {
            for (Map.Entry<InputObject, EnergyBuffer> tEntry : mInputBuffers.entrySet()) {
                long tOutputAmperage = tEntry.getValue().outputAmperage();
                if (tOutputAmperage > 0 ) {
                    long tInserted = ITileEntityEnergy.Util.insertEnergyInto(TD.Energy.EU, OPOS[mIOSide], tEntry.getValue().mVoltage, tOutputAmperage, mIOCore.mTE, mIOTE);
                    tEntry.getValue().emit(tInserted);
                    // 如果刚好消耗完了目前电流量，并且没有剩余电流，则增大电流
                    if (tInserted == tEntry.getValue().mAmperage && tEntry.getValue().mEnergy < tEntry.getValue().mVoltage) tEntry.getValue().mAmperage *= 2;
                    tEntry.getValue().mAmperage = Math.min(tEntry.getValue().mAmperage, tEntry.getKey().mEnergyBuffer.mAmperage);
                }
            }
            // 每次输出完成重置这些变量
            mInputAmperages.clear();
        }
        // 检测是否合理，并且处理不合理部分
        @Override public boolean valid(MTEC_ElectricWiresManager aManager) {
            if (!super.valid(aManager)) {mInputBuffers.clear(); mInputAmperages.clear(); mInputPaths.clear(); return F;}
            // 遍历清除非法元素，注意需要使用迭代器来清除
            Iterator<InputObject> keyIt = mInputBuffers.keySet().iterator();
            //noinspection Java8CollectionRemoveIf
            while (keyIt.hasNext()) {
                InputObject tKey = keyIt.next();
                if (!aManager.mInputs.containsKey(tKey) || !tKey.valid(aManager)) keyIt.remove();
            }
            // 清空缓存的一些变量
            mInputAmperages.clear();
            // 清空原本缓存的路径
            mInputPaths.clear();
            return T;
        }
    }
    
    // 获取最短路径 A* 算法，用于得到电流的流过路径，返回是否成功找到
    private static boolean getPath(InputObject aInputObject, OutputObject aOutputObject, LinkedList<MTEC_ElectricWireBase> rPath) {
        rPath.clear();
        TreeMap<Integer, LinkedList<Node>> openSetWithValue = new TreeMap<>();
        Set<Node> openSet = new HashSet<>();
        Set<Node> closeSet = new HashSet<>();
        // 添加起始节点
        {
        Node tInitNode = new Node(aInputObject.mIOCore, null);
        openSet.add(tInitNode);
        openSetWithValue.put(0, UT_CH.STL.newLinkedList(tInitNode));
        }
        // 最终节点，未找到则为 null
        Node endNode = null;
        // 开始算法
        while (true) {
            Map.Entry<Integer, LinkedList<Node>> tEntry = openSetWithValue.firstEntry();
            if (tEntry == null) break;
            // 从头挑选，并且顺便将其从 openset 放入 closeset
            Node tNextNode = tEntry.getValue().pollLast(); // 采用先进先出的原则，模拟深度优先搜索来加速退出
            if (tNextNode == null) {openSetWithValue.remove(tEntry.getKey()); continue;} // 如果对应的队列为空则清除此 key，重新寻找
            // 如果为终点，则退出循环，已经找到
            if (tNextNode.equalToCore(aOutputObject.mIOCore)) {endNode = tNextNode; break;}
            // 否则更新 openset 和 closeset
            openSet.remove(tNextNode);
            closeSet.add(tNextNode);
            // 获取近邻，加入到 openset 以便下一次循环
            for (byte tSide : ALL_SIDES) {
                Node tNode = tNextNode.getAdjacentNode(tSide);
                if (tNode != null) {
                    if (closeSet.contains(tNode) || openSet.contains(tNode)) continue;
                    openSet.add(tNode);
                    int tValue = tNode.mDistance + tNode.getDistanceToEnd(aOutputObject.mIOCore);
                    LinkedList<Node> tNodeList = openSetWithValue.get(tValue);
                    //noinspection Java8MapApi
                    if (tNodeList == null) {
                        tNodeList = new LinkedList<>();
                        openSetWithValue.put(tValue, tNodeList);
                    }
                    tNodeList.addLast(tNode); // 采用先进先出的原则，模拟深度优先搜索来加速退出
                }
            }
        }
        // 根据最终节点获取路径
        if (endNode == null) return F;
    
        rPath.addFirst(endNode.mCore);
        while (endNode.mParent != null) {
            endNode = endNode.mParent;
            rPath.addFirst(endNode.mCore); // 保证构造后顺序是从 input 到 output
        }
        return T;
    }
    // 用于实现 A* 算法的 Node，提供周围搜索，父节点记录，等
    private static class Node {
        public final MTEC_ElectricWireBase mCore;
        public final Node mParent; // null 表示起始节点
        public final int mDistance; // 表示距离起点的距离
        private Node(MTEC_ElectricWireBase aCore, Node aParent) {
            mCore = aCore; mParent = aParent;
            if (aParent == null) mDistance = 0;
            else mDistance = mParent.mDistance + 1;
        }
        // 获取近邻
        public Node getAdjacentNode(byte aSide) {
            MTEC_ElectricWireBase tCore = mCore.getAdjacentCore(aSide);
            if (tCore == null) return null;
            return new Node(tCore, this);
        }
        // 直接选用曼哈顿距离来估计距离终点的距离
        public int getDistanceToEnd(MTEC_ElectricWireBase aCore) {
            return Math.abs(mCore.mTE.xCoord - aCore.mTE.xCoord) + Math.abs(mCore.mTE.yCoord - aCore.mTE.yCoord) + Math.abs(mCore.mTE.zCoord - aCore.mTE.zCoord);
        }
        // 重写 equals 和 hashCode 来使其作为 key 时可以按照我希望的方式运行
        @Override public boolean equals(Object aRHS) {return aRHS instanceof Node ? equals((Node) aRHS) : F;}
        public boolean equals(Node aRHS) {return mCore==aRHS.mCore;}
        @Override public int hashCode() {return mCore.hashCode();}
        // 自用方法
        public boolean equalToCore(MTEC_ElectricWireBase aCore) {return mCore==aCore;}
    }
    
    private boolean mInited = F, mValid = T; // 初始一定合法，非法后一定需要创建新的 manager 来进行替代
    private int mValidCounter = 0; // 设置非法后用于计数延迟更新
    public synchronized void setInvalid() {
        if (mValid) {mValidCounter = 8; mValid = F;}
        // 无论如何都需要将 output 暂存的路径合法值清空
        for (OutputObject tOutput : mOutputs.values()) tOutput.mPathValidBuffer.clear();
    }
    public synchronized boolean needUpdate() {return !mValid && mValidCounter == 0;}
    public boolean valid() {return mInited && mValid;}
    protected final Map<IOObject, InputObject> mInputs = new LinkedHashMap<>(); // linked 用于加速遍历
    protected final Map<IOObject, OutputObject> mOutputs = new LinkedHashMap<>(); // linked 用于加速遍历
    
    // 合并 Manager
    public void mergeManager(@NotNull MTEC_ElectricWiresManager aManagerToMerge) {
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (aManagerToMerge) {
            if (!mInited && aManagerToMerge.mInited) {
                // 仅已存在的并且 te 相同的，能够继承旧的能量数据
                for (InputObject tInput : aManagerToMerge.mInputs.values()) {
                    IOObject existIO = mOutputs.get(tInput);
                    if (existIO != null && existIO.mIOTE == tInput.mIOTE) {
                        mInputs.put(tInput, tInput);
                    }
                }
                for (OutputObject tOutput : aManagerToMerge.mOutputs.values()) {
                    IOObject existIO = mOutputs.get(tOutput);
                    if (existIO != null && existIO.mIOTE == tOutput.mIOTE) {
                        mOutputs.put(tOutput, tOutput);
                    }
                }
            }
        }
    }
    // 更新 Manager，移除过时的输入输出
    public void update() {
        // 遍历清除非法元素，注意需要使用迭代器来清除
        Iterator<InputObject> inputIt = mInputs.values().iterator();
        //noinspection Java8CollectionRemoveIf
        while (inputIt.hasNext()) {
            InputObject tInput = inputIt.next();
            if (!tInput.valid(this)) inputIt.remove();
        }
        Iterator<OutputObject> outputIt = mOutputs.values().iterator();
        //noinspection Java8CollectionRemoveIf
        while (outputIt.hasNext()) {
            OutputObject tOutput = outputIt.next();
            if (!tOutput.valid(this)) outputIt.remove();
        }
        mCoresActive.clear();
        mInited = T;
    }
    
    /* main code */
    private final Set<MTEC_ElectricWireBase> mCoresActive = new LinkedHashSet<>(); // linked 用于加速遍历，临时变量暂存上一步的结果
    // ticking
    @Override public void onServerTickPar(boolean aFirst) {
        // 每 tick 进行 counter 计数
        if (mValidCounter > 0) --mValidCounter;
        // 非法 manager 依旧需要进行输出，避免延迟更新造成的闪断
        
        // 每 tick 分配输入进行输出
        // 电流电压分配前先清空路径的临时值（上一步没有激活的 core 也不需要清空路径的暂存值）
        for (MTEC_ElectricWireBase tCore : mCoresActive) {
            tCore.clearTemporary();
        }
        // 电流分配
        {
        Integer tRestIdx = null; // 用来保证多个输入会接着上一个输入进行分配
        List<Pair<OutputObject, Long>> tActiveOutputs = new LinkedList<>();
        for (InputObject tInput : mInputs.values()) if (tInput.active()) { // 输入不需要考虑 dead，因为 dead 的不会注入能量到电网
            // 对于每个输入，统计所有需要的输出，并且将其放入 list 中，并存储每个输出需要的电流数
            tActiveOutputs.clear();
            for (OutputObject tOutput : mOutputs.values()) if (!tOutput.mIOCore.isDead()) { // 因为区块卸载等原因 dead 的输出不会进行注入
                long tNeedA = tOutput.getNeededAmperage(tInput);
                if (tNeedA > 0) tActiveOutputs.add(new Pair<>(tOutput, tNeedA));
            }
            int tRestActiveNum = tActiveOutputs.size();
            long tTotalAmperage = tInput.mEnergyBuffer.mAmperage;
            // 根据输入获取每个输出分配的电流，直到没有需要分配的目标或者没有剩余的电流
            while (tRestActiveNum > 0 && tTotalAmperage >= tRestActiveNum) {
                long tAmperage = tTotalAmperage / tRestActiveNum;
                for (Pair<OutputObject, Long> tPair : tActiveOutputs) if (tPair.second > 0) {
                    tTotalAmperage -= assignAmperage(tInput, tPair, tAmperage);
                    if (tPair.second == 0) --tRestActiveNum;
                }
            }
            // 剩下不能均分的从上一个结束的位置开始遍历，如果没有则随机位置开始
            if (tRestActiveNum == 0 || tTotalAmperage == 0) continue;
            Iterator<Pair<OutputObject, Long>> outputIt = tActiveOutputs.iterator();
            if (tRestIdx == null) tRestIdx = RNGSUS.nextInt(tActiveOutputs.size());
            if (tRestIdx >= tActiveOutputs.size()) tRestIdx %= tActiveOutputs.size();
            for (int i = 0; i < tRestIdx; ++i) outputIt.next();
            while (tRestActiveNum > 0 && tTotalAmperage > 0) {
                if (!outputIt.hasNext()) outputIt = tActiveOutputs.iterator();
                Pair<OutputObject, Long> tPair = outputIt.next();
                if (tPair.second > 0) {
                    tTotalAmperage -= assignAmperage(tInput, tPair, 1);
                    if (tPair.second == 0) --tRestActiveNum;
                }
                ++tRestIdx;
                if (tRestIdx == tActiveOutputs.size()) tRestIdx = 0;
            }
        }
        }
        // 根据分配的电流计算路径的电压值，必须要求这个输出有电流（在工作）才会计算对应电压
        for (OutputObject tOutput : mOutputs.values()) for (Map.Entry<InputObject, LinkedList<MTEC_ElectricWireBase>> tEntry : tOutput.mInputPaths.entrySet()) if (tOutput.mInputAmperages.containsKey(tEntry.getKey()) && tOutput.mInputAmperages.get(tEntry.getKey()) > 0) {
            Pair<Long, Long> tLastVoltage = new Pair<>(tEntry.getKey().mEnergyBuffer.mVoltage, 0L);
            for (MTEC_ElectricWireBase tCore : tEntry.getValue()) {
                tLastVoltage = tCore.setAndGetVoltageFromSourceVoltage(tEntry.getKey(), tLastVoltage);
            }
        }
        // 加速原本的一些算法，统一构造所有需要更新的 core
        mCoresActive.clear();
        for (OutputObject tOutput : mOutputs.values()) for (Map.Entry<InputObject, LinkedList<MTEC_ElectricWireBase>> tEntry : tOutput.mInputPaths.entrySet()) if (tOutput.mInputAmperages.containsKey(tEntry.getKey()) && tOutput.mInputAmperages.get(tEntry.getKey()) > 0) {
            mCoresActive.addAll(tEntry.getValue());
        }
        // 熔毁检测放到这里，整个电网会随机熔毁掉一个然后其余的进行冷却
        {
        Set<MTEC_ElectricWireBase> tCoresToBurn = new HashSet<>(); // set 保证同类的只会保留一个
        for (MTEC_ElectricWireBase tCore : mCoresActive) {
            if (tCore.willBurn()) tCoresToBurn.add(tCore);
        }
        if (!tCoresToBurn.isEmpty()) {
            Iterator<MTEC_ElectricWireBase> coreIt = tCoresToBurn.iterator();
            int tIdx = 0;
            int tBurnIdx = RNGSUS.nextInt(tCoresToBurn.size());
            while (coreIt.hasNext()) {
                MTEC_ElectricWireBase tCore = coreIt.next();
                if (tIdx == tBurnIdx) tCore.mTE.setToFire();
                else tCore.cooldown();
                ++tIdx;
            }
            setInvalid(); // 标记非法，包含此的 core 需要更新
            // 依旧执行完此 tick
        }
        }
        // 根据终端的电压值进行注入
        for (OutputObject tOutput : mOutputs.values()) for (Map.Entry<InputObject, Long> tEntry : tOutput.mInputAmperages.entrySet()) {
            tOutput.injectEnergy(tEntry.getKey(), tOutput.mIOCore.getOutputVoltage(tEntry.getKey()), tEntry.getValue());
        }
        // 输入完成后遍历进行输出
        for (OutputObject tOutput : mOutputs.values()) tOutput.doOutput();
    }
    
    // 分配电流需要进行的操作，这里暂时同时直接注入，返回成功分配的数目
    protected long assignAmperage(InputObject aInput, Pair<OutputObject, Long> rPairToAssign, long aAmperage) {
        aAmperage = Math.min(aAmperage, rPairToAssign.second);
        rPairToAssign.second -= aAmperage;
        rPairToAssign.first.injectAmperageToPath(aInput, aAmperage);
        return aAmperage;
    }
    
    
    // 能量注入，标记输入，并获取成功注入的电流量
    public synchronized long doEnergyInjection(MTEC_ElectricWireBase aInputCore, byte aInputSide, long aVoltage, long aAmperage) {
        // 严格起见，需要保证网络已经初始化
        if (!mInited || aVoltage == 0 || aAmperage == 0) return 0;
        aVoltage = Math.abs(aVoltage);
        InputObject tInput = new InputObject(aInputCore, aInputSide);
        InputObject existInput = mInputs.get(tInput);
        if (existInput == null || existInput.mIOTE != tInput.mIOTE) mInputs.put(tInput, tInput); // 如果已有的 input 的 te 不同，也需要进行重置更新
        else tInput = existInput;
        return tInput.doInput(aVoltage, aAmperage);
    }
    // 标记输出
    public synchronized void putOutput(MTEC_ElectricWireBase aOutputCore, byte aOutputSide, TileEntity aOutputTE) {
        OutputObject tOutput = new OutputObject(aOutputCore, aOutputSide, aOutputTE);
        OutputObject existOutput = mOutputs.get(tOutput);
        if (existOutput == null || existOutput.mIOTE != tOutput.mIOTE) mOutputs.put(tOutput, tOutput); // 如果已有的 output 的 te 不同，也需要进行重置更新
    }
}
