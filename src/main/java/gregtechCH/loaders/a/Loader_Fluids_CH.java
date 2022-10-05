package gregtechCH.loaders.a;

import gregtech.loaders.a.Loader_Fluids;

import static gregtechCH.config.ConfigForge_CH.*;

public class Loader_Fluids_CH extends Loader_Fluids {
    /* 采用和 MTE 类似的方法实现不直接修改文件的添加流体 */
    @Override public final void run() {
        if (DATA_GTCH.enableChangeLoader_Fluids) fluidBeforeLoad();
        super.run();
        if (DATA_GTCH.enableChangeLoader_Fluids) fluidFinishLoad();
    }
    
    protected void fluidBeforeLoad() {
        /// 修改前标记修改开始
//        aRegistry.MODIFYING_ADD_START();
        
        /// 修改项
    
        /// 删除项
    
        /// 添加项（插入到指定位置后或者直接添加到最前）
    }
    protected void fluidFinishLoad() {
        /// 添加项（直接添加到最后）
    
        /// 最后标记修改结束，并进行错误检测
//        aRegistry.MODIFYING_ADD_END();
    }
}
