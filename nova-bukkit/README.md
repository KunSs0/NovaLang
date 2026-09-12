# NovaLang Bukkit 模块布局

`core` 保存跨 Bukkit 版本共享的运行时接口和注册基础设施。Core 的公共绑定由主模块统一构建，版本模块只追加对应 API 可用的类型或方法。

`paper-1_21` 保存以 Paper 1.21.8 为主版本的扩展和 MockBukkit 测试。后续旧版本扩展按相同结构增加模块，模块之间不共享相互专属的 Bukkit 类型。

主模块通过 `NovaBukkit.builder(BukkitJavaTypesModule)` 或
`NovaBukkit.create(Plugin, BukkitJavaTypesModule)` 组合版本扩展。发行时由
`shadowJar` 构建唯一的 `NovaLang-<version>.jar`；`assembleSingleJar` 只是它的发行入口别名，不会再额外生成第二个运行时 JAR。运行方应显式选择与 Bukkit 服务端匹配的版本模块，不通过反射或系统级 fallback 探测版本。

新增版本模块时，版本类型放入对应模块；仅方法签名差异则在 Core 提供公共方法，并在版本模块追加差异方法。每个模块都必须独立编译测试，再参与单 JAR 装配。
