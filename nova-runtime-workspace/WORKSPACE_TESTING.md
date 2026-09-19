# Workspace 测试

`nova-test` 是不依赖 Workspace 的底层测试执行能力；Workspace 测试入口已经合并到 `nova-runtime-workspace`，负责把底层执行器绑定到 Workspace 的 `nova.config.yml`。

## 配置

测试配置直接放在 Workspace 的 `nova.config.yml` 的 `test` 节点中：

```yaml
test:
  dependencies:
    novabukkit:
      jar: "../../../plugins/NovaLang-0.2.0.jar"
    feature:
      jar: "../../../plugins/FeaturePlugin.jar"
      java-types:
        - class: "com.example.feature.FeatureJavaTypes"
          method: create
    shield:
      jar: "../../../plugins/ShieldExtension.jar"
      java-types:
        - class: "com.example.shield.ShieldJavaTypes"
          method: create
  classpath:
    - novabukkit
    - feature
  runtime:
    - "../../../libraries"
  cases:
    - file: tests/scene-mode.mock.nova
    - file: tests/shield.mock.nova
      classpath:
        - shield
```

每个依赖只声明一个 JAR，JavaTypes 工厂和 JAR 绑定在同一个依赖项中；`method` 省略时默认调用静态 `create()`。`test.classpath` 是所有测试共享的依赖 ID，单个 case 的 `classpath` 只追加额外依赖。

`test.runtime` 可以声明离线宿主运行时所需的 JAR 或 JAR 目录。使用 NovaBukkit 时，将服务端本地的 Bukkit/Paper API 依赖目录放在这里；它不属于业务依赖，也不需要启动服务端。

未声明 `cases` 时，执行 Workspace 目录会递归查找 `*.mock.nova`；测试文件可以直接放在 Workspace 目录或其子目录中。

## CLI

```text
nova test <测试文件或 Workspace 目录>
nova test <测试文件或 Workspace 目录> --report=build/test-report.json
```

部署到 Bukkit/Paper 服务端时，NovaBukkit 的单一 JAR 同时包含插件运行时和 CLI，可以直接离线执行测试：

```text
java -jar plugins/NovaLang-0.2.0.jar test plugins/ExampleWorkspace/tests
java -jar plugins/NovaLang-0.2.0.jar test plugins/AnotherWorkspace/tests
```

服务端未启动时，`test.runtime` 负责补充 Bukkit/Paper API 和其他宿主库；通常指向服务端的 `libraries` 目录及 `paper-runtime.jar`。

测试依赖 JAR 中只需要提供静态 JavaTypes 工厂，例如：

```java
public final class FeatureJavaTypes {
    public static JavaTypes create() {
        return /* JavaTypes */;
    }
}
```

插件不需要实现测试 Provider；执行器会按配置加载 JAR，反射调用 JavaTypes 工厂，并将返回值安装到测试 Workspace。
