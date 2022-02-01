# json 配置文件
以`.json`结尾的配置文件，用来存储机器数据。（在目录`.minecraft\config\gregtechCH\*.json`）

目前包含

- `machines_generator` 产生能量的机器
  - `BurningBoxBrick` 砖块燃烧室
  - `BurningBoxSolid` 固体燃烧室
  - `DenseBurningBoxSolid` 致密固体燃烧室
  - `BurningBoxLiquid` 液体燃烧室
  - `DenseBurningBoxLiquid` 致密液体燃烧室
  - `BurningBoxGas` 气体燃烧室
  - `DenseBurningBoxGas` 致密气体燃烧室
  - `BurningBoxFluidizedBed` 流化床燃烧室
  - `DenseBurningBoxFluidizedBed` 致密流化床燃烧室
  - `HeatExchanger` 热交换器
  - `DenseHeatExchanger` 致密热交换器
  - `DieselEngine` 燃油引擎
- `machines_steam` 蒸汽相关机器
  - `SteamBoilerTank` 蒸汽锅炉
  - `StrongSteamBoilerTank` 加强蒸汽锅炉
  - `SteamTurbine` 蒸汽涡轮
  - `SteamEngine` 蒸汽引擎
  - `StrongSteamEngine` 加强蒸汽引擎
- `machines_multi-block` 多方快机器
  - `LargeSteamTurbine` 大型蒸汽涡轮
  - `LargeGasTurbine` 大型燃气涡轮

主要可以修改的属性有：
- 机器的 `ID`（实际为 `damage`），不建议修改
- 机器的材料 `material` ，不建议修改
- 机器的 `nbtHardness` 和 `nbtResistance`
- 机器的 `stackSize` 堆叠数量
- 机器的输入输出效率等属性
- 机器的 `recipeObject` 合成表，详见 [合成表介绍](recipe.md)
- 以及其他的属性，可以参见对应机器的 wiki 或者 json 文件名称的字面意思
- 后续更新会增加更多可修改的数据
- 原则上可以直接增加或删除项目来添加或移除对应的机器

读取失败会创建默认的配置文件，而原本文件会以 `.bak` 后缀保留一份