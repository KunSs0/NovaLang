# import 弱化与性能设计报告

## 1. 结论

NovaLang 可以向弱语言方向降解，但不建议把整个运行时都改成动态分派。推荐只弱化模块和宿主 API 边界，保留脚本内部的静态分析、局部变量槽位、同模块函数调用和字节码优化。

目标结构如下：

```text
源码 import
    ↓
模块标识解析（Workspace 加载阶段）
    ↓
不可变 ModuleDescriptor
    ↓
每个 import 绑定一个 ModuleRef
    ↓
成员调用通过缓存后的 MethodHandle 执行
```

这样可以减少 import 对编译器、类型系统和运行时全局环境的侵入，同时把动态开销限制在跨模块调用边界。

## 2. 当前设计结构

### 2.1 import 的语义范围

当前 `ImportDecl` 同时支持以下形式：

- Nova 模块导入；
- Java 类型导入；
- Java 静态成员导入；
- wildcard 导入；
- alias 导入；
- 字符串模块导入。

对应实现位于 [`ImportDecl.java`](../../nova-compiler/src/main/java/com/novalang/compiler/ast/decl/ImportDecl.java)。

一个 import 会贯穿以下阶段：

```text
Lexer / Parser
    ↓
ImportDecl
    ↓
SemanticAnalyzer
    ├─ 预声明类型
    ├─ 注册 Nova / Java 类型映射
    ├─ 解析静态函数和字段
    └─ 查询内置模块导出
    ↓
AST → HIR → MIR
    ├─ Java 类型表
    ├─ 静态成员表
    ├─ wildcard 元数据
    └─ Nova 模块元数据
    ↓
解释器或字节码运行时
    ├─ 注册 Java 类
    ├─ 注册静态导入
    ├─ 注册 wildcard 包
    ├─ 加载字符串模块
    └─ 将导出写入当前 Environment
```

### 2.2 编译期负担

`SemanticAnalyzer` 会先调用 `predeclareImportTypes()`，随后在访问 `Program` 时再次访问 import。Java 导入会进入 `TypeResolver`，最终由 `JavaTypeOracle` 解析 Java 类和方法描述符。

因此 import 不只是名称别名，还参与：

- 类型可见性；
- 泛型和继承关系解析；
- Java 重载选择；
- 静态函数和字段类型推导；
- 未知类型和未知调用诊断。

Java 类型解析依赖 `Class.forName`、嵌套类探测、方法扫描和描述符构建。虽然已有正缓存和负缓存，但冷编译时仍然会产生明显的反射工作。

### 2.3 运行时负担

MIR 运行时的 `prepareModule()` 会把 import 转换为当前解释器环境中的绑定。字符串模块会被加载并通过 `exportAll()` 平铺到当前环境，Java 类型和静态成员也会直接写入环境。

这种设计带来三个问题：

1. 模块边界被抹平，名称容易冲突；
2. 导入带有环境修改和初始化副作用；
3. import 的使用者和模块导出者之间没有稳定的描述符层。

### 2.4 两套模块系统并存

当前存在两条不同的模块路径：

#### Workspace 模块系统

[`WorkspaceModuleResolver`](../../nova-runtime-workspace/src/main/java/com/novalang/workspace/WorkspaceModuleResolver.java) 会解析 Alias、相对路径、虚拟模块和物理文件，构建完整依赖图并检测循环依赖。

[`WorkspaceCompilationPlanner`](../../nova-runtime-workspace/src/main/java/com/novalang/workspace/WorkspaceCompilationPlanner.java) 根据入口消费者集合划分编译组，配合字节码缓存和 Generation ClassLoader，已经具备较好的编译隔离能力。

#### 传统 ModuleLoader

[`ModuleLoader`](../../nova-runtime/src/main/java/com/novalang/runtime/interpreter/ModuleLoader.java) 仍然支持字符串模块的文本展开。`expandVirtualImports()` 会递归读取模块源码、拼接文本，再交给解析器和解释器处理。

两条路径对模块身份、初始化时机、缓存范围和错误位置的处理不同，是当前结构变重的主要原因之一。

## 3. 重量来源判断

import 的重量主要来自以下组合，而不是关键字本身：

```text
文件解析
+ 依赖图构建
+ 编译期类型查询
+ Java 反射
+ 全局符号平铺
+ 模块初始化
+ 跨编译组链接
+ 旧文本展开路径
```

其中影响最大的结构性问题是：

1. import 同时承担依赖声明、类型声明、运行时绑定和初始化；
2. Java API 通过源码级 import 进入编译器，而不是通过稳定的宿主描述符进入；
3. 模块导出默认进入当前环境，缺少命名空间边界；
4. Workspace 和传统 ModuleLoader 重复实现模块加载。

## 4. 弱语言降解目标

建议将模块使用方式调整为命名空间对象：

```nova
import "@/ui" as ui

ui.Button.create("确认")
ui.Time.now()
```

核心语义调整如下：

- import 只绑定模块对象，不再将模块所有符号平铺到当前作用域；
- 模块依赖在 Workspace 加载阶段解析，运行时不重复读文件；
- 模块只暴露稳定的导出描述符；
- 未声明类型的模块成员按 `Dynamic` 处理；
- 局部变量、同模块函数和显式类型注解继续使用静态分析；
- wildcard 和静态成员平铺不再作为默认模块模型。

### 4.1 ModuleDescriptor

建议增加一个不可变的模块描述符，至少包含：

```text
ModuleDescriptor
├─ moduleId
├─ version / apiVersion
├─ exported values
├─ exported functions
├─ exported types
├─ constructors
├─ capabilities / security metadata
└─ initialization policy
```

宿主插件注册模块时提供描述符和实现句柄，Nova 编译器不需要为了每个 Java 类型重新扫描完整类信息。

### 4.2 ModuleRef

每个 import 在编译后只生成一个模块槽位或常量引用：

```text
import "@/ui" as ui
        ↓
ModuleRef("@workspace/source-0/ui.nova")
```

成员访问在模块引用上进行，不修改调用方的全局符号表。

### 4.3 动态调用缓存

模块成员调用第一次执行时解析导出描述符和参数适配器，并缓存 `MethodHandle`。缓存键建议包含：

```text
Generation + moduleId + memberName + arity + call shape
```

后续调用直接走缓存句柄。当前 [`NovaDynamic`](../../nova-runtime-api/src/main/java/com/novalang/runtime/NovaDynamic.java) 和 `MethodHandleCache` 已经提供了动态成员查找和句柄缓存基础，可以复用其缓存思路，但不能让所有静态调用无条件退化到通用反射路径。

## 5. 性能保持策略

### 5.1 保留静态热路径

以下代码继续保持当前静态编译方式：

- 数值运算；
- 局部变量读写；
- 循环和条件分支；
- 同模块函数调用；
- 明确类型的 Java 调用；
- 已知签名的宿主函数调用。

弱化只影响跨模块、动态成员和未声明宿主值。

### 5.2 加载时完成依赖解析

Workspace 仍然负责：

1. 解析 Alias 和相对路径；
2. 构建完整依赖图；
3. 检测循环依赖；
4. 读取 UTF-8 源码；
5. 生成稳定模块 ID；
6. 缓存不可变字节码产物。

运行时调用不能重新扫描目录、读取源码或重新解析 import。

### 5.3 初始化与查找分离

模块初始化应当是每个 Generation 一次。成员查找不应触发重复初始化，也不应将导出复制到调用方环境。

推荐使用：

```text
Generation
└─ ModuleRegistry
   ├─ moduleId → ModuleDescriptor
   ├─ moduleId → ModuleInstance
   └─ callsite → cached MethodHandle
```

### 5.4 已知模块的静态特化

如果模块描述符在编译或链接阶段已知，编译器可以直接生成静态调用或特化句柄。只有模块成员真正未知、宿主允许热替换或脚本使用动态名称时，才使用通用动态分派。

这样可以同时得到：

- 弱化后的源码和模块边界；
- 已知 API 的直接字节码；
- 未知 API 的运行时可扩展能力。

## 6. 方案对比

| 方案 | 弱化程度 | 冷启动 | 热调用 | 类型诊断 | 结论 |
| --- | --- | --- | --- | --- | --- |
| 所有 API 注入全局变量 | 高 | 好 | 动态查找开销较大 | 弱 | 适合 REPL，不适合作为主模型 |
| 每次调用动态加载 import | 高 | 差 | 很差 | 弱 | 不建议 |
| 命名空间 ModuleRef + 首次链接缓存 | 中 | 好 | 接近静态调用 | 可保留边界检查 | 推荐 |
| 保持当前强 import | 低 | 依赖数量越多越重 | 最好 | 最强 | 适合性能敏感模块 |
| 严格模式和弱模式并存 | 可调 | 可调 | 可调 | 可调 | 适合作为最终形态 |

## 7. 推荐迁移阶段

### 阶段一：建立基准

分别测量以下场景：

- 无 import；
- Java 类型 import；
- Java static import；
- 单层字符串模块；
- 多层字符串模块；
- 多入口共享依赖；
- 冷编译、缓存命中、首次调用和稳定调用。

必须把编译时间、首次执行时间和稳定运行时间分开统计，否则无法判断优化是否只改善了启动而损害了热路径。

### 阶段二：统一模块解析

以 Workspace 模块图作为唯一模块解析入口，停止传统 `ModuleLoader.expandVirtualImports()` 的文本展开链路。

这个步骤会改变独立 `Nova` 使用者的模块行为，属于破坏性重构。是否直接删除旧机制，需要单独确认，不能通过隐式 fallback 保留两套语义。

### 阶段三：引入模块描述符

将 `registerModule`、`registerSharedModule` 和 Workspace 虚拟源码统一映射到模块描述符协议。描述符负责公开 API、权限和初始化策略，编译器不再依赖大量源码级 Java import。

### 阶段四：引入 ModuleRef

将字符串 import 编译为模块引用，把成员访问改成命名空间访问。模块导出不再通过 `exportAll()` 复制进调用方环境。

### 阶段五：实现弱模式

弱模式允许动态模块成员和运行时错误；严格模式保留完整类型检查。性能敏感的公共库和底层 API 使用严格模式，业务脚本可以使用弱模式。

### 阶段六：清理旧语义

在确认迁移完成后，再删除 wildcard、静态成员平铺和旧 ModuleLoader 分支。不要长期维护两套 import 语义，否则编译器和运行时复杂度不会下降。

## 8. 不推荐的方向

### 8.1 完全取消 import，全部由宿主注入

现有 `externalValueNames` 已支持宿主动态值，但这种方案会造成：

- 隐式依赖；
- 全局名称冲突；
- 权限边界不清晰；
- 依赖图无法静态构建；
- 错误只能在运行时发现。

它可以作为 REPL 或临时脚本模式，不能作为 Workspace 的默认模块模型。

### 8.2 每次调用都进行反射查找

这会把 import 的启动成本转化为持续运行成本。反射和重载解析必须在模块加载或调用点首次链接时完成，并且在 Generation 生命周期内缓存。

### 8.3 只增加隐式 Prelude

隐式 Prelude 能减少源码中的 import 行数，但不能减少依赖图、类型解析和运行时初始化成本，还会扩大全局符号表和权限面。

## 9. 需要单独确认的破坏性决策

以下事项会改变现有语言或运行时行为，应逐项确认：

1. 是否废弃传统 `ModuleLoader` 文本展开机制；
2. 是否停止 wildcard import；
3. 是否停止 static import 的全局平铺；
4. 是否将字符串 import 改为必须使用 alias；
5. 是否允许弱模式下未知成员延迟到运行时失败；
6. 是否保留严格模式作为公共库和高性能脚本的默认模式。

## 10. 最终建议

采用“严格模块图 + 弱模块边界 + 缓存调用点”的混合架构：

- Workspace 严格解析模块依赖；
- ModuleDescriptor 取代源码级宿主 import；
- ModuleRef 取代全局符号平铺；
- MethodHandle 缓存保证稳定调用性能；
- Nova 内部计算和同模块调用继续走静态字节码；
- 传统 ModuleLoader、wildcard 和 static 平铺在迁移确认后删除。

这个方向能够降低 import 对语言实现的侵入程度，同时最大限度保留当前编译后代码的性能。
