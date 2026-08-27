# Nova Runtime Workspace 任务进度快照

> 快照日期：2026-08-27
>
> 用途：换电脑或切换开发环境后，从本文件恢复本次任务的背景、已完成内容、验证结果和下一步工作。

## 1. 任务背景

本次任务的目标是为 NovaLang 增加服务端可复用的 Runtime Workspace 能力，并完成 Bukkit 平台接入。Workspace 用来统一管理脚本模块、编译程序、执行上下文、资源作用域、回调、调度任务和生命周期；Bukkit 只作为平台宿主，提供全局调度器和事件适配，不保存业务插件自己的 Workspace 实例。

任务还包含一项 Java 互操作核查：确认文档和实现是否支持通过 `import java` 后直接使用 `Location(...)` 这类外部类构造表达式。JDK 示例能够证明 Nova 的通用语法，但不能自动证明 Bukkit 或其他宿主 ClassLoader 中的类在解释器和字节码路径都可用，因此该项必须由回归测试分别验证。

## 2. 当前总体状态

截至快照生成时，代码改动仍在工作区，尚未提交或推送。当前分支为 `main`，基线提交为 `418be6c`（`docs: 添加最小 Hello World 脚本示例`）。工作区的改动均围绕以下同一任务范围：

1. 新增 `nova-runtime-workspace` 模块及其完整测试。
2. 加强进程级调度器的安装、唯一性和注销语义。
3. 为 Nova 运行时增加模块注册和字节码隔离调用能力。
4. 将 `nova-bukkit` 改造成可独立部署的 NovaLang Bukkit/Paper 平台插件，并接入 Workspace。
5. 更新根项目模块清单、版本、README 和设计文档。
6. 增加外部 ClassLoader Java 类的直接构造回归测试，记录当前字节码编译路径缺口。

没有删除失败测试，也没有把失败测试标记为通过。编译路径缺口必须在后续修复或明确关闭前保持可见。

## 3. 已实现内容

### 3.1 `nova-runtime-workspace` 模块

新增独立 Gradle 模块，并在 `settings.gradle` 中注册。主要能力如下：

- `RuntimeWorkspace`：Workspace 的加载、入口执行、作用域打开和销毁。
- `WorkspaceConfig`、`WorkspaceConfigLoader`、`WorkspaceConfigDocument`：加载和表示严格的 `nova.config.yml` 配置。
- `WorkspaceConfigFiles`：对 Workspace 根目录内 YAML 文件做安全路径检查，并提供严格类型读取。
- `WorkspaceModuleResolver`、`WorkspaceModuleGraph`、`WorkspaceModule`：解析 Alias、source roots 和显式 import，构建不可变依赖图。
- `SourceUnit`、`MappedSourceLocation`、`WorkspaceSourceMap`：为普通文件和虚拟源码保留原始来源及诊断映射。
- `WorkspaceGeneration`、`GenerationState`：表示一次完整加载代际，负责程序和根资源的生命周期。
- `ResourceScope`、`ResourceScopeState`、`ScopeType`：表示 Generation、业务实例、阶段和单次调用等嵌套作用域。
- `WorkspaceExecutionContext`、`WorkspaceExecutionDispatcher`、`ExecutionPolicy`：传播执行上下文，支持主线程、调用线程、并行安全和作用域串行策略。
- `NovaCallback`、`WorkspaceCallbacks`：保存稳定的入口/函数引用，在 Generation 或 Scope 销毁后自动失效。
- `WorkspaceTasks`：创建归属于当前作用域的延迟任务和循环任务，并在作用域销毁时清理。
- `WorkspaceHost`、`WorkspaceProgram`、`WorkspaceBundle`、`WorkspaceBundleBuilder`：抽象宿主绑定、模块程序和加载产物。

设计边界是：Workspace 不创建第二套调度器，不提供隐式 Prelude，不做未声明依赖的 fallback，也不提供自身的 `reload()`。上层重载必须销毁旧实例，再创建和加载新实例。

### 3.2 调度器和运行时

- `SchedulerHolder` 改为进程级唯一调度器持有者：拒绝空值和不同实例覆盖；提供明确的 `clear()` 清理入口。
- `BukkitSchedulers` 增加参数检查、唯一注册、所有者校验和注销流程。
- `BukkitNovaScheduler` 补充完整 API/KDoc，并保证 Bukkit tick 延迟和周期不会变成零。
- `Nova.registerModule(moduleId, source)` 支持宿主注册字符串 import 可引用的虚拟 Nova 模块。
- `CompiledNova.callIsolated(...)` 为共享字节码程序提供每次调用独立绑定，避免并发服务端调用互相覆盖上下文。
- `IncrementalCompiler` 和部分运行时错误信息改为 UTF-8 可读的英文文本，避免原有乱码信息继续扩散。

### 3.3 Bukkit 平台插件

`nova-bukkit` 现在构建为可独立部署的 `NovaLang` 插件：

- `NovaBukkitPlugin` 在 `STARTUP` 阶段注册唯一 Bukkit 调度器，在停服阶段注销。
- `BukkitWorkspaceEvents` 提供将 Bukkit Listener 绑定到当前 Workspace ResourceScope 的适配入口。
- `plugin.yml` 声明平台插件元数据和 `${version}` 展开。
- Shadow JAR 携带 Workspace、编译器和运行时，重定位可能冲突的第三方依赖。
- `verifyPluginJar` 检查插件元数据、主类、Workspace 核心类、任务支持、配置支持和 Bukkit 事件支持是否进入可部署 JAR。

业务插件必须硬依赖 NovaLang 平台插件，使用 `compileOnly` 消费平台提供的运行时；业务插件自行创建和销毁 Workspace，不得重复打包 Nova 运行时或重复注册调度器。

## 4. 文档和配置改动

- `docs/设计/nova-runtime-workspace.md`：Workspace 的模块边界、配置、Alias 规则、资源作用域、回调并发、dispose 流程和 Bukkit 接入约束。
- `README.md`、`README_CN.md`：版本更新到 `0.2.0`，加入 `nova-runtime-workspace`，说明 Bukkit 现在是独立平台插件，并链接 Workspace 设计文档。
- `build.gradle`：根项目版本更新到 `0.2.0`。
- `settings.gradle`：纳入 `nova-runtime-workspace`。
- `nova-bukkit/build.gradle`：加入 Shadow、插件资源展开、发布配置、JAR 验证和构建依赖。

## 5. 验证结果

### 5.1 Workspace 测试

已有 Gradle 测试结果显示 `nova-runtime-workspace` 的 `RuntimeWorkspaceTest` 共 17 个测试全部通过（失败数 0）。覆盖内容包括：

- 跨目录模块解析和入口执行。
- Alias/source root 安全约束。
- 虚拟 SourceUnit 及 YAML 行号映射。
- 每次调用的绑定隔离。
- Generation、业务作用域、Stage 和 Invocation 的资源销毁。
- 同一作用域串行执行。
- 稳定回调在 Workspace 销毁后失效。
- 缺少全局调度器或异步执行器时直接失败。
- 编译失败进入不可重试的 FAILED 状态。
- 通过销毁旧实例并创建新实例完成上层重载。

### 5.2 外部 ClassLoader 直接构造回归测试

新增文件：

`nova-runtime/src/test/java/com/novalang/runtime/codegen/ExternalJavaImportConstructorTest.java`

测试动态编译一个独立的 `dynamic.ConstructorFixture`，通过仅包含该测试类目录的 `URLClassLoader` 加载，然后分别验证：

```nova
import java dynamic.ConstructorFixture
val fixture = ConstructorFixture(42)
fixture.getValue()
```

当前结果为：

- 解释器路径：通过。
- 字节码路径：失败，`NoClassDefFoundError: dynamic/ConstructorFixture`，根因是 `NovaIrCompiler$NovaClassLoader` 没有沿用脚本设置的外部 ClassLoader。

因此当前不能据此宣称 Bukkit `Location(...)` 已经在编译模式可用。已有文档只证明“导入 Java 类后直接构造”是通用语法；没有找到直接以 `Location()` 为例的文档或测试，也没有证明外部 Bukkit ClassLoader 在字节码路径能被解析。

### 5.3 相关测试保留原则

`ExternalJavaImportConstructorTest` 的失败状态是本次任务的重要验收证据，后续修复时应保留该测试并让解释器、字节码两个用例都通过。不得删除测试、跳过测试或增加系统级 fallback 来掩盖类加载路径问题。

## 6. 已知缺口和风险

1. **外部类的字节码 ClassLoader 传递未完成**：需要检查 `NovaIrCompiler` 创建 `NovaClassLoader` 的路径，将脚本配置的外部 ClassLoader 纳入类查找链，同时验证类隔离和资源释放。
2. **Bukkit `Location(...)` 尚未验证**：在通用外部构造回归测试修复后，再增加或运行 Bukkit API 夹具/集成测试；不要把 JDK 构造示例直接外推为 Bukkit 结论。
3. **完整多模块构建需在换机后重跑**：尤其是 `nova-bukkit:build`、`verifyPluginJar`、Workspace 测试和 runtime 全量测试。
4. **工作区提交尚未完成**：提交时需检查是否有构建目录或其他明显无关文件被加入；`build/` 产物应继续由 `.gitignore` 忽略。

## 7. 换机后的建议继续顺序

1. 读取本快照和 `docs/设计/nova-runtime-workspace.md`。
2. 执行 `git status --short --branch`，确认提交前文件范围仍与本快照一致。
3. 先定位 `NovaIrCompiler$NovaClassLoader` 和 `setScriptClassLoader` 的关系，修复并保留 `ExternalJavaImportConstructorTest`。
4. 运行外部构造回归测试，确认解释器和字节码都通过。
5. 运行 Workspace、runtime 和 Bukkit 的相关 Gradle 编译/测试任务，并检查 `verifyPluginJar`。
6. 再决定是否将业务侧 `Java.new("org.bukkit.Location", ...)` 迁移为 `Location(...)`；迁移前必须有 Bukkit 具体回归测试和文档依据。
7. 完成审查后提交中文 Conventional Commit，并将当前分支推送到 `origin`。

## 8. 当前工作区文件范围摘要

### 已修改的受跟踪文件

```text
README.md
README_CN.md
build.gradle
nova-bukkit/build.gradle
nova-bukkit/src/main/java/com/novalang/bukkit/BukkitNovaScheduler.java
nova-bukkit/src/main/java/com/novalang/bukkit/BukkitSchedulers.java
nova-compiler/src/main/java/com/novalang/compiler/compiler/IncrementalCompiler.java
nova-runtime-api/src/main/java/com/novalang/runtime/SchedulerHolder.java
nova-runtime/src/main/java/com/novalang/runtime/CompiledNova.java
nova-runtime/src/main/java/com/novalang/runtime/Nova.java
nova-runtime/src/test/java/com/novalang/runtime/interpreter/NovaSchedulerTest.java
settings.gradle
```

### 新增的任务文件

```text
docs/设计/nova-runtime-workspace.md
docs/设计/nova-runtime-workspace-进度快照.md
nova-bukkit/src/main/java/com/novalang/bukkit/BukkitWorkspaceEvents.java
nova-bukkit/src/main/java/com/novalang/bukkit/NovaBukkitPlugin.java
nova-bukkit/src/main/resources/plugin.yml
nova-runtime-api/src/test/java/com/novalang/runtime/SchedulerHolderTest.java
nova-runtime-workspace/
nova-runtime/src/test/java/com/novalang/runtime/CompiledNovaIsolatedCallTest.java
nova-runtime/src/test/java/com/novalang/runtime/codegen/ExternalJavaImportConstructorTest.java
```

本摘要用于换机后的快速核对；如果 `git status` 出现不在上述范围内的文件，应先单独审查，不要无条件加入任务提交。
