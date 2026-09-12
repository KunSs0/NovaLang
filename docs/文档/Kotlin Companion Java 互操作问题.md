# Kotlin Companion Java 互操作问题记录

## 现象

2026-09-12 服务端启动时，NovaLang 编译服务端脚本仍然出现以下错误：

```text
No matching Java method overload found for 'getINSTANCE'
No matching Java method overload found for 'getMapping'
No matching Java method overload found for 'getENGINE'
No matching Java method overload found for 'getAccessContainer'
No matching Java method overload found for 'get'
```

错误发生在 FightCorePlugin、Creator 以及 Planners 动态生成脚本中。服务端日志：

```text
E:\temp\server-main\logs\latest.log
```

前一批嵌套 Java 类型错误（例如 `Post`、`Source`、`Grant`、`CloseableType`）已经消失，说明嵌套类解析修复本身已生效。本次是新的 Kotlin/Java 互操作问题。

## 典型调用

服务端脚本中存在以下调用形式：

```nova
MagicPointProvider.Companion.getINSTANCE().getPoint(player)
PositionMapping.Companion.getMapping(id)
FeatureEntityManager.Companion.getENGINE().create(actualId, argument)
FeatureEntityManager.Companion.getAccessContainer(ctx)
```

对应的 Kotlin 源码将方法声明在 `companion object` 中。Kotlin 编译后通常会生成：

```java
public static final Companion Companion = new Companion();

public static final class Companion {
    public String getMapping(String id) { ... }
}
```

因此，`Type.Companion.getMapping(...)` 的正确含义是：

1. 从外层类型读取静态字段 `Companion`；
2. 得到 `Companion` 实例；
3. 在该实例上调用普通 Java 实例方法 `getMapping(...)`。

它不是对 `Companion` 嵌套类直接进行静态方法调用。

## 根因判断

NovaLang 当前同时把 `Outer.Companion` 识别为：

- 外层 Java 类的公开嵌套类型路径；
- Kotlin 生成的静态 `Companion` 字段。

语义分析在 `SemanticAnalyzer.resolveNestedJavaType` 和 `isJavaTypeExpression` 中优先将其当作嵌套类。后续调用解析因此使用 `staticOnly = true`，尝试在 `Companion` 类上查找静态方法；而 `getMapping`、`getINSTANCE`、`getENGINE`、`getAccessContainer` 实际都是实例方法，所以报告“没有匹配的 Java 方法重载”。

这个问题与普通嵌套类调用不同：

```nova
Outer.Nested("value")
Outer.Kind.FLOW
Outer.Kind.label()
```

这些形式确实需要按嵌套类型处理，不能简单移除嵌套类型解析。

## 最小复现

已在 `nova-runtime/src/test/java/com/novalang/runtime/codegen/CompiledStaticImportTest.java` 增加 Java fixture，模拟 Kotlin 生成的 `Companion` 字段和实例方法：

```java
public static final Companion Companion = new Companion();

public static final class Companion {
    public String getMapping(String id) { return "mapping:" + id; }
    public String getINSTANCE() { return "instance"; }
}
```

Nova 复现代码：

```nova
import java dynamic.StaticFixture
StaticFixture.Companion.getMapping("x")
```

以及：

```nova
import java dynamic.StaticFixture
StaticFixture.Companion.getINSTANCE()
```

当前版本在语义分析阶段复现 `No matching Java method overload found`。该单测是待修复的回归用例，当前预期失败。

## 影响范围

- FightCorePlugin：`MagicPointProvider.Companion.getINSTANCE()`。
- Creator：`PositionMapping.Companion.getMapping(...)`、`FeatureEntityManager.Companion.getENGINE()`、`FeatureEntityManager.Companion.getAccessContainer(...)`。
- Planners：动态生成技能脚本中对 `MagicPointProvider.Companion` 的访问。
- `FeatureEntityManager.getAccessContainer` 存在多个重载，修复时仍必须保留正常的 Java 重载选择。

## 后续修复方向

1. 在 Java 成员解析中优先检查外层类型上的公开静态字段；若字段名与嵌套类同名，应将该成员解析为字段类型。
2. 只有不存在同名静态字段时，才将 `Outer.Member` 解析为嵌套 Java 类型路径。
3. `Outer.Companion.method(...)` 的 lowering 应先生成外层静态字段读取，再按实例方法生成调用；不能继续走 Java 静态方法 lowering。
4. 保留普通嵌套类的构造器、静态字段和静态方法行为。
5. 增加编译路径和解释器路径的回归测试，并覆盖 `Companion` 实例方法重载。

## 当前版本状态

- NovaLang 嵌套类型修复已推送：`c24a6e6`。
- 服务端脚本迁移及远端锤盾音效提交已合并并推送：`e703b745`。
- 本次 `Companion` 复现单测和本文档尚未修复实现；本文档提交后会一并推送。
- 本次没有完成 NovaLang 全量测试；目标单测在修复前应当失败。
