package gregtechCH.tileentity.cores.electric;

import gregapi.data.TD;
import gregapi.tileentity.energy.ITileEntityEnergy;
import gregapi.util.UT;
import net.minecraft.tileentity.TileEntity;

import java.util.*;

import static gregapi.data.CS.*;

/**
 * WIP
 * 电网的公用类，用来接受和输出能量
 * 根据输入来调整上限，并且存储输入用于最近搜索
 */
public class MTEC_ElectricWiresManager {
    protected long mTimer = 0; // 用来保证每 tick 只会 tick 一次
    
    // 通用电力 buffer，标记电压电流和能量
    private static class EnergyBuffer {
        public long mVoltage = 128, mAmperage = 1, mEnergy = 0;
        
        // 注入能量直到满，输出成功注入的电流值（用于 input 注入能量）
        public long injectUtilFull(long aVoltage, long aAmperage) {
            mAmperage = aAmperage;
            mVoltage = aVoltage;
            long tInEnergy = aAmperage*aVoltage;
            if (mEnergy >= tInEnergy) return 0;
            long tInAmperage = UT.Code.divup(tInEnergy-mEnergy, aVoltage);
            mEnergy += tInAmperage*aVoltage;
            return tInAmperage;
        }
        // 无论如何都要注入能量（用于 output 注入能量）
        public void injectAny(long aVoltage, long aAmperage) {
            mAmperage = aAmperage;
            mVoltage = aVoltage;
            mEnergy += aAmperage*aVoltage;
        }
        // 消耗电流量的能量
        public void emit(long aAmperage) {mEnergy -= mVoltage*aAmperage;}
        public long stdEnergy() {return mVoltage*mAmperage;}
        public boolean full() {return mEnergy >= mVoltage*mAmperage;}
    }
    // 输入类和输出类以及通用部分（输入或输出的 core 和方向以及对应的 TE）
    private static class IOObject {
        protected final MTEC_ElectricWireBase mIOCore;
        protected final byte mIOSide;
        protected final TileEntity mIOTE;
        
        private IOObject(MTEC_ElectricWireBase aIOCore, byte aIOSide, TileEntity aIOTE) {mIOCore = aIOCore; mIOSide = aIOSide; mIOTE = aIOTE;}
        public boolean valid(MTEC_ElectricWiresManager aManager) {return mIOCore.mManager == aManager && mIOCore.mTE.getAdjacentTileEntity(mIOSide).mTileEntity == mIOTE;}
        
        // 重写 equals 和 hashCode 来使其作为 key 时可以按照我希望的方式运行
        @Override public boolean equals(Object aRHS) {return aRHS instanceof IOObject ? equals((IOObject) aRHS) : F;}
        public boolean equals(IOObject aRHS) {return mIOCore==aRHS.mIOCore && mIOSide==aRHS.mIOSide;}
        @Override public int hashCode() {return (mIOCore.hashCode()) ^ (mIOSide<<24);}
    }
    // 输入需要暂存电压电流用来统计此 tick 所有的输入，buffer 能量来比较简单实现能量守恒
    private static class InputObject extends IOObject {
        protected final EnergyBuffer mEnergyBuffer = new EnergyBuffer();
        private InputObject(MTEC_ElectricWireBase aInputCore, byte aInputSide, TileEntity aInputTE) {super(aInputCore, aInputSide, aInputTE);}
        private InputObject(MTEC_ElectricWireBase aInputCore, byte aInputSide) {this(aInputCore, aInputSide, aInputCore.mTE.getAdjacentTileEntity(aInputSide).mTileEntity);}
        
        // 返回成功输入的电流
        public long doInput(long aVoltage, long aAmperage) {return mEnergyBuffer.injectUtilFull(aVoltage, aAmperage);}
        // 是否可以输出
        public boolean active() {return mEnergyBuffer.full();}
        // 检测是否合理，并且处理不合理部分
        @Override public boolean valid(MTEC_ElectricWiresManager aManager) {
            if (!super.valid(aManager)) {mEnergyBuffer.mEnergy = 0; return F;}
            return T;
        }
    }
    // 输出需要 buffer 所有的能量，用来保证可以预知输出是否成功，由于有多输入以及电阻，输出会有多组电压电流和对应的 buffer
    private static class OutputObject extends IOObject {
        protected final Map<InputObject, EnergyBuffer> mInputList = new LinkedHashMap<>(); // linked 用于加速遍历
        private OutputObject(MTEC_ElectricWireBase aOutputCore, byte aOutputSide, TileEntity aOutputTE) {super(aOutputCore, aOutputSide, aOutputTE);}
        
        // 注入能量，一定成功（需要外部注入之前进行判断是否可以注入，而注入一定成功）
        public void injectEnergy(InputObject aInputObject, long aVoltage, long aAmperage) {
            if (equals(aInputObject)) return; // 无论如何都不需要自己的输入
            EnergyBuffer tEnergyBuffer = mInputList.get(aInputObject);
            if (tEnergyBuffer == null) {
                tEnergyBuffer = new EnergyBuffer();
                mInputList.put(aInputObject, tEnergyBuffer);
            }
            aInputObject.mEnergyBuffer.emit(aAmperage);
            tEnergyBuffer.injectAny(aVoltage, aAmperage);
            assert aInputObject.mEnergyBuffer.mEnergy >= 0;
        }
        // 执行输出，需要遍历 mInputList 来输出，保证过高电压一定会导致爆炸之类的
        public void doOutput() {
            for (EnergyBuffer tEnergyBuffer : mInputList.values()) if (tEnergyBuffer.full()) {
                long tInserted = ITileEntityEnergy.Util.insertEnergyInto(TD.Energy.EU, OPOS[mIOSide], tEnergyBuffer.mVoltage, tEnergyBuffer.mAmperage, mIOCore.mTE, mIOTE);
                tEnergyBuffer.emit(tInserted);
                assert tEnergyBuffer.mEnergy >= 0;
            }
        }
        // 是否需要输入
        public boolean active(InputObject aInputObject) {
            if (equals(aInputObject)) return F; // 无论如何都不需要自己的输入
            EnergyBuffer tEnergyBuffer = mInputList.get(aInputObject);
            if (tEnergyBuffer == null) return T;
            return !tEnergyBuffer.full(); // 没有 full 都接受输入
        }
        // 检测是否合理，并且处理不合理部分
        @Override public boolean valid(MTEC_ElectricWiresManager aManager) {
            if (!super.valid(aManager)) {mInputList.clear(); return F;}
            // 遍历清除非法元素，注意需要使用迭代器来清除
            Iterator<InputObject> keyIt = mInputList.keySet().iterator();
            while (keyIt.hasNext()) {
                InputObject tKey = keyIt.next();
                if (!aManager.mInputs.containsKey(tKey) || !tKey.valid(aManager)) keyIt.remove();
            }
            return T;
        }
    }
    
    protected boolean mInited = F;
    protected final Map<IOObject, InputObject> mInputs = new LinkedHashMap<>(); // linked 用于加速遍历
    protected final Map<IOObject, OutputObject> mOutputs = new LinkedHashMap<>(); // linked 用于加速遍历
    
    // 合并 Manager
    public void mergeManager(MTEC_ElectricWiresManager aManagerToMerge) {
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
    // 更新 Manager，移除过时的输入输出
    public void update() {
        // 遍历清除非法元素，注意需要使用迭代器来清除
        Iterator<InputObject> inputIt = mInputs.values().iterator();
        while (inputIt.hasNext()) {
            InputObject tInput = inputIt.next();
            if (!tInput.valid(this)) inputIt.remove();
        }
        Iterator<OutputObject> outputIt = mOutputs.values().iterator();
        while (outputIt.hasNext()) {
            OutputObject tOutput = outputIt.next();
            if (!tOutput.valid(this)) outputIt.remove();
        }
        mInited = T;
    }
    
    /* main code */
    // ticking，由拥有此 Manager 的 core 竞争 tick 即可
    public final void onTick() {
        if (mTimer == SERVER_TIME) return; // 直接和 server_time 比较保证只会 tick 一次
        // 使用序列化的方法保证一次一定只会有一个线程调用
        onTickSy();
    }
    
    protected synchronized void onTickSy() {
        if (mTimer == SERVER_TIME) return; // 考虑到有可能平行执行，因此需要在这里再次进行判断
        mTimer = SERVER_TIME;
        
        // 每 tick 分配输入进行输出
        Integer tRestIdx = null; // 用来保证多个输入会接着上一个输入进行分配
        for (InputObject tInput : mInputs.values()) {
            // 对于每个输入，统计所有需要的输出，并且将其放入 list 中
            List<OutputObject> tActiveOutputs = new LinkedList<>();
            for (OutputObject tOutput : mOutputs.values()) if (tOutput.active(tInput)) tActiveOutputs.add(tOutput);
            // 根据输入获取每个输出分配的电流，这里可以直接注入
            if (tActiveOutputs.isEmpty()) continue;
            long tAmperage = tInput.mEnergyBuffer.mAmperage / tActiveOutputs.size();
            int tRest = (int)tInput.mEnergyBuffer.mAmperage % tActiveOutputs.size();
            if (tAmperage > 0) for (OutputObject tOutput : tActiveOutputs) tOutput.injectEnergy(tInput, tInput.mEnergyBuffer.mVoltage, tAmperage);
            if (tRest > 0) {
                // 从上一个结束的位置开始遍历，如果没有则随机位置开始
                Iterator<OutputObject> outputIt = tActiveOutputs.iterator();
                if (tRestIdx == null) tRestIdx = RNGSUS.nextInt(tActiveOutputs.size());
                if (tRestIdx >= tActiveOutputs.size()) tRestIdx %= tActiveOutputs.size();
                for (int i = 0; i < tRestIdx; ++i) {outputIt.next();}
                int tInjected = 0;
                while (tInjected < tRest) {
                    if (!outputIt.hasNext()) outputIt = tActiveOutputs.iterator();
                    (outputIt.next()).injectEnergy(tInput, tInput.mEnergyBuffer.mVoltage, 1);
                    ++tInjected;
                    ++tRestIdx;
                    if (tRestIdx == tActiveOutputs.size()) tRestIdx = 0;
                }
            }
        }
        // 输入完成后遍历进行输出
        for (OutputObject tOutput : mOutputs.values()) tOutput.doOutput();
    }
    
    // 能量注入，标记输入，并获取成功注入的电流量
    public synchronized long doEnergyInjection(MTEC_ElectricWireBase aInputCore, byte aInputSide, long aVoltage, long aAmperage) {
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
