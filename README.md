Litematica Printer
==================
This fork adds printing functionality for Litematica fabric 1.18 and 1.17 versions. Printer allows players to build
big structures more quickly by automatically placing the correct blocks around you.

The main branch (printing) is dedicated to latest version of Minecraft, while printing_1.17 and printing_1.16 are
for the older versions respectively. If you have issues with the printer, **do not** bother the original creator of
Litematica (maruohon) with them. Contact me instead. Feature requests or bugs can be reported via github issues.

For downloads check out [releases](https://github.com/aleksilassila/litematica-printing/releases/latest).
To install the mod, first download the original Litematica and MaLiLib from [here](https://www.curseforge.com/minecraft/mc-mods/litematica).
You will also need [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api/).
Finally, move the printer's .jar from [releases](https://github.com/aleksilassila/litematica-printing/releases/latest) to your mods folder.

![Demo](printer_demo.gif)

---

## v1.0.0-beta — Minecraft 26.3 版（2026-09-26）

本版本為適配 **Minecraft 26.3 / Fabric Loader 0.19.5 / Fabric API 0.161.0** 的測試版本，由 `zhaixianyu/litematica-printer` 主分支（v2.4 / 26.2）升級而來。

### 本次修改（本次升級所做）
- **移除無法升級到 26.3 的卡點依賴**：chest-tracker(port)、where-is-it(port)、jackfredlib、searchables（這些 mod 尚未提供 26.3 版本）。
- **摘除 3 項非優先功能**：遠程容器記憶、找物品、容器同步；**Quick Shulker 保留**。
- **完整保留特殊打印模式**：挖掘（ExcavateMode）、替換（ReplaceMode）、破基岩（bedrockminer）——**均已在 26.3 實測通過**。

### 26.3 API 適配（因應 26.3 大幅重構）
- `ResourceLocation` → `net.minecraft.resources.Identifier`
- `DirtPathBlock` → `PathBlock`；`RedStoneWireBlock` → `RedstoneWireBlock`
- `AxeItem` / `AxeItemAccessor` 移除 → 改用自建 strippables 映射
- `blocksMotion()` → `isSolid()`
- `swinging` 欄位 → `isSwinging()`
- `VertexFormat` / `GpuBufferSlice` → `com.mojang.renderpearl.*`
- malilib `onRenderWorldLast` 改為 7 參數（移除 `Matrix4fc`）
- 視窗刷新率改用 `getActiveVideoMode().getRefreshRate()`

### 待完善事項
- **preprocess 插件 rootNode 仍停在 26.2**，導致多版本共同建置（`gradlew build`）會連帶觸發 26.2 編譯並失敗；26.3 單版本建置 `gradlew :26.3:jar` 正常。
- 替換模式「水換成沙子/沙礫」需在 26.3 環境再次確認（本版為 zhaixianyu 原生實作）。
- 破基岩運作前提：生存模式 + 效率Ⅴ鎬 + 急迫Ⅱ + 材料（活塞×2、紅石火把×1、黏液塊×1）。

---

How To Use
----------
Using the printer is straightforward: You can toggle the feature by pressing `CAPS_LOCK` by default. To configure variables such as
printing speed and range, open Litematica's settings by pressing `M + C` and navigate to "Generic" tab. Printer's configuration can be
found at the bottom of the page. You can also rebind the printing toggle under "Hotkeys" tab. Holding down `V` by default will also
print regardless if the printer is toggled on or off.

### List of blacklisted blocks
These blocks have not been implemented yet for various reasons and the printer will skip them instead of placing them wrong. If any
other blocks are placed incorrectly, try to lower the printing speed. If certain block is still placed incorrectly, you can create
[an issue](https://github.com/aleksilassila/litematica-printer/issues).
 - Grindstones
 - Skulls placed on the ground
 - Signs
 - Glow lichen and vines
 - Entities, including item frames and armor stands

----------

[![Curseforge](http://cf.way2muchnoise.eu/full_litematica_downloads.svg)](https://minecraft.curseforge.com/projects/litematica) [![Curseforge](http://cf.way2muchnoise.eu/versions/For%20MC_litematica_all.svg)](https://minecraft.curseforge.com/projects/litematica)

# Litematica
Litematica is a client-side schematic mod for Minecraft, with also lots of extra functionality
especially for creative mode (such as schematic pasting, area cloning, moving, filling, deletion).

It's primarily developed on MC 1.12.2 for LiteLoader. It has also been ported to Rift on MC 1.13.2,
and for Fabric on MC 1.14 and later. There are also Forge versions for 1.12.2, and Forge ports for 1.14.4+
are also planned, but Forge will need to start shipping the Mixin library before those can happen.

Litematica was started as an alternative for [Schematica](https://minecraft.curseforge.com/projects/schematica),
for players who don't want to have Forge installed on their client, and that's why it was developed for Liteloader.

For compiled builds (= downloads), see:
* CurseForge: http://minecraft.curseforge.com/projects/litematica
* For more up-to-date development builds: https://masa.dy.fi/mcmods/client_mods/
* **Note:** Litematica also requires the malilib library mod! But on the other hand Fabric API is not needed.

## Compiling
* Clone the repository
* Open a command prompt/terminal to the repository directory
* On 1.12.x you will first need to run `gradlew setupDecompWorkspace`
  (unless you have already done it once for another project on the same 1.12.x MC version
  and mappings and the same mod loader, Forge or LiteLoader)
* Run `gradlew build` to build the mod
* The built jar file will be inside `build/libs/`

## YourKit
![](https://www.yourkit.com/images/yklogo.png)

We appreciate YourKit for providing the project developers licenses of its profiler to help us improve performance! 

YourKit supports open source projects with innovative and intelligent tools
for monitoring and profiling Java and .NET applications.
YourKit is the creator of [YourKit Java Profiler](https://www.yourkit.com/java/profiler/),
[YourKit .NET Profiler](https://www.yourkit.com/.net/profiler/) and
[YourKit YouMonitor](https://www.yourkit.com/youmonitor), tools for profiling Java and .NET applications.
