# 合成表介绍
对于固体燃烧室（铅），对应 json 配置文件为：
```json
{
  "ID":1100,
  "material":"Lead",
  "nbtEfficiency":5000,
  "nbtHardness":4.0,
  "nbtOutput":32,
  "nbtResistance":4.0,
  "recipeObject":["PCP","PwP","BBB","B","Blocks:brick_block","P","OreDictItemData:plateLead","C","OreDictItemData:plateDoubleAnyCopper"],
  "stackSize":16
}
```
其中`"recipeObject"`为其合成表，使用`gregapi`提供的有序合成接口的格式，其中大写字母`"P", "C", "B"`代指物品（没有实际意义），在后面统一定义，物品格式为 `[格式名]:[物品名]`；小写字母`"w"`代表工具（扳手），具体如下表：

| 支持的格式名            | 解释                                               |
|-------------------|--------------------------------------------------|
| `OreDictItemData` | GT6 添加的矿物词典，可以识别对应材料                             |
| `OD`              | GT6 类 `gregapi.data.OD` 中添加的矿物词典                 |
| `IL`              | GT6 类 `gregapi.data.IL` 中添加的矿物词典或物品              |
| `Blocks`          | Minecraft 类 `net.minecraft.init.Blocks` 中添加的原版方块 |
| `Items`           | Minecraft 类 `net.minecraft.init.Items` 中添加的原版物品  |
| `ore`             | 矿物词典，注意这个不能识别出材料                                 |
| `[任意已加载的 ModID]`  | 可以添加其他 mod 的物品                                   |

| 字母    | 工具名                                     |
|-------|-----------------------------------------|
| `"a"` | `OreDictToolNames.axe`                  |
| `"b"` | `OreDictToolNames.blade`                |
| `"c"` | `OreDictToolNames.crowbar`              |
| `"d"` | `OreDictToolNames.screwdriver`          |
| `"e"` | `OreDictToolNames.drill`                |
| `"f"` | `OreDictToolNames.file`                 |
| `"g"` | `OreDictToolNames.handdrill`            |
| `"h"` | `OreDictToolNames.hammer`               |
| `"i"` | `OreDictToolNames.solderingiron`        |
| `"j"` | `OreDictToolNames.solderingmetal`       |
| `"k"` | `OreDictToolNames.knife`                |
| `"l"` | `OreDictToolNames.magnifyingglass`      |
| `"n"` | `OreDictToolNames.monkeywrench`         |
| `"o"` | `OreDictToolNames.bendingcylindersmall` |
| `"p"` | `OreDictToolNames.drawplate`            |
| `"q"` | `OreDictToolNames.scissors`             |
| `"r"` | `OreDictToolNames.softhammer`           |
| `"s"` | `OreDictToolNames.saw`                  |
| `"v"` | `OreDictToolNames.sawaxe`               |
| `"w"` | `OreDictToolNames.wrench`               |
| `"x"` | `OreDictToolNames.wirecutter`           |
| `"y"` | `OreDictToolNames.chisel`               |
| `"z"` | `OreDictToolNames.bendingcylinder`      |