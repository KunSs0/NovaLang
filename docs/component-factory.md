# 预准备组件工厂与隔离作用域

字节码 UI、工作流等宿主可以在编译阶段准备组件工厂，避免每个实例重新扫描所有模块方法、
重复构建资源路径，以及使用通用函数重载匹配调用无参数工厂。

## API 与生命周期

- `CompiledProgramLayout.fromBytecode(bytecode)`：使用 ASM 读取函数所属模块和 main 入口，
  只保存名称，不保存已加载 Class，可以在隔离作用域之间复用。
- `Nova.createCompiledNova(classes, layout)`：为本次隔离类创建运行绑定，不再反射扫描整份程序。
- `CompiledNova.prepareComponentFactory(name, type)`：绑定一个 public static 无参、非 void 工厂入口。
- `CompiledComponentFactory<T>.create()`：直接通过已绑定 MethodHandle 创建组件，维护 Nova 上下文，
  并检查返回值符合宿主类型。`getProgram()` 返回所属隔离程序。
- `BytecodeArtifact.prepareComponentFactory(nova, parentLoader, entry, type)`：准备可复用的
  `IsolatedComponentFactory<T>`；共享程序索引及不可变字节码资源表。
- `IsolatedComponentFactory<T>.createScope()`：创建新的类加载器和运行绑定，返回这个作用域的工厂。

同一个 `createScope()` 返回的工厂可以多次 `create()`：组件的实例字段独立，但模块静态状态属于
这个作用域。需要模块静态字段也隔离时，必须分别 `createScope()`。不同作用域不共享脚本 Class。

宿主在工厂内部执行脚本时不需要再包一层 Nova 绑定 Map 复制；如果宿主还维护自己的回调归属标记，
可以只在外围设置和恢复这个标记。工厂成功时回写所属程序绑定，异常时恢复外层线程上下文，
不会替换其他作用域的运行绑定。

该接口不取消字节码编译、链接与 JVM 验证，也不承诺跨实例共享可变对象。模块资源 URL 及路径表
在产物构建时准备一次，每个隔离作用域仍使用自己的 URLClassLoader。

## 原生参数覆盖

Nova 类覆盖 Java 宿主的 Long/Double/Float 参数方法时，后端生成一个保持宿主签名的入口，
将原生参数装箱后交给 Object 参数布局的 MIR 方法体，再转换返回值。
这解决了双槽参数被单槽局部变量初始化覆盖，以及对原生参数错误使用 ALOAD 的问题。
桥接是编译器生成的 JVM ABI 适配，不是运行时错误回退。

## 验证

`IsolatedComponentFactoryTest` 覆盖组件字段、作用域静态状态、绑定隔离、无参入口约束、返回类型检查、
异常上下文恢复。`WorkspaceBytecodeArtifactCacheMockTest` 保持原有不同 Workspace 的静态隔离。
`CompiledWideOverrideTest` 覆盖 Long 参数、大于 int 范围的 Long 值、Float/Double 混合参数和返回值。

```powershell
.\gradlew.bat --no-daemon :nova-ir:build :nova-runtime:build :nova-runtime-workspace:build
.\gradlew.bat --no-daemon :nova-ir:publishToMavenLocal :nova-runtime:publishToMavenLocal :nova-runtime-workspace:publishToMavenLocal
```
