# Nova 编译元数据

`nova-runtime-workspace` 已移除。模块解析和编译配置归入 `nova-runtime` 的
`com.novalang.runtime.compilation`；执行、变量绑定和资源生命周期统一由普通
`CompiledNova` 承担。依赖 `nova-runtime` 即可使用，不再创建 Workspace 或 Generation。

## 单入口编译与执行

直接传入唯一入口文件即可，不需要声明根目录、源码目录或入口列表：

```java
import com.novalang.runtime.Nova;
import com.novalang.runtime.CompiledNova;
import java.nio.file.Paths;

Nova nova = new Nova();
try (CompiledNova program = nova.compileToBytecode(Paths.get("main.nova"))) {
    program.run();
    Object result = program.call("execute", argument);
}
```

入口文件不要求命名为 `main.nova`。相对入口路径按宿主工作目录解析；每条相对 `import`
按发起导入的源码文件所在目录解析，可引用上级或其他目录。编译器从唯一入口递归构建
依赖图，只编译可达模块；未引用的文件和共享模块不参与编译。
同一个物理文件按真实路径去重，即使被不同模块重复引用，也只编译及初始化一次。
这里的按需编译指依赖可达性，不是执行到某行时再编译。

编译仅解析、生成和链接程序，`run()` 才按依赖顺序执行模块初始化。编译和普通同步调用
不要求安装调度器；脚本使用异步调度功能时，由宿主明确提供 `NovaScheduler`。
安全策略由宿主创建 `Nova` 时设置；调用线程由宿主决定，编译元数据不改变线程。

调用函数使用普通 `call(function, arguments)` 或 `callIsolated(function, bindings, arguments)`。
程序只有一个入口，入口自身声明的函数优先于依赖导出的同名声明。

## 可选编译元数据

编译元数据只包含诊断名称 `name` 和路径别名 `aliases`，需要时显式传入：

```java
import com.novalang.runtime.compilation.CompilationConfig;

CompilationConfig.Builder metadata = new CompilationConfig.Builder();
metadata.alias("@shared", Paths.get("../shared"));
try (CompiledNova program = nova.compileToBytecode(Paths.get("main.nova"), metadata.build())) {
    program.run();
}
```

编程配置中的别名目录按宿主工作目录解析。`name` 仅用于诊断，可以省略。
配置不会扫描目录，也不维护程序实例或执行隔离。

## YAML 配置

宿主可以通过 `new CompilationConfigLoader().load(path)` 显式读取 `nova.config.yml`：

```yaml
version: 1
aliases:
  "@shared": "../shared"
```

随后通过 `nova.compileToBytecode(entryFile, metadata)` 传入配置。YAML 中的别名路径
相对配置文件所在目录解析；入口仍由编译调用传入，不受配置文件位置影响。
除了必填的 `version`，编译字段均可省略；同一文件可包含独立的 `test` 配置。

Alias 必须以 `@` 开头，采用完整边界和最长名称匹配。只有实际引用的 Alias 才读取其
目标目录。缺失模块和循环依赖在编译阶段报错，不会猜测其他目录或补充未声明的 Alias。

`sources`、`entries` 和根节点 `runtime` 已移除，继续声明会明确报错。安全策略、线程
选择由 Nova 和宿主配置。旧入口调用 API 已删除，不保留兼容层。

## 宿主源码入口与模块

`SourceUnit.virtual(...)` 保留原始文件、业务路径、起始行和生成源码偏移；
`SourceUnit.inline(...)` 支持具有明确函数名和返回类型的行内动作。

宿主生成的源码也可以直接作为唯一入口传入：

```java
import com.novalang.runtime.compilation.SourceUnit;

SourceUnit entry = SourceUnit.virtual("generated-main", source, originFile, "actions.open", 12, 0);
try (CompiledNova program = nova.compileToBytecode(entry, metadata.build())) {
    program.run();
}
```

入口直接传给编译器。内存源码使用相对 import 时必须提供物理来源文件，
也可以使用显式 Alias 引用文件依赖。编译失败和执行失败会映射回原始源码。

宿主 API 不再通过 Nova 源码模块注册。宿主应使用 `JavaTypes` 描述和安装 Java 类型、
函数及扩展，Nova 源码直接使用原生 Java import。源码依赖只作为编译器内部的输入组织，
不会创建运行时模块环境，也不会通过 `exportAll()` 注入符号。

## 编译缓存与程序资源

`nova.compileToBytecode(entryFile, metadata, cache)` 接受 `BytecodeArtifactCache`。缓存只共享不可变
字节码，依赖源码改变时同步使调用模块的缓存失效。每次编译仍生成独立的 Nova 程序类和模块静态状态。同一配置、同一 Nova 编译
门面可以创建多个独立程序；关闭其中一个不会影响其他程序。

隔离调用复制变量绑定表，不深拷贝宿主对象。同一程序内的脚本静态状态，以及宿主显式
传入的共享可变对象，需要由宿主按照业务并发需求管理。

`ScriptCallbacks.bind(...)` 创建的回调、原生延迟任务、循环任务和异步任务直接归属于
所属 `CompiledNova`。`program.registerResource(resource)` 可登记其他宿主资源。
`program.close()` 取消任务、释放已登记资源并使回调失效。业务需要更短的资源生命周期时，
由业务宿主显式管理，不再使用 Workspace 业务资源树。

## Bukkit 与测试迁移

Bukkit 宿主显式调用 `NovaBukkit.install(nova, plugin)` 安装 API。平台插件仍提供进程级
调度器，不再通过全局 Workspace 扩展注册表推断业务插件。脚本注册的 Bukkit 监听器
复用 `ScriptCallback` 捕获上下文，随所属程序关闭自动注销。

项目测试入口已移入 `nova-test` 的 `ProjectTestRunner`。CLI `nova test` 和 Bukkit
`/nova test` 命令保持可用，读取同一配置的 `test` 节点。每份测试文件直接作为一次编译的唯一入口，
测试结束后关闭程序；生产脚本只有被测试文件显式引用时才参与编译。
