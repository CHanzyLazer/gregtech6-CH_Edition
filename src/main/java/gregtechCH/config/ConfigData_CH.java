package gregtechCH.config;

import gregtechCH.data.CS_CH;

import static gregapi.data.CS.T;

public class ConfigData_CH {
    public static boolean enableMachineUnsorted1 = CS_CH.ConfigsGTCH.GTCH.get(ConfigCategories_CH.general_CH, "enable_mechine_unsorted1_(gt6_false)", T);

    public static int adjustCoolantOtherDiv = CS_CH.ConfigsGTCH.REACTORS.get(ConfigCategories_CH.Reactors.adjustemission, "adjust_coolant_other_div_(gt6_1)", 8);
    public static int adjustCoolantOtherMul = CS_CH.ConfigsGTCH.REACTORS.get(ConfigCategories_CH.Reactors.adjustemission, "adjust_coolant_other_mul_(gt6_1)", 3);
}
