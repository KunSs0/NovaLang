# Nova Runtime Workspace 设计

## 1. 目标

新增独立通用模块 `nova-runtime-workspace`，统一处理 Nova 脚本的模块解析、编译缓存、执行隔离、资源作用域和实例销毁。

Workspace 不持有调度器。调度器继续使用 Nova 全局单例；Workspace 只记录任务、监听器、RPC、订阅等资源的所属作用域，并在作用域销毁时释放资源。

本模块不提供 JavaScript 兼容层、隐式 Prelude 或运行失败后的 fallback。

## 2. 模块边界

`nova-runtime-workspace` 负责：

- 读取 Workspace 根目录的 `nova.config.yml`。
- 解析模块并构建完整依赖图。
- 编译文件模块及宿主生成的虚拟源。
- 为每次执行安装隔离绑定和当前资源作用域。
- 管理 Generation、业务实例、阶段和单次调用等子作用域。
- 提供归属于当前作用域的稳定回调、调度任务和严格配置读取。
- 在 shutdown、业务卸载或上层重载时递归 dispose。
- 将编译及运行诊断映射回原始来源。

`nova-runtime-workspace` 不负责：

- 扫描并猜测业务目录。
- 解释具体业务的 YAML 结构。
- 创建第二套调度器。
- 保存宿主持久状态。
- 自动补全未声明的依赖或 Alias。

接入方负责提供入口、必要的类加载环境和虚拟源适配器。通用运行时能力由 Workspace 模块提供，不要求各接入方重复实现 Host/Factory。

### 2.1 Bukkit 平台宿主

`nova-bukkit` 构建为可独立安装的 `NovaLang` 插件，完整携带 Workspace、编译器和运行时。它只负责在 `STARTUP` 加载阶段注册进程级唯一 Bukkit 调度器，并在停服时注销；不创建、不重载也不保存任何业务 Workspace。

Bukkit 接入方必须硬依赖 `NovaLang`，编译期以 `compileOnly` 引用 `nova-runtime-workspace`；使用通用 Bukkit 事件入口时引用 `nova-bukkit`。每个接入方自行创建 Workspace，并在卸载或 reload 时先 `dispose()` 旧实例，再创建全新实例。接入方不得打包 Nova 运行时、重复注册调度器或在 NovaLang 缺失时回退到其他脚本引擎。

## 3. Workspace 配置

每个 Workspace 根目录只允许一个 `nova.config.yml`：

```yaml
version: 1
name: example-workspace

aliases:
  "@": "../.."
  "@self": "."
  "@shared-api": "../../shared/libs"

sources:
  - "."
  - "../../shared/libs"

entries:
  - "scripts/main.nova"

runtime:
  security: trusted-server
  thread: main
```

### 3.1 Alias 强约束

Alias 必须以 `@` 开头，合法格式为：

```text
^@(?:[A-Za-z][A-Za-z0-9._-]*)?$
```

`@`、`@self` 和 `@shared-api` 合法；`shared`、`$shared` 和 `foo@bar` 非法。发现非法 Alias 时必须终止 Workspace 加载，不允许忽略或自动补充 `@`。

### 3.2 解析规则

- Alias 仅匹配完整名称或名称后的 `/` 边界。
- 多个 Alias 同时匹配时使用最长名称。
- Alias 目标和 `sources` 均相对配置文件所在目录解析。
- 规范化后的文件必须位于声明的 source roots 内。
- 禁止通过 `..`、符号链接或盘符切换逃逸 source roots。
- 无扩展名导入固定映射为 `.nova`，不探测 `.js`、目录 `index` 或其他候选文件。
- `./` 和 `../` 相对当前模块解析，不参与 Alias 翻译。
- Nova 标准库及 Java 类导入不参与 Alias 翻译。
- 不读取或继承子目录中的其他 `nova.config.yml`。

```nova
import "@/shared/libs/core.api"
import "@self/lib/business.api"
```

## 4. 模块与依赖图

所有依赖必须显式 `import`，不在函数执行前注入或执行 Prelude。

Workspace 首次加载时构建不可变模块图；解释执行和字节码编译必须共用同一解析结果。模块标识使用规范化后的逻辑路径，物理路径只用于读取、诊断和安全校验。

依赖建议分为：

1. 纯 Nova 模块：不依赖宿主状态的通用逻辑。
2. Host API 模块：由宿主注入的稳定服务门面。
3. 资源 API 模块：会创建任务、监听器、RPC 或订阅等资源的能力。

未显式导入、未注入或宿主能力缺失时，编译或加载必须直接失败。

### 4.1 Workspace 配置文件

`WorkspaceConfigFiles.loadYaml(relativePath)` 仅允许读取 `nova.config.yml` 所在目录内的 `.yml` 或 `.yaml` 文件。绝对路径、根目录逃逸、重复 YAML key、非映射根节点和缺失文件均直接失败。

加载结果为 `WorkspaceConfigDocument`。读取接口执行严格类型检查，不把字符串、数字和布尔值相互隐式转换。编译模式下可通过 `WorkspaceConfigFiles.readString/readInt/readDouble/readBoolean/readStringList` 静态入口读取已加载文档，避免重复解析文件。

## 5. 源单元

Workspace 统一接收 `SourceUnit`，但不关心源码来自文件还是业务配置：

```text
SourceUnit
├─ moduleId
├─ sourceText
├─ originFile
├─ originPath
├─ originLine
└─ generatedLineOffset
```

普通 `.nova` 文件由默认文件加载器生成 SourceUnit；YAML、数据库或网络配置由业务适配器生成虚拟 SourceUnit。编译错误和运行异常必须通过来源信息定位到原文件及业务路径。

## 6. 作用域与资源归属

```text
Workspace
└─ Generation
   ├─ Persistent Registration
   ├─ Business Instance
   │  └─ Stage
   └─ Invocation
```

- `Generation`：一次完整加载代际，拥有模块图、程序和常驻注册资源。
- `Business Instance`：宿主业务实例对应的脚本资源边界。
- `Stage`：业务实例内部可单独切换和销毁的阶段。
- `Invocation`：一次调用的绑定、临时任务和临时回调。

资源 API 必须读取当前执行上下文中的 `ResourceScope` 并自动登记，不能依赖业务脚本逐个实现 reload 清理。宿主持久状态不属于 Workspace 资源表。

原生 `schedule/scheduleRepeat` 在 Workspace 调用中会自动登记到当前 Scope；一次性任务完成后解除登记，循环任务在 Scope dispose 时取消。`BukkitWorkspaceEvents.listen` 创建的监听器同样自动登记，并在 dispose 时注销。

## 7. 回调与并发

宿主不得长期保存解释器内部函数对象，只能保存稳定的 `NovaCallback`：

```text
NovaCallback = Program + Generation + ResourceScope + CapturedBindings
```

调用规则：

- Generation 或 ResourceScope 已 dispose 时拒绝执行。
- 每次调用重新安装独立执行绑定。
- 跨线程调用显式传播执行上下文，不依赖线程残留的 ThreadLocal。
- 必须同步返回的宿主事件在其调用线程内完成。
- 无状态脚本可并发执行；带可变状态的脚本按 Business Instance 串行执行。

执行策略固定为 `MAIN_THREAD`、`CALLER_THREAD`、`PARALLEL_SAFE`、`SERIAL_SCOPE`，不根据异常自动切换线程。

## 8. Dispose 与上层重载

Workspace 自身不提供 `reload()`，也不存在 `RELOADING` 中间状态。重载属于上层业务编排：

1. 上层停止向旧 Workspace 路由新调用。
2. 调用旧 Workspace 的 `dispose()`。
3. 当前 Generation 进入 disposing 状态并等待正在执行的同步回调退出。
4. 递归 dispose Invocation、Stage、Business Instance 和 Generation。
5. 注销监听器、RPC、Provider、订阅并取消任务。
6. 清除程序、回调、模块图和代际 ClassLoader 引用。
7. 上层创建并加载全新的 Workspace，再切换业务引用。

新 Workspace 加载失败时不得恢复或复用已销毁的旧 Workspace。业务实例需要跨重载存续时，由宿主保存身份和持久状态，并在新 Workspace 中创建新的脚本实例作用域；不得复用旧脚本对象。

## 9. 核心接口

```text
RuntimeWorkspace
├─ load()
├─ openScope(type, ownerId)
├─ invoke(entry, bindings, scope)
└─ dispose()

WorkspaceCallbacks
└─ create(entry, function, policy?)

schedule
└─ schedule(delayMs, block)

scheduleRepeat
└─ scheduleRepeat(delayMs, periodMs, block)

WorkspaceConfigFiles
├─ loadYaml(relativePath)
└─ read*(document, path, defaultValue?)

BukkitWorkspaceEvents
└─ listen(eventType, priority, ignoreCancelled, entry, function, policy?)

WorkspaceGeneration
├─ moduleGraph
├─ compiledPrograms
├─ rootScope
└─ state
```

`RuntimeWorkspace` 可由各插件分别实例化，调度器、编译器服务和宿主能力注册表使用进程级单例注入。Workspace 之间不共享 Generation、资源作用域或业务绑定。

## 10. 实现前置项

- 支持 Alias 和多 source roots 的统一模块解析器。
- 解释器与字节码编译共用模块依赖图。
- ResourceScope 执行上下文及资源自动登记。
- 可跨线程、可失效、可释放的 NovaCallback。
- Workspace 模式禁用全局名称 fallback。
- Workspace 级编译缓存与 ClassLoader 释放。
- 虚拟源和 Source Map 诊断。
- Workspace 使用全局调度器；未安装调度器时直接加载失败。

具体服务端工程、脚本来源、迁移顺序和业务验收标准由服务端部署仓库维护。
