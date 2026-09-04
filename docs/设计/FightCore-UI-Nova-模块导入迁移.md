# FightCore UI Nova 模块导入迁移

## 1. 文档目的

本文用于指导 `fight-core-monorepo` 与服务端 FightCore ConfigPack 将 UI Nova 脚本从逐类 Java 导入迁移到宿主虚拟模块导入。

目标写法：

```nova
import "@fightcore/ui.api"
```

如果模块标识使用 `nova:fs` 这类带冒号的名称，正确语法同样必须是字符串导入：

```nova
import "nova:fs"
```

Nova 当前不支持无引号的 `import nova:fs`。字符串模块由宿主调用 `Nova.registerModule(moduleId, source)` 注册，并在解释执行或字节码编译前展开。

本文只定义目标结构和迁移步骤，不要求保留旧导入方式的兼容层，也不允许在模块缺失时静默回退到逐类导入。

## 2. 当前状态

审计目录：

```text
plugins/FightCorePlugin/config/ui
```

统计结果：

| 项目 | 数量 |
| --- | ---: |
| `.nova` 文件 | 70 |
| 含 Java import 的文件 | 67 |
| import 总行数 | 510 |
| Java import 行数 | 490 |
| Nova 标准库 import 行数 | 20 |
| 唯一 import | 59 |
| 唯一 Java import | 56 |

如果 67 个脚本统一改成一个 FightCore UI 模块导入，目标目录的 import 总量约从 510 行降至 87 行，即 67 条 UI 模块导入加 20 条按需 Nova 标准库导入，净减少约 423 行。

当前资源还有两条确定无效的导入：

```nova
import java java.util.ZeList
```

位置：

```text
ui/host/hud/skill/skill_list_view.nova
ui/host/hud/skill/skill_view.nova
```

`ZeList` 属于 FightCore UI 公共 API，不属于 `java.util`。完成模块迁移后应删除这两条错误导入，不增加别名或兼容类型。

## 3. 迁移原则

### 3.1 业务脚本只依赖公共模块

UI 业务脚本不得继续直接导入以下实现包：

```text
com.gitee.fightcoremod.module.ui.*
com.gitee.fightcoremod.core.*
```

所有会被 UI 脚本作为参数、返回值、字段类型、事件载荷或工具使用的类型，都必须先归入稳定公共 API：

```text
common/src/main/kotlin/com/gitee/fightcoremod/api/ui
```

模块清单只能导出公共 API。不得为了减少 import 而把 Mod 内部实现类整体暴露到 `@fightcore/ui.api`。

### 3.2 JavaTypes 与模块导出共用一份类型目录

当前 `FightCoreUIJavaTypes` 维护 UI 编译期 Java 类型。模块导出不能再手写第二份独立类型清单，否则新增类型时容易只更新 JavaTypes、漏更新模块。

建议提取：

```text
mod/src/main/kotlin/com/gitee/fightcoremod/module/ui/script/FightCoreUIApiCatalog.kt
```

该目录统一提供：

- 公共类型 `Class<?>` 列表；
- JavaTypes 注册所需类型；
- Nova 模块源码生成所需的全限定类名；
- 模块 ID 常量；
- 向单个 `Nova` 实例安装全部模块的方法。

建议接口形态：

```kotlin
object FightCoreUIApiCatalog {
    const val CORE_MODULE_ID: String = "@fightcore/ui.core.api"
    const val STRUCT_MODULE_ID: String = "@fightcore/ui.struct.api"
    const val ROOT_MODULE_ID: String = "@fightcore/ui.api"

    fun install(nova: Nova) {
        // 注册子模块与聚合模块。
    }
}
```

不得在 `install` 中捕获模块注册错误后继续编译。模块清单缺失、重复或类型不可解析时必须立即失败。

### 3.3 使用聚合模块，内部仍按职责拆分

业务脚本只导入：

```nova
import "@fightcore/ui.api"
```

宿主内部建议拆成：

| 模块 | 职责 |
| --- | --- |
| `@fightcore/ui.core.api` | `FightCoreClientAPI`、`ZeView`、组件、ScriptContext、事件、Tween、RPC、构建器和公共工具 |
| `@fightcore/ui.struct.api` | Actionbar、Bossbar、Chat、Health、Hotbar、Scoreboard、TabList、Title、Tooltip 等快照 DTO |
| `@fightcore/ui.api` | 只导入以上子模块，作为业务脚本唯一入口 |

聚合模块示意：

```nova
import "@fightcore/ui.core.api"
import "@fightcore/ui.struct.api"
```

Nova 支持嵌套虚拟模块导入。使用聚合模块可以保持脚本一行导入，同时避免把所有类型塞入一个不可维护的清单。

### 3.4 标准库仍按需导入

以下 Nova 标准库导入继续留在实际使用它们的脚本中：

```nova
import nova.json.*
import nova.time.now
import nova.time.now as currentTimeMillis
```

不要为了追求绝对单行 import，把 JSON、时间、HTTP、文件系统等无关能力全部加入 UI API 模块。

### 3.5 不采用 Java 包通配符替代模块

不要把当前代码机械替换为：

```nova
import java com.gitee.fightcoremod.api.ui.*
import java com.gitee.fightcoremod.api.ui.component.*
import java com.gitee.fightcoremod.api.ui.script.*
```

当前 Nova 语义分析能够识别 Java wildcard，但字节码 lowering 对自定义包的简单类名解析并不完整。FightCore UI 运行时使用 `compileToBytecode`，因此自定义 Java 包通配符不是本次迁移的可靠目标。

## 4. 公共 API 前置迁移

### 4.1 Snapshot DTO

当前脚本使用 13 种 `module.ui.struct` 类型，共涉及 13 个脚本。目标包为：

```text
com.gitee.fightcoremod.api.ui.struct
```

包括但不限于：

- `ActionbarSnapshot`
- `BossbarEntry`
- `ChatScreenSnapshot`
- `ChatSnapshot`
- `ChatSuggestionItem`
- `HealthSnapshot`
- `HotbarSnapshot`
- `ScoreboardSnapshot`
- `TabListPlayer`
- `TabListSnapshot`
- `TitleSnapshot`
- `TooltipLineSnapshot`
- `TooltipSnapshot`

FightCore 工作区当前已经存在一组未提交的 DTO 公共化改动。实施模块迁移时应接续这组改动，不得覆盖、重复创建或保留旧包转发类。

### 4.2 仍然泄漏的内部类型

以下 9 个内部类型被 7 个脚本直接导入，必须在模块切换前处理：

| 内部类型 | 使用位置 | 目标处理 |
| --- | --- | --- |
| `SlotItemSetEvent` | `components/container_slot.nova` | 移入公共 UI event API |
| `CarouselController` | `host/hud/hotbar/hotbar.nova` | 提取公共控制器接口 |
| `CarouselLib` | `host/hud/hotbar/hotbar.nova` | 提取公共 Lib 接口 |
| `ScriptItemTool` | `host/hud/skill/skill_view.nova`、`skill/skill_tree_selected_skill.nova` | 提取公共 item tool API |
| `MouseWheelEvent` | `mapview/mapview.nova` | 移入公共 UI event API |
| `FightCoreClientScriptAPI` | `mapview/mapview.nova` | 改用公共根 API，不导出内部 facade |
| `ScriptDraggingEvent` | `mapview/mapview.nova`、`showcase/game_2048.nova` | 移入公共 drag event API |
| `ScriptDragRegistration` | `mapview/mapview.nova` | 使用公共 `Registration` 契约 |
| `ScriptCursorRegistration` | `skill/job_slot_item.nova` | 使用公共 `Registration` 契约 |

禁止做法：

- 把上述实现类直接加入 `@fightcore/ui.api`；
- 在旧包保留 typealias、转发类或双注册；
- 模块解析失败时回退到实现类导入。

### 4.3 外部平台类型

当前还有 7 个脚本依赖以下外部或平台类型：

| 类型 | 建议 |
| --- | --- |
| `net.minecraft.world.item.ItemStack` | 若公共 UI API 的方法签名直接使用它，则作为受控集成类型加入 core 模块；否则保留在少量脚本中显式导入 |
| `org.slf4j.Logger` | 业务脚本改用公共 `ScriptLogger`，不导出 SLF4J |
| `com.gitee.fightcoremod.core.platform.IEntityView` | 提取到公共 API 或改用已有公共 Entity/View 契约 |

是否把 `ItemStack` 加入聚合模块必须由最终公共 API 签名决定，不能通过系统级 fallback 同时支持两种路径。

## 5. 两类客户端运行时必须分别接入

FightCore 客户端有两类 Nova 运行时，模块安装不能只改其中一个。

### 5.1 UI 组件脚本

入口：

```text
mod/src/main/kotlin/com/gitee/fightcoremod/module/ui/script/NovaScriptRuntime.kt
```

当前流程：

1. 创建 `Nova`；
2. 设置脚本 ClassLoader；
3. 安装 `FightCoreUIJavaTypes`；
4. 直接 `compileToBytecode` 单个 UI 组件脚本。

目标流程：

1. 创建 `Nova`；
2. 设置脚本 ClassLoader；
3. 安装 JavaTypes；
4. 安装 FightCore UI 虚拟模块；
5. 编译单个 UI 组件脚本。

模块必须在 `compileToBytecode` 前注册。

### 5.2 客户端全局脚本

入口：

```text
mod/src/main/kotlin/com/gitee/fightcoremod/module/ui/script/FightCoreClientScriptWorkspace.kt
```

该运行时会按照 `script/config.yml` 合并 prelude 和 scripts，然后编译成一个全局程序。它也必须在合并源码调用 `compileToBytecode` 前安装相同模块目录。

UI 组件脚本与客户端全局脚本可以共享模块类型清单，但不能共享脚本实例、生命周期状态、RPC handler 或 disposer。

## 6. ConfigPack 目录修正

ConfigPack 的顶层约定是：

```text
config/client.yml
config/ui/**
config/script/**
```

当前服务器目录把 9 个客户端全局脚本放在：

```text
plugins/FightCorePlugin/config/ui/script/**
```

但客户端 `ClientScriptResourceStore` 只读取：

```text
configpack/script/**
```

因此在模块迁移前，应把全局脚本一次性移动到：

```text
plugins/FightCorePlugin/config/script/**
```

需要移动的内容：

```text
script/config.yml
script/client.nova
script/interaction/client.actions.nova
script/interaction/client.execute.nova
script/interaction/client.finder.nova
script/interaction/client.focus.nova
script/interaction/client.sync.nova
script/interaction/type.nova
script/lib/fightcore_hud_visible.nova
script/lib/fightcore.performance.api.nova
```

迁移后删除 `config/ui/script`，不保留旧目录扫描或复制 fallback。

注意：这 9 个 `.nova` 文件中只有 6 个含 Java import；另外 3 个只有 Nova 标准库导入，不需要强行增加 `@fightcore/ui.api`。

## 7. 脚本批量迁移规则

### 7.1 普通 UI 组件

迁移前：

```nova
import java com.gitee.fightcoremod.api.FightCoreClientAPI
import java com.gitee.fightcoremod.api.ui.component.ZeBox
import java com.gitee.fightcoremod.api.ui.component.ZeImage
import java com.gitee.fightcoremod.api.ui.script.ScriptContext
import java com.gitee.fightcoremod.api.ui.script.ScriptEventListener
import java com.gitee.fightcoremod.api.ui.script.ScriptComponent
import java java.lang.IllegalStateException
```

迁移后：

```nova
import "@fightcore/ui.api"
```

### 7.2 使用 Nova 标准库的组件

迁移前：

```nova
import nova.json.*
import java com.gitee.fightcoremod.api.FightCoreClientAPI
import java com.gitee.fightcoremod.api.ui.component.ZeLabel
import java com.gitee.fightcoremod.api.ui.script.ScriptContext
import java com.gitee.fightcoremod.api.ui.script.ScriptComponent
```

迁移后：

```nova
import "@fightcore/ui.api"
import nova.json.*
```

### 7.3 批量替换约束

批量脚本只允许做以下机械操作：

1. 识别文件是否存在 `import java ...`；
2. 删除已由 `@fightcore/ui.api` 导出的 Java import；
3. 在 import 区首部插入且只插入一次 `import "@fightcore/ui.api"`；
4. 保留 `nova.*` import、注释与业务正文；
5. 删除文件正文末尾被提升的 Java import；
6. 不重排非 import 代码；
7. 不顺手修改业务逻辑、类型推断 workaround 或 UI 生命周期。

如果文件只含 `nova.*` import 且不使用 UI 公共 Java 类型，则不增加 FightCore 模块导入。

## 8. 资源副本与改动范围

当前脚本存在三类物理副本：

| 位置 | Nova 文件 | 含 Java import |
| --- | ---: | ---: |
| `mod/src/main/resources/configpack/ui` | 12 | 12 |
| `plugin/src/main/resources/configpack/ui` | 12 | 12 |
| 服务器 `plugins/FightCorePlugin/config/ui` | 70 | 67 |

三处共有的 12 个脚本当前内容并不完全一致。迁移不能假设简单覆盖安全，必须先明确哪一处是源文件、哪一处是发布副本。

建议以 `fight-core-monorepo` 的 ConfigPack 源资源为权威，通过现有发布流程生成或同步服务器副本。若项目暂时没有生成流程，则本次提交必须同步修改三处，并增加内容一致性测试，避免以后继续漂移。

按三处全部同步计算，需要修改 91 个物理脚本文件；逻辑上仍是 67 个需要模块导入的脚本。

## 9. 建议文件改动清单

### 9.1 FightCore Mod/Common

建议新增或修改：

```text
common/src/main/kotlin/com/gitee/fightcoremod/api/ui/struct/**
common/src/main/kotlin/com/gitee/fightcoremod/api/ui/event/**
common/src/main/kotlin/com/gitee/fightcoremod/api/ui/script/**
mod/src/main/kotlin/com/gitee/fightcoremod/module/ui/script/FightCoreUIApiCatalog.kt
mod/src/main/kotlin/com/gitee/fightcoremod/module/ui/script/FightCoreUIJavaTypes.kt
mod/src/main/kotlin/com/gitee/fightcoremod/module/ui/script/NovaScriptRuntime.kt
mod/src/main/kotlin/com/gitee/fightcoremod/module/ui/script/FightCoreClientScriptWorkspace.kt
```

具体公共 API 文件数量取决于 9 个内部类型的接口拆分结果，预计新增或调整 7～15 个 Kotlin 文件。

### 9.2 ConfigPack

需要：

- 迁移 67 个逻辑脚本的 import；
- 移动 `ui/script` 到顶层 `script`；
- 同步 Mod、Plugin 和服务器资源副本；
- 不保留旧路径和旧 import 双轨运行。

### 9.3 NovaLang

本迁移不要求修改 NovaLang 生产代码。以下能力已经存在：

- `Nova.registerModule(moduleId, source)`；
- 字符串模块导入；
- 嵌套虚拟模块；
- `compileToBytecode` 前模块展开。

可以在 NovaLang 或 FightCore 测试中补一条“模块仅包含 Java import，入口脚本只导入模块并成功字节码编译”的回归测试，但不要为 FightCore 写 NovaLang 系统级特殊解析。

## 10. 测试与验证

### 10.1 模块契约测试

新增测试必须覆盖：

1. 创建 `Nova`；
2. 设置 FightCore ClassLoader；
3. 安装 JavaTypes 和三个 UI 模块；
4. 编译只包含 `import "@fightcore/ui.api"` 的脚本；
5. 使用 `ZeBox`、`ScriptContext`、`ScriptComponent`、快照 DTO 和回调接口；
6. 确认 `compileToBytecode` 成功；
7. 确认遗漏模块注册时明确失败。

禁止在测试辅助代码里偷偷把原逐类 import 拼到入口源码前面。

### 10.2 全量资源编译

更新：

```text
mod/src/test/kotlin/com/gitee/fightcoremod/test/NovaUIResourceCompileTest.kt
mod/src/test/kotlin/com/gitee/fightcoremod/test/NovaClientScriptWorkspaceCompileTest.kt
mod/src/test/kotlin/com/gitee/fightcoremod/test/UIHudChatRuntimeTest.kt
```

所有直接创建 `Nova()` 并编译 UI/客户端脚本的测试，都必须调用与生产代码相同的模块安装入口。

验证点：

- 67 个脚本不再出现已纳入模块的 `import java`；
- 不存在 `java.util.ZeList`；
- 不存在 `com.gitee.fightcoremod.module.ui.*` 导入；
- 每个组件脚本仍导出 `createComponent()`；
- 客户端全局脚本仍导出 `onEnable()`、`onDisable()`；
- Chat、HUD、Tween、RPC、drag、tooltip 和 slot 的真实运行测试通过；
- ConfigPack 重载后使用新模块重新编译，不复用旧代际脚本实例。

### 10.3 编译命令

Windows 11 使用 JDK 21：

```powershell
$env:JAVA_HOME='C:\Program Files\Java\jdk-21'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
.\gradlew.bat --no-daemon build
```

如果出现 `Unable to establish loopback connection`，追加：

```powershell
-Djdk.net.unixdomain.tmpdir=C:/jtmp
```

资源修改完成后还要执行项目现有的 ConfigPack 全量编译测试，并在测试客户端执行一次强制 ConfigPack 重载。

## 11. 实施顺序

建议按一个原子迁移分支完成，顺序如下：

1. 保存并确认当前未提交的 Snapshot DTO 公共化改动；
2. 将剩余 9 个内部类型迁移到公共 API；
3. 提取 `FightCoreUIApiCatalog`；
4. 让 `FightCoreUIJavaTypes` 使用公共目录；
5. 注册 core、struct、root 三个虚拟模块；
6. 在 UI 组件和客户端全局脚本两个编译入口安装模块；
7. 修正 ConfigPack `ui/script` 目录；
8. 批量迁移 67 个逻辑脚本；
9. 同步三份资源副本；
10. 更新测试并执行 JDK 21 全量构建；
11. 启动客户端执行 ConfigPack 强制重载和关键 UI 冒烟验证。

不建议拆成“先改脚本、后补宿主”的可发布提交，因为旧客户端运行时无法解析新模块；也不建议先发布包含新模块但仍保留全部逐类 import 的长期双轨版本。

## 12. 验收标准

全部满足才算迁移完成：

- [ ] UI 业务脚本统一使用 `import "@fightcore/ui.api"`；
- [ ] 按需 `nova.*` 标准库导入仍然保留；
- [ ] 两类客户端 Nova 运行时都安装相同模块目录；
- [ ] JavaTypes 与模块导出来自同一类型清单；
- [ ] UI 脚本不再依赖 `module.ui.*` 和 `core.*` 实现包；
- [ ] Snapshot DTO 位于公共 API；
- [ ] `java.util.ZeList` 错误导入已经删除；
- [ ] `config/script` 与 `config/ui` 目录职责正确；
- [ ] 三份 ConfigPack 资源不再漂移；
- [ ] 模块缺失会明确失败，没有 fallback；
- [ ] 不存在旧包转发、typealias 或双注册兼容代码；
- [ ] FightCore Mod 使用 JDK 21 全量编译和测试通过；
- [ ] ConfigPack 强制重载后关键 UI 实际可用。

## 13. 预计改动规模

| 层级 | 预计规模 |
| --- | --- |
| NovaLang 生产代码 | 0 |
| FightCore 宿主与模块目录 | 3～5 个 Kotlin 文件 |
| 公共 API 清理 | 7～15 个 Kotlin 文件 |
| 逻辑脚本 | 67 个 |
| 三处物理脚本副本 | 91 个 |
| 测试 | 3～4 个 |
| 总物理文件 | 约 100～115 个 |

改动量主要来自资源副本和机械 import 替换，真正需要人工设计评审的是公共 API 边界、两个运行时的模块安装以及 ConfigPack 顶层目录修正。

## 14. Creator 对照审计

审计目录：

```text
plugins/Creator
```

Creator 已经采用 Nova Workspace 的正式模块图机制，可以作为 FightCore 迁移的结构参照，但它目前只完成了业务模块化：宿主 Java 类型仍散落在各个公共 Nova 文件中。服务器部署副本还存在一处漏导入和一组资源归属问题。

### 14.1 已经正确实现的部分

Creator 的 `script/nova.config.yml` 声明了：

```yaml
aliases:
  "@creator": "."
  "@nova": "../../NovaLang/libs"
sources:
  - "."
  - "../../NovaLang/libs"
entries:
  - "@creator/main"
```

`SimpleAction.buildGeneratedSource()` 会为每个 YAML 虚拟脚本自动添加：

```nova
import "@creator/main"
```

`CreatorScriptManager` 再把物理模块和 YAML 生成的 `SourceUnit` 一起交给 `RuntimeWorkspace`。`WorkspaceModuleResolver` 只解析入口可达的依赖，并由 `WorkspaceBundleBuilder` 按拓扑顺序合并。因此 Creator 的字符串 import 是实际模块依赖，不是简单把一段 prelude 文本无条件拼到所有文件前面。

这正是“业务脚本导入一个包即可使用公共能力”的正确实现：

- YAML 业务脚本依赖 `@creator/main`；
- `main.nova` 聚合稳定公共模块；
- `lib/*.nova` 已经声明业务模块依赖，但底层 Java 类型尚未完全收口到类型模块；
- `@nova/*` 用于 MythicMobs、Zaphkiel、FightCore、QuestsEngine 等跨插件模块。

Creator 服务器脚本当前共有 39 个 Nova 文件、309 条 import，其中 138 条是字符串模块 import，163 条是 Java import，7 条是 Java static import。Java import 的重复主要集中在公共 `lib` 实现层，不是 YAML 业务层，因此改动范围小于 FightCore UI，但目标仍应是让公共库依赖窄模块，而不是继续逐类依赖宿主实现。

### 14.2 服务器模块图结果

以服务器 `nova.config.yml` 和 `@creator/main` 为入口检查：

| 项目 | 结果 |
| --- | ---: |
| 物理 Nova 模块 | 39 |
| `@creator/main` 可达模块 | 37 |
| Creator 模块依赖边 | 124 |
| 引用的 `@nova/*` 模块 | 5 |
| 缺失的 `@creator/*` 文件 | 0 |
| 缺失的 `@nova/*` 文件 | 0 |

未被 `@creator/main` 直接或间接包含的两个文件是：

```text
@creator/lib/creator.editor.data
@creator/lib/creator.haider.outpost.api
```

`creator.haider.outpost.api` 属于前哨站模板的业务扩展。该模板的动作脚本会按需显式导入它，这个边界是合理的，不应强行并入所有 Creator 动作的公共入口。

### 14.3 确定的漏导入：CreatorEditorData

`creator.editor.data.nova` 声明公共命名空间：

```nova
object CreatorEditorData {
    val pos = CreatorEditorPosition
}
```

但它自身没有导入定义 `CreatorEditorPosition` 的模块：

```nova
import "@creator/lib/creator.editor.data.pos"
```

同时服务器 `main.nova` 直接导入了 `creator.editor.data.pos`，没有导入 `creator.editor.data`。前哨站模板却实际调用：

```nova
CreatorEditorData.pos.location(...)
CreatorEditorData.pos.interpolate(...)
```

Nova Workspace 只合并入口可达模块，不会因为文件位于 `sources` 目录就自动加载。Nova 的动态名称解析也可能让未定义名称通过字节码编译，直到对应业务分支执行才暴露问题。因此“服务器启动成功”不能证明这个命名空间已经进入动作脚本。

正确修正应为：

1. 在 `creator.editor.data.nova` 中导入 `@creator/lib/creator.editor.data.pos`；
2. 在需要公开 `CreatorEditorData` 的聚合入口中导入 `@creator/lib/creator.editor.data`；
3. `main.nova` 不再为了该命名空间直接导入 `.pos`；
4. 内部只使用 `CreatorEditorPosition` 的模块仍显式依赖 `.pos`，不依赖偶然的全局合并顺序；
5. 不保留旧路径、双导入兼容或运行时 fallback。

### 14.4 源码资源与服务器副本已经漂移

Creator 源码仓库位置：

```text
F:/minecraft/bamboos/creator/src/main/resources/script
```

与服务器部署目录对比：

| 项目 | 数量 |
| --- | ---: |
| 源码仓库 Nova 文件 | 28 |
| 服务器 Nova 文件 | 39 |
| 两边同路径文件 | 28 |
| 内容相同 | 13 |
| 内容不同 | 15 |
| 仅服务器存在 | 11 |

15 个同路径但内容不同的文件包括 `main.nova`、`core.nova`、编辑器点位、镜头、倒计时、生命周期、NPC、回合和宝箱等核心公共模块。11 个服务器独有文件则主要是防守设施、玩家设施、回合控制、补给箱和前哨站业务模块。

这组差异不能直接用任意一侧覆盖另一侧。迁移仓库必须先明确：

- Creator 插件仓库负责通用公共模块；
- 部署或副本仓库负责服内专用业务模块；
- 发布流程如何组合两类资源；
- 哪一侧的 15 个同名文件才是当前权威实现。

建议让服内专用模块使用独立 Alias 和聚合入口，避免长期覆盖 `@creator/main` 与通用 `lib` 同名文件。该调整属于资源所有权重构，应一次性切换，不保留两套扫描路径。

### 14.5 当前测试没有覆盖服务器部署资源

Creator 已有 `CreatorNovaWorkspaceCompileTest`，但它明确只编译源码仓库内的：

```text
src/main/resources/script
src/main/resources/template/example.yml
```

它不接受外部资源路径，因此不会覆盖：

- 服务器独有的 11 个模块；
- 15 个已经漂移的同路径模块；
- 前哨站模板中的 `CreatorEditorData` 调用；
- 部署版 `@nova/*` Alias 和跨插件模块闭包。

另一个仓库至少应增加一条部署 Workspace 编译门禁：以实际 `plugins/Creator/script/nova.config.yml` 为配置，加载全部部署模板生成虚拟 `SourceUnit`，并逐入口进行字节码编译和关键公共符号执行验证。仅检查服务器能够启动不够，因为未定义的动态名称可能延迟到业务分支运行时才失败。

### 14.6 Creator 的建议改动范围

Creator 不需要像 FightCore UI 那样批量迁移数十个业务脚本。建议范围如下：

| 优先级 | 改动 | 预计范围 |
| --- | --- | ---: |
| P0 | 在 Creator 源码注册单一 `creator.dungeon` 模块，并迁移源码公共库 | 1 个 Kotlin 模块定义，20 个源码 Nova 文件 |
| P0 | 修正 `creator.editor.data` 对 `.pos` 的依赖，并让公共入口导入正确命名空间 | 2～4 个 Nova 文件 |
| P0 | 增加部署资源与真实模板的 Workspace 编译/执行门禁 | 1～3 个测试或构建文件 |
| P1 | 明确通用资源与服务器独有 11 个模块的所有权及发布组合方式 | 1 个构建流程，加资源移动 |
| P1 | 审核 15 个漂移文件，逐个保留权威版本 | 15 个文件的人工 diff |
| P1 | 审核剩余 Workspace、Bukkit 及第三方插件直接 import | 不并入 `creator.dungeon` |

不建议进行以下改动：

- 把 Creator、Bukkit、Workspace、JDK 和全部第三方类型塞进一个巨型聚合模块；
- 把前哨站等单模板能力并入所有动作默认加载的 `@creator/main`；
- 依赖未声明模块恰好被别的入口先编译；
- 继续人工维护源码仓库和服务器目录两份同名公共模块；
- 为缺失模块增加静默 fallback 或兼容转发层。

### 14.7 对 FightCore 迁移的参考结论

FightCore 应复用 Creator 的三个设计点：

1. 业务脚本只依赖一个稳定聚合入口；
2. 宿主在编译前注册或解析真实模块图；
3. 每个公共库显式导入能力所有者提供的逻辑模块，由模块集中维护底层 Java import；同一 Creator 地牢领域不继续细拆大量小模块。

FightCore 不应复制 Creator 当前的三个部署问题：

1. 聚合入口漏掉业务实际使用的公共命名空间；
2. 通用资源和服内扩展以同名文件长期漂移；
3. 测试只覆盖源码示例，不覆盖真实部署资源和模板。

### 14.8 `creator.dungeon` ModuleLoader 模块迁移

`DungeonContext` 能被插件 ClassLoader 解析，只能说明当前脚本可以运行，不能代替模块设计。Creator 不需要为了普通 Java 类补 `JavaTypes`，而应通过 ModuleLoader 注册一个逻辑模块：

```nova
import "creator.dungeon"
```

这里不能写成 `@nova/creator.dungeon` 或 `@creator/creator.dungeon`。带 `@` 的标识属于 Workspace 文件 Alias；`creator.dungeon` 是 ModuleLoader 中直接注册的稳定模块 ID，不对应业务脚本可见的物理路径。

权威实现以 Creator 源码仓库为准：

```text
F:/minecraft/bamboos/creator
```

`src/main/resources/script` 中 Creator 自身类型的直接依赖为：

| 类型组 | Java import | 唯一类型 | 涉及文件 |
| --- | ---: | ---: | ---: |
| `com.gitee.creator.core.dungeon.*` | 37 | 12 | 20 |
| `com.gitee.creator.api.event.*` | 3 | 3 | 1 |
| 其他 `com.gitee.creator.core.*` | 1 | 1 | 1 |
| 合计 | 41 | 16 | 20 |

服务器部署副本比源码多11个 Nova 文件，并增加了 `FeatureHealth.State` 与 `DungeonContextHealthEvents.StateUpdate` 两个 Creator 类型。为了让源码模块同时覆盖正式部署扩展，`creator.dungeon` 使用源码与服务器副本的并集，共18个类型；不再拆成 editor、entity、objective、event 等子模块。模块源码统一包含：

```nova
import java com.gitee.creator.core.dungeon.DungeonContext
import java com.gitee.creator.core.dungeon.DungeonLoader
import java com.gitee.creator.core.dungeon.Vec3d
import java com.gitee.creator.core.common.PositionMapping
import java com.gitee.creator.core.dungeon.editor.EditorDataRepository
import java com.gitee.creator.core.dungeon.feature.entity.EntityCreator
import java com.gitee.creator.core.dungeon.feature.entity.EntityCreator.Argument
import java com.gitee.creator.core.dungeon.feature.entity.FeatureEntityManager
import java com.gitee.creator.core.dungeon.feature.FeatureHealth
import java com.gitee.creator.core.dungeon.feature.FeatureHealth.State
import java com.gitee.creator.core.dungeon.feature.objective.CreatorObjectiveTrigger
import java com.gitee.creator.core.dungeon.feature.objective.ObjectiveEventBus
import java com.gitee.creator.core.dungeon.feature.objective.ObjectiveTypeRegistry
import java com.gitee.creator.core.dungeon.feature.objective.ScriptObjectiveDefinition
import java com.gitee.creator.api.event.DungeonContextCuboidEvent
import java com.gitee.creator.api.event.DungeonContextCuboidEvent.Pre
import java com.gitee.creator.api.event.DungeonContextCuboidEvent.Post
import java com.gitee.creator.api.event.DungeonContextHealthEvents.StateUpdate
```

原公共库：

```nova
import java com.gitee.creator.core.dungeon.DungeonContext

fun currentContext(): DungeonContext {
    // ...
}
```

迁移后：

```nova
import "creator.dungeon"

fun currentContext(): DungeonContext {
    // ...
}
```

#### RuntimeWorkspace 接入

普通 `Nova` 编译入口在编译前调用：

```kotlin
nova.registerModule("creator.dungeon", creatorDungeonModuleSource)
```

Creator 当前使用 `RuntimeWorkspace`。Workspace 会先解析完整依赖图，再创建 `Nova` 和调用 Host 安装逻辑，因此不能只在 `WorkspaceHost.install(nova)` 中注册模块，否则解析器会先报告无法解析 `creator.dungeon`。

模块定义应由 Creator 源码所有，建议新增：

```text
src/main/kotlin/com/gitee/creator/core/script/CreatorDungeonModule.kt
```

该文件只声明一个顶层 `object CreatorDungeonModule`，统一维护：

- 模块 ID `creator.dungeon`；
- 包含18个 Java import 的模块源码；
- 创建非入口 `SourceUnit` 的方法。

不要把该模块放到服务器可人工覆盖的 `plugins/Creator/script` 资源目录，也不要放入 `@creator` 或 `@nova` Alias。它是 Creator 插件版本绑定的宿主模块，必须随 Creator 代码发布。

正确接入方式是在 `RuntimeWorkspace.load()` 前，把同一个模块源码登记为非入口虚拟 `SourceUnit`：

```kotlin
val source = SourceUnit(
    "creator.dungeon",
    creatorDungeonModuleSource,
    null,
    null,
    1,
    0,
    null,
)
workspace.registerVirtualSource(source, false)
```

`WorkspaceModuleResolver` 会优先按模块 ID 命中该虚拟源码；`RuntimeWorkspace` 构建完依赖图后会把图内模块统一注册到 Nova ModuleLoader。因此 Workspace 和普通 `Nova` 编译入口都使用完全相同的 `creator.dungeon` ID 与源码。

在 `CreatorScriptManager.createWorkspace()` 中，必须先登记 `CreatorDungeonModule`，再登记 YAML 动作：

```kotlin
workspace.registerVirtualSource(CreatorDungeonModule.sourceUnit(), false)
for (action in SimpleAction.registeredActions()) {
    val source = action.toSourceUnit()
    workspace.registerVirtualSource(source, true)
}
```

现有 `CreatorNovaWorkspaceCompileTest` 直接调用 `WorkspaceModuleResolver.resolve(config)`，迁移后必须通过统一测试辅助入口始终附加 `CreatorDungeonModule.sourceUnit()`。物理入口、普通虚拟动作和模板虚拟动作三条编译路径都要覆盖，不能只改生产 `CreatorScriptManager`。

迁移约束：

1. Creator 源码中的20个公共 Nova 文件只要使用任一上述类型，就显式写 `import "creator.dungeon"`；服务器扩展仓库另迁移10个文件；
2. 不再按 editor、entity、objective、event 拆分 Creator 子模块；
3. 业务模板仍只导入 `@creator/main`，不直接依赖 `creator.dungeon`；
4. `@creator/main` 继续聚合 Creator 业务 API，不能变成 Java 类型清单；
5. 18个 Creator Java 类型的包路径只保留在 `creator.dungeon` 模块源码中；
6. Bukkit、Nova Workspace、FightCore、HaiderCore、Malkuth、WorldEdit 等非 Creator 类型不并入 `creator.dungeon`；
7. 不安装仅用于重复声明普通 Java 类的 Creator JavaTypes；
8. 模块缺失、重复注册或类不可见时直接加载失败，不回退到原逐类 import。

源码迁移完成后，41处 Creator 直接 Java import 收口到一个 `creator.dungeon` 模块，20个公共库文件改为单行 ModuleLoader import。服务器扩展仓库同步迁移后，整体覆盖53处直接 import、18个类型和30个公共库文件。
