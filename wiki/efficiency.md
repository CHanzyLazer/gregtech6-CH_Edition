# 效率计算
`效率`采用 gt6 代码中 0 到 10000 的整数表示，实际效率为`效率/10000 * 100%`

许多能量生产（转换）机器效率和机器和材料种类有关，其中材料决定机器的`效率损失`，
机器种类利用这个值来得到最终的效率，满足公式`效率 = 10000 - max(a * 效率损失 + b, 0)`，
给定意义的话则其中`a`是机器的损失倍率，`b`是额外的效率损失，`效率损失`和`b`都可以为负值。
为了使得结果比较规整，实际效率可能只会精确到`2.5%`或者`3.33%`

具体数据如下表：

| 机器材料                          | 效率损失    |
|-------------------------------|---------|
| `ClayBrick` 粘土砖               | `10000` |
| `Lead`     铅                  | `7500`  |
| `Bismuth`  铋                  | `8000`  |
| `Bronze`   青铜                 | `5000`  |
| `Invar`    殷钢                 | `1500`  |
| `AnyIronSteel`   钢            | `6000`  |
| `Chromium`     铬              | `4500`  |
| `Titanium`     钛              | `4500`  |
| `Netherite`     下届合金          | `3500`  |
| `AnyTungsten`    钨            | `2000`  |
| `Tungstensteel`      钨钢       | `3500`  |
| `TantalumHafniumCarbide` 碳化钽铪 | `3000`  |
| `TinAlloy`             锡合金    | `6000`  |
| `Brass`                黄铜     | `5000`  |
| `Ironwood`             铁木     | `1000`  |
| `FierySteel`           炙热钢    | `2000`  |
| `Iridium`              铱      | `2500`  |
| `StainlessSteel`        不锈钢   | `2000`  |

| 机器种类                           | a      | b       |
|--------------------------------|--------|---------|
| `BurningBox` 燃烧室               | `1`    | `-2500` |
| `DenseBurningBox` 致密燃烧室        | `1`    | `-2500` |
| `HeatExchanger`   热交换器         | `1`    | `-2500` |
| `DenseHeatExchanger`   致密热交换器  | `1`    | `-2500` |
| `LargeHeatExchanger`   大型热交换器  | `1`    | `-2500` |
| `SteamBoilerTank`   蒸汽锅炉       | `0.2`  | `4000`  |
| `StrongSteamBoilerTank` 强化蒸汽锅炉 | `0.2`  | `3000`  |
| `LargeSteamBoilerTank` 大型蒸汽锅炉  | `0`    | `2000`  |
| `SteamTurbine`       蒸汽涡轮      | `0.5`  | `4000`  |
| `LargeSteamTurbine`  大型蒸汽涡轮    | `1`    | `2000`  |
| `SteamEngine`       蒸汽引擎       | `0.33` | `5000`  |
| `StrongSteamEngine`    强化蒸汽引擎  | `0.33` | `5000`  |
| `DieselEngine`     燃油引擎        | `0.2`  | `6000`  |
| `LargeTurbineGas`    大型燃气涡轮    | `1`    | `2000`  |
| `ElectricDynamo`    发电机        | `0`    | `4000`  |
| `LargeDynamo`    大型发电机         | `0`    | `500`   |

其中涡轮的效率和转子的材料有关，数据如下表：

| 转子材料                  | 效率损失   |
|-----------------------|--------|
| `Bronze`   青铜         | `5000` |
| `Brass`    黄铜         | `5000` |
| `Invar`    殷钢         | `4000` |
| `AnyIronSteel`   钢    | `6000` |
| `Chromium`     铬      | `4000` |
| `Ironwood`     铁木     | `2500` |
| `Steeleaf`    钢叶      | `3000` |
| `Thaumium`    神秘锭     | `4000` |
| `Titanium`     钛      | `3000` |
| `FierySteel`    炙热钢   | `5000` |
| `Aluminium`     铝     | `1800` |
| `Magnalium`    镁铝合金   | `1500` |
| `VoidMetal`    虚空金属   | `500`  |
| `Trinitanium`   特林钛合金 | `1500` |
| `Graphene`      石墨烯   | `1500` |

大型涡轮的效率还和机器长度有关，详见：[大型涡轮](large_motor.md)