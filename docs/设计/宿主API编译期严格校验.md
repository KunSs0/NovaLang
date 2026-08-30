# 宿主 API 编译期严格校验设计

## 1. 背景

Nova 已经具备宿主绑定描述与运行时安装能力：

- `JavaTypes` 可以描述全局变量、函数、对象、对象成员、按 Java 类型注册的扩展函数、参数类型和返回类型。
- `JavaTypesInstaller` 可以把宿主变量、对象、函数和扩展函数安装到 `Nova` 实例。
- `RuntimeWorkspace` 会在编译脚本前调用宿主安装逻辑。
- `SemanticAnalyzer` 已具备 Nova 类型检查、Java 成员解析和 Java 重载解析能力。

改造前缺失的是这些能力之间的连接。`JavaTypesInstaller` 把描述符转换为 `defineVal` 或原生函数后，完整的类型和成员信息没有进入编译器；`Nova.compileToBytecode*()` 创建的 `NovaIrCompiler` 也没有获得当前 Nova 实例的 Java 类型注册表。最终结果是 Java 函数或对象在运行时确实存在，但编译器无法严格验证其调用是否合法。

这会导致以下错误在编译阶段通过，直到脚本执行时才暴露：

- 调用不存在的宿主函数。
- 宿主函数参数数量错误。
- 宿主函数参数类型错误。
- 对宿主对象调用不存在的成员。
- 把属性当作函数，或把函数当作属性。
- 返回值类型丢失后继续进行错误的链式调用。
- Java 方法不存在、不可访问、静态与实例调用方式错误。
- Java 重载无法匹配或存在歧义。

这类错误的定位和纠正成本高，尤其是在 Bukkit 等需要完整服务器环境才能运行脚本的宿主中。因此，宿主 API 的合法性必须在语义分析阶段完成验证，检查失败时不得生成字节码。

## 2. 目标

本设计的核心目标是：

> 编译器使用当前 Nova 实例已经注册的宿主 API 作为编译期符号表，在生成 IR 和字节码前发现宿主函数、对象成员、参数、返回类型和 Java 互操作错误。

具体目标包括：

1. 宿主 API 的签名与运行时实现同源注册。
2. `JavaTypes` 同时服务于运行时安装、编译期校验和工具链展示。
3. 已知宿主类型的函数和成员调用必须进行严格校验。
4. 函数返回类型必须向后传播，支持多级链式调用检查。
5. Workspace、普通 `Nova` 实例、CLI 和 LSP 使用一致的宿主符号语义。
6. 缺少宿主 API 信息时明确终止编译，不把错误延迟到运行时。
7. 编译诊断必须包含源码位置、错误原因、候选签名和可执行的修改建议。

## 3. 非目标

本设计不包含以下行为：

- 不把 JSON 文件作为服务器运行时的权威 API 来源。
- 不在运行时通过反射猜测缺失的宿主契约并继续执行。
- 不在宿主 API 缺失时自动切换为动态调用。
- 不为错误函数名或成员名自动修改源代码。
- 不通过捕获运行时异常模拟编译期检查。
- 不保留“编译器不知道，但运行时可能存在”的隐式兼容路径。

编译器可以提供候选名称和修改建议，但是否修改源代码由开发者决定。

## 4. 当前链路与问题

当前主要链路为：

```text
JavaTypes
        |
        v
JavaTypesInstaller
        |
        +-- 变量/对象 --> Nova.defineVal 或 Nova.set
        |
        +-- 函数 -----> NovaNativeFunction --> Nova.defineVal

Nova.compileToBytecode*()
        |
        v
new NovaIrCompiler()
        |
        v
PassPipeline
        |
        v
new SemanticAnalyzer()
```

问题发生在两处：

1. `JavaTypesInstaller` 只安装运行时值和调用器，没有让 `Nova` 持续持有用于编译的描述符。
2. `PassPipeline` 创建独立的 `SemanticAnalyzer`，目前只补充少量外部类型名称，没有注册宿主变量、函数、对象和成员签名。

因此，运行时绑定表和编译期符号表是分离的。

目标链路应调整为：

```text
                 JavaTypes
                  /              \
                 /                \
                v                  v
     JavaTypesInstaller     JavaSemanticSymbols
                |                  |
                v                  v
          Runtime Values      SemanticAnalyzer
                                   |
                                   v
                           JavaOverloadResolver
                                   |
                                   v
                         IR / Bytecode Generation
```

只有语义分析无错误时，才能进入 IR 和字节码生成。

## 5. 权威数据源

### 5.1 注册表是唯一事实来源

`JavaTypes` 应当成为宿主 API 的唯一事实来源。一次注册同时提供：

- 符号名称。
- 符号种类。
- 参数名称和参数类型。
- 返回类型。
- 可变参数信息。
- 对象成员。
- Java 对应类型。
- 可空性和泛型参数。
- 文档、弃用信息和示例。
- 运行时值、值提供器或函数调用器。

宿主不应分别维护一份运行时注册代码和一份手写 JSON 契约，否则两者会发生漂移。

### 5.2 JSON 的定位

JSON 只能是注册表的派生产物，适用于无法加载宿主代码的工具场景，例如：

- 独立 CLI 编译。
- VS Code 和 LSP。
- CI 中的离线检查。
- API 文档生成。

同进程的 Bukkit 或 Workspace 编译必须直接读取注册表，不需要先导出再读取 JSON。

现有《宿主绑定配置规范》仍可作为工具链输入格式，但后续应支持从权威注册表生成，不能要求宿主重复维护。

## 6. 类型模型要求

当前 `JavaTypeRef` 只保存 `displayName`，无法支撑严格 Java 成员校验。它需要演进为结构化类型引用，至少表达：

```text
JavaTypeRef
├─ displayName
├─ javaClass 或 Java 类名
├─ nullable
├─ typeArguments
├─ arrayComponentType
└─ dynamic
```

建议支持以下构造方式：

```java
JavaTypeRef.of("Player")
JavaTypeRef.javaType(Player.class)
JavaTypeRef.javaType(ItemStack.class).nullable()
JavaTypeRef.listOf(JavaTypeRef.javaType(Player.class))
```

其中：

- 只有显示名称的类型可以用于文档展示，但不能自动视为可反射的 Java 类型。
- 绑定了 `Class<?>` 的类型可以进入 Java 成员和重载解析。
- 容器类型必须保留元素类型，否则 `players()[0]` 之后会退化为 `Any` 或 `dynamic`。
- 可空类型必须参与安全调用和空值检查。
- `dynamic` 必须是显式类型，不能由“编译器没找到符号”隐式产生。

## 7. 编译上下文

新增不可变的编译上下文，用于把当前宿主环境传递到编译器：

```text
NovaCompilationContext
├─ hostBindings
├─ namespace
├─ classLoader
├─ securityPolicy
├─ strictHostValidation
└─ bindingFingerprint
```

职责如下：

- `Nova` 在调用 `compileToBytecode*()` 时创建上下文。
- 上下文持有当前命名空间解析后的宿主注册表快照。
- `NovaIrCompiler` 把上下文传递给 `PassPipeline`。
- `PassPipeline` 使用上下文创建并配置 `SemanticAnalyzer`。
- 编译期间不再读取可变的全局宿主状态。

使用不可变快照可以避免注册表在并行编译中发生变化，也能保证编译缓存键稳定。

## 8. 宿主符号桥接

在编译器侧增加 Java 符号桥接逻辑，负责把 `JavaTypes` 转换为语义分析器可理解的符号：

```text
JavaVariableDescriptor -> VARIABLE symbol
JavaFunctionDescriptor -> FUNCTION symbol + FunctionNovaType
JavaObjectDescriptor   -> OBJECT symbol + member scope
JavaPropertyDescriptor -> PROPERTY symbol
JavaTypeRef            -> NovaType / Java-backed NovaType
```

桥接时必须保留重载，不允许后注册的同名函数简单覆盖前一个函数。全局函数、对象成员函数和 Java 方法都需要以候选集合参与匹配。

命名空间应在创建编译上下文时通过 `JavaTypes.resolveNamespace()` 完成合并。语义分析器只接收已经解析好的当前命名空间视图，避免编译器重复实现命名空间继承规则。

## 9. 语义检查规则

### 9.1 全局标识符

标识符按以下顺序解析：

1. 当前局部作用域。
2. 当前 Nova 模块的声明。
3. 显式导入。
4. Nova 内置符号。
5. 当前命名空间下的宿主符号。

全部未找到时产生 `E_UNRESOLVED_SYMBOL`，不得生成动态符号继续编译。

### 9.2 全局函数调用

对于：

```nova
player("Haider")
```

编译器必须验证：

1. `player` 是否存在。
2. `player` 是否是可调用符号。
3. 实参数量是否满足固定参数或可变参数要求。
4. 每个实参类型是否能转换为声明参数类型。
5. 是否存在唯一最佳重载。
6. 返回类型是否能够确定。

失败时分别产生：

- `E_NOT_CALLABLE`
- `E_ARGUMENT_COUNT_MISMATCH`
- `E_ARGUMENT_TYPE_MISMATCH`
- `E_NO_MATCHING_OVERLOAD`
- `E_AMBIGUOUS_OVERLOAD`

### 9.3 对象成员访问

对于：

```nova
player.name
player.sendMessage("hello")
```

解析顺序为：

1. 查询宿主对象描述符中显式注册的成员。
2. 如果类型绑定了 Java `Class<?>`，查询允许访问的 Java 成员。
3. 应用 `MemberNameResolver` 后重新解析允许映射的 Java 成员。
4. 仍未找到则产生 `E_UNRESOLVED_MEMBER`。

显式注册成员应优先于 Java 反射成员，因为显式注册代表宿主公开给脚本的稳定 API 表面。

属性访问和函数调用必须区分：

- 对函数使用属性语法时产生 `E_FUNCTION_USED_AS_PROPERTY`。
- 对属性使用调用语法时产生 `E_PROPERTY_USED_AS_FUNCTION`。
- 对只读属性赋值时产生 `E_READONLY_PROPERTY`。

### 9.4 返回类型传播

每次调用都必须将已解析签名的返回类型写入表达式类型表：

```nova
player("Haider").inventory().item(0).amount()
```

对应类型链为：

```text
player(String)       -> Player
Player.inventory()   -> PlayerInventory
Inventory.item(Int)  -> ItemStack?
ItemStack.amount()   -> Int
```

任何一步返回 `Any` 或 `dynamic`，都会降低后续检查能力。因此宿主资源必须尽量提供准确返回类型；对集合还必须提供元素类型。

### 9.5 Java 构造器、静态成员和实例成员

对显式导入的 Java 类，编译阶段必须检查：

- 构造器是否存在且可访问。
- 静态字段或方法是否真实存在。
- 实例成员是否真实存在。
- 静态方法是否被错误地通过实例调用。
- 实例方法是否被错误地通过类型调用。
- 参数是否能完成 Nova 到 Java 的合法转换。
- varargs、数组和 SAM 转换是否合法。
- 安全策略是否允许访问目标类和成员。

重载选择应复用现有 Java 重载解析能力，语义分析和字节码生成必须使用同一个解析结果，避免编译检查选择的方法与最终生成的调用目标不一致。

### 9.6 动态类型

严格宿主校验模式下，未解析的符号和成员不得自动退化为 `dynamic`。

只有源码或宿主描述符明确声明为 `dynamic` 时，才允许动态成员访问。显式动态调用不属于编译期完全保证范围，诊断中应标记该调用无法静态验证。

Workspace 和生产字节码编译默认启用严格宿主校验。是否允许显式动态能力由上层安全策略决定，不能由编译器在解析失败后自动启用。

## 10. 诊断格式

诊断必须是结构化结果，而不只是拼接后的异常文本。建议包含：

```text
code
severity
message
sourceLocation
symbolName
receiverType
actualArgumentTypes
candidateSignatures
suggestions
```

不存在的成员示例：

```text
E_UNRESOLVED_MEMBER: Player 上不存在成员 sendMessag
  --> scripts/reward.nova:12:8
  候选:
    sendMessage(String): Unit
  建议: 将 sendMessag 修改为 sendMessage
```

重载不匹配示例：

```text
E_NO_MATCHING_OVERLOAD: Player.teleport(String, Int) 没有匹配的重载
  --> scripts/main.nova:8:1
  可用重载:
    teleport(Location): Boolean
    teleport(Location, TeleportCause): Boolean
  原因: 第 1 个参数需要 Location，实际为 String
```

宿主 API 未加载示例：

```text
E_HOST_API_UNAVAILABLE: 当前编译环境没有提供 player 所属的宿主 API
  --> scripts/main.nova:3:1
  建议: 为当前编译任务安装对应的 JavaTypes 或 API Provider
```

名称建议可以使用编辑距离，但只用于诊断展示，不能自动改变符号解析结果。

## 11. Workspace 行为

`RuntimeWorkspace.load()` 当前已经先调用 `host.install(nova)`，再编译入口。后续需要把安装动作拆成两个明确结果：

1. 把运行时值和调用器安装到 `Nova`。
2. 把相同的 `JavaTypes` 绑定到 `Nova` 的编译上下文。

Workspace 加载顺序应固定为：

```text
创建 Nova
  -> 设置 Workspace ClassLoader
  -> 构建并绑定 JavaTypes
  -> 安装运行时值和调用器
  -> 冻结宿主绑定快照
  -> 构建模块图
  -> 语义分析全部入口
  -> 全部成功后生成和加载字节码
  -> 发布新 Workspace
```

如果任意入口存在宿主 API 错误，整个 Workspace 加载失败，不允许只加载剩余脚本，也不允许继续使用部分编译结果。

## 12. 单体编译、CLI 与 LSP

### 12.1 同进程编译

使用 `Nova` 实例编译时，直接使用绑定在该实例上的注册表：

```text
Nova instance -> JavaTypes snapshot -> SemanticAnalyzer
```

这是 Bukkit、测试和嵌入式调用的主要路径。

### 12.2 独立 CLI 编译

独立编译必须显式获得宿主 API，来源可以是：

- CLI 启动参数指定的宿主 API Provider。
- Workspace 配置声明的 API Provider。
- 从权威注册表导出的离线契约。

如果源码引用宿主符号但没有加载任何对应 API，必须报 `E_HOST_API_UNAVAILABLE`，不得把调用保留到运行时。

### 12.3 LSP

LSP 应复用编译器的宿主符号桥接和类型检查逻辑，不再单独实现一套仅用于补全的弱校验。

LSP 可以读取 `.nova/*.json`，但读取后必须转换为与运行时注册表一致的描述符模型，再交给 `SemanticAnalyzer`。这样 CLI、Workspace 和编辑器才能产生一致诊断。

## 13. 编译缓存

宿主 API 变化会改变合法重载、成员和返回类型，因此编译缓存键必须包含宿主绑定指纹。

指纹至少覆盖：

- 当前命名空间。
- 符号名称和种类。
- 参数类型与可变参数标记。
- 返回类型。
- 对象成员。
- Java 类型身份。
- 可空性和泛型参数。
- 安全策略中影响成员可见性的部分。

运行时对象实例和值不应进入指纹，除非它们会改变编译期可见 API。相同契约、不同玩家或不同服务器对象不应导致重复编译。

## 14. API 资源注册建议

宿主 API 资源应采用“签名与实现同源”的注册形式：

```java
JavaTypes registry = JavaTypes.builder()
        .globalFunction("player", function -> function
                .param("name", JavaTypeRefs.STRING)
                .returns(JavaTypeRef.javaType(Player.class))
                .invoke1(String.class, Bukkit::getPlayerExact))
        .extension(Player.class, "name", function -> function
                .returns(String.class)
                // 扩展 invoker 的 arguments[0] 固定为 receiver。
                .invoke(arguments -> ((Player) arguments[0]).getName()))
        .extensionProperty(Location.class, "x", property -> property
                .type(double.class)
                .getter(arguments -> ((Location) arguments[0]).getX())
                // setter 的 arguments 为 [receiver, value]。
                .setter(arguments -> {
                    Location location = (Location) arguments[0];
                    location.setX((Double) arguments[1]);
                    return null;
                }))
        .build();
```

扩展函数描述中的参数列表不包含 receiver；运行时 invoker 的参数数组按
`[receiver, arg0, arg1, ...]` 排列。该描述会同时进入编译器和运行时扩展注册表，
因此 `player("Alex")?.name()` 的成员存在性、参数和返回类型都能在编译阶段确认。

扩展属性同样是编译期与运行时共用的资源。只配置 getter 时属性只读；同时配置 setter
后才允许赋值。编译器会在生成字节码前拒绝不存在的 setter、只读属性赋值和赋值类型
不匹配，例如 `location(...).blockX = 4` 与 `location(...).x = "bad"`。原生 Java
对象的公开字段和 JavaBean `getX/isX/setX` 也使用相同规则校验。

NovaBukkit 在全部领域扩展注册完成后显式调用
`javaBeanPropertiesFromExtensions()`，把这些扩展涉及类型的公开 JavaBean 属性登记为
`extensionProperties`。这不是编译器的系统级反射兜底：其他宿主若需要同样能力，应主动
调用该方法，或逐项使用 `extensionProperty(...)` 声明别名属性。导出的契约 JSON 会同时
包含根命名空间和子命名空间的 `extensionProperties`、属性类型及 `mutable` 标记。

当前 Bukkit 基础实现位于 `nova-bukkit`：

```java
// 单体 Nova
Nova nova = NovaBukkit.install(new Nova());

// Workspace：在每次创建 Workspace 独占 Nova 时安装
RuntimeWorkspace workspace = new RuntimeWorkspace(configFile, NovaBukkit::install);
```

业务插件还要增加自己的 API 时，应使用 `NovaBukkit.builder()` 继续追加后一次性
`build()`、`nova.install(...)`，避免后一次安装的新注册表替换前一次编译期视图。

`nova-bukkit` 当前声明的基线是 Spigot API 1.12.2。Particle、Advancement 和 1.20+
专有枚举不进入基础层；若要完整复刻新版 Fluxon `platform-bukkit`，应建立明确版本模块，
不能在基础类中通过反射或缺类兜底静默降级。

当前 `nova-bukkit` 的编译期资源按领域分包，根包只保留公开入口和 Bukkit 运行设施：

```text
com.novalang.bukkit
├─ NovaBukkit
├─ NovaBukkitPlugin
└─ types
   ├─ entity
   ├─ enums
   ├─ event
   ├─ gameplay
   ├─ inventory
   ├─ platform
   ├─ server
   ├─ value
   └─ world
```

各领域注册器最终由 `NovaBukkit` 合并为当前 Nova 或 Workspace 的不可变注册表。资源模块负责声明能力，Nova 负责编译校验，不要求每个模块生成和维护独立 JSON。

## 15. 实现阶段

### 阶段一：接通注册表和编译器

- 让 `Nova` 持有当前 `JavaTypes`。
- 增加不可变 `NovaCompilationContext`。
- 将编译上下文传入 `NovaIrCompiler` 和 `PassPipeline`。
- 实现 Java 描述符到语义符号的转换。
- 让宿主全局变量和函数参与语义分析。
- 未知宿主函数、参数数量错误和基础参数类型错误必须阻断编译。

### 阶段二：对象成员与类型传播

- 扩展 `JavaTypeRef` 为结构化类型。
- 注册宿主对象和对象成员符号。
- 传播函数与属性返回类型。
- 校验成员存在性、成员种类、可变性和可空性。
- 增加集合元素类型传播。

### 阶段三：Java 严格解析

- 统一语义分析和代码生成的 Java 重载解析结果。
- 校验构造器、静态成员、实例成员和访问权限。
- 校验 varargs、数组、SAM 和数值转换。
- 增加候选签名与成员名称建议。

### 阶段四：Workspace、缓存与工具链

- Workspace 在编译前冻结宿主绑定快照。
- 编译缓存加入绑定指纹。
- CLI 支持显式 API Provider 或离线契约。
- LSP 复用编译器宿主符号模型。
- 从注册表导出工具链 JSON，避免手工维护重复契约。

## 16. 验收标准

实现完成后，至少满足以下测试：

### 16.1 应当编译失败

```nova
missingHostFunction()
```

```nova
player()
```

```nova
player(123)
```

```nova
player("Haider").sendMessag("hello")
```

```nova
player("Haider").name()
```

```nova
player("Haider").teleport("world", 1)
```

```nova
SomeJavaClass.missingStaticMethod()
```

### 16.2 应当编译成功

```nova
val target = player("Haider")
target.sendMessage("hello")
```

```nova
val item = player("Haider").inventory().item(0)
item?.setAmount(2)
```

### 16.3 一致性要求

- 解释执行与字节码编译对同一宿主 API 产生相同的符号解析结果。
- Workspace 与直接 `Nova.compileToBytecode()` 产生相同诊断。
- CLI 与 LSP 在加载相同契约时产生相同错误码和候选签名。
- 宿主注册表变化后不得命中旧编译缓存。
- 语义分析失败后不得生成、缓存或加载部分字节码。

## 17. 最终原则

本设计遵循以下原则：

1. 运行时能够调用，不代表编译期合法。
2. 宿主签名必须进入语义分析器，不能只安装为运行时值。
3. 未知符号和未知成员在严格模式下必须失败，不能静默动态化。
4. 类型信息必须沿调用链传播，才能真正提前发现对象成员错误。
5. JSON 是工具链快照，不是服务器运行时契约来源。
6. API 缺失、类型未知或重载不明确时，应当停止编译，而不是把错误留到执行阶段。
