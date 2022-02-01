# 如何开始
下载地址 [Release](https://github.com/CHanzyLazer/gregtech6-CH_Edition/releases/tag/v6.14.23-CH-0.2)

除了 mod 本体 [gregtech_1.7.10-6.14.23-CH-0.2.jar](https://github.com/CHanzyLazer/gregtech6-CH_Edition/releases/download/v6.14.23-CH-0.2/gregtech_1.7.10-6.14.23-CH-0.2.jar),
还包含了默认配置文件 [gregtech_1.7.10-6.14.23-CH-0.2-defaultConfigPack.zip](https://github.com/CHanzyLazer/gregtech6-CH_Edition/releases/download/v6.14.23-CH-0.2/gregtech_1.7.10-6.14.23-CH-0.2-defaultConfigPack.zip),
此版本专用的 OmniOcular 配置文件 [gregtech_1.7.10-6.14.23-CH-0.2-OmniOcular_CH.zip](https://github.com/CHanzyLazer/gregtech6-CH_Edition/releases/download/v6.14.23-CH-0.2/gregtech_1.7.10-6.14.23-CH-0.2-OmniOcular_CH.zip),
此版本额外增加文本的汉化文件 [gregtech_1.7.10-6.14.23-CH-0.2-langrage_CH.zip](https://github.com/CHanzyLazer/gregtech6-CH_Edition/releases/download/v6.14.23-CH-0.2/gregtech_1.7.10-6.14.23-CH-0.2-langrage_CH.zip)

和原版一样，此版本需要 [Minecraft Forge 1614](https://files.minecraftforge.net/maven/net/minecraftforge/forge/1.7.10-10.13.4.1614-1.7.10/forge-1.7.10-10.13.4.1614-1.7.10-installer.jar) 
和前置 mod [CodeChickenCore](https://gregtech.overminddl1.com/codechicken/CodeChickenCore/1.7.10-1.0.7.47/CodeChickenCore-1.7.10-1.0.7.47-universal.jar) 以及 [CodeChickenLib](https://gregtech.overminddl1.com/codechicken/CodeChickenLib/1.7.10-1.1.3.140/CodeChickenLib-1.7.10-1.1.3.140-universal.jar)

更新可以参看 [**Changelog**](changelog.md)， **注意其中某些内容可能会剧透**

详细信息可以参考 [**wiki**](content.md)，**注意其中某些内容可能会剧透**

## 默认配置文件
和原版不完全一样，由于许多机器的参数都使用了 json 格式，改成配置文件的方式存储，所以在进行版本更新时应当覆盖掉原本的 json 配置文件。
否则那些本来应该在 json 中的机器如果没有读取到，则这些机器都不会出现在游戏中。

如果你有进行了一些自定义修改，可以使用 git 来进行管理。

关于 json 配置文件详见 [json 配置文件](config_json.md)

## OmniOcular 配置文件
增加的更多关键信息可以通过放大镜来得到，不过也对 OmniOcular 对应信息进行了修改和补充，
初次游玩配合 OmniOcular 应当可以得到更好的体验。

和原版一样，安装 mod [OmniOcular](https://www.curseforge.com/minecraft/mc-mods/omni-ocular) 
运行一次后将 OmniOcular 配置文件解压放入 `.minecraft\config\OmniOcular`

## 汉化文件
为了开发方便，魔改添加的汉化文本放到了额外的汉化文件 `gregtechCH.lang` 中，
和原版一样，将汉化文件解压放入根目录 `.minecraft` 即可，需要和原版汉化文件并存

可以使用的原版 GT6 汉化文件：[gregtech.lang](https://github.com/TeamNED/GregTech6-Translation-Groupware/releases)

## 整合包
也可以直接到 QQ 群 [潘多拉茶壶催更群](https://jq.qq.com/?_wv=1027&k=oUd8S3Tt) 的群文件中下载魔改版整合包

## 注意事项
由于改动比较激进，进行版本更新或者配置文件修改可能会导致存档损坏，注意存档备份

由于精力有限，只对于小版本更新，如 `0.2` 到 `0.2.1` 的更新会考虑更新的兼容性，而大版本更新不会考虑

