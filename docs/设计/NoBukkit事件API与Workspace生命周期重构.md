# NoBukkit 事件 API 与 Workspace 生命周期重构提案

## 1. 文档状态

本文档是设计提案，仅用于确认 API 边界和生命周期模型。本阶段不修改 Java、Nova、构建脚本或服务端脚本。

当前实现继续使用 `BukkitWorkspaceEvents`，待本文档中的边界确认后再实施重构。重构实施时不保留旧的字符串函数名回调或隐式兼容入口。

## 2. 背景

当前 Bukkit 事件入口位于 `nova-bukkit` 模块的 `BukkitWorkspaceEvents`：

```text
BukkitWorkspaceEvents.listen(...)
    -> WorkspaceExecutionContext.requireScope()
    -> WorkspaceCallbacks.createDirect(...)
    -> PluginManager.registerEvent(...)
    -> ResourceScope.register(...)
```

该实现解决了 Workspace 场景的几个问题：

- `::函数名` 在编译期解析为 `BukkitEventListener`，不再使用字符串函数名。
- 回调执行时恢复 Generation、ResourceScope、绑定和执行策略。
- Workspace 或 Scope 销毁时自动注销 Bukkit 监听器。

但当前入口有两个边界问题：

1. `listen()` 强制要求当前线程存在 Workspace，普通 `Nova` 场景无法使用。
2. Bukkit 注册所有者固定为 `NovaBukkitPlugin`，没有使用实际业务插件的 `JavaPlugin`，业务插件禁用时无法依赖 Bukkit 的插件所有者清理语义。

因此需要将“Bukkit 注册所有权”和“Workspace 资源所有权”拆开，并通过 `NoBukkit.event` 提供统一的脚本门面。

## 3. 设计目标

1. `NoBukkit` 可以在普通 Nova 场景使用，不依赖 Workspace。
2. Workspace 场景可以自动绑定当前 Generation 和 ResourceScope。
3. Bukkit 注册始终绑定实际的 `Plugin`/`JavaPlugin` 所有者。
4. 事件回调继续使用编译期检查的 `::函数名`，禁止字符串函数名。
5. Workspace 不依赖 Bukkit，Bukkit 适配器反向依赖 Workspace 生命周期接口。
6. 事件注册句柄可手动 `dispose()`，Workspace 场景仍由 Scope 自动清理。
7. 多个 Workspace 并存时不会通过全局可变状态串联到错误的 Workspace。
8. 不通过空参数、异常捕获或运行时 fallback 隐式切换注册模式。

## 4. 非目标

- 不把 Bukkit 类型和 Bukkit 事件注册器放入 `nova-runtime-workspace`。
- 不让 Nova 脚本持有 `RuntimeWorkspace` 并调用其 `dispose()`、`openScope()` 或任意入口。
- 不让 `NoBukkit` 维护进程级 Workspace 列表并代替业务插件编排重载。
- 不恢复 `entryName`、`functionName` 字符串回调。
- 不用 `Object` 或动态函数类型取代 `BukkitEventListener` 的 SAM 编译检查。

## 5. 两种所有权

事件注册必须同时考虑两个独立的所有者：

| 所有者 | 负责内容 | 销毁入口 |
| --- | --- | --- |
| Bukkit `Plugin` | `PluginManager.registerEvent` 的注册归属、插件禁用时的 Bukkit 清理 | Bukkit 插件生命周期 |
| Workspace `Generation`/`ResourceScope` | Nova 执行上下文、绑定快照、执行策略、脚本资源树 | `RuntimeWorkspace.dispose()` 或 `ResourceScope.dispose()` |

两者不是替代关系：

```text
Plugin 负责“这个监听器属于哪个 Bukkit 插件”
Workspace 负责“这个 Nova 回调属于哪个脚本代际和资源作用域”
```

Workspace 模式下，一个事件注册同时绑定二者。任一方先销毁时，注销操作必须幂等；Workspace 不能因为 Bukkit 已经清理监听器而继续调用失效回调。

## 6. 公开 API 方向

### 6.1 脚本门面

脚本侧只看到 NoBukkit，不直接导入 `BukkitWorkspaceEvents` 或 `RuntimeWorkspace`：

```nova
NoBukkit.event.listen(
    "org.bukkit.event.player.PlayerJoinEvent",
    MONITOR,
    false,
    ::onPlayerJoin
)
```

如果项目确定使用 `handle` 作为注册动作名称，可以使用：

```nova
NoBukkit.event.handle(
    "org.bukkit.event.player.PlayerJoinEvent",
    MONITOR,
    false,
    ::onPlayerJoin
)
```

但 `handle` 同时也是 `BukkitEventListener.handle(Event)` 的回调方法名，容易产生“注册监听”与“执行回调”的语义混淆。推荐最终公开名称为 `listen` 或 `register`，`handle` 保留给 SAM 回调方法。

### 6.2 Bukkit Plugin 所有者

NoBukkit 必须知道实际注册所有者，但不建议每一条脚本调用都传入 `JavaPlugin`。宿主在安装 NoBukkit API 时绑定 Plugin：

```text
NoBukkitBinding(plugin)
    -> NoBukkit.event.listen(...)
```

业务插件创建 Workspace 时，将自己的 `JavaPlugin` 绑定到该 Workspace 的 Nova 宿主 API。普通 Nova 场景也通过同一绑定方式提供 Plugin，但不提供 Workspace 上下文。

禁止把 NovaLang 平台插件作为所有业务监听器的统一所有者，除非该监听器确实属于 NovaLang 平台本身。

### 6.3 宿主侧显式注册器

如果宿主需要在脚本执行之外注册监听器，应使用显式注册器，不依赖当前线程上下文：

```text
BukkitEventRegistrar.forPlugin(plugin)
BukkitEventRegistrar.forWorkspace(plugin, workspaceOrScope)
```

这两个入口的模式必须明确，不使用 `workspace == null` 表示隐藏的 standalone fallback。注册器返回 `EventRegistration`，该句柄实现 `dispose()`。

## 7. 运行模式

### 7.1 非 Workspace 模式

适用于普通 `Nova` 或宿主直接使用 Bukkit API 的场景：

```text
NoBukkitBinding(plugin)
    -> resolve event class
    -> registerEvent(..., plugin, ...)
    -> return EventRegistration
```

该模式不读取 `WorkspaceExecutionContext`，不创建 `WorkspaceDirectCallback`，不向 ResourceScope 登记资源。调用方必须保存注册句柄，或者依赖 Bukkit 的 Plugin 禁用清理。

非 Workspace 回调的执行语义必须明确为调用线程直接执行，或者由宿主显式提供调度器；不能伪装成 Workspace 回调，也不能在异常后自动切换执行模式。

### 7.2 Workspace 模式

脚本在 Workspace 初始化或后续入口中调用同一个 `NoBukkit.event.listen`：

```text
WorkspaceExecutionContext
    -> currentGeneration()
    -> currentScope()
    -> create WorkspaceDirectCallback
    -> registerEvent(..., plugin, ...)
    -> scope.register(EventRegistration)
```

回调必须经过 Workspace 的执行入口，以恢复：

- Generation 活跃状态。
- ResourceScope 所有权和生命周期状态。
- 调用时绑定快照。
- Workspace 默认执行策略。
- Scope 串行化和销毁竞态保护。

脚本不传 `RuntimeWorkspace`。Workspace 绑定由当前执行上下文完成。

## 8. Workspace 参数是否进入 `handle()`

### 8.1 不推荐脚本显式传递

不推荐：

```nova
NoBukkit.event.handle(workspace, eventType, priority, false, ::onEvent)
```

原因：

- 暴露宿主控制平面，脚本可以误调用 `dispose()`。
- 允许把监听器注册到其他 Workspace，破坏 Generation 归属校验。
- 多 Workspace 并存时，脚本容易保存过期 Workspace 引用。
- 每次调用重复传递相同控制对象，API 噪声大。

### 8.2 宿主侧可以绑定一次

如果宿主必须在 Workspace 执行上下文之外注册，可以创建绑定注册器：

```text
registrar = BukkitEventRegistrar.forWorkspace(plugin, workspace)
registrar.listen(eventType, priority, ignoreCancelled, listener)
```

注册器内部保存 Generation 或指定 ResourceScope，但不应把 `RuntimeWorkspace` 暴露到 Nova 脚本值域。优先绑定具体 `ResourceScope`，以便事件资源归属于业务实例或阶段，而不是无条件挂到 Generation 根。

## 9. Dispose 和竞态语义

### 9.1 资源清理链

Workspace 模式的清理链保持如下：

```text
RuntimeWorkspace.dispose()
  -> WorkspaceGeneration.dispose()
    -> rootScope.dispose()
      -> EventRegistration.dispose()
        -> HandlerList.unregisterAll(listener)
```

非 Workspace 模式的清理链为：

```text
EventRegistration.dispose()
  -> HandlerList.unregisterAll(listener)
```

### 9.2 幂等和先后顺序

- `EventRegistration.dispose()` 必须幂等。
- Bukkit 已因 Plugin 禁用而注销后，再次 `dispose()` 不得报错。
- Workspace 进入 `DISPOSING` 后拒绝新的回调执行。
- 已进入执行锁的同步回调由 Generation 读写锁保证完成或明确失败。
- 监听器被注销后不得再创建新的 Workspace 回调。

## 10. 类加载器边界

事件类名解析不能依赖一个与调用者无关的全局可变 ClassLoader。

Workspace 模式应使用当前 Generation 的脚本 ClassLoader；普通 Nova 模式应使用绑定到该 Nova/Registrar 的 ClassLoader。若仍通过 `JavaInterop` 的 ThreadLocal 解析，必须保证 standalone 宿主调用也在安装正确上下文后执行，不能假设所有调用都来自 Workspace。

事件类名继续可以使用字符串，因为跨插件事件类型可能由运行时插件 ClassLoader 提供；本提案只要求回调函数引用和参数签名在编译期检查，不把事件类字符串误认为函数名字符串。

## 11. 模块边界

```text
nova-runtime-workspace
  RuntimeWorkspace
  WorkspaceGeneration
  ResourceScope
  WorkspaceExecutionContext
  WorkspaceResource

nova-bukkit
  NoBukkit
  BukkitEventRegistrar
  EventRegistration
  BukkitEventListener
```

依赖方向保持：

```text
nova-runtime-workspace  <-  nova-bukkit
```

`nova-runtime-workspace` 不导入 Bukkit，不注册 Bukkit 事件，也不维护 Plugin。`BukkitEventRegistrar` 可以使用 Workspace 的公共资源和执行接口，但具体 Bukkit 逻辑全部留在 `nova-bukkit`。

## 12. 宿主安装方式

当前 `WorkspaceHost.install(Nova)` 只接收 Nova 实例。NoBukkit 的 Plugin 绑定可以通过宿主闭包完成：

```java
RuntimeWorkspace workspace = new RuntimeWorkspace(
        configFile,
        nova -> NoBukkit.install(nova, businessPlugin));
```

`NoBukkit.install` 只安装与 Plugin 绑定的 API 描述；Workspace Generation 和 ResourceScope 仍由运行时执行上下文提供。这样不会要求在 Workspace 尚未创建 Generation 的安装阶段强行传递 Workspace 对象。

如果后续确实需要宿主在上下文外创建 Workspace 绑定注册器，再增加独立的 `BukkitEventRegistrar.forWorkspace(...)`，不修改 NoBukkit 全局单例的可变状态。

## 13. 实施顺序

1. 增加 `BukkitEventRegistrar` 和 `EventRegistration`，复用现有事件解析、SAM 回调和注销逻辑。
2. 将 Bukkit Plugin 所有者从固定的 `NovaBukkitPlugin` 改为宿主绑定的实际业务 `Plugin`。
3. 增加 NoBukkit 的显式宿主绑定和编译期 API 描述。
4. 实现 standalone 注册路径，不读取 Workspace 上下文。
5. 实现 Workspace 注册路径，继续自动登记 ResourceScope。
6. 将服务端脚本从 `BukkitWorkspaceEvents` 迁移到 `NoBukkit.event.listen`。
7. 增加普通 Nova、Workspace、Plugin disable、Workspace dispose、重复 dispose 和多 Workspace 并存测试。
8. 删除 `BukkitWorkspaceEvents` 公开入口及所有旧文档，执行全仓静态扫描。

## 14. 验收标准

- 普通 Nova 未创建 Workspace 时可以成功注册并注销 Bukkit 监听器。
- Workspace 脚本注册的监听器在对应 Scope dispose 后不再触发。
- 两个 Workspace 同时运行时，事件回调不会使用另一个 Workspace 的绑定或 ClassLoader。
- Bukkit 注册使用实际业务 Plugin；业务 Plugin 禁用后监听器不会继续执行脚本回调。
- `::missing`、参数不兼容和返回值不兼容在编译期失败。
- 代码中不存在以 `String functionName` 作为 Bukkit 回调的入口。
- 不存在通过 null、异常或全局可变 Workspace 自动选择运行模式的 fallback。
- `nova-runtime-workspace` 的依赖树中不出现 Bukkit 类型。

## 15. 待确认决策

1. 公开注册动作最终命名使用 `listen`、`register`，还是保留用户提出的 `handle`。
2. 脚本侧的 Plugin 是否由 NoBukkit 宿主绑定，还是作为显式脚本对象传递。
3. Workspace 外部注册器绑定 `RuntimeWorkspace`、`WorkspaceGeneration` 还是具体 `ResourceScope`。
4. standalone Nova 回调是否允许异步执行，以及异步执行器由谁提供。
5. 事件类字符串是否继续保留，还是另行增加编译期 `EventType` 描述。
