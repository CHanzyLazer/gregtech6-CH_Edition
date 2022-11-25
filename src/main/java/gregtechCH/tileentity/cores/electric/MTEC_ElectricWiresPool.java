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
public class MTEC_ElectricWiresPool {
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
    private abstract class IOObject {
        protected final MTEC_ElectricWireBase mIOCore;
        protected final byte mIOSide;
        protected final TileEntity mIOTE;
        
        private IOObject(MTEC_ElectricWireBase aIOCore, byte aIOSide, TileEntity aIOTE) {mIOCore = aIOCore; mIOSide = aIOSide; mIOTE = aIOTE; assert valid();}
        public boolean valid() {return mIOCore.mPoolUpdated && mIOCore.mPool==MTEC_ElectricWiresPool.this && mIOCore.mTE.getAdjacentTileEntity(mIOSide).mTileEntity == mIOTE;}
        
        // 重写 equals 和 hashCode 来使其作为 key 时可以按照我希望的方式运行
        @Override public boolean equals(Object aRHS) {
            return aRHS instanceof IOObject ? equals((IOObject) aRHS) : F;
        }
        public boolean equals(IOObject aRHS) {
            return mIOCore==aRHS.mIOCore && mIOSide==aRHS.mIOSide;
        }
        @Override public int hashCode() {return (mIOCore.hashCode()) ^ (mIOSide<<24);}
    }
    // 输入需要暂存电压电流用来统计此 tick 所有的输入，buffer 能量来比较简单实现能量守恒
    private class InputObject extends IOObject {
        protected final EnergyBuffer mEnergyBuffer = new EnergyBuffer();
        private InputObject(MTEC_ElectricWireBase aInputCore, byte aInputSide, TileEntity aInputTE) {super(aInputCore, aInputSide, aInputTE);}
        private InputObject(MTEC_ElectricWireBase aInputCore, byte aInputSide) {super(aInputCore, aInputSide, aInputCore.mTE.getAdjacentTileEntity(aInputSide).mTileEntity);}
        
        // 返回成功输入的电流
        public long doInput(long aVoltage, long aAmperage) {return mEnergyBuffer.injectUtilFull(aVoltage, aAmperage);}
        // 是否可以输出
        public boolean active() {return mEnergyBuffer.full();}
        // 检测是否合理，并且处理不合理部分
        @Override public boolean valid() {
            if (!super.valid()) {mEnergyBuffer.mEnergy = 0; return F;}
            return T;
        }
    }
    // 输出需要 buffer 所有的能量，用来保证可以预知输出是否成功，由于有多输入以及电阻，输出会有多组电压电流和对应的 buffer
    private class OutputObject extends IOObject {
        protected final Map<InputObject, EnergyBuffer> mInputList = new LinkedHashMap<>(); // linked 用于加速遍历
        private OutputObject(MTEC_ElectricWireBase aOutputCore, byte aOutputSide, TileEntity aOutputTE) {super(aOutputCore, aOutputSide, aOutputTE);}
        
        // 注入能量，一定成功（需要外部注入之前进行判断是否可以注入，而注入一定成功）
        public void injectEnergy(InputObject aInputObject, long aVoltage, long aAmperage) {
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
            EnergyBuffer tEnergyBuffer = mInputList.get(aInputObject);
            if (tEnergyBuffer == null) return T;
            return !tEnergyBuffer.full(); // 没有 full 都接受输入
        }
        // 检测是否合理，并且处理不合理部分
        @Override public boolean valid() {
            if (!super.valid()) {mInputList.clear(); return F;}
            // 遍历清除非法元素，注意需要使用迭代器来清除
            Iterator<InputObject> keyIt = mInputList.keySet().iterator();
            while (keyIt.hasNext()) {
                InputObject tKey = keyIt.next();
                if (!tKey.valid()) keyIt.remove();
            }
            return T;
        }
    }
    
    protected boolean mInited = F;
    protected final Map<IOObject, InputObject> mInputs = new LinkedHashMap<>(); // linked 用于加速遍历
    protected final Map<IOObject, OutputObject> mOutputs = new LinkedHashMap<>(); // linked 用于加速遍历
    
    // 合并 pool
    public void mergePool(MTEC_ElectricWiresPool aPoolToMerge) {
        if (aPoolToMerge.mInited) {
            mInputs.putAll(aPoolToMerge.mInputs);
            mOutputs.putAll(aPoolToMerge.mOutputs);
        }
    }
    // 更新 pool，移除过时的输入输出
    public void update() {
        // 遍历清除非法元素，注意需要使用迭代器来清除
        Iterator<InputObject> inputIt = mInputs.values().iterator();
        while (inputIt.hasNext()) {
            InputObject tInput = inputIt.next();
            if (!tInput.valid()) inputIt.remove();
        }
        Iterator<OutputObject> outputIt = mOutputs.values().iterator();
        while (outputIt.hasNext()) {
            OutputObject tOutput = outputIt.next();
            if (!tOutput.valid()) outputIt.remove();
        }
        mInited = T;
    }
    
    /* main code */
    // ticking，由拥有此 pool 的 core 竞争 tick 即可
    public final void onTick() {
        if (mTimer == SERVER_TIME) return; // 直接和 server_time 比较保证只会 tick 一次
        // 使用序列化的方法保证一次一定只会有一个线程调用
        onTickSy();
    }
    
    protected synchronized void onTickSy() {
        if (mTimer == SERVER_TIME) return; // 考虑到有可能平行执行，因此需要在这里再次进行判断
        mTimer = SERVER_TIME;
        
        // 每 tick 分配输入进行输出
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
                // 从随机位置开始遍历
                Iterator<OutputObject> outputIt = tActiveOutputs.iterator();
                for (int i = 0; i < RNGSUS.nextInt(tActiveOutputs.size()); ++i) {outputIt.next();}
                int tInjected = 0;
                while (tInjected < tRest) {
                    if (!outputIt.hasNext()) outputIt = tActiveOutputs.iterator();
                    (outputIt.next()).injectEnergy(tInput, tInput.mEnergyBuffer.mVoltage, 1);
                    ++tInjected;
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
